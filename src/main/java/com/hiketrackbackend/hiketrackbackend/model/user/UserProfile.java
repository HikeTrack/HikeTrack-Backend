package com.hiketrackbackend.hiketrackbackend.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String city;
    private String phoneNumber;
    private String aboutMe;
    private LocalDate dateOfBirth;

    @Column(nullable = false, updatable = false)
    private LocalDate registrationDate;

    @Column(nullable = false)
    private String photo = "https://elasticbeanstalk-eu-"
            + "central-1-954976302863.s3.amazonaws.com/user_profile/defaultAvatar.svg";

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now();
    }
}
