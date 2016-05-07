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

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.api.Logic;
import teammates.storage.api.CommentsDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.datastore.Datastore;
import teammates.test.driver.TestProperties;
import teammates.test.util.FileHelper;

public class OfflineBackup extends RemoteApiClient {
    protected String backupFileDirectory = "";
    protected String currentFileName = "";
    protected boolean hasPreviousEntity = false;
    protected Set<String> accountsSaved = new HashSet<String>();
    
    public static void main(String[] args) throws IOException {
        OfflineBackup offlineBackup = new OfflineBackup();
        offlineBackup.doOperationRemotely();
    }
    
    protected void doOperation() {
        Datastore.initialize();
        Vector<String> logs = getModifiedLogs();
        Set<String> courses = extractModifiedCourseIds(logs);
        backupFileDirectory = "BackupFiles/Backup/" + getCurrentDateAndTime();
        createBackupDirectory(backupFileDirectory);
        retrieveEntitiesByCourse(courses);
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
     * Look through the logs and extracts all recently modified courses. 
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
    protected String getCurrentDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
    
    /**
     * Creates a directory to store the backup files
     */
    protected void createBackupDirectory(String directoryName) {
        File directory = new File(directoryName);

       try {
           directory.mkdirs();
       } catch(SecurityException se){
           System.out.println("Error making directory: " + directoryName);
       }        
       
    }
    
    /** 
     *  Looks through all the modified courses and retrieve their respective entities.
     */
    protected void retrieveEntitiesByCourse(Set<String> coursesList) {

        Iterator<String> it = coursesList.iterator();
        
        while(it.hasNext()) {
            String courseId = it.next();
            currentFileName = backupFileDirectory + "/" + courseId + ".json";
            FileHelper.appendToFile(currentFileName, "{\n");
            
            retrieveAndSaveAccountsByCourse(courseId);
            retrieveAndSaveCommentsByCourse(courseId);
            retrieveAndSaveCourse(courseId);
            retrieveAndSaveFeedbackQuestionsByCourse(courseId);
            retrieveAndSaveFeedbackResponsesByCourse(courseId);
            retrieveAndSaveFeedbackResponseCommentsByCourse(courseId);
            retrieveAndSaveFeedbackSessionsByCourse(courseId);
            retrieveAndSaveInstructorsByCourse(courseId);
            retrieveAndSaveStudentsByCourse(courseId);
            retrieveAndSaveStudentProfilesByCourse(courseId);
            
            FileHelper.appendToFile(currentFileName, "\n}"); 
        }              
    }
    
    /** 
     *  Retrieves all the accounts from a course and saves them
     */
    protected void retrieveAndSaveAccountsByCourse(String courseId) {
        
        try {
            Logic logic = new Logic();
            List<StudentAttributes> students = logic.getStudentsForCourse(courseId);
            List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
            
            FileHelper.appendToFile(currentFileName, "\t\"accounts\":{\n");
            
            for(StudentAttributes student : students) {
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
    protected void retrieveAndSaveCommentsByCourse(String courseId) {
        CommentsDb commentsDb = new CommentsDb();
        List<CommentAttributes> comments = commentsDb.getCommentsForCourse(courseId);
        
        FileHelper.appendToFile(currentFileName, "\t\"comments\":{\n");
        
        for(CommentAttributes comment: comments) {
            saveComment(comment);
        }
        hasPreviousEntity = false;
        FileHelper.appendToFile(currentFileName, "\n\t},\n");
    }
  
    /** 
     *  Retrieves the course and saves them
     */
    protected void retrieveAndSaveCourse(String courseId) {
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
     *  Retrieves all the feedback questions from a course and saves them
     */
    protected void retrieveAndSaveFeedbackQuestionsByCourse(String courseId) {
        
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
    protected void retrieveAndSaveFeedbackResponsesByCourse(String courseId) {
        
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
     *  Retrieves all the feedback responses comments from a course and saves them
     */
    protected void retrieveAndSaveFeedbackResponseCommentsByCourse(String courseId) {
        
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
     *  Retrieves all the feedback sessions from a course and saves them
     */
    protected void retrieveAndSaveFeedbackSessionsByCourse(String courseId) {
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
     *  Retrieves all the instructors from a course and saves them
     */
    protected void retrieveAndSaveInstructorsByCourse(String courseId) {
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
     *  Retrieves all the students from a course and saves them
     */
    protected void retrieveAndSaveStudentsByCourse(String courseId) {
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
     *  Retrieves all the submissions from a course and saves them
     */
    protected void retrieveAndSaveStudentProfilesByCourse(String courseId) {
  
        try {
            Logic logic = new Logic();
            List<StudentAttributes> students = logic.getStudentsForCourse(courseId);
            
            FileHelper.appendToFile(currentFileName, "\t\"profiles\":{\n");
            
            for(StudentAttributes student : students) {
                if(student != null && student.googleId != null && !student.googleId.equals("")) {
                    StudentProfileAttributes profile = logic.getStudentProfile(student.googleId);
                    if(profile != null) {
                        saveProfile(profile);
                    }
                }
            }
            
            FileHelper.appendToFile(currentFileName, "\n\t}\n");
            hasPreviousEntity = false;
        } catch (EntityDoesNotExistException entityException) {
            System.out.println("Error occurred while trying to save profiles within course " + courseId);
        }
    }
    
    /** 
     *  Perform formatting of the string to ensure that it conforms to json formatting
     */
    protected String formatJsonString(String entityJsonString, String name) {
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
    
    /** 
     *  Retrieves all the student accounts and saves them
     */
    protected void saveStudentAccount(StudentAttributes student) {
        if(student == null) {
            return;
        }
        
        Logic logic = new Logic();
        AccountAttributes account = logic.getAccount(student.googleId.trim());
        
        if(account == null || accountsSaved.contains(account.email)) {
            return;
        }
        
        
        FileHelper.appendToFile(currentFileName, formatJsonString(account.getJsonString(), account.email));
        accountsSaved.add(account.email);
    }
    
    /** 
     *  Retrieves all the instructor accounts and saves them
     */
    protected void saveInstructorAccount(InstructorAttributes instructor) {
        if(instructor == null) {
            return;
        }
        
        Logic logic = new Logic();
        AccountAttributes account = logic.getAccount(instructor.googleId.trim());
        
        if(account == null || accountsSaved.contains(account.email)) {
            return;
        }
        
        FileHelper.appendToFile(currentFileName, formatJsonString(account.getJsonString(), account.email));
        accountsSaved.add(account.email);
    }
    
    protected void saveComment(CommentAttributes comment) {
        FileHelper.appendToFile(currentFileName, formatJsonString(comment.getJsonString(), comment.getCommentId().toString()));
    }   
    
    protected void saveFeedbackQuestion(FeedbackQuestionAttributes feedbackQuestion) {   
        FileHelper.appendToFile(currentFileName, formatJsonString(feedbackQuestion.getJsonString(), feedbackQuestion.getId()));
    }
    
    protected void saveFeedbackResponse(FeedbackResponseAttributes feedbackResponse) {
        FileHelper.appendToFile(currentFileName, formatJsonString(feedbackResponse.getJsonString(), feedbackResponse.getId()));
    }
    
    protected void saveFeedbackResponseComment(FeedbackResponseCommentAttributes feedbackResponseComment) {
        FileHelper.appendToFile(currentFileName, formatJsonString(feedbackResponseComment.getJsonString(), feedbackResponseComment.getId().toString()));
    }
    
    protected void saveFeedbackSession(FeedbackSessionAttributes feedbackSession) {
        FileHelper.appendToFile(currentFileName, formatJsonString(feedbackSession.getJsonString(), feedbackSession.feedbackSessionName + "%" + feedbackSession.courseId));
    }
    
    protected void saveInstructor(InstructorAttributes instructor) {
        FileHelper.appendToFile(currentFileName, formatJsonString(instructor.getJsonString(), instructor.googleId));
    }
    
    protected void saveStudent(StudentAttributes student) {
        FileHelper.appendToFile(currentFileName, formatJsonString(student.getJsonString(), student.googleId));
    }
    
    protected void saveProfile(StudentProfileAttributes studentProfile) {
        FileHelper.appendToFile(currentFileName, formatJsonString(studentProfile.getJsonString(), studentProfile.googleId));
    }

}
