package teammates.logic.core;

import java.io.IOException;

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
    public byte[] getContent(String fileKey) throws IOException {
        // TODO implement this
        return new byte[0];
    }

}
