package teammates.ui.controller;

import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

public class StudentEvalSubmissionEditPageAction extends Action {
    
    private StudentEvalSubmissionEditPageData data;
    

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        Assumption.assertNotNull(evalName);
        
        String recentlyJoinedCourseId = getRequestParamValue(Const.ParamsNames.CHECK_PERSISTENCE_COURSE);
        
        if(!isJoinedCourse(courseId, account.googleId)){
            if(recentlyJoinedCourseId == null) {
                return createPleaseJoinCourseResponse(courseId);
            } else {
                statusToUser.add("Updating of the course data on our servers is currently in progress "
                        + "and will be completed in a few minutes. "
                        + "<br>Please wait a few minutes to submit the evaluation again.");
                
                RedirectResult response = createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
                return response;
            }
        }
        //No need to call GateKeeper because of the above redirect
        
        data = new StudentEvalSubmissionEditPageData(account);
        data.student = logic.getStudentForGoogleId(courseId, account.googleId);
        data.eval = logic.getEvaluation(courseId, evalName);
        
        if(data.eval == null) {
            statusToUser.add("The evaluation has been deleted and is no longer accessible.");
            
            RedirectResult response = createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
            return response;
        }
   
        
        try{
            data.submissions = logic.getSubmissionsForEvaluationFromStudent(courseId, evalName, data.student.email);
        } catch (InvalidParametersException e) {
            Assumption.fail("Invalid parameters not expected at this stage");
        }

        SubmissionAttributes.sortByReviewee(data.submissions);
        SubmissionAttributes.putSelfSubmissionFirst(data.submissions);
        
        if(data.eval.getStatus() != EvalStatus.OPEN){
            statusToUser.add(Const.StatusMessages.EVALUATION_NOT_OPEN);
            data.disableAttribute = "disabled=\"disabled\"";
        }
        
        statusToAdmin = "studentEvalEdit Page Load<br>"+ 
                "Editing <span class=\"bold\">" + data.student.email + "'s</span> Submission " +
                "<span class=\"bold\">("+ data.eval.name +")</span> " +
                "for Course <span class=\"bold\">[" + courseId + "]</span>";
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.STUDENT_EVAL_SUBMISSION_EDIT, data);
        return response;

    }
    
}
