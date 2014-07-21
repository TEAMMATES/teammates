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
public class FileHelper {

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
     * Reads the contents of an {@link InputStream} as a String.
     */
    public static String readStream(InputStream stream) {
        Scanner scanner = new Scanner(stream, "UTF-8");
        String content = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return content;
    }

    public static String readResourseFile(String file) {
        return readStream(Config.class.getClassLoader()
                .getResourceAsStream(file));
    }

}
