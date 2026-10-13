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

## Registration Audit Log

The application maintains an in-memory audit log in `RegistrationService`.

Each successful registration creates one entry in the following format:

`Student <studentId> registered for course <courseCode>`

Example:

`Student S301 registered for course CS501`

The log records successful registrations only. Rejected registrations do not
create entries, and a failed attempt does not remove previous entries.

The audit log exists only in memory and is not persisted to a database or file.

## Registration Audit Log

The application maintains an in-memory audit log in the
RegistrationService class.

Each successful student registration creates one audit entry
containing the student's ID and the course code.

### Audit Entry Format

The format used is:

Student <studentId> registered for course <courseCode>

Example:

Student S301 registered for course CS501

### Audit Rules

- Successful registrations create one audit entry each.
- Rejected registrations do not create audit entries.
- Failed registration attempts do not remove previous entries.
- The audit log is maintained in memory only and is not
  stored in a database or external file.
