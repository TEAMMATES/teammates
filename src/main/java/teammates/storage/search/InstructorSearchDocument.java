package teammates.storage.search;

import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The {@link SearchDocument} object that defines how we store {@link SolrInputDocument} for instructors.
 */
public class InstructorSearchDocument extends SearchDocument {

    private InstructorAttributes instructor;
    private CourseAttributes course;

    public InstructorSearchDocument(InstructorAttributes instructor) {
        this.instructor = instructor;
    }

    @Override
    void prepareData() {
        if (instructor == null) {
            return;
        }

        course = coursesDb.getCourse(instructor.courseId);
    }

    @Override
    SolrInputDocument toDocument() {
        SolrInputDocument document = new SolrInputDocument();

        document.addField("name", instructor.getName());
        document.addField("email", instructor.getEmail());
        document.addField("course_id", instructor.getCourseId());
        document.addField("course_name", course == null ? "" : course.getName());
        document.addField("google_id", instructor.getGoogleId());
        document.addField("role", instructor.getRole());
        document.addField("displayed_name", instructor.getDisplayedName());

        return document;
    }
}
