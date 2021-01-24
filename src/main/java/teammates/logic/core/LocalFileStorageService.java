package teammates.logic.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Logger;

/**
 * Holds functions for operations related to binary file storage in local dev environment.
 */
public final class LocalFileStorageService implements FileStorageService {

    private static final String BASE_DIRECTORY = "../../filestorage-dev";
    private static final Logger log = Logger.getLogger();

    private static String constructFilePath(String fileKey) {
        return BASE_DIRECTORY + "/" + fileKey;
    }

    @Override
    public void delete(String fileKey) {
        File file = new File(constructFilePath(fileKey));
        file.delete();
    }

    @Override
    public void create(String fileKey, byte[] contentBytes, String contentType) {
        try (OutputStream os = Files.newOutputStream(Paths.get(constructFilePath(fileKey)))) {
            os.write(contentBytes);
        } catch (IOException e) {
            log.warning(TeammatesException.toStringWithStackTrace(e));
        }
    }

    @Override
    public boolean doesFileExist(String fileKey) {
        return Files.exists(Paths.get(constructFilePath(fileKey)));
    }

    @Override
    public byte[] getContent(String fileKey) {
        byte[] buffer = new byte[1024 * 300];
        try (InputStream fis = Files.newInputStream(Paths.get(constructFilePath(fileKey)))) {
            fis.read(buffer);
        } catch (IOException e) {
            log.warning(TeammatesException.toStringWithStackTrace(e));
            return new byte[0];
        }
        return buffer;
    }

}
