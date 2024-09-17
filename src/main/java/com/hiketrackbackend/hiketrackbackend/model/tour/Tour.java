package com.hiketrackbackend.hiketrackbackend.model.tour;

import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.model.details.Details;
import com.hiketrackbackend.hiketrackbackend.model.Review;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
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

    @Column(nullable = false)
    @Min(0)
    private int length;

    @Column(nullable = false)
    private String mainPhoto;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private ZonedDateTime date;

    private int rating;

    private int savedToBookmarks;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private Set<Review> reviews;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToOne(mappedBy = "tour", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Details details;

    @Column(nullable = false)
    private boolean isDeleted;
}
