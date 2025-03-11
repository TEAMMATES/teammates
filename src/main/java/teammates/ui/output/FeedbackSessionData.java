package teammates.ui.output;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * The API output format of {@link FeedbackSessionAttributes}.
 */
public class FeedbackSessionData extends ApiOutput {

    @Nullable
    private final UUID feedbackSessionId;

    private final String courseId;
    private final String timeZone;
    private final String feedbackSessionName;
    private final String instructions;

    private final Long submissionStartTimestamp;
    private final Long submissionEndTimestamp;
    @Nullable
    private Long submissionEndWithExtensionTimestamp;
    @Nullable
    private Long sessionVisibleFromTimestamp;
    @Nullable
    private Long resultVisibleFromTimestamp;
    private Long gracePeriod;

    private SessionVisibleSetting sessionVisibleSetting;
    @Nullable
    private Long customSessionVisibleTimestamp;

    private ResponseVisibleSetting responseVisibleSetting;
    @Nullable
    private Long customResponseVisibleTimestamp;

    private FeedbackSessionSubmissionStatus submissionStatus;
    private FeedbackSessionPublishStatus publishStatus;

    private Boolean isClosingSoonEmailEnabled;
    private Boolean isPublishedEmailEnabled;

    private long createdAtTimestamp;
    @Nullable
    private final Long deletedAtTimestamp;
    @Nullable
    private InstructorPermissionSet privileges;

    private Map<String, Long> studentDeadlines;
    private Map<String, Long> instructorDeadlines;

