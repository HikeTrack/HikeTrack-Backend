package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.User;
import com.hiketrackbackend.hiketrackbackend.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
        // TODO вроде как не правильно так доставать юзера, но может и норм, подумай оставить или что то придумать
        User user = (User) authentication.getPrincipal();
        return bookmarkService.addToBookmarks(requestDto, user);
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @GetMapping("/{userId}")
    @Operation(summary = "",
            description = "")
    public Set<BookmarkRespondDto> getBookmarksByUserId(@PathVariable @Positive Long userId) {
        return bookmarkService.getByUserId(userId);
    }

    @PreAuthorize("hasAnyRole('USER', 'GUIDE', 'ADMIN')")
    @DeleteMapping("/{userId}/{tourId}")
    @Operation(summary = "",
            description = "")
    public void deleteBookmark(@PathVariable @Positive Long userId,
                               @PathVariable @Positive Long tourId) {
        bookmarkService.deleteBookmarkById(tourId, userId);
    }
}
