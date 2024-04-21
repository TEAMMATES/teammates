package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.ui.output.AccountData;

/**
 * Page Object Model for the admin accounts page.
 */
public class AdminAccountsPage extends AppPage {

    @FindBy(id = "account-google-id")
    private WebElement accountId;

    @FindBy(id = "account-name")
    private WebElement accountName;

    @FindBy(id = "account-email")
    private WebElement accountEmail;

    @FindBy(id = "instructor-table")
    private WebElement instructorTable;

    @FindBy(id = "student-table")
    private WebElement studentTable;

    @FindBy(id = "btn-delete-account")
    private WebElement deleteAccountButton;

    public AdminAccountsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Account Details");
    }

    public void verifyAccountDetails(AccountAttributes account) {
        assertEquals(account.getGoogleId(), accountId.getText());
        assertEquals(account.getName(), accountName.getText());
        assertEquals(account.getEmail(), accountEmail.getText());
    }

    public void verifyAccountDetails(AccountData account) {
        assertEquals(account.getGoogleId(), accountId.getText());
        assertEquals(account.getName(), accountName.getText());
        assertEquals(account.getEmail(), accountEmail.getText());
    }

    public void clickRemoveInstructorFromCourse(String courseId) {
        List<WebElement> instructorRows =
                instructorTable.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));

        WebElement deleteButton = null;
        for (WebElement instructorRow : instructorRows) {
            List<WebElement> cells = instructorRow.findElements(By.tagName("td"));
            if (cells.get(0).getText().startsWith("[" + courseId + "]")) {
                deleteButton = cells.get(2).findElement(By.className("btn-danger"));
            }
        }

        if (deleteButton == null) {
            fail("Instructor to be deleted is not found");
        }
        click(deleteButton);
        waitForPageToLoad(true);
    }

    public void clickRemoveStudentFromCourse(String courseId) {
        List<WebElement> studentRows =
                studentTable.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));

        WebElement deleteButton = null;
        for (WebElement studentRow : studentRows) {
            List<WebElement> cells = studentRow.findElements(By.tagName("td"));
            if (cells.get(0).getText().startsWith("[" + courseId + "]")) {
                deleteButton = cells.get(2).findElement(By.className("btn-danger"));
            }
        }

        if (deleteButton == null) {
            fail("Student to be deleted is not found");
        }
        click(deleteButton);
        waitForPageToLoad(true);
    }

    public void clickDeleteAccount() {
        click(deleteAccountButton);
        waitForPageToLoad(true);
    }

}
