package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.ui.template.FeedbackResponseComment;
import teammates.ui.template.FeedbackResultsQuestionDetails;
import teammates.ui.template.FeedbackResultsResponse;
import teammates.ui.template.FeedbackResultsResponseTable;
import teammates.ui.template.StudentFeedbackResultsQuestionWithResponses;

public class StudentFeedbackResultsPageData extends PageData {
    private FeedbackSessionResultsBundle bundle = null;
    private String registerMessage;
    private List<StudentFeedbackResultsQuestionWithResponses> feedbackResultsQuestionsWithResponses;
    
    public StudentFeedbackResultsPageData(AccountAttributes account, StudentAttributes student) {
        super(account, student);
    }
    
    public void init(Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses) {
        
        String joinUrl = new Url(Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
                                                   .withRegistrationKey(student.key)
                                                   .withStudentEmail(student.email)
                                                   .withCourseId(student.course)
                                                   .toString();
        
        registerMessage = String.format(Const.StatusMessages.UNREGISTERED_STUDENT_RESULTS, 
                                            student.name, joinUrl);
        createFeedbackResultsQuestionsWithResponses(questionsWithResponses);
    }

    public FeedbackSessionResultsBundle getBundle() {
        return bundle;
    }
    
    public AccountAttributes getAccount() {
        return account;
    }
    
    public String getRegisterMessage() {
        return registerMessage;
    }

    public List<StudentFeedbackResultsQuestionWithResponses> getFeedbackResultsQuestionsWithResponses() {
        return feedbackResultsQuestionsWithResponses;
    }
    
    /**
     * Parses the contents of the map and keeps only those data which will be displayed on the browser
     * @param questionsWithResponses Question with all responses
     */
    private void createFeedbackResultsQuestionsWithResponses(
                              Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses) {
        
        feedbackResultsQuestionsWithResponses = new ArrayList<StudentFeedbackResultsQuestionWithResponses>();
        int questionIndex = 1;
        
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> 
                                   questionWithResponses : questionsWithResponses.entrySet()) {
            
            FeedbackQuestionAttributes question = questionWithResponses.getKey();
            List<FeedbackResponseAttributes> responsesBundle = questionWithResponses.getValue();
            FeedbackQuestionDetails questionDetailsBundle = question.getQuestionDetails(); 
            
            /* Contain only those attributes which will be displayed on the page */
            FeedbackResultsQuestionDetails questionDetails = createQuestionDetails(
                                                                 questionIndex, question, questionDetailsBundle, responsesBundle);
            List<FeedbackResultsResponseTable> responseTables = createResponseTables(question, responsesBundle);
            
            feedbackResultsQuestionsWithResponses.add(new StudentFeedbackResultsQuestionWithResponses(questionDetails, responseTables));
            questionIndex++;
            
        }
    }
    
    public void setBundle(FeedbackSessionResultsBundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Parses question details which will be displayed on the browser
     * @param questionIndex
     * @param question
     * @param questionDetailsBundle
     * @param responsesBundle  Responses for the question
     * @return Only those details which will be displayed on the page are returned
     */
    private FeedbackResultsQuestionDetails createQuestionDetails(
                                    int questionIndex, FeedbackQuestionAttributes question, 
                                    FeedbackQuestionDetails questionDetailsBundle, 
                                    List<FeedbackResponseAttributes> responsesBundle) {
        
        String questionText = questionDetailsBundle.questionText;
        String additionalInfo = questionDetailsBundle.getQuestionAdditionalInfoHtml(questionIndex, "");
        String studentEmail = (student != null) ? student.email : null;
        String questionResultStatistics = questionDetailsBundle.getQuestionResultStatisticsHtml(
                                                                    responsesBundle, question, studentEmail,
                                                                    bundle, "student");

        boolean isIndividualResponsesShownToStudents = questionDetailsBundle.isIndividualResponsesShownToStudents();
        
        return new FeedbackResultsQuestionDetails(Integer.toString(questionIndex), questionText, additionalInfo, 
                                                      questionResultStatistics, isIndividualResponsesShownToStudents);
    }
    
    /**
     * Create feedback results responses tables for every recipient
     * @param question  Question for which the responses are generated
     * @param responsesBundle  All responses for a question
     * @return List of feedback results response tables for a question
     */
    private List<FeedbackResultsResponseTable> createResponseTables(
                                    FeedbackQuestionAttributes question, List<FeedbackResponseAttributes> responsesBundle) {

        List<FeedbackResultsResponseTable> responseTables = new ArrayList<FeedbackResultsResponseTable>();        
        List<String> recipients = new ArrayList<String>();
        
        for (FeedbackResponseAttributes singleResponse : responsesBundle) {
            if (!recipients.contains(singleResponse.recipientEmail)) {
                recipients.add(singleResponse.recipientEmail);
            }           
        }
        
        for (String recipientEmail : recipients) {
            List<FeedbackResponseAttributes> responsesBundleForRecipient = filterResponsesByRecipientEmail(recipientEmail, responsesBundle);
            responseTables.add(createResponseTable(question, responsesBundleForRecipient));
        }
        return responseTables;
    }

    /**
     * Creates a feedback results responses table for a recipient
     * @param question  Question for which the responses are generated
     * @param responsesBundleForRecipient  All responses for the question having a particular recipient 
     * @return Feedback results responses table for a question and a recipient
     */
    private FeedbackResultsResponseTable createResponseTable(FeedbackQuestionAttributes question, 
                                    List<FeedbackResponseAttributes> responsesBundleForRecipient) {
        
        List<FeedbackResultsResponse> responses = new ArrayList<FeedbackResultsResponse>();
        String recipientName = responsesBundleForRecipient == null || responsesBundleForRecipient.isEmpty() ?
                                 "" : bundle.getRecipientNameForResponse(responsesBundleForRecipient.get(0));
     
        for (FeedbackResponseAttributes singleResponse : responsesBundleForRecipient) {
            String giverName = bundle.getGiverNameForResponse(singleResponse);

            /* Change display name to 'You' or 'Your team' if necessary */
            if (question.giverType == FeedbackParticipantType.TEAMS) {
                if (student.team.equals(giverName)) {
                    giverName = "Your Team (" + giverName + ")";
                }
            } else if (student.email.equals(singleResponse.giverEmail)) {
                giverName = "You";
            }
            
            if (question.recipientType == FeedbackParticipantType.TEAMS) {
                if (student.team.equals(singleResponse.recipientEmail) && 
                      !(recipientName.startsWith("Your Team (") && recipientName.endsWith(")"))) { // To avoid duplicate replacement
                    recipientName = "Your Team (" + recipientName + ")";
                }
            } else if (student.email.equals(singleResponse.recipientEmail)
                       && student.name.equals(recipientName)) {
                recipientName = "You";
            }

            /* If the giver is the same user, show the real name of the receiver */
            if (giverName.equals("You") && (!recipientName.equals("You"))) {
                recipientName = bundle.getNameForEmail(singleResponse.recipientEmail);
            }
            
            String answer = singleResponse.getResponseDetails().getAnswerHtml(question.getQuestionDetails());
            List<FeedbackResponseComment> comments = createStudentFeedbackResultsResponseComments(
                                                                                          singleResponse.getId());
            
            responses.add(new FeedbackResultsResponse(giverName, answer, comments));
        }
        return new FeedbackResultsResponseTable(recipientName, responses);
    }

    /**
     * Creates a list of comments for a feedback results response
     * @param feedbackResponseId  Response ID for which comments are created
     * @return Comments for the response
     */
    private List<FeedbackResponseComment> createStudentFeedbackResultsResponseComments(
                                                                               String feedbackResponseId) {
        
        List<FeedbackResponseComment> comments = new ArrayList<FeedbackResponseComment>();
        List<FeedbackResponseCommentAttributes> commentsBundle = bundle.responseComments.get(feedbackResponseId);
        
        if (commentsBundle != null) {
            for (FeedbackResponseCommentAttributes comment : commentsBundle) { 
                comments.add(new FeedbackResponseComment(comment, comment.giverEmail));
            }
        }        
        return comments;
    }

    /**
     * Filters responses by recipient's email
     * @param recipientEmail  Check whether a response's recipient email is equal to this parameter
     * @param responsesBundle  All responses for a question
     * @return Responses whose recipient email is equal to the parameter
     */
    private List<FeedbackResponseAttributes> filterResponsesByRecipientEmail(
                                    String recipientEmail, List<FeedbackResponseAttributes> responsesBundle) {
        
        List<FeedbackResponseAttributes> responsesForRecipient = new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponseAttributes singleResponse : responsesBundle) {
            if (singleResponse.recipientEmail.equals(recipientEmail)) {
                responsesForRecipient.add(singleResponse);
            }           
        }
        return responsesForRecipient;
    }    
}
