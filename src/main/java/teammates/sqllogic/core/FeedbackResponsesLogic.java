package teammates.sqllogic.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.SqlCourseRoster;
import teammates.common.datatransfer.SqlSessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.RequestTracer;
import teammates.storage.sqlapi.FeedbackResponsesDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.storage.sqlentity.responses.FeedbackMissingResponse;
import teammates.storage.sqlentity.responses.FeedbackRankRecipientsResponse;

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
                   question.getRecipientType().equals(FeedbackParticipantType.STUDENTS)
                || question.getRecipientType().equals(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF)
                || question.getRecipientType().equals(FeedbackParticipantType.STUDENTS_IN_SAME_SECTION)
                || question.getRecipientType().equals(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                || question.getRecipientType().equals(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF)
                || question.getRecipientType().equals(FeedbackParticipantType.GIVER)
                   && question.getGiverType().equals(FeedbackParticipantType.STUDENTS);

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
            UUID feedbackQuestionId, String courseId, String teamName, @Nullable SqlCourseRoster courseRoster) {

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
     * Updates a non-null feedback response by {@link FeedbackResponse}.
     *
     * <p>Cascade updates its associated feedback response comment
     * (e.g. associated response ID, giverSection and recipientSection).
     *
     * <p>If the giver/recipient field is changed, the response is updated by recreating the response
     * as question-giver-recipient is the primary key.
     *
     * @return updated feedback response
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     */
    public FeedbackResponse updateFeedbackResponseCascade(FeedbackResponse feedbackResponse)
            throws InvalidParametersException, EntityDoesNotExistException {

        FeedbackResponse oldResponse = frDb.getFeedbackResponse(feedbackResponse.getId());
        FeedbackResponse newResponse = frDb.updateFeedbackResponse(feedbackResponse);

        List<FeedbackResponseComment> oldResponseComments =
                frcLogic.getFeedbackResponseCommentForResponse(oldResponse.getId());

        for (FeedbackResponseComment oldResponseComment : oldResponseComments) {
            oldResponseComment.setGiverSection(newResponse.getGiverSection());
            oldResponseComment.setRecipientSection(newResponse.getRecipientSection());

            frcLogic.updateFeedbackResponseComment(oldResponseComment);
        }

        return newResponse;
    }

    /**
     * Deletes a feedback response cascade its associated feedback response comments.
     * Implicitly makes use of CascadeType.REMOVE.
     */
    public void deleteFeedbackResponsesAndCommentsCascade(FeedbackResponse feedbackResponse) {
        frDb.deleteFeedbackResponse(feedbackResponse);
    }

    /**
     * Deletes all feedback responses of a question cascade its associated comments.
     */
    public void deleteFeedbackResponsesForQuestionCascade(UUID feedbackQuestionId) {
        // delete all responses, comments of the question
        frDb.deleteFeedbackResponsesForQuestionCascade(feedbackQuestionId);
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
            frDb.deleteFeedbackResponse(response);
        }

        // delete responses to the entity
        List<FeedbackResponse> responsesToStudent =
                getFeedbackResponsesForRecipientForCourse(courseId, entityEmail);
        for (FeedbackResponse response : responsesToStudent) {
            frDb.deleteFeedbackResponse(response);
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
     * Gets all responses from a specific giver and recipient for a course.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromGiverAndRecipientForCourse(
            String courseId, String giverEmail, String recipientEmail) {
        assert courseId != null;
        assert giverEmail != null;
        assert recipientEmail != null;

        return frDb.getFeedbackResponsesForGiverAndRecipientForCourse(courseId, giverEmail, recipientEmail);
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
        SqlCourseRoster roster = new SqlCourseRoster(
                usersLogic.getStudentsForCourse(courseId),
                usersLogic.getInstructorsForCourse(courseId));

        for (FeedbackQuestion question : filteredQuestions) {
            makeRankRecipientQuestionResponsesConsistent(question, roster);
        }
    }

    /**
     * Makes the rankings by one giver in the response to a 'rank recipient question' consistent, after deleting a
     * student.
     * <p>
     *     Fails silently if the question type is not 'rank recipient question'.
     * </p>
     */
    private void makeRankRecipientQuestionResponsesConsistent(
            FeedbackQuestion question, SqlCourseRoster roster) {
        assert question.getQuestionDetailsCopy().getQuestionType()
                .equals(FeedbackQuestionType.RANK_RECIPIENTS);

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
     * <p>
     *     This is done by deleting responses that are no longer relevant to him in his new team.
     * </p>
     */
    public void updateFeedbackResponsesForChangingTeam(Course course, String newEmail, Team newTeam, Team oldTeam)
            throws InvalidParametersException, EntityDoesNotExistException {

        FeedbackQuestion qn;

        List<FeedbackResponse> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(course.getId(), newEmail);

        for (FeedbackResponse response : responsesFromUser) {
            qn = fqLogic.getFeedbackQuestion(response.getId());
            if (qn != null && qn.getGiverType() == FeedbackParticipantType.TEAMS) {
                deleteFeedbackResponsesForQuestionCascade(qn.getId());
            }
        }

        List<FeedbackResponse> responsesToUser =
                getFeedbackResponsesForRecipientForCourse(course.getId(), newEmail);

        for (FeedbackResponse response : responsesToUser) {
            qn = fqLogic.getFeedbackQuestion(response.getId());
            if (qn != null && qn.getGiverType() == FeedbackParticipantType.TEAMS) {
                deleteFeedbackResponsesForQuestionCascade(qn.getId());
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
            throws InvalidParametersException, EntityDoesNotExistException {

        List<FeedbackResponse> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(course.getId(), newEmail);

        for (FeedbackResponse response : responsesFromUser) {
            response.setGiverSection(newSection);
            frDb.updateFeedbackResponse(response);
            frcLogic.updateFeedbackResponseCommentsForResponse(response);
        }

        List<FeedbackResponse> responsesToUser =
                getFeedbackResponsesForRecipientForCourse(course.getId(), newEmail);

        for (FeedbackResponse response : responsesToUser) {
            response.setRecipientSection(newSection);
            frDb.updateFeedbackResponse(response);
            frcLogic.updateFeedbackResponseCommentsForResponse(response);
        }
    }

    /**
     * Updates a student's email in their given/received responses.
     */
    public void updateFeedbackResponsesForChangingEmail(String courseId, String oldEmail, String newEmail)
            throws InvalidParametersException, EntityDoesNotExistException {

        List<FeedbackResponse> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(courseId, oldEmail);

        for (FeedbackResponse response : responsesFromUser) {
            response.setGiver(newEmail);
            frDb.updateFeedbackResponse(response);
        }

        List<FeedbackResponse> responsesToUser =
                getFeedbackResponsesForRecipientForCourse(courseId, oldEmail);

        for (FeedbackResponse response : responsesToUser) {
            response.setRecipient(newEmail);
            frDb.updateFeedbackResponse(response);
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

    private SqlSessionResultsBundle buildResultsBundle(
            boolean isCourseWide, FeedbackSession feedbackSession, String courseId, String sectionName, UUID questionId,
            boolean isInstructor, String userEmail, Instructor instructor, Student student,
            SqlCourseRoster roster, List<FeedbackQuestion> allQuestions,
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
                    : roster.getTeamToMembersTable().getOrDefault(student.getTeam().getName(), Collections.emptyList())) {
                studentsEmailInTeam.add(studentInTeam.getEmail());
            }
        }

        // visibility table for each response and comment
        Map<FeedbackResponse, Boolean> responseGiverVisibilityTable = new HashMap<>();
        Map<FeedbackResponse, Boolean> responseRecipientVisibilityTable = new HashMap<>();
        Map<Long, Boolean> commentVisibilityTable = new HashMap<>();

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
                    userEmail, isInstructor, student, studentsEmailInTeam, response, correspondingQuestion, instructor);
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
            responseGiverVisibilityTable.put(response,
                    isNameVisibleToUser(correspondingQuestion, response, userEmail, isInstructor, true, roster));
            responseRecipientVisibilityTable.put(response,
                    isNameVisibleToUser(correspondingQuestion, response, userEmail, isInstructor, false, roster));
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
        List<FeedbackResponse> missingResponses = Collections.emptyList();
        if (isCourseWide) {
            missingResponses = buildMissingResponses(
                    instructor, responseGiverVisibilityTable, responseRecipientVisibilityTable, relatedQuestions,
                    existingResponses, roster, sectionName);
        }
        RequestTracer.checkRemainingTime();

        return new SqlSessionResultsBundle(relatedQuestions, relatedQuestionsNotVisibleForPreviewSet,
                relatedQuestionsWithCommentNotVisibleForPreview, existingResponses, missingResponses,
                responseGiverVisibilityTable, responseRecipientVisibilityTable, relatedCommentsMap,
                commentVisibilityTable, roster);
    }

    /**
     * Gets the session result for a feedback session.
     *
     * @param feedbackSession the feedback session
     * @param courseId the ID of the course
     * @param instructorEmail the instructor viewing the feedback session
     * @param questionId if not null, will only return partial bundle for the question
     * @param sectionName if not null, will only return partial bundle for the section
     * @param fetchType if not null, will fetch responses by giver, receiver sections, or both
     * @return the session result bundle
     */
    public SqlSessionResultsBundle getSessionResultsForCourse(
            FeedbackSession feedbackSession, String courseId, String instructorEmail,
            @Nullable UUID questionId, @Nullable String sectionName, @Nullable FeedbackResultFetchType fetchType) {

        SqlCourseRoster roster = new SqlCourseRoster(
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
     * @param courseId the ID of the course
     * @param userEmail the user viewing the feedback session
     * @param isInstructor true if the user is an instructor
     * @param questionId if not null, will only return partial bundle for the question
     * @param isPreviewResults true if getting session results for preview purpose
     * @return the session result bundle
     */
    public SqlSessionResultsBundle getSessionResultsForUser(
            FeedbackSession feedbackSession, String courseId, String userEmail, boolean isInstructor,
            @Nullable UUID questionId, boolean isPreviewResults) {
        SqlCourseRoster roster = new SqlCourseRoster(
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
    private List<FeedbackResponse> buildMissingResponses(
            Instructor instructor, Map<FeedbackResponse, Boolean> responseGiverVisibilityTable,
            Map<FeedbackResponse, Boolean> responseRecipientVisibilityTable, List<FeedbackQuestion> relatedQuestions,
            List<FeedbackResponse> existingResponses, SqlCourseRoster courseRoster, @Nullable String sectionName) {

        // first get all possible giver recipient pairs
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

        List<FeedbackResponse> missingResponses = new ArrayList<>();
        // build dummy responses
        for (Map.Entry<FeedbackQuestion, Map<String, Set<String>>> currGiverRecipientMapEntry
                : questionCompleteGiverRecipientMap.entrySet()) {
            FeedbackQuestion correspondingQuestion = currGiverRecipientMapEntry.getKey();

            for (Map.Entry<String, Set<String>> giverRecipientEntry
                    : currGiverRecipientMapEntry.getValue().entrySet()) {
                // giver
                String giverIdentifier = giverRecipientEntry.getKey();
                SqlCourseRoster.ParticipantInfo giverInfo = courseRoster.getInfoForIdentifier(giverIdentifier);

                for (String recipientIdentifier : giverRecipientEntry.getValue()) {
                    // recipient
                    SqlCourseRoster.ParticipantInfo recipientInfo = courseRoster.getInfoForIdentifier(recipientIdentifier);

                    // skip responses not in current section
                    if (sectionName != null
                            && !giverInfo.getSectionName().equals(sectionName)
                            && !recipientInfo.getSectionName().equals(sectionName)) {
                        continue;
                    }

                    FeedbackResponse missingResponse = new FeedbackMissingResponse(
                            correspondingQuestion,
                            giverIdentifier, giverInfo.getSectionName(),
                            recipientIdentifier, recipientInfo.getSectionName());

                    // check visibility of the missing response
                    boolean isVisibleResponse = isResponseVisibleForUser(
                            instructor.getEmail(), true, null, Collections.emptySet(),
                            missingResponse, correspondingQuestion, instructor);
                    if (!isVisibleResponse) {
                        continue;
                    }

                    // generate giver/recipient name visibility table
                    responseGiverVisibilityTable.put(missingResponse,
                            isNameVisibleToUser(correspondingQuestion, missingResponse,
                                    instructor.getEmail(), true, true, courseRoster));
                    responseRecipientVisibilityTable.put(missingResponse,
                            isNameVisibleToUser(correspondingQuestion, missingResponse,
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
            FeedbackResponse response,
            String userEmail,
            boolean isInstructor, boolean isGiverName, SqlCourseRoster roster) {

        if (question == null) {
            return false;
        }

        // Early return if user is giver
        if (question.getGiverType() == FeedbackParticipantType.TEAMS) {
            // if response is given by team, then anyone in the team can see the response
            if (roster.isStudentInTeam(userEmail, response.getGiver())) {
                return true;
            }
        } else {
            if (response.getGiver().equals(userEmail)) {
                return true;
            }
        }

        return isFeedbackParticipantNameVisibleToUser(question, response,
                userEmail, isInstructor, isGiverName, roster);
    }

    private boolean isFeedbackParticipantNameVisibleToUser(
            FeedbackQuestion question, FeedbackResponse response,
            String userEmail, boolean isInstructor, boolean isGiverName, SqlCourseRoster roster) {
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
            case OWN_TEAM_MEMBERS:
            case OWN_TEAM_MEMBERS_INCLUDING_SELF:
                // Refers to Giver's Team Members
                if (roster.isStudentsInSameTeam(response.getGiver(), userEmail)) {
                    return true;
                }
                break;
            case RECEIVER:
                // Response to team
                if (question.getRecipientType().isTeam()) {
                    if (roster.isStudentInTeam(userEmail, response.getRecipient())) {
                        // this is a team name
                        return true;
                    }
                    break;
                    // Response to individual
                } else if (response.getRecipient().equals(userEmail)) {
                    return true;
                } else {
                    break;
                }
            case RECEIVER_TEAM_MEMBERS:
                // Response to team; recipient = teamName
                if (question.getRecipientType().isTeam()) {
                    if (roster.isStudentInTeam(userEmail, response.getRecipient())) {
                        // this is a team name
                        return true;
                    }
                    break;
                } else if (roster.isStudentsInSameTeam(response.getRecipient(), userEmail)) {
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
            String userEmail, boolean isInstructor, Student student,
            Set<String> studentsEmailInTeam, FeedbackResponse response,
            FeedbackQuestion relatedQuestion, Instructor instructor) {

        boolean isVisibleResponse = false;
        if (isInstructor && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)
                || response.getRecipient().equals(userEmail)
                && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                || response.getGiver().equals(userEmail)
                || !isInstructor && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
            isVisibleResponse = true;
        } else if (studentsEmailInTeam != null && !isInstructor) {
            if ((relatedQuestion.getRecipientType() == FeedbackParticipantType.TEAMS
                    || relatedQuestion.getRecipientType() == FeedbackParticipantType.TEAMS_IN_SAME_SECTION
                    || relatedQuestion.getRecipientType() == FeedbackParticipantType.TEAMS_EXCLUDING_SELF)
                    && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                    && response.getRecipient().equals(student.getTeamName())) {
                isVisibleResponse = true;
            } else if (relatedQuestion.getGiverType() == FeedbackParticipantType.TEAMS
                    && response.getGiver().equals(student.getTeamName())) {
                isVisibleResponse = true;
            } else if (relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                    && studentsEmailInTeam.contains(response.getGiver())) {
                isVisibleResponse = true;
            } else if (relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                    && studentsEmailInTeam.contains(response.getRecipient())) {
                isVisibleResponse = true;
            }
        }
        if (isVisibleResponse && instructor != null) {
            boolean isGiverSectionRestricted =
                    !instructor.isAllowedForPrivilege(response.getGiverSectionName(),
                            response.getFeedbackQuestion().getFeedbackSession().getName(),
                            Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
            // If instructors are not restricted to view the giver's section,
            // they are allowed to view responses to GENERAL, subject to visibility options
            boolean isRecipientSectionRestricted =
                    relatedQuestion.getRecipientType() != FeedbackParticipantType.NONE
                            && !instructor.isAllowedForPrivilege(response.getRecipientSectionName(),
                            response.getFeedbackQuestion().getFeedbackSession().getName(),
                            Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);

            boolean isNotAllowedForInstructor = isGiverSectionRestricted || isRecipientSectionRestricted;
            if (isNotAllowedForInstructor) {
                isVisibleResponse = false;
            }
        }
        return isVisibleResponse;
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
            FeedbackQuestion question, Student student, SqlCourseRoster courseRoster) {
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
                if (studentInTeam.getEmail().equals(student.getEmail())) {
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
