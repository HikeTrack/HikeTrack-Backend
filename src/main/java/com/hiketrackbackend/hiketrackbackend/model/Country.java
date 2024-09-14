package com.hiketrackbackend.hiketrackbackend.model;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "countries")
//@SQLDelete(sql = "UPDATE countries SET is_deleted = true WHERE id = ?")
//@SQLRestriction("is_deleted=false") activate after adding delete endpoint functionality
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Continent continent;

    @OneToMany(mappedBy = "country")
    private Set<Tour> tours = new HashSet<>();

    @Column(nullable = false)
    private boolean isDeleted;

    public enum Continent {
        EUROPE,
        ASIA,
        SOUTH_AMERICA,
        NORTH_AMERICA,
        AFRICA,
        AUSTRALIA
    }
}
