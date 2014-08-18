package teammates.ui.controller;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorEvalAddAction extends InstructorEvalsPageAction {
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        EvaluationAttributes eval = extractEvaluationData();
        
        try {         
            logic.createEvaluation(eval);
            
            statusToUser.add(Const.StatusMessages.EVALUATION_ADDED);
            statusToAdmin = "New Evaluation <span class=\"bold\">(" + eval.name + ")</span> for Course <span class=\"bold\">[" + eval.courseId + "]</span> created.<br>" +
                    "<span class=\"bold\">From:</span> " + eval.startTime + "<span class=\"bold\"> to</span> " + eval.endTime + "<br>" +
                    "<span class=\"bold\">Peer feedback:</span> " + (eval.p2pEnabled ? "enabled" : "disabled") + "<br><br>" + 
                    "<span class=\"bold\">Instructions:</span> " + eval.instructions;
            
        } catch (EntityAlreadyExistsException e) {
            setStatusForException(e, Const.StatusMessages.EVALUATION_EXISTS);
            
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            
        }
        
        return createRedirectResult(new PageData(account).getInstructorEvaluationLink());
    }
    

    
}
