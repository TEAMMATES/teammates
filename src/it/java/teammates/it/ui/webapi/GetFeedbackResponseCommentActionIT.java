package teammates.it.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.Student;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.webapi.GetFeedbackResponseCommentAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetFeedbackResponseCommentAction}.
 */
public class GetFeedbackResponseCommentActionIT extends BaseActionIT<GetFeedbackResponseCommentAction> {
    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
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
        ______TS("typical success case");
        FeedbackResponse fr = typicalBundle.feedbackResponses.get("response1ForQ1");
        FeedbackResponseComment expectedComment = typicalBundle.feedbackResponseComments.get("comment2ToResponse1ForQ1");
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, fr.getId().toString(),
        };

        GetFeedbackResponseCommentAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        FeedbackResponseCommentData output = (FeedbackResponseCommentData) result.getOutput();

        assertEquals(expectedComment.getId(), output.getFeedbackResponseCommentId());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        ______TS("student cannot access");
        Student student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.getGoogleId());

        FeedbackResponse fr = typicalBundle.feedbackResponses.get("response1ForQ1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, fr.getId().toString(),
        };

        verifyCannotAccess(submissionParams);
    }

}
