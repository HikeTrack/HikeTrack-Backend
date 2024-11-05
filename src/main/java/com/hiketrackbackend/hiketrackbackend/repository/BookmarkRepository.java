package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.bookmark.Bookmark;
import com.hiketrackbackend.hiketrackbackend.model.bookmark.BookmarkId;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {
    Set<Bookmark> findByUser_Id(Long userId);

    boolean existsById(BookmarkId id);
}
