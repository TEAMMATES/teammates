package teammates.exception;

/**
 * 
 * @author Kalpit Jain, Dec 2011
 *
 */
@SuppressWarnings("serial")
public class TeamFormingSessionExistsException extends Exception {
	public TeamFormingSessionExistsException() {
		super("Team Forming Session exists in database already.");
	}
}