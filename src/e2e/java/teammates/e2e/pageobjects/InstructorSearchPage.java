package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.StringHelper;
import teammates.test.ThreadHelper;

/**
 * Represents the instructor search page.
 */
public class InstructorSearchPage extends AppPage {

    @FindBy(id = "search-keyword")
    private WebElement searchKeyword;

    @FindBy(id = "btn-search")
    private WebElement searchButton;

    public InstructorSearchPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Search");
    }

    public void search(String searchTerm) {
        searchKeyword.clear();
        searchKeyword.sendKeys(searchTerm);
        if (StringHelper.isEmpty(searchTerm)) {
            verifyUnclickable(searchButton);
            return;
        }
        click(searchButton);
        WebElement loadingContainer = null;
        try {
            loadingContainer = waitForElementPresence(By.className("loading-container"));
        } catch (TimeoutException e) {
            // loading has finished before this block is reached
        }
        if (loadingContainer != null) {
            waitForElementStaleness(loadingContainer);
        }
    }

    private List<WebElement> getStudentCoursesResult() {
        return browser.driver.findElements(By.className("student-course-table"));
    }

    private String createHeaderText(CourseAttributes course) {
        return "[" + course.getId() + "]";
    }

    public void verifyStudentDetails(Map<String, CourseAttributes> courses, Map<String, StudentAttributes[]> students) {
        List<WebElement> studentCoursesResult = getStudentCoursesResult();
        assertEquals(students.size(), courses.size());
        assertEquals(students.size(), studentCoursesResult.size());

        students.forEach((courseId, studentsForCourse) -> verifyStudentDetails(courses.get(courseId), studentsForCourse));
    }

    public void verifyStudentDetails(CourseAttributes course, StudentAttributes[] students) {
        WebElement targetCourse = getStudentTableForHeader(course);
        if (targetCourse == null) {
            fail("Course with ID " + course.getId() + " is not found");
        }

        WebElement studentList = targetCourse.findElement(By.tagName("table"));
        verifyTableBodyValues(studentList, getExpectedStudentValues(students));
    }

    private WebElement getStudentTableForHeader(CourseAttributes course) {
        String targetHeader = createHeaderText(course);
        List<WebElement> studentCoursesResult = getStudentCoursesResult();

        return studentCoursesResult.stream().filter(studentCourse -> {
            String courseHeader = studentCourse.findElement(By.className("card-header")).getText();
            return targetHeader.equals(courseHeader);
        }).findFirst().orElse(null);
    }

    private String[][] getExpectedStudentValues(StudentAttributes[] students) {
        String[][] expected = new String[students.length][5];
        for (int i = 0; i < students.length; i++) {
            StudentAttributes student = students[i];
            expected[i][0] = student.getSection();
            expected[i][1] = student.getTeam();
            expected[i][2] = student.getName();
            expected[i][3] = student.getGoogleId().isEmpty() ? "Yet to Join" : "Joined";
            expected[i][4] = student.getEmail();
        }
        return expected;
    }

    public void deleteStudent(CourseAttributes course, String studentEmail) {
        clickAndConfirm(getDeleteButton(course, studentEmail));
        waitUntilAnimationFinish();
    }

    private WebElement getDeleteButton(CourseAttributes course, String studentEmail) {
        WebElement studentRow = getStudentRow(course, studentEmail);
        return studentRow.findElement(By.id("btn-delete"));
    }

    private WebElement getStudentRow(CourseAttributes course, String studentEmail) {
        WebElement targetCourse = getStudentTableForHeader(course);
        if (targetCourse == null) {
            fail("Course with ID " + course.getId() + " is not found");
        }

        List<WebElement> studentRows = targetCourse.findElements(By.cssSelector("tbody tr"));
        for (WebElement studentRow : studentRows) {
            List<WebElement> studentCells = studentRow.findElements(By.tagName("td"));
            if (studentCells.get(4).getText().equals(studentEmail)) {
                return studentRow;
            }
        }
        return null;
    }

    public InstructorCourseStudentDetailsViewPage clickViewStudent(CourseAttributes course, String studentEmail) {
        WebElement studentRow = getStudentRow(course, studentEmail);
        WebElement viewButton = studentRow.findElement(By.id("btn-view-details"));
        click(viewButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(InstructorCourseStudentDetailsViewPage.class);
    }

    public InstructorCourseStudentDetailsEditPage clickEditStudent(CourseAttributes course, String studentEmail) {
        WebElement studentRow = getStudentRow(course, studentEmail);
        WebElement viewButton = studentRow.findElement(By.id("btn-edit-details"));
        click(viewButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(InstructorCourseStudentDetailsEditPage.class);
    }

    public InstructorStudentRecordsPage clickViewAllRecords(CourseAttributes course, String studentEmail) {
        WebElement studentRow = getStudentRow(course, studentEmail);
        WebElement viewButton = studentRow.findElement(By.id("btn-view-records"));
        click(viewButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(InstructorStudentRecordsPage.class);
    }

}
