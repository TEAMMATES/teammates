package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.Utils;
import teammates.storage.entity.Submission;

/**
 * Handles CRUD Operations for submission entities.
 * The API uses data transfer classes (i.e. *Attributes) instead of presistable classes.
 */
public class SubmissionsDb extends EntitiesDb {

    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Submission: ";    
    private static final Logger log = Utils.getLogger();
    
    /**
     * Preconditions: <br>
     * * {@code submissionToAdd} is not null and contains valid submission objects.
     */
    public void createSubmissions(List<SubmissionAttributes> newList) throws InvalidParametersException {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newList);
        
        List<Submission> newEntityList = new ArrayList<Submission>();
        
        for (SubmissionAttributes sd : newList) {
            if (!sd.isValid()) {
                throw new InvalidParametersException(sd.getInvalidityInfo());
            }
            //Existence check omitted to save time
            newEntityList.add(sd.toEntity());
            log.info(sd.getBackupIdentifier());
        }
        
        getPM().makePersistentAll(newEntityList);
        getPM().flush();
        
        //Persistence check omitted to save time
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Null if not found.
     */
    public SubmissionAttributes getSubmission(String courseId, String evaluationName,
            String toStudent, String fromStudent) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, evaluationName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, toStudent);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, fromStudent);

        Submission s = getSubmissionEntity(courseId, evaluationName, toStudent, fromStudent);

        if (s == null) {
            log.info("Trying to get non-existent Submission : " + courseId
                    + "/" + evaluationName + "| from " + fromStudent + " to "
                    + toStudent);
            return null;
        }
        return new SubmissionAttributes(s);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Empty list if no matching objects found.
     */
    public List<SubmissionAttributes> getSubmissionsForCourse(String courseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<Submission> submissionList = getSubmissionEntitiesForCourse(courseId);

        List<SubmissionAttributes> submissionDataList = new ArrayList<SubmissionAttributes>();
        for (Submission s : submissionList) {
            if (!JDOHelper.isDeleted(s)) {
                submissionDataList.add(new SubmissionAttributes(s));
            }
        }

        return submissionDataList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Empty list if no matching objects found.
     */
    public List<SubmissionAttributes> getSubmissionsForEvaluation(
            String courseId, String evaluationName) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, evaluationName);
        
        List<Submission> submissionList = getSubmissionEntititesForEvaluation(
                courseId, evaluationName);
        
        List<SubmissionAttributes> submissionDataList = new ArrayList<SubmissionAttributes>();
        for (Submission s : submissionList) {
            if (!JDOHelper.isDeleted(s)) {
                submissionDataList.add(new SubmissionAttributes(s));
            }
        }

        return submissionDataList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Empty list if no matching objects found.
     */
    public List<SubmissionAttributes> getSubmissionsForEvaluationFromStudent(
            String courseId, String evaluationName, String reviewerEmail) {
        //TODO: There is not much use in using Const.StatusCodes.DBLEVEL_NULL_INPUT here.
        //  We can omit that parameter altogether, in all other places similar to the below.
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, evaluationName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, reviewerEmail);

        List<Submission> submissionList = getSubmissionEntitiesForEvaluationFromStudent(
                courseId, evaluationName, reviewerEmail);

        List<SubmissionAttributes> submissionDataList = new ArrayList<SubmissionAttributes>();
        for (Submission s : submissionList) {
            submissionDataList.add(new SubmissionAttributes(s));
        }
        return submissionDataList;
    }

    /**
     * Course ID, evaluation name, reviewer/reviewee emails will not be changed. <br>
     * Does not follow the 'Keep existing' policy. <br>
     * Preconditions: <br> 
     * * {@code newSubmissionAttributes} is not null and has valid data. <br>
     */
    public void updateSubmission(SubmissionAttributes newSubmissionAttributes) 
            throws EntityDoesNotExistException, InvalidParametersException {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newSubmissionAttributes);

        newSubmissionAttributes.sanitizeForSaving();
        
        if (!newSubmissionAttributes.isValid()) {
            throw new InvalidParametersException(newSubmissionAttributes.getInvalidityInfo());
        }

        Submission submission = getSubmissionEntity(newSubmissionAttributes.course, newSubmissionAttributes.evaluation,
                newSubmissionAttributes.reviewee, newSubmissionAttributes.reviewer);

        if (submission == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + Const.EOL
                    + newSubmissionAttributes.toString());
        }

        submission.setPoints(newSubmissionAttributes.points);
        submission.setJustification(newSubmissionAttributes.justification);
        submission.setCommentsToStudent(newSubmissionAttributes.p2pFeedback);

        log.info(newSubmissionAttributes.getBackupIdentifier());
        // closing PM because otherwise the data is not updated during dev server testing
        getPM().close();

    }

    /**
     * Course ID, evaluation name, reviewer/reviewee emails will not be changed. <br>
     * Does not follow the 'Keep existing' policy. <br>
     * Preconditions: <br> 
     * * The given list is not null and contains valid {@link SubmissionAttributes} objects. <br>
     */
    public void updateSubmissions(List<SubmissionAttributes> submissionsList) 
            throws EntityDoesNotExistException, InvalidParametersException {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, submissionsList);

        for (SubmissionAttributes sd : submissionsList) {
            updateSubmission(sd);
        }

        getPM().close();

    }

    /**
     * Preconditions: <br> 
     * * All parameters are non-null. <br>
     * * {@code newEmail} is a valid email.
     */
    public void updateStudentEmailForSubmissionsInCourse(String courseId,
            String originalEmail, String newEmail) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, originalEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newEmail);
        Assumption.assertTrue(new FieldValidator().getInvalidityInfo(FieldType.EMAIL, newEmail).isEmpty());
    
        List<Submission> submissionsFromStudent = 
                getSubmissionEntitiesForCourseFromStudent(courseId, originalEmail);
        for (Submission s : submissionsFromStudent) {
            s.setReviewerEmail(newEmail);
        }
        
        List<Submission> submissionsToStudent = 
                getSubmissionEntitiesForCourseToStudent(courseId, originalEmail);
        for (Submission s : submissionsToStudent) {
            s.setRevieweeEmail(newEmail);
            
            /*
             * This has to be done for cases where a submission belongs to both submissionsFromStudent
             * and submissionsToStudent e.g in case of self-evaluations
             * Without code below, only reviewee email will be be updated as changes to
             * reviewer email of the common element, made in the previous loop, were not persisted
             */
            if (s.getReviewerEmail().equals(originalEmail)) {
                s.setReviewerEmail(newEmail);
            }
        }
        
        log.info(Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId);
        //TODO: We need to update feedback submissions too.
        
        getPM().close();
    }

    /**
     * Fails silently if no matching objects. <br>
     * Preconditions: <br> 
     * * all parameters are non-null.
     */
    public void deleteAllSubmissionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<Submission> submissionList = getSubmissionEntitiesForCourse(courseId);

        getPM().deletePersistentAll(submissionList);
        getPM().flush();

        return;
    }
    
    public void deleteSubmissionsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);
        
        List<Submission> submissionList = getSubmissionEntitiesForCourses(courseIds);
        
        getPM().deletePersistentAll(submissionList);
        getPM().flush();
    }

    /**
     * Fails silently if no matching objects. <br>
     * Preconditions: <br> 
     * * all parameters are non-null.
     */
    public void deleteAllSubmissionsForEvaluation(String courseId,
            String evaluationName) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, evaluationName);
        
        List<Submission> submissionList = getSubmissionEntititesForEvaluation(courseId, evaluationName);

        getPM().deletePersistentAll(submissionList);
        getPM().flush();
    }

    /**
     * Fails silently if no matching objects. <br>
     * Preconditions: <br> 
     * * all parameters are non-null.
     */
    public void deleteAllSubmissionsForStudent(String courseId,
            String studentEmail) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, studentEmail);

        List<Submission> submissionsFromStudent = 
                getSubmissionEntitiesForCourseFromStudent(courseId, studentEmail);
        getPM().deletePersistentAll(submissionsFromStudent);
        
        List<Submission> submissionsToStudent = 
                getSubmissionEntitiesForCourseToStudent(courseId, studentEmail);
        getPM().deletePersistentAll(submissionsToStudent);
        
        getPM().flush();
    }
    
    public void deleteAllSubmissionsForEvaluationForStudent(String courseId,
            String evaluationName, String studentEmail) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, studentEmail);

        List<Submission> submissionsFromStudent = 
                getSubmissionEntitiesForEvaluationFromStudent(courseId, evaluationName, studentEmail);
        getPM().deletePersistentAll(submissionsFromStudent);
        
        List<Submission> submissionsToStudent = 
                getSubmissionEntitiesForEvaluationToStudent(courseId, evaluationName, studentEmail);
        getPM().deletePersistentAll(submissionsToStudent);
        
        getPM().flush();
    }
    
    private Submission getSubmissionEntity(String courseId,
            String evaluationName, String toStudent, String fromStudent) {

        Query q = getPM().newQuery(Submission.class);
        q.declareParameters(
                "String courseIdParam, " +
                "String evluationNameParam, " +
                "String fromStudentParam, " +
                "String toStudentParam");
        
        q.setFilter("courseID == courseIdParam"
                + " && evaluationName == evluationNameParam"
                + " && fromStudent == fromStudentParam"
                + " && toStudent == toStudentParam");
        
        // To pass in more than 3 parameters, an object array is needed. 
        Object[] parameters = {courseId, evaluationName, fromStudent, toStudent};

        // jdo.Query.execute() method only support up to 3 parameter.
        // executeWithArray() is used when more than 3 parameters are used in a query.
        @SuppressWarnings("unchecked")
        List<Submission> submissionList = (List<Submission>) q.executeWithArray(parameters);

        if (submissionList.isEmpty() || JDOHelper.isDeleted(submissionList.get(0))) {
            return null;
        }

        return submissionList.get(0);
    }

    private List<Submission> getSubmissionEntitiesForCourse(String courseId) {
        Query q = getPM().newQuery(Submission.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseID == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<Submission> submissionList = (List<Submission>) q.execute(courseId);
        return submissionList;
    }
    
    private List<Submission> getSubmissionEntitiesForCourses(List<String> courseIds) {
        Query q = getPM().newQuery(Submission.class);
        q.setFilter(":p.contains(courseID)");
        
        @SuppressWarnings("unchecked")
        List<Submission> submissionList = (List<Submission>) q.execute(courseIds);
        return submissionList;
    }

    private List<Submission> getSubmissionEntititesForEvaluation(
            String courseId, String evaluationName) {
        Query q = getPM().newQuery(Submission.class);
        q.declareParameters("String courseIdParam, String evaluationNameParam");
        q.setFilter("courseID == courseIdParam && evaluationName == evaluationNameParam");

        @SuppressWarnings("unchecked")
        List<Submission> submissionList = (List<Submission>) q.execute(
                courseId, evaluationName);
        return submissionList;
    }

    private List<Submission> getSubmissionEntitiesForCourseFromStudent(
            String courseId, String reviewerEmail) {
        
        Query q = getPM().newQuery(Submission.class);
        q.declareParameters(
                "String courseIdParam, " +
                "String reviewerEmailParam");
        q.setFilter("courseID == courseIdParam " +
                "&& fromStudent == reviewerEmailParam");
        
        @SuppressWarnings("unchecked")
        List<Submission> submissionList = (List<Submission>) q.execute(courseId, reviewerEmail);
        return submissionList;
    }
    
    private List<Submission> getSubmissionEntitiesForCourseToStudent(
            String courseId, String revieweeEmail) {
        
        Query q = getPM().newQuery(Submission.class);
        q.declareParameters(
                "String courseIdParam, " +
                "String revieweeEmailParam");
        q.setFilter("courseID == courseIdParam " +
                "&& toStudent == revieweeEmailParam");
        
        @SuppressWarnings("unchecked")
        List<Submission> submissionList = (List<Submission>) q.execute(courseId, revieweeEmail);
        return submissionList;
    }
    
    private List<Submission> getSubmissionEntitiesForEvaluationFromStudent(
            String courseId, String evaluationName, String reviewerEmail) {
        
        Query q = getPM().newQuery(Submission.class);
        q.declareParameters(
                "String courseIdParam, " +
                "String evaluationNameParam, " +
                "String reviewerEmailParam");
        q.setFilter("courseID == courseIdParam " +
                "&& evaluationName == evaluationNameParam" +
                "&& fromStudent == reviewerEmailParam");
        
        @SuppressWarnings("unchecked")
        List<Submission> submissionList = (List<Submission>) q.execute(courseId, evaluationName, reviewerEmail);
        return submissionList;
    }
    
    private List<Submission> getSubmissionEntitiesForEvaluationToStudent(
            String courseId, String evaluationName, String reviewerEmail) {
        
        Query q = getPM().newQuery(Submission.class);
        q.declareParameters(
                "String courseIdParam, " +
                "String evaluationNameParam, " +
                "String revieweeEmailParam");
        q.setFilter("courseID == courseIdParam " +
                "&& evaluationName == evaluationNameParam" +
                "&& toStudent == revieweeEmailParam");
        
        @SuppressWarnings("unchecked")
        List<Submission> submissionList = (List<Submission>) q.execute(courseId, evaluationName, reviewerEmail);
        return submissionList;
    }

    @Override
    protected Object getEntity(EntityAttributes attributes) {
        SubmissionAttributes submissionToAdd = (SubmissionAttributes)attributes;
        return getSubmissionEntity(
                submissionToAdd.course,
                submissionToAdd.evaluation, 
                submissionToAdd.reviewee,
                submissionToAdd.reviewer);
    }

}
