package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FeedbackSessionTemplates;
import teammates.common.util.Sanitizer;
import teammates.common.util.TimeHelper;
import teammates.logic.api.GateKeeper;
import teammates.logic.core.Emails.EmailType;

import com.google.appengine.api.datastore.Text;

public class InstructorFeedbackAddAction extends InstructorFeedbacksPageAction {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        Assumption.assertNotNull(courseId);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId); 
        
        new GateKeeper().verifyAccessible(
                instructor, 
                logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
                
        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(account);

        FeedbackSessionAttributes fs = extractFeedbackSessionData();

        // Set creator email as instructors' email
        fs.creatorEmail = instructor.email;
        
        data.newFeedbackSession = fs;
        
        String feedbackSessionType = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TYPE);
        data.feedbackSessionType = feedbackSessionType;
        
        try {
            logic.createFeedbackSession(fs);
            
            try {
                createTemaplateFeedbackQuestions(fs.courseId, fs.feedbackSessionName, fs.creatorEmail, feedbackSessionType);
            } catch(Exception e){
                //Failed to create feedback questions for specified template/feedback session type.
                //TODO: let the user know an error has occurred? delete the feedback session?
            }
            
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_ADDED);
            statusToAdmin = "New Feedback Session <span class=\"bold\">(" + fs.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" + fs.courseId + "]</span> created.<br>" +
                    "<span class=\"bold\">From:</span> " + fs.startTime + "<span class=\"bold\"> to</span> " + fs.endTime + "<br>" +
                    "<span class=\"bold\">Session visible from:</span> " + fs.sessionVisibleFromTime + "<br>" +
                    "<span class=\"bold\">Results visible from:</span> " + fs.resultsVisibleFromTime + "<br><br>" +
                    "<span class=\"bold\">Instructions:</span> " + fs.instructions;
            
            //TODO: add a condition to include the status due to inconsistency problem of database 
            //      (similar to the one below)
            return createRedirectResult(new PageData(account).getInstructorFeedbackSessionEditLink(fs.courseId,fs.feedbackSessionName));
            
        } catch (EntityAlreadyExistsException e) {
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
            statusToAdmin = e.getMessage();
            isError = true;
            
        } catch (InvalidParametersException e) {
            // updates isError attribute
            setStatusForException(e);
        } 
        
        // if isError == true,
        data.instructors = new HashMap<String, InstructorAttributes>();
        data.courses = loadCoursesList(account.googleId, data.instructors);
        data.existingEvalSessions = loadEvaluationsList(account.googleId);
        data.existingFeedbackSessions = loadFeedbackSessionsList(account.googleId);
        
        if (data.existingFeedbackSessions.size() == 0) {
            statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_ADD_DB_INCONSISTENCY);
        }
    
        EvaluationAttributes.sortEvaluationsByDeadlineDescending(data.existingEvalSessions);
        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(data.existingFeedbackSessions);
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACKS, data);
    }
    
    private void createTemaplateFeedbackQuestions(String courseId,
            String feedbackSessionName, String creatorEmail,
            String feedbackSessionType) throws InvalidParametersException {
        if(feedbackSessionType == null){
            return;
        }
        switch(feedbackSessionType){
            case "TEAMEVALUATION":
                List<FeedbackQuestionAttributes> questions =
                        FeedbackSessionTemplates.getFeedbackSessionTemplateQuestions(FeedbackSessionTemplates.FEEDBACK_SESSION_TEAMEVALUATION, courseId, feedbackSessionName, creatorEmail);
                int questionNumber = 1;
                for(FeedbackQuestionAttributes fqa : questions){
                    logic.createFeedbackQuestionForTemplate(fqa, questionNumber);
                    questionNumber++;
                }
                break;
            default:
                break;
        }
    }

    private FeedbackSessionAttributes extractFeedbackSessionData() {
        //TODO assert parameters are not null then update test
        //TODO make this method stateless
        
        FeedbackSessionAttributes newSession = new FeedbackSessionAttributes();
        newSession.courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        newSession.feedbackSessionName = Sanitizer.sanitizeTextField(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME));
        
        newSession.createdTime = new Date();
        newSession.startTime = TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME));
        newSession.endTime = TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME));        
        String paramTimeZone = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE);
        if (paramTimeZone != null) {
            newSession.timeZone = Double.parseDouble(paramTimeZone);
        }
        String paramGracePeriod = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD);
        if (paramGracePeriod != null) {
            newSession.gracePeriod = Integer.parseInt(paramGracePeriod);
        }
        newSession.sentOpenEmail = false; 
        newSession.sentPublishedEmail = false;
        newSession.feedbackSessionType = FeedbackSessionType.STANDARD;
        newSession.instructions = new Text(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS));
        
        String type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM:
            newSession.resultsVisibleFromTime = TimeHelper.combineDateTime(
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE),
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME));
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE:
            newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_VISIBLE;
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER:
            newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_NEVER:
            newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
            break;
        }
        
        type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM: //TODO Magic strings. Use enums to prevent potentila bugs caused by typos.
            newSession.sessionVisibleFromTime = TimeHelper.combineDateTime(
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE),
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME));
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN:
            newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_NEVER:
            newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
            // overwrite if private
            newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
            newSession.feedbackSessionType = FeedbackSessionType.PRIVATE;
            break;
        }
        
        String[] sendReminderEmailsArray = getRequestParamValues(Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL);
        List<String> sendReminderEmailsList = sendReminderEmailsArray == null ? new ArrayList<String>() : Arrays.asList(sendReminderEmailsArray);
        newSession.isOpeningEmailEnabled = sendReminderEmailsList.contains(EmailType.FEEDBACK_OPENING.toString());
        newSession.isClosingEmailEnabled = sendReminderEmailsList.contains(EmailType.FEEDBACK_CLOSING.toString());
        newSession.isPublishedEmailEnabled = sendReminderEmailsList.contains(EmailType.FEEDBACK_PUBLISHED.toString());
        
        return newSession;
    }

}
