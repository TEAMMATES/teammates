package teammates.logic.backdoor;

import java.util.ArrayList;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Utils;
import teammates.logic.api.Logic;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CommentsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class BackDoorLogic extends Logic {
    private static Logger log = Utils.getLogger();
    private static final AccountsDb accountsDb = new AccountsDb();
    private static final CoursesDb coursesDb = new CoursesDb();
    private static final CommentsDb commentsDb = new CommentsDb();
    private static final StudentsDb studentsDb = new StudentsDb();
    private static final InstructorsDb instructorsDb = new InstructorsDb();
    private static final FeedbackSessionsDb fbDb = new FeedbackSessionsDb();
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private static final FeedbackResponseCommentsDb fcDb = new FeedbackResponseCommentsDb();
    
    private static final int WAIT_DURATION_FOR_DELETE_CHECKING = 5;
    private static final int MAX_RETRY_COUNT_FOR_DELETE_CHECKING = 20;
    
    public String putDocumentsForStudents(DataBundle dataBundle) {
        for(StudentAttributes student : dataBundle.students.values()){
            student = getStudentForEmail(student.course, student.email);
            putDocument(student);
            ThreadHelper.waitFor(50);
        }
        
        return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
    }
    
    /**
     * Persists given data in the datastore Works ONLY if the data is correct.
     *  //Any existing copies of the data in the datastore will be overwritten.
     *      - edit: use removeDataBundle/deleteExistingData to remove.
     *              made this change for speed when deletion is not necessary.
     * @return status of the request in the form 'status meassage'+'additional
     *         info (if any)' e.g., "[BACKEND_STATUS_SUCCESS]" e.g.,
     *         "[BACKEND_STATUS_FAILURE]NullPointerException at ..."
     */

    public String persistDataBundle(DataBundle dataBundle)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        
        if (dataBundle == null) {
            throw new InvalidParametersException(
                    Const.StatusCodes.NULL_PARAMETER, "Null data bundle");
        }
        
        //deleteExistingData(dataBundle);
        
        HashMap<String, AccountAttributes> accounts = dataBundle.accounts;
        for (AccountAttributes account : accounts.values()) {
            if (account.studentProfile == null) {
                account.studentProfile = new StudentProfileAttributes();
                account.studentProfile.googleId = account.googleId;
            }
        }
        accountsDb.createAccounts(accounts.values(), true);
        
        HashMap<String, CourseAttributes> courses = dataBundle.courses;
        coursesDb.createCourses(courses.values());

        HashMap<String, InstructorAttributes> instructors = dataBundle.instructors;
        List<AccountAttributes> instructorAccounts = new ArrayList<AccountAttributes>();
        for (InstructorAttributes instructor : instructors.values()) {
            if (instructor.googleId != null && !instructor.googleId.equals("")) {
                AccountAttributes account = new AccountAttributes(instructor.googleId, instructor.name, true, instructor.email, "TEAMMATES Test Institute 1");
                if (account.studentProfile == null) {
                    account.studentProfile = new StudentProfileAttributes();
                    account.studentProfile.googleId = account.googleId;
                }
                instructorAccounts.add(account);
            }
        }
        accountsDb.createAccounts(instructorAccounts, false);
        instructorsDb.createInstructorsWithoutSearchability(instructors.values());

        HashMap<String, StudentAttributes> students = dataBundle.students;
        List<AccountAttributes> studentAccounts = new ArrayList<AccountAttributes>();
        for (StudentAttributes student : students.values()) {
            student.section = (student.section == null) ? "None" : student.section;
            if (student.googleId != null && !student.googleId.equals("")) {
                AccountAttributes account = new AccountAttributes(student.googleId, student.name, false, student.email, "TEAMMATES Test Institute 1");
                if (account.studentProfile == null) {
                    account.studentProfile = new StudentProfileAttributes();
                    account.studentProfile.googleId = account.googleId;
                }
                studentAccounts.add(account);
            }
        }
        accountsDb.createAccounts(studentAccounts, false);
        studentsDb.createStudentsWithoutSearchability(students.values());
        

        HashMap<String, FeedbackSessionAttributes> sessions = dataBundle.feedbackSessions;
        for(FeedbackSessionAttributes session : sessions.values()){
            cleanSessionData(session);
        }
        fbDb.createFeedbackSessions(sessions.values());
        
        HashMap<String, FeedbackQuestionAttributes> questions = dataBundle.feedbackQuestions;
        List<FeedbackQuestionAttributes> questionList = new ArrayList<FeedbackQuestionAttributes>(questions.values());
        Collections.sort(questionList);
        for(FeedbackQuestionAttributes question : questionList){
            question.removeIrrelevantVisibilityOptions();
        }
        fqDb.createFeedbackQuestions(questionList);
        
        HashMap<String, FeedbackResponseAttributes> responses = dataBundle.feedbackResponses;
        for (FeedbackResponseAttributes response : responses.values()) {
            response = injectRealIds(response);
        }
        frDb.createFeedbackResponses(responses.values());
        
        for(FeedbackSessionAttributes session : sessions.values()){
            updateRespondants(session.feedbackSessionName, session.courseId);
        }
        
        HashMap<String, FeedbackResponseCommentAttributes> responseComments = dataBundle.feedbackResponseComments;
        for (FeedbackResponseCommentAttributes responseComment : responseComments.values()) {
            responseComment = injectRealIds(responseComment);
        }
        fcDb.createFeedbackResponseComments(responseComments.values());
        
        HashMap<String, CommentAttributes> comments = dataBundle.comments;
        commentsDb.createComments(comments.values());
        
        // any Db can be used to commit the changes. 
        // accountsDb is used as it is already used in the file
        accountsDb.commitOutstandingChanges();

        
        
        return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
    }

    /**
     * Removes any and all occurrences of the entities in the given databundle
     * from the database
     * @param dataBundle
     */
    public void removeDataBundle(DataBundle dataBundle) {
        deleteExistingData(dataBundle);
    }
    
    /**
     * create document for entities that have document--searchable
     * @param dataBundle
     * @return status of the request in the form 'status meassage'+'additional
     *         info (if any)' e.g., "[BACKEND_STATUS_SUCCESS]" e.g.,
     *         "[BACKEND_STATUS_FAILURE]NullPointerException at ..."
     */
    public String putDocuments(DataBundle dataBundle) {
        // query the entity in db first to get the actual data and create document for actual entity
        
        HashMap<String, StudentAttributes> students = dataBundle.students;
        for (StudentAttributes student : students.values()) {
            StudentAttributes studentInDb = studentsDb.getStudentForEmail(student.course, student.email);
            studentsDb.putDocument(studentInDb);
            ThreadHelper.waitFor(50);
        }
        
        HashMap<String, FeedbackResponseCommentAttributes> responseComments = dataBundle.feedbackResponseComments;
        for (FeedbackResponseCommentAttributes responseComment : responseComments.values()) {
            FeedbackResponseCommentAttributes fcInDb = fcDb.getFeedbackResponseComment(
                    responseComment.courseId, responseComment.createdAt, responseComment.giverEmail);
            fcDb.putDocument(fcInDb);
        }
        
        HashMap<String, CommentAttributes> comments = dataBundle.comments;
        for (CommentAttributes comment : comments.values()) {
            CommentAttributes commentInDb = commentsDb.getComment(comment);
            commentsDb.putDocument(commentInDb);
        }
        
        return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
    }

    public String getAccountAsJson(String googleId) {
        AccountAttributes accountData = getAccount(googleId, true);
        return Utils.getTeammatesGson().toJson(accountData);
    }

    public String getStudentProfileAsJson(String googleId) {
        StudentProfileAttributes profileData = getStudentProfile(googleId);
        return Utils.getTeammatesGson().toJson(profileData);
    }
    
    public String getInstructorAsJsonById(String instructorId, String courseId) {
        InstructorAttributes instructorData = getInstructorForGoogleId(courseId, instructorId);
        return Utils.getTeammatesGson().toJson(instructorData);
    }
    
    public String getInstructorAsJsonByEmail(String instructorEmail, String courseId) {
        InstructorAttributes instructorData = getInstructorForEmail(courseId, instructorEmail);
        return Utils.getTeammatesGson().toJson(instructorData);
    }

    public String getCourseAsJson(String courseId) {
        CourseAttributes course = getCourse(courseId);
        return Utils.getTeammatesGson().toJson(course);
    }

    public String getStudentAsJson(String courseId, String email) {
        StudentAttributes student = getStudentForEmail(courseId, email);
        return Utils.getTeammatesGson().toJson(student);
    }
    
    public String getAllStudentsAsJson(String courseId) throws EntityDoesNotExistException {
        List<StudentAttributes> studentList = studentsLogic
                .getStudentsForCourse(courseId);
        return Utils.getTeammatesGson().toJson(studentList);
    }
    
    public String getFeedbackSessionAsJson(String feedbackSessionName, String courseId) {
        FeedbackSessionAttributes fs = getFeedbackSession(feedbackSessionName, courseId);
        return Utils.getTeammatesGson().toJson(fs);
    }
    
    public String getFeedbackQuestionAsJson(String feedbackSessionName, String courseId, int qnNumber) {
        FeedbackQuestionAttributes fq = 
                feedbackQuestionsLogic.getFeedbackQuestion(feedbackSessionName, courseId, qnNumber);
        return Utils.getTeammatesGson().toJson(fq);
    }
    
    public String getFeedbackQuestionForIdAsJson(String questionId) {
        FeedbackQuestionAttributes fq = 
                feedbackQuestionsLogic.getFeedbackQuestion(questionId);
        return Utils.getTeammatesGson().toJson(fq);
    }

    public String getFeedbackResponseAsJson(String feedbackQuestionId, String giverEmail, String recipient) {
        FeedbackResponseAttributes fq = 
                feedbackResponsesLogic.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
        return Utils.getTeammatesGson().toJson(fq);
    }
    
    public String getFeedbackResponsesForGiverAsJson(String courseId, String giverEmail) {
        List<FeedbackResponseAttributes> responseList = 
                feedbackResponsesLogic.getFeedbackResponsesFromGiverForCourse(courseId, giverEmail);
        return Utils.getTeammatesGson().toJson(responseList);
    }
    
    public String getFeedbackResponsesForReceiverAsJson(String courseId, String recipient) {
        List<FeedbackResponseAttributes> responseList = 
                feedbackResponsesLogic.getFeedbackResponsesForReceiverForCourse(courseId, recipient);
        return Utils.getTeammatesGson().toJson(responseList);
    }
    
    public void editAccountAsJson(String newValues)
            throws InvalidParametersException, EntityDoesNotExistException {
        AccountAttributes account = Utils.getTeammatesGson().fromJson(newValues,
                AccountAttributes.class);
        updateAccount(account);
    }
    
    public void editStudentAsJson(String originalEmail, String newValues)
            throws InvalidParametersException, EntityDoesNotExistException, EnrollException {
        StudentAttributes student = Utils.getTeammatesGson().fromJson(newValues,
                StudentAttributes.class);
        student.section = (student.section == null) ? "None" : student.section;
        updateStudentWithoutDocument(originalEmail, student);
    }
    
    public void editFeedbackSessionAsJson(String feedbackSessionJson)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSessionAttributes feedbackSession = Utils.getTeammatesGson().fromJson(
                feedbackSessionJson, FeedbackSessionAttributes.class);
        updateFeedbackSession(feedbackSession);
    }
    
    /**
     * This method ensures consistency for private feedback sessions
     * between the type and visibility times. This allows easier creation
     * of private sessions by setting the feedbackSessionType field as PRIVATE
     * in the json file.
     */
    private FeedbackSessionAttributes cleanSessionData(FeedbackSessionAttributes session) {
        if (session.feedbackSessionType.equals(FeedbackSessionType.PRIVATE)) {
            session.sessionVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
            session.resultsVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
        }
        return session;
    }
                
    /**
    * This method is necessary to generate the feedbackQuestionId of the
    * question the response is for.<br />
    * Normally, the ID is already generated on creation,
    * but the json file does not contain the actual response ID. <br />
    * Therefore the question number corresponding to the created response 
    * should be inserted in the json file in place of the actual response ID.<br />
    * This method will then generate the correct ID and replace the field.
    **/
    private FeedbackResponseAttributes injectRealIds(FeedbackResponseAttributes response) {
        try {
            int qnNumber = Integer.parseInt(response.feedbackQuestionId);
        
            response.feedbackQuestionId = 
                feedbackQuestionsLogic.getFeedbackQuestion(
                        response.feedbackSessionName, response.courseId,
                        qnNumber).getId();
        } catch (NumberFormatException e) {
            // Correct question ID was already attached to response.
        }
        
        return response;
    }
    
    /**
    * This method is necessary to generate the feedbackQuestionId 
    * and feedbackResponseId of the question and response the comment is for.<br />
    * Normally, the ID is already generated on creation,
    * but the json file does not contain the actual response ID. <br />
    * Therefore the question number and questionNumber%giverEmail%recipient
    * corresponding to the created comment should be inserted in the json 
    * file in place of the actual ID.<br />
    * This method will then generate the correct ID and replace the field.
     * @throws EntityDoesNotExistException 
    **/
    private FeedbackResponseCommentAttributes injectRealIds(FeedbackResponseCommentAttributes responseComment) {
        try {
            int qnNumber = Integer.parseInt(responseComment.feedbackQuestionId);
            
            responseComment.feedbackQuestionId =
                    feedbackQuestionsLogic.getFeedbackQuestion(
                            responseComment.feedbackSessionName,
                            responseComment.courseId,
                            qnNumber).getId();
        } catch (NumberFormatException e) {
            // Correct question ID was already attached to response.
        }
        
        String[] responseIdParam = responseComment.feedbackResponseId.split("%");
        
        responseComment.feedbackResponseId = 
                responseComment.feedbackQuestionId
                + "%" + responseIdParam[1] + "%" + responseIdParam[2];
        
        return responseComment;
    }
    

    /**
     * Creates a COURSE without an INSTRUCTOR relation
     * Used in persisting DataBundles for Test cases
     */
    public void createCourseWithArchiveStatus(CourseAttributes course) 
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, course);
        try {
            coursesLogic.setArchiveStatusOfCourse(course.id, course.isArchived);
        } catch (EntityDoesNotExistException e) {
            coursesLogic.createCourse(course.id, course.name);
            coursesLogic.setArchiveStatusOfCourse(course.id, course.isArchived);
        }
    }

    public void deleteExistingData(DataBundle dataBundle) {
                
        //TODO: questions and responses will be deleted automatically.
        //  We don't attempt to delete them again, to save time.
        deleteCourses(dataBundle.courses.values());
        
        for (AccountAttributes account : dataBundle.accounts.values()) {
            if (account.studentProfile == null) {
                account.studentProfile = new StudentProfileAttributes();
                account.studentProfile.googleId = account.googleId;
            }
        }
        accountsDb.deleteAccounts(dataBundle.accounts.values());
        //waitUntilDeletePersists(dataBundle);
    }

    private void deleteCourses(Collection<CourseAttributes> courses) {  
        List<String> courseIds = new ArrayList<String>();
        for(CourseAttributes course : courses){
            courseIds.add(course.id);
        }
        if(!courseIds.isEmpty()){
            coursesDb.deleteEntities(courses);
            instructorsDb.deleteInstructorsForCourses(courseIds);
            studentsDb.deleteStudentsForCourses(courseIds);
            commentsDb.deleteCommentsForCourses(courseIds);
            fbDb.deleteFeedbackSessionsForCourses(courseIds);
            fqDb.deleteFeedbackQuestionsForCourses(courseIds);
            frDb.deleteFeedbackResponsesForCourses(courseIds);
            fcDb.deleteFeedbackResponseCommentsForCourses(courseIds);
        }
    }

    //TODO: remove this when we confirm it is not needed
    @SuppressWarnings("unused")
    private void waitUntilDeletePersists(DataBundle dataBundle) {
        
        //TODO: this method has too much duplication. 
        for (AccountAttributes a : dataBundle.accounts.values()) {
            Object retreived = null;
            int retryCount = 0;
            while(retryCount < MAX_RETRY_COUNT_FOR_DELETE_CHECKING){
                retreived = this.getAccount(a.googleId);
                if(retreived == null){
                    break;
                }else {
                    retryCount++;
                    ThreadHelper.waitFor(WAIT_DURATION_FOR_DELETE_CHECKING);
                }
            }
            if(retreived != null) {
                log.warning("Object did not get deleted in time \n"+ a.toString());
            }
        }
        
        for (CourseAttributes c : dataBundle.courses.values()) {
            Object retreived = null;
            int retryCount = 0;
            while(retryCount < MAX_RETRY_COUNT_FOR_DELETE_CHECKING){
                retreived = this.getCourse(c.id);
                if(retreived == null){
                    break;
                }else {
                    retryCount++;
                    ThreadHelper.waitFor(WAIT_DURATION_FOR_DELETE_CHECKING);
                }
            }
            if(retreived != null) {
                log.warning("Object did not get deleted in time \n"+ c.toString());
            }
        }
        
        
        for (FeedbackSessionAttributes f : dataBundle.feedbackSessions.values()) {
            Object retreived = null;
            int retryCount = 0;
            while(retryCount < MAX_RETRY_COUNT_FOR_DELETE_CHECKING){
                retreived = this.getFeedbackSession(f.courseId, f.feedbackSessionName);
                if(retreived == null){
                    break;
                }else {
                    retryCount++;
                    if(retryCount%10 == 0) { log.info("Waiting for delete to persist"); }
                    ThreadHelper.waitFor(WAIT_DURATION_FOR_DELETE_CHECKING);
                }
            }
            if(retreived != null) {
                log.warning("Object did not get deleted in time \n"+ f.toString());
            }
        }
        
        //TODO: add missing entity types here
        
        
        for (StudentAttributes s : dataBundle.students.values()) {
            Object retreived = null;
            int retryCount = 0;
            while(retryCount < MAX_RETRY_COUNT_FOR_DELETE_CHECKING){
                retreived = this.getStudentForEmail(s.course, s.email);
                if(retreived == null){
                    break;
                }else {
                    retryCount++;
                    ThreadHelper.waitFor(WAIT_DURATION_FOR_DELETE_CHECKING);
                }
            }
            if(retreived != null) {
                log.warning("Object did not get deleted in time \n"+ s.toString());
            }
        }
        
        for (InstructorAttributes i : dataBundle.instructors.values()) {
            Object retreived = null;
            int retryCount = 0;
            while(retryCount < MAX_RETRY_COUNT_FOR_DELETE_CHECKING){
                retreived = this.getInstructorForEmail(i.courseId, i.email);
                if(retreived == null){
                    break;
                }else {
                    retryCount++;
                    ThreadHelper.waitFor(WAIT_DURATION_FOR_DELETE_CHECKING);
                }
            }
            if(retreived != null) {
                log.warning("Object did not get deleted in time \n"+ i.toString());
            }
        }
    }

    public String isPicturePresentInGcs(String pictureKey) {
        try {
            BlobstoreServiceFactory.getBlobstoreService().fetchData(new BlobKey(pictureKey), 0, 10);
            return BackDoorServlet.RETURN_VALUE_TRUE;
        } catch(IllegalArgumentException | BlobstoreFailureException e) {
            return BackDoorServlet.RETURN_VALUE_FALSE;
        }
    }

    public void uploadAndUpdateStudentProfilePicture(String googleId,
            byte[] pictureData) throws Exception {
        String pictureKey = GoogleCloudStorageHelper.writeDataToGcs(googleId, pictureData, "");
        updateStudentProfilePicture(googleId, pictureKey);
    }
}
