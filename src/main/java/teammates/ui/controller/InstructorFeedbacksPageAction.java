package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;

public class InstructorFeedbacksPageAction extends Action {

    protected HashMap<String, CourseDetailsBundle> courseDetailsList = null;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        //This can be null. Non-null value indicates the page is being loaded 
        //   to add a feedback to the specified course
        String courseIdForNewSession = getRequestParamValue(Const.ParamsNames.COURSE_ID);
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
        // This indicates that an empty form to be shown (except possibly the course value filled in)
        data.newFeedbackSession = null;
        // HashMap with courseId as key and InstructorAttributes as value
        data.instructors = loadCourseInstructorMap();
        
        // Get courseDetailsBundles
        boolean omitArchived = true; // TODO: implement as a request parameter
        courseDetailsList = logic.getCourseDetailsListForInstructor(account.googleId, omitArchived);
        if (omitArchived) {
            // omitArchivedCourses(data.instructors);
        }
        
        data.courses = loadCoursesList();
        if (data.courses.size() == 0) {
            statusToUser.add(Const.StatusMessages.COURSE_EMPTY_IN_EVALUATION.replace("${user}", "?user="+account.googleId));
        }
        
        if(data.courses.size() == 0 ||!data.isUsingAjax) {
            data.existingEvalSessions = new ArrayList<EvaluationAttributes>();
            data.existingFeedbackSessions = new ArrayList<FeedbackSessionAttributes>();
        } else {
            data.existingEvalSessions = loadEvaluationsList();
            data.existingFeedbackSessions = loadFeedbackSessionsList();
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
    
    protected List<FeedbackSessionAttributes> loadFeedbackSessionsList()
            throws EntityDoesNotExistException {
        List<FeedbackSessionAttributes> feedbackSessions = new ArrayList<FeedbackSessionAttributes>();
        
        // Get feedbackSessions from courseDetailsBundle
        for (CourseDetailsBundle courseDetails : courseDetailsList.values()) {
            feedbackSessions.addAll(courseDetails.getFeedbackSessionsList());
        }
        
        return feedbackSessions;
    }

    protected List<EvaluationAttributes> loadEvaluationsList()
            throws EntityDoesNotExistException {
        List<EvaluationAttributes> evaluations = new ArrayList<EvaluationAttributes>();
        
        // Get evaluations from courseDetailsBundle
        for (CourseDetailsBundle courseDetails : courseDetailsList.values()) {
            evaluations.addAll(courseDetails.getEvaluationsList());
        }

        return evaluations;
    }
    
    protected List<CourseAttributes> loadCoursesList()
            throws EntityDoesNotExistException {
        List<CourseAttributes> courses = new ArrayList<CourseAttributes>();
        
        for (CourseDetailsBundle courseDetails : courseDetailsList.values()) {
            courses.add(courseDetails.course);
        }
        
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
    protected HashMap<String, InstructorAttributes> loadCourseInstructorMap() {
        HashMap<String, InstructorAttributes> courseInstructorMap = new HashMap<String, InstructorAttributes>();
        List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(account.googleId);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }
        return courseInstructorMap;
    }
    
    /**
     * Removes archived courses from courseDetailsList
     * @param instructors 
     */
    protected void omitArchivedCourses(HashMap<String, InstructorAttributes> instructors) {
        HashMap<String, CourseDetailsBundle> newCourseDetailsList = new HashMap<String, CourseDetailsBundle>();
        for (CourseDetailsBundle courseDetails : courseDetailsList.values()) {
            CourseAttributes course = courseDetails.course;
            String courseId = course.id;
            InstructorAttributes instructor = instructors.get(courseId);
            
            if (!Logic.isCourseArchived(course, instructor)) {
                newCourseDetailsList.put(courseId, courseDetails);
            }
        }
        courseDetailsList = newCourseDetailsList;
    }
    
}
