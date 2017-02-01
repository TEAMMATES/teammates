package teammates.ui.automated;

import java.util.List;

import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.UserType;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.api.GateKeeper;
import teammates.common.util.JsonUtils;

import com.google.gson.reflect.TypeToken;

/**
 * Task queue worker action: adjusts feedback responses in the database due to
 * change in student enrollment details of a course.
 */
public class FeedbackResponseAdjustmentWorkerAction extends AutomatedAction {
    
    @Override
    protected String getActionDescription() {
        return null;
    }
    
    @Override
    protected String getActionMessage() {
        return null;
    }
    
    @Override
    public void execute() {
        String courseId = getRequestParamValue(ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String sessionName = getRequestParamValue(ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(sessionName);
        
        String enrollmentDetails = getRequestParamValue(ParamsNames.ENROLLMENT_DETAILS);
        Assumption.assertNotNull(enrollmentDetails);
        
        log.info("Adjusting submissions for feedback session :" + sessionName + "in course : " + courseId);
        
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(sessionName, courseId);
        
        String errorString = "Error encountered while adjusting feedback session responses of %s in course %s: %s\n%s";
        
        if (feedbackSession == null) {
            log.severe(String.format(errorString, sessionName, courseId, "feedback session is null", ""));
            setForRetry();
            return;
        }
        
        List<FeedbackResponseAttributes> allResponses =
                logic.getFeedbackResponsesForSession(feedbackSession.getFeedbackSessionName(),
                                                     feedbackSession.getCourseId());
        List<StudentEnrollDetails> enrollmentList =
                JsonUtils.fromJson(enrollmentDetails, new TypeToken<List<StudentEnrollDetails>>(){}.getType());
        for (FeedbackResponseAttributes response : allResponses) {
            try {
                logic.adjustFeedbackResponseForEnrollments(enrollmentList, response);
            } catch (Exception e) {
                UserType userType = new GateKeeper().getCurrentUser();
                log.severe(String.format(errorString, sessionName, courseId, e.getMessage(),
                                         ActivityLogEntry.generateServletActionFailureLogMessage(request, e, userType)));
                setForRetry();
                return;
            }
        }
    }
    
}
