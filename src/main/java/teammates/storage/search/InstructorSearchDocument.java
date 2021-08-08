package teammates.storage.search;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The {@link SearchDocument} object that defines how we store document for instructors.
 */
class InstructorSearchDocument extends SearchDocument<InstructorAttributes> {

    private final CourseAttributes course;

    InstructorSearchDocument(InstructorAttributes instructor, CourseAttributes course) {
        super(instructor);
        this.course = course;
    }

    @Override
    Map<String, Object> getSearchableFields() {
        Map<String, Object> fields = new HashMap<>();
        InstructorAttributes instructor = attribute;
        String[] searchableTexts = {
                instructor.getName(), instructor.getEmail(), instructor.getCourseId(),
                course == null ? "" : course.getName(),
                instructor.getGoogleId(), instructor.getRole(), instructor.getDisplayedName(),
        };

        fields.put("id", instructor.getEmail() + "%" + instructor.getCourseId());
        fields.put("_text_", String.join(" ", searchableTexts));
        fields.put("courseId", instructor.getCourseId());
        fields.put("email", instructor.getEmail());

        return fields;
    }

}
