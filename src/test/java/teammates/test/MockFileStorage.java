package teammates.test;

import java.io.IOException;

import teammates.logic.api.FileStorage;

/**
 * Allows mocking of {@link FileStorage}.
 */
public class MockFileStorage extends FileStorage {

    private static final String TEST_FILESTORAGE_DIRECTORY = "src/test/resources/filestorage";

    @Override
    public boolean doesFileExist(String fileKey) {
        try {
            FileHelper.readFileAsBytes(TEST_FILESTORAGE_DIRECTORY + "/" + fileKey);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public byte[] getContent(String fileKey) {
        try {
            return FileHelper.readFileAsBytes(TEST_FILESTORAGE_DIRECTORY + "/" + fileKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String fileKey) {
        FileHelper.deleteFile(TEST_FILESTORAGE_DIRECTORY + "/" + fileKey);
    }

    @Override
    public void create(String fileKey, byte[] contentBytes, String contentType) {
        try {
            FileHelper.saveFile(TEST_FILESTORAGE_DIRECTORY + "/" + fileKey, contentBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
