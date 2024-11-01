package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.model.user.UserProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Find user by id")
    public void testFindByIdWhenUserExistsThenReturnUser() {
        UserProfile userProfile = new UserProfile();

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserProfile(userProfile);

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(user.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Find user by id whe user not exist")
    public void testFindByIdWhenUserDoesNotExistThenReturnEmptyOptional() {
        Optional<User> foundUser = userRepository.findById(999L);

        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("Find user by email when user not exist")
    public void testFindByEmailWhenUserDoesNotExistThenReturnEmptyOptional() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("Is user exist by user email")
    public void testExistsUserByEmailWhenUserExistsThenReturnTrue() {
        UserProfile userProfile = new UserProfile();
        userProfile.setCountry("USA");
        userProfile.setCity("New York");
        userProfile.setPhoneNumber("1234567890");
        userProfile.setAboutMe("About me");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserProfile(userProfile);

        userRepository.save(user);

        boolean exists = userRepository.existsUserByEmail(user.getEmail());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Exist by email when no user in DB")
    public void testExistsUserByEmailWhenUserDoesNotExistThenReturnFalse() {
        boolean exists = userRepository.existsUserByEmail("nonexistent@example.com");

        assertThat(exists).isFalse();
    }
}
