package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.FeedbackSession;

/**
 * Handles CRUD operations for feedback sessions.
 *
 * @see FeedbackSession
 * @see FeedbackSessionAttributes
 */
public class FeedbackSessionsDb extends EntitiesDb<FeedbackSession, FeedbackSessionAttributes> {

    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Feedback Session : ";

    public void createFeedbackSessions(Collection<FeedbackSessionAttributes> feedbackSessionsToAdd)
            throws InvalidParametersException {
        List<FeedbackSessionAttributes> feedbackSessionsToUpdate = createEntities(feedbackSessionsToAdd);
        for (FeedbackSessionAttributes session : feedbackSessionsToUpdate) {
            try {
                updateFeedbackSession(session);
            } catch (EntityDoesNotExistException e) {
                // This situation is not tested as replicating such a situation is
                // difficult during testing
                Assumption.fail("Entity found be already existing and not existing simultaneously");
            }
        }
    }

    public List<FeedbackSessionAttributes> getAllOpenFeedbackSessions(Date start, Date end, double zone) {
        List<FeedbackSessionAttributes> list = new LinkedList<>();

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);

        Date curStart = TimeHelper.convertToUserTimeZone(startCal, -25).getTime();
        Date curEnd = TimeHelper.convertToUserTimeZone(endCal, 25).getTime();

        List<FeedbackSession> endEntities = load()
                .filter("endTime >", curStart)
                .filter("endTime <=", curEnd)
                .list();

        List<FeedbackSession> startEntities = load()
                .filter("startTime >=", curStart)
                .filter("startTime <", curEnd)
                .list();

        List<FeedbackSession> endTimeEntities = new ArrayList<>(endEntities);
        List<FeedbackSession> startTimeEntities = new ArrayList<>(startEntities);

        endTimeEntities.removeAll(startTimeEntities);
        startTimeEntities.removeAll(endTimeEntities);
        endTimeEntities.addAll(startTimeEntities);

        for (FeedbackSession feedbackSession : endTimeEntities) {
            startCal.setTime(start);
            endCal.setTime(end);
            FeedbackSessionAttributes fs = makeAttributes(feedbackSession);

            Date standardStart = TimeHelper.convertToUserTimeZone(startCal, fs.getTimeZone() - zone).getTime();
            Date standardEnd = TimeHelper.convertToUserTimeZone(endCal, fs.getTimeZone() - zone).getTime();

            boolean isStartTimeWithinRange =
                    TimeHelper.isTimeWithinPeriod(standardStart, standardEnd, fs.getStartTime(), true, false);
            boolean isEndTimeWithinRange =
                    TimeHelper.isTimeWithinPeriod(standardStart, standardEnd, fs.getEndTime(), false, true);

            if (isStartTimeWithinRange || isEndTimeWithinRange) {
                list.add(fs);
            }
        }

