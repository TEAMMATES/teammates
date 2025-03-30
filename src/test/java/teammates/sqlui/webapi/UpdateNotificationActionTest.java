package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.NotificationData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.NotificationUpdateRequest;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.UpdateNotificationAction;

/**
 * SUT: {@link UpdateNotificationAction}.
 */
public class UpdateNotificationActionTest extends BaseActionTest<UpdateNotificationAction> {
    private Notification testNotification;
    private Notification newNotification;
    private NotificationUpdateRequest notificationRequest;
    private String[] requestParams;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION;
    }

    @Override
    String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        notificationRequest = getTypicalUpdateRequest();

        testNotification = getTypicalNotificationWithId();
        newNotification = new Notification(
                Instant.ofEpochMilli(notificationRequest.getStartTimestamp()),
                Instant.ofEpochMilli(notificationRequest.getEndTimestamp()),
                notificationRequest.getStyle(),
                notificationRequest.getTargetUser(),
                notificationRequest.getTitle(),
                notificationRequest.getMessage());
        newNotification.setId(testNotification.getId());

        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, testNotification.getId().toString(),
        };

        loginAsAdmin();
    }

    @Test
    void testAccessControl_admin_canAccess() {
        verifyCanAccess();
    }

    @Test
    void testAccessControl_maintainers_cannotAccess() {
        logoutUser();
        loginAsMaintainer();
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_instructor_cannotAccess() {
        logoutUser();
        loginAsInstructor(Const.ParamsNames.INSTRUCTOR_ID);
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_student_cannotAccess() {
        logoutUser();
        loginAsStudent(Const.ParamsNames.STUDENT_ID);
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }

    @Test
    void testAccessControl_unregistered_cannotAccess() {
        logoutUser();
        loginAsUnregistered(Const.ParamsNames.USER_ID);
        verifyCannotAccess();
    }

    @Test
    protected void testExecute_typicalCase_shouldSucceed() throws Exception {
        NotificationStyle style = notificationRequest.getStyle();
        NotificationTargetUser targetUser = notificationRequest.getTargetUser();
        String title = notificationRequest.getTitle();
        String message = notificationRequest.getMessage();

        newNotification.setId(testNotification.getId());
        when(mockLogic.getNotification(testNotification.getId())).thenReturn(testNotification);
        when(mockLogic.updateNotification(
                testNotification.getId(),
                newNotification.getStartTime(),
                newNotification.getEndTime(),
                newNotification.getStyle(),
                newNotification.getTargetUser(),
                newNotification.getTitle(),
                newNotification.getMessage()
        )).thenReturn(newNotification);

        UpdateNotificationAction action = getAction(notificationRequest, requestParams);
        NotificationData res = (NotificationData) action.execute().getOutput();

        when(mockLogic.getNotification(testNotification.getId())).thenReturn(newNotification);

        Notification updatedNotification = mockLogic.getNotification(UUID.fromString(res.getNotificationId()));

        // Verify that correctly updated in the DB
        assertEquals(notificationRequest.getStartTimestamp(), updatedNotification.getStartTime().toEpochMilli());
        assertEquals(notificationRequest.getEndTimestamp(), updatedNotification.getEndTime().toEpochMilli());
        assertEquals(style, updatedNotification.getStyle());
        assertEquals(targetUser, updatedNotification.getTargetUser());
        assertEquals(title, updatedNotification.getTitle());
        assertEquals(message, updatedNotification.getMessage());
    }

    @Test
    protected void testExecute_nullStyle_shouldFail() {
        notificationRequest.setStyle(null);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(notificationRequest, requestParams);
        assertEquals("Notification style cannot be null", ex.getMessage());
    }

    @Test
    protected void testExecute_nullTargetUser_shouldFail() {
        notificationRequest.setTargetUser(null);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(notificationRequest, requestParams);
        assertEquals("Notification target user cannot be null", ex.getMessage());
    }

    @Test
    protected void testExecute_nullTitle_shouldFail() {
        notificationRequest.setTitle(null);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(notificationRequest, requestParams);
        assertEquals("Notification title cannot be null", ex.getMessage());
    }

    @Test
    protected void testExecute_nullMessage_shouldFail() {
        notificationRequest.setMessage(null);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(notificationRequest, requestParams);
        assertEquals("Notification message cannot be null", ex.getMessage());
    }

    @Test
    protected void testExecute_invalidNotificationId_shouldFail() {
        String invalidNotificationId = "InvalidNotificationId";
        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, invalidNotificationId,
        };
        InvalidHttpParameterException ex = verifyHttpParameterFailure(notificationRequest, requestParams);
        assertEquals("Expected UUID value for notificationid parameter, but found: ["
                + invalidNotificationId
                + "]", ex.getMessage());
    }

    @Test
    protected void testExecute_timestampStartLessThanZero_shouldFail() {
        notificationRequest.setStartTimestamp(-1);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(notificationRequest, requestParams);
        assertEquals("Start timestamp should be greater than zero", ex.getMessage());
    }

    @Test
    protected void testExecute_timestampEndLessThanZero_shouldFail() {
        notificationRequest.setEndTimestamp(-1);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(notificationRequest, requestParams);
        assertEquals("End timestamp should be greater than zero", ex.getMessage());
    }

    @Test
    protected void testExecute_timestampStartAfterEnd_shouldFail() throws Exception {
        notificationRequest = getTypicalUpdateRequest();
        notificationRequest.setEndTimestamp(notificationRequest.getStartTimestamp() - 100);

        newNotification = new Notification(
                Instant.ofEpochMilli(notificationRequest.getStartTimestamp()),
                Instant.ofEpochMilli(notificationRequest.getEndTimestamp()),
                notificationRequest.getStyle(),
                notificationRequest.getTargetUser(),
                notificationRequest.getTitle(),
                notificationRequest.getMessage());
        newNotification.setId(testNotification.getId());

        when(mockLogic.getNotification(testNotification.getId())).thenReturn(testNotification);
        when(mockLogic.updateNotification(
                testNotification.getId(),
                newNotification.getStartTime(),
                newNotification.getEndTime(),
                newNotification.getStyle(),
                newNotification.getTargetUser(),
                newNotification.getTitle(),
                newNotification.getMessage()
        )).thenThrow(new InvalidParametersException("The time when the notification will expire for this notification "
                + "cannot be earlier than the time when the notification will be visible."));

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(notificationRequest, requestParams);
        assertEquals("The time when the notification will expire for this notification "
                + "cannot be earlier than the time when the notification will be visible.",
                ex.getMessage());
    }

    @Test
    protected void testExecute_invalidParameter_shouldFail() {
        notificationRequest.setTitle(" ");
        verifyHttpParameterFailure(notificationRequest);
    }

    @Test
    protected void testExecute_notEnoughRequestParameters_shouldFail() {
        verifyHttpParameterFailure(notificationRequest);
    }

    private NotificationUpdateRequest getTypicalUpdateRequest() {
        NotificationUpdateRequest req = new NotificationUpdateRequest();

        req.setStartTimestamp(Instant.now().toEpochMilli());
        req.setEndTimestamp(Instant.now().plus(5, ChronoUnit.DAYS).toEpochMilli());
        req.setStyle(NotificationStyle.INFO);
        req.setTargetUser(NotificationTargetUser.GENERAL);
        req.setTitle("New notification title");
        req.setMessage("New notification message");

        return req;
    }

}
