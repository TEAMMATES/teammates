package teammates.common.util;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * Holds functions for operations related to Blobstore.
 *
 * @deprecated Blobstore is not supported in newer GAE runtime and should no longer be used.
 *         The only current usage that cannot be easily replaced is BlobKey within the StudentProfile entity,
 *         which needs data migration in order to remove the usage.
 */
@Deprecated
public final class LegacyBlobstoreService {

    private LegacyBlobstoreService() {
        // utility class
    }

    private static BlobstoreService service() {
        return BlobstoreServiceFactory.getBlobstoreService();
    }

    /**
     * Creates a blob key for the object with the given identifier in the production GCS bucket.
     */
    public static String createBlobKey(String identifier) {
        return service().createGsBlobKey("/gs/" + Config.PRODUCTION_GCS_BUCKETNAME + "/" + identifier).getKeyString();
    }

}
