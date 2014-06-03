package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentEvalResultsPageAction extends Action {
    
    private StudentEvalResultsPageData data;
    

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        Assumption.assertNotNull(evalName);
        
        String recentlyJoinedCourseId = getRequestParamValue(Const.ParamsNames.CHECK_PERSISTENCE_COURSE);
        
        data = new StudentEvalResultsPageData(account);
        
        if(!isJoinedCourse(courseId, account.googleId)){
            if(recentlyJoinedCourseId == null) {
                return createPleaseJoinCourseResponse(courseId);
            } else {
                statusToUser.add("Updating of the course data on our servers is currently in progress "
                        + "and will be completed in a few minutes. "
                        + "<br>Please wait few minutes to view the evaluation again.");
                
                RedirectResult response = createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
                return response;
            }
        }
        
        data.student = logic.getStudentForGoogleId(courseId, account.googleId);
        
        new GateKeeper().verifyAccessible(
                logic.getStudentForGoogleId(courseId, account.googleId),
                logic.getEvaluation(courseId,evalName));
        
        data.eval = logic.getEvaluation(courseId, evalName);
        
        if(data.eval.getStatus() != EvalStatus.PUBLISHED ){
            throw new UnauthorizedAccessException("Results of this evaluation are not yet published");
        }
        
        try{
            data.evalResult = logic.getEvaluationResultForStudent(courseId, evalName, data.student.email);
            SubmissionAttributes.sortByJustification(data.evalResult.incoming);
            data.incoming = organizeSubmissions(data.evalResult.incoming, data);
            SubmissionAttributes.sortByPointsAscending(data.evalResult.outgoing);
            data.outgoing = organizeSubmissions(data.evalResult.outgoing, data);
            SubmissionAttributes.sortByReviewee(data.evalResult.selfEvaluations);
            data.selfEvaluations = organizeSubmissions(data.evalResult.selfEvaluations, data);
            
            statusToAdmin = "studentEvalResults Page Load<br>" + 
                    "Viewing evaluation results for Evaluation <span class=\"bold\">(" + evalName + ")</span> " +
                            "of Course <span class=\"bold\">[" + courseId + "]</span>"; 
            
        } catch (InvalidParametersException e) {
            Assumption.fail("Invalid parameters are not expected at this stage");
        }
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.STUDENT_EVAL_RESULTS, data);
        return response;

    }
    
    /**
     * Puts the self submission in front and return the
     * list of submission excluding self submission.
     */
    private List<SubmissionAttributes> organizeSubmissions(List<SubmissionAttributes> subs, StudentEvalResultsPageData data) {
        for(int i=0; i<subs.size(); i++){
            SubmissionAttributes sub = subs.get(i);
            if(sub.reviewee.equals(sub.reviewer) && sub.reviewee.equals(data.student.email)){
                subs.remove(sub);
                subs.add(0,sub);
                break;
            }
        }
        return subs.subList(1,subs.size());
    }
    
    
    
}
