package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.util.Const;
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
    public Map<String, boolean[]> visibilityTable = null;
    public FeedbackSessionResponseStatus responseStatus = null;
    public Map<String, List<FeedbackResponseCommentAttributes>> responseComments = null;
    public boolean isComplete;    

	 
    /**
     * Responses with identities of giver/recipients NOT hidden.
     * To be used for anonymous result calculation only, and identities hidden before showing to users.
     */
    public List<FeedbackResponseAttributes> actualResponses = null;
    

    //For contribution questions.
    //Key is questionId, value is a map of student email to StudentResultSumary
    public Map<String, Map<String, StudentResultSummary>> contributionQuestionStats =
            new HashMap<String, Map<String, StudentResultSummary>>();

    
    public FeedbackSessionResultsBundle (FeedbackSessionAttributes feedbackSession,
            List<FeedbackResponseAttributes> responses,
            Map<String, FeedbackQuestionAttributes> questions,
            Map<String, String> emailNameTable,
            Map<String, String> emailLastNameTable,
            Map<String, String> emailTeamNameTable,
            Map<String, boolean[]> visibilityTable,
            FeedbackSessionResponseStatus responseStatus,
            Map<String, List<FeedbackResponseCommentAttributes>> responseComments){
        this(feedbackSession, responses, questions, emailNameTable, emailLastNameTable, emailTeamNameTable, visibilityTable, responseStatus, responseComments, true);
    }

    public FeedbackSessionResultsBundle (FeedbackSessionAttributes feedbackSession,
            List<FeedbackResponseAttributes> responses,
            Map<String, FeedbackQuestionAttributes> questions,
            Map<String, String> emailNameTable,
            Map<String, String> emailLastNameTable,
            Map<String, String> emailTeamNameTable,
            Map<String, boolean[]> visibilityTable,
            FeedbackSessionResponseStatus responseStatus,
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
    
    private String getAnonEmailFromEmail(String email) {
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
    
    /**
     * Used for instructor feedback results views.
     */
    @SuppressWarnings("static-access")
    public String getResponseAnswerHtml(FeedbackResponseAttributes response, FeedbackQuestionAttributes question){
        FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
        if(question.questionType == FeedbackQuestionType.CONTRIB){
            String responseAnswerHtml = response.getResponseDetails().getAnswerHtml(questionDetails);
            if(response.giverEmail.equals(response.recipientEmail)){
                //For CONTRIB qns, We want to show PC if giver == recipient.
                Map<String, StudentResultSummary> stats = getContribQnStats(question);
                StudentResultSummary studentResult = stats.get(response.giverEmail);
                if(studentResult != null){
                    int pc = studentResult.perceivedToInstructor;
                    String pcHtml = ((FeedbackContributionQuestionDetails) questionDetails).convertToEqualShareFormatHtml(pc);
                    responseAnswerHtml += "<span>&nbsp;&nbsp;["
                            + "Perceived Contribution: "
                            + pcHtml
                            + "]</span>";
                }
            }
            return responseAnswerHtml;
        } else {
            return response.getResponseDetails().getAnswerHtml(questionDetails);
        }
    }
    
    private Map<String, StudentResultSummary> getContribQnStats(FeedbackQuestionAttributes question) {
        Map<String, StudentResultSummary> contribQnStats = contributionQuestionStats.get(question.getId());
        if(contribQnStats == null){
            FeedbackContributionQuestionDetails fqcd = (FeedbackContributionQuestionDetails) question.getQuestionDetails();
            contribQnStats = fqcd.getStudentResults(this, question);
            
            //Convert email to anonEmail and add stats.
            Map<String, StudentResultSummary> anonContribQnStats = new HashMap<String, StudentResultSummary>();
            for(Map.Entry<String, StudentResultSummary> entry : contribQnStats.entrySet()){
                anonContribQnStats.put(getAnonEmailFromEmail(entry.getKey()), entry.getValue());
            }
            for(Map.Entry<String, StudentResultSummary> entry : anonContribQnStats.entrySet()){
                if(contribQnStats.get(entry.getKey()) == null){
                    contribQnStats.put(entry.getKey(), entry.getValue());
                }
            }
            
            contributionQuestionStats.put(question.getId(), contribQnStats);
        }
        
        return contribQnStats;
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
