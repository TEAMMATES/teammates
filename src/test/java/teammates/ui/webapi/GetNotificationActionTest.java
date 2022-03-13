package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.NotificationType;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.ui.output.NotificationData;
import teammates.ui.output.NotificationsData;
import teammates.ui.output.StudentProfileData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetNotificationAction}.
 */
public class GetNotificationActionTest extends BaseActionTest<GetNotificationAction> {

    private NotificationAttributes notificationAttributes;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Override
    protected void prepareTestData() {
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    @Override
    protected void testExecute() {
        // See independent test cases
    }

    @Override
    protected void testAccessControl(){
        verifyAnyUserCanAccess();
    }

    @Test
    public void testExecute_withFullDetailIntentForNonAdmin_shouldReturnDataWithFullDetail() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        NotificationAttributes notification = typicalBundle.notifications.get("notification-1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.NOTIFICATION_TARGET_USER, NotificationTargetUser.INSTRUCTOR.toString(),
                Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL, String.valueOf(true),
        };

        GetNotificationAction action = getAction(submissionParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationsData output = (NotificationsData) jsonResult.getOutput();
        List<NotificationData> notifications = output.getNotifications();

        assertEquals(2, logic.getActiveNotificationsByTargetUser(NotificationTargetUser.INSTRUCTOR).size());
        assertEquals(2, notifications.size());

        NotificationData firstNotification = notifications.get(0);
        assertEquals("notification-3", firstNotification.getNotificationId());
        assertEquals(NotificationType.VERSION_NOTE, firstNotification.getNotificationType());
        assertEquals(NotificationTargetUser.INSTRUCTOR, firstNotification.getTargetUser());
        assertEquals("The first version note", firstNotification.getTitle());
        assertEquals("The version note content", firstNotification.getMessage());
    }
}
