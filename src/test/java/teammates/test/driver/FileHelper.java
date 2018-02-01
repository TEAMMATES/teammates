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
        try (Scanner sc = new Scanner(new BufferedReader(new FileReader(filePath)))) {
            return sc.useDelimiter("\\Z").next();
        }
    }

    /**
     * Reads the file with the specified path as a byte array.
     */
    public static byte[] readFileAsBytes(String filePath) throws IOException {
        byte[] buffer = new byte[1024 * 300];
        try (FileInputStream fis = new FileInputStream(filePath);) {
            fis.read(buffer);
        }
        return buffer;
    }

    /**
     * Saves the supplied content to the specified file path.
     */
    public static void saveFile(String filePath, String content) throws IOException {
        try (FileWriter fw = new FileWriter(new File(filePath))) {
            fw.write(content);
        }

    }

    /**
     * Deletes the file with the specified path.
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }

}
