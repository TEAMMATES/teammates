package teammates.logic.api;

import teammates.common.util.Config;
import teammates.logic.core.FileStorageService;
import teammates.logic.core.GoogleCloudStorageService;
import teammates.logic.core.LocalFileStorageService;

/**
 * Handles operations related to binary files.
 */
public class FileStorage {

    private static final FileStorage instance = new FileStorage();
    private final FileStorageService service;

    FileStorage() {
        if (Config.isDevServer()) {
            service = new LocalFileStorageService();
        } else {
            service = new GoogleCloudStorageService();
        }
    }

    public static FileStorage inst() {
        return instance;
    }

    /**
     * Returns true if a file with the specified {@code fileKey} exists in the storage.
     */
    public boolean doesFileExist(String fileKey) {
        return service.doesFileExist(fileKey);
    }

    /**
     * Gets the content of the file with the specified {@code fileKey} as bytes.
     */
    public byte[] getContent(String fileKey) {
        return service.getContent(fileKey);
    }

    /**
     * Deletes the file with the specified {@code fileKey}.
     */
    public void delete(String fileKey) {
        service.delete(fileKey);
    }

    /**
     * Creates a file with the specified {@code contentBytes} as content and with type {@code contentType}.
     */
    public void create(String fileKey, byte[] contentBytes, String contentType) {
        service.create(fileKey, contentBytes, contentType);
    }

}
