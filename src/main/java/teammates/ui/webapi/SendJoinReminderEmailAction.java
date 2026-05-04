package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

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
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Course course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("Course with ID " + courseId + " does not exist!");
        }

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.id);

        boolean isSendingToStudent = studentEmail != null;
        boolean isSendingToInstructor = instructorEmail != null;
        if (isSendingToStudent) {
            gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_STUDENT);
        } else if (isSendingToInstructor) {
            gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
        } else {
            // this is sending registration emails to all students in the course and we will check if the instructor
            // canmodifystudent for course level since for modifystudent privilege there is only course level setting for now
            gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_STUDENT);
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Course course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("Course with ID " + courseId + " does not exist!");
        }

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        boolean isSendingToStudent = studentEmail != null;
        boolean isSendingToInstructor = instructorEmail != null;

        JsonResult statusMsg;

        if (isSendingToStudent) {
            Student studentData = logic.getStudentForEmail(courseId, studentEmail);
            if (studentData == null) {
                throw new EntityNotFoundException(
                        "Student with email " + studentEmail + " does not exist in course " + courseId + "!");
            }
            EmailWrapper email = emailGenerator.generateStudentCourseJoinEmail(course, studentData);
            List<EmailWrapper> emails = new ArrayList<>();
            emails.add(email);
            taskQueuer.scheduleEmailsForPrioritySending(emails);
            statusMsg = new JsonResult("An email has been sent to " + studentEmail);

        } else if (isSendingToInstructor) {
            Instructor instructorData = logic.getInstructorForEmail(courseId, instructorEmail);
            if (instructorData == null) {
                throw new EntityNotFoundException(
                        "Instructor with email " + instructorEmail + " does not exist in course " + courseId + "!");
            }
            Account inviter = logic.getAccountForGoogleId(userInfo.id);
            if (inviter == null) {
                throw new EntityNotFoundException("Inviter account does not exist.");
            }
            EmailWrapper email = emailGenerator.generateInstructorCourseJoinEmail(inviter, instructorData, course);
            List<EmailWrapper> emails = new ArrayList<>();
            emails.add(email);
            taskQueuer.scheduleEmailsForPrioritySending(emails);
            statusMsg = new JsonResult("An email has been sent to " + instructorEmail);

        } else {
            List<Student> studentDataList = logic.getUnregisteredStudentsForCourse(courseId);
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
}
