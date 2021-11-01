package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

/**
 * Action: deletes an unregistered instructor account.
 */
class DeleteUnregisteredAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor;
        if (instructorEmail == null) {
            throw new InvalidHttpParameterException("Instructor to delete not specified");
        } else {
            instructor = logic.getInstructorForEmail(courseId, instructorEmail);
        }
        if (instructor == null) {
            return new JsonResult("Instructor is successfully deleted.");
        }

        logic.deleteUnregisteredInstructorCascade(courseId, instructor.getEmail());

        return new JsonResult("Instructor is successfully deleted.");
    }

}
