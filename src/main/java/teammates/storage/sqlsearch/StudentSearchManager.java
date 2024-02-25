package teammates.storage.sqlsearch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import teammates.common.exception.SearchServiceException;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * Acts as a proxy to search service for student-related search features.
 */
public class StudentSearchManager extends SearchManager<Student> {

    private final CoursesDb coursesDb = CoursesDb.inst();
    private final UsersDb studentsDb = UsersDb.inst();

    public StudentSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
    }

    @Override
    String getCollectionName() {
        return "students";
    }

    @Override
    StudentSearchDocument createDocument(Student student) {
        Course course = coursesDb.getCourse(student.getCourseId());
        return new StudentSearchDocument(student, course);
    }

    @Override
    Student getEntityFromDocument(SolrDocument document) {
        String courseId = (String) document.getFirstValue("courseId");
        String email = (String) document.getFirstValue("email");
        return studentsDb.getStudentForEmail(courseId, email);
    }

    @Override
    void sortResult(List<Student> result) {
        result.sort(Comparator.comparing((Student student) -> student.getCourseId())
                .thenComparing(student -> student.getSection().getName())
                .thenComparing(student -> student.getTeam().getName())
                .thenComparing(student -> student.getName())
                .thenComparing(student -> student.getEmail()));
    }

    /**
     * Searches for students.
     *
     * @param instructors the constraint that restricts the search result
     */
    public List<Student> searchStudents(String queryString, List<Instructor> instructors)
            throws SearchServiceException {
        SolrQuery query = getBasicQuery(queryString);

        List<String> courseIdsWithViewStudentPrivilege;
        if (instructors == null) {
            courseIdsWithViewStudentPrivilege = new ArrayList<>();
        } else {
            courseIdsWithViewStudentPrivilege = instructors.stream()
                    .filter(i -> i.getPrivileges().getCourseLevelPrivileges().isCanViewStudentInSections())
                    .map(ins -> ins.getCourseId())
                    .collect(Collectors.toList());
            if (courseIdsWithViewStudentPrivilege.isEmpty()) {
                return new ArrayList<>();
            }
            String courseIdFq = String.join("\" OR \"", courseIdsWithViewStudentPrivilege);
            query.addFilterQuery("courseId:(\"" + courseIdFq + "\")");
        }

        QueryResponse response = performQuery(query);
        SolrDocumentList documents = response.getResults();

        // Sanity check such that the course ID of the students match exactly.
        // In ideal case, this check is not expected to do anything,
        // i.e. the resulting list should be the same as the incoming list.

        List<SolrDocument> filteredDocuments = documents.stream()
                .filter(document -> {
                    if (instructors == null) {
                        return true;
                    }
                    String courseId = (String) document.getFirstValue("courseId");
                    return courseIdsWithViewStudentPrivilege.contains(courseId);
                })
                .collect(Collectors.toList());

        return convertDocumentToEntities(filteredDocuments);
    }

}
