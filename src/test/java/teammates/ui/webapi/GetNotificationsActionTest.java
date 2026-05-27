package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.Const;
import teammates.storage.entity.Notification;
import teammates.ui.output.NotificationData;
import teammates.ui.output.NotificationsData;

/**
 * SUT: {@link GetNotificationsAction}.
 */
public class GetNotificationsActionTest extends BaseActionTest<GetNotificationsAction> {
    private static final String GOOGLE_ID = "google-id";
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

    @BeforeMethod
    public void setUpMethod() {
        when(mockLogic.getAccountForGoogleId(GOOGLE_ID)).thenReturn(getTypicalAccount());
    }

    @Test
    void testAccessControl_instructorAccessStudentNotification_shouldFail() {
        loginAsInstructor(GOOGLE_ID);
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.toString(),
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(true),
        };
        verifyCannotAccess(requestParams);
    }

    @Test(enabled = false)
    void testAccessControl_instructorAccessInstructorNotification_shouldSucceed() {
        loginAsInstructor(GOOGLE_ID);
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(true),
        };
        verifyCanAccess(requestParams);
    }

    @Test
    void testAccessControl_studentAccessInstructorNotification_shouldFail() {
        loginAsStudent(GOOGLE_ID);
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(true),
        };
        verifyCannotAccess(requestParams);
    }

    @Test(enabled = false)
    void testAccessControl_studentAccessStudentNotification_shouldSucceed() {
        loginAsStudent(GOOGLE_ID);
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.toString(),
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(true),
        };

        verifyCanAccess(requestParams);
    }

    @Test
    void testAccessControl_unknownTargetUser_shouldFail() {
        loginAsInstructor(GOOGLE_ID);
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, "unknown",
        };
        verifyHttpParameterFailureAcl(requestParams);
    }

    @Test
    void testAccessControl_adminAccessAllNotification_shouldSucceed() {
        loginAsAdmin();
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.toString(),
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(false),
        };
        verifyCanAccess(requestParams);
    }

    @Test
    void testExecute_withValidTargetUsersForNonAdmin_shouldReturnData() {
        List<Notification> testNotifications = new ArrayList<>();

        loginAsInstructor(GOOGLE_ID);

        int expectedNumberOfNotifications = UNREAD_NOTIFICATION_COUNT + READ_NOTIFICATION_COUNT;

        for (int i = 0; i < expectedNumberOfNotifications; i++) {
            testNotifications.add(getTypicalNotificationWithId());
        }

        List<NotificationTargetUser> targetUsers = List.of(
                NotificationTargetUser.INSTRUCTOR, NotificationTargetUser.GENERAL);
        when(mockLogic.getActiveNotificationsByTargetUsers(targetUsers))
                .thenReturn(testNotifications);

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(true),
        };

        GetNotificationsAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notifications = output.getNotifications();

        // should fetch correct number of notifications
        assertEquals(expectedNumberOfNotifications, notifications.size());
        verify(mockLogic).getActiveNotificationsByTargetUsers(targetUsers);
    }

    @Test
    public void testExecute_withAllUserTypesForAdmin_shouldReturnAllNotifications() {
        final int expectedNumberOfNotifications = 5;

        loginAsAdmin();

        List<Notification> testNotifications = new ArrayList<>();
        for (int i = 0; i < expectedNumberOfNotifications; i++) {
            testNotifications.add(getTypicalNotificationWithId());
        }

        List<NotificationTargetUser> targetUsers = List.of(
                NotificationTargetUser.STUDENT, NotificationTargetUser.INSTRUCTOR, NotificationTargetUser.GENERAL);
        when(mockLogic.getNotificationsByTargetUsers(targetUsers)).thenReturn(testNotifications);

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.toString(),
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(false),
        };

        GetNotificationsAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notificationOutput = output.getNotifications();

        assertEquals(expectedNumberOfNotifications, notificationOutput.size());
        verify(mockLogic).getNotificationsByTargetUsers(targetUsers);

        NotificationData expected = new NotificationData(testNotifications.get(0));
        NotificationData firstNotification = notificationOutput.get(0);
        verifyNotificationEquals(expected, firstNotification);
    }

    @Test
    public void testExecute_withoutUserTypeForNonAdmin_shouldFail() {
        loginAsInstructor(GOOGLE_ID);

        verifyHttpParameterFailure(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(false));
    }

    @Test
    public void testExecute_withoutUserTypeForAdmin_shouldFail() {
        loginAsAdmin();

        verifyHttpParameterFailure(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(false));
    }

    @Test
    public void testExecute_withoutIsFetchingActive_shouldFail() {
        loginAsInstructor(GOOGLE_ID);

        verifyHttpParameterFailure(
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString());
    }

    @Test
    public void testExecute_invalidUserType_shouldFail() {
        loginAsInstructor(GOOGLE_ID);

        // when usertype is a random string
        verifyHttpParameterFailure(
                Const.ParamsNames.NOTIFICATION_TARGET_USER, "invalid string",
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(true));
    }

    @Test
    public void testExecute_withInvalidIsFetchingActive_shouldFail() {
        loginAsInstructor(GOOGLE_ID);

        verifyHttpParameterFailure(
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, "not-a-boolean");
    }

    @Test
    public void testExecute_withSingleTargetUser_shouldNotImplicitlyAddGeneralTargetUser() {
        loginAsInstructor(GOOGLE_ID);

        List<Notification> testNotifications = new ArrayList<>();

        for (int i = 0; i < UNREAD_NOTIFICATION_COUNT; i++) {
            testNotifications.add(getTypicalNotificationWithId());
        }

        List<NotificationTargetUser> targetUsers = List.of(NotificationTargetUser.INSTRUCTOR);
        when(mockLogic.getActiveNotificationsByTargetUsers(targetUsers))
                .thenReturn(testNotifications);

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(true),
        };

        GetNotificationsAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notifications = output.getNotifications();

        assertEquals(testNotifications.size(), notifications.size());
        for (int i = 0; i < testNotifications.size(); i++) {
            verifyNotificationEquals(new NotificationData(testNotifications.get(i)), notifications.get(i));
        }
        verify(mockLogic).getActiveNotificationsByTargetUsers(targetUsers);
    }

    @Test
    public void testExecute_withIsFetchingActiveFalseAndTargetUsers_shouldReturnAllNotificationsForTargetUsers() {
        loginAsInstructor(GOOGLE_ID);

        List<Notification> testNotifications = new ArrayList<>();

        for (int i = 0; i < UNREAD_NOTIFICATION_COUNT; i++) {
            testNotifications.add(getTypicalNotificationWithId());
        }

        List<NotificationTargetUser> targetUsers = List.of(NotificationTargetUser.INSTRUCTOR);
        when(mockLogic.getNotificationsByTargetUsers(targetUsers))
                .thenReturn(testNotifications);

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(false),
        };

        GetNotificationsAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notifications = output.getNotifications();

        assertEquals(testNotifications.size(), notifications.size());
        for (int i = 0; i < testNotifications.size(); i++) {
            verifyNotificationEquals(new NotificationData(testNotifications.get(i)), notifications.get(i));
        }
        verify(mockLogic).getNotificationsByTargetUsers(targetUsers);
    }

    @Test
    public void testExecute_withGeneralTargetUser_shouldReturnGeneralNotifications() {
        List<Notification> testNotifications = new ArrayList<>();

        loginAsInstructor(GOOGLE_ID);

        for (int i = 0; i < READ_NOTIFICATION_COUNT; i++) {
            testNotifications.add(getTypicalNotificationWithId());
        }

        List<NotificationTargetUser> targetUsers = List.of(NotificationTargetUser.GENERAL);
        when(mockLogic.getActiveNotificationsByTargetUsers(targetUsers))
                .thenReturn(testNotifications);

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.GENERAL.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE, String.valueOf(true),
        };

        GetNotificationsAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notifications = output.getNotifications();

        assertEquals(testNotifications.size(), notifications.size());
        for (int i = 0; i < testNotifications.size(); i++) {
            verifyNotificationEquals(new NotificationData(testNotifications.get(i)), notifications.get(i));
        }
        verify(mockLogic).getActiveNotificationsByTargetUsers(targetUsers);
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
}
