package teammates.test.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * File-related helper methods used for testing. There is another FileHelper on
 * the server side.
 */
public class FileHelper {

    public static void writeToFile(String fileName, String fileContent) {
        try {

            File file = new File(fileName);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileContent);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void appendToFile(String fileName, String fileContent) {
        try {

            File file = new File(fileName);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileContent);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void deleteFile(String fileName) {
       File file = new File(fileName);
       file.delete();
    }

    public static String readFile(String path, Charset encoding) {
        byte[] encoded = null;
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded, encoding);
    }
}