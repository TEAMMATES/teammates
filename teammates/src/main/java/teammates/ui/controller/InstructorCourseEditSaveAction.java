package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class InstructorCourseEditSaveAction extends Action {
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        String courseName = getRequestParamValue(Const.ParamsNames.COURSE_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_NAME, courseName);

        String courseTimeZone = getRequestParamValue(Const.ParamsNames.COURSE_TIME_ZONE);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_TIME_ZONE, courseTimeZone);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);

        try {
            logic.updateCourse(courseId, courseName, courseTimeZone);

            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_EDITED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Updated Course <span class=\"bold\">[" + courseId + "]</span> details:<br>"
                            + "Name: " + courseName + "<br>"
                            + "Time zone: " + courseTimeZone;

        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }

        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
        result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return result;
    }
}
