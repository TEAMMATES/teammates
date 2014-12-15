package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import teammates.common.util.Const;
import teammates.logic.core.TeamEvalResult;
import teammates.ui.controller.PageData;

/**
 * Represents detailed results for an feedback session.
 * <br> Contains:
 * <br> * The basic {@link FeedbackSessionAttributes} 
 * <br> * {@link List} of viewable responses as {@link FeedbackResponseAttributes} objects.
 */
public class FeedbackSessionResultsBundle implements SessionResultsBundle{
    public FeedbackSessionAttributes feedbackSession = null;
    public List<FeedbackResponseAttributes> responses = null;
    public Map<String, FeedbackQuestionAttributes> questions = null;
    public Map<String, String> emailNameTable = null;
    public Map<String, String> emailLastNameTable = null;
    public Map<String, String> emailTeamNameTable = null;
    public Map<String, Map<String, String>> rosterTeamNameEmailTable = null; 
    public Map<String, Set<String>> rosterSectionTeamNameTable = null;
    public Map<String, boolean[]> visibilityTable = null;
    public FeedbackSessionResponseStatus responseStatus = null;
    public CourseRoster roster = null;
    public Map<String, List<FeedbackResponseCommentAttributes>> responseComments = null;
    public boolean isComplete;    

	 
    /**
     * Responses with identities of giver/recipients NOT hidden.
     * To be used for anonymous result calculation only, and identities hidden before showing to users.
     */
    public List<FeedbackResponseAttributes> actualResponses = null;
    

    //For contribution questions.
    //Key is questionId, value is a map of student email to StudentResultSumary
    public Map<String, Map<String, StudentResultSummary>> contributionQuestionStudentResultSummary =
            new HashMap<String, Map<String, StudentResultSummary>>();
    //Key is questionId, value is a map of team name to TeamEvalResult
    public Map<String, Map<String, TeamEvalResult>> contributionQuestionTeamEvalResults =
            new HashMap<String, Map<String, TeamEvalResult>>();
    
    public FeedbackSessionResultsBundle (FeedbackSessionAttributes feedbackSession,
            List<FeedbackResponseAttributes> responses,
            Map<String, FeedbackQuestionAttributes> questions,
            Map<String, String> emailNameTable,
            Map<String, String> emailLastNameTable,
            Map<String, String> emailTeamNameTable,
            Map<String, boolean[]> visibilityTable,
            FeedbackSessionResponseStatus responseStatus,
            CourseRoster roster,
            Map<String, List<FeedbackResponseCommentAttributes>> responseComments){
        this(feedbackSession, responses, questions, emailNameTable, emailLastNameTable, emailTeamNameTable, visibilityTable, responseStatus, roster, responseComments, true);
    }

    public FeedbackSessionResultsBundle (FeedbackSessionAttributes feedbackSession,
            List<FeedbackResponseAttributes> responses,
            Map<String, FeedbackQuestionAttributes> questions,
            Map<String, String> emailNameTable,
            Map<String, String> emailLastNameTable,
            Map<String, String> emailTeamNameTable,
            Map<String, boolean[]> visibilityTable,
            FeedbackSessionResponseStatus responseStatus,
            CourseRoster roster,
            Map<String, List<FeedbackResponseCommentAttributes>> responseComments,
            boolean isComplete) {
        this.feedbackSession = feedbackSession;
        this.questions = questions;
        this.responses = responses;
        this.emailNameTable = emailNameTable;
        this.emailLastNameTable = emailLastNameTable;
        this.emailTeamNameTable = emailTeamNameTable;
        this.visibilityTable = visibilityTable;
        this.responseStatus = responseStatus;
        this.roster = roster;
        this.responseComments = responseComments;
        this.actualResponses = new ArrayList<FeedbackResponseAttributes>();

        // We change user email to team name here for display purposes.
        for (FeedbackResponseAttributes response : responses) {
            if (questions.get(response.feedbackQuestionId).giverType == FeedbackParticipantType.TEAMS){ 
                response.giverEmail += Const.TEAM_OF_EMAIL_OWNER;
            }
            //Copy the data before hiding response recipient and giver.
            FeedbackResponseAttributes fraCopy = new FeedbackResponseAttributes(response);
            actualResponses.add(fraCopy);
        }
        this.isComplete = isComplete;
        
        hideResponsesGiverRecipient(responses, questions, emailNameTable,
                emailTeamNameTable, visibilityTable);
        
        // unlike emailTeamNameTable, emailLastNameTable and emailTeamNameTable,
        // roster.*Table is populated using the CourseRoster data directly
        this.rosterTeamNameEmailTable = getTeamNameToStudentsTableFromRoster(roster);
        this.rosterSectionTeamNameTable = getSectionToTeamNamesFromRoster(roster);
    }

    /**
     * Hides response names/emails and teams that are not visible to the current user.
     * Replaces the giver/recipient email in responses to an email with two "@@"s, to
     * indicate it is invalid and should not be displayed.
     * 
     * @param responses
     * @param questions
     * @param emailNameTable
     * @param emailTeamNameTable
     * @param visibilityTable
     */
    private void hideResponsesGiverRecipient(
            List<FeedbackResponseAttributes> responses,
            Map<String, FeedbackQuestionAttributes> questions,
            Map<String, String> emailNameTable,
            Map<String, String> emailTeamNameTable,
            Map<String, boolean[]> visibilityTable) {
        
        for (FeedbackResponseAttributes response : responses) {
            FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
            FeedbackParticipantType type = question.recipientType;
            
            //Recipient
            String name = emailNameTable.get(response.recipientEmail);
            if (visibilityTable.get(response.getId())[1] == false &&
                    type != FeedbackParticipantType.SELF &&
                    type != FeedbackParticipantType.NONE) {
                String anonEmail = getAnonEmail(type, name);
                name = getAnonName(type, name);
                
                emailNameTable.put(anonEmail, name);
                emailTeamNameTable.put(anonEmail, name + Const.TEAM_OF_EMAIL_OWNER);
                
                response.recipientEmail = anonEmail;
            }

            //Giver
            name = emailNameTable.get(response.giverEmail);
            type = question.giverType;
            if (visibilityTable.get(response.getId())[0] == false &&
                    type != FeedbackParticipantType.SELF) {
                String anonEmail = getAnonEmail(type, name);
                name = getAnonName(type, name);
                
                emailNameTable.put(anonEmail, name);
                emailTeamNameTable.put(anonEmail, name + Const.TEAM_OF_EMAIL_OWNER);
                if(type == FeedbackParticipantType.TEAMS){
                    emailTeamNameTable.put(anonEmail, name);
                }
                response.giverEmail = anonEmail;
            }
        }
    }

    private String getAnonEmail(FeedbackParticipantType type, String name) {
        String anonName = getAnonName(type, name);
        return anonName+"@@"+anonName+".com";
    }
    
    public String getAnonEmailFromEmail(String email) {
        String name = emailNameTable.get(email);
        return getAnonEmail(FeedbackParticipantType.STUDENTS, name);
    }

    private String getAnonName(FeedbackParticipantType type, String name) {
        String hash = getHashOfName(name);
        String anonName = type.toSingularFormString();
        anonName = "Anonymous " + anonName + " " + hash;
        return anonName;
    }

