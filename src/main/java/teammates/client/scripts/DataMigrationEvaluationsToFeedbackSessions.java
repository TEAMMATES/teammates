package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackResponseDetails;
import teammates.common.datatransfer.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.FeedbackTextResponseDetails;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.SubmissionsDb;
import teammates.storage.datastore.Datastore;

import com.google.appengine.api.datastore.Text;

/**
 * Migrates Evaluations and Submissions to FeedbackSessions and Responses.
 * Feedback session/question creator will be any instructor from the course.
 */
public class DataMigrationEvaluationsToFeedbackSessions extends RemoteApiClient {
    
    protected static Logic logic = new Logic();
    protected static EvaluationsDb evalsDb = new EvaluationsDb();
    protected static SubmissionsDb subDb = new SubmissionsDb();
    protected static FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    
    public static void main(String[] args) throws IOException {
        final long startTime = System.currentTimeMillis();
        DataMigrationEvaluationsToFeedbackSessions migrator = new DataMigrationEvaluationsToFeedbackSessions();
        migrator.doOperationRemotely();
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms" );
    }

    @Override
    protected void doOperation() {
        Datastore.initialize();
        
        /**
         * Specify courseId, evaluationName, and new feedback session name here.
         * If new feedbackSessionName already exists, a number in brackets will be appended.
         */
        String courseId = "example.gma-demo";
        String evaluationName = "First Evaluation";
        String newFeedbackSessionName = "Migrated FeedbackSession";
        
        EvaluationAttributes evalAttribute = logic.getEvaluation(courseId, evaluationName);
        if(evalAttribute != null){
            convertOneEvaluationToFeedbackSession(evalAttribute , newFeedbackSessionName);
        } else {
            System.out.println("Specified evaluation not found." + courseId + " : " + evaluationName);
        }
        
        //Converts all evaluations to feedback sessions
        //convertEvaluationsToFeedbackSessions();
    }
    
    @SuppressWarnings("deprecation")
    protected void convertEvaluationsToFeedbackSessions(){
        List<EvaluationAttributes> allEvaluations = evalsDb.getAllEvaluations();
        
        System.out.println(allEvaluations.size() + " evaluations found.");
        
        int fsNum = fsDb.getAllFeedbackSessions().size();
        
        for(EvaluationAttributes evalAttribute : allEvaluations){
            convertOneEvaluationToFeedbackSession(evalAttribute, "");
        }

        System.out.println(allEvaluations.size() + " evaluations found and migrated.");
        System.out.println("Original number of FS: " + fsNum);
        System.out.println("After migration number of FS: " + fsDb.getAllFeedbackSessions().size());
    }
    
