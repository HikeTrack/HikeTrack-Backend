package com.hiketrackbackend.hiketrackbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Setter
@Getter
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    public Role() {
    }

    @Override
    public String getAuthority() {
        return name.name();
    }

    public enum RoleName {
        ROLE_ADMIN,
        ROLE_USER,
        ROLE_GUIDE
    }

    public Role(RoleName roleName) {
        this.name = roleName;
    }
}
