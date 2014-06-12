package teammates.logic.backdoor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPermissionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Utils;
import teammates.logic.api.Logic;
import teammates.storage.api.EvaluationsDb;

public class BackDoorLogic extends Logic {
    private static Logger log = Utils.getLogger();
    
    private static final int WAIT_DURATION_FOR_DELETE_CHECKING = 500;
    private static final int MAX_RETRY_COUNT_FOR_DELETE_CHECKING = 20;
    
    
    /**
     * Persists given data in the datastore Works ONLY if the data is correct.
     * Any existing copies of the data in the datastore will be overwritten.
     * 
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
        
        deleteExistingData(dataBundle);
        
        HashMap<String, AccountAttributes> accounts = dataBundle.accounts;
        for (AccountAttributes account : accounts.values()) {
            log.fine("API Servlet adding account :" + account.googleId);
            super.createAccount(account.googleId, account.name, account.isInstructor,
                                    account.email, account.institute);
        }

        HashMap<String, CourseAttributes> courses = dataBundle.courses;
        for (CourseAttributes course : courses.values()) {
            log.fine("API Servlet adding course :" + course.id);
            this.createCourseWithArchiveStatus(course.id, course.name, course.isArchived);
            
        }

        HashMap<String, InstructorAttributes> instructors = dataBundle.instructors;
        for (InstructorAttributes instructor : instructors.values()) {
            if (instructor.googleId != null) {
                log.fine("API Servlet adding instructor :" + instructor.googleId);
                AccountAttributes existingAccount = getAccount(instructor.googleId);
                //Hardcoding institute value because this is used for testing only
                super.createInstructorAccount(instructor.googleId, instructor.courseId, 
                        instructor.name, instructor.email, existingAccount==null? "National University of Singapore" : existingAccount.institute);
            } else {
                log.fine("API Servlet adding instructor :" + instructor.email);
                //Hardcoding institute value because this is used for testing only
                super.instructorsLogic.createInstructor(instructor.googleId, instructor.courseId, instructor.name, instructor.email);
                //TODO:may not need to access instructorsLogic here
            }
        }
        
        HashMap<String, InstructorPermissionAttributes> instructorPermissions = dataBundle.instructorPermissions;
        for (InstructorPermissionAttributes instrPermission : instructorPermissions.values()) {
            log.fine("API Servlet adding instructorPermission:" + instrPermission.instructorEmail);
            try {
                super.addInstructorPermission(instrPermission);
            } catch(EntityAlreadyExistsException e) {
                super.updateInstructorPermissionByEmail(instrPermission, instrPermission.instructorEmail);
            }
        }

        HashMap<String, StudentAttributes> students = dataBundle.students;
        for (StudentAttributes student : students.values()) {
            log.fine("API Servlet adding student :" + student.email
                    + " to course " + student.course);
            student.section = (student.section == null) ? "None" : student.section;
            super.createStudent(student);
        }

        HashMap<String, EvaluationAttributes> evaluations = dataBundle.evaluations;
        for (EvaluationAttributes evaluation : evaluations.values()) {
            log.fine("API Servlet adding evaluation :" + evaluation.name
                    + " to course " + evaluation.courseId);
            createEvaluationWithoutSubmissionQueue(evaluation);
        }

        // processing is slightly different for submissions because we are
        // adding all submissions in one go
        HashMap<String, SubmissionAttributes> submissionsMap = dataBundle.submissions;
        List<SubmissionAttributes> submissionsList = new ArrayList<SubmissionAttributes>();
        for (SubmissionAttributes submission : submissionsMap.values()) {
            log.fine("API Servlet adding submission for "
                    + submission.evaluation + " from " + submission.reviewer
                    + " to " + submission.reviewee);
            submissionsList.add(submission);
        }
        submissionsLogic.updateSubmissions(submissionsList);
        log.fine("API Servlet added " + submissionsList.size() + " submissions");

        HashMap<String, FeedbackSessionAttributes> sessions = dataBundle.feedbackSessions;
        for (FeedbackSessionAttributes session : sessions.values()) {
            log.info("API Servlet adding feedback session :" + session.feedbackSessionName
                    + " to course " + session.courseId);            
            this.createFeedbackSession(session);
        }
        
        HashMap<String, FeedbackQuestionAttributes> questions = dataBundle.feedbackQuestions;
        List<FeedbackQuestionAttributes> questionList = new ArrayList<FeedbackQuestionAttributes>(questions.values());
        Collections.sort(questionList);

        for (FeedbackQuestionAttributes question : questionList) {
            log.fine("API Servlet adding feedback question :" + question.getId()
                    + " to session " + question.feedbackSessionName);
            super.createFeedbackQuestion(question);
        }
        
        HashMap<String, FeedbackResponseAttributes> responses = dataBundle.feedbackResponses;
        for (FeedbackResponseAttributes response : responses.values()) {
            log.fine("API Servlet adding feedback response :" + response.getId()
                    + " to session " + response.feedbackSessionName);
            this.createFeedbackResponse(response);
        }
        
        HashMap<String, FeedbackResponseCommentAttributes> responseComments = dataBundle.feedbackResponseComments;
        for (FeedbackResponseCommentAttributes responseComment : responseComments.values()) {
            log.fine("API Servlet adding feedback response comment :" + responseComment.getId()
                    + " to session " + responseComment.feedbackSessionName);
            this.createFeedbackResponseComment(responseComment);
        }
        
        HashMap<String, CommentAttributes> comments = dataBundle.comments;
        for(CommentAttributes comment : comments.values()){
            log.fine("API Servlet adding comment :" + comment.getCommentId() + " from "
                    + comment.giverEmail + " to " + comment.receiverEmail + " in course " + comment.courseId);
            this.createComment(comment);
        }
        
        return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
    }

    public String getAccountAsJson(String googleId) {
        AccountAttributes accountData = getAccount(googleId);
        return Utils.getTeammatesGson().toJson(accountData);
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

    public String getEvaluationAsJson(String courseId, String evaluationName) {
        EvaluationAttributes evaluation = getEvaluation(courseId, evaluationName);
        return Utils.getTeammatesGson().toJson(evaluation);
    }

    public String getSubmissionAsJson(String courseId, String evaluationName,
            String reviewerEmail, String revieweeEmail) {
        SubmissionAttributes target = getSubmission(courseId, evaluationName,
                reviewerEmail, revieweeEmail);
        return Utils.getTeammatesGson().toJson(target);
    }
    
    public String getAllSubmissionsAsJson(String courseId) {
        List<SubmissionAttributes> submissionList = submissionsLogic
                .getSubmissionsForCourse(courseId);
        return Utils.getTeammatesGson().toJson(submissionList);
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
        updateStudent(originalEmail, student);
    }

    public void editEvaluationAsJson(String evaluationJson)
            throws InvalidParametersException, EntityDoesNotExistException {
        EvaluationAttributes evaluation = Utils.getTeammatesGson().fromJson(
                evaluationJson, EvaluationAttributes.class);
        updateEvaluation(evaluation);
    }
    
    public void editFeedbackSessionAsJson(String feedbackSessionJson)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSessionAttributes feedbackSession = Utils.getTeammatesGson().fromJson(
                feedbackSessionJson, FeedbackSessionAttributes.class);
        updateFeedbackSession(feedbackSession);
    }

    public void editSubmissionAsJson(String submissionJson) throws InvalidParametersException, EntityDoesNotExistException {
        SubmissionAttributes submission = Utils.getTeammatesGson().fromJson(
                submissionJson, SubmissionAttributes.class);
        ArrayList<SubmissionAttributes> submissionList = new ArrayList<SubmissionAttributes>();
        submissionList.add(submission);
        updateSubmissions(submissionList);
    }
    
    public void updateEvaluation(EvaluationAttributes evaluation) 
            throws InvalidParametersException, EntityDoesNotExistException{
        //Using EvaluationsDb here because the update operations at higher levels are too restrictive.
        new EvaluationsDb().updateEvaluation(evaluation);
    }
    
    /**
     * This method ensures consistency for private feedback sessions
     * between the type and visibility times. This allows easier creation
     * of private sessions by setting the feedbackSessionType field as PRIVATE
     * in the json file.
     */
    @Override
    public void createFeedbackSession(FeedbackSessionAttributes session) 
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        if (session.feedbackSessionType.equals(FeedbackSessionType.PRIVATE)) {
            session.sessionVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
            session.resultsVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
        }
        super.createFeedbackSession(session);
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
    @Override
    public void createFeedbackResponse(FeedbackResponseAttributes response) 
            throws InvalidParametersException, EntityAlreadyExistsException {
        
        try {
            int qnNumber = Integer.parseInt(response.feedbackQuestionId);
        
            response.feedbackQuestionId = 
                feedbackQuestionsLogic.getFeedbackQuestion(
                        response.feedbackSessionName, response.courseId,
                        qnNumber).getId();
        } catch (NumberFormatException e) {
            // Correct question ID was already attached to response.
        }

        super.createFeedbackResponse(response);
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
    @Override
    public void createFeedbackResponseComment(FeedbackResponseCommentAttributes responseComment) 
            throws InvalidParametersException, EntityDoesNotExistException {
        
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

        super.createFeedbackResponseComment(responseComment);
    }
    

    /**
     * Creates a COURSE without an INSTRUCTOR relation
     * Used in persisting DataBundles for Test cases
     */
    public void createCourseWithArchiveStatus(String courseId, String courseName, boolean archiveStatus) 
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseName);

