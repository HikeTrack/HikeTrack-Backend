package com.hiketrackbackend.hiketrackbackend.model.bookmark;

import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "bookmarks")
public class Bookmark {
    @EmbeddedId
    private BookmarkId id = new BookmarkId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tourId")
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @Column(nullable = false)
    private LocalDateTime addedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bookmark bookmark = (Bookmark) o;

        return id.equals(bookmark.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
