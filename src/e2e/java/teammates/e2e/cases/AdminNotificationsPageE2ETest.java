package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminNotificationsPage;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_NOTIFICATIONS_PAGE}.
 */
public class AdminNotificationsPageE2ETest extends BaseE2ETestCase {
    private NotificationAttributes[] notifications = new NotificationAttributes[2];

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminNotificationsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        notifications[0] = testData.notifications.get("notification1");
        notifications[1] = testData.notifications.get("notification2");
    }

    @Test
    @Override
    public void testAll() {

        ______TS("verify loaded data");

        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_NOTIFICATIONS_PAGE);
        AdminNotificationsPage notificationsPage = loginAdminToPage(url, AdminNotificationsPage.class);
        notificationsPage.verifyNotificationsTable(notifications);

        ______TS("add new notification");

        ______TS("edit notification");

        ______TS("delete notification");

    }

}
