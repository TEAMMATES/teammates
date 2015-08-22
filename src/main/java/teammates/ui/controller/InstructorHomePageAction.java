package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Const.StatusMessages;
import teammates.logic.api.GateKeeper;

public class InstructorHomePageAction extends Action {
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        if (!account.isInstructor && isPersistenceIssue()) {
            ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_HOME,
                                                           new InstructorHomePageData(account));
            statusToUser.add(Const.StatusMessages.INSTRUCTOR_PERSISTENCE_ISSUE);
            return response;
        }
        
        new GateKeeper().verifyInstructorPrivileges(account);
        
        String courseToLoad = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        return courseToLoad == null ? loadPage() : loadCourse(courseToLoad);
    }

    private ActionResult loadCourse(String courseToLoad) throws EntityDoesNotExistException {
        String index = getRequestParamValue("index");
        CourseSummaryBundle course = logic.getCourseSummaryWithFeedbackSessions(courseToLoad);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseToLoad, account.googleId);
                
        int commentsForSendingStateCount =
                logic.getCommentsForSendingState(courseToLoad, CommentSendingState.PENDING).size();
        int feedbackResponseCommentsForSendingStateCount =
                logic.getFeedbackResponseCommentsForSendingState(courseToLoad, CommentSendingState.PENDING)
                     .size();
        int pendingCommentsCount = commentsForSendingStateCount + feedbackResponseCommentsForSendingStateCount;
        
        InstructorHomeCourseAjaxPageData data = new InstructorHomeCourseAjaxPageData(account);
        data.init(Integer.parseInt(index), course, instructor, pendingCommentsCount);
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_HOME_AJAX_COURSE_TABLE, data);
    }

    private ActionResult loadPage() throws EntityDoesNotExistException {
        boolean omitArchived = true;
        HashMap<String, CourseSummaryBundle> courses = logic.getCourseSummariesWithoutStatsForInstructor(
                                                                 account.googleId, omitArchived);
        
        ArrayList<CourseSummaryBundle> courseList = new ArrayList<CourseSummaryBundle>(courses.values());
        
        String sortCriteria = getSortCriteria(courseList,
                                              getRequestParamValue(Const.ParamsNames.COURSE_SORTING_CRITERIA));
        
        HashMap<String, Integer> numberOfPendingComments = new HashMap<String, Integer>();
        HashMap<String, InstructorAttributes> instructors = new HashMap<String, InstructorAttributes>();
        for (CourseSummaryBundle course : courseList) {
            String courseId = course.course.id;
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
            instructors.put(courseId, instructor);
            
            int numberOfCommentsForSendingState =
                    logic.getCommentsForSendingState(courseId, CommentSendingState.PENDING).size();
            
            int numberOfFeedbackResponseCommentsForSendingState =
                    logic.getFeedbackResponseCommentsForSendingState(courseId, CommentSendingState.PENDING)
                         .size();
            
            int numberOfPendingCommentsForThisCourse = numberOfCommentsForSendingState
                                                       + numberOfFeedbackResponseCommentsForSendingState;
            
            numberOfPendingComments.put(courseId, numberOfPendingCommentsForThisCourse);
            
            FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(course.feedbackSessions);
        }
        
        InstructorHomePageData data = new InstructorHomePageData(account);
        data.init(courseList, sortCriteria, instructors, numberOfPendingComments);
        
        if (logic.isNewInstructor(account.googleId)) {
            statusToUser.add(StatusMessages.HINT_FOR_NEW_INSTRUCTOR);
        }
        statusToAdmin = "instructorHome Page Load<br>" + "Total Courses: " + courseList.size();
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_HOME, data);
        return response;
    }

    private String getSortCriteria(ArrayList<CourseSummaryBundle> courseList, String sortCriteria) {
        if (sortCriteria == null) {
            sortCriteria = Const.DEFAULT_SORT_CRITERIA;
        }
        switch (sortCriteria) {
            case Const.SORT_BY_COURSE_ID:
                CourseSummaryBundle.sortSummarizedCoursesByCourseId(courseList);
                break;
            case Const.SORT_BY_COURSE_NAME:
                CourseSummaryBundle.sortSummarizedCoursesByCourseName(courseList);
                break;
            case Const.SORT_BY_COURSE_CREATION_DATE:
                CourseSummaryBundle.sortSummarizedCoursesByCreationDate(courseList);
                break;
            default:
                throw new RuntimeException("Invalid course sorting criteria.");
        }
        return sortCriteria;
    }    
}
