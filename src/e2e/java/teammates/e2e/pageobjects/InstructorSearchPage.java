package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Represents the instructor search page.
 */
public class InstructorSearchPage extends AppPage {

    @FindBy(id = "search-keyword")
    private WebElement searchKeyword;

    @FindBy(id = "btn-search")
    private WebElement searchButton;

    @FindBy(id = "students-checkbox")
    private WebElement studentsCheckbox;

    @FindBy(id = "comment-checkbox")
    private WebElement commentCheckbox;

    public InstructorSearchPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Search");
    }

    public void verifyNumCoursesInStudentResults(int expectedNum) {
        List<WebElement> studentCoursesResult = getStudentCoursesResult();
        assertEquals(expectedNum, studentCoursesResult.size());
    }

    public void inputSearchContent(String content) {
        searchKeyword.clear();
        searchKeyword.sendKeys(content);
    }

    public void verifyCannotClickSearchButton() {
        verifyUnclickable(searchButton);
    }

    public void clickSearchButton() {
        click(searchButton);
        waitForPageToLoad(true);
        waitUntilAnimationFinish();
    }

    public void clickStudentsCheckbox() {
        click(studentsCheckbox);
    }

    public void clickCommentsCheckbox() {
        click(commentCheckbox);
    }

    private List<WebElement> getStudentCoursesResult() {
        return browser.driver.findElements(By.className("student-course-table"));
    }

    public void verifyStudentDetails(Map<String, StudentAttributes[]> students) {
        List<WebElement> studentCoursesResult = getStudentCoursesResult();
        assertEquals(students.size(), studentCoursesResult.size());

        for (WebElement studentCourse : studentCoursesResult) {
            String courseId = studentCourse.findElement(By.className("card-header")).getText();
            WebElement studentList = studentCourse.findElement(By.tagName("table"));

            StudentAttributes[] studentsForCourse = students.get(courseId);
            verifyTableBodyValues(studentList, getExpectedStudentValues(studentsForCourse));
        }
    }

    private String[][] getExpectedStudentValues(StudentAttributes[] students) {
        String[][] expected = new String[students.length][6];
        for (int i = 0; i < students.length; i++) {
            StudentAttributes student = students[i];
            expected[i][0] = "View Photo";
            expected[i][1] = student.getSection();
            expected[i][2] = student.getTeam();
            expected[i][3] = student.getName();
            expected[i][4] = student.getGoogleId().isEmpty() ? "Yet to Join" : "Joined";
            expected[i][5] = student.getEmail();
        }
        return expected;
    }

}
