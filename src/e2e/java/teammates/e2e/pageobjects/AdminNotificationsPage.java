package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;

/**
 * Page Object Model for the admin notifications page.
 */
public class AdminNotificationsPage extends AppPage {

    @FindBy(id = "btn-add-notification")
    private WebElement addNotificationButton;

    @FindBy(id = "notifications-timezone")
    private WebElement notificationsTimezone;

    @FindBy(id = "target-user")
    private WebElement notificationTargetUserDropdown;

    @FindBy(id = "notification-style")
    private WebElement notificationStyleDropdown;

    @FindBy(id = "notification-title")
    private WebElement notificationTitleTextBox;

    @FindBy(id = "message")
    private WebElement notificationMessageEditor;

    @FindBy(id = "notification-start-date")
    private WebElement startDateBox;

    @FindBy(id = "notification-start-time")
    private WebElement startTimeDropdown;

    @FindBy(id = "notification-end-date")
    private WebElement endDateBox;

    @FindBy(id = "notification-end-time")
    private WebElement endTimeDropdown;

    @FindBy(id = "btn-create-notification")
    private WebElement createNotificationButton;

    @FindBy(id = "btn-edit-notification")
    private WebElement editNotificationButton;

    @FindBy(id = "notifications-table")
    private WebElement notificationsTable;

    public AdminNotificationsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Notifications");
    }

    public void verifyNotificationsTable(NotificationAttributes[] notifications) {
        // Only validates that the notifications are present in the notifications table instead of checking every row
        // This is because the page will display all notifications in the database, which is not predictable
        for (NotificationAttributes notification : notifications) {
            WebElement notificationRow = notificationsTable.findElement(By.id(notification.getNotificationId()));
            verifyTableRowValues(notificationRow, getNotificationTableDisplayDetails(notification));
        }
    }

    public void verifyNotificationAttributes(NotificationAttributes expected, NotificationAttributes actual) {
        // Note: Notification ID is not checked
        // as the actual notification has a randomly generated ID
        assertEquals(expected.getStartTime(), actual.getStartTime());
        assertEquals(expected.getEndTime(), actual.getEndTime());
        assertEquals(expected.getStyle(), actual.getStyle());
        assertEquals(expected.getTargetUser(), actual.getTargetUser());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getMessage(), actual.getMessage());
    }

    public void addNotification(NotificationAttributes notification) {
        clickAddNotificationButton();
        waitForElementPresence(By.id("btn-create-notification"));

        fillNotificationForm(notification);

        clickCreateNotificationButton();
        waitForPageToLoad(true);
    }

    public void editNotification(NotificationAttributes notification) {
        WebElement notificationRow = notificationsTable.findElement(By.id(notification.getNotificationId()));
        WebElement editButton = notificationRow.findElement(By.className("btn-light"));
        editButton.click();
        waitForElementPresence(By.id("btn-edit-notification"));

        fillNotificationForm(notification);

        clickEditNotificationButton();
        waitForPageToLoad(true);
    }

    public void deleteNotification(NotificationAttributes notification) {
        WebElement notificationRow = notificationsTable.findElement(By.id(notification.getNotificationId()));
        WebElement deleteButton = notificationRow.findElement(By.className("btn-danger"));

        deleteButton.click();
        waitForConfirmationModalAndClickOk();
        waitForPageToLoad(true);
    }

    public void fillNotificationForm(NotificationAttributes notification) {
        selectDropdownOptionByText(notificationTargetUserDropdown, getTargetUserText(notification.getTargetUser()));
        selectDropdownOptionByText(notificationStyleDropdown, getNotificationStyle(notification.getStyle()));
        fillTextBox(notificationTitleTextBox, notification.getTitle());
        setMessage(notification.getMessage());
        setNotificationStartDateTime(notification.getStartTime());
        setNotificationEndDateTime(notification.getEndTime());
    }

    public String getNewestNotificationId() {
        WebElement latestNotificationRow = notificationsTable.findElement(By.className("table-success"));
        return latestNotificationRow.getAttribute("id");
    }

    private void clickAddNotificationButton() {
        click(addNotificationButton);
    }

    private void clickCreateNotificationButton() {
        click(createNotificationButton);
    }

    private void clickEditNotificationButton() {
        click(editNotificationButton);
    }

    private void setMessage(String message) {
        writeToRichTextEditor(notificationMessageEditor, message);
    }

    private void setNotificationStartDateTime(Instant startInstant) {
        setDateTime(startDateBox.findElement(By.tagName("input")), startTimeDropdown, startInstant);
    }

    private void setNotificationEndDateTime(Instant endInstant) {
        setDateTime(endDateBox.findElement(By.tagName("input")), endTimeDropdown, endInstant);
    }

    private void setDateTime(WebElement dateBox, WebElement timeBox, Instant startInstant) {
        fillTextBox(dateBox, getInputDateString(startInstant));
        selectDropdownOptionByText(timeBox.findElement(By.tagName("select")), getInputTimeString(startInstant));
    }

    private String[] getNotificationTableDisplayDetails(NotificationAttributes notification) {
        String[] details = new String[6];
        details[0] = notification.getTitle();
        details[1] = getTableDisplayDateString(notification.getStartTime());
        details[2] = getTableDisplayDateString(notification.getEndTime());
        details[3] = notification.getTargetUser().toString();
        details[4] = getNotificationStyle(notification.getStyle());
        details[5] = getTableDisplayDateString(notification.getCreatedAt());
        return details;
    }

    private String getTimezone() {
        return notificationsTimezone.getText().replace("All dates are displayed in ", "").replace(" time.", "");
    }

    private String getInputDateString(Instant instant) {
        return getDisplayedDateTime(instant, getTimezone(), "EE, dd MMM, yyyy");
    }

    private String getInputTimeString(Instant instant) {
        String timezone = getTimezone();
        ZonedDateTime dateTime = instant.atZone(ZoneId.of(timezone));
        if (dateTime.getHour() == 0 && dateTime.getMinute() == 0) {
            return "23:59H";
        }
        return getDisplayedDateTime(instant, timezone, "HH:00") + "H";
    }

    private String getTableDisplayDateString(Instant date) {
        return getDisplayedDateTime(date, getTimezone(), "d MMM h:mm a");
    }

    private String getTargetUserText(NotificationTargetUser userType) {
        switch (userType) {
        case STUDENT:
            return "Students";
        case INSTRUCTOR:
            return "Instructors";
        case GENERAL:
            return "General (for both students and instructors)";
        default:
            return "";
        }
    }

    private String getNotificationStyle(NotificationStyle style) {
        switch (style) {
        case PRIMARY:
            return "Primary (blue)";
        case SECONDARY:
            return "Secondary (grey)";
        case SUCCESS:
            return "Success (green)";
        case DANGER:
            return "Danger (red)";
        case WARNING:
            return "Warning (yellow)";
        case INFO:
            return "Info (cyan)";
        case LIGHT:
            return "Light";
        case DARK:
            return "Dark";
        default:
            return "";
        }
    }

}
