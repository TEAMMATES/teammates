package teammates.storage.sqlsearch;

import java.util.Map;

import org.testng.annotations.Test;

import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link StudentSearchDocument}.
 */
public class StudentSearchDocumentTest extends BaseTestCase {

    @Test
    public void testGetSearchableFields_containsAllRequiredFields() {
        // Setup
        Course course = createTestCourse("test-course", "Test Course");
        Student student = createTestStudent(course, "student@example.com", "John Doe", "Team Alpha", "Section 1");

        StudentSearchDocument document = new StudentSearchDocument(student, course);

        // Execute
        Map<String, Object> fields = document.getSearchableFields();

        // Verify: All required fields are present
        assertNotNull(fields);
        assertEquals(fields.get("id"), student.getId());
        assertEquals(fields.get("courseId"), student.getCourseId());
        assertEquals(fields.get("email"), "student@example.com");
        assertNotNull(fields.get("_text_"));

        // Verify _text_ contains all searchable text
        String searchableText = (String) fields.get("_text_");
        assertTrue(searchableText.contains("John Doe"));
        assertTrue(searchableText.contains("student@example.com"));
        assertTrue(searchableText.contains("test-course"));
        assertTrue(searchableText.contains("Test Course"));
        assertTrue(searchableText.contains("Team Alpha"));
        assertTrue(searchableText.contains("Section 1"));
    }

    @Test
    public void testGetSearchableFields_withNullCourse_handlesGracefully() {
        // Setup
        Course course = createTestCourse("test-course", "Test Course");
        Student student = createTestStudent(course, "student@example.com", "John Doe", "Team Alpha", "Section 1");

        StudentSearchDocument document = new StudentSearchDocument(student, null);

        // Execute
        Map<String, Object> fields = document.getSearchableFields();

        // Verify: Fields are still created correctly even with null course
        assertNotNull(fields);
        assertEquals(fields.get("id"), student.getId());
        assertEquals(fields.get("courseId"), student.getCourseId());
        assertEquals(fields.get("email"), "student@example.com");

        // Verify _text_ doesn't crash with null course
        String searchableText = (String) fields.get("_text_");
        assertNotNull(searchableText);
        assertTrue(searchableText.contains("John Doe"));
    }

    @Test
    public void testGetSearchableFields_withSpecialCharacters_escapesCorrectly() {
        // Setup: Student with special characters in name
        Course course = createTestCourse("test-course", "Test Course");
        Student student = createTestStudent(course, "test+email@example.com", "John O'Brien", "Team & Co.", "Section 1");

        StudentSearchDocument document = new StudentSearchDocument(student, course);

        // Execute
        Map<String, Object> fields = document.getSearchableFields();

        // Verify: Special characters are included in searchable text
        assertNotNull(fields);
        String searchableText = (String) fields.get("_text_");
        assertTrue(searchableText.contains("John O'Brien"));
        assertTrue(searchableText.contains("test+email@example.com"));
    }

    @Test
    public void testGetSearchableFields_differentStudents_haveDifferentFields() {
        // Setup
        Course course1 = createTestCourse("course1", "Course One");
        Course course2 = createTestCourse("course2", "Course Two");

        Student student1 = createTestStudent(course1, "student1@example.com", "Alice", "Team A", "Section 1");
        Student student2 = createTestStudent(course2, "student2@example.com", "Bob", "Team B", "Section 2");

        StudentSearchDocument doc1 = new StudentSearchDocument(student1, course1);
        StudentSearchDocument doc2 = new StudentSearchDocument(student2, course2);

        // Execute
        Map<String, Object> fields1 = doc1.getSearchableFields();
        Map<String, Object> fields2 = doc2.getSearchableFields();

        // Verify: Fields are different for different students
        assertNotNull(fields1);
        assertNotNull(fields2);
        assertEquals(fields1.get("email"), "student1@example.com");
        assertEquals(fields2.get("email"), "student2@example.com");
        assertEquals(fields1.get("courseId"), "course1");
        assertEquals(fields2.get("courseId"), "course2");
    }

    // Helper methods
    private Course createTestCourse(String courseId, String courseName) {
        return new Course(courseId, courseName, "UTC", "Test Institute");
    }

    private Student createTestStudent(Course course, String email, String name, String teamName, String sectionName) {
        Section section = new Section(course, sectionName);
        Team team = new Team(section, teamName);
        Student student = new Student(course, name, email, "Comments");
        student.setTeam(team);
        return student;
    }
}
