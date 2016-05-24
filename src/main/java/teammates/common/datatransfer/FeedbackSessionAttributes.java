package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import com.google.appengine.api.datastore.Text;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.TimeHelper;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.Utils;
import teammates.storage.entity.FeedbackSession;

public class FeedbackSessionAttributes extends EntityAttributes implements SessionAttributes {
    private String feedbackSessionName;
    private String courseId;
    private String creatorEmail;
    private Text instructions;
    private Date createdTime;
    private Date startTime;
    private Date endTime;
    private Date sessionVisibleFromTime;
    private Date resultsVisibleFromTime;
    private double timeZone;
    private int gracePeriod;
    private FeedbackSessionType feedbackSessionType;
    private boolean sentOpenEmail;
    private boolean sentPublishedEmail;
    private boolean isOpeningEmailEnabled;
    private boolean isClosingEmailEnabled;
    private boolean isPublishedEmailEnabled;
    private Set<String> respondingInstructorList;
    private Set<String> respondingStudentList;

    public FeedbackSessionAttributes() {
        this.setOpeningEmailEnabled(true);
        this.setClosingEmailEnabled(true);
        this.setPublishedEmailEnabled(true);
        this.setRespondingInstructorList(new HashSet<String>());
        this.setRespondingStudentList(new HashSet<String>());
    }

    public FeedbackSessionAttributes(FeedbackSession fs) {
        this.setFeedbackSessionName(fs.getFeedbackSessionName());
        this.setCourseId(fs.getCourseId());
        this.setCreatorEmail(fs.getCreatorEmail());
        this.setInstructions(fs.getInstructions());
        this.setCreatedTime(fs.getCreatedTime());
        this.setStartTime(fs.getStartTime());
        this.setEndTime(fs.getEndTime());
        this.setSessionVisibleFromTime(fs.getSessionVisibleFromTime());
        this.setResultsVisibleFromTime(fs.getResultsVisibleFromTime());
        this.setTimeZone(fs.getTimeZone());
        this.setGracePeriod(fs.getGracePeriod());
        this.setFeedbackSessionType(fs.getFeedbackSessionType());
        this.setSentOpenEmail(fs.isSentOpenEmail());
        this.setSentPublishedEmail(fs.isSentPublishedEmail());
        this.setOpeningEmailEnabled(fs.isOpeningEmailEnabled());
        this.setClosingEmailEnabled(fs.isClosingEmailEnabled());
        this.setPublishedEmailEnabled(fs.isPublishedEmailEnabled());
        this.setRespondingInstructorList((fs.getRespondingInstructorList() == null ? new HashSet<String>()
                                                                                  : fs.getRespondingInstructorList()));
        this.setRespondingStudentList((fs.getRespondingStudentList() == null ? new HashSet<String>()
                                                                            : fs.getRespondingStudentList()));
    }

    public FeedbackSessionAttributes(String feedbackSessionName, String courseId, String creatorId, 
                                     Text instructions, Date createdTime, Date startTime, Date endTime,
                                     Date sessionVisibleFromTime, Date resultsVisibleFromTime,
                                     double timeZone, int gracePeriod, FeedbackSessionType feedbackSessionType,
                                     boolean sentOpenEmail, boolean sentPublishedEmail,
                                     boolean isOpeningEmailEnabled, boolean isClosingEmailEnabled, 
                                     boolean isPublishedEmailEnabled) {
        this(feedbackSessionName, courseId, creatorId, instructions, createdTime, startTime, endTime,
             sessionVisibleFromTime, resultsVisibleFromTime, timeZone, gracePeriod, feedbackSessionType,
             sentOpenEmail, sentPublishedEmail, isOpeningEmailEnabled, isClosingEmailEnabled, isPublishedEmailEnabled,
             new HashSet<String>(), new HashSet<String>());
    }

