package teammates.sqllogic.core;

import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SqlCourseRoster;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.FeedbackResponsesDb;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.responses.FeedbackRankRecipientsResponse;

/**
 * Handles operations related to feedback sessions.
 *
 * @see FeedbackResponse
 * @see FeedbackResponsesDb
 */
public final class FeedbackResponsesLogic {

    private static final FeedbackResponsesLogic instance = new FeedbackResponsesLogic();

    private FeedbackResponsesDb frDb;
    private UsersLogic usersLogic;
    private FeedbackQuestionsLogic fqLogic;

    private FeedbackResponsesLogic() {
        // prevent initialization
    }

    public static FeedbackResponsesLogic inst() {
        return instance;
    }

    /**
     * Initialize dependencies for {@code FeedbackResponsesLogic}.
     */
    void initLogicDependencies(FeedbackResponsesDb frDb, UsersLogic usersLogic, FeedbackQuestionsLogic fqLogic) {
        this.frDb = frDb;
        this.usersLogic = usersLogic;
        this.fqLogic = fqLogic;
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
     */
    public void deleteFeedbackResponsesInvolvedEntityOfCourseCascade(String courseId, String entityEmail) {
        // delete responses from the entity
        List<FeedbackResponse> responsesFromStudent =
                getFeedbackResponsesFromGiverForCourse(courseId, entityEmail);
        for (FeedbackResponse response : responsesFromStudent) {
            deleteFeedbackResponseCascade(response.getId());
        }

        // delete responses to the entity
        List<FeedbackResponse> responsesToStudent =
                getFeedbackResponsesForRecipientForCourse(courseId, entityEmail);
        for (FeedbackResponse response : responsesToStudent) {
            deleteFeedbackResponseCascade(response.getId());
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
     * Deletes a feedback response, cascade its associated comments.
     */
    public void deleteFeedbackResponseCascade(UUID responseId) {
        FeedbackResponse feedbackResponseToDelete = frDb.getFeedbackResponse(responseId);

        frDb.deleteFeedbackResponse(feedbackResponseToDelete);
    }

    /**
     * Updates the relevant responses before the deletion of a student.
     * This method takes care of the following:
     * Making existing responses of 'rank recipient question' consistent.
     */
    public void updateFeedbackResponsesForDeletingStudent(String courseId) {
        updateRankRecipientQuestionResponsesAfterDeletingStudent(courseId);
    }

    private void updateRankRecipientQuestionResponsesAfterDeletingStudent(String courseId) {
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
        if (!question.getQuestionDetailsCopy().getQuestionType().equals(FeedbackQuestionType.RANK_RECIPIENTS)) {
            return;
        }

        FeedbackParticipantType giverType = question.getGiverType();
        List<FeedbackResponse> responses;

        int numberOfRecipients;
        List<FeedbackResponse> updates = new ArrayList<>();

        switch (giverType) {
        case INSTRUCTORS:
        case SELF:
            for (Instructor instructor : roster.getInstructors()) {
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, instructor, null, roster).size();
                responses = getFeedbackResponsesFromGiverForQuestion(question.getId(), instructor.getEmail());
                updates.addAll(FeedbackRankRecipientsResponseDetails
                        .getUpdatedResponsesForRankRecipientQuestionsMigrated(responses, numberOfRecipients));
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
                updates.addAll(FeedbackRankRecipientsResponseDetails
                        .getUpdatedResponsesForRankRecipientQuestionsMigrated(responses, numberOfRecipients));
            }
            break;
        default:
            for (Student student : roster.getStudents()) {
                numberOfRecipients =
                        fqLogic.getRecipientsOfQuestion(question, null, student, roster).size();
                responses = getFeedbackResponsesFromGiverForQuestion(question.getId(), student.getEmail());
                updates.addAll(FeedbackRankRecipientsResponseDetails
                        .getUpdatedResponsesForRankRecipientQuestionsMigrated(responses, numberOfRecipients));
            }
            break;
        }

        for (FeedbackResponse update : updates) {
            try {
                updateFeedbackResponse(update);
            } catch (EntityAlreadyExistsException | EntityDoesNotExistException | InvalidParametersException e) {
                assert false : "Exception occurred when updating responses after deleting students.";
            }
        }
    }

    /**
     * Updates a feedback response.
     *
     * <p>If the giver/recipient field is changed, the response is updated by recreating the response
     * as question-giver-recipient is the primary key.
     */
    void updateFeedbackResponse(FeedbackResponse feedbackResponse)
            throws EntityAlreadyExistsException, EntityDoesNotExistException, InvalidParametersException {
        FeedbackResponse oldResponse = frDb.getFeedbackResponse(feedbackResponse.getId());
        if (oldResponse == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        if (feedbackResponse.getRecipient().equals(oldResponse.getRecipient())
                && feedbackResponse.getGiver().equals(oldResponse.getGiver())) {
            oldResponse.setGiverSection(feedbackResponse.getGiverSection());
            oldResponse.setRecipientSection(feedbackResponse.getRecipientSection());
            ((FeedbackRankRecipientsResponse) oldResponse)
                    .setAnswer(((FeedbackRankRecipientsResponse) feedbackResponse).getAnswer());
        } else {
            frDb.deleteFeedbackResponse(oldResponse);
        }
    }

}
