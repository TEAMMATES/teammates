package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.test.GroupNames;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.request.ResponseInstructorCommentUpdateRequest;

/**
 * SUT: {@link UpdateResponseInstructorCommentAction}.
 */
public class UpdateResponseInstructorCommentActionIT extends BaseActionIT<UpdateResponseInstructorCommentAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testExecute() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        ResponseInstructorComment frc = typicalBundle.responseInstructorComments.get("comment1ToResponse1ForQ1");
        ______TS("Typical successful case for INSTRUCTOR_RESULT");
        loginAsInstructor(instructor.getGoogleId());

        String [] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, frc.getId().toString(),
        };
        String newCommentText = frc.getCommentText() + " (Edited)";
        ResponseInstructorCommentUpdateRequest requestBody = new ResponseInstructorCommentUpdateRequest(
                newCommentText, Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS));

        UpdateResponseInstructorCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ResponseInstructorComment actualFrc = inTransaction(() -> logic.getResponseInstructorComment(frc.getId()));
        assertEquals(newCommentText, actualFrc.getCommentText());
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testAccessControl() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        ResponseInstructorComment frc = typicalBundle.responseInstructorComments.get("comment1ToResponse1ForQ1");
        ______TS("successful case for instructor result");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, frc.getId().toString(),
        };

        loginAsInstructor(instructor.getGoogleId());
        verifyCanAccess(submissionParams);
    }

}
