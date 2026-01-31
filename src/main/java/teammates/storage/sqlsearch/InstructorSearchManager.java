package teammates.storage.sqlsearch;

import java.util.Comparator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
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

    private final CoursesDb coursesDb;
    private final UsersDb instructorsDb;

    /**
     * Creates an InstructorSearchManager with the given Solr client and database dependencies.
     * This constructor allows dependency injection for testing purposes.
     *
     * @param client the Solr client to use (can be null or a mock)
     * @param coursesDb the CoursesDb to use (can be a mock)
     * @param instructorsDb the UsersDb to use (can be a mock)
     * @param isResetAllowed whether reset operations are allowed
     */
    public InstructorSearchManager(HttpSolrClient client, CoursesDb coursesDb, UsersDb instructorsDb,
            boolean isResetAllowed) {
        super(client, isResetAllowed);
        this.coursesDb = coursesDb;
        this.instructorsDb = instructorsDb;
    }

    /**
     * Creates an InstructorSearchManager with the given Solr client.
     * This constructor allows dependency injection for testing purposes.
     *
     * @param client the Solr client to use (can be null or a mock)
     * @param isResetAllowed whether reset operations are allowed
     */
    public InstructorSearchManager(HttpSolrClient client, boolean isResetAllowed) {
        super(client, isResetAllowed);
        this.coursesDb = CoursesDb.inst();
        this.instructorsDb = UsersDb.inst();
    }

    /**
     * Creates an InstructorSearchManager with the given search service host.
     * This constructor maintains backward compatibility.
     *
     * @param searchServiceHost the Solr service host URL
     * @param isResetAllowed whether reset operations are allowed
     */
    public InstructorSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
        this.coursesDb = CoursesDb.inst();
        this.instructorsDb = UsersDb.inst();
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
