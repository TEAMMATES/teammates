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
public abstract class SearchDocument {

    static final CoursesDb coursesDb = new CoursesDb();
    static final InstructorsDb instructorsDb = new InstructorsDb();
    static final StudentsDb studentsDb = new StudentsDb();

    /**
     * Builds the search document.
     */
    public SolrInputDocument build() {
        prepareData();
        return toDocument();
    }

    abstract void prepareData();

    abstract SolrInputDocument toDocument();

    /**
     * This method must be called to filter out the search result for course Id.
     */
    static List<SolrDocument> filterOutCourseId(QueryResponse response,
                                                  List<InstructorAttributes> instructors) {
        SolrDocumentList documents = response.getResults();
        List<SolrDocument> filteredResults = new ArrayList<>();

        // unfiltered case
        if (instructors == null) {
            filteredResults.addAll(documents);
            return filteredResults;
        }

        // filtered case
        Set<String> courseIdSet = new HashSet<>();
        for (InstructorAttributes ins : instructors) {
            courseIdSet.add(ins.courseId);
        }

        for (SolrDocument document : documents) {
            String courseId = (String) document.getFirstValue("courseId");
            if (courseIdSet.contains(courseId)) {
                filteredResults.add(document);
            }
        }

        return filteredResults;
    }
}
