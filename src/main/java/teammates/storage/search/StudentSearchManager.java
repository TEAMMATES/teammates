package teammates.storage.search;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.SearchNotImplementedException;
import teammates.common.util.Const;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.StudentsDb;

/**
 * Acts as a proxy to search service for student-related search features.
 */
public class StudentSearchManager extends SearchManager<StudentAttributes> {

    private final CoursesDb coursesDb = new CoursesDb();
    private final StudentsDb studentsDb = new StudentsDb();

    public StudentSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
    }

    @Override
    String getCollectionName() {
        return "students";
    }

    @Override
    StudentSearchDocument createDocument(StudentAttributes student) {
        CourseAttributes course = coursesDb.getCourse(student.course);
        return new StudentSearchDocument(student, course);
    }

    @Override
    StudentAttributes getAttributeFromDocument(SolrDocument document) {
        String courseId = (String) document.getFirstValue("courseId");
        String email = (String) document.getFirstValue("email");
        return studentsDb.getStudentForEmail(courseId, email);
    }

    @Override
    void sortResult(List<StudentAttributes> result) {
        result.sort(Comparator.comparing((StudentAttributes student) -> student.course)
                .thenComparing(student -> student.section)
                .thenComparing(student -> student.team)
                .thenComparing(student -> student.name)
                .thenComparing(student -> student.email));
    }

    /**
     * Searches for students.
     *
     * @param instructors the constraint that restricts the search result
     */
    public List<StudentAttributes> searchStudents(String queryString, List<InstructorAttributes> instructors)
            throws SearchNotImplementedException {
        SolrQuery query = getBasicQuery(queryString);

        if (instructors != null) {
            String filterQueryString = prepareFilterQueryString(instructors);
            query.addFilterQuery(filterQueryString);
        }

        QueryResponse response = performQuery(query);
        return convertDocumentToAttributes(response);
    }

    private String prepareFilterQueryString(List<InstructorAttributes> instructors) {
        return instructors.stream()
                .filter(i -> i.privileges.getCourseLevelPrivileges()
                        .get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS))
                .map(ins -> ins.courseId).collect(Collectors.joining(" "));
    }

}
