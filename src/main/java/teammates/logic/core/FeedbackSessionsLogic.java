package teammates.logic.core;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.UserRole;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
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

    private static FeedbackSessionsLogic instance = new FeedbackSessionsLogic();

    private static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    private FeedbackSessionsLogic() {
        // prevent initialization
    }

    public static FeedbackSessionsLogic inst() {
        return instance;
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
                .filter(instructor -> !coursesLogic.getCourse(instructor.courseId).isCourseDeleted())
                .collect(Collectors.toList());

        List<FeedbackSessionAttributes> fsList = new ArrayList<>();

        for (InstructorAttributes instructor : courseNotDeletedInstructorList) {
            fsList.addAll(getFeedbackSessionsListForCourse(instructor.courseId));
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
                .filter(instructor -> !coursesLogic.getCourse(instructor.courseId).isCourseDeleted())
                .collect(Collectors.toList());

        List<FeedbackSessionAttributes> fsList = new ArrayList<>();

        for (InstructorAttributes instructor : courseNotDeletedInstructorList) {
            fsList.addAll(getSoftDeletedFeedbackSessionsListForCourse(instructor.courseId));
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

    public boolean isCreatorOfSession(String feedbackSessionName, String courseId, String userEmail) {
        FeedbackSessionAttributes fs = getFeedbackSession(feedbackSessionName, courseId);
        return fs.getCreatorEmail().equals(userEmail);
    }

    private boolean isFeedbackSessionExists(String feedbackSessionName, String courseId) {
        return fsDb.getFeedbackSession(courseId, feedbackSessionName) != null;
    }

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
        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName, courseId);
        // if there is no question for students, session is complete
        return allQuestions.isEmpty();
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
    public void updateFeedbackSessionsTimeZoneForCourse(String courseId, ZoneId courseTimeZone) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(courseTimeZone);

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
                    fqLogic.getFeedbackQuestionsForInstructor(questions, fsa.isCreator(instructor.email));
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

    /**
     * Gets the session result for a feedback session.
     *
     * @param feedbackSessionName the feedback session name
     * @param courseId the ID of the course
     * @param userEmail the user viewing the feedback session
     * @param role the role of the use
     * @param questionId if not null, will only return partial bundle for the question
     * @param section if not null, will only return partial bundle for the section
     * @return the session result bundle
     */
    public SessionResultsBundle getSessionResultsForUser(
            String feedbackSessionName, String courseId, String userEmail, UserRole role,
            @Nullable String questionId, @Nullable String section) {
        CourseRoster roster = new CourseRoster(
                studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));

        // load question(s)
        List<FeedbackQuestionAttributes> allQuestions;
        Map<String, FeedbackQuestionAttributes> allQuestionsMap = new HashMap<>();
        if (questionId == null) {
            allQuestions = fqLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        } else {
            FeedbackQuestionAttributes fqa = fqLogic.getFeedbackQuestion(questionId);
            if (fqa == null) {
                allQuestions = Collections.emptyList();
            } else {
                allQuestions = Collections.singletonList(fqa);
            }
        }
        for (FeedbackQuestionAttributes qn : allQuestions) {
            allQuestionsMap.put(qn.getId(), qn);
        }

        // load response(s)
        StudentAttributes student = getStudent(courseId, userEmail, role);
        List<FeedbackResponseAttributes> allResponses;
        if (isInstructor(role)) {
            // load all response for instructors and passively filter them later
            if (questionId == null) {
                allResponses = frLogic.getFeedbackResponsesForSessionInSection(feedbackSessionName, courseId, section);
            } else {
                allResponses = frLogic.getFeedbackResponsesForQuestionInSection(questionId, section);
            }
        } else {
            if (section != null) {
                throw new UnsupportedOperationException("Specify section filtering is not supported for student result");
            }
            allResponses = new ArrayList<>();
            // load viewable responses for students proactively
            // this is cost-effective as in most of time responses for the whole session will not be viewable to students
            for (FeedbackQuestionAttributes question : allQuestions) {
                List<FeedbackResponseAttributes> viewableResponses =
                        frLogic.getViewableFeedbackResponsesForStudentForQuestion(question, student, roster);
                allResponses.addAll(viewableResponses);
            }
        }

        // load comment(s)
        List<FeedbackResponseCommentAttributes> allComments;
        if (questionId == null) {
            allComments = frcLogic.getFeedbackResponseCommentForSessionInSection(courseId, feedbackSessionName, section);
        } else {
            allComments = frcLogic.getFeedbackResponseCommentForQuestionInSection(questionId, section);
        }

        // related questions, responses, and comment
        Map<String, FeedbackQuestionAttributes> relatedQuestionsMap = new HashMap<>();
        Map<String, FeedbackResponseAttributes> relatedResponsesMap = new HashMap<>();
        Map<String, List<FeedbackResponseCommentAttributes>> relatedCommentsMap = new HashMap<>();
        // student will have no related question at the beginning
        if (isInstructor(role)) {
            // all questions are related questions for instructor
            for (FeedbackQuestionAttributes qn : allQuestions) {
                relatedQuestionsMap.put(qn.getId(), qn);
            }
        }

        // consider the current viewing user
        Set<String> studentsEmailInTeam = getTeammateEmails(student, roster);
        InstructorAttributes instructor = getInstructor(courseId, userEmail, role);

        // visibility table for each response and comment
        Map<String, Boolean> responseGiverVisibilityTable = new HashMap<>();
        Map<String, Boolean> responseRecipientVisibilityTable = new HashMap<>();
        Map<Long, Boolean> commentVisibilityTable = new HashMap<>();

        // build response
        for (FeedbackResponseAttributes response : allResponses) {
            FeedbackQuestionAttributes correspondingQuestion = allQuestionsMap.get(response.feedbackQuestionId);
            if (correspondingQuestion == null) {
                // orphan response without corresponding question, ignore it
                continue;
            }
            // check visibility of response
            boolean isVisibleResponse = isResponseVisibleForUser(
                    userEmail, role, student, studentsEmailInTeam, response, correspondingQuestion, instructor);
            if (!isVisibleResponse) {
                continue;
            }

            // only if there are viewable responses, the corresponding question becomes related.
            // this operation is redundant for instructor but necessary for student
            relatedQuestionsMap.put(response.getFeedbackQuestionId(), correspondingQuestion);
            relatedResponsesMap.put(response.getId(), response);
            // generate giver/recipient name visibility table
            responseGiverVisibilityTable.put(response.getId(),
                    frLogic.isNameVisibleToUser(correspondingQuestion, response, userEmail, role, true, roster));
            responseRecipientVisibilityTable.put(response.getId(),
                    frLogic.isNameVisibleToUser(correspondingQuestion, response, userEmail, role, false, roster));
        }

        // build comment
        for (FeedbackResponseCommentAttributes frc : allComments) {
            FeedbackResponseAttributes relatedResponse = relatedResponsesMap.get(frc.feedbackResponseId);
            FeedbackQuestionAttributes relatedQuestion = relatedQuestionsMap.get(frc.feedbackQuestionId);
            // the comment needs to be relevant to the question and response
            if (relatedQuestion == null || relatedResponse == null) {
                continue;
            }
            // check visibility of comment
            boolean isVisibleResponseComment = frcLogic.isResponseCommentVisibleForUser(
                    userEmail, role, student, studentsEmailInTeam, relatedResponse, relatedQuestion, frc);
            if (!isVisibleResponseComment) {
                continue;
            }

            relatedCommentsMap.computeIfAbsent(relatedResponse.getId(), key -> new ArrayList<>()).add(frc);
            // generate comment giver name visibility table
            commentVisibilityTable.put(frc.getId(), frcLogic.isNameVisibleToUser(frc, relatedResponse, userEmail, roster));
        }

        List<FeedbackResponseAttributes> existingResponses = new ArrayList<>(relatedResponsesMap.values());
        List<FeedbackResponseAttributes> missingResponses = Collections.emptyList();
        FeedbackSessionAttributes session = fsDb.getFeedbackSession(courseId, feedbackSessionName);
        if (role == UserRole.INSTRUCTOR) {
            missingResponses = buildMissingResponses(
                    instructor, responseGiverVisibilityTable, responseRecipientVisibilityTable, session,
                    relatedQuestionsMap, existingResponses, roster, section);
        }

        return new SessionResultsBundle(session, relatedQuestionsMap, existingResponses, missingResponses,
                responseGiverVisibilityTable, responseRecipientVisibilityTable, relatedCommentsMap,
                commentVisibilityTable, roster);
    }

    /**
     * Builds viewable missing responses for the session for instructor.
     *
     * @param instructor the instructor
     * @param responseGiverVisibilityTable
     *         the giver visibility table which will be updated with the visibility of missing responses
     * @param responseRecipientVisibilityTable
     *         the recipient visibility table which will be updated with the visibility of missing responses
     * @param feedbackSession the feedback sessions
     * @param relatedQuestionsMap the relevant questions
     * @param existingResponses existing responses
     * @param courseRoster the course roster
     * @param section if not null, will only build missing responses for the section
     * @return a list of missing responses for the session.
     */
    private List<FeedbackResponseAttributes> buildMissingResponses(
            InstructorAttributes instructor,
            Map<String, Boolean> responseGiverVisibilityTable, Map<String, Boolean> responseRecipientVisibilityTable,
            FeedbackSessionAttributes feedbackSession, Map<String, FeedbackQuestionAttributes> relatedQuestionsMap,
            List<FeedbackResponseAttributes> existingResponses, CourseRoster courseRoster, @Nullable String section) {

        // first get all possible giver recipient pairs
        Map<String, Map<String, Set<String>>> questionCompleteGiverRecipientMap = new HashMap<>();
        for (FeedbackQuestionAttributes feedbackQuestion : relatedQuestionsMap.values()) {
            if (feedbackQuestion.getQuestionDetails().shouldGenerateMissingResponses(feedbackQuestion)) {
                questionCompleteGiverRecipientMap.put(feedbackQuestion.getId(),
                        fqLogic.buildCompleteGiverRecipientMap(feedbackSession, feedbackQuestion, courseRoster));
            } else {
                questionCompleteGiverRecipientMap.put(feedbackQuestion.getId(), new HashMap<>());
            }
        }

        // remove the existing responses in those pairs
        for (FeedbackResponseAttributes existingResponse : existingResponses) {
            Map<String, Set<String>> currGiverRecipientMap =
                    questionCompleteGiverRecipientMap.get(existingResponse.getFeedbackQuestionId());
            if (!currGiverRecipientMap.containsKey(existingResponse.getGiver())) {
                continue;
            }
            currGiverRecipientMap.get(existingResponse.getGiver()).remove(existingResponse.getRecipient());
        }

        List<FeedbackResponseAttributes> missingResponses = new ArrayList<>();
        // build dummy responses
        for (Map.Entry<String, Map<String, Set<String>>> currGiverRecipientMapEntry
                : questionCompleteGiverRecipientMap.entrySet()) {
            FeedbackQuestionAttributes correspondingQuestion =
                    relatedQuestionsMap.get(currGiverRecipientMapEntry.getKey());
            String questionId = correspondingQuestion.getId();

            for (Map.Entry<String, Set<String>> giverRecipientEntry
                    : currGiverRecipientMapEntry.getValue().entrySet()) {
                // giver
                String giverIdentifier = giverRecipientEntry.getKey();
                CourseRoster.ParticipantInfo giverInfo = courseRoster.getInfoForIdentifier(giverIdentifier);

                for (String recipientIdentifier : giverRecipientEntry.getValue()) {
                    // recipient
                    CourseRoster.ParticipantInfo recipientInfo = courseRoster.getInfoForIdentifier(recipientIdentifier);

                    // skip responses not in current section
                    if (section != null
                            && !giverInfo.getSectionName().equals(section)
                                    && !recipientInfo.getSectionName().equals(section)) {
                        continue;
                    }

                    FeedbackResponseAttributes missingResponse =
                            FeedbackResponseAttributes.builder(questionId, giverIdentifier, recipientIdentifier)
                                    .withCourseId(feedbackSession.getCourseId())
                                    .withFeedbackSessionName(feedbackSession.getFeedbackSessionName())
                                    .withGiverSection(giverInfo.getSectionName())
                                    .withRecipientSection(recipientInfo.getSectionName())
                                    .withResponseDetails(new FeedbackTextResponseDetails("No Response"))
                                    .build();

                    // check visibility of the missing response
                    boolean isVisibleResponse = isResponseVisibleForUser(
                            instructor.getEmail(), UserRole.INSTRUCTOR, null, Collections.emptySet(),
                            missingResponse, correspondingQuestion, instructor);
                    if (!isVisibleResponse) {
                        continue;
                    }

                    // generate giver/recipient name visibility table
                    responseGiverVisibilityTable.put(missingResponse.getId(),
                            frLogic.isNameVisibleToUser(correspondingQuestion, missingResponse,
                                    instructor.getEmail(), UserRole.INSTRUCTOR, true, courseRoster));
                    responseRecipientVisibilityTable.put(missingResponse.getId(),
                            frLogic.isNameVisibleToUser(correspondingQuestion, missingResponse,
                                    instructor.getEmail(), UserRole.INSTRUCTOR, false, courseRoster));
                    missingResponses.add(missingResponse);
                }
            }
        }

        return missingResponses;
    }

    /**
     * Gets the associated instructor if {@code role} is {@link UserRole#INSTRUCTOR}.
     *
     * <p>Returns null if it is not an instructor or the instructor cannot be found.
     */
    private InstructorAttributes getInstructor(String courseId, String userEmail, UserRole role) {
        if (isInstructor(role)) {
            return instructorsLogic.getInstructorForEmail(courseId, userEmail);
        }
        return null;
    }

    /*
     * Gets emails of student's teammates if student is not null, else returns an empty set.
     */
    private Set<String> getTeammateEmails(StudentAttributes student, CourseRoster roster) {
        if (student == null) {
            return Collections.emptySet();
        }
        Set<String> studentsEmailInTeam = new HashSet<>();
        List<StudentAttributes> studentsInTeam =
                roster.getTeamToMembersTable().getOrDefault(student.getTeam(), Collections.emptyList());
        for (StudentAttributes teammates : studentsInTeam) {
            studentsEmailInTeam.add(teammates.getEmail());
        }
        return studentsEmailInTeam;
    }

    /**
     * Gets the associated student if {@code role} is {@link UserRole#STUDENT}.
     *
     * <p>Returns null if it is not a student or the student cannot be found.
     */
    private StudentAttributes getStudent(String courseId, String userEmail, UserRole role) {
        if (isStudent(role)) {
            return studentsLogic.getStudentForEmail(courseId, userEmail);
        }
        return null;
    }

    private boolean isStudent(UserRole role) {
        return role == UserRole.STUDENT;
    }

    private boolean isInstructor(UserRole role) {
        return role == UserRole.INSTRUCTOR;
    }

    private boolean isResponseVisibleForUser(String userEmail,
            UserRole role, StudentAttributes student,
            Set<String> studentsEmailInTeam,
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes relatedQuestion, InstructorAttributes instructor) {

        boolean isVisibleResponse = false;
        if (isInstructor(role) && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)
                || response.recipient.equals(userEmail)
                        && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                || response.giver.equals(userEmail)
                || isStudent(role) && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
            isVisibleResponse = true;
        } else if (studentsEmailInTeam != null && isStudent(role)) {
            if (relatedQuestion.recipientType == FeedbackParticipantType.TEAMS
                    && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                    && response.recipient.equals(student.team)) {
                isVisibleResponse = true;
            } else if (relatedQuestion.giverType == FeedbackParticipantType.TEAMS
                       && studentsEmailInTeam.contains(response.giver)) {
                isVisibleResponse = true;
            } else if (relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                       && studentsEmailInTeam.contains(response.giver)) {
                isVisibleResponse = true;
            } else if (relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                       && studentsEmailInTeam.contains(response.recipient)) {
                isVisibleResponse = true;
            }
        }
        if (isVisibleResponse && instructor != null) {
            boolean isGiverSectionRestricted =
                    !instructor.isAllowedForPrivilege(response.giverSection,
                                                      response.feedbackSessionName,
                                                      Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
            // If instructors are not restricted to view the giver's section,
            // they are allowed to view responses to GENERAL, subject to visibility options
            boolean isRecipientSectionRestricted =
                    relatedQuestion.recipientType != FeedbackParticipantType.NONE
                    && !instructor.isAllowedForPrivilege(response.recipientSection,
                                                         response.feedbackSessionName,
                                                         Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);

            boolean isNotAllowedForInstructor = isGiverSectionRestricted || isRecipientSectionRestricted;
            if (isNotAllowedForInstructor) {
                isVisibleResponse = false;
            }
        }
        return isVisibleResponse;
    }

    private List<FeedbackSessionAttributes> getFeedbackSessionsListForCourse(String courseId) {

        return fsDb.getFeedbackSessionsForCourse(courseId);
    }

    private List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsListForCourse(String courseId) {

        return fsDb.getSoftDeletedFeedbackSessionsForCourse(courseId);
    }

    public boolean isFeedbackSessionFullyCompletedByStudent(
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
            if (!fqLogic.isQuestionFullyAnsweredByUser(question, userEmail)) {
                // If any question is not completely answered, session is not
                // completed
                return false;
            }
        }
        return true;
    }

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
