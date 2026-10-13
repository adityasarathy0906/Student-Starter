FROM eclipse-temurin:17-jre
WORKDIR /app
COPY course-registration-app/target/course-registration-app-1.0-SNAPSHOT.jar app.jar
CMD ["java", "-version"]
