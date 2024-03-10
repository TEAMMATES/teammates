package teammates.e2e.pageobjects;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.storage.sqlentity.Notification;

/**
 * Page Object Model for the admin notifications page.
 */
public class AdminNotificationsPage extends AppPage {

    @FindBy(id = "btn-add-notification")
    private WebElement addNotificationButton;

    @FindBy(id = "btn-create-notification")
    private WebElement createNotificationButton;

    @FindBy(id = "btn-edit-notification")
    private WebElement editNotificationButton;

    @FindBy(id = "notifications-timezone")
    private WebElement notificationsTimezone;

    @FindBy(id = "notification-target-user")
    private WebElement notificationTargetUserDropdown;

    @FindBy(id = "notification-style")
    private WebElement notificationStyleDropdown;

    @FindBy(id = "notification-title")
    private WebElement notificationTitleTextBox;

    @FindBy(id = "notification-message")
    private WebElement notificationMessageEditor;

    @FindBy(id = "notification-start-date")
    private WebElement startDateBox;

    @FindBy(id = "notification-start-time")
    private WebElement startTimeDropdown;

    @FindBy(id = "notification-end-date")
    private WebElement endDateBox;

    @FindBy(id = "notification-end-time")
    private WebElement endTimeDropdown;

    @FindBy(id = "notifications-table")
    private WebElement notificationsTable;

    public AdminNotificationsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Notifications");
    }

    public void verifyNotificationsTableRow(Notification notification) {
        WebElement notificationRow = notificationsTable.findElement(By.id(notification.getId().toString()));
        verifyTableRowValues(notificationRow, getNotificationTableDisplayDetails(notification));
    }

    public void addNotification(Notification notification) {
        clickAddNotificationButton();
        waitForElementPresence(By.id("btn-create-notification"));

        fillNotificationForm(notification);

        clickCreateNotificationButton();
        waitForPageToLoad(true);
    }

    public void editNotification(Notification notification) {
        WebElement notificationRow = notificationsTable.findElement(By.id(notification.getId().toString()));
        WebElement editButton = notificationRow.findElement(By.className("btn-light"));
        editButton.click();
        waitForElementPresence(By.id("btn-edit-notification"));

        fillNotificationForm(notification);

        clickEditNotificationButton();
        waitForPageToLoad(true);
    }

    public void deleteNotification(Notification notification) {
        WebElement notificationRow = notificationsTable.findElement(By.id(notification.getId().toString()));
        WebElement deleteButton = notificationRow.findElement(By.className("btn-danger"));

        deleteButton.click();
        waitForConfirmationModalAndClickOk();
        waitForPageToLoad(true);
    }

    public void fillNotificationForm(Notification notification) {
        selectDropdownOptionByText(notificationTargetUserDropdown, getTargetUserText(notification.getTargetUser()));
        selectDropdownOptionByText(notificationStyleDropdown, getNotificationStyle(notification.getStyle()));
        fillTextBox(notificationTitleTextBox, notification.getTitle());
        setMessage(notification.getMessage());
        setNotificationStartDateTime(notification.getStartTime());
        setNotificationEndDateTime(notification.getEndTime());
    }

    public String getFirstRowNotificationId() {
        List<WebElement> notificationRows =
                notificationsTable.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
        return notificationRows.get(0).getAttribute("id");
    }

    public void sortNotificationsTableByDescendingCreateTime() {
        WebElement creationTimeHeader = notificationsTable.findElements(By.tagName("th")).get(5);
        if (creationTimeHeader.findElements(By.className("fa-sort-down")).isEmpty()) {
            click(creationTimeHeader);
        }
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
        setDateTime(startDateBox, startTimeDropdown, startInstant);
    }

    private void setNotificationEndDateTime(Instant endInstant) {
        setDateTime(endDateBox, endTimeDropdown, endInstant);
    }

    private void setDateTime(WebElement dateBox, WebElement timeBox, Instant startInstant) {
        fillDatePicker(dateBox, startInstant, getTimezone());
        selectDropdownOptionByText(timeBox.findElement(By.tagName("select")), getInputTimeString(startInstant));
    }

    private String[] getNotificationTableDisplayDetails(Notification notification) {
        return new String[] {
            notification.getTitle(),
            getTableDisplayDateString(notification.getStartTime()),
            getTableDisplayDateString(notification.getEndTime()),
            notification.getTargetUser().toString(),
            getNotificationStyle(notification.getStyle()),
        };
    }

    private String getTimezone() {
        return notificationsTimezone.getText().replace("All dates are displayed in ", "").replace(" time.", "");
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
