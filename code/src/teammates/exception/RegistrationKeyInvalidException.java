package teammates.exception;

@SuppressWarnings("serial")
public class RegistrationKeyInvalidException extends Exception
{
	public RegistrationKeyInvalidException()
	{
		super("Registration key is not valid.");
	}

}
