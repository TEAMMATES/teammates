package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.RequestTracer;
import teammates.storage.api.FeedbackResponsesDb;

/**
 * Handles operations related to feedback responses.
 *
 * @see FeedbackResponseAttributes
 * @see FeedbackResponsesDb
 */
public final class FeedbackResponsesLogic {

    private static final FeedbackResponsesLogic instance = new FeedbackResponsesLogic();

    private final FeedbackResponsesDb frDb = FeedbackResponsesDb.inst();

    private FeedbackQuestionsLogic fqLogic;
    private FeedbackResponseCommentsLogic frcLogic;
    private InstructorsLogic instructorsLogic;
    private StudentsLogic studentsLogic;

    private FeedbackResponsesLogic() {
        // prevent initialization
    }

    public static FeedbackResponsesLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        fqLogic = FeedbackQuestionsLogic.inst();
        frcLogic = FeedbackResponseCommentsLogic.inst();
        instructorsLogic = InstructorsLogic.inst();
        studentsLogic = StudentsLogic.inst();
    }

    /**
     * Gets a set of giver identifiers that has at least one response under a feedback session.
     */
    public Set<String> getGiverSetThatAnswerFeedbackSession(String courseId, String feedbackSessionName) {
        return frDb.getGiverSetThatAnswerFeedbackSession(courseId, feedbackSessionName);
    }

    /**
     * Creates a feedback response.
     *
     * @return created feedback response
     * @throws InvalidParametersException if the response is not valid
     * @throws EntityAlreadyExistsException if the response already exist
     */
    public FeedbackResponseAttributes createFeedbackResponse(FeedbackResponseAttributes fra)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return frDb.createEntity(fra);
    }

    /**
     * Gets a feedback response by its ID.
     */
    public FeedbackResponseAttributes getFeedbackResponse(
            String feedbackResponseId) {
        return frDb.getFeedbackResponse(feedbackResponseId);
    }

    /**
     * Gets a feedback response by its unique key.
     */
    public FeedbackResponseAttributes getFeedbackResponse(
            String feedbackQuestionId, String giverEmail, String recipient) {
        return frDb.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
    }

    /**
     * Gets all responses for a session.
     */
    List<FeedbackResponseAttributes> getFeedbackResponsesForSession(
            String feedbackSessionName, String courseId) {
        return frDb.getFeedbackResponsesForSession(feedbackSessionName, courseId);
    }

    /**
     * Gets all responses given to/from a section in a feedback session in a course.
     *
     * @param feedbackSessionName the name if the session
     * @param courseId the course ID of the session
     * @param section if null, will retrieve all responses in the session
     * @param fetchType if not null, will retrieve responses by giver, receiver sections, or both
     * @return a list of responses
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSection(
            String feedbackSessionName, String courseId, @Nullable String section,
            @Nullable FeedbackResultFetchType fetchType) {
        if (section == null) {
            return getFeedbackResponsesForSession(feedbackSessionName, courseId);
        }
        return frDb.getFeedbackResponsesForSessionInSection(feedbackSessionName, courseId, section, fetchType);
    }

    /**
     * Gets all responses for a question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(String feedbackQuestionId) {
        return frDb.getFeedbackResponsesForQuestion(feedbackQuestionId);
    }

    /**
     * Checks whether there are responses for a question.
     */
    public boolean areThereResponsesForQuestion(String feedbackQuestionId) {
        return frDb.areThereResponsesForQuestion(feedbackQuestionId);
    }

    /**
     * Gets all responses given to/from a section for a question.
     *
     * @param feedbackQuestionId the ID of the question
     * @param section if null, will retrieve all responses for the question
     * @return a list of responses
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionInSection(
            String feedbackQuestionId, @Nullable String section, FeedbackResultFetchType fetchType) {
        if (section == null) {
            return getFeedbackResponsesForQuestion(feedbackQuestionId);
        }
        return frDb.getFeedbackResponsesForQuestionInSection(feedbackQuestionId, section, fetchType);
    }

    /**
     * Gets all responses given by a user for a question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestion(
            String feedbackQuestionId, String userEmail) {
        return frDb.getFeedbackResponsesFromGiverForQuestion(feedbackQuestionId, userEmail);
    }

    /**
     * Gets all responses received by a user for a question.
     */
    private List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestion(
            String feedbackQuestionId, String userEmail) {
        return frDb.getFeedbackResponsesForReceiverForQuestion(feedbackQuestionId, userEmail);
    }

    /**
     * Checks whether a giver has responded a session.
     */
    public boolean hasGiverRespondedForSession(String giverIdentifier, String feedbackSessionName, String courseId) {

        return frDb.hasResponsesFromGiverInSession(giverIdentifier, feedbackSessionName, courseId);
    }

    /**
     * Gets all responses received by an user for a course.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForCourse(
            String courseId, String userEmail) {
        return frDb.getFeedbackResponsesForReceiverForCourse(courseId, userEmail);
    }

    /**
     * Gets all responses given by an user for a course.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForCourse(
            String courseId, String userEmail) {
        return frDb.getFeedbackResponsesFromGiverForCourse(courseId, userEmail);
    }

    /**
     * Get existing feedback responses from student or his team for the given
     * question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromStudentOrTeamForQuestion(
            FeedbackQuestionAttributes question, StudentAttributes student) {
        if (question.getGiverType() == FeedbackParticipantType.TEAMS) {
            return getFeedbackResponsesFromTeamForQuestion(
                    question.getId(), question.getCourseId(), student.getTeam(), null);
        }
        return frDb.getFeedbackResponsesFromGiverForQuestion(question.getId(), student.getEmail());
    }

    /**
     * Checks whether the giver name of a response is visible to an user.
     */
    public boolean isNameVisibleToUser(
            FeedbackQuestionAttributes question,
            FeedbackResponseAttributes response,
            String userEmail,
            boolean isInstructor, boolean isGiverName, CourseRoster roster) {

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
            FeedbackQuestionAttributes question, FeedbackResponseAttributes response,
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

    /**
     * Returns true if the responses of the question are visible to students.
     */
    public boolean isResponseOfFeedbackQuestionVisibleToStudent(FeedbackQuestionAttributes question) {
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
    public boolean isResponseOfFeedbackQuestionVisibleToInstructor(FeedbackQuestionAttributes question) {
        return question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS);
    }

    private List<FeedbackQuestionAttributes> getQuestionsForSession(
            String feedbackSessionName, String courseId, @Nullable String questionId) {
        if (questionId == null) {
            return fqLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        }
        FeedbackQuestionAttributes fqa = fqLogic.getFeedbackQuestion(questionId);
        return fqa == null ? Collections.emptyList() : Collections.singletonList(fqa);
    }

    private SessionResultsBundle buildResultsBundle(
            boolean isCourseWide, String feedbackSessionName, String courseId, String section, String questionId,
            boolean isInstructor, String userEmail, InstructorAttributes instructor, StudentAttributes student,
            CourseRoster roster, List<FeedbackQuestionAttributes> allQuestions,
            List<FeedbackResponseAttributes> allResponses, boolean isPreviewResults) {
        Map<String, FeedbackQuestionAttributes> allQuestionsMap = new HashMap<>();
        Set<String> questionsNotVisibleToInstructors = new HashSet<>();
        for (FeedbackQuestionAttributes qn : allQuestions) {
            allQuestionsMap.put(qn.getId(), qn);

            // set questions that should not be visible to instructors if results are being previewed
            if (isPreviewResults && !canInstructorsSeeQuestion(qn)) {
                questionsNotVisibleToInstructors.add(qn.getId());
            }
        }

        // load comment(s)
        List<FeedbackResponseCommentAttributes> allComments;
        if (questionId == null) {
            allComments = frcLogic.getFeedbackResponseCommentForSessionInSection(courseId, feedbackSessionName, section);
        } else {
            allComments = frcLogic.getFeedbackResponseCommentForQuestionInSection(questionId, section);
        }
        RequestTracer.checkRemainingTime();

        // related questions, responses, and comment
        Map<String, FeedbackQuestionAttributes> relatedQuestionsMap = new HashMap<>();
        Map<String, FeedbackQuestionAttributes> relatedQuestionsNotVisibleForPreviewMap = new HashMap<>();
        Set<String> relatedQuestionsWithCommentNotVisibleForPreview = new HashSet<>();
        Map<String, FeedbackResponseAttributes> relatedResponsesMap = new HashMap<>();
        Map<String, List<FeedbackResponseCommentAttributes>> relatedCommentsMap = new HashMap<>();
        if (isCourseWide) {
            // all questions are related questions when viewing course-wide result
            for (FeedbackQuestionAttributes qn : allQuestions) {
                relatedQuestionsMap.put(qn.getId(), qn);
            }
        }

        Set<String> studentsEmailInTeam = new HashSet<>();
        if (student != null) {
            for (StudentAttributes studentInTeam
                    : roster.getTeamToMembersTable().getOrDefault(student.getTeam(), Collections.emptyList())) {
                studentsEmailInTeam.add(studentInTeam.getEmail());
            }
        }

        // visibility table for each response and comment
        Map<String, Boolean> responseGiverVisibilityTable = new HashMap<>();
        Map<String, Boolean> responseRecipientVisibilityTable = new HashMap<>();
        Map<Long, Boolean> commentVisibilityTable = new HashMap<>();

        // build response
        for (FeedbackResponseAttributes response : allResponses) {
            if (isPreviewResults
                    && relatedQuestionsNotVisibleForPreviewMap.get(response.getFeedbackQuestionId()) != null) {
                // corresponding question's responses will not be shown to previewer, ignore the response
                continue;
            }

            FeedbackQuestionAttributes correspondingQuestion = allQuestionsMap.get(response.getFeedbackQuestionId());
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
            if (isPreviewResults && questionsNotVisibleToInstructors.contains(response.getFeedbackQuestionId())) {
                relatedQuestionsNotVisibleForPreviewMap.put(response.getFeedbackQuestionId(), correspondingQuestion);
                continue;
            }

            // if there are viewable responses, the corresponding question becomes related
            relatedQuestionsMap.put(response.getFeedbackQuestionId(), correspondingQuestion);
            relatedResponsesMap.put(response.getId(), response);
            // generate giver/recipient name visibility table
            responseGiverVisibilityTable.put(response.getId(),
                    isNameVisibleToUser(correspondingQuestion, response, userEmail, isInstructor, true, roster));
            responseRecipientVisibilityTable.put(response.getId(),
                    isNameVisibleToUser(correspondingQuestion, response, userEmail, isInstructor, false, roster));
        }
        RequestTracer.checkRemainingTime();

        // build comment
        for (FeedbackResponseCommentAttributes frc : allComments) {
            FeedbackResponseAttributes relatedResponse = relatedResponsesMap.get(frc.getFeedbackResponseId());
            FeedbackQuestionAttributes relatedQuestion = relatedQuestionsMap.get(frc.getFeedbackQuestionId());
            // the comment needs to be relevant to the question and response
            if (relatedQuestion == null || relatedResponse == null) {
                continue;
            }
            // check visibility of comment
            boolean isVisibleResponseComment = frcLogic.isResponseCommentVisibleForUser(
                    userEmail, isInstructor, student, studentsEmailInTeam, relatedResponse, relatedQuestion, frc);
            if (!isVisibleResponseComment) {
                continue;
            }

            // if previewing results and the comment should not be visible to instructors,
            // note down the corresponding question and do not add the comment
            if (isPreviewResults && !canInstructorsSeeComment(frc)) {
                relatedQuestionsWithCommentNotVisibleForPreview.add(frc.getFeedbackQuestionId());
                continue;
            }

            relatedCommentsMap.computeIfAbsent(relatedResponse.getId(), key -> new ArrayList<>()).add(frc);
            // generate comment giver name visibility table
            commentVisibilityTable.put(frc.getId(), frcLogic.isNameVisibleToUser(frc, relatedResponse, userEmail, roster));
        }
        RequestTracer.checkRemainingTime();

        List<FeedbackResponseAttributes> existingResponses = new ArrayList<>(relatedResponsesMap.values());
        List<FeedbackResponseAttributes> missingResponses = Collections.emptyList();
        if (isCourseWide) {
            missingResponses = buildMissingResponses(
                    courseId, feedbackSessionName, instructor, responseGiverVisibilityTable,
                    responseRecipientVisibilityTable, relatedQuestionsMap, existingResponses, roster, section);
        }
        RequestTracer.checkRemainingTime();

        return new SessionResultsBundle(relatedQuestionsMap, relatedQuestionsNotVisibleForPreviewMap,
                relatedQuestionsWithCommentNotVisibleForPreview,
                existingResponses, missingResponses, responseGiverVisibilityTable, responseRecipientVisibilityTable,
                relatedCommentsMap, commentVisibilityTable, roster);
    }

    /**
     * Gets the session result for a feedback session.
     *
     * @param feedbackSessionName the feedback session name
     * @param courseId the ID of the course
     * @param instructorEmail the instructor viewing the feedback session
     * @param questionId if not null, will only return partial bundle for the question
     * @param section if not null, will only return partial bundle for the section
     * @param fetchType if not null, will fetch responses by giver, receiver sections, or both
     * @return the session result bundle
     */
    public SessionResultsBundle getSessionResultsForCourse(
            String feedbackSessionName, String courseId, String instructorEmail,
            @Nullable String questionId, @Nullable String section, @Nullable FeedbackResultFetchType fetchType) {
        CourseRoster roster = new CourseRoster(
                studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));

        // load question(s)
        List<FeedbackQuestionAttributes> allQuestions = getQuestionsForSession(feedbackSessionName, courseId, questionId);
        RequestTracer.checkRemainingTime();

        // load response(s)
        List<FeedbackResponseAttributes> allResponses;
        // load all response for instructors and passively filter them later
        if (questionId == null) {
            allResponses = getFeedbackResponsesForSessionInSection(feedbackSessionName, courseId, section, fetchType);
        } else {
            allResponses = getFeedbackResponsesForQuestionInSection(questionId, section, fetchType);
        }
        RequestTracer.checkRemainingTime();

        // consider the current viewing user
        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, instructorEmail);

        return buildResultsBundle(true, feedbackSessionName, courseId, section, questionId, true, instructorEmail,
                instructor, null, roster, allQuestions, allResponses, false);
    }

    /**
     * Gets the session result for a feedback session for the given user.
     *
     * @param feedbackSessionName the feedback session name
     * @param courseId the ID of the course
     * @param userEmail the user viewing the feedback session
     * @param isInstructor true if the user is an instructor
     * @param questionId if not null, will only return partial bundle for the question
     * @param isPreviewResults true if getting session results for preview purpose
     * @return the session result bundle
     */
    public SessionResultsBundle getSessionResultsForUser(
            String feedbackSessionName, String courseId, String userEmail, boolean isInstructor,
            @Nullable String questionId, boolean isPreviewResults) {
        CourseRoster roster = new CourseRoster(
                studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));

        // load question(s)
        List<FeedbackQuestionAttributes> allQuestions = getQuestionsForSession(feedbackSessionName, courseId, questionId);
        RequestTracer.checkRemainingTime();

        // load response(s)
        StudentAttributes student = isInstructor ? null : studentsLogic.getStudentForEmail(courseId, userEmail);
        InstructorAttributes instructor = isInstructor ? instructorsLogic.getInstructorForEmail(courseId, userEmail) : null;
        List<FeedbackResponseAttributes> allResponses = new ArrayList<>();
        for (FeedbackQuestionAttributes question : allQuestions) {
            // load viewable responses for students/instructors proactively
            // this is cost-effective as in most of time responses for the whole session will not be viewable to individuals
            List<FeedbackResponseAttributes> viewableResponses = isInstructor
                    ? getFeedbackResponsesToOrFromInstructorForQuestion(question, instructor)
                    : getViewableFeedbackResponsesForStudentForQuestion(question, student, roster);
            allResponses.addAll(viewableResponses);
        }
        RequestTracer.checkRemainingTime();

        return buildResultsBundle(false, feedbackSessionName, courseId, null, questionId, isInstructor, userEmail,
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
     * @param relatedQuestionsMap the relevant questions
     * @param existingResponses existing responses
     * @param courseRoster the course roster
     * @param section if not null, will only build missing responses for the section
     * @return a list of missing responses for the session.
     */
    private List<FeedbackResponseAttributes> buildMissingResponses(
            String courseId, String feedbackSessionName, InstructorAttributes instructor,
            Map<String, Boolean> responseGiverVisibilityTable, Map<String, Boolean> responseRecipientVisibilityTable,
            Map<String, FeedbackQuestionAttributes> relatedQuestionsMap,
            List<FeedbackResponseAttributes> existingResponses, CourseRoster courseRoster, @Nullable String section) {

        // first get all possible giver recipient pairs
        Map<String, Map<String, Set<String>>> questionCompleteGiverRecipientMap = new HashMap<>();
        for (FeedbackQuestionAttributes feedbackQuestion : relatedQuestionsMap.values()) {
            if (feedbackQuestion.getQuestionDetailsCopy().shouldGenerateMissingResponses(feedbackQuestion)) {
                questionCompleteGiverRecipientMap.put(feedbackQuestion.getId(),
                        fqLogic.buildCompleteGiverRecipientMap(feedbackQuestion, courseRoster));
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
                                    .withCourseId(courseId)
                                    .withFeedbackSessionName(feedbackSessionName)
                                    .withGiverSection(giverInfo.getSectionName())
                                    .withRecipientSection(recipientInfo.getSectionName())
                                    .withResponseDetails(new FeedbackTextResponseDetails("No Response"))
                                    .build();

                    // check visibility of the missing response
                    boolean isVisibleResponse = isResponseVisibleForUser(
                            instructor.getEmail(), true, null, Collections.emptySet(),
                            missingResponse, correspondingQuestion, instructor);
                    if (!isVisibleResponse) {
                        continue;
                    }

                    // generate giver/recipient name visibility table
                    responseGiverVisibilityTable.put(missingResponse.getId(),
                            isNameVisibleToUser(correspondingQuestion, missingResponse,
                                    instructor.getEmail(), true, true, courseRoster));
                    responseRecipientVisibilityTable.put(missingResponse.getId(),
                            isNameVisibleToUser(correspondingQuestion, missingResponse,
                                    instructor.getEmail(), true, false, courseRoster));
                    missingResponses.add(missingResponse);
                }
            }
        }

        return missingResponses;
    }

    boolean isResponseVisibleForUser(
            String userEmail, boolean isInstructor, StudentAttributes student,
            Set<String> studentsEmailInTeam, FeedbackResponseAttributes response,
            FeedbackQuestionAttributes relatedQuestion, InstructorAttributes instructor) {

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
                    && response.getRecipient().equals(student.getTeam())) {
                isVisibleResponse = true;
            } else if (relatedQuestion.getGiverType() == FeedbackParticipantType.TEAMS
                    && response.getGiver().equals(student.getTeam())) {
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
                    !instructor.isAllowedForPrivilege(response.getGiverSection(),
                            response.getFeedbackSessionName(),
                            Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
            // If instructors are not restricted to view the giver's section,
            // they are allowed to view responses to GENERAL, subject to visibility options
            boolean isRecipientSectionRestricted =
                    relatedQuestion.getRecipientType() != FeedbackParticipantType.NONE
                            && !instructor.isAllowedForPrivilege(response.getRecipientSection(),
                            response.getFeedbackSessionName(),
                            Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);

            boolean isNotAllowedForInstructor = isGiverSectionRestricted || isRecipientSectionRestricted;
            if (isNotAllowedForInstructor) {
                isVisibleResponse = false;
            }
        }
        return isVisibleResponse;
    }

    /**
     * Checks whether there are responses for a course.
     */
    public boolean hasResponsesForCourse(String courseId) {
        return frDb.hasFeedbackResponseEntitiesForCourse(courseId);
    }

    /**
     * Updates a feedback response by {@link FeedbackResponseAttributes.UpdateOptions}.
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
     * @throws EntityAlreadyExistsException if the response cannot be updated
     *         by recreation because of an existent response
     */
    public FeedbackResponseAttributes updateFeedbackResponseCascade(FeedbackResponseAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        FeedbackResponseAttributes oldResponse = frDb.getFeedbackResponse(updateOptions.getFeedbackResponseId());
        FeedbackResponseAttributes newResponse = frDb.updateFeedbackResponse(updateOptions);

        boolean isResponseIdChanged = !oldResponse.getId().equals(newResponse.getId());
        boolean isGiverSectionChanged = !oldResponse.getGiverSection().equals(newResponse.getGiverSection());
        boolean isRecipientSectionChanged = !oldResponse.getRecipientSection().equals(newResponse.getRecipientSection());

        if (isResponseIdChanged || isGiverSectionChanged || isRecipientSectionChanged) {
            List<FeedbackResponseCommentAttributes> responseComments =
                    frcLogic.getFeedbackResponseCommentForResponse(oldResponse.getId());
            for (FeedbackResponseCommentAttributes responseComment : responseComments) {
                FeedbackResponseCommentAttributes.UpdateOptions.Builder updateOptionsBuilder =
                        FeedbackResponseCommentAttributes.updateOptionsBuilder(responseComment.getId());

                if (isResponseIdChanged) {
                    updateOptionsBuilder.withFeedbackResponseId(newResponse.getId());
                }

                if (isGiverSectionChanged) {
                    updateOptionsBuilder.withGiverSection(newResponse.getGiverSection());
                }

                if (isRecipientSectionChanged) {
                    updateOptionsBuilder.withReceiverSection(newResponse.getRecipientSection());
                }

                frcLogic.updateFeedbackResponseComment(updateOptionsBuilder.build());
            }
        }

        return newResponse;
    }

    /**
     * Updates responses for a student when his team changes.
     *
     * <p>This is done by deleting responses that are no longer relevant to him in his new team.
     */
    public void updateFeedbackResponsesForChangingTeam(
            String courseId, String userEmail, String oldTeam, String newTeam) {
        FeedbackQuestionAttributes question;
        // deletes all responses given by the user to team members or given by the user as a representative of a team.
        List<FeedbackResponseAttributes> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(courseId, userEmail);
        for (FeedbackResponseAttributes response : responsesFromUser) {
            question = fqLogic.getFeedbackQuestion(response.getFeedbackQuestionId());
            if (question.getGiverType() == FeedbackParticipantType.TEAMS
                    || isRecipientTypeTeamMembers(question)) {
                deleteFeedbackResponseCascade(response.getId());
            }
        }

        // Deletes all responses given by other team members to the user.
        List<FeedbackResponseAttributes> responsesToUser =
                getFeedbackResponsesForReceiverForCourse(courseId, userEmail);
        for (FeedbackResponseAttributes response : responsesToUser) {
            question = fqLogic.getFeedbackQuestion(response.getFeedbackQuestionId());
            if (isRecipientTypeTeamMembers(question)) {
                deleteFeedbackResponseCascade(response.getId());
            }
        }

        boolean isOldTeamEmpty = studentsLogic.getStudentsForTeam(oldTeam, courseId).isEmpty();
        if (isOldTeamEmpty) {
            deleteFeedbackResponsesInvolvedEntityOfCourseCascade(courseId, oldTeam);
        }
    }

    /**
     * Updates responses for a student when his section changes.
     */
    public void updateFeedbackResponsesForChangingSection(
            String courseId, String userEmail, String oldSection, String newSection)
            throws EntityDoesNotExistException, InvalidParametersException {
        updateSectionOfResponsesFromUser(courseId, userEmail, newSection);
        updateSectionOfResponsesToUser(courseId, userEmail, newSection);
    }

    /**
     * Updates the relevant responses before the deletion of a student.
     * This method takes care of the following:
     * <ul>
     *     <li>
     *         Making existing responses of 'rank recipient question' consistent.
     *     </li>
     * </ul>
     */
    public void updateFeedbackResponsesForDeletingStudent(String courseId) {
        updateRankRecipientQuestionResponsesAfterDeletingStudent(courseId);
    }

    private void updateRankRecipientQuestionResponsesAfterDeletingStudent(String courseId) {
        List<FeedbackQuestionAttributes> filteredQuestions =
                fqLogic.getFeedbackQuestionForCourseWithType(courseId, FeedbackQuestionType.RANK_RECIPIENTS);
        CourseRoster roster = new CourseRoster(
                studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));
        for (FeedbackQuestionAttributes question : filteredQuestions) {
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
            FeedbackQuestionAttributes question, CourseRoster roster) {
        if (!question.getQuestionType().equals(FeedbackQuestionType.RANK_RECIPIENTS)) {
            return;
        }

        FeedbackParticipantType giverType = question.getGiverType();
        List<FeedbackResponseAttributes> responses;

        int numberOfRecipients;
        List<FeedbackResponseAttributes.UpdateOptions> updates = new ArrayList<>();

        switch (giverType) {
        case INSTRUCTORS:
        case SELF:
            for (InstructorAttributes instructor : roster.getInstructors()) {
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, instructor, null, roster).size();
                responses = getFeedbackResponsesFromGiverForQuestion(question.getId(), instructor.getEmail());
                updates.addAll(FeedbackRankRecipientsResponseDetails
                        .getUpdateOptionsForRankRecipientQuestions(responses, numberOfRecipients));
            }
            break;
        case TEAMS:
        case TEAMS_IN_SAME_SECTION:
            StudentAttributes firstMemberOfTeam;
            String team;
            Map<String, List<StudentAttributes>> teams = roster.getTeamToMembersTable();
            for (Map.Entry<String, List<StudentAttributes>> entry : teams.entrySet()) {
                team = entry.getKey();
                firstMemberOfTeam = entry.getValue().get(0);
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, null, firstMemberOfTeam, roster).size();
                responses =
                        getFeedbackResponsesFromTeamForQuestion(question.getId(), question.getCourseId(), team, roster);
                updates.addAll(FeedbackRankRecipientsResponseDetails
                        .getUpdateOptionsForRankRecipientQuestions(responses, numberOfRecipients));
            }
            break;
        default:
            for (StudentAttributes student : roster.getStudents()) {
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, null, student, roster).size();
                responses = getFeedbackResponsesFromGiverForQuestion(question.getId(), student.getEmail());
                updates.addAll(FeedbackRankRecipientsResponseDetails
                        .getUpdateOptionsForRankRecipientQuestions(responses, numberOfRecipients));
            }
            break;
        }

        for (FeedbackResponseAttributes.UpdateOptions update : updates) {
            try {
                frDb.updateFeedbackResponse(update);
            } catch (EntityAlreadyExistsException | EntityDoesNotExistException | InvalidParametersException e) {
                assert false : "Exception occurred when updating responses after deleting students.";
            }
        }
    }

    private void updateSectionOfResponsesToUser(String courseId, String userEmail, String newSection)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseAttributes> responsesToUser =
                getFeedbackResponsesForReceiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesToUser) {
            try {
                frDb.updateFeedbackResponse(
                        FeedbackResponseAttributes.updateOptionsBuilder(response.getId())
                                .withRecipientSection(newSection)
                                .build());
            } catch (EntityAlreadyExistsException e) {
                assert false : "Not possible to trigger recreating of response";
            }
            frcLogic.updateFeedbackResponseCommentsForResponse(response.getId());
        }
    }

    private void updateSectionOfResponsesFromUser(String courseId, String userEmail, String newSection)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseAttributes> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesFromUser) {
            try {
                frDb.updateFeedbackResponse(
                        FeedbackResponseAttributes.updateOptionsBuilder(response.getId())
                                .withGiverSection(newSection)
                                .build());
            } catch (EntityAlreadyExistsException e) {
                assert false : "Not possible to trigger recreating of response";
            }
            frcLogic.updateFeedbackResponseCommentsForResponse(response.getId());
        }
    }

    private boolean isRecipientTypeTeamMembers(FeedbackQuestionAttributes question) {
        return question.getRecipientType() == FeedbackParticipantType.OWN_TEAM_MEMBERS
               || question.getRecipientType() == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;
    }

    /**
     * Updates responses for a student when his email changes.
     */
    public void updateFeedbackResponsesForChangingEmail(
            String courseId, String oldEmail, String newEmail)
            throws InvalidParametersException, EntityDoesNotExistException {

        List<FeedbackResponseAttributes> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(courseId, oldEmail);

        for (FeedbackResponseAttributes response : responsesFromUser) {
            try {
                updateFeedbackResponseCascade(
                        FeedbackResponseAttributes.updateOptionsBuilder(response.getId())
                                .withGiver(newEmail)
                                .build());
                frcLogic.updateFeedbackResponseCommentsEmails(courseId, oldEmail, newEmail);
            } catch (EntityAlreadyExistsException e) {
                assert false : "Feedback response failed to update successfully as email was already in use.";
            }
        }

        List<FeedbackResponseAttributes> responsesToUser =
                getFeedbackResponsesForReceiverForCourse(courseId, oldEmail);

        for (FeedbackResponseAttributes response : responsesToUser) {
            try {
                updateFeedbackResponseCascade(
                        FeedbackResponseAttributes.updateOptionsBuilder(response.getId())
                                .withRecipient(newEmail)
                                .build());
            } catch (EntityAlreadyExistsException e) {
                assert false : "Feedback response failed to update successfully as email was already in use.";
            }
        }
    }

    /**
     * Deletes responses using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackResponses(AttributesDeletionQuery query) {
        frDb.deleteFeedbackResponses(query);
    }

    /**
     * Deletes a feedback response cascade its associated comments.
     */
    public void deleteFeedbackResponseCascade(String responseId) {
        frcLogic.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withResponseId(responseId)
                        .build());
        frDb.deleteFeedbackResponse(responseId);
    }

    /**
     * Deletes all feedback responses of a question cascade its associated comments.
     */
    public void deleteFeedbackResponsesForQuestionCascade(String feedbackQuestionId) {
        // delete all responses, comments of the question
        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withQuestionId(feedbackQuestionId)
                .build();
        deleteFeedbackResponses(query);
        frcLogic.deleteFeedbackResponseComments(query);
    }

    /**
     * Deletes all feedback responses involved an entity cascade its associated comments.
     *
     * @param courseId the course id
     * @param entityEmail the entity email
     */
    public void deleteFeedbackResponsesInvolvedEntityOfCourseCascade(String courseId, String entityEmail) {
        // delete responses from the entity
        List<FeedbackResponseAttributes> responsesFromStudent =
                getFeedbackResponsesFromGiverForCourse(courseId, entityEmail);
        for (FeedbackResponseAttributes response : responsesFromStudent) {
            deleteFeedbackResponseCascade(response.getId());
        }

        // delete responses to the entity
        List<FeedbackResponseAttributes> responsesToStudent =
                getFeedbackResponsesForReceiverForCourse(courseId, entityEmail);
        for (FeedbackResponseAttributes response : responsesToStudent) {
            deleteFeedbackResponseCascade(response.getId());
        }
    }

    private List<FeedbackResponseAttributes> getFeedbackResponsesFromTeamForQuestion(
            String feedbackQuestionId, String courseId, String teamName, @Nullable CourseRoster courseRoster) {

        List<FeedbackResponseAttributes> responses = new ArrayList<>();
        List<StudentAttributes> studentsInTeam = courseRoster == null
                ? studentsLogic.getStudentsForTeam(teamName, courseId) : courseRoster.getTeamToMembersTable().get(teamName);

        for (StudentAttributes student : studentsInTeam) {
            responses.addAll(frDb.getFeedbackResponsesFromGiverForQuestion(
                    feedbackQuestionId, student.getEmail()));
        }

        responses.addAll(frDb.getFeedbackResponsesFromGiverForQuestion(
                                        feedbackQuestionId, teamName));

        return responses;
    }

    /**
     * Returns feedback responses given/received by an instructor.
     */
    private List<FeedbackResponseAttributes> getFeedbackResponsesToOrFromInstructorForQuestion(
            FeedbackQuestionAttributes question, InstructorAttributes instructor) {
        UniqueResponsesSet viewableResponses = new UniqueResponsesSet();

        // Add responses that the instructor submitted him/herself
        if (question.getGiverType() == FeedbackParticipantType.INSTRUCTORS) {
            viewableResponses.addNewResponses(
                    getFeedbackResponsesFromGiverForQuestion(question.getFeedbackQuestionId(), instructor.getEmail())
            );
        }

        // Add responses that user is a receiver of when response is visible to receiver or instructors
        if (question.getRecipientType() == FeedbackParticipantType.INSTRUCTORS
                && (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                || question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS))) {
            viewableResponses.addNewResponses(
                    getFeedbackResponsesForReceiverForQuestion(question.getFeedbackQuestionId(), instructor.getEmail())
            );
        }

        return viewableResponses.getResponses();
    }

    /**
     * Returns viewable feedback responses for a student.
     */
    private List<FeedbackResponseAttributes> getViewableFeedbackResponsesForStudentForQuestion(
            FeedbackQuestionAttributes question, StudentAttributes student, CourseRoster courseRoster) {
        UniqueResponsesSet viewableResponses = new UniqueResponsesSet();

        // Add responses that the student submitted him/herself
        if (question.getGiverType() != FeedbackParticipantType.INSTRUCTORS) {
            viewableResponses.addNewResponses(
                    getFeedbackResponsesFromGiverForQuestion(question.getFeedbackQuestionId(), student.getEmail())
            );
        }

        // Add responses that user is a receiver of when response is visible to receiver
        if (question.getRecipientType() != FeedbackParticipantType.INSTRUCTORS
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            viewableResponses.addNewResponses(
                    getFeedbackResponsesForReceiverForQuestion(question.getFeedbackQuestionId(), student.getEmail())
            );
        }

        if (question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
            viewableResponses.addNewResponses(getFeedbackResponsesForQuestion(question.getId()));

            // Early return as STUDENTS covers all cases below.
            return viewableResponses.getResponses();
        }

        if (question.getRecipientType().isTeam()
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            viewableResponses.addNewResponses(
                    getFeedbackResponsesForReceiverForQuestion(question.getId(), student.getTeam())
            );
        }

        if (question.getGiverType() == FeedbackParticipantType.TEAMS
                || question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            viewableResponses.addNewResponses(
                    getFeedbackResponsesFromTeamForQuestion(
                            question.getId(), question.getCourseId(), student.getTeam(), courseRoster));
        }

        if (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {
            for (StudentAttributes studentInTeam : courseRoster.getTeamToMembersTable().get(student.getTeam())) {
                if (studentInTeam.getEmail().equals(student.getEmail())) {
                    continue;
                }
                List<FeedbackResponseAttributes> responses =
                        frDb.getFeedbackResponsesForReceiverForQuestion(question.getId(), studentInTeam.getEmail());
                viewableResponses.addNewResponses(responses);
            }
        }

        return viewableResponses.getResponses();
    }

    /**
     * Gets the number of feedback responses created within a specified time range.
     */
    int getNumFeedbackResponsesByTimeRange(Instant startTime, Instant endTime) {
        return frDb.getNumFeedbackResponsesByTimeRange(startTime, endTime);
    }

    /**
     * Checks whether instructors can see the question.
     */
    boolean canInstructorsSeeQuestion(FeedbackQuestionAttributes feedbackQuestion) {
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
    boolean canInstructorsSeeComment(FeedbackResponseCommentAttributes feedbackResponseComment) {
        boolean isCommentVisibleToInstructor =
                feedbackResponseComment.getShowCommentTo().contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isGiverVisibleToInstructor =
                feedbackResponseComment.getShowGiverNameTo().contains(FeedbackParticipantType.INSTRUCTORS);
        return isCommentVisibleToInstructor && isGiverVisibleToInstructor;
    }

    /**
     * Set contains only unique response.
     */
    private static class UniqueResponsesSet {

        private final Set<String> responseIds;
        private final List<FeedbackResponseAttributes> responses;

        private UniqueResponsesSet() {
            responseIds = new HashSet<>();
            responses = new ArrayList<>();
        }

        private void addNewResponses(Collection<FeedbackResponseAttributes> newResponses) {
            newResponses.forEach(this::addNewResponse);
        }

        private void addNewResponse(FeedbackResponseAttributes newResponse) {
            if (responseIds.contains(newResponse.getId())) {
                return;
            }
            responseIds.add(newResponse.getId());
            responses.add(newResponse);
        }

        private List<FeedbackResponseAttributes> getResponses() {
            return responses;
        }
    }
}
