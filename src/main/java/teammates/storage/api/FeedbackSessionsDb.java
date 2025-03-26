package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.FeedbackSession;

/**
 * Handles CRUD operations for feedback sessions.
 *
 * @see FeedbackSession
 * @see FeedbackSessionAttributes
 */
public final class FeedbackSessionsDb extends EntitiesDb<FeedbackSession, FeedbackSessionAttributes> {

    private static final FeedbackSessionsDb instance = new FeedbackSessionsDb();

    private FeedbackSessionsDb() {
        // prevent initialization
    }

    public static FeedbackSessionsDb inst() {
        return instance;
    }

    /**
     * Gets a list of feedback sessions that is ongoing, i.e. starting before {@code rangeEnd}
     * and ending after {@code rangeStart}.
     *
     * <p>The time window of searching is limited to (range + 30) days (e.g. only sessions starting
     * before {@code rangeEnd} but not before [{@code rangeStart} - 30 days] will be considered)
     * to not return excessive amount of results.
     */
    public List<FeedbackSessionAttributes> getAllOngoingSessions(Instant rangeStart, Instant rangeEnd) {
        List<FeedbackSession> endEntities = load()
                .filter("endTime >", rangeStart)
                .filter("endTime <",
                        Instant.ofEpochMilli(rangeEnd.toEpochMilli()).plus(Const.FEEDBACK_SESSIONS_SEARCH_WINDOW))
                .list();

        List<FeedbackSession> startEntities = load()
                .filter("startTime <", rangeEnd)
                .filter("startTime >",
                        Instant.ofEpochMilli(rangeStart.toEpochMilli()).minus(Const.FEEDBACK_SESSIONS_SEARCH_WINDOW))
                .list();

        List<String> startEntitiesIds = startEntities.stream()
                .map(session -> session.getCourseId() + "::" + session.getFeedbackSessionName())
                .collect(Collectors.toList());

        List<FeedbackSession> ongoingSessions = endEntities.stream()
                .filter(session -> {
                    String id = session.getCourseId() + "::" + session.getFeedbackSessionName();
                    return startEntitiesIds.contains(id);
                })
                .collect(Collectors.toList());

        return makeAttributes(ongoingSessions);
    }

    /**
     * Gets a feedback session that is not soft-deleted.
     *
     * @return null if not found or soft-deleted.
     */
    public FeedbackSessionAttributes getFeedbackSession(String courseId, String feedbackSessionName) {
        assert feedbackSessionName != null;
        assert courseId != null;

        FeedbackSessionAttributes feedbackSession =
                makeAttributesOrNull(getFeedbackSessionEntity(feedbackSessionName, courseId));

        if (feedbackSession != null && feedbackSession.isSessionDeleted()) {
            log.info("Trying to access soft-deleted session: " + feedbackSessionName + "/" + courseId);
            return null;
        }
        return feedbackSession;
    }

    /**
     * Gets a soft-deleted feedback session.
     *
     * @return null if not found or not soft-deleted.
     */
    public FeedbackSessionAttributes getSoftDeletedFeedbackSession(String courseId, String feedbackSessionName) {
        assert feedbackSessionName != null;
        assert courseId != null;

        FeedbackSessionAttributes feedbackSession =
                makeAttributesOrNull(getFeedbackSessionEntity(feedbackSessionName, courseId));

        if (feedbackSession != null && !feedbackSession.isSessionDeleted()) {
            log.info(feedbackSessionName + "/" + courseId + " is not soft-deleted!");
            return null;
        }

        return feedbackSession;
    }

