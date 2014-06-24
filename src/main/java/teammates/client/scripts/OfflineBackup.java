package teammates.client.scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.repackaged.org.apache.commons.collections.MultiMap;
import com.google.appengine.repackaged.org.apache.commons.collections.map.MultiValueMap;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.storage.api.CommentsDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.SubmissionsDb;
import teammates.storage.datastore.Datastore;
import teammates.test.driver.TestProperties;
import teammates.test.util.FileHelper;

public class OfflineBackup extends RemoteApiClient {
    
    public static void main(String[] args) throws IOException {
        OfflineBackup offlineBackup = new OfflineBackup();
        offlineBackup.doOperationRemotely();
    }
    
    protected void doOperation() {
        Datastore.initialize();
        Vector<String> logs = getModifiedLogs();
        retrieveAllEntities(mapModifiedEntities(logs));
    }
    
    
    private Vector<String> getModifiedLogs() {
        Vector<String> modifiedLogs = new Vector<String>();
        TestProperties testProperties = TestProperties.inst();
        try {
            //Opens a URL connection to obtain the entity modified logs
            
            URL myURL = new URL(testProperties.TEAMMATES_URL + "/entityModifiedLogs");
                    //"http://4-18-dot-teammates-shawn2.appspot.com/entityModifiedLogs");
            
            URLConnection myURLConnection = myURL.openConnection();        
        
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    myURLConnection.getInputStream()));
            String logMessage;
            while ((logMessage = in.readLine()) != null) {
                System.out.println(logMessage);
                modifiedLogs.add(logMessage);
            }
            in.close();
        } 
        
        catch (IOException e) { 
            // new URL() failed
            // ...
        } 
        
        return modifiedLogs;
    }
    
   
    private MultiMap mapModifiedEntities(Vector<String> modifiedLogs) {
        
        //Removes all duplicates using a set
        Set<String> entities = new HashSet<String>();
        for(String entity : modifiedLogs) {
            entities.add(entity);
        }
        
        //Puts all the entities into a multimap based on entity type to make 
        //it easier to retrieve all entities of a certain type
        Iterator<String> it = entities.iterator();
  
        MultiMap entitiesMap = new MultiValueMap();
        
        while(it.hasNext()) {
            String entity = it.next();
            String tokens[] = entity.split("::");
            String type = tokens[0];
            String id = tokens[1];
            entitiesMap.put(type, id);
        }
        
        return entitiesMap;
    }
    
    @SuppressWarnings("unchecked")
    private void retrieveAllEntities(MultiMap entityMap) {

        Set<String> keys = entityMap.keySet();
        Iterator<String> it = keys.iterator();
        
        while(it.hasNext()) {
            String entityType = it.next();
            Collection<String> ids = (Collection<String>) entityMap.get(entityType);
            
            Iterator<String> idit = ids.iterator();
            
            while(idit.hasNext()) {
                String id = idit.next();
                try {
                    retrieveEntity(entityType,id);
                    
                } catch (ParseException e) {
                    System.out.println("Error while retrieving entities: " + e.getMessage());
                }     
            }
        }
    }
    
    private void retrieveEntity(String type, String id) throws ParseException {
        System.out.println(type);
        
        switch(type) {
            case "Account":
                retrieveAndSaveAccount(id);
                break;
                
            case "Comment":
                retrieveAndSaveComment(id);
                break;
               
            case "Course":
                retrieveAndSaveCourse(id);
                break;
                
            case "Evaluation":
                retrieveAndSaveEvaluation(id);
                break;
                
            case "Feedback Question":
                retrieveAndSaveFeedbackQuestion(id);
                break;
                
            case "Feedback Response":
                retrieveAndSaveFeedbackResponse(id);
                break;
                
            case "FeedbackResponseComment":
                retrieveAndSaveFeedbackResponseComment(id);
                break;
                
            case "Feedback Session":
                retrieveAndSaveFeedbackSession(id);
                break;
                
            case "Instructor":
                retrieveAndSaveInstructor(id);
                break;
            
            case "Student":
                retrieveAndSaveStudent(id);
                break;
                
            case "Submission":
                retrieveAndSaveSubmission(id);
                break;
        }
        
    }
    
    private void retrieveAndSaveAccount(String id) {
        Logic logic = new Logic();
        String googleId = id.trim();
        AccountAttributes account = logic.getAccount(googleId);
        String accountCsv = account.googleId + "," + account.name + "," + account.isInstructor + "," + account.email + "," + account.institute + "," + account.createdAt + Const.EOL;
        FileHelper.appendToFile("account.csv", accountCsv);
    }
    
    private void retrieveAndSaveComment(String id) throws ParseException {
        CommentsDb commentDb = new CommentsDb();
        String[] idTokens = id.split("|");
        String courseId = idTokens[0].trim();
        String giverEmail = idTokens[1].trim();
        String receiverEmail = idTokens[2].trim();
        Text commentText = new Text(idTokens[3].trim());
        Date date = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").parse(idTokens[4].trim());
        CommentAttributes comment = commentDb.getComment(courseId, giverEmail, receiverEmail, commentText, date);
        String commentCsv = comment.getCommentId() + "," + comment.courseId + "," + comment.giverEmail + "," + comment.receiverEmail + "," + date + "," + commentText + Const.EOL;
        FileHelper.appendToFile("comment.csv", commentCsv);
    }
    
    private void retrieveAndSaveCourse(String id) {
        Logic logic = new Logic();
        String courseId = id.trim();
        CourseAttributes course = logic.getCourse(courseId);
        String createdAt = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(course.createdAt);
        String courseCsv = course.id + "," + course.name + "," + createdAt + "," + course.isArchived + Const.EOL;
        FileHelper.appendToFile("course.csv", courseCsv);
    }
    
    private void retrieveAndSaveEvaluation(String id) {
        Logic logic = new Logic();
        String[] idTokens = id.split("|");
        String courseId = idTokens[0].trim();
        String evaluationName = idTokens[1].trim();
        EvaluationAttributes evaluation = logic.getEvaluation(courseId, evaluationName);
        String startTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(evaluation.startTime);
        String endTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(evaluation.endTime);
        String evaluationCsv = evaluation.courseId + "," + evaluation.name + "," + evaluation.instructions.getValue() + "," + startTime + "," + endTime + "," +
                evaluation.timeZone + "," + evaluation.gracePeriod + "," + evaluation.p2pEnabled + "," + evaluation.published + "," + evaluation.activated + Const.EOL;
        FileHelper.appendToFile("evaluation.csv", evaluationCsv);
    }
    
    private void retrieveAndSaveFeedbackQuestion(String id) {
        FeedbackQuestionsDb feedbackQuestionDb = new FeedbackQuestionsDb();
        String[] idTokens = id.split("/");
        String feedbackSessionName = idTokens[0].trim();
        String courseId = idTokens[1].trim();
        int questionNumber = Integer.parseInt(idTokens[2].trim());
        FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionDb.getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);
        String feedbackQuestionCsv = feedbackQuestion.getId() + "," + feedbackQuestion.feedbackSessionName + "," + feedbackQuestion.courseId + "," +
                                        feedbackQuestion.creatorEmail + "," + feedbackQuestion.questionMetaData.getValue() + "," + feedbackQuestion.questionNumber +
                                        feedbackQuestion.questionType + "," + feedbackQuestion.giverType + "," + feedbackQuestion.recipientType + "," +
                                        feedbackQuestion.numberOfEntitiesToGiveFeedbackTo + "," + feedbackQuestion.showResponsesTo + "," + feedbackQuestion.showGiverNameTo +
                                        feedbackQuestion.showRecipientNameTo + Const.EOL;
        FileHelper.appendToFile("feedbackQuestion.csv", feedbackQuestionCsv);
    }
    
    private void retrieveAndSaveFeedbackResponse(String id) {
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        String[] idTokens = id.split("/");
        String feedbackQuestionId = idTokens[0].trim();
        String giverEmail = idTokens[1].split(":")[0].trim();
        String receiverEmail = idTokens[1].split(":")[1].trim();
        
        FeedbackResponseAttributes feedbackResponse = feedbackResponsesDb.getFeedbackResponse(feedbackQuestionId, giverEmail, receiverEmail);
        String feedbackResponseCsv = feedbackResponse.getId() + "," + feedbackResponse.feedbackSessionName + "," + feedbackResponse.courseId + "," +
                                        feedbackResponse.feedbackQuestionId + "," + feedbackResponse.feedbackQuestionType + "," + feedbackResponse.giverEmail +
                                        "," + feedbackResponse.recipientEmail + "," + feedbackResponse.responseMetaData.getValue() + Const.EOL;
        FileHelper.appendToFile("feedbackResponse.csv", feedbackResponseCsv);
    }
    
    private void retrieveAndSaveFeedbackResponseComment(String id) throws ParseException {
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
        String[] idTokens = id.split("|");
        String feedbackResponseId = idTokens[0].trim();
        String giverEmail = idTokens[1].trim();
        Date createdAt = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").parse(idTokens[2].trim());
        FeedbackResponseCommentAttributes feedbackResponseComment = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseId, giverEmail, createdAt);
        String feedbackResponseCommentCsv = feedbackResponseComment.getId() + "," + feedbackResponseComment.courseId + "," + feedbackResponseComment.feedbackSessionName +
                                                "," + feedbackResponseComment.feedbackQuestionId + "," + feedbackResponseComment.giverEmail + "," + 
                                                feedbackResponseComment.feedbackResponseId + "," + createdAt + "," +
                                                feedbackResponseComment.commentText.getValue() + Const.EOL;
        FileHelper.appendToFile("feedbackResponseComment.csv", feedbackResponseCommentCsv);
    }
    
    private void retrieveAndSaveFeedbackSession(String id) {
        Logic logic = new Logic();
        String[] idTokens = id.split("/");
        String feedbackSessionName = idTokens[0].trim();
        String courseId = idTokens[1].trim();
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
        
        String createdTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(feedbackSession.createdTime);
        String startTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(feedbackSession.startTime);
        String endTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(feedbackSession.endTime);
        String sessionVisibleFromTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(feedbackSession.sessionVisibleFromTime);
        String resultsVisibleFromTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(feedbackSession.resultsVisibleFromTime);
        
        String feedbackSessionCsv = feedbackSessionName + "%" + courseId + "," + feedbackSessionName + "," + courseId + "," + feedbackSession.creatorEmail + "," +
                                    feedbackSession.instructions.getValue() + "," + createdTime + "," + startTime + "," + endTime + "," + sessionVisibleFromTime + "," +
                                    resultsVisibleFromTime + "," + feedbackSession.timeZone + "," + feedbackSession.gracePeriod + "," + feedbackSession.feedbackSessionType +
                                    "," + feedbackSession.sentOpenEmail + "," + feedbackSession.sentPublishedEmail + "," + feedbackSession.isOpeningEmailEnabled + "," +
                                    feedbackSession.isClosingEmailEnabled + "," + feedbackSession.isPublishedEmailEnabled + Const.EOL;
        FileHelper.appendToFile("feedbackSession.csv", feedbackSessionCsv);
    }
    
    private void retrieveAndSaveInstructor(String id) {
        Logic logic = new Logic();
        String[] idTokens = id.split(",");
        String courseId = idTokens[1].trim();
        String email = idTokens[0].trim();
        InstructorAttributes instructor = logic.getInstructorForEmail(courseId, email);
        String instructorCsv = instructor.googleId + "," + instructor.name + "," + instructor.email + "," + instructor.courseId + "," + instructor.key + Const.EOL;
        FileHelper.appendToFile("instructor.csv", instructorCsv);
    }
    
    private void retrieveAndSaveStudent(String id) {
        Logic logic = new Logic();
        String[] idTokens = id.split("/");
        String courseId = idTokens[0].trim();
        String email = idTokens[1].trim();
        StudentAttributes student = logic.getStudentForEmail(courseId, email);
        String studentCsv = student.googleId + "," + student.name + "," + student.email + "," + student.course + "," + student.comments + "," + student.team + 
                            "," + student.key + Const.EOL;
        FileHelper.appendToFile("student.csv", studentCsv);
    }
    
    private void retrieveAndSaveSubmission(String id) {
        SubmissionsDb submissionsDb = new SubmissionsDb();
        String[] idTokens = id.split("[/|]");
        String courseId = idTokens[0].trim();
        String evaluationName = idTokens[1].trim();
        String toStudent = idTokens[2].split("to:")[1].trim();
        String fromStudent = idTokens[3].split("from:")[1].trim();
        SubmissionAttributes submission = submissionsDb.getSubmission(courseId, evaluationName, toStudent, fromStudent);
        String submissionCsv = submission.course + "," + submission.evaluation + "," + submission.team + "," + submission.reviewer + "," + submission.reviewee + "," +
                                submission.points + "," + submission.justification.getValue() + "," + submission.p2pFeedback.getValue() + Const.EOL;
        FileHelper.appendToFile("submission.csv", submissionCsv);
    }
}
