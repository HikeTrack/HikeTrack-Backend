package com.hiketrackbackend.hiketrackbackend.model;

import com.hiketrackbackend.hiketrackbackend.model.enam.Activity;
import com.hiketrackbackend.hiketrackbackend.model.enam.RouteType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "details")
public class Details {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String photo; //link for photo

    @Column(nullable = false)
    private Difficulty difficulty;

    private int elevationGain;

    @Column(nullable = false)
    private RouteType routeType;

    @Column(nullable = false)
    private String map; // link for map photo(later add map api?)

    private Activity activity;

    @OneToOne(fetch = FetchType.LAZY)
    private Tour tour;

    public enum Difficulty {
        Easy, Medium, Hard
    }
}
