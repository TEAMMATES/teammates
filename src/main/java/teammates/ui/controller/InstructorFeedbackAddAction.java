package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseAttributes;
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
import teammates.common.util.StatusMessage;
import teammates.common.util.TimeHelper;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;
import teammates.logic.core.Emails.EmailType;

import com.google.appengine.api.datastore.Text;

public class InstructorFeedbackAddAction extends InstructorFeedbacksPageAction {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertNotEmpty(courseId);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId); 
        
        new GateKeeper().verifyAccessible(
                instructor,
                logic.getCourse(courseId),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(account);

        FeedbackSessionAttributes fs = extractFeedbackSessionData();

        // Set creator email as instructors' email
        fs.creatorEmail = instructor.email;
        
        // A session opening reminder email is always sent as students
        // without accounts need to receive the email to be able to respond
        fs.isOpeningEmailEnabled = true;
        
     
        String feedbackSessionType = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TYPE);

        try {
            logic.createFeedbackSession(fs);
            
            try {
                createTemplateFeedbackQuestions(fs.courseId, fs.feedbackSessionName,
                                                fs.creatorEmail, feedbackSessionType);
            } catch(InvalidParametersException e) {
                //Failed to create feedback questions for specified template/feedback session type.
                //TODO: let the user know an error has occurred? delete the feedback session?
            }
            
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_ADDED, StatusMessageColor.SUCCESS));
            statusToAdmin =
                    "New Feedback Session <span class=\"bold\">(" + fs.feedbackSessionName + ")</span> for Course " +
                    "<span class=\"bold\">[" + fs.courseId + "]</span> created.<br>" +
                    "<span class=\"bold\">From:</span> " + fs.startTime +
                    "<span class=\"bold\"> to</span> " + fs.endTime + "<br>" +
                    "<span class=\"bold\">Session visible from:</span> " + fs.sessionVisibleFromTime + "<br>" +
                    "<span class=\"bold\">Results visible from:</span> " + fs.resultsVisibleFromTime + "<br><br>" +
                    "<span class=\"bold\">Instructions:</span> " + fs.instructions;
            
            //TODO: add a condition to include the status due to inconsistency problem of database 
            //      (similar to the one below)
            return createRedirectResult(
                    new PageData(account).getInstructorFeedbackEditLink(
                            fs.courseId, fs.feedbackSessionName));
            
        } catch (EntityAlreadyExistsException e) {
            setStatusForException(e, Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
        // isError == true if an exception occurred above

        boolean omitArchived = true;
        Map<String, InstructorAttributes> instructors = loadCourseInstructorMap(omitArchived);
        List<InstructorAttributes> instructorList = new ArrayList<InstructorAttributes>(instructors.values());
        List<CourseAttributes> courses = loadCoursesList(instructorList);
        List<FeedbackSessionAttributes> feedbackSessions = loadFeedbackSessionsList(instructorList);
        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(feedbackSessions);
        
        if (feedbackSessions.isEmpty()) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_ADD_DB_INCONSISTENCY, StatusMessageColor.WARNING));
        }
        
        Map<String, List<String>> courseIdToSectionName = logic.getCourseIdToSectionNamesMap(courses);
        
        data.initWithoutHighlightedRow(courses, courseId, feedbackSessions, instructors, fs, 
                                       feedbackSessionType, courseIdToSectionName);
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACKS, data);
    }
    
    private void createTemplateFeedbackQuestions(String courseId, String feedbackSessionName,
            String creatorEmail, String feedbackSessionType) throws InvalidParametersException {
        if (feedbackSessionType == null) {
            return;
        }
        
        List<FeedbackQuestionAttributes> questions = 
                FeedbackSessionTemplates.getFeedbackSessionTemplateQuestions(
                        feedbackSessionType, courseId, feedbackSessionName, creatorEmail);
        int questionNumber = 1;
        for (FeedbackQuestionAttributes fqa : questions){
            logic.createFeedbackQuestionForTemplate(fqa, questionNumber);
            questionNumber++;
        }
    }

    private FeedbackSessionAttributes extractFeedbackSessionData() {
        //TODO assert parameters are not null then update test
        //TODO make this method stateless
        
        FeedbackSessionAttributes newSession = new FeedbackSessionAttributes();
        newSession.courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        newSession.feedbackSessionName = Sanitizer.sanitizeTextField(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME));
        
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
            case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM:
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
        
        String[] sendReminderEmailsArray =
                getRequestParamValues(Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL);
        List<String> sendReminderEmailsList =
                sendReminderEmailsArray == null ? new ArrayList<String>()
                                                : Arrays.asList(sendReminderEmailsArray);
        newSession.isClosingEmailEnabled =
                sendReminderEmailsList.contains(EmailType.FEEDBACK_CLOSING.toString());
        newSession.isPublishedEmailEnabled =
                sendReminderEmailsList.contains(EmailType.FEEDBACK_PUBLISHED.toString());
        
        return newSession;
    }

}
