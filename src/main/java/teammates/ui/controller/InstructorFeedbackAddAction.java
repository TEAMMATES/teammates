package teammates.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Text;
import com.google.gson.reflect.TypeToken;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackSessionTemplates;
import teammates.common.util.TimeHelper;
import teammates.ui.pagedata.InstructorFeedbacksPageData;

public class InstructorFeedbackAddAction extends InstructorFeedbacksPageAction {

    private static final Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertNotEmpty(courseId);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);

        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        FeedbackSessionAttributes fs = extractFeedbackSessionData();

        // Set creator email as instructors' email
        fs.setCreatorEmail(instructor.email);

        // A session opening reminder email is always sent as students
        // without accounts need to receive the email to be able to respond
        fs.setOpeningEmailEnabled(true);

        String feedbackSessionType = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TYPE);

        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(account);
        try {
            logic.createFeedbackSession(fs);

            try {
                createTemplateFeedbackQuestions(fs.getCourseId(), fs.getFeedbackSessionName(),
                                                fs.getCreatorEmail(), feedbackSessionType);
            } catch (InvalidParametersException e) {
                //Failed to create feedback questions for specified template/feedback session type.
                //TODO: let the user know an error has occurred? delete the feedback session?
                log.severe(TeammatesException.toStringWithStackTrace(e));
            }

            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_ADDED, StatusMessageColor.SUCCESS));
            statusToAdmin =
                    "New Feedback Session <span class=\"bold\">(" + fs.getFeedbackSessionName() + ")</span> for Course "
                    + "<span class=\"bold\">[" + fs.getCourseId() + "]</span> created.<br>"
                    + "<span class=\"bold\">From:</span> " + fs.getStartTime()
                    + "<span class=\"bold\"> to</span> " + fs.getEndTime() + "<br>"
                    + "<span class=\"bold\">Session visible from:</span> " + fs.getSessionVisibleFromTime() + "<br>"
                    + "<span class=\"bold\">Results visible from:</span> " + fs.getResultsVisibleFromTime() + "<br><br>"
                    + "<span class=\"bold\">Instructions:</span> " + fs.getInstructions();

            //TODO: add a condition to include the status due to inconsistency problem of database
            //      (similar to the one below)
            return createRedirectResult(
                    data.getInstructorFeedbackEditLink(
                            fs.getCourseId(), fs.getFeedbackSessionName()));

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
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_ADD_DB_INCONSISTENCY,
                                               StatusMessageColor.WARNING));
        }

        data.initWithoutHighlightedRow(courses, courseId, feedbackSessions, instructors, fs,
                                       feedbackSessionType);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACKS, data);
    }

    private void createTemplateFeedbackQuestions(String courseId, String feedbackSessionName,
            String creatorEmail, String feedbackSessionType) throws InvalidParametersException {
        if (feedbackSessionType == null) {
            return;
        }

        List<FeedbackQuestionAttributes> questions =
                getFeedbackSessionTemplateQuestions(feedbackSessionType, courseId, feedbackSessionName, creatorEmail);

        int questionNumber = 1;
        for (FeedbackQuestionAttributes fqa : questions) {
            logic.createFeedbackQuestionForTemplate(fqa, questionNumber);
            questionNumber++;
        }
    }

    /**
     * Gets the list of questions for the specified feedback session template.
     */
    private static List<FeedbackQuestionAttributes> getFeedbackSessionTemplateQuestions(
            String templateType, String courseId, String feedbackSessionName, String creatorEmail) {
        Assumption.assertNotNull(templateType);

        if ("TEAMEVALUATION".equals(templateType)) {
            String jsonString = Templates.populateTemplate(FeedbackSessionTemplates.TEAM_EVALUATION,
                    "${courseId}", courseId,
                    "${feedbackSessionName}", feedbackSessionName,
                    "${creatorEmail}", creatorEmail);

            Type listType = new TypeToken<ArrayList<FeedbackQuestionAttributes>>(){}.getType();
            return JsonUtils.fromJson(jsonString, listType);
        }

        return new ArrayList<FeedbackQuestionAttributes>();
    }

    private FeedbackSessionAttributes extractFeedbackSessionData() {
        //TODO assert parameters are not null then update test
        //TODO make this method stateless

        FeedbackSessionAttributes newSession = new FeedbackSessionAttributes();
        newSession.setCourseId(getRequestParamValue(Const.ParamsNames.COURSE_ID));
        newSession.setFeedbackSessionName(SanitizationHelper.sanitizeTitle(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME)));

        newSession.setCreatedTime(new Date());
        newSession.setStartTime(TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME)));
        newSession.setEndTime(TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME)));
        String paramTimeZone = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE);
        if (paramTimeZone != null) {
            newSession.setTimeZone(Double.parseDouble(paramTimeZone));
        }
        String paramGracePeriod = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD);
        if (paramGracePeriod != null) {
            newSession.setGracePeriod(Integer.parseInt(paramGracePeriod));
        }

        newSession.setSentOpenEmail(false);
        newSession.setSentPublishedEmail(false);

        newSession.setFeedbackSessionType(FeedbackSessionType.STANDARD);
        newSession.setInstructions(new Text(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS)));

        String type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM:
            newSession.setResultsVisibleFromTime(TimeHelper.combineDateTime(
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE),
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME)));
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE:
            newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_VISIBLE);
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER:
            newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_NEVER:
            newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
            break;
        default:
            log.severe("Invalid resultsVisibleFrom setting in creating" + newSession.getIdentificationString());
            break;
        }

        type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM:
            newSession.setSessionVisibleFromTime(TimeHelper.combineDateTime(
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE),
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME)));
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN:
            newSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_NEVER:
            newSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
            // overwrite if private
            newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
            newSession.setFeedbackSessionType(FeedbackSessionType.PRIVATE);
            break;
        default:
            log.severe("Invalid sessionVisibleFrom setting in creating " + newSession.getIdentificationString());
            break;
        }

        String[] sendReminderEmailsArray =
                getRequestParamValues(Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL);
        List<String> sendReminderEmailsList =
                sendReminderEmailsArray == null ? new ArrayList<String>()
                                                : Arrays.asList(sendReminderEmailsArray);
        newSession.setClosingEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_CLOSING.toString()));
        newSession.setPublishedEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_PUBLISHED.toString()));

        return newSession;
    }

}
