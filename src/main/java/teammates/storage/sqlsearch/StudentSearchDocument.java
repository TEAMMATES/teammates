package teammates.storage.sqlsearch;

import java.util.HashMap;
import java.util.Map;

import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Student;

/**
 * The {@link SearchDocument} object that defines how we store document for
 * students.
 */
class StudentSearchDocument extends SearchDocument<Student> {

    private final Course course;

    StudentSearchDocument(Student student, Course course) {
        super(student);
        this.course = course;
    }

    @Override
    Map<String, Object> getSearchableFields() {
        Map<String, Object> fields = new HashMap<>();
        Student student = entity;
        String[] searchableTexts = {
                student.getName(), student.getEmail(), student.getCourseId(),
                course == null ? "" : course.getName(),
                student.getTeam().getName(), student.getSection().getName(),
        };

        fields.put("id", student.getId());
        fields.put("_text_", String.join(" ", searchableTexts));
        fields.put("courseId", student.getCourseId());
        fields.put("email", student.getEmail());

        return fields;
    }

}
