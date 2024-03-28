package teammates.it.ui.webapi;

import java.util.Arrays;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.StringHelper;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.request.FeedbackResponseCommentCreateRequest;
import teammates.ui.request.Intent;
import teammates.ui.webapi.CreateFeedbackResponseCommentAction;

/**
 * SUT: {@link CreateFeedbackResponseCommentAction}.
 */
public class CreateFeedbackResponseCommentActionIT extends BaseActionIT<CreateFeedbackResponseCommentAction> {

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
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        ______TS("Successful case: student submission");
        Student student = typicalBundle.students.get("student1InCourse1");
        FeedbackResponse fr = typicalBundle.feedbackResponses.get("response1ForQ1InSession2");
        loginAsStudent(student.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(fr.getId().toString()),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest(
                "Student submission comment", Arrays.asList(CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.INSTRUCTORS));
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseComment comment =
                logic.getFeedbackResponseCommentForResponseFromParticipant(fr.getId());
        assertEquals(comment.getCommentText(), "Student submission comment");
        assertEquals(student.getEmail(), comment.getGiver());
        assertTrue(comment.getIsCommentFromFeedbackParticipant());
        assertTrue(comment.getIsVisibilityFollowingFeedbackQuestion());
        assertEquals(FeedbackParticipantType.STUDENTS, comment.getGiverType());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Student student = typicalBundle.students.get("student1InCourse1");
        FeedbackResponse fr = typicalBundle.feedbackResponses.get("response1ForQ1InSession2");

        String[] submissionParamsStudentToStudents = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, StringHelper.encrypt(fr.getId().toString()),
        };

        ______TS("students access own response to give comments");

        loginAsStudent(student.getGoogleId());
        verifyCanAccess(submissionParamsStudentToStudents);
    }

}
