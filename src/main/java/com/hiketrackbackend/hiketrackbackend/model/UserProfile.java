package com.hiketrackbackend.hiketrackbackend.model;

import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(
            nullable = false,
            unique = true,
            name = "user_id"
    )
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    private String city;

    @Column(nullable = false)
    private LocalDate registrationDate;

    @Column(nullable = false)
    private String userPhoto;
}
