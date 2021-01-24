package teammates.logic.core;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import teammates.common.util.Config;
import teammates.common.util.Logger;

/**
 * Holds functions for operations related to Google Cloud Storage.
 */
public final class GoogleCloudStorageService implements FileStorageService {

    private static final Logger log = Logger.getLogger();

    private static BlobstoreService service() {
        return BlobstoreServiceFactory.getBlobstoreService();
    }

    /**
     * Deletes the file with the specified {@code fileKey} in the Google Cloud Storage.
     */
    @Override
    public void delete(String fileKey) {
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
    @Override
    public String create(String googleId, byte[] imageData, String contentType) throws IOException {
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

    @Override
    public byte[] getContent(String objectKey) throws IOException {
        // TODO this is not supported in blobstore
        // service().serve(new BlobKey(fileKey), resp);
        return new byte[0];
    }

}
