package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.e2e.util.TestProperties;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.test.ThreadHelper;

/**
 * Represents the instructor course details page of the website.
 */
public class InstructorCourseDetailsPageSql extends AppPage {
    @FindBy(id = "course-id")
    private WebElement courseIdField;

    @FindBy(id = "course-name")
    private WebElement courseNameField;

    @FindBy(id = "course-institute")
    private WebElement courseInstituteField;

    @FindBy(id = "num-sections")
    private WebElement numSectionsField;

    @FindBy(id = "num-teams")
    private WebElement numTeamsField;

    @FindBy(id = "num-students")
    private WebElement numStudentsField;

    @FindBy(id = "instructors")
    private WebElement instructorsField;

    public InstructorCourseDetailsPageSql(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Course Details");
    }

    public void verifyCourseDetails(Course course, List<Instructor> instructors,
                                    int numSections, int numTeams, int numStudents) {
        assertEquals(course.getId(), courseIdField.getText());
        assertEquals(course.getName(), courseNameField.getText());
        assertEquals(course.getInstitute(), courseInstituteField.getText());
        assertEquals(Integer.toString(numSections), numSectionsField.getText());
        assertEquals(Integer.toString(numTeams), numTeamsField.getText());
        assertEquals(Integer.toString(numStudents), numStudentsField.getText());
        assertEquals(getExpectedInstructorsString(instructors), instructorsField.getText());
    }

    public void verifyStudentDetails(List<Student> students) {
        verifyTableBodyValues(getStudentList(), getExpectedStudentValues(students));
    }

    public void verifyNumStudents(int expected) {
        assertEquals(expected, getNumStudents());
    }

    public void sendInvite(String studentEmailAddress) {
        clickAndConfirm(getSendInviteButton(studentEmailAddress));
    }

    public void remindAllToJoin() {
        clickAndConfirm(waitForElementPresence(By.id("btn-remind-all")));
    }

    public void downloadStudentList() {
        click(waitForElementPresence(By.id("btn-download")));
    }

    public void sortByName() {
        click(browser.driver.findElement(By.className("sort-by-name")));
        waitUntilAnimationFinish();
    }

    public void sortByStatus() {
        click(browser.driver.findElement(By.className("sort-by-status")));
        waitUntilAnimationFinish();
    }

    public void deleteStudent(String studentEmailAddress) {
        clickAndConfirm(getDeleteButton(studentEmailAddress));
    }

    public void deleteAllStudents() {
        clickAndConfirm(waitForElementPresence(By.id("btn-delete-all")));
    }

    private String getExpectedInstructorsString(List<Instructor> instructors) {
        return instructors.stream()
                .map(instructor -> instructor.getRole().getRoleName() + ": "
                        + instructor.getName() + " (" + instructor.getEmail() + ")")
                .collect(Collectors.joining(TestProperties.LINE_SEPARATOR));
    }

    private WebElement getStudentList() {
        return browser.driver.findElement(By.cssSelector("#student-list table"));
    }

    private String[][] getExpectedStudentValues(List<Student> students) {
        String[][] expected = new String[students.size()][5];
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            expected[i][0] = student.getSectionName();
            expected[i][1] = student.getTeamName();
            expected[i][2] = student.getName();
            String googleId = student.getGoogleId();
            if (googleId == null || googleId.isEmpty()) {
                expected[i][3] = "Yet to Join";
            } else {
                expected[i][3] = "Joined";
            }
            expected[i][4] = student.getEmail();
        }
        return expected;
    }

    private WebElement getSendInviteButton(String studentEmailAddress) {
        WebElement studentRow = getStudentRow(studentEmailAddress);
        return studentRow.findElement(By.cssSelector("[id^='btn-send-invite-']"));
    }

    private WebElement getDeleteButton(String studentEmailAddress) {
        WebElement studentRow = getStudentRow(studentEmailAddress);
        return studentRow.findElement(By.cssSelector("[id^='btn-delete-']"));
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

    private WebElement getStudentRow(String studentEmailAddress) {
        List<WebElement> studentRows = getAllStudentRows();
        for (WebElement studentRow : studentRows) {
            List<WebElement> studentCells = studentRow.findElements(By.tagName("td"));
            if (studentCells.get(4).getText().equals(studentEmailAddress)) {
                return studentRow;
            }
        }
        return null;
    }

    public InstructorCourseStudentDetailsViewPage clickViewStudent(String studentEmailAddress) {
        WebElement studentRow = getStudentRow(studentEmailAddress);
        WebElement viewButton = studentRow.findElement(By.cssSelector("[id^='btn-view-details-']"));
        click(viewButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(InstructorCourseStudentDetailsViewPage.class);
    }

    public InstructorCourseStudentDetailsEditPage clickEditStudent(String studentEmailAddress) {
        WebElement studentRow = getStudentRow(studentEmailAddress);
        WebElement viewButton = studentRow.findElement(By.cssSelector("[id^='btn-edit-details-']"));
        click(viewButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(InstructorCourseStudentDetailsEditPage.class);
    }

    public InstructorStudentRecordsPage clickViewAllRecords(String studentEmailAddress) {
        WebElement studentRow = getStudentRow(studentEmailAddress);
        WebElement viewButton = studentRow.findElement(By.cssSelector("[id^='btn-view-records-']"));
        click(viewButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(InstructorStudentRecordsPage.class);
    }

}
