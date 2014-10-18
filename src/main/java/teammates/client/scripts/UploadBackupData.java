package teammates.client.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.util.FileHelper;
import teammates.common.util.Utils;
import teammates.logic.api.Logic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CommentsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.EvaluationsLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.logic.core.SubmissionsLogic;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.StudentProfile;
import teammates.test.driver.BackDoor;

/**
 * Usage: This script imports a large data bundle to the appengine. The target of the script is the app with
 * appID in the test.properties file.Can use DataGenerator.java to generate random data.
 * 
 * Notes:
 * -Edit SOURCE_FILE_NAME before use
 * -Should not have any limit on the size of the databundle. However, the number of entities per request
 * should not be set to too large as it may cause Deadline Exception (especially for evaluations)
 * 
 */
public class UploadBackupData extends RemoteApiClient {
    //  
    // Data source file name (under src/test/resources/data folder) to import
    private static String BACKUP_FOLDER = "Backup";
    
    private static final int MAX_NUMBER_OF_ENTITY_PER_REQUEST = 100;
    private static final int MAX_NUMBER_OF_EVALUATION_PER_REQUEST = 1;
    private static final int WAIT_TIME_BETWEEN_REQUEST =1000 ;//ms
    
    private static DataBundle data;
    private static Gson gson = Utils.getTeammatesGson();
    private static String jsonString;
    
    private static Set<String> coursesPersisted;
    
    private static AccountsLogic accountsLogic = new AccountsLogic();
    private static CommentsLogic commentsLogic = new CommentsLogic();
    private static CoursesLogic coursesLogic = new CoursesLogic();
    private static EvaluationsLogic evaluationsLogic = new EvaluationsLogic();
    private static FeedbackQuestionsLogic feedbackQuestionsLogic = new FeedbackQuestionsLogic();
    private static FeedbackResponsesLogic feedbackResponsesLogic = new FeedbackResponsesLogic();
    private static FeedbackResponseCommentsLogic feedbackResponseCommentsLogic = new FeedbackResponseCommentsLogic();
    private static FeedbackSessionsLogic feedbackSessionsLogic = new FeedbackSessionsLogic();
    private static InstructorsLogic instructorsLogic = new InstructorsLogic();
    private static StudentsLogic studentsLogic = new StudentsLogic();
    private static SubmissionsLogic submissionsLogic = new SubmissionsLogic();
    
    public static void main(String args[]) throws Exception {
        UploadBackupData uploadBackupData = new UploadBackupData();
        uploadBackupData.doOperationRemotely();
    }
    
    protected void doOperation() {
        Datastore.initialize();
        File backupFolder = new File(BACKUP_FOLDER);
        String[] folders = backupFolder.list();
        for(String folder : folders) {
            String folderName = BACKUP_FOLDER + "/" + folder;
            File currentFolder = new File(folderName);   
            String[] backupFiles = currentFolder.list();
            for(String backupFile : backupFiles) {
                try {
                jsonString = FileHelper.readFile(folderName + "/" + backupFile);
                data = gson.fromJson(jsonString, DataBundle.class);  
                    
                    if (!data.accounts.isEmpty()) {                         // Accounts
                        persist(data.accounts);    
                    } 
                    if(!data.comments.isEmpty()) {                   // Comments
                        persist(data.comments);
                    } 
                    if (!data.courses.isEmpty()){                    // Courses
                        persist(data.courses);
                    } 
                    if (!data.evaluations.isEmpty()){                // Evaluations
                        persist(data.evaluations);
                    } 
                    if (!data.feedbackQuestions.isEmpty()){          // Feedback questions
                        persist(data.feedbackQuestions);
                    } 
                    if(!data.feedbackResponses.isEmpty()) {          // Feedback responses
                        persist(data.feedbackResponses);
                    } 
                    if (!data.feedbackResponseComments.isEmpty()){   // Feedback response comments
                        persist(data.feedbackResponseComments);
                    } 
                    if (!data.feedbackSessions.isEmpty()){           // Feedback sessions
                        persist(data.feedbackSessions);
                    } 
                    if (!data.instructors.isEmpty()){                // Instructors
                        persist(data.feedbackSessions);
                    } 
                    if (!data.students.isEmpty()){                   // Students
                        persist(data.instructors);
                    } 
                    if (!data.submissions.isEmpty()){                // Submissions
                        persist(data.submissions);
                    } 
                } catch (Exception e) {
                    
                }
            }
        }
        
        
        
    }
    
