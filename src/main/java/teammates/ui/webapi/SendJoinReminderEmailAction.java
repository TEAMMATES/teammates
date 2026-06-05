package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Send join reminder emails to register for a course.
 */
public class SendJoinReminderEmailAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        User user = getUserToRemind();
        Course course = user == null ? getCourseForCourseWideReminder() : user.getCourse();
        if (course == null) {
            throw new EntityNotFoundException("Course does not exist!");
        }

        Instructor instructor = getInstructorFromRequest(course.getId());

        if (user == null || user instanceof Student) {
            gateKeeper.verifyInstructorInCourse(instructor, course);
            gateKeeper.verifyAccessible(instructor, Const.InstructorPermissions.CAN_MODIFY_STUDENT);
        } else if (user instanceof Instructor) {
            gateKeeper.verifyInstructorInCourse(instructor, course);
            gateKeeper.verifyAccessible(instructor, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
        }
    }

    @Override
    public JsonResult execute() {
        User user = getUserToRemind();

        JsonResult statusMsg;

        if (user instanceof Student student) {
            Course course = student.getCourse();
            EmailWrapper email = emailGenerator.generateStudentCourseJoinEmail(course, student);
            List<EmailWrapper> emails = new ArrayList<>();
            emails.add(email);
            taskQueuer.scheduleEmailsForPrioritySending(emails);
            statusMsg = new JsonResult("An email has been sent to " + student.getEmail());

        } else if (user instanceof Instructor instructorData) {
            Course course = instructorData.getCourse();
            Instructor inviter = getInstructorFromRequest(course.getId());
            if (inviter == null) {
                throw new EntityNotFoundException("Inviter does not exist.");
            }
            EmailWrapper email = emailGenerator.generateInstructorCourseJoinEmail(inviter, instructorData, course);
            List<EmailWrapper> emails = new ArrayList<>();
            emails.add(email);
            taskQueuer.scheduleEmailsForPrioritySending(emails);
            statusMsg = new JsonResult("An email has been sent to " + instructorData.getEmail());

        } else {
            Course course = getCourseForCourseWideReminder();
            List<Student> studentDataList = logic.getUnregisteredStudentsForCourse(course.getId());
            List<EmailWrapper> emails = new ArrayList<>();
            for (Student student : studentDataList) {
                EmailWrapper email = emailGenerator.generateStudentCourseJoinEmail(course, student);
                emails.add(email);
            }
            taskQueuer.scheduleEmailsForPrioritySending(emails);
            statusMsg = new JsonResult("Emails have been sent to unregistered students.");
        }

        return statusMsg;
    }

    private User getUserToRemind() {
        UUID userId = getNullableUuidRequestParamValue(Const.ParamsNames.USER_ID);
        if (userId == null) {
            return null;
        }

        User user = logic.getUser(userId);
        if (user == null) {
            throw new EntityNotFoundException("User with ID " + userId + " does not exist.");
        }
        return user;
    }

    private Course getCourseForCourseWideReminder() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Course course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("Course with ID " + courseId + " does not exist.");
        }
        return course;
    }
}
