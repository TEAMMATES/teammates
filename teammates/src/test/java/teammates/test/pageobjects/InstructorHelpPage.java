package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class InstructorHelpPage extends AppPage {

    public InstructorHelpPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Help for Instructors</h1>");
    }

    public void inputSearchQuery(String query) {
        WebElement searchBox = getSearchBox();
        fillTextBox(searchBox, query);
    }

    public void clickSearchButton() {
        WebElement searchButton = getSearchButton();
        click(searchButton);
    }

    public void clickResetButton() {
        WebElement resetButton = getResetButton();
        click(resetButton);
    }

    public void clearSearchBox() {
        WebElement searchBox = getSearchBox();
        searchBox.clear();
    }

    public String getSearchResults() {
        WebElement searchResults = browser.driver.findElement(By.id("searchResults"));
        return searchResults.getAttribute("innerHTML");
    }

    private WebElement getResetButton() {
        return browser.driver.findElement(By.id("clear"));
    }

    private WebElement getSearchButton() {
        return browser.driver.findElement(By.id("search"));
    }

    private WebElement getSearchBox() {
        return browser.driver.findElement(By.id("searchQuery"));
    }
}
