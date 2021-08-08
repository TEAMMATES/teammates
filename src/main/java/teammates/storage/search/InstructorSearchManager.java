package teammates.storage.search;

import java.util.Comparator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.InstructorsDb;

/**
 * Acts as a proxy to search service for instructor-related search features.
 */
public class InstructorSearchManager extends SearchManager<InstructorAttributes> {

    private final CoursesDb coursesDb = CoursesDb.inst();
    private final InstructorsDb instructorsDb = InstructorsDb.inst();

    public InstructorSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
    }

    @Override
    String getCollectionName() {
        return "instructors";
    }

    @Override
    InstructorSearchDocument createDocument(InstructorAttributes instructor) {
        CourseAttributes course = coursesDb.getCourse(instructor.getCourseId());
        return new InstructorSearchDocument(instructor, course);
    }

    @Override
    InstructorAttributes getAttributeFromDocument(SolrDocument document) {
        String courseId = (String) document.getFirstValue("courseId");
        String email = (String) document.getFirstValue("email");
        return instructorsDb.getInstructorById(courseId, email);
    }

    @Override
    void sortResult(List<InstructorAttributes> result) {
        result.sort(Comparator.comparing((InstructorAttributes instructor) -> instructor.getCourseId())
                .thenComparing(instructor -> instructor.getRole())
                .thenComparing(instructor -> instructor.getName())
                .thenComparing(instructor -> instructor.getEmail()));
    }

    /**
     * Searches for instructors.
     */
    public List<InstructorAttributes> searchInstructors(String queryString) throws SearchServiceException {
        SolrQuery query = getBasicQuery(queryString);

        QueryResponse response = performQuery(query);
        return convertDocumentToAttributes(response.getResults());
    }

}
