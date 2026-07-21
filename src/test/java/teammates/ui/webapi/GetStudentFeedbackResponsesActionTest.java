package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.output.FeedbackResponsesData;

/**
 * Tests for {@link GetStudentFeedbackResponsesAction}.
 */
public class GetStudentFeedbackResponsesActionTest
        extends BaseActionTest<GetStudentFeedbackResponsesAction, FeedbackResponsesData> {

    @Test(groups = GroupNames.ACTION)
    public void getStudentFeedbackResponsesAction_validQuestion_returnsResponsesFromStudent() {
        var account = given.account("account");
        var student = given.student("student", s -> s.defaultCourse().account(account.alias()));
        var otherStudent = given.student("other-student", s -> s.defaultCourse());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        var question = given.feedbackQuestion("question", q -> q.feedbackSession(session.alias()).studentsToSelf());
        var response = given.feedbackResponse("response", r -> r.feedbackQuestion(question.alias())
                .giverStudent(student.alias()).recipientStudent(student.alias()).text("student answer"));
        given.feedbackResponse("other-response", r -> r.feedbackQuestion(question.alias())
                .giverStudent(otherStudent.alias()).recipientStudent(otherStudent.alias()).text("other answer"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, question.id().toString())
                .withAccountAuth(account.id());

        FeedbackResponsesData result = execute(request);

        assertEquals(1, result.getResponses().size());
        assertEquals(response.id(), result.getResponses().get(0).getFeedbackResponseId());
    }
}
