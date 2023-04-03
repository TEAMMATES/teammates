package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.Intent;
import teammates.ui.webapi.DeleteFeedbackResponseCommentAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link DeleteFeedbackResponseCommentAction}.
 */
public class DeleteFeedbackResponseCommentActionIT extends BaseActionIT<DeleteFeedbackResponseCommentAction> {

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
        return DELETE;
    }

    @Test
    @Override
    protected void testExecute() {
        ______TS("Typical successful case, comment deleted");
        FeedbackResponseComment frc = typicalBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, frc.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        DeleteFeedbackResponseCommentAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertNull(logic.getFeedbackResponseComment(frc.getId()));
        assertEquals("Successfully deleted feedback response comment.", output.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        ______TS("Instructor who give the comment can delete comment");
        FeedbackResponseComment frc = typicalBundle.feedbackResponseComments.get("comment1ToResponse1ForQ3");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, frc.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        Instructor instructorWhoGiveComment = typicalBundle.instructors.get("instructor1OfCourse1");

        assertEquals(instructorWhoGiveComment.getEmail(), frc.getGiver());
        loginAsInstructor(instructorWhoGiveComment.getGoogleId());
        verifyCanAccess(submissionParams);

        ______TS("Different instructor of same course cannot delete comment");

        Instructor differentInstructorInSameCourse = typicalBundle.instructors.get("instructor2OfCourse1");
        assertNotEquals(differentInstructorInSameCourse.getEmail(), frc.getGiver());
        loginAsInstructor(differentInstructorInSameCourse.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

}
