package teammates.ui.controller;

import java.util.ArrayList;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.datastore.Text;

public class InstructorEvalSubmissionEditSaveAction extends Action {
    
    //TODO: this action has highly similar to student counterpart. extract parent class?

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        Assumption.assertNotNull(evalName);
        
        String fromEmail = getRequestParamValue(Const.ParamsNames.FROM_EMAIL);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        StudentAttributes student = logic.getStudentForEmail(courseId, fromEmail);
        EvaluationAttributes evaluation = logic.getEvaluation(courseId, evalName);
        new GateKeeper().verifyAccessible(
                instructor, evaluation, student.section, evaluation.name, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        
        String teamName = getRequestParamValue(Const.ParamsNames.TEAM_NAME);
        String[] toEmails = getRequestParamValues(Const.ParamsNames.TO_EMAIL);
        String[] points = getRequestParamValues(Const.ParamsNames.POINTS);
        String[] justifications = getRequestParamValues(Const.ParamsNames.JUSTIFICATION);
        String[] comments = getRequestParamValues(Const.ParamsNames.COMMENTS);
        
        //extract submission data
        ArrayList<SubmissionAttributes> submissionData = new ArrayList<SubmissionAttributes>();
        int submissionCount = ((toEmails == null) ? 0 : toEmails.length);
        for(int i=0; i<submissionCount; i++){
            SubmissionAttributes sub = new SubmissionAttributes();
            sub.course = courseId;
            sub.evaluation = evalName;
            sub.justification = new Text(justifications[i]);
            
            if (evaluation.p2pEnabled) {
                sub.p2pFeedback = new Text(comments[i]);
            }
            
            sub.points = Integer.parseInt(points[i]);
            sub.reviewee = toEmails[i];
            sub.reviewer = fromEmail;
            sub.team = teamName;
            submissionData.add(sub);
        }
        
        
        try{
            logic.updateSubmissions(submissionData);
             statusToUser.add(String.format(Const.StatusMessages.INSTRUCTOR_EVALUATION_SUBMISSION_RECEIVED,
                    Sanitizer.sanitizeForHtml(student.name),
                    Sanitizer.sanitizeForHtml(evalName), courseId));
            statusToAdmin = createLogMesage(courseId, evalName, teamName, fromEmail, toEmails, points, justifications, comments);
            
        } catch (InvalidParametersException e) {
            //TODO: redirect to the same page?
            setStatusForException(e);
        }        
        
        RedirectResult response = createRedirectResult(Const.ActionURIs.INSTRUCTOR_EVALS_PAGE);
        return response;

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
        
        String message = "<span class=\"bold\">(" + teamName + ") " + fromEmail + 
                "'s</span> Submission for Evaluation <span class=\"bold\">(" + evalName + ")</span> " +
                        "for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br><br>";
        int submissionCount = ((toEmails == null) ? 0 : toEmails.length);
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

    
}
