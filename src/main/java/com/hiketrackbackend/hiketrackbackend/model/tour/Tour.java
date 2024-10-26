package com.hiketrackbackend.hiketrackbackend.model.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetails;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.model.bookmark.Bookmark;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.model.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "tours")
@SQLDelete(sql = "UPDATE tours SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted=false")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // meters
    @Column(nullable = false)
    @Min(0)
    private int length;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private ZonedDateTime date;

    @Min(0)
    private int rating;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(nullable = false)
    private String mainPhoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.REMOVE)
    private List<Review> reviews = new ArrayList<>();

    @OneToOne(mappedBy = "tour", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TourDetails tourDetails;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Bookmark> bookmarks = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean isDeleted;
}
