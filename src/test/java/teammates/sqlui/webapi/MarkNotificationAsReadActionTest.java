package teammates.sqlui.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.ReadNotificationsData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.MarkNotificationAsReadRequest;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.MarkNotificationAsReadAction;

/**
 * SUT: {@link MarkNotificationAsReadAction}.
 */
public class MarkNotificationAsReadActionTest extends BaseActionTest<MarkNotificationAsReadAction> {
    Instructor instructor;
    String instructorId;
    Notification testNotification;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION_READ;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        instructor = getTypicalInstructor();
        instructorId = instructor.getGoogleId();
        testNotification = getTypicalNotificationWithId();
        loginAsInstructor(instructorId);
    }

    @Test
    protected void testExecute_markNotificationAsRead_shouldSucceed() throws Exception {
        when(mockLogic.updateReadNotifications(
                instructorId,
                testNotification.getId(),
                testNotification.getEndTime()
        )).thenReturn(List.of(testNotification.getId()));

        MarkNotificationAsReadRequest reqBody = new MarkNotificationAsReadRequest(
                testNotification.getId().toString(), testNotification.getEndTime().toEpochMilli());

        MarkNotificationAsReadAction action = getAction(reqBody);
        JsonResult actionOutput = getJsonResult(action);

        ReadNotificationsData response = (ReadNotificationsData) actionOutput.getOutput();
        List<String> readNotifications = response.getReadNotifications();

        assertTrue(readNotifications.contains(testNotification.getId().toString()));
    }

    @Test
    protected void testExecute_markInvalidNotificationAsRead_shouldThrowIllegalArgumentError() {
        MarkNotificationAsReadRequest reqBody =
                new MarkNotificationAsReadRequest("invalid id", testNotification.getEndTime().toEpochMilli());

        MarkNotificationAsReadAction action = getAction(reqBody);

        assertThrows(IllegalArgumentException.class, () -> action.execute());
    }

    @Test
    protected void testExecute_markNonExistentNotificationAsRead_shouldFail() throws Exception {
        UUID nonExistentNotificationId = UUID.randomUUID();

        MarkNotificationAsReadRequest reqBody =
                new MarkNotificationAsReadRequest(
                        nonExistentNotificationId.toString(),
                        testNotification.getEndTime().toEpochMilli());

        when(mockLogic.updateReadNotifications(
                any(),
                eq(nonExistentNotificationId),
                eq(testNotification.getEndTime()))).thenThrow(new EntityDoesNotExistException(""));

        MarkNotificationAsReadAction action = getAction(reqBody);

        assertThrows(EntityNotFoundException.class, () -> action.execute());
    }

    @Test
    protected void testExecute_notificationEndTimeIsZero_shouldFail() {
        MarkNotificationAsReadRequest reqBody =
                new MarkNotificationAsReadRequest(testNotification.getId().toString(), 0L);
        verifyHttpRequestBodyFailure(reqBody);
    }

    @Test
    protected void testExecute_markExpiredNotificationAsRead_shouldFail() throws Exception {
        when(mockLogic.updateReadNotifications(
                instructorId,
                testNotification.getId(),
                testNotification.getEndTime()
        )).thenThrow(new InvalidParametersException("Trying to mark an expired notification as read."));

        MarkNotificationAsReadRequest reqBody = new MarkNotificationAsReadRequest(
                testNotification.getId().toString(),
                testNotification.getEndTime().toEpochMilli());

        MarkNotificationAsReadAction action = getAction(reqBody);

        assertThrows(InvalidHttpRequestBodyException.class, () -> action.execute());
    }
}
