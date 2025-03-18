package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
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
    private DeadlineExtensionsLogic deLogic;

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
        deLogic = DeadlineExtensionsLogic.inst();
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
     * Gets all feedback sessions of a course started after time.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourseStartingAfter(String courseId, Instant after) {
        return fsDb.getFeedbackSessionsForCourseStartingAfter(courseId, after);
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
    public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedOpenedEmailsToBeSent() {
        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsPossiblyNeedingOpenedEmail();
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
        if (fs == null) {
            return false;
        }
        return fs.getCreatorEmail().equals(userEmail);
    }

    /**
     * Checks whether a student has attempted a feedback session.
     *
     * <p>If feedback session consists of all team questions, session is attempted by student only
     * if someone from the team has responded. If feedback session has some individual questions,
     * session is attempted only if the student has responded to any of the individual questions
     * (regardless of the completion status of the team questions).</p>
     */
    public boolean isFeedbackSessionAttemptedByStudent(FeedbackSessionAttributes fsa, String userEmail, String userTeam) {
        String feedbackSessionName = fsa.getFeedbackSessionName();
        String courseId = fsa.getCourseId();

        if (!fqLogic.sessionHasQuestions(feedbackSessionName, courseId)) {
            // if there are no questions for student, session is attempted
            return true;
        } else if (fqLogic.sessionHasQuestionsForGiverType(
                feedbackSessionName, courseId, FeedbackParticipantType.STUDENTS)) {
            // case where there are some individual questions
            return frLogic.hasGiverRespondedForSession(userEmail, feedbackSessionName, courseId);
        } else {
            // case where all are team questions
            return frLogic.hasGiverRespondedForSession(userTeam, feedbackSessionName, courseId);
        }
    }

    /**
     * Checks whether an instructor has attempted a feedback session.
     *
     * <p>If there is no question for instructors, the feedback session is considered as attempted.</p>
     */
    public boolean isFeedbackSessionAttemptedByInstructor(FeedbackSessionAttributes fsa, String userEmail) {
        if (frLogic.hasGiverRespondedForSession(userEmail, fsa.getFeedbackSessionName(), fsa.getCourseId())) {
            return true;
        }

        // if there is no question for instructor, session is attempted
        return !fqLogic.hasFeedbackQuestionsForInstructors(fsa, fsa.isCreator(userEmail));
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

        // reset sentOpenedEmail if the session has opened but is being un-opened
        // now, or else leave it as sent if so.
        if (oldSession.isSentOpenedEmail()) {
            newUpdateOptions.withSentOpenedEmail(newSession.isOpened());

            // also reset sentOpeningSoonEmail
            newUpdateOptions.withSentOpeningSoonEmail(
                    newSession.isOpened()
                            || newSession.isOpeningInHours(NUMBER_OF_HOURS_BEFORE_OPENING_SOON_ALERT));
        }

        // reset sentClosedEmail if the session has closed but is being un-closed
        // now, or else leave it as sent if so.
        if (oldSession.isSentClosedEmail()) {
            newUpdateOptions.withSentClosedEmail(newSession.isClosed());

            // also reset sentClosingSoonEmail
            newUpdateOptions.withSentClosingSoonEmail(
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
     * Updates the instructor email address for all their deadlines in the feedback sessions of the given course.
     */
    public void updateFeedbackSessionsInstructorDeadlinesWithNewEmail(String courseId, String oldEmailAddress,
            String newEmailAddress) {
        updateFeedbackSessionsDeadlinesWithNewEmail(courseId, oldEmailAddress, newEmailAddress, true);
    }

    /**
     * Updates the student email address for all their deadlines in the feedback sessions of the given course.
     */
    public void updateFeedbackSessionsStudentDeadlinesWithNewEmail(String courseId, String oldEmailAddress,
            String newEmailAddress) {
        updateFeedbackSessionsDeadlinesWithNewEmail(courseId, oldEmailAddress, newEmailAddress, false);
    }

    /**
     * Deletes the instructor email address for all their deadlines in the feedback sessions of the given course.
     */
    public void deleteFeedbackSessionsDeadlinesForInstructor(String courseId, String emailAddress) {
        deleteFeedbackSessionsDeadlinesForUser(courseId, emailAddress, true);
    }

    /**
     * Deletes the student email address for all their deadlines in the feedback sessions of the given course.
     */
    public void deleteFeedbackSessionsDeadlinesForStudent(String courseId, String emailAddress) {
        deleteFeedbackSessionsDeadlinesForUser(courseId, emailAddress, false);
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

        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsPossiblyNeedingClosingSoonEmail();
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
     * Deletes a feedback session cascade to its associated questions, responses, deadline extensions and comments.
     */
    public void deleteFeedbackSessionCascade(String feedbackSessionName, String courseId) {
        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .withFeedbackSessionName(feedbackSessionName)
                .build();
        frcLogic.deleteFeedbackResponseComments(query);
        frLogic.deleteFeedbackResponses(query);
        fqLogic.deleteFeedbackQuestions(query);
        deLogic.deleteDeadlineExtensions(query);

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
        int expectedTotal = 0;

        if (fqLogic.hasFeedbackQuestionsForStudents(fsa)) {
            expectedTotal += studentsLogic.getNumberOfStudentsForCourse(fsa.getCourseId());
        }

        // Pre-flight check to ensure there are questions for instructors.
        if (!fqLogic.hasFeedbackQuestionsForInstructors(fsa, true)) {
            return expectedTotal;
        }

        List<String> instructorEmails = instructorsLogic.getInstructorEmailsForCourse(fsa.getCourseId());
        if (instructorEmails.isEmpty()) {
            return expectedTotal;
        }

        // Check presence of questions for instructors.
        if (fqLogic.hasFeedbackQuestionsForInstructors(fsa, false)) {
            expectedTotal += instructorEmails.size();
        } else {
            // No questions for instructors. There must be questions for creator.
            List<String> creatorEmails = instructorEmails.stream()
                    .filter(fsa::isCreator)
                    .collect(Collectors.toList());
            expectedTotal += creatorEmails.size();
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
     * Returns true if the feedback session is viewable by the given user type (students/instructors).
     */
    public boolean isFeedbackSessionViewableToUserType(FeedbackSessionAttributes session, boolean isInstructor) {
        // Allow user to view the feedback session if there are questions for them
        if (isFeedbackSessionForUserTypeToAnswer(session, isInstructor)) {
            return true;
        }

        // Allow user to view the feedback session if there are any question whose responses are visible to the user
        List<FeedbackQuestionAttributes> questionsWithVisibleResponses = new ArrayList<>();
        List<FeedbackQuestionAttributes> questionsForUser =
                fqLogic.getFeedbackQuestionsForSession(session.getFeedbackSessionName(), session.getCourseId());
        for (FeedbackQuestionAttributes question : questionsForUser) {
            if (!isInstructor && frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question)
                    || isInstructor && frLogic.isResponseOfFeedbackQuestionVisibleToInstructor(question)) {
                // We only need one question with visible responses for the entire session to be visible
                questionsWithVisibleResponses.add(question);
                break;
            }
        }

        return session.isVisible() && !questionsWithVisibleResponses.isEmpty();
    }

    /**
     * Returns true if there are any questions for the specified user type (students/instructors) to answer.
     */
    public boolean isFeedbackSessionForUserTypeToAnswer(FeedbackSessionAttributes session, boolean isInstructor) {
        if (!session.isVisible()) {
            return false;
        }

        return isInstructor
                ? fqLogic.hasFeedbackQuestionsForInstructors(session, false)
                : fqLogic.hasFeedbackQuestionsForStudents(session);
    }

    private void updateFeedbackSessionsDeadlinesWithNewEmail(String courseId, String oldEmailAddress,
            String newEmailAddress, boolean isInstructor) {
        if (oldEmailAddress.equals(newEmailAddress)) {
            return;
        }
        updateFeedbackSessionsDeadlinesForUser(courseId, oldEmailAddress, isInstructor,
                deadlines -> deadlines.put(newEmailAddress, deadlines.remove(oldEmailAddress)));
    }

    private void deleteFeedbackSessionsDeadlinesForUser(String courseId, String emailAddress, boolean isInstructor) {
        updateFeedbackSessionsDeadlinesForUser(courseId, emailAddress, isInstructor,
                deadlines -> deadlines.remove(emailAddress));
    }

    private void updateFeedbackSessionsDeadlinesForUser(String courseId, String emailAddress, boolean isInstructor,
            Consumer<Map<String, Instant>> deadlinesUpdater) {
        List<FeedbackSessionAttributes> feedbackSessions = fsDb.getFeedbackSessionsForCourse(courseId);
        feedbackSessions.forEach(feedbackSession -> {
            FeedbackSessionAttributes.UpdateOptions.Builder updateOptionsBuilder = FeedbackSessionAttributes
                    .updateOptionsBuilder(feedbackSession.getFeedbackSessionName(), courseId);
            if (isInstructor) {
                Map<String, Instant> instructorDeadlines = feedbackSession.getInstructorDeadlines();
                if (!instructorDeadlines.containsKey(emailAddress)) {
                    return;
                }
                Map<String, Instant> newInstructorDeadlines = new HashMap<>(instructorDeadlines);
                deadlinesUpdater.accept(newInstructorDeadlines);
                updateOptionsBuilder.withInstructorDeadlines(newInstructorDeadlines);
            } else {
                Map<String, Instant> studentDeadlines = feedbackSession.getStudentDeadlines();
                if (!studentDeadlines.containsKey(emailAddress)) {
                    return;
                }
                Map<String, Instant> newStudentDeadlines = new HashMap<>(studentDeadlines);
                deadlinesUpdater.accept(newStudentDeadlines);
                updateOptionsBuilder.withStudentDeadlines(newStudentDeadlines);
            }
            try {
                fsDb.updateFeedbackSession(updateOptionsBuilder.build());
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                // Both Exceptions should not be thrown.
                log.severe("Unexpected error", e);
            }
        });
    }

}
