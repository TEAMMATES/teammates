package teammates.test.automated;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;

import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.test.cases.BaseTaskQueueCallback;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

@SuppressWarnings("serial")
public class FeedbackSessionsEmailTaskQueueCallback extends BaseTaskQueueCallback {

    @Override
    public int execute(URLFetchRequest request) {
        HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);

        assertTrue(paramMap.containsKey(ParamsNames.SUBMISSION_FEEDBACK));
        assertNotNull(paramMap.get(ParamsNames.SUBMISSION_FEEDBACK));

        assertTrue(paramMap.containsKey(ParamsNames.SUBMISSION_COURSE));
        assertNotNull(paramMap.get(ParamsNames.SUBMISSION_COURSE));

        FeedbackSessionsEmailTaskQueueCallback.taskCount++;
        return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
    }

}
