package teammates.test.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * File-related helper methods used for testing.
 */
public final class FileHelper {

    private FileHelper() {
        // utility class
    }

    /**
     * Reads the file with the specified path as a String.
     */
    public static String readFile(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        Scanner sc = new Scanner(br);
        try {
            return sc.useDelimiter("\\Z").next();
        } finally {
            sc.close();
            br.close();
        }
    }

    /**
     * Reads the file with the specified path as a byte array.
     */
    public static byte[] readFileAsBytes(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] buffer = new byte[1024 * 300];
        fis.read(buffer);
        fis.close();
        return buffer;
    }

    /**
     * Saves the supplied content to the specified file path.
     */
    public static void saveFile(String filePath, String content) throws IOException {
        FileWriter fw = new FileWriter(new File(filePath));
        fw.write(content);
        fw.close();
    }

    /**
     * Deletes the file with the specified path.
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }

}
