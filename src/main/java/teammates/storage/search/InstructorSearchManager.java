package teammates.storage.search;

import org.apache.solr.common.SolrInputDocument;

import teammates.common.datatransfer.attributes.InstructorAttributes;

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

}