    protected void convertOneEvaluationToFeedbackSession(EvaluationAttributes eval, String newFeedbackSessionName){

        if(newFeedbackSessionName == null || newFeedbackSessionName.isEmpty()){
            newFeedbackSessionName = "Migrated - " + eval.name;
        }
        
        //Create FeedbackSession
        int num = 0;
        
        
        String feedbackSessionName = newFeedbackSessionName + (num==0 ? "" : ("("+num+")"));//Use same name, or if exists, use "<name>(<num>)"
        String courseId = eval.courseId;
        
        String instEmail = logic.getInstructorsForCourse(courseId).get(0).email;//Use email of any instructor in the course.
        
        String creatorEmail = instEmail;
        Text instructions = eval.instructions;
        Date createdTime = (new Date()).compareTo(eval.startTime) > 0 ? new Date() : eval.startTime; //Now, or opening time if start time is earlier.
        Date startTime = eval.startTime;
        Date endTime = eval.endTime;
        Date sessionVisibleFromTime = eval.startTime;
        Date resultsVisibleFromTime = eval.endTime;
        double timeZone = eval.timeZone;
        int gracePeriod = eval.gracePeriod;
        FeedbackSessionType feedbackSessionType = FeedbackSessionType.STANDARD;
        boolean sentOpenEmail = (new Date()).compareTo(eval.startTime) > 0; //Assume sent openEmail if now > startTime
        boolean sentPublishedEmail = eval.published; //If eval is already published, assume email already sent.
        boolean isOpeningEmailEnabled = true; //Default value
        boolean isClosingEmailEnabled = true; //Default value
        boolean isPublishedEmailEnabled = true; //Default value
        
        while(true){ //Loop to retry with a different name if entity already exists.
            
            feedbackSessionName = newFeedbackSessionName + (num==0 ? "" : ("("+num+")"));//Use same name, or if exists, use "<name>(<num>)"
            
            FeedbackSessionAttributes fsa = new FeedbackSessionAttributes(feedbackSessionName,
                    courseId, creatorEmail, instructions,
                    createdTime, startTime, endTime,
                    sessionVisibleFromTime, resultsVisibleFromTime,
                    timeZone, gracePeriod,
                    feedbackSessionType, sentOpenEmail, sentPublishedEmail,
                    isOpeningEmailEnabled, isClosingEmailEnabled, isPublishedEmailEnabled);
            
            try {
                fsDb.createEntity(fsa);
                break;
            } catch (InvalidParametersException e) {
                System.out.println("Something went wrong.");
                e.printStackTrace();
                break;
            } catch (EntityAlreadyExistsException e) {
                System.out.println("Entity already exists, retrying with a different name.");
                e.printStackTrace();
            }
            
            num++;
        }
        

        boolean peerFeedback = eval.p2pEnabled;
        
        //Create feedback questions
        createFeedbackQuestions(eval, feedbackSessionName, courseId, creatorEmail, peerFeedback);
        
        
        //Get feedbackQuestionIds
        List<String> fqIds = new ArrayList<String>();
        for(int i=1 ; i<=(peerFeedback?5:3) ; i++){
            fqIds.add(logic.getFeedbackQuestion(feedbackSessionName, courseId, i).getId());
        }
        
        //Create feedback Responses
        List<SubmissionAttributes> allSubmissions = subDb.getSubmissionsForEvaluation(courseId, eval.name);
        for(SubmissionAttributes sub : allSubmissions){
            
            //Create Feedback Responses for Submission
            
            createFeedbackResponsesFromSubmission(feedbackSessionName,
                    courseId, peerFeedback, fqIds, sub);
            
        }
        
        
    }

