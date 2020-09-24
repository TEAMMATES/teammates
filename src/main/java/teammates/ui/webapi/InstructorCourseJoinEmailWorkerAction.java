package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;

/**
 * Task queue worker action: sends registration email for an instructor of a course.
 */
class InstructorCourseJoinEmailWorkerAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(ParamsNames.COURSE_ID);
        String instructorEmail = getNonNullRequestParamValue(ParamsNames.INSTRUCTOR_EMAIL);
        boolean isRejoin = getBooleanRequestParamValue(ParamsNames.IS_INSTRUCTOR_REJOINING);

        CourseAttributes course = logic.getCourse(courseId);
        Assumption.assertNotNull(course);

        // The instructor is queried using the `id`of instructor as it ensures that the
        // instructor is retrieved (and not null) even if the index building for
        // saving the new instructor takes more time in GAE.
        // The instructor `id` can be constructed back using (instructorEmail%courseId)
        // because instructors' email cannot be changed before joining the course.
        InstructorAttributes instructor = logic.getInstructorById(courseId, instructorEmail);
        Assumption.assertNotNull(instructor);

        EmailWrapper email;
        if (isRejoin) {
            String institute = getRequestParamValue(ParamsNames.INSTRUCTOR_INSTITUTION);
            email = emailGenerator
                    .generateInstructorCourseRejoinEmailAfterGoogleIdReset(instructor, course, institute);
        } else {
            String inviterId = getNonNullRequestParamValue(ParamsNames.INVITER_ID);
            AccountAttributes inviter = logic.getAccount(inviterId);
            Assumption.assertNotNull(inviter);

            email = emailGenerator.generateInstructorCourseJoinEmail(inviter, instructor, course);
        }

        emailSender.sendEmail(email);
        return new JsonResult("Successful");
    }

}
