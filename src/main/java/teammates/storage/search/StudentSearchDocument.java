package teammates.storage.search;

import java.util.Comparator;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.StringHelper;

/**
 * The {@link SearchDocument} object that defines how we store {@link SolrInputDocument} for students.
 */
public class StudentSearchDocument extends SearchDocument {

    private StudentAttributes student;

    public StudentSearchDocument(StudentAttributes student) {
        this.student = student;
    }

    @Override
    SolrInputDocument toDocument() {
        SolrInputDocument document = new SolrInputDocument();

        CourseAttributes course = coursesDb.getCourse(student.course);

        document.addField("id", student.key);
        document.addField("name", student.getName());
        document.addField("email", student.getEmail());
        document.addField("courseId", student.getCourse());
        document.addField("courseName", course == null ? "" : course.getName());
        document.addField("team", student.getTeam());
        document.addField("section", student.getSection());

        return document;
    }

    /**
     * Produces a {@link StudentSearchResultBundle} from the {@code QueryResponse} collection.
     *
     * <p>The list of {@link InstructorAttributes} is used to filter out the search result.</p>
     */
    public static StudentSearchResultBundle fromResponse(QueryResponse response,
                                                        List<InstructorAttributes> instructors) {
        if (response == null) {
            return new StudentSearchResultBundle();
        }

        List<SolrDocument> filteredResults = filterOutCourseId(response, instructors);
        StudentSearchResultBundle bundle = constructBaseBundle(filteredResults);
        sortStudentResultList(bundle.studentList);

        return bundle;
    }

    private static StudentSearchResultBundle constructBaseBundle(List<SolrDocument> results) {
        StudentSearchResultBundle bundle = new StudentSearchResultBundle();

        for (SolrDocument document : results) {
            String studentId = (String) document.getFirstValue("id");
            StudentAttributes student = studentsDb.getStudentForRegistrationKey(StringHelper.encrypt(studentId));
            if (student == null) {
                // search engine out of sync as SearchManager may fail to delete documents
                // the chance is low and it is generally not a big problem
                studentsDb.deleteDocumentByStudentKey(studentId);
                continue;
            }

            bundle.studentList.add(student);
        }

        bundle.numberOfResults = bundle.studentList.size();
        return bundle;
    }

    private static void sortStudentResultList(List<StudentAttributes> studentList) {
        studentList.sort(Comparator.comparing((StudentAttributes student) -> student.course)
                .thenComparing(student -> student.section)
                .thenComparing(student -> student.team)
                .thenComparing(student -> student.name)
                .thenComparing(student -> student.email));
    }
}
