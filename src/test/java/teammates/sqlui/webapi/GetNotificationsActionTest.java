package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
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

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATIONS;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    public void baseClassSetup() {
        loginAsAdmin();
    }

    @Test
    protected void testAccessControl_instructorAccessStudentNotification_shouldFail() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCannotAccess(requestParams);
    }

    @Test
    protected void testAccessControl_instructorAccessInstructorNotification_shouldSucceed() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCanAccess(requestParams);
    }

    @Test
    protected void testAccessControl_studentAccessInstructorNotification_shouldFail() {
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.getGoogleId());
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCannotAccess(requestParams);
    }

    @Test
    protected void testAccessControl_studentAccessStudentNotification_shouldSucceed() {
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(studentAttributes.getGoogleId());
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.STUDENT.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCanAccess(requestParams);
    }

    @Test
    protected void testAccessControl_unknownTargetUser_shouldFail() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, "unknown",
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyHttpParameterFailureAcl(requestParams);
    }

    @Test
    protected void testAccessControl_adminAccessAllNotification_shouldSucceed() {
        loginAsAdmin();
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, null,
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };
        verifyCanAccess(requestParams);
    }

    @Test
    protected void testExecute_withValidUserTypeForNonAdmin_shouldReturnData() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        NotificationAttributes notification = typicalBundle.notifications.get("notification5");
        Notification testNotification = new Notification(
                notification.getStartTime(),
                notification.getEndTime(),
                notification.getStyle(),
                notification.getTargetUser(),
                notification.getTitle(),
                notification.getMessage());

        when(mockLogic.getActiveNotificationsByTargetUser(notification.getTargetUser()))
                .thenReturn(List.of(testNotification));

        int expectedNumberOfNotifications =
                mockLogic.getActiveNotificationsByTargetUser(notification.getTargetUser()).size();

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
        loginAsAdmin();

        int expectedNumberOfNotifications = typicalBundle.notifications.size();
        NotificationAttributes notification = typicalBundle.notifications.get("notStartedNotification2");

        List<Notification> testNotifications = new ArrayList<>();
        typicalBundle.notifications.forEach((key, eachNotification) -> testNotifications.add(new Notification(
                eachNotification.getStartTime(),
                eachNotification.getEndTime(),
                eachNotification.getStyle(),
                eachNotification.getTargetUser(),
                eachNotification.getTitle(),
                eachNotification.getMessage())));

        when(mockLogic.getAllNotifications()).thenReturn(testNotifications);

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, null,
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };

        GetNotificationsAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notifications = output.getNotifications();

        assertEquals(expectedNumberOfNotifications, mockLogic.getAllNotifications().size());
        assertEquals(expectedNumberOfNotifications, notifications.size());

        NotificationData expected = new NotificationData(testNotifications.get(0));
        NotificationData firstNotification = notifications.get(0);
        verifyNotificationEquals(expected, firstNotification);

        // notification's shown attribute should not be updated
        List<Notification> notificationToCheck =
                mockLogic.getActiveNotificationsByTargetUser(notification.getTargetUser());
        notificationToCheck.forEach(n -> assertFalse(n.isShown()));
    }

    @Test
    public void testExecute_withoutUserTypeForNonAdmin_shouldFail() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };

        GetNotificationsAction action = getAction(requestParams);
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

    @Test
    public void testExecute_withFalseIsFetchingAll_shouldUpdateShownAndReturnUnreadNotifications() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        Set<String> readNotificationsId = typicalBundle.accounts.get("instructor1OfCourse1").getReadNotifications().keySet();

        List<Notification> testNotifications = new ArrayList<>();

        typicalBundle.notifications.forEach((key, eachNotification) -> {
            if (!readNotificationsId.contains(eachNotification.getNotificationId())) {
                testNotifications.add(new Notification(
                        eachNotification.getStartTime(),
                        eachNotification.getEndTime(),
                        eachNotification.getStyle(),
                        eachNotification.getTargetUser(),
                        eachNotification.getTitle(),
                        eachNotification.getMessage()));
            }
        });

        when(mockLogic.getAllNotifications()).thenReturn(testNotifications);

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
        List<Notification> notificationAttributes =
                mockLogic.getActiveNotificationsByTargetUser(NotificationTargetUser.INSTRUCTOR);
        notificationAttributes = notificationAttributes.stream()
                .filter(n -> !readNotificationsId.contains(n.getId().toString()))
                .collect(Collectors.toList());
        notificationAttributes.forEach(n -> assertTrue(n.isShown()));
    }

    @Test
    public void testExecute_withoutIsFetchingAll_shouldUpdateShownAndReturnUnreadNotifications() {
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
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

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
