# Course Registration Application

## Project Structure

This project uses a multi-module Maven structure.

- `pom.xml`: Parent POM containing shared properties, dependency management, plugin configuration, and module declarations.
- `course-registration-app/`: Child module containing the application source code and tests.
- `course-registration-app/src/main/java/`: Java application source files.
- `course-registration-app/src/test/java/`: JUnit test files.

## Requirements

- Java 17
- Maven 3.9 or compatible

## Build and Test

Run these commands from the repository root:

```bash
mvn clean test
mvn package