        coursesLogic.createCourse(courseId, courseName);
        coursesLogic.setArchiveStatusOfCourse(courseId, archiveStatus);
    }

    private void deleteExistingData(DataBundle dataBundle) {
        
        //Deleting submissions is not supported at Logic level. However, they
        //  will be deleted automatically when we delete evaluations.
        
        for (StudentAttributes s : dataBundle.students.values()) {
            deleteStudent(s.course, s.email);
        }
        
        for (InstructorAttributes i : dataBundle.instructors.values()) {
            deleteInstructor(i.courseId, i.email);
        }
        
        for (InstructorPermissionAttributes i : dataBundle.instructorPermissions.values()) {
            deleteInstructorPermission(i.courseId, i.instructorEmail);
        }
        
        for (EvaluationAttributes e : dataBundle.evaluations.values()) {
            deleteEvaluation(e.courseId, e.name);
        }
        
        for (FeedbackSessionAttributes f : dataBundle.feedbackSessions.values()) {
            deleteFeedbackSession(f.feedbackSessionName, f.courseId);
        }
        
        //TODO: questions and responses will be deleted automatically.
        //  We don't attempt to delete them again, to save time.
        
        for (CourseAttributes c : dataBundle.courses.values()) {
            this.deleteCourse(c.id);
        }
        
        for (AccountAttributes a : dataBundle.accounts.values()) {
            deleteAccount(a.googleId);
        }
        
        for(FeedbackResponseCommentAttributes frc : dataBundle.feedbackResponseComments.values()) {
            deleteFeedbackResponseComment(frc);
        }
        
        for(CommentAttributes c : dataBundle.comments.values()){
            deleteComment(c);
        }
        
        waitUntilDeletePersists(dataBundle);
    }

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
        
        for (EvaluationAttributes e : dataBundle.evaluations.values()) {
            Object retreived = null;
            int retryCount = 0;
            while(retryCount < MAX_RETRY_COUNT_FOR_DELETE_CHECKING){
                retreived = this.getEvaluation(e.courseId, e.name);
                if(retreived == null){
                    break;
                }else {
                    retryCount++;
                    if(retryCount%10 == 0) { log.info("Waiting for delete to persist"); }
                    ThreadHelper.waitFor(WAIT_DURATION_FOR_DELETE_CHECKING);
                }
            }
            if(retreived != null) {
                log.warning("Object did not get deleted in time \n"+ e.toString());
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
        
        for (SubmissionAttributes s : dataBundle.submissions.values()) {
            Object retreived = null;
            int retryCount = 0;
            while(retryCount < MAX_RETRY_COUNT_FOR_DELETE_CHECKING){
                retreived = this.getSubmission(s.course, s.evaluation, s.reviewer, s.reviewee);
                if(retreived == null){
                    break;
                }else {
                    retryCount++;
                    ThreadHelper.waitFor(WAIT_DURATION_FOR_DELETE_CHECKING);
                }
            }
            if(retreived != null) {
                log.warning("Object did not get deleted in time \n"+ Utils.getTeammatesGson().toJson(s));
            }
        }
        
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

    private SubmissionAttributes getSubmission(
            String courseId, String evaluationName, String reviewerEmail, String revieweeEmail) {
                
        return submissionsLogic.getSubmission(
                courseId, evaluationName, revieweeEmail, reviewerEmail);
    }
}
