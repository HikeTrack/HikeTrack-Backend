package com.hiketrackbackend.hiketrackbackend.model.bookmark;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class BookmarkId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tour_id")
    private Long tourId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BookmarkId that = (BookmarkId) o;

        if (!userId.equals(that.userId)) {
            return false;
        }
        return tourId.equals(that.tourId);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + tourId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BookmarkId{"
                + "userId=" + userId
                + ", tourId=" + tourId
                + '}';
    }
}
