package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentHomePageAction extends Action {
    
    private StudentHomePageData data;
    

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyLoggedInUserPrivileges();
        
        data = new StudentHomePageData(account);
        String recentlyJoinedCourseId = getRequestParamValue(Const.ParamsNames.CHECK_PERSISTENCE_COURSE);
        
        
        try{
            data.courses = logic.getCourseDetailsListForStudent(account.googleId);
    
            data.evalSubmissionStatusMap = generateEvalSubmissionStatusMap(data.courses, account.googleId);
            data.sessionSubmissionStatusMap = generateFeedbackSessionSubmissionStatusMap(data.courses, account.googleId);
            CourseDetailsBundle.sortDetailedCourses(data.courses);
            
            statusToAdmin = "studentHome Page Load<br>" + "Total courses: " + data.courses.size();
            
            boolean isDataConsistent = checkEventualConsistency(recentlyJoinedCourseId);
            if(!isDataConsistent) {
                addPlaceholderCourse(recentlyJoinedCourseId, account.googleId);
                data.setEventualConsistencyCourse(recentlyJoinedCourseId);
            }
            
            for(CourseDetailsBundle course: data.courses){
                EvaluationDetailsBundle.sortEvaluationsByDeadline(course.evaluations);
                FeedbackSessionDetailsBundle.sortFeedbackSessionsByCreationTime(course.feedbackSessions);
            }
            
        } catch (EntityDoesNotExistException e){
            if(recentlyJoinedCourseId != null) {
                addPlaceholderCourse(recentlyJoinedCourseId, account.googleId);
            } else {
                statusToUser.add(Const.StatusMessages.STUDENT_FIRST_TIME);
                statusToAdmin = Const.ACTION_RESULT_FAILURE + " :" + e.getMessage();
            }
        } 
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.STUDENT_HOME, data);
        
        return response;

    }
    
    
    private Map<String, String> generateEvalSubmissionStatusMap(
            List<CourseDetailsBundle> courses, String googleId) {
        Map<String, String> returnValue = new HashMap<String, String>();
        
        String recentlySubmittedEvaluation = getRequestParamValue(Const.ParamsNames.CHECK_PERSISTENCE_EVALUATION);
        
        for(CourseDetailsBundle c: courses){
            for(EvaluationDetailsBundle edb: c.evaluations){
                EvaluationAttributes e = edb.evaluation;
                
                String currentEvaluation = e.courseId+e.name;
                boolean isEvaluationRecentlySubmitted = currentEvaluation.equals(recentlySubmittedEvaluation);
                
                if(isEvaluationRecentlySubmitted) {
                    returnValue.put(e.courseId+"%"+e.name, Const.STUDENT_EVALUATION_STATUS_SUBMITTED);
                } else {
                    returnValue.put(e.courseId+"%"+e.name, getStudentStatusForEval(e, googleId));
                }
            }
        }
        return returnValue;
    }

    private Map<String, Boolean> generateFeedbackSessionSubmissionStatusMap(
            List<CourseDetailsBundle> courses, String googleId) {
        Map<String, Boolean> returnValue = new HashMap<String, Boolean>();

        
        for(CourseDetailsBundle c: courses){
            for(FeedbackSessionDetailsBundle fsb: c.feedbackSessions){
                FeedbackSessionAttributes f = fsb.feedbackSession;
                returnValue.put(f.courseId+"%"+f.feedbackSessionName, getStudentStatusForSession(f, googleId));
            }
        }
        return returnValue;
    }

    private String getStudentStatusForEval(EvaluationAttributes eval, String googleId){
        
        StudentAttributes student = logic.getStudentForGoogleId(eval.courseId, googleId);
        Assumption.assertNotNull(student);

        String studentEmail = student.email;
        
        switch (eval.getStatus()) {
            case PUBLISHED:
                return Const.STUDENT_EVALUATION_STATUS_PUBLISHED;
            case CLOSED:
                return Const.STUDENT_EVALUATION_STATUS_CLOSED;
            default:
                break; // continue processing.
        }
        
        boolean submitted = false;
        
        try {
            submitted = logic.hasStudentSubmittedEvaluation(eval.courseId, eval.name, studentEmail);
        } catch (InvalidParametersException e) {
            Assumption.fail("Parameters are expected to be valid at this point :" + TeammatesException.toStringWithStackTrace(e));
        }
        
        return submitted ? 
                Const.STUDENT_EVALUATION_STATUS_SUBMITTED 
                : Const.STUDENT_EVALUATION_STATUS_PENDING;
    }
    
    private boolean getStudentStatusForSession(FeedbackSessionAttributes fs, String googleId){
        
        StudentAttributes student = logic.getStudentForGoogleId(fs.courseId, googleId);
        Assumption.assertNotNull(student);

        String studentEmail = student.email;
        
        try {
            return logic.hasStudentSubmittedFeedback(
                    fs.courseId, fs.feedbackSessionName, studentEmail);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            Assumption.fail("Parameters are expected to be valid at this point :" + TeammatesException.toStringWithStackTrace(e));
            return false;
        }
    }
    
    private boolean checkEventualConsistency(String recentlyJoinedCourseId) {
        boolean isDataConsistent = false;
        
        if(recentlyJoinedCourseId == null) {
            isDataConsistent = true;
        } else {
            for(CourseDetailsBundle currentCourse : data.courses) {
                if(currentCourse.course.id.equals(recentlyJoinedCourseId)) {
                    isDataConsistent = true;
                }
            }
        }
        
        return isDataConsistent;
    }

    private void showEventualConsistencyMessage(String recentlyJoinedCourseId) {
        String errorMessage = String.format(Const.StatusMessages.EVENTUAL_CONSISTENCY_MESSAGE_STUDENT, recentlyJoinedCourseId);
        statusToUser.add(errorMessage);
    }
    
    private void addPlaceholderCourse(String courseId, String googleId) {
        try {
            CourseDetailsBundle course = logic.getCourseDetails(courseId);
            data.courses.add(course);

            addPlaceholderEvaluations(course);
            addPlaceholderFeedbackSessions(course);        
            
        } catch (EntityDoesNotExistException e){
            showEventualConsistencyMessage(courseId);
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " :" + e.getMessage();
        } 
    }
    
    private void addPlaceholderEvaluations(CourseDetailsBundle course) {
        for(EvaluationDetailsBundle edb: course.evaluations){
            EvaluationAttributes eval = edb.evaluation;
            switch (eval.getStatus()) {
                case PUBLISHED:
                    data.evalSubmissionStatusMap.put(eval.courseId+"%"+eval.name, Const.STUDENT_EVALUATION_STATUS_PUBLISHED);
                    break;
                case CLOSED:
                    data.evalSubmissionStatusMap.put(eval.courseId+"%"+eval.name, Const.STUDENT_EVALUATION_STATUS_CLOSED);
                    break;
                default:
                    data.evalSubmissionStatusMap.put(eval.courseId+"%"+eval.name, Const.STUDENT_EVALUATION_STATUS_PENDING);
                    break;
            }
        }
    }
    
    private void addPlaceholderFeedbackSessions(CourseDetailsBundle course) {
        for(FeedbackSessionDetailsBundle fsb: course.feedbackSessions){
            FeedbackSessionAttributes f = fsb.feedbackSession;
            data.sessionSubmissionStatusMap.put(f.courseId+"%"+f.feedbackSessionName, true);
        }
    }
}
