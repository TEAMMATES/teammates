package teammates.common.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import teammates.logic.api.Logic;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

public final class GoogleCloudStorageHelper {
    
    private static GcsService gcsService;
    private static Logic logic = new Logic();

    private GoogleCloudStorageHelper() {
        // utility class
    }
    
    public static boolean doesFileExistInGcs(String googleId, boolean isGcsFilename) throws IOException {
        if (isGcsFilename) {
            GcsFilename name = new GcsFilename(Config.GCS_BUCKETNAME, googleId);
            try {
                return null != GcsServiceFactory.createGcsService().getMetadata(name);
            } catch (FileNotFoundException fne) {
                return false;
            }
        }
        BlobKey key = new BlobKey(logic.getStudentProfile(googleId).pictureKey);
        return doesFileExistInGcs(key);
    }
    
    public static boolean doesFileExistInGcs(BlobKey fileKey) {
        try {
            BlobstoreServiceFactory.getBlobstoreService().fetchData(fileKey, 0, 1);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String writeDataToGcs(String googleId, byte[] pictureData) throws IOException {
        GcsFilename gcsFilename = new GcsFilename(Config.GCS_BUCKETNAME, googleId);
        gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        GcsOutputChannel outputChannel =
                gcsService.createOrReplace(gcsFilename, new GcsFileOptions.Builder().mimeType("image/png").build());

        outputChannel.write(ByteBuffer.wrap(pictureData));
        outputChannel.close();
        
        return BlobstoreServiceFactory.getBlobstoreService()
                .createGsBlobKey("/gs/" + Config.GCS_BUCKETNAME + "/" + googleId).getKeyString();
    }
}
