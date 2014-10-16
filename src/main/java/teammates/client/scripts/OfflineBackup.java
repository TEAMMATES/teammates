package teammates.client.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.appengine.repackaged.com.google.api.client.util.Charsets;
import com.google.gson.Gson;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Utils;
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
    private String backupFileDirectory = "";
    private String currentFileName = "";
    private boolean hasPreviousEntity = false;
    
    public static void main(String[] args) throws IOException {
        OfflineBackup offlineBackup = new OfflineBackup();
        offlineBackup.doOperationRemotely();
    }
    
    protected void doOperation() {
        Datastore.initialize();
        Vector<String> logs = getModifiedLogs();
        Set<String> courses = extractModifiedCourseIds(logs);
        backupFileDirectory = "Backup " + getCurrentDateAndTime();
        createBackupDirectory(backupFileDirectory);
        retrieveEntitiesByCourse(courses);
        Gson gson = Utils.getTeammatesGson();
        
        String jsonString = FileHelper.readFile(currentFileName, Charsets.UTF_8);
        DataBundle data = gson.fromJson(jsonString, DataBundle.class);
      
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
    private Set<String> extractModifiedCourseIds(Vector<String> modifiedLogs) {
        
        //Extracts the course Ids to be backup from the logs
        Set<String> courses = new HashSet<String>();
        for(String course : modifiedLogs) {
            course = course.trim();
            if(!course.equals("")) {
                courses.add(course);
            }
            
        }
        return courses;
    }
    
    
    /**
     * Returns the current date and time to label the backup folder
     */
    private String getCurrentDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
    
    /**
     * Creates a directory to store the backup files
     */
    private void createBackupDirectory(String directoryName) {
        File directory = new File(directoryName);

       try {
           directory.mkdirs();
       } catch(SecurityException se){
           System.out.println("Error making directory: " + directoryName);
       }        
       
    }
    
    /** 
     *  Looks through an entity map to obtain all entities that were modified recently. Those entities are 
     *  then retrieved for backup.
     */
    private void retrieveEntitiesByCourse(Set<String> coursesList) {

        Iterator<String> it = coursesList.iterator();
        
        while(it.hasNext()) {
            String courseId = it.next();
            currentFileName = backupFileDirectory + "/" + courseId + ".json";
            FileHelper.appendToFile(currentFileName, "{\n");
            retrieveAndSaveAccountsByCourse(courseId);
            retrieveAndSaveCommentsByCourse(courseId);
            retrieveAndSaveCourse(courseId);
            retrieveAndSaveEvaluationsByCourse(courseId);
            retrieveAndSaveFeedbackQuestionsByCourse(courseId);
            retrieveAndSaveFeedbackResponsesByCourse(courseId);
            retrieveAndSaveFeedbackResponseCommentsByCourse(courseId);
            retrieveAndSaveFeedbackSessionsByCourse(courseId);
            retrieveAndSaveInstructorsByCourse(courseId);
            retrieveAndSaveStudentsByCourse(courseId);
            retrieveAndSaveSubmissionsByCourse(courseId);
            FileHelper.appendToFile(currentFileName, "\n}"); 
        }              
    }
    
    /** 
     *  Retrieves all the accounts from a course and saves them
     */
    private void retrieveAndSaveAccountsByCourse(String courseId) {
        
        try {
            Logic logic = new Logic();
            List<StudentAttributes> students = logic.getStudentsForCourse(courseId);
            List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
            
            FileHelper.appendToFile(currentFileName, "\t\"accounts\":{\n");
            
            for(int i = 0; i < students.size(); i++) {
                StudentAttributes student = students.get(i);
                saveStudentAccount(student);
            }
            
            for(InstructorAttributes instructor : instructors) {
                saveInstructorAccount(instructor);
            } 
            
            FileHelper.appendToFile(currentFileName, "\n\t},\n");
            hasPreviousEntity = false;
        } catch (EntityDoesNotExistException entityException) {
            System.out.println("Error occurred while trying to save accounts within course " + courseId);
        }
    }
    
    /** 
     *  Retrieves all the comments from a course and saves them
     */
    private void retrieveAndSaveCommentsByCourse(String courseId) {
        CommentsDb commentsDb = new CommentsDb();
        List<CommentAttributes> comments = commentsDb.getCommentsForCourse(courseId);
        
        FileHelper.appendToFile(currentFileName, "\t\"comments\":{\n");
        
        for(CommentAttributes comment: comments) {
            saveComment(comment);
        }
        hasPreviousEntity = false;
        FileHelper.appendToFile(currentFileName, "\n\t},\n");
    }
  
    private void retrieveAndSaveCourse(String courseId) {
        Logic logic = new Logic();
        CourseAttributes course = logic.getCourse(courseId);
        
        if(course == null) {
            return;
        }
        
        FileHelper.appendToFile(currentFileName, "\t\"courses\":{\n");
        FileHelper.appendToFile(currentFileName, formatJsonString(course.getJsonString(), course.id));
        
        hasPreviousEntity = false;
        FileHelper.appendToFile(currentFileName, "\n\t},\n");
    }
    
    /** 
     *  Retrieves all the evaluations from a course and saves them
     */
    private void retrieveAndSaveEvaluationsByCourse(String courseId) {
        Logic logic = new Logic();

        List<EvaluationAttributes> evaluations = logic.getEvaluationsForCourse(courseId);
   
        FileHelper.appendToFile(currentFileName, "\t\"evaluations\":{\n");
        
        for(EvaluationAttributes evaluation : evaluations) {
            saveEvaluation(evaluation);
        }
        hasPreviousEntity = false;
        FileHelper.appendToFile(currentFileName, "\n\t},\n");
    }
    
    /** 
     *  Retrieves all the feedback questions from a course and saves them
     */
    private void retrieveAndSaveFeedbackQuestionsByCourse(String courseId) {
        
        FeedbackQuestionsDb feedbackQuestionDb = new FeedbackQuestionsDb();
        List<FeedbackQuestionAttributes> feedbackQuestions = feedbackQuestionDb.getFeedbackQuestionsForCourse(courseId);

        FileHelper.appendToFile(currentFileName, "\t\"feedbackQuestions\":{\n");
        
        for(FeedbackQuestionAttributes feedbackQuestion : feedbackQuestions) {
            saveFeedbackQuestion(feedbackQuestion);
        }
        hasPreviousEntity = false;
        FileHelper.appendToFile(currentFileName, "\n\t},\n");
    }
    
    /** 
     *  Retrieves all the feedback responses from a course and saves them
     */
    private void retrieveAndSaveFeedbackResponsesByCourse(String courseId) {
        
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        List<FeedbackResponseAttributes> feedbackResponses = feedbackResponsesDb.getFeedbackResponsesForCourse(courseId);

        FileHelper.appendToFile(currentFileName, "\t\"feedbackResponses\":{\n");
        
        for(FeedbackResponseAttributes feedbackResponse : feedbackResponses) {
            saveFeedbackResponse(feedbackResponse);
        }
        hasPreviousEntity = false;
        FileHelper.appendToFile(currentFileName, "\n\t},\n");
    }
    
    /** 
     *  Retrieves all the feedback responses from a course and saves them
     */
    private void retrieveAndSaveFeedbackResponseCommentsByCourse(String courseId) {
        
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
        List<FeedbackResponseCommentAttributes> feedbackResponseComments = feedbackResponseCommentsDb.getFeedbackResponseCommentsForCourse(courseId);

        FileHelper.appendToFile(currentFileName, "\t\"feedbackResponseComments\":{\n");
        
        for(FeedbackResponseCommentAttributes feedbackResponseComment : feedbackResponseComments) {
            saveFeedbackResponseComment(feedbackResponseComment);
        }
        hasPreviousEntity = false;
        FileHelper.appendToFile(currentFileName, "\n\t},\n");
    }
    
    /** 
     *  Retrieves all the feedback responses from a course and saves them
     */
    private void retrieveAndSaveFeedbackSessionsByCourse(String courseId) {
        Logic logic = new Logic();
        List<FeedbackSessionAttributes> feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
        
        FileHelper.appendToFile(currentFileName, "\t\"feedbackSessions\":{\n");
        
        for(FeedbackSessionAttributes feedbackSession : feedbackSessions) {
            saveFeedbackSession(feedbackSession);
        }
        hasPreviousEntity = false;
        FileHelper.appendToFile(currentFileName, "\n\t},\n");
    }
    
    /** 
     *  Retrieves all the feedback responses from a course and saves them
     */
    private void retrieveAndSaveInstructorsByCourse(String courseId) {
        Logic logic = new Logic();
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        
        FileHelper.appendToFile(currentFileName, "\t\"instructors\":{\n");
        
        for(InstructorAttributes instructor : instructors) {
            saveInstructor(instructor);
        }
        hasPreviousEntity = false;
        FileHelper.appendToFile(currentFileName, "\n\t},\n");
    }
    
    /** 
     *  Retrieves all the feedback responses from a course and saves them
     */
    private void retrieveAndSaveStudentsByCourse(String courseId) {
        try {
            Logic logic = new Logic();
            List<StudentAttributes> students = logic.getStudentsForCourse(courseId);
            
            FileHelper.appendToFile(currentFileName, "\t\"students\":{\n");
            
            for(StudentAttributes student : students) {
                saveStudent(student);
            }
            hasPreviousEntity = false;
            FileHelper.appendToFile(currentFileName, "\n\t},\n");
        } catch (EntityDoesNotExistException exception) {
            System.out.println("Error while trying to save students in course " + courseId);
        }
    }
    
    /** 
     *  Retrieves all the feedback responses from a course and saves them
     */
    private void retrieveAndSaveSubmissionsByCourse(String courseId) {
        SubmissionsDb submissionsDb = new SubmissionsDb();
        List<SubmissionAttributes> submissions = submissionsDb.getSubmissionsForCourse(courseId);
        
        FileHelper.appendToFile(currentFileName, "\t\"submissions\":{\n");
        
        for(SubmissionAttributes submission : submissions) {
            saveSubmission(submission);
        }
        hasPreviousEntity = false;
        FileHelper.appendToFile(currentFileName, "\n\t}\n");
    }
    
    private String formatJsonString(String entityJsonString, String name) {
        String formattedString = "";
        
        if(hasPreviousEntity) {
            formattedString += ",\n";
        } else {
            hasPreviousEntity = true;
        }
        
        entityJsonString = entityJsonString.replace("\n", "\n\t\t");
        formattedString += "\t\t\"" + name + "\":" + entityJsonString;
        
        return formattedString;
    }
    
    private void saveStudentAccount(StudentAttributes student) {
        Logic logic = new Logic();
        AccountAttributes account = logic.getAccount(student.googleId.trim());
        
        if(account == null) {
            return;
        }

        FileHelper.appendToFile(currentFileName, formatJsonString(account.getJsonString(), account.email));
        
    }
    
    private void saveInstructorAccount(InstructorAttributes instructor) {
        Logic logic = new Logic();
        AccountAttributes account = logic.getAccount(instructor.googleId.trim());
        
        if(account == null) {
            return;
        }
        
        FileHelper.appendToFile(currentFileName, formatJsonString(account.getJsonString(), account.email));
    }
    
    private void saveComment(CommentAttributes comment) {
        FileHelper.appendToFile(currentFileName, formatJsonString(comment.getJsonString(), comment.getCommentId().toString()));
    }   
    
    private void saveEvaluation(EvaluationAttributes evaluation) {
        FileHelper.appendToFile(currentFileName, formatJsonString(evaluation.getJsonString(), evaluation.name));
    }
    
    private void saveFeedbackQuestion(FeedbackQuestionAttributes feedbackQuestion) {   
        FileHelper.appendToFile(currentFileName, formatJsonString(feedbackQuestion.getJsonString(), feedbackQuestion.getId()));
    }
    
    private void saveFeedbackResponse(FeedbackResponseAttributes feedbackResponse) {
        FileHelper.appendToFile(currentFileName, formatJsonString(feedbackResponse.getJsonString(), feedbackResponse.getId()));
    }
    
    private void saveFeedbackResponseComment(FeedbackResponseCommentAttributes feedbackResponseComment) {
        FileHelper.appendToFile(currentFileName, formatJsonString(feedbackResponseComment.getJsonString(), feedbackResponseComment.getId().toString()));
    }
    
    private void saveFeedbackSession(FeedbackSessionAttributes feedbackSession) {
        FileHelper.appendToFile(currentFileName, formatJsonString(feedbackSession.getJsonString(), feedbackSession.feedbackSessionName + "%" + feedbackSession.courseId));
    }
    
    private void saveInstructor(InstructorAttributes instructor) {
        FileHelper.appendToFile(currentFileName, formatJsonString(instructor.getJsonString(), instructor.googleId));
    }
    
    private void saveStudent(StudentAttributes student) {
        FileHelper.appendToFile(currentFileName, formatJsonString(student.getJsonString(), student.googleId));
    }
    
    private void saveSubmission(SubmissionAttributes submission) {
        FileHelper.appendToFile(currentFileName, formatJsonString(submission.getJsonString(), submission.getId().toString()));
    }

}
