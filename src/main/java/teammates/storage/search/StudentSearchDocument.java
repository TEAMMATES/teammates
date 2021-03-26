package teammates.storage.search;

import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * The {@link SearchDocument} object that defines how we store {@link SolrInputDocument} for students.
 */
public class StudentSearchDocument extends SearchDocument {

    private StudentAttributes student;
    private CourseAttributes course;

    public StudentSearchDocument(StudentAttributes student) {
        this.student = student;
    }

    @Override
    void prepareData() {
        if (student == null) {
            return;
        }

        course = coursesDb.getCourse(student.course);
    }

    @Override
    SolrInputDocument toDocument() {
        SolrInputDocument document = new SolrInputDocument();

        document.addField("name", student.getName());
        document.addField("email", student.getEmail());
        document.addField("course_id", student.getCourse());
        document.addField("course_name", course == null ? "" : course.getName());
        document.addField("team", student.getTeam());
        document.addField("section", student.getSection());

        return document;
    }
}
