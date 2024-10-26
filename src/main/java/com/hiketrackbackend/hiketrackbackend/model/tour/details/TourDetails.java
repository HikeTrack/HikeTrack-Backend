package com.hiketrackbackend.hiketrackbackend.model.tour.details;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tour_details")
@SQLDelete(sql = "UPDATE tour_details SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted=false")
public class TourDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "tourDetails")
    private List<TourDetailsFile> additionalPhotos = new ArrayList<>();

    @Column(nullable = false)
    // meters
    private int elevationGain;

    @Size(max = 500)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RouteType routeType;

    @Column(nullable = false)
    //minutes
    private int duration;

    // TODO link for map photo(later add map api?)
    @Column(nullable = false)
    private String map;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Activity activity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "tour_id")
    private Tour tour;

    @Column(nullable = false)
    private boolean isDeleted;
}
