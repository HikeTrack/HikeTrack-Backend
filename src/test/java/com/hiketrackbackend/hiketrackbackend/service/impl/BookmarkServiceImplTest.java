package com.hiketrackbackend.hiketrackbackend.service.impl;

import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityAlreadyExistException;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.mapper.BookmarkMapper;
import com.hiketrackbackend.hiketrackbackend.model.bookmark.Bookmark;
import com.hiketrackbackend.hiketrackbackend.model.bookmark.BookmarkId;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.BookmarkRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookmarkServiceImplTest {
    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private BookmarkMapper bookmarkMapper;

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private BookmarkServiceImpl bookmarkService;

    @Test
    @DisplayName("Add bookmark with all valid data")
    public void testAddToBookmarksWhenBookmarkIsSuccessfullyAddedThenReturnBookmarkRespondDto() {
        BookmarkRequestDto requestDto = new BookmarkRequestDto();
        requestDto.setTourId(1L);

        User user = new User();
        user.setId(1L);

        BookmarkId bookmarkId = new BookmarkId(user.getId(), requestDto.getTourId());

        when(bookmarkRepository.existsById(bookmarkId)).thenReturn(false);

        Tour tour = new Tour();
        tour.setId(1L);
        when(tourRepository.findById(requestDto.getTourId())).thenReturn(Optional.of(tour));

        Bookmark bookmark = new Bookmark();
        bookmark.setId(bookmarkId);
        bookmark.setUser(user);
        bookmark.setTour(tour);
        bookmark.setAddedAt(LocalDateTime.now());
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(bookmark);

        BookmarkRespondDto respondDto = new BookmarkRespondDto();
        respondDto.setId(1L);
        respondDto.setUserId(user.getId());
        respondDto.setTourId(tour.getId());
        respondDto.setAddedAt(LocalDateTime.now());
        when(bookmarkMapper.toDto(any(Bookmark.class))).thenReturn(respondDto);

        BookmarkRespondDto result = bookmarkService.addToBookmarks(requestDto, user);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getTourId()).isEqualTo(tour.getId());
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    @DisplayName("Add bookmark when bookmark already exists")
    public void testAddToBookmarksWhenBookmarkAlreadyExistsThenThrowIllegalStateException() {
        BookmarkRequestDto requestDto = new BookmarkRequestDto();
        requestDto.setTourId(1L);

        User user = new User();
        user.setId(1L);

        BookmarkId bookmarkId = new BookmarkId(user.getId(), requestDto.getTourId());

        when(bookmarkRepository.existsById(bookmarkId)).thenReturn(true);

        assertThatThrownBy(() -> bookmarkService.addToBookmarks(requestDto, user))
                .isInstanceOf(EntityAlreadyExistException.class)
                .hasMessage("This tour is already saved");
    }

    @Test
    @DisplayName("Add bookmark with not valid tour id")
    public void testAddToBookmarksWhenTourDoesNotExistThenThrowEntityNotFoundException() {
        BookmarkRequestDto requestDto = new BookmarkRequestDto();
        requestDto.setTourId(1L);

        User user = new User();
        user.setId(1L);

        BookmarkId bookmarkId = new BookmarkId(user.getId(), requestDto.getTourId());

        when(bookmarkRepository.existsById(bookmarkId)).thenReturn(false);
        when(tourRepository.findById(requestDto.getTourId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookmarkService.addToBookmarks(requestDto, user))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Tour not found with id: " + requestDto.getTourId());
    }

    @Test
    @DisplayName("Get bookmarks by user id")
    public void testGetByUserIdWhenUserHasBookmarksThenReturnSetOfBookmarkRespondDto() {
        Long userId = 1L;
        Set<Bookmark> bookmarks = new HashSet<>();
        Bookmark bookmark = new Bookmark();
        bookmark.setId(new BookmarkId(userId, 1L));
        bookmarks.add(bookmark);

        when(bookmarkRepository.findByUser_Id(userId)).thenReturn(bookmarks);

        Set<BookmarkRespondDto> bookmarkRespondDtos = new HashSet<>();
        BookmarkRespondDto respondDto = new BookmarkRespondDto();
        respondDto.setId(1L);
        respondDto.setUserId(userId);
        respondDto.setTourId(1L);
        bookmarkRespondDtos.add(respondDto);

        when(bookmarkMapper.toDto(bookmarks)).thenReturn(bookmarkRespondDtos);

        Set<BookmarkRespondDto> result = bookmarkService.getByUserId(userId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getUserId()).isEqualTo(userId);
        verify(bookmarkRepository).findByUser_Id(userId);
    }

    @Test
    @DisplayName("Get bookmark by user id when user has no bookmarks")
    public void testGetByUserIdWhenUserHasNoBookmarksThenReturnEmptySet() {
        Long userId = 1L;
        Set<Bookmark> bookmarks = Collections.emptySet();

        when(bookmarkRepository.findByUser_Id(userId)).thenReturn(bookmarks);

        Set<BookmarkRespondDto> bookmarkRespondDtos = Collections.emptySet();
        when(bookmarkMapper.toDto(bookmarks)).thenReturn(bookmarkRespondDtos);

        Set<BookmarkRespondDto> result = bookmarkService.getByUserId(userId);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(bookmarkRepository).findByUser_Id(userId);
    }

    @Test
    @DisplayName("Delete bookmark with valid id")
    public void testDeleteBookmarkByIdWhenBookmarkExistsThenDeleteBookmark() {
        Long userId = 1L;
        Long tourId = 1L;
        BookmarkId bookmarkId = new BookmarkId(userId, tourId);

        Bookmark bookmark = new Bookmark();
        bookmark.setId(bookmarkId);

        when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.of(bookmark));

        bookmarkService.deleteBookmarkById(tourId, userId);

        verify(bookmarkRepository).delete(bookmark);
    }

    @Test
    @DisplayName("Delete bookmark with not valid id")
    public void testDeleteBookmarkByIdWhenBookmarkDoesNotExistThenThrowEntityNotFoundException() {
        Long userId = 1L;
        Long tourId = 1L;
        BookmarkId bookmarkId = new BookmarkId(userId, tourId);

        when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookmarkService.deleteBookmarkById(tourId, userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Bookmark with id BookmarkId{userId=1, tourId=1} not found");
    }
}