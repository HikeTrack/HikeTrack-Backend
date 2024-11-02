# HikeTrack-Backend

## Overview

HikeTrack-Backend is a comprehensive backend service for managing hiking tours, user profiles, reviews, and more. It leverages modern technologies and best practices to provide a robust and scalable solution.

## Technologies

| Name                        | Version   |
|-----------------------------|-----------|
| Spring Boot                 | 3.3.3     |
| Spring Data JPA             | 3.3.3     |
| Spring Web                  | 3.3.3     |
| Liquibase                   | 4.18.2    |
| MySQL Connector J           | 8.0.33    |
| Lombok                      | 1.18.24   |
| Spring Boot Starter Test    | 3.3.3     |
| Spring Boot Starter Actuator| 3.3.3     |
| Springdoc Openapi UI        | 1.8.0     |
| Testcontainers MySQL        | 1.20.1    |
| MapStruct                   | 1.6.0     |
| JJWT API                    | 0.11.5    |
| JJWT Impl                   | 0.11.5    |
| JJWT Jackson                | 0.11.5    |
| MapStruct Processor         | 1.5.5.Final|
| Spring Security Core        | 6.3.3     |
| Spring Security Web         | 6.3.3     |
| Spring Security Config      | 6.3.3     |
| Spring Boot Starter OAuth2 Client | 3.3.3 |
| Spring Boot Starter Data Redis | 3.3.3 |
| Spring Boot Starter Mail    | 3.3.3     |
| Maven                       | 3.9.9     |
| Checkstyle                  | 3.5.0     |
| Docker                      | latest    |
| Redis                       | 7.4.0     |
| MySQL                       | 8.0.33    |

## Project Structure

C:\Users\suige\IdeaProjects\HikeTrack-Backend 
├── .github 
│ └── workflows 
│ ├── aws-publish.yml 
│ ├── ci.yml 
│ └── docker-publish.yml 
├── .mvn │ └── wrapper 
│ ├── maven-wrapper.jar 
│ └── maven-wrapper.properties 
└── src 
├── main 
│ ├── resources 
│ │ └── db 
│ │ └── changelog 
│ │ └── changeset 
│ │ ├── 01-create-countries-table.yaml 
│ │ ├── 02-insert-data-to-countries-table.yaml 
│ │ ├── 03-create-tours-table.yaml 
│ │ ├── 04-create-tourDetails-table.yaml 
│ │ ├── 05-create-review-table.yaml 
│ │ ├── 07-create-user-table.yaml 
│ │ ├── 08-create-role-table.yaml 
│ │ ├── 09-create-users-roles-table.yaml 
│ │ ├── 10-insert-data-to-user-table.yaml 
│ │ ├── 11-create-user-profile-table.yaml 
│ │ └── 12-create-bookmarks-table.yaml 
│ └── java 
│ └── com 
│ └── hiketrackbackend 
│ ├── config 
│ ├── controller 
│ ├── exception 
│ ├── mapper 
│ ├── model 
│ ├── repository 
│ ├── security 
│ ├── service
│ ├── validation 
│ └── dto └── test 
├── resources 
└── java 
└── com 
└── hiketrackbackend 
└── HikeTrackBackendApplicationTests.java

## Business Logic

### Authentication

- **Registration**: Allows users to create new accounts with email, password, first name, and last name.
- **Login**: Enables users to authenticate using their email and password.
- **Forgot Password**: Provides a mechanism for users to reset their passwords by sending a reset link to their email.
- **Update Password**: Allows users to update their passwords after receiving a reset link.
- **Logout**: Logs users out of their accounts.

### Country Management

- **Create Country**: Allows authorized users to create new countries with name, continent, and photo.
- **Search Countries**: Enables users to search for countries based on continent or country name.
- **Get Country by ID**: Retrieves a specific country by its ID.
- **Get All Countries**: Retrieves a list of all countries.

### Tour Management

- **Create Tour**: Allows authorized users to create new tours with name, length, price, date, difficulty, country ID, main photo, and tour details.
- **Update Tour**: Allows authorized users to update existing tours.
- **Delete Tour**: Allows authorized users to soft delete tours, making them invisible to users.
- **Get All Tours**: Retrieves a list of all tours.
- **Get Tour by ID**: Retrieves a specific tour by its ID, including tour details and reviews.
- **Get Most Rated Tours**: Retrieves a list of the top 7 most rated tours.
- **Search Tours**: Enables users to search for tours based on various parameters, including route type, difficulty, length, activity, date, duration, price, and country.

