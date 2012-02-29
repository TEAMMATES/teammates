package teammates.exception;

/**
 * 
 * @author Kalpit Jain, Feb 2012
 *
 */
@SuppressWarnings("serial")
public class TeamProfileExistsException extends Exception {
	public TeamProfileExistsException() {
		super("Same team profile exists in database already.");
	}
}