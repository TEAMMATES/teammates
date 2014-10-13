package teammates.client.scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.appengine.api.datastore.KeyFactory;

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
import teammates.common.exception.EntityDoesNotExistException;
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
        Set<String> courses = extractModifiedCourseIds(logs);
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
                //System.out.println(logMessage);
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
            courses.add(course);
        }
        
        return courses;
    }
    
    /** 
     *  Looks through an entity map to obtain all entities that were modified recently. Those entities are 
     *  then retrieved for backup.
     */
    private void retrieveEntitiesByCourse(Set<String> coursesList) {

        Iterator<String> it = coursesList.iterator();
        while(it.hasNext()) {
            String courseId = it.next();
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
            
            for(EntityAttributes student : students) {
                saveAccount(student);
            }
            
            for(EntityAttributes instructor : instructors) {
                saveAccount(instructor);
            } 
            
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
        
        for(CommentAttributes comment: comments) {
            saveComment(comment);
        }
    }
  
    private void retrieveAndSaveCourse(String courseId) {
        Logic logic = new Logic();
        CourseAttributes course = logic.getCourse(courseId);
        
        if(course == null) {
            return;
        }
        
        String createdAt = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(course.createdAt);
        String courseCsv = course.id + "," + course.name + "," + createdAt + "," + course.isArchived + Const.EOL;
        FileHelper.appendToFile(courseCsvFile, courseCsv);
    }
    
    /** 
     *  Retrieves all the evaluations from a course and saves them
     */
    private void retrieveAndSaveEvaluationsByCourse(String courseId) {
        Logic logic = new Logic();

        List<EvaluationAttributes> evaluations = logic.getEvaluationsForCourse(courseId);
   
        for(EvaluationAttributes evaluation : evaluations) {
            saveEvaluation(evaluation);
        }
    }
    
    /** 
     *  Retrieves all the feedback questions from a course and saves them
     */
    private void retrieveAndSaveFeedbackQuestionsByCourse(String courseId) {
        
        FeedbackQuestionsDb feedbackQuestionDb = new FeedbackQuestionsDb();
        List<FeedbackQuestionAttributes> feedbackQuestions = feedbackQuestionDb.getFeedbackQuestionsForCourse(courseId);

        for(FeedbackQuestionAttributes feedbackQuestion : feedbackQuestions) {
            saveFeedbackQuestion(feedbackQuestion);
        }
    }
    
    /** 
     *  Retrieves all the feedback responses from a course and saves them
     */
    private void retrieveAndSaveFeedbackResponsesByCourse(String courseId) {
        
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        List<FeedbackResponseAttributes> feedbackResponses = feedbackResponsesDb.getFeedbackResponsesForCourse(courseId);

        for(FeedbackResponseAttributes feedbackResponse : feedbackResponses) {
            saveFeedbackResponse(feedbackResponse);
        }
    }
    
    /** 
     *  Retrieves all the feedback responses from a course and saves them
     */
    private void retrieveAndSaveFeedbackResponseCommentsByCourse(String courseId) {
        
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
        List<FeedbackResponseCommentAttributes> feedbackResponseComments = feedbackResponseCommentsDb.getFeedbackResponseCommentsForCourse(courseId);

        for(FeedbackResponseCommentAttributes feedbackResponseComment : feedbackResponseComments) {
            saveFeedbackResponseComment(feedbackResponseComment);
        }
    }
    
    /** 
     *  Retrieves all the feedback responses from a course and saves them
     */
    private void retrieveAndSaveFeedbackSessionsByCourse(String courseId) {
        Logic logic = new Logic();
        List<FeedbackSessionAttributes> feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
        
        for(FeedbackSessionAttributes feedbackSession : feedbackSessions) {
            saveFeedbackSession(feedbackSession);
        }
    }
    
    /** 
     *  Retrieves all the feedback responses from a course and saves them
     */
    private void retrieveAndSaveInstructorsByCourse(String courseId) {
        Logic logic = new Logic();
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        
        for(InstructorAttributes instructor : instructors) {
            saveInstructor(instructor);
        }
    }
    
    /** 
     *  Retrieves all the feedback responses from a course and saves them
     */
    private void retrieveAndSaveStudentsByCourse(String courseId) {
        try {
            Logic logic = new Logic();
            List<StudentAttributes> students = logic.getStudentsForCourse(courseId);
            
            for(StudentAttributes student : students) {
                saveStudent(student);
            }
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
        
        for(SubmissionAttributes submission : submissions) {
            saveSubmission(submission);
        }
    }
    
    private void saveAccount(EntityAttributes entity) {
        if(entity == null) {
            return;
        }
        
        String type = entity.getEntityTypeAsString();
        String googleId = "";
        
        if(type.equals("Student")) {
            StudentAttributes student = (StudentAttributes)entity;
            googleId = student.googleId;
        } else if(type.equals("Instructor")) {
            InstructorAttributes instructor = (InstructorAttributes)entity;
            googleId = instructor.googleId;
        }
        
        Logic logic = new Logic();
        googleId = googleId.trim();
        AccountAttributes account = logic.getAccount(googleId);
        
        String createdAt = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(account.createdAt);
        String accountCsv = account.googleId + "," + account.name + "," + account.isInstructor + "," 
                                + account.email + "," + account.institute + "," + createdAt + Const.EOL;
        FileHelper.appendToFile(accountCsvFile, accountCsv);
    }
    
    private void saveComment(CommentAttributes comment) {
        if(comment == null) {
            return;
        }
        
        String createdAt = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(comment.createdAt);
        String commentCsv = comment.getCommentId() + "," + comment.courseId + "," + comment.giverEmail + "," + 
                                comment.recipientType + "," + comment.recipients.toString() + "," + comment.status + "," +
                                comment.sendingState + "," + formatList(comment.showCommentTo) + "," +
                                formatList(comment.showGiverNameTo) + "," + formatList(comment.showRecipientNameTo) + "," +
                                comment.commentText.getValue() + "," + createdAt + Const.EOL;
        FileHelper.appendToFile(commentCsvFile, commentCsv);
    }   
    
    private void saveEvaluation(EvaluationAttributes evaluation) {
        if(evaluation == null) {
            return;
        }
        
        String startTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(evaluation.startTime);
        String endTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(evaluation.endTime);
        String evaluationCsv = evaluation.getId() + "," + evaluation.courseId + "," + evaluation.name + "," + evaluation.instructions.getValue() + "," + startTime + 
                "," + endTime + "," + evaluation.timeZone + "," + evaluation.gracePeriod + "," + evaluation.p2pEnabled + "," + evaluation.published + "," + 
                evaluation.activated + Const.EOL;
        FileHelper.appendToFile(evaluationCsvFile, evaluationCsv);
    }
    
    private void saveFeedbackQuestion(FeedbackQuestionAttributes feedbackQuestion) {   
        if(feedbackQuestion == null) {
            return;
        }
        
        String showResponsesTo = formatList(feedbackQuestion.showResponsesTo);
        String showGiverNameTo = formatList(feedbackQuestion.showGiverNameTo);
        String showRecipientNameTo = formatList(feedbackQuestion.showRecipientNameTo);
       
        String feedbackQuestionCsv = feedbackQuestion.getId() + "," + feedbackQuestion.feedbackSessionName + "," + feedbackQuestion.courseId + "," +
                                        feedbackQuestion.creatorEmail + "," + feedbackQuestion.questionMetaData.getValue() + "," + feedbackQuestion.questionNumber + "," +
                                        feedbackQuestion.questionType + "," + feedbackQuestion.giverType + "," + feedbackQuestion.recipientType + "," +
                                        feedbackQuestion.numberOfEntitiesToGiveFeedbackTo + "," + showResponsesTo + "," + showGiverNameTo +
                                        "," + showRecipientNameTo + Const.EOL;
        FileHelper.appendToFile(feedbackQuestionCsvFile, feedbackQuestionCsv);
    }
    
    private void saveFeedbackResponse(FeedbackResponseAttributes feedbackResponse) {
        if(feedbackResponse == null) {
            return;
        }
        
        String feedbackResponseCsv = feedbackResponse.getId() + "," + feedbackResponse.feedbackSessionName + "," + feedbackResponse.courseId + "," +
                                        feedbackResponse.feedbackQuestionId + "," + feedbackResponse.feedbackQuestionType + "," + feedbackResponse.giverEmail +
                                        "," + feedbackResponse.giverSection + "," + feedbackResponse.recipientEmail + "," + 
                                        feedbackResponse.recipientSection + "," + feedbackResponse.responseMetaData.getValue() + Const.EOL;
        FileHelper.appendToFile(feedbackResponseCsvFile, feedbackResponseCsv);
    }
    
    private void saveFeedbackResponseComment(FeedbackResponseCommentAttributes feedbackResponseComment) {
        if(feedbackResponseComment == null) {
            return;
        }
        
        String createdAt = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(feedbackResponseComment.createdAt);
        
        String feedbackResponseCommentCsv = feedbackResponseComment.getId() + "," + feedbackResponseComment.courseId + "," + feedbackResponseComment.feedbackSessionName +
                                                "," + feedbackResponseComment.feedbackQuestionId + "," + feedbackResponseComment.giverEmail + "," + 
                                                feedbackResponseComment.giverSection + "," + feedbackResponseComment.receiverSection + "," + 
                                                feedbackResponseComment.feedbackResponseId + "," + feedbackResponseComment.sendingState + "," + 
                                                formatList(feedbackResponseComment.showCommentTo) + "," + formatList(feedbackResponseComment.showGiverNameTo) + "," + 
                                                feedbackResponseComment.isVisibilityFollowingFeedbackQuestion + "," +createdAt + "," +
                                                feedbackResponseComment.commentText.getValue() + Const.EOL;
        FileHelper.appendToFile(feedbackResponseCommentCsvFile, feedbackResponseCommentCsv);
    }
    
    private void saveFeedbackSession(FeedbackSessionAttributes feedbackSession) {
        if(feedbackSession == null) {
            return;
        }
        
        String createdTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(feedbackSession.createdTime);
        String startTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(feedbackSession.startTime);
        String endTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(feedbackSession.endTime);
        String sessionVisibleFromTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(feedbackSession.sessionVisibleFromTime);
        String resultsVisibleFromTime = new SimpleDateFormat("EEE MMM d HH:mm:ss.SSSSSS zzz yyyy").format(feedbackSession.resultsVisibleFromTime);
        
        String feedbackSessionCsv = feedbackSession.feedbackSessionName + "%" + feedbackSession.courseId + "," + feedbackSession.feedbackSessionName + "," + 
                                    feedbackSession.courseId + "," + feedbackSession.creatorEmail + "," + feedbackSession.instructions.getValue() + "," + 
                                    createdTime + "," + startTime + "," + endTime + "," + sessionVisibleFromTime + "," +
                                    resultsVisibleFromTime + "," + feedbackSession.timeZone + "," + feedbackSession.gracePeriod + "," + feedbackSession.feedbackSessionType +
                                    "," + feedbackSession.sentOpenEmail + "," + feedbackSession.sentPublishedEmail + "," + feedbackSession.isOpeningEmailEnabled + "," +
                                    feedbackSession.isClosingEmailEnabled + "," + feedbackSession.isPublishedEmailEnabled + "," + feedbackSession.respondingInstructorList + "," +
                                    feedbackSession.respondingStudentList + Const.EOL;
        FileHelper.appendToFile(feedbackSessionCsvFile, feedbackSessionCsv);
    }
    
    private void saveInstructor(InstructorAttributes instructor) {
        if(instructor == null) {
            return;
        }
        
        String instructorCsv = instructor.getId() + "," + instructor.googleId + "," + instructor.name + "," + instructor.email + "," + 
                                instructor.courseId + "," + instructor.isArchived + "," + instructor.key + "," + instructor.role + "," + 
                                instructor.isDisplayedToStudents + "," + instructor.displayedName + Const.EOL;
        FileHelper.appendToFile(InstructorCsvFile, instructorCsv);
    }
    
    private void saveStudent(StudentAttributes student) {
        if(student == null) {
            return;
        }
        
        Long key = KeyFactory.stringToKey(student.key).getId();
        String studentCsv = key + "," + student.googleId + "," + student.name + "," + student.lastName + "," + student.email + "," + 
                            student.course + "," + student.comments + "," + student.team + "," + student.section + "," + 
                            student.updateStatus + Const.EOL;
        FileHelper.appendToFile(studentCsvFile, studentCsv);
    }
    
    private void saveSubmission(SubmissionAttributes submission) {
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
        String accountCsv = "key,name,isInstructor,email,institute,createdAt" + Const.EOL;
        FileHelper.writeToFile(accountCsvFile, accountCsv);
        
        String commentCsv = "key,courseID,giverEmail,receiverEmail,createdAt,commentText" + Const.EOL;
        FileHelper.writeToFile(commentCsvFile, commentCsv);
        
        String courseCsv = "key,name,createdAt,archiveStatus" + Const.EOL;
        FileHelper.writeToFile(courseCsvFile, courseCsv);
        
        String evaluationCsv = "key,courseID,name,longInstructions,startTime,endTime,timeZone,gracePeriod,commentsEnabled,published,activated" + Const.EOL;
        FileHelper.writeToFile(evaluationCsvFile, evaluationCsv);
        
        String feedbackQuestionCsv = "key,feedbackSessionName,courseId,creatorEmail,questionText,questionNumber,questionType,giverType,recipientType,"
                + "numberOfEntitiesToGiveFeedbackTo,showResponsesTo,showGiverNameTo,showRecipientNameTo" + Const.EOL;
        FileHelper.writeToFile(feedbackQuestionCsvFile, feedbackQuestionCsv);
        
        String feedbackResponseCsv = "key,feedbackSessionName,courseId,feedbackQuestionID,feedbackQuestionType,giverEmail,receiver,answer" + Const.EOL;
        FileHelper.writeToFile(feedbackResponseCsvFile, feedbackResponseCsv);
        
        String feedbackResponseCommentCsv = "key,courseId,feedbackSessionName,feedbackQuestionID,giverEmail,feedbackResponseID,createdAt,commentText" + Const.EOL;
        FileHelper.writeToFile(feedbackResponseCommentCsvFile, feedbackResponseCommentCsv);
        
        String feedbackSessionCsv = "key,feedbackSessionName,courseId,creatorEmail,instructions,createdTime,startTime,endTime,sessionVisibleFromTime,"
                + "resultsVisibleFromTime,timeZoneDouble,gracePeriod,feedbackSessionType,sentOpenEmail,sentPublishedEmail,isOpeningEmailEnabled,isClosingEmailEnabled,"
                + "isPublishedEmailEnabled" + Const.EOL;
        FileHelper.writeToFile(feedbackSessionCsvFile, feedbackSessionCsv);
        
        String instructorCsv = "key,googleId,name,email,courseId,registrationKey" + Const.EOL;
        FileHelper.writeToFile(InstructorCsvFile, instructorCsv);
        
        String studentCsv = "key,ID,name,email,courseID,comments,teamName" + Const.EOL;
        FileHelper.writeToFile(studentCsvFile, studentCsv);
        
        String submissionCsv = "key,courseID,evaluationName,teamName,fromStudent,toStudent,points,justification,commentsToStudent" + Const.EOL;
        FileHelper.writeToFile(submissionCsvFile, submissionCsv); 
    }
    
    private String formatList(List<?> list) {
        String formattedString = "\"[u'";
        formattedString += list.get(0) + "'";
        
        for(int i = 1; i < list.size(); i++) {
            formattedString += ", u'" + list.get(i) + "'";
        }
        formattedString += "]\"";
        
        return formattedString;
    }
}
