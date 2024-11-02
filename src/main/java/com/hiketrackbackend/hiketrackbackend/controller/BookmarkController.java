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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@Tag(name = "Bookmarks", description = "Operations related to managing user bookmarks.")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @Operation(summary = "Add Bookmark", description = "Add a new bookmark for the authenticated user.")
    public BookmarkRespondDto addToBookmark(@RequestBody @Valid BookmarkRequestDto requestDto,
                                            @AuthenticationPrincipal User user) {
        return bookmarkService.addToBookmarks(requestDto, user);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @Operation(summary = "Get Bookmarks", description = "Retrieve all bookmarks for the specified user.")
    public Set<BookmarkRespondDto> getBookmarksByUserId(@PathVariable @Positive Long userId) {
        return bookmarkService.getByUserId(userId);
    }

    @DeleteMapping("/{userId}/{tourId}")
    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN') && #userId == authentication.principal.id")
    @Operation(summary = "Delete Bookmark",
            description = "Delete a bookmark for the specified user with specified tour.")
    public void deleteBookmark(@PathVariable @Positive Long userId,
                               @PathVariable @Positive Long tourId) {
        bookmarkService.deleteBookmarkById(tourId, userId);
    }
}
