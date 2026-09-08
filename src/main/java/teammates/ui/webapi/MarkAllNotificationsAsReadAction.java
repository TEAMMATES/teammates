package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.storage.entity.Account;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.ReadNotificationsData;

/**
 * Action: Marks all notifications visible to the current user as read.
 */
public class MarkAllNotificationsAsReadAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Any logged in user can mark their own visible notifications as read.
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        Account account = getCurrentAccount();

        try {
            List<UUID> markedNotificationIds = logic.createReadNotificationsForAllVisible(
                    account.getId(), deriveTargetUsersForCurrentAccount(), true);
            return new JsonResult(new ReadNotificationsData(markedNotificationIds));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

    private List<NotificationTargetUser> deriveTargetUsersForCurrentAccount() {
        List<NotificationTargetUser> targetUsers = new ArrayList<>();
        targetUsers.add(NotificationTargetUser.GENERAL);

        try {
            gateKeeper.verifyStudentInAnyCourse(requestContext);
            targetUsers.add(NotificationTargetUser.STUDENT);
        } catch (UnauthorizedAccessException e) {
            // Not a student.
        }

        try {
            gateKeeper.verifyInstructorInAnyCourse(requestContext);
            targetUsers.add(NotificationTargetUser.INSTRUCTOR);
        } catch (UnauthorizedAccessException e) {
            // Not an instructor.
        }

        return targetUsers;
    }
}
