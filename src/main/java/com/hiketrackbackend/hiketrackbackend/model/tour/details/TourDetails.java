package com.hiketrackbackend.hiketrackbackend.model.tour.details;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/*
    used meters and minutes to calculate variables
 */
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

    @OneToMany(mappedBy = "tourDetails", fetch = FetchType.LAZY)
    private List<TourDetailsFile> additionalPhotos = new ArrayList<>();

    @Column(nullable = false)
    private int elevationGain;

    @Size(max = 500)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RouteType routeType;

    @Column(nullable = false)
    private int duration;

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
