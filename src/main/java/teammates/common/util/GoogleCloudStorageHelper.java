package teammates.common.util;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

/**
 * Holds functions for operations related to Google Cloud Storage.
 */
public final class GoogleCloudStorageHelper {

    private static final Logger log = Logger.getLogger();

    private GoogleCloudStorageHelper() {
        // utility class
    }

    private static BlobstoreService service() {
        return BlobstoreServiceFactory.getBlobstoreService();
    }

    /**
     * Returns true if a file with the specified {@code fileKey} exists in the
     *         Google Cloud Storage.
     */
    public static boolean doesFileExistInGcs(String fileKey) {
        try {
            service().fetchData(new BlobKey(fileKey), 0, 1);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Deletes the file with the specified {@code fileKey} in the Google Cloud Storage.
     */
    public static void deleteFile(String fileKey) {
        try {
            service().delete(new BlobKey(fileKey));
        } catch (Exception e) {
            log.warning("Trying to delete non-existent file with key: " + fileKey);
        }
    }

    /**
     * Writes a byte array {@code imageData} as image to the Google Cloud Storage,
     * with the {@code googleId} as the identifier name for the image.
     *
     * @return the {@link BlobKey} used as the image's identifier in Google Cloud Storage
     */
    public static String writeImageDataToGcs(String googleId, byte[] imageData, String contentType) throws IOException {
        GcsFilename gcsFilename = new GcsFilename(Config.PRODUCTION_GCS_BUCKETNAME, googleId);
        try (GcsOutputChannel outputChannel =
                GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance())
                .createOrReplace(gcsFilename, new GcsFileOptions.Builder().mimeType(contentType).build())) {

            outputChannel.write(ByteBuffer.wrap(imageData));
        }

        return createBlobKey(googleId);
    }

    /**
     * Creates a blob key for the object with the given identifier in the production GCS bucket.
     */
    public static String createBlobKey(String identifier) {
        return service().createGsBlobKey("/gs/" + Config.PRODUCTION_GCS_BUCKETNAME + "/" + identifier).getKeyString();
    }

    /**
     * Serves the content of the file with the specified {@code fileKey} as the body of the given HTTP response.
     */
    public static void serve(HttpServletResponse resp, String fileKey) throws IOException {
        service().serve(new BlobKey(fileKey), resp);
    }

}
