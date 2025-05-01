package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

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
import teammates.ui.request.NotificationCreateRequest;
import teammates.ui.webapi.CreateNotificationAction;

/**
 * SUT: {@link CreateNotificationAction}.
 */
public class CreateNotificationActionTest extends BaseActionTest<CreateNotificationAction> {
    private static final String INVALID_TITLE = "";
    NotificationCreateRequest testReq;
    private Notification testNotification;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        loginAsAdmin();
        testNotification = getTypicalNotificationWithId();
    }

    @Test
    void testExecute_addNotification_success() throws Exception {
        long startTime = testNotification.getStartTime().toEpochMilli();
        long endTime = testNotification.getEndTime().toEpochMilli();
        NotificationStyle style = testNotification.getStyle();
        NotificationTargetUser targetUser = testNotification.getTargetUser();
        String title = testNotification.getTitle();
        String message = testNotification.getMessage();

        when(mockLogic.createNotification(isA(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationCreateRequest req = getTypicalCreateRequest();
        CreateNotificationAction action = getAction(req);
        NotificationData res = (NotificationData) action.execute().getOutput();

        when(mockLogic.getNotification(UUID.fromString(res.getNotificationId()))).thenReturn(testNotification);

        Notification createdNotification = mockLogic.getNotification(UUID.fromString(res.getNotificationId()));

        // check that notification returned has same properties as notification created
        assertEquals(createdNotification.getStartTime().toEpochMilli(), res.getStartTimestamp());
        assertEquals(createdNotification.getEndTime().toEpochMilli(), res.getEndTimestamp());
        assertEquals(createdNotification.getStyle(), res.getStyle());
        assertEquals(createdNotification.getTargetUser(), res.getTargetUser());
        assertEquals(createdNotification.getTitle(), res.getTitle());
        assertEquals(createdNotification.getMessage(), res.getMessage());

        // check DB correctly processed request
        assertEquals(startTime, createdNotification.getStartTime().toEpochMilli());
        assertEquals(endTime, createdNotification.getEndTime().toEpochMilli());
        assertEquals(style, createdNotification.getStyle());
        assertEquals(targetUser, createdNotification.getTargetUser());
        assertEquals(title, createdNotification.getTitle());
        assertEquals(message, createdNotification.getMessage());
    }

    @Test
    void testAccessControl() throws Exception {
        verifyOnlyAdminsCanAccess();
    }

    @Test
    void testExecute_invalidStyle_throwsInvalidHttpParameterException() {
        testReq = getTypicalCreateRequest();
        testReq.setStyle(null);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);

        assertEquals("Notification style cannot be null", ex.getMessage());
    }

    @Test
    void testExecute_invalidTargetUser_throwsInvalidHttpParameterException() {
        testReq = getTypicalCreateRequest();
        testReq.setTargetUser(null);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);

        assertEquals("Notification target user cannot be null", ex.getMessage());
    }

    @Test
    void testExecute_invalidTitle_throwsInvalidHttpParameterException() {
        testReq = getTypicalCreateRequest();
        testReq.setTitle(null);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);

        assertEquals("Notification title cannot be null", ex.getMessage());
    }

    @Test
    void testExecute_invalidMessage_throwsInvalidHttpParameterException() {
        testReq = getTypicalCreateRequest();
        testReq.setMessage(null);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);

        assertEquals("Notification message cannot be null", ex.getMessage());
    }

    @Test
    void testExecute_negativeStartTimestamp_throwsInvalidHttpParameterException() {
        testReq = getTypicalCreateRequest();
        testReq.setStartTimestamp(-1);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);

        assertEquals("Start timestamp should be greater than zero", ex.getMessage());
    }

    @Test
    void testExecute_negativeEndTimestamp_throwsInvalidHttpParameterException() {
        testReq = getTypicalCreateRequest();
        testReq.setEndTimestamp(-1);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);

        assertEquals("End timestamp should be greater than zero", ex.getMessage());
    }

    @Test
    void testExecute_invalidParameter_throwsInvalidHttpParameterException() throws Exception {
        testReq = getTypicalCreateRequest();
        testReq.setTitle(INVALID_TITLE);

        when(mockLogic.createNotification(any())).thenThrow(new InvalidParametersException("Invalid title"));
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);

        assertEquals("Notification title cannot be empty", ex.getMessage());
    }

    private NotificationCreateRequest getTypicalCreateRequest() {
        NotificationCreateRequest req = new NotificationCreateRequest();

        req.setStartTimestamp(testNotification.getStartTime().toEpochMilli());
        req.setEndTimestamp(testNotification.getEndTime().toEpochMilli());
        req.setStyle(testNotification.getStyle());
        req.setTargetUser(testNotification.getTargetUser());
        req.setTitle(testNotification.getTitle());
        req.setMessage(testNotification.getMessage());

        return req;
    }

}
