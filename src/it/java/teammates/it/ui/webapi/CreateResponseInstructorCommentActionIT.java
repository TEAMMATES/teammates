package teammates.it.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.request.ResponseInstructorCommentCreateRequest;
import teammates.ui.webapi.CreateResponseInstructorCommentAction;

/**
 * SUT: {@link CreateResponseInstructorCommentAction}.
 */
public class CreateResponseInstructorCommentActionIT extends BaseActionIT<CreateResponseInstructorCommentAction> {
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
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        ______TS("Successful case: instructor result comment");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackResponse fr = typicalBundle.feedbackResponses.get("response1ForQ1InSession2");
        loginAsInstructor(instructor.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, fr.getId().toString(),
        };

        ResponseInstructorCommentCreateRequest requestBody = new ResponseInstructorCommentCreateRequest(
                "Instructor result comment", Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS));
        CreateResponseInstructorCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ResponseInstructorComment comment = fr.getResponseInstructorComments().stream()
                .filter(frc -> "Instructor result comment".equals(frc.getCommentText()))
                .findFirst()
                .orElseThrow();
        assertEquals(instructor, comment.getGiver().getGiverUser());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackResponse fr = typicalBundle.feedbackResponses.get("response1ForQ1InSession2");

        String[] submissionParamsStudentToStudents = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, fr.getId().toString(),
        };

        ______TS("instructor access response to give result comments");

        loginAsInstructor(instructor.getGoogleId());
        verifyCanAccess(submissionParamsStudentToStudents);
    }

}
