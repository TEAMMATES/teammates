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
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;

public abstract class InstructorFeedbackAbstractAction extends Action {

    private static final Logger log = Logger.getLogger();

    /**
     * Creates a feedback session attributes object from the request parameters.
     * @param isCreatingNewSession true if creating a new session; false if editing an existing session.
     * @return feedback session attributes object.
     */
    protected FeedbackSessionAttributes extractFeedbackSessionData(boolean isCreatingNewSession) {
        // TODO: When creating a new session, assert parameters are not null.
        // Not necessary when editing an existing session as null values do not affect data integrity.

        FeedbackSessionAttributes attributes = new FeedbackSessionAttributes();
        attributes.setCourseId(getRequestParamValue(Const.ParamsNames.COURSE_ID));

        String title = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        if (isCreatingNewSession) {
            title = SanitizationHelper.sanitizeTitle(title);
        }
        attributes.setFeedbackSessionName(title);
        attributes.setCreatorEmail(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_CREATOR));

        attributes.setStartTime(TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME)));
        attributes.setEndTime(TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME)));
        String paramTimeZone = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE);
        if (paramTimeZone != null) {
            try {
                attributes.setTimeZone(Double.parseDouble(paramTimeZone));
            } catch (NumberFormatException nfe) {
                log.warning("Failed to parse time zone parameter: " + paramTimeZone);
            }
        }

        String paramGracePeriod = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD);
        try {
            attributes.setGracePeriod(Integer.parseInt(paramGracePeriod));
        } catch (NumberFormatException nfe) {
            log.warning("Failed to parse graced period parameter: " + paramGracePeriod);
        }

        if (isCreatingNewSession) {
            attributes.setCreatedTime(new Date());
            attributes.setSentOpenEmail(false);
            attributes.setSentPublishedEmail(false);
        }

        attributes.setFeedbackSessionType(FeedbackSessionType.STANDARD);
        attributes.setInstructions(new Text(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS)));

        String type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM:
            attributes.setResultsVisibleFromTime(TimeHelper.combineDateTime(
                        getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE),
                        getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME)));
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE:
            attributes.setResultsVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_VISIBLE);
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER:
            attributes.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_NEVER:
            attributes.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
            break;
        default:
            log.severe("Invalid sessionVisibleFrom setting " + attributes.getIdentificationString());
            break;
        }

        // Handle session visible after results visible to avoid having a
        // results visible date when session is private (session not visible)
        type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM:
            attributes.setSessionVisibleFromTime(TimeHelper.combineDateTime(
                        getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE),
                        getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME)));
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN:
            attributes.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_NEVER:
            attributes.setSessionVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
            // Overwrite if private
            attributes.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
            attributes.setFeedbackSessionType(FeedbackSessionType.PRIVATE);
            if (!isCreatingNewSession) {
                attributes.setEndTime(null);
            }
            break;
        default:
            log.severe("Invalid sessionVisibleFrom setting " + attributes.getIdentificationString());
            break;
        }

        String[] sendReminderEmailsArray = getRequestParamValues(Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL);
        List<String> sendReminderEmailsList = sendReminderEmailsArray == null ? new ArrayList<String>()
                : Arrays.asList(sendReminderEmailsArray);
        attributes.setOpeningEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_OPENING.toString()));
        attributes.setClosingEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_CLOSING.toString()));
        attributes.setPublishedEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_PUBLISHED.toString()));

        return attributes;
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
    protected Map<String, InstructorAttributes> loadCourseInstructorMap(boolean omitArchived) {
        HashMap<String, InstructorAttributes> courseInstructorMap = new HashMap<>();
        List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(account.googleId, omitArchived);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }
        return courseInstructorMap;
    }

}
