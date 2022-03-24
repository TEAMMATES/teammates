package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.TimeHelper;

/**
 * Page Object Model for the admin notifications page.
 */
public class AdminNotificationsPage extends AppPage {

    @FindBy(id = "btn-add-notification")
    private WebElement addNotificationButton;

    @FindBy(id = "notifications-table")
    private WebElement notificationsTable;

    @FindBy(id = "notifications-timezone")
    private WebElement notificationsTimezone;

    public AdminNotificationsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Notifications");
    }

    public void verifyNotificationsTable(NotificationAttributes[] notifications) {
        boolean[] notificationVerified = new boolean[notifications.length];
        List<WebElement> notificationRows = notificationsTable.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
        for (int i = 0; i < notifications.length; i++) {
            String[] notificationDetails = getNotificationDetails(notifications[i]);
            for (WebElement notificationRow : notificationRows) {
                List<WebElement> cells = notificationRow.findElements(By.tagName("td"));
                if (notificationDetails[0].equals(cells.get(0).getText()) && notificationDetails[5].equals(cells.get(5).getText())) {
                    verifyTableRowValues(notificationRow, notificationDetails);
                    notificationVerified[i] = true;
                }
            }
        }
        for (int i = 0; i < notifications.length; i++) {
            assertEquals(notificationVerified[i], true);
        }
    }

    private String[] getNotificationDetails(NotificationAttributes notification) {
        String[] details = new String[6];
        details[0] = notification.getTitle();
        details[1] = getDisplayedDate(notification.getStartTime());
        details[2] = getDisplayedDate(notification.getEndTime());
        details[3] = notification.getTargetUser().toString();
        details[4] = notification.getType().toString();
        details[5] = getDisplayedDate(notification.getCreatedAt());
        return details;
    }

    private String getTimezone() {
        return notificationsTimezone.getText().replace("All dates are displayed in ", "").replace(" time.", "");
    }

    private String getDisplayedDate(Instant date) {
        return TimeHelper.formatInstant(date, getTimezone(), "d MMM h:mm a");
    }

}
