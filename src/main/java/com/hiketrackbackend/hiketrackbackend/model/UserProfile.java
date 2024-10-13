package com.hiketrackbackend.hiketrackbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "user_profiles")
public class UserProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String city;
    private String phoneNumber;
    private String aboutMe;

    @Column(nullable = false, updatable = false)
    private LocalDate registrationDate;

    @Column(nullable = false)
    private String userPhoto;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now();
        this.userPhoto = "img/icons/defaultAvatar.svg";
    }
}
