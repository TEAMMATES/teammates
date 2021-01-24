package teammates.logic.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Config;
import teammates.logic.core.FileStorageService;
import teammates.logic.core.GoogleCloudStorageService;
import teammates.logic.core.LocalFileStorageService;

/**
 * Handles operations related to binary files.
 */
public class FileStorage {

    private final FileStorageService service;

    public FileStorage() {
        if (Config.isDevServer()) {
            service = new LocalFileStorageService();
        } else {
            service = new GoogleCloudStorageService();
        }
    }

    /**
     * Gets the file with the specified {@code fileKey} and serves it within the HTTP response.
     */
    public void serve(HttpServletResponse resp, String fileKey) throws IOException {
        service.serve(resp, fileKey);
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
    public String create(String fileKey, byte[] contentBytes, String contentType) throws IOException {
        return service.create(fileKey, contentBytes, contentType);
    }

}
