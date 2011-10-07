package teammates.exception;

@SuppressWarnings("serial")
public class CourseExistsException extends Exception
{
	public CourseExistsException()
	{
		super("Course exists for the coordinator already.");
	}
}
