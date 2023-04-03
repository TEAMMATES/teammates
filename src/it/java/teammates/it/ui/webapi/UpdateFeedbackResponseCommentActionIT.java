package teammates.it.ui.webapi;

import java.util.Arrays;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;
import teammates.ui.request.Intent;
import teammates.ui.webapi.UpdateFeedbackResponseCommentAction;

/**
 * SUT: {@link UpdateFeedbackResponseCommentAction}.
 */
public class UpdateFeedbackResponseCommentActionIT extends BaseActionIT<UpdateFeedbackResponseCommentAction> {

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
        return PUT;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackResponseComment frc = typicalBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        ______TS("Typical successful case for INSTRUCTOR_RESULT");
        loginAsInstructor(instructor.getGoogleId());

        String [] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, frc.getId().toString(),
        };
        String newCommentText = frc.getCommentText() + " (Edited)";
        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                newCommentText, Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS));

        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseComment actualFrc = logic.getFeedbackResponseComment(frc.getId());
        assertEquals(newCommentText, actualFrc.getCommentText());
        assertEquals(instructor.getEmail(), actualFrc.getLastEditorEmail());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackResponseComment frc = typicalBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        ______TS("successful case for instructor result");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, frc.getId().toString(),
        };

        loginAsInstructor(instructor.getGoogleId());
        verifyCanAccess(submissionParams);
    }

}
