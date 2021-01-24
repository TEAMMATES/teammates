package teammates.logic.core;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Holds functions for operations related to binary file storage in local dev environment.
 */
public final class LocalFileStorageService implements FileStorageService {

    @Override
    public void delete(String fileKey) {
        // TODO implement this
    }

    @Override
    public String create(String googleId, byte[] imageData, String contentType) throws IOException {
        // TODO implement this
        return null;
    }

    @Override
    public void serve(HttpServletResponse resp, String fileKey) throws IOException {
        // TODO implement this
    }

}
