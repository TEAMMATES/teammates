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
        String feedbackSessionNameForSessionList = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String isUsingAjax = getRequestParamValue(Const.ParamsNames.IS_USING_AJAX);
        
        new GateKeeper().verifyInstructorPrivileges(account);
                
        if (courseIdForNewSession!=null) {
            new GateKeeper().verifyAccessible(
                    logic.getInstructorForGoogleId(courseIdForNewSession, account.googleId), 
                    logic.getCourse(courseIdForNewSession), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        }

        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(account);
        data.isUsingAjax = (isUsingAjax == null) ? false : true;
        data.courseIdForNewSession = courseIdForNewSession;
        data.feedbackSessionNameForSessionList = feedbackSessionNameForSessionList;
        // This indicates that an empty form to be shown (except possibly the course value filled in)
        data.newFeedbackSession = null;
        boolean omitArchived = true; // TODO: implement as a request parameter
        // HashMap with courseId as key and InstructorAttributes as value
        data.instructors = loadCourseInstructorMap(omitArchived);
        
        List<InstructorAttributes> instructorList = new ArrayList<InstructorAttributes>(data.instructors.values());
        data.courses = loadCoursesList(instructorList);
        
        if (data.courses.size() == 0) {
            statusToUser.add(Const.StatusMessages.COURSE_EMPTY_IN_EVALUATION.replace("${user}", "?user="+account.googleId));
        }
        
        if(data.courses.size() == 0 ||!data.isUsingAjax) {
            data.existingEvalSessions = new ArrayList<EvaluationAttributes>();
            data.existingFeedbackSessions = new ArrayList<FeedbackSessionAttributes>();
        } else {
            data.existingEvalSessions = loadEvaluationsList(instructorList);
            data.existingFeedbackSessions = loadFeedbackSessionsList(instructorList);
            if (data.existingFeedbackSessions.isEmpty() &&
                data.existingEvalSessions.isEmpty()) {
                statusToUser.add(Const.StatusMessages.EVALUATION_EMPTY);
            }
        }            
        
        EvaluationAttributes.sortEvaluationsByDeadlineDescending(data.existingEvalSessions);
        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(data.existingFeedbackSessions);
        
        statusToAdmin = "Number of feedback sessions: "+data.existingFeedbackSessions.size();
        
        data.getCourseIdOptions();
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACKS, data);
    }
    
    protected List<FeedbackSessionAttributes> loadFeedbackSessionsList(List<InstructorAttributes> instructorList)
            throws EntityDoesNotExistException {
        
        List<FeedbackSessionAttributes> sessions =  logic.getFeedbackSessionsListForInstructor(instructorList);
        return sessions;
    }

    protected List<EvaluationAttributes> loadEvaluationsList(List<InstructorAttributes> instructorList)
            throws EntityDoesNotExistException {
        List<EvaluationAttributes> evaluations =  logic.getEvaluationsListForInstructor(instructorList);
        
        return evaluations;
    }
    
    protected List<CourseAttributes> loadCoursesList(List<InstructorAttributes> instructorList)
            throws EntityDoesNotExistException {
        
        List<CourseAttributes> courses = logic.getCoursesForInstructor(instructorList);
        
        Collections.sort(courses, new Comparator<CourseAttributes>() {
            @Override
            public int compare(CourseAttributes c1, CourseAttributes c2){
                return c1.id.compareTo(c2.id);
            }
        });
        
        return courses;
    }
    
    /**
     * Gets a Map with courseId as key, and InstructorAttributes as value.
     * @return
     */
    protected HashMap<String, InstructorAttributes> loadCourseInstructorMap(boolean omitArchived) {
        HashMap<String, InstructorAttributes> courseInstructorMap = new HashMap<String, InstructorAttributes>();
        List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(account.googleId, omitArchived);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }
        return courseInstructorMap;
    }
}
