package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.NotificationData;
import teammates.ui.output.NotificationsData;
import teammates.ui.webapi.GetNotificationsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetNotificationsAction}.
 */
public class GetNotificationsActionTest extends BaseActionTest<GetNotificationsAction> {
    private static final String GOOGLE_ID = "user-googleId";
    private static final int READ_NOTIFICATION_COUNT = 5;
    private static final int UNREAD_NOTIFICATION_COUNT = 10;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATIONS;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    void testAccessControl_instructorAccessStudentNotification_shouldFail() {
        loginAsInstructor(GOOGLE_ID);
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCannotAccess(requestParams);
    }

    @Test
    void testAccessControl_instructorAccessInstructorNotification_shouldSucceed() {
        loginAsInstructor(GOOGLE_ID);
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCanAccess(requestParams);
    }

    @Test
    void testAccessControl_studentAccessInstructorNotification_shouldFail() {
        loginAsStudent(GOOGLE_ID);
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCannotAccess(requestParams);
    }

    @Test
    void testAccessControl_studentAccessStudentNotification_shouldSucceed() {
        loginAsStudent(GOOGLE_ID);
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCanAccess(requestParams);
    }

    @Test
    void testAccessControl_unknownTargetUser_shouldFail() {
        loginAsInstructor(GOOGLE_ID);
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, "unknown",
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyHttpParameterFailureAcl(requestParams);
    }

    @Test
    void testAccessControl_adminAccessAllNotification_shouldSucceed() {
        loginAsAdmin();
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, null,
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCanAccess(requestParams);
    }

    @Test
    void testExecute_withValidUserTypeForNonAdmin_shouldReturnData() {
        List<Notification> testNotifications = new ArrayList<>();

        loginAsInstructor(GOOGLE_ID);

        int expectedNumberOfNotifications = UNREAD_NOTIFICATION_COUNT + READ_NOTIFICATION_COUNT;

        for (int i = 0; i < expectedNumberOfNotifications; i++) {
            testNotifications.add(getTypicalNotificationWithId());
        }

        when(mockLogic.getActiveNotificationsByTargetUser(NotificationTargetUser.INSTRUCTOR))
                .thenReturn(testNotifications);

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
        final int expectedNumberOfNotifications = 5;
        Notification testNotification = getTypicalNotificationWithId();

        loginAsAdmin();

        List<Notification> testNotifications = new ArrayList<>();
        for (int i = 0; i < expectedNumberOfNotifications; i++) {
            testNotifications.add(getTypicalNotificationWithId());
        }

        when(mockLogic.getAllNotifications()).thenReturn(testNotifications);
        when(mockLogic.getActiveNotificationsByTargetUser(testNotification.getTargetUser()))
                .thenReturn(testNotifications);

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, null,
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };

        GetNotificationsAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notificationOutput = output.getNotifications();

        assertEquals(expectedNumberOfNotifications, mockLogic.getAllNotifications().size());
        assertEquals(expectedNumberOfNotifications, notificationOutput.size());

        NotificationData expected = new NotificationData(testNotifications.get(0));
        NotificationData firstNotification = notificationOutput.get(0);
        verifyNotificationEquals(expected, firstNotification);

        // notification's shown attribute should not be updated
        List<Notification> notificationToCheck =
                mockLogic.getActiveNotificationsByTargetUser(testNotification.getTargetUser());
        notificationToCheck.forEach(n -> assertFalse(n.isShown()));
    }

    @Test
    public void testExecute_withoutUserTypeForNonAdmin_shouldFail() {
        loginAsInstructor(GOOGLE_ID);

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };

        GetNotificationsAction action = getAction(requestParams);
        assertThrows(AssertionError.class, action::execute);
    }

    @Test
    public void testExecute_invalidUserType_shouldFail() {
        loginAsInstructor(GOOGLE_ID);

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
        loginAsInstructor(GOOGLE_ID);

        List<Notification> testAllNotifications = new ArrayList<>();
        List<Notification> testUnreadNotifications = new ArrayList<>();
        Set<String> readNotificationsId;

        for (int i = 0; i < UNREAD_NOTIFICATION_COUNT; i++) {
            testUnreadNotifications.add(getTypicalNotificationWithId());
        }

        for (int i = 0; i < READ_NOTIFICATION_COUNT; i++) {
            Notification readNotification = getTypicalNotificationWithId();
            readNotification.setShown();
            testAllNotifications.add(readNotification);
        }

        readNotificationsId = testAllNotifications.stream()
                .map(n -> n.getId().toString())
                .collect(Collectors.toSet());

        testAllNotifications.addAll(testUnreadNotifications);

        when(mockLogic.getAllNotifications()).thenReturn(testAllNotifications);
        when(mockLogic.getActiveNotificationsByTargetUser(NotificationTargetUser.INSTRUCTOR))
                .thenReturn(testUnreadNotifications);

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
        List<Notification> activeNotifications =
                mockLogic.getActiveNotificationsByTargetUser(NotificationTargetUser.INSTRUCTOR);
        activeNotifications = activeNotifications.stream()
                .filter(n -> !readNotificationsId.contains(n.getId().toString()))
                .collect(Collectors.toList());
        activeNotifications.forEach(n -> assertTrue(n.isShown()));
    }

    @Test
    public void testExecute_withoutIsFetchingAll_shouldUpdateShownAndReturnUnreadNotifications() {
        List<Notification> testAllNotifications = new ArrayList<>();
        List<Notification> testUnreadNotifications = new ArrayList<>();
        Set<String> readNotificationsId;

        loginAsInstructor(GOOGLE_ID);

        for (int i = 0; i < READ_NOTIFICATION_COUNT; i++) {
            Notification readNotification = getTypicalNotificationWithId();
            readNotification.setShown();
            testAllNotifications.add(readNotification);
        }

        readNotificationsId = testAllNotifications.stream()
                .map(n -> n.getId().toString())
                .collect(Collectors.toSet());

        when(mockLogic.getActiveNotificationsByTargetUser(NotificationTargetUser.INSTRUCTOR))
                .thenReturn(testUnreadNotifications);

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
        loginAsInstructor(GOOGLE_ID);

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, "random-value",
        };

        verifyHttpParameterFailure(requestParams);
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
