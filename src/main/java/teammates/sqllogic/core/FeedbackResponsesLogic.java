package teammates.sqllogic.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SqlCourseRoster;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.FeedbackResponsesDb;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

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
    void initLogicDependencies(FeedbackResponsesDb frDb, UsersLogic usersLogic,
            FeedbackResponseCommentsLogic frcLogic) {
        this.frDb = frDb;
        this.usersLogic = usersLogic;
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
     * Gets a feedback response by its ID.
     */
    public FeedbackResponse getFeedbackResponse(UUID feedbackResponseId) {
        return frDb.getFeedbackResponse(feedbackResponseId);
    }

    /**
     * Gets a feedback response by feedbackQuestionId, giver and recipient.
     */
    public FeedbackResponse getFeedbackResponse(
            UUID feedbackQuestionId, String giver, String recipient) {
        return frDb.getFeedbackResponse(feedbackQuestionId, giver, recipient);
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
                    question.getId(), question.getCourseId(), student.getTeam().getName(), null);
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
     * Updates a feedback response.
     *
     * <p>Cascade updates its associated feedback response comment
     * (e.g. associated response ID, giverSection and recipientSection).
     *
     * <p>If the giver/recipient field is changed, the response is updated by recreating the response
     * as question-giver-recipient is the primary key.
     */
    public FeedbackResponse updateFeedbackResponseCascade(FeedbackResponse feedbackResponse)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        FeedbackResponse oldResponse = frDb.getFeedbackResponse(feedbackResponse.getId());
        FeedbackResponse newResponse = frDb.updateFeedbackResponse(feedbackResponse);

        boolean isResponseIdChanged = !oldResponse.getId().equals(newResponse.getId());
        boolean isGiverSectionChanged = !oldResponse.getGiverSection().equals(newResponse.getGiverSection());
        boolean isRecipientSectionChanged = !oldResponse.getRecipientSection().equals(newResponse.getRecipientSection());

        if (isResponseIdChanged || isGiverSectionChanged || isRecipientSectionChanged) {
            List<FeedbackResponseComment> responseComments =
                    frcLogic.getFeedbackResponseCommentForResponse(oldResponse.getId());
            for (FeedbackResponseComment responseComment : responseComments) {
                if (isResponseIdChanged) {
                    responseComment.setFeedbackResponse(newResponse);
                }

                if (isGiverSectionChanged) {
                    responseComment.setGiverSection(newResponse.getGiverSection());
                }

                if (isRecipientSectionChanged) {
                    responseComment.setRecipientSection(newResponse.getRecipientSection());
                }

                frcLogic.updateFeedbackResponseComment(responseComment);
            }
        }

        return newResponse;
    }

    /**
     * Deletes a feedback response cascade its associated comments.
     */
    public void deleteFeedbackResponseCascade(UUID responseId) {
        frDb.deleteFeedbackResponse(getFeedbackResponse(responseId));
    }
}
