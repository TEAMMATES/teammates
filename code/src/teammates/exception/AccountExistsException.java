package teammates.exception;

@SuppressWarnings("serial")
public class AccountExistsException extends Exception 
{
	public AccountExistsException()
	{
		super("Account exists already.");
	}
}
