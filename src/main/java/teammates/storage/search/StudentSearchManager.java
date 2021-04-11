package teammates.storage.search;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.SearchNotImplementedException;
import teammates.common.util.Const;

/**
 * Acts as a proxy to search service for student-related search features.
 */
public class StudentSearchManager extends SearchManager<StudentAttributes> {

    public StudentSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
    }

    @Override
    String getCollectionName() {
        return "students";
    }

    @Override
    SolrInputDocument createDocument(StudentAttributes student) {
        return new StudentSearchDocument(student).toDocument();
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
        return StudentSearchDocument.fromResponse(response);
    }

    private String prepareFilterQueryString(List<InstructorAttributes> instructors) {
        return instructors.stream()
                .filter(i -> i.privileges.getCourseLevelPrivileges()
                        .get(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS))
                .map(ins -> ins.courseId).collect(Collectors.joining(" "));
    }

}
