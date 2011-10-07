package teammates.exception;

@SuppressWarnings("serial")
public class GoogleIDExistsInCourseException extends Exception 
{
	public GoogleIDExistsInCourseException()
	{
		super("The specified Google ID exists in the course.");
	}
}
