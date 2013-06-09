package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class StudentHomePage extends AppPage {
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[1]/a")
	protected WebElement homeTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[2]/a")
	protected WebElement helpTab;
	
	@FindBy(xpath = "//*[@id=\"navbar\"]/li[3]/a")
	protected WebElement logoutLink;
	
	@FindBy(id = "regkey")
	protected WebElement keyTextBox;
	
	@FindBy(id = "button_join_course")
	protected WebElement joinButton;
	
	public StudentHomePage(Browser browser) {
		super(browser);
	}

	@Override
	protected boolean containsExpectedPageContents() {
		return containsExpectedPageContents(getPageSource());
	}

	public static boolean containsExpectedPageContents(String pageSource) {
		return pageSource.contains("<h1>Student Home</h1>");
	}

	public StudentHelpPage clickHelpLink() {
		helpTab.click();
		waitForPageToLoad();
		String curWin = browser.driver.getWindowHandle();
		for (String handle : browser.driver.getWindowHandles()) {
			if (handle.equals(curWin))
				continue;
			browser.selenium.selectWindow(handle);
			browser.selenium.windowFocus();
		}
		return changePageType(StudentHelpPage.class);
	}

	public void clickHomeTab() {
		homeTab.click();
		waitForPageToLoad();
		
	}

	public void fillKey(String key) {
		fillTextBox(keyTextBox, key);
		
	}

	public void clickJoinButton() {
		joinButton.click();
		waitForPageToLoad();
	}

}
