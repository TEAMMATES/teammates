package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AdminSessionsPage extends AppPage {

    public AdminSessionsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Ongoing Sessions");
    }

    public void clickDetailButton() {
        click(getDetailButton());
        waitForPageToLoad();
    }

    private WebElement getDetailButton() {
        return browser.driver.findElement(By.id("detailButton"));
    }

}
