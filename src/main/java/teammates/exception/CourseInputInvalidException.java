package teammates.exception;

@SuppressWarnings("serial")
public class CourseInputInvalidException extends Exception {

	public CourseInputInvalidException() {
		super("Invalid course id or name");
	}
	
	public CourseInputInvalidException(String err) {
		super("Invalid course input: " + err);
	}
}
