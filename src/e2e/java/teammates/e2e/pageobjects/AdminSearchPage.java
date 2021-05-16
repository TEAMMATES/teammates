package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.e2e.util.TestProperties;

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

    private static final String EXPANDED_ROWS_HEADER_EMAIL = "Email";
    private static final String EXPANDED_ROWS_HEADER_COURSE_JOIN_LINK = "Course Join Link";
    private static final String LINK_TEXT_RESET_GOOGLE_ID = "Reset Google ID";

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
        waitForPageToLoad();
    }

    public void regenerateLinksForStudent(StudentAttributes student) {
        WebElement studentRow = getStudentRow(student);
        studentRow.findElement(By.xpath("//button[text()='Regenerate links']")).click();

        waitForConfirmationModalAndClickOk();
        waitForPageToLoad(true);
    }

    public void clickExpandStudentLinks() {
        click(expandStudentLinksButton);
        waitForPageToLoad();
    }

    public void clickExpandInstructorLinks() {
        click(expandInstructorLinksButton);
        waitForPageToLoad();
    }

    public void clickCollapseStudentLinks() {
        click(collapseStudentLinksButton);
        waitForPageToLoad();
    }

    public void clickCollapseInstructorLinks() {
        click(collapseInstructorLinksButton);
        waitForPageToLoad();
    }

    public WebElement getStudentRow(StudentAttributes student) {
        String details = String.format("%s [%s] (%s)", student.course,
                student.section == null ? Const.DEFAULT_SECTION : student.section, student.team);
        List<WebElement> rows = browser.driver.findElements(By.cssSelector("#search-table-student tbody tr"));
        for (WebElement row : rows) {
            List<WebElement> columns = row.findElements(By.tagName("td"));
            if (columns.get(STUDENT_COL_DETAILS - 1).getAttribute("innerHTML").contains(details)
                    && columns.get(STUDENT_COL_NAME - 1).getAttribute("innerHTML").contains(student.name)) {
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

    public String getStudentGoogleId(WebElement studentRow) {
        return getColumnText(studentRow, STUDENT_COL_GOOGLE_ID);
    }

    public String getStudentHomeLink(WebElement studentRow) {
        return getColumnLink(studentRow, STUDENT_COL_GOOGLE_ID);
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

    public String getStudentEmail(WebElement studentRow) {
        return getExpandedRowInputValue(studentRow, EXPANDED_ROWS_HEADER_EMAIL);
    }

    public String getStudentJoinLink(WebElement studentRow) {
        return getExpandedRowInputValue(studentRow, EXPANDED_ROWS_HEADER_COURSE_JOIN_LINK);
    }

    public String getStudentJoinLink(StudentAttributes student) {
        WebElement studentRow = getStudentRow(student);
        return getStudentJoinLink(studentRow);
    }

    public void resetStudentGoogleId(StudentAttributes student) {
        WebElement studentRow = getStudentRow(student);
        WebElement link = studentRow.findElement(By.linkText(LINK_TEXT_RESET_GOOGLE_ID));
        link.click();

        waitForConfirmationModalAndClickOk();
        waitForElementStaleness(link);
    }

    public WebElement getInstructorRow(InstructorAttributes instructor) {
        String xpath = String.format(
                "//table[@id='search-table-instructor']/tbody/tr[td[%d][span[text()='%s']] and td[%d]='%s']",
                INSTRUCTOR_COL_COURSE_ID, instructor.getCourseId(), INSTRUCTOR_COL_NAME, instructor.name);
        return browser.driver.findElement(By.xpath(xpath));
    }

    public String getInstructorCourseId(WebElement instructorRow) {
        return getColumnText(instructorRow, INSTRUCTOR_COL_COURSE_ID);
    }

    public String getInstructorName(WebElement instructorRow) {
        return getColumnText(instructorRow, INSTRUCTOR_COL_NAME);
    }

    public String getInstructorGoogleId(WebElement instructorRow) {
        return getColumnText(instructorRow, INSTRUCTOR_COL_GOOGLE_ID);
    }

    public String getInstructorHomePageLink(WebElement instructorRow) {
        return getColumnLink(instructorRow, INSTRUCTOR_COL_GOOGLE_ID);
    }

    public String getInstructorInstitute(WebElement instructorRow) {
        return getColumnText(instructorRow, INSTRUCTOR_COL_INSTITUTE);
    }

    public String getInstructorManageAccountLink(WebElement instructorRow) {
        return getColumnLink(instructorRow, INSTRUCTOR_COL_OPTIONS);
    }

    public String getInstructorEmail(WebElement instructorRow) {
        return getExpandedRowInputValue(instructorRow, EXPANDED_ROWS_HEADER_EMAIL);
    }

    public String getInstructorJoinLink(WebElement instructorRow) {
        return getExpandedRowInputValue(instructorRow, EXPANDED_ROWS_HEADER_COURSE_JOIN_LINK);
    }

    public void resetInstructorGoogleId(InstructorAttributes instructor) {
        WebElement instructorRow = getInstructorRow(instructor);
        WebElement link = instructorRow.findElement(By.linkText(LINK_TEXT_RESET_GOOGLE_ID));
        link.click();

        waitForConfirmationModalAndClickOk();
        waitForElementStaleness(link);
    }

    public int getNumExpandedRows(WebElement row) {
        String xpath = "following-sibling::tr[1]/td/ul/li";
        return row.findElements(By.xpath(xpath)).size();
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

    private String getExpandedRowInputValue(WebElement row, String rowHeader) {
        try {
            String xpath = String.format("following-sibling::tr[1]/td/ul/li[contains(., '%s')]/input", rowHeader);
            return row.findElement(By.xpath(xpath)).getAttribute("value");
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public void verifyStudentRowContent(StudentAttributes student, AccountAttributes account,
                                        String expectedDetails, String expectedManageAccountLink,
                                        String expectedHomePageLink) {
        WebElement studentRow = getStudentRow(student);
        String actualDetails = getStudentDetails(studentRow);
        String actualName = getStudentName(studentRow);
        String actualGoogleId = getStudentGoogleId(studentRow);
        String actualHomepageLink = getStudentHomeLink(studentRow);
        String actualInstitute = getStudentInstitute(studentRow);
        String actualComment = getStudentComments(studentRow);
        String actualManageAccountLink = getStudentManageAccountLink(studentRow);

        String expectedName = student.name;
        String expectedGoogleId = StringHelper.convertToEmptyStringIfNull(student.googleId);
        String expectedInstitute = StringHelper.convertToEmptyStringIfNull(account.institute);
        String expectedComment = StringHelper.convertToEmptyStringIfNull(student.comments);

        assertEquals(expectedDetails, actualDetails);
        assertEquals(expectedName, actualName);
        assertEquals(expectedGoogleId, actualGoogleId);
        assertEquals(expectedInstitute, actualInstitute);
        assertEquals(expectedComment, actualComment);
        assertEquals(expectedManageAccountLink, actualManageAccountLink);
        assertEquals(expectedHomePageLink, actualHomepageLink);
    }

    public void verifyStudentExpandedLinks(StudentAttributes student, int expectedNumExpandedRows) {
        clickExpandStudentLinks();
        WebElement studentRow = getStudentRow(student);
        String actualEmail = getStudentEmail(studentRow);
        String actualJoinLink = getStudentJoinLink(studentRow);
        int actualNumExpandedRows = getNumExpandedRows(studentRow);

        String expectedEmail = student.email;

        assertEquals(expectedEmail, actualEmail);
        assertNotEquals("", actualJoinLink);
        assertEquals(expectedNumExpandedRows, actualNumExpandedRows);
    }

    public void verifyInstructorRowContent(InstructorAttributes instructor, AccountAttributes account,
                                           String expectedManageAccountLink, String expectedHomePageLink) {
        WebElement instructorRow = getInstructorRow(instructor);
        String actualCourseId = getInstructorCourseId(instructorRow);
        String actualName = getInstructorName(instructorRow);
        String actualGoogleId = getInstructorGoogleId(instructorRow);
        String actualHomePageLink = getInstructorHomePageLink(instructorRow);
        String actualInstitute = getInstructorInstitute(instructorRow);
        String actualManageAccountLink = getInstructorManageAccountLink(instructorRow);

        String expectedCourseId = instructor.courseId;
        String expectedName = instructor.name;
        String expectedGoogleId = StringHelper.convertToEmptyStringIfNull(instructor.googleId);
        String expectedInstitute = StringHelper.convertToEmptyStringIfNull(account.institute);

        assertEquals(expectedCourseId, actualCourseId);
        assertEquals(expectedName, actualName);
        assertEquals(expectedGoogleId, actualGoogleId);
        assertEquals(expectedHomePageLink, actualHomePageLink);
        assertEquals(expectedInstitute, actualInstitute);
        assertEquals(expectedManageAccountLink, actualManageAccountLink);
    }

    public void verifyInstructorExpandedLinks(InstructorAttributes instructor) {
        clickExpandInstructorLinks();
        WebElement instructorRow = getInstructorRow(instructor);
        String actualEmail = getInstructorEmail(instructorRow);
        String actualJoinLink = getInstructorJoinLink(instructorRow);

        String expectedEmail = instructor.email;

        assertEquals(expectedEmail, actualEmail);
        assertNotEquals("", actualJoinLink);
    }

    public void verifyLinkExpansionButtons(StudentAttributes student, InstructorAttributes instructor) {
        WebElement studentRow = getStudentRow(student);
        WebElement instructorRow = getInstructorRow(instructor);

        clickExpandStudentLinks();
        clickExpandInstructorLinks();
        int numExpandedStudentRows = getNumExpandedRows(studentRow);
        int numExpandedInstructorRows = getNumExpandedRows(instructorRow);
        assertNotEquals(numExpandedStudentRows, 0);
        assertNotEquals(numExpandedInstructorRows, 0);

        clickCollapseInstructorLinks();
        numExpandedStudentRows = getNumExpandedRows(studentRow);
        numExpandedInstructorRows = getNumExpandedRows(instructorRow);
        assertNotEquals(numExpandedStudentRows, 0);
        assertEquals(numExpandedInstructorRows, 0);

        clickExpandInstructorLinks();
        clickCollapseStudentLinks();
        waitUntilAnimationFinish();

        numExpandedStudentRows = getNumExpandedRows(studentRow);
        numExpandedInstructorRows = getNumExpandedRows(instructorRow);
        assertEquals(numExpandedStudentRows, 0);
        assertNotEquals(numExpandedInstructorRows, 0);
    }

    public void verifyRegenerateStudentCourseLinks(StudentAttributes student, String originalJoinLink) {
        if (TestProperties.isDevServer() || TestProperties.INCLUDE_EMAIL_VERIFICATION) {
            verifyStatusMessage("Student's links for this course have been successfully regenerated,"
                    + " and the email has been sent.");
        }

        String regeneratedJoinLink = getStudentJoinLink(student);
        assertNotEquals(regeneratedJoinLink, originalJoinLink);
    }

}
