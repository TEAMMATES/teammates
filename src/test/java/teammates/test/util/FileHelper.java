package teammates.test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * File-related helper methods used for testing. There is another FileHelper on
 * the server side.
 */
public final class FileHelper {
    
    private FileHelper() {
        // utility class
    }

    public static String readFile(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(filename)));
        String ans = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return ans;
    }
    
    public static byte[] readFileAsBytes(String fileName) throws IOException {
        FileInputStream stream = new FileInputStream(fileName);
        byte[] buffer = new byte[1024 * 300];
        stream.read(buffer);
        stream.close();
        return buffer;
    }
    
    public static void saveFile(String filePath, String content) throws IOException {
        FileWriter output = new FileWriter(new File(filePath));
        output.write(content);
        output.close();
    }

    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        file.delete();
    }

}
