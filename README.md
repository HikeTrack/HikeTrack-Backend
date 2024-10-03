# HikeTrack-Backend

Overview of Project HikeTrack-Backend:                          // ask this to machinet after project done
Technologies:
Name	Version
Spring Boot	3.3.3
Spring Data JPA	3.3.3
Spring Web	3.3.3
Liquibase	4.18.2
MySQL Connector J	8.0.33
Lombok	1.18.24
Spring Boot Starter Test	3.3.3
Spring Boot Starter Actuator	3.3.3
Springdoc Openapi UI	1.8.0
Testcontainers MySQL	1.20.1
MapStruct	1.6.0
JJWT API	0.11.5
JJWT Impl	0.11.5
JJWT Jackson	0.11.5
MapStruct Processor	1.5.5.Final
Spring Security Core	6.3.3
Spring Security Web	6.3.3
Spring Security Config	6.3.3
Spring Boot Starter OAuth2 Client	3.3.3
Spring Boot Starter Data Redis	3.3.3
Spring Boot Starter Mail	3.3.3
Maven	3.9.9
Checkstyle	3.5.0
Docker	latest
Redis	7.4.0
MySQL	8.0.33
Project details:
Detailed project folders description:
COPY
C:\Users\suige\IdeaProjects\HikeTrack-Backend
├── .github
│   └── workflows
│       ├── aws-publish.yml - Workflow for publishing the application to AWS Elastic Beanstalk
│       ├── ci.yml - Workflow for continuous integration, including building and testing the application
│       └── docker-publish.yml - Workflow for building and pushing Docker images to Docker Hub
├── .mvn
│   └── wrapper
│       ├── maven-wrapper.jar - Maven wrapper executable
│       └── maven-wrapper.properties - Maven wrapper configuration
└── src
├── main
│   ├── resources
│   │   └── db
│   │       └── changelog
│   │           └── changeset
│   │               ├── 01-create-countries-table.yaml - Liquibase changeset for creating the countries table
│   │               ├── 02-insert-data-to-countries-table.yaml - Liquibase changeset for inserting data into the countries table
│   │               ├── 03-create-tours-table.yaml - Liquibase changeset for creating the tours table
│   │               ├── 04-create-details-table.yaml - Liquibase changeset for creating the details table
│   │               ├── 05-create-review-table.yaml - Liquibase changeset for creating the reviews table
│   │               ├── 07-create-user-table.yaml - Liquibase changeset for creating the users table
│   │               ├── 08-create-role-table.yaml - Liquibase changeset for creating the roles table
│   │               ├── 09-create-users-roles-table.yaml - Liquibase changeset for creating the users_roles table
│   │               ├── 10-insert-data-to-user-table.yaml - Liquibase changeset for inserting data into the users table
│   │               ├── 11-create-user-profile-table.yaml - Liquibase changeset for creating the user_profiles table
│   │               └── 12-create-bookmarks-table.yaml - Liquibase changeset for creating the bookmarks table
│   └── java
│       └── com
│           └── hiketrackbackend
│               └── hiketrackbackend
│                   ├── config
│                   │   ├── MapperConfig.java - Configuration for MapStruct mapper
│                   │   ├── RedisConfig.java - Configuration for Redis
│                   │   ├── SecurityConfig.java - Configuration for Spring Security
│                   │   ├── SwaggerConfig.java - Configuration for Swagger
│                   │   └── WebConfig.java - Configuration for Web MVC
│                   ├── controller
│                   │   ├── AuthenticationController.java - Controller for authentication-related endpoints
│                   │   ├── CountryController.java - Controller for country-related endpoints
│                   │   ├── ReviewController.java - Controller for review-related endpoints
│                   │   ├── TestController.java - Test controller
│                   │   ├── TourController.java - Controller for tour-related endpoints
│                   │   └── UserProfileController.java - Controller for user profile-related endpoints
│                   ├── exception
│                   │   ├── CustomGlobalExceptionHandler.java - Custom exception handler
│                   │   ├── EntityNotFoundException.java - Custom exception for entity not found
│                   │   ├── RegistrationException.java - Custom exception for registration errors
│                   │   └── SpecificationNotFoundException.java - Custom exception for specification not found
│                   ├── mapper
│                   │   ├── BookmarkMapper.java - Mapper for Bookmark entities
│                   │   ├── CountryMapper.java - Mapper for Country entities
│                   │   ├── ReviewMapper.java - Mapper for Review entities
│                   │   ├── TourMapper.java - Mapper for Tour entities
│                   │   ├── UserMapper.java - Mapper for User entities
│                   │   └── UserProfileMapper.java - Mapper for UserProfile entities
│                   ├── model
│                   │   ├── bookmark
│                   │   │   ├── Bookmark.java - Model for bookmarks
│                   │   │   └── BookmarkId.java - Embedded ID for bookmarks
│                   │   ├── country
│                   │   │   ├── Continent.java - Enum for continents
│                   │   │   └── Country.java - Model for countries
│                   │   ├── details
│                   │   │   ├── Activity.java - Enum for activities
│                   │   │   ├── Details.java - Model for tour details
│                   │   │   └── RouteType.java - Enum for route types
│                   │   └── tour
│                   │       ├── Difficulty.java - Enum for tour difficulties
│                   │       └── Tour.java - Model for tours
│                   ├── repository
│                   │   ├── country
│                   │   │   ├── ContinentSpecificationProvider.java - Specification provider for continent filtering
│                   │   │   ├── CountryRepository.java - Repository for Country entities
│                   │   │   ├── CountrySpecificationBuilder.java - Specification builder for Country entities
│                   │   │   ├── CountrySpecificationProviderManager.java - Specification provider manager for Country entities
│                   │   │   └── NameSpecificationProvider.java - Specification provider for name filtering
│                   │   └── tour
│                   │       ├── ActivitySpecificationProvider.java - Specification provider for activity filtering
│                   │       ├── CountrySpecificationProvider.java - Specification provider for country filtering
│                   │       ├── DateSpecificationProvider.java - Specification provider for date filtering
│                   │       ├── DifficultySpecificationProvider.java - Specification provider for difficulty filtering
│                   │       ├── DurationSpecificationProvider.java - Specification provider for duration filtering
│                   │       ├── LengthSpecificationProvider.java - Specification provider for length filtering
│                   │       ├── PriceSpecificationProvider.java - Specification provider for price filtering
│                   │       ├── RouteTypeSpecificationProvider.java - Specification provider for route type filtering
│                   │       ├── TourRepository.java - Repository for Tour entities
│                   │       └── TourSpecificationBuilder.java - Specification builder for Tour entities
│                   │           └── TourSpecificationProviderManager.java - Specification provider manager for Tour entities
│                   ├── security
│                   │   ├── AuthenticationService.java - Service for authentication
│                   │   ├── CustomOAuth2UserService.java - Custom OAuth2 user service
│                   │   ├── CustomUserDetailsService.java - Custom user details service
│                   │   ├── JwtAuthenticationFilter.java - JWT authentication filter
│                   │   ├── JwtTokenServiceImpl.java - Service for managing JWT tokens
│                   │   ├── JwtUtil.java - Utility class for JWT operations
│                   │   ├── OAuth2AuthenticationSuccessHandler.java - Authentication success handler for OAuth2
│                   │   ├── TokenService.java - Abstract class for token services
│                   │   └── UUIDTokenServiceImpl.java - Service for managing UUID tokens
│                   ├── service
│                   │   └── impl
│                   │       ├── BookmarkServiceImpl.java - Implementation of BookmarkService
│                   │       ├── CountryServiceImpl.java - Implementation of CountryService
│                   │       ├── MailSenderImpl.java - Implementation of MailSender
│                   │       ├── ReviewServiceImpl.java - Implementation of ReviewService
│                   │       ├── TourServiceImpl.java - Implementation of TourService
│                   │       ├── UserProfileServiceImpl.java - Implementation of UserProfileService
│                   │       └── UserServiceImpl.java - Implementation of UserService
│                   ├── validation
│                   │   ├── FieldMatch.java - Constraint annotation for field matching
│                   │   ├── FieldMatchValidation.java - Constraint validator for field matching
│                   │   ├── Password.java - Constraint annotation for password validation
│                   │   ├── PasswordValidation.java - Constraint validator for password validation
│                   │   ├── TokenValidation.java - Constraint validator for token validation
│                   │   └── ValidToken.java - Constraint annotation for token validation
│                   └── dto
│                       ├── bookmark
│                       │   ├── BookmarkRequestDto.java - DTO for bookmark requests
│                       │   └── BookmarkRespondDto.java - DTO for bookmark responses
│                       ├── country
│                       │   ├── CountryRequestDto.java - DTO for country requests
│                       │   ├── CountryRespondDto.java - DTO for country responses
│                       │   └── CountrySearchParameters.java - DTO for country search parameters
│                       ├── details
│                       │   ├── DetailsRequestDto.java - DTO for tour details requests
│                       │   └── DetailsRespondDto.java - DTO for tour details responses
│                       ├── reviews
│                       │   ├── ReviewRequestDto.java - DTO for review requests
│                       │   └── ReviewsRespondDto.java - DTO for review responses
│                       ├── tour
│                       │   ├── TourRequestDto.java - DTO for tour requests
│                       │   ├── TourRespondDto.java - DTO for tour responses
│                       │   ├── TourRespondWithoutDetailsAndReviews.java - DTO for tour responses without details and reviews
│                       │   ├── TourRespondWithoutReviews.java - DTO for tour responses without reviews
│                       │   └── TourSearchParameters.java - DTO for tour search parameters
│                       ├── user
│                       │   ├── UserForgotRequestDto.java - DTO for forgot password requests
│                       │   ├── UserForgotRespondDto.java - DTO for forgot password responses
│                       │   ├── UserLoginRequestDto.java - DTO for login requests
│                       │   ├── UserLoginResponseDto.java - DTO for login responses
│                       │   ├── UserRegistrationRequestDto.java - DTO for registration requests
│                       │   ├── UserRegistrationRespondDto.java - DTO for registration responses
│                       │   └── UserRestoreRequestDto.java - DTO for password restore requests
│                       └── userProfile
│                           ├── UserProfileRequestDto.java - DTO for user profile requests
│                           └── UserProfileRespondDto.java - DTO for user profile responses
└── test
├── resources
│   └── application.properties - Application properties for testing
└── java
└── com
└── hiketrackbackend
└── hiketrackbackend
└── HikeTrackBackendApplicationTests.java - Test class for the application

