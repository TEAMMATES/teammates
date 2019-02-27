package teammates.common.util;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
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

    /**
     * Returns true if a file with the specified {@link BlobKey} exists in the
     *         Google Cloud Storage.
     */
    public static boolean doesFileExistInGcs(BlobKey fileKey) {
        try {
            BlobstoreServiceFactory.getBlobstoreService().fetchData(fileKey, 0, 1);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Deletes the file with the specified {@link BlobKey} in the Google Cloud Storage.
     */
    public static void deleteFile(BlobKey fileKey) {
        try {
            BlobstoreServiceFactory.getBlobstoreService().delete(fileKey);
        } catch (Exception e) {
            log.warning("Trying to delete non-existent file with key: " + fileKey.getKeyString());
        }
    }

    /**
     * Writes a byte array {@code imageData} as image to the Google Cloud Storage,
     * with the {@code googleId} as the identifier name for the image.
     *
     * @return the {@link BlobKey} used as the image's identifier in Google Cloud Storage
     */
    public static String writeImageDataToGcs(String googleId, byte[] imageData) throws IOException {
        GcsFilename gcsFilename = new GcsFilename(Config.PRODUCTION_GCS_BUCKETNAME, googleId);
        try (GcsOutputChannel outputChannel =
                GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance())
                .createOrReplace(gcsFilename, new GcsFileOptions.Builder().mimeType("image/png").build())) {

            outputChannel.write(ByteBuffer.wrap(imageData));
        }

        return BlobstoreServiceFactory.getBlobstoreService()
                .createGsBlobKey("/gs/" + Config.PRODUCTION_GCS_BUCKETNAME + "/" + googleId).getKeyString();
    }

    /**
     * Creates and invokes a URL for uploading a large blob to Google Cloud Storage.
     * Upon completion of the upload, a callback is made to the specified {@code callbackUrl}.<br>
     * Refer to {@link com.google.appengine.api.blobstore.BlobstoreService#createUploadUrl}.
     */
    public static String getNewUploadUrl(String callbackUrl) {
        UploadOptions uploadOptions =
                UploadOptions.Builder.withDefaults()
                             .googleStorageBucketName(Config.PRODUCTION_GCS_BUCKETNAME)
                             .maxUploadSizeBytes(Const.SystemParams.MAX_FILE_LIMIT_FOR_BLOBSTOREAPI);

        return BlobstoreServiceFactory.getBlobstoreService()
                                      .createUploadUrl(callbackUrl, uploadOptions);
    }

}
