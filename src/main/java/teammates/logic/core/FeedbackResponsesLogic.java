package teammates.logic.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackMissingResponse;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.RequestTracer;
import teammates.common.util.SanitizationHelper;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.responses.FeedbackRankRecipientsResponse;

/**
 * Handles operations related to feedback responses.
 *
 * @see FeedbackResponse
 * @see FeedbackResponsesDb
 */
public final class FeedbackResponsesLogic {

    private static final FeedbackResponsesLogic instance = new FeedbackResponsesLogic();

    private FeedbackResponsesDb frDb;
    private UsersLogic usersLogic;
    private FeedbackQuestionsLogic fqLogic;
    private FeedbackResponseCommentsLogic frcLogic;

    private FeedbackResponsesLogic() {
        // prevent initialization
    }

    public static FeedbackResponsesLogic inst() {
        return instance;
    }

    /**
     * Initialize dependencies for {@code FeedbackResponsesLogic}.
     */
    void initLogicDependencies(FeedbackResponsesDb frDb,
            UsersLogic usersLogic, FeedbackQuestionsLogic fqLogic, FeedbackResponseCommentsLogic frcLogic) {
        this.frDb = frDb;
        this.usersLogic = usersLogic;
        this.fqLogic = fqLogic;
        this.frcLogic = frcLogic;
    }

    /**
     * Gets a feedbackResponse or null if it does not exist.
     */
    public FeedbackResponse getFeedbackResponse(UUID frId) {
        return frDb.getFeedbackResponse(frId);
    }

    /**
     * Returns true if the responses of the question are visible to students.
     */
    public boolean isResponseOfFeedbackQuestionVisibleToStudent(FeedbackQuestion question) {
        if (question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
            return true;
        }
        boolean isStudentRecipientType =
                   question.getRecipientType() == FeedbackParticipantType.STUDENTS
                || question.getRecipientType() == FeedbackParticipantType.STUDENTS_EXCLUDING_SELF
                || question.getRecipientType() == FeedbackParticipantType.STUDENTS_IN_SAME_SECTION
                || question.getRecipientType() == FeedbackParticipantType.OWN_TEAM_MEMBERS
                || question.getRecipientType() == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF
                || question.getRecipientType() == FeedbackParticipantType.GIVER
                   && question.getGiverType() == FeedbackParticipantType.STUDENTS;

        if ((isStudentRecipientType || question.getRecipientType().isTeam())
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            return true;
        }
        if (question.getGiverType() == FeedbackParticipantType.TEAMS
                || question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            return true;
        }
        return question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
    }

