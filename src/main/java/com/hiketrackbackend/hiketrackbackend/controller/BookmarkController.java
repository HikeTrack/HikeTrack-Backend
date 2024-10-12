package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.security.AuthenticationService;
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
    private final AuthenticationService authenticationService;

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @PostMapping("/new")
    @Operation(summary = "Add tour to bookmarks", description = "Add tour to current logged user bookmarks")
    public BookmarkRespondDto addToBookmark(@RequestBody @Valid BookmarkRequestDto requestDto,
                                            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return bookmarkService.addToBookmarks(requestDto, user);
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @GetMapping()
    @Operation(summary = "Get bookmark",
            description = "Get bookmarks from logged user by his id(Set of all his saved tours)")
    public Set<BookmarkRespondDto> getBookmarksByUserId(Authentication authentication) {
        Long userId = authenticationService.getUserId(authentication);
        return bookmarkService.getByUserId(userId);
    }

    // TODO check path
    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @DeleteMapping("/{tourId}")
    @Operation(summary = "Delete tour from bookmarks",
            description = "Delete tour by tourId from current logged user bookmarks")
    public void deleteBookmark(@PathVariable @Positive Long tourId,
                               Authentication authentication) {
        Long userId = authenticationService.getUserId(authentication);
        bookmarkService.deleteBookmarkById(tourId, userId);
    }
}
