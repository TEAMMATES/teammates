package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.webapi.DeleteFeedbackQuestionAction;

/**
 * SUT: {@link DeleteFeedbackQuestionAction}.
 */
public class DeleteFeedbackQuestionActionIT extends BaseActionIT<DeleteFeedbackQuestionAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
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
        FeedbackResponseComment frc1 = typicalBundle.feedbackResponseComments.get("comment1ToResponse1ForQ1");
        FeedbackQuestion typicalQuestion =
                logic.getFeedbackQuestion(fq1.getId());
        assertEquals(FeedbackQuestionType.TEXT, typicalQuestion.getQuestionDetailsCopy().getQuestionType());

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
        assertNull(logic.getFeedbackQuestion(typicalQuestion.getId()));
        // responses to this question are deleted
        assertNull(logic.getFeedbackResponse(fr1.getId()));
        assertNull(logic.getFeedbackResponse(fr2.getId()));
        // feedback response comments to the responses are deleted
        assertNull(logic.getFeedbackResponseComment(frc1.getId()));
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        Course course1 = typicalBundle.courses.get("course1");
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackQuestion fq1 = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        FeedbackQuestion typicalQuestion = logic.getFeedbackQuestion(fq1.getId());

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
