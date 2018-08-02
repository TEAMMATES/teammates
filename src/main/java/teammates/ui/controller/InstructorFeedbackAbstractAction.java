package teammates.ui.controller;

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

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidPostParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.FieldValidator;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.TimeHelper;

public abstract class InstructorFeedbackAbstractAction extends Action {

    protected LocalDateTime inputStartTimeLocal;
    protected LocalDateTime inputEndTimeLocal;
    protected LocalDateTime inputVisibleTimeLocal;
    protected LocalDateTime inputPublishTimeLocal;

    /**
     * Creates a feedback session attributes object from the request parameters.
     * The created time is always set to now, and the opening email enabled flag is always set to true.
     * @param fsName the name of the feedback session (should be sanitized when creating a new session).
     * @param course the course the feedback session is in.
     * @param creatorEmail the email address of the feedback session's creator.
     * @return feedback session attributes object.
     * @throws InvalidPostParametersException if any of the request parameters are not in the expected format.
     */
    protected FeedbackSessionAttributes extractFeedbackSessionData(
            String fsName, CourseAttributes course, String creatorEmail) {
        Assumption.assertNotNull(fsName);
        Assumption.assertNotNull(course);
        Assumption.assertNotNull(creatorEmail);

        FeedbackSessionAttributes attributes = FeedbackSessionAttributes.builder(fsName, course.getId(), creatorEmail)
                // For existing sessions, creation time will be overwritten to its existing value at the logic layer
                .withCreatedTime(Instant.now())
                .withTimeZone(course.getTimeZone())
                .build();

        inputStartTimeLocal = TimeHelper.parseDateTimeFromSessionsForm(
                getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE),
                getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME));
        inputEndTimeLocal = TimeHelper.parseDateTimeFromSessionsForm(
                getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE),
                getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME));
        attributes.setStartTime(TimeHelper.convertLocalDateTimeToInstant(inputStartTimeLocal, attributes.getTimeZone()));
        attributes.setEndTime(TimeHelper.convertLocalDateTimeToInstant(inputEndTimeLocal, attributes.getTimeZone()));

        String paramGracePeriod = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD);
        try {
            attributes.setGracePeriodMinutes(Integer.parseInt(paramGracePeriod));
        } catch (NumberFormatException nfe) {
            throw new InvalidPostParametersException("Failed to parse grace period parameter: " + paramGracePeriod, nfe);
        }

        attributes.setInstructions(new Text(getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS)));

        String type = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM:
            inputPublishTimeLocal = TimeHelper.parseDateTimeFromSessionsForm(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE),
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME));
            attributes.setResultsVisibleFromTime(TimeHelper.convertLocalDateTimeToInstant(
                    inputPublishTimeLocal, attributes.getTimeZone()));
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE:
            attributes.setResultsVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_VISIBLE);
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER:
            attributes.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
            break;
        default:
            throw new InvalidPostParametersException("Invalid resultsVisibleFrom setting: " + type);
        }

        type = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM:
            inputVisibleTimeLocal = TimeHelper.parseDateTimeFromSessionsForm(
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE),
                    getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME));
            attributes.setSessionVisibleFromTime(TimeHelper.convertLocalDateTimeToInstant(
                    inputVisibleTimeLocal, attributes.getTimeZone()));
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN:
            attributes.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
            break;
        default:
            throw new InvalidPostParametersException("Invalid sessionVisibleFrom setting: " + type);
        }

        String[] sendReminderEmailsArray = getRequestParamValues(Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL);
        List<String> sendReminderEmailsList = sendReminderEmailsArray == null ? new ArrayList<>()
                : Arrays.asList(sendReminderEmailsArray);
        attributes.setClosingEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_CLOSING.toString()));
        attributes.setPublishedEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_PUBLISHED.toString()));
        // A session opening reminder email is always sent as students
        // without accounts need to receive the email to be able to respond
        attributes.setOpeningEmailEnabled(true);

        return attributes;
    }

    protected void validateTimeData(FeedbackSessionAttributes attributes) {
        ZoneId timeZone = attributes.getTimeZone();

        // Warn if ambiguity of time fields (brought about by DST) is detected
        validateLocalDateTimeUnambiguity(inputStartTimeLocal, attributes.getStartTime(), timeZone,
                FieldValidator.SESSION_START_TIME_FIELD_NAME);
        validateLocalDateTimeUnambiguity(inputEndTimeLocal, attributes.getEndTime(), timeZone,
                FieldValidator.SESSION_END_TIME_FIELD_NAME);
        validateLocalDateTimeUnambiguity(inputVisibleTimeLocal, attributes.getSessionVisibleFromTime(), timeZone,
                FieldValidator.SESSION_VISIBLE_TIME_FIELD_NAME);
        validateLocalDateTimeUnambiguity(inputPublishTimeLocal, attributes.getResultsVisibleFromTime(), timeZone,
                FieldValidator.RESULTS_VISIBLE_TIME_FIELD_NAME);
    }

    private void validateLocalDateTimeUnambiguity(LocalDateTime dateTime, Instant resolved, ZoneId zone, String fieldName) {
        if (dateTime == null || resolved == null || zone == null) {
            return;
        }

        switch(TimeHelper.LocalDateTimeAmbiguityStatus.of(dateTime, zone)) {
        case UNAMBIGUOUS:
            return;
        case GAP:
            String gapWarningText = String.format(Const.StatusMessages.AMBIGUOUS_LOCAL_DATE_TIME_GAP, fieldName,
                    TimeHelper.formatDateTimeForDisplay(dateTime),
                    TimeHelper.formatDateTimeForDisplayFull(resolved, zone));
            statusToUser.add(new StatusMessage(gapWarningText, StatusMessageColor.WARNING));
            break;
        case OVERLAP:
            Instant earlierInterpretation = dateTime.atZone(zone).withEarlierOffsetAtOverlap().toInstant();
            Instant laterInterpretation = dateTime.atZone(zone).withLaterOffsetAtOverlap().toInstant();
            String overlapWarningText = String.format(Const.StatusMessages.AMBIGUOUS_LOCAL_DATE_TIME_OVERLAP, fieldName,
                    TimeHelper.formatDateTimeForDisplay(dateTime),
                    TimeHelper.formatDateTimeForDisplayFull(earlierInterpretation, zone),
                    TimeHelper.formatDateTimeForDisplayFull(laterInterpretation, zone),
                    TimeHelper.formatDateTimeForDisplayFull(resolved, zone));
            statusToUser.add(new StatusMessage(overlapWarningText, StatusMessageColor.WARNING));
            break;
        default:
        }
    }

    protected List<FeedbackSessionAttributes> loadFeedbackSessionsList(
            List<InstructorAttributes> instructorList) {
        return logic.getFeedbackSessionsListForInstructor(instructorList);
    }

    protected List<FeedbackSessionAttributes> loadSoftDeletedFeedbackSessionsList(
            List<InstructorAttributes> instructorList) {
        return logic.getSoftDeletedFeedbackSessionsListForInstructors(instructorList);
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
