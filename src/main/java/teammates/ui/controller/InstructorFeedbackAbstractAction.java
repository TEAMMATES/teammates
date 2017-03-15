package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;

public abstract class InstructorFeedbackAbstractAction extends Action {

    protected abstract void setUniqueAttributesForSession(
            FeedbackSessionAttributes newSession, List<String> sendReminderEmailsList);

    protected FeedbackSessionAttributes extractFeedbackSessionData() {
        // TODO assert parameters are not null then update test
        // TODO make this method stateless

        FeedbackSessionAttributes newSession = new FeedbackSessionAttributes();
        newSession.setCourseId(getRequestParamValue(Const.ParamsNames.COURSE_ID));
        newSession.setFeedbackSessionName(SanitizationHelper.sanitizeTitle(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME)));
        newSession.setFeedbackSessionType(FeedbackSessionType.STANDARD);
        setTime(newSession);

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
            log.severe("Invalid resultsVisibleFrom setting in creating"
                       + newSession.getIdentificationString());
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
            newSession.setFeedbackSessionType(FeedbackSessionType.PRIVATE);
            break;
        default:
            log.severe("Invalid sessionVisibleFrom setting in creating "
                       + newSession.getIdentificationString());
            break;
        }

        String[] sendReminderEmailsArray =
                getRequestParamValues(Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL);
        List<String> sendReminderEmailsList =
                sendReminderEmailsArray == null ? new ArrayList<String>()
                                                : Arrays.asList(sendReminderEmailsArray);
        newSession.setClosingEmailEnabled(
                sendReminderEmailsList.contains(EmailType.FEEDBACK_CLOSING.toString()));
        newSession.setPublishedEmailEnabled(
                sendReminderEmailsList.contains(EmailType.FEEDBACK_PUBLISHED.toString()));
        this.setUniqueAttributesForSession(newSession, sendReminderEmailsList);
        return newSession;
    }

    private void setTime(FeedbackSessionAttributes newSession) {
        newSession.setStartTime(TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME)));
        newSession.setEndTime(TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME)));
    }

    protected List<FeedbackSessionAttributes> loadFeedbackSessionsList(
            List<InstructorAttributes> instructorList) {

        List<FeedbackSessionAttributes> sessions =
                logic.getFeedbackSessionsListForInstructor(instructorList);
        return sessions;
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
    protected Map<String, InstructorAttributes> loadCourseInstructorMap(boolean omitArchived) {
        Map<String, InstructorAttributes> courseInstructorMap = new HashMap<String, InstructorAttributes>();
        List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(account.googleId,
                                                                                 omitArchived);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }
        return courseInstructorMap;
    }
    
}