package teammates.logic.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.datatransfer.UserType.Role;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Utils;
import teammates.storage.api.FeedbackResponseCommentsDb;

/**
 * Handles the logic related to {@link FeedbackResponseCommentAttributes}.
 */
public class FeedbackResponseCommentsLogic {
    @SuppressWarnings("unused") //used by test
    private static final Logger log = Utils.getLogger();
    
    private static FeedbackResponseCommentsLogic instance;

    private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();
    
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();

    public static FeedbackResponseCommentsLogic inst() {
        if (instance == null) {
            instance = new FeedbackResponseCommentsLogic();
        }
        return instance;
    }

    public FeedbackResponseCommentAttributes createFeedbackResponseComment(FeedbackResponseCommentAttributes frComment)
            throws InvalidParametersException, EntityDoesNotExistException {
        verifyIsCoursePresent(frComment.courseId);
        verifyIsInstructorOfCourse(frComment.courseId, frComment.giverEmail);
        verifyIsFeedbackSessionOfCourse(frComment.courseId, frComment.feedbackSessionName);
        
        try {
            return frcDb.createEntity(frComment);
        } catch (EntityAlreadyExistsException e) {
            try {
                FeedbackResponseCommentAttributes existingComment = new FeedbackResponseCommentAttributes();
                
                existingComment = frcDb.getFeedbackResponseComment(frComment.feedbackResponseId, frComment.giverEmail,
                                                                   frComment.createdAt);
                if (existingComment == null) {
                    existingComment = frcDb.getFeedbackResponseComment(frComment.courseId, frComment.createdAt,
                                                                       frComment.giverEmail);
                }
                frComment.setId(existingComment.getId());
                
                return frcDb.updateFeedbackResponseComment(frComment);            
            } catch (Exception EntityDoesNotExistException) {
                Assumption.fail();
                return null;
            }
        }
    }
    
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(Long feedbackResponseCommentId) {
        return frcDb.getFeedbackResponseComment(feedbackResponseCommentId);
    }
    
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(String responseId, String giverEmail,
                                                                        Date creationDate) {
        return frcDb.getFeedbackResponseComment(responseId, giverEmail, creationDate);
    }

    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForResponse(String feedbackResponseId) {
        return frcDb.getFeedbackResponseCommentsForResponse(feedbackResponseId);
    }
    
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForSession(String courseId,
                                                                                        String feedbackSessionName) {
        return frcDb.getFeedbackResponseCommentsForSession(courseId, feedbackSessionName);
    }

    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForSessionInSection(String courseId,
                                                           String feedbackSessionName, String section) {
        if (section == null) {
            return getFeedbackResponseCommentForSession(courseId, feedbackSessionName);
        } else {
            return frcDb.getFeedbackResponseCommentsForSessionInSection(courseId, feedbackSessionName, section);
        }
    }
    
