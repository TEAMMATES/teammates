package teammates.logic.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows mocking of {@link FileStorage}.
 */
public class MockFileStorage extends FileStorage {

    private Map<String, byte[]> fileMap = new HashMap<>();

    @Override
    public boolean doesFileExist(String fileKey) {
        return fileMap.containsKey(fileKey);
    }

    @Override
    public byte[] getContent(String fileKey) {
        return fileMap.getOrDefault(fileKey, new byte[0]);
    }

    @Override
    public void delete(String fileKey) {
        fileMap.remove(fileKey);
    }

    @Override
    public void create(String fileKey, byte[] contentBytes, String contentType) {
        fileMap.put(fileKey, contentBytes);
    }

}
