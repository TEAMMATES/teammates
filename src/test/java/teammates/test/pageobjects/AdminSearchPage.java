package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.e2e.pageobjects.Browser;

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
        By by = By.xpath("//table[@id = 'search_table']/tbody/tr[@id='" + createId(student) + "']");
        return browser.driver.findElement(by);
    }

    /**
     * Generates the id of the row for the {@code student}.
     */
    private static String createId(StudentAttributes student) {
        String id = SanitizationHelper.sanitizeForSearch(student.getCourse() + "/" + student.getEmail());
        id = id.replace(" ", "").replace("@", "");
        return "student_" + id;
    }

    /**
     * Generates the id of the row for the {@code instructor}.
     */
    private static String createId(InstructorAttributes instructor) {
        String id = SanitizationHelper.sanitizeForSearch(instructor.getCourseId() + "/" + instructor.getEmail());
        id = StringHelper.removeExtraSpace(id);
        id = id.replace(" ", "").replace("@", "");

        return "instructor_" + id;
    }

    public WebElement getInstructorRow(InstructorAttributes instructor) {
        By by = By.xpath("//table[@id = 'search_table_instructor']/tbody/tr[@id='"
                + createId(instructor) + "']");
        return browser.driver.findElement(by);
    }

    private WebElement getSearchBox() {
        return browser.driver.findElement(By.id("filterQuery"));
    }

    private WebElement getSearchButton() {
        return browser.driver.findElement(By.id("searchButton"));
    }
}
