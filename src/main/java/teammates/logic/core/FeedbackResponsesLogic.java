package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.UserRole;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Logger;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.entity.FeedbackResponse;

/**
 * Handles operations related to feedback responses.
 *
 * @see FeedbackResponseAttributes
 * @see FeedbackResponsesDb
 */
public final class FeedbackResponsesLogic {

    private static final Logger log = Logger.getLogger();

    private static FeedbackResponsesLogic instance = new FeedbackResponsesLogic();

    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();

    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    private FeedbackResponsesLogic() {
        // prevent initialization
    }

    public static FeedbackResponsesLogic inst() {
        return instance;
    }

    public void createFeedbackResponse(FeedbackResponseAttributes fra)
            throws InvalidParametersException, EntityDoesNotExistException {
        try {
            frDb.createEntity(fra);
        } catch (EntityAlreadyExistsException eaee) {
            FeedbackResponse existingResponse = frDb.getFeedbackResponseEntityOptimized(fra);
            try {
                updateFeedbackResponse(fra, existingResponse);
            } catch (EntityAlreadyExistsException entityAlreadyExistsException) {
                Assumption.fail();
            }
        }
    }

    public void createFeedbackResponses(List<FeedbackResponseAttributes> fra)
            throws InvalidParametersException {
        frDb.createEntities(fra);
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
            String feedbackSessionName, String courseId, int range) {
        return frDb.getFeedbackResponsesForSessionWithinRange(feedbackSessionName, courseId, range);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSectionWithinRange(
            String feedbackSessionName, String courseId, String section, int range) {
        if (section == null) {
            return getFeedbackResponsesForSessionWithinRange(feedbackSessionName, courseId, range);
        }
        return frDb.getFeedbackResponsesForSessionInSectionWithinRange(feedbackSessionName, courseId, section, range);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSectionWithinRange(
            String feedbackSessionName, String courseId, String section, int range) {
        if (section == null) {
            return getFeedbackResponsesForSessionWithinRange(feedbackSessionName, courseId, range);
        }
        return frDb.getFeedbackResponsesForSessionFromSectionWithinRange(feedbackSessionName, courseId, section, range);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSectionWithinRange(
            String feedbackSessionName, String courseId, String section, int range) {
        if (section == null) {
            return getFeedbackResponsesForSessionWithinRange(feedbackSessionName, courseId, range);
        }
        return frDb.getFeedbackResponsesForSessionToSectionWithinRange(feedbackSessionName, courseId, section, range);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(String feedbackQuestionId) {
        return frDb.getFeedbackResponsesForQuestion(feedbackQuestionId);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionWithinRange(
            String feedbackQuestionId, int range) {
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
            String giverEmail, String feedbackSessionName, String courseId, int range) {
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

    public List<FeedbackResponseAttributes> getViewableFeedbackResponsesForQuestionInSection(
            FeedbackQuestionAttributes question, String userEmail,
            UserRole role, String section) {

        List<FeedbackResponseAttributes> viewableResponses = new ArrayList<>();

        // Add responses that the user submitted himself
        addNewResponses(
                viewableResponses,
                getFeedbackResponsesFromGiverForQuestionInSection(
                        question.getId(), userEmail, section));

        // Add responses that user is a receiver of when question is visible to
        // receiver.
        if (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            addNewResponses(
                    viewableResponses,
                    getFeedbackResponsesForReceiverForQuestionInSection(
                            question.getId(), userEmail, section));
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

    public boolean isNameVisibleToUser(
            FeedbackQuestionAttributes question,
            FeedbackResponseAttributes response,
            String userEmail,
            UserRole role, boolean isGiverName, CourseRoster roster) {

        if (question == null) {
            return false;
        }

        // Early return if user is giver
        if (question.giverType == FeedbackParticipantType.TEAMS) {
            // if response is given by team, then anyone in the team can see the response
            if (roster.isStudentsInSameTeam(response.giver, userEmail)) {
                return true;
            }
        } else {
            if (response.giver.equals(userEmail)) {
                return true;
            }
        }

        return isFeedbackParticipantNameVisibleToUser(question, response,
                userEmail, role, isGiverName, roster);
    }

    private boolean isFeedbackParticipantNameVisibleToUser(
            FeedbackQuestionAttributes question, FeedbackResponseAttributes response,
            String userEmail, UserRole role, boolean isGiverName, CourseRoster roster) {
        List<FeedbackParticipantType> showNameTo = isGiverName
                                                 ? question.showGiverNameTo
                                                 : question.showRecipientNameTo;
        for (FeedbackParticipantType type : showNameTo) {
            switch (type) {
            case INSTRUCTORS:
                if (roster.getInstructorForEmail(userEmail) != null && role == UserRole.INSTRUCTOR) {
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
                // Response to team
                if (question.recipientType.isTeam()) {
                    if (roster.isStudentInTeam(userEmail, response.recipient)) {
                        // this is a team name
                        return true;
                    }
                    break;
                    // Response to individual
                } else if (response.recipient.equals(userEmail)) {
                    return true;
                } else {
                    break;
                }
            case RECEIVER_TEAM_MEMBERS:
                // Response to team; recipient = teamName
                if (question.recipientType.isTeam()) {
                    if (roster.isStudentInTeam(userEmail, response.recipient)) {
                        // this is a team name
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
                Assumption.fail("Invalid FeedbackParticipantType for showNameTo in "
                                + "FeedbackResponseLogic.isFeedbackParticipantNameVisibleToUser()");
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
                   question.recipientType.equals(FeedbackParticipantType.STUDENTS)
                || question.recipientType.equals(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                || question.recipientType.equals(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF)
                || question.recipientType.equals(FeedbackParticipantType.GIVER)
                   && question.giverType.equals(FeedbackParticipantType.STUDENTS);

        if ((isStudentRecipientType || question.recipientType.isTeam())
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            return true;
        }
        if (question.giverType == FeedbackParticipantType.TEAMS
                || question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            return true;
        }
        return question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
    }

    public boolean hasResponsesForCourse(String courseId) {
        return frDb.hasFeedbackResponseEntitiesForCourse(courseId);
    }

    /**
     * Updates a {@link FeedbackResponse} based on it's {@code id}.<br>
     * If the giver/recipient field is changed, the {@link FeedbackResponse} is
     * updated by recreating the response<br>
     * in order to prevent an id clash if the previous email is reused later on.
     */
    public void updateFeedbackResponse(
            FeedbackResponseAttributes responseToUpdate)
            throws InvalidParametersException, EntityDoesNotExistException,
            EntityAlreadyExistsException {

        // Create a copy.
        FeedbackResponseAttributes newResponse = new FeedbackResponseAttributes(
                responseToUpdate);
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
     * @param oldResponseEntity  a FeedbackResponse retrieved from the database
     * @throws EntityAlreadyExistsException  if trying to prevent an id clash by recreating a response,
     *                                       a response with the same id already exist.
     */
    public void updateFeedbackResponse(
            FeedbackResponseAttributes updatedResponse, FeedbackResponse oldResponseEntity)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        Assumption.assertNotNull(oldResponseEntity);

        // Create a copy.
        FeedbackResponseAttributes newResponse = new FeedbackResponseAttributes(updatedResponse);
        FeedbackResponseAttributes oldResponse = new FeedbackResponseAttributes(oldResponseEntity);

        // Copy values that cannot be changed to defensively avoid invalid
        // parameters.
        copyFixedValuesFromOldToNew(newResponse, oldResponse);

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
     * Copies values that cannot be changed to defensively avoid invalid parameters.
     * @param newResponse  values are copied from oldResponse
     * @param oldResponse  values are copied to newResponse
     */
    private void copyFixedValuesFromOldToNew(FeedbackResponseAttributes newResponse,
            FeedbackResponseAttributes oldResponse) {
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
                    frDb.createEntity(newResponse);
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
            String courseId, String userEmail, String oldTeam, String newTeam) {

        deleteResponsesFromUserToTeam(courseId, userEmail);
        deleteResponsesFromTeamToUser(courseId, userEmail);

        boolean isOldTeamEmpty = studentsLogic.getStudentsForTeam(oldTeam, courseId).isEmpty();
        if (isOldTeamEmpty) {
            deleteTeamResponses(courseId, oldTeam);
        }
    }

    private void deleteTeamResponses(String courseId, String oldTeam) {
        List<FeedbackResponseAttributes> responsesToOldTeam =
                getFeedbackResponsesForReceiverForCourse(courseId, oldTeam);
        for (FeedbackResponseAttributes response : responsesToOldTeam) {
            frDb.deleteEntity(response);
        }
    }

    private void deleteResponsesFromTeamToUser(String courseId, String userEmail) {
        FeedbackQuestionAttributes question;
        List<FeedbackResponseAttributes> responsesToUser =
                getFeedbackResponsesForReceiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesToUser) {
            question = fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
            if (isRecipientTypeTeamMembers(question)) {
                frDb.deleteEntity(response);
            }
        }
    }

    private void deleteResponsesFromUserToTeam(String courseId, String userEmail) {
        FeedbackQuestionAttributes question;

        List<FeedbackResponseAttributes> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesFromUser) {
            question = fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
            if (question.giverType == FeedbackParticipantType.TEAMS
                    || isRecipientTypeTeamMembers(question)) {
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
        updateSectionOfResponsesFromUser(courseId, userEmail, newSection);
        updateSectionOfResponsesToUser(courseId, userEmail, newSection);
    }

    private void updateSectionOfResponsesToUser(String courseId, String userEmail, String newSection)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseAttributes> responsesToUser =
                getFeedbackResponsesForReceiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesToUser) {
            response.recipientSection = newSection;
            frDb.updateFeedbackResponse(response);
            frcLogic.updateFeedbackResponseCommentsForResponse(response.getId());
        }
    }

    private void updateSectionOfResponsesFromUser(String courseId, String userEmail, String newSection)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseAttributes> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesFromUser) {
            response.giverSection = newSection;
            frDb.updateFeedbackResponse(response);
            frcLogic.updateFeedbackResponseCommentsForResponse(response.getId());
        }
    }

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
            updateSessionResponseRateForDeletingStudentResponse(enrollment.email,
                    response.feedbackSessionName, enrollment.course);
        }

        return shouldDeleteResponse;
    }

    private void updateSessionResponseRateForDeletingStudentResponse(String studentEmail, String sessionName,
            String courseId) throws InvalidParametersException, EntityDoesNotExistException {
        if (!hasGiverRespondedForSession(studentEmail, sessionName, courseId)) {
            fsLogic.deleteStudentFromRespondentList(studentEmail, sessionName, courseId);
        }
    }

    private boolean isRecipientTypeTeamMembers(FeedbackQuestionAttributes question) {
        return question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS
               || question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;
    }

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

        frDb.saveEntity(feedbackResponse);

        if (isGiverSameForResponseAndEnrollment || isReceiverSameForResponseAndEnrollment) {
            frcLogic.updateFeedbackResponseCommentsForResponse(response.getId());
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
                Assumption
                        .fail("Feedback response failed to update successfully"
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
                Assumption
                        .fail("Feedback response failed to update successfully"
                            + "as email was already in use.");
            }
        }
    }

    public void deleteFeedbackResponseAndCascade(FeedbackResponseAttributes responseToDelete) {
        frcLogic.deleteFeedbackResponseCommentsForResponse(responseToDelete.getId());
        frDb.deleteEntity(responseToDelete);
    }

    public void deleteFeedbackResponsesForQuestionAndCascade(
            String feedbackQuestionId, boolean hasResponseRateUpdate) {
        List<FeedbackResponseAttributes> responsesForQuestion =
                getFeedbackResponsesForQuestion(feedbackQuestionId);

        Set<String> emails = new HashSet<>();

        for (FeedbackResponseAttributes response : responsesForQuestion) {
            this.deleteFeedbackResponseAndCascade(response);
            emails.add(response.giver);
        }

        if (!hasResponseRateUpdate) {
            return;
        }

        try {
            FeedbackQuestionAttributes question = fqLogic
                    .getFeedbackQuestion(feedbackQuestionId);
            boolean isInstructor = question.giverType == FeedbackParticipantType.SELF
                                   || question.giverType == FeedbackParticipantType.INSTRUCTORS;
            for (String email : emails) {
                boolean hasResponses = hasGiverRespondedForSession(email, question.feedbackSessionName, question.courseId);
                if (!hasResponses) {
                    if (isInstructor) {
                        fsLogic.deleteInstructorRespondent(email,
                                question.feedbackSessionName,
                                question.courseId);
                    } else {
                        fsLogic.deleteStudentFromRespondentList(email,
                                question.feedbackSessionName,
                                question.courseId);
                    }
                }
            }
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            Assumption.fail("Fail to delete respondent");
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
        responses
                .addAll(
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
     * not already in to {@code existingResponses} to {@code existingResponses}.
     */
    private void addNewResponses(
            List<FeedbackResponseAttributes> existingResponses,
            List<FeedbackResponseAttributes> newResponses) {

        Map<String, FeedbackResponseAttributes> responses = new HashMap<>();

        for (FeedbackResponseAttributes existingResponse : existingResponses) {
            responses.put(existingResponse.getId(), existingResponse);
        }
        for (FeedbackResponseAttributes newResponse : newResponses) {
            responses.computeIfAbsent(newResponse.getId(), key -> {
                existingResponses.add(newResponse);
                return newResponse;
            });
        }
    }

    private List<FeedbackResponseAttributes> getFeedbackResponsesFromTeamForQuestion(
            String feedbackQuestionId, String courseId, String teamName) {

        List<FeedbackResponseAttributes> responses = new ArrayList<>();
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

    private List<FeedbackResponseAttributes> getFeedbackResponsesForTeamMembersOfStudent(
            String feedbackQuestionId, StudentAttributes student) {

        List<StudentAttributes> studentsInTeam = studentsLogic.getStudentsForTeam(student.team, student.course);

        List<FeedbackResponseAttributes> teamResponses = new ArrayList<>();

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

    private List<FeedbackResponseAttributes> getViewableFeedbackResponsesForStudentForQuestion(
            FeedbackQuestionAttributes question, String studentEmail) {

        List<FeedbackResponseAttributes> viewableResponses = new ArrayList<>();

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
        if (question
                .isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {
            addNewResponses(
                    viewableResponses,
                    getFeedbackResponsesForTeamMembersOfStudent(
                            question.getId(), student));
        }

        return viewableResponses;
    }
}
