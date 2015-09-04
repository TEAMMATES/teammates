package teammates.test.automated;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;

import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.Emails.EmailType;
import teammates.test.cases.BaseTaskQueueCallback;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

@SuppressWarnings("serial")
public class FeedbackSessionClosingCallback extends BaseTaskQueueCallback {

    @Override
    public int execute(URLFetchRequest request) {
        HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);

        assertTrue(paramMap.containsKey(ParamsNames.EMAIL_TYPE));
        EmailType typeOfMail = EmailType.valueOf((String) paramMap.get(ParamsNames.EMAIL_TYPE));
        assertEquals(EmailType.FEEDBACK_CLOSING, typeOfMail);

        assertTrue(paramMap.containsKey(ParamsNames.EMAIL_FEEDBACK));
        assertNotNull(paramMap.get(ParamsNames.EMAIL_FEEDBACK));

        assertTrue(paramMap.containsKey(ParamsNames.EMAIL_COURSE));
        assertNotNull(paramMap.get(ParamsNames.EMAIL_COURSE));

        FeedbackSessionClosingCallback.taskCount++;
        return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
    }

}
