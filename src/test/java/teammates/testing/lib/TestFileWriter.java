package teammates.testing.lib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class that extends FileWriter to prevent the Eclipse from complaining compile error
 * at the testing files. Let the complain be only here =)
 * UPDATE: you can even exclude this file from being validated in Eclipse =)
 * @author Aldrian Obaja
 *
 */
public class TestFileWriter extends FileWriter{

	public TestFileWriter(String fileName) throws IOException {
		super(fileName);
	}
	
	public TestFileWriter(File file) throws IOException {
		super(file);
	}
	
}
