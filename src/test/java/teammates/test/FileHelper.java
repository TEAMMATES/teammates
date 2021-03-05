package teammates.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        try (Scanner sc = new Scanner(Files.newBufferedReader(Paths.get(filePath)))) {
            return sc.useDelimiter("\\Z").next();
        }
    }

    /**
     * Reads the file with the specified path as a byte array.
     */
    public static byte[] readFileAsBytes(String filePath) throws IOException {
        byte[] buffer = new byte[1024 * 300];
        try (InputStream fis = Files.newInputStream(Paths.get(filePath))) {
            fis.read(buffer);
        }
        return buffer;
    }

    /**
     * Saves the supplied content to the specified file path.
     */
    public static void saveFile(String filePath, String content) throws IOException {
        try (BufferedWriter fw = Files.newBufferedWriter(Paths.get(filePath))) {
            fw.write(content);
        }
    }

    /**
     * Saves the supplied content to the specified file path.
     */
    public static void saveFile(String filePath, byte[] content) throws IOException {
        try (OutputStream os = Files.newOutputStream(Paths.get(filePath))) {
            os.write(content);
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
