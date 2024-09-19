package com.hiketrackbackend.hiketrackbackend.service;

import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRequestDto;
import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.User;
import java.util.Set;

public interface BookmarkService {
    Set<BookmarkRespondDto> getByUserId(Long userId);

    BookmarkRespondDto addToBookmarks(BookmarkRequestDto requestDto, User user);

    void deleteBookmarkById(Long tourId, Long userId);
}
