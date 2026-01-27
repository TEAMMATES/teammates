package teammates.storage.sqlsearch;

import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link InstructorSearchDocument}.
 */
public class InstructorSearchDocumentTest extends BaseTestCase {

    @Test
    public void testGetSearchableFields_containsAllRequiredFields() {
        // Setup
        Course course = createTestCourse("test-course", "Test Course");
        Instructor instructor = createTestInstructor(course, "instructor@example.com",
                "John Doe", "Display Name", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        InstructorSearchDocument document = new InstructorSearchDocument(instructor, course);

        // Execute
        Map<String, Object> fields = document.getSearchableFields();

        // Verify: All required fields are present
        assertNotNull(fields);
        assertEquals(fields.get("id"), instructor.getId());
        assertEquals(fields.get("courseId"), instructor.getCourseId());
        assertEquals(fields.get("email"), "instructor@example.com");
        assertNotNull(fields.get("_text_"));

        // Verify _text_ contains all searchable text
        String searchableText = (String) fields.get("_text_");
        assertTrue(searchableText.contains("John Doe"));
        assertTrue(searchableText.contains("instructor@example.com"));
        assertTrue(searchableText.contains("test-course"));
        assertTrue(searchableText.contains("Test Course"));
        assertTrue(searchableText.contains("Display Name"));
        assertTrue(searchableText.contains(
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.getRoleName()));
    }

    @Test
    public void testGetSearchableFields_withNullCourse_handlesGracefully() {
        // Setup
        Course course = createTestCourse("test-course", "Test Course");
        Instructor instructor = createTestInstructor(course, "instructor@example.com",
                "John Doe", "Display Name", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        InstructorSearchDocument document = new InstructorSearchDocument(instructor, null);

        // Execute
        Map<String, Object> fields = document.getSearchableFields();

        // Verify: Fields are still created correctly even with null course
        assertNotNull(fields);
        assertEquals(fields.get("id"), instructor.getId());
        assertEquals(fields.get("courseId"), instructor.getCourseId());
        assertEquals(fields.get("email"), "instructor@example.com");

        // Verify _text_ doesn't crash with null course
        String searchableText = (String) fields.get("_text_");
        assertNotNull(searchableText);
        assertTrue(searchableText.contains("John Doe"));
    }

    @Test
    public void testGetSearchableFields_withSpecialCharacters_escapesCorrectly() {
        // Setup: Instructor with special characters in name/email
        Course course = createTestCourse("test-course", "Test Course");
        Instructor instructor = createTestInstructor(course, "test+email@example.com",
                "John O'Brien", "Team & Co.",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        InstructorSearchDocument document = new InstructorSearchDocument(instructor, course);

        // Execute
        Map<String, Object> fields = document.getSearchableFields();

        // Verify: Special characters are included in searchable text
        assertNotNull(fields);
        String searchableText = (String) fields.get("_text_");
        assertTrue(searchableText.contains("John O'Brien"));
        assertTrue(searchableText.contains("test+email@example.com"));
    }

    @Test
    public void testGetSearchableFields_differentInstructors_haveDifferentFields() {
        // Setup
        Course course1 = createTestCourse("course1", "Course One");
        Course course2 = createTestCourse("course2", "Course Two");

        Instructor instructor1 = createTestInstructor(course1, "inst1@example.com",
                "Alice", "Display A", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        Instructor instructor2 = createTestInstructor(course2, "inst2@example.com",
                "Bob", "Display B", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        InstructorSearchDocument doc1 = new InstructorSearchDocument(instructor1, course1);
        InstructorSearchDocument doc2 = new InstructorSearchDocument(instructor2, course2);

        // Execute
        Map<String, Object> fields1 = doc1.getSearchableFields();
        Map<String, Object> fields2 = doc2.getSearchableFields();

        // Verify: Fields are different for different instructors
        assertNotNull(fields1);
        assertNotNull(fields2);
        assertEquals(fields1.get("email"), "inst1@example.com");
        assertEquals(fields2.get("email"), "inst2@example.com");
        assertEquals(fields1.get("courseId"), "course1");
        assertEquals(fields2.get("courseId"), "course2");
    }

    // Helper methods
    private Course createTestCourse(String courseId, String courseName) {
        return new Course(courseId, courseName, "UTC", "Test Institute");
    }

    private Instructor createTestInstructor(Course course, String email, String name,
            String displayName, InstructorPermissionRole role) {
        InstructorPrivileges privileges = new InstructorPrivileges();
        return new Instructor(course, name, email, false, displayName, role, privileges);
    }
}

