package teammates.ui.controller;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.FieldValidator;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.TimeHelper;

public abstract class InstructorFeedbackAbstractAction extends Action {

    private static final Logger log = Logger.getLogger();

    private LocalDateTime startTimeLocal;
    private LocalDateTime endTimeLocal;
    private LocalDateTime visibleTimeLocal;
    private LocalDateTime publishTimeLocal;

    /**
     * Creates a feedback session attributes object from the request parameters.
     * @param isCreatingNewSession true if creating a new session; false if editing an existing session.
     * @return feedback session attributes object.
     */
    protected FeedbackSessionAttributes extractFeedbackSessionData(boolean isCreatingNewSession) {
        // TODO: When creating a new session, assert parameters are not null.
        // Not necessary when editing an existing session as null values do not affect data integrity.

        String title = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        if (isCreatingNewSession) {
            title = SanitizationHelper.sanitizeTitle(title);
        }
        FeedbackSessionAttributes attributes = FeedbackSessionAttributes
                .builder(title, getRequestParamValue(Const.ParamsNames.COURSE_ID),
                        getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_CREATOR))
                .build();

        String paramTimeZone = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE, paramTimeZone);
        try {
            attributes.setTimeZone(ZoneId.of(paramTimeZone));
        } catch (DateTimeException e) {
            // Leave the attributes time zone field at its default valid value (i.e. UTC)
        }

        startTimeLocal = TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME));
        endTimeLocal = TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME));
        attributes.setStartTime(TimeHelper.convertLocalDateTimeToInstant(startTimeLocal, attributes.getTimeZone()));
        attributes.setEndTime(TimeHelper.convertLocalDateTimeToInstant(endTimeLocal, attributes.getTimeZone()));

        String paramGracePeriod = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD);
        try {
            attributes.setGracePeriodMinutes(Integer.parseInt(paramGracePeriod));
        } catch (NumberFormatException nfe) {
            log.warning("Failed to parse graced period parameter: " + paramGracePeriod);
        }

        if (isCreatingNewSession) {
            attributes.setCreatedTime(Instant.now());
            attributes.setSentOpenEmail(false);
            attributes.setSentPublishedEmail(false);
        }

        attributes.setFeedbackSessionType(FeedbackSessionType.STANDARD);
        attributes.setInstructions(new Text(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS)));

        String type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM:
            publishTimeLocal = TimeHelper.combineDateTime(
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE),
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME));
            attributes.setResultsVisibleFromTime(TimeHelper.convertLocalDateTimeToInstant(
                    publishTimeLocal, attributes.getTimeZone()));
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE:
            attributes.setResultsVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_VISIBLE);
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER:
            attributes.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
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
            visibleTimeLocal = TimeHelper.combineDateTime(
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE),
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME));
            attributes.setSessionVisibleFromTime(TimeHelper.convertLocalDateTimeToInstant(
                    visibleTimeLocal, attributes.getTimeZone()));
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN:
            attributes.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_NEVER:
            attributes.setSessionVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
            // Overwrite if private
            attributes.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
            attributes.setFeedbackSessionType(FeedbackSessionType.PRIVATE);
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

    protected void validateTimeData(FeedbackSessionAttributes attributes, boolean isCreatingNewSession)
            throws InvalidParametersException {
        FieldValidator validator = new FieldValidator();

        // Stop if invalid or fixed offset time zone is detected
        String paramTimeZone = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE);
        String timeZoneErrorMessage = validator.getInvalidityInfoForTimeZone(paramTimeZone);
        if (!timeZoneErrorMessage.isEmpty()) {
            // Collect other errors before throwing an exception
            List<String> errors = new ArrayList<>();
            // When editing an existing session, this will fail as some fields might be validly set to null
            if (isCreatingNewSession) {
                errors.addAll(attributes.getInvalidityInfo());
            }
            errors.add(timeZoneErrorMessage);
            throw new InvalidParametersException(errors);
        }

        // The time zone is valid at this point and can be used for future calculations
        ZoneId timeZone = attributes.getTimeZone();

        // Warn if ambiguity of time fields (brought about by DST) is detected
        validateLocalDateTimeUnambiguity(startTimeLocal, attributes.getStartTime(), timeZone,
                FieldValidator.SESSION_START_TIME_FIELD_NAME);
        validateLocalDateTimeUnambiguity(endTimeLocal, attributes.getEndTime(), timeZone,
                FieldValidator.SESSION_END_TIME_FIELD_NAME);
        validateLocalDateTimeUnambiguity(visibleTimeLocal, attributes.getSessionVisibleFromTime(), timeZone,
                FieldValidator.SESSION_VISIBLE_TIME_FIELD_NAME);
        validateLocalDateTimeUnambiguity(publishTimeLocal, attributes.getResultsVisibleFromTime(), timeZone,
                FieldValidator.RESULTS_VISIBLE_TIME_FIELD_NAME);
    }

    private void validateLocalDateTimeUnambiguity(LocalDateTime dateTime, Instant resolved, ZoneId zone, String fieldName) {
        if (dateTime == null || resolved == null || zone == null) {
            return;
        }

        if (!TimeHelper.isLocalDateTimeUnambiguousAtZone(dateTime, zone)) {
            String warningText = String.format(Const.StatusMessages.AMBIGUOUS_LOCAL_DATE_TIME, fieldName,
                    TimeHelper.formatTime12H(dateTime), TimeHelper.formatDateTimeForDisambiguation(resolved, zone));
            statusToUser.add(new StatusMessage(warningText, StatusMessageColor.WARNING));
        }
    }

    protected List<FeedbackSessionAttributes> loadFeedbackSessionsList(
            List<InstructorAttributes> instructorList) {
        return logic.getFeedbackSessionsListForInstructor(instructorList);
    }

    protected List<CourseAttributes> loadCoursesList(List<InstructorAttributes> instructorList) {

        List<CourseAttributes> courses = logic.getCoursesForInstructor(instructorList);

        courses.sort(Comparator.comparing(course -> course.getId()));

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
