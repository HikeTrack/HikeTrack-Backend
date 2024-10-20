package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.reviews.ReviewsRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.tour.*;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.ReviewMapper;
import com.hiketrackbackend.hiketrackbackend.mapper.TourMapper;
import com.hiketrackbackend.hiketrackbackend.model.Review;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.Details;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetailsFile;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.ReviewRepository;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourSpecificationBuilder;
import com.hiketrackbackend.hiketrackbackend.service.TourService;
import com.hiketrackbackend.hiketrackbackend.service.files.FileStorageService;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {
    private static final String FOLDER_NAME = "tours";
    private static final int FIRST_ELEMENT = 0;
    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final CountryRepository countryRepository;
    private final TourSpecificationBuilder tourSpecificationBuilder;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final FileStorageService s3Service;

    @Override
    @Transactional
    public TourRespondWithoutReviews createTour(
            TourRequestDto requestDto,
            User user,
            MultipartFile mainPhoto,
            List<MultipartFile> additionalPhotos
    ) {
        isExistTour(requestDto.getName(), user.getId());
        Tour tour = tourMapper.toEntity(requestDto);
        tour.setUser(user);

        Country country = findCountry(requestDto.getCountryId());
        tour.setCountry(country);

        List<String> mainPhotoUrl = s3Service.uploadFile(FOLDER_NAME, Collections.singletonList(mainPhoto));
        tour.setMainPhoto(mainPhotoUrl.get(FIRST_ELEMENT));
// TODO сделать дитеился сервис и перенести все что по деталям туда

        Details details = tourMapper.toEntity(requestDto.getDetailsRequestDto());
        setAdditionalFilesToTourDetails(additionalPhotos, details);
        details.setTour(tour);
        tour.setDetails(details);

        return tourMapper.toDtoWithoutReviews(tourRepository.save(tour));
    }

    // TODO что бы удалять фото надо сделать отдельный сервис у гида будет просто страницчка с загружеными фото и будет
    //  возможность удалить каждую, соотв мне будет приходить ид какой фото удалить и я просто удаляю ее
    //  и удаляю с деталей, а потом они себе грузят новые, тут главное проверить что в сохраненых фото не
    //  больше 5 фото + 1 мейн фото
    @Override
    @Transactional
    public TourRespondWithoutReviews updateTour(
            TourUpdateRequestDto requestDto,
            Long userId,
            MultipartFile photo,
            List<MultipartFile> additionalPhoto
    ) {
        Tour tour = findTourByIdAndUserId(requestDto.getTourId(), userId);
        tourMapper.updateEntityFromDto(requestDto, tour);

        Details details = tour.getDetails();
            List<TourDetailsFile> additionalPhotos = details.getAdditionalPhotos();
        // проверяю сколько ссылок сохранено уже
        // беру первые фото что бы стало 5ть и сохнаряю
        // если уже есть 5 сейвов то возврат ошибку
        // после сохранения возвразаю все вместе с сылками

        return tourMapper.toDtoWithoutReviews(tourRepository.save(tour));
    }

    @Override
    public List<TourRespondWithoutDetailsAndReviews> getAll(Pageable pageable) {
        return tourRepository.findAll(pageable)
                .stream()
                .map(tourMapper::toDtoWithoutDetailsAndReviews)
                .toList();
    }

    /*
        Transactional annotation is used to skip lazy init exception,
        so we can get reviews the way we needed
     */
    @Override
    @Transactional
    public TourRespondDto getById(Long id, int page, int size) {
        Tour tour = findTour(id);
        TourRespondDto respondDto = tourMapper.toDto(tour);
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreated").descending());
        Page<Review> reviewPage = reviewRepository.findByTourId(id, pageable);
        List<ReviewsRespondDto> dtoList = reviewPage.getContent()
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
        respondDto.setReviews(dtoList);
        respondDto.setCurrentReviewPage(reviewPage.getNumber());
        respondDto.setTotalReviewPages(reviewPage.getTotalPages());
        respondDto.setTotalReviewElements(reviewPage.getTotalElements());
        return respondDto;
    }

    @Override
    public List<TourRespondWithoutDetailsAndReviews> search(TourSearchParameters params, Pageable pageable) {
        Specification<Tour> tourSpecification = tourSpecificationBuilder.build(params);
        return tourRepository.findAll(tourSpecification, pageable)
                .stream()
                .map(tourMapper::toDtoWithoutDetailsAndReviews)
                .toList();
    }

    @Override
    public List<TourRespondWithoutDetailsAndReviews> getByRating() {
        return tourRepository
                .findTop7ByRatingGreaterThanOrderByRatingDesc(0)
                .stream()
                .map(tourMapper::toDtoWithoutDetailsAndReviews)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        //TODO доставать тур по ид юзера и ид тура что бы гарантировать что удаляет тот что надо юзер тот что надо тур
        findTour(id);
        tourRepository.deleteById(id);
    }

    private Country findCountry(Long id) {
        return countryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Country not found with id: " + id)
        );
    }

    private Tour findTour(Long id) {
       return tourRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Tour is not found with id: " + id)
        );
    }

    private void isExistTour(String tourName, Long guideId) {
        boolean exist = tourRepository.existsTourByUserIdAndName(guideId, tourName);
        if (exist) {
            throw new EntityExistsException("Tour already exists fot this guide with id: " +
                    guideId + " and with tour name: " + tourName);
        }
    }

    private void setAdditionalFilesToTourDetails(List<MultipartFile> files, Details details) {
        List<TourDetailsFile> photos = new ArrayList<>();
        List<String> urls = s3Service.uploadFile(FOLDER_NAME, files);
        for (String url : urls) {
            TourDetailsFile file = new TourDetailsFile();
            file.setFileUrl(url);
            file.setDetails(details);
            photos.add(file);
        }
        details.setAdditionalPhotos(photos);
    }

    private Tour findTourByIdAndUserId(Long id, Long userId) {
        return tourRepository.findTourByIdAndUserId(id, userId).orElseThrow(
                () -> new EntityNotFoundException("Tour not found with id: " + id + " and user id: " + userId)
        );
    }
}
