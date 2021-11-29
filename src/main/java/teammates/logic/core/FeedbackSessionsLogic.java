package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;
import teammates.storage.api.FeedbackSessionsDb;

/**
 * Handles operations related to feedback sessions.
 *
 * @see FeedbackSessionAttributes
 * @see FeedbackSessionsDb
 */
public final class FeedbackSessionsLogic {

    private static final Logger log = Logger.getLogger();

    private static final String ERROR_NON_EXISTENT_FS_STRING_FORMAT = "Trying to %s a non-existent feedback session: ";
    private static final String ERROR_NON_EXISTENT_FS_UPDATE = String.format(ERROR_NON_EXISTENT_FS_STRING_FORMAT, "update");
    private static final String ERROR_NON_EXISTENT_FS_CHECK = String.format(ERROR_NON_EXISTENT_FS_STRING_FORMAT, "check");
    private static final String ERROR_FS_ALREADY_PUBLISH = "Error publishing feedback session: "
                                                           + "Session has already been published.";
    private static final String ERROR_FS_ALREADY_UNPUBLISH = "Error unpublishing feedback session: "
                                                             + "Session has already been unpublished.";

    private static final int NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT = 24;
    private static final int NUMBER_OF_HOURS_BEFORE_OPENING_SOON_ALERT = 24;

    private static final FeedbackSessionsLogic instance = new FeedbackSessionsLogic();

    private final FeedbackSessionsDb fsDb = FeedbackSessionsDb.inst();

    private CoursesLogic coursesLogic;
    private FeedbackQuestionsLogic fqLogic;
    private FeedbackResponsesLogic frLogic;
    private FeedbackResponseCommentsLogic frcLogic;
    private InstructorsLogic instructorsLogic;
    private StudentsLogic studentsLogic;

    private FeedbackSessionsLogic() {
        // prevent initialization
    }

