package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

public class InstructorEvalsPageAction extends Action {
    Logger log = Utils.getLogger();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        new GateKeeper().verifyInstructorPrivileges(account);
        
        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE);
        /*
        //This can be null. Non-null value indicates the page is being loaded 
        //   to add an evaluation to the specified course
        String courseIdForNewEvaluation = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        if (courseIdForNewEvaluation!=null) {
            new GateKeeper().verifyAccessible(
                    logic.getInstructorForGoogleId(courseIdForNewEvaluation, account.googleId), 
                    logic.getCourse(courseIdForNewEvaluation), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        }

        InstructorEvalPageData data = new InstructorEvalPageData(account);
        data.courseIdForNewEvaluation = courseIdForNewEvaluation;
        // This indicates that an empty form to be shown (except possibly the course value filled in)
        data.newEvaluationToBeCreated = null;
        
        data.instructors = new HashMap<String, InstructorAttributes>();
        data.courses = loadCoursesListAndInstructors(account.googleId, data.instructors);
        if (data.courses.size() == 0) {
            statusToUser.add(Const.StatusMessages.COURSE_EMPTY_IN_EVALUATION.replace("${user}", "?user="+account.googleId));
            data.existingEvalSessions = new ArrayList<EvaluationAttributes>();
            data.existingFeedbackSessions = new ArrayList<FeedbackSessionAttributes>();
        
        } else {
            data.existingEvalSessions = loadEvaluationsList(account.googleId);            
            data.existingFeedbackSessions = loadFeedbackSessionsList(account.googleId);
            if (data.existingFeedbackSessions.isEmpty() &&
                    data.existingEvalSessions.isEmpty()) {
                statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_EMPTY);
            }
        }    
        EvaluationAttributes.sortEvaluationsByDeadlineDescending(data.existingEvalSessions);
        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(data.existingFeedbackSessions);
        statusToAdmin = "Number of evaluations :"+data.existingEvalSessions.size();
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_EVALS, data);
        */
    }

    protected List<FeedbackSessionAttributes> loadFeedbackSessionsList(
            String googleId) throws EntityDoesNotExistException {
        List<FeedbackSessionAttributes> sessions =
                logic.getFeedbackSessionsListForInstructor(googleId);
        
        return sessions;
    }
    
    protected List<EvaluationAttributes> loadEvaluationsList(String userId)
            throws EntityDoesNotExistException {
        List<EvaluationAttributes> evaluations =
                logic.getEvaluationsListForInstructor(userId);
        EvaluationAttributes.sortEvaluationsByDeadline(evaluations);

        return evaluations;
    }
    
    protected List<CourseAttributes> loadCoursesListAndInstructors(String userId,
            HashMap<String, InstructorAttributes> instructors)
            throws EntityDoesNotExistException {
       
        List<CourseAttributes> allCourses = logic.getCoursesForInstructor(userId);
        List<CourseAttributes> allowedCourses = new ArrayList<CourseAttributes>();
        for (CourseAttributes course : allCourses) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(course.id, account.googleId);
            instructors.put(course.id, instructor);
            if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)) {
                allowedCourses.add(course);
            }
        }
        Collections.sort(allowedCourses, new Comparator<CourseAttributes>() {
            @Override
            public int compare(CourseAttributes c1, CourseAttributes c2){
                return c1.id.compareTo(c2.id);
            }
        });      
        return allowedCourses;
    }

}