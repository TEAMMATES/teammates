package teammates.storage.search;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Acts as a proxy to search service for student-related search features.
 */
public class StudentSearchManager extends SearchManager<StudentAttributes> {

    public StudentSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
    }

    @Override
    public void putDocuments(StudentAttributes... attributes) {
        putStudentSearchDocuments(attributes);
    }

    @Override
    public void deleteDocuments(String... keys) {
        deleteStudentSearchDocuments(keys);
    }

}
