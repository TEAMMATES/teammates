package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

public class InstructorEvalEditPageAction extends Action {
    Logger log = Utils.getLogger();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        Assumption.assertNotNull(evalName);
        
        EvaluationAttributes eval = logic.getEvaluation(courseId, evalName);
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                eval, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        InstructorEvalEditPageData data = new InstructorEvalEditPageData(account);
        
        data.evaluation = eval;

        if(data.evaluation == null){
            throw new EntityDoesNotExistException("The evaluation \""+evalName+"\" in course "+courseId+" does not exist");
        }
        
        statusToAdmin = "Editing Evaluation <span class=\"bold\">(" + data.evaluation.name + 
                ")</span> for Course <span class=\"bold\">[" + data.evaluation.courseId + "]</span>.<br>" +
                "<span class=\"bold\">From:</span> " + data.evaluation.startTime + 
                "<span class=\"bold\"> to</span> " + data.evaluation.endTime + "<br>" +
                "<span class=\"bold\">Peer feedback:</span> " + (data.evaluation.p2pEnabled ? "enabled" : "disabled") + 
                "<br><br><span class=\"bold\">Instructions:</span> " + data.evaluation.instructions;
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_EVAL_EDIT, data);

    }

}