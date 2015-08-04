package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AdminSearchPage extends AppPage {
    
    public AdminSearchPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Admin Search</h1>");
    }
    
    public void inputSearchContent(String content) {
        WebElement inputBox = this.getSearchBox();
        inputBox.sendKeys(content);
    }
    
    public void clearSearchBox() {
        WebElement inputBox = this.getSearchBox();
        inputBox.clear();
    }
    
    public void clickSearchButton() {
        this.getSearchButton().click();
        this.waitForPageToLoad();
    }
    
    private WebElement getSearchBox() {
        return browser.driver.findElement(By.id("filterQuery"));
    }
    
    private WebElement getSearchButton() {
        return browser.driver.findElement(By.id("searchButton"));
    }
}
