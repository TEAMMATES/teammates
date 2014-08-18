package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorEvalPublishAction extends InstructorEvalsPageAction {
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);

        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getEvaluation(courseId, evalName), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        try {
            logic.publishEvaluation(courseId, evalName);
        } catch (InvalidParametersException e) {
            Assumption.fail("InvalidParametersException not expected at this point");
        }
        
        statusToUser.add(Const.StatusMessages.EVALUATION_PUBLISHED);
        statusToAdmin = "Evaluation <span class=\"bold\">(" + evalName + ")</span> " +
                "for Course <span class=\"bold\">[" + courseId + "]</span> published.";
        
        nextUrl = nextUrl == null ? Const.ActionURIs.INSTRUCTOR_EVALS_PAGE : nextUrl;

        return createRedirectResult(nextUrl);
    }
    
}