    public FeedbackSessionAttributes(String feedbackSessionName, String courseId, String creatorId, 
                                     Text instructions, Date createdTime, Date startTime, Date endTime,
                                     Date sessionVisibleFromTime, Date resultsVisibleFromTime,
                                     double timeZone, int gracePeriod, FeedbackSessionType feedbackSessionType,
                                     boolean sentOpenEmail, boolean sentPublishedEmail,
                                     boolean isOpeningEmailEnabled, boolean isClosingEmailEnabled,
                                     boolean isPublishedEmailEnabled, Set<String> instructorList, 
                                     Set<String> studentList) {
        this.setFeedbackSessionName(Sanitizer.sanitizeTitle(feedbackSessionName));
        this.setCourseId(Sanitizer.sanitizeTitle(courseId));
        this.setCreatorEmail(Sanitizer.sanitizeEmail(creatorId));
        this.setInstructions(Sanitizer.sanitizeTextField(instructions));
        this.setCreatedTime(createdTime);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setSessionVisibleFromTime(sessionVisibleFromTime);
        this.setResultsVisibleFromTime(resultsVisibleFromTime);
        this.setTimeZone(timeZone);
        this.setGracePeriod(gracePeriod);
        this.setFeedbackSessionType(feedbackSessionType);
        this.setSentOpenEmail(sentOpenEmail);
        this.setSentPublishedEmail(sentPublishedEmail);
        this.setOpeningEmailEnabled(isOpeningEmailEnabled);
        this.setClosingEmailEnabled(isClosingEmailEnabled);
        this.setPublishedEmailEnabled(isPublishedEmailEnabled);
        this.setRespondingInstructorList(instructorList);
        this.setRespondingStudentList(studentList);
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }
    
    public String getStartTimeString() {
        return TimeHelper.formatTime12H(getStartTime());
    }
    
    public String getEndTimeString() {
        return TimeHelper.formatTime12H(getEndTime());
    }
    
    public String getInstructionsString() {
        return Sanitizer.sanitizeForHtml(getInstructions().getValue());
    }

    @Override
    public FeedbackSession toEntity() {
        return new FeedbackSession(getFeedbackSessionName(), getCourseId(), getCreatorEmail(), getInstructions(), getCreatedTime(), 
                                   getStartTime(), getEndTime(), getSessionVisibleFromTime(), getResultsVisibleFromTime(),
                                   getTimeZone(), getGracePeriod(), getFeedbackSessionType(), isSentOpenEmail(), isSentPublishedEmail(),
                                   isOpeningEmailEnabled(), isClosingEmailEnabled(), isPublishedEmailEnabled(), 
                                   getRespondingInstructorList(), getRespondingStudentList());
    }

    @Override
    public String getIdentificationString() {
        return this.getFeedbackSessionName() + "/" + this.getCourseId();
    }

    @Override
    public String getEntityTypeAsString() {
        return "Feedback Session";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + getCourseId();
    }

    @Override
    public String getJsonString() {
        return Utils.getTeammatesGson().toJson(this, FeedbackSessionAttributes.class);
    }

    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;

        // Check for null fields.

        error = validator.getValidityInfoForNonNullField("feedback session name", getFeedbackSessionName());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getValidityInfoForNonNullField("course ID", getCourseId());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getValidityInfoForNonNullField("instructions to students", getInstructions());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getValidityInfoForNonNullField("time for the session to become visible", getSessionVisibleFromTime());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getValidityInfoForNonNullField("creator's email", getCreatorEmail());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getValidityInfoForNonNullField("session creation time", getCreatedTime());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        error = validator.getInvalidityInfoForFeedbackSessionName(getFeedbackSessionName());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getInvalidityInfo(FieldType.COURSE_ID, getCourseId());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getInvalidityInfo(FieldType.EMAIL, "creator's email", getCreatorEmail());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        // Skip time frame checks if session type is private.
        if (this.isPrivateSession()) {
            return errors;
        }

        error = validator.getValidityInfoForNonNullField("submission opening time", getStartTime());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getValidityInfoForNonNullField("submission closing time", getEndTime());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getValidityInfoForNonNullField("time for the responses to become visible", getResultsVisibleFromTime());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        // Early return if any null fields
        if (!errors.isEmpty()) {
            return errors;
        }

