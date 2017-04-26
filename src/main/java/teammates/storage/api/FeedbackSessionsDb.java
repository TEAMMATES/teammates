package teammates.storage.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.FeedbackSession;

/**
 * Handles CRUD operations for feedback sessions.
 *
 * @see FeedbackSession
 * @see FeedbackSessionAttributes
 */
public class FeedbackSessionsDb extends EntitiesDb {

    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Feedback Session : ";

    private static final Logger log = Logger.getLogger();

    public void createFeedbackSessions(Collection<FeedbackSessionAttributes> feedbackSessionsToAdd)
            throws InvalidParametersException {
        List<EntityAttributes> feedbackSessionsToUpdate = createEntities(feedbackSessionsToAdd);
        for (EntityAttributes entity : feedbackSessionsToUpdate) {
            FeedbackSessionAttributes session = (FeedbackSessionAttributes) entity;
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

        List<FeedbackSessionAttributes> list = new LinkedList<FeedbackSessionAttributes>();

        final Query endTimequery = getPm().newQuery("SELECT FROM teammates.storage.entity.FeedbackSession "
                                                    + "WHERE this.endTime>rangeStart && this.endTime<=rangeEnd "
                                                    + " PARAMETERS java.util.Date rangeStart, "
                                                    + "java.util.Date rangeEnd");

        final Query startTimequery = getPm().newQuery("SELECT FROM teammates.storage.entity.FeedbackSession "
                                                      + "WHERE this.startTime>=rangeStart && this.startTime<rangeEnd "
                                                      + "PARAMETERS java.util.Date rangeStart, "
                                                      + "java.util.Date rangeEnd");

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);

        Date curStart = TimeHelper.convertToUserTimeZone(startCal, -25).getTime();
        Date curEnd = TimeHelper.convertToUserTimeZone(endCal, 25).getTime();

        @SuppressWarnings("unchecked")
        List<FeedbackSession> endEntities = (List<FeedbackSession>) endTimequery.execute(curStart, curEnd);
        @SuppressWarnings("unchecked")
        List<FeedbackSession> startEntities = (List<FeedbackSession>) startTimequery.execute(curStart, curEnd);

        List<FeedbackSession> endTimeEntities = new ArrayList<FeedbackSession>(endEntities);
        List<FeedbackSession> startTimeEntities = new ArrayList<FeedbackSession>(startEntities);

        endTimeEntities.removeAll(startTimeEntities);
        startTimeEntities.removeAll(endTimeEntities);
        endTimeEntities.addAll(startTimeEntities);

        Iterator<FeedbackSession> it = endTimeEntities.iterator();

        while (it.hasNext()) {
            FeedbackSession feedbackSession = it.next();

            // Continue to the next element if the current element is deleted
            if (JDOHelper.isDeleted(feedbackSession)) {
                continue;
            }

            startCal.setTime(start);
            endCal.setTime(end);
            FeedbackSessionAttributes fs = new FeedbackSessionAttributes(feedbackSession);

            Date standardStart = TimeHelper.convertToUserTimeZone(startCal, fs.getTimeZone() - zone).getTime();
            Date standardEnd = TimeHelper.convertToUserTimeZone(endCal, fs.getTimeZone() - zone).getTime();

            boolean isStartTimeWithinRange = TimeHelper.isTimeWithinPeriod(standardStart,
                                                                           standardEnd,
                                                                           fs.getStartTime(),
                                                                           true,
                                                                           false);
            boolean isEndTimeWithinRange = TimeHelper.isTimeWithinPeriod(standardStart,
                                                                         standardEnd,
                                                                         fs.getEndTime(),
                                                                         false,
                                                                         true);

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

        FeedbackSession fs = getFeedbackSessionEntity(feedbackSessionName, courseId);

        if (fs == null) {
            log.info("Trying to get non-existent Session: " + feedbackSessionName + "/" + courseId);
            return null;
        }
        return new FeedbackSessionAttributes(fs);

    }

    /**
     * Returns empty list if none found.
     * @deprecated Not scalable. Created for data migration purposes.
     */
    @Deprecated
    public List<FeedbackSessionAttributes> getAllFeedbackSessions() {
        List<FeedbackSession> allFs = getAllFeedbackSessionEntities();
        List<FeedbackSessionAttributes> fsaList = new ArrayList<FeedbackSessionAttributes>();

        for (FeedbackSession fs : allFs) {
            if (!JDOHelper.isDeleted(fs)) {
                fsaList.add(new FeedbackSessionAttributes(fs));
            }
        }
        return fsaList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no sessions are found for the given course.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<FeedbackSession> fsList = getFeedbackSessionEntitiesForCourse(courseId);
        List<FeedbackSessionAttributes> fsaList = new ArrayList<FeedbackSessionAttributes>();

        for (FeedbackSession fs : fsList) {
            if (!JDOHelper.isDeleted(fs)) {
                fsaList.add(new FeedbackSessionAttributes(fs));
            }
        }
        return fsaList;
    }

    /**
     * Returns An empty list if no sessions are found that have unsent open emails.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingOpenEmail() {

        List<FeedbackSession> fsList = getFeedbackSessionEntitiesPossiblyNeedingOpenEmail();
        List<FeedbackSessionAttributes> fsaList = new ArrayList<FeedbackSessionAttributes>();

        for (FeedbackSession fs : fsList) {
            if (!JDOHelper.isDeleted(fs)) {
                fsaList.add(new FeedbackSessionAttributes(fs));
            }
        }
        return fsaList;
    }

    /**
     * Returns An empty list if no sessions are found that have unsent closing emails.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingClosingEmail() {

        List<FeedbackSession> fsList = getFeedbackSessionEntitiesPossiblyNeedingClosingEmail();
        List<FeedbackSessionAttributes> fsaList = new ArrayList<FeedbackSessionAttributes>();

        for (FeedbackSession fs : fsList) {
            if (!JDOHelper.isDeleted(fs)) {
                fsaList.add(new FeedbackSessionAttributes(fs));
            }
        }
        return fsaList;
    }

    /**
     * Returns An empty list if no sessions are found that have unsent closed emails.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingClosedEmail() {

        List<FeedbackSession> fsList = getFeedbackSessionEntitiesPossiblyNeedingClosedEmail();
        List<FeedbackSessionAttributes> fsaList = new ArrayList<FeedbackSessionAttributes>();

        for (FeedbackSession fs : fsList) {
            if (!JDOHelper.isDeleted(fs)) {
                fsaList.add(new FeedbackSessionAttributes(fs));
            }
        }
        return fsaList;
    }

    /**
     * Returns An empty list if no sessions are found that have unsent published emails.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsPossiblyNeedingPublishedEmail() {

        List<FeedbackSession> fsList = getFeedbackSessionEntitiesPossiblyNeedingPublishedEmail();
        List<FeedbackSessionAttributes> fsaList = new ArrayList<FeedbackSessionAttributes>();

        for (FeedbackSession fs : fsList) {
            if (!JDOHelper.isDeleted(fs)) {
                fsaList.add(new FeedbackSessionAttributes(fs));
            }
        }
        return fsaList;
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

        Assumption.assertNotNull(
                Const.StatusCodes.DBLEVEL_NULL_INPUT,
                newAttributes);

        newAttributes.sanitizeForSaving();

        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        FeedbackSession fs = (FeedbackSession) getEntity(newAttributes);

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

        log.info(newAttributes.getBackupIdentifier());
        getPm().close();
    }

    public void addInstructorRespondent(String email, FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {

        List<String> emails = new ArrayList<String>();
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

        FeedbackSession fs = (FeedbackSession) getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        fs.getRespondingInstructorList().addAll(emails);

        log.info(feedbackSession.getBackupIdentifier());
        getPm().close();
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

        FeedbackSession fs = (FeedbackSession) getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        if (fs.getRespondingInstructorList().contains(oldEmail)) {
            fs.getRespondingInstructorList().remove(oldEmail);
            fs.getRespondingInstructorList().add(newEmail);
        }

        log.info(feedbackSession.getBackupIdentifier());
        getPm().close();
    }

    public void clearInstructorRespondents(FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        FeedbackSession fs = (FeedbackSession) getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        fs.getRespondingInstructorList().clear();

        log.info(feedbackSession.getBackupIdentifier());
        getPm().close();
    }

    public void addStudentRespondent(String email, FeedbackSessionAttributes feedbackSession)
            throws EntityDoesNotExistException, InvalidParametersException {

        List<String> emails = new ArrayList<String>();
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

        FeedbackSession fs = (FeedbackSession) getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        fs.getRespondingInstructorList().remove(email);

        log.info(feedbackSession.getBackupIdentifier());
        getPm().close();
    }

    public void addStudentRespondents(List<String> emails, FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, emails);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        FeedbackSession fs = (FeedbackSession) getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        fs.getRespondingStudentList().addAll(emails);

        log.info(feedbackSession.getBackupIdentifier());
        getPm().close();
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

        FeedbackSession fs = (FeedbackSession) getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        if (fs.getRespondingStudentList().contains(oldEmail)) {
            fs.getRespondingStudentList().remove(oldEmail);
            fs.getRespondingStudentList().add(newEmail);
        }

        log.info(feedbackSession.getBackupIdentifier());
        getPm().close();
    }

    public void clearStudentRespondents(FeedbackSessionAttributes feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        FeedbackSession fs = (FeedbackSession) getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        fs.getRespondingStudentList().clear();

        log.info(feedbackSession.getBackupIdentifier());
        getPm().close();
    }

    public void deleteStudentRespondent(String email, FeedbackSessionAttributes feedbackSession)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSession);

        feedbackSession.sanitizeForSaving();

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        FeedbackSession fs = (FeedbackSession) getEntity(feedbackSession);
        if (fs == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + feedbackSession.toString());
        }

        fs.getRespondingStudentList().remove(email);

        log.info(feedbackSession.getBackupIdentifier());
        getPm().close();
    }

    public void deleteFeedbackSessionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        deleteFeedbackSessionsForCourses(Arrays.asList(courseId));
    }

    public void deleteFeedbackSessionsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

        getFeedbackSessionsForCoursesQuery(courseIds)
            .deletePersistentAll();
    }

    private QueryWithParams getFeedbackSessionsForCoursesQuery(List<String> courseIds) {
        Query q = getPm().newQuery(FeedbackSession.class);
        q.setFilter(":p.contains(courseId)");
        return new QueryWithParams(q, new Object[] {courseIds});
    }

    @SuppressWarnings("unchecked")
    private List<FeedbackSession> getAllFeedbackSessionEntities() {
        Query q = getPm().newQuery(FeedbackSession.class);

        return (List<FeedbackSession>) q.execute();
    }

    @SuppressWarnings("unchecked")
    private List<FeedbackSession> getFeedbackSessionEntitiesForCourse(String courseId) {
        Query q = getPm().newQuery(FeedbackSession.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");

        return (List<FeedbackSession>) q.execute(courseId);
    }

    @SuppressWarnings("unchecked")
    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingOpenEmail() {
        Query q = getPm().newQuery(FeedbackSession.class);
        q.declareParameters("java.util.Date startTimeParam, boolean sentParam");
        q.setFilter("startTime > startTimeParam && sentOpenEmail == sentParam");

        // only get sessions with startTime within the past two days to reduce the number of sessions returned
        Date d = TimeHelper.getDateOffsetToCurrentTime(-2);

        return (List<FeedbackSession>) q.execute(d, false);
    }

    @SuppressWarnings("unchecked")
    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingClosingEmail() {
        Query q = getPm().newQuery(FeedbackSession.class);
        q.declareParameters("java.util.Date endTimeParam, boolean sentParam, boolean enableParam");
        q.setFilter("endTime > endTimeParam && sentClosingEmail == sentParam && isClosingEmailEnabled == enableParam");

        // only get sessions with endTime within the past two days to reduce the number of sessions returned
        Date d = TimeHelper.getDateOffsetToCurrentTime(-2);

        return (List<FeedbackSession>) q.execute(d, false, true);
    }

    @SuppressWarnings("unchecked")
    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingClosedEmail() {
        Query q = getPm().newQuery(FeedbackSession.class);
        q.declareParameters("java.util.Date endTimeParam, boolean sentParam, boolean enableParam");
        q.setFilter("endTime > endTimeParam && sentClosedEmail == sentParam && isClosingEmailEnabled == enableParam");

        // only get sessions with endTime within the past two days to reduce the number of sessions returned
        Date d = TimeHelper.getDateOffsetToCurrentTime(-2);

        return (List<FeedbackSession>) q.execute(d, false, true);
    }

    @SuppressWarnings("unchecked")
    private List<FeedbackSession> getFeedbackSessionEntitiesPossiblyNeedingPublishedEmail() {
        Query q = getPm().newQuery(FeedbackSession.class);
        q.declareParameters("boolean sentParam, boolean enableParam, Enum notTypeParam");
        q.setFilter("sentPublishedEmail == sentParam && isPublishedEmailEnabled == enableParam "
                    + "&& feedbackSessionType != notTypeParam");

        return (List<FeedbackSession>) q.execute(false, true, FeedbackSessionType.PRIVATE);
    }

    private FeedbackSession getFeedbackSessionEntity(String feedbackSessionName, String courseId) {

        Query q = getPm().newQuery(FeedbackSession.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");

        @SuppressWarnings("unchecked")
        List<FeedbackSession> feedbackSessionList =
                (List<FeedbackSession>) q.execute(feedbackSessionName, courseId);

        if (feedbackSessionList.isEmpty() || JDOHelper.isDeleted(feedbackSessionList.get(0))) {
            return null;
        }

        return feedbackSessionList.get(0);
    }

    @Override
    protected Object getEntity(EntityAttributes attributes) {
        FeedbackSessionAttributes feedbackSessionToGet = (FeedbackSessionAttributes) attributes;
        return getFeedbackSessionEntity(feedbackSessionToGet.getFeedbackSessionName(),
                                        feedbackSessionToGet.getCourseId());
    }

    @Override
    protected QueryWithParams getEntityKeyOnlyQuery(EntityAttributes attributes) {
        Class<?> entityClass = FeedbackSession.class;
        String primaryKeyName = FeedbackSession.PRIMARY_KEY_NAME;
        FeedbackSessionAttributes fsa = (FeedbackSessionAttributes) attributes;

        Query q = getPm().newQuery(entityClass);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");

        return new QueryWithParams(q, new Object[] {fsa.getFeedbackSessionName(), fsa.getCourseId()}, primaryKeyName);
    }
}
