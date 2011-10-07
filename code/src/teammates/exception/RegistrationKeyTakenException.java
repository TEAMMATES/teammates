package teammates.exception;

@SuppressWarnings("serial")
public class RegistrationKeyTakenException extends Exception
{
	public RegistrationKeyTakenException()
	{
		super("Registration key has been taken.");
	}

}

