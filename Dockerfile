# ---- Build Stage ----
FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
COPY images ./images
RUN mvn clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY --from=build /build/target/marketplace-spring-ca-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
