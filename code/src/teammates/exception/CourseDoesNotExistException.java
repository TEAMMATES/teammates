package teammates.exception;

@SuppressWarnings("serial")
public class CourseDoesNotExistException extends Exception 
{
	public CourseDoesNotExistException()
	{
		super("Course does not exist.");
	}
}
