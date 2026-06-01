package teammates.logic.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackMissingResponse;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.RequestTracer;
import teammates.common.util.SanitizationHelper;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;
import teammates.storage.entity.responses.FeedbackRankRecipientsResponse;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.request.FeedbackResponsesRequest;
import teammates.ui.request.FeedbackResponsesRequest.FeedbackResponseRequest;

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
    private ResponseInstructorCommentsLogic frcLogic;

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
            UsersLogic usersLogic, FeedbackQuestionsLogic fqLogic, ResponseInstructorCommentsLogic frcLogic) {
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
     * Deletes the giver comment for a feedback response by clearing it.
     *
     * @throws EntityDoesNotExistException if the feedback response does not exist
     */
    public FeedbackResponse deleteFeedbackResponseGiverComment(UUID frId) throws EntityDoesNotExistException {
        FeedbackResponse feedbackResponse = frDb.getFeedbackResponse(frId);
        if (feedbackResponse == null) {
            throw new EntityDoesNotExistException("The feedback response does not exist.");
        }

        feedbackResponse.setGiverComment(null);
        return feedbackResponse;
    }

    /**
     * Returns true if the responses of the question are visible to students.
     */
    public boolean isResponseOfFeedbackQuestionVisibleToStudent(FeedbackQuestion question) {
        if (question.isResponseVisibleTo(ViewerType.STUDENTS)) {
            return true;
        }
        boolean isStudentRecipientType =
                   question.getRecipientType() == QuestionRecipientType.STUDENTS
                || question.getRecipientType() == QuestionRecipientType.STUDENTS_EXCLUDING_SELF
                || question.getRecipientType() == QuestionRecipientType.STUDENTS_IN_SAME_SECTION
                || question.getRecipientType() == QuestionRecipientType.OWN_TEAM_MEMBERS
                || question.getRecipientType() == QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF
                || question.getRecipientType() == QuestionRecipientType.SELF
                   && question.getGiverType() == QuestionGiverType.STUDENTS;

        if ((isStudentRecipientType || question.getRecipientType().isTeam())
                && question.isResponseVisibleTo(ViewerType.RECEIVER)) {
            return true;
        }
        if (question.getGiverType() == QuestionGiverType.TEAMS
                || question.isResponseVisibleTo(ViewerType.OWN_TEAM_MEMBERS)) {
            return true;
        }
        return question.isResponseVisibleTo(ViewerType.RECEIVER_TEAM_MEMBERS);
    }

    /**
     * Returns true if the responses of the question are visible to instructors.
     */
    public boolean isResponseOfFeedbackQuestionVisibleToInstructor(FeedbackQuestion question) {
        return question.isResponseVisibleTo(ViewerType.INSTRUCTORS);
    }

    /**
     * Checks whether a giver has responded a session.
     */
    public boolean hasGiverRespondedForSession(String giverIdentifier, Set<FeedbackQuestion> questions) {
        assert questions != null;

        for (FeedbackQuestion question : questions) {
            boolean hasResponse = question
                    .getFeedbackResponses()
                    .stream()
                    .anyMatch(response -> {
                        ResponseGiver responseGiver = response.getGiver();
                        return responseGiver.getGiverUser() != null
                                && responseGiver.getGiverUser().getEmail().equals(giverIdentifier);
                    });
            if (hasResponse) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get existing feedback responses from instructor for the given question.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromInstructorForQuestion(
            FeedbackQuestion question, Instructor instructor) {
        return frDb.getFeedbackResponsesFromGiverForQuestion(
                question.getId(), instructor.getId(), null);
    }

    /**
     * Get existing feedback responses from student or his team for the given
     * question.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromStudentOrTeamForQuestion(
            FeedbackQuestion question, Student student) {
        if (question.getGiverType() == QuestionGiverType.TEAMS) {
            return getFeedbackResponsesFromTeamForQuestion(
                    question.getId(), question.getCourseId(), student.getTeam(), null);
        }
        return frDb.getFeedbackResponsesFromGiverForQuestion(question.getId(), student.getId(), null);
    }

    private List<FeedbackResponse> getFeedbackResponsesFromTeamForQuestion(
            UUID feedbackQuestionId, String courseId, Team team, @Nullable CourseRoster courseRoster) {

        List<FeedbackResponse> responses = new ArrayList<>();
        List<Student> studentsInTeam = courseRoster == null
                ? usersLogic.getStudentsForTeam(team.getName(), courseId)
                : courseRoster.getTeamToMembers().get(team.getName());

        for (Student student : studentsInTeam) {
            responses.addAll(frDb.getFeedbackResponsesFromGiverForQuestion(
                    feedbackQuestionId, student.getId(), null));
        }

        responses.addAll(frDb.getFeedbackResponsesFromGiverForQuestion(
                feedbackQuestionId, null, team.getId()));
        return responses;
    }

    /**
     * Submits feedback responses from a student or the student's team for one or more feedback questions
     * in the same feedback session.
     */
    public List<FeedbackResponse> submitFeedbackResponsesFromStudent(
            FeedbackSession feedbackSession, Student student, FeedbackResponsesRequest submitRequest)
            throws InvalidOperationException, InvalidParametersException {
        List<FeedbackResponse> submittedResponses = new ArrayList<>();
        for (Map.Entry<UUID, List<FeedbackResponseRequest>> entry : submitRequest.getQuestionResponses().entrySet()) {
            FeedbackQuestion feedbackQuestion = fqLogic.getFeedbackQuestion(entry.getKey());
            if (feedbackQuestion == null) {
                throw new InvalidOperationException("The feedback question does not exist.");
            }

            if (!feedbackQuestion.getFeedbackSession().getId().equals(feedbackSession.getId())) {
                throw new InvalidOperationException("The feedback question does not belong to the feedback session.");
            }

            if (feedbackQuestion.getGiverType() != QuestionGiverType.STUDENTS
                    && feedbackQuestion.getGiverType() != QuestionGiverType.TEAMS) {
                throw new InvalidOperationException("Feedback question is not answerable for students");
            }

            ResponseGiver responseGiver = feedbackQuestion.getGiverType() == QuestionGiverType.TEAMS
                    ? new ResponseGiver(student.getTeam())
                    : new ResponseGiver(student);
            List<FeedbackResponse> existingResponses = getFeedbackResponsesFromStudentOrTeamForQuestion(
                    feedbackQuestion, student);

            List<FeedbackResponse> responses = submitFeedbackResponses(
                    feedbackQuestion, responseGiver, existingResponses, student, entry.getValue());

            submittedResponses.addAll(responses);
        }

        return submittedResponses;
    }

    /**
     * Submits feedback responses from an instructor for one or more feedback questions in the same feedback session.
     */
    public List<FeedbackResponse> submitFeedbackResponsesFromInstructor(
            FeedbackSession feedbackSession, Instructor instructor, FeedbackResponsesRequest submitRequest)
            throws InvalidOperationException, InvalidParametersException {
        List<FeedbackResponse> submittedResponses = new ArrayList<>();
        for (Map.Entry<UUID, List<FeedbackResponseRequest>> entry : submitRequest.getQuestionResponses().entrySet()) {
            FeedbackQuestion feedbackQuestion = fqLogic.getFeedbackQuestion(entry.getKey());
            if (feedbackQuestion == null) {
                throw new InvalidOperationException("The feedback question does not exist.");
            }

            if (!feedbackQuestion.getFeedbackSession().getId().equals(feedbackSession.getId())) {
                throw new InvalidOperationException("The feedback question does not belong to the feedback session.");
            }

            if (feedbackQuestion.getGiverType() != QuestionGiverType.INSTRUCTORS
                    && feedbackQuestion.getGiverType() != QuestionGiverType.SESSION_CREATOR) {
                throw new InvalidOperationException("Feedback question is not answerable for instructors");
            }

            ResponseGiver responseGiver = new ResponseGiver(instructor);
            List<FeedbackResponse> existingResponses = getFeedbackResponsesFromInstructorForQuestion(
                    feedbackQuestion, instructor);

            List<FeedbackResponse> responses = submitFeedbackResponses(
                    feedbackQuestion, responseGiver, existingResponses, null, entry.getValue());

            submittedResponses.addAll(responses);
        }

        return submittedResponses;
    }

    private List<FeedbackResponse> submitFeedbackResponses(FeedbackQuestion feedbackQuestion,
            ResponseGiver responseGiver, List<FeedbackResponse> existingResponses, @Nullable Student student,
            List<FeedbackResponseRequest> submitResponses)
            throws InvalidOperationException, InvalidParametersException {
        // TODO: Optimise logic to reduce the number of calls to the database.
        FeedbackQuestionDetails questionDetails = feedbackQuestion.getQuestionDetailsCopy();
        Optional<List<String>> dynamicallyGeneratedOptions = fqLogic.getDynamicallyGeneratedOptions(
                feedbackQuestion, student);

        if (dynamicallyGeneratedOptions.isPresent()) {
            // Dynamically generated options are only supported for MCQ and MSQ questions.
            if (questionDetails instanceof FeedbackMcqQuestionDetails feedbackMcqQuestionDetails) {
                feedbackMcqQuestionDetails.setMcqChoices(dynamicallyGeneratedOptions.get());
            } else if (questionDetails instanceof FeedbackMsqQuestionDetails feedbackMsqQuestionDetails) {
                feedbackMsqQuestionDetails.setMsqChoices(dynamicallyGeneratedOptions.get());
            }
        }

        Set<ResponseRecipient> recipientsOfTheQuestion = fqLogic.getRecipientsOfQuestion(
                feedbackQuestion, responseGiver);
        Map<String, ResponseRecipient> recipientsByIdentifier = recipientsOfTheQuestion.stream()
                .collect(Collectors.toMap(ResponseRecipient::getIdentifier, recipient -> recipient));

        Map<UUID, FeedbackResponse> existingResponsesById = new HashMap<>();
        existingResponses.forEach(response -> existingResponsesById.put(response.getId(), response));

        List<String> recipients = submitResponses.stream()
                .map(FeedbackResponseRequest::getRecipient)
                .toList();

        for (String recipient : recipients) {
            if (recipient == null || !recipientsByIdentifier.containsKey(recipient)) {
                throw new InvalidOperationException(
                        "The recipient " + recipient + " is not a valid recipient of the question");
            }
        }

        List<FeedbackResponse> feedbackResponses = new ArrayList<>();
        Set<UUID> submittedResponseIds = new HashSet<>();

        for (FeedbackResponseRequest responseRequest : submitResponses) {
            String recipient = responseRequest.getRecipient();
            ResponseRecipient responseRecipient = recipientsByIdentifier.get(recipient);
            FeedbackResponseDetails responseDetails = responseRequest.getResponseDetails();
            UUID responseId = responseRequest.getResponseId();

            if (responseId != null) {
                FeedbackResponse existingFeedbackResponse = existingResponsesById.get(responseId);
                if (existingFeedbackResponse == null) {
                    throw new InvalidOperationException("The response " + responseId + " does not exist.");
                }
                // Update the existing response
                submittedResponseIds.add(responseId);
                existingFeedbackResponse.setGiver(responseGiver);
                existingFeedbackResponse.setRecipient(responseRecipient);
                existingFeedbackResponse.setFeedbackResponseDetails(responseDetails);
                existingFeedbackResponse.setGiverComment(responseRequest.getGiverComment());
                feedbackResponses.add(existingFeedbackResponse);
                validateFeedbackResponse(existingFeedbackResponse);
            } else {
                // Create a new response
                FeedbackResponse feedbackResponse = FeedbackResponse.makeResponse(
                        responseGiver,
                        responseRecipient,
                        responseDetails,
                        responseRequest.getGiverComment());

                feedbackQuestion.addFeedbackResponse(feedbackResponse);
                feedbackResponses.add(feedbackResponse);
                validateFeedbackResponse(feedbackResponse);
                frDb.createFeedbackResponse(feedbackResponse);
            }
        }

        List<FeedbackResponseDetails> responseDetails = feedbackResponses.stream()
                .map(FeedbackResponse::getFeedbackResponseDetailsCopy)
                .toList();

        int numRecipients = feedbackQuestion.getNumOfEntitiesToGiveFeedbackTo();
        if (numRecipients == Const.MAX_POSSIBLE_RECIPIENTS
                || numRecipients > recipientsOfTheQuestion.size()) {
            numRecipients = recipientsOfTheQuestion.size();
        }

        List<String> questionSpecificErrors = questionDetails
                .validateResponsesDetails(responseDetails, numRecipients);

        if (!questionSpecificErrors.isEmpty()) {
            throw new InvalidParametersException(questionSpecificErrors.toString());
        }

        // Delete responses that are deleted by the user
        List<FeedbackResponse> feedbackResponsesToDelete = existingResponsesById.entrySet().stream()
                .filter(entry -> !submittedResponseIds.contains(entry.getKey()))
                .map(Entry::getValue)
                .toList();

        for (FeedbackResponse feedbackResponse : feedbackResponsesToDelete) {
            deleteFeedbackResponsesAndCommentsCascade(feedbackResponse);
        }

        return feedbackResponses;
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
        Set<FeedbackResponse> responses = feedbackQuestion.getFeedbackResponses();
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
     * Gets all responses given by a user for a question.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromGiverForQuestion(
            UUID feedbackQuestionId, UUID giverUserId) {
        return frDb.getFeedbackResponsesFromGiverForQuestion(feedbackQuestionId, giverUserId, null);
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

        QuestionGiverType giverType = question.getGiverType();
        List<FeedbackResponse> responses = new ArrayList<>();
        int numberOfRecipients = 0;

        switch (giverType) {
        case INSTRUCTORS, SESSION_CREATOR:
            for (Instructor instructor : roster.getInstructors()) {
                ResponseGiver responseGiver = new ResponseGiver(instructor);
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, responseGiver, roster).size();
                responses = getFeedbackResponsesFromGiverForQuestion(question.getId(), instructor.getId());
            }
            break;
        case TEAMS:
            Map<String, List<Student>> teams = roster.getTeamToMembers();
            for (Map.Entry<String, List<Student>> entry : teams.entrySet()) {
                String teamName = entry.getKey();
                ResponseGiver teamResponseGiver = new ResponseGiver(roster.getTeamNameToTeam().get(teamName));
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, teamResponseGiver, roster).size();
                Team team = roster.getTeamNameToTeam().get(teamName);
                responses =
                        getFeedbackResponsesFromTeamForQuestion(
                                question.getId(), question.getCourseId(), team, roster);
            }
            break;
        default:
            for (Student student : roster.getStudents()) {
                ResponseGiver responseGiver = new ResponseGiver(student);
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, responseGiver, roster).size();
                responses = getFeedbackResponsesFromGiverForQuestion(question.getId(), student.getId());
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

    private List<FeedbackQuestion> getQuestionsForSession(
            FeedbackSession feedbackSession, @Nullable UUID questionId) {
        if (questionId == null) {
            return fqLogic.getFeedbackQuestionsForSession(feedbackSession);
        }
        FeedbackQuestion fq = fqLogic.getFeedbackQuestion(questionId);
        return fq == null ? Collections.emptyList() : Collections.singletonList(fq);
    }

    private SessionResultsBundle buildResultsBundle(
            boolean isCourseWide, String sectionName, User user,
            CourseRoster roster, List<FeedbackQuestion> relatedQuestions,
            List<FeedbackResponse> allResponses, boolean isPreviewResults) {
        List<FeedbackResponse> relatedResponses = new ArrayList<>();
        Map<FeedbackResponse, List<ResponseInstructorComment>> relatedCommentsMap = new HashMap<>();
        Set<FeedbackQuestion> relatedQuestionsNotVisibleForPreviewSet = new HashSet<>();
        Set<FeedbackQuestion> relatedQuestionsWithCommentNotVisibleForPreview = new HashSet<>();
        for (FeedbackQuestion qn : relatedQuestions) {
            // set questions that should not be visible to instructors if results are being previewed
            if (isPreviewResults && !checkCanInstructorsSeeQuestion(qn)) {
                relatedQuestionsNotVisibleForPreviewSet.add(qn);
            }
        }

        Set<String> studentsEmailInTeam = new HashSet<>();
        if (user instanceof Student student) {
            for (Student studentInTeam
                    : roster.getTeamToMembers().getOrDefault(student.getTeamName(), Collections.emptyList())) {
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
                    user, studentsEmailInTeam,
                    response.getGiver(), response.getRecipient(),
                    correspondingQuestion);
            if (!isVisibleResponse) {
                continue;
            }

            relatedResponses.add(response);

            // generate giver/recipient name visibility table
            responseGiverVisibilityTable.put(response.getId(),
                    isNameVisibleToUser(correspondingQuestion, response.getGiver(), response.getRecipient(),
                        user, true));
            responseRecipientVisibilityTable.put(response.getId(),
                    isNameVisibleToUser(correspondingQuestion, response.getGiver(), response.getRecipient(),
                        user, false));
        }
        RequestTracer.checkRemainingTime();

        // load comment(s) for related responses only
        List<UUID> relatedResponseIds = new ArrayList<>();
        for (FeedbackResponse relatedResponse : relatedResponses) {
            relatedResponseIds.add(relatedResponse.getId());
        }
        List<ResponseInstructorComment> allComments = frcLogic.getResponseInstructorCommentsForResponses(relatedResponseIds);
        RequestTracer.checkRemainingTime();

        // build comment
        for (ResponseInstructorComment frc : allComments) {
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
                    user, relatedResponse, relatedQuestion, frc);
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
                    frcLogic.checkIsNameVisibleToUser(frc, relatedResponse, user));
        }
        RequestTracer.checkRemainingTime();

        List<FeedbackResponse> existingResponses = new ArrayList<>(relatedResponses);
        List<FeedbackMissingResponse> missingResponses = Collections.emptyList();
        if (isCourseWide && user instanceof Instructor instructor) {
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
     * @return the session result bundle
     */
    public SessionResultsBundle getSessionResults(
            FeedbackSession feedbackSession, String instructorEmail,
            @Nullable UUID questionId, @Nullable String sectionName) {

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
            allResponses = getFeedbackResponsesForSessionInSection(feedbackSession, courseId, sectionName);
        } else {
            allResponses = getFeedbackResponsesForQuestionInSection(questionId, sectionName);
        }
        RequestTracer.checkRemainingTime();

        // consider the current viewing user
        Instructor instructor = usersLogic.getInstructorForEmail(courseId, instructorEmail);

        return buildResultsBundle(true, sectionName, instructor, roster, allQuestions, allResponses, false);
    }

    /**
     * Gets the session result for a feedback session for the given user.
     *
     * @param feedbackSession the feedback session
     * @param user the user viewing the feedback session
     * @param isPreviewResults true if getting session results for preview purpose
     * @return the session result bundle
     */
    public SessionResultsBundle getSessionResultsForUser(
            FeedbackSession feedbackSession, User user, boolean isPreviewResults) {
        String courseId = feedbackSession.getCourseId();
        CourseRoster roster = new CourseRoster(
                usersLogic.getStudentsForCourse(courseId),
                usersLogic.getInstructorsForCourse(courseId));

        // load question(s)
        List<FeedbackQuestion> allQuestions = fqLogic.getFeedbackQuestionsForSession(feedbackSession)
                .stream()
                .filter(question -> isQuestionRelevantForUserResult(question, user))
                .toList();

        // load response(s)
        List<FeedbackResponse> allResponses = new ArrayList<>();
        for (FeedbackQuestion question : allQuestions) {
            // load viewable responses for students/instructors proactively
            // this is cost-effective as in most of time responses for the whole session will not be viewable to individuals
            List<FeedbackResponse> viewableResponses = Collections.emptyList();
            if (user instanceof Instructor instructor) {
                viewableResponses = getFeedbackResponsesToOrFromInstructorForQuestion(question, instructor);
            } else if (user instanceof Student student) {
                viewableResponses = getViewableFeedbackResponsesForStudentForQuestion(question, student, roster);
            }

            allResponses.addAll(viewableResponses);
        }

        return buildResultsBundle(false, null, user, roster, allQuestions, allResponses, isPreviewResults);
    }

    /**
     * Returns whether the question is relevant to the user in user-scoped result view.
     */
    private boolean isQuestionRelevantForUserResult(FeedbackQuestion question, User user) {
        if (user instanceof Instructor instructor) {
            boolean isRelevantAsGiver = question.getGiverType() == QuestionGiverType.INSTRUCTORS
                    || question.getGiverType() == QuestionGiverType.SESSION_CREATOR
                            && SanitizationHelper.areEmailsEqual(
                                    question.getFeedbackSession().getCreatorEmail(), instructor.getEmail());
            boolean isRelevantAsRecipient = isResponseOfFeedbackQuestionVisibleToInstructor(question)
                    && question.getRecipientType() == QuestionRecipientType.INSTRUCTORS;
            return isRelevantAsGiver || isRelevantAsRecipient;
        }

        if (user instanceof Student) {
            boolean isRelevantAsGiver = question.getGiverType() == QuestionGiverType.STUDENTS
                    || question.getGiverType() == QuestionGiverType.TEAMS;
            boolean isRelevantAsRecipient = isResponseOfFeedbackQuestionVisibleToStudent(question);
            return isRelevantAsGiver || isRelevantAsRecipient;
        }

        return false;
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
        Map<FeedbackQuestion, Map<ResponseGiver, Set<ResponseRecipient>>> questionCompleteGiverRecipientMap =
                new HashMap<>();
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
            Map<ResponseGiver, Set<ResponseRecipient>> currGiverRecipientMap =
                    questionCompleteGiverRecipientMap.get(existingResponse.getFeedbackQuestion());
            if (!currGiverRecipientMap.containsKey(existingResponse.getGiver())) {
                continue;
            }
            currGiverRecipientMap.get(existingResponse.getGiver()).remove(existingResponse.getRecipient());
        }

        List<FeedbackMissingResponse> missingResponses = new ArrayList<>();
        // build dummy responses
        for (Map.Entry<FeedbackQuestion, Map<ResponseGiver, Set<ResponseRecipient>>> currGiverRecipientMapEntry
                : questionCompleteGiverRecipientMap.entrySet()) {
            FeedbackQuestion correspondingQuestion = currGiverRecipientMapEntry.getKey();

            for (Map.Entry<ResponseGiver, Set<ResponseRecipient>> giverRecipientEntry
                    : currGiverRecipientMapEntry.getValue().entrySet()) {
                // giver
                ResponseGiver giver = giverRecipientEntry.getKey();

                for (ResponseRecipient recipient : giverRecipientEntry.getValue()) {
                    if (sectionName != null
                            && !sectionName.equals(giver.getSectionName())
                            && !sectionName.equals(recipient.getSectionName())) {
                        continue;
                    }

                    // recipient
                    FeedbackMissingResponse missingResponse = new FeedbackMissingResponse(
                            correspondingQuestion,
                            giver,
                            recipient);

                    boolean isVisibleResponse = isResponseVisibleForUser(
                            instructor, Collections.emptySet(),
                            missingResponse.giver(), missingResponse.recipient(),
                            correspondingQuestion);
                    if (!isVisibleResponse) {
                        continue;
                    }

                    // generate giver/recipient name visibility table
                    responseGiverVisibilityTable.put(missingResponse.id(),
                            isNameVisibleToUser(correspondingQuestion, missingResponse.giver(), missingResponse.recipient(),
                                    instructor, true));
                    responseRecipientVisibilityTable.put(missingResponse.id(),
                            isNameVisibleToUser(correspondingQuestion, missingResponse.giver(), missingResponse.recipient(),
                                    instructor, false));
                    missingResponses.add(missingResponse);
                }
            }
        }

        return missingResponses;
    }

    /**
     * Checks whether the giver name of a response is visible to an user.
     */
    private boolean isNameVisibleToUser(
            FeedbackQuestion question,
            ResponseGiver responseGiver, ResponseRecipient responseRecipient,
            User user, boolean isGiverName) {

        if (question == null) {
            return false;
        }

        // Early return if user is giver
        if (responseGiver.isGiverTeam()) {
            // if response is given by team, then anyone in the team can see the response
            if (user instanceof Student student
                    && student.getTeam().equals(responseGiver.getGiverTeam())) {
                return true;
            }
        } else {
            if (Objects.equals(responseGiver.getGiverUser(), user)) {
                return true;
            }
        }

        return isFeedbackParticipantNameVisibleToUser(question, responseGiver, responseRecipient, user, isGiverName);
    }

    private boolean isFeedbackParticipantNameVisibleToUser(
            FeedbackQuestion question, ResponseGiver responseGiver, ResponseRecipient responseRecipient,
            User user, boolean isGiverName) {
        List<ViewerType> showNameTo = isGiverName
                ? question.getShowGiverNameTo()
                : question.getShowRecipientNameTo();
        for (ViewerType type : showNameTo) {
            switch (type) {
            case INSTRUCTORS:
                if (user instanceof Instructor) {
                    return true;
                }
                break;
            case OWN_TEAM_MEMBERS, OWN_TEAM_MEMBERS_INCLUDING_SELF:
                Team userTeam = user instanceof Student student ? student.getTeam() : null;
                Team receiverTeam = null;
                if (responseGiver.isGiverTeam()) {
                    receiverTeam = responseGiver.getGiverTeam();
                } else if (responseGiver.getGiverUser() instanceof Student student) {
                    receiverTeam = student.getTeam();
                }

                if (userTeam != null && userTeam.equals(receiverTeam)) {
                    return true;
                }
                break;
            case RECEIVER:
                // Response to team
                if (responseRecipient.isRecipientTeam()) {
                    if (user instanceof Student student && student.getTeam().equals(responseRecipient.getRecipientTeam())) {
                        return true;
                    }
                    break;
                    // Response to individual
                } else if (user.equals(responseRecipient.getRecipientUser())) {
                    return true;
                } else {
                    break;
                }
            case RECEIVER_TEAM_MEMBERS:
                userTeam = user instanceof Student student ? student.getTeam() : null;
                receiverTeam = null;
                if (responseRecipient.isRecipientTeam()) {
                    receiverTeam = responseRecipient.getRecipientTeam();
                } else if (responseRecipient.getRecipientUser() instanceof Student recipientStudent) {
                    receiverTeam = recipientStudent.getTeam();
                }

                if (userTeam != null && userTeam.equals(receiverTeam)) {
                    return true;
                }
                break;
            case STUDENTS:
                if (user instanceof Student) {
                    return true;
                }
                break;
            default:
                assert false : "Invalid ViewerType for showNameTo in "
                        + "FeedbackResponseLogic.isFeedbackParticipantNameVisibleToUser()";
                break;
            }
        }
        return false;
    }

    private boolean isResponseVisibleForUser(
            User user,
            Set<String> studentsEmailInTeam,
            ResponseGiver giver,
            ResponseRecipient recipient,
            FeedbackQuestion relatedQuestion
    ) {
        boolean isVisibleToRecipient = Objects.equals(user, recipient.getRecipientUser())
                && relatedQuestion.isResponseVisibleTo(ViewerType.RECEIVER);
        boolean isVisibleToGiver = Objects.equals(user, giver.getGiverUser());

        boolean isGiverSectionRestrictedForInstructor = false;
        boolean isRecipientSectionRestrictedForInstructor = false;
        boolean isVisibleToInstructor = false;
        if (user instanceof Instructor instructor) {
            isGiverSectionRestrictedForInstructor = !instructor.isAllowedForPrivilege(
                        Const.DEFAULT_SECTION,
                        relatedQuestion.getFeedbackSessionName(),
                        Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS
                );

            isRecipientSectionRestrictedForInstructor =
                    relatedQuestion.getRecipientType() != QuestionRecipientType.NONE
                    && !instructor.isAllowedForPrivilege(
                            Const.DEFAULT_SECTION,
                            relatedQuestion.getFeedbackSessionName(),
                            Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS
                    );

            isVisibleToInstructor =
                    relatedQuestion.isResponseVisibleTo(ViewerType.INSTRUCTORS)
                    && !isGiverSectionRestrictedForInstructor
                    && !isRecipientSectionRestrictedForInstructor;
        }

        boolean isVisibleToStudents = false;
        boolean isVisibleToTeamRecipient = false;
        boolean isVisibleToTeamGiver = false;
        boolean isVisibleToOwnTeamMembers = false;
        boolean isVisibleToReceiverTeamMembers = false;
        if (user instanceof Student student) {
            isVisibleToStudents = relatedQuestion.isResponseVisibleTo(ViewerType.STUDENTS);
            isVisibleToTeamRecipient = studentsEmailInTeam != null
                    && (relatedQuestion.getRecipientType() == QuestionRecipientType.TEAMS
                        || relatedQuestion.getRecipientType() == QuestionRecipientType.TEAMS_IN_SAME_SECTION
                        || relatedQuestion.getRecipientType() == QuestionRecipientType.TEAMS_EXCLUDING_SELF)
                    && relatedQuestion.isResponseVisibleTo(ViewerType.RECEIVER)
                    && Objects.equals(recipient.getRecipientTeam(), student.getTeam());
            isVisibleToTeamGiver = studentsEmailInTeam != null
                    && relatedQuestion.getGiverType() == QuestionGiverType.TEAMS
                    && Objects.equals(giver.getGiverTeam(), student.getTeam());
            isVisibleToOwnTeamMembers = studentsEmailInTeam != null
                    && relatedQuestion.isResponseVisibleTo(ViewerType.OWN_TEAM_MEMBERS)
                    && studentsEmailInTeam.contains(giver.getIdentifier());
            isVisibleToReceiverTeamMembers = studentsEmailInTeam != null
                    && relatedQuestion.isResponseVisibleTo(ViewerType.RECEIVER_TEAM_MEMBERS)
                    && studentsEmailInTeam.contains(recipient.getIdentifier());
        }

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
     * @return a list of responses
     */
    public List<FeedbackResponse> getFeedbackResponsesForSessionInSection(
            FeedbackSession feedbackSession, String courseId, @Nullable String sectionName) {
        List<FeedbackResponse> responses = frDb.getFeedbackResponsesForSession(feedbackSession, courseId);
        if (sectionName == null) {
            return responses;
        } else {
            return filterResponsesBySection(responses, sectionName);
        }
    }

    /**
     * Gets all responses given to/from a section for a question.
     *
     * @param feedbackQuestionId the question UUID
     * @param sectionName if null, will retrieve all responses for the question
     * @return a list of responses
     */
    public List<FeedbackResponse> getFeedbackResponsesForQuestionInSection(
            UUID feedbackQuestionId, @Nullable String sectionName) {
        List<FeedbackResponse> responses = frDb.getResponsesForQuestion(feedbackQuestionId);
        if (sectionName == null) {
            return responses;
        } else {
            return filterResponsesBySection(responses, sectionName);
        }
    }

    private List<FeedbackResponse> filterResponsesBySection(List<FeedbackResponse> responses, String sectionName) {
        List<FeedbackResponse> filteredResponses = new ArrayList<>();
        for (FeedbackResponse response : responses) {
            ResponseGiver giver = response.getGiver();
            ResponseRecipient recipient = response.getRecipient();
            boolean isGiverInSection;
            if (giver.isGiverTeam()) {
                isGiverInSection = giver.getGiverTeam().getSection().getName().equals(sectionName);
            } else if (giver.getGiverUser() instanceof Student giverStudent) {
                isGiverInSection = giverStudent.getSection().getName().equals(sectionName);
            } else {
                // instructor
                isGiverInSection = Objects.equals(sectionName, Const.DEFAULT_SECTION);
            }

            boolean isRecipientInSection;
            if (recipient.isRecipientTeam()) {
                isRecipientInSection = recipient.getRecipientTeam().getSection().getName().equals(sectionName);
            } else if (recipient.getRecipientUser() instanceof Student recipientStudent) {
                isRecipientInSection = recipientStudent.getSection().getName().equals(sectionName);
            } else {
                // instructor
                isRecipientInSection = Objects.equals(sectionName, Const.DEFAULT_SECTION);
            }

            if (isGiverInSection || isRecipientInSection) {
                filteredResponses.add(response);
            }
        }
        return filteredResponses;
    }

    /**
     * Returns feedback responses given/received by an instructor.
     */
    private List<FeedbackResponse> getFeedbackResponsesToOrFromInstructorForQuestion(
            FeedbackQuestion question, Instructor instructor) {
        Set<FeedbackResponse> viewableResponses = new HashSet<>();

        // Add responses that the instructor submitted him/herself
        if (question.getGiverType() == QuestionGiverType.INSTRUCTORS) {
            viewableResponses.addAll(
                    getFeedbackResponsesFromGiverForQuestion(question.getId(), instructor.getId())
            );
        }

        // Add responses that user is a receiver of when response is visible to receiver or instructors
        if (question.getRecipientType() == QuestionRecipientType.INSTRUCTORS
                && (question.isResponseVisibleTo(ViewerType.RECEIVER)
                || question.isResponseVisibleTo(ViewerType.INSTRUCTORS))) {
            viewableResponses.addAll(
                    getFeedbackResponsesForRecipientForQuestion(question.getId(), instructor.getId(), null)
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
        if (question.getGiverType() != QuestionGiverType.INSTRUCTORS) {
            viewableResponses.addAll(
                    getFeedbackResponsesFromGiverForQuestion(question.getId(), student.getId())
            );
        }

        // Add responses that user is a receiver of when response is visible to receiver
        if (question.getRecipientType() != QuestionRecipientType.INSTRUCTORS
                && question.isResponseVisibleTo(ViewerType.RECEIVER)) {
            viewableResponses.addAll(
                    getFeedbackResponsesForRecipientForQuestion(question.getId(), student.getId(), null)
            );
        }

        if (question.isResponseVisibleTo(ViewerType.STUDENTS)) {
            viewableResponses.addAll(getFeedbackResponsesForQuestion(question.getId()));

            // Early return as STUDENTS covers all cases below.
            return new ArrayList<>(viewableResponses);
        }

        if (question.getRecipientType().isTeam()
                && question.isResponseVisibleTo(ViewerType.RECEIVER)) {
            viewableResponses.addAll(
                    getFeedbackResponsesForRecipientForQuestion(question.getId(), null, student.getTeam().getId())
            );
        }

        if (question.getGiverType() == QuestionGiverType.TEAMS
                || question.isResponseVisibleTo(ViewerType.OWN_TEAM_MEMBERS)) {
            viewableResponses.addAll(
                    getFeedbackResponsesFromTeamForQuestion(
                            question.getId(), question.getCourseId(), student.getTeam(), courseRoster));
        }

        if (question.isResponseVisibleTo(ViewerType.RECEIVER_TEAM_MEMBERS)) {
            for (Student studentInTeam : courseRoster.getTeamToMembers().get(student.getTeamName())) {
                if (SanitizationHelper.areEmailsEqual(studentInTeam.getEmail(), student.getEmail())) {
                    continue;
                }
                List<FeedbackResponse> responses =
                        getFeedbackResponsesForRecipientForQuestion(question.getId(), studentInTeam.getId(), null);
                viewableResponses.addAll(responses);
            }
        }

        return new ArrayList<>(viewableResponses);
    }

    /**
     * Gets all responses received by a user for a question.
     */
    private List<FeedbackResponse> getFeedbackResponsesForRecipientForQuestion(
            UUID feedbackQuestionId, UUID recipientUserId, UUID recipientTeamId) {
        return frDb.getFeedbackResponsesForRecipientForQuestion(feedbackQuestionId, recipientUserId, recipientTeamId);
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
                feedbackQuestion.getShowResponsesTo().contains(ViewerType.INSTRUCTORS);
        boolean isGiverVisibleToInstructor =
                feedbackQuestion.getShowGiverNameTo().contains(ViewerType.INSTRUCTORS);
        boolean isRecipientVisibleToInstructor =
                feedbackQuestion.getShowRecipientNameTo().contains(ViewerType.INSTRUCTORS);
        return isResponseVisibleToInstructor && isGiverVisibleToInstructor && isRecipientVisibleToInstructor;
    }

    /**
     * Checks whether instructors can see the comment.
     */
    boolean checkCanInstructorsSeeComment(ResponseInstructorComment responseInstructorComment) {
        boolean isCommentVisibleToInstructor =
                responseInstructorComment.getShowCommentTo().contains(ViewerType.INSTRUCTORS);
        boolean isGiverVisibleToInstructor =
                responseInstructorComment.getShowGiverNameTo().contains(ViewerType.INSTRUCTORS);
        return isCommentVisibleToInstructor && isGiverVisibleToInstructor;
    }

}