Full Business logic:
Authentication:
Registration: Allows users to create new accounts with email, password, first name, and last name.
Login: Enables users to authenticate using their email and password.
Forgot Password: Provides a mechanism for users to reset their passwords by sending a reset link to their email.
Update Password: Allows users to update their passwords after receiving a reset link.
Logout: Logs users out of their accounts.
Country Management:
Create Country: Allows authorized users to create new countries with name, continent, and photo.
Search Countries: Enables users to search for countries based on continent or country name.
Get Country by ID: Retrieves a specific country by its ID.
Get All Countries: Retrieves a list of all countries.
Tour Management:
Create Tour: Allows authorized users to create new tours with name, length, price, date, difficulty, country ID, main photo, and details.
Update Tour: Allows authorized users to update existing tours.
Delete Tour: Allows authorized users to soft delete tours, making them invisible to users.
Get All Tours: Retrieves a list of all tours.
Get Tour by ID: Retrieves a specific tour by its ID, including details and reviews.
Get Most Rated Tours: Retrieves a list of the top 7 most rated tours.
Search Tours: Enables users to search for tours based on various parameters, including route type, difficulty, length, activity, date, duration, price, and country.
Review Management:
Create Review: Allows users to create reviews for tours.
Delete Review: Allows users to delete their own reviews.
Update Review: Allows users to update their own reviews.
Get All Reviews by User: Retrieves a list of all reviews created by a specific user.
User Profile Management:
Update User Profile: Allows users to update their profile information, including country, city, and user photo.
Get User Profile: Retrieves the profile information of the currently logged-in user.
Add Tour to Bookmarks: Allows users to add tours to their bookmarks.
Get Bookmarks: Retrieves a list of all tours bookmarked by the currently logged-in user.
Delete Tour from Bookmarks: Allows users to remove tours from their bookmarks.
Dependency management:
Maven is used for dependency management and project build.
The project uses a Maven wrapper to ensure consistent Maven versions across development environments.
Notes:
The project uses Liquibase for database migrations.
The project uses Spring Security for authentication and authorization.
The project uses JWT for authentication tokens.
The project uses OAuth2 for Google authentication.
The project uses Redis for caching and storing JWT tokens.
The project uses Springdoc OpenAPI for API documentation.
The project uses Testcontainers for testing with Docker containers.
The project uses MapStruct for object mapping.
The project uses Lombok for code generation.
The project uses Checkstyle for code style enforcement.
All endpoints/All events the project exposes and listens:
HTTP
/auth/registration - POST - Register a new user
/auth/login - POST - Login a user
/auth/forgot-password - POST - Send a password reset link to the user's email
/auth/reset-password - GET - Validate the password reset link and redirect to the password reset page
/auth/update-password/{email} - POST - Update the user's password
/auth/logout - POST - Logout the user
/countries/new - POST - Create a new country
/countries/search - GET - Search for countries
/countries/{id} - GET - Get a country by ID
/countries - GET - Get all countries
/tours/new - POST - Create a new tour
/tours/{tourId} - PUT - Update a tour
/tours/{id} - DELETE - Delete a tour
/tours - GET - Get all tours
/tours/{id} - GET - Get a tour by ID
/tours/popular - GET - Get the most rated tours
/tours/search - GET - Search for tours
/tours/{tourId}/reviews - POST - Create a new review for a tour
/tours/{tourId}/reviews/{reviewId} - DELETE - Delete a review
/tours/{tourId}/reviews/{reviewId} - PUT - Update a review
/profile/bookmarks/new - POST - Add a tour to bookmarks
/profile - PUT - Update the user's profile
/profile/bookmarks - GET - Get all bookmarks
/profile - GET - Get the user's profile
/profile/bookmarks/{tourId} - DELETE - Delete a tour from bookmarks
Redis
token - String - Store JWT tokens for blacklisting
email - String - Store email for password reset requests
All downstream services
MySQL - Database for storing application data
Redis - Cache for JWT tokens and password reset requests
Google OAuth2 - Authentication provider for Google logins
Mail Server - Server for sending password reset emails
All upstream services
Frontend - Client application that interacts with the backend API
Testing
Dependencies:
Spring Boot Starter Test
Testcontainers MySQL
Summary of Found Test Flows:
Unit tests for individual classes and methods
Integration tests for interacting with the database and other services
End-to-end tests for verifying the functionality of the entire application
Fixtures Storages:
Test data is stored in the src/test/resources folder.
Useful to know:
The project uses Testcontainers to run MySQL and Redis containers for testing.
The project uses Mockito for mocking dependencies in unit tests.
Deployment
Summary:
The project can be deployed to AWS Elastic Beanstalk using the provided GitHub workflow.
The project can be deployed to Docker Hub using the provided GitHub workflow.
Setting Up the Development Environment
Install Java Development Kit (JDK):
Download and install JDK 17 from https://www.oracle.com/java/technologies/downloads/#java17.
Install Maven:
Download and install Maven from https://maven.apache.org/download.cgi.
Install Docker:
Download and install Docker Desktop from https://www.docker.com/products/docker-desktop.
Install Redis:
Download and install Redis from https://redis.io/docs/getting-started/install/.
Install MySQL:
Download and install MySQL from https://dev.mysql.com/downloads/mysql/.
Set up environment variables:
Create a .env file in the project root directory and set the following environment variables:
MYSQLDB_ROOT_PASSWORD - Root password for MySQL
MYSQLDB_USER - Username for MySQL
MYSQLDB_PASSWORD - Password for MySQL
MYSQLDB_DATABASE - Database name for MySQL
MYSQLDB_LOCAL_PORT - Local port for MySQL
MYSQLDB_DOCKER_PORT - Docker port for MySQL
SPRING_REDIS_PORT - Local port for Redis
SPRING_REDIS_DOCKER_PORT - Docker port for Redis
SPRING_LOCAL_PORT - Local port for the application
SPRING_DOCKER_PORT - Docker port for the application
DEBUG_PORT - Debug port for the application
JWT_SECRET - Secret key for JWT
GOOGLE_CLIENT_ID - Client ID for Google OAuth2
GOOGLE_CLIENT_SECRET - Client secret for Google OAuth2
SMTP_PASSWORD - Password for SMTP server
MAIL_PORT - Port for SMTP server
FROM_EMAIL - Email address for sending emails
REDIS_HOST - Hostname for Redis
REDIS_PORT - Port for Redis
Running the Project in the Development Environment
Start MySQL:
Open a terminal and navigate to the MySQL installation directory.
Run the following command:
COPY
mysqld --defaults-file=/path/to/my.cnf
Replace /path/to/my.cnf with the actual path to the MySQL configuration file.
Start Redis:
Open a terminal and navigate to the Redis installation directory.
Run the following command:
COPY
redis-server
Build the project:
Open a terminal and navigate to the project root directory.
Run the following command:
COPY
mvn clean install
Run the application:
Open a terminal and navigate to the project root directory.
Run the following command:
COPY
mvn spring-boot:run
Access the application:
Open a web browser and navigate to http://localhost:8080.
Prerequisites
COPY
mysqld --defaults-file=/path/to/my.cnf
Start MySQL server using the configuration file.

