package com.hiketrackbackend.hiketrackbackend.mapper;

import com.hiketrackbackend.hiketrackbackend.config.MapperConfig;
import com.hiketrackbackend.hiketrackbackend.dto.bookmark.BookmarkRespondDto;
import com.hiketrackbackend.hiketrackbackend.model.bookmark.Bookmark;
import com.hiketrackbackend.hiketrackbackend.model.bookmark.BookmarkId;
import java.util.Set;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookmarkMapper {
    BookmarkRespondDto toDto(Bookmark bookmark);

    Set<BookmarkRespondDto> toDto(Set<Bookmark> bookmarks);

    default Long map(BookmarkId bookmarkId) {
        return bookmarkId.getTourId();
    }

    @AfterMapping
    default void setUserAndTourIds(
            @MappingTarget BookmarkRespondDto respondDto,
            Bookmark bookmark
    ) {
        Long tourId = bookmark.getTour().getId();
        Long userId = bookmark.getUser().getId();
        respondDto.setTourId(tourId);
        respondDto.setUserId(userId);
    }
}

