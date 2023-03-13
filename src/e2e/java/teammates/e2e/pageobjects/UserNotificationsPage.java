package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.attributes.NotificationAttributes;

/**
 * Page Object Model for user notifications page.
 */
public class UserNotificationsPage extends AppPage {

    @FindBy(id = "notification-tabs")
    private WebElement notificationTabs;

    @FindBy(id = "notifications-timezone")
    private WebElement notificationsTimezone;

    public UserNotificationsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Notifications");
    }

    public void verifyNotShownNotifications(NotificationAttributes[] notifications) {
        List<String> shownNotificationIds = notificationTabs.findElements(By.className("card"))
                .stream().map(e -> e.getAttribute("id")).collect(Collectors.toList());
        for (NotificationAttributes notification : notifications) {
            assertFalse(shownNotificationIds.contains(notification.getNotificationId()));
        }
    }

    public void verifyShownNotifications(NotificationAttributes[] notifications, Set<String> readNotificationIds) {
        // Only validates that the preset notifications are present instead of checking every notification
        // This is because the page will display all active notifications in the database, which is not predictable
        for (NotificationAttributes notification : notifications) {
            verifyNotificationTab(notification, readNotificationIds);
        }
    }

    public void verifyNotificationTab(NotificationAttributes notification, Set<String> readNotificationIds) {
        boolean isRead = readNotificationIds.contains(notification.getNotificationId());
        WebElement notificationTab = notificationTabs.findElement(By.id(notification.getNotificationId()));

        // Check text and style of notification header
        WebElement cardHeader = notificationTab.findElement(By.className("card-header"));
        assertEquals(getHeaderText(notification), cardHeader.getText());
        assertTrue(cardHeader.getAttribute("class").contains(getHeaderClass(notification.getStyle())));

        // Checks if tab is open if notification is unread, and closed if notification is read
        String chevronClass = notificationTab.findElement(By.tagName("i")).getAttribute("class");
        if (isRead) {
            assertTrue(chevronClass.contains("fa-chevron-down"));
            // Open tab if notification is unread
            click(cardHeader);
            waitForPageToLoad();
        } else {
            assertTrue(chevronClass.contains("fa-chevron-up"));
        }

        // Check notification message
        WebElement notifMessage = notificationTab.findElement(By.className("notification-message"));
        assertEquals(notification.getMessage(), notifMessage.getAttribute("innerHTML"));

        List<WebElement> markAsReadBtnList = notificationTab.findElements(By.className("btn-mark-as-read"));

        if (isRead) {
            // Check that mark as read button cannot be found if notification is read
            assertEquals(0, markAsReadBtnList.size());

            // Close tab if notification is read
            click(cardHeader);
            waitForPageToLoad();
        } else {
            // Check style of mark as read button if notification is unread
            assertTrue(markAsReadBtnList.get(0).getAttribute("class").contains(getButtonClass(notification.getStyle())));
        }
    }

    public void markNotificationAsRead(NotificationAttributes notification) {
        WebElement notificationTab = notificationTabs.findElement(By.id(notification.getNotificationId()));
        click(notificationTab.findElement(By.className("btn-mark-as-read")));
        waitForPageToLoad(true);
    }

    private String getTimezone() {
        return notificationsTimezone.getText().replace("All dates are displayed in ", "").replace(" time.", "");
    }

    private String getHeaderText(NotificationAttributes notification) {
        return String.format("%s [%s - %s]", notification.getTitle(),
                getHeaderDateString(notification.getStartTime()), getHeaderDateString(notification.getEndTime()));
    }

    private String getHeaderDateString(Instant date) {
        return getDisplayedDateTime(date, getTimezone(), "dd MMM yyyy");
    }

    private String getHeaderClass(NotificationStyle style) {
        return String.format("alert alert-%s", style.toString().toLowerCase());
    }

    private String getButtonClass(NotificationStyle style) {
        return String.format("btn btn-%s", style.toString().toLowerCase());
    }

}
