package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.api.FeedbackResponsesDb;

public class FeedbackResponsesLogic {

    private static final Logger log = Utils.getLogger();

    private static FeedbackResponsesLogic instance = null;
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic
            .inst();
    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();

    public static FeedbackResponsesLogic inst() {
        if (instance == null)
            instance = new FeedbackResponsesLogic();
        return instance;
    }

    public void createFeedbackResponse(FeedbackResponseAttributes fra) throws InvalidParametersException {
        try {
            frDb.createEntity(fra);
        } catch (Exception EntityAlreadyExistsException) {
            try {
                FeedbackResponseAttributes existingFeedback = new FeedbackResponseAttributes();

                existingFeedback = frDb.getFeedbackResponse(
                        fra.feedbackQuestionId, fra.giverEmail,
                        fra.recipientEmail);
                fra.setId(existingFeedback.getId());

                frDb.updateFeedbackResponse(fra);
            } catch (Exception EntityDoesNotExistException) {
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
        // TODO: check what is this line doing here!!!
        log.warning(feedbackQuestionId);
        return frDb.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSession(
            String feedbackSessionName, String courseId) {
        return frDb.getFeedbackResponsesForSession(feedbackSessionName, courseId);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSection(
            String feedbackSessionName, String courseId, String section){
        if(section == null){
            return getFeedbackResponsesForSession(feedbackSessionName, courseId);
        } else {
            return frDb.getFeedbackResponsesForSessionInSection(feedbackSessionName, courseId, section);
        }
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSection(
            String feedbackSessionName, String courseId, String section){
        if(section == null){
            return getFeedbackResponsesForSession(feedbackSessionName, courseId);
        } else {
            return frDb.getFeedbackResponsesForSessionFromSection(feedbackSessionName, courseId, section);
        }
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSection(
            String feedbackSessionName, String courseId, String section){
        if(section == null){
            return getFeedbackResponsesForSession(feedbackSessionName, courseId);
        } else {
            return frDb.getFeedbackResponsesForSessionToSection(feedbackSessionName, courseId, section);
        }
    }
    
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionWithinRange(
            String feedbackSessionName, String courseId, long range) {
        return frDb.getFeedbackResponsesForSessionWithinRange(
                feedbackSessionName, courseId, range);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSectionWithinRange(
            String feedbackSessionName, String courseId, String section,
            long range) {
        if (section == null) {
            return getFeedbackResponsesForSessionWithinRange(
                    feedbackSessionName, courseId, range);
        } else {
            return frDb.getFeedbackResponsesForSessionInSectionWithinRange(
                    feedbackSessionName, courseId, section, range);
        }
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSectionWithinRange(
            String feedbackSessionName, String courseId, String section,
            long range) {
        if(section == null) {
            return getFeedbackResponsesForSessionWithinRange(
                    feedbackSessionName, courseId, range);
        } else {
            return frDb.getFeedbackResponsesForSessionFromSectionWithinRange(
                    feedbackSessionName, courseId, section, range);
        }
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSectionWithinRange(
            String feedbackSessionName, String courseId, String section,
            long range) {
        if(section == null) {
            return getFeedbackResponsesForSessionWithinRange(
                    feedbackSessionName, courseId, range);
        } else {
            return frDb.getFeedbackResponsesForSessionToSectionWithinRange(
                    feedbackSessionName, courseId, section, range);
        }
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(String feedbackQuestionId) {
        return frDb.getFeedbackResponsesForQuestion(feedbackQuestionId);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionWithinRange(String feedbackQuestionId, long range) {
        return frDb.getFeedbackResponsesForQuestionWithinRange(feedbackQuestionId, range);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionInSection(
            String feedbackQuestionId, String section) {
        if(section == null){
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
        
        if(section == null){
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
        
        if(section == null){
            return getFeedbackResponsesFromGiverForQuestion(feedbackQuestionId, userEmail);
        }
        return frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                    feedbackQuestionId, userEmail, section);
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
        } else {
            return frDb.getFeedbackResponsesFromGiverForQuestion(question.getId(), student.email);
        }
    }

    public List<FeedbackResponseAttributes> getViewableFeedbackResponsesForQuestionInSection(
            FeedbackQuestionAttributes question, String userEmail,
            UserType.Role role, String section)
            throws EntityDoesNotExistException {

        List<FeedbackResponseAttributes> viewableResponses =
                new ArrayList<FeedbackResponseAttributes>();

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
            addNewResponses(
                    viewableResponses,
                    // many queries
                    getViewableFeedbackResponsesForStudentForQuestion(question,
                            userEmail));
            break;
        case INSTRUCTOR:
            if (question
                    .isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)) {
                addNewResponses(
                        viewableResponses,
                        getFeedbackResponsesForQuestionInSection(
                                question.getId(), section));
            }
            break;
        default:
            Assumption
                    .fail("The role of the requesting use has to be Student or Instructor");
        }

        return viewableResponses;
    }

    public boolean isNameVisibleTo(
            FeedbackQuestionAttributes question,
            FeedbackResponseAttributes response,
            String userEmail,
            UserType.Role role, boolean isGiverName, CourseRoster roster) {

        if (question == null) {
            return false;
        }

        List<FeedbackParticipantType> showNameTo =
                isGiverName ? question.showGiverNameTo
                        : question.showRecipientNameTo;
        
        //Giver can always see giver and recipient.(because he answered.)
        if(response.giverEmail.equals(userEmail)){
            return true;
        }
        
        for (FeedbackParticipantType type : showNameTo) {
            switch (type) {
            case INSTRUCTORS:
                if (roster.getInstructorForEmail(userEmail) != null && role == UserType.Role.INSTRUCTOR) {
                    return true;
                } else {
                    break;
                }
            case OWN_TEAM_MEMBERS:
            case OWN_TEAM_MEMBERS_INCLUDING_SELF:
                // Refers to Giver's Team Members
                if (roster.isStudentsInSameTeam(response.giverEmail, userEmail)) {
                    return true;
                } else {
                    break;
                }
            case RECEIVER:
                // Response to team
                if (question.recipientType == FeedbackParticipantType.TEAMS) {
                    if (roster.isStudentInTeam(userEmail, /* this is a team name */
                            response.recipientEmail)) {
                        return true;
                    }
                    // Response to individual
                } else if (response.recipientEmail.equals(userEmail)) {
                    return true;
                } else {
                    break;
                }
            case RECEIVER_TEAM_MEMBERS:
                // Response to team; recipient = teamName
                if (question.recipientType == FeedbackParticipantType.TEAMS) {
                    if (roster.isStudentInTeam(userEmail, /* this is a team name */
                            response.recipientEmail)) {
                        return true;
                    }
                    // Response to individual
                } else if (roster.isStudentsInSameTeam(response.recipientEmail,
                        userEmail)) {
                    return true;
                } else {
                    break;
                }
            case STUDENTS:
                if (roster.isStudentInCourse(userEmail)) {
                    return true;
                } else {
                    break;
                }
            default:
                Assumption.fail("Invalid FeedbackPariticipantType for showNameTo in "
                                + "FeedbackResponseLogic.isNameVisible()");
                break;
            }
        }
        return false;
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
        FeedbackResponseAttributes oldResponse = null;
        if (newResponse.getId() == null) {
            oldResponse = frDb.getFeedbackResponse(newResponse.feedbackQuestionId, 
                    newResponse.giverEmail, newResponse.recipientEmail);
        } else {
            oldResponse = frDb.getFeedbackResponse(newResponse.getId());
        }

        if (oldResponse == null) {
            throw new EntityDoesNotExistException(
                    "Trying to update a feedback response that does not exist.");
        }

        // Copy values that cannot be changed to defensively avoid invalid
        // parameters.
        newResponse.courseId = oldResponse.courseId;
        newResponse.feedbackSessionName = oldResponse.feedbackSessionName;
        newResponse.feedbackQuestionId = oldResponse.feedbackQuestionId;
        newResponse.feedbackQuestionType = oldResponse.feedbackQuestionType;

        if (newResponse.responseMetaData == null) {
            newResponse.responseMetaData = oldResponse.responseMetaData;
        }
        if (newResponse.giverEmail == null) {
            newResponse.giverEmail = oldResponse.giverEmail;
        }
        if (newResponse.recipientEmail == null) {
            newResponse.recipientEmail = oldResponse.recipientEmail;
        }
        if (newResponse.giverSection == null) {
            newResponse.giverSection = oldResponse.giverSection;
        }
        if (newResponse.recipientSection == null) {
            newResponse.recipientSection = oldResponse.recipientSection;
        }

        if (!newResponse.recipientEmail.equals(oldResponse.recipientEmail) ||
                !newResponse.giverEmail.equals(oldResponse.giverEmail)) {
            // Recreate response to prevent possible future id conflict.
            try {
                newResponse.setId(null);
                frDb.createEntity(newResponse);
                frDb.deleteEntity(oldResponse);
            } catch (EntityAlreadyExistsException e) {
                log.warning("Trying to update an existing response to one that already exists.");
                throw new EntityAlreadyExistsException(e.getMessage() + Const.EOL
                            + "Trying to update recipient for response to one that already exists for this giver.");
            }
        } else {
            frDb.updateFeedbackResponse(newResponse);
        }
    }

    /**
     * Updates responses for a student when his team changes. This is done by
     * deleting responses that are no longer relevant to him in his new team.
     */
    public void updateFeedbackResponsesForChangingTeam(
            String courseId, String userEmail, String oldTeam, String newTeam)
            throws EntityDoesNotExistException {

        FeedbackQuestionAttributes question;

        List<FeedbackResponseAttributes> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesFromUser) {
            question = fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
            if (question.giverType == FeedbackParticipantType.TEAMS
                    || question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS
                    || question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF) {
                frDb.deleteEntity(response);
            }
        }

        List<FeedbackResponseAttributes> responsesToUser =
                getFeedbackResponsesForReceiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesToUser) {
            question = fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
            if (question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS
                    || question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF ) {
                frDb.deleteEntity(response);
            }
        }

        if (studentsLogic.getStudentsForTeam(oldTeam, courseId).isEmpty()) {
            List<FeedbackResponseAttributes> responsesToTeam =
                    getFeedbackResponsesForReceiverForCourse(courseId, oldTeam);
            for (FeedbackResponseAttributes response : responsesToTeam) {
                frDb.deleteEntity(response);
            }
        }
    }

    public void updateFeedbackResponsesForChangingSection(
            String courseId, String userEmail, String oldSection, String newSection)
            throws EntityDoesNotExistException, InvalidParametersException {

        List<FeedbackResponseAttributes> responsesFromUser =
                getFeedbackResponsesFromGiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesFromUser) {
            response.giverSection = newSection;
            frDb.updateFeedbackResponse(response);
            frcLogic.updateFeedbackResponseCommentsForResponse(response.getId());
        }

        List<FeedbackResponseAttributes> responsesToUser =
                getFeedbackResponsesForReceiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesToUser) {
            response.recipientSection = newSection;
            frDb.updateFeedbackResponse(response);
            frcLogic.updateFeedbackResponseCommentsForResponse(response.getId());
        }

    }

    public boolean updateFeedbackResponseForChangingTeam(
            StudentEnrollDetails enrollment,
            FeedbackResponseAttributes response) {

        FeedbackQuestionAttributes question = fqLogic
                .getFeedbackQuestion(response.feedbackQuestionId);

        boolean isGiverSameForResponseAndEnrollment = response.giverEmail
                .equals(enrollment.email);
        boolean isReceiverSameForResponseAndEnrollment = response.recipientEmail
                .equals(enrollment.email);

        boolean shouldDeleteByChangeOfGiver = (isGiverSameForResponseAndEnrollment && (question.giverType == FeedbackParticipantType.TEAMS
                || question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS));
        boolean shouldDeleteByChangeOfRecipient = (isReceiverSameForResponseAndEnrollment
                && question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS);

        boolean shouldDeleteResponse = shouldDeleteByChangeOfGiver
                || shouldDeleteByChangeOfRecipient;

        if (shouldDeleteResponse) {
            frDb.deleteEntity(response);
        }
        
        return shouldDeleteResponse;
    }
    
    public void updateFeedbackResponseForChangingSection(
            StudentEnrollDetails enrollment,
            FeedbackResponseAttributes response) throws InvalidParametersException, EntityDoesNotExistException {

        boolean isGiverSameForResponseAndEnrollment = response.giverEmail
                .equals(enrollment.email);
        boolean isReceiverSameForResponseAndEnrollment = response.recipientEmail
                .equals(enrollment.email);

        if(isGiverSameForResponseAndEnrollment){
            response.giverSection = enrollment.newSection;
        }
        if(isReceiverSameForResponseAndEnrollment){
            response.recipientSection = enrollment.newSection;
        }
        
        if(isGiverSameForResponseAndEnrollment || isReceiverSameForResponseAndEnrollment){
            frDb.updateFeedbackResponse(response);
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
            response.giverEmail = newEmail;
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
            response.recipientEmail = newEmail;
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

    public void deleteFeedbackResponsesForQuestionAndCascade(String feedbackQuestionId) {
        List<FeedbackResponseAttributes> responsesForQuestion =
                getFeedbackResponsesForQuestion(feedbackQuestionId);
        for (FeedbackResponseAttributes response : responsesForQuestion) {
            this.deleteFeedbackResponseAndCascade(response);
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
     * Adds {@link FeedbackResponseAttributes} in {@code newResponses} that are
     * not already in to {@code existingResponses} to {@code existingResponses}.
     */
    private void addNewResponses(
            List<FeedbackResponseAttributes> existingResponses,
            List<FeedbackResponseAttributes> newResponses) {

        Map<String, FeedbackResponseAttributes> responses =
                new HashMap<String, FeedbackResponseAttributes>();

        for (FeedbackResponseAttributes existingResponse : existingResponses) {
            responses.put(existingResponse.getId(), existingResponse);
        }
        for (FeedbackResponseAttributes newResponse : newResponses) {
            if (!responses.containsKey(newResponse.getId())) {
                responses.put(newResponse.getId(), newResponse);
                existingResponses.add(newResponse);
            }
        }
    }

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

        return responses;
    }

    private List<FeedbackResponseAttributes> getFeedbackResponsesForTeamMembersOfStudent(
            String feedbackQuestionId, StudentAttributes student) {
        
        List<StudentAttributes> studentsInTeam = studentsLogic.getStudentsForTeam(student.team, student.course);
       
        List<FeedbackResponseAttributes> teamResponses =
                new ArrayList<FeedbackResponseAttributes>();
        
        for(StudentAttributes studentInTeam : studentsInTeam){
            if(studentInTeam.email.equals(student.email)){
                continue;
            }
            List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForReceiverForQuestion(feedbackQuestionId, studentInTeam.email);
            teamResponses.addAll(responses);
        }
        
        return teamResponses;
    }

    private List<FeedbackResponseAttributes> getViewableFeedbackResponsesForStudentForQuestion(
            FeedbackQuestionAttributes question, String studentEmail) {

        List<FeedbackResponseAttributes> viewableResponses =
                new ArrayList<FeedbackResponseAttributes>();

        StudentAttributes student =
                studentsLogic.getStudentForEmail(question.courseId,
                        studentEmail);

        if (question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
            addNewResponses(viewableResponses,
                    getFeedbackResponsesForQuestion(question.getId()));

            // Early return as STUDENTS covers all other student types.
            return viewableResponses;
        }

        if (question.recipientType == FeedbackParticipantType.TEAMS &&
                question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
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
