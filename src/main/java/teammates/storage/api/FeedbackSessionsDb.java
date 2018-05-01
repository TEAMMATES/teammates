package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

    public List<FeedbackSessionAttributes> getAllOpenFeedbackSessions(Instant rangeStart, Instant rangeEnd) {
        List<FeedbackSessionAttributes> list = new LinkedList<>();

        // To retrieve legacy data where local dates are stored instead of UTC
        // TODO: remove after all legacy data has been converted
        Instant start = rangeStart.minus(Duration.ofHours(25));
        Instant end = rangeEnd.plus(Duration.ofHours(25));

        List<FeedbackSession> endEntities = load()
                .filter("endTime >", TimeHelper.convertInstantToDate(start))
                .filter("endTime <=", TimeHelper.convertInstantToDate(end))
                .list();

        List<FeedbackSession> startEntities = load()
                .filter("startTime >=", TimeHelper.convertInstantToDate(start))
                .filter("startTime <", TimeHelper.convertInstantToDate(end))
                .list();

        List<FeedbackSession> endTimeEntities = new ArrayList<>(endEntities);
        List<FeedbackSession> startTimeEntities = new ArrayList<>(startEntities);

        endTimeEntities.removeAll(startTimeEntities);
        startTimeEntities.removeAll(endTimeEntities);
        endTimeEntities.addAll(startTimeEntities);

        // TODO: remove after all legacy data has been converted
        for (FeedbackSession feedbackSession : endTimeEntities) {
            FeedbackSessionAttributes fs = makeAttributes(feedbackSession);
            Instant fsStart = fs.getStartTime();
            Instant fsEnd = fs.getEndTime();

            boolean isStartTimeWithinRange = (fsStart.isAfter(rangeStart) || fsStart.equals(rangeStart))
                    && fsStart.isBefore(rangeEnd);
            boolean isEndTimeWithinRange = fsEnd.isAfter(rangeStart) && (fsEnd.isBefore(rangeEnd) || fsEnd.equals(rangeEnd));

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
                .filter("startTime >", TimeHelper.convertInstantToDate(TimeHelper.getInstantDaysOffsetFromNow(-2)))
                .filter("sentOpenEmail =", false)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingClosingEmail() {
        return load()
                .filter("endTime >", TimeHelper.convertInstantToDate(TimeHelper.getInstantDaysOffsetFromNow(-2)))
                .filter("sentClosingEmail =", false)
                .filter("isClosingEmailEnabled =", true)
                .list();
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingClosedEmail() {
        return load()
                .filter("endTime >", TimeHelper.convertInstantToDate(TimeHelper.getInstantDaysOffsetFromNow(-2)))
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
