package com.university;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    @Test
    void shouldRegisterStudentForValidCourse() {
        Student student = new Student("S101", "Anita");
        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null);

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS301"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectInactiveCourse() {
        Student student = new Student("S101", "Anita");
        Course course = new Course(
                "CS301", "Data Structures", 4, 30, false, null);

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
    }

    @Test
    void shouldRejectMissingPrerequisite() {
        Student student = new Student("S101", "Anita");
        Course course = new Course(
                "CS401", "Advanced Algorithms", 4, 30, true, "CS301");

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS401"));
    }

    @Test
    void shouldAllowRegistrationWhenPrerequisiteIsCompleted() {
        Student student = new Student("S101", "Anita");
        student.addCompletedPrerequisite("CS301");

        Course course = new Course(
                "CS401", "Advanced Algorithms", 4, 30, true, "CS301");

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS401"));
    }

    @Test
    void shouldRejectDuplicateRegistration() {
        Student student = new Student("S101", "Anita");
        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null);

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));

        boolean secondAttempt = service.register(student, course);

        assertFalse(secondAttempt);
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectRegistrationWhenCreditLimitIsExceeded() {
        Student student = new Student("S101", "Anita");

        student.addRegistration("CS101", 4);
        student.addRegistration("CS102", 4);
        student.addRegistration("CS201", 4);
        student.addRegistration("CS202", 4);
        student.addRegistration("CS203", 4);

        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null);

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
    }

    @Test
    void shouldRegisterWhenOneSeatRemains() {
        Student student = new Student("S102", "Rahul");
        Course course = new Course(
                "CS302", "Database Systems", 3, 2, true, null);

        course.incrementEnrollment();

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS302"));
        assertEquals(3, student.getTotalCredits());
        assertEquals(2, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectRegistrationWhenCourseIsFull() {
        Student student = new Student("S103", "Priya");
        Course course = new Course(
                "CS303", "Computer Networks", 3, 1, true, null);

        course.incrementEnrollment();

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS303"));
        assertEquals(0, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void rejectedFullCourseRegistrationShouldNotChangeStudentOrCourseState() {
        Student student = new Student("S104", "Arun");
        Course course = new Course(
                "CS304", "Operating Systems", 4, 1, true, null);

        course.incrementEnrollment();

        RegistrationService service = new RegistrationService();

        int initialCredits = student.getTotalCredits();
        int initialEnrollment = course.getCurrentEnrollment();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS304"));
        assertEquals(initialCredits, student.getTotalCredits());
        assertEquals(initialEnrollment, course.getCurrentEnrollment());
    }

    @Test
    void successfulRegistrationShouldAddAuditLogEntry() {
        Student student = new Student("S201", "Rahul");
        Course course = new Course(
                "CS401", "Software Engineering", 3, 5, true, null);

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertEquals(1, service.getAuditLog().size());
        assertTrue(service.getAuditLog().get(0).contains("S201"));
        assertTrue(service.getAuditLog().get(0).contains("CS401"));
    }

    @Test
    void rejectedRegistrationShouldNotAddAuditLogEntry() {
        Student student = new Student("S202", "Priya");
        Course course = new Course(
                "CS402", "Computer Networks", 3, 1, true, null);

        course.incrementEnrollment();

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertTrue(service.getAuditLog().isEmpty());
    }

    @Test
    void failedRegistrationShouldPreservePreviousAuditEntry() {
        Student firstStudent = new Student("S301", "Rahul");
        Student secondStudent = new Student("S302", "Priya");
        Student thirdStudent = new Student("S303", "Arun");

        Course course = new Course(
                "CS501", "Database Systems", 3, 2, true, null);

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(firstStudent, course));
        assertEquals(1, service.getAuditLog().size());

        assertTrue(service.register(secondStudent, course));
        assertEquals(2, service.getAuditLog().size());

        assertFalse(service.register(thirdStudent, course));

        assertEquals(2, service.getAuditLog().size());

        assertTrue(service.getAuditLog().get(0).contains("S301"));
        assertTrue(service.getAuditLog().get(0).contains("CS501"));

        assertTrue(service.getAuditLog().get(1).contains("S302"));
        assertTrue(service.getAuditLog().get(1).contains("CS501"));
    }
}
