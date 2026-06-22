FROM gradle:jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/ProjectMiddleware-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]