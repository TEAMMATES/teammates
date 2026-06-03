package teammates.it.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteResponseInstructorCommentAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link DeleteResponseInstructorCommentAction}.
 */
public class DeleteResponseInstructorCommentActionIT extends BaseActionIT<DeleteResponseInstructorCommentAction> {
    private DataBundle typicalBundle;

    @BeforeMethod
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
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
        ResponseInstructorComment frc = typicalBundle.responseInstructorComments.get("comment1ToResponse1ForQ1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, frc.getId().toString(),
        };

        DeleteResponseInstructorCommentAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertNull(inTransaction(() -> logic.getResponseInstructorComment(frc.getId())));
        assertEquals("Successfully deleted feedback response comment.", output.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        ______TS("Instructor who give the comment can delete comment");
        ResponseInstructorComment frc = typicalBundle.responseInstructorComments.get("comment1ToResponse1ForQ3");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, frc.getId().toString(),
        };
        Instructor instructorWhoGiveComment = typicalBundle.instructors.get("instructor1OfCourse1");

        assertEquals(instructorWhoGiveComment, frc.getGiver().getGiverUser());
        loginAsInstructor(instructorWhoGiveComment.getGoogleId());
        verifyCanAccess(submissionParams);

        ______TS("Different instructor of same course cannot delete comment");

        Instructor differentInstructorInSameCourse = typicalBundle.instructors.get("instructor2OfCourse1");
        assertNotEquals(differentInstructorInSameCourse, frc.getGiver().getGiverUser());
        loginAsInstructor(differentInstructorInSameCourse.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

}
