package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;

/**
 * Unlinks the account associated with the user profile without deleting
 * either entity, allowing the profile to be linked to a different account.
 */
public class UnlinkAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        User existingUser;

        try {
            existingUser = logic.unlinkAccount(userId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        Course course = existingUser.getCourse();

        if (existingUser instanceof Student existingStudent) {
            // Generate and queue rejoin email to priority queue
            EmailWrapper email = emailGenerator
                    .generateStudentCourseRejoinEmailAfterUnlinkAccount(course, existingStudent);
            List<EmailWrapper> emails = new ArrayList<>();
            emails.add(email);
            emailQueueService.enqueuePriority(emails);
        } else if (existingUser instanceof Instructor existingInstructor) {
            // Generate and queue rejoin email to priority queue
            EmailWrapper email = emailGenerator
                    .generateInstructorCourseRejoinEmailAfterUnlinkAccount(existingInstructor, course);
            List<EmailWrapper> emails = new ArrayList<>();
            emails.add(email);
            emailQueueService.enqueuePriority(emails);
        }

        return new JsonResult("Account unlinked successfully.");
    }

}
