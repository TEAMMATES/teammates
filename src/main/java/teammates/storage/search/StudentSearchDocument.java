package teammates.storage.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
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

    /**
     * Produces a list of {@link StudentAttributes} from the {@code QueryResponse} collection.
     */
    static List<StudentAttributes> fromResponse(QueryResponse response) {
        if (response == null) {
            return new ArrayList<>();
        }

        List<StudentAttributes> studentList = constructBaseBundle(response.getResults());
        sortStudentResultList(studentList);

        return studentList;
    }

    private static List<StudentAttributes> constructBaseBundle(List<SolrDocument> results) {
        List<StudentAttributes> studentList = new ArrayList<>();

        for (SolrDocument document : results) {
            String courseId = (String) document.getFirstValue("courseId");
            String email = (String) document.getFirstValue("email");
            StudentAttributes student = studentsDb.getStudentForEmail(courseId, email);
            if (student == null) {
                // search engine out of sync as SearchManager may fail to delete documents
                // the chance is low and it is generally not a big problem
                String id = (String) document.getFirstValue("id");
                studentsDb.deleteDocumentByStudentId(id);
                continue;
            }

            studentList.add(student);
        }

        return studentList;
    }

    private static void sortStudentResultList(List<StudentAttributes> studentList) {
        studentList.sort(Comparator.comparing((StudentAttributes student) -> student.course)
                .thenComparing(student -> student.section)
                .thenComparing(student -> student.team)
                .thenComparing(student -> student.name)
                .thenComparing(student -> student.email));
    }
}
