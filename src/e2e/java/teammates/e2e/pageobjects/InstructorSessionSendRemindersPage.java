package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

/**
 * Represents the instructor send reminders page.
 */
public class InstructorSessionSendRemindersPage extends AppPage {
    private static final int STUDENT_SECTION_INDEX = 1;
    private static final int STUDENT_TEAM_INDEX = 2;
    private static final int STUDENT_NAME_INDEX = 3;
    private static final int STUDENT_EMAIL_INDEX = 4;
    private static final int STUDENT_SUBMITTED_INDEX = 5;
    private static final int INSTRUCTOR_NAME_INDEX = 1;
    private static final int INSTRUCTOR_EMAIL_INDEX = 2;
    private static final int INSTRUCTOR_SUBMITTED_INDEX = 3;

    public InstructorSessionSendRemindersPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Send Reminders");
    }

    public void verifySessionDetails(Course course, FeedbackSession session) {
        assertEquals(course.getId(), waitForElementVisibility(By.id("course-id")).getText());
        assertEquals(course.getTimeZone(), waitForElementVisibility(By.id("time-zone")).getText());
        assertEquals(course.getName(), waitForElementVisibility(By.id("course-name")).getText());
        assertEquals(session.getName(), waitForElementVisibility(By.id("session-name")).getText());
        assertEquals(getDetailedDateString(session.getEndTime(), course.getTimeZone()),
                waitForElementVisibility(By.id("deadline")).getText());
    }

    public void verifyStudentListDetails(Collection<Student> students, Set<String> submittedStudentEmails) {
        Map<String, Student> studentsByEmail = new HashMap<>();
        for (Student student : students) {
            studentsByEmail.put(student.getEmail(), student);
        }

        List<WebElement> rows = waitForElementVisibility(By.id("student-list-table")).findElements(By.cssSelector("tbody tr"));
        assertEquals(studentsByEmail.size(), rows.size());

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));
            String email = cells.get(STUDENT_EMAIL_INDEX).getText();
            Student expectedStudent = studentsByEmail.get(email);

            assertTrue(expectedStudent != null, "Unexpected student row found for email: " + email);
            assertEquals(expectedStudent.getSectionName(), cells.get(STUDENT_SECTION_INDEX).getText());
            assertEquals(expectedStudent.getTeamName(), cells.get(STUDENT_TEAM_INDEX).getText());
            assertEquals(expectedStudent.getName(), cells.get(STUDENT_NAME_INDEX).getText());
            assertEquals(email, cells.get(STUDENT_EMAIL_INDEX).getText());
            assertEquals(submittedStudentEmails.contains(email) ? "Yes" : "No",
                    cells.get(STUDENT_SUBMITTED_INDEX).getText());
        }
    }

    public void verifyInstructorListDetails(Collection<Instructor> instructors, Set<String> submittedInstructorEmails) {
        Map<String, Instructor> instructorsByEmail = new HashMap<>();
        for (Instructor instructor : instructors) {
            instructorsByEmail.put(instructor.getEmail(), instructor);
        }

        List<WebElement> rows = waitForElementVisibility(By.id("instructor-list-table"))
                .findElements(By.cssSelector("tbody tr"));
        assertEquals(instructorsByEmail.size(), rows.size());

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));
            String email = cells.get(INSTRUCTOR_EMAIL_INDEX).getText();
            Instructor expectedInstructor = instructorsByEmail.get(email);

            assertTrue(expectedInstructor != null, "Unexpected instructor row found for email: " + email);
            assertEquals(expectedInstructor.getName(), cells.get(INSTRUCTOR_NAME_INDEX).getText());
            assertEquals(email, cells.get(INSTRUCTOR_EMAIL_INDEX).getText());
            assertEquals(submittedInstructorEmails.contains(email) ? "Yes" : "No",
                    cells.get(INSTRUCTOR_SUBMITTED_INDEX).getText());
        }
    }

    public void submitReminders() {
        click(waitForElementPresence(By.id("btn-confirm-send-reminder")));
    }

    public void selectStudentsByEmail(String... studentEmails) {
        for (String studentEmail : studentEmails) {
            selectStudentByEmail(studentEmail);
        }
    }

    public void selectStudentByEmail(String studentEmail) {
        WebElement studentList = waitForElementVisibility(By.id("student-list-table"));
        List<WebElement> rows = studentList.findElements(By.cssSelector("tbody tr"));

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));
            if (!cells.isEmpty() && cells.get(4).getText().equals(studentEmail)) {
                click(cells.get(0).findElement(By.tagName("input")));
                break;
            }
        }
    }

    public void verifySelectedStudents(Set<String> selectedStudentEmails) {
        WebElement studentList = waitForElementVisibility(By.id("student-list-table"));
        List<WebElement> rows = studentList.findElements(By.cssSelector("tbody tr"));

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));
            String email = cells.get(STUDENT_EMAIL_INDEX).getText();
            boolean isSelected = cells.get(0).findElement(By.tagName("input")).isSelected();
            assertEquals(selectedStudentEmails.contains(email), isSelected);
        }
    }

    public void submitReminderToSelectedStudent(String studentEmail) {
        selectStudentByEmail(studentEmail);
        submitReminders();
    }

    public void submitReminderToPreselectedNonSubmitters() {
        submitReminders();
    }

    private String getDetailedDateString(Instant instant, String timeZone) {
        return getDisplayedDateTime(instant, timeZone, "EEE, dd MMM yyyy, hh:mm a X");
    }
}
