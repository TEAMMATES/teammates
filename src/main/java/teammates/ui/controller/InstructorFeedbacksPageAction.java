package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.TimeHelper;
import teammates.ui.pagedata.InstructorFeedbacksPageData;

public class InstructorFeedbacksPageAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        // This can be null. Non-null value indicates the page is being loaded
        // to add a feedback to the specified course
        String courseIdForNewSession = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionToHighlight = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String isUsingAjax = getRequestParamValue(Const.ParamsNames.IS_USING_AJAX);

        gateKeeper.verifyInstructorPrivileges(account);

        if (courseIdForNewSession != null) {
            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(courseIdForNewSession, account.googleId),
                    logic.getCourse(courseIdForNewSession),
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        }

        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(account);
        data.setUsingAjax(isUsingAjax != null);

        boolean omitArchived = true; // TODO: implement as a request parameter
        // HashMap with courseId as key and InstructorAttributes as value
        Map<String, InstructorAttributes> instructors = loadCourseInstructorMap(omitArchived);

        List<InstructorAttributes> instructorList =
                new ArrayList<InstructorAttributes>(instructors.values());
        List<CourseAttributes> courses = loadCoursesList(instructorList);

        List<FeedbackSessionAttributes> existingFeedbackSessions;
        if (courses.isEmpty() || !data.isUsingAjax()) {
            existingFeedbackSessions = new ArrayList<FeedbackSessionAttributes>();
        } else {
            existingFeedbackSessions = loadFeedbackSessionsList(instructorList);
            if (existingFeedbackSessions.isEmpty()) {
                statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_EMPTY, StatusMessageColor.WARNING));
            }
        }

        if (courses.isEmpty()) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_EMPTY_IN_INSTRUCTOR_FEEDBACKS
                    .replace("${user}", "?user=" + account.googleId),
                    StatusMessageColor.WARNING));
        }

        statusToAdmin = "Number of feedback sessions: " + existingFeedbackSessions.size();

        data.initWithoutDefaultFormValues(courses, courseIdForNewSession, existingFeedbackSessions,
                instructors, feedbackSessionToHighlight);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACKS, data);
    }

    protected List<FeedbackSessionAttributes> loadFeedbackSessionsList(
            List<InstructorAttributes> instructorList) {
        return logic.getFeedbackSessionsListForInstructor(instructorList);
    }

    protected List<CourseAttributes> loadCoursesList(List<InstructorAttributes> instructorList) {

        List<CourseAttributes> courses = logic.getCoursesForInstructor(instructorList);

        Collections.sort(courses, new Comparator<CourseAttributes>() {
            @Override
            public int compare(CourseAttributes c1, CourseAttributes c2) {
                return c1.getId().compareTo(c2.getId());
            }
        });

        return courses;
    }

    /**
     * Gets a Map with courseId as key, and InstructorAttributes as value.
     */
    protected HashMap<String, InstructorAttributes> loadCourseInstructorMap(boolean omitArchived) {
        HashMap<String, InstructorAttributes> courseInstructorMap = new HashMap<String, InstructorAttributes>();
        List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(account.googleId, omitArchived);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }
        return courseInstructorMap;
    }

    protected FeedbackSessionAttributes extractFeedbackSessionData(boolean isCreating) {
        //TODO make this method stateless

        // null checks for parameters not done as null values do not affect data integrity

        FeedbackSessionAttributes newSession = new FeedbackSessionAttributes();
        newSession.setCourseId(getRequestParamValue(Const.ParamsNames.COURSE_ID));

        //Default title.. If the class is InstructorFeedbackAddAction then sanitze the title
        String title = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        if (isCreating) {
            title = SanitizationHelper.sanitizeTitle(title);
        }

        newSession.setFeedbackSessionName(title);
        newSession.setCreatorEmail(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_CREATOR));

        newSession.setStartTime(TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME)));
        newSession.setEndTime(TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME)));
        String paramTimeZone = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE);
        if (paramTimeZone != null) {
            try {
                newSession.setTimeZone(Double.parseDouble(paramTimeZone));
            } catch (NumberFormatException nfe) {
                log.warning("Failed to parse time zone parameter: " + paramTimeZone);
            }
        }

        String paramGracePeriod = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD);
        try {
            newSession.setGracePeriod(Integer.parseInt(paramGracePeriod));
        } catch (NumberFormatException nfe) {
            log.warning("Failed to parse graced period parameter: " + paramGracePeriod);
        }
        //Only run if its not editing
        if (isCreating) {
            newSession.setCreatedTime(new Date());
            newSession.setSentOpenEmail(false);
            newSession.setSentPublishedEmail(false);
        }

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
            log.severe("Invalid sessionVisibleFrom setting " + newSession.getIdentificationString());
            break;
        }

        // handle session visible after results visible to avoid having a
        // results visible date when session is private (session not visible)
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
            newSession.setEndTime(null);
            newSession.setFeedbackSessionType(FeedbackSessionType.PRIVATE);
            break;
        default:
            log.severe("Invalid sessionVisibleFrom setting " + newSession.getIdentificationString());
            break;
        }

        String[] sendReminderEmailsArray = getRequestParamValues(Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL);
        List<String> sendReminderEmailsList = sendReminderEmailsArray == null ? new ArrayList<String>()
                : Arrays.asList(sendReminderEmailsArray);
        newSession.setOpeningEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_OPENING.toString()));
        newSession.setClosingEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_CLOSING.toString()));
        newSession.setPublishedEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_PUBLISHED.toString()));

        return newSession;
    }
}

