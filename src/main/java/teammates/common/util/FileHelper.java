package teammates.common.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/** Holds file-related functions
 */
public final class FileHelper {

    private FileHelper() {
        // utility class
    }
    
    /**
     * Reads a file content and return a String
     */
    public static String readFile(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(filename)));
        String ans = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return ans;
    }
    
    public static byte[] readFileAsBytes(String fileName) throws FileNotFoundException, IOException {
        FileInputStream stream = new FileInputStream(fileName);
        byte[] buffer = new byte[1024 * 300];
        stream.read(buffer);
        stream.close();
        
        return buffer;
    }

    /**
     * Reads the contents of a file in the {@code resources} folder
     * as an {@link InputStream}.
     * @param file The file name, which must be in the {@code resources} folder.
     */
    public static InputStream getResourceAsStream(String file) {
        return Config.class.getClassLoader().getResourceAsStream(file);
    }

    /**
     * Reads the contents of a file in the {@code resources} folder.
     * @param file The file name, which must be in the {@code resources} folder.
     */
    public static String readResourceFile(String file) {
        InputStream is = getResourceAsStream(file);
        Scanner scanner = new Scanner(is, Const.SystemParams.ENCODING);
        String content = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return content;
    }

}
