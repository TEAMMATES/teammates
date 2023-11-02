package teammates.storage.search;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;

import java.time.Instant;
import java.util.Map;

public class InstructorSearchDocumentTest extends BaseSearchTest {
    Instructor instructor;
    InstructorAttributes instructorAttributes;
    Course course;
    CourseAttributes courseAttributes;

    @BeforeTest
    public void setup() {
        // Arrange
        instructor = new Instructor(
                "jacob.martin@gmail.com",
                "CS101",
                false,
                "Jacob Martin",
                "jacob.martin@example.com",
                "Teaching",
                true,
                "Professor Martin",
                null
        );
        instructorAttributes = InstructorAttributes.valueOf(instructor);
    }

    @Test
    public void testGetSearchableFields() {
        // Arrange
        course = new Course(
                "CS101",
                "Advanced Software Development Concepts",
                "UTC",
                "Dalhousie University",
                Instant.now(),
                null
        );
        courseAttributes = CourseAttributes.valueOf(course);

        InstructorSearchDocument instructorSearchDocument = new InstructorSearchDocument(instructorAttributes, courseAttributes);

        // Act
        Map<String, Object> searchableFields = instructorSearchDocument.getSearchableFields();

        // Assert
        assertEquals("jacob.martin@example.com%CS101", searchableFields.get("id"));
        assertEquals("Jacob Martin jacob.martin@example.com CS101 Advanced Software Development Concepts jacob.martin@gmail.com Teaching Professor Martin", searchableFields.get("_text_"));
        assertEquals("CS101", searchableFields.get("courseId"));
        assertEquals("jacob.martin@example.com", searchableFields.get("email"));
    }

    @Test
    public void testGetSearchableFieldsWithoutCourse() {
        // Arrange
        course = null;
        courseAttributes = null;

        InstructorSearchDocument instructorSearchDocument = new InstructorSearchDocument(instructorAttributes, courseAttributes);

        // Act
        Map<String, Object> searchableFields = instructorSearchDocument.getSearchableFields();

        // Assert
        assertEquals("jacob.martin@example.com%CS101", searchableFields.get("id"));
        assertEquals("Jacob Martin jacob.martin@example.com CS101  jacob.martin@gmail.com Teaching Professor Martin", searchableFields.get("_text_"));
        assertEquals("CS101", searchableFields.get("courseId"));
        assertEquals("jacob.martin@example.com", searchableFields.get("email"));
    }
}
