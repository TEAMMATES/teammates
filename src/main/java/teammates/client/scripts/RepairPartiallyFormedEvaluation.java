package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.Course;
import teammates.storage.entity.Evaluation;
import teammates.storage.entity.Student;
import teammates.storage.entity.Submission;

import com.google.appengine.api.datastore.Text;

/**
 * Adds any missing submission entities to an evaluation.
 * This is useful in cases where submission creation process for a new evaluation
 * was only partially completed.
 */
public class RepairPartiallyFormedEvaluation extends RemoteApiClient {
    
    //TODO: remove pm and use Datastore.initialize(); as done in GenerateFeedbackReport
    protected static final PersistenceManager pm = JDOHelper
            .getPersistenceManagerFactory("transactions-optional")
            .getPersistenceManager();
    
    static boolean isTrialRun = true; //set this true to skip writing to database
    
    //TODO: This class contains lot of code copy-pasted from the Logic and 
    //  Storage layer. This duplication can be removed if we figure out 
    //  to reuse the Logic API from here.
    
    public static void main(String[] args) throws IOException {
        RepairPartiallyFormedEvaluation repairman = new RepairPartiallyFormedEvaluation();
        repairman.doOperationRemotely();
    }

    private int missingSubmissionsCount;
    private int duplicateSubmissionsCount;
    private int problematicSubmissionsCount;
    private int repariedEvaluationCount;
    
    protected void doOperation() {
        // repair all evaluations with course created between startDate and endDate inclusive
        Date startDate = TimeHelper.getDateOffsetToCurrentTime(-60);
        Date endDate = TimeHelper.getDateOffsetToCurrentTime(-50);
        repariedEvaluationCount = 0;
        
        List<Evaluation> evaluationsToFix = getEvaluationsWithCourseCreatedBetweenDates(startDate, endDate);
        for (Evaluation eval : evaluationsToFix) {
            if (!JDOHelper.isDeleted(eval)) {
                repairEvaluation(eval.getCourseId(), eval.getName());
            }
        }
        System.out.println("Number of evaluations repaired :" + repariedEvaluationCount);
    }

    private void repairEvaluation(String courseId, String evaluationName) {
        try {
            if (courseId.contains("-demo")) {
                return;
            }
            System.out.println("Reparing ["+courseId+"]"+evaluationName);
            repariedEvaluationCount++;
            
            repairSubmissionsForEvaluation(courseId,    evaluationName);
            
            printInNextLine("Number of submissions added :"+missingSubmissionsCount);
            System.out.println("Number of submissions deleted :"+duplicateSubmissionsCount);
            System.out.println("Number of problematic submissions :"+problematicSubmissionsCount);
            
        } catch (EntityAlreadyExistsException | InvalidParametersException
                | EntityDoesNotExistException e) {
            e.printStackTrace();
        }
    }
    
    private void repairSubmissionsForEvaluation(String courseId, String evaluationName)
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
    
        List<StudentAttributes> studentDataList = getStudentsForCourse(courseId);
        
        // This double loop creates 3 submissions for a pair of students:
        // x->x, x->y, y->x
        for (StudentAttributes sx : studentDataList) {
            System.out.print("[" + sx.name + "]");
            for (StudentAttributes sy : studentDataList) {
                
                if (!sx.team.equals(sy.team)) { //not in the same team
                    continue;
                }
                
                List<Submission> existingSubmissions = 
                        getSubmissionEntities(courseId, evaluationName, sx.email, sy.email);
                if (existingSubmissions.size() == 1) {
                    continue;
                } else if(existingSubmissions.size()>1){
                    pruneDuplicateSubmissions(existingSubmissions);
                } else {
                    addMissingSubmission(courseId, evaluationName, sx.team, sx.email, sy.email);
                }
            }
        }
    
    }
    
    private void pruneDuplicateSubmissions(List<Submission> submissions) {
        //for convenience, we assume there aren't more than 1 duplicate
        Assumption.assertTrue("\nThere is more than one duplicate for "+submissions.get(0).toString(), 
                submissions.size()==2); 
        
        Submission firstSubmission = submissions.get(0);
        Submission secondSubmission = submissions.get(1);
        
        if(isEmptySubmission(firstSubmission)){
            deleteSubmission(firstSubmission);
        }else if(isEmptySubmission(secondSubmission)){
            deleteSubmission(secondSubmission);
        }else{
            problematicSubmissionsCount++;
            printInNextLine("###### both submissions not empty!!!!!!!!" + firstSubmission.toString());
        }
        
    }

    private void addMissingSubmission(String courseId, String evaluationName,
            String team, String toStudent, String fromStudent) {
        SubmissionAttributes submissionToAdd = 
                new SubmissionAttributes(courseId, evaluationName, team, toStudent, fromStudent);
        submissionToAdd.p2pFeedback = new Text("");
        submissionToAdd.justification = new Text("");
        printInNextLine("Creating missing submission "+ submissionToAdd.toString());
        
        if (!isTrialRun) {
            pm.makePersistent(submissionToAdd.toEntity());
            pm.flush();
        }
        missingSubmissionsCount++;
        
    }


    private void deleteSubmission(Submission s) {
        printInNextLine("Deleting duplicate submisssion " + s.toString());
        if (!isTrialRun) {
            pm.deletePersistent(s);
            pm.flush();
        }
        duplicateSubmissionsCount++;
    }

    private void printInNextLine(String string) {
        System.out.println("\n"+string);
    }

    private boolean isEmptySubmission(Submission s) {
        return s.getPoints() == Const.POINTS_NOT_SUBMITTED 
                && hasNoValue(s.getCommentsToStudent())
                && hasNoValue(s.getJustification());
    }
    
    private boolean hasNoValue(Text text){
        return text.getValue() == null || 
                text.getValue().isEmpty();
    }

    private List<Submission> getSubmissionEntities(String courseId,
            String evaluationName, String toStudent, String fromStudent) {

        Query q = pm.newQuery(Submission.class);
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
        
        return submissionList;
    }
    
    private List<StudentAttributes> getStudentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<Student> studentList = getStudentEntitiesForCourse(courseId);
        
        List<StudentAttributes> studentDataList = new ArrayList<StudentAttributes>();
    
        for (Student s : studentList) {
            if (!JDOHelper.isDeleted(s)) {
                studentDataList.add(new StudentAttributes(s));
            }
        }
    
        return studentDataList;
    }
    
    private List<Student> getStudentEntitiesForCourse(String courseId) {
        Query q = pm.newQuery(Student.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseID == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<Student> studentList = (List<Student>) q.execute(courseId);
        return studentList;
    }
    
    private List<Evaluation> getEvaluationsWithCourseCreatedBetweenDates(Date startDate, Date endDate){
        Query q = pm.newQuery(Course.class);
        q.declareParameters("java.util.Date startDateParam, java.util.Date endDateParam");
        q.setFilter("createdAt >= startDateParam && createdAt <= endDateParam");

        @SuppressWarnings("unchecked")
        List<Course> courseList = (List<Course>) q.execute(startDate, endDate);
        
        List<Evaluation> evaluationList = new ArrayList<Evaluation>();
        for (Course course : courseList) {
            q = pm.newQuery(Evaluation.class);
            q.declareParameters("String courseIdParam");
            q.setFilter("courseID == courseIdParam");
            
            @SuppressWarnings("unchecked")
            List<Evaluation> courseEvalList =
                    (List<Evaluation>) q.execute(course.getUniqueId());

            evaluationList.addAll(courseEvalList);
        }

        return evaluationList;
    }
}
