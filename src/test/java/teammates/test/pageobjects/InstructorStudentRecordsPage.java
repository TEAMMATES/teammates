package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;


public class InstructorStudentRecordsPage extends AppPage {
	
	public InstructorStudentRecordsPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("'s Records</h1>");
	}
	
	public void verifyIsCorrectPage() {
		assertTrue(containsExpectedPageContents());
	}
}
