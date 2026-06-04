package teammates.logic.core;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.ResponseInstructorCommentsDb;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;
import teammates.ui.request.ResponseInstructorCommentUpdateRequest;

/**
 * Handles operations related to feedback response comments.
 *
 * @see ResponseInstructorComment
 * @see ResponseInstructorCommentsDb
 */
public final class ResponseInstructorCommentsLogic {

    private static final ResponseInstructorCommentsLogic instance = new ResponseInstructorCommentsLogic();
    private ResponseInstructorCommentsDb frcDb;
    private FeedbackResponsesLogic frLogic;

    private ResponseInstructorCommentsLogic() {
        // prevent initialization
    }

    public static ResponseInstructorCommentsLogic inst() {
        return instance;
    }

    /**
     * Initialize dependencies for {@code ResponseInstructorCommentsLogic}.
     */
    void initLogicDependencies(ResponseInstructorCommentsDb frcDb, FeedbackResponsesLogic frLogic) {
        this.frcDb = frcDb;
        this.frLogic = frLogic;
    }

    /**
     * Gets an feedback response comment by feedback response comment id.
     * @param id of feedback response comment.
     * @return the specified feedback response comment.
     */
    public ResponseInstructorComment getResponseInstructorComment(UUID id) {
        return frcDb.getResponseInstructorComment(id);
    }

    /**
     * Creates a feedback response comment.
     *
     * @throws EntityDoesNotExistException if the feedback response does not exist
     * @throws InvalidParametersException if the comment is invalid
     */
    public ResponseInstructorComment createResponseInstructorComment(UUID feedbackResponseId, ResponseGiver giver,
            String commentText, List<ViewerType> showCommentTo, List<ViewerType> showGiverNameTo)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackResponse feedbackResponse = frLogic.getFeedbackResponse(feedbackResponseId);
        if (feedbackResponse == null) {
            throw new EntityDoesNotExistException("The feedback response does not exist.");
        }

        ResponseInstructorComment frc = new ResponseInstructorComment(giver, commentText,
                showCommentTo, showGiverNameTo, giver);
        feedbackResponse.addResponseInstructorComment(frc);

        validateResponseInstructorComment(frc);

