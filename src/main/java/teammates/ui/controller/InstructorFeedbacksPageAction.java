package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbacksPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        //This can be null. Non-null value indicates the page is being loaded 
        //   to add a feedback to the specified course
        String courseIdForNewSession = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        new GateKeeper().verifyInstructorPrivileges(account);
                
        if (courseIdForNewSession!=null) {
            new GateKeeper().verifyAccessible(
                    logic.getInstructorForGoogleId(courseIdForNewSession, account.googleId), 
                    logic.getCourse(courseIdForNewSession), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        }

        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(account);
        data.courseIdForNewSession = courseIdForNewSession;
        // This indicates that an empty form to be shown (except possibly the course value filled in)
        data.newFeedbackSession = null; 

        data.instructors = new HashMap<String, InstructorAttributes>();
        data.courses = loadCoursesList(account.googleId, data.instructors);
        if (data.courses.size() == 0) {
            statusToUser.add(Const.StatusMessages.COURSE_EMPTY_IN_EVALUATION.replace("${user}", "?user="+account.googleId));
            data.existingEvalSessions = new ArrayList<EvaluationAttributes>();
            data.existingFeedbackSessions = new ArrayList<FeedbackSessionAttributes>();
        } else {
            data.existingEvalSessions = loadEvaluationsList(account.googleId);            
            data.existingFeedbackSessions = loadFeedbackSessionsList(account.googleId);
            if (data.existingFeedbackSessions.isEmpty() &&
                data.existingEvalSessions.isEmpty()) {
                statusToUser.add(Const.StatusMessages.EVALUATION_EMPTY);
            }
        }            
        
        EvaluationAttributes.sortEvaluationsByDeadlineDescending(data.existingEvalSessions);
        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(data.existingFeedbackSessions);
        
        statusToAdmin = "Number of feedback sessions: "+data.existingFeedbackSessions.size();
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACKS, data);
    }
    
    // TODO: make use of the courseDetailsBundle. do not make new DB calls!!!
    protected List<FeedbackSessionAttributes> loadFeedbackSessionsList(
            String googleId) throws EntityDoesNotExistException {
        List<FeedbackSessionAttributes> sessions =
                logic.getFeedbackSessionsListForInstructor(googleId);
        
        return sessions;
    }

    // TODO: same here as above!!!
    // these two methods are used in add action. maybe we should reconsider this implementation--why add action has to inherit from this?
    // it is highly possible that this is because of historical reasons--for evaluations previously the structure is add inherit pageAction 
    protected List<EvaluationAttributes> loadEvaluationsList(String userId)
            throws EntityDoesNotExistException {
        List<EvaluationAttributes> evaluations =
                logic.getEvaluationsListForInstructor(userId);

        return evaluations;
    }
    
    protected List<CourseAttributes> loadCoursesList(String userId, HashMap<String, InstructorAttributes> instructors)
            throws EntityDoesNotExistException {
        
        List<CourseAttributes> courses = logic.getCoursesForInstructor(userId); 
        for (CourseAttributes course : courses) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(course.id, account.googleId);
            instructors.put(course.id, instructor);
        }
        Collections.sort(courses, new Comparator<CourseAttributes>() {
            @Override
            public int compare(CourseAttributes c1, CourseAttributes c2){
                return c1.id.compareTo(c2.id);
            }
        });
        
        return courses;
    }

}
