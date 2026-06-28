package teammates.e2e.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Represents the instructor send reminders page.
 */
public class InstructorSessionSendRemindersPage extends AppPage {

    public InstructorSessionSendRemindersPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Send Reminders");
    }

    public void submitReminders() {
        click(waitForElementPresence(By.id("btn-confirm-send-reminder")));
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

    public void submitReminderToSelectedStudent(String studentEmail) {
        selectStudentByEmail(studentEmail);
        submitReminders();
    }

    public void submitReminderToPreselectedNonSubmitters() {
        submitReminders();
    }
}
