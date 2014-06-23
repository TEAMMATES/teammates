package teammates.client.scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
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
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.logic.api.Logic;
import teammates.storage.api.CommentsDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.SubmissionsDb;
import teammates.storage.datastore.Datastore;

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

        try {
            //Opens a URL connection to obtain the entity modified logs
            URL myURL = new URL("http://4-18-dot-teammates-shawn.appspot.com/entityModifiedLogs");
            
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
            String[] id = tokens[1].split("[,|/]");
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
            Collection<String[]> ids = (Collection<String[]>) entityMap.get(entityType);
            
            Iterator<String[]> idit = ids.iterator();
            
            while(idit.hasNext()) {
                String[] id = idit.next();
                try {
                    EntityAttributes ea = retrieveEntity(entityType,id);
                    System.out.println("Retrieving:" + ea.toString());  
                } catch (ParseException e) {
                    System.out.println("Error while retrieving entities: " + e.getMessage());
                }     
            }
        }
    }
    
    private EntityAttributes retrieveEntity(String type, String[] id) throws ParseException {
        System.out.println(type);
        Logic logic = new Logic();
        switch(type) {
            case "Account":
                AccountAttributes account = logic.getAccount(id[0].trim());
                return account;
                
            case "Comment":
                CommentsDb commentDb = new CommentsDb();
                CommentAttributes comment = commentDb.getComment(id[0].trim(),id[1].trim(),id[2].trim(),new Text(id[3].trim()), new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").parse(id[4].trim()));
                return comment;
                
            case "Course":
                CourseAttributes course = logic.getCourse(id[0].trim());
                return course;
                
            case "Evaluation":
                EvaluationAttributes evaluation = logic.getEvaluation(id[0].trim(), id[1].trim());
                return evaluation;
                
            case "Feedback Question":
                FeedbackQuestionsDb feedbackQuestionDb = new FeedbackQuestionsDb();
                FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionDb.getFeedbackQuestion(id[0].trim(), id[1].trim(), Integer.parseInt(id[2].trim()));
                return feedbackQuestion;
                
            case "Feedback Response":
                FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
                FeedbackResponseAttributes feedbackResponse = feedbackResponsesDb.getFeedbackResponse(id[0].trim(), id[1].split(":")[0], id[1].split(":")[1]);
                return feedbackResponse;
                
            case "FeedbackResponseComment":
                FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
                FeedbackResponseCommentAttributes feedbackResponseComment = feedbackResponseCommentsDb.getFeedbackResponseComment(id[0].trim(), id[1].trim(), new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").parse(id[2].trim()));
                return feedbackResponseComment;
                
            case "Feedback Session":
                FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(id[0].trim(), id[1].trim());
                return feedbackSession;
                
            case "Instructor":
                InstructorAttributes instructor = logic.getInstructorForEmail(id[1].trim(), id[0].trim());
                return instructor;
            
            case "Student":
                StudentAttributes student = logic.getStudentForEmail(id[0].trim(), id[1].trim());
                return student;
                
            case "Submission":
                SubmissionsDb submissionsDb = new SubmissionsDb();
                SubmissionAttributes submission = submissionsDb.getSubmission(id[0].trim(), id[1].trim(), id[2].split("to:")[1].trim(), id[3].split("from:")[1].trim());
                return submission;
        }
        return null;
    }
}