    /**
     * Returns true if the responses of the question are visible to instructors.
     */
    public boolean isResponseOfFeedbackQuestionVisibleToInstructor(FeedbackQuestion question) {
        return question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS);
    }

    /**
     * Checks whether a giver has responded a session.
     */
    public boolean hasGiverRespondedForSession(String giverIdentifier, List<FeedbackQuestion> questions) {
        assert questions != null;

        for (FeedbackQuestion question : questions) {
            boolean hasResponse = question
                    .getFeedbackResponses()
                    .stream()
                    .anyMatch(response -> response.getGiver().equals(giverIdentifier));
            if (hasResponse) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether a giver has responded a session.
     */
    public boolean hasGiverRespondedForSession(String giver, String feedbackSessionName, String courseId) {

        return frDb.hasResponsesFromGiverInSession(giver, feedbackSessionName, courseId);
    }

    /**
     * Creates a feedback response.
     * @return the created response
     * @throws InvalidParametersException if the response is not valid
     * @throws EntityAlreadyExistsException if the response already exist
     */
    public FeedbackResponse createFeedbackResponse(FeedbackResponse feedbackResponse)
            throws InvalidParametersException, EntityAlreadyExistsException {
        validateFeedbackResponse(feedbackResponse);

        if (frDb.getFeedbackResponse(feedbackResponse.getId()) != null) {
            throw new EntityAlreadyExistsException(
                    String.format(Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS, feedbackResponse.toString()));
        }

        return frDb.createFeedbackResponse(feedbackResponse);
    }

    /**
     * Get existing feedback responses from instructor for the given question.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromInstructorForQuestion(
            FeedbackQuestion question, Instructor instructor) {
        return frDb.getFeedbackResponsesFromGiverForQuestion(
                question.getId(), instructor.getEmail());
    }

    /**
     * Get existing feedback responses from student or his team for the given
     * question.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromStudentOrTeamForQuestion(
            FeedbackQuestion question, Student student) {
        if (question.getGiverType() == FeedbackParticipantType.TEAMS) {
            return getFeedbackResponsesFromTeamForQuestion(
                    question.getId(), question.getCourseId(), student.getTeamName(), null);
        }
        return frDb.getFeedbackResponsesFromGiverForQuestion(question.getId(), student.getEmail());
    }

    private List<FeedbackResponse> getFeedbackResponsesFromTeamForQuestion(
            UUID feedbackQuestionId, String courseId, String teamName, @Nullable CourseRoster courseRoster) {

        List<FeedbackResponse> responses = new ArrayList<>();
        List<Student> studentsInTeam = courseRoster == null
                ? usersLogic.getStudentsForTeam(teamName, courseId) : courseRoster.getTeamToMembersTable().get(teamName);

        for (Student student : studentsInTeam) {
            responses.addAll(frDb.getFeedbackResponsesFromGiverForQuestion(
                    feedbackQuestionId, student.getEmail()));
        }

        responses.addAll(frDb.getFeedbackResponsesFromGiverForQuestion(
                feedbackQuestionId, teamName));
        return responses;
    }

    /**
     * Updates a feedback response.
     *
     * <p>Cascade-updates the associated feedback response comments.
     *
     * @return updated feedback response
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     */
    public FeedbackResponse updateFeedbackResponseCascade(FeedbackResponse feedbackResponse)
            throws InvalidParametersException, EntityDoesNotExistException {

        FeedbackResponse oldResponse = frDb.getFeedbackResponse(feedbackResponse.getId());
        if (oldResponse == null) {
            throw new EntityDoesNotExistException("Trying to update non-existent Entity: " + feedbackResponse);
        }

        // TODO: do not pass detached entities around
        HibernateUtil.merge(feedbackResponse);

        List<FeedbackResponseComment> oldResponseComments = oldResponse.getFeedbackResponseComments();

        for (FeedbackResponseComment oldResponseComment : oldResponseComments) {
            oldResponseComment.setGiverSection(feedbackResponse.getGiverSection());
            oldResponseComment.setRecipientSection(feedbackResponse.getRecipientSection());

            frcLogic.updateFeedbackResponseComment(oldResponseComment);
        }

        validateFeedbackResponse(feedbackResponse);

        return feedbackResponse;
    }

    /**
     * Deletes a feedback response and its associated feedback response comments.
     *
     * <p>Fails silently if the feedback response doesn't exist.</p>
     */
    public void deleteFeedbackResponsesAndCommentsCascade(FeedbackResponse feedbackResponse) {
        if (feedbackResponse == null) {
            return;
        }

        frDb.deleteFeedbackResponse(feedbackResponse);
    }

    /**
     * Deletes all feedback responses of a question and its associated comments.
     */
    public void deleteFeedbackResponsesForQuestionCascade(FeedbackQuestion feedbackQuestion) {
        List<FeedbackResponse> responses = feedbackQuestion.getFeedbackResponses();
        for (FeedbackResponse response : responses) {
            deleteFeedbackResponsesAndCommentsCascade(response);
        }
    }

    /**
     * Checks whether there are responses for a question.
     */
    public boolean areThereResponsesForQuestion(UUID questionId) {
        return frDb.areThereResponsesForQuestion(questionId);
    }

    /**
     * Checks whether there are responses for a course.
     */
    public boolean hasResponsesForCourse(String courseId) {
        return frDb.hasResponsesForCourse(courseId);

    }

    /**
     * Deletes all feedback responses involved an entity, cascade its associated comments.
     * Deletion will automatically be cascaded to each feedback response's comments,
     * handled by Hibernate using the OnDelete annotation.
     */
    public void deleteFeedbackResponsesForCourseCascade(String courseId, String entityEmail) {
        // delete responses from the entity
        List<FeedbackResponse> responsesFromStudent =
                getFeedbackResponsesFromGiverForCourse(courseId, entityEmail);
        for (FeedbackResponse response : responsesFromStudent) {
            deleteFeedbackResponsesAndCommentsCascade(response);
        }

        // delete responses to the entity
        List<FeedbackResponse> responsesToStudent =
                getFeedbackResponsesForRecipientForCourse(courseId, entityEmail);
        for (FeedbackResponse response : responsesToStudent) {
            deleteFeedbackResponsesAndCommentsCascade(response);
        }
    }

    /**
     * Gets all responses given by a user for a course.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromGiverForCourse(
            String courseId, String giver) {
        assert courseId != null;
        assert giver != null;

        return frDb.getFeedbackResponsesFromGiverForCourse(courseId, giver);
    }

    /**
     * Gets all responses received by a user for a course.
     */
    public List<FeedbackResponse> getFeedbackResponsesForRecipientForCourse(
            String courseId, String recipient) {
        assert courseId != null;
        assert recipient != null;

        return frDb.getFeedbackResponsesForRecipientForCourse(courseId, recipient);
    }

    /**
     * Gets all responses given by a user for a question.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromGiverForQuestion(
            UUID feedbackQuestionId, String giver) {
        return frDb.getFeedbackResponsesFromGiverForQuestion(feedbackQuestionId, giver);
    }

    /**
     * Gets all responses for a question.
     */
    public List<FeedbackResponse> getFeedbackResponsesForQuestion(UUID feedbackQuestionId) {
        return frDb.getResponsesForQuestion(feedbackQuestionId);
    }

    /**
     * Updates the relevant responses before the deletion of a student.
     * This method takes care of the following:
     * Making existing responses of 'rank recipient question' consistent.
     */
    public void updateRankRecipientQuestionResponsesAfterDeletingStudent(String courseId) {
        List<FeedbackQuestion> filteredQuestions =
                fqLogic.getFeedbackQuestionForCourseWithType(courseId, FeedbackQuestionType.RANK_RECIPIENTS);
        CourseRoster roster = new CourseRoster(
                usersLogic.getStudentsForCourse(courseId),
                usersLogic.getInstructorsForCourse(courseId));

        for (FeedbackQuestion question : filteredQuestions) {
            makeRankRecipientQuestionResponsesConsistent(question, roster);
        }
    }

    /**
     * Makes the rankings by one giver in the response to a 'rank recipient question' consistent, after deleting a
     * student.
     *
     * <p>
     *     Fails silently if the question type is not 'rank recipient question'.
     * </p>
     */
    private void makeRankRecipientQuestionResponsesConsistent(
            FeedbackQuestion question, CourseRoster roster) {
        assert question.getQuestionType() == FeedbackQuestionType.RANK_RECIPIENTS;

        FeedbackParticipantType giverType = question.getGiverType();
        List<FeedbackResponse> responses = new ArrayList<>();
        int numberOfRecipients = 0;

        switch (giverType) {
        case INSTRUCTORS:
        case SELF:
            for (Instructor instructor : roster.getInstructors()) {
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, instructor, null, roster).size();
                responses = getFeedbackResponsesFromGiverForQuestion(question.getId(), instructor.getEmail());
            }
            break;
        case TEAMS:
        case TEAMS_IN_SAME_SECTION:
            Student firstMemberOfTeam;
            String team;
            Map<String, List<Student>> teams = roster.getTeamToMembersTable();
            for (Map.Entry<String, List<Student>> entry : teams.entrySet()) {
                team = entry.getKey();
                firstMemberOfTeam = entry.getValue().get(0);
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, null, firstMemberOfTeam, roster).size();
                responses =
                        getFeedbackResponsesFromTeamForQuestion(
                                question.getId(), question.getCourseId(), team, roster);
            }
            break;
        default:
            for (Student student : roster.getStudents()) {
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, null, student, roster).size();
                responses = getFeedbackResponsesFromGiverForQuestion(question.getId(), student.getEmail());
            }
            break;
        }

        updateFeedbackResponsesForRankRecipientQuestions(responses, numberOfRecipients);
    }

    /**
     * Updates responses for 'rank recipient question', such that the ranks in the responses are consistent.
     * @param responses responses to one feedback question, from one giver
     * @param maxRank the maximum rank in each response
     */
    private void updateFeedbackResponsesForRankRecipientQuestions(
            List<FeedbackResponse> responses, int maxRank) {
        if (maxRank <= 0) {
            return;
        }

        FeedbackRankRecipientsResponseDetails responseDetails;
        boolean[] isRankUsed;
        boolean isUpdateNeeded = false;
        int answer;
        int maxUnusedRank = 0;

        // Checks whether update is needed.
        for (FeedbackResponse response : responses) {
            if (!(response instanceof FeedbackRankRecipientsResponse)) {
                continue;
            }
            responseDetails = ((FeedbackRankRecipientsResponse) response).getAnswer();
            answer = responseDetails.getAnswer();
            if (answer > maxRank) {
                isUpdateNeeded = true;
                break;
            }
        }

        // Updates repeatedly, until all responses are consistent.
        while (isUpdateNeeded) {
            isUpdateNeeded = false; // will be set to true again once invalid rank appears after update
            isRankUsed = new boolean[maxRank];

            // Obtains the largest unused rank.
            for (FeedbackResponse response : responses) {
                if (!(response instanceof FeedbackRankRecipientsResponse)) {
                    continue;
                }
                responseDetails = ((FeedbackRankRecipientsResponse) response).getAnswer();
                answer = responseDetails.getAnswer();
                if (answer <= maxRank) {
                    isRankUsed[answer - 1] = true;
                }
            }
            for (int i = maxRank - 1; i >= 0; i--) {
                if (!isRankUsed[i]) {
                    maxUnusedRank = i + 1;
                    break;
                }
            }
            assert maxUnusedRank > 0; // if update is needed, there must be at least one unused rank

            for (FeedbackResponse response : responses) {
                if (response instanceof FeedbackRankRecipientsResponse) {
                    responseDetails = ((FeedbackRankRecipientsResponse) response).getAnswer();
                    answer = responseDetails.getAnswer();
                    if (answer > maxUnusedRank) {
                        answer--;
                        responseDetails.setAnswer(answer);
                    }
                    if (answer > maxRank) {
                        isUpdateNeeded = true; // sets the flag to true if the updated rank is still invalid
                    }
                }
            }
        }
    }

    /**
     * Updates responses for a student when his team changes.
     *
     * <p>
     *     This is done by deleting responses that are no longer relevant to him in his new team.
     * </p>
     */
    public void updateFeedbackResponsesForChangingTeam(Course course, String newEmail, Team oldTeam) {
        List<FeedbackResponse> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(course.getId(), newEmail);

        for (FeedbackResponse response : responsesFromUser) {
            FeedbackQuestion qn = response.getFeedbackQuestion();
            if (qn != null && qn.getGiverType() == FeedbackParticipantType.TEAMS) {
                deleteFeedbackResponsesForQuestionCascade(qn);
            }
        }

        List<FeedbackResponse> responsesToUser =
                getFeedbackResponsesForRecipientForCourse(course.getId(), newEmail);

        for (FeedbackResponse response : responsesToUser) {
            FeedbackQuestion qn = response.getFeedbackQuestion();
            if (qn != null && qn.getGiverType() == FeedbackParticipantType.TEAMS) {
                deleteFeedbackResponsesForQuestionCascade(qn);
            }
        }

        boolean isOldTeamEmpty = usersLogic.getStudentsForTeam(oldTeam.getName(), course.getId()).isEmpty();

        if (isOldTeamEmpty) {
            deleteFeedbackResponsesForCourseCascade(course.getId(), oldTeam.getName());
        }
    }

    /**
     * Updates responses for a student when his section changes.
     */
    public void updateFeedbackResponsesForChangingSection(Course course, String newEmail, Section newSection)
            throws InvalidParametersException {

        List<FeedbackResponse> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(course.getId(), newEmail);

        for (FeedbackResponse response : responsesFromUser) {
            response.setGiverSection(newSection);
            if (!response.isValid()) {
                throw new InvalidParametersException(response.getInvalidityInfo());
            }
            frcLogic.updateFeedbackResponseCommentsForResponse(response);
        }

        List<FeedbackResponse> responsesToUser =
                getFeedbackResponsesForRecipientForCourse(course.getId(), newEmail);

        for (FeedbackResponse response : responsesToUser) {
            response.setRecipientSection(newSection);
            if (!response.isValid()) {
                throw new InvalidParametersException(response.getInvalidityInfo());
            }
            frcLogic.updateFeedbackResponseCommentsForResponse(response);
        }
    }

    /**
     * Updates a student's email in their given/received responses.
     */
    public void updateFeedbackResponsesForChangingEmail(String courseId, String oldEmail, String newEmail)
            throws InvalidParametersException {
        List<FeedbackResponse> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(courseId, oldEmail);

        for (FeedbackResponse response : responsesFromUser) {
            response.setGiver(newEmail);
            if (!response.isValid()) {
                throw new InvalidParametersException(response.getInvalidityInfo());
            }
        }

        List<FeedbackResponse> responsesToUser =
                getFeedbackResponsesForRecipientForCourse(courseId, oldEmail);

        for (FeedbackResponse response : responsesToUser) {
            response.setRecipient(newEmail);
            if (!response.isValid()) {
                throw new InvalidParametersException(response.getInvalidityInfo());
            }
        }
    }

    private List<FeedbackQuestion> getQuestionsForSession(
            FeedbackSession feedbackSession, @Nullable UUID questionId) {
        if (questionId == null) {
            return fqLogic.getFeedbackQuestionsForSession(feedbackSession);
        }
        FeedbackQuestion fq = fqLogic.getFeedbackQuestion(questionId);
        return fq == null ? Collections.emptyList() : Collections.singletonList(fq);
    }

    private SessionResultsBundle buildResultsBundle(
            boolean isCourseWide, FeedbackSession feedbackSession, String courseId, String sectionName, UUID questionId,
            boolean isInstructor, String userEmail, Instructor instructor, Student student,
            CourseRoster roster, List<FeedbackQuestion> allQuestions,
            List<FeedbackResponse> allResponses, boolean isPreviewResults) {

        Set<FeedbackQuestion> questionsNotVisibleToInstructors = new HashSet<>();
        for (FeedbackQuestion qn : allQuestions) {

            // set questions that should not be visible to instructors if results are being previewed
            if (isPreviewResults && !checkCanInstructorsSeeQuestion(qn)) {
                questionsNotVisibleToInstructors.add(qn);
            }
        }

        // load comment(s)
        List<FeedbackResponseComment> allComments;
        if (questionId == null) {
            allComments = frcLogic.getFeedbackResponseCommentForSessionInSection(
                    courseId, feedbackSession.getName(), sectionName);
        } else {
            allComments = frcLogic.getFeedbackResponseCommentForQuestionInSection(questionId, sectionName);
        }
        RequestTracer.checkRemainingTime();

        // related questions, responses, and comment
        List<FeedbackQuestion> relatedQuestions = new ArrayList<>();
        List<FeedbackResponse> relatedResponses = new ArrayList<>();
        Map<FeedbackResponse, List<FeedbackResponseComment>> relatedCommentsMap = new HashMap<>();
        Set<FeedbackQuestion> relatedQuestionsNotVisibleForPreviewSet = new HashSet<>();
        Set<FeedbackQuestion> relatedQuestionsWithCommentNotVisibleForPreview = new HashSet<>();
        if (isCourseWide) {
            // all questions are related questions when viewing course-wide result
            for (FeedbackQuestion qn : allQuestions) {
                relatedQuestions.add(qn);
            }
        }

        Set<String> studentsEmailInTeam = new HashSet<>();
        if (student != null) {
            for (Student studentInTeam
                    : roster.getTeamToMembersTable().getOrDefault(student.getTeamName(), Collections.emptyList())) {
                studentsEmailInTeam.add(studentInTeam.getEmail());
            }
        }

        // visibility table for each response and comment
        Map<UUID, Boolean> responseGiverVisibilityTable = new HashMap<>();
        Map<UUID, Boolean> responseRecipientVisibilityTable = new HashMap<>();
        Map<UUID, Boolean> commentVisibilityTable = new HashMap<>();

        // build response
        for (FeedbackResponse response : allResponses) {
            if (isPreviewResults
                    && relatedQuestionsNotVisibleForPreviewSet.contains(response.getFeedbackQuestion())) {
                // corresponding question's responses will not be shown to previewer, ignore the response
                continue;
            }
            FeedbackQuestion correspondingQuestion = response.getFeedbackQuestion();
            if (correspondingQuestion == null) {
                // orphan response without corresponding question, ignore it
                continue;
            }
            // check visibility of response
            boolean isVisibleResponse = isResponseVisibleForUser(
                    userEmail, student, instructor, studentsEmailInTeam,
                    response.getGiver(), response.getRecipient(),
                    response.getGiverSectionName(), response.getRecipientSectionName(),
                    correspondingQuestion);
            if (!isVisibleResponse) {
                continue;
            }

            // if previewing results and corresponding question should not be visible to instructors,
            // note down the question and do not add the response
            if (isPreviewResults && questionsNotVisibleToInstructors.contains(response.getFeedbackQuestion())) {
                relatedQuestionsNotVisibleForPreviewSet.add(response.getFeedbackQuestion());
                continue;
            }

            // if there are viewable responses, the corresponding question becomes related
            relatedQuestions.add(response.getFeedbackQuestion());
            relatedResponses.add(response);

            // generate giver/recipient name visibility table
            responseGiverVisibilityTable.put(response.getId(),
                    isNameVisibleToUser(correspondingQuestion, response.getGiver(), response.getRecipient(),
                        userEmail, isInstructor, true, roster));
            responseRecipientVisibilityTable.put(response.getId(),
                    isNameVisibleToUser(correspondingQuestion, response.getGiver(), response.getRecipient(),
                        userEmail, isInstructor, false, roster));
        }
        RequestTracer.checkRemainingTime();

        // build comment
        for (FeedbackResponseComment frc : allComments) {
            FeedbackResponse relatedResponse = frc.getFeedbackResponse();
            // the comment needs to be relevant to the question and response
            if (relatedResponse == null) {
                continue;
            }
            FeedbackQuestion relatedQuestion = relatedResponse.getFeedbackQuestion();
            if (relatedQuestion == null) {
                continue;
            }
            // check visibility of comment
            boolean isVisibleResponseComment = frcLogic.checkIsResponseCommentVisibleForUser(
                    userEmail, isInstructor, student, studentsEmailInTeam, relatedResponse, relatedQuestion, frc);
            if (!isVisibleResponseComment) {
                continue;
            }

            // if previewing results and the comment should not be visible to instructors,
            // note down the corresponding question and do not add the comment
            if (isPreviewResults && !checkCanInstructorsSeeComment(frc)) {
                relatedQuestionsWithCommentNotVisibleForPreview.add(frc.getFeedbackResponse().getFeedbackQuestion());
                continue;
            }

            relatedCommentsMap.computeIfAbsent(relatedResponse, key -> new ArrayList<>()).add(frc);
            // generate comment giver name visibility table
            commentVisibilityTable.put(frc.getId(),
                    frcLogic.checkIsNameVisibleToUser(frc, relatedResponse, userEmail, roster));
        }
        RequestTracer.checkRemainingTime();

        List<FeedbackResponse> existingResponses = new ArrayList<>(relatedResponses);
        List<FeedbackMissingResponse> missingResponses = Collections.emptyList();
        if (isCourseWide) {
            missingResponses = buildMissingResponses(
                    instructor, responseGiverVisibilityTable, responseRecipientVisibilityTable, relatedQuestions,
                    existingResponses, roster, sectionName);
        }
        RequestTracer.checkRemainingTime();

        return new SessionResultsBundle(relatedQuestions, relatedQuestionsNotVisibleForPreviewSet,
                relatedQuestionsWithCommentNotVisibleForPreview, existingResponses, missingResponses,
                responseGiverVisibilityTable, responseRecipientVisibilityTable, relatedCommentsMap,
                commentVisibilityTable, roster);
    }

    /**
     * Gets the session result for a feedback session.
     *
     * @param feedbackSession the feedback session
     * @param instructorEmail the instructor viewing the feedback session
     * @param questionId if not null, will only return partial bundle for the question
     * @param sectionName if not null, will only return partial bundle for the section
     * @param fetchType if not null, will fetch responses by giver, receiver sections, or both
     * @return the session result bundle
     */
    public SessionResultsBundle getSessionResults(
            FeedbackSession feedbackSession, String instructorEmail,
            @Nullable UUID questionId, @Nullable String sectionName, @Nullable FeedbackResultFetchType fetchType) {

        String courseId = feedbackSession.getCourseId();
        CourseRoster roster = new CourseRoster(
                usersLogic.getStudentsForCourse(courseId),
                usersLogic.getInstructorsForCourse(courseId));

        // load question(s)
        List<FeedbackQuestion> allQuestions = getQuestionsForSession(feedbackSession, questionId);
        RequestTracer.checkRemainingTime();

        // load response(s)
        List<FeedbackResponse> allResponses;
        // load all response for instructors and passively filter them later
        if (questionId == null) {
            allResponses = getFeedbackResponsesForSessionInSection(feedbackSession, courseId, sectionName, fetchType);
        } else {
            allResponses = getFeedbackResponsesForQuestionInSection(questionId, sectionName, fetchType);
        }
        RequestTracer.checkRemainingTime();

        // consider the current viewing user
        Instructor instructor = usersLogic.getInstructorForEmail(courseId, instructorEmail);

        return buildResultsBundle(true, feedbackSession, courseId, sectionName, questionId, true, instructorEmail,
                instructor, null, roster, allQuestions, allResponses, false);
    }

    /**
     * Gets the session result for a feedback session for the given user.
     *
     * @param feedbackSession the feedback session
     * @param userEmail the user viewing the feedback session
     * @param isInstructor true if the user is an instructor
     * @param questionId if not null, will only return partial bundle for the question
     * @param isPreviewResults true if getting session results for preview purpose
     * @return the session result bundle
     */
    public SessionResultsBundle getSessionResultsForUser(
            FeedbackSession feedbackSession, String userEmail, boolean isInstructor,
            @Nullable UUID questionId, boolean isPreviewResults) {
        String courseId = feedbackSession.getCourseId();
        CourseRoster roster = new CourseRoster(
                usersLogic.getStudentsForCourse(courseId),
                usersLogic.getInstructorsForCourse(courseId));

        // load question(s)
        List<FeedbackQuestion> allQuestions = getQuestionsForSession(feedbackSession, questionId);
        RequestTracer.checkRemainingTime();

        // load response(s)
        Student student = isInstructor ? null : usersLogic.getStudentForEmail(courseId, userEmail);
        Instructor instructor = isInstructor ? usersLogic.getInstructorForEmail(courseId, userEmail) : null;
        List<FeedbackResponse> allResponses = new ArrayList<>();
        for (FeedbackQuestion question : allQuestions) {
            // load viewable responses for students/instructors proactively
            // this is cost-effective as in most of time responses for the whole session will not be viewable to individuals
            List<FeedbackResponse> viewableResponses = isInstructor
                    ? getFeedbackResponsesToOrFromInstructorForQuestion(question, instructor)
                    : getViewableFeedbackResponsesForStudentForQuestion(question, student, roster);
            allResponses.addAll(viewableResponses);
        }
        RequestTracer.checkRemainingTime();

        return buildResultsBundle(false, feedbackSession, courseId, null, questionId, isInstructor, userEmail,
                instructor, student, roster, allQuestions, allResponses, isPreviewResults);
    }

    /**
     * Builds viewable missing responses for the session for instructor.
     *
     * @param instructor the instructor
     * @param responseGiverVisibilityTable
     *         the giver visibility table which will be updated with the visibility of missing responses
     * @param responseRecipientVisibilityTable
     *         the recipient visibility table which will be updated with the visibility of missing responses
     * @param relatedQuestions the relevant questions
     * @param existingResponses existing responses
     * @param courseRoster the course roster
     * @param sectionName if not null, will only build missing responses for the section
     * @return a list of missing responses for the session.
     */
    private List<FeedbackMissingResponse> buildMissingResponses(
            Instructor instructor, Map<UUID, Boolean> responseGiverVisibilityTable,
            Map<UUID, Boolean> responseRecipientVisibilityTable, List<FeedbackQuestion> relatedQuestions,
            List<FeedbackResponse> existingResponses, CourseRoster courseRoster, @Nullable String sectionName) {

        // get all possible giver recipient pairs
        Map<FeedbackQuestion, Map<String, Set<String>>> questionCompleteGiverRecipientMap = new HashMap<>();
        for (FeedbackQuestion feedbackQuestion : relatedQuestions) {
            if (feedbackQuestion.getQuestionDetailsCopy().shouldGenerateMissingResponses(feedbackQuestion)) {
                questionCompleteGiverRecipientMap.put(feedbackQuestion,
                        fqLogic.buildCompleteGiverRecipientMap(feedbackQuestion, courseRoster));
            } else {
                questionCompleteGiverRecipientMap.put(feedbackQuestion, new HashMap<>());
            }
        }

        // remove the existing responses in those pairs
        for (FeedbackResponse existingResponse : existingResponses) {
            Map<String, Set<String>> currGiverRecipientMap =
                    questionCompleteGiverRecipientMap.get(existingResponse.getFeedbackQuestion());
            if (!currGiverRecipientMap.containsKey(existingResponse.getGiver())) {
                continue;
            }
            currGiverRecipientMap.get(existingResponse.getGiver()).remove(existingResponse.getRecipient());
        }

        List<FeedbackMissingResponse> missingResponses = new ArrayList<>();
        // build dummy responses
        for (Map.Entry<FeedbackQuestion, Map<String, Set<String>>> currGiverRecipientMapEntry
                : questionCompleteGiverRecipientMap.entrySet()) {
            FeedbackQuestion correspondingQuestion = currGiverRecipientMapEntry.getKey();

            for (Map.Entry<String, Set<String>> giverRecipientEntry
                    : currGiverRecipientMapEntry.getValue().entrySet()) {
                // giver
                String giverIdentifier = giverRecipientEntry.getKey();
                CourseRoster.ParticipantInfo giverInfo = courseRoster.getInfoForIdentifier(giverIdentifier);

                for (String recipientIdentifier : giverRecipientEntry.getValue()) {
                    // recipient
                    CourseRoster.ParticipantInfo recipientInfo = courseRoster.getInfoForIdentifier(recipientIdentifier);

                    // skip responses not in current section
                    if (sectionName != null
                            && !giverInfo.getSectionName().equals(sectionName)
                            && !recipientInfo.getSectionName().equals(sectionName)) {
                        continue;
                    }

                    FeedbackMissingResponse missingResponse = new FeedbackMissingResponse(
                            correspondingQuestion,
                            giverIdentifier, giverInfo.getSectionName(),
                            recipientIdentifier, recipientInfo.getSectionName());

                    boolean isVisibleResponse = isResponseVisibleForUser(
                            instructor.getEmail(), null, instructor, Collections.emptySet(),
                            missingResponse.giver(), missingResponse.recipient(),
                            missingResponse.giverSectionName(), missingResponse.recipientSectionName(),
                            correspondingQuestion);
                    if (!isVisibleResponse) {
                        continue;
                    }

                    // generate giver/recipient name visibility table
                    responseGiverVisibilityTable.put(missingResponse.id(),
                            isNameVisibleToUser(correspondingQuestion, missingResponse.giver(), missingResponse.recipient(),
                                    instructor.getEmail(), true, true, courseRoster));
                    responseRecipientVisibilityTable.put(missingResponse.id(),
                            isNameVisibleToUser(correspondingQuestion, missingResponse.giver(), missingResponse.recipient(),
                                    instructor.getEmail(), true, false, courseRoster));
                    missingResponses.add(missingResponse);
                }
            }
        }

        return missingResponses;
    }

    /**
     * Checks whether the giver name of a response is visible to an user.
     */
    public boolean isNameVisibleToUser(
            FeedbackQuestion question,
            String responseGiver, String responseRecipient,
            String userEmail,
            boolean isInstructor, boolean isGiverName, CourseRoster roster) {

        if (question == null) {
            return false;
        }

        // Early return if user is giver
        if (question.getGiverType() == FeedbackParticipantType.TEAMS) {
            // if response is given by team, then anyone in the team can see the response
            if (roster.isStudentInTeam(userEmail, responseGiver)) {
                return true;
            }
        } else {
            if (SanitizationHelper.areEmailsEqual(responseGiver, userEmail)) {
                return true;
            }
        }

        return isFeedbackParticipantNameVisibleToUser(question, responseGiver, responseRecipient,
                userEmail, isInstructor, isGiverName, roster);
    }

    private boolean isFeedbackParticipantNameVisibleToUser(
            FeedbackQuestion question, String responseGiver, String responseRecipient,
            String userEmail, boolean isInstructor, boolean isGiverName, CourseRoster roster) {
        List<FeedbackParticipantType> showNameTo = isGiverName
                                                 ? question.getShowGiverNameTo()
                                                 : question.getShowRecipientNameTo();
        for (FeedbackParticipantType type : showNameTo) {
            switch (type) {
            case INSTRUCTORS:
                if (roster.getInstructorForEmail(userEmail) != null && isInstructor) {
                    return true;
                }
                break;
            case OWN_TEAM_MEMBERS, OWN_TEAM_MEMBERS_INCLUDING_SELF:
                // Refers to Giver's Team Members
                if (roster.isStudentsInSameTeam(responseGiver, userEmail)) {
                    return true;
                }
                break;
            case RECEIVER:
                // Response to team
                if (question.getRecipientType().isTeam()) {
                    if (roster.isStudentInTeam(userEmail, responseRecipient)) {
                        // this is a team name
                        return true;
                    }
                    break;
                    // Response to individual
                } else if (SanitizationHelper.areEmailsEqual(responseRecipient, userEmail)) {
                    return true;
                } else {
                    break;
                }
            case RECEIVER_TEAM_MEMBERS:
                // Response to team; recipient = teamName
                if (question.getRecipientType().isTeam()) {
                    if (roster.isStudentInTeam(userEmail, responseRecipient)) {
                        // this is a team name
                        return true;
                    }
                    break;
                } else if (roster.isStudentsInSameTeam(responseRecipient, userEmail)) {
                    // Response to individual
                    return true;
                }
                break;
            case STUDENTS:
                if (roster.isStudentInCourse(userEmail)) {
                    return true;
                }
                break;
            default:
                assert false : "Invalid FeedbackParticipantType for showNameTo in "
                        + "FeedbackResponseLogic.isFeedbackParticipantNameVisibleToUser()";
                break;
            }
        }
        return false;
    }

    private boolean isResponseVisibleForUser(
            String userEmail,
            Student student,
            Instructor instructor,
            Set<String> studentsEmailInTeam,
            String giver,
            String recipient,
            String giverSectionName,
            String recipientSectionName,
            FeedbackQuestion relatedQuestion
    ) {
        boolean isInstructor = instructor != null;

        boolean isGiverSectionRestrictedForInstructor = isInstructor && !instructor.isAllowedForPrivilege(
                        giverSectionName,
                        relatedQuestion.getFeedbackSessionName(),
                        Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS
                );

        boolean isRecipientSectionRestrictedForInstructor = isInstructor
                && relatedQuestion.getRecipientType() != FeedbackParticipantType.NONE
                && !instructor.isAllowedForPrivilege(
                        recipientSectionName,
                        relatedQuestion.getFeedbackSessionName(),
                        Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS
                );

        boolean isVisibleToInstructor = isInstructor
                && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)
                && !isGiverSectionRestrictedForInstructor
                && !isRecipientSectionRestrictedForInstructor;
        boolean isVisibleToRecipient = SanitizationHelper.areEmailsEqual(recipient, userEmail)
                && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER);
        boolean isVisibleToGiver = SanitizationHelper.areEmailsEqual(giver, userEmail);
        boolean isVisibleToStudents = !isInstructor && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.STUDENTS);
        boolean isVisibleToTeamRecipient = studentsEmailInTeam != null && !isInstructor
                && (relatedQuestion.getRecipientType() == FeedbackParticipantType.TEAMS
                    || relatedQuestion.getRecipientType() == FeedbackParticipantType.TEAMS_IN_SAME_SECTION
                    || relatedQuestion.getRecipientType() == FeedbackParticipantType.TEAMS_EXCLUDING_SELF)
                && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                && recipient.equals(student.getTeamName());
        boolean isVisibleToTeamGiver = studentsEmailInTeam != null && !isInstructor
                && relatedQuestion.getGiverType() == FeedbackParticipantType.TEAMS
                && giver.equals(student.getTeamName());
        boolean isVisibleToOwnTeamMembers = studentsEmailInTeam != null && !isInstructor
                && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                && studentsEmailInTeam.contains(giver);
        boolean isVisibleToReceiverTeamMembers = studentsEmailInTeam != null && !isInstructor
                && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                && studentsEmailInTeam.contains(recipient);

        return isVisibleToInstructor || isVisibleToRecipient || isVisibleToGiver
                || isVisibleToStudents || isVisibleToTeamRecipient || isVisibleToTeamGiver
                || isVisibleToOwnTeamMembers || isVisibleToReceiverTeamMembers;
    }

    /**
     * Gets all responses for a session.
     */
    List<FeedbackResponse> getFeedbackResponsesForSession(
            FeedbackSession feedbackSession, String courseId) {
        return frDb.getFeedbackResponsesForSession(feedbackSession, courseId);
    }

    /**
     * Gets all responses given to/from a section in a feedback session in a course.
     *
     * @param feedbackSession the session
     * @param courseId the course ID of the session
     * @param sectionName if null, will retrieve all responses in the session
     * @param fetchType if not null, will retrieve responses by giver, receiver sections, or both
     * @return a list of responses
     */
    public List<FeedbackResponse> getFeedbackResponsesForSessionInSection(
            FeedbackSession feedbackSession, String courseId, @Nullable String sectionName,
            @Nullable FeedbackResultFetchType fetchType) {
        if (sectionName == null) {
            return getFeedbackResponsesForSession(feedbackSession, courseId);
        }
        return frDb.getFeedbackResponsesForSessionInSection(feedbackSession, courseId, sectionName, fetchType);
    }

    /**
     * Gets all responses given to/from a section for a question.
     *
     * @param feedbackQuestionId the question UUID
     * @param sectionName if null, will retrieve all responses for the question
     * @return a list of responses
     */
    public List<FeedbackResponse> getFeedbackResponsesForQuestionInSection(
            UUID feedbackQuestionId, @Nullable String sectionName, FeedbackResultFetchType fetchType) {
        if (sectionName == null) {
            return getFeedbackResponsesForQuestion(feedbackQuestionId);
        }
        return frDb.getFeedbackResponsesForQuestionInSection(feedbackQuestionId, sectionName, fetchType);
    }

    /**
     * Returns feedback responses given/received by an instructor.
     */
    private List<FeedbackResponse> getFeedbackResponsesToOrFromInstructorForQuestion(
            FeedbackQuestion question, Instructor instructor) {
        Set<FeedbackResponse> viewableResponses = new HashSet<>();

        // Add responses that the instructor submitted him/herself
        if (question.getGiverType() == FeedbackParticipantType.INSTRUCTORS) {
            viewableResponses.addAll(
                    getFeedbackResponsesFromGiverForQuestion(question.getId(), instructor.getEmail())
            );
        }

        // Add responses that user is a receiver of when response is visible to receiver or instructors
        if (question.getRecipientType() == FeedbackParticipantType.INSTRUCTORS
                && (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                || question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS))) {
            viewableResponses.addAll(
                    getFeedbackResponsesForRecipientForQuestion(question.getId(), instructor.getEmail())
            );
        }

        return new ArrayList<>(viewableResponses);
    }

    /**
     * Returns viewable feedback responses for a student.
     */
    private List<FeedbackResponse> getViewableFeedbackResponsesForStudentForQuestion(
            FeedbackQuestion question, Student student, CourseRoster courseRoster) {
        Set<FeedbackResponse> viewableResponses = new HashSet<>();

        // Add responses that the student submitted him/herself
        if (question.getGiverType() != FeedbackParticipantType.INSTRUCTORS) {
            viewableResponses.addAll(
                    getFeedbackResponsesFromGiverForQuestion(question.getId(), student.getEmail())
            );
        }

        // Add responses that user is a receiver of when response is visible to receiver
        if (question.getRecipientType() != FeedbackParticipantType.INSTRUCTORS
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            viewableResponses.addAll(
                    getFeedbackResponsesForRecipientForQuestion(question.getId(), student.getEmail())
            );
        }

        if (question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
            viewableResponses.addAll(getFeedbackResponsesForQuestion(question.getId()));

            // Early return as STUDENTS covers all cases below.
            return new ArrayList<>(viewableResponses);
        }

        if (question.getRecipientType().isTeam()
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            viewableResponses.addAll(
                    getFeedbackResponsesForRecipientForQuestion(question.getId(), student.getTeamName())
            );
        }

        if (question.getGiverType() == FeedbackParticipantType.TEAMS
                || question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            viewableResponses.addAll(
                    getFeedbackResponsesFromTeamForQuestion(
                            question.getId(), question.getCourseId(), student.getTeamName(), courseRoster));
        }

        if (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {
            for (Student studentInTeam : courseRoster.getTeamToMembersTable().get(student.getTeamName())) {
                if (SanitizationHelper.areEmailsEqual(studentInTeam.getEmail(), student.getEmail())) {
                    continue;
                }
                List<FeedbackResponse> responses =
                        getFeedbackResponsesForRecipientForQuestion(question.getId(), studentInTeam.getEmail());
                viewableResponses.addAll(responses);
            }
        }

        return new ArrayList<>(viewableResponses);
    }

    /**
     * Gets all responses received by a user for a question.
     */
    private List<FeedbackResponse> getFeedbackResponsesForRecipientForQuestion(
            UUID feedbackQuestionId, String userEmail) {
        return frDb.getFeedbackResponsesForRecipientForQuestion(feedbackQuestionId, userEmail);
    }

    private void validateFeedbackResponse(FeedbackResponse feedbackResponse) throws InvalidParametersException {
        if (!feedbackResponse.isValid()) {
            throw new InvalidParametersException(feedbackResponse.getInvalidityInfo());
        }
    }

    /**
     * Checks whether instructors can see the question.
     */
    boolean checkCanInstructorsSeeQuestion(FeedbackQuestion feedbackQuestion) {
        boolean isResponseVisibleToInstructor =
                feedbackQuestion.getShowResponsesTo().contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isGiverVisibleToInstructor =
                feedbackQuestion.getShowGiverNameTo().contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isRecipientVisibleToInstructor =
                feedbackQuestion.getShowRecipientNameTo().contains(FeedbackParticipantType.INSTRUCTORS);
        return isResponseVisibleToInstructor && isGiverVisibleToInstructor && isRecipientVisibleToInstructor;
    }

    /**
     * Checks whether instructors can see the comment.
     */
    boolean checkCanInstructorsSeeComment(FeedbackResponseComment feedbackResponseComment) {
        boolean isCommentVisibleToInstructor =
                feedbackResponseComment.getShowCommentTo().contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isGiverVisibleToInstructor =
                feedbackResponseComment.getShowGiverNameTo().contains(FeedbackParticipantType.INSTRUCTORS);
        return isCommentVisibleToInstructor && isGiverVisibleToInstructor;
    }

}
