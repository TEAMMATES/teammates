package teammates.test.pageobjects;

public class StudenEvalResultsPage extends AppPage {

	public StudenEvalResultsPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Evaluation Results</h1>");
	}
	

}
