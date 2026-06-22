package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.NotificationData;
import teammates.ui.output.NotificationsData;

/**
 * Tests for {@link GetNotificationsAction}.
 */
public class GetNotificationsActionTest extends BaseActionTest<GetNotificationsAction, NotificationsData> {

    @Test(groups = GroupNames.ACTION)
    public void getNotificationsAction_adminFetchesAll_returnsAllNotifications() {
        var activeNotification = given.notification("active", n -> n.active().forGeneral());
        var expiredNotification = given.notification("expired", n -> n.expired().forGeneral());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, "false")
                .withParam(Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.name())
                .withAdminAuth();

        NotificationsData result = execute(request);

        List<UUID> returnedIds = result.getNotifications().stream()
                .map(NotificationData::getNotificationId)
                .toList();
        assertEquals(2, returnedIds.size());
        assertTrue(returnedIds.contains(activeNotification.id()));
        assertTrue(returnedIds.contains(expiredNotification.id()));
    }

    @Test(groups = GroupNames.ACTION)
    public void getNotificationsAction_loggedInUserFetchesActiveGeneral_returnsActiveNotifications() {
        var account = given.account("account");
        given.notification("active", n -> n.active().forGeneral());
        given.notification("expired", n -> n.expired().forGeneral());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, "true")
                .withParam(Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.name())
                .withAccountAuth(account.id());

        NotificationsData result = execute(request);

        List<NotificationData> notifications = result.getNotifications();
        assertEquals(1, notifications.size());
    }

    @Test(groups = GroupNames.ACTION)
    public void getNotificationsAction_nonAdminFetchesNonActive_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, "false")
                .withParam(Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.name())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getNotificationsAction_regularUserRequestingStudentNotifications_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, "true")
                .withParam(Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.name())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getNotificationsAction_noNotificationsExist_returnsEmptyList() {
        var account = given.account("account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, "true")
                .withParam(Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.name())
                .withAccountAuth(account.id());

        NotificationsData result = execute(request);

        assertTrue(result.getNotifications().isEmpty());
    }
}
