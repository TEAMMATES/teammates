package teammates.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Represents the admin home page of the website.
 */
public class AdminSearchPage extends AppPage {
    private static final int STUDENT_COL_DETAILS = 1;
    private static final int STUDENT_COL_NAME = 2;
    private static final int STUDENT_COL_GOOGLE_ID = 3;
    private static final int STUDENT_COL_INSTITUTE = 4;
    private static final int STUDENT_COL_COMMENTS = 5;
    private static final int STUDENT_COL_OPTIONS = 6;

    private static final int INSTRUCTOR_COL_COURSE_ID = 1;
    private static final int INSTRUCTOR_COL_NAME = 2;
    private static final int INSTRUCTOR_COL_GOOGLE_ID = 3;
    private static final int INSTRUCTOR_COL_INSTITUTE = 4;
    private static final int INSTRUCTOR_COL_OPTIONS = 5;

    @FindBy(id = "search-box")
    private WebElement inputBox;

    @FindBy(id = "search-button")
    private WebElement searchButton;

    @FindBy(id = "show-student-links")
    private WebElement expandStudentLinksButton;

    @FindBy(id = "show-instructor-links")
    private WebElement expandInstructorLinksButton;

    public AdminSearchPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Admin Search</h1>");
    }

    public void inputSearchContent(String content) {
        inputBox.sendKeys(content);
    }

    public void clearSearchBox() {
        inputBox.clear();
    }

    public void clickSearchButton() {
        click(searchButton);
        waitForPageToLoad();
    }

    public void clickExpandInstructorLinks() {
        click(expandInstructorLinksButton);
        waitForPageToLoad();
    }

    public void clickExpandStudentLinks() {
        click(expandStudentLinksButton);
        waitForPageToLoad();
    }

    public WebElement getStudentRow(StudentAttributes student) {
        String details = String.format("%s [%s] (%s)", student.course, student.section,
                student.team);
        String xpath = String.format("//table[@id='search-table-student']/tbody/tr[td[%d]='%s' and td[%d]='%s']",
                    STUDENT_COL_DETAILS, details, STUDENT_COL_NAME, student.name);
        return browser.driver.findElement(By.xpath(xpath));
    }

    public String getStudentDetails(WebElement studentRow) {
        String xpath = String.format("td[%d]", STUDENT_COL_DETAILS);
        return studentRow.findElement(By.xpath(xpath)).getText();
    }

    public String getStudentName(WebElement studentRow) {
        String xpath = String.format("td[%d]", STUDENT_COL_NAME);
        return studentRow.findElement(By.xpath(xpath)).getText();
    }

    public String getStudentGoogleId(WebElement studentRow) {
        String xpath = String.format("td[%d]", STUDENT_COL_GOOGLE_ID);
        return studentRow.findElement(By.xpath(xpath)).getText();
    }

    public String getStudentHomeLink(WebElement studentRow) {
        String xpath = String.format("td[%d]/a", STUDENT_COL_GOOGLE_ID);
        return studentRow.findElement(By.xpath(xpath)).getAttribute("href");
    }

    public String getStudentInstitute(WebElement studentRow) {
        String xpath = String.format("td[%d]", STUDENT_COL_INSTITUTE);
        return studentRow.findElement(By.xpath(xpath)).getText();
    }

    public String getStudentComments(WebElement studentRow) {
        String xpath = String.format("td[%d]", STUDENT_COL_COMMENTS);
        return studentRow.findElement(By.xpath(xpath)).getText();
    }

    public String getStudentManageAccountLink(WebElement studentRow) {
        try {
            String xpath = String.format("td[%d]/a", STUDENT_COL_OPTIONS);
            return studentRow.findElement(By.xpath(xpath)).getAttribute("href");
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public String getStudentEmail(WebElement studentRow) {
        String xpath = "following-sibling::tr[1]/td/ul/li[//text()[contains(., 'Email')]]/input";
        return studentRow.findElement(By.xpath(xpath)).getAttribute("value");

    }

    public String getStudentJoinLink(WebElement studentRow) {
        String xpath = "following-sibling::tr[1]/td/ul/li[//text()[contains(., 'Course Join Link')]]/input";
        return studentRow.findElement(By.xpath(xpath)).getAttribute("value");
    }

    public int getNumExpandedRows(WebElement studentRow) {
        String xpath = "following-sibling::tr[1]/td/ul/li";
        return studentRow.findElements(By.xpath(xpath)).size();
    }

    public void resetStudentGoogleId(StudentAttributes student) {
        WebElement studentRow = getStudentRow(student);
        String xpath = String.format("td[%d]/a[2]", STUDENT_COL_OPTIONS);
        studentRow.findElement(By.xpath(xpath)).click();
        waitForPageToLoad();
    }

    public WebElement getInstructorRow(InstructorAttributes instructor) {
        String xpath = String.format("//table[@id='search-table-instructor']/tbody/tr[td[%d]='%s' and td[%d]='%s']",
                INSTRUCTOR_COL_COURSE_ID, instructor.getCourseId(), INSTRUCTOR_COL_NAME, instructor.name);
        return browser.driver.findElement(By.xpath(xpath));
    }

    public String getInstructorCourseId(WebElement instructorRow) {
        String xpath = String.format("td[%d]", INSTRUCTOR_COL_COURSE_ID);
        return instructorRow.findElement(By.xpath(xpath)).getText();
    }

    public String getInstructorName(WebElement instructorRow) {
        String xpath = String.format("td[%d]", INSTRUCTOR_COL_NAME);
        return instructorRow.findElement(By.xpath(xpath)).getText();
    }

    public String getInstructorGoogleId(WebElement instructorRow) {
        String xpath = String.format("td[%d]", INSTRUCTOR_COL_GOOGLE_ID);
        return instructorRow.findElement(By.xpath(xpath)).getText();
    }

    public String getInstructorHomePageLink(WebElement instructorRow) {
        String xpath = String.format("td[%d]/a", INSTRUCTOR_COL_GOOGLE_ID);
        return instructorRow.findElement(By.xpath(xpath)).getAttribute("href");
    }

    public String getInstructorInstitute(WebElement instructorRow) {
        String xpath = String.format("td[%d]", INSTRUCTOR_COL_INSTITUTE);
        return instructorRow.findElement(By.xpath(xpath)).getText();
    }

    public String getInstructorManageAccountLink(WebElement instructorRow) {
        try {
            String xpath = String.format("td[%d]/a", INSTRUCTOR_COL_OPTIONS);
            return instructorRow.findElement(By.xpath(xpath)).getAttribute("href");
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public String getInstructorEmail(WebElement instructorRow) {
        String xpath = "following-sibling::tr[1]/td/ul/li[//text()[contains(., 'Email')]]/input";
        return instructorRow.findElement(By.xpath(xpath)).getAttribute("value");

    }

    public String getInstructorJoinLink(WebElement instructorRow) {
        String xpath = "following-sibling::tr[1]/td/ul/li[//text()[contains(., 'Course Join Link')]]/input";
        return instructorRow.findElement(By.xpath(xpath)).getAttribute("value");
    }

    public void resetInstructorGoogleId(InstructorAttributes instructor) {
        WebElement instructorRow = getInstructorRow(instructor);
        String xpath = String.format("td[%d]/a[2]", INSTRUCTOR_COL_OPTIONS);
        instructorRow.findElement(By.xpath(xpath)).click();
        waitForPageToLoad();
    }
}


