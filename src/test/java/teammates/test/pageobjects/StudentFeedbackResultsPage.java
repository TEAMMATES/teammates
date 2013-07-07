package teammates.test.pageobjects;

public class StudentFeedbackResultsPage extends AppPage {

	public StudentFeedbackResultsPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Feedback Results - Student</h1>");
	}
}
