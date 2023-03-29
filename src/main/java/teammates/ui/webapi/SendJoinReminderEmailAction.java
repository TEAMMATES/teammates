package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

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

        if (!isCourseMigrated(courseId)) {
            CourseAttributes course = logic.getCourse(courseId);
            if (course == null) {
                throw new EntityNotFoundException("Course with ID " + courseId + " does not exist!");
            }

            String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
            String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);

            boolean isSendingToStudent = studentEmail != null;
            boolean isSendingToInstructor = instructorEmail != null;
            if (isSendingToStudent) {
                gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_STUDENT);
            } else if (isSendingToInstructor) {
                gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
            } else {
                // this is sending registration emails to all students in the course and we will check if the instructor
                // canmodifystudent for course level since for modifystudent privilege there is only course level setting
                // for now
                gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_STUDENT);
            }

            return;
        }

        Course course = sqlLogic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("Course with ID " + courseId + " does not exist!");
        }

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.id);

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

        if (!isCourseMigrated(courseId)) {
            CourseAttributes course = logic.getCourse(courseId);
            if (course == null) {
                throw new EntityNotFoundException("Course with ID " + courseId + " does not exist!");
            }

            String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
            String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
            boolean isSendingToStudent = studentEmail != null;
            boolean isSendingToInstructor = instructorEmail != null;

            JsonResult statusMsg;

            if (isSendingToStudent) {
                taskQueuer.scheduleCourseRegistrationInviteToStudent(courseId, studentEmail, false);
                StudentAttributes studentData = logic.getStudentForEmail(courseId, studentEmail);
                if (studentData == null) {
                    throw new EntityNotFoundException(
                            "Student with email " + studentEmail + " does not exist in course " + courseId + "!");
                }
                statusMsg = new JsonResult("An email has been sent to " + studentEmail);

            } else if (isSendingToInstructor) {
                taskQueuer.scheduleCourseRegistrationInviteToInstructor(userInfo.id,
                        instructorEmail, courseId, false);

                InstructorAttributes instructorData = logic.getInstructorForEmail(courseId, instructorEmail);
                if (instructorData == null) {
                    throw new EntityNotFoundException(
                            "Instructor with email " + instructorEmail + " does not exist in course " + courseId + "!");
                }
                statusMsg = new JsonResult("An email has been sent to " + instructorEmail);

            } else {
                List<StudentAttributes> studentDataList = logic.getUnregisteredStudentsForCourse(courseId);
                for (StudentAttributes student : studentDataList) {
                    taskQueuer.scheduleCourseRegistrationInviteToStudent(course.getId(), student.getEmail(), false);
                }
                statusMsg = new JsonResult("Emails have been sent to unregistered students.");
            }

            return statusMsg;
        }

        Course course = sqlLogic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("Course with ID " + courseId + " does not exist!");
        }

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        boolean isSendingToStudent = studentEmail != null;
        boolean isSendingToInstructor = instructorEmail != null;

        JsonResult statusMsg;

        if (isSendingToStudent) {
            taskQueuer.scheduleCourseRegistrationInviteToStudent(courseId, studentEmail, false);
            Student studentData = sqlLogic.getStudentForEmail(courseId, studentEmail);
            if (studentData == null) {
                throw new EntityNotFoundException(
                        "Student with email " + studentEmail + " does not exist in course " + courseId + "!");
            }
            statusMsg = new JsonResult("An email has been sent to " + studentEmail);

        } else if (isSendingToInstructor) {
            taskQueuer.scheduleCourseRegistrationInviteToInstructor(userInfo.id,
                    instructorEmail, courseId, false);

            Instructor instructorData = sqlLogic.getInstructorForEmail(courseId, instructorEmail);
            if (instructorData == null) {
                throw new EntityNotFoundException(
                        "Instructor with email " + instructorEmail + " does not exist in course " + courseId + "!");
            }
            statusMsg = new JsonResult("An email has been sent to " + instructorEmail);

        } else {
            List<Student> studentDataList = sqlLogic.getUnregisteredStudentsForCourse(courseId);
            for (Student student : studentDataList) {
                taskQueuer.scheduleCourseRegistrationInviteToStudent(course.getId(), student.getEmail(), false);
            }
            statusMsg = new JsonResult("Emails have been sent to unregistered students.");
        }

        return statusMsg;
    }
}