    public static FeedbackSessionsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        coursesLogic = CoursesLogic.inst();
        fqLogic = FeedbackQuestionsLogic.inst();
        frLogic = FeedbackResponsesLogic.inst();
        frcLogic = FeedbackResponseCommentsLogic.inst();
        instructorsLogic = InstructorsLogic.inst();
        studentsLogic = StudentsLogic.inst();
    }

    /**
     * Creates a feedback session.
     *
     * @return created feedback session
     * @throws InvalidParametersException if the session is not valid
     * @throws EntityAlreadyExistsException if the session already exist
     */
    public FeedbackSessionAttributes createFeedbackSession(FeedbackSessionAttributes fsa)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return fsDb.createEntity(fsa);
    }

    /**
     * Gets all ongoing feedback sessions.
     */
    public List<FeedbackSessionAttributes> getAllOngoingSessions(Instant rangeStart, Instant rangeEnd) {
        return fsDb.getAllOngoingSessions(rangeStart, rangeEnd);
    }

    /**
     * Gets a feedback session from the data storage.
     *
     * @return null if not found or in recycle bin.
     */
    public FeedbackSessionAttributes getFeedbackSession(String feedbackSessionName, String courseId) {
        return fsDb.getFeedbackSession(courseId, feedbackSessionName);
    }

    /**
     * Gets a feedback session from the recycle bin.
     *
     * @return null if not found.
     */
    public FeedbackSessionAttributes getFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId) {
        return fsDb.getSoftDeletedFeedbackSession(courseId, feedbackSessionName);
    }

    /**
     * Gets all feedback sessions of a course.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {
        return fsDb.getFeedbackSessionsForCourse(courseId);
    }

    /**
     * Returns a list of feedback sessions within the time range or an empty list if nothing was found.
     */
    public List<FeedbackSessionAttributes> getAllFeedbackSessionsWithinTimeRange(Instant rangeStart, Instant rangeEnd) {
        return fsDb.getFeedbackSessionsWithinTimeRange(rangeStart, rangeEnd);
    }

    /**
     * Gets a list of feedback sessions for instructors.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(
            List<InstructorAttributes> instructorList) {

        List<InstructorAttributes> courseNotDeletedInstructorList = instructorList.stream()
                .filter(instructor -> !coursesLogic.getCourse(instructor.getCourseId()).isCourseDeleted())
                .collect(Collectors.toList());

        List<FeedbackSessionAttributes> fsList = new ArrayList<>();

        for (InstructorAttributes instructor : courseNotDeletedInstructorList) {
            fsList.addAll(getFeedbackSessionsListForCourse(instructor.getCourseId()));
        }

        return fsList;
    }

    /**
     * Returns a {@code List} of feedback sessions in the Recycle Bin for the instructors.
     * <br>
     * Omits sessions if the corresponding courses are archived or in Recycle Bin
     */
    public List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsListForInstructors(
            List<InstructorAttributes> instructorList) {

        List<InstructorAttributes> courseNotDeletedInstructorList = instructorList.stream()
                .filter(instructor -> !coursesLogic.getCourse(instructor.getCourseId()).isCourseDeleted())
                .collect(Collectors.toList());

        List<FeedbackSessionAttributes> fsList = new ArrayList<>();

        for (InstructorAttributes instructor : courseNotDeletedInstructorList) {
            fsList.addAll(getSoftDeletedFeedbackSessionsListForCourse(instructor.getCourseId()));
        }

        return fsList;
    }

    /**
     * Criteria: must be published, publishEmail must be enabled and
     * resultsVisibleTime must be custom.
     *
     * @return returns a list of sessions that require automated emails to be
     *         sent as they are published
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent() {
        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsPossiblyNeedingPublishedEmail();
        log.info(String.format("Number of sessions under consideration: %d", sessions.size()));
        List<FeedbackSessionAttributes> sessionsToSendEmailsFor = new ArrayList<>();

        for (FeedbackSessionAttributes session : sessions) {
            // automated emails are required only for custom publish times
            if (session.isPublished()
                    && !TimeHelper.isSpecialTime(session.getResultsVisibleFromTime())
                    && !coursesLogic.getCourse(session.getCourseId()).isCourseDeleted()) {
                sessionsToSendEmailsFor.add(session);
            }
        }
        log.info(String.format("Number of sessions under consideration after filtering: %d",
                sessionsToSendEmailsFor.size()));
        return sessionsToSendEmailsFor;
    }

    /**
     * Gets a list of undeleted feedback sessions which start within the last 2 hours
     * and need an open email to be sent.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedOpenEmailsToBeSent() {
        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsPossiblyNeedingOpenEmail();
        List<FeedbackSessionAttributes> sessionsToSendEmailsFor = new ArrayList<>();
        log.info(String.format("Number of sessions under consideration: %d", sessions.size()));

        for (FeedbackSessionAttributes session : sessions) {
            if (session.isOpened() && !coursesLogic.getCourse(session.getCourseId()).isCourseDeleted()) {
                sessionsToSendEmailsFor.add(session);
            }
        }

        log.info(String.format("Number of sessions under consideration after filtering: %d",
                sessionsToSendEmailsFor.size()));
        return sessionsToSendEmailsFor;
    }

    /**
     * Returns true if the given email is the creator of the given session.
     */
    public boolean isCreatorOfSession(String feedbackSessionName, String courseId, String userEmail) {
        FeedbackSessionAttributes fs = getFeedbackSession(feedbackSessionName, courseId);
        return fs.getCreatorEmail().equals(userEmail);
    }

    private boolean isFeedbackSessionExists(String feedbackSessionName, String courseId) {
        return fsDb.getFeedbackSession(courseId, feedbackSessionName) != null;
    }

    /**
     * Returns true if the feedback session has question for students.
     */
    public boolean isFeedbackSessionHasQuestionForStudents(
            String feedbackSessionName,
            String courseId) throws EntityDoesNotExistException {
        if (!isFeedbackSessionExists(feedbackSessionName, courseId)) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_CHECK + courseId + "/" + feedbackSessionName);
        }

        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName,
                        courseId);

        return !allQuestions.isEmpty();
    }

    /**
     * Checks whether a student has completed a feedback session.
     *
     * <p> If there is no question for students, the feedback session is completed</p>
     */
    public boolean isFeedbackSessionCompletedByStudent(FeedbackSessionAttributes fsa, String userEmail) {
        if (frLogic.hasGiverRespondedForSession(userEmail, fsa.getFeedbackSessionName(), fsa.getCourseId())) {
            return true;
        }

        String feedbackSessionName = fsa.getFeedbackSessionName();
        String courseId = fsa.getCourseId();
        // if there is no question for students, session is complete
        return !fqLogic.sessionHasQuestions(feedbackSessionName, courseId);
    }

    /**
     * Checks whether an instructor has completed a feedback session.
     *
     * <p> If there is no question for instructors, the feedback session is completed</p>
     */
    public boolean isFeedbackSessionCompletedByInstructor(FeedbackSessionAttributes fsa, String userEmail)
            throws EntityDoesNotExistException {
        if (frLogic.hasGiverRespondedForSession(userEmail, fsa.getFeedbackSessionName(), fsa.getCourseId())) {
            return true;
        }

        String feedbackSessionName = fsa.getFeedbackSessionName();
        String courseId = fsa.getCourseId();
        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForInstructor(feedbackSessionName, courseId, userEmail);
        // if there is no question for instructor, session is complete
        return allQuestions.isEmpty();
    }

    /**
     * Updates the details of a feedback session by {@link FeedbackSessionAttributes.UpdateOptions}.
     *
     * <p>Adjust email sending status if necessary.
     *
     * @return updated feedback session
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSessionAttributes updateFeedbackSession(FeedbackSessionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSessionAttributes oldSession =
                fsDb.getFeedbackSession(updateOptions.getCourseId(), updateOptions.getFeedbackSessionName());

        if (oldSession == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + updateOptions.getCourseId()
                    + "/" + updateOptions.getFeedbackSessionName());
        }

        FeedbackSessionAttributes newSession = oldSession.getCopy();
        newSession.update(updateOptions);
        FeedbackSessionAttributes.UpdateOptions.Builder newUpdateOptions =
                FeedbackSessionAttributes.updateOptionsBuilder(updateOptions);

        // adjust email sending status

        // reset sentOpenEmail if the session has opened but is being un-opened
        // now, or else leave it as sent if so.
        if (oldSession.isSentOpenEmail()) {
            newUpdateOptions.withSentOpenEmail(newSession.isOpened());

            // also reset sentOpeningSoonEmail
            newUpdateOptions.withSentOpeningSoonEmail(
                    newSession.isOpened()
                            || newSession.isOpeningInHours(NUMBER_OF_HOURS_BEFORE_OPENING_SOON_ALERT));
        }

        // reset sentClosedEmail if the session has closed but is being un-closed
        // now, or else leave it as sent if so.
        if (oldSession.isSentClosedEmail()) {
            newUpdateOptions.withSentClosedEmail(newSession.isClosed());

            // also reset sentClosingEmail
            newUpdateOptions.withSentClosingEmail(
                    newSession.isClosed()
                            || newSession.isClosedAfter(NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT));
        }

        // reset sentPublishedEmail if the session has been published but is
        // going to be unpublished now, or else leave it as sent if so.
        if (oldSession.isSentPublishedEmail()) {
            newUpdateOptions.withSentPublishedEmail(newSession.isPublished());
        }

        return fsDb.updateFeedbackSession(newUpdateOptions.build());
    }

    /**
     * Updates all feedback sessions of {@code courseId} to have be in {@code courseTimeZone}.
     */
    public void updateFeedbackSessionsTimeZoneForCourse(String courseId, String courseTimeZone) {
        assert courseId != null;
        assert courseTimeZone != null;

        List<FeedbackSessionAttributes> fsForCourse = fsDb.getFeedbackSessionsForCourse(courseId);
        fsForCourse.forEach(fs -> {
            try {
                fsDb.updateFeedbackSession(
                        FeedbackSessionAttributes.updateOptionsBuilder(fs.getFeedbackSessionName(), fs.getCourseId())
                                .withTimeZone(courseTimeZone)
                                .build());
            } catch (EntityDoesNotExistException | InvalidParametersException e) {
                log.severe("Cannot adjust timezone of courses: " + e.getMessage());
            }
        });
    }

    /**
     * Publishes a feedback session.
     *
     * @return the published feedback session
     * @throws InvalidParametersException if session is already published
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSessionAttributes publishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        FeedbackSessionAttributes sessionToPublish = getFeedbackSession(feedbackSessionName, courseId);

        if (sessionToPublish == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }
        if (sessionToPublish.isPublished()) {
            throw new InvalidParametersException(ERROR_FS_ALREADY_PUBLISH);
        }

        return updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(sessionToPublish.getFeedbackSessionName(), sessionToPublish.getCourseId())
                        .withResultsVisibleFromTime(Instant.now())
                        .build());
    }

    /**
     * Unpublishes a feedback session.
     *
     * @return the unpublished feedback session
     * @throws InvalidParametersException if session is already unpublished
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSessionAttributes unpublishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        FeedbackSessionAttributes sessionToUnpublish = getFeedbackSession(feedbackSessionName, courseId);

        if (sessionToUnpublish == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }
        if (!sessionToUnpublish.isPublished()) {
            throw new InvalidParametersException(ERROR_FS_ALREADY_UNPUBLISH);
        }

        return updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(sessionToUnpublish.getFeedbackSessionName(), sessionToUnpublish.getCourseId())
                        .withResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER)
                        .build());
    }

    /**
     * Returns returns a list of sessions that are going to open in 24 hours.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsOpeningWithinTimeLimit() {
        List<FeedbackSessionAttributes> requiredSessions = new ArrayList<>();

        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsPossiblyNeedingOpeningSoonEmail();
        log.info(String.format("Number of sessions under consideration: %d", sessions.size()));

        for (FeedbackSessionAttributes session : sessions) {
            if (session.isOpeningWithinTimeLimit(NUMBER_OF_HOURS_BEFORE_OPENING_SOON_ALERT)
                    && !coursesLogic.getCourse(session.getCourseId()).isCourseDeleted()) {
                requiredSessions.add(session);
            }
        }

        log.info(String.format("Number of sessions under consideration after filtering: %d",
                requiredSessions.size()));
        return requiredSessions;
    }

    /**
     * Returns returns a list of sessions that are going to close within the next 24 hours.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsClosingWithinTimeLimit() {
        List<FeedbackSessionAttributes> requiredSessions = new ArrayList<>();

        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsPossiblyNeedingClosingEmail();
        log.info(String.format("Number of sessions under consideration: %d", sessions.size()));

        for (FeedbackSessionAttributes session : sessions) {
            if (session.isClosingWithinTimeLimit(NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT)
                    && !coursesLogic.getCourse(session.getCourseId()).isCourseDeleted()) {
                requiredSessions.add(session);
            }
        }

        log.info(String.format("Number of sessions under consideration after filtering: %d",
                requiredSessions.size()));
        return requiredSessions;
    }

    /**
     * Returns returns a list of sessions that were closed within past hour.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsClosedWithinThePastHour() {
        List<FeedbackSessionAttributes> requiredSessions = new ArrayList<>();
        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsPossiblyNeedingClosedEmail();
        log.info(String.format("Number of sessions under consideration: %d", sessions.size()));

        for (FeedbackSessionAttributes session : sessions) {
            // is session closed in the past 1 hour
            if (session.isClosedWithinPastHour()
                    && !coursesLogic.getCourse(session.getCourseId()).isCourseDeleted()) {
                requiredSessions.add(session);
            }
        }
        log.info(String.format("Number of sessions under consideration after filtering: %d",
                requiredSessions.size()));
        return requiredSessions;
    }

    /**
     * Deletes a feedback session cascade to its associated questions, responses and comments.
     */
    public void deleteFeedbackSessionCascade(String feedbackSessionName, String courseId) {
        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .withFeedbackSessionName(feedbackSessionName)
                .build();
        frcLogic.deleteFeedbackResponseComments(query);
        frLogic.deleteFeedbackResponses(query);
        fqLogic.deleteFeedbackQuestions(query);

        fsDb.deleteFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Deletes sessions using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackSessions(AttributesDeletionQuery query) {
        fsDb.deleteFeedbackSessions(query);
    }

    /**
     * Soft-deletes a specific feedback session to Recycle Bin.
     * @return the time when the feedback session is moved to the recycle bin
     */
    public Instant moveFeedbackSessionToRecycleBin(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        return fsDb.softDeleteFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Restores a specific feedback session from Recycle Bin.
     */
    public void restoreFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
        fsDb.restoreDeletedFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Gets the expected number of submissions for a feedback session.
     */
    public int getExpectedTotalSubmission(FeedbackSessionAttributes fsa) {
        List<StudentAttributes> students = studentsLogic.getStudentsForCourse(fsa.getCourseId());
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(fsa.getCourseId());
        List<FeedbackQuestionAttributes> questions =
                fqLogic.getFeedbackQuestionsForSession(fsa.getFeedbackSessionName(), fsa.getCourseId());
        List<FeedbackQuestionAttributes> studentQns = fqLogic.getFeedbackQuestionsForStudents(questions);

        int expectedTotal = 0;

        if (!studentQns.isEmpty()) {
            expectedTotal += students.size();
        }

        for (InstructorAttributes instructor : instructors) {
            List<FeedbackQuestionAttributes> instructorQns =
                    fqLogic.getFeedbackQuestionsForInstructor(questions, fsa.isCreator(instructor.getEmail()));
            if (!instructorQns.isEmpty()) {
                expectedTotal += 1;
            }
        }

        return expectedTotal;
    }

    /**
     * Gets the actual number of submissions for a feedback session.
     */
    public int getActualTotalSubmission(FeedbackSessionAttributes fsa) {
        return frLogic.getGiverSetThatAnswerFeedbackSession(fsa.getCourseId(), fsa.getFeedbackSessionName()).size();
    }

    private List<FeedbackSessionAttributes> getFeedbackSessionsListForCourse(String courseId) {

        return fsDb.getFeedbackSessionsForCourse(courseId);
    }

    private List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsListForCourse(String courseId) {

        return fsDb.getSoftDeletedFeedbackSessionsForCourse(courseId);
    }

    /**
     * Returns true if the feedback session has been attempted (i.e. any question is answered) by the given student.
     */
    public boolean isFeedbackSessionAttemptedByStudent(
            String feedbackSessionName,
            String courseId, String userEmail)
            throws EntityDoesNotExistException {

        if (!isFeedbackSessionExists(feedbackSessionName, courseId)) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_CHECK + courseId + "/" + feedbackSessionName);
        }

        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName,
                        courseId);

        for (FeedbackQuestionAttributes question : allQuestions) {
            //as long as one question is fully answered, student has attempted
            if (fqLogic.isQuestionFullyAnsweredByUser(question, userEmail)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the feedback session is viewable by the given student.
     */
    public boolean isFeedbackSessionViewableToStudents(
            FeedbackSessionAttributes session) {
        // Allow students to view the feedback session if there are questions for them
        List<FeedbackQuestionAttributes> questionsToAnswer =
                fqLogic.getFeedbackQuestionsForStudents(
                        session.getFeedbackSessionName(), session.getCourseId());

        if (session.isVisible() && !questionsToAnswer.isEmpty()) {
            return true;
        }

        // Allow students to view the feedback session
        // if there are any questions for instructors to answer
        // where the responses of the questions are visible to the students
        List<FeedbackQuestionAttributes> questionsWithVisibleResponses = new ArrayList<>();
        List<FeedbackQuestionAttributes> questionsForInstructors =
                                        fqLogic.getFeedbackQuestionsForCreatorInstructor(session);
        for (FeedbackQuestionAttributes question : questionsForInstructors) {
            if (frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question)) {
                questionsWithVisibleResponses.add(question);
            }
        }

        return session.isVisible() && !questionsWithVisibleResponses.isEmpty();
    }

    /**
     * Returns true if there are any questions for students to answer.
     */
    public boolean isFeedbackSessionForStudentsToAnswer(FeedbackSessionAttributes session) {

        List<FeedbackQuestionAttributes> questionsToAnswer =
                fqLogic.getFeedbackQuestionsForStudents(
                        session.getFeedbackSessionName(), session.getCourseId());

        return session.isVisible() && !questionsToAnswer.isEmpty();
    }

}