    public void updateFeedbackResponseCommentsGiverEmail(String courseId, String oldEmail, String updatedEmail) {
        frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, oldEmail, updatedEmail);
    }
    
    // right now this method only updates comment's giverSection and receiverSection for a given response
    public void updateFeedbackResponseCommentsForResponse(String feedbackResponseId)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseCommentAttributes> comments = getFeedbackResponseCommentForResponse(feedbackResponseId);
        FeedbackResponseAttributes response = frLogic.getFeedbackResponse(feedbackResponseId);
        for (FeedbackResponseCommentAttributes comment : comments) {
            comment.giverSection = response.giverSection;
            comment.receiverSection = response.recipientSection;
            frcDb.updateFeedbackResponseComment(comment);
        }
    }

    public FeedbackResponseCommentAttributes updateFeedbackResponseComment(
                                                     FeedbackResponseCommentAttributes feedbackResponseComment)
                                                     throws InvalidParametersException, EntityDoesNotExistException {
        return frcDb.updateFeedbackResponseComment(feedbackResponseComment);    
    }
    
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSendingState(
                                                           String courseId, CommentSendingState state) 
                                                           throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId);
        
        List<FeedbackResponseCommentAttributes> frcList = new ArrayList<FeedbackResponseCommentAttributes>();
        List<FeedbackSessionAttributes> feedbackSessions = fsLogic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes fs : feedbackSessions) {
            if (fs.isPublished()) {
                frcList.addAll(
                        frcDb.getFeedbackResponseCommentsForSendingState(courseId, fs.feedbackSessionName, state));
            }
        }
        return frcList;
    }
    
    public void updateFeedbackResponseCommentsSendingState(
            String courseId, CommentSendingState oldState, CommentSendingState newState) 
            throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId);
        
        List<FeedbackSessionAttributes> feedbackSessions = fsLogic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes fs : feedbackSessions) {
            if (fs.isPublished()) {
                frcDb.updateFeedbackResponseComments(courseId, fs.feedbackSessionName, oldState, newState);    
            }
        }
    }
    
    /**
     * Create or update document for the given comment
     * @param comment
     */
    public void putDocument(FeedbackResponseCommentAttributes comment) {
        frcDb.putDocument(comment);
    }
    
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForGiver(String courseId,
                                                                                       String giverEmail) {
        return frcDb.getFeedbackResponseCommentForGiver(courseId, giverEmail);
    }
    
    public FeedbackResponseCommentSearchResultBundle searchFeedbackResponseComments(String queryString,
                                                             String googleId, String cursorString) {
        return frcDb.search(queryString, googleId, cursorString);
    }
    
    public void deleteFeedbackResponseCommentsForCourse(String courseId) {
        frcDb.deleteFeedbackResponseCommentsForCourse(courseId);
    }
    
    public void deleteFeedbackResponseCommentsForResponse(String responseId) {
        frcDb.deleteFeedbackResponseCommentsForResponse(responseId);
    }
    
    public void deleteFeedbackResponseComment(FeedbackResponseCommentAttributes feedbackResponseComment) {
        frcDb.deleteEntity(feedbackResponseComment);    
    }
    
    /**
     * Remove document for the given comment
     * @param commentToDelete
     */
    public void deleteDocument(FeedbackResponseCommentAttributes commentToDelete) {
        frcDb.deleteDocument(commentToDelete);
    }
    
    /**
     * Verify whether the comment's giver name is visible to certain user
     * @param comment
     * @param response
     * @param userEmail
     * @param roster
     * @return true/false
     */
    public boolean isNameVisibleTo(FeedbackResponseCommentAttributes comment, FeedbackResponseAttributes response,
                                   String userEmail, CourseRoster roster) {
        List<FeedbackParticipantType> showNameTo = comment.showGiverNameTo;
        //in the old ver, name is always visible
        if (showNameTo == null || comment.isVisibilityFollowingFeedbackQuestion) {
            return true;
        }
        
        //comment giver can always see
        if (userEmail.equals(comment.giverEmail)) {
            return true;
        }
        
        String responseGiverTeam = "giverTeam";
        if (roster.getStudentForEmail(response.giverEmail) != null) {
            responseGiverTeam = roster.getStudentForEmail(response.giverEmail).team;
        }
        String responseRecipientTeam = "recipientTeam";
        if (roster.getStudentForEmail(response.recipientEmail) != null) {
            responseRecipientTeam = roster.getStudentForEmail(response.recipientEmail).team;
        }
        String currentUserTeam = "currentUserTeam";
        if (roster.getStudentForEmail(userEmail) != null) {
            currentUserTeam = roster.getStudentForEmail(userEmail).team;
        }
        
        for (FeedbackParticipantType type : showNameTo) {
            if (type == FeedbackParticipantType.GIVER && userEmail.equals(response.giverEmail)) {
                return true;
            } else if (type == FeedbackParticipantType.INSTRUCTORS && roster.getInstructorForEmail(userEmail) != null) {
                return true;
            } else if(type == FeedbackParticipantType.RECEIVER && userEmail.equals(response.recipientEmail)) {
                return true;
            } else if(type == FeedbackParticipantType.OWN_TEAM_MEMBERS && responseGiverTeam.equals(currentUserTeam)) {
                return true;
            } else if(type == FeedbackParticipantType.RECEIVER_TEAM_MEMBERS
                    && responseRecipientTeam.equals(currentUserTeam)) {
                return true;
            } else if(type == FeedbackParticipantType.STUDENTS && roster.getStudentForEmail(userEmail) != null) {
                return true;
            }
        }   
        return false;
    }
    
    /**
     * Verify whether the comment is visible to certain user
     * @return true/false
     */
    public boolean isResponseCommentVisibleForUser(String userEmail, String courseId, UserType.Role role,
            String section, StudentAttributes student, Set<String> studentsEmailInTeam,
            FeedbackResponseAttributes response, FeedbackQuestionAttributes relatedQuestion,
            FeedbackResponseCommentAttributes relatedComment, InstructorAttributes instructor) {
        if (response == null || relatedQuestion == null) {
            return false;
        }
        
        boolean isVisibilityFollowingFeedbackQuestion = relatedComment.isVisibilityFollowingFeedbackQuestion;
        boolean isVisibleToGiver = isVisibilityFollowingFeedbackQuestion ? 
                true : relatedComment.isVisibleTo(FeedbackParticipantType.GIVER);
        
        boolean isVisibleResponseComment = false;
        
        
        boolean userIsInstructor = role == Role.INSTRUCTOR;
        boolean userIsStudent = role == Role.STUDENT;
        
        boolean userIsInstructorAndRelatedResponseCommentIsVisibleToInstructors =
                userIsInstructor && isResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                                               FeedbackParticipantType.INSTRUCTORS);
        boolean userIsResponseRecipientAndRelatedResponseCommentIsVisibleToRecipients =
                response.recipientEmail.equals(userEmail) && isResponseCommentVisibleTo(relatedQuestion,
                                                                     relatedComment, FeedbackParticipantType.RECEIVER);
        boolean userIsResponseGiverAndRelatedResponseCommentIsVisibleToGivers =
                response.giverEmail.equals(userEmail) && isVisibleToGiver;
        boolean userIsRelatedResponseCommentGiver = relatedComment.giverEmail.equals(userEmail);
        boolean userIsStudentAndRelatedResponseCommentIsVisibleToStudents =
                userIsStudent && isResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                                            FeedbackParticipantType.STUDENTS);
        
        boolean userIsInResponseRecipientTeamAndRelatedResponseCommentIsVisibleToRecipients = 
                userIsStudent 
                && ((relatedQuestion.recipientType == FeedbackParticipantType.TEAMS
                    && isResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                                  FeedbackParticipantType.RECEIVER))
                && response.recipientEmail.equals(student.team));
        boolean userIsInResponseGiverTeamAndRelatedResponseCommentIsVisibleToGiversTeamMembers =
                (relatedQuestion.giverType == FeedbackParticipantType.TEAMS
                || isResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                              FeedbackParticipantType.OWN_TEAM_MEMBERS))
                && studentsEmailInTeam.contains(response.giverEmail);
        boolean userIsInResponseRecipientTeamAndRelatedResponseCommentIsVisibleToRecipientsTeamMembers =
                isResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                           FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                && studentsEmailInTeam.contains(response.recipientEmail);
        
        if (userIsInstructorAndRelatedResponseCommentIsVisibleToInstructors
                || userIsResponseRecipientAndRelatedResponseCommentIsVisibleToRecipients
                || userIsResponseGiverAndRelatedResponseCommentIsVisibleToGivers
                || userIsRelatedResponseCommentGiver
                || userIsStudentAndRelatedResponseCommentIsVisibleToStudents) {
            isVisibleResponseComment = true;
        } else if (userIsInResponseRecipientTeamAndRelatedResponseCommentIsVisibleToRecipients
                || userIsInResponseGiverTeamAndRelatedResponseCommentIsVisibleToGiversTeamMembers
                || userIsInResponseRecipientTeamAndRelatedResponseCommentIsVisibleToRecipientsTeamMembers) {
            isVisibleResponseComment = true;
        }
        return isVisibleResponseComment;
    }

    private boolean isResponseCommentVisibleTo(FeedbackQuestionAttributes relatedQuestion,
                                               FeedbackResponseCommentAttributes relatedComment,
                                               FeedbackParticipantType viewerType) {
        boolean isVisibilityFollowingFeedbackQuestion = relatedComment.isVisibilityFollowingFeedbackQuestion;
        boolean isVisibleTo = isVisibilityFollowingFeedbackQuestion ?
                relatedQuestion.isResponseVisibleTo(viewerType) : relatedComment.isVisibleTo(viewerType);
        return isVisibleTo;
    }
    
    private void verifyIsCoursePresent(String courseId) throws EntityDoesNotExistException {
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to create feedback response comments for a course that does not exist.");
        }
    }
    
    private void verifyIsInstructorOfCourse(String courseId, String email) throws EntityDoesNotExistException {
        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, email);
        if (instructor == null) {
            throw new EntityDoesNotExistException("User " + email + " is not a registered instructor for course "
                                                + courseId + ".");
        }
    }
    
    private void verifyIsFeedbackSessionOfCourse(String courseId, String feedbackSessionName)
            throws EntityDoesNotExistException {
        FeedbackSessionAttributes session = fsLogic.getFeedbackSession(feedbackSessionName, courseId);
        if (session == null) {
            throw new EntityDoesNotExistException("Feedback session " + feedbackSessionName 
                                                + " is not a session for course "+ courseId + ".");
        }
    }

    @SuppressWarnings("deprecation")
    public List<FeedbackResponseCommentAttributes> getAllFeedbackResponseComments() {
        return frcDb.getAllFeedbackResponseComments();
    }
}