### Review Management

- **Create Review**: Allows users to create reviews for tours.
- **Delete Review**: Allows users to delete their own reviews.
- **Update Review**: Allows users to update their own reviews.
- **Get All Reviews by User**: Retrieves a list of all reviews created by a specific user.

### User Profile Management

- **Update User Profile**: Allows users to update their profile information, including country, city, and user photo.
- **Get User Profile**: Retrieves the profile information of the currently logged-in user.
- **Add Tour to Bookmarks**: Allows users to add tours to their bookmarks.
- **Get Bookmarks**: Retrieves a list of all tours bookmarked by the currently logged-in user.
- **Delete Tour from Bookmarks**: Allows users to remove tours from their bookmarks.

## Dependency Management

- **Maven**: Used for dependency management and project build.
- **Maven Wrapper**: Ensures consistent Maven versions across development environments.

## Notes

- **Liquibase**: Used for database migrations.
- **Spring Security**: Used for authentication and authorization.
- **JWT**: Used for authentication tokens.
- **OAuth2**: Used for Google authentication.
- **Redis**: Used for caching and storing JWT tokens.
- **Springdoc OpenAPI**: Used for API documentation.
- **Testcontainers**: Used for testing with Docker containers.
- **MapStruct**: Used for object mapping.
- **Lombok**: Used for code generation.
- **Checkstyle**: Used for code style enforcement.

## Endpoints

### Authentication

- `POST /auth/registration` - Register a new user
- `POST /auth/login` - Login a user
- `POST /auth/forgot-password` - Send a password reset link to the user's email
- `GET /auth/reset-password` - Validate the password reset link and redirect to the password reset page
- `POST /auth/update-password/{email}` - Update the user's password
- `POST /auth/logout` - Logout the user

### Country Management

- `POST /countries/new` - Create a new country
- `GET /countries/search` - Search for countries
- `GET /countries/{id}` - Get a country by ID
- `GET /countries` - Get all countries

### Tour Management

- `POST /tours/new` - Create a new tour
- `PUT /tours/{tourId}` - Update a tour
- `DELETE /tours/{id}` - Delete a tour
- `GET /tours` - Get all tours
- `GET /tours/{id}` - Get a tour by ID
- `GET /tours/popular` - Get the most rated tours
- `GET /tours/search` - Search for tours

### Review Management

- `POST /tours/{tourId}/reviews` - Create a new review for a tour
- `DELETE /tours/{tourId}/reviews/{reviewId}` - Delete a review
- `PUT /tours/{tourId}/reviews/{reviewId}` - Update a review

### User Profile Management

- `POST /profile/bookmarks/new` - Add a tour to bookmarks
- `PUT /profile` - Update the user's profile
- `GET /profile/bookmarks` - Get all bookmarks
- `GET /profile` - Get the user's profile
- `DELETE /profile/bookmarks/{tourId}` - Delete a tour from bookmarks

## Downstream Services

- **MySQL**: Database for storing application data
- **Redis**: Cache for JWT tokens and password reset requests
- **Google OAuth2**: Authentication provider for Google logins
- **Mail Server**: Server for sending password reset emails

## Upstream Services

- **Frontend**: Client application that interacts with the backend API

## Testing

### Dependencies

- **Spring Boot Starter Test**
- **Testcontainers MySQL**

### Test Flows

- **Unit Tests**: For individual classes and methods
- **Integration Tests**: For interacting with the database and other services
- **End-to-End Tests**: For verifying the functionality of the entire application

### Fixtures Storage

- Test data is stored in the `src/test/resources` folder.

### Useful to Know

- The project uses Testcontainers to run MySQL and Redis containers for testing.
- The project uses Mockito for mocking dependencies in unit tests.

## Deployment

### Summary

- The project can be deployed to AWS Elastic Beanstalk using the provided GitHub workflow.
- The project can be deployed to Docker Hub using the provided GitHub workflow.

## Setting Up the Development Environment

### Prerequisites

