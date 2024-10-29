package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
@Validated
@Tag(name = "", description = "")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @PostMapping
    @Operation(summary = "", description = "")
    public BookmarkRespondDto addToBookmark(@RequestBody @Valid BookmarkRequestDto requestDto,
                                            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return bookmarkService.addToBookmarks(requestDto, user);
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @GetMapping("/{userId}")
    @Operation(summary = "",
            description = "")
    public Set<BookmarkRespondDto> getBookmarksByUserId(@PathVariable @Positive Long userId) {
        return bookmarkService.getByUserId(userId);
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @DeleteMapping("/{userId}/{tourId}")
    @Operation(summary = "",
            description = "")
    public void deleteBookmark(@PathVariable @Positive Long userId,
                               @PathVariable @Positive Long tourId) {
        bookmarkService.deleteBookmarkById(tourId, userId);
    }
}
