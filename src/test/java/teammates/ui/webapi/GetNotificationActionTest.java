package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.NotificationType;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.ui.output.NotificationData;
import teammates.ui.output.NotificationsData;

/**
 * SUT: {@link GetNotificationAction}.
 */
public class GetNotificationActionTest extends BaseActionTest<GetNotificationAction> {

    // TODO: add tests for isfetchingall

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() {
        // See independent test cases
    }

    @Override
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }

    @Test
    public void testExecute_withFullDetailUserTypeForNonAdmin_shouldReturnDataWithFullDetail() {
        int expectedNumberOfNotifications = 4;
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        NotificationAttributes notification = typicalBundle.notifications.get("notification-5");

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };

        GetNotificationAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notifications = output.getNotifications();

        assertEquals(expectedNumberOfNotifications,
                logic.getActiveNotificationsByTargetUser(notification.getTargetUser()).size());
        assertEquals(expectedNumberOfNotifications, notifications.size());

        NotificationData firstNotification = notifications.get(0);
        assertEquals("notification-5", firstNotification.getNotificationId());
        assertEquals(NotificationType.TIPS, firstNotification.getNotificationType());
        assertEquals(NotificationTargetUser.INSTRUCTOR, firstNotification.getTargetUser());
        assertEquals("The first tip to instructor", firstNotification.getTitle());
        assertEquals("The first tip content", firstNotification.getMessage());
    }

    @Test
    public void testExecute_withFullDetailUserTypeForAdmin_shouldReturnDataWithFullDetail() {
        int expectedNumberOfNotifications = 4;
        loginAsAdmin();
        NotificationAttributes notification = typicalBundle.notifications.get("notification-6");

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };

        GetNotificationAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notifications = output.getNotifications();

        assertEquals(expectedNumberOfNotifications,
                logic.getActiveNotificationsByTargetUser(notification.getTargetUser()).size());
        assertEquals(expectedNumberOfNotifications, notifications.size());

        NotificationData firstNotification = notifications.get(0);
        assertEquals("notification-6", firstNotification.getNotificationId());
        assertEquals(NotificationType.DEPRECATION, firstNotification.getNotificationType());
        assertEquals(NotificationTargetUser.STUDENT, firstNotification.getTargetUser());
        assertEquals("The note of maintenance", firstNotification.getTitle());
        assertEquals("The content of maintenance", firstNotification.getMessage());
    }

    @Test
    public void testExecute_withoutUserTypeForNonAdmin_shouldFail() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        GetNotificationAction action = getAction(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true));
        assertThrows(AssertionError.class, action::execute);
    }

    @Test
    public void testExecute_invalidUserType_shouldFail() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        // when usertype is GENERAL
        verifyHttpParameterFailure(Const.ParamsNames.NOTIFICATION_TARGET_USER,
                NotificationTargetUser.GENERAL.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL,
                String.valueOf(true));

        // when usertype is a random string
        verifyHttpParameterFailure(Const.ParamsNames.NOTIFICATION_TARGET_USER,
                "invalid string",
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL,
                String.valueOf(true));
    }
}
