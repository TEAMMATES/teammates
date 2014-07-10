package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Const.StatusMessages;
import teammates.logic.api.GateKeeper;

public class InstructorHomePageAction extends Action {
    
    private InstructorHomePageData data;
    

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyInstructorPrivileges(account);
        
        data = new InstructorHomePageData(account);
        data.sortCriteria = getRequestParamValue(Const.ParamsNames.COURSE_SORTING_CRITERIA);
        if (data.sortCriteria == null) {
            data.sortCriteria = Const.DEFAULT_SORT_CRITERIA;
        }
        
        HashMap<String, CourseSummaryBundle> courses = logic.getCourseSummariesWithoutStatsForInstructor(account.googleId);
        
        
        ArrayList<CourseSummaryBundle> courseList = new ArrayList<CourseSummaryBundle>(courses.values());
        data.courses = updateWithInstructorArchiveStatus(courseList);
        
        switch (data.sortCriteria) {
            case Const.SORT_BY_COURSE_ID:
                CourseSummaryBundle.sortSummarizedCoursesByCourseId(data.courses);
                break;
            case Const.SORT_BY_COURSE_NAME:
                CourseSummaryBundle.sortSummarizedCoursesByCourseName(data.courses);
                break;
            case Const.SORT_BY_COURSE_CREATION_DATE:
                CourseSummaryBundle.sortSummarizedCoursesByCreationDate(data.courses);
                break;
            default:
                throw new RuntimeException("Invalid course sorting criteria.");
        }
        
        data.instructors = new HashMap<String, InstructorAttributes>();
        data.numberOfPendingComments = new HashMap<String, Integer>();
        
        for(CourseSummaryBundle course: data.courses) {
            String courseId = course.course.id;
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
            data.instructors.put(courseId, instructor);
            int numberOfPendingCommentsForThisCourse = logic.getCommentsForSendingState(courseId, CommentSendingState.PENDING).size() 
                    + logic.getFeedbackResponseCommentsForSendingState(courseId, CommentSendingState.PENDING).size();
            data.numberOfPendingComments.put(courseId, numberOfPendingCommentsForThisCourse);
            
            EvaluationAttributes.sortEvaluationsByDeadlineDescending(course.evaluations);
            FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(course.feedbackSessions);
        }
        
        if (logic.isNewInstructor(account.googleId)) {
            statusToUser.add(StatusMessages.HINT_FOR_NEW_INSTRUCTOR);
        }
        statusToAdmin = "instructorHome Page Load<br>" + "Total Courses: " + data.courses.size();
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_HOME, data);
        return response;

    }
    
    private ArrayList<CourseSummaryBundle> updateWithInstructorArchiveStatus(ArrayList<CourseSummaryBundle> courseList){
        
        for(CourseSummaryBundle course : courseList){
            
            InstructorAttributes curInstructor = logic.getInstructorForGoogleId(course.course.id, account.googleId);
            
            if(curInstructor.isArchived != null){          
                course.course.isArchived = curInstructor.isArchived;       
            }
        }
        
        return courseList;
        
    }

}
