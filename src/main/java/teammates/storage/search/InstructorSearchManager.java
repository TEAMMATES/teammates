package teammates.storage.search;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * Acts as a proxy to search service for instructor-related search features.
 */
public class InstructorSearchManager extends SearchManager<InstructorAttributes> {

    public InstructorSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
    }

    @Override
    public void putDocuments(InstructorAttributes... attributes) {
        putInstructorSearchDocuments(attributes);
    }

    @Override
    public void deleteDocuments(String... keys) {
        deleteInstructorSearchDocuments(keys);
    }

}
