package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRespondDto;
import com.hiketrackbackend.hiketrackbackend.exception.EntityNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.service.BookmarkService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookmarkControllerTest {
    @Mock
    protected BookmarkService bookmarkService;

    @InjectMocks
    private BookmarkController bookmarkController;

    @Test
    @DisplayName("Add bookmark with valid request")
    public void testAddToBookmarkWhenValidRequestThenReturnBookmarkRespondDto() {
        BookmarkRequestDto requestDto = new BookmarkRequestDto();
        requestDto.setTourId(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");

        BookmarkRespondDto expectedResponse = new BookmarkRespondDto();
        expectedResponse.setId(1L);
        expectedResponse.setUserId(1L);
        expectedResponse.setTourId(1L);

        when(bookmarkService.addToBookmarks(any(BookmarkRequestDto.class), any(User.class))).thenReturn(expectedResponse);

        BookmarkRespondDto actualResponse = bookmarkController.addToBookmark(requestDto, user);

        assertEquals(expectedResponse, actualResponse);
        verify(bookmarkService, times(1)).addToBookmarks(requestDto, user);
    }

    @Test
    @DisplayName("Add bookmark with null id")
    public void testReplicateAddBookmarkNullTourId() {
        BookmarkService bookmarkService = mock(BookmarkService.class);
        BookmarkController bookmarkController = new BookmarkController(bookmarkService);
        BookmarkRequestDto requestDto = new BookmarkRequestDto();
        requestDto.setTourId(null);
        User user = new User();
        user.setId(1L);
        Authentication authentication = mock(Authentication.class);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        assertThrows(ConstraintViolationException.class, () -> {
            Set<ConstraintViolation<BookmarkRequestDto>> violations = validator.validate(requestDto);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            bookmarkController.addToBookmark(requestDto, (User) authentication);
        });
    }

    @Test
    @DisplayName("Get bookmarks by user id valid data")
    public void testGetBookmarksByUserIdWhenValidUserIdThenReturnBookmarks() {
        Long userId = 1L;
        Set<BookmarkRespondDto> expectedBookmarks = new HashSet<>();
        BookmarkRespondDto bookmark1 = new BookmarkRespondDto();
        bookmark1.setId(1L);
        bookmark1.setUserId(userId);
        bookmark1.setTourId(1L);
        expectedBookmarks.add(bookmark1);

        BookmarkRespondDto bookmark2 = new BookmarkRespondDto();
        bookmark2.setId(2L);
        bookmark2.setUserId(userId);
        bookmark2.setTourId(2L);
        expectedBookmarks.add(bookmark2);

        when(bookmarkService.getByUserId(userId)).thenReturn(expectedBookmarks);

        Set<BookmarkRespondDto> actualBookmarks = bookmarkController.getBookmarksByUserId(userId);

        assertEquals(expectedBookmarks, actualBookmarks);
        verify(bookmarkService, times(1)).getByUserId(userId);
    }

    @Test
    @DisplayName("Get bookmarks by user id with not valid id")
    public void testGetBookmarksByUserIdWhenInvalidUserIdThenThrowException() {
        Long invalidUserId = -1L;

        when(bookmarkService.getByUserId(invalidUserId)).thenThrow(new IllegalArgumentException("Invalid userId"));

        assertThrows(IllegalArgumentException.class, () -> {
            bookmarkController.getBookmarksByUserId(invalidUserId);
        });
        verify(bookmarkService, times(1)).getByUserId(invalidUserId);
    }

    @Test
    @DisplayName("Delete bookmark with user and tour valid id")
    public void testDeleteBookmarkWhenValidUserIdAndTourIdThenDeleteBookmark() {
        Long userId = 1L;
        Long tourId = 1L;

        bookmarkController.deleteBookmark(userId, tourId);

        verify(bookmarkService, times(1)).deleteBookmarkById(tourId, userId);
    }

    @Test
    @DisplayName("Delete bookmark with invalid tour id")
    public void testDeleteBookmarkWhenInvalidTourIdThenThrowIllegalArgumentException() {
        Long userId = 1L;
        Long invalidTourId = -1L;

        doThrow(new EntityNotFoundException("Invalid tourId")).when(bookmarkService).deleteBookmarkById(invalidTourId, userId);

        assertThrows(EntityNotFoundException.class, () -> {
            bookmarkController.deleteBookmark(userId, invalidTourId);
        });
    }
}
