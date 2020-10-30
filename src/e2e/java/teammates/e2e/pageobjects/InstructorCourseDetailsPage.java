package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.StringJoiner;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.ThreadHelper;

/**
 * Represents the instructor course details page of the website.
 */
public class InstructorCourseDetailsPage extends AppPage {
    @FindBy(id = "course-id")
    private WebElement courseIdField;

    @FindBy(id = "course-name")
    private WebElement courseNameField;

    @FindBy(id = "num-sections")
    private WebElement numSectionsField;

    @FindBy(id = "num-teams")
    private WebElement numTeamsField;

    @FindBy(id = "num-students")
    private WebElement numStudentsField;

    @FindBy(id = "instructors")
    private WebElement instructorsField;

    public InstructorCourseDetailsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Course Details");
    }

    public void verifyCourseDetails(CourseAttributes course, InstructorAttributes[] instructors,
                                    int numSections, int numTeams, int numStudents) {
        assertEquals(course.getId(), courseIdField.getText());
        assertEquals(course.getName(), courseNameField.getText());
        assertEquals(Integer.toString(numSections), numSectionsField.getText());
        assertEquals(Integer.toString(numTeams), numTeamsField.getText());
        assertEquals(Integer.toString(numStudents), numStudentsField.getText());
        assertEquals(getExpectedInstructorString(instructors), instructorsField.getText());
    }

    public void verifyStudentDetails(StudentAttributes[] students) {
        verifyTableBodyValues(getStudentList(), getExpectedStudentValues(students));
    }

    public void verifyNumStudents(int expected) {
        assertEquals(expected, getNumStudents());
    }

    public void sendInvite(StudentAttributes student) {
        clickAndConfirm(getSendInviteButton(student));
    }

    public void remindAllToJoin() {
        clickAndConfirm(waitForElementPresence(By.id("btn-remind-all")));
    }

    public void downloadStudentList() {
        click(waitForElementPresence(By.id("btn-download")));
    }

    public void sortByName() {
        click(browser.driver.findElement(By.id("sort-by-name")));
        waitUntilAnimationFinish();
    }

    public void sortByStatus() {
        click(browser.driver.findElement(By.id("sort-by-status")));
        waitUntilAnimationFinish();
    }

    public void deleteStudent(StudentAttributes student) {
        clickAndConfirm(getDeleteButton(student));
    }

    public void deleteAllStudents() {
        clickAndConfirm(waitForElementPresence(By.id("btn-delete-all")));
    }

    private String getExpectedInstructorString(InstructorAttributes[] instructors) {
        StringJoiner expected = new StringJoiner(System.lineSeparator());
        for (InstructorAttributes instructor : instructors) {
            expected.add(instructor.getRole() + ": " + instructor.getName() + " (" + instructor.getEmail() + ")");
        }
        return expected.toString();
    }

    private WebElement getStudentList() {
        return browser.driver.findElement(By.cssSelector("#student-list table"));
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

    private WebElement getSendInviteButton(StudentAttributes student) {
        WebElement studentRow = getStudentRow(student);
        return studentRow.findElement(By.id("btn-send-invite"));
    }

    private WebElement getDeleteButton(StudentAttributes student) {
        WebElement studentRow = getStudentRow(student);
        return studentRow.findElement(By.id("btn-delete"));
    }

    private List<WebElement> getAllStudentRows() {
        return getStudentList().findElements(By.cssSelector("tbody tr"));
    }

    private int getNumStudents() {
        try {
            return getAllStudentRows().size();
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    private WebElement getStudentRow(StudentAttributes student) {
        List<WebElement> studentRows = getAllStudentRows();
        for (WebElement studentRow : studentRows) {
            List<WebElement> studentCells = studentRow.findElements(By.tagName("td"));
            if (studentCells.get(5).getText().equals(student.getEmail())) {
                return studentRow;
            }
        }
        return null;
    }

    public InstructorCourseStudentDetailsViewPage clickViewStudent(StudentAttributes student) {
        WebElement studentRow = getStudentRow(student);
        WebElement viewButton = studentRow.findElement(By.id("btn-view-details"));
        click(viewButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(InstructorCourseStudentDetailsViewPage.class);
    }

    public InstructorCourseStudentDetailsEditPage clickEditStudent(StudentAttributes student) {
        WebElement studentRow = getStudentRow(student);
        WebElement viewButton = studentRow.findElement(By.id("btn-edit-details"));
        click(viewButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(InstructorCourseStudentDetailsEditPage.class);
    }

    public InstructorStudentRecordsPage clickViewAllRecords(StudentAttributes student) {
        WebElement studentRow = getStudentRow(student);
        WebElement viewButton = studentRow.findElement(By.id("btn-view-records"));
        click(viewButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(InstructorStudentRecordsPage.class);
    }

}
