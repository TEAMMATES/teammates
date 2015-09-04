package teammates.test.automated;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;

import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

@SuppressWarnings("serial")
public class FeedbackSubmissionsAdjustmentCallback extends BaseTaskQueueCallback {
    
    @Override
    public int execute(URLFetchRequest request) {
        HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
        
        assertTrue(paramMap.containsKey(ParamsNames.COURSE_ID));
        assertNotNull(paramMap.get(ParamsNames.COURSE_ID));
        
        assertTrue(paramMap.containsKey(ParamsNames.ENROLLMENT_DETAILS));
        assertNotNull(paramMap.get(ParamsNames.ENROLLMENT_DETAILS));
        
        assertTrue(paramMap.containsKey(ParamsNames.FEEDBACK_SESSION_NAME));
        assertNotNull(paramMap.get(ParamsNames.FEEDBACK_SESSION_NAME));
        
        FeedbackSubmissionsAdjustmentCallback.taskCount++;
        return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
    }

}
