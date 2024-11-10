package com.hiketrackbackend.hiketrackbackend.model.country;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String photo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Continent continent;

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private Set<Tour> tours = new HashSet<>();
}
