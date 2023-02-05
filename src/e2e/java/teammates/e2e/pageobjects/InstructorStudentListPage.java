package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.e2e.util.TestProperties;
import teammates.test.ThreadHelper;

/**
 * Page Object Model for instructor student list page.
 */
public class InstructorStudentListPage extends AppPage {

    public InstructorStudentListPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Students");
    }

    private List<WebElement> getCoursesTabs() {
        return browser.driver.findElements(By.className("course-table"));
    }

    private String createHeaderText(CourseAttributes course) {
        return String.format("[%s]: %s", course.getId(), course.getName());
    }

    public void clickCourseTabHeader(CourseAttributes course) {
        String targetHeader = createHeaderText(course);
        List<WebElement> courseTabs = getCoursesTabs();
        for (WebElement courseTab : courseTabs) {
            WebElement headerElement = courseTab.findElement(By.className("card-header"));
            String header = headerElement.getText();
            if (header.equals(targetHeader)) {
                click(headerElement);
                waitForPageToLoad();
                waitUntilAnimationFinish();
            }
        }
    }

    public void verifyAllCoursesHaveTabs(Collection<CourseAttributes> courses) {
        List<WebElement> courseTabs = getCoursesTabs();
        assertEquals(courses.size(), courseTabs.size());
    }

    public void verifyStudentDetails(Map<String, CourseAttributes> courses, Map<String, StudentAttributes[]> students) {
        assertEquals(students.size(), courses.size());

        students.forEach((courseId, studentsForCourse) -> verifyStudentDetails(courses.get(courseId), studentsForCourse));
    }

    public void verifyStudentDetails(CourseAttributes course, StudentAttributes[] students) {
        WebElement targetCourse = getCourseTab(course);
        if (targetCourse == null) {
            fail("Course with ID " + course.getId() + " is not found");
        }

        if (students.length == 0) {
            String noStudentText = targetCourse.findElement(By.className("card-body")).getText();
            // Need to account for the text from the enroll students button as well
            String expectedText = "There are no students in this course."
                    + TestProperties.LINE_SEPARATOR + "Enroll Students";
            assertEquals(expectedText, noStudentText);
        } else {
            WebElement studentList = targetCourse.findElement(By.tagName("table"));
            verifyTableBodyValues(studentList, getExpectedStudentValues(students));
            verifyDisplayedNumbers(targetCourse, students);
        }
    }

    public void verifyStudentDetailsNotViewable(CourseAttributes course) {
        WebElement targetCourse = getCourseTab(course);
        if (targetCourse == null) {
            fail("Course with ID " + course.getId() + " is not found");
        }
        String noViewStudentsPermissionText = targetCourse.findElement(By.className("card-body")).getText();
        String expectedText = "You do not have permission to view the details of the students in this course.";
        assertEquals(expectedText, noViewStudentsPermissionText);
    }

    private WebElement getCourseTab(CourseAttributes course) {
        String targetHeader = createHeaderText(course);
        List<WebElement> courseTabs = getCoursesTabs();

        return courseTabs.stream().filter(courseTab -> {
            String courseHeader = courseTab.findElement(By.className("card-header")).getText();
            return targetHeader.equals(courseHeader);
        }).findFirst().orElse(null);
    }

    private void verifyDisplayedNumbers(WebElement courseTab, StudentAttributes[] students) {
        String nStudents = courseTab.findElement(By.id("num-students")).getText();
        String nSections = courseTab.findElement(By.id("num-sections")).getText();
        String nTeams = courseTab.findElement(By.id("num-teams")).getText();

        String expectedNStudents = students.length + " students";
        String expectedNSections = Arrays.stream(students)
                .map(StudentAttributes::getSection)
                .distinct()
                .count() + " sections";
        String expectedNTeams = Arrays.stream(students)
                .map(StudentAttributes::getTeam)
                .distinct()
                .count() + " teams";

        assertEquals(expectedNStudents, nStudents);
        assertEquals(expectedNSections, nSections);
        assertEquals(expectedNTeams, nTeams);
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
        WebElement targetCourse = getCourseTab(course);
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

    public InstructorCourseEnrollPage clickEnrollStudents(CourseAttributes course) {
        WebElement studentRow = getCourseTab(course);
        WebElement enrollButton = studentRow.findElement(By.id("btn-enroll"));
        click(enrollButton);
        waitForPageToLoad();
        return changePageType(InstructorCourseEnrollPage.class);
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
