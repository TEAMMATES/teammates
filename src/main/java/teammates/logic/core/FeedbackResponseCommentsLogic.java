package teammates.logic.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
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
                FeedbackResponseCommentAttributes existingComment = getExistingFeedbackResponseComment(frComment);
                frComment.setId(existingComment.getId());
                return frcDb.updateFeedbackResponseComment(frComment);
            } catch (Exception ex) {
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

    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForResponse(String feedbackResponseId) {
        return frcDb.getFeedbackResponseCommentsForResponse(feedbackResponseId);
    }
    
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSession(String courseId,
                                                                                         String feedbackSessionName) {
        return frcDb.getFeedbackResponseCommentsForSession(courseId, feedbackSessionName);
    }

    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSessionInSection(String courseId,
                                                           String feedbackSessionName, String section) {
        if (section == null) {
            return getFeedbackResponseCommentsForSession(courseId, feedbackSessionName);
        }
        return frcDb.getFeedbackResponseCommentsForSessionInSection(courseId, feedbackSessionName, section);
    }
    
    /**
     * Updates comments corresponding to {@code oldResponseId} to point to {@code newResponseId} instead
     */
    public void updateFeedbackResponseCommentsForChangingResponseId(
            String oldResponseId, String newResponseId)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseCommentAttributes> responseComments =
                getFeedbackResponseCommentsForResponse(oldResponseId);
        for (FeedbackResponseCommentAttributes responseComment : responseComments) {
            responseComment.feedbackResponseId = newResponseId;
            frcDb.updateFeedbackResponseComment(responseComment);
        }
    }
    

    /**
     * Updates all email fields of feedback response comments with the new email
     */
    public void updateFeedbackResponseCommentsEmails(String courseId, String oldEmail, String updatedEmail) {
        frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, oldEmail, updatedEmail);
        frcDb.updateLastEditorEmailOfFeedbackResponseComments(courseId, oldEmail, updatedEmail);
    }
    
    /**
     * Updates the giver and receiver sections for feedback response comments associated with a particular response.
     */
    public void updateSectionsForFeedbackResponseCommentsForResponse(String feedbackResponseId)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseCommentAttributes> comments = getFeedbackResponseCommentsForResponse(feedbackResponseId);
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
                        frcDb.getFeedbackResponseCommentsForSendingState(courseId, fs.getFeedbackSessionName(), state));
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
                frcDb.updateFeedbackResponseComments(courseId, fs.getFeedbackSessionName(), oldState, newState);
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
                                                             List<InstructorAttributes> instructors,
                                                             String cursorString) {
        return frcDb.search(queryString, instructors, cursorString);
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
        if (roster.getStudentForEmail(response.giver) != null) {
            responseGiverTeam = roster.getStudentForEmail(response.giver).team;
        }
        String responseRecipientTeam = "recipientTeam";
        if (roster.getStudentForEmail(response.recipient) != null) {
            responseRecipientTeam = roster.getStudentForEmail(response.recipient).team;
        }
        String currentUserTeam = "currentUserTeam";
        if (roster.getStudentForEmail(userEmail) != null) {
            currentUserTeam = roster.getStudentForEmail(userEmail).team;
        }
        
        return doesVisibilityRecipientTypeMatchUserDetails(response, userEmail, roster, showNameTo, responseGiverTeam,
                                                           responseRecipientTeam, currentUserTeam);
    }

    /**
     * Checks if the user matches any of the feedback participant types for the given response.
     */
    private boolean doesVisibilityRecipientTypeMatchUserDetails(FeedbackResponseAttributes response, String userEmail,
            CourseRoster roster, List<FeedbackParticipantType> showNameTo, String responseGiverTeam,
            String responseRecipientTeam, String currentUserTeam) {
        for (FeedbackParticipantType type : showNameTo) {
            if (type == FeedbackParticipantType.GIVER && userEmail.equals(response.giver)) {
                return true;
            } else if (type == FeedbackParticipantType.INSTRUCTORS && roster.getInstructorForEmail(userEmail) != null) {
                return true;
            } else if (type == FeedbackParticipantType.RECEIVER && userEmail.equals(response.recipient)) {
                return true;
            } else if (type == FeedbackParticipantType.OWN_TEAM_MEMBERS && responseGiverTeam.equals(currentUserTeam)) {
                return true;
            } else if (type == FeedbackParticipantType.RECEIVER_TEAM_MEMBERS
                    && responseRecipientTeam.equals(currentUserTeam)) {
                return true;
            } else if (type == FeedbackParticipantType.STUDENTS && roster.getStudentForEmail(userEmail) != null) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Verify whether the comment is visible to certain user
     * Does not take individual instructor privileges into account
     * @return true/false
     */
    public boolean isResponseCommentVisibleForUser(String userEmail, String courseId, UserType.Role role,
            StudentAttributes student, Set<String> studentsEmailInTeam, FeedbackResponseAttributes relatedResponse,
            FeedbackQuestionAttributes relatedQuestion, FeedbackResponseCommentAttributes comment) {
        if (relatedResponse == null || relatedQuestion == null) {
            return false;
        }
        
        boolean isVisibilityFollowingFeedbackQuestion = comment.isVisibilityFollowingFeedbackQuestion;
        boolean isVisibleToGiver = isVisibilityFollowingFeedbackQuestion
                                 || comment.isVisibleTo(FeedbackParticipantType.GIVER);
        
        boolean userIsInstructor = role == Role.INSTRUCTOR;
        boolean userIsStudent = role == Role.STUDENT;
        
        boolean isVisibleToUserAsIndividual =
                isResponseCommentVisibleToUserAsIndividual(
                        userEmail, relatedResponse, relatedQuestion, comment,
                        isVisibleToGiver, userIsInstructor, userIsStudent);
        
        boolean isVisibleToUserAsTeamMember =
                isResponseCommentVisibleToUserAsTeamMember(
                        student, studentsEmailInTeam, relatedResponse,
                        relatedQuestion, comment, userIsStudent);
        
        return isVisibleToUserAsIndividual || isVisibleToUserAsTeamMember;
    }

    /**
     * Checks if the response comment is visible to the user as an individual.
     */
    private boolean isResponseCommentVisibleToUserAsIndividual(String userEmail,
            FeedbackResponseAttributes relatedResponse, FeedbackQuestionAttributes relatedQuestion,
            FeedbackResponseCommentAttributes comment, boolean isVisibleToGiver,
            boolean userIsInstructor, boolean userIsStudent) {
        boolean userIsInstructorAndResponseCommentIsVisibleToInstructors =
                userIsInstructor && isResponseCommentVisibleTo(relatedQuestion, comment,
                                                               FeedbackParticipantType.INSTRUCTORS);
        
        boolean userIsResponseRecipientAndResponseCommentIsVisibleToRecipients =
                relatedResponse.recipient.equals(userEmail) && isResponseCommentVisibleTo(relatedQuestion,
                                                                     comment, FeedbackParticipantType.RECEIVER);
        
        boolean userIsResponseGiverAndResponseCommentIsVisibleToGivers =
                relatedResponse.giver.equals(userEmail) && isVisibleToGiver;
        
        boolean userIsResponseCommentGiver = comment.giverEmail.equals(userEmail);
        
        boolean userIsStudentAndResponseCommentIsVisibleToStudents =
                userIsStudent && isResponseCommentVisibleTo(relatedQuestion, comment,
                                                            FeedbackParticipantType.STUDENTS);
        
        return userIsInstructorAndResponseCommentIsVisibleToInstructors
                || userIsResponseRecipientAndResponseCommentIsVisibleToRecipients
                || userIsResponseGiverAndResponseCommentIsVisibleToGivers
                || userIsResponseCommentGiver
                || userIsStudentAndResponseCommentIsVisibleToStudents;
    }

    /**
     * Checks if the response comment is visible to the user as part of a team.
     */
    private boolean isResponseCommentVisibleToUserAsTeamMember(StudentAttributes student,
            Set<String> studentsEmailInTeam, FeedbackResponseAttributes relatedResponse,
            FeedbackQuestionAttributes relatedQuestion, FeedbackResponseCommentAttributes comment,
            boolean userIsStudent) {
        boolean userIsInResponseRecipientTeamAndResponseCommentIsVisibleToRecipients =
                userIsStudent
                && relatedQuestion.recipientType == FeedbackParticipantType.TEAMS
                && isResponseCommentVisibleTo(relatedQuestion, comment,
                                              FeedbackParticipantType.RECEIVER)
                && relatedResponse.recipient.equals(student.team);
        
        boolean userIsInResponseGiverTeamAndResponseCommentIsVisibleToGiversTeamMembers =
                (relatedQuestion.giverType == FeedbackParticipantType.TEAMS
                || isResponseCommentVisibleTo(relatedQuestion, comment,
                                              FeedbackParticipantType.OWN_TEAM_MEMBERS))
                && studentsEmailInTeam.contains(relatedResponse.giver);
        
        boolean userIsInResponseRecipientTeamAndResponseCommentIsVisibleToRecipientsTeamMembers =
                isResponseCommentVisibleTo(relatedQuestion, comment,
                                           FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                && studentsEmailInTeam.contains(relatedResponse.recipient);
        
        return userIsInResponseRecipientTeamAndResponseCommentIsVisibleToRecipients
                || userIsInResponseGiverTeamAndResponseCommentIsVisibleToGiversTeamMembers
                || userIsInResponseRecipientTeamAndResponseCommentIsVisibleToRecipientsTeamMembers;
    }

    private FeedbackResponseCommentAttributes getExistingFeedbackResponseComment(
            FeedbackResponseCommentAttributes frComment) {
        FeedbackResponseCommentAttributes existingComment =
                frcDb.getFeedbackResponseComment(frComment.feedbackResponseId, frComment.giverEmail,
                                                 frComment.createdAt);
        if (existingComment == null) {
            // less robust method of retrieving a comment if the first method fails
            existingComment = frcDb.getFeedbackResponseComment(frComment.courseId, frComment.createdAt,
                                                               frComment.giverEmail);
        }
        return existingComment;
    }

    private boolean isResponseCommentVisibleTo(FeedbackQuestionAttributes relatedQuestion,
                                               FeedbackResponseCommentAttributes comment,
                                               FeedbackParticipantType viewerType) {
        boolean isVisibilityFollowingFeedbackQuestion = comment.isVisibilityFollowingFeedbackQuestion;
        boolean isVisibleTo = isVisibilityFollowingFeedbackQuestion
                              ? relatedQuestion.isResponseVisibleTo(viewerType)
                              : comment.isVisibleTo(viewerType);
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
                                                  + " is not a session for course " + courseId + ".");
        }
    }

    @SuppressWarnings("deprecation")
    public List<FeedbackResponseCommentAttributes> getAllFeedbackResponseComments() {
        return frcDb.getAllFeedbackResponseComments();
    }
}
