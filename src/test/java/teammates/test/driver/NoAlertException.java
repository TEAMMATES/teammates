package teammates.test.driver;

@SuppressWarnings("serial")
public class NoAlertException extends RuntimeException {
	public NoAlertException(String clickedObj){
		super("No alert message appear when clicking "+clickedObj);
	}
}
