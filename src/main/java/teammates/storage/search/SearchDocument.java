package teammates.storage.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;

/**
 * Defines how we store {@link SolrInputDocument} for indexing/searching.
 */
abstract class SearchDocument {

    static final CoursesDb coursesDb = new CoursesDb();
    static final InstructorsDb instructorsDb = new InstructorsDb();
    static final StudentsDb studentsDb = new StudentsDb();

    abstract SolrInputDocument toDocument();

    /**
     * This method must be called to filter out the search result for course Id.
     */
    static List<SolrDocument> filterOutCourseId(QueryResponse response,
                                                  List<InstructorAttributes> instructors) {
        SolrDocumentList documents = response.getResults();

        // unfiltered case
        if (instructors == null) {
            return documents;
        }

        // filtered case
        Set<String> courseIdSet = new HashSet<>();
        for (InstructorAttributes ins : instructors) {
            courseIdSet.add(ins.courseId);
        }

        List<SolrDocument> filteredResults = new ArrayList<>();
        for (SolrDocument document : documents) {
            String courseId = (String) document.getFirstValue("courseId");
            if (courseIdSet.contains(courseId)) {
                filteredResults.add(document);
            }
        }

        return filteredResults;
    }
}
