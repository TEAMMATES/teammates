package teammates.storage.search;

import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * The {@link SearchDocument} object that defines how we store {@link SolrInputDocument} for students.
 */
class StudentSearchDocument extends SearchDocument {

    private final StudentAttributes student;

    StudentSearchDocument(StudentAttributes student) {
        this.student = student;
    }

    @Override
    SolrInputDocument toDocument() {
        SolrInputDocument document = new SolrInputDocument();

        CourseAttributes course = coursesDb.getCourse(student.course);

        document.addField("id", student.getId());
        document.addField("name", student.getName());
        document.addField("email", student.getEmail());
        document.addField("courseId", student.getCourse());
        document.addField("courseName", course == null ? "" : course.getName());
        document.addField("team", student.getTeam());
        document.addField("section", student.getSection());

        return document;
    }

}
