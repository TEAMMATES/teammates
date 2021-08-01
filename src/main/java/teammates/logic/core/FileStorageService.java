package teammates.logic.core;

/**
 * A binary file storage interface used for managing binary files such as profile pictures.
 */
public interface FileStorageService {

    /**
     * Returns true if a file with the specified {@code fileKey} exists in the storage.
     */
    boolean doesFileExist(String fileKey);

    /**
     * Gets the content of the file with the specified {@code fileKey} as bytes.
     */
    byte[] getContent(String fileKey);

    /**
     * Deletes the file with the specified {@code fileKey}.
     */
    void delete(String fileKey);

    /**
     * Creates a file with the specified {@code contentBytes} as content and with type {@code contentType}.
     */
    void create(String fileKey, byte[] contentBytes, String contentType);

}
