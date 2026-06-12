package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.InstructorData;

/**
 * Get the information of the instructor associated with the request.
 */
public class GetOwnInstructorAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.REG_KEY;
    }

    @Override
    void checkSpecificAccessControl() {
        // No specific access control required.
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Instructor instructor = getInstructorFromRequest(courseId);
        if (instructor == null) {
            throw new EntityNotFoundException("Instructor could not be found for this course");
        }

        return new JsonResult(new InstructorData(instructor));
    }

}