    private String getHashOfName(String name) {
        return Integer.toString(Math.abs(name.hashCode()));
    }
    
    public String getNameFromRoster(String email) {
        StudentAttributes student = roster.getStudentForEmail(email);
        InstructorAttributes instructor = roster.getInstructorForEmail(email);
        if (student != null) {
            return student.name;
        } else if (instructor != null) {
            return instructor.name;
        }
        
        return "";
    }
    
    public String getTeamNameFromRoster(String email) {
        StudentAttributes student = roster.getStudentForEmail(email);
        InstructorAttributes instructor = roster.getInstructorForEmail(email);
        if (student != null) {
            return student.team;
        } else if (instructor != null) {
            return "Instructors";
        }
        
        return "";
    }
    
    public Set<String> getTeamMembersFromRoster(String teamName) {
        if (rosterTeamNameEmailTable.get(teamName) != null) {
            return rosterTeamNameEmailTable.get(teamName).keySet();
        } else {
            return new HashSet<String>();
        }
    }
    
    
    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa, String recipientEmail) {
        boolean recipientIsAnonymous = recipientEmail.contains("@@");
       
        if (recipientEmail == null || recipientIsAnonymous) {
            return new ArrayList<String>();
        }
        
        if (emailNameTable.containsKey(recipientEmail)) {
            StudentAttributes student = roster.getStudentForEmail(recipientEmail);
            InstructorAttributes instructor = roster.getInstructorForEmail(recipientEmail);
            
            if (student != null) {
                return getPossibleGivers(fqa, student);
            } else if (instructor != null) {
                return getPossibleGivers(fqa, instructor);
            } else {
                return getPossibleGiversForTeam(fqa, recipientEmail);
            }
        }
        
        return new ArrayList<String>();
    }
    
    public List<String> getPossibleGiversForTeam(FeedbackQuestionAttributes fqa, String team) {
        FeedbackParticipantType givertype = fqa.giverType;
        FeedbackParticipantType recipienttype = fqa.recipientType;
        List<String> possibleGivers = new ArrayList<String>();
        
        switch(recipienttype) {
        case TEAMS:
            if (givertype == FeedbackParticipantType.TEAMS) {
                possibleGivers = getListOfTeams();
            } else if (givertype == FeedbackParticipantType.STUDENTS) {
                possibleGivers = getListOfStudentEmailsSortedBySection();
            } else if (givertype == FeedbackParticipantType.INSTRUCTORS) {
                possibleGivers = getListOfInstructorEmails();
            } else if (givertype == FeedbackParticipantType.SELF) {
                possibleGivers.add(fqa.creatorEmail);
            }
            break;
        case OWN_TEAM:
            if (givertype == FeedbackParticipantType.TEAMS) {
                possibleGivers.add(team);
            } else {
                possibleGivers = new ArrayList<String>(getTeamMembersFromRoster(team));
            }
            break;
        case INSTRUCTORS:
        case STUDENTS:
        case SELF:
        case NONE:
        case OWN_TEAM_MEMBERS:
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
        default:
            break;
        }
        
        return possibleGivers;
    }
    
    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa, StudentAttributes recipient) {
        FeedbackParticipantType givertype = fqa.giverType;
        FeedbackParticipantType recipienttype = fqa.recipientType;
        List<String> possibleGivers = new ArrayList<String>();
        switch(givertype) {
        case STUDENTS:
            possibleGivers = getListOfStudentEmailsSortedBySection();
            break;
        case INSTRUCTORS:
            possibleGivers = getListOfInstructorEmails();
            break;
        case TEAMS:
            possibleGivers = getListOfTeams();
            break;
        case SELF:
            possibleGivers.add(fqa.creatorEmail);
            break;
        case NONE:
        case OWN_TEAM:
        case OWN_TEAM_MEMBERS:
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
        default:
            break;
        }
        
        switch(recipienttype) {
        case STUDENTS:
            break;
        case INSTRUCTORS:
            break;
        case TEAMS:
            break;
        case SELF:
            possibleGivers = new ArrayList<String>();
            possibleGivers.add(recipient.email);
            break;
        case NONE:
            break;
        case OWN_TEAM:
            possibleGivers.retainAll(getListOfTeamMembers(recipient));
        case OWN_TEAM_MEMBERS:
            possibleGivers.retainAll(getListOfTeamMembersExcludingSelf(recipient));
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            possibleGivers.retainAll(getListOfTeamMembers(recipient));
        default:
            break;
        }

        return possibleGivers;
    }
    
    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa, InstructorAttributes recipient) {
        FeedbackParticipantType givertype = fqa.giverType;
        List<String> possibleGivers = new ArrayList<String>();
        
        switch(givertype) {
        case STUDENTS:
            possibleGivers = getListOfStudentEmailsSortedBySection();
            break;
        case INSTRUCTORS:
            possibleGivers = getListOfInstructorEmails();
            break;
        case TEAMS:
            possibleGivers = getListOfTeams();
            break;
        case SELF:
            possibleGivers.add(fqa.creatorEmail);
            break;
        case NONE:
        case OWN_TEAM:
        case OWN_TEAM_MEMBERS:
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
        default:
            break;
        }
        
        return possibleGivers;
    }
    
    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa) {
        FeedbackParticipantType givertype = fqa.giverType;
        List<String> possibleGivers = new ArrayList<String>();
        
        switch(givertype) {
        case STUDENTS:
            possibleGivers = getListOfStudentEmailsSortedBySection();
            break;
        case INSTRUCTORS:
            possibleGivers = getListOfInstructorEmails();
            break;
        case TEAMS:
            possibleGivers = getListOfTeams();
            break;
        case SELF:
            possibleGivers = new ArrayList<String>();
            possibleGivers.add(fqa.creatorEmail);
            break;
        case NONE:
            break;
        case OWN_TEAM:
        case OWN_TEAM_MEMBERS:
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
        default:
            break;
        }
        return possibleGivers;
    }
    
    
    public List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa, String giverEmail) {
        boolean giverIsAnonymous = giverEmail.contains("@@");
       
        if (giverEmail == null || giverIsAnonymous) {
            return new ArrayList<String>();
        }
        
        //if (emailNameTable.containsKey(giverEmail)) {
            StudentAttributes student = roster.getStudentForEmail(giverEmail);
            InstructorAttributes instructor = roster.getInstructorForEmail(giverEmail);
            
            if (student != null) {
                return getPossibleRecipients(fqa, student);
            } else if (instructor != null) {
                return getPossibleRecipients(fqa, instructor);
            } else {
                return getPossibleRecipientsForTeam(fqa, giverEmail);
            }
        //}
        
    }
    
    public List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa, InstructorAttributes giver) {
        FeedbackParticipantType type = fqa.recipientType;
        List<String> possibleRecipients = new ArrayList<String>();
        switch(type) {
            case STUDENTS:
                // all students
                possibleRecipients = getListOfStudentEmailsSortedBySection();
                break;
            case INSTRUCTORS:
                possibleRecipients = getListOfInstructorEmails();
                break;
            case TEAMS:
                possibleRecipients = getListOfTeams();
                break;
            case SELF:
                possibleRecipients.add(giver.email);
                break;
            case NONE:
            case OWN_TEAM:
            case OWN_TEAM_MEMBERS:
            case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            default:
                break;
        }
        
        return possibleRecipients;
    }
    
    public List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa, StudentAttributes giver) {
        FeedbackParticipantType type = fqa.recipientType;
        List<String> possibleRecipients = new ArrayList<String>();
        switch(type) {
            case STUDENTS:
                // all students
                possibleRecipients = getListOfStudentEmailsSortedBySection();
                break;
            case OWN_TEAM_MEMBERS:
                possibleRecipients = getListOfTeamMembersExcludingSelf(giver);
                break;
            case OWN_TEAM_MEMBERS_INCLUDING_SELF:
                possibleRecipients = getListOfTeamMembers(giver);
                break;
            case INSTRUCTORS:
                possibleRecipients = getListOfInstructorEmails();
                break;
            case TEAMS:
                possibleRecipients = getListOfTeamsExcludingOwnTeam(giver);
                break;
            case OWN_TEAM:
                possibleRecipients.add(giver.team);
                break;
            case SELF:
                possibleRecipients.add(giver.email);
                break;
            case NONE:
            default:
                break;
        }
        
        return possibleRecipients;
    }
    
    public List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa) {
        FeedbackParticipantType type = fqa.recipientType;
        List<String> possibleRecipients = null;
        
        if (fqa.recipientType== FeedbackParticipantType.SELF) {
            type = fqa.giverType;
        }
        
        switch(type) {
            case STUDENTS:
            case OWN_TEAM_MEMBERS:
            case OWN_TEAM_MEMBERS_INCLUDING_SELF:
                // all students
                possibleRecipients = getListOfStudentEmailsSortedBySection();
                break;
            case INSTRUCTORS:
                possibleRecipients = getListOfInstructorEmails();
                break;
            case TEAMS:
            case OWN_TEAM:
                possibleRecipients = getListOfTeams();
                break;
            case NONE:
                break;
            default:
                break;
        }
        
        return possibleRecipients;
    }
    
    public List<String> getPossibleRecipientsForTeam(FeedbackQuestionAttributes fqa, String team) {
        
        FeedbackParticipantType recipienttype = fqa.recipientType;
        List<String> possibleRecipients = new ArrayList<String>();
        
        switch(recipienttype) {
        case TEAMS:
            possibleRecipients = getListOfTeams();
            break;
        case OWN_TEAM:
            possibleRecipients.add(team);
            break;
        case INSTRUCTORS:
            possibleRecipients = getListOfInstructorEmails();
        case STUDENTS:
            possibleRecipients = getListOfStudentEmailsSortedBySection();
        case SELF:
            possibleRecipients.add(team);
        case OWN_TEAM_MEMBERS:
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            if (rosterTeamNameEmailTable.containsKey(team)) {
                Map<String, String> teamToEmails = rosterTeamNameEmailTable.get(team);
                if (teamToEmails != null) {
                    possibleRecipients = new ArrayList<String>(teamToEmails.keySet());
                }
            }
        case NONE:
        default:
            break;
        }
        
        return possibleRecipients;
    }
    
    public List<String> getListOfTeamsExcludingOwnTeam(StudentAttributes student) {
        String studentTeam = student.team;
        Set<String> teams = rosterTeamNameEmailTable.keySet();
        teams.remove(studentTeam);
        
        return new ArrayList<String>(teams);
    }
    
    public List<String> getListOfTeams() {
        Set<String> teams = rosterTeamNameEmailTable.keySet();
        
        return new ArrayList<String>(teams);
    }
    
    public List<String> getListOfTeamMembers(StudentAttributes student) {
        String teamName = student.team;
        Map<String, String> teamMembers = rosterTeamNameEmailTable.get(teamName);
        
        return new ArrayList<String>(teamMembers.keySet());
    }
    
    public List<String> getListOfTeamMembersExcludingSelf(StudentAttributes student) {
        List<String> teamMembers = getListOfTeamMembers(student);
        String currentStudentEmail = student.email;
        
        teamMembers.remove(currentStudentEmail);
        return teamMembers;
    }
    
    public List<String> getListOfCourseEmailsSortedBySection() {
        List<String> emailList = new ArrayList<String>();
        List<StudentAttributes> students = roster.getStudents();
        StudentAttributes.sortBySectionName(students);
        for (StudentAttributes student : students) {
            emailList.add(student.email);
        }
        
        List<InstructorAttributes> instructors = roster.getInstructors();
        for (InstructorAttributes instructor : instructors) {
            emailList.add(instructor.email);
        }
        
        return emailList;
    }
    public List<String> getListOfStudentEmailsSortedBySection() {
        List<String> emailList = new ArrayList<String>();
        
        List<StudentAttributes> students = roster.getStudents();
        StudentAttributes.sortBySectionName(students);
        for (StudentAttributes student : students) {
            emailList.add(student.email);
        }
        
        return emailList;
    }
    public List<String> getListOfInstructorEmails() {
        List<String> emailList = new ArrayList<String>();
        
        List<InstructorAttributes> instructors = roster.getInstructors();
        for (InstructorAttributes instructor : instructors) {
            emailList.add(instructor.email);
        }
        
        return emailList;
    }
    
    
    public List<String> getListOfAnonymisedStudentEmailsSortedBySection() {
        List<String> emailList = new ArrayList<String>();
        List<StudentAttributes> students = roster.getStudents();
        for (StudentAttributes student : students) {
            String anonEmail = getAnonEmail(FeedbackParticipantType.STUDENTS, student.name);
            emailList.add(anonEmail);
        }
        
        return emailList;
    }
    
    /**
     * Used for instructor feedback results views.
     */
    public String getResponseAnswerHtml(FeedbackResponseAttributes response, FeedbackQuestionAttributes question){
        return response.getResponseDetails().getAnswerHtml(response, question, this);
    }
    
    public String getResponseAnswerCsv(FeedbackResponseAttributes response, FeedbackQuestionAttributes question){
        return response.getResponseDetails().getAnswerCsv(response, question, this);
    }

    public boolean hasTargetGivenResponseToSelfInContributionQuestion(FeedbackQuestionAttributes question, String targetEmail) {
        Map<String, StudentResultSummary> stats = FeedbackContributionResponseDetails.getContribQnStudentResultSummary(question, this);
        StudentResultSummary studentResult = stats.get(targetEmail);
        
        return studentResult.claimedFromStudent != Const.INT_UNINITIALIZED && 
               studentResult.claimedFromStudent != Const.POINTS_NOT_SUBMITTED;
    }
    
    

    public FeedbackResponseAttributes getActualResponse(
            FeedbackResponseAttributes response) {
        FeedbackResponseAttributes actualResponse = null;
        for (FeedbackResponseAttributes resp : actualResponses) {
            if (resp.getId().equals(response.getId())) {
                actualResponse = resp;
                break;
            }
        }
        return actualResponse;
    }
    
    public String getNameForEmail(String email) {
        String name = emailNameTable.get(email);
        if (name == null || name.equals(Const.USER_IS_TEAM)) {
            return Const.USER_UNKNOWN_TEXT; //TODO: this doesn't look right
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else {
            return PageData.sanitizeForHtml(name);
        }
    }
    
    public String getLastNameForEmail(String email) {
        String name = emailLastNameTable.get(email);
        if (name == null || name.equals(Const.USER_IS_TEAM)) {
            return Const.USER_UNKNOWN_TEXT; //TODO: this doesn't look right
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else {
            return PageData.sanitizeForHtml(name);
        }
    }
    
    public String getTeamNameForEmail(String email) {
        String teamName = emailTeamNameTable.get(email);
        if (teamName == null || email.equals(Const.GENERAL_QUESTION) ) {
            return Const.USER_NOBODY_TEXT;
        } else {
            return PageData.sanitizeForHtml(teamName);
        }
    }
    
    public String getRecipientNameForResponse(FeedbackQuestionAttributes question,
            FeedbackResponseAttributes response) {
        String name = emailNameTable.get(response.recipientEmail);
        if (name == null || name.equals(Const.USER_IS_TEAM)) {
            return Const.USER_UNKNOWN_TEXT; //TODO: this doesn't look right
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else {
            return PageData.sanitizeForHtml(name);
        }
    }
    
    public String getGiverNameForResponse(FeedbackQuestionAttributes question,
            FeedbackResponseAttributes response) {
        String name = emailNameTable.get(response.giverEmail);
        if (name == null || name.equals(Const.USER_IS_TEAM)) {
            return Const.USER_UNKNOWN_TEXT;
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else {
            return PageData.sanitizeForHtml(name);
        }
    }
    
    public String appendTeamNameToName(String name, String teamName){
        String outputName;
        if(name.contains("Anonymous") 
                || name.equals(Const.USER_UNKNOWN_TEXT) 
                || name.equals(Const.USER_NOBODY_TEXT)
                || teamName.isEmpty()){
            outputName = name;
        }
        else{
            outputName = name + " (" + teamName + ")";
        }
        return outputName;
    }
    
    //TODO consider removing this to increase cohesion
    public String getQuestionText(String feedbackQuestionId){
        return PageData.sanitizeForHtml(
                questions.get(feedbackQuestionId).getQuestionDetails().questionText);
    }

    // TODO: make responses to the student calling this method always on top.
    /**
     * Gets the questions and responses in this bundle as a map. 
     * 
     * @return An ordered {@code Map} with keys as {@link FeedbackQuestionAttributes}
     *  sorted by questionNumber.
     * The mapped values for each key are the corresponding
     *  {@link FeedbackResponseAttributes} as a {@code List}. 
     */
    public Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> getQuestionResponseMap() {
        if (questions == null || responses == null) {
            return null;
        }
        
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> sortedMap
             = new TreeMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        
        for(FeedbackQuestionAttributes question : questions.values()){
            sortedMap.put(question, new ArrayList<FeedbackResponseAttributes>());
        }
        
        for(FeedbackResponseAttributes response : responses){
            List<FeedbackResponseAttributes> responsesForQuestion = sortedMap.get(questions.get(response.feedbackQuestionId));
            responsesForQuestion.add(response);
        }

        for(List<FeedbackResponseAttributes> responsesForQuestion : sortedMap.values()){
            Collections.sort(responsesForQuestion, compareByGiverRecipient);
        }
          
        return sortedMap;
              
    }
    
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> 
                getQuestionResponseMapByRecipientTeam() {

        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap
            = new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForOneRecipient = null;
        List<FeedbackResponseAttributes> responsesForOneRecipientOneQuestion = null;
   
        Collections.sort(responses, compareByTeamQuestionRecipientTeamGiver);
        
        String recipientTeam = null;
        String questionId = null;
        
        for (FeedbackResponseAttributes response : responses) {
            if(recipientTeam == null ||
                    !(getTeamNameForEmail(response.recipientEmail).equals("")? getNameForEmail(response.recipientEmail).equals(recipientTeam): getTeamNameForEmail(response.recipientEmail).equals(recipientTeam))){
                if(questionId!=null && responsesForOneRecipientOneQuestion!=null && responsesForOneRecipient!=null){
                    responsesForOneRecipient.put(questions.get(questionId), responsesForOneRecipientOneQuestion);
                }
                if(recipientTeam!=null && responsesForOneRecipient!=null){
                    sortedMap.put(recipientTeam, responsesForOneRecipient);
                }
                responsesForOneRecipient = new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
                recipientTeam = getTeamNameForEmail(response.recipientEmail);
                if(recipientTeam == ""){
                    recipientTeam = getNameForEmail(response.recipientEmail);
                }
                questionId = null;
            }
            if(questionId == null || !response.feedbackQuestionId.equals(questionId)){
                if(questionId!=null && responsesForOneRecipientOneQuestion!=null){
                    responsesForOneRecipient.put(questions.get(questionId), responsesForOneRecipientOneQuestion);
                }
                responsesForOneRecipientOneQuestion = new ArrayList<FeedbackResponseAttributes>();
                questionId = response.feedbackQuestionId;
            }
            responsesForOneRecipientOneQuestion.add(response);
        }
        if(questionId!=null && responsesForOneRecipientOneQuestion!=null && responsesForOneRecipient!=null){
            responsesForOneRecipient.put(questions.get(questionId), responsesForOneRecipientOneQuestion);
        }
        if(recipientTeam!=null && responsesForOneRecipient!=null){

            sortedMap.put(recipientTeam, responsesForOneRecipient);
        }
        
        return sortedMap;
        
    }
    
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> 
    getQuestionResponseMapByGiverTeam() {
        
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap
        = new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesFromOneGiver = null;
        List<FeedbackResponseAttributes> responsesFromOneGiverOneQuestion = null;
        
        Collections.sort(responses, compareByTeamQuestionGiverTeamRecipient);
        
        String giverTeam = null;
        String questionId = null;
        
        for (FeedbackResponseAttributes response : responses) {
            if(giverTeam == null || 
                    !(getTeamNameForEmail(response.giverEmail).equals("")? getNameForEmail(response.giverEmail).equals(giverTeam) : getTeamNameForEmail(response.giverEmail).equals(giverTeam))){
                if(questionId!=null && responsesFromOneGiverOneQuestion!=null && responsesFromOneGiver!=null){
                    responsesFromOneGiver.put(questions.get(questionId), responsesFromOneGiverOneQuestion);
                }
                if(giverTeam!=null && responsesFromOneGiver!=null){
                    sortedMap.put(giverTeam, responsesFromOneGiver);
                }
                responsesFromOneGiver = new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
                giverTeam = getTeamNameForEmail(response.giverEmail);
                if(giverTeam == ""){
                    giverTeam = getNameForEmail(response.giverEmail);
                }
                questionId = null;
            }
            if(questionId == null || !response.feedbackQuestionId.equals(questionId)){
                if(questionId!=null && responsesFromOneGiverOneQuestion!=null){
                    responsesFromOneGiver.put(questions.get(questionId), responsesFromOneGiverOneQuestion);
                }
                responsesFromOneGiverOneQuestion = new ArrayList<FeedbackResponseAttributes>();
                questionId = response.feedbackQuestionId;
            }
            responsesFromOneGiverOneQuestion.add(response);
        }
        if(questionId!=null && responsesFromOneGiverOneQuestion!=null && responsesFromOneGiver!=null){
            responsesFromOneGiver.put(questions.get(questionId), responsesFromOneGiverOneQuestion);
        }
        if(giverTeam!=null && responsesFromOneGiver!=null){
            sortedMap.put(giverTeam, responsesFromOneGiver);
        }
        
        return sortedMap;
    }
    
    /**
     * Returns responses as a Map<recipientName, Map<question, List<response>>>
     * Where the responses are sorted in the order of recipient, question, giver.
     * @param sortByTeam
     * @return responses sorted by Recipient > Question > Giver
     */
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
                    getResponsesSortedByRecipientQuestionGiver(boolean sortByTeam) {
        
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap
             = new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForOneRecipient = null;
        List<FeedbackResponseAttributes> responsesForOneRecipientOneQuestion = null;
        
        if(sortByTeam){
            Collections.sort(responses, compareByTeamRecipientQuestionTeamGiver);
        } else {
            Collections.sort(responses, compareByRecipientQuestionTeamGiver);
        }
        
        String recipient = null;
        String questionId = null;
        String recipientName = null;
        
        for (FeedbackResponseAttributes response : responses) {
            if(recipient == null || !response.recipientEmail.equals(recipient)){
                if(questionId!=null && responsesForOneRecipientOneQuestion!=null && responsesForOneRecipient!=null){
                    responsesForOneRecipient.put(questions.get(questionId), responsesForOneRecipientOneQuestion);
                }
                if(recipient!=null && responsesForOneRecipient!=null){
                    sortedMap.put(recipientName, responsesForOneRecipient);
                }
                responsesForOneRecipient = new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
                recipient = response.recipientEmail;
                recipientName = this.getRecipientNameForResponse(questions.get(response.feedbackQuestionId), response);
                questionId = null;
            }
            if(questionId == null || !response.feedbackQuestionId.equals(questionId)){
                if(questionId!=null && responsesForOneRecipientOneQuestion!=null){
                    responsesForOneRecipient.put(questions.get(questionId), responsesForOneRecipientOneQuestion);
                }
                responsesForOneRecipientOneQuestion = new ArrayList<FeedbackResponseAttributes>();
                questionId = response.feedbackQuestionId;
            }
            responsesForOneRecipientOneQuestion.add(response);
        }
        if(questionId!=null && responsesForOneRecipientOneQuestion!=null && responsesForOneRecipient!=null){
            responsesForOneRecipient.put(questions.get(questionId), responsesForOneRecipientOneQuestion);
        }
        if(recipient!=null && responsesForOneRecipient!=null){

            sortedMap.put(recipientName, responsesForOneRecipient);
        }
        
        return sortedMap;
    }

    
    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node using a {@code LinkedHashMap} implementation.
     * <br>The tree is sorted by recipientName > giverName > questionNumber.
     * <br>The key of each map represents the parent node, while the value represents the leaf.
     * <br>The top-most parent {@code String recipientName} is the recipient's name of all it's leafs.
     * <br>The inner parent {@code String giverName} is the giver's name of all it's leafs.
     * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
     * <br>with attributes corresponding to it's parents.
     * @return The responses in this bundle sorted by recipient's name > giver's name > question number.
     */
    public Map<String, Map<String, List<FeedbackResponseAttributes>>> getResponsesSortedByRecipient() {
        return getResponsesSortedByRecipient(false);
    }
    
    public Map<String, Map<String, List<FeedbackResponseAttributes>>> getResponsesSortedByRecipient(boolean sortByTeam) {

        Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>();

        if(sortByTeam){
            Collections.sort(responses, compareByTeamRecipientGiverQuestion);
        } else {
            Collections.sort(responses, compareByRecipientGiverQuestion);
        }  

        String prevGiver = null;
        String prevRecipient = null;
        String recipientName = null;
        String giverName = null;
        String recipientTeamName = null;
        String giverTeamName = null;

        List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                new ArrayList<FeedbackResponseAttributes>();
        Map<String, List<FeedbackResponseAttributes>> responsesToOneRecipient =
                new LinkedHashMap<String, List<FeedbackResponseAttributes>>();

        for (FeedbackResponseAttributes response : responses) {
            // New recipient, add response package to map.
            if (response.recipientEmail.equals(prevRecipient) == false
                    && prevRecipient != null) {
                // Put previous giver responses into inner map. 
                responsesToOneRecipient.put(giverName,
                        responsesFromOneGiverToOneRecipient);
                // Put all responses for previous recipient into outer map.
                sortedMap.put(recipientName, responsesToOneRecipient);
                // Clear responses
                responsesToOneRecipient = new LinkedHashMap<String,
                        List<FeedbackResponseAttributes>>();
                responsesFromOneGiverToOneRecipient = new 
                        ArrayList<FeedbackResponseAttributes>();
            } else if (response.giverEmail.equals(prevGiver) == false 
                    && prevGiver != null) {
                // New giver, add giver responses to response package for
                // one recipient
                responsesToOneRecipient.put(giverName,
                        responsesFromOneGiverToOneRecipient);
                // Clear response list
                responsesFromOneGiverToOneRecipient = new
                        ArrayList<FeedbackResponseAttributes>();
            }
            
            responsesFromOneGiverToOneRecipient.add(response);

            prevGiver = response.giverEmail;
            prevRecipient = response.recipientEmail;
            recipientName = this.getRecipientNameForResponse(
                    questions.get(response.feedbackQuestionId), response);
            recipientTeamName = this.getTeamNameForEmail(response.recipientEmail);
            recipientName = this.appendTeamNameToName(recipientName, recipientTeamName);
            giverName = this.getGiverNameForResponse(
                    questions.get(response.feedbackQuestionId), response);
            giverTeamName = this.getTeamNameForEmail(response.giverEmail);
            giverName = this.appendTeamNameToName(giverName, giverTeamName);
        }
        
        if (responses.isEmpty() == false ) {
            // Put responses for final giver
            responsesToOneRecipient.put(giverName,
                    responsesFromOneGiverToOneRecipient);
            sortedMap.put(recipientName, responsesToOneRecipient);
        }

        return sortedMap;
    }
    
    /**
     * Returns responses as a Map<giverName, Map<question, List<response>>>
     * Where the responses are sorted in the order of giver, question, recipient.
     * @param sortByTeam
     * @return responses sorted by Giver > Question > Recipient
     */
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
                    getResponsesSortedByGiverQuestionRecipient(boolean sortByTeam) {
        
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap
             = new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesFromOneGiver = null;
        List<FeedbackResponseAttributes> responsesFromOneGiverOneQuestion = null;
        
        if(sortByTeam){
            Collections.sort(responses, compareByTeamGiverQuestionTeamRecipient);
        } else {
            Collections.sort(responses, compareByGiverQuestionTeamRecipient);
        }
        
        String giver = null;
        String questionId = null;
        String giverName = null;
        
        for (FeedbackResponseAttributes response : responses) {
            if(giver == null || !response.giverEmail.equals(giver)){
                if(questionId!=null && responsesFromOneGiverOneQuestion!=null && responsesFromOneGiver!=null){
                    responsesFromOneGiver.put(questions.get(questionId), responsesFromOneGiverOneQuestion);
                }
                if(giver!=null && responsesFromOneGiver!=null){
                    sortedMap.put(giverName, responsesFromOneGiver);
                }
                responsesFromOneGiver = new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
                giver = response.giverEmail;
                giverName = this.getGiverNameForResponse(questions.get(response.feedbackQuestionId), response);
                questionId = null;
            }
            if(questionId == null || !response.feedbackQuestionId.equals(questionId)){
                if(questionId!=null && responsesFromOneGiverOneQuestion!=null){
                    responsesFromOneGiver.put(questions.get(questionId), responsesFromOneGiverOneQuestion);
                }
                responsesFromOneGiverOneQuestion = new ArrayList<FeedbackResponseAttributes>();
                questionId = response.feedbackQuestionId;
            }
            responsesFromOneGiverOneQuestion.add(response);
        }
        if(questionId!=null && responsesFromOneGiverOneQuestion!=null && responsesFromOneGiver!=null){
            responsesFromOneGiver.put(questions.get(questionId), responsesFromOneGiverOneQuestion);
        }
        if(giver!=null && responsesFromOneGiver!=null){

            sortedMap.put(giverName, responsesFromOneGiver);
        }
        
        return sortedMap;
    }
    
    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node using a {@code LinkedHashMap} implementation.
     * <br>The tree is sorted by giverName > recipientName > questionNumber.
     * <br>The key of each map represents the parent node, while the value represents the leaf.
     * <br>The top-most parent {@code String giverName} is the recipient's name of all it's leafs.
     * <br>The inner parent {@code String recipientName} is the giver's name of all it's leafs.
     * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
     * <br>with attributes corresponding to it's parents.
     * @return The responses in this bundle sorted by giver's name > recipient's name > question number.
     */
    public Map<String, Map<String, List<FeedbackResponseAttributes>>> getResponsesSortedByGiver() {
        return getResponsesSortedByGiver(false);
    }
    public Map<String, Map<String, List<FeedbackResponseAttributes>>> getResponsesSortedByGiver(boolean sortByTeam) {

        Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>();

        if(sortByTeam){
            Collections.sort(responses, compareByTeamGiverRecipientQuestion);
        } else {
            Collections.sort(responses, compareByGiverRecipientQuestion);
        }
        
        String prevRecipient = null;
        String prevGiver = null;
        String recipientName = null;
        String giverName = null;
        String recipientTeamName = null;
        String giverTeamName = null;
        
        List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                new ArrayList<FeedbackResponseAttributes>();
        Map<String, List<FeedbackResponseAttributes>> responsesFromOneGiver =
                new LinkedHashMap<String, List<FeedbackResponseAttributes>>();

        for (FeedbackResponseAttributes response : responses) {
            // New recipient, add response package to map.
            if (response.giverEmail.equals(prevGiver) == false
                    && prevGiver != null) {
                // Put previous recipient responses into inner map. 
                responsesFromOneGiver.put(recipientName,
                        responsesFromOneGiverToOneRecipient);
                // Put all responses for previous giver into outer map.
                sortedMap.put(giverName, responsesFromOneGiver);
                // Clear responses
                responsesFromOneGiver = new LinkedHashMap<String,
                        List<FeedbackResponseAttributes>>();
                responsesFromOneGiverToOneRecipient = new 
                        ArrayList<FeedbackResponseAttributes>();
            } else if (response.recipientEmail.equals(prevRecipient) == false 
                    && prevRecipient != null) {
                // New recipient, add recipient responses to response package for
                // one giver
                responsesFromOneGiver.put(recipientName,
                        responsesFromOneGiverToOneRecipient);
                // Clear response list
                responsesFromOneGiverToOneRecipient = new
                        ArrayList<FeedbackResponseAttributes>();
            }
            
            responsesFromOneGiverToOneRecipient.add(response);

            prevRecipient = response.recipientEmail;
            prevGiver = response.giverEmail;            
            recipientName = this.getRecipientNameForResponse(
                    questions.get(response.feedbackQuestionId), response);
            recipientTeamName = this.getTeamNameForEmail(response.recipientEmail);
            recipientName = this.appendTeamNameToName(recipientName, recipientTeamName);
            giverName = this.getGiverNameForResponse(
                    questions.get(response.feedbackQuestionId), response);
            giverTeamName = this.getTeamNameForEmail(response.giverEmail);
            giverName = this.appendTeamNameToName(giverName, giverTeamName);
        }
        
        if (responses.isEmpty() == false ) {
            // Put responses for final recipient
            responsesFromOneGiver.put(recipientName,
                    responsesFromOneGiverToOneRecipient);
            sortedMap.put(giverName, responsesFromOneGiver);
        }

        return sortedMap;
    }
    
    
    public boolean isRecipientReceivedSomething(String email) {
        for (FeedbackResponseAttributes response : responses) {
            if (!response.recipientEmail.equals(email)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isStudentHasSomethingNewToSee(StudentAttributes student) {
        for (FeedbackResponseAttributes response : responses) {
            // There is a response not written by the student 
            // which is visible to the student 
            if (!response.giverEmail.equals(student.email)) {
                return true;
            }
            
            // There is a response comment visible to the student
            if (responseComments.containsKey(response.getId())) {
                return true;
            }
        }

        return false;
    }
    
    public Map<String, Map<String, String>> getTeamNameToStudentsTableFromRoster(CourseRoster courseroster) {
        List<StudentAttributes> students = courseroster.getStudents();
        Map<String, Map<String, String>> teamNameToStudents = new HashMap<String, Map<String, String>>();
        
        for (StudentAttributes student : students) {
            String studentTeam = student.team; 
            Map<String, String> emailToName;
            
            if (teamNameToStudents.containsKey(studentTeam)) {
                emailToName = teamNameToStudents.get(studentTeam);
            } else {
                emailToName = new HashMap<String, String>();
            }
            
            emailToName.put(student.email, student.name);
            teamNameToStudents.put(studentTeam, emailToName);
        }
        
        List<InstructorAttributes> instructors = courseroster.getInstructors();
        String instructorsTeam = "Instructors";
        for (InstructorAttributes instructor : instructors) {
            Map<String, String> emailToName;
            
            if (teamNameToStudents.containsKey(instructorsTeam)) {
                emailToName = teamNameToStudents.get(instructorsTeam);
            } else {
                emailToName = new HashMap<String, String>();
            }
            
            emailToName.put(instructor.email, instructor.name);
            teamNameToStudents.put(instructorsTeam, emailToName);
        }
        
        return teamNameToStudents;
    }
    
    public Map<String, Set<String>> getSectionToTeamNamesFromRoster(CourseRoster courseroster) {
        List<StudentAttributes> students = courseroster.getStudents();
        Map<String, Set<String>> sectionToTeam = new HashMap<String, Set<String>>();
        
        for (StudentAttributes student : students) {
            String studentSection = student.section;
            String studentTeam = student.team; 
            Set<String> teamNames; 
            
            if (sectionToTeam.containsKey(studentSection)) {
                teamNames = sectionToTeam.get(studentSection);
            } else {
                teamNames = new HashSet<String>();
            }
            
            teamNames.add(studentTeam);
            sectionToTeam.put(studentSection, teamNames);
        }
        
        return sectionToTeam;
    }
    
    @SuppressWarnings("unused")
    private void ________________COMPARATORS_____________(){}
    
    // Sorts by giverName > recipientName
    public Comparator<FeedbackResponseAttributes> compareByGiverRecipient
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            int order = compareByNames(giverName1, giverName2);
            if(order != 0){
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);            
            order = compareByNames(recipientName1, recipientName2);
            
            if(order != 0){
                return order;
            }
            
            String resp1 = o1.getResponseDetails().getAnswerString();
            String resp2 = o2.getResponseDetails().getAnswerString();
            order = compareByNames(resp1, resp2);
            
            return order; 
        }
    };


    // Sorts by giverName > recipientName > qnNumber
    // General questions and team questions at the bottom.
    public Comparator<FeedbackResponseAttributes> compareByGiverRecipientQuestion
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {

            String giverSection1 = o1.giverSection;
            String giverSection2 = o2.giverSection;
            int order = giverSection1.compareTo(giverSection2);
            if(order != 0){
                return order;
            }


            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if(order != 0){
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);            
            order = compareByNames(recipientName1, recipientName2);
            if(order != 0){
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            return order; 
        }
    };

    // Sorts by teamName > giverName > recipientName > qnNumber
    public final Comparator<FeedbackResponseAttributes> compareByTeamGiverRecipientQuestion 
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2){
            
            String giverSection1 = o1.giverSection;
            String giverSection2 = o2.giverSection;
            int order = giverSection1.compareTo(giverSection2);
            if(order != 0){
                return order;
            }

            String t1 = getTeamNameForEmail(o1.giverEmail).equals("")?getNameForEmail(o1.giverEmail):getTeamNameForEmail(o1.giverEmail);
            String t2 = getTeamNameForEmail(o2.giverEmail).equals("")?getNameForEmail(o2.giverEmail):getTeamNameForEmail(o2.giverEmail);
            order = t1.compareTo(t2);
            if(order != 0){
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if(order != 0){
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);            
            order = compareByNames(recipientName1, recipientName2);
            if(order != 0){
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            return order; 
        }
        
    };
    
    //Sorts by recipientName > giverName > qnNumber
    public final Comparator<FeedbackResponseAttributes> compareByRecipientGiverQuestion
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {

            String recipientSection1 = o1.recipientSection;
            String recipientSection2 = o2.recipientSection;
            int order = recipientSection1.compareTo(recipientSection2);
            if(order != 0){
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if(order != 0){
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if(order != 0){
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            return order;
        }
    };

    //Sorts by teamName > recipientName > giverName > qnNumber
    public final Comparator<FeedbackResponseAttributes> compareByTeamRecipientGiverQuestion
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {
            
            String recipientSection1 = o1.recipientSection;
            String recipientSection2 = o2.recipientSection;
            int order = recipientSection1.compareTo(recipientSection2);
            if(order != 0){
                return order;
            }

            String t1 = getTeamNameForEmail(o1.recipientEmail).equals("")?getNameForEmail(o1.recipientEmail):getTeamNameForEmail(o1.recipientEmail);
            String t2 = getTeamNameForEmail(o2.recipientEmail).equals("")?getNameForEmail(o2.recipientEmail):getTeamNameForEmail(o2.recipientEmail);
            order = t1.compareTo(t2);
            if(order != 0){
                return order;
            }
            
            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if(order != 0){
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if(order != 0){
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            return order;
        }
    };
    
    // Sorts by giverName > question > recipientTeam > recipientName
    public final Comparator<FeedbackResponseAttributes> compareByGiverQuestionTeamRecipient
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {
            
            String giverSection1 = o1.giverSection;
            String giverSection2 = o2.giverSection;
            int order = giverSection1.compareTo(giverSection2);
            if(order != 0){
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if(order != 0){
                return order;
            }
            
            order = compareByQuestionNumber(o1, o2);
            if(order != 0){
                return order;
            }
            
            String t1 = getTeamNameForEmail(o1.recipientEmail).equals("")?getNameForEmail(o1.recipientEmail):getTeamNameForEmail(o1.recipientEmail);
            String t2 = getTeamNameForEmail(o2.recipientEmail).equals("")?getNameForEmail(o2.recipientEmail):getTeamNameForEmail(o2.recipientEmail);
            order = t1.compareTo(t2);
            if(order != 0){
                return order;
            }
            
            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            return order;
        }
    };
    
    // Sorts by giverTeam > giverName > question > recipientTeam > recipientName
    public final Comparator<FeedbackResponseAttributes> compareByTeamGiverQuestionTeamRecipient
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {

            String giverSection1 = o1.giverSection;
            String giverSection2 = o2.giverSection;
            int order = giverSection1.compareTo(giverSection2);
            if(order != 0){
                return order;
            }

            String giverTeam1 = getTeamNameForEmail(o1.giverEmail).equals("")?getNameForEmail(o1.giverEmail):getTeamNameForEmail(o1.giverEmail);
            String giverTeam2 = getTeamNameForEmail(o2.giverEmail).equals("")?getNameForEmail(o2.giverEmail):getTeamNameForEmail(o2.giverEmail);
            order = giverTeam1.compareTo(giverTeam2);
            if(order != 0){
                return order;
            }
            
            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if(order != 0){
                return order;
            }
            
            order = compareByQuestionNumber(o1, o2);
            if(order != 0){
                return order;
            }
            
            String receiverTeam1 = getTeamNameForEmail(o1.recipientEmail).equals("")?getNameForEmail(o1.recipientEmail):getTeamNameForEmail(o1.recipientEmail);
            String receiverTeam2 = getTeamNameForEmail(o2.recipientEmail).equals("")?getNameForEmail(o2.recipientEmail):getTeamNameForEmail(o2.recipientEmail);
            order = receiverTeam1.compareTo(receiverTeam2);
            if(order != 0){
                return order;
            }
            
            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            return order;
        }
    };
    
 // Sorts by recipientName > question > giverTeam > giverName
    public final Comparator<FeedbackResponseAttributes> compareByRecipientQuestionTeamGiver
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {

            String recipientSection1 = o1.recipientSection;
            String recipientSection2 = o2.recipientSection;
            int order = recipientSection1.compareTo(recipientSection2);
            if(order != 0){
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if(order != 0){
                return order;
            }
            
            order = compareByQuestionNumber(o1, o2);
            if(order != 0){
                return order;
            }
            
            String t1 = getTeamNameForEmail(o1.giverEmail).equals("")?getNameForEmail(o1.giverEmail):getTeamNameForEmail(o1.giverEmail);
            String t2 = getTeamNameForEmail(o2.giverEmail).equals("")?getNameForEmail(o2.giverEmail):getTeamNameForEmail(o2.giverEmail);
            order = t1.compareTo(t2);
            if(order != 0){
                return order;
            }
            
            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            return order;
        }
    };
    
    // Sorts by recipientTeam > recipientName > question > giverTeam > giverName
    public final Comparator<FeedbackResponseAttributes> compareByTeamRecipientQuestionTeamGiver
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {

            String recipientSection1 = o1.recipientSection;
            String recipientSection2 = o2.recipientSection;
            int order = recipientSection1.compareTo(recipientSection2);
            if(order != 0){
                return order;
            }
            
            String recipientTeam1 = getTeamNameForEmail(o1.recipientEmail).equals("")?getNameForEmail(o1.recipientEmail):getTeamNameForEmail(o1.recipientEmail);
            String recipientTeam2 = getTeamNameForEmail(o2.recipientEmail).equals("")?getNameForEmail(o2.recipientEmail):getTeamNameForEmail(o2.recipientEmail);
            order = recipientTeam1.compareTo(recipientTeam2);
            if(order != 0){
                return order;
            }
            
            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if(order != 0){
                return order;
            }
            
            order = compareByQuestionNumber(o1, o2);
            if(order != 0){
                return order;
            }
            
            String giverTeam1 = getTeamNameForEmail(o1.giverEmail).equals("")?getNameForEmail(o1.giverEmail):getTeamNameForEmail(o1.giverEmail);
            String giverTeam2 = getTeamNameForEmail(o2.giverEmail).equals("")?getNameForEmail(o2.giverEmail):getTeamNameForEmail(o2.giverEmail);
            order = giverTeam1.compareTo(giverTeam2);
            if(order != 0){
                return order;
            }
            
            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            return order;
        }
    };
    
    // Sorts by recipientTeam > question > recipientName > giverTeam > giverName
    public final Comparator<FeedbackResponseAttributes> compareByTeamQuestionRecipientTeamGiver
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {
            String recipientTeam1 = getTeamNameForEmail(o1.recipientEmail).equals("")?getNameForEmail(o1.recipientEmail):getTeamNameForEmail(o1.recipientEmail);
            String recipientTeam2 = getTeamNameForEmail(o2.recipientEmail).equals("")?getNameForEmail(o2.recipientEmail):getTeamNameForEmail(o2.recipientEmail);
            int order = recipientTeam1.compareTo(recipientTeam2);
            if(order != 0){
                return order;
            }
            
            order = compareByQuestionNumber(o1, o2);
            if(order != 0){
                return order;
            }
            
            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if(order != 0){
                return order;
            }
            
            String giverTeam1 = getTeamNameForEmail(o1.giverEmail).equals("")?getNameForEmail(o1.giverEmail):getTeamNameForEmail(o1.giverEmail);
            String giverTeam2 = getTeamNameForEmail(o2.giverEmail).equals("")?getNameForEmail(o2.giverEmail):getTeamNameForEmail(o2.giverEmail);
            order = giverTeam1.compareTo(giverTeam2);
            if(order != 0){
                return order;
            }
            
            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            return order;
        }
    };
    
 // Sorts by giverTeam > question > giverName > recipientTeam > recipientName
    public final Comparator<FeedbackResponseAttributes> compareByTeamQuestionGiverTeamRecipient
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {
            String giverTeam1 = getTeamNameForEmail(o1.giverEmail).equals("")?getNameForEmail(o1.giverEmail):getTeamNameForEmail(o1.giverEmail);
            String giverTeam2 = getTeamNameForEmail(o2.giverEmail).equals("")?getNameForEmail(o2.giverEmail):getTeamNameForEmail(o2.giverEmail);
            int order = giverTeam1.compareTo(giverTeam2);
            if(order != 0){
                return order;
            }
            
            order = compareByQuestionNumber(o1, o2);
            if(order != 0){
                return order;
            }
            
            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if(order != 0){
                return order;
            }
            
            String receiverTeam1 = getTeamNameForEmail(o1.recipientEmail).equals("")?getNameForEmail(o1.recipientEmail):getTeamNameForEmail(o1.recipientEmail);
            String receiverTeam2 = getTeamNameForEmail(o2.recipientEmail).equals("")?getNameForEmail(o2.recipientEmail):getTeamNameForEmail(o2.recipientEmail);
            order = receiverTeam1.compareTo(receiverTeam2);
            if(order != 0){
                return order;
            }
            
            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            return order;
        }
    };
    
    
    //Sorts by questionNumber
    public final Comparator<FeedbackResponseAttributes> compareByQuestionNumber
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {
            return compareByQuestionNumber(o1,o2);
        }
    };
    
    //Sorts by recipientName
    public final Comparator<FeedbackResponseAttributes> compareByRecipientName
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {
            return compareByNames(getNameForEmail(o1.recipientEmail),
                                getNameForEmail(o2.recipientEmail));
        }
    };
    
    //Sorts by recipientName
    public final Comparator<FeedbackResponseAttributes> compareByGiverName
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {
            return compareByNames(getNameForEmail(o1.giverEmail),
                                getNameForEmail(o2.giverEmail));
        }
    };
    
    //Sorts by recipientTeamName
    public final Comparator<FeedbackResponseAttributes> compareByRecipientTeamName
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {
            String t1 = getTeamNameForEmail(o1.recipientEmail).equals("")?getNameForEmail(o1.recipientEmail):getTeamNameForEmail(o1.recipientEmail);
            String t2 = getTeamNameForEmail(o2.recipientEmail).equals("")?getNameForEmail(o2.recipientEmail):getTeamNameForEmail(o2.recipientEmail);
            
            return t1.compareTo(t2);
        }
    };
    
    //Sorts by giverTeamName
    public final Comparator<FeedbackResponseAttributes> compareByGiverTeamName
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {
            String t1 = getTeamNameForEmail(o1.giverEmail).equals("")?getNameForEmail(o1.giverEmail):getTeamNameForEmail(o1.giverEmail);
            String t2 = getTeamNameForEmail(o2.giverEmail).equals("")?getNameForEmail(o2.giverEmail):getTeamNameForEmail(o2.giverEmail);
            
            return t1.compareTo(t2);
        }
    };
    
    private int compareByQuestionNumber(FeedbackResponseAttributes r1, FeedbackResponseAttributes r2) {
        FeedbackQuestionAttributes q1 = questions.get(r1.feedbackQuestionId);
        FeedbackQuestionAttributes q2 = questions.get(r2.feedbackQuestionId);        
        if (q1 == null || q2 == null) {
            return 0;
        } else {
            return q1.compareTo(q2);
        }
    }
    
    private int compareByNames(String n1, String n2) {
        
        // Make class feedback always appear on top, and team responses at bottom.
        int n1Priority = 0;
        int n2Priority = 0;
        
        if (n1.equals(Const.USER_IS_NOBODY)) {
            n1Priority = -1;
        } else if(n1.equals(Const.USER_IS_TEAM)) {
            n1Priority = 1;
        }
        if (n2.equals(Const.USER_IS_NOBODY)) {
            n2Priority = -1;
        } else if(n2.equals(Const.USER_IS_TEAM)) {
            n2Priority = 1;
        }
        
        int order = Integer.compare(n1Priority, n2Priority);
        return order == 0 ? n1.compareTo(n2) : order; 
    }
}
