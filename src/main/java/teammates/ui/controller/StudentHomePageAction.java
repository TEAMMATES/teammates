package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseDetailsBundle;
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
        
        List<CourseDetailsBundle> courses = new ArrayList<CourseDetailsBundle>();
        Map<String, Boolean> sessionSubmissionStatusMap = new HashMap<String, Boolean>();
        
        try {
            courses = logic.getCourseDetailsListForStudent(account.googleId);
            sessionSubmissionStatusMap = generateFeedbackSessionSubmissionStatusMap(courses, account.googleId);
            
            CourseDetailsBundle.sortDetailedCourses(courses);
            
            statusToAdmin = "studentHome Page Load<br>" + "Total courses: " + courses.size();
            
            boolean isDataConsistent = isCourseIncluded(recentlyJoinedCourseId, courses);
            if (!isDataConsistent) {
                addPlaceholderCourse(courses, recentlyJoinedCourseId, account.googleId, sessionSubmissionStatusMap);
            }
            
            for (CourseDetailsBundle course : courses) {
                FeedbackSessionDetailsBundle.sortFeedbackSessionsByCreationTime(course.feedbackSessions);
            }
        
        } catch (EntityDoesNotExistException e) {
            if (recentlyJoinedCourseId != null) {
                addPlaceholderCourse(courses, recentlyJoinedCourseId, account.googleId, sessionSubmissionStatusMap);
            } else {
                statusToUser.add(Const.StatusMessages.STUDENT_FIRST_TIME);
                statusToAdmin = Const.ACTION_RESULT_FAILURE + " :" + e.getMessage();
            }
        }
        
        data.init(courses, sessionSubmissionStatusMap);
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.STUDENT_HOME, data);
        
        return response;
    }
    
    private Map<String, Boolean> generateFeedbackSessionSubmissionStatusMap(
            List<CourseDetailsBundle> courses, String googleId) {
        Map<String, Boolean> returnValue = new HashMap<String, Boolean>();
        
        for(CourseDetailsBundle c : courses) {
            for(FeedbackSessionDetailsBundle fsb : c.feedbackSessions) {
                FeedbackSessionAttributes f = fsb.feedbackSession;
                returnValue.put(f.courseId + "%" + f.feedbackSessionName, getStudentStatusForSession(f, googleId));
            }
        }
        return returnValue;
    }

    private boolean getStudentStatusForSession(FeedbackSessionAttributes fs, String googleId){
        StudentAttributes student = logic.getStudentForGoogleId(fs.courseId, googleId);
        Assumption.assertNotNull(student);

        String studentEmail = student.email;
        
        try {
            return logic.hasStudentSubmittedFeedback(
                    fs.courseId, fs.feedbackSessionName, studentEmail);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            Assumption.fail("Parameters are expected to be valid at this point :"
                             + TeammatesException.toStringWithStackTrace(e));
            return false;
        }
    }
    
    private boolean isCourseIncluded(String recentlyJoinedCourseId, List<CourseDetailsBundle> courses) {
        boolean isCourseIncluded = false;
        
        if (recentlyJoinedCourseId == null) {
            isCourseIncluded = true;
        } else {
            for (CourseDetailsBundle currentCourse : courses) {
                if (currentCourse.course.id.equals(recentlyJoinedCourseId)) {
                    isCourseIncluded = true;
                }
            }
        }
        
        return isCourseIncluded;
    }

    private void showEventualConsistencyMessage(String recentlyJoinedCourseId) {
        String errorMessage = String.format(Const.StatusMessages.EVENTUAL_CONSISTENCY_MESSAGE_STUDENT,
                                            recentlyJoinedCourseId);
        statusToUser.add(errorMessage);
    }
    
    private void addPlaceholderCourse(List<CourseDetailsBundle> courses, String courseId,
            String googleId, Map<String, Boolean> sessionSubmissionStatusMap) {
        try {
            CourseDetailsBundle course = logic.getCourseDetails(courseId);
            courses.add(course);

            addPlaceholderFeedbackSessions(course, sessionSubmissionStatusMap);
            FeedbackSessionDetailsBundle.sortFeedbackSessionsByCreationTime(course.feedbackSessions);
            
        } catch (EntityDoesNotExistException e){
            showEventualConsistencyMessage(courseId);
            statusToAdmin = Const.ACTION_RESULT_FAILURE + " :" + e.getMessage();
        } 
    } 
    
    private void addPlaceholderFeedbackSessions(CourseDetailsBundle course,
                                                Map<String, Boolean> sessionSubmissionStatusMap) {
        for (FeedbackSessionDetailsBundle fsb: course.feedbackSessions){
            FeedbackSessionAttributes f = fsb.feedbackSession;
            sessionSubmissionStatusMap.put(f.courseId+"%" + f.feedbackSessionName, true);
        }
    }
}
