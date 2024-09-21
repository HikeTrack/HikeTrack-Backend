package com.hiketrackbackend.hiketrackbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HikeTrackBackendApplicationTests {

    @Test
    void contextLoads() {
    }

//    @SpringBootTest
//    public class UserServiceIntegrationTest {
//
//        @Autowired
//        private UserService userService;
//
//        @Autowired
//        private CountryRepository countryRepository;
//
//        @Autowired
//        private UserRepository userRepository;
//
//        @Test
//        @Transactional
//        public void testRegisterUser_createsProfile() {
//            // Предварительно добавьте страну в базу данных или используйте @BeforeEach
//            Country country = new Country();
//            country.setName("Россия");
//            countryRepository.save(country);
//
//            // Создание DTO запроса
//            UserRegistrationDto dto = new UserRegistrationDto(
//                    "jane_doe",
//                    "password123",
//                    "jane.doe@example.com",
//                    country.getId()
//            );
//
//            // Регистрация пользователя
//            User user = userService.registerUser(dto);
//
//            // Проверка, что пользователь сохранён
//            assertThat(user.getId()).isNotNull();
//            assertThat(user.getUsername()).isEqualTo("jane_doe");
//            assertThat(user.getEmail()).isEqualTo("jane.doe@example.com");
//
//            // Проверка, что профиль создан и связан с пользователем
//            Profile profile = user.getProfile();
//            assertThat(profile).isNotNull();
//            assertThat(profile.getUser()).isEqualTo(user);
//            assertThat(profile.getCountry()).isEqualTo(country);
//
//            // Дополнительные проверки полей профиля
//            assertThat(profile.getFirstName()).isEmpty();
//            assertThat(profile.getLastName()).isEmpty();
//            assertThat(profile.getPhoneNumber()).isEmpty();
//            assertThat(profile.getAddress()).isEmpty();
//        }
//    }
}
