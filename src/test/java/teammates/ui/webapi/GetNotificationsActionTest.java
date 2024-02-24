package teammates.ui.webapi;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.NotificationData;
import teammates.ui.output.NotificationsData;

/**
 * SUT: {@link GetNotificationsAction}.
 */
@Ignore
public class GetNotificationsActionTest extends BaseActionTest<GetNotificationsAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATIONS;
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

    @Test
    @Override
    protected void testAccessControl() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        ______TS("student notification not accessible to instructor");
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCannotAccess(requestParams);

        ______TS("accessible to instructor");
        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCanAccess(requestParams);

        ______TS("instructor notification not accessible to student");
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.getGoogleId());
        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCannotAccess(requestParams);

        ______TS("accessible to student");
        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCanAccess(requestParams);

        ______TS("unknown target user");
        loginAsInstructor(instructor.getGoogleId());
        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, "unknown",
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyHttpParameterFailureAcl(requestParams);

        ______TS("accessible to admin");
        loginAsAdmin();
        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, null,
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCanAccess(requestParams);
    }

    @Test
    public void testExecute_withValidUserTypeForNonAdmin_shouldReturnData() {
        ______TS("Request to fetch notification");
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        NotificationAttributes notification = typicalBundle.notifications.get("notification5");
        int expectedNumberOfNotifications =
                logic.getActiveNotificationsByTargetUser(notification.getTargetUser()).size();

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };

        GetNotificationsAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notifications = output.getNotifications();

        // should fetch correct number of notifications
        assertEquals(expectedNumberOfNotifications, notifications.size());
    }

    @Test
    public void testExecute_withoutUserTypeForAdmin_shouldReturnAllNotifications() {
        ______TS("Admin request to fetch notification");
        int expectedNumberOfNotifications = typicalBundle.notifications.size();
        loginAsAdmin();
        NotificationAttributes notification = typicalBundle.notifications.get("notStartedNotification2");

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, null,
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };

        GetNotificationsAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notifications = output.getNotifications();

        assertEquals(expectedNumberOfNotifications,
                logic.getAllNotifications().size());
        assertEquals(expectedNumberOfNotifications, notifications.size());

        NotificationData expected = new NotificationData(notification);
        NotificationData firstNotification = notifications.get(0);
        verifyNotificationEquals(expected, firstNotification);

        // notification's shown attribute should not be updated
        List<NotificationAttributes> notificationAttributes =
                logic.getActiveNotificationsByTargetUser(notification.getTargetUser());
        notificationAttributes.forEach(n -> assertFalse(n.isShown()));
    }

    @Test
    public void testExecute_withoutUserTypeForNonAdmin_shouldFail() {
        ______TS("Request without user type for non admin");
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        GetNotificationsAction action = getAction(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true));
        assertThrows(AssertionError.class, action::execute);
    }

    @Test
    public void testExecute_invalidUserType_shouldFail() {
        ______TS("Request without invalid user type");
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

    @Test
    public void testExecute_withFalseIsFetchingAll_shouldUpdateShownAndReturnUnreadNotifications() {
        ______TS("Request to fetch unread notification only");

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        Set<String> readNotificationsId = typicalBundle.accounts.get("instructor1OfCourse1").getReadNotifications().keySet();

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(false),
        };

        GetNotificationsAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notifications = output.getNotifications();
        verifyDoesNotContainNotifications(notifications, readNotificationsId);

        // should update notification has shown attribute
        List<NotificationAttributes> notificationAttributes =
                logic.getActiveNotificationsByTargetUser(NotificationTargetUser.INSTRUCTOR);
        notificationAttributes = notificationAttributes.stream()
                .filter(n -> !readNotificationsId.contains(n.getNotificationId()))
                .collect(Collectors.toList());
        notificationAttributes.forEach(n -> assertTrue(n.isShown()));
    }

    @Test
    public void testExecute_withoutIsFetchingAll_shouldUpdateShownAndReturnUnreadNotifications() {
        ______TS("Request without isfetchingall is equivalent to a false isfetchingall");

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        Set<String> readNotificationsId = typicalBundle.accounts.get("instructor1OfCourse1").getReadNotifications().keySet();

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
        };

        GetNotificationsAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notifications = output.getNotifications();
        verifyDoesNotContainNotifications(notifications, readNotificationsId);
    }

    @Test
    public void testExecute_withInvalidIsFetchingAll_shouldFail() {
        ______TS("Request with invalid isfetchingall");

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, "random-value",
        };

        GetNotificationsAction action = getAction(requestParams);
        assertThrows(InvalidHttpParameterException.class, action::execute);
    }

    private void verifyNotificationEquals(NotificationData expected, NotificationData actual) {
        assertEquals(expected.getNotificationId(), actual.getNotificationId());
        assertEquals(expected.getStyle(), actual.getStyle());
        assertEquals(expected.getTargetUser(), actual.getTargetUser());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getMessage(), actual.getMessage());
        assertEquals(expected.getStartTimestamp(), actual.getStartTimestamp());
        assertEquals(expected.getEndTimestamp(), actual.getEndTimestamp());
    }

    private void verifyDoesNotContainNotifications(List<NotificationData> notifications, Set<String> readIds) {
        for (NotificationData n : notifications) {
            assertFalse(readIds.contains(n.getNotificationId()));
        }
    }
}
