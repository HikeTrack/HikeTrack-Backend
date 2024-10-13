package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.BookmarkMapper;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.model.bookmark.Bookmark;
import com.hiketrackbackend.hiketrackbackend.model.bookmark.BookmarkId;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.BookmarkRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import com.hiketrackbackend.hiketrackbackend.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkMapper bookmarkMapper;
    private final TourRepository tourRepository;

    @Override
    public BookmarkRespondDto addToBookmarks(BookmarkRequestDto requestDto, User user) {
        BookmarkId bookmarkId = new BookmarkId(user.getId(), requestDto.getTourId());
        findBookmarkById(bookmarkId);
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setId(bookmarkId);
        Tour tour = getTour(requestDto);
        bookmark.setTour(tour);
        bookmark.setAddedAt(LocalDateTime.now());
        return bookmarkMapper.toDto(bookmarkRepository.save(bookmark));
    }

    @Override
    public Set<BookmarkRespondDto> getByUserId(Long userId) {
        Set<Bookmark> bookmarks = bookmarkRepository.findByUser_Id(userId);
        return bookmarkMapper.toDto(bookmarks);
    }

    @Override
    public void deleteBookmarkById(Long tourId, Long userId) {
        BookmarkId bookmarkId = new BookmarkId(userId, tourId);
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElseThrow(
                () -> new EntityNotFoundException("Bookmark with id " + bookmarkId + " not found"));
        bookmarkRepository.delete(bookmark);
    }

    private Tour getTour(BookmarkRequestDto requestDto) {
        return tourRepository.findById(requestDto.getTourId()).orElseThrow(
                () -> new EntityNotFoundException("Tour not found with id: " + requestDto.getTourId())
        );
    }

    private void findBookmarkById(BookmarkId id) {
        boolean exists = bookmarkRepository.existsById(id);
        if (exists) {
            throw new IllegalStateException("This tour is already saved");
        }
    }
}
