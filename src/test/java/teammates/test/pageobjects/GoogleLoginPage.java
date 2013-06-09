package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GoogleLoginPage extends LoginPage {
	
	@FindBy(id = "Email")
	private WebElement usernameTextBox;
	
	@FindBy(id = "Passwd")
	private WebElement passwordTextBox;
	
	@FindBy(id = "signIn")
	private WebElement loginButton;

	public GoogleLoginPage(Browser browser){
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return containsExpectedPageContents(getPageSource());
	}
	
	public static boolean containsExpectedPageContents(String pageSource){
		return pageSource.contains("uses Google Accounts for Sign In");
	}

	@Override
	public InstructorHomePage loginAsInstructor(String username, String password) {
		submitCredentials(username, password);
		handleApprovalPageIfAny();
		InstructorHomePage homePage = changePageType(InstructorHomePage.class);
		browser.isAdminLoggedIn = false;
		return homePage;
	}

	@Override
	public void loginAdminAsInstructor(
			String adminUsername, String adminPassword, String instructorUsername) {
		submitCredentials(adminUsername, adminPassword);
		handleApprovalPageIfAny();
		browser.isAdminLoggedIn = true;
	}

	@Override
	public StudentHomePage loginAsStudent(String username, String password) {
		submitCredentials(username, password);
		handleApprovalPageIfAny();
		StudentHomePage homePage = changePageType(StudentHomePage.class);
		browser.isAdminLoggedIn = false;
		return homePage;
	}

	private void handleApprovalPageIfAny() {
		boolean isPageRequestingAccessApproval = isElementPresent(By.id("approve_button"));
		if (isPageRequestingAccessApproval) {
			click(By.id("persist_checkbox"));
			click(By.id("approve_button"));
			waitForPageToLoad();
		}
	}

	private void submitCredentials(String username, String password) {
		fillTextBox(usernameTextBox, username);
		fillTextBox(passwordTextBox, password);
		loginButton.click();
		waitForPageToLoad();
	}

}
