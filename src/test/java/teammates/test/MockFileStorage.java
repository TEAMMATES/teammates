package teammates.test;

import teammates.logic.api.FileStorage;

/**
 * Allows mocking of {@link FileStorage}.
 */
public class MockFileStorage extends FileStorage {

    /**
     * Returns true if a file with the specified {@code fileKey} exists in the storage.
     */
    public boolean doesFileExist(String fileKey) {
        // TODO implement this
        return true;
    }

}