    private void createFeedbackResponsesFromSubmission(
            String feedbackSessionName, String courseId, boolean peerFeedback,
            List<String> fqIds, SubmissionAttributes sub) {
        String giver = sub.reviewer;
        String recipient = sub.reviewee;
        String giverSection = "";
        String recipientSection = "";
        
        StudentAttributes studentGiver = logic.getStudentForEmail(courseId, giver);
        giverSection = (studentGiver == null) ? Const.DEFAULT_SECTION : studentGiver.section;
        StudentAttributes studentRecipient = logic.getStudentForEmail(courseId, recipient);
        recipientSection = (studentRecipient == null) ? Const.DEFAULT_SECTION : studentRecipient.section;
        
        FeedbackResponseAttributes q1Response = null;
        FeedbackResponseAttributes q2Response = null;
        FeedbackResponseAttributes q3Response = null;
        FeedbackResponseAttributes q4Response = null;
        FeedbackResponseAttributes q5Response = null;
        
        //Question 1 Response: Contribution points
        if(sub.points != Const.POINTS_NOT_SUBMITTED){
            q1Response = new FeedbackResponseAttributes();
            q1Response.setId(null);//null for new response.
            q1Response.feedbackSessionName = feedbackSessionName;
            q1Response.courseId = courseId;
            q1Response.giverEmail = giver;
            q1Response.giverSection = giverSection;
            q1Response.recipientEmail = recipient;
            q1Response.recipientSection = recipientSection;
            
            q1Response.feedbackQuestionType = FeedbackQuestionType.CONTRIB;
            q1Response.feedbackQuestionId = fqIds.get(0);
            FeedbackResponseDetails responseDetails1 = new FeedbackContributionResponseDetails(sub.points);
            q1Response.setResponseDetails(responseDetails1);
        }
        
        if(giver.equals(recipient)){
            //Question 2 Response: Essay Question "Comments about my contribution(shown to other teammates)"
            q2Response = new FeedbackResponseAttributes();
            q2Response.setId(null);//null for new response.
            q2Response.feedbackSessionName = feedbackSessionName;
            q2Response.courseId = courseId;
            q2Response.giverEmail = giver;
            q2Response.giverSection = giverSection;
            q2Response.recipientEmail = recipient;
            q2Response.recipientSection = recipientSection;
            
            q2Response.feedbackQuestionType = FeedbackQuestionType.TEXT;
            q2Response.feedbackQuestionId = fqIds.get(1);
            FeedbackResponseDetails responseDetails2 = new FeedbackTextResponseDetails(sub.justification.getValue());
            q2Response.setResponseDetails(responseDetails2);
        } else {
            //Question 3 Response: Essay Question "My comments about this teammate(confidential and only shown to instructor)"
            q3Response = new FeedbackResponseAttributes();
            q3Response.setId(null);//null for new response.
            q3Response.feedbackSessionName = feedbackSessionName;
            q3Response.courseId = courseId;
            q3Response.giverEmail = giver;
            q3Response.giverSection = giverSection;
            q3Response.recipientEmail = recipient;
            q3Response.recipientSection = recipientSection;
            
            q3Response.feedbackQuestionType = FeedbackQuestionType.TEXT;
            q3Response.feedbackQuestionId = fqIds.get(2);
            FeedbackResponseDetails responseDetails3 = new FeedbackTextResponseDetails(sub.justification.getValue());
            q3Response.setResponseDetails(responseDetails3);
        }
        
        if(peerFeedback){
            if(giver.equals(recipient)){
                //Question 4 Response: Essay Question "Comments about team dynamics(confidential and only shown to instructor)"
                q4Response = new FeedbackResponseAttributes();
                q4Response.setId(null);//null for new response.
                q4Response.feedbackSessionName = feedbackSessionName;
                q4Response.courseId = courseId;
                q4Response.giverEmail = giver;
                q4Response.giverSection = giverSection;
                q4Response.recipientEmail = recipient;
                q4Response.recipientSection = recipientSection;
                
                q4Response.feedbackQuestionType = FeedbackQuestionType.TEXT;
                q4Response.feedbackQuestionId = fqIds.get(3);
                FeedbackResponseDetails responseDetails4 = new FeedbackTextResponseDetails(sub.p2pFeedback.getValue());
                q4Response.setResponseDetails(responseDetails4);
            } else {
                //Question 5 Response: Essay Question "My feedback to this teammate(shown anonymously to the teammate)"
                q5Response = new FeedbackResponseAttributes();
                q5Response.setId(null);//null for new response.
                q5Response.feedbackSessionName = feedbackSessionName;
                q5Response.courseId = courseId;
                q5Response.giverEmail = giver;
                q5Response.giverSection = giverSection;
                q5Response.recipientEmail = recipient;
                q5Response.recipientSection = recipientSection;
                
                q5Response.feedbackQuestionType = FeedbackQuestionType.TEXT;
                q5Response.feedbackQuestionId = fqIds.get(4);
                FeedbackResponseDetails responseDetails5 = new FeedbackTextResponseDetails(sub.p2pFeedback.getValue());
                q5Response.setResponseDetails(responseDetails5);
            }
        }
        
        //Save responses
        List<FeedbackResponseAttributes> allResponses = new ArrayList<FeedbackResponseAttributes>();
        allResponses.add(q1Response);
        allResponses.add(q2Response);
        allResponses.add(q3Response);
        allResponses.add(q4Response);
        allResponses.add(q5Response);
        
        for(FeedbackResponseAttributes response : allResponses){
            if(response != null){
                if(!(response.responseMetaData.getValue().isEmpty() || 
                        response.recipientEmail.isEmpty())){
                    try {
                        logic.createFeedbackResponse(response);
                    } catch (EntityAlreadyExistsException e) {
                        System.out.println("Response already exists.");
                        e.printStackTrace();
                    } catch (InvalidParametersException e) {
                        System.out.println("Invalid Parameters for Response");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void createFeedbackQuestions(EvaluationAttributes eval,
            String feedbackSessionName, String courseId, String creatorEmail, boolean peerFeedback) {
        //Question 1: Contribution Question
        createQuestion1(feedbackSessionName, courseId, creatorEmail);
        
        //Question 2: Essay Question "Comments about my contribution(shown to other teammates)"
        createQuestion2(feedbackSessionName, courseId, creatorEmail);
        
        //Question3: Essay Question "My comments about this teammate(confidential and only shown to instructor)"
        createQuestion3(feedbackSessionName, courseId, creatorEmail);
        
        //Questions below are only enabled if peer to peer feedback is enabled.
        if(peerFeedback){
            //Question 4: Essay Question "Comments about team dynamics(confidential and only shown to instructor)"
            createQuestion4(feedbackSessionName, courseId, creatorEmail);
            
            //Question 5: Essay Question "My feedback to this teammate(shown anonymously to the teammate)"
            createQuestion5(feedbackSessionName, courseId, creatorEmail);
        }
    }

    private void createQuestion5(String feedbackSessionName, String courseId,
            String creatorEmail) {
        FeedbackQuestionAttributes feedbackQuestion = new FeedbackQuestionAttributes();
        feedbackQuestion.creatorEmail = creatorEmail;
        feedbackQuestion.courseId = courseId;
        feedbackQuestion.feedbackSessionName = feedbackSessionName;
        
        feedbackQuestion.questionNumber = 5;
        feedbackQuestion.questionType = FeedbackQuestionType.TEXT;
        feedbackQuestion.giverType = FeedbackParticipantType.STUDENTS;
        feedbackQuestion.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;
        feedbackQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        
        List<FeedbackParticipantType> showResponsesTo = new ArrayList<FeedbackParticipantType>();
        List<FeedbackParticipantType> showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        List<FeedbackParticipantType> showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        
        showResponsesTo.add(FeedbackParticipantType.INSTRUCTORS);
        showResponsesTo.add(FeedbackParticipantType.RECEIVER);
        showGiverNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        showRecipientNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        showRecipientNameTo.add(FeedbackParticipantType.RECEIVER);
        
        feedbackQuestion.showResponsesTo = showResponsesTo;
        feedbackQuestion.showGiverNameTo = showGiverNameTo;
        feedbackQuestion.showRecipientNameTo = showRecipientNameTo;
        
        String questionText = "My feedback to this teammate(shown anonymously to the teammate)";
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails(questionText );
        feedbackQuestion.setQuestionDetails(questionDetails);
        
        try {
            logic.createFeedbackQuestion(feedbackQuestion);
        } catch (InvalidParametersException e) {
            System.out.println("Failed to create Feedback Question 5 =(");
            e.printStackTrace();
        }
    }

    private void createQuestion4(String feedbackSessionName, String courseId,
            String creatorEmail) {
        FeedbackQuestionAttributes feedbackQuestion = new FeedbackQuestionAttributes();
        feedbackQuestion.creatorEmail = creatorEmail;
        feedbackQuestion.courseId = courseId;
        feedbackQuestion.feedbackSessionName = feedbackSessionName;
        
        feedbackQuestion.questionNumber = 4;
        feedbackQuestion.questionType = FeedbackQuestionType.TEXT;
        feedbackQuestion.giverType = FeedbackParticipantType.STUDENTS;
        feedbackQuestion.recipientType = FeedbackParticipantType.OWN_TEAM;
        feedbackQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        
        List<FeedbackParticipantType> showResponsesTo = new ArrayList<FeedbackParticipantType>();
        List<FeedbackParticipantType> showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        List<FeedbackParticipantType> showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        
        showResponsesTo.add(FeedbackParticipantType.INSTRUCTORS);
        showGiverNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        showRecipientNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        
        feedbackQuestion.showResponsesTo = showResponsesTo;
        feedbackQuestion.showGiverNameTo = showGiverNameTo;
        feedbackQuestion.showRecipientNameTo = showRecipientNameTo;
        
        String questionText = "Comments about team dynamics(confidential and only shown to instructor)";
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails(questionText );
        feedbackQuestion.setQuestionDetails(questionDetails);
        
        try {
            logic.createFeedbackQuestion(feedbackQuestion);
        } catch (InvalidParametersException e) {
            System.out.println("Failed to create Feedback Question 4 =(");
            e.printStackTrace();
        }
    }

    private void createQuestion3(String feedbackSessionName, String courseId,
            String creatorEmail) {
        FeedbackQuestionAttributes feedbackQuestion = new FeedbackQuestionAttributes();
        feedbackQuestion.creatorEmail = creatorEmail;
        feedbackQuestion.courseId = courseId;
        feedbackQuestion.feedbackSessionName = feedbackSessionName;
        
        feedbackQuestion.questionNumber = 3;
        feedbackQuestion.questionType = FeedbackQuestionType.TEXT;
        feedbackQuestion.giverType = FeedbackParticipantType.STUDENTS;
        feedbackQuestion.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;
        feedbackQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        
        List<FeedbackParticipantType> showResponsesTo = new ArrayList<FeedbackParticipantType>();
        List<FeedbackParticipantType> showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        List<FeedbackParticipantType> showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        
        showResponsesTo.add(FeedbackParticipantType.INSTRUCTORS);
        showGiverNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        showRecipientNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        
        feedbackQuestion.showResponsesTo = showResponsesTo;
        feedbackQuestion.showGiverNameTo = showGiverNameTo;
        feedbackQuestion.showRecipientNameTo = showRecipientNameTo;
        
        String questionText = "My comments about this teammate(confidential and only shown to instructor)";
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails(questionText );
        feedbackQuestion.setQuestionDetails(questionDetails);
        
        try {
            logic.createFeedbackQuestion(feedbackQuestion);
        } catch (InvalidParametersException e) {
            System.out.println("Failed to create Feedback Question 3 =(");
            e.printStackTrace();
        }
    }

    private void createQuestion2(String feedbackSessionName, String courseId,
            String creatorEmail) {
        FeedbackQuestionAttributes feedbackQuestion = new FeedbackQuestionAttributes();
        feedbackQuestion.creatorEmail = creatorEmail;
        feedbackQuestion.courseId = courseId;
        feedbackQuestion.feedbackSessionName = feedbackSessionName;
        
        feedbackQuestion.questionNumber = 2;
        feedbackQuestion.questionType = FeedbackQuestionType.TEXT;
        feedbackQuestion.giverType = FeedbackParticipantType.STUDENTS;
        feedbackQuestion.recipientType = FeedbackParticipantType.SELF;
        feedbackQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        
        List<FeedbackParticipantType> showResponsesTo = new ArrayList<FeedbackParticipantType>();
        List<FeedbackParticipantType> showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        List<FeedbackParticipantType> showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        
        showResponsesTo.add(FeedbackParticipantType.INSTRUCTORS);
        showResponsesTo.add(FeedbackParticipantType.RECEIVER);
        showResponsesTo.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        showResponsesTo.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        showGiverNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        showGiverNameTo.add(FeedbackParticipantType.RECEIVER);
        showGiverNameTo.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        showGiverNameTo.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        showRecipientNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        showRecipientNameTo.add(FeedbackParticipantType.RECEIVER);
        showRecipientNameTo.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        showRecipientNameTo.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        
        feedbackQuestion.showResponsesTo = showResponsesTo;
        feedbackQuestion.showGiverNameTo = showGiverNameTo;
        feedbackQuestion.showRecipientNameTo = showRecipientNameTo;
        
        String questionText = "Comments about my contribution(shown to other teammates)";
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails(questionText );
        feedbackQuestion.setQuestionDetails(questionDetails);
        
        try {
            logic.createFeedbackQuestion(feedbackQuestion);
        } catch (InvalidParametersException e) {
            System.out.println("Failed to create Feedback Question 2 =(");
            e.printStackTrace();
        }
    }

    private void createQuestion1(String feedbackSessionName, String courseId,
            String creatorEmail) {
        FeedbackQuestionAttributes feedbackQuestion = new FeedbackQuestionAttributes();
        feedbackQuestion.creatorEmail = creatorEmail;
        feedbackQuestion.courseId = courseId;
        feedbackQuestion.feedbackSessionName = feedbackSessionName;
        
        feedbackQuestion.questionNumber = 1;
        feedbackQuestion.questionType = FeedbackQuestionType.CONTRIB;
        feedbackQuestion.giverType = FeedbackParticipantType.STUDENTS;
        feedbackQuestion.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;
        feedbackQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        
        List<FeedbackParticipantType> showResponsesTo = new ArrayList<FeedbackParticipantType>();
        List<FeedbackParticipantType> showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        List<FeedbackParticipantType> showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        
        showResponsesTo.add(FeedbackParticipantType.INSTRUCTORS);
        showResponsesTo.add(FeedbackParticipantType.RECEIVER);
        showResponsesTo.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        showResponsesTo.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        showGiverNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        showRecipientNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        showRecipientNameTo.add(FeedbackParticipantType.RECEIVER);
        
        feedbackQuestion.showResponsesTo = showResponsesTo;
        feedbackQuestion.showGiverNameTo = showGiverNameTo;
        feedbackQuestion.showRecipientNameTo = showRecipientNameTo;
        
        String questionText = "Please rate the estimated contribution of the following recipients.";
        FeedbackContributionQuestionDetails questionDetails = new FeedbackContributionQuestionDetails(questionText );
        feedbackQuestion.setQuestionDetails(questionDetails);
        
        try {
            logic.createFeedbackQuestion(feedbackQuestion);
        } catch (InvalidParametersException e) {
            System.out.println("Failed to create Feedback Question 1 =(");
            e.printStackTrace();
        }
    }
}
