package teammates.storage.search;

import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.attributes.StudentAttributes;

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

}
