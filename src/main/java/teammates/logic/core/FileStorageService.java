package teammates.logic.core;

/**
 * A binary file storage interface used for managing binary files such as profile pictures.
 */
public interface FileStorageService {

    byte[] getContent(String fileKey);

    void delete(String fileKey);

    void create(String fileKey, byte[] contentBytes, String contentType);

}
