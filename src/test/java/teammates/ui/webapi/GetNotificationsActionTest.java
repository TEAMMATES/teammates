package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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
        var adminAccount = given.account("admin", a -> a.admin());
        given.notification("active", n -> n.active().forGeneral());
        given.notification("expired", n -> n.expired().forGeneral());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, "false")
                .withParam(Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.name())
                .withCookie(getAuthCookie(adminAccount.id()));

        NotificationsData result = execute(request);

        List<NotificationData> notifications = result.getNotifications();
        assertEquals(2, notifications.size());
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
                .withCookie(getAuthCookie(account.id()));

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
                .withCookie(getAuthCookie(account.id()));

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getNotificationsAction_loggedInUserWithoutStudentRole_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, "true")
                .withParam(Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.name())
                .withCookie(getAuthCookie(account.id()));

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getNotificationsAction_noNotificationsExist_returnsEmptyList() {
        var account = given.account("account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, "true")
                .withParam(Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.name())
                .withCookie(getAuthCookie(account.id()));

        NotificationsData result = execute(request);

        assertTrue(result.getNotifications().isEmpty());
    }
}
