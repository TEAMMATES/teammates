package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.api.SubmissionsDb;

/**
 * Handles  operations related to submission entities.
 */
public class SubmissionsLogic {
    //The API of this class doesn't have header comments because it sits behind
    //  the API of the logic class. Those who use this class is expected to be
    //  familiar with the its code and Logic's code. Hence, we have minimal
    //  header comments in this class.
    
    private static SubmissionsLogic instance = null;
    @SuppressWarnings("unused")
    private static final Logger log = Utils.getLogger();

    private static final SubmissionsDb submissionsDb = new SubmissionsDb();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    public static SubmissionsLogic inst() {
        if (instance == null){
            instance = new SubmissionsLogic();
        }
        return instance;
    }

    public void createSubmissions(
            List<SubmissionAttributes> listOfSubmissionsToAdd) throws InvalidParametersException {
        submissionsDb.createSubmissions(listOfSubmissionsToAdd);
        
    }

    public SubmissionAttributes getSubmission(String course, String evaluation, String reviewee, String reviewer) {
        return submissionsDb.getSubmission(course, evaluation, reviewee, reviewer);
    }

    public List<SubmissionAttributes> getSubmissionsForCourse(String courseId) {
        return submissionsDb.getSubmissionsForCourse(courseId);
    }

    public List<SubmissionAttributes> getSubmissionsForEvaluation(String courseId,String evaluationName) {
        return submissionsDb.getSubmissionsForEvaluation(courseId, evaluationName);
    }
    
    public HashMap<String, SubmissionAttributes> getSubmissionsForEvaluationAsMap(
            String courseId, String evaluationName){

        List<SubmissionAttributes> submissionsList = getSubmissionsForEvaluation(courseId, evaluationName);

        HashMap<String, SubmissionAttributes> submissionDataList = new HashMap<String, SubmissionAttributes>();
        for (SubmissionAttributes sd : submissionsList) {
            submissionDataList.put(sd.reviewer + "->" + sd.reviewee, sd);
        }
        return submissionDataList;
    }

    public List<SubmissionAttributes> getSubmissionsForEvaluationFromStudent(
            String courseId, String evaluationName, String reviewerEmail) {
        
        List<SubmissionAttributes> submissions = submissionsDb.getSubmissionsForEvaluationFromStudent(courseId, evaluationName, reviewerEmail);

        StudentAttributes student = studentsLogic.getStudentForEmail(courseId, reviewerEmail);
        ArrayList<SubmissionAttributes> returnList = new ArrayList<SubmissionAttributes>();
        for (SubmissionAttributes sd : submissions) {
            StudentAttributes reviewee = studentsLogic.getStudentForEmail(courseId, sd.reviewee);
            if (!isOrphanSubmission(student, reviewee, sd)) {
                sd.details.reviewerName = student.name;
                sd.details.revieweeName = reviewee.name;
                returnList.add(sd);
            }
        }
        return returnList;
    }

    public boolean hasStudentSubmittedEvaluation(
            String courseId, String evaluationName, String studentEmail) {
        List<SubmissionAttributes> submissions = null;
        
        submissions = getSubmissionsForEvaluationFromStudent(
                courseId, evaluationName, studentEmail);
    
        for (SubmissionAttributes sd : submissions) {
            if (sd.points != Const.POINTS_NOT_SUBMITTED) {
                return true;
            }
        }
        
        return false;
    }

    public void updateSubmission(SubmissionAttributes submission) 
            throws InvalidParametersException, EntityDoesNotExistException {
        
        submissionsDb.updateSubmission(submission);
    }

    public void updateSubmissions(List<SubmissionAttributes> submissionsDataList) 
            throws EntityDoesNotExistException, InvalidParametersException {
        submissionsDb.updateSubmissions(submissionsDataList);
    }

    public void updateStudentEmailForSubmissionsInCourse(String course,
            String originalEmail, String newEmail) {
        submissionsDb.updateStudentEmailForSubmissionsInCourse(course, originalEmail, newEmail);
        
    }


    public void deleteAllSubmissionsForStudent(String courseId, String studentEmail) {
        submissionsDb.deleteAllSubmissionsForStudent(courseId, studentEmail);
    }
    
    public void deleteAllSubmissionsForEvaluationForStudent(String courseId,
            String evaluationName, String studentEmail) {
        submissionsDb.deleteAllSubmissionsForEvaluationForStudent(courseId,
                evaluationName, studentEmail);
    }

    
    public void deleteAllSubmissionsForCourse(String courseId) {
        submissionsDb.deleteAllSubmissionsForCourse(courseId);
        
    }

    public void deleteAllSubmissionsForEvaluation(String courseId,
            String evaluationName) {
        submissionsDb.deleteAllSubmissionsForEvaluation(courseId, evaluationName);
        
    }
    
    /**
     * Returns true if either of the three objects is null or if the team in
     * submission is different from those in two students.
     */
    private boolean isOrphanSubmission(StudentAttributes reviewer,
            StudentAttributes reviewee, SubmissionAttributes submission) {
        if ((reviewer == null) || (reviewee == null) || (submission == null)) {
            return true;
        }
        if (!submission.team.equals(reviewer.team)) {
            return true;
        }
        if (!submission.team.equals(reviewee.team)) {
            return true;
        }
        return false;
    }


}
