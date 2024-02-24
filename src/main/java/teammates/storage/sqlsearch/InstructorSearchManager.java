package teammates.storage.sqlsearch;

import java.util.Comparator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import teammates.common.exception.SearchServiceException;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;

/**
 * Acts as a proxy to search service for instructor-related search features.
 */
public class InstructorSearchManager extends SearchManager<Instructor> {

    private final CoursesDb coursesDb = CoursesDb.inst();
    private final UsersDb instructorsDb = UsersDb.inst();

    public InstructorSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
    }

    @Override
    String getCollectionName() {
        return "instructors";
    }

    @Override
    InstructorSearchDocument createDocument(Instructor instructor) {
        Course course = coursesDb.getCourse(instructor.getCourseId());
        return new InstructorSearchDocument(instructor, course);
    }

    @Override
    Instructor getEntityFromDocument(SolrDocument document) {
        String courseId = (String) document.getFirstValue("courseId");
        String email = (String) document.getFirstValue("email");
        return instructorsDb.getInstructorForEmail(courseId, email);
    }

    @Override
    void sortResult(List<Instructor> result) {
        result.sort(Comparator.comparing((Instructor instructor) -> instructor.getCourseId())
                .thenComparing(instructor -> instructor.getRole())
                .thenComparing(instructor -> instructor.getName())
                .thenComparing(instructor -> instructor.getEmail()));
    }

    /**
     * Searches for instructors.
     */
    public List<Instructor> searchInstructors(String queryString) throws SearchServiceException {
        SolrQuery query = getBasicQuery(queryString);

        QueryResponse response = performQuery(query);
        return convertDocumentToEntities(response.getResults());
    }

}