    /**
     * This method will persist a number of entity and remove them from the source, return the 
     * status of the operation.
     *  
     * @param map - HashMap which has data to persist
     * @return status of the Backdoor operation
     */
    private static String persist(@SuppressWarnings("rawtypes") HashMap map)
    {
        DataBundle bundle = new DataBundle();
        int count =0;
        @SuppressWarnings("unchecked")
        Set<String> set = map.keySet();
        @SuppressWarnings("rawtypes")
        Iterator itr = set.iterator();
        
        String type = "";
        while (itr.hasNext()) {
            try {
                String key = (String) itr.next();
                Object obj = map.get(key);
                if (obj instanceof AccountAttributes) {
                    type = "AccountData";
                    AccountAttributes accountData = (AccountAttributes)obj;
                    Logic logic = new Logic();
                    logic.createAccount(accountData.googleId, accountData.name, 
                            accountData.isInstructor, accountData.email, accountData.institute);
                    //StudentProfileAttributes studentProfile = new StudentProfileAttributes();
                    //AccountAttributes newAccountData = new AccountAttributes(, studentProfile);
                    //accountsLogic.createAccount(newAccountData);
                } else if(obj instanceof CommentAttributes){
                    type = "CommentData";
                    CommentAttributes commentData = (CommentAttributes)obj;
                    CommentsLogic commentsLogic = new CommentsLogic();
                    commentsLogic.createComment(commentData);
                } else if(obj instanceof CourseAttributes) {
                    type = "CourseData";
                    CourseAttributes courseData = (CourseAttributes)obj;
                    coursesLogic.createCourse(courseData.id, courseData.name);
                } else if (obj instanceof EvaluationAttributes) {
                    type = "EvaluationData";
                    EvaluationAttributes evaluationData = (EvaluationAttributes)obj;
                    evaluationsLogic.createEvaluationCascade(evaluationData);
                } else if (obj instanceof FeedbackQuestionAttributes) {
                    type = "FeedbackQuestionData";
                    FeedbackQuestionAttributes feedbackQuestionData = (FeedbackQuestionAttributes)obj;
                    feedbackQuestionsLogic.createFeedbackQuestion(feedbackQuestionData);
                } else if (obj instanceof FeedbackResponseAttributes) {
                    type = "FeedbackResponseData";
                    FeedbackResponseAttributes feedbackResponseData = (FeedbackResponseAttributes)obj;
                    System.out.println(feedbackResponseData.courseId);
                    feedbackResponsesLogic.createFeedbackResponse(feedbackResponseData);
                } else if (obj instanceof FeedbackResponseCommentAttributes) {
                    type = "FeedbackResponseCommentData";
                    FeedbackResponseCommentAttributes feedbackResponseCommentData = (FeedbackResponseCommentAttributes)obj;
                    feedbackResponseCommentsLogic.createFeedbackResponseComment(feedbackResponseCommentData);
                } else if (obj instanceof FeedbackSessionAttributes) {
                    type = "FeedbackSessionData";
                    FeedbackSessionAttributes feedbackSessionData = (FeedbackSessionAttributes)obj;
                    feedbackSessionsLogic.createFeedbackSession(feedbackSessionData);
                } else if(obj instanceof InstructorAttributes) {
                    type = "InstructorData";
                    InstructorAttributes instructorData = (InstructorAttributes)obj;
                    instructorsLogic.createInstructor(instructorData);
                } else if(obj instanceof StudentAttributes) {
                    type = "StudentData";
                    StudentAttributes studentData = (StudentAttributes)obj;
                    studentsLogic.createStudentCascade(studentData);
                } else if (obj instanceof SubmissionAttributes) {
                    type = "SubmissionData";
                    SubmissionAttributes submissionData = (SubmissionAttributes)obj;
                    List<SubmissionAttributes> listOfSubmissionsToAdd = new ArrayList<SubmissionAttributes>();
                    listOfSubmissionsToAdd.add(submissionData);
                    submissionsLogic.createSubmissions(listOfSubmissionsToAdd);
                }
            } catch (Exception e) {
                
            }
            /*if (obj instanceof AccountAttributes) {
                type = "AccountData";
                AccountAttributes accountData = (AccountAttributes)obj;
                bundle.accounts.put(key, accountData);
            } else if(obj instanceof CommentAttributes){
                type = "CommentData";
                CommentAttributes instructorData = (CommentAttributes)obj;
                bundle.comments.put(key, instructorData);
            } else if(obj instanceof CourseAttributes) {
                type = "CourseData";
                CourseAttributes courseData = (CourseAttributes)obj;
                bundle.courses.put(key, courseData);
            } else if (obj instanceof EvaluationAttributes) {
                type = "EvaluationData";
                EvaluationAttributes evaluationData = (EvaluationAttributes)obj;
                bundle.evaluations.put(key, evaluationData);
            } else if (obj instanceof FeedbackQuestionAttributes) {
                type = "FeedbackQuestionData";
                FeedbackQuestionAttributes feedbackQuestionData = (FeedbackQuestionAttributes)obj;
                bundle.feedbackQuestions.put(key, feedbackQuestionData);
            } else if (obj instanceof FeedbackResponseAttributes) {
                type = "FeedbackResponseData";
                FeedbackResponseAttributes feedbackResponseData = (FeedbackResponseAttributes)obj;
                bundle.feedbackResponses.put(key, feedbackResponseData);
            } else if (obj instanceof FeedbackResponseCommentAttributes) {
                type = "FeedbackResponseCommentData";
                FeedbackResponseCommentAttributes feedbackResponseCommentData = (FeedbackResponseCommentAttributes)obj;
                bundle.feedbackResponseComments.put(key, feedbackResponseCommentData);
            } else if (obj instanceof FeedbackSessionAttributes) {
                type = "FeedbackSessionData";
                FeedbackSessionAttributes feedbackSessionData = (FeedbackSessionAttributes)obj;
                bundle.feedbackSessions.put(key, feedbackSessionData);
            } else if(obj instanceof InstructorAttributes) {
                type = "InstructorData";
                InstructorAttributes instructorData = (InstructorAttributes)obj;
                bundle.instructors.put(key, instructorData);
            } else if(obj instanceof StudentAttributes) {
                type = "StudentData";
                StudentAttributes studentData = (StudentAttributes)obj;
                bundle.students.put(key, studentData);
            } else if (obj instanceof SubmissionAttributes) {
                type = "SubmissionData";
                SubmissionAttributes submissionData = (SubmissionAttributes)obj;
                bundle.submissions.put(key, submissionData);
            }*/
            
            count ++;
            /*itr.remove();
            System.out.print(key + "\n");
            if(type.equals("EvaluationData")&& count >= MAX_NUMBER_OF_EVALUATION_PER_REQUEST)
                break;
            if(count >= MAX_NUMBER_OF_ENTITY_PER_REQUEST)
                break;*/
        }
        System.out.print("Persisting: " + count + " entities of type "+ type + "\n" );
        
        String status = BackDoor.persistNewDataBundle(gson.toJson(bundle));
        
        // wait a few seconds to allow data to persist completedly
        try {
            Thread.sleep(WAIT_TIME_BETWEEN_REQUEST);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return status;
    }
}
