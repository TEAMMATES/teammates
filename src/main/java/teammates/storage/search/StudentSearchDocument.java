package teammates.storage.search;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * The {@link SearchDocument} object that defines how we store document for students.
 */
class StudentSearchDocument extends SearchDocument<StudentAttributes> {

    private final CourseAttributes course;

    StudentSearchDocument(StudentAttributes student, CourseAttributes course) {
        super(student);
        this.course = course;
    }

    @Override
    Map<String, Object> getSearchableFields() {
        Map<String, Object> fields = new HashMap<>();
        StudentAttributes student = attribute;

        fields.put("id", student.getId());
        fields.put("name", student.getName());
        fields.put("email", student.getEmail());
        fields.put("courseId", student.getCourse());
        fields.put("courseName", course == null ? "" : course.getName());
        fields.put("team", student.getTeam());
        fields.put("section", student.getSection());

        return fields;
    }

}
