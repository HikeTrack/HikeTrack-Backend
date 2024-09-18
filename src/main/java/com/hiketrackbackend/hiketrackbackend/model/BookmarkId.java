package com.hiketrackbackend.hiketrackbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class BookmarkId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tour_id")
    private Long tourId;

    public BookmarkId() {

    }

    public BookmarkId(Long userId, Long tourId) {
        this.userId = userId;
        this.tourId = tourId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookmarkId that = (BookmarkId) o;

        if (!userId.equals(that.userId)) return false;
        return tourId.equals(that.tourId);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + tourId.hashCode();
        return result;
    }
}