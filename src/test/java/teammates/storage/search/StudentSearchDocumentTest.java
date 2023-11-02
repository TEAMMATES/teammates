package teammates.storage.search;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;

import java.time.Instant;
import java.util.Map;

public class StudentSearchDocumentTest extends BaseSearchTest {
    CourseStudent courseStudent;
    StudentAttributes studentAttributes;
    Course course;
    CourseAttributes courseAttributes;

    @BeforeTest
    public void setup() {
        courseStudent = new CourseStudent(
                "john.doe@example.com",
                "John Doe",
                "john.doe@gmail.com",
                "Introduction to Computer Science",
                "CS101",
                "Engineering",
                "ASDC"
        );
        studentAttributes = StudentAttributes.valueOf(courseStudent);
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

        StudentSearchDocument studentSearchDocument = new StudentSearchDocument(studentAttributes, courseAttributes);

        // Act
        Map<String, Object> searchableFields = studentSearchDocument.getSearchableFields();

        // Assert
        assertEquals("john.doe@example.com%CS101", searchableFields.get("id"));
        assertEquals("John Doe john.doe@example.com CS101 Advanced Software Development Concepts Engineering ASDC", searchableFields.get("_text_"));
        assertEquals("CS101", searchableFields.get("courseId"));
        assertEquals("john.doe@example.com", searchableFields.get("email"));
    }

    @Test
    public void testGetSearchableFieldsWithoutCourse() {
        // Arrange
        CourseAttributes courseAttributes = null;

        StudentSearchDocument studentSearchDocument = new StudentSearchDocument(studentAttributes, courseAttributes);

        // Act
        Map<String, Object> searchableFields = studentSearchDocument.getSearchableFields();

        // Assert
        assertEquals("john.doe@example.com%CS101", searchableFields.get("id"));
        assertEquals("John Doe john.doe@example.com CS101  Engineering ASDC", searchableFields.get("_text_"));
        assertEquals("CS101", searchableFields.get("courseId"));
        assertEquals("john.doe@example.com", searchableFields.get("email"));
    }
}
