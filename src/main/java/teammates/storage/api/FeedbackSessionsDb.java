package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.AttributesDeletionQuery;
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
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        FeedbackSessionAttributes feedbackSession =
                makeAttributesOrNull(getFeedbackSessionEntity(feedbackSessionName, courseId));

        if (feedbackSession != null && feedbackSession.isSessionDeleted()) {
            log.info("Trying to access soft-deleted session: " + feedbackSessionName + "/" + courseId);
            return null;
        }
        return feedbackSession;
    }

    /**
     * Gets a list of feedback sessions within the given time range.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsWithinTimeRange(Instant rangeStart, Instant rangeEnd) {

        List<FeedbackSession> feedbackSessionList = new LinkedList<>();

        List<FeedbackSession> startEntities = load()
                .filter("startTime >=", rangeStart)
                .filter("startTime <", rangeEnd)
                .list();
        List<FeedbackSession> endEntities = load()
                .filter("endTime >=", rangeStart)
                .filter("endTime <", rangeEnd)
                .list();
        List<FeedbackSession> resultsVisibleEntities = load()
                .filter("resultsVisibleFromTime >", rangeStart)
                .filter("resultsVisibleFromTime <=", rangeEnd)
                .list();

        endEntities.removeAll(startEntities);
        resultsVisibleEntities.removeAll(startEntities);
        resultsVisibleEntities.removeAll(endEntities);

        feedbackSessionList.addAll(startEntities);
        feedbackSessionList.addAll(endEntities);
        feedbackSessionList.addAll(resultsVisibleEntities);

        return makeAttributes(feedbackSessionList).stream()
                .sorted(Comparator.comparing(FeedbackSessionAttributes::getStartTime))
                .filter(fs -> !fs.isSessionDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Gets a soft-deleted feedback session.
     *
     * @return null if not found or not soft-deleted.
     */
    public FeedbackSessionAttributes getSoftDeletedFeedbackSession(String courseId, String feedbackSessionName) {
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

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
        Assumption.assertNotNull(courseId);

        return makeAttributes(getFeedbackSessionEntitiesForCourse(courseId)).stream()
                .filter(session -> !session.isSessionDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of sessions for the given course that are soft-deleted.
     */
    public List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsForCourse(String courseId) {
        Assumption.assertNotNull(courseId);

        return makeAttributes(getFeedbackSessionEntitiesForCourse(courseId)).stream()
                .filter(FeedbackSessionAttributes::isSessionDeleted)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of undeleted feedback sessions which start within the last 2 hours
     * and possibly need an open email to be sent.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingOpenEmail() {
        return makeAttributes(getFeedbackSessionEntitiesPossiblyNeedingOpenEmail()).stream()
                .filter(session -> !session.isSessionDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of undeleted feedback sessions which end in the future (2 hour ago onward)
     * and possibly need a closing email to be sent.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingClosingEmail() {
        return makeAttributes(getFeedbackSessionEntitiesPossiblyNeedingClosingEmail()).stream()
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
     * <p>The update will be done in a transaction.
     *
     * @return updated feedback session
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    // The objectify library does not support throwing checked exceptions inside transactions
    public FeedbackSessionAttributes updateFeedbackSession(FeedbackSessionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(updateOptions);

        FeedbackSessionAttributes[] newAttributesFinal = new FeedbackSessionAttributes[] { null };
        try {
            FeedbackSessionsDb thisDb = this;
            ofy().transact(new VoidWork() {
                @Override
                public void vrun() {
                    FeedbackSession feedbackSession =
                            getFeedbackSessionEntity(updateOptions.getFeedbackSessionName(), updateOptions.getCourseId());
                    if (feedbackSession == null) {
                        throw new RuntimeException(
                                new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions));
                    }

                    newAttributesFinal[0] = makeAttributes(feedbackSession);
                    FeedbackSessionAttributes newAttributes = newAttributesFinal[0];
                    newAttributes.update(updateOptions);

                    newAttributes.sanitizeForSaving();
                    if (!newAttributes.isValid()) {
                        throw new RuntimeException(
                                new InvalidParametersException(newAttributes.getInvalidityInfo()));
                    }

                    // update only if change
                    boolean hasSameAttributes =
                            thisDb.<String>hasSameValue(feedbackSession.getInstructions(), newAttributes.getInstructions())
                            && thisDb.<Instant>hasSameValue(feedbackSession.getStartTime(), newAttributes.getStartTime())
                            && thisDb.<Instant>hasSameValue(feedbackSession.getEndTime(), newAttributes.getEndTime())
                            && thisDb.<Instant>hasSameValue(
                                    feedbackSession.getSessionVisibleFromTime(), newAttributes.getSessionVisibleFromTime())
                            && thisDb.<Instant>hasSameValue(
                                    feedbackSession.getResultsVisibleFromTime(), newAttributes.getResultsVisibleFromTime())
                            && thisDb.<String>hasSameValue(
                                    feedbackSession.getTimeZone(), newAttributes.getTimeZone().getId())
                            && thisDb.<Long>hasSameValue(
                                    feedbackSession.getGracePeriod(), newAttributes.getGracePeriodMinutes())
                            && thisDb.<Boolean>hasSameValue(
                                    feedbackSession.isSentOpenEmail(), newAttributes.isSentOpenEmail())
                            && thisDb.<Boolean>hasSameValue(
                                    feedbackSession.isSentClosingEmail(), newAttributes.isSentClosingEmail())
                            && thisDb.<Boolean>hasSameValue(
                                    feedbackSession.isSentClosedEmail(), newAttributes.isSentClosedEmail())
                            && thisDb.<Boolean>hasSameValue(
                                    feedbackSession.isSentPublishedEmail(), newAttributes.isSentPublishedEmail())
                            && thisDb.<Boolean>hasSameValue(
                                    feedbackSession.isClosingEmailEnabled(), newAttributes.isClosingEmailEnabled())
                            && thisDb.<Boolean>hasSameValue(
                                    feedbackSession.isPublishedEmailEnabled(), newAttributes.isPublishedEmailEnabled());
                    if (hasSameAttributes) {
                        log.info(String.format(
                                OPTIMIZED_SAVING_POLICY_APPLIED, FeedbackSession.class.getSimpleName(), updateOptions));
                        newAttributesFinal[0] = makeAttributes(feedbackSession);
                        return;
                    }

                    feedbackSession.setInstructions(newAttributes.getInstructions());
                    feedbackSession.setStartTime(newAttributes.getStartTime());
                    feedbackSession.setEndTime(newAttributes.getEndTime());
                    feedbackSession.setSessionVisibleFromTime(newAttributes.getSessionVisibleFromTime());
                    feedbackSession.setResultsVisibleFromTime(newAttributes.getResultsVisibleFromTime());
                    feedbackSession.setTimeZone(newAttributes.getTimeZone().getId());
                    feedbackSession.setGracePeriod(newAttributes.getGracePeriodMinutes());
                    feedbackSession.setSentOpenEmail(newAttributes.isSentOpenEmail());
                    feedbackSession.setSentClosingEmail(newAttributes.isSentClosingEmail());
                    feedbackSession.setSentClosedEmail(newAttributes.isSentClosedEmail());
                    feedbackSession.setSentPublishedEmail(newAttributes.isSentPublishedEmail());
                    feedbackSession.setSendClosingEmail(newAttributes.isClosingEmailEnabled());
                    feedbackSession.setSendPublishedEmail(newAttributes.isPublishedEmailEnabled());

                    saveEntity(feedbackSession);

                    newAttributesFinal[0] = makeAttributes(feedbackSession);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof EntityDoesNotExistException) {
                throw (EntityDoesNotExistException) e.getCause();
            } else if (e.getCause() instanceof InvalidParametersException) {
                throw (InvalidParametersException) e.getCause();
            } else {
                throw e;
            }
        }
        return newAttributesFinal[0];
    }

    /**
     * Soft-deletes a specific feedback session by its name and course id.
     *
     * @return Soft-deletion time of the feedback session.
     */
    public Instant softDeleteFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);

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
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);

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
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        deleteEntity(Key.create(FeedbackSession.class, FeedbackSession.generateId(feedbackSessionName, courseId)));
    }

    /**
     * Deletes sessions using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackSessions(AttributesDeletionQuery query) {
        Assumption.assertNotNull(query);

        Query<FeedbackSession> entitiesToDelete = load().project();
        if (query.isCourseIdPresent()) {
            entitiesToDelete = entitiesToDelete.filter("courseId =", query.getCourseId());
        }

        deleteEntity(entitiesToDelete.keys().list().toArray(new Key<?>[0]));
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesForCourse(String courseId) {
        return load().filter("courseId =", courseId).list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingOpenEmail() {
        return load()
                .filter("startTime >", TimeHelper.getInstantDaysOffsetFromNow(-2))
                .filter("sentOpenEmail =", false)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingClosingEmail() {
        return load()
                .filter("endTime >", TimeHelper.getInstantDaysOffsetFromNow(-2))
                .filter("sentClosingEmail =", false)
                .filter("isClosingEmailEnabled =", true)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingClosedEmail() {
        return load()
                .filter("endTime >", TimeHelper.getInstantDaysOffsetFromNow(-2))
                .filter("sentClosedEmail =", false)
                .filter("isClosingEmailEnabled =", true)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingPublishedEmail() {
        return load()
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
                .list()
                .isEmpty();
    }

    @Override
    FeedbackSessionAttributes makeAttributes(FeedbackSession entity) {
        Assumption.assertNotNull(entity);

        return FeedbackSessionAttributes.valueOf(entity);
    }
}
