package teammates.logic.automated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.JsonUtils;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.StudentsLogic;

import com.google.gson.reflect.TypeToken;

public class FeedbackSubmissionAdjustmentAction extends TaskQueueWorkerAction {
    private String courseId;
    private String sessionName;
    private String enrollmentDetails;
    
    public FeedbackSubmissionAdjustmentAction(
            HttpServletRequest request) {
        super(request);
        
        this.courseId = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        this.sessionName = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(sessionName);
        
        this.enrollmentDetails = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.ENROLLMENT_DETAILS);
        Assumption.assertNotNull(enrollmentDetails);
    }

    public FeedbackSubmissionAdjustmentAction(HashMap<String, String> paramMap) {
        super(null);
        
        this.courseId = paramMap.get(ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        this.sessionName = paramMap.get(ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(sessionName);
        
        this.enrollmentDetails = paramMap.get(ParamsNames.ENROLLMENT_DETAILS);
        Assumption.assertNotNull(enrollmentDetails);
    }
    
    @Override
    public boolean execute() {
        

        log.info("Adjusting submissions for feedback session :" + sessionName
                 + "in course : " + courseId);
        
        FeedbackSessionAttributes feedbackSession = FeedbackSessionsLogic.inst()
                .getFeedbackSession(sessionName, courseId);
        
        String errorString =
                "Error encountered while adjusting feedback session responses of %s in course : %s : %s\n%s";
        
        if (feedbackSession == null) {
            log.severe(String.format(errorString, sessionName, courseId, "feedback session is null", ""));
            return false;
        }
        
        List<FeedbackResponseAttributes> allResponses =
                                        FeedbackResponsesLogic.inst().getFeedbackResponsesForSession(
                                                                        feedbackSession.getFeedbackSessionName(),
                                                                        feedbackSession.getCourseId());
        ArrayList<StudentEnrollDetails> enrollmentList =
                JsonUtils.fromJson(enrollmentDetails, new TypeToken<ArrayList<StudentEnrollDetails>>(){}.getType());
        for (FeedbackResponseAttributes response : allResponses) {
            try {
                StudentsLogic.inst().adjustFeedbackResponseForEnrollments(enrollmentList, response);
            } catch (Exception e) {
                log.severe(String.format(errorString, sessionName, courseId, e.getMessage(),
                                                ActivityLogEntry.generateServletActionFailureLogMessage(request, e)));
                return false;
            }
        }
        return true;
           
    }

}
