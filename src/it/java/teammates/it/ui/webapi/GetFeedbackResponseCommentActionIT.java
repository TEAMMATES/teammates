package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.StringHelper;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.GetFeedbackResponseCommentAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetFeedbackResponseCommentAction}.
 */
public class GetFeedbackResponseCommentActionIT extends BaseActionIT<GetFeedbackResponseCommentAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() {
        ______TS("typical successful case as student_submission");
        FeedbackResponse fr = typicalBundle.feedbackResponses.get("response1ForQ1");
        FeedbackResponseComment expectedComment = typicalBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(fr.getId().toString()),
        };

        GetFeedbackResponseCommentAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        FeedbackResponseCommentData output = (FeedbackResponseCommentData) result.getOutput();

        assertTrue(expectedComment.getId() == output.getFeedbackResponseCommentId());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        ______TS("typical success case as student_submission");
        Student student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.getGoogleId());

        FeedbackResponse fr = typicalBundle.feedbackResponses.get("response1ForQ1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(fr.getId().toString()),
        };

        verifyCanAccess(submissionParams);
    }

}
