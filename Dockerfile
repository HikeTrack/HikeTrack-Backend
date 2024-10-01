FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /application
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -Dcheckstyle.skip=true

FROM openjdk:17-jdk AS builder2
WORKDIR /application
COPY --from=builder /application/target/*.jar application.jar
ENV SPRING_PROFILES_ACTIVE=local
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:17-jdk
WORKDIR /application
COPY --from=builder2 /application/dependencies/ ./
COPY --from=builder2 /application/spring-boot-loader/ ./
COPY --from=builder2 /application/snapshot-dependencies/ ./
COPY --from=builder2 /application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8080
