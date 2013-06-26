package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorFeedbackResultsPage extends AppPage {

	@FindBy(id = "button_sortgiver")
	private WebElement sortTableGiverButton;
	
	@FindBy(id = "button_sortrecipient")
	private WebElement sortTableRecipientButton;
	
	@FindBy(id = "button_sortanswer")
	private WebElement sortTableAnswerButton;
	
	public InstructorFeedbackResultsPage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return getPageSource().contains("<h1>Feedback Results - Instructor</h1>");
	}
	
	public void displayByGiver() {
		WebElement button = browser.driver.findElement(By.xpath("//input[@name='frsorttype' and @value='giver']"));
		button.click();
	}
	
	public void displayByRecipient() {
		WebElement button = browser.driver.findElement(By.xpath("//input[@name='frsorttype' and @value='recipient']"));
		button.click();
	}
	
	public void displayByTable() {
		WebElement button = browser.driver.findElement(By.xpath("//input[@name='frsorttype' and @value='table']"));
		button.click();
		waitForPageToLoad();
	}
	
	public AppPage sortTableByGiver() {
		sortTableGiverButton.click();
		return this;
	}
	
	public AppPage sortTableByRecipient() {
		sortTableRecipientButton.click();
		return this;
	}
	
	public AppPage sortTableByAnswer() {
		sortTableAnswerButton.click();
		return this;
	}
	
	public InstructorFeedbackEditPage clickEditLink() {
		WebElement button = browser.driver.findElement(By.linkText("[Edit]"));
		button.click();
		waitForPageToLoad();
		return changePageType(InstructorFeedbackEditPage.class);
	}
}
