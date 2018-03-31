package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Const;

public class InstructorCourseDetailsPage extends AppPage {

    @FindBy (id = "button_sortstudentstatus")
    private WebElement sortByStatusIcon;

    @FindBy (id = "button_sortstudentname-0")
    private WebElement sortByNameIcon;

    @FindBy (id = "button_sortteam-0")
    private WebElement sortByTeamIcon;

    @FindBy (id = "button_remind")
    private WebElement remindAllButton;

    public InstructorCourseDetailsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Course Details</h1>");
    }

    public String getCourseId() {
        return browser.driver.findElement(By.id("courseid")).getText();
    }

    public InstructorCourseDetailsPage verifyIsCorrectPage(String courseId) {
        assertEquals(courseId, this.getCourseId());
        return this;
    }

    public InstructorCourseDetailsPage sortByStatus() {
        click(sortByStatusIcon);
        return this;
    }

    public InstructorCourseDetailsPage sortByName() {
        click(sortByNameIcon);
        return this;
    }

    public InstructorCourseDetailsPage sortByTeam() {
        click(sortByTeamIcon);
        return this;
    }

    public InstructorCourseDetailsPage clickRemindAllAndCancel() {
        click(remindAllButton);
        waitForConfirmationModalAndClickCancel();
        return this;
    }

    public InstructorCourseDetailsPage clickRemindAllAndConfirm() {
        click(remindAllButton);
        waitForConfirmationModalAndClickOk();
        return this;
    }

    public InstructorCourseStudentDetailsViewPage clickViewStudent(String studentName) {
        int rowId = getStudentRowId(studentName);
        click(getViewLink(rowId));
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorCourseStudentDetailsViewPage.class);
    }

    public InstructorCourseStudentDetailsEditPage clickEditStudent(String studentName) {
        int rowId = getStudentRowId(studentName);
        click(getEditLink(rowId));
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorCourseStudentDetailsEditPage.class);
    }

    public InstructorStudentRecordsPage clickAllRecordsLink(String studentName) {
        int rowId = getStudentRowId(studentName);
        click(getAllRecordsLink(rowId));
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorStudentRecordsPage.class);
    }

    public InstructorCourseDetailsPage clickRemindStudentAndCancel(String studentName) {
        int rowId = getStudentRowId(studentName);
        click(getRemindLink(rowId));
        waitForConfirmationModalAndClickCancel();
        return this;
    }

    public InstructorCourseDetailsPage clickRemindStudentAndConfirm(String studentName) {
        int rowId = getStudentRowId(studentName);
        click(getRemindLink(rowId));
        waitForConfirmationModalAndClickOk();
        return this;
    }

    public InstructorCourseDetailsPage clickDeleteAndCancel(String studentName) {
        int rowId = getStudentRowId(studentName);
        click(getDeleteLink(rowId));
        waitForConfirmationModalAndClickCancel();
        return this;
    }

    public InstructorCourseDetailsPage clickDeleteAndConfirm(String studentName) {
        int rowId = getStudentRowId(studentName);
        click(getDeleteLink(rowId));
        waitForConfirmationModalAndClickOk();
        return this;
    }

    public InstructorCourseDetailsPage clickDeleteAllAndCancel() {
        click(getDeleteAllLink());
        waitForConfirmationModalAndClickCancel();
        return this;
    }

    public InstructorCourseDetailsPage clickDeleteAllAndConfirm() {
        click(getDeleteAllLink());
        waitForConfirmationModalAndClickOk();
        return this;
    }

    private WebElement getViewLink(int studentNum) {
        WebElement studentRow = browser.driver.findElement(By.id("student-c0." + studentNum));
        return studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(1)"));
    }

    private WebElement getEditLink(int studentNum) {
        WebElement studentRow = browser.driver.findElement(By.id("student-c0." + studentNum));
        return studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(2)"));
    }

    private WebElement getRemindLink(int studentNum) {
        WebElement studentRow = browser.driver.findElement(By.id("student-c0." + studentNum));
        return studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(3)"));
    }

    private WebElement getDeleteLink(int studentNum) {
        WebElement studentRow = browser.driver.findElement(By.id("student-c0." + studentNum));
        WebElement thirdLink = studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(3)"));

        if ("Delete".equals(thirdLink.getText())) {
            return thirdLink;
        }
        return studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(4)"));
    }

    private WebElement getDeleteAllLink() {
        return browser.driver.findElement(By.id("button-delete-all"));
    }

    private WebElement getAllRecordsLink(int studentNum) {
        WebElement studentRow = browser.driver.findElement(By.id("student-c0." + studentNum));
        WebElement fourthLink = studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(4)"));

        if ("All Records".equals(fourthLink.getText())) {
            return fourthLink;
        }
        return studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(5)"));
    }

    private int getStudentRowId(String studentName) {
        int studentCount = browser.driver.findElements(By.className("student_row")).size();
        for (int i = 0; i < studentCount; i++) {
            String studentNameInRow = getStudentNameInRow(i);
            if (studentNameInRow.equals(studentName)) {
                return i;
            }
        }
        return -1;
    }

    private String getStudentNameInRow(int studentNum) {
        return browser.driver.findElement(By.id(Const.ParamsNames.STUDENT_NAME + "-c0." + studentNum)).getText();
    }

}
