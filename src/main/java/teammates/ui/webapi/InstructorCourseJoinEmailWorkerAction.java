package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;

/**
 * Task queue worker action: sends registration email for an instructor of a course.
 */
public class InstructorCourseJoinEmailWorkerAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(ParamsNames.COURSE_ID);

        if (!isCourseMigrated(courseId)) {
            CourseAttributes course = logic.getCourse(courseId);
            if (course == null) {
                throw new EntityNotFoundException("Course with ID " + courseId + " does not exist!");
            }

            String instructorEmail = getNonNullRequestParamValue(ParamsNames.INSTRUCTOR_EMAIL);

            // The instructor is queried using the `id`of instructor as it ensures that the
            // instructor is retrieved (and not null) even if the index building for
            // saving the new instructor takes more time in database.
            // The instructor `id` can be constructed back using (instructorEmail%courseId)
            // because instructors' email cannot be changed before joining the course.
            InstructorAttributes instructor = logic.getInstructorById(courseId, instructorEmail);
            if (instructor == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }

            boolean isRejoin = getBooleanRequestParamValue(ParamsNames.IS_INSTRUCTOR_REJOINING);

            EmailWrapper email;
            if (isRejoin) {
                email = emailGenerator.generateInstructorCourseRejoinEmailAfterGoogleIdReset(instructor, course);
            } else {
                String inviterId = getNonNullRequestParamValue(ParamsNames.INVITER_ID);
                // we get account from sql, as all accounts are migrated to sql
                Account inviter = sqlLogic.getAccountForGoogleId(inviterId);
                if (inviter == null) {
                    throw new EntityNotFoundException("Inviter account does not exist.");
                }

                email = emailGenerator.generateInstructorCourseJoinEmail(inviter, instructor, course);
            }

            emailSender.sendEmail(email);
            return new JsonResult("Successful");
        }

        Course course = sqlLogic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("Course with ID " + courseId + " does not exist!");
        }
        String instructorEmail = getNonNullRequestParamValue(ParamsNames.INSTRUCTOR_EMAIL);

        Instructor instructor = sqlLogic.getInstructorForEmail(courseId, instructorEmail);
        if (instructor == null) {
            throw new EntityNotFoundException("Instructor does not exist.");
        }

        boolean isRejoin = getBooleanRequestParamValue(ParamsNames.IS_INSTRUCTOR_REJOINING);

        EmailWrapper email;
        if (isRejoin) {
            email = sqlEmailGenerator.generateInstructorCourseRejoinEmailAfterGoogleIdReset(instructor, course);
        } else {
            String inviterId = getNonNullRequestParamValue(ParamsNames.INVITER_ID);
            Account inviter = sqlLogic.getAccountForGoogleId(inviterId);
            if (inviter == null) {
                throw new EntityNotFoundException("Inviter account does not exist.");
            }

            email = sqlEmailGenerator.generateInstructorCourseJoinEmail(inviter, instructor, course);
        }

        emailSender.sendEmail(email);
        return new JsonResult("Successful");
    }

}
