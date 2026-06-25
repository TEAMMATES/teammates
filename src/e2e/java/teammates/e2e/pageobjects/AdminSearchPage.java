package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.StringHelper;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

/**
 * Represents the admin home page of the website.
 */
public class AdminSearchPage extends AppPage {
    private static final int STUDENT_COL_DETAILS = 1;
    private static final int STUDENT_COL_NAME = 2;
    private static final int STUDENT_COL_EMAIL = 3;
    private static final int STUDENT_COL_INSTITUTE = 4;
    private static final int STUDENT_COL_COMMENTS = 5;
    private static final int STUDENT_COL_OPTIONS = 6;

    private static final int INSTRUCTOR_COL_COURSE_ID = 1;
    private static final int INSTRUCTOR_COL_NAME = 2;
    private static final int INSTRUCTOR_COL_EMAIL = 3;
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

    @FindBy(id = "hide-student-links")
    private WebElement collapseStudentLinksButton;

    @FindBy(id = "hide-instructor-links")
    private WebElement collapseInstructorLinksButton;

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
        waitForPageToLoad(false);
    }

    public void regenerateStudentKey(Student student) {
        WebElement studentRow = getStudentRow(student);
        studentRow.findElement(
                By.cssSelector("[data-testid='regenerate-student-key']")).click();

        waitForConfirmationModalAndClickOk();
        waitForPageToLoad(true);
    }

    public void verifyRegenerateStudentKey() {
        verifyStatusMessage("User's key for this course has been successfully regenerated,"
                + " and the email has been sent.");
    }

    public void regenerateInstructorKey(Instructor instructor) {
        WebElement instructorRow = getInstructorRow(instructor);
        instructorRow.findElement(
                By.cssSelector("[data-testid='regenerate-instructor-key']")).click();

        waitForConfirmationModalAndClickOk();
        waitForPageToLoad(true);
    }

    public String removeSpanFromText(String text) {
        return text.replace("<span class=\"highlighted-text\">", "").replace("</span>", "");
    }

    public WebElement getStudentRow(Student student) {
        String details = String.format("%s [%s] (%s)", student.getCourseId(),
                student.getSectionName(), student.getTeamName());
        WebElement table = browser.driver.findElement(By.id("search-table-student"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        for (WebElement row : rows) {
            List<WebElement> columns = row.findElements(By.tagName("td"));
            if (!columns.isEmpty() && removeSpanFromText(columns.get(STUDENT_COL_DETAILS - 1)
                    .getAttribute("innerHTML")).contains(details)
                    && removeSpanFromText(columns.get(STUDENT_COL_NAME - 1)
                    .getAttribute("innerHTML")).contains(student.getName())) {
                return row;
            }
        }
        return null;
    }

    public String getStudentDetails(WebElement studentRow) {
        return getColumnText(studentRow, STUDENT_COL_DETAILS);
    }

    public String getStudentName(WebElement studentRow) {
        return getColumnText(studentRow, STUDENT_COL_NAME);
    }

    public String getStudentEmail(WebElement studentRow) {
        return getColumnText(studentRow, STUDENT_COL_EMAIL);
    }

    public String getStudentInstitute(WebElement studentRow) {
        return getColumnText(studentRow, STUDENT_COL_INSTITUTE);
    }

    public String getStudentComments(WebElement studentRow) {
        return getColumnText(studentRow, STUDENT_COL_COMMENTS);
    }

    public String getStudentManageAccountLink(WebElement studentRow) {
        return getColumnLink(studentRow, STUDENT_COL_OPTIONS);
    }

    public WebElement getInstructorRow(Instructor instructor) {
        WebElement table = browser.driver.findElement(By.id("search-table-instructor"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        for (WebElement row : rows) {
            List<WebElement> columns = row.findElements(By.tagName("td"));
            if (columns.size() >= 3 && (removeSpanFromText(columns.get(2)
                    .getAttribute("innerHTML")).contains(instructor.getEmail())
                    || removeSpanFromText(columns.get(1)
                    .getAttribute("innerHTML")).contains(instructor.getName()))) {
                return row;
            }
        }
        return null;
    }

    public String getInstructorCourseId(WebElement instructorRow) {
        return getColumnText(instructorRow, INSTRUCTOR_COL_COURSE_ID);
    }

    public String getInstructorName(WebElement instructorRow) {
        return getColumnText(instructorRow, INSTRUCTOR_COL_NAME);
    }

    public String getInstructorEmail(WebElement instructorRow) {
        return getColumnText(instructorRow, INSTRUCTOR_COL_EMAIL);
    }

    public String getInstructorInstitute(WebElement instructorRow) {
        return getColumnText(instructorRow, INSTRUCTOR_COL_INSTITUTE);
    }

    public String getInstructorManageAccountLink(WebElement instructorRow) {
        return getColumnLink(instructorRow, INSTRUCTOR_COL_OPTIONS);
    }

    private String getColumnText(WebElement row, int columnNum) {
        String xpath = String.format("td[%d]", columnNum);
        return row.findElement(By.xpath(xpath)).getText();
    }

    private String getColumnLink(WebElement row, int columnNum) {
        try {
            String xpath = String.format("td[%d]/a", columnNum);
            return row.findElement(By.xpath(xpath)).getAttribute("href");
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public void verifyStudentRowContent(Student student, Course course,
                                        String expectedDetails, String expectedManageAccountLink) {
        WebElement studentRow = getStudentRow(student);
        String actualDetails = getStudentDetails(studentRow);
        String actualName = getStudentName(studentRow);
        String actualEmail = getStudentEmail(studentRow);
        String actualInstitute = getStudentInstitute(studentRow);
        String actualComment = getStudentComments(studentRow);
        String actualManageAccountLink = getStudentManageAccountLink(studentRow);

        String expectedName = student.getName();
        String expectedEmail = StringHelper.convertToEmptyStringIfNull(student.getEmail());
        String expectedInstitute = StringHelper.convertToEmptyStringIfNull(course.getInstitute().getName());
        String expectedComment = StringHelper.convertToEmptyStringIfNull(student.getComments());

        assertEquals(expectedDetails, actualDetails);
        assertEquals(expectedName, actualName);
        assertEquals(expectedEmail, actualEmail);
        assertEquals(expectedInstitute, actualInstitute);
        assertEquals(expectedComment, actualComment);
        assertEquals(expectedManageAccountLink, actualManageAccountLink);
    }

    public void verifyInstructorRowContent(Instructor instructor, Course course,
                                           String expectedManageAccountLink) {
        WebElement instructorRow = getInstructorRow(instructor);
        String actualCourseId = getInstructorCourseId(instructorRow);
        String actualName = getInstructorName(instructorRow);
        String actualEmail = getInstructorEmail(instructorRow);
        String actualInstitute = getInstructorInstitute(instructorRow);
        String actualManageAccountLink = getInstructorManageAccountLink(instructorRow);

        String expectedCourseId = instructor.getCourseId();
        String expectedName = instructor.getName();
        String expectedEmail = StringHelper.convertToEmptyStringIfNull(instructor.getEmail());
        String expectedInstitute = StringHelper.convertToEmptyStringIfNull(course.getInstitute().getName());

        assertEquals(expectedCourseId, actualCourseId);
        assertEquals(expectedName, actualName);
        assertEquals(expectedEmail, actualEmail);
        assertEquals(expectedInstitute, actualInstitute);
        assertEquals(expectedManageAccountLink, actualManageAccountLink);
    }

    public void verifyRegenerateInstructorKey() {
        verifyStatusMessage("User's key for this course has been successfully regenerated,"
                + " and the email has been sent.");
    }
}
