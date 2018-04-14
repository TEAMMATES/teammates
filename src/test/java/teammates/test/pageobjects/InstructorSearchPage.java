package teammates.test.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.common.util.Const;

public class InstructorSearchPage extends AppPage {

    public InstructorSearchPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Search</h1>");
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

    public void clickFeedbackResponseCommentCheckBox() {
        click(getFeedbackResponseCommentCheckBox());
    }

    public void clickStudentCheckBox() {
        click(getStudentCheckBox());
    }

    public InstructorSearchPage clickDeleteAndCancel(String courseId, String studentName) {
        String rowId = getStudentRowId(courseId, studentName);
        click(getDeleteLink(rowId));
        waitForConfirmationModalAndClickCancel();
        return this;
    }

    public InstructorSearchPage clickDeleteAndConfirm(String courseId, String studentName) {
        String rowId = getStudentRowId(courseId, studentName);
        click(getDeleteLink(rowId));
        waitForConfirmationModalAndClickOk();
        return this;
    }

    private WebElement getSearchBox() {
        return browser.driver.findElement(By.id("searchBox"));
    }

    private WebElement getSearchButton() {
        return browser.driver.findElement(By.id("buttonSearch"));
    }

    private WebElement getFeedbackResponseCommentCheckBox() {
        return browser.driver.findElement(By.id("search-feedback-sessions-data-check"));
    }

    private WebElement getStudentCheckBox() {
        return browser.driver.findElement(By.id("students-check"));
    }

    private int getCourseNumber(String courseId) {
        int id = -1;
        List<WebElement> panels = browser.driver.findElements(By.className("panel-heading"));
        for (WebElement panel : panels) {
            if (panel.getText().startsWith("[" + courseId + "]")) {
                break;
            }
            id++;
        }
        return id;
    }

    public String getStudentRowId(String courseId, String studentName) {
        int courseNumber = getCourseNumber(courseId);
        int studentCount = browser.driver.findElements(By.cssSelector("tr[id^='student-c" + courseNumber + "']"))
                .size();
        for (int i = 0; i < studentCount; i++) {
            String studentNameInRow = getStudentNameInRow(courseNumber, i);
            if (studentNameInRow.equals(studentName)) {
                return courseNumber + "." + i;
            }
        }
        return "";
    }

    private String getStudentNameInRow(int courseNumber, int rowId) {
        String xpath = "//tr[@id='student-c" + courseNumber + "." + rowId + "']"
                + "//td[@id='" + Const.ParamsNames.STUDENT_NAME + "-c" + courseNumber + "." + rowId + "']";
        return browser.driver.findElement(By.xpath(xpath)).getText();
    }

    private WebElement getDeleteLink(String rowId) {
        WebElement studentRow = browser.driver.findElement(By.id("student-c" + rowId));
        WebElement thirdLink = studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(3)"));

        if ("Delete".equals(thirdLink.getText())) {
            return thirdLink;
        }
        return studentRow.findElement(By.cssSelector("td.no-print.align-center > a:nth-child(4)"));
    }

    public void clickAndHoverPicture(String cellId) {
        click(browser.driver.findElement(By.id(cellId)).findElement(By.tagName("a")));
    }

}