        error = validator.getValidityInfoForTimeFrame(FieldType.FEEDBACK_SESSION_TIME_FRAME,
                                                      FieldType.START_TIME, FieldType.END_TIME,
                                                      getStartTime(), getEndTime());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getValidityInfoForTimeFrame(FieldType.FEEDBACK_SESSION_TIME_FRAME,
                                                      FieldType.SESSION_VISIBLE_TIME, FieldType.START_TIME,
                                                      getSessionVisibleFromTime(), getStartTime());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        Date actualSessionVisibleFromTime = getSessionVisibleFromTime();

        if (actualSessionVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            actualSessionVisibleFromTime = getStartTime();
        }

        error = validator.getValidityInfoForTimeFrame(FieldType.FEEDBACK_SESSION_TIME_FRAME,
                                                      FieldType.SESSION_VISIBLE_TIME, FieldType.RESULTS_VISIBLE_TIME,
                                                      actualSessionVisibleFromTime, getResultsVisibleFromTime());
        if (!error.isEmpty()) {
            errors.add(error);
        }

        return errors;
    }

    @Override
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
    }

    public boolean isClosingWithinTimeLimit(int hours) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // Fix the time zone accordingly
        now.add(Calendar.MILLISECOND,
                (int) (60 * 60 * 1000 * getTimeZone()));

        Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        start.setTime(getStartTime());

        Calendar deadline = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        deadline.setTime(getEndTime());

        long nowMillis = now.getTimeInMillis();
        long deadlineMillis = deadline.getTimeInMillis();
        long differenceBetweenDeadlineAndNow = (deadlineMillis - nowMillis) / (60 * 60 * 1000);

        // If now and start are almost similar, it means the feedback session
        // is open for only 24 hours.
        // Hence we do not send a reminder e-mail for feedback session.
        return now.after(start)
               && differenceBetweenDeadlineAndNow >= hours - 1
               && differenceBetweenDeadlineAndNow < hours;
    }

    /**
     * @return {@code true} if it is after the closing time of this feedback session; {@code false} if not.
     */
    public boolean isClosed() {
        Calendar now = TimeHelper.now(getTimeZone());
        Calendar end = TimeHelper.dateToCalendar(getEndTime());
        end.add(Calendar.MINUTE, getGracePeriod());

        return now.after(end);
    }

    /**
     * @return {@code true} is currently open and accepting responses
     */
    public boolean isOpened() {
        Calendar now = TimeHelper.now(getTimeZone());
        Calendar start = TimeHelper.dateToCalendar(getStartTime());
        Calendar end = TimeHelper.dateToCalendar(getEndTime());

        return now.after(start) && now.before(end);
    }

    /**
     * @return {@code true} is currently close but is still accept responses
     */
    public boolean isInGracePeriod() {
        Calendar now = TimeHelper.now(getTimeZone());
        Calendar end = TimeHelper.dateToCalendar(getEndTime());
        Calendar gracedEnd = TimeHelper.dateToCalendar(getEndTime());
        gracedEnd.add(Calendar.MINUTE, getGracePeriod());

        return now.after(end) && now.before(gracedEnd);
    }

    /**
     * @return {@code true} has not opened before and is waiting to open.<br>
     * {@code false} if session has opened before.
     */
    public boolean isWaitingToOpen() {
        Calendar now = TimeHelper.now(getTimeZone());
        Calendar start = TimeHelper.dateToCalendar(getStartTime());

        return now.before(start);
    }

    /**
     * @return {@code true} if the session is visible; {@code false} if not.
     * Does not care if the session has started or not.
     */
    public boolean isVisible() {
        Date visibleTime = this.getSessionVisibleFromTime();

        if (visibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            visibleTime = this.getStartTime();
        } else if (visibleTime.equals(Const.TIME_REPRESENTS_NEVER)) {
            return false;
        }

        Date now = TimeHelper.now(getTimeZone()).getTime();
        return visibleTime.before(now);
    }

    /**
     * @return {@code true} if the results of the feedback session is visible; {@code false} if not.
     * Does not care if the session has ended or not.
     */
    public boolean isPublished() {
        Date now = TimeHelper.now(getTimeZone()).getTime();
        Date publishTime = this.getResultsVisibleFromTime();

        if (publishTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            return isVisible();
        } else if (publishTime.equals(Const.TIME_REPRESENTS_LATER)) {
            return false;
        } else if (publishTime.equals(Const.TIME_REPRESENTS_NEVER)) {
            return false;
        } else if (publishTime.equals(Const.TIME_REPRESENTS_NOW)) {
            return true;
        } else {
            return publishTime.before(now);
        }
    }

    /**
     * @return {@code true} if the session has been set by the creator to be manually published.
     */
    public boolean isManuallyPublished() {
        return getResultsVisibleFromTime().equals(Const.TIME_REPRESENTS_LATER)
               || getResultsVisibleFromTime().equals(Const.TIME_REPRESENTS_NOW);
    }

    /**
     * @return {@code true} if session is a private session (only open to the session creator),
     *  {@code false} if not.
     */
    public boolean isPrivateSession() {
        return getSessionVisibleFromTime().equals(Const.TIME_REPRESENTS_NEVER) 
               || getFeedbackSessionType().equals(FeedbackSessionType.PRIVATE);
    }

    public boolean isCreator(String instructorEmail) {
        return getCreatorEmail().equals(instructorEmail);
    }

    @Override
    public void sanitizeForSaving() {
        this.setCourseId(Sanitizer.sanitizeForHtml(getCourseId()));
        this.setCreatorEmail(Sanitizer.sanitizeForHtml(getCreatorEmail()));

        if (getInstructions() != null) {
            this.setInstructions(new Text(Sanitizer.sanitizeForHtml(getInstructions().getValue())));
        }
    }

    @Override
    public String toString() {
        return "FeedbackSessionAttributes [feedbackSessionName="
               + getFeedbackSessionName() + ", courseId=" + getCourseId()
               + ", creatorEmail=" + getCreatorEmail() + ", instructions=" + getInstructions()
               + ", startTime=" + getStartTime()
               + ", endTime=" + getEndTime() + ", sessionVisibleFromTime="
               + getSessionVisibleFromTime() + ", resultsVisibleFromTime="
               + getResultsVisibleFromTime() + ", timeZone=" + getTimeZone()
               + ", gracePeriod=" + getGracePeriod() + ", feedbackSessionType="
               + getFeedbackSessionType() + ", sentOpenEmail=" + isSentOpenEmail()
               + ", sentPublishedEmail=" + isSentPublishedEmail()
               + ", isOpeningEmailEnabled=" + isOpeningEmailEnabled()
               + ", isClosingEmailEnabled=" + isClosingEmailEnabled()
               + ", isPublishedEmailEnabled=" + isPublishedEmailEnabled() + "]";
    }

    /**
     * Sorts feedback session based courseID (ascending), then by create time (ascending), deadline
     * (ascending), then by start time (ascending), then by feedback session name
     * (ascending). The sort by CourseID part is to cater the case when this
     * method is called with combined feedback sessions from many courses
     *
     * @param sessions
     */
    public static void sortFeedbackSessionsByCreationTime(List<FeedbackSessionAttributes> sessions) {
        Collections.sort(sessions, new Comparator<FeedbackSessionAttributes>() {
            @Override
            public int compare(FeedbackSessionAttributes session1, FeedbackSessionAttributes session2) {
                int result = session1.getCourseId().compareTo(session2.getCourseId());

                if (result == 0) {
                    result = session1.getCreatedTime().compareTo(session2.getCreatedTime());
                }

                if (result == 0) {
                    result = session1.getEndTime().compareTo(session2.getEndTime());
                }

                if (result == 0) {
                    result = session1.getStartTime().compareTo(session2.getStartTime());
                }

                if (result == 0) {
                    result = session1.getFeedbackSessionName().compareTo(session2.getFeedbackSessionName());
                }

                return result;
            }
        });
    }

    /**
     * Sorts feedback session based on create time (descending), deadline
     * (descending), then by start time (descending),then by courseID (ascending),then by feedback session name
     * (ascending). The sort by CourseID part is to cater the case when this
     * method is called with combined feedback sessions from many courses
     *
     * @param sessions
     */
    public static void sortFeedbackSessionsByCreationTimeDescending(List<FeedbackSessionAttributes> sessions) {
        Collections.sort(sessions, new Comparator<FeedbackSessionAttributes>() {
            @Override
            public int compare(FeedbackSessionAttributes session1, FeedbackSessionAttributes session2) {
                int result = session2.getCreatedTime().compareTo(session1.getCreatedTime());
                if (result == 0) {
                    if (session1.getEndTime() == null || session2.getEndTime() == null) {
                        if (session1.getEndTime() == null) {
                            --result;
                        }
                        if (session2.getEndTime() == null) {
                            ++result;
                        }
                    } else {
                        result = session2.getEndTime().compareTo(session1.getEndTime());
                    }
                }

                if (result == 0) {
                    result = session2.getStartTime().compareTo(session1.getStartTime());
                }
                if (result == 0) {
                    result = session1.getCourseId().compareTo(session2.getCourseId());
                }

                if (result == 0) {
                    result = session1.getFeedbackSessionName().compareTo(session2.getFeedbackSessionName());
                }
                
                return result;
            }
        });
    }

    @Override
    public Date getSessionStartTime() {
        return this.getStartTime();
    }

    @Override
    public Date getSessionEndTime() {
        return this.getEndTime();
    }

    @Override
    public String getSessionName() {
        return this.getFeedbackSessionName();
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public Text getInstructions() {
        return instructions;
    }

    public void setInstructions(Text instructions) {
        this.instructions = instructions;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getSessionVisibleFromTime() {
        return sessionVisibleFromTime;
    }

    public void setSessionVisibleFromTime(Date sessionVisibleFromTime) {
        this.sessionVisibleFromTime = sessionVisibleFromTime;
    }

    public Date getResultsVisibleFromTime() {
        return resultsVisibleFromTime;
    }

    public void setResultsVisibleFromTime(Date resultsVisibleFromTime) {
        this.resultsVisibleFromTime = resultsVisibleFromTime;
    }

    public double getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(double timeZone) {
        this.timeZone = timeZone;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public FeedbackSessionType getFeedbackSessionType() {
        return feedbackSessionType;
    }

    public void setFeedbackSessionType(FeedbackSessionType feedbackSessionType) {
        this.feedbackSessionType = feedbackSessionType;
    }

    public boolean isSentOpenEmail() {
        return sentOpenEmail;
    }

    public void setSentOpenEmail(boolean sentOpenEmail) {
        this.sentOpenEmail = sentOpenEmail;
    }

    public boolean isSentPublishedEmail() {
        return sentPublishedEmail;
    }

    public void setSentPublishedEmail(boolean sentPublishedEmail) {
        this.sentPublishedEmail = sentPublishedEmail;
    }

    public boolean isOpeningEmailEnabled() {
        return isOpeningEmailEnabled;
    }

    public void setOpeningEmailEnabled(boolean isOpeningEmailEnabled) {
        this.isOpeningEmailEnabled = isOpeningEmailEnabled;
    }

    public boolean isClosingEmailEnabled() {
        return isClosingEmailEnabled;
    }

    public void setClosingEmailEnabled(boolean isClosingEmailEnabled) {
        this.isClosingEmailEnabled = isClosingEmailEnabled;
    }

    public boolean isPublishedEmailEnabled() {
        return isPublishedEmailEnabled;
    }

    public void setPublishedEmailEnabled(boolean isPublishedEmailEnabled) {
        this.isPublishedEmailEnabled = isPublishedEmailEnabled;
    }

    public Set<String> getRespondingInstructorList() {
        return respondingInstructorList;
    }

    public void setRespondingInstructorList(Set<String> respondingInstructorList) {
        this.respondingInstructorList = respondingInstructorList;
    }

    public Set<String> getRespondingStudentList() {
        return respondingStudentList;
    }

    public void setRespondingStudentList(Set<String> respondingStudentList) {
        this.respondingStudentList = respondingStudentList;
    }
}
