package teammates.sqlui.webapi;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
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
    private static final String GOOGLE_ID = "user-googleId";
    private static final String TEST_NOTIFICATION = "notification1";
    NotificationCreateRequest testReq;
    private final NotificationAttributes testNotificationAttribute = typicalBundle.notifications.get(TEST_NOTIFICATION);

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
    }

    @Test(enabled = false)
    void testExecute_addNotification_success() throws Exception {
        long startTime = testNotificationAttribute.getStartTime().toEpochMilli();
        long endTime = testNotificationAttribute.getEndTime().toEpochMilli();
        NotificationStyle style = testNotificationAttribute.getStyle();
        NotificationTargetUser targetUser = testNotificationAttribute.getTargetUser();
        String title = testNotificationAttribute.getTitle();
        String message = testNotificationAttribute.getMessage();

        NotificationCreateRequest req = getTypicalCreateRequest();
        CreateNotificationAction action = getAction(req);
        NotificationData res = (NotificationData) action.execute().getOutput();

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
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();
        verifyCanAccess();
    }

    @Test
    void testSpecificAccessControl_instructor_cannotAccess() {
        loginAsInstructor(GOOGLE_ID);
        verifyCannotAccess();
    }

    @Test
    void testSpecificAccessControl_student_cannotAccess() {
        loginAsStudent(GOOGLE_ID);
        verifyCannotAccess();
    }

    @Test
    void testSpecificAccessControl_loggedOut_cannotAccess() {
        logoutUser();
        verifyCannotAccess();
    }

    @Test
    void testExecute_invalidStyle_throwsInvalidHttpParameterException() throws Exception {
        testReq = getTypicalCreateRequest();
        testReq.setStyle(null);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);
        assertEquals("Notification style cannot be null", ex.getMessage());
    }

    @Test
    void testExecute_invalidTargetUser_throwsInvalidHttpParameterException() throws Exception {
        testReq = getTypicalCreateRequest();
        testReq.setTargetUser(null);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);
        assertEquals("Notification target user cannot be null", ex.getMessage());
    }

    @Test
    void testExecute_invalidTitle_throwsInvalidHttpParameterException() throws Exception {
        testReq = getTypicalCreateRequest();
        testReq.setTitle(null);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);
        assertEquals("Notification title cannot be null", ex.getMessage());
    }

    @Test
    void testExecute_invalidMessage_throwsInvalidHttpParameterException() throws Exception {
        testReq = getTypicalCreateRequest();
        testReq.setMessage(null);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);
        assertEquals("Notification message cannot be null", ex.getMessage());
    }

    @Test
    void testExecute_negativeStartTimestamp_throwsInvalidHttpParameterException() throws Exception {
        testReq = getTypicalCreateRequest();
        testReq.setStartTimestamp(-1);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);
        assertEquals("Start timestamp should be greater than zero", ex.getMessage());
    }

    @Test
    void testExecute_negativeEndTimestamp_throwsInvalidHttpParameterException() throws Exception {
        testReq = getTypicalCreateRequest();
        testReq.setEndTimestamp(-1);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(testReq);
        assertEquals("End timestamp should be greater than zero", ex.getMessage());
    }

    @Test(enabled = false)
    void testExecute_invalidParameter_throwsInvalidHttpParameterException() throws Exception {
        testReq = getTypicalCreateRequest();
        String invalidTitle = "";
        testReq.setTitle(invalidTitle);
        verifyHttpRequestBodyFailure(testReq);
    }

    private NotificationCreateRequest getTypicalCreateRequest() {
        NotificationCreateRequest req = new NotificationCreateRequest();

        req.setStartTimestamp(testNotificationAttribute.getStartTime().toEpochMilli());
        req.setEndTimestamp(testNotificationAttribute.getEndTime().toEpochMilli());
        req.setStyle(testNotificationAttribute.getStyle());
        req.setTargetUser(testNotificationAttribute.getTargetUser());
        req.setTitle(testNotificationAttribute.getTitle());
        req.setMessage(testNotificationAttribute.getMessage());

        return req;
    }

}
