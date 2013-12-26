package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


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
	
	public InstructorEvalSubmissionEditPage clickEvalEditLink(String evalName){
		int rowId = getEvalRowId(evalName);
		getEditLink(rowId).click();
		waitForPageToLoad();
		return changePageType(InstructorEvalSubmissionEditPage.class);
	}
	
	private int getEvalRowId(String evalName) {
		int evalCount = browser.driver.findElements(By.className("student_eval")).size();
		for (int i = 0; i < evalCount; i++) {
			String evalNameInRow = getEvalNameInRow(i);
			if (evalNameInRow.equals(evalName)) {
				return i;
			}
		}
		return -1;
	}
	
	private String getEvalNameInRow(int rowId) {
		String xpath = "//div[@class='student_eval' and @id='studentEval" + rowId + "']"
				+ "//table//tr//td[@id='eval_name" + rowId + "']";
		return browser.driver.findElement(By.xpath(xpath)).getText();
	}
	
	private WebElement getEditLink(int rowId) {
		return browser.driver.findElement(By.id("button_edit" + rowId));
	}
}
