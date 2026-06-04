package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.ui.webapi.DeleteFeedbackQuestionAction;

/**
 * SUT: {@link DeleteFeedbackQuestionAction}.
 */
public class DeleteFeedbackQuestionActionIT extends BaseActionIT<DeleteFeedbackQuestionAction> {
    private DataBundle typicalBundle;

    @BeforeMethod
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        Instructor instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackQuestion fq1 = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackResponse fr1 = typicalBundle.feedbackResponses.get("response1ForQ1");
        FeedbackResponse fr2 = typicalBundle.feedbackResponses.get("response2ForQ1");
        ResponseInstructorComment frc1 = typicalBundle.responseInstructorComments.get("comment1ToResponse1ForQ1");
        FeedbackQuestion typicalQuestion = inTransaction(() -> logic.getFeedbackQuestion(fq1.getId()));
        assertEquals(FeedbackQuestionType.TEXT, typicalQuestion.getQuestionType());

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical success case");

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getId().toString(),
        };

        DeleteFeedbackQuestionAction a = getAction(params);
        getJsonResult(a);

        // question is deleted
        assertNull(inTransaction(() -> logic.getFeedbackQuestion(typicalQuestion.getId())));
        // responses to this question are deleted
        assertNull(inTransaction(() -> logic.getFeedbackResponse(fr1.getId())));
        assertNull(inTransaction(() -> logic.getFeedbackResponse(fr2.getId())));
        // feedback response comments to the responses are deleted
        assertNull(inTransaction(() -> logic.getResponseInstructorComment(frc1.getId())));
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        Course course1 = typicalBundle.courses.get("course1");
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackQuestion fq1 = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        FeedbackQuestion typicalQuestion = inTransaction(() -> logic.getFeedbackQuestion(fq1.getId()));

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("inaccessible without ModifySessionPrivilege");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getId().toString(),
        };

        verifyInaccessibleWithoutModifySessionPrivilege(course1, submissionParams);

        ______TS("only instructors of the same course with correct privilege can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                course1, Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }
}
