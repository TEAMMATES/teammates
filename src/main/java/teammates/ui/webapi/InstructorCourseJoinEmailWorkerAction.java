package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;

/**
 * Task queue worker action: sends registration email for an instructor of a course.
 */
class InstructorCourseJoinEmailWorkerAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(ParamsNames.COURSE_ID);
        CourseAttributes course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Course with ID " + courseId + " does not exist!"));
        }

        String instructorEmail = getNonNullRequestParamValue(ParamsNames.INSTRUCTOR_EMAIL);

        // The instructor is queried using the `id`of instructor as it ensures that the
        // instructor is retrieved (and not null) even if the index building for
        // saving the new instructor takes more time in database.
        // The instructor `id` can be constructed back using (instructorEmail%courseId)
        // because instructors' email cannot be changed before joining the course.
        InstructorAttributes instructor = logic.getInstructorById(courseId, instructorEmail);
        if (instructor == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Instructor does not exist."));
        }

        boolean isRejoin = getBooleanRequestParamValue(ParamsNames.IS_INSTRUCTOR_REJOINING);

        EmailWrapper email;
        if (isRejoin) {
            String institute = getRequestParamValue(ParamsNames.INSTRUCTOR_INSTITUTION);
            email = emailGenerator
                    .generateInstructorCourseRejoinEmailAfterGoogleIdReset(instructor, course, institute);
        } else {
            String inviterId = getNonNullRequestParamValue(ParamsNames.INVITER_ID);
            AccountAttributes inviter = logic.getAccount(inviterId);
            if (inviter == null) {
                throw new EntityNotFoundException(
                        new EntityDoesNotExistException("Inviter account does not exist."));
            }

            email = emailGenerator.generateInstructorCourseJoinEmail(inviter, instructor, course);
        }

        emailSender.sendEmail(email);
        return new JsonResult("Successful");
    }

}
