package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    public Map<String, String> emailTeamNameTable = null;
    public Map<String, boolean[]> visibilityTable = null;
    public FeedbackSessionResponseStatus responseStatus = null;
    public Map<String, List<FeedbackResponseCommentAttributes>> responseComments = null;
    
    public FeedbackSessionResultsBundle (FeedbackSessionAttributes feedbackSession,
            List<FeedbackResponseAttributes> responses,
            Map<String, FeedbackQuestionAttributes> questions,
            Map<String, String> emailNameTable,
            Map<String, String> emailTeamNameTable,
            Map<String, boolean[]> visibilityTable,
            FeedbackSessionResponseStatus responseStatus,
            Map<String, List<FeedbackResponseCommentAttributes>> responseComments) {
        this.feedbackSession = feedbackSession;
        this.questions = questions;
        this.responses = responses;
        this.emailNameTable = emailNameTable;
        this.emailTeamNameTable = emailTeamNameTable;
        this.visibilityTable = visibilityTable;
        this.responseStatus = responseStatus;
        this.responseComments = responseComments;

        // We change user email to team name here for display purposes.
        for (FeedbackResponseAttributes response : responses) {
            if (questions.get(response.feedbackQuestionId).giverType == FeedbackParticipantType.TEAMS){ 
                response.giverEmail += Const.TEAM_OF_EMAIL_OWNER;
            }
        }
        
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
        
        //
        for (FeedbackResponseAttributes response : responses) {
            FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
            FeedbackParticipantType type = question.recipientType;
            
            //Recipient
            String name = emailNameTable.get(response.recipientEmail);
            if (visibilityTable.get(response.getId())[1] == false &&
                    type != FeedbackParticipantType.SELF) {
                String hash = Integer.toString(Math.abs(name.hashCode()));
                name = type.toSingularFormString();
                name = "Anonymous " + name + " " + hash;
                
                String anonEmail = name+"@@"+name+".com";
                emailNameTable.put(anonEmail, name);
                emailTeamNameTable.put(anonEmail, name + Const.TEAM_OF_EMAIL_OWNER);
                
                response.recipientEmail = anonEmail;
            }

            //Giver
            name = emailNameTable.get(response.giverEmail);
            type = question.giverType;
            if (visibilityTable.get(response.getId())[0] == false &&
                    type != FeedbackParticipantType.SELF) {
                String hash = Integer.toString(Math.abs(name.hashCode()));
                name = type.toSingularFormString();
                name = "Anonymous " + name + " " + hash;
                
                String anonEmail = name+"@@"+name+".com";
                emailNameTable.put(anonEmail, name);
                emailTeamNameTable.put(anonEmail, name + Const.TEAM_OF_EMAIL_OWNER);
                
                response.giverEmail = anonEmail;
            }
        }
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
             = new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        
        List<FeedbackQuestionAttributes> questionList =
                new ArrayList<FeedbackQuestionAttributes>(questions.values());
        
        Collections.sort(questionList);
        
        for (FeedbackQuestionAttributes question : questionList) {
            List<FeedbackResponseAttributes> responsesForQn =
                    new ArrayList<FeedbackResponseAttributes>();
            for (FeedbackResponseAttributes response : responses) {
                if(response.feedbackQuestionId.equals(question.getId())) {
                    responsesForQn.add(response);
                }
            }
            Collections.sort(responsesForQn, compareByRecipientGiverQuesion);
            sortedMap.put(question, responsesForQn);
        }
        
        return sortedMap;        
    }
    
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> 
                getQuestionResponseMapByRecipientTeam() {

        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap
            = new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForOneRecipient = null;
        List<FeedbackResponseAttributes> responsesForOneRecipientOneQuestion = null;
   
        Collections.sort(responses, compareByGiverName);
        Collections.sort(responses, compareByGiverTeamName);
        Collections.sort(responses, compareByRecipientName);
        Collections.sort(responses, compareByQuestionNumber);
        Collections.sort(responses, compareByRecipientTeamName);
        
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
        
        Collections.sort(responses, compareByRecipientName);
        Collections.sort(responses, compareByRecipientTeamName);
        Collections.sort(responses, compareByGiverName);
        Collections.sort(responses, compareByQuestionNumber);
        Collections.sort(responses, compareByGiverTeamName);
        
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
        
        Collections.sort(responses, compareByGiverName);
        Collections.sort(responses, compareByGiverTeamName);
        Collections.sort(responses, compareByQuestionNumber);
        Collections.sort(responses, compareByRecipientName);
        if(sortByTeam==true){
            Collections.sort(responses, compareByRecipientTeamName);
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

        Collections.sort(responses, compareByRecipientGiverQuesion);
        if(sortByTeam == true){
            Collections.sort(responses, compareByRecipientTeamName);
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
        
        Collections.sort(responses, compareByRecipientName);
        Collections.sort(responses, compareByRecipientTeamName);
        Collections.sort(responses, compareByQuestionNumber);
        Collections.sort(responses, compareByGiverName);
        if(sortByTeam==true){
            Collections.sort(responses, compareByGiverTeamName);
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

        Collections.sort(responses, compareByGiverRecipientQuestion);
        if(sortByTeam == true){
            Collections.sort(responses, compareByGiverTeamName);
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
    
    // Sorts by giverName > recipientName > qnNumber
    // General questions and team questions at the bottom.
    public Comparator<FeedbackResponseAttributes> compareByGiverRecipientQuestion
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {
            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);            
                
            int order = compareByNames(giverName1, giverName2);
            order = (order == 0 ? compareByNames(recipientName1, recipientName2) : order);
            return order == 0? compareByQuestionNumber(o1, o2) : order; 
        }
    };
    
    //Sorts by recipientName > giverName > qnNumber
    public final Comparator<FeedbackResponseAttributes> compareByRecipientGiverQuesion
        = new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                FeedbackResponseAttributes o2) {
            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            int order = compareByNames(recipientName1, recipientName2);
            order = (order == 0 ? compareByNames(giverName1, giverName2) : order);
            return order == 0 ? compareByQuestionNumber(o1, o2) : order; 
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
