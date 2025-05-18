FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /wheelpicker
COPY pom.xml ./
COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /wheelpicker
COPY --from=build /wheelpicker/target/wheelpicker-*.jar wheelpicker.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "wheelpicker.jar"]