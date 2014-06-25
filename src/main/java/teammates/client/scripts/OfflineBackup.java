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

import com.google.appengine.api.datastore.KeyFactory;
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
    
    private final String accountCsvFile = "account.csv";
    private final String commentCsvFile = "comment.csv";
    private final String courseCsvFile = "course.csv";
    private final String evaluationCsvFile = "evaluation.csv";
    private final String feedbackQuestionCsvFile = "feedbackQuestion.csv";
    private final String feedbackResponseCsvFile = "feedbackResponse.csv";
    private final String feedbackResponseCommentCsvFile = "feedbackResponseComment.csv";
    private final String feedbackSessionCsvFile = "feedbackSession.csv";
    private final String InstructorCsvFile = "instructor.csv";
    private final String studentCsvFile = "student.csv";
    private final String submissionCsvFile = "submission.csv";
    
    public static void main(String[] args) throws IOException {
        OfflineBackup offlineBackup = new OfflineBackup();
        offlineBackup.doOperationRemotely();
    }
    
    protected void doOperation() {
        Datastore.initialize();
        Vector<String> logs = getModifiedLogs();
        setupCsvFiles();
        retrieveAllEntities(mapModifiedEntities(logs));
    }
    
    /**
     * Opens a connection to the entityModifiedLogs servlet to retrieve a log of all recently modified entities
     */
    private Vector<String> getModifiedLogs() {
        Vector<String> modifiedLogs = new Vector<String>();
        TestProperties testProperties = TestProperties.inst();
        try {
            //Opens a URL connection to obtain the entity modified logs
            URL myURL = new URL(testProperties.TEAMMATES_URL + "/entityModifiedLogs");
            
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
            System.out.println("Error occurred while trying to access modified entity logs: " + e.getMessage());
        } 
        
        return modifiedLogs;
    }
    
   
    /**
     * Look through the logs and extracts all recently modified entities. Duplicates are removed
     * and the entities are placed into a multimap based on their types (instructor, student etc)
     */
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
    
    /** 
     *  Looks through an entity map to obtain all entities that were modified recently. Those entities are 
     *  then retrieved for backup.
     */
    @SuppressWarnings("unchecked")
    private void retrieveAllEntities(MultiMap entityMap) {

        Set<String> keys = entityMap.keySet();
        Iterator<String> it = keys.iterator();
        
        while(it.hasNext()) {
            String entityType = it.next();
            Collection<String> entityIds = (Collection<String>) entityMap.get(entityType);
            
            Iterator<String> entityIdsIt = entityIds.iterator();
            
            while(entityIdsIt.hasNext()) {
                String id = entityIdsIt.next();
                try {
                    retrieveEntity(entityType,id);
                    
                } catch (ParseException e) {
                    System.out.println("Error while retrieving entities: " + e.getMessage());
                }     
            }
        }
    }
    
    /**
     * Retrieve a recently modified entity and saves its contents to a .csv file
     */
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
        
        if(account == null) {
            return;
        }
        
        String accountCsv = account.googleId + "," + account.name + "," + account.isInstructor + "," + account.email + "," + account.institute + "," + account.createdAt + Const.EOL;
        FileHelper.appendToFile(accountCsvFile, accountCsv);
    }
    
    private void retrieveAndSaveComment(String id) throws ParseException {
        CommentsDb commentDb = new CommentsDb();
        String[] idTokens = id.split("\\|");
        String courseId = idTokens[0].trim();
        String giverEmail = idTokens[1].trim();
        String receiverEmail = idTokens[2].trim();
        Text commentText = new Text(idTokens[3].trim());
        Date date = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").parse(idTokens[4].trim());
        CommentAttributes comment = commentDb.getComment(courseId, giverEmail, receiverEmail, commentText, date);
        
        if(comment == null) {
            return;
        }
        
        String commentCsv = comment.getCommentId() + "," + comment.courseId + "," + comment.giverEmail + "," + comment.receiverEmail + "," + date + "," + 
                                commentText.getValue() + Const.EOL;
        FileHelper.appendToFile(commentCsvFile, commentCsv);
    }
    
    private void retrieveAndSaveCourse(String id) {
        Logic logic = new Logic();
        String courseId = id.trim();
        CourseAttributes course = logic.getCourse(courseId);
        
        if(course == null) {
            return;
        }
        
        String createdAt = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(course.createdAt);
        String courseCsv = course.id + "," + course.name + "," + createdAt + "," + course.isArchived + Const.EOL;
        FileHelper.appendToFile(courseCsvFile, courseCsv);
    }
    
    private void retrieveAndSaveEvaluation(String id) {
        Logic logic = new Logic();
        String[] idTokens = id.split("\\|");
        String courseId = idTokens[0].trim();
        String evaluationName = idTokens[1].trim();
        EvaluationAttributes evaluation = logic.getEvaluation(courseId, evaluationName);
        
        if(evaluation == null) {
            System.out.println("Eval is: " + courseId + "|" + evaluationName + "|" + id + "|" + idTokens.length);
            return;
        }
        
        String startTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(evaluation.startTime);
        String endTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(evaluation.endTime);
        String evaluationCsv = evaluation.getId() + "," + evaluation.courseId + "," + evaluation.name + "," + evaluation.instructions.getValue() + "," + startTime + 
                "," + endTime + "," + evaluation.timeZone + "," + evaluation.gracePeriod + "," + evaluation.p2pEnabled + "," + evaluation.published + "," + 
                evaluation.activated + Const.EOL;
        FileHelper.appendToFile(evaluationCsvFile, evaluationCsv);
    }
    
    private void retrieveAndSaveFeedbackQuestion(String id) {
        FeedbackQuestionsDb feedbackQuestionDb = new FeedbackQuestionsDb();
        String[] idTokens = id.split("/");
        String feedbackSessionName = idTokens[0].trim();
        String courseId = idTokens[1].trim();
        int questionNumber = Integer.parseInt(idTokens[2].trim());
        FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionDb.getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);
        
        if(feedbackQuestion == null) {
            return;
        }
        
        String feedbackQuestionCsv = feedbackQuestion.getId() + "," + feedbackQuestion.feedbackSessionName + "," + feedbackQuestion.courseId + "," +
                                        feedbackQuestion.creatorEmail + "," + feedbackQuestion.questionMetaData.getValue() + "," + feedbackQuestion.questionNumber + "," +
                                        feedbackQuestion.questionType + "," + feedbackQuestion.giverType + "," + feedbackQuestion.recipientType + "," +
                                        feedbackQuestion.numberOfEntitiesToGiveFeedbackTo + "," + feedbackQuestion.showResponsesTo + "," + feedbackQuestion.showGiverNameTo +
                                        feedbackQuestion.showRecipientNameTo + Const.EOL;
        FileHelper.appendToFile(feedbackQuestionCsvFile, feedbackQuestionCsv);
    }
    
    private void retrieveAndSaveFeedbackResponse(String id) {
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        String[] idTokens = id.split("/");
        String feedbackQuestionId = idTokens[0].trim();
        String giverEmail = idTokens[1].split(":")[0].trim();
        String receiverEmail = idTokens[1].split(":")[1].trim();
        
        FeedbackResponseAttributes feedbackResponse = feedbackResponsesDb.getFeedbackResponse(feedbackQuestionId, giverEmail, receiverEmail);
        
        if(feedbackResponse == null) {
            return;
        }
        
        String feedbackResponseCsv = feedbackResponse.getId() + "," + feedbackResponse.feedbackSessionName + "," + feedbackResponse.courseId + "," +
                                        feedbackResponse.feedbackQuestionId + "," + feedbackResponse.feedbackQuestionType + "," + feedbackResponse.giverEmail +
                                        "," + feedbackResponse.recipientEmail + "," + feedbackResponse.responseMetaData.getValue() + Const.EOL;
        FileHelper.appendToFile(feedbackResponseCsvFile, feedbackResponseCsv);
    }
    
    private void retrieveAndSaveFeedbackResponseComment(String id) throws ParseException {
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
        String[] idTokens = id.split("\\|");
        String feedbackResponseId = idTokens[0].trim();
        String giverEmail = idTokens[1].trim();
        Date createdAt = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").parse(idTokens[2].trim());
        FeedbackResponseCommentAttributes feedbackResponseComment = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponseId, giverEmail, createdAt);
        
        if(feedbackResponseComment == null) {
            return;
        }
        
        String feedbackResponseCommentCsv = feedbackResponseComment.getId() + "," + feedbackResponseComment.courseId + "," + feedbackResponseComment.feedbackSessionName +
                                                "," + feedbackResponseComment.feedbackQuestionId + "," + feedbackResponseComment.giverEmail + "," + 
                                                feedbackResponseComment.feedbackResponseId + "," + createdAt + "," +
                                                feedbackResponseComment.commentText.getValue() + Const.EOL;
        FileHelper.appendToFile(feedbackResponseCommentCsvFile, feedbackResponseCommentCsv);
    }
    
    private void retrieveAndSaveFeedbackSession(String id) {
        Logic logic = new Logic();
        String[] idTokens = id.split("/");
        String feedbackSessionName = idTokens[0].trim();
        String courseId = idTokens[1].trim();
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
        
        if(feedbackSession == null) {
            return;
        }
        
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
        FileHelper.appendToFile(feedbackSessionCsvFile, feedbackSessionCsv);
    }
    
    private void retrieveAndSaveInstructor(String id) {
        Logic logic = new Logic();
        String[] idTokens = id.split(",");
        String courseId = idTokens[1].trim();
        String email = idTokens[0].trim();
        InstructorAttributes instructor = logic.getInstructorForEmail(courseId, email);
        
        if(instructor == null) {
            return;
        }
        
        String instructorCsv = instructor.getId() + "," + instructor.googleId + "," + instructor.name + "," + instructor.email + "," + instructor.courseId + 
                                "," + instructor.key + Const.EOL;
        FileHelper.appendToFile(InstructorCsvFile, instructorCsv);
    }
    
    private void retrieveAndSaveStudent(String id) {
        Logic logic = new Logic();
        String[] idTokens = id.split("/");
        String courseId = idTokens[0].trim();
        String email = idTokens[1].trim();
        StudentAttributes student = logic.getStudentForEmail(courseId, email);
        
        if(student == null) {
            return;
        }
        
        Long key = KeyFactory.stringToKey(student.key).getId();
        String studentCsv = key + "," + student.googleId + "," + student.name + "," + student.email + "," + student.course + "," + student.comments + 
                                "," + student.team + Const.EOL;
        FileHelper.appendToFile(studentCsvFile, studentCsv);
    }
    
    private void retrieveAndSaveSubmission(String id) {
        SubmissionsDb submissionsDb = new SubmissionsDb();
        String[] idTokens = id.split("[/|]");
        String courseId = idTokens[0].trim();
        String evaluationName = idTokens[1].trim();
        String toStudent = idTokens[2].split("to:")[1].trim();
        String fromStudent = idTokens[3].split("from:")[1].trim();
        SubmissionAttributes submission = submissionsDb.getSubmission(courseId, evaluationName, toStudent, fromStudent);
        
        if(submission == null) {
            return;
        }
        
        String submissionCsv = submission.getId() + "," + submission.course + "," + submission.evaluation + "," + submission.team + "," + submission.reviewer + "," 
                                + submission.reviewee + "," + submission.points + "," + submission.justification.getValue() + "," + 
                                submission.p2pFeedback.getValue() + Const.EOL;
        FileHelper.appendToFile(submissionCsvFile, submissionCsv);
    }
    
    /**
     * Prepares all entity types .csv files with their headers extracted from GAE datastore
     */
    private void setupCsvFiles() {
        String accountCsv = "key,name,isInstructor,email,institute,createAt" + Const.EOL;
        FileHelper.writeToFile(accountCsvFile, accountCsv);
        
        String commentCsv = "key,courseID,giverEmail,receiverEmail,commentText,createdAt" + Const.EOL;
        FileHelper.writeToFile(commentCsvFile, commentCsv);
        
        String courseCsv = "key,name,createdAt,archiveStatus" + Const.EOL;
        FileHelper.writeToFile(courseCsvFile, courseCsv);
        
        String evaluationCsv = "key,courseID,name,longInstructions,startTime,endTime,timeZone,gracePeriod,commentsEnabled,published,activated" + Const.EOL;
        FileHelper.writeToFile(evaluationCsvFile, evaluationCsv);
        
        String feedbackQuestionCsv = "key,feedbackSessionName,courseID,creatorEmail,questionText,questionNumber,questionType,giverType,recipientType,"
                + "numberOfEntitiesToGiveFeedbackTo,showResponsesTo,showGiverNameTo,showRecipientNameTo" + Const.EOL;
        FileHelper.writeToFile(feedbackQuestionCsvFile, feedbackQuestionCsv);
        
        String feedbackResponseCsv = "key,feedbackSessionName,courseID,feedbackQuestionID,feedbackQuestionType,giverEmail,receiver,answer" + Const.EOL;
        FileHelper.writeToFile(feedbackResponseCsvFile, feedbackResponseCsv);
        
        String feedbackResponseCommentCsv = "key,courseID,feedbackSessionName,feedbackQuestionID,giverEmail,feedbackResponseID,createdAt,commentText" + Const.EOL;
        FileHelper.writeToFile(feedbackResponseCommentCsvFile, feedbackResponseCommentCsv);
        
        String feedbackSessionCsv = "key,feedbackSessionName,courseID,creatorEmail,instructions,createdTime,startTime,endTime,sessionVisibleFromTime,"
                + "resultsVisibleFromTime,timeZoneDouble,gracePeriod,feedbackSessionType,sentOpenEmail,sentPublishedEmail,isOpeningEmailEnabled,isClosingEmailEnabled,"
                + "isPublishedEmailEnabled" + Const.EOL;
        FileHelper.writeToFile(feedbackSessionCsvFile, feedbackSessionCsv);
        
        String instructorCsv = "key,name,email,courseID,registrationKey" + Const.EOL;
        FileHelper.writeToFile(InstructorCsvFile, instructorCsv);
        
        String studentCsv = "key,ID,name,email,courseID,comments,teamName" + Const.EOL;
        FileHelper.writeToFile(studentCsvFile, studentCsv);
        
        String submissionCsv = "key,courseID,evaluationName,teamName,fromStudent,toStudent,points,justification,commentsToStudent" + Const.EOL;
        FileHelper.writeToFile(submissionCsvFile, submissionCsv); 
    }
}
