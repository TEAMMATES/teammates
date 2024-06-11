package teammates.sqllogic.core;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SqlCourseRoster;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.FeedbackResponseCommentsDb;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.Student;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;

/**
 * Handles operations related to feedback response comments.
 *
 * @see FeedbackResponseComment
 * @see FeedbackResponseCommentsDb
 */
public final class FeedbackResponseCommentsLogic {

    private static final FeedbackResponseCommentsLogic instance = new FeedbackResponseCommentsLogic();
    private FeedbackResponseCommentsDb frcDb;

    private FeedbackResponseCommentsLogic() {
        // prevent initialization
    }

    public static FeedbackResponseCommentsLogic inst() {
        return instance;
    }

    /**
     * Initialize dependencies for {@code FeedbackResponseCommentsLogic}.
     */
    void initLogicDependencies(FeedbackResponseCommentsDb frcDb) {
        this.frcDb = frcDb;
    }

    /**
     * Gets an feedback response comment by feedback response comment id.
     * @param id of feedback response comment.
     * @return the specified feedback response comment.
     */
    public FeedbackResponseComment getFeedbackResponseComment(Long id) {
        return frcDb.getFeedbackResponseComment(id);
    }

    /**
     * Gets all feedback response comments for a response.
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentForResponse(UUID feedbackResponseId) {
        return frcDb.getFeedbackResponseCommentsForResponse(feedbackResponseId);
    }

    /**
     * Gets all response comments for a response.
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentsForResponse(UUID feedbackResponseId) {
        assert feedbackResponseId != null;

        return frcDb.getFeedbackResponseCommentsForResponse(feedbackResponseId);
    }

    /**
     * Gets the comment associated with the response.
     */
    public FeedbackResponseComment getFeedbackResponseCommentForResponseFromParticipant(
            UUID feedbackResponseId) {
        return frcDb.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);
    }

    /**
     * Creates a feedback response comment.
     * @throws EntityAlreadyExistsException if the comment alreadty exists
     * @throws InvalidParametersException if the comment is invalid
     */
    public FeedbackResponseComment createFeedbackResponseComment(FeedbackResponseComment frc)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return frcDb.createFeedbackResponseComment(frc);
    }

    /**
     * Deletes a feedbackResponseComment.
     */
    public void deleteFeedbackResponseComment(Long frcId) {
        frcDb.deleteFeedbackResponseComment(frcId);
    }

    /**
     * Updates a feedback response comment by {@link FeedbackResponseComment}.
     *
     * @return updated comment
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     */
    public FeedbackResponseComment updateFeedbackResponseComment(FeedbackResponseComment feedbackResponseComment)
            throws InvalidParametersException, EntityDoesNotExistException {

        return frcDb.updateFeedbackResponseComment(feedbackResponseComment);
    }

    /**
     * Updates a feedback response comment.
     * @throws EntityDoesNotExistException if the comment does not exist
     */
    public FeedbackResponseComment updateFeedbackResponseComment(Long frcId,
            FeedbackResponseCommentUpdateRequest updateRequest, String updaterEmail)
            throws EntityDoesNotExistException {
        FeedbackResponseComment comment = frcDb.getFeedbackResponseComment(frcId);
        if (comment == null) {
            throw new EntityDoesNotExistException("Trying to update a feedback response comment that does not exist.");
        }

        comment.setCommentText(updateRequest.getCommentText());
        comment.setShowCommentTo(updateRequest.getShowCommentTo());
        comment.setShowGiverNameTo(updateRequest.getShowGiverNameTo());
        comment.setLastEditorEmail(updaterEmail);

        return comment;
    }

    /**
     * Updates all feedback response comments with new emails.
     */
    public void updateFeedbackResponseCommentsEmails(String courseId, String oldEmail, String updatedEmail) {
        frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, oldEmail, updatedEmail);
        frcDb.updateLastEditorEmailOfFeedbackResponseComments(courseId, oldEmail, updatedEmail);
    }

    /**
     * Updates all feedback response comments with new sections.
     */
    public void updateFeedbackResponseCommentsForResponse(FeedbackResponse response)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseComment> comments = getFeedbackResponseCommentForResponse(response.getId());
        for (FeedbackResponseComment comment : comments) {
            comment.setGiverSection(response.getGiverSection());
            comment.setRecipientSection(response.getRecipientSection());
            frcDb.updateFeedbackResponseComment(comment);
        }
    }

    /**
     * Gets all feedback response comments for session in a section.
     *
     * @param courseId the course ID of the feedback session
     * @param feedbackSessionName the feedback session name
     * @param sectionName if null, will retrieve all comments in the session
     * @return a list of feedback response comments
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentForSessionInSection(
            String courseId, String feedbackSessionName, @Nullable String sectionName) {
        if (sectionName == null) {
            return frcDb.getFeedbackResponseCommentsForSession(courseId, feedbackSessionName);
        }
        return frcDb.getFeedbackResponseCommentsForSessionInSection(courseId, feedbackSessionName, sectionName);
    }

    /**
     * Gets all feedback response comments for a question in a section.
     *
     * @param questionId the ID of the question
     * @param sectionName if null, will retrieve all comments for the question
     * @return a list of feedback response comments
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentForQuestionInSection(
            UUID questionId, @Nullable String sectionName) {
        if (sectionName == null) {
            return frcDb.getFeedbackResponseCommentsForQuestion(questionId);
        }
        return frcDb.getFeedbackResponseCommentsForQuestionInSection(questionId, sectionName);
    }

    /**
     * Verifies whether the comment is visible to certain user.
     * @return true/false
     */
    public boolean checkIsResponseCommentVisibleForUser(String userEmail, boolean isInstructor,
            Student student, Set<String> studentsEmailInTeam, FeedbackResponse response,
            FeedbackQuestion relatedQuestion, FeedbackResponseComment relatedComment) {

        if (response == null || relatedQuestion == null) {
            return false;
        }

        boolean isVisibilityFollowingFeedbackQuestion = relatedComment.getIsVisibilityFollowingFeedbackQuestion();
        boolean isVisibleToGiver = isVisibilityFollowingFeedbackQuestion
                                 || relatedComment.checkIsVisibleTo(FeedbackParticipantType.GIVER);

        boolean isVisibleToUser = checkIsVisibleToUser(userEmail, response, relatedQuestion, relatedComment,
                isVisibleToGiver, isInstructor, !isInstructor);

        boolean isVisibleToUserTeam = checkIsVisibleToUserTeam(student, studentsEmailInTeam, response,
                relatedQuestion, relatedComment, !isInstructor);

        return isVisibleToUser || isVisibleToUserTeam;
    }

    private boolean checkIsVisibleToUserTeam(Student student, Set<String> studentsEmailInTeam,
            FeedbackResponse response, FeedbackQuestion relatedQuestion,
            FeedbackResponseComment relatedComment, boolean isUserStudent) {

        boolean isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipients =
                isUserStudent
                && relatedQuestion.getRecipientType() == FeedbackParticipantType.TEAMS
                && checkIsResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                              FeedbackParticipantType.RECEIVER)
                && response.getRecipient().equals(student.getTeamName());

        boolean isUserInResponseGiverTeamAndRelatedResponseCommentVisibleToGiversTeamMembers =
                (relatedQuestion.getGiverType() == FeedbackParticipantType.TEAMS
                || checkIsResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                              FeedbackParticipantType.OWN_TEAM_MEMBERS))
                && (studentsEmailInTeam.contains(response.getGiver())
                        || isUserStudent && student.getTeamName().equals(response.getGiver()));

        boolean isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipientsTeamMembers =
                checkIsResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                           FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                && studentsEmailInTeam.contains(response.getRecipient());

        return isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipients
                || isUserInResponseGiverTeamAndRelatedResponseCommentVisibleToGiversTeamMembers
                || isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipientsTeamMembers;
    }

    private boolean checkIsVisibleToUser(String userEmail, FeedbackResponse response,
            FeedbackQuestion relatedQuestion, FeedbackResponseComment relatedComment,
            boolean isVisibleToGiver, boolean isUserInstructor, boolean isUserStudent) {

        boolean isUserInstructorAndRelatedResponseCommentVisibleToInstructors =
                isUserInstructor && checkIsResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                                               FeedbackParticipantType.INSTRUCTORS);

        boolean isUserResponseRecipientAndRelatedResponseCommentVisibleToRecipients =
                response.getRecipient().equals(userEmail) && checkIsResponseCommentVisibleTo(relatedQuestion,
                        relatedComment, FeedbackParticipantType.RECEIVER);

        boolean isUserResponseGiverAndRelatedResponseCommentVisibleToGivers =
                response.getGiver().equals(userEmail) && isVisibleToGiver;

        boolean isUserRelatedResponseCommentGiver = relatedComment.getGiver().equals(userEmail);

        boolean isUserStudentAndRelatedResponseCommentVisibleToStudents =
                isUserStudent && checkIsResponseCommentVisibleTo(relatedQuestion,
                        relatedComment, FeedbackParticipantType.STUDENTS);

        return isUserInstructorAndRelatedResponseCommentVisibleToInstructors
                || isUserResponseRecipientAndRelatedResponseCommentVisibleToRecipients
                || isUserResponseGiverAndRelatedResponseCommentVisibleToGivers
                || isUserRelatedResponseCommentGiver
                || isUserStudentAndRelatedResponseCommentVisibleToStudents;
    }

    private boolean checkIsResponseCommentVisibleTo(FeedbackQuestion relatedQuestion,
                                               FeedbackResponseComment relatedComment,
                                               FeedbackParticipantType viewerType) {
        boolean isVisibilityFollowingFeedbackQuestion = relatedComment.getIsVisibilityFollowingFeedbackQuestion();
        return isVisibilityFollowingFeedbackQuestion
                ? relatedQuestion.isResponseVisibleTo(viewerType)
                : relatedComment.checkIsVisibleTo(viewerType);
    }

    /**
     * Returns true if the comment's giver name is visible to certain user.
     */
    public boolean checkIsNameVisibleToUser(FeedbackResponseComment comment, FeedbackResponse response,
                                   String userEmail, SqlCourseRoster roster) {
        List<FeedbackParticipantType> showNameTo = comment.getShowGiverNameTo();
        //in the old ver, name is always visible
        if (showNameTo == null || comment.getIsVisibilityFollowingFeedbackQuestion()) {
            return true;
        }

        //comment giver can always see
        if (userEmail.equals(comment.getGiver())) {
            return true;
        }

        return checkIsFeedbackParticipantNameVisibleToUser(response, userEmail, roster, showNameTo);
    }

    private boolean checkIsFeedbackParticipantNameVisibleToUser(FeedbackResponse response,
            String userEmail, SqlCourseRoster roster, List<FeedbackParticipantType> showNameTo) {
        String responseGiverTeam = "giverTeam";
        if (roster.getStudentForEmail(response.getGiver()) != null) {
            responseGiverTeam = roster.getStudentForEmail(response.getGiver()).getTeamName();
        }
        String responseRecipientTeam = "recipientTeam";
        if (roster.getStudentForEmail(response.getRecipient()) != null) {
            responseRecipientTeam = roster.getStudentForEmail(response.getRecipient()).getTeamName();
        }
        String currentUserTeam = "currentUserTeam";
        if (roster.getStudentForEmail(userEmail) != null) {
            currentUserTeam = roster.getStudentForEmail(userEmail).getTeamName();
        }
        for (FeedbackParticipantType type : showNameTo) {
            switch (type) {
            case INSTRUCTORS:
                if (roster.getInstructorForEmail(userEmail) != null) {
                    return true;
                }
                break;
            case OWN_TEAM_MEMBERS:
                if (responseGiverTeam.equals(currentUserTeam)) {
                    return true;
                }
                break;
            case RECEIVER:
                if (userEmail.equals(response.getRecipient())) {
                    return true;
                }
                break;
            case RECEIVER_TEAM_MEMBERS:
                if (responseRecipientTeam.equals(currentUserTeam)) {
                    return true;
                }
                break;
            case STUDENTS:
                if (roster.getStudentForEmail(userEmail) != null) {
                    return true;
                }
                break;
            case GIVER:
                if (userEmail.equals(response.getGiver())) {
                    return true;
                }
                break;
            default:
                break;
            }
        }
        return false;
    }
}
