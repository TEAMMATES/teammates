package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.entity.FeedbackResponse;

public class FeedbackResponsesLogic {

    private static final Logger log = Utils.getLogger();

    private static FeedbackResponsesLogic instance = null;
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final InstructorsLogic instructorLogic = InstructorsLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic
            .inst();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();

    public static FeedbackResponsesLogic inst() {
        if (instance == null)
            instance = new FeedbackResponsesLogic();
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
        FeedbackResponseAttributes response = frDb.getFeedbackResponse(feedbackResponseId);
        if (isResponseValid(response, fqLogic.getFeedbackQuestion(response.feedbackQuestionId))) {
            return response;
        } 
        return null;
    }

    public FeedbackResponseAttributes getFeedbackResponse(
            String feedbackQuestionId, String giverEmail, String recipient) {
        // TODO: check what is this line doing here!!!
        log.warning(feedbackQuestionId);
        FeedbackResponseAttributes response = frDb.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
        if (isResponseValid(response, fqLogic.getFeedbackQuestion(feedbackQuestionId))) {
            return response;
        } 
        return null;
    }
    
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSession(
            String feedbackSessionName, String courseId) {
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSession(feedbackSessionName, courseId);
        List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response: responses) {
            if (isResponseValid(response, fqLogic.getFeedbackQuestion(response.feedbackQuestionId))) {
                validResponses.add(response);
            }
        }
        return validResponses;
    }
    
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSection(
            String feedbackSessionName, String courseId, String section){
        if(section == null){
            return getFeedbackResponsesForSession(feedbackSessionName, courseId);
        } else {
            List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionInSection(feedbackSessionName, courseId, section);
            List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
            for (FeedbackResponseAttributes response: responses) {
                if (isResponseValid(response, fqLogic.getFeedbackQuestion(response.feedbackQuestionId))) {
                    validResponses.add(response);
                }
            }
            return validResponses;
        }
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSection(
            String feedbackSessionName, String courseId, String section){
        if(section == null){
            return getFeedbackResponsesForSession(feedbackSessionName, courseId);
        } else {
            List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionFromSection(feedbackSessionName, courseId, section);
            List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
            for (FeedbackResponseAttributes response: responses) {
                if (isResponseValid(response, fqLogic.getFeedbackQuestion(response.feedbackQuestionId))) {
                    validResponses.add(response);
                }
            }
            return validResponses;
        }
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSection(
            String feedbackSessionName, String courseId, String section){
        if(section == null){
            return getFeedbackResponsesForSession(feedbackSessionName, courseId);
        } else {
            List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionToSection(feedbackSessionName, courseId, section);
            List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
            for (FeedbackResponseAttributes response: responses) {
                if (isResponseValid(response, fqLogic.getFeedbackQuestion(response.feedbackQuestionId))) {
                    validResponses.add(response);
                }
            }
            return validResponses;
        }
    }
    
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionWithinRange(
            String feedbackSessionName, String courseId, long range) {
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionWithinRange(
                                                     feedbackSessionName, courseId, range);
        List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response: responses) {
            if (isResponseValid(response, fqLogic.getFeedbackQuestion(response.feedbackQuestionId))) {
                validResponses.add(response);
            }
        }
        return validResponses;
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSectionWithinRange(
            String feedbackSessionName, String courseId, String section,
            long range) {
        if (section == null) {
            return getFeedbackResponsesForSessionWithinRange(
                    feedbackSessionName, courseId, range);
        } else {
            List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionInSectionWithinRange(
                                                         feedbackSessionName, courseId, section, range);
            List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
            for (FeedbackResponseAttributes response: responses) {
                if (isResponseValid(response, fqLogic.getFeedbackQuestion(response.feedbackQuestionId))) {
                    validResponses.add(response);
                }
            }
            return validResponses;
        }
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSectionWithinRange(
            String feedbackSessionName, String courseId, String section,
            long range) {
        if(section == null) {
            return getFeedbackResponsesForSessionWithinRange(
                    feedbackSessionName, courseId, range);
        } else {
            List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionFromSectionWithinRange(
                                                         feedbackSessionName, courseId, section, range);
            List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
            for (FeedbackResponseAttributes response: responses) {
                if (isResponseValid(response, fqLogic.getFeedbackQuestion(response.feedbackQuestionId))) {
                    validResponses.add(response);
                }
            }
            return validResponses;
        }
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSectionWithinRange(
            String feedbackSessionName, String courseId, String section,
            long range) {
        if(section == null) {
            return getFeedbackResponsesForSessionWithinRange(
                    feedbackSessionName, courseId, range);
        } else {
            List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionToSectionWithinRange(
                                                         feedbackSessionName, courseId, section, range);
            List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
            for (FeedbackResponseAttributes response: responses) {
                if (isResponseValid(response, fqLogic.getFeedbackQuestion(response.feedbackQuestionId))) {
                    validResponses.add(response);
                }
            }
            return validResponses;
        }
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(String feedbackQuestionId) {
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForQuestion(feedbackQuestionId);
        List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response: responses) {
            if (isResponseValid(response, fqLogic.getFeedbackQuestion(feedbackQuestionId))) {
                validResponses.add(response);
            }
        }
        return validResponses;
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionWithinRange(String feedbackQuestionId, long range) {
        List<FeedbackResponseAttributes> responsesWithRange = frDb.getFeedbackResponsesForQuestionWithinRange(feedbackQuestionId, range);
        List<FeedbackResponseAttributes> validResponsesWithRange = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response: responsesWithRange) {
            if (isResponseValid(response, fqLogic.getFeedbackQuestion(feedbackQuestionId))) {
                validResponsesWithRange.add(response);
            }
        }
        return validResponsesWithRange;
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionInSection(
            String feedbackQuestionId, String section) {
        if (section == null) {
            return getFeedbackResponsesForQuestion(feedbackQuestionId);
        }
        
        List<FeedbackResponseAttributes> responsesInSession = frDb.getFeedbackResponsesForQuestionInSection(feedbackQuestionId, section);
        List<FeedbackResponseAttributes> validResponsesInSession = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response: responsesInSession) {
            if (isResponseValid(response, fqLogic.getFeedbackQuestion(feedbackQuestionId))) {
                validResponsesInSession.add(response);
            }
        }
        return validResponsesInSession;
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestion(
            String feedbackQuestionId, String userEmail) {
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForReceiverForQuestion(feedbackQuestionId, userEmail);
        List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response: responses) {
            if (isResponseValid(response, fqLogic.getFeedbackQuestion(feedbackQuestionId))) {
                validResponses.add(response);
            }
        }
        return validResponses;
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestionInSection(
            String feedbackQuestionId, String userEmail, String section) {
        if (section == null) {
            return getFeedbackResponsesForReceiverForQuestion(feedbackQuestionId, userEmail);
        }
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                                                              feedbackQuestionId, userEmail, section);
        List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response: responses) {
            if (isResponseValid(response, fqLogic.getFeedbackQuestion(feedbackQuestionId))) {
                validResponses.add(response);
            }
        }
        return validResponses;
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestion(
            String feedbackQuestionId, String userEmail) {
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesFromGiverForQuestion(feedbackQuestionId, userEmail);
        List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response: responses) {
            if (isResponseValid(response, fqLogic.getFeedbackQuestion(feedbackQuestionId))) {
                validResponses.add(response);
            }
        }
        return validResponses;
     }

    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestionInSection(
            String feedbackQuestionId, String userEmail, String section) {
        if(section == null){
            return getFeedbackResponsesFromGiverForQuestion(feedbackQuestionId, userEmail);
        }
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                                                     feedbackQuestionId, userEmail, section);
        List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response: responses) {
            if (isResponseValid(response, fqLogic.getFeedbackQuestion(feedbackQuestionId))) {
                validResponses.add(response);
            }
        }
        return validResponses;
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForSessionWithinRange(
            String giverEmail, String feedbackSessionName, String courseId, long range) {
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesFromGiverForSessionWithinRange(giverEmail, 
                                                     feedbackSessionName, courseId, range);
        List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response: responses) {
            if (isResponseValid(response, fqLogic.getFeedbackQuestion(response.feedbackQuestionId))) {
                validResponses.add(response);
            }
        }
        return validResponses;
    }

    public boolean hasGiverRespondedForSession(String userEmail, String feedbackSessionName, String courseId){

        return getFeedbackResponsesFromGiverForSessionWithinRange(userEmail, feedbackSessionName, courseId, 1).size() > 0;
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForCourse(
            String courseId, String userEmail) {
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForReceiverForCourse(courseId, userEmail);
        List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response: responses) {
            if (isResponseValid(response, fqLogic.getFeedbackQuestion(response.feedbackQuestionId))) {
                validResponses.add(response);
            }
        }
        return validResponses;
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForCourse(
            String courseId, String userEmail) {
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesFromGiverForCourse(courseId, userEmail);
        List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response: responses) {
            if (isResponseValid(response, fqLogic.getFeedbackQuestion(response.feedbackQuestionId))) {
                validResponses.add(response);
            }
        }
        return validResponses;
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
            List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesFromGiverForQuestion(question.getId(), student.email);
            List<FeedbackResponseAttributes> validResponses = new ArrayList<FeedbackResponseAttributes>();
            for (FeedbackResponseAttributes response: responses) {
                if (isResponseValid(response, fqLogic.getFeedbackQuestion(question.getId()))) {
                    validResponses.add(response);
                }
            }
            return validResponses;
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
        
        // Early return if user is giver
        if (question.giverType != FeedbackParticipantType.TEAMS) {
            if (response.giverEmail.equals(userEmail)) {
                return true;
            }
        } else {
            // if response is given by team, then anyone in the team can see the response
            if (roster.isStudentsInSameTeam(response.giverEmail, userEmail)) {
                return true;
            }
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
                if (question.recipientType.isTeam()) {
                    if (roster.isStudentInTeam(userEmail, /* this is a team name */
                            response.recipientEmail)) {
                        return true;
                    }
                    break;
                    // Response to individual
                } else if (response.recipientEmail.equals(userEmail)) {
                    return true;
                } else {
                    break;
                }
            case RECEIVER_TEAM_MEMBERS:
                // Response to team; recipient = teamName
                if (question.recipientType.isTeam()) {
                    if (roster.isStudentInTeam(userEmail, /* this is a team name */
                            response.recipientEmail)) {
                        return true;
                    }
                    break;
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
                || (question.recipientType.equals(FeedbackParticipantType.GIVER)  
                    && question.giverType.equals(FeedbackParticipantType.STUDENTS)); 
                                        
        if (isStudentRecipientType
            && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            return true;
        }
        if (question.recipientType.isTeam() 
            && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
            return true;
        }
        if (question.giverType == FeedbackParticipantType.TEAMS 
            || question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            return true;
        }
        if (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {
            return true;
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
        FeedbackResponse oldResponseEntity = null;
        if (newResponse.getId() == null) {
            oldResponseEntity = frDb.getFeedbackResponseEntityWithCheck(newResponse.feedbackQuestionId, 
                    newResponse.giverEmail, newResponse.recipientEmail);
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
     * @throws EntityAlreadyExistsException  if trying to prevent an id clash by recreating a response,
     *                                       a response with the same id already exist. 
     * @throws InvalidParametersException               
     */
    public void updateFeedbackResponse(
                        FeedbackResponseAttributes updatedResponse,
                        FeedbackResponse oldResponseEntity)
                                throws InvalidParametersException, EntityAlreadyExistsException {
        Assumption.assertNotNull(oldResponseEntity);
        
        // Create a copy.
        FeedbackResponseAttributes newResponse = new FeedbackResponseAttributes(updatedResponse);
        
        FeedbackResponseAttributes oldResponse = new FeedbackResponseAttributes(oldResponseEntity);

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
    
        if (!newResponse.recipientEmail.equals(oldResponse.recipientEmail) 
            || !newResponse.giverEmail.equals(oldResponse.giverEmail)) {
            // Recreate response to prevent possible future id conflict.
            recreateResponse(newResponse, oldResponse);
        } else {
            try {
                frDb.updateFeedbackResponseOptimized(newResponse, oldResponseEntity);
            } catch (EntityDoesNotExistException e) {
                Assumption.fail();
            }
        }
    }

    private void recreateResponse(FeedbackResponseAttributes newResponse,
                                    FeedbackResponseAttributes oldResponse)
                                    throws InvalidParametersException, EntityAlreadyExistsException {
        try {
            newResponse.setId(null);
            frDb.createEntity(newResponse);
            frDb.deleteEntity(oldResponse);
        } catch (EntityAlreadyExistsException e) {
            log.warning("Trying to update an existing response to one that already exists.");
            throw new EntityAlreadyExistsException(Const.StatusMessages.FEEDBACK_RESPONSE_RECIPIENT_ALREADY_EXISTS);
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
                    || isRecipientTypeTeamMembers(question)) {
                frDb.deleteEntity(response);
            }
        }

        List<FeedbackResponseAttributes> responsesToUser =
                getFeedbackResponsesForReceiverForCourse(courseId, userEmail);

        for (FeedbackResponseAttributes response : responsesToUser) {
            question = fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
            if (isRecipientTypeTeamMembers(question) ) {
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
                || isRecipientTypeTeamMembers(question)));
        boolean shouldDeleteByChangeOfRecipient = (isReceiverSameForResponseAndEnrollment
                && isRecipientTypeTeamMembers(question));

        boolean shouldDeleteResponse = shouldDeleteByChangeOfGiver
                || shouldDeleteByChangeOfRecipient;

        if (shouldDeleteResponse) {
            frDb.deleteEntity(response);
        }
        
        return shouldDeleteResponse;
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
        
        if(isGiverSameForResponseAndEnrollment){
            feedbackResponse.setGiverSection(enrollment.newSection);
        }
        
        if(isReceiverSameForResponseAndEnrollment){
            feedbackResponse.setRecipientSection(enrollment.newSection);  
        }
        
        frDb.commitOutstandingChanges();
        
        if(isGiverSameForResponseAndEnrollment || isReceiverSameForResponseAndEnrollment){      
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
            // If the recipient and giver is the same person that needs to be updated
            // update them at the same time
            if (response.recipientEmail.equals(response.giverEmail)) {
                response.recipientEmail = newEmail;
            }
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

    public void deleteFeedbackResponsesForQuestionAndCascade(
            String feedbackQuestionId, boolean hasResponseRateUpdate) {
        List<FeedbackResponseAttributes> responsesForQuestion =
                getFeedbackResponsesForQuestion(feedbackQuestionId);

        Set<String> emails = new HashSet<String>();

        for (FeedbackResponseAttributes response : responsesForQuestion) {
            this.deleteFeedbackResponseAndCascade(response);
            emails.add(response.giverEmail);
        }

        if (!hasResponseRateUpdate) {
            return;
        }

        try {
            FeedbackQuestionAttributes question = fqLogic
                    .getFeedbackQuestion(feedbackQuestionId);
            boolean isInstructor = (question.giverType == FeedbackParticipantType.SELF || question.giverType == FeedbackParticipantType.INSTRUCTORS);
            for (String email : emails) {
                boolean hasResponses = hasGiverRespondedForSession(email, question.feedbackSessionName, question.courseId);
                if (!hasResponses) {
                    if (isInstructor) {
                        fsLogic.deleteInstructorRespondant(email,
                                question.feedbackSessionName,
                                question.courseId);
                    } else {
                        fsLogic.deleteStudentRespondant(email,
                                question.feedbackSessionName,
                                question.courseId);
                    }
                }
            }
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            Assumption.fail("Fail to delete respondant");
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
            responses.addAll(getFeedbackResponsesFromGiverForQuestion(
                    feedbackQuestionId, student.email));
        }
        
        responses.addAll(getFeedbackResponsesFromGiverForQuestion(
                                        feedbackQuestionId, teamName));
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
            List<FeedbackResponseAttributes> responses = getFeedbackResponsesForReceiverForQuestion(feedbackQuestionId, studentInTeam.email);
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

        if (question.recipientType.isTeam() &&
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
    
    public boolean isResponseValid(FeedbackResponseAttributes response,
                                   FeedbackQuestionAttributes question) {
        if (response == null || question == null) {
            return false;
        }
        return response.isValid() && isResponseGiverAndReceiverValid(response, question);
    }

    public boolean isResponseGiverAndReceiverValid(FeedbackResponseAttributes response,
                                                   FeedbackQuestionAttributes question) {
        if (question == null || response == null) {
            return false;
        }

        return isResponseGiverTypeMatchedToQuestionSetting(response, question) 
               && isResponseRecipientTypeMatchedToQuestionSetting(response, question);
    }

    private boolean isResponseGiverTypeMatchedToQuestionSetting(FeedbackResponseAttributes response,
                                                                FeedbackQuestionAttributes question) {
        
        FeedbackParticipantType giverType = question.giverType;
        
        String courseId = response.courseId;
        if (!courseId.equals(question.courseId)) {
            return false;
        }
        
        switch (giverType) {
        case TEAMS:
            // Check if giver is in the course (as a student or an instructor or a team)
            if (!studentsLogic.isStudentInCourse(courseId, response.giverEmail) 
                && !instructorLogic.isEmailOfInstructorOfCourse(response.giverEmail, courseId)
                && studentsLogic.getStudentsForTeam(response.giverEmail, courseId) == null) {
                return false;
            }
            break;
        case INSTRUCTORS:
            if (!instructorLogic.isEmailOfInstructorOfCourse(response.giverEmail, courseId)) {
                return false;
            }
            break;
        case SELF:
            String creatorEmail = question.creatorEmail;
            if (!response.giverEmail.equals(creatorEmail)) {
                return false;
            } 
            break;
        case STUDENTS:
            if (!studentsLogic.isStudentInCourse(courseId, response.giverEmail)) {
                return false;
            }
            break;
        default:
            log.severe("Invalid giver type specified");
        }
        return true;
    }
    
    private boolean isResponseRecipientTypeMatchedToQuestionSetting(FeedbackResponseAttributes response,
                                                                    FeedbackQuestionAttributes question) {
        
        FeedbackParticipantType recipientType = question.recipientType;    
        
        String courseId = response.courseId;
        if (!courseId.equals(question.courseId)) {
            return false;
        }
        
        String giver;
        String recipient;
        String giverTeam;
        String recipientTeam;
        
        if (studentsLogic.isStudentInCourse(courseId, response.giverEmail)) {
            giver = response.giverEmail;
            StudentAttributes student = studentsLogic.getStudentForEmail(courseId, response.giverEmail);
            giverTeam = student.team;
        } else if (instructorLogic.isEmailOfInstructorOfCourse(response.giverEmail, courseId)) {
            giver = response.giverEmail;
            giverTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
        } else {
            giver = response.giverEmail;
            giverTeam = response.giverEmail;
        }
        
        if (studentsLogic.isStudentInCourse(courseId, response.recipientEmail)) {
            recipient = response.recipientEmail;
            StudentAttributes student = studentsLogic.getStudentForEmail(courseId, response.recipientEmail);
            recipientTeam = student.team;
        } else if (instructorLogic.isEmailOfInstructorOfCourse(response.recipientEmail, courseId)) {
            recipient = response.recipientEmail;
            recipientTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
        } else {
            recipient = response.recipientEmail;
            recipientTeam = response.recipientEmail;
        }

        switch(recipientType) {
            case TEAMS:
                List<TeamDetailsBundle> teams;
                try {
                    teams = coursesLogic.getTeamsForCourse(courseId);
                } catch (EntityDoesNotExistException e) {
                    log.severe(e.toString());
                    return false;
                }
                ArrayList<String> teamName = new ArrayList<String>();
                for (int i = 0; i < teams.size(); i++) {
                    teamName.add(teams.get(i).name);
                }
                if (!teamName.contains(recipientTeam) || recipientTeam.equals(giverTeam)) {
                    return false;
                }
                break;
            case OWN_TEAM:
                if (!recipientTeam.equals(giverTeam)) {
                    return false;
                }
                break;
            case SELF:
                if (!recipient.equals(giver)) {                    
                    return false;
                }
                break;
            case INSTRUCTORS:
                if (!instructorLogic.isEmailOfInstructorOfCourse(response.recipientEmail, courseId)) {
                    return false;
                }
                break;
            case STUDENTS:
                if (!studentsLogic.isStudentInCourse(courseId, response.recipientEmail)) {
                    return false;
                }
                break;
            case OWN_TEAM_MEMBERS:
                if (!studentsLogic.isStudentsInSameTeam(courseId, giver, recipient) || recipient.equals(giver)) {
                    return false;
                }
                break;
            case OWN_TEAM_MEMBERS_INCLUDING_SELF:
                if (!studentsLogic.isStudentsInSameTeam(courseId, giver, recipient)) {
                    return false;
                }
                break;
            case NONE:
                if (recipient != null && !recipient.equals(Const.USER_NOBODY_TEXT) 
                                      && !recipient.equals(Const.USER_IS_NOBODY)
                                      && !recipient.equals(Const.GENERAL_QUESTION)) {
                    return false;
                }
                break;
            default:
                log.severe("Invalid recipient type specified");
                return false;
        }
        return true;
    }
}
