package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.bookmark.Bookmark;
import com.hiketrackbackend.hiketrackbackend.model.bookmark.BookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Set;

public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {
    Set<Bookmark> findByUser_Id(Long user_id);

    boolean existsById(BookmarkId id);

}
