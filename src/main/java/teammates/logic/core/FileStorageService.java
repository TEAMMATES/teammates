package teammates.logic.core;

import java.io.IOException;

/**
 * A binary file storage interface used for managing binary files such as profile pictures.
 */
public interface FileStorageService {

    byte[] getContent(String fileKey) throws IOException;

    void delete(String fileKey);

    String create(String fileKey, byte[] contentBytes, String contentType) throws IOException;

}
