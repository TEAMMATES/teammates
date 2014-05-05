package teammates.ui.controller;

import java.util.ArrayList;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.Url;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.datastore.Text;

public class StudentEvalSubmissionEditSaveAction extends Action {
    

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        String fromEmail = getRequestParamValue(Const.ParamsNames.FROM_EMAIL);        
        String teamName = getRequestParamValue(Const.ParamsNames.TEAM_NAME);
        
        if(isParameterNull(courseId) || isParameterNull(evalName) || 
                isParameterNull(fromEmail) || isParameterNull(teamName)) {
            return redirectAndShowExpiredRequest();
        } else {
            
            String[] toEmails = getRequestParamValues(Const.ParamsNames.TO_EMAIL);
            String[] points = getRequestParamValues(Const.ParamsNames.POINTS);
            String[] justifications = getRequestParamValues(Const.ParamsNames.JUSTIFICATION);
            String[] comments = getRequestParamValues(Const.ParamsNames.COMMENTS);
            
            EvaluationAttributes eval = logic.getEvaluation(courseId, evalName);
            
            if (eval.getStatus() == EvalStatus.PUBLISHED) {
                throw new UnauthorizedAccessException(Const.Tooltips.EVALUATION_STATUS_PUBLISHED);
            } else if (eval.getStatus() == EvalStatus.CLOSED) {
                throw new UnauthorizedAccessException(Const.Tooltips.EVALUATION_STATUS_CLOSED);
            } else if (eval.getStatus() == EvalStatus.AWAITING) {
                throw new UnauthorizedAccessException(Const.Tooltips.EVALUATION_STATUS_AWAITING);
            } else if (eval.getStatus() == EvalStatus.DOES_NOT_EXIST) {
                throw new UnauthorizedAccessException(Const.StatusMessages.EVALUATION_DELETED);
            }
            
            //extract submission data
            ArrayList<SubmissionAttributes> submissionData = new ArrayList<SubmissionAttributes>();
            int submissionCount = (toEmails == null ? 0 : toEmails.length);
            boolean emptyPointExists = false;
            for (int i = 0; i < submissionCount; i++) {
                SubmissionAttributes sub = new SubmissionAttributes();
                sub.course = courseId;
                sub.evaluation = evalName;
                sub.justification = new Text(justifications[i]);
                
                if (eval.p2pEnabled) {
                    sub.p2pFeedback = new Text(comments[i]);
                }
                
                try {
                    sub.points = Integer.parseInt(points[i]);
                } catch (NumberFormatException e) {
                    //The point dropdown is unfilled and is blank
                    sub.points = Const.POINTS_NOT_SUBMITTED;
                    emptyPointExists = true;
                }
                
                sub.reviewee = toEmails[i];
                sub.reviewer = fromEmail;
                sub.team = teamName;
                submissionData.add(sub);
            }
            
            if (emptyPointExists) {
                isError = true;
                statusToUser.add("Please give contribution scale to everyone");
            }
            
            new GateKeeper().verifyAccessible(
                    logic.getStudentForGoogleId(courseId, account.googleId),
                    submissionData);
            
            try{
                logic.updateSubmissions(submissionData);
                statusToAdmin = createLogMesage(courseId, evalName, teamName, fromEmail, toEmails, points, justifications, comments);
            } catch (InvalidParametersException e) {
                //TODO: Let the user retry?
                setStatusForException(e);
            }        
            
            RedirectResult response;
            if (isError) {
                String submissionUrl = Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE;
                submissionUrl = Url.addParamToUrl(submissionUrl, Const.ParamsNames.COURSE_ID, courseId);
                submissionUrl = Url.addParamToUrl(submissionUrl, Const.ParamsNames.EVALUATION_NAME, evalName);
                submissionUrl = Url.addParamToUrl(submissionUrl, Const.ParamsNames.USER_ID, account.googleId);
                response = createRedirectResult(submissionUrl);
            } else {
                statusToUser.add(String.format(Const.StatusMessages.STUDENT_EVALUATION_SUBMISSION_RECEIVED, Sanitizer.sanitizeForHtml(evalName), courseId));
                String submissionUrl = Const.ActionURIs.STUDENT_HOME_PAGE;
                
                String submittedEvaluationName = courseId + evalName;
                submissionUrl = Url.addParamToUrl(submissionUrl, Const.ParamsNames.CHECK_PERSISTENCE_EVALUATION, submittedEvaluationName);
                log.info(submittedEvaluationName);
                response = createRedirectResult(submissionUrl);
            }
            return response;
        }
    }

    private String createLogMesage(
            String courseId, 
            String evalName,
            String teamName,
            String fromEmail, 
            String[] toEmails, 
            String[] points,
            String[] justifications, 
            String[] comments) {
        
        String message = "<span class=\"bold\">(" + teamName + ") " + fromEmail + "'s</span> Submission for Evaluation <span class=\"bold\">(" + evalName + ")</span> for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br><br>";
        
        int submissionCount = ((toEmails == null ? 0 : toEmails.length));
        for (int i = 0; i < submissionCount; i++){
            message += "<span class=\"bold\">To:</span> " + toEmails[i] + "<br>";
            message += "<span class=\"bold\">Points:</span> " + points[i] + "<br>";
            if (comments == null){    //p2pDisabled
                message += "<span class=\"bold\">Comments: </span>Disabled<br>";
            } else {
                message += "<span class=\"bold\">Comments:</span> " + comments[i].replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>") + "<br>";
            }
            message += "<span class=\"bold\">Justification:</span> " + justifications[i].replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
            message += "<br><br>";
        }  
        return message;
    }

    private boolean isParameterNull(String param) {
        if(param == null) {
            return true;
        }
        return false;
    }
    
    private ActionResult redirectAndShowExpiredRequest() {
        statusToUser.add(Const.StatusMessages.EVALUATION_REQUEST_EXPIRED);
        return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
    }
    
}
