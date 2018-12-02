package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.QueryKeys;

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
                .filter("endTime <", Instant.ofEpochMilli(rangeEnd.toEpochMilli()).plus(Duration.ofDays(30)))
                .list();

        List<FeedbackSession> startEntities = load()
                .filter("startTime <", rangeEnd)
                .filter("startTime >", Instant.ofEpochMilli(rangeStart.toEpochMilli()).minus(Duration.ofDays(30)))
                .list();

        // remove duplications
        endEntities.removeAll(startEntities);
        endEntities.addAll(startEntities);

        return makeAttributes(endEntities);
    }

    /**
     * Gets a feedback session that is not soft-deleted.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return null if not found or soft-deleted.
     */
    public FeedbackSessionAttributes getFeedbackSession(String courseId, String feedbackSessionName) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        FeedbackSessionAttributes feedbackSession =
                makeAttributesOrNull(getFeedbackSessionEntity(feedbackSessionName, courseId),
                "Trying to get non-existent Session: " + feedbackSessionName + "/" + courseId);

        if (feedbackSession != null && feedbackSession.isSessionDeleted()) {
            log.info("Trying to access soft-deleted session: " + feedbackSessionName + "/" + courseId);
            return null;
        }
        return feedbackSession;
    }

    /**
     * Gets a soft-deleted feedback session.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return null if not found or not soft-deleted.
     */
    public FeedbackSessionAttributes getSoftDeletedFeedbackSession(String courseId, String feedbackSessionName) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        FeedbackSessionAttributes feedbackSession =
                makeAttributesOrNull(getFeedbackSessionEntity(feedbackSessionName, courseId),
                "Trying to get non-existent Session: " + feedbackSessionName + "/" + courseId);

        if (feedbackSession != null && !feedbackSession.isSessionDeleted()) {
            log.info(feedbackSessionName + "/" + courseId + " is not soft-deleted!");
            return null;
        }

        return feedbackSession;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return a list of all sessions for the given course expect those in the Recycle Bin. Otherwise returns an empty list.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getFeedbackSessionEntitiesForCourse(courseId)).stream()
                .filter(session -> !session.isSessionDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return a list of sessions for the given course in the Recycle Bin. Otherwise returns an empty list.
     */
    public List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

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
        fs.setDeletedTime(newAttributes.getDeletedTime());
        fs.setStartTime(newAttributes.getStartTime());
        fs.setEndTime(newAttributes.getEndTime());
        fs.setSessionVisibleFromTime(newAttributes.getSessionVisibleFromTime());
        fs.setResultsVisibleFromTime(newAttributes.getResultsVisibleFromTime());
        fs.setTimeZone(newAttributes.getTimeZone().getId());
        fs.setGracePeriod(newAttributes.getGracePeriodMinutes());
        fs.setSentOpenEmail(newAttributes.isSentOpenEmail());
        fs.setSentClosingEmail(newAttributes.isSentClosingEmail());
        fs.setSentClosedEmail(newAttributes.isSentClosedEmail());
        fs.setSentPublishedEmail(newAttributes.isSentPublishedEmail());
        fs.setIsOpeningEmailEnabled(newAttributes.isOpeningEmailEnabled());
        fs.setSendClosingEmail(newAttributes.isClosingEmailEnabled());
        fs.setSendPublishedEmail(newAttributes.isPublishedEmailEnabled());

        saveEntity(fs, newAttributes);
    }

    // The objectify library does not support throwing checked exceptions inside transactions
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public void updateFeedbackSessionsTimeZoneForCourse(String courseId, ZoneId courseTimeZone) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseTimeZone);

        List<Key<FeedbackSession>> sessionKeys = getFeedbackSessionKeysForCourse(courseId);
        for (Key<FeedbackSession> sessionKey : sessionKeys) {
            try {
                ofy().transact(new VoidWork() {
                    @Override
                    public void vrun() {
                        FeedbackSession session = ofy().load().key(sessionKey).now();
                        if (session == null) {
                            throw new RuntimeException(new EntityDoesNotExistException(
                                    ERROR_UPDATE_NON_EXISTENT + sessionKey.getName()));
                        }
                        session.setTimeZone(courseTimeZone.getId());
                        saveEntity(session);
                    }
                });
            } catch (RuntimeException e) {
                if (e.getCause() instanceof EntityDoesNotExistException) {
                    log.severe(e.getMessage());
                    continue;
                }
                throw e;
            }
        }
    }

    public void addInstructorRespondent(String email, FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<String> emails = new ArrayList<>();
        emails.add(email);
        addInstructorRespondents(emails, feedbackSession);
    }

    // The objectify library does not support throwing checked exceptions inside transactions
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public void addInstructorRespondents(List<String> emails, FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, emails);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        try {
            ofy().transact(new VoidWork() {
                @Override
                public void vrun() {
                    FeedbackSession fs = getEntity(feedbackSession);
                    if (fs == null) {
                        throw new RuntimeException(new EntityDoesNotExistException(
                                ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString()));
                    }

                    fs.getRespondingInstructorList().addAll(emails);

                    saveEntity(fs, feedbackSession);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof EntityDoesNotExistException) {
                throw (EntityDoesNotExistException) e.getCause();
            }
            throw e;
        }
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

    // The objectify library does not support throwing checked exceptions inside transactions
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public void deleteInstructorRespondent(String email, FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        try {
            ofy().transact(new VoidWork() {
                @Override
                public void vrun() {
                    FeedbackSession fs = getEntity(feedbackSession);
                    if (fs == null) {
                        throw new RuntimeException(new EntityDoesNotExistException(
                                ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString()));
                    }

                    fs.getRespondingInstructorList().remove(email);

                    saveEntity(fs, feedbackSession);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof EntityDoesNotExistException) {
                throw (EntityDoesNotExistException) e.getCause();
            }
            throw e;
        }
    }

    // The objectify library does not support throwing checked exceptions inside transactions
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public void addStudentRespondents(List<String> emails, FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, emails);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        try {
            ofy().transact(new VoidWork() {
                @Override
                public void vrun() {
                    FeedbackSession fs = getEntity(feedbackSession);
                    if (fs == null) {
                        throw new RuntimeException(new EntityDoesNotExistException(
                                ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString()));
                    }

                    fs.getRespondingStudentList().addAll(emails);

                    saveEntity(fs, feedbackSession);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof EntityDoesNotExistException) {
                throw (EntityDoesNotExistException) e.getCause();
            }
            throw e;
        }
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

    // The objectify library does not support throwing checked exceptions inside transactions
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public void deleteStudentRespondent(String email, FeedbackSessionAttributes feedbackSession)
            throws EntityDoesNotExistException, InvalidParametersException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        try {
            ofy().transact(new VoidWork() {
                @Override
                public void vrun() {
                    FeedbackSession fs = getEntity(feedbackSession);
                    if (fs == null) {
                        throw new RuntimeException(new EntityDoesNotExistException(
                                ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString()));
                    }

                    fs.getRespondingStudentList().remove(email);

                    saveEntity(fs, feedbackSession);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof EntityDoesNotExistException) {
                throw (EntityDoesNotExistException) e.getCause();
            }
            throw e;
        }
    }

    public void deleteFeedbackSessionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        deleteFeedbackSessionsForCourses(Arrays.asList(courseId));
    }

    public void deleteFeedbackSessionsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

        ofy().delete().keys(load().filter("courseId in", courseIds).keys()).now();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesForCourse(String courseId) {
        return load().filter("courseId =", courseId).list();
    }

    private List<Key<FeedbackSession>> getFeedbackSessionKeysForCourse(String courseId) {
        return load().filter("courseId =", courseId).keys().list();
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
        return load().id(feedbackSessionName + "%" + courseId).now();
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

        return FeedbackSessionAttributes.valueOf(entity);
    }
}