        return list;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackSessionAttributes getFeedbackSession(String courseId, String feedbackSessionName) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributesOrNull(getFeedbackSessionEntity(feedbackSessionName, courseId),
                "Trying to get non-existent Session: " + feedbackSessionName + "/" + courseId);
    }

    /**
     * Returns empty list if none found.
     * @deprecated Not scalable. Created for data migration purposes.
     */
    @Deprecated
    public List<FeedbackSessionAttributes> getAllFeedbackSessions() {
        return makeAttributes(getAllFeedbackSessionEntities());
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no sessions are found for the given course.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getFeedbackSessionEntitiesForCourse(courseId));
    }

    /**
     * Returns An empty list if no sessions are found that have unsent open emails.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingOpenEmail() {
        return makeAttributes(getFeedbackSessionEntitiesPossiblyNeedingOpenEmail());
    }

    /**
     * Returns An empty list if no sessions are found that have unsent closing emails.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingClosingEmail() {
        return makeAttributes(getFeedbackSessionEntitiesPossiblyNeedingClosingEmail());
    }

    /**
     * Returns An empty list if no sessions are found that have unsent closed emails.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingClosedEmail() {
        return makeAttributes(getFeedbackSessionEntitiesPossiblyNeedingClosedEmail());
    }

    /**
     * Returns An empty list if no sessions are found that have unsent published emails.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingPublishedEmail() {
        return makeAttributes(getFeedbackSessionEntitiesPossiblyNeedingPublishedEmail());
    }

    /**
     * Updates the feedback session identified by {@code newAttributes.feedbackSesionName}
     * and {@code newAttributes.courseId}.
     * For the remaining parameters, the existing value is preserved
     *   if the parameter is null (due to 'keep existing' policy).<br>
     * Preconditions: <br>
     * * {@code newAttributes.feedbackSesionName} and {@code newAttributes.courseId}
     *  are non-null and correspond to an existing feedback session. <br>
     */
    public void updateFeedbackSession(FeedbackSessionAttributes newAttributes)
        throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newAttributes);

        newAttributes.sanitizeForSaving();

        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        FeedbackSession fs = getEntity(newAttributes);

        if (fs == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
        }
        fs.setInstructions(newAttributes.getInstructions());
        fs.setStartTime(newAttributes.getStartTime());
        fs.setEndTime(newAttributes.getEndTime());
        fs.setSessionVisibleFromTime(newAttributes.getSessionVisibleFromTime());
        fs.setResultsVisibleFromTime(newAttributes.getResultsVisibleFromTime());
        fs.setTimeZone(newAttributes.getTimeZone());
        fs.setGracePeriod(newAttributes.getGracePeriod());
        fs.setFeedbackSessionType(newAttributes.getFeedbackSessionType());
        fs.setSentOpenEmail(newAttributes.isSentOpenEmail());
        fs.setSentClosingEmail(newAttributes.isSentClosingEmail());
        fs.setSentClosedEmail(newAttributes.isSentClosedEmail());
        fs.setSentPublishedEmail(newAttributes.isSentPublishedEmail());
        fs.setIsOpeningEmailEnabled(newAttributes.isOpeningEmailEnabled());
        fs.setSendClosingEmail(newAttributes.isClosingEmailEnabled());
        fs.setSendPublishedEmail(newAttributes.isPublishedEmailEnabled());

        saveEntity(fs, newAttributes);
    }

    public void addInstructorRespondent(String email, FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<String> emails = new ArrayList<>();
        emails.add(email);
        addInstructorRespondents(emails, feedbackSession);
    }

    public void addInstructorRespondents(List<String> emails, FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, emails);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        FeedbackSession fs = getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        fs.getRespondingInstructorList().addAll(emails);

        saveEntity(fs, feedbackSession);
    }

    public void updateInstructorRespondent(String oldEmail, String newEmail, FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, oldEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        FeedbackSession fs = getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        if (fs.getRespondingInstructorList().contains(oldEmail)) {
            fs.getRespondingInstructorList().remove(oldEmail);
            fs.getRespondingInstructorList().add(newEmail);
        }

        saveEntity(fs, feedbackSession);
    }

    public void clearInstructorRespondents(FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        FeedbackSession fs = getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        fs.getRespondingInstructorList().clear();

        saveEntity(fs, feedbackSession);
    }

    public void addStudentRespondent(String email, FeedbackSessionAttributes feedbackSession)
            throws EntityDoesNotExistException, InvalidParametersException {
        List<String> emails = new ArrayList<>();
        emails.add(email);
        addStudentRespondents(emails, feedbackSession);
    }

    public void deleteInstructorRespondent(String email, FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        FeedbackSession fs = getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        fs.getRespondingInstructorList().remove(email);

        saveEntity(fs, feedbackSession);
    }

    public void addStudentRespondents(List<String> emails, FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, emails);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        FeedbackSession fs = getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        fs.getRespondingStudentList().addAll(emails);

        saveEntity(fs, feedbackSession);
    }

    public void updateStudentRespondent(String oldEmail, String newEmail, FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, oldEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        FeedbackSession fs = getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        if (fs.getRespondingStudentList().contains(oldEmail)) {
            fs.getRespondingStudentList().remove(oldEmail);
            fs.getRespondingStudentList().add(newEmail);
        }

        saveEntity(fs, feedbackSession);
    }

    public void clearStudentRespondents(FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        FeedbackSession fs = getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        fs.getRespondingStudentList().clear();

        saveEntity(fs, feedbackSession);
    }

    public void deleteStudentRespondent(String email, FeedbackSessionAttributes feedbackSession)
            throws EntityDoesNotExistException, InvalidParametersException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        FeedbackSession fs = getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        fs.getRespondingStudentList().remove(email);

        saveEntity(fs, feedbackSession);
    }

    public void deleteFeedbackSessionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        deleteFeedbackSessionsForCourses(Arrays.asList(courseId));
    }

    public void deleteFeedbackSessionsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

        ofy().delete().keys(load().filter("courseId in", courseIds).keys()).now();
    }

    private List<FeedbackSession> getAllFeedbackSessionEntities() {
        return load().list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesForCourse(String courseId) {
        return load().filter("courseId =", courseId).list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingOpenEmail() {
        return load()
                .filter("startTime >", TimeHelper.getDateOffsetToCurrentTime(-2))
                .filter("sentOpenEmail =", false)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingClosingEmail() {
        return load()
                .filter("endTime >", TimeHelper.getDateOffsetToCurrentTime(-2))
                .filter("sentClosingEmail =", false)
                .filter("isClosingEmailEnabled =", true)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingClosedEmail() {
        return load()
                .filter("endTime >", TimeHelper.getDateOffsetToCurrentTime(-2))
                .filter("sentClosedEmail =", false)
                .filter("isClosingEmailEnabled =", true)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingPublishedEmail() {
        return load()
                .filter("sentPublishedEmail =", false)
                .filter("isPublishedEmailEnabled =", true)
                .filter("feedbackSessionType !=", FeedbackSessionType.PRIVATE)
                .list();
    }

    private FeedbackSession getFeedbackSessionEntity(String feedbackSessionName, String courseId) {
        return load()
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .first().now();
    }

    @Override
    protected LoadType<FeedbackSession> load() {
        return ofy().load().type(FeedbackSession.class);
    }

    @Override
    protected FeedbackSession getEntity(FeedbackSessionAttributes attributes) {
        return getFeedbackSessionEntity(attributes.getFeedbackSessionName(), attributes.getCourseId());
    }

    @Override
    protected QueryKeys<FeedbackSession> getEntityQueryKeys(FeedbackSessionAttributes attributes) {
        return load()
                .filter("feedbackSessionName =", attributes.getFeedbackSessionName())
                .filter("courseId =", attributes.getCourseId()).keys();
    }

    @Override
    protected FeedbackSessionAttributes makeAttributes(FeedbackSession entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return new FeedbackSessionAttributes(entity);
    }
}
