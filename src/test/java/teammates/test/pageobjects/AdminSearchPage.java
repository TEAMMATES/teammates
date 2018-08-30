package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.ui.pagedata.AdminSearchPageData;

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
        click(getSearchButton());
        waitForPageToLoad();
    }

    public String getPageTitle() {
        return browser.driver.findElement(By.tagName("h1")).getText();
    }

    public WebElement getStudentRow(StudentAttributes student) {
        By by = By.xpath("//table[@id = 'search_table']/tbody/tr[@id='" + AdminSearchPageData.createId(student) + "']");
        return browser.driver.findElement(by);
    }

    public WebElement getInstructorRow(InstructorAttributes instructor) {
        By by = By.xpath("//table[@id = 'search_table_instructor']/tbody/tr[@id='"
                + AdminSearchPageData.createId(instructor) + "']");
        return browser.driver.findElement(by);
    }

    private WebElement getSearchBox() {
        return browser.driver.findElement(By.id("filterQuery"));
    }

    private WebElement getSearchButton() {
        return browser.driver.findElement(By.id("searchButton"));
    }
}
