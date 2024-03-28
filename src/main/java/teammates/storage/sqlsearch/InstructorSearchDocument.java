package teammates.storage.sqlsearch;

import java.util.HashMap;
import java.util.Map;

import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;

/**
 * The {@link SearchDocument} object that defines how we store document for
 * instructors.
 */
class InstructorSearchDocument extends SearchDocument<Instructor> {

    private final Course course;

    InstructorSearchDocument(Instructor instructor, Course course) {
        super(instructor);
        this.course = course;
    }

    @Override
    Map<String, Object> getSearchableFields() {
        Map<String, Object> fields = new HashMap<>();
        Instructor instructor = entity;
        String[] searchableTexts = {
                instructor.getName(), instructor.getEmail(), instructor.getCourseId(),
                course == null ? "" : course.getName(),
                instructor.getGoogleId(), instructor.getRole().getRoleName(), instructor.getDisplayName(),
        };

        fields.put("id", instructor.getId());
        fields.put("_text_", String.join(" ", searchableTexts));
        fields.put("courseId", instructor.getCourseId());
        fields.put("email", instructor.getEmail());

        return fields;
    }

}
