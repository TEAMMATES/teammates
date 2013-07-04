package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.util.Const;

public class StudentEvalEditPage extends AppPage {
	
	@FindBy(id = "button_submit")
	private WebElement submitButton;

	public StudentEvalEditPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Evaluation Submission</h1>");
	}
	
	public void fillSubmissionValues(String receiverName, SubmissionAttributes s){
		fillSubmissionValues(receiverName, s.points, s.justification.getValue(), s.p2pFeedback.getValue());
	}
	
	public StudentHomePage submit() {
		submitButton.click();
		waitForPageToLoad();
		return changePageType(StudentHomePage.class);
	}
	
	public StudentEvalEditPage submitUnsuccessfully() {
		submitButton.click();
		waitForPageToLoad();
		return this;
	}

	private void fillSubmissionValues(String receiverName, int points, String justification, String p2pComments){
		int rowId = getStudentRowIdInEditSubmission(receiverName);
		setPoints(rowId, points);
		setJustification(rowId, justification);
		setComments(rowId, p2pComments);
	}
	
	private void setPoints(int rowId, int points) {
		browser.selenium.select("id=" + Const.ParamsNames.POINTS + rowId, "value="+points);
	}
	
	private void setJustification(int rowId, String justification) {
		WebElement element = browser.driver.findElement(By.id(Const.ParamsNames.JUSTIFICATION + rowId));
		fillTextBox(element, justification);
	}
	
	private void setComments(int rowId, String comments) {
		WebElement element = browser.driver.findElement(By.id(Const.ParamsNames.COMMENTS + rowId));
		fillTextBox(element, comments);
	}
	
	/**
	 * Returns the rowID of the specified student for use in edit submission
	 * both in instructor page and student page.<br />
	 * This works by looking up the name in the section title. To get the row ID
	 * for the student itself (in case of student self submission which has no
	 * name in its title), put "self" as the student name.
	 */
	private int getStudentRowIdInEditSubmission(String studentNameOrSelf) {
		int max = browser.driver.findElements(By.className("reportHeader")).size();
		for (int i = 0; i < max; i++) {
			if (browser.driver.findElement(By.id("sectiontitle" + i)).getText()
					.toUpperCase().contains(studentNameOrSelf.toUpperCase())) {
				return i;
			}
		}
		return -1;
	}

}