COPY
redis-server
Start Redis server.

Execution Instructions
Local:
COPY
mvn spring-boot:run
Run the application using Maven.
Debug:
COPY
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:$DEBUG_PORT"
Run the application in debug mode with the specified debug port.
Docker:
COPY
docker-compose up -d
Build and run the application in a Docker container using Docker Compose.
Sequence diagrams for each API/event with all downstream dependencies and data details:
Event: User registration
COPY
Frontend->AuthenticationController: registration(email, password, firstName, lastName)
AuthenticationController->UserRepository: save(user)
UserRepository->MySQL: INSERT INTO users (email, password, firstName, lastName) VALUES (?, ?, ?, ?)
Event: User login
COPY
Frontend->AuthenticationController: login(email, password)
AuthenticationController->AuthenticationManager: authenticate(email, password)
AuthenticationManager->UserRepository: findByEmail(email)
UserRepository->MySQL: SELECT * FROM users WHERE email = ?
AuthenticationManager->JwtUtil: generateToken(email)
AuthenticationController->Frontend: token
Event: Forgot password
COPY
Frontend->AuthenticationController: forgotPassword(email)
AuthenticationController->UserService: createRestoreRequest(email)
UserService->UserRepository: findByEmail(email)
UserRepository->MySQL: SELECT * FROM users WHERE email = ?
UserService->UUIDTokenServiceImpl: saveTokenToDB(token, email)
UUIDTokenServiceImpl->Redis: SET token email 1 hour
UserService->MailSender: sendResetPasswordMailToGMail(email, token)
MailSender->SMTP Server: SEND EMAIL
Event: Update password
COPY
Frontend->AuthenticationController: updatePassword(password, repeatPassword, email)
AuthenticationController->UserService: updatePassword(password, repeatPassword, email)
UserService->UserRepository: findByEmail(email)
UserRepository->MySQL: SELECT * FROM users WHERE email = ?
UserService->UserRepository: save(user)
UserRepository->MySQL: UPDATE users SET password = ? WHERE email = ?
Event: Logout
COPY
Frontend->AuthenticationController: logout(token)
AuthenticationController->JwtTokenServiceImpl: saveTokenToDB(token, email)
JwtTokenServiceImpl->Redis: SET token email 20 minutes
Event: Create country
COPY
Frontend->CountryController: createCountry(name, continent, photo)
CountryController->CountryService: createCountry(name, continent, photo)
CountryService->CountryMapper: toEntity(name, continent, photo)
CountryService->CountryRepository: save(country)
CountryRepository->MySQL: INSERT INTO countries (name, continent, photo) VALUES (?, ?, ?)
CountryService->CountryMapper: toDto(country)
CountryController->Frontend: country
Event: Search countries
COPY
Frontend->CountryController: search(continent, countryName)
CountryController->CountrySpecificationBuilder: build(continent, countryName)
CountrySpecificationBuilder->CountrySpecificationProviderManager: getSpecificationProvider(continent)
CountrySpecificationProviderManager->ContinentSpecificationProvider: getSpecification(continent)
CountrySpecificationBuilder->CountrySpecificationProviderManager: getSpecificationProvider(countryName)
CountrySpecificationProviderManager->NameSpecificationProvider: getSpecification(countryName)
CountryController->CountryRepository: findAll(specification, pageable)
CountryRepository->MySQL: SELECT * FROM countries WHERE continent IN (?) AND name IN (?)
CountryRepository->CountryController: countries
CountryController->CountryMapper: toDto(countries)
CountryController->Frontend: countries
Event: Get country by ID
COPY
Frontend->CountryController: getById(id)
CountryController->CountryService: getById(id)
CountryService->CountryRepository: findById(id)
CountryRepository->MySQL: SELECT * FROM countries WHERE id = ?
CountryRepository->CountryService: country
CountryService->CountryMapper: toDto(country)
CountryController->Frontend: country
Event: Get all countries
COPY
Frontend->CountryController: getAll(pageable)
CountryController->CountryRepository: findAll(pageable)
CountryRepository->MySQL: SELECT * FROM countries
CountryRepository->CountryController: countries
CountryController->CountryMapper: toDto(countries)
CountryController->Frontend: countries
Event: Create tour
COPY
Frontend->TourController: createTour(name, length, price, date, difficulty, countryId, mainPhoto, details)
TourController->TourService: createTour(name, length, price, date, difficulty, countryId, mainPhoto, details)
TourService->TourMapper: toEntity(name, length, price, date, difficulty, countryId, mainPhoto, details)
TourService->CountryRepository: findById(countryId)
CountryRepository->MySQL: SELECT * FROM countries WHERE id = ?
TourService->TourRepository: save(tour)
TourRepository->MySQL: INSERT INTO tours (name, length, price, date, difficulty, country_id, mainPhoto) VALUES (?, ?, ?, ?, ?, ?, ?)
TourService->TourMapper: toDtoWithoutReviews(tour)
TourController->Frontend: tour
Event: Update tour
COPY
Frontend->TourController: updateTour(tourId, name, length, price, date, difficulty, countryId, mainPhoto, details)
TourController->TourService: updateTour(tourId, name, length, price, date, difficulty, countryId, mainPhoto, details)
TourService->TourRepository: findById(tourId)
TourRepository->MySQL: SELECT * FROM tours WHERE id = ?
TourService->TourMapper: updateEntityFromDto(tour, name, length, price, date, difficulty, countryId, mainPhoto, details)
TourService->TourRepository: save(tour)
TourRepository->MySQL: UPDATE tours SET name = ?, length = ?, price = ?, date = ?, difficulty = ?, country_id = ?, mainPhoto = ? WHERE id = ?
TourService->TourMapper: toDtoWithoutReviews(tour)
TourController->Frontend: tour
Event: Delete tour
COPY
Frontend->TourController: deleteTour(id)
TourController->TourService: deleteById(id)
TourService->TourRepository: deleteById(id)
TourRepository->MySQL: UPDATE tours SET is_deleted = true WHERE id = ?
Event: Get all tours
COPY
Frontend->TourController: getAll(pageable)
TourController->TourRepository: findAll(pageable)
TourRepository->MySQL: SELECT * FROM tours WHERE is_deleted = false
TourRepository->TourController: tours
TourController->TourMapper: toDtoWithoutDetailsAndReviews(tours)
TourController->Frontend: tours
Event: Get tour by ID
COPY
Frontend->TourController: getById(id, page, size)
TourController->TourService: getById(id, page, size)
TourService->TourRepository: findById(id)
TourRepository->MySQL: SELECT * FROM tours WHERE id = ?
TourRepository->TourService: tour
TourService->TourMapper: toDto(tour)
TourService->ReviewRepository: findByTourId(id, pageable)
ReviewRepository->MySQL: SELECT * FROM reviews WHERE tour_id = ?
ReviewRepository->TourService: reviews
TourService->ReviewMapper: toDto(reviews)
TourController->Frontend: tour
Event: Get most rated tours
COPY
Frontend->TourController: getMostRatedTour()
TourController->TourService: getByRating()
TourService->TourRepository: findTop7ByRatingGreaterThanOrderByRatingDesc(0)
TourRepository->MySQL: SELECT * FROM tours WHERE rating > 0 ORDER BY rating DESC LIMIT 7
TourRepository->TourService: tours
TourService->TourMapper: toDtoWithoutReviews(tours)
TourController->Frontend: tours
Event: Search tours
COPY
Frontend->TourController: search(routeType, difficulty, length, activity, date, duration, price, country)
TourController->TourSpecificationBuilder: build(routeType, difficulty, length, activity, date, duration, price, country)
TourSpecificationBuilder->TourSpecificationProviderManager: getSpecificationProvider(routeType)
TourSpecificationProviderManager->RouteTypeSpecificationProvider: getSpecification(routeType)
TourSpecificationBuilder->TourSpecificationProviderManager: getSpecificationProvider(difficulty)
TourSpecificationProviderManager->DifficultySpecificationProvider: getSpecification(difficulty)
TourSpecificationBuilder->TourSpecificationProviderManager: getSpecificationProvider(length)
TourSpecificationProviderManager->LengthSpecificationProvider: getSpecification(length)
TourSpecificationBuilder->TourSpecificationProviderManager: getSpecificationProvider(activity)
TourSpecificationProviderManager->ActivitySpecificationProvider: getSpecification(activity)
TourSpecificationBuilder->TourSpecificationProviderManager: getSpecificationProvider(date)
TourSpecificationProviderManager->DateSpecificationProvider: getSpecification(date)
TourSpecificationBuilder->TourSpecificationProviderManager: getSpecificationProvider(duration)
TourSpecificationProviderManager->DurationSpecificationProvider: getSpecification(duration)
TourSpecificationBuilder->TourSpecificationProviderManager: getSpecificationProvider(price)
TourSpecificationProviderManager->PriceSpecificationProvider: getSpecification(price)
TourSpecificationBuilder->TourSpecificationProviderManager: getSpecificationProvider(country)
TourSpecificationProviderManager->CountrySpecificationProvider: getSpecification(country)
TourController->TourRepository: findAll(specification, pageable)
TourRepository->MySQL: SELECT * FROM tours WHERE routeType IN (?) AND difficulty IN (?) AND length IN (?) AND activity IN (?) AND date IN (?) AND duration IN (?) AND price IN (?) AND country IN (?)
TourRepository->TourController: tours
TourController->TourMapper: toDtoWithoutReviews(tours)
TourController->Frontend: tours
Event: Create review
COPY
Frontend->TourController: createReview(tourId, content)
TourController->ReviewService: createReview(tourId, content)
ReviewService->TourRepository: findById(tourId)
TourRepository->MySQL: SELECT * FROM tours WHERE id = ?
ReviewService->ReviewMapper: toEntity(content)
ReviewService->ReviewRepository: save(review)
ReviewRepository->MySQL: INSERT INTO reviews (content, tour_id, user_id, date_created) VALUES (?, ?, ?, ?)
ReviewService->ReviewMapper: toDto(review)
TourController->Frontend: review
Event: Delete review
COPY
Frontend->TourController: deleteReview(tourId, reviewId)
TourController->ReviewService: deleteById(reviewId, tourId)
ReviewService->ReviewRepository: deleteById(reviewId)
ReviewRepository->MySQL: DELETE FROM reviews WHERE id = ?
Event: Update review
COPY
Frontend->TourController: updateReview(tourId, reviewId, content)
TourController->ReviewService: updateReview(tourId, reviewId, content)
ReviewService->ReviewRepository: findById(reviewId)
ReviewRepository->MySQL: SELECT * FROM reviews WHERE id = ?
ReviewService->ReviewMapper: updateEntityFromDto(review, content)
ReviewService->ReviewRepository: save(review)
ReviewRepository->MySQL: UPDATE reviews SET content = ? WHERE id = ?
ReviewService->ReviewMapper: toDto(review)
TourController->Frontend: review
Event: Get all reviews by user
COPY
Frontend->ReviewController: getAllReviewsByUser(userId, pageable)
ReviewController->ReviewService: getAllByUserId(userId, pageable)
ReviewService->ReviewRepository: findReviewsByUserId(userId, pageable)
ReviewRepository->MySQL: SELECT * FROM reviews WHERE user_id = ?
ReviewRepository->ReviewService: reviews
ReviewService->ReviewMapper: toDto(reviews)
ReviewController->Frontend: reviews
Event: Add tour to bookmarks
COPY
Frontend->UserProfileController: addToBookmark(tourId)
UserProfileController->BookmarkService: addToBookmarks(tourId, userId)
BookmarkService->TourRepository: findById(tourId)
TourRepository->MySQL: SELECT * FROM tours WHERE id = ?
BookmarkService->BookmarkRepository: save(bookmark)
BookmarkRepository->MySQL: INSERT INTO bookmarks (user_id, tour_id, added_at) VALUES (?, ?, ?)
BookmarkService->BookmarkMapper: toDto(bookmark)
UserProfileController->Frontend: bookmark
Event: Update user profile
COPY
Frontend->UserProfileController: updateUserProfile(countryId, city, userPhoto)
UserProfileController->UserProfileService: updateUserProfile(countryId, city, userPhoto)
UserProfileService->UserProfileRepository: findByUserId(userId)
UserProfileRepository->MySQL: SELECT * FROM user_profiles WHERE user_id = ?
UserProfileService->UserProfileMapper: updateFromDto(userProfile, countryId, city, userPhoto)
UserProfileService->CountryRepository: findById(countryId)
CountryRepository->MySQL: SELECT * FROM countries WHERE id = ?
UserProfileService->UserProfileRepository: save(userProfile)
UserProfileRepository->MySQL: UPDATE user_profiles SET country_id = ?, city = ?, userPhoto = ? WHERE user_id = ?
UserProfileService->UserProfileMapper: toDto(userProfile)
UserProfileController->Frontend: userProfile
Event: Get bookmarks
COPY
Frontend->UserProfileController: getBookmarksByUserId(userId)
UserProfileController->BookmarkService: getByUserId(userId)
BookmarkService->BookmarkRepository: findByUser_Id(userId)
BookmarkRepository->MySQL: SELECT * FROM bookmarks WHERE user_id = ?
BookmarkRepository->BookmarkService: bookmarks
BookmarkService->BookmarkMapper: toDto(bookmarks)
UserProfileController->Frontend: bookmarks
Event: Get user profile
COPY
Frontend->UserProfileController: getUserProfile(userId)
UserProfileController->UserProfileService: getById(userId)
UserProfileService->UserProfileRepository: findByUserId(userId)
UserProfileRepository->MySQL: SELECT * FROM user_profiles WHERE user_id = ?
UserProfileRepository->UserProfileService: userProfile
UserProfileService->UserProfileMapper: toDto(userProfile)
UserProfileController->Frontend: userProfile
Event: Delete tour from bookmarks
COPY
Frontend->UserProfileController: deleteBookmark(tourId)
UserProfileController->BookmarkService: deleteBookmarkById(tourId, userId)
BookmarkService->BookmarkRepository: findById(bookmarkId)
BookmarkRepository->MySQL: SELECT * FROM bookmarks WHERE user_id = ? AND tour_id = ?
BookmarkRepository->BookmarkService: bookmark
BookmarkService->BookmarkRepository: delete(bookmark)
BookmarkRepository->MySQL: DELETE FROM bookmarks WHERE user_id = ? AND tour_id = ?