        return frcDb.persistResponseInstructorComment(frc);
    }

    /**
     * Deletes a ResponseInstructorComment.
     *
     * <p>Fails silently if the comment does not exist.</p>
     */
    public void deleteResponseInstructorComment(UUID frcId) {
        ResponseInstructorComment frc = getResponseInstructorComment(frcId);
        if (frc == null) {
            return;
        }
        frcDb.deleteResponseInstructorComment(frc);
    }

    /**
     * Updates a feedback response comment by {@link ResponseInstructorComment}.
     *
     * @return updated comment
     * @throws InvalidParametersException if attributes to update are not valid
     */
    public ResponseInstructorComment updateResponseInstructorComment(ResponseInstructorComment responseInstructorComment)
            throws InvalidParametersException {
        validateResponseInstructorComment(responseInstructorComment);
        return responseInstructorComment;
    }

    /**
     * Updates a feedback response comment.
     * @throws EntityDoesNotExistException if the comment does not exist
     */
    public ResponseInstructorComment updateResponseInstructorComment(UUID frcId,
            ResponseInstructorCommentUpdateRequest updateRequest, ResponseGiver updater)
            throws EntityDoesNotExistException {
        ResponseInstructorComment comment = frcDb.getResponseInstructorComment(frcId);
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
    public List<ResponseInstructorComment> getResponseInstructorCommentsForResponses(List<UUID> feedbackResponseIds) {
        return frcDb.getResponseInstructorCommentsForResponses(feedbackResponseIds);
    }

    /**
     * Verifies whether the comment is visible to certain user.
     * @return true/false
     */
    public boolean checkIsResponseCommentVisibleForUser(User user,
            FeedbackResponse response, FeedbackQuestion relatedQuestion, ResponseInstructorComment relatedComment) {

        if (response == null || relatedQuestion == null) {
            return false;
        }

        boolean isVisibleToGiver = relatedComment.checkIsVisibleTo(ViewerType.GIVER);

        boolean isVisibleToUser = checkIsVisibleToUser(user, response, relatedComment, isVisibleToGiver);

        boolean isVisibleToUserTeam = false;
        if (user instanceof Student student) {
            isVisibleToUserTeam = checkIsVisibleToUserTeam(student, response,
                    relatedQuestion, relatedComment);
        }

        return isVisibleToUser || isVisibleToUserTeam;
    }

    private boolean checkIsVisibleToUserTeam(Student student,
            FeedbackResponse response, FeedbackQuestion relatedQuestion,
            ResponseInstructorComment relatedComment) {
        Team studentTeam = student.getTeam();

        ResponseGiver responseGiver = response.getGiver();
        Team giverTeam = null;
        if (responseGiver.isGiverTeam()) {
            giverTeam = responseGiver.getGiverTeam();
        } else if (responseGiver.getGiverUser() instanceof Student studentGiver) {
            giverTeam = studentGiver.getTeam();
        }

        ResponseRecipient responseRecipient = response.getRecipient();
        Team recipientTeam = null;
        if (responseRecipient.getRecipientTeam() != null) {
            recipientTeam = responseRecipient.getRecipientTeam();
        } else if (responseRecipient.getRecipientUser() instanceof Student studentRecipient) {
            recipientTeam = studentRecipient.getTeam();
        }

        boolean isUserInGiverTeam = giverTeam != null && giverTeam.equals(studentTeam);
        boolean isUserInRecipientTeam = recipientTeam != null && recipientTeam.equals(studentTeam);

        boolean isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipients =
                relatedQuestion.getRecipientType() == QuestionRecipientType.TEAMS
                && checkIsResponseCommentVisibleTo(relatedComment, ViewerType.RECEIVER)
                && isUserInRecipientTeam;

        boolean isUserInResponseGiverTeamAndRelatedResponseCommentVisibleToGiversTeamMembers =
                (relatedQuestion.getGiverType() == QuestionGiverType.TEAMS
                || checkIsResponseCommentVisibleTo(relatedComment, ViewerType.OWN_TEAM_MEMBERS))
                && isUserInGiverTeam;

        boolean isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipientsTeamMembers =
                checkIsResponseCommentVisibleTo(relatedComment, ViewerType.RECEIVER_TEAM_MEMBERS)
                && isUserInRecipientTeam;

        return isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipients
                || isUserInResponseGiverTeamAndRelatedResponseCommentVisibleToGiversTeamMembers
                || isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipientsTeamMembers;
    }

    private boolean checkIsVisibleToUser(User user, FeedbackResponse response,
            ResponseInstructorComment relatedComment,
            boolean isVisibleToGiver) {
        boolean isUserInstructor = user instanceof Instructor;

        boolean isUserInstructorAndRelatedResponseCommentVisibleToInstructors =
                isUserInstructor && checkIsResponseCommentVisibleTo(relatedComment, ViewerType.INSTRUCTORS);

        boolean isUserResponseRecipientAndRelatedResponseCommentVisibleToRecipients =
                Objects.equals(response.getRecipient().getRecipientUser(), user)
                        && checkIsResponseCommentVisibleTo(relatedComment, ViewerType.RECEIVER);

        boolean isUserResponseGiverAndRelatedResponseCommentVisibleToGivers =
                Objects.equals(response.getGiver().getGiverUser(), user) && isVisibleToGiver;

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
                !isUserInstructor && checkIsResponseCommentVisibleTo(relatedComment, ViewerType.STUDENTS);

        return isUserInstructorAndRelatedResponseCommentVisibleToInstructors
                || isUserResponseRecipientAndRelatedResponseCommentVisibleToRecipients
                || isUserResponseGiverAndRelatedResponseCommentVisibleToGivers
                || isUserRelatedResponseCommentGiver
                || isUserStudentAndRelatedResponseCommentVisibleToStudents;
    }

    private boolean checkIsResponseCommentVisibleTo(
                                               ResponseInstructorComment relatedComment,
                                               ViewerType viewerType) {
        return relatedComment.checkIsVisibleTo(viewerType);
    }

    /**
     * Returns true if the comment's giver name is visible to certain user.
     */
    public boolean checkIsNameVisibleToUser(ResponseInstructorComment comment, FeedbackResponse response, User user) {
        //comment giver can always see
        ResponseGiver commentGiver = comment.getGiver();
        if (Objects.equals(user, commentGiver.getGiverUser()) || user instanceof Student student
                && commentGiver.isGiverTeam()
                && student.getTeam().equals(commentGiver.getGiverTeam())) {
            return true;
        }

        List<ViewerType> showNameTo = comment.getShowGiverNameTo();
        assert showNameTo != null : "showNameTo should not be null";

        return checkIsFeedbackGiverNameVisibleToUser(response, user, showNameTo);
    }

    private void validateResponseInstructorComment(ResponseInstructorComment responseInstructorComment)
            throws InvalidParametersException {
        if (!responseInstructorComment.isValid()) {
            throw new InvalidParametersException(responseInstructorComment.getInvalidityInfo());
        }
    }

    private boolean checkIsFeedbackGiverNameVisibleToUser(FeedbackResponse response,
            User user, List<ViewerType> showNameTo) {
        ResponseGiver responseGiver = response.getGiver();
        ResponseRecipient responseRecipient = response.getRecipient();
        for (ViewerType type : showNameTo) {
            switch (type) {
            case INSTRUCTORS:
                if (user instanceof Instructor) {
                    return true;
                }
                break;
            case OWN_TEAM_MEMBERS:
                if (user instanceof Student student && student.getTeam().equals(responseGiver.getGiverTeam())) {
                    return true;
                }
                break;
            case RECEIVER:
                if (responseRecipient.isRecipientUser() && user.equals(responseRecipient.getRecipientUser())) {
                    return true;
                }
                if (responseRecipient.isRecipientTeam() && user instanceof Student student
                        && student.getTeam().equals(responseRecipient.getRecipientTeam())) {
                    return true;
                }
                break;
            case RECEIVER_TEAM_MEMBERS:
                if (user instanceof Student student && student.getTeam().equals(responseRecipient.getRecipientTeam())) {
                    return true;
                }
                break;
            case STUDENTS:
                if (user instanceof Student) {
                    return true;
                }
                break;
            case GIVER:
                if (responseGiver.isGiverUser() && user.equals(responseGiver.getGiverUser())) {
                    return true;
                }
                if (responseGiver.isGiverTeam() && user instanceof Student student
                        && student.getTeam().equals(responseGiver.getGiverTeam())) {
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
