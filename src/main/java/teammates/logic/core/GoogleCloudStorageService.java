package teammates.logic.core;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import teammates.common.util.Config;

/**
 * Holds functions for operations related to Google Cloud Storage.
 */
public final class GoogleCloudStorageService implements FileStorageService {

    private static Storage storage = StorageOptions.getDefaultInstance().getService();

    @Override
    public void delete(String fileKey) {
        storage.delete(BlobId.of(Config.PRODUCTION_GCS_BUCKETNAME, fileKey));
    }

    @Override
    public void create(String fileKey, byte[] contentBytes, String contentType) {
        BlobId blobId = BlobId.of(Config.PRODUCTION_GCS_BUCKETNAME, fileKey);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        storage.create(blobInfo, contentBytes);
    }

    @Override
    public boolean doesFileExist(String fileKey) {
        BlobId blobId = BlobId.of(Config.PRODUCTION_GCS_BUCKETNAME, fileKey);
        Blob blob = storage.get(blobId);
        return blob != null && blob.exists();
    }

    @Override
    public byte[] getContent(String fileKey) {
        BlobId blobId = BlobId.of(Config.PRODUCTION_GCS_BUCKETNAME, fileKey);
        Blob blob = storage.get(blobId);
        if (blob == null) {
            return new byte[0];
        }
        return blob.getContent();
    }

}
