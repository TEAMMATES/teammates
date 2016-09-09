package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Utils;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.entity.FeedbackResponse;

public class FeedbackResponsesLogic {

    private static final Logger log = Utils.getLogger();

    private static FeedbackResponsesLogic instance;
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();

    public static FeedbackResponsesLogic inst() {
        if (instance == null) {
            instance = new FeedbackResponsesLogic();
        }
        return instance;
    }

    public void createFeedbackResponse(FeedbackResponseAttributes fra)
            throws InvalidParametersException, EntityDoesNotExistException {
        try {
            frDb.createEntity(fra);
        } catch (EntityAlreadyExistsException eaee) {
            try {
                updateFeedbackResponse(fra, (FeedbackResponse) eaee.existingEntity);
            } catch (EntityAlreadyExistsException entityAlreadyExistsException) {
                Assumption.fail();
            }
        }
    }

    public FeedbackResponseAttributes getFeedbackResponse(
            String feedbackResponseId) {
        return frDb.getFeedbackResponse(feedbackResponseId);
    }

    public FeedbackResponseAttributes getFeedbackResponse(
            String feedbackQuestionId, String giverEmail, String recipient) {
        return frDb.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
    }
    
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSession(
            String feedbackSessionName, String courseId) {
        return frDb.getFeedbackResponsesForSession(feedbackSessionName, courseId);
    }
    
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSection(
            String feedbackSessionName, String courseId, String section) {
        if (section == null) {
            return getFeedbackResponsesForSession(feedbackSessionName, courseId);
        }
        return frDb.getFeedbackResponsesForSessionInSection(feedbackSessionName, courseId, section);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSection(
            String feedbackSessionName, String courseId, String section) {
        if (section == null) {
            return getFeedbackResponsesForSession(feedbackSessionName, courseId);
        }
        return frDb.getFeedbackResponsesForSessionFromSection(feedbackSessionName, courseId, section);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSection(
            String feedbackSessionName, String courseId, String section) {
        if (section == null) {
            return getFeedbackResponsesForSession(feedbackSessionName, courseId);
        }
        return frDb.getFeedbackResponsesForSessionToSection(feedbackSessionName, courseId, section);
    }
    
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionWithinRange(
            String feedbackSessionName, String courseId, long range) {
        return frDb.getFeedbackResponsesForSessionWithinRange(feedbackSessionName, courseId, range);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {
        if (section == null) {
            return getFeedbackResponsesForSessionWithinRange(feedbackSessionName, courseId, range);
        }
        return frDb.getFeedbackResponsesForSessionInSectionWithinRange(feedbackSessionName, courseId, section, range);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {
        if (section == null) {
            return getFeedbackResponsesForSessionWithinRange(feedbackSessionName, courseId, range);
        }
        return frDb.getFeedbackResponsesForSessionFromSectionWithinRange(feedbackSessionName, courseId, section, range);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {
        if (section == null) {
            return getFeedbackResponsesForSessionWithinRange(feedbackSessionName, courseId, range);
        }
        return frDb.getFeedbackResponsesForSessionToSectionWithinRange(feedbackSessionName, courseId, section, range);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(String feedbackQuestionId) {
        return frDb.getFeedbackResponsesForQuestion(feedbackQuestionId);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionWithinRange(
            String feedbackQuestionId, long range) {
        return frDb.getFeedbackResponsesForQuestionWithinRange(feedbackQuestionId, range);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionInSection(
            String feedbackQuestionId, String section) {
        if (section == null) {
            return getFeedbackResponsesForQuestion(feedbackQuestionId);
        }
        return frDb.getFeedbackResponsesForQuestionInSection(feedbackQuestionId, section);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestion(
            String feedbackQuestionId, String userEmail) {
        return frDb.getFeedbackResponsesForReceiverForQuestion(feedbackQuestionId, userEmail);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestionInSection(
            String feedbackQuestionId, String userEmail, String section) {
        
        if (section == null) {
            return getFeedbackResponsesForReceiverForQuestion(feedbackQuestionId, userEmail);
        }
        return frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                    feedbackQuestionId, userEmail, section);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestion(
            String feedbackQuestionId, String userEmail) {
        return frDb.getFeedbackResponsesFromGiverForQuestion(feedbackQuestionId, userEmail);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestionInSection(
            String feedbackQuestionId, String userEmail, String section) {
        
        if (section == null) {
            return getFeedbackResponsesFromGiverForQuestion(feedbackQuestionId, userEmail);
        }
        return frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                    feedbackQuestionId, userEmail, section);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForSessionWithinRange(
            String giverEmail, String feedbackSessionName, String courseId, long range) {
        return frDb.getFeedbackResponsesFromGiverForSessionWithinRange(giverEmail, feedbackSessionName, courseId, range);
    }

    public boolean hasGiverRespondedForSession(String userEmail, String feedbackSessionName, String courseId) {

        return !getFeedbackResponsesFromGiverForSessionWithinRange(userEmail, feedbackSessionName, courseId, 1).isEmpty();
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForCourse(
            String courseId, String userEmail) {
        return frDb.getFeedbackResponsesForReceiverForCourse(courseId, userEmail);
    }

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
        if (question.giverType == FeedbackParticipantType.TEAMS) {
            return getFeedbackResponsesFromTeamForQuestion(
                    question.getId(), question.courseId, student.team);
        }
        return frDb.getFeedbackResponsesFromGiverForQuestion(question.getId(), student.email);
    }

    /**
     * Gets all viewable responses for the feedback question.
     * Does not take instructor privileges into account.
     */
    public List<FeedbackResponseAttributes> getViewableFeedbackResponsesForQuestionInSection(
            FeedbackQuestionAttributes question, String userEmail, UserType.Role role, String section) {

        List<FeedbackResponseAttributes> viewableResponses =
                new ArrayList<FeedbackResponseAttributes>();

        // Add responses that the user submitted himself
        addNewResponses(viewableResponses,
                        getFeedbackResponsesFromGiverForQuestionInSection(question.getId(), userEmail, section));

        // Add responses that user is a receiver of when question is visible to receiver.
        if (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            addNewResponses(viewableResponses,
                            getFeedbackResponsesForReceiverForQuestionInSection(question.getId(), userEmail, section));
        }

        switch (role) {
        case STUDENT:
            // many queries
            addNewResponses(viewableResponses,
                            getViewableFeedbackResponsesForStudentForQuestion(question, userEmail));
            break;
        case INSTRUCTOR:
            if (question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)) {
                addNewResponses(viewableResponses,
                                getFeedbackResponsesForQuestionInSection(question.getId(), section));
            }
            break;
        default:
            Assumption.fail("The role of the requesting use has to be Student or Instructor");
            break;
        }

        return viewableResponses;
    }
    
    /**
     * Checks if the name is visible to the given user.
     * If {@code isGiverName} is false, it checks for recipient name instead.
     */
    public boolean isNameVisibleTo(FeedbackQuestionAttributes question, FeedbackResponseAttributes response,
                                   String userEmail, UserType.Role role, boolean isGiverName, CourseRoster roster) {

        if (question == null) {
            return false;
        }
        
        // Early return if user is giver or if user is part of giver team
        if (isUserGiverOfResponse(question, response, userEmail, roster)) {
            return true;
        }
        
        List<FeedbackParticipantType> showNameTo = isGiverName
                                                   ? question.showGiverNameTo
                                                   : question.showRecipientNameTo;
        return doesVisibilityRecipientTypeMatchUserDetails(question, response, userEmail, role, roster,
                                                           showNameTo);
    }

    /**
     * Checks if the user matches any of the feedback participant types for the given response
     */
    private boolean doesVisibilityRecipientTypeMatchUserDetails(FeedbackQuestionAttributes question,
            FeedbackResponseAttributes response, String userEmail, UserType.Role role, CourseRoster roster,
            List<FeedbackParticipantType> showNameTo) {
        for (FeedbackParticipantType type : showNameTo) {
            switch (type) {
            case INSTRUCTORS:
                if (roster.getInstructorForEmail(userEmail) != null && role == UserType.Role.INSTRUCTOR) {
                    return true;
                }
                break;
            case OWN_TEAM_MEMBERS:
            case OWN_TEAM_MEMBERS_INCLUDING_SELF:
                // Refers to Giver's Team Members
                if (roster.isStudentsInSameTeam(response.giver, userEmail)) {
                    return true;
                }
                break;
            case RECEIVER:
                // Response to team; recipient is a team name
                if (question.recipientType.isTeam()) {
                    if (roster.isStudentInTeam(userEmail, response.recipient)) {
                        return true;
                    }
                    break;
                    // Response to individual
                } else if (response.recipient.equals(userEmail)) {
                    return true;
                }
                break;
            case RECEIVER_TEAM_MEMBERS:
                // Response to team; recipient is a team name
                if (question.recipientType.isTeam()) {
                    if (roster.isStudentInTeam(userEmail, response.recipient)) {
                        return true;
                    }
                    break;
                } else if (roster.isStudentsInSameTeam(response.recipient, userEmail)) {
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
                Assumption.fail("Invalid FeedbackPariticipantType for showNameTo in "
                                + "FeedbackResponseLogic.isNameVisible()");
                break;
            }
        }
        return false;
    }

    /**
     * Checks if the user gave the response, or is part of the team that gave the response.
     */
    private boolean isUserGiverOfResponse(FeedbackQuestionAttributes question,
            FeedbackResponseAttributes response, String userEmail, CourseRoster roster) {
        return question.giverType == FeedbackParticipantType.TEAMS && roster.isStudentsInSameTeam(response.giver, userEmail)
                || question.giverType != FeedbackParticipantType.TEAMS && response.giver.equals(userEmail);
    }
    
    /**
     * Return true if the responses of the question are visible to students
     * @param question
     */
    public boolean isResponseOfFeedbackQuestionVisibleToStudent(FeedbackQuestionAttributes question) {
        if (question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
            return true;
        }
        boolean isStudentRecipientType =
                   question.recipientType.equals(FeedbackParticipantType.STUDENTS)
                || question.recipientType.equals(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                || question.recipientType.equals(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF)
                || question.recipientType.equals(FeedbackParticipantType.GIVER)
                   && question.giverType.equals(FeedbackParticipantType.STUDENTS);
                                        
        if (isStudentRecipientType && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            return true;
        }
        if (question.recipientType.isTeam() && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            return true;
        }
        if (question.giverType == FeedbackParticipantType.TEAMS
                || question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            return true;
        }
        return question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
    }
    
    public boolean isCourseHasResponses(String courseId) {
        return frDb.hasFeedbackResponseEntitiesForCourse(courseId);
    }

    /**
     * Updates a {@link FeedbackResponse} based on it's {@code id}.<br>
     * If the giver/recipient field is changed, the {@link FeedbackResponse} is
     * updated by recreating the response<br>
     * in order to prevent an id clash if the previous email is reused later on.
     */
    public void updateFeedbackResponse(FeedbackResponseAttributes responseToUpdate)
            throws InvalidParametersException, EntityDoesNotExistException,
            EntityAlreadyExistsException {

        // Create a copy.
        FeedbackResponseAttributes newResponse = new FeedbackResponseAttributes(responseToUpdate);
        FeedbackResponse oldResponseEntity = null;
        if (newResponse.getId() == null) {
            oldResponseEntity = frDb.getFeedbackResponseEntityWithCheck(newResponse.feedbackQuestionId,
                    newResponse.giver, newResponse.recipient);
        } else {
            oldResponseEntity = frDb.getFeedbackResponseEntityWithCheck(newResponse.getId());
        }

        FeedbackResponseAttributes oldResponse = null;
        if (oldResponseEntity != null) {
            oldResponse = new FeedbackResponseAttributes(oldResponseEntity);
        }
        
        if (oldResponse == null) {
            throw new EntityDoesNotExistException(
                    "Trying to update a feedback response that does not exist.");
        }

        updateFeedbackResponse(newResponse, oldResponseEntity);
    }

    /**
     * Updates a {@link FeedbackResponse} using a {@link FeedbackResponseAttributes} <br>
     * If the giver/recipient field is changed, the {@link FeedbackResponse} is
     * updated by recreating the response<br>
     * in order to prevent an id clash if the previous email is reused later on.
     * @param updatedResponse
     * @param oldResponseEntity  a FeedbackResponse retrieved from the database
     * @throws EntityAlreadyExistsException  if when recreating a response to avoid an id clash,
     *                                       a response with the same id as the recreated response already exists.
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     */
    public void updateFeedbackResponse(
            FeedbackResponseAttributes updatedResponse, FeedbackResponse oldResponseEntity)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        Assumption.assertNotNull(oldResponseEntity);
        
        // Create a copy.
        FeedbackResponseAttributes newResponse = new FeedbackResponseAttributes(updatedResponse);
        
        FeedbackResponseAttributes oldResponse = new FeedbackResponseAttributes(oldResponseEntity);

        copyResponseFields(newResponse, oldResponse);
    
        if (newResponse.recipient.equals(oldResponse.recipient)
                && newResponse.giver.equals(oldResponse.giver)) {
            try {
                frDb.updateFeedbackResponseOptimized(newResponse, oldResponseEntity);
            } catch (EntityDoesNotExistException e) {
                Assumption.fail();
            }
        } else {
            // Recreate response to prevent possible future id conflict.
            recreateResponse(newResponse, oldResponse);
        }
    }

    /**
     * Copies information from {@code oldResponse} to {@code newResponse}
     */
    private void copyResponseFields(FeedbackResponseAttributes newResponse,
            FeedbackResponseAttributes oldResponse) {
        // Copy values that cannot be changed to defensively avoid invalid
        // parameters.
        newResponse.courseId = oldResponse.courseId;
        newResponse.feedbackSessionName = oldResponse.feedbackSessionName;
        newResponse.feedbackQuestionId = oldResponse.feedbackQuestionId;
        newResponse.feedbackQuestionType = oldResponse.feedbackQuestionType;
        
        if (newResponse.responseMetaData == null) {
            newResponse.responseMetaData = oldResponse.responseMetaData;
        }
        if (newResponse.giver == null) {
            newResponse.giver = oldResponse.giver;
        }
        if (newResponse.recipient == null) {
            newResponse.recipient = oldResponse.recipient;
        }
        if (newResponse.giverSection == null) {
            newResponse.giverSection = oldResponse.giverSection;
        }
        if (newResponse.recipientSection == null) {
            newResponse.recipientSection = oldResponse.recipientSection;
        }
    }

    private void recreateResponse(
            FeedbackResponseAttributes newResponse, FeedbackResponseAttributes oldResponse)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        try {
            newResponse.setId(null);
            FeedbackResponse createdResponseEntity =
                    (FeedbackResponse) frDb.createEntity(newResponse);
            frDb.deleteEntity(oldResponse);
            frcLogic.updateFeedbackResponseCommentsForChangingResponseId(
                    oldResponse.getId(), createdResponseEntity.getId());
        } catch (EntityAlreadyExistsException e) {
            log.warning("Trying to update an existing response to one that already exists.");
            throw e;
        }
    }

    
    /**
     * Updates responses for a student when his team changes. This is done by
     * deleting responses that are no longer relevant to him in his new team.
     */
    public void updateFeedbackResponsesForChangingTeam(
            String courseId, String userEmail, String oldTeam) {
        deleteIrrelevantGivenResponses(courseId, userEmail);
        deleteIrrelevantReceivedResponses(courseId, userEmail);
        deleteResponsesForTeamIfTeamIsEmpty(courseId, oldTeam);
    }

    /**
     * Deletes any given responses that are no longer relevant when the user changes team.
     */
    private void deleteIrrelevantGivenResponses(String courseId, String userEmail) {
        List<FeedbackResponseAttributes> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesFromUser) {
            FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
            if (question.giverType == FeedbackParticipantType.TEAMS
                    || isRecipientTypeTeamMembers(question)) {
                frDb.deleteEntity(response);
            }
        }
    }

    /**
     * Deletes any received responses that are no longer relevant when the user changes team.
     */
    private void deleteIrrelevantReceivedResponses(String courseId, String userEmail) {
        List<FeedbackResponseAttributes> responsesToUser =
                getFeedbackResponsesForReceiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesToUser) {
            FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
            if (isRecipientTypeTeamMembers(question)) {
                frDb.deleteEntity(response);
            }
        }
    }

    /**
     * If the team is empty, delete the responses to the team
     */
    private void deleteResponsesForTeamIfTeamIsEmpty(String courseId, String team) {
        if (studentsLogic.getStudentsForTeam(team, courseId).isEmpty()) {
            List<FeedbackResponseAttributes> responsesToTeam =
                    getFeedbackResponsesForReceiverForCourse(courseId, team);
            for (FeedbackResponseAttributes response : responsesToTeam) {
                frDb.deleteEntity(response);
            }
        }
    }
    
    /**
     * Updates responses for a student when his section changes.
     */
    public void updateFeedbackResponsesForChangingSection(
            String courseId, String userEmail, String oldSection, String newSection)
            throws EntityDoesNotExistException, InvalidParametersException {
        updateSectionForGivenResponses(courseId, userEmail, newSection);
        updateSectionForReceivedResponses(courseId, userEmail, newSection);
    }

    /**
     * Updates responses that the student has given to the new section he/she is in.
     */
    private void updateSectionForGivenResponses(String courseId, String userEmail, String newSection)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseAttributes> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesFromUser) {
            response.giverSection = newSection;
            frDb.updateFeedbackResponse(response);
            frcLogic.updateSectionsForFeedbackResponseCommentsForResponse(response.getId());
        }
    }

    /**
     * Updates responses that the student has received to the new section he/she is in.
     */
    private void updateSectionForReceivedResponses(String courseId, String userEmail, String newSection)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseAttributes> responsesToUser =
                getFeedbackResponsesForReceiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesToUser) {
            response.recipientSection = newSection;
            frDb.updateFeedbackResponse(response);
            frcLogic.updateSectionsForFeedbackResponseCommentsForResponse(response.getId());
        }
    }

    /**
     * Updates a single feedback response by deleting it if the response is no longer relevant to the student
     * in his/her new team.
     */
    public boolean updateFeedbackResponseForChangingTeam(StudentEnrollDetails enrollment,
            FeedbackResponseAttributes response) throws InvalidParametersException, EntityDoesNotExistException {

        FeedbackQuestionAttributes question = fqLogic
                .getFeedbackQuestion(response.feedbackQuestionId);

        boolean isGiverSameForResponseAndEnrollment = response.giver
                .equals(enrollment.email);
        boolean isReceiverSameForResponseAndEnrollment = response.recipient
                .equals(enrollment.email);

        boolean shouldDeleteByChangeOfGiver = isGiverSameForResponseAndEnrollment
                                              && (question.giverType == FeedbackParticipantType.TEAMS
                                                  || isRecipientTypeTeamMembers(question));
        boolean shouldDeleteByChangeOfRecipient = isReceiverSameForResponseAndEnrollment
                                                  && isRecipientTypeTeamMembers(question);

        boolean shouldDeleteResponse = shouldDeleteByChangeOfGiver
                || shouldDeleteByChangeOfRecipient;

        if (shouldDeleteResponse) {
            frDb.deleteEntity(response);
            updateSessionResponseRateForIndividualUser(enrollment.email,
                    response.feedbackSessionName, enrollment.course, false);
        }
        
        return shouldDeleteResponse;
    }

    private boolean isRecipientTypeTeamMembers(FeedbackQuestionAttributes question) {
        return question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS
               || question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;
    }
    
    /**
     * Updates responses for a student if his/her section changes.
     * Currently does not support the case where a student's team and section changes at the same time.
     */
    public void updateFeedbackResponseForChangingSection(
            StudentEnrollDetails enrollment,
            FeedbackResponseAttributes response) throws InvalidParametersException, EntityDoesNotExistException {

        FeedbackResponse feedbackResponse = frDb.getFeedbackResponseEntityOptimized(response);
        boolean isGiverSameForResponseAndEnrollment = feedbackResponse.getGiverEmail()
                .equals(enrollment.email);
        boolean isReceiverSameForResponseAndEnrollment = feedbackResponse.getRecipientEmail()
                .equals(enrollment.email);
        
        if (isGiverSameForResponseAndEnrollment) {
            feedbackResponse.setGiverSection(enrollment.newSection);
        }
        
        if (isReceiverSameForResponseAndEnrollment) {
            feedbackResponse.setRecipientSection(enrollment.newSection);
        }
        
        frDb.commitOutstandingChanges();
        
        if (isGiverSameForResponseAndEnrollment || isReceiverSameForResponseAndEnrollment) {
            frcLogic.updateSectionsForFeedbackResponseCommentsForResponse(response.getId());
        }
    }

    /**
     * Updates responses for a student when his email changes.
     */
    // TODO: cascade the update to response comments
    public void updateFeedbackResponsesForChangingEmail(
            String courseId, String oldEmail, String newEmail)
            throws InvalidParametersException, EntityDoesNotExistException {

        List<FeedbackResponseAttributes> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(courseId, oldEmail);

        for (FeedbackResponseAttributes response : responsesFromUser) {
            response.giver = newEmail;
            try {
                updateFeedbackResponse(response);
            } catch (EntityAlreadyExistsException e) {
                Assumption.fail("Feedback response failed to update successfully "
                                + "as email was already in use.");
            }
        }

        List<FeedbackResponseAttributes> responsesToUser =
                getFeedbackResponsesForReceiverForCourse(courseId, oldEmail);

        for (FeedbackResponseAttributes response : responsesToUser) {
            response.recipient = newEmail;
            try {
                updateFeedbackResponse(response);
            } catch (EntityAlreadyExistsException e) {
                Assumption.fail("Feedback response failed to update successfully "
                                + "as email was already in use.");
            }
        }
    }

    /**
     * Deletes a response and cascades the deletion to feedback response comments.
     */
    public void deleteFeedbackResponseAndCascade(FeedbackResponseAttributes responseToDelete) {
        frcLogic.deleteFeedbackResponseCommentsForResponse(responseToDelete.getId());
        frDb.deleteEntity(responseToDelete);
    }

    /**
     * Deletes responses for the given question and cascades the deletion to feedback response comments.
     * Updates the response rate as well if {@code hasResponseRateUpdate} is set to true.
     */
    public void deleteFeedbackResponsesForQuestionAndCascade(
            String feedbackQuestionId, boolean hasResponseRateUpdate) {
        List<FeedbackResponseAttributes> responsesForQuestion =
                getFeedbackResponsesForQuestion(feedbackQuestionId);

        Set<String> emails = new HashSet<String>();

        for (FeedbackResponseAttributes response : responsesForQuestion) {
            deleteFeedbackResponseAndCascade(response);
            emails.add(response.giver);
        }

        if (hasResponseRateUpdate) {
            updateResponseRate(feedbackQuestionId, emails);
        }
    }

    private void updateResponseRate(String feedbackQuestionId, Set<String> emails) {
        try {
            FeedbackQuestionAttributes question = fqLogic
                    .getFeedbackQuestion(feedbackQuestionId);
            boolean isInstructor = question.giverType == FeedbackParticipantType.SELF
                                   || question.giverType == FeedbackParticipantType.INSTRUCTORS;
            for (String email : emails) {
                updateSessionResponseRateForIndividualUser(
                        email, question.feedbackSessionName, question.courseId, isInstructor);
            }
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            Assumption.fail("Fail to delete respondant");
        }
    }

    /**
     * If the user no longer has any responses for the session, delete him/her from the respondent list.
     */
    private void updateSessionResponseRateForIndividualUser(
            String userEmail, String sessionName, String courseId, boolean isInstructor)
            throws InvalidParametersException, EntityDoesNotExistException {
        boolean hasResponses = hasGiverRespondedForSession(userEmail, sessionName, courseId);
        if (!hasResponses) {
            if (isInstructor) {
                fsLogic.deleteInstructorRespondant(userEmail, sessionName, courseId);
            } else {
                fsLogic.deleteStudentFromRespondentList(userEmail, sessionName, courseId);
            }
        }
    }

    public void deleteFeedbackResponsesForStudentAndCascade(String courseId, String studentEmail) {

        String studentTeam = "";
        StudentAttributes student = studentsLogic.getStudentForEmail(courseId,
                studentEmail);

        if (student != null) {
            studentTeam = student.team;
        }

        List<FeedbackResponseAttributes> responses =
                getFeedbackResponsesFromGiverForCourse(courseId, studentEmail);
        responses.addAll(
                getFeedbackResponsesForReceiverForCourse(courseId, studentEmail));
        // Delete responses to team as well if student is last person in team.
        if (studentsLogic.getStudentsForTeam(studentTeam, courseId).size() <= 1) {
            responses.addAll(getFeedbackResponsesForReceiverForCourse(courseId, studentTeam));
        }

        for (FeedbackResponseAttributes response : responses) {
            this.deleteFeedbackResponseAndCascade(response);
        }
    }

    /**
     * Deletes all feedback responses in every feedback session in
     * the specified course. This is a non-cascade delete and the
     * feedback response comments are not deleted, and should be handled.
     */
    public void deleteFeedbackResponsesForCourse(String courseId) {
        frDb.deleteFeedbackResponsesForCourse(courseId);
    }

    /**
     * Adds {@link FeedbackResponseAttributes} in {@code newResponses} that are
     * not already in {@code existingResponses} to {@code existingResponses}.
     * Assumes all responses already have a unique id.
     */
    private void addNewResponses(
            List<FeedbackResponseAttributes> existingResponses,
            List<FeedbackResponseAttributes> newResponses) {

        Set<String> responses = new HashSet<String>();

        for (FeedbackResponseAttributes existingResponse : existingResponses) {
            responses.add(existingResponse.getId());
        }
        for (FeedbackResponseAttributes newResponse : newResponses) {
            if (!responses.contains(newResponse.getId())) {
                responses.add(newResponse.getId());
                existingResponses.add(newResponse);
            }
        }
    }

    /**
     * Gets a list of responses given by all team members and the team itself.
     */
    private List<FeedbackResponseAttributes> getFeedbackResponsesFromTeamForQuestion(
            String feedbackQuestionId, String courseId, String teamName) {

        List<FeedbackResponseAttributes> responses =
                new ArrayList<FeedbackResponseAttributes>();
        List<StudentAttributes> studentsInTeam =
                studentsLogic.getStudentsForTeam(teamName, courseId);

        for (StudentAttributes student : studentsInTeam) {
            responses.addAll(frDb.getFeedbackResponsesFromGiverForQuestion(
                    feedbackQuestionId, student.email));
        }
        
        responses.addAll(frDb.getFeedbackResponsesFromGiverForQuestion(
                                        feedbackQuestionId, teamName));

        return responses;
    }

    /**
     * Gets a list of responses given by the student's team members, excluding himself/herself.
     */
    private List<FeedbackResponseAttributes> getFeedbackResponsesForTeamMembersOfStudent(
            String feedbackQuestionId, StudentAttributes student) {
        
        List<StudentAttributes> studentsInTeam = studentsLogic.getStudentsForTeam(student.team, student.course);
       
        List<FeedbackResponseAttributes> teamResponses =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (StudentAttributes studentInTeam : studentsInTeam) {
            if (studentInTeam.email.equals(student.email)) {
                continue;
            }
            List<FeedbackResponseAttributes> responses =
                    frDb.getFeedbackResponsesForReceiverForQuestion(feedbackQuestionId, studentInTeam.email);
            teamResponses.addAll(responses);
        }
        
        return teamResponses;
    }

    /**
     * Gets all other responses that the student can see which does not involve him/her as a
     * giver or receiver directly.
     */
    private List<FeedbackResponseAttributes> getViewableFeedbackResponsesForStudentForQuestion(
            FeedbackQuestionAttributes question, String studentEmail) {

        List<FeedbackResponseAttributes> viewableResponses =
                new ArrayList<FeedbackResponseAttributes>();

        if (question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
            addNewResponses(viewableResponses,
                    getFeedbackResponsesForQuestion(question.getId()));

            // Early return as STUDENTS covers all other student types.
            return viewableResponses;
        }

        StudentAttributes student = studentsLogic.getStudentForEmail(question.courseId, studentEmail);
        if (question.recipientType.isTeam()
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            addNewResponses(
                    viewableResponses,
                    getFeedbackResponsesForReceiverForQuestion(
                            question.getId(), student.team));
        }

        if (question.giverType == FeedbackParticipantType.TEAMS
                || question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            addNewResponses(viewableResponses,
                    getFeedbackResponsesFromTeamForQuestion(
                            question.getId(), question.courseId, student.team));
        }
        if (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {
            addNewResponses(
                    viewableResponses,
                    getFeedbackResponsesForTeamMembersOfStudent(
                            question.getId(), student));
        }

        return viewableResponses;
    }
}