    public FeedbackSessionData(FeedbackSessionAttributes feedbackSessionAttributes) {
        String timeZone = feedbackSessionAttributes.getTimeZone();
        this.feedbackSessionId = null;
        this.courseId = feedbackSessionAttributes.getCourseId();
        this.timeZone = timeZone;
        this.feedbackSessionName = feedbackSessionAttributes.getFeedbackSessionName();
        this.instructions = feedbackSessionAttributes.getInstructions();
        this.submissionStartTimestamp = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                feedbackSessionAttributes.getStartTime(), timeZone, true).toEpochMilli();
        this.submissionEndTimestamp = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                feedbackSessionAttributes.getEndTime(), timeZone, true).toEpochMilli();
        this.submissionEndWithExtensionTimestamp = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                feedbackSessionAttributes.getDeadline(), timeZone, true).toEpochMilli();
        this.gracePeriod = feedbackSessionAttributes.getGracePeriodMinutes();

        Instant sessionVisibleTime = feedbackSessionAttributes.getSessionVisibleFromTime();
        this.sessionVisibleFromTimestamp = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                sessionVisibleTime, timeZone, true).toEpochMilli();
        if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            this.sessionVisibleSetting = SessionVisibleSetting.AT_OPEN;
        } else {
            this.sessionVisibleSetting = SessionVisibleSetting.CUSTOM;
            this.customSessionVisibleTimestamp = this.sessionVisibleFromTimestamp;
        }

        Instant responseVisibleTime = feedbackSessionAttributes.getResultsVisibleFromTime();
        this.resultVisibleFromTimestamp = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                responseVisibleTime, timeZone, true).toEpochMilli();
        if (responseVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            this.responseVisibleSetting = ResponseVisibleSetting.AT_VISIBLE;
        } else if (responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
            this.responseVisibleSetting = ResponseVisibleSetting.LATER;
        } else {
            this.responseVisibleSetting = ResponseVisibleSetting.CUSTOM;
            this.customResponseVisibleTimestamp = this.resultVisibleFromTimestamp;
        }

        if (!feedbackSessionAttributes.isVisible()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.NOT_VISIBLE;
        }
        if (feedbackSessionAttributes.isVisible() && !feedbackSessionAttributes.isOpened()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN;
        }
        if (feedbackSessionAttributes.isOpened()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.OPEN;
        }
        if (feedbackSessionAttributes.isInGracePeriod()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.GRACE_PERIOD;
        }
        if (feedbackSessionAttributes.isClosed()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.CLOSED;
        }

        if (feedbackSessionAttributes.isPublished()) {
            this.publishStatus = FeedbackSessionPublishStatus.PUBLISHED;
        } else {
            this.publishStatus = FeedbackSessionPublishStatus.NOT_PUBLISHED;
        }

        this.isClosingSoonEmailEnabled = feedbackSessionAttributes.isClosingSoonEmailEnabled();
        this.isPublishedEmailEnabled = feedbackSessionAttributes.isPublishedEmailEnabled();

        this.createdAtTimestamp = feedbackSessionAttributes.getCreatedTime().toEpochMilli();
        if (feedbackSessionAttributes.getDeletedTime() == null) {
            this.deletedAtTimestamp = null;
        } else {
            this.deletedAtTimestamp = feedbackSessionAttributes.getDeletedTime().toEpochMilli();
        }

        String userEmail = feedbackSessionAttributes.getUserEmail();
        this.studentDeadlines = feedbackSessionAttributes.getStudentDeadlines()
                .entrySet()
                .stream()
                .filter(entry -> userEmail == null || userEmail.equals(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        TimeHelper.getMidnightAdjustedInstantBasedOnZone(entry.getValue(), timeZone, true)
                                .toEpochMilli()));

        this.instructorDeadlines = feedbackSessionAttributes.getInstructorDeadlines()
                .entrySet()
                .stream()
                .filter(entry -> userEmail == null || userEmail.equals(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        TimeHelper.getMidnightAdjustedInstantBasedOnZone(entry.getValue(), timeZone, true)
                                .toEpochMilli()));
    }

    public FeedbackSessionData(FeedbackSession feedbackSession) {
        assert feedbackSession != null;
        assert feedbackSession.getCourse() != null;
        String timeZone = feedbackSession.getCourse().getTimeZone();
        this.feedbackSessionId = feedbackSession.getId();
        this.courseId = feedbackSession.getCourse().getId();
        this.timeZone = timeZone;
        this.feedbackSessionName = feedbackSession.getName();
        this.instructions = feedbackSession.getInstructions();
        this.submissionStartTimestamp = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                feedbackSession.getStartTime(), timeZone, true).toEpochMilli();
        this.submissionEndTimestamp = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                feedbackSession.getEndTime(), timeZone, true).toEpochMilli();
        // If no deadline extension time is provided, then the end time with extension is assumed to be
        // just the end time.
        this.submissionEndWithExtensionTimestamp = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
            feedbackSession.getEndTime(), timeZone, true).toEpochMilli();
        this.gracePeriod = feedbackSession.getGracePeriod().toMinutes();

        Instant sessionVisibleTime = feedbackSession.getSessionVisibleFromTime();
        this.sessionVisibleFromTimestamp = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                sessionVisibleTime, timeZone, true).toEpochMilli();
        if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            this.sessionVisibleSetting = SessionVisibleSetting.AT_OPEN;
        } else {
            this.sessionVisibleSetting = SessionVisibleSetting.CUSTOM;
            this.customSessionVisibleTimestamp = this.sessionVisibleFromTimestamp;
        }

        Instant responseVisibleTime = feedbackSession.getResultsVisibleFromTime();
        this.resultVisibleFromTimestamp = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                responseVisibleTime, timeZone, true).toEpochMilli();
        if (responseVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            this.responseVisibleSetting = ResponseVisibleSetting.AT_VISIBLE;
        } else if (responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
            this.responseVisibleSetting = ResponseVisibleSetting.LATER;
        } else {
            this.responseVisibleSetting = ResponseVisibleSetting.CUSTOM;
            this.customResponseVisibleTimestamp = this.resultVisibleFromTimestamp;
        }

        if (!feedbackSession.isVisible()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.NOT_VISIBLE;
        } else if (feedbackSession.isVisible() && !feedbackSession.isOpened()
                && !feedbackSession.isClosed()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN;
        } else if (feedbackSession.isInGracePeriod()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.GRACE_PERIOD;
        } else if (feedbackSession.isOpened()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.OPEN;
        } else if (feedbackSession.isClosed()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.CLOSED;
        }

        if (feedbackSession.isPublished()) {
            this.publishStatus = FeedbackSessionPublishStatus.PUBLISHED;
        } else {
            this.publishStatus = FeedbackSessionPublishStatus.NOT_PUBLISHED;
        }

        this.isClosingSoonEmailEnabled = feedbackSession.isClosingSoonEmailEnabled();
        this.isPublishedEmailEnabled = feedbackSession.isPublishedEmailEnabled();

        this.createdAtTimestamp = feedbackSession.getCreatedAt().toEpochMilli();
        if (feedbackSession.getDeletedAt() == null) {
            this.deletedAtTimestamp = null;
        } else {
            this.deletedAtTimestamp = feedbackSession.getDeletedAt().toEpochMilli();
        }

        this.studentDeadlines = new HashMap<>();
        this.instructorDeadlines = new HashMap<>();

        // place deadline extensions into appropriate student and instructor deadline maps
        for (DeadlineExtension de : feedbackSession.getDeadlineExtensions()) {
            if (de.getUser() instanceof Student) {
                this.studentDeadlines.put(de.getUser().getEmail(),
                        TimeHelper.getMidnightAdjustedInstantBasedOnZone(de.getEndTime(), timeZone, true).toEpochMilli());
            }
            if (de.getUser() instanceof Instructor) {
                this.instructorDeadlines.put(de.getUser().getEmail(),
                        TimeHelper.getMidnightAdjustedInstantBasedOnZone(de.getEndTime(), timeZone, true).toEpochMilli());
            }
        }
    }

    /**
     * Constructs FeedbackSessionData for a given user deadline.
     */
    public FeedbackSessionData(FeedbackSession feedbackSession, Instant extendedDeadline) {
        this(feedbackSession);

        this.submissionEndWithExtensionTimestamp = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
            extendedDeadline, timeZone, true).toEpochMilli();

        if (!feedbackSession.isVisible()) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.NOT_VISIBLE;
        } else if (feedbackSession.isVisible() && !feedbackSession.isOpenedGivenExtendedDeadline(extendedDeadline)
                && !feedbackSession.isClosedGivenExtendedDeadline(extendedDeadline)) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN;
        } else if (feedbackSession.isInGracePeriodGivenExtendedDeadline(extendedDeadline)) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.GRACE_PERIOD;
        } else if (feedbackSession.isOpenedGivenExtendedDeadline(extendedDeadline)) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.OPEN;
        } else if (feedbackSession.isClosedGivenExtendedDeadline(extendedDeadline)) {
            this.submissionStatus = FeedbackSessionSubmissionStatus.CLOSED;
        }
    }

    public UUID getFeedbackSessionId() {
        return feedbackSessionId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public String getInstructions() {
        return instructions;
    }

    public long getSubmissionStartTimestamp() {
        return submissionStartTimestamp;
    }

    public long getSubmissionEndTimestamp() {
        return submissionEndTimestamp;
    }

    public long getSubmissionEndWithExtensionTimestamp() {
        return submissionEndWithExtensionTimestamp;
    }

    public Long getSessionVisibleFromTimestamp() {
        return sessionVisibleFromTimestamp;
    }

    public Long getResultVisibleFromTimestamp() {
        return resultVisibleFromTimestamp;
    }

    public Long getGracePeriod() {
        return gracePeriod;
    }

    public SessionVisibleSetting getSessionVisibleSetting() {
        return sessionVisibleSetting;
    }

    public Long getCustomSessionVisibleTimestamp() {
        return customSessionVisibleTimestamp;
    }

    public ResponseVisibleSetting getResponseVisibleSetting() {
        return responseVisibleSetting;
    }

    public Long getCustomResponseVisibleTimestamp() {
        return customResponseVisibleTimestamp;
    }

    public FeedbackSessionSubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    public FeedbackSessionPublishStatus getPublishStatus() {
        return publishStatus;
    }

    public Boolean getIsClosingSoonEmailEnabled() {
        return isClosingSoonEmailEnabled;
    }

    public Boolean getIsPublishedEmailEnabled() {
        return isPublishedEmailEnabled;
    }

    public Map<String, Long> getStudentDeadlines() {
        return studentDeadlines;
    }

    public Map<String, Long> getInstructorDeadlines() {
        return instructorDeadlines;
    }

    public void setSessionVisibleFromTimestamp(Long sessionVisibleFromTimestamp) {
        this.sessionVisibleFromTimestamp = sessionVisibleFromTimestamp;
    }

    public void setResultVisibleFromTimestamp(Long resultVisibleFromTimestamp) {
        this.resultVisibleFromTimestamp = resultVisibleFromTimestamp;
    }

    public void setGracePeriod(Long gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public void setSessionVisibleSetting(SessionVisibleSetting sessionVisibleSetting) {
        this.sessionVisibleSetting = sessionVisibleSetting;
    }

    public void setCustomSessionVisibleTimestamp(Long customSessionVisibleTimestamp) {
        this.customSessionVisibleTimestamp = customSessionVisibleTimestamp;
    }

    public void setResponseVisibleSetting(ResponseVisibleSetting responseVisibleSetting) {
        this.responseVisibleSetting = responseVisibleSetting;
    }

    public void setCustomResponseVisibleTimestamp(Long customResponseVisibleTimestamp) {
        this.customResponseVisibleTimestamp = customResponseVisibleTimestamp;
    }

    public void setPublishStatus(FeedbackSessionPublishStatus publishStatus) {
        this.publishStatus = publishStatus;
    }

    public void setClosingSoonEmailEnabled(Boolean closingSoonEmailEnabled) {
        isClosingSoonEmailEnabled = closingSoonEmailEnabled;
    }

    public void setPublishedEmailEnabled(Boolean publishedEmailEnabled) {
        isPublishedEmailEnabled = publishedEmailEnabled;
    }

    public long getCreatedAtTimestamp() {
        return createdAtTimestamp;
    }

    public void setCreatedAtTimestamp(long timestamp) {
        createdAtTimestamp = timestamp;
    }

    public Long getDeletedAtTimestamp() {
        return deletedAtTimestamp;
    }

    public InstructorPermissionSet getPrivileges() {
        return privileges;
    }

    public void setPrivileges(InstructorPermissionSet privileges) {
        this.privileges = privileges;
    }

    public void setStudentDeadlines(Map<String, Long> studentDeadlines) {
        this.studentDeadlines = studentDeadlines;
    }

    public void setInstructorDeadlines(Map<String, Long> instructorDeadlines) {
        this.instructorDeadlines = instructorDeadlines;
    }

    /**
     * Hides some attributes to student.
     */
    public void hideInformationForStudent() {
        hideInformationForStudentAndInstructor();
        hideSessionVisibilityTimestamps();
        instructorDeadlines.clear();
    }

    /**
     * Hides some attributes to student.
     */
    public void hideInformationForStudent(String studentEmail) {
        hideInformationForStudentAndInstructor();
        hideSessionVisibilityTimestamps();
        studentDeadlines.keySet().removeIf(email -> !(email.equals(studentEmail)));
        instructorDeadlines.clear();
    }

    /**
     * Hides some attributes to instructor without appropriate privilege.
     */
    public void hideInformationForInstructor() {
        hideInformationForStudentAndInstructor();
        studentDeadlines.clear();
    }

    /**
     * Hides some attributes to instructor without appropriate privilege.
     */
    public void hideInformationForInstructor(String instructorEmail) {
        hideInformationForStudentAndInstructor();
        instructorDeadlines.keySet().removeIf(email -> !(email.equals(instructorEmail)));
        studentDeadlines.clear();
    }

    /**
     * Hides some attributes for instructor who is submitting feedback session.
     */
    public void hideInformationForInstructorSubmission() {
        hideInformationForInstructor();
        hideSessionVisibilityTimestamps();
    }

    /**
     * Hides some attributes for instructor who is submitting feedback session.
     */
    public void hideInformationForInstructorSubmission(String userEmail) {
        hideInformationForInstructor(userEmail);
        hideSessionVisibilityTimestamps();
    }

    private void hideSessionVisibilityTimestamps() {
        setSessionVisibleFromTimestamp(null);
        setResultVisibleFromTimestamp(null);
        setSessionVisibleSetting(null);
        setCustomSessionVisibleTimestamp(null);
        setResponseVisibleSetting(null);
        setCustomResponseVisibleTimestamp(null);
    }

    private void hideInformationForStudentAndInstructor() {
        setClosingSoonEmailEnabled(null);
        setPublishedEmailEnabled(null);
        setGracePeriod(null);
        setCreatedAtTimestamp(0);
    }
}
