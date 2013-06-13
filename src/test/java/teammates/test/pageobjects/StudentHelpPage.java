package teammates.test.pageobjects;

public class StudentHelpPage extends AppPage {

	public StudentHelpPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains(
				"TEAMMATES Online Peer Feedback System for Student Team Projects - Student Help");
	}

}
