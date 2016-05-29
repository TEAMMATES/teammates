package teammates.test.util;

import java.io.File;

/**
 * File-related helper methods used for testing. There is another FileHelper on
 * the server side.
 */
public final class FileHelper {
    
    private FileHelper() {
        // utility class
    }

    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        file.delete();
    }

}