    /**
     * Gets a list of all sessions for the given course except those are soft-deleted.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {
        assert courseId != null;

        return makeAttributes(getFeedbackSessionEntitiesForCourse(courseId)).stream()
                .filter(session -> !session.isSessionDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of all sessions starting from some date for the given course except those are soft-deleted.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourseStartingAfter(String courseId, Instant after) {
        return makeAttributes(getFeedbackSessionEntitiesForCourseStartingAfter(courseId, after)).stream()
                .filter(session -> !session.isSessionDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of sessions for the given course that are soft-deleted.
     */
    public List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsForCourse(String courseId) {
        assert courseId != null;

        return makeAttributes(getFeedbackSessionEntitiesForCourse(courseId)).stream()
                .filter(FeedbackSessionAttributes::isSessionDeleted)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of undeleted feedback sessions which start within the last 2 hours
     * and possibly need an open email to be sent.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingOpenedEmail() {
        return makeAttributes(getFeedbackSessionEntitiesPossiblyNeedingOpenedEmail()).stream()
                .filter(session -> !session.isSessionDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of undeleted feedback sessions which end in the future (2 hour ago onward)
     * and possibly need a closing soon email to be sent.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingClosingSoonEmail() {
        return makeAttributes(getFeedbackSessionEntitiesPossiblyNeedingClosingSoonEmail()).stream()
                .filter(session -> !session.isSessionDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of undeleted feedback sessions which end in the future (2 hour ago onward)
     * and possibly need a closed email to be sent.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingClosedEmail() {
        return makeAttributes(getFeedbackSessionEntitiesPossiblyNeedingClosedEmail()).stream()
                .filter(session -> !session.isSessionDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of undeleted feedback sessions which open in the future
     * and possibly need a opening soon email to be sent.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingOpeningSoonEmail() {
        return makeAttributes(getFeedbackSessionEntitiesPossiblyNeedingOpeningSoonEmail()).stream()
                .filter(session -> !session.isSessionDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of undeleted published feedback sessions which possibly need a published email
     * to be sent.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingPublishedEmail() {
        return makeAttributes(getFeedbackSessionEntitiesPossiblyNeedingPublishedEmail()).stream()
                .filter(session -> !session.isSessionDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Update a feedback session by {@link FeedbackSessionAttributes.UpdateOptions}.
     *
     * @return updated feedback session
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSessionAttributes updateFeedbackSession(FeedbackSessionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        FeedbackSession feedbackSession =
                getFeedbackSessionEntity(updateOptions.getFeedbackSessionName(), updateOptions.getCourseId());
        if (feedbackSession == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        FeedbackSessionAttributes newAttributes = makeAttributes(feedbackSession);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes =
                this.<String>hasSameValue(feedbackSession.getInstructions(), newAttributes.getInstructions())
                && this.<Instant>hasSameValue(feedbackSession.getStartTime(), newAttributes.getStartTime())
                && this.<Instant>hasSameValue(feedbackSession.getEndTime(), newAttributes.getEndTime())
                && this.<Instant>hasSameValue(
                        feedbackSession.getSessionVisibleFromTime(), newAttributes.getSessionVisibleFromTime())
                && this.<Instant>hasSameValue(
                        feedbackSession.getResultsVisibleFromTime(), newAttributes.getResultsVisibleFromTime())
                && this.<String>hasSameValue(
                        feedbackSession.getTimeZone(), newAttributes.getTimeZone())
                && this.<Long>hasSameValue(
                        feedbackSession.getGracePeriod(), newAttributes.getGracePeriodMinutes())
                && this.<Boolean>hasSameValue(
                        feedbackSession.isSentOpeningSoonEmail(), newAttributes.isSentOpeningSoonEmail())
                && this.<Boolean>hasSameValue(
                        feedbackSession.isSentOpenedEmail(), newAttributes.isSentOpenedEmail())
                && this.<Boolean>hasSameValue(
                        feedbackSession.isSentClosingSoonEmail(), newAttributes.isSentClosingSoonEmail())
                && this.<Boolean>hasSameValue(
                        feedbackSession.isSentClosedEmail(), newAttributes.isSentClosedEmail())
                && this.<Boolean>hasSameValue(
                        feedbackSession.isSentPublishedEmail(), newAttributes.isSentPublishedEmail())
                && this.<Boolean>hasSameValue(
                        feedbackSession.isClosingSoonEmailEnabled(), newAttributes.isClosingSoonEmailEnabled())
                && this.<Boolean>hasSameValue(
                        feedbackSession.isPublishedEmailEnabled(), newAttributes.isPublishedEmailEnabled())
                && this.<Map<String, Instant>>hasSameValue(
                        feedbackSession.getStudentDeadlines(), newAttributes.getStudentDeadlines())
                && this.<Map<String, Instant>>hasSameValue(
                        feedbackSession.getInstructorDeadlines(), newAttributes.getInstructorDeadlines());
        if (hasSameAttributes) {
            log.info(String.format(
                    OPTIMIZED_SAVING_POLICY_APPLIED, FeedbackSession.class.getSimpleName(), updateOptions));
            return makeAttributes(feedbackSession);
        }

        feedbackSession.setInstructions(newAttributes.getInstructions());
        feedbackSession.setStartTime(newAttributes.getStartTime());
        feedbackSession.setEndTime(newAttributes.getEndTime());
        feedbackSession.setSessionVisibleFromTime(newAttributes.getSessionVisibleFromTime());
        feedbackSession.setResultsVisibleFromTime(newAttributes.getResultsVisibleFromTime());
        feedbackSession.setTimeZone(newAttributes.getTimeZone());
        feedbackSession.setGracePeriod(newAttributes.getGracePeriodMinutes());
        feedbackSession.setSentOpeningSoonEmail(newAttributes.isSentOpeningSoonEmail());
        feedbackSession.setSentOpenedEmail(newAttributes.isSentOpenedEmail());
        feedbackSession.setSentClosingSoonEmail(newAttributes.isSentClosingSoonEmail());
        feedbackSession.setSentClosedEmail(newAttributes.isSentClosedEmail());
        feedbackSession.setSentPublishedEmail(newAttributes.isSentPublishedEmail());
        feedbackSession.setSendClosingSoonEmail(newAttributes.isClosingSoonEmailEnabled());
        feedbackSession.setSendPublishedEmail(newAttributes.isPublishedEmailEnabled());
        feedbackSession.setStudentDeadlines(newAttributes.getStudentDeadlines());
        feedbackSession.setInstructorDeadlines(newAttributes.getInstructorDeadlines());

        saveEntity(feedbackSession);

        return makeAttributes(feedbackSession);
    }

    /**
     * Soft-deletes a specific feedback session by its name and course id.
     *
     * @return Soft-deletion time of the feedback session.
     */
    public Instant softDeleteFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
        assert courseId != null;
        assert feedbackSessionName != null;

        FeedbackSession sessionEntity = getFeedbackSessionEntity(feedbackSessionName, courseId);

        if (sessionEntity == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        sessionEntity.setDeletedTime(Instant.now());
        saveEntity(sessionEntity);

        return sessionEntity.getDeletedTime();
    }

    /**
     * Restores a specific soft deleted feedback session.
     */
    public void restoreDeletedFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
        assert courseId != null;
        assert feedbackSessionName != null;

        FeedbackSession sessionEntity = getFeedbackSessionEntity(feedbackSessionName, courseId);

        if (sessionEntity == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        sessionEntity.setDeletedTime(null);
        saveEntity(sessionEntity);
    }

    /**
     * Deletes a feedback session.
     */
    public void deleteFeedbackSession(String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        deleteEntity(Key.create(FeedbackSession.class, FeedbackSession.generateId(feedbackSessionName, courseId)));
    }

    /**
     * Deletes sessions using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackSessions(AttributesDeletionQuery query) {
        assert query != null;

        Query<FeedbackSession> entitiesToDelete = load().project();
        if (query.isCourseIdPresent()) {
            entitiesToDelete = entitiesToDelete.filter("courseId =", query.getCourseId());
        }

        deleteEntity(entitiesToDelete.keys().list());
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesForCourse(String courseId) {
        return load().filter("courseId =", courseId).list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesForCourseStartingAfter(String courseId, Instant after) {
        return load()
                .filter("courseId =", courseId)
                .filter("startTime >=", after)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingOpeningSoonEmail() {
        return load()
                .filter("startTime >", TimeHelper.getInstantDaysOffsetFromNow(-2))
                .filter("sentOpeningSoonEmail =", false)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingOpenedEmail() {
        return load()
                .filter("startTime >", TimeHelper.getInstantDaysOffsetFromNow(-2))
                .filter("sentOpenedEmail =", false)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingClosingSoonEmail() {
        return load()
                // Retrieve sessions with endTime from 2 days ago onwards to prevent issues caused by time zone differences
                .filter("endTime >", TimeHelper.getInstantDaysOffsetFromNow(-2))
                .filter("sentClosingSoonEmail =", false)
                .filter("isClosingSoonEmailEnabled =", true)
                .filter("sentClosedEmail =", false)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingClosedEmail() {
        return load()
                .filter("endTime >", TimeHelper.getInstantDaysOffsetFromNow(-2))
                .filter("sentClosedEmail =", false)
                .filter("isClosingSoonEmailEnabled =", true)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingPublishedEmail() {
        return load()
                .filter("resultsVisibleFromTime >", TimeHelper.getInstantDaysOffsetFromNow(-2))
                .filter("sentPublishedEmail =", false)
                .filter("isPublishedEmailEnabled =", true)
                .list();
    }

    private FeedbackSession getFeedbackSessionEntity(String feedbackSessionName, String courseId) {
        return load().id(FeedbackSession.generateId(feedbackSessionName, courseId)).now();
    }

    @Override
    LoadType<FeedbackSession> load() {
        return ofy().load().type(FeedbackSession.class);
    }

    @Override
    boolean hasExistingEntities(FeedbackSessionAttributes entityToCreate) {
        return !load()
                .filterKey(Key.create(FeedbackSession.class,
                        FeedbackSession.generateId(entityToCreate.getFeedbackSessionName(), entityToCreate.getCourseId())))
                .keys()
                .list()
                .isEmpty();
    }

    @Override
    FeedbackSessionAttributes makeAttributes(FeedbackSession entity) {
        assert entity != null;

        return FeedbackSessionAttributes.valueOf(entity);
    }
}