1. **Install Java Development Kit (JDK)**:
    - Download and install JDK 17 from [Oracle](https://www.oracle.com/java/technologies/downloads/#java17).

2. **Install Maven**:
    - Download and install Maven from [Apache Maven](https://maven.apache.org/download.cgi).

3. **Install Docker**:
    - Download and install Docker Desktop from [Docker](https://www.docker.com/products/docker-desktop).

4. **Install Redis**:
    - Download and install Redis from [Redis](https://redis.io/docs/getting-started/install/).

5. **Install MySQL**:
    - Download and install MySQL from [MySQL](https://dev.mysql.com/downloads/mysql/).

### Set Up Environment Variables

Create a `.env` file in the project root directory and set the following environment variables:

MYSQLDB_ROOT_PASSWORD=your_root_password 
MYSQLDB_USER=your_username 
MYSQLDB_PASSWORD=your_password 
MYSQLDB_DATABASE=your_database 
MYSQLDB_LOCAL_PORT=your_local_port 
MYSQLDB_DOCKER_PORT=your_docker_port 
SPRING_REDIS_PORT=your_redis_port 
SPRING_REDIS_DOCKER_PORT=your_redis_docker_port 
SPRING_LOCAL_PORT=your_local_port 
SPRING_DOCKER_PORT=your_docker_port 
DEBUG_PORT=your_debug_port 
JWT_SECRET=your_jwt_secret 
GOOGLE_CLIENT_ID=your_google_client_id 
GOOGLE_CLIENT_SECRET=your_google_client_secret 
SMTP_PASSWORD=your_smtp_password 
MAIL_PORT=your_mail_port 
FROM_EMAIL=your_from_email 
REDIS_HOST=your_redis_host 
REDIS_PORT=your_redis_port

### Running the Project in the Development Environment

1. **Start MySQL**:
    - Open a terminal and navigate to the MySQL installation directory.
    - Run the following command:
      ```
      mysqld --defaults-file=/path/to/my.cnf
      ```

2. **Start Redis**:
    - Open a terminal and navigate to the Redis installation directory.
    - Run the following command:
      ```
      redis-server
      ```

3. **Build the Project**:
    - Open a terminal and navigate to the project root directory.
    - Run the following command:
      ```
      mvn clean install
      ```

4. **Run the Application**:
    - Open a terminal and navigate to the project root directory.
    - Run the following command:
      ```
      mvn spring-boot:run
      ```

5. **Access the Application**:
    - Open a web browser and navigate to `http://localhost:8080`.

### Execution Instructions

#### Local

mvn spring-boot:run

Run the application using Maven.

#### Debug

mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:$DEBUG_PORT"

Run the application in debug mode with the specified debug port.

#### Docker

docker-compose up -d

Build and run the application in a Docker container using Docker Compose.

## Sequence Diagrams

### User Registration

Frontend -> AuthenticationController: registration(email, password, firstName, lastName) 
AuthenticationController -> UserRepository: save(user) 
UserRepository -> MySQL: INSERT INTO users (email, password, firstName, lastName) VALUES (?, ?, ?, ?)

### User Login

Frontend -> AuthenticationController: login(email, password) 
AuthenticationController -> AuthenticationManager: authenticate(email, password) 
AuthenticationManager -> UserRepository: findByEmail(email) 
UserRepository -> MySQL: SELECT * FROM users WHERE email = ? 
AuthenticationManager -> JwtUtil: generateToken(email) 
AuthenticationController -> Frontend: token

### Forgot Password

Frontend -> AuthenticationController: forgotPassword(email) 
AuthenticationController -> UserService: createRestoreRequest(email) 
UserService -> UserRepository: findByEmail(email) 
UserRepository -> MySQL: SELECT * FROM users WHERE email = ? 
UserService -> UUIDTokenServiceImpl: saveTokenToDB(token, email) 
UUIDTokenServiceImpl -> Redis: SET token email 1 hour 
UserService -> MailSender: sendResetPasswordMailToGMail(email, token) 
MailSender -> SMTP Server: SEND EMAIL

### Update Password

Frontend -> AuthenticationController: updatePassword(password, repeatPassword, email) 
AuthenticationController -> UserService: updatePassword(password, repeatPassword, email) 
UserService -> UserRepository: findByEmail(email) 
UserRepository -> MySQL: SELECT * FROM users WHERE email = ? 
UserService -> UserRepository: save(user) 
UserRepository -> MySQL: UPDATE users SET password = ? WHERE email = ?

### Logout

Frontend -> AuthenticationController: logout(token) 
AuthenticationController -> JwtTokenServiceImpl: saveTokenToDB(token, email) 
JwtTokenServiceImpl -> Redis: SET token email 20 minutes

### Create Country

Frontend -> CountryController: createCountry(name, continent, photo) 
CountryController -> CountryService: createCountry(name, continent, photo) 
CountryService -> CountryMapper: toEntity(name, continent, photo) 
CountryService -> CountryRepository: save(country) 
CountryRepository -> MySQL: INSERT INTO countries (name, continent, photo) VALUES (?, ?, ?) 
CountryService -> CountryMapper: toDto(country) 
CountryController -> Frontend: country

### Search Countries

Frontend -> CountryController: search(continent, countryName) 
CountryController -> CountrySpecificationBuilder: build(continent, countryName) 
CountrySpecificationBuilder -> CountrySpecificationProviderManager: getSpecificationProvider(continent) 
CountrySpecificationProviderManager -> ContinentSpecificationProvider: getSpecification(continent) 
CountrySpecificationBuilder -> CountrySpecificationProviderManager: getSpecificationProvider(countryName) 
CountrySpecificationProviderManager -> NameSpecificationProvider: getSpecification(countryName) 
CountryController -> CountryRepository: findAll(specification, pageable) 
CountryRepository -> MySQL: SELECT * FROM countries WHERE continent IN (?) AND name IN (?) 
CountryRepository -> CountryController: countries CountryController -> CountryMapper: toDto(countries) 
CountryController -> Frontend: countries

### Get Country by ID

Frontend -> CountryController: getById(id) 
CountryController -> CountryService: getById(id) 
CountryService -> CountryRepository: findById(id) 
CountryRepository -> MySQL: SELECT * FROM countries WHERE id = ? 
CountryRepository -> CountryService: country 
CountryService -> CountryMapper: toDto(country) 
CountryController -> Frontend: country

### Get All Countries

Frontend -> CountryController: getAll(pageable) 
CountryController -> CountryRepository: findAll(pageable) 
CountryRepository -> MySQL: SELECT * FROM countries 
CountryRepository -> CountryController: countries 
CountryController -> CountryMapper: toDto(countries) 
CountryController -> Frontend: countries


### Create Tour

Frontend -> TourController: createTour(name, length, price, date, difficulty, countryId, mainPhoto, tourDetails) 
TourController -> TourService: createTour(name, length, price, date, difficulty, countryId, mainPhoto, tourDetails) 
TourService -> TourMapper: toEntity(name, length, price, date, difficulty, countryId, mainPhoto, tourDetails) 
TourService -> CountryRepository: findById(countryId) 
CountryRepository -> MySQL: SELECT * FROM countries WHERE id = ? TourService -> TourRepository: save(tour) 
TourRepository -> MySQL: INSERT INTO tours (name, length, price, date, difficulty, country_id, mainPhoto) VALUES (?, ?, ?, ?, ?, ?, ?) 
TourService -> TourMapper: toDtoWithoutReviews(tour) 
TourController -> Frontend: tour

### Update Tour

Frontend -> TourController: updateTour(tourId, name, length, price, date, difficulty, countryId, mainPhoto, tourDetails)
TourController -> TourService: updateTour(tourId, name, length, price, date, difficulty, countryId, mainPhoto, tourDetails) 
TourService -> TourRepository: findById(tourId) 
TourRepository -> MySQL: SELECT * FROM tours WHERE id = ? 
TourService -> TourMapper: updateEntityFromDto(tour, name, length, price, date, difficulty, countryId, mainPhoto, tourDetails) 
TourService -> TourRepository: save(tour) 
TourRepository -> MySQL: UPDATE tours SET name = ?, length = ?, price = ?, date = ?, difficulty = ?, country_id = ?, mainPhoto = ? WHERE id = ? 
TourService -> TourMapper: toDtoWithoutReviews(tour) 
TourController -> Frontend: tour

### Delete Tour

Frontend -> TourController: deleteTour(id) TourController -> TourService: deleteById(id) TourService -> TourRepository: deleteById(id) TourRepository -> MySQL: UPDATE tours SET is_deleted = true WHERE id = ?

### Get All Tours

Frontend -> TourController: getAll(pageable) TourController -> TourRepository: findAll(pageable) TourRepository -> MySQL: SELECT * FROM tours WHERE is_deleted = false TourRepository -> TourController: tours TourController -> TourMapper: toDtoWithoutDetailsAndReviews(tours) TourController -> Frontend: tours

### Get Tour by ID

Frontend -> TourController: getById(id, page, size) TourController -> TourService: getById(id, page, size) TourService -> TourRepository: findById(id) TourRepository -> MySQL: SELECT * FROM tours WHERE id = ? TourRepository -> TourService: tour TourService -> TourMapper: toDto(tour) TourService -> ReviewRepository: findByTourId(id, pageable) ReviewRepository -> MySQL: SELECT * FROM reviews WHERE tour_id = ? ReviewRepository -> TourService: reviews TourService -> ReviewMapper: toDto(reviews) TourController -> Frontend: tour

### Get Most Rated Tours

Frontend -> TourController: getMostRatedTour() TourController -> TourService: getByRating() TourService -> TourRepository: findTop7ByRatingGreaterThanOrderByRatingDesc(0) TourRepository -> MySQL: SELECT * FROM tours WHERE rating > 0 ORDER BY rating DESC LIMIT 7 TourRepository -> TourService: tours TourService -> TourMapper: toDtoWithoutReviews(tours) TourController -> Frontend: tours

### Search Tours

Frontend -> TourController: search(routeType, difficulty, length, activity, date, duration, price, country) TourController -> TourSpecificationBuilder: build(routeType, difficulty, length, activity, date, duration, price, country) TourSpecificationBuilder -> TourSpecificationProviderManager: getSpecificationProvider(routeType) TourSpecificationProviderManager -> RouteTypeSpecificationProvider: getSpecification(routeType) TourSpecificationBuilder -> TourSpecificationProviderManager: getSpecificationProvider(difficulty) TourSpecificationProviderManager -> DifficultySpecificationProvider: getSpecification(difficulty) TourSpecificationBuilder -> TourSpecificationProviderManager: getSpecificationProvider(length) TourSpecificationProviderManager -> LengthSpecificationProvider: getSpecification(length) TourSpecificationBuilder -> TourSpecificationProviderManager: getSpecificationProvider(activity) TourSpecificationProviderManager -> ActivitySpecificationProvider: getSpecification(activity) TourSpecificationBuilder -> TourSpecificationProviderManager: getSpecificationProvider(date) TourSpecificationProviderManager -> DateSpecificationProvider: getSpecification(date) TourSpecificationBuilder -> TourSpecificationProviderManager: getSpecificationProvider(duration) TourSpecificationProviderManager -> DurationSpecificationProvider: getSpecification(duration) TourSpecificationBuilder -> TourSpecificationProviderManager: getSpecificationProvider(price) TourSpecificationProviderManager -> PriceSpecificationProvider: getSpecification(price) TourSpecificationBuilder -> TourSpecificationProviderManager: getSpecificationProvider(country) TourSpecificationProviderManager -> CountrySpecificationProvider: getSpecification(country) TourController -> TourRepository: findAll(specification, pageable) TourRepository -> MySQL: SELECT * FROM tours WHERE routeType IN (?) AND difficulty IN (?) AND length IN (?) AND activity IN (?) AND date IN (?) AND duration IN (?) AND price IN (?) AND country IN (?) TourRepository -> TourController: tours TourController -> TourMapper: toDtoWithoutReviews(tours) TourController -> Frontend: tours

### Create Review

Frontend -> TourController: createReview(tourId, content) TourController -> ReviewService: createReview(tourId, content) ReviewService -> TourRepository: findById(tourId) TourRepository -> MySQL: SELECT * FROM tours WHERE id = ? ReviewService -> ReviewMapper: toEntity(content) ReviewService -> ReviewRepository: save(review) ReviewRepository -> MySQL: INSERT INTO reviews (content, tour_id, user_id, date_created) VALUES (?, ?, ?, ?) ReviewService -> ReviewMapper: toDto(review) TourController -> Frontend: review


### Delete Review
Frontend -> TourController: deleteReview(tourId, reviewId) TourController -> ReviewService: deleteById(reviewId, tourId) ReviewService -> ReviewRepository: deleteById(reviewId) ReviewRepository -> MySQL: DELETE FROM reviews WHERE id = ?

### Update Review
Frontend -> TourController: updateReview(tourId, reviewId, content) TourController -> ReviewService: updateReview(tourId, reviewId, content) ReviewService -> ReviewRepository: findById(reviewId) ReviewRepository -> MySQL: SELECT * FROM reviews WHERE id = ? ReviewService -> ReviewMapper: updateEntityFromDto(review, content) ReviewService -> ReviewRepository: save(review) ReviewRepository -> MySQL: UPDATE reviews SET content = ? WHERE id = ? ReviewService -> ReviewMapper: toDto(review) TourController -> Frontend: review

### Get All Reviews by User
Frontend -> ReviewController: getAllReviewsByUser(userId, pageable) ReviewController -> ReviewService: getAllByUserId(userId, pageable) ReviewService -> ReviewRepository: findReviewsByUserId(userId, pageable) ReviewRepository -> MySQL: SELECT * FROM reviews WHERE user_id = ? ReviewRepository -> ReviewService: reviews ReviewService -> ReviewMapper: toDto(reviews) ReviewController -> Frontend: reviews

### Add Tour to Bookmarks
Frontend -> UserProfileController: addToBookmark(tourId) UserProfileController -> BookmarkService: addToBookmarks(tourId, userId) BookmarkService -> TourRepository: findById(tourId) TourRepository -> MySQL: SELECT * FROM tours WHERE id = ? BookmarkService -> BookmarkRepository: save(bookmark) BookmarkRepository -> MySQL: INSERT INTO bookmarks (user_id, tour_id, added_at) VALUES (?, ?, ?) BookmarkService -> BookmarkMapper: toDto(bookmark) UserProfileController -> Frontend: bookmark

### Update User Profile
Frontend -> UserProfileController: updateUserProfile(countryId, city, userPhoto) UserProfileController -> UserProfileService: updateUserProfile(countryId, city, userPhoto) UserProfileService -> UserProfileRepository: findByUserId(userId) UserProfileRepository -> MySQL: SELECT * FROM user_profiles WHERE user_id = ? UserProfileService -> UserProfileMapper: updateFromDto(userProfile, countryId, city, userPhoto) UserProfileService -> CountryRepository: findById(countryId) CountryRepository -> MySQL: SELECT * FROM countries WHERE id = ? UserProfileService -> UserProfileRepository: save(userProfile) UserProfileRepository -> MySQL: UPDATE user_profiles SET country_id = ?, city = ?, userPhoto = ? WHERE user_id = ? UserProfileService -> UserProfileMapper: toDto(userProfile) UserProfileController -> Frontend: userProfile

### Get Bookmarks
Frontend -> UserProfileController: getBookmarksByUserId(userId) UserProfileController -> BookmarkService: getByUserId(userId) BookmarkService -> BookmarkRepository: findByUser_Id(userId) BookmarkRepository -> MySQL: SELECT * FROM bookmarks WHERE user_id = ? BookmarkRepository -> BookmarkService: bookmarks BookmarkService -> BookmarkMapper: toDto(bookmarks) UserProfileController -> Frontend: bookmarks

### Get User Profile
Frontend -> UserProfileController: getUserProfile(userId) UserProfileController -> UserProfileService: getById(userId) UserProfileService -> UserProfileRepository: findByUserId(userId) UserProfileRepository -> MySQL: SELECT * FROM user_profiles WHERE user_id = ? UserProfileRepository -> UserProfileService: userProfile UserProfileService -> UserProfileMapper: toDto(userProfile) UserProfileController -> Frontend: userProfile

### Delete Tour from Bookmarks
Frontend -> UserProfileController: deleteBookmark(tourId) UserProfileController -> BookmarkService: deleteBookmarkById(tourId, userId) BookmarkService -> BookmarkRepository: findById(bookmarkId) BookmarkRepository -> MySQL: SELECT * FROM bookmarks WHERE user_id = ? AND tour_id = ? BookmarkRepository -> BookmarkService: bookmark BookmarkService -> BookmarkRepository: delete(bookmark) BookmarkRepository -> MySQL: DELETE FROM bookmarks WHERE user_id = ? AND tour_id = ?

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
