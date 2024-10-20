package com.hiketrackbackend.hiketrackbackend.model.tour.details;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.util.List;

// TODO переименовать в TOURDETAILS
@Entity
@Getter
@Setter
@Table(name = "details")
@SQLDelete(sql = "UPDATE details SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted=false")
public class Details {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "details")
    private List<TourDetailsFile> additionalPhotos;

    @Column(nullable = false)
    private int elevationGain;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RouteType routeType;

    @Column(nullable = false)
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
