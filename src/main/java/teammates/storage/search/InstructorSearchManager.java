package teammates.storage.search;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.SearchNotImplementedException;

/**
 * Acts as a proxy to search service for instructor-related search features.
 */
public class InstructorSearchManager extends SearchManager<InstructorAttributes> {

    public InstructorSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
    }

    @Override
    String getCollectionName() {
        return "instructors";
    }

    @Override
    SolrInputDocument createDocument(InstructorAttributes instructor) {
        return new InstructorSearchDocument(instructor).toDocument();
    }

    /**
     * Searches for instructors.
     */
    public List<InstructorAttributes> searchInstructors(String queryString) throws SearchNotImplementedException {
        SolrQuery query = getBasicQuery(queryString);

        QueryResponse response = performQuery(query);
        return InstructorSearchDocument.fromResponse(response);
    }

}
