package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.user.Role;
import com.hiketrackbackend.hiketrackbackend.model.user.Role.RoleName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Successfully find role by name")
    public void testFindByNameWhenRoleExistsThenReturnRole() {
        RoleName roleName = RoleName.ROLE_USER;

        Role foundRole = roleRepository.findByName(roleName);

        assertNotNull(foundRole);
        assertEquals(roleName, foundRole.getName());
    }

    @Test
    @DisplayName("Attempt to retrieve a Role with a non-existent name")
    public void testRetrieveRoleWithNonExistentName() {
        RoleRepository roleRepository = mock(RoleRepository.class);
        when(roleRepository.findByName(Role.RoleName.ROLE_GUIDE)).thenReturn(null);

        Role actualRole = roleRepository.findByName(Role.RoleName.ROLE_GUIDE);

        assertNull(actualRole);
    }
}
