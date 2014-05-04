package teammates.ui.controller;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class EvaluationStatsPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
 
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        Assumption.assertNotNull(evalName);
        
        EvaluationAttributes eval = logic.getEvaluation(courseId, evalName);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        
        new GateKeeper().verifyAccessible(
                instructor,
                eval);
        
        EvaluationStatsPageData data = new EvaluationStatsPageData(account);
        
        data.evaluationDetails = logic.getEvaluationDetails(courseId,evalName);
        
        return createAjaxResult(Const.ViewURIs.INSTRUCTOR_EVAL_STATS, data);
    }

}
