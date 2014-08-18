package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

public class InstructorEvalEditSaveAction extends Action {
    Logger log = Utils.getLogger();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        Assumption.assertNotNull(evalName);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getEvaluation(courseId, evalName), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        InstructorEvalEditPageData data = new InstructorEvalEditPageData(account);
        
        data.evaluation = extractEvaluationData();
        
        try {
            
            logic.updateEvaluation(data.evaluation.courseId, data.evaluation.name, 
                    data.evaluation.instructions.getValue(), data.evaluation.startTime,
                    data.evaluation.endTime, data.evaluation.timeZone, 
                    data.evaluation.gracePeriod, data.evaluation.p2pEnabled);
            
            statusToUser.add(Const.StatusMessages.EVALUATION_EDITED);
            statusToAdmin = "Editing Evaluation <span class=\"bold\">(" + data.evaluation.name + 
                    ")</span> for Course <span class=\"bold\">[" + data.evaluation.courseId + "]</span>.<br>" +
                    "<span class=\"bold\">From:</span> " + data.evaluation.startTime + 
                    "<span class=\"bold\"> to</span> " + data.evaluation.endTime + "<br>" +
                    "<span class=\"bold\">Peer feedback:</span> " + (data.evaluation.p2pEnabled ? "enabled" : "disabled") + 
                    "<br><br><span class=\"bold\">Instructions:</span> " + data.evaluation.instructions;
            
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_EVALS_PAGE);
            
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_EVAL_EDIT, data);
        }
        

    }

}