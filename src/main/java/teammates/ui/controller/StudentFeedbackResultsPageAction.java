package teammates.ui.controller;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentFeedbackResultsPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        
        if(courseId==null || feedbackSessionName == null) {
            return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
        }
        
        if(!isJoinedCourse(courseId)){
            return createPleaseJoinCourseResponse(courseId);
        }
        
        new GateKeeper().verifyAccessible(
                getCurrentStudent(courseId), 
                logic.getFeedbackSession(feedbackSessionName, courseId));
        
        StudentFeedbackResultsPageData data = new StudentFeedbackResultsPageData(account);
        
        data.student = getCurrentStudent(courseId);
        data.bundle = logic.getFeedbackSessionResultsForStudent(feedbackSessionName, courseId, data.student.email);
        if(data.bundle == null) {
            throw new EntityDoesNotExistException("Feedback session "+feedbackSessionName+" does not exist in "+courseId+".");
        }
        
        if (data.bundle.feedbackSession.isPublished() == false) {
            throw new UnauthorizedAccessException(
                    "This feedback session is not yet visible.");
        }
        
        if (data.bundle.isStudentHasSomethingNewToSee(data.student)) {
            statusToUser.add(Const.StatusMessages.FEEDBACK_RESULTS_SOMETHINGNEW);
        } else {
            statusToUser.add(Const.StatusMessages.FEEDBACK_RESULTS_NOTHINGNEW);
        }
        
        statusToAdmin = "Show student feedback result page<br>" +
                "Session Name: " + feedbackSessionName + "<br>" + 
                "Course ID: " + courseId;
        return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_RESULTS, data);
    }
    
    // The following methods are overridden by the unregistered version of this action

    protected boolean isJoinedCourse(String courseId) {
        return isJoinedCourse(courseId, account.googleId);
    }

    protected StudentAttributes getCurrentStudent(String courseId) {
        if (student != null) {
            return student;
        } else {
            return logic.getStudentForGoogleId(courseId, account.googleId);
        }
    }
}
