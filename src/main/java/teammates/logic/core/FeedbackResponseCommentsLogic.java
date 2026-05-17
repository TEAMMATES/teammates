package teammates.logic.core;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
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
    public FeedbackResponseComment getFeedbackResponseComment(UUID id) {
        return frcDb.getFeedbackResponseComment(id);
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
        validateFeedbackResponseComment(frc);

        if (frcDb.getFeedbackResponseComment(frc.getId()) != null) {
            throw new EntityAlreadyExistsException(
                    String.format(Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS, frc.toString()));
        }

        return frcDb.createFeedbackResponseComment(frc);
    }

    /**
     * Deletes a feedbackResponseComment.
     *
     * <p>Fails silently if the comment does not exist.</p>
     */
    public void deleteFeedbackResponseComment(UUID frcId) {
        FeedbackResponseComment frc = getFeedbackResponseComment(frcId);
        if (frc == null) {
            return;
        }
        frcDb.deleteFeedbackResponseComment(frc);
    }

    /**
     * Updates a feedback response comment by {@link FeedbackResponseComment}.
     *
     * @return updated comment
     * @throws InvalidParametersException if attributes to update are not valid
     */
    public FeedbackResponseComment updateFeedbackResponseComment(FeedbackResponseComment feedbackResponseComment)
            throws InvalidParametersException {
        validateFeedbackResponseComment(feedbackResponseComment);
        return feedbackResponseComment;
    }

    /**
     * Updates a feedback response comment.
     * @throws EntityDoesNotExistException if the comment does not exist
     */
    public FeedbackResponseComment updateFeedbackResponseComment(UUID frcId,
            FeedbackResponseCommentUpdateRequest updateRequest, ResponseGiver updater)
            throws EntityDoesNotExistException {
        FeedbackResponseComment comment = frcDb.getFeedbackResponseComment(frcId);
        if (comment == null) {
            throw new EntityDoesNotExistException("Trying to update a feedback response comment that does not exist.");
        }

        comment.setCommentText(updateRequest.getCommentText());
        comment.setShowCommentTo(updateRequest.getShowCommentTo());
        comment.setShowGiverNameTo(updateRequest.getShowGiverNameTo());
        comment.setLastEditedBy(updater);

        return comment;
    }

    /**
     * Gets all feedback response comments for the given feedback response IDs.
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentsForResponses(List<UUID> feedbackResponseIds) {
        return frcDb.getFeedbackResponseCommentsForResponses(feedbackResponseIds);
    }

    /**
     * Verifies whether the comment is visible to certain user.
     * @return true/false
     */
    public boolean checkIsResponseCommentVisibleForUser(User user,
            Set<String> studentsEmailInTeam, FeedbackResponse response,
            FeedbackQuestion relatedQuestion, FeedbackResponseComment relatedComment) {

        if (response == null || relatedQuestion == null) {
            return false;
        }

        boolean isVisibilityFollowingFeedbackQuestion = relatedComment.getIsVisibilityFollowingFeedbackQuestion();
        boolean isVisibleToGiver = isVisibilityFollowingFeedbackQuestion
                || relatedComment.checkIsVisibleTo(ViewerType.GIVER);

        boolean isVisibleToUser = checkIsVisibleToUser(user, response, relatedQuestion, relatedComment, isVisibleToGiver);

        boolean isVisibleToUserTeam = false;
        if (user instanceof Student student) {
            isVisibleToUserTeam = checkIsVisibleToUserTeam(student, studentsEmailInTeam, response,
                    relatedQuestion, relatedComment);
        }

        return isVisibleToUser || isVisibleToUserTeam;
    }

    private boolean checkIsVisibleToUserTeam(Student student, Set<String> studentsEmailInTeam,
            FeedbackResponse response, FeedbackQuestion relatedQuestion,
            FeedbackResponseComment relatedComment) {

        boolean isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipients =
                relatedQuestion.getRecipientType() == QuestionRecipientType.TEAMS
                && checkIsResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                              ViewerType.RECEIVER)
                && response.getRecipient().equals(student.getTeamName());

        boolean isUserInResponseGiverTeamAndRelatedResponseCommentVisibleToGiversTeamMembers =
                (relatedQuestion.getGiverType() == QuestionGiverType.TEAMS
                || checkIsResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                              ViewerType.OWN_TEAM_MEMBERS))
                && (studentsEmailInTeam.contains(response.getGiver())
                        || student.getTeamName().equals(response.getGiver()));

        boolean isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipientsTeamMembers =
                checkIsResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                           ViewerType.RECEIVER_TEAM_MEMBERS)
                && studentsEmailInTeam.contains(response.getRecipient());

        return isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipients
                || isUserInResponseGiverTeamAndRelatedResponseCommentVisibleToGiversTeamMembers
                || isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipientsTeamMembers;
    }

    private boolean checkIsVisibleToUser(User user, FeedbackResponse response,
            FeedbackQuestion relatedQuestion, FeedbackResponseComment relatedComment,
            boolean isVisibleToGiver) {
        boolean isUserInstructor = user instanceof Instructor;

        boolean isUserInstructorAndRelatedResponseCommentVisibleToInstructors =
                isUserInstructor && checkIsResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                                               ViewerType.INSTRUCTORS);

        boolean isUserResponseRecipientAndRelatedResponseCommentVisibleToRecipients =
                SanitizationHelper.areEmailsEqual(response.getRecipient(), user.getEmail())
                        && checkIsResponseCommentVisibleTo(relatedQuestion,
                        relatedComment, ViewerType.RECEIVER);

        boolean isUserResponseGiverAndRelatedResponseCommentVisibleToGivers =
                SanitizationHelper.areEmailsEqual(response.getGiver(), user.getEmail()) && isVisibleToGiver;

        boolean isUserRelatedResponseCommentGiver = false;
        ResponseGiver commentGiver = relatedComment.getGiver();
        if (commentGiver.isGiverTeam() && user instanceof Student student) {
            isUserRelatedResponseCommentGiver = student.getTeamId().equals(commentGiver.getGiverTeamId());
        } else if (commentGiver.isGiverUser() && user instanceof Student student) {
            isUserRelatedResponseCommentGiver = student.getId().equals(commentGiver.getGiverUserId());
        } else if (commentGiver.isGiverUser() && user instanceof Instructor instructor) {
            isUserRelatedResponseCommentGiver = instructor.getId().equals(commentGiver.getGiverUserId());
        }

        boolean isUserStudentAndRelatedResponseCommentVisibleToStudents =
                !isUserInstructor && checkIsResponseCommentVisibleTo(relatedQuestion,
                        relatedComment, ViewerType.STUDENTS);

        return isUserInstructorAndRelatedResponseCommentVisibleToInstructors
                || isUserResponseRecipientAndRelatedResponseCommentVisibleToRecipients
                || isUserResponseGiverAndRelatedResponseCommentVisibleToGivers
                || isUserRelatedResponseCommentGiver
                || isUserStudentAndRelatedResponseCommentVisibleToStudents;
    }

    private boolean checkIsResponseCommentVisibleTo(FeedbackQuestion relatedQuestion,
                                               FeedbackResponseComment relatedComment,
                                               ViewerType viewerType) {
        boolean isVisibilityFollowingFeedbackQuestion = relatedComment.getIsVisibilityFollowingFeedbackQuestion();
        return isVisibilityFollowingFeedbackQuestion
                ? relatedQuestion.isResponseVisibleTo(viewerType)
                : relatedComment.checkIsVisibleTo(viewerType);
    }

    /**
     * Returns true if the comment's giver name is visible to certain user.
     */
    public boolean checkIsNameVisibleToUser(FeedbackResponseComment comment, FeedbackResponse response,
                                   String userEmail, CourseRoster roster) {
        if (comment.getIsVisibilityFollowingFeedbackQuestion()) {
            return true;
        }

        //comment giver can always see
        ResponseGiver commentGiver = comment.getGiver();
        if (Objects.equals(userEmail, commentGiver.getIdentifier())) {
            return true;
        }

        List<ViewerType> showNameTo = comment.getShowGiverNameTo();
        assert showNameTo != null : "showNameTo should not be null if isVisibilityFollowingFeedbackQuestion is false";

        return checkIsFeedbackParticipantNameVisibleToUser(response, userEmail, roster, showNameTo);
    }

    private void validateFeedbackResponseComment(FeedbackResponseComment feedbackResponseComment)
            throws InvalidParametersException {
        if (!feedbackResponseComment.isValid()) {
            throw new InvalidParametersException(feedbackResponseComment.getInvalidityInfo());
        }
    }

    private boolean checkIsFeedbackParticipantNameVisibleToUser(FeedbackResponse response,
            String userEmail, CourseRoster roster, List<ViewerType> showNameTo) {
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
        for (ViewerType type : showNameTo) {
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
                if (SanitizationHelper.areEmailsEqual(userEmail, response.getRecipient())) {
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
                if (SanitizationHelper.areEmailsEqual(userEmail, response.getGiver())) {
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
