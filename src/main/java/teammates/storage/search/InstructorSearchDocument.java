package teammates.storage.search;

import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The {@link SearchDocument} object that defines how we store {@link SolrInputDocument} for instructors.
 */
class InstructorSearchDocument extends SearchDocument {

    private final InstructorAttributes instructor;

    InstructorSearchDocument(InstructorAttributes instructor) {
        this.instructor = instructor;
    }

    @Override
    SolrInputDocument toDocument() {
        SolrInputDocument document = new SolrInputDocument();

        CourseAttributes course = coursesDb.getCourse(instructor.courseId);

        document.addField("id", instructor.getEmail() + "%" + instructor.getCourseId());
        document.addField("name", instructor.getName());
        document.addField("email", instructor.getEmail());
        document.addField("courseId", instructor.getCourseId());
        document.addField("courseName", course == null ? "" : course.getName());
        document.addField("googleId", instructor.getGoogleId());
        document.addField("role", instructor.getRole());
        document.addField("displayedName", instructor.getDisplayedName());

        return document;
    }

}
