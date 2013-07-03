package teammates.common.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Scanner;

/** Holds file-related functions
 */
public class FileHelper {

	/**
	 * Reads a file content and return a String
	 */
	public static String readFile(String filename) throws FileNotFoundException {
		Scanner scanner = new Scanner(new FileReader(filename));
		String ans = scanner.useDelimiter("\\Z").next();
		scanner.close();
		return ans;
	}

	/**
	 * Reads the contents of an {@link InputStream} as a String.
	 */
	public static String readStream(InputStream stream) {
		Scanner scanner = new Scanner(stream);
		String content = scanner.useDelimiter("\\Z").next();
		scanner.close();
		return content;
	}

	public static String readResourseFile(String file) {
		return readStream(Config.class.getClassLoader()
				.getResourceAsStream(file));
	}

}
