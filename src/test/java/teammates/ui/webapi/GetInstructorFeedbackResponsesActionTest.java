package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackResponsesData;

/**
 * Tests for {@link GetInstructorFeedbackResponsesAction}.
 */
public class GetInstructorFeedbackResponsesActionTest
        extends BaseActionTest<GetInstructorFeedbackResponsesAction, FeedbackResponsesData> {

    private static final String NON_EXISTENT_FEEDBACK_QUESTION_ID =
            UUID.fromString("00000000-0000-4000-8000-000000000102").toString();

    @Test(groups = GroupNames.ACTION)
    public void getInstructorFeedbackResponsesAction_validQuestion_returnsResponsesFromInstructor() {
        var account = given.account("account");
        var instructor = given.instructor("instructor", i -> i.defaultCourse().account(account.alias()).coOwner());
        var otherInstructor = given.instructor("other-instructor", i -> i.defaultCourse().coOwner());
        var student = given.student("student", s -> s.defaultCourse());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        var question = given.feedbackQuestion("question", q -> q.feedbackSession(session.alias()).instructorsToStudents());
        var response = given.feedbackResponse("response", r -> r.feedbackQuestion(question.alias())
                .giverInstructor(instructor.alias()).recipientStudent(student.alias()).text("instructor answer"));
        given.feedbackResponse("other-response", r -> r.feedbackQuestion(question.alias())
                .giverInstructor(otherInstructor.alias()).recipientStudent(student.alias()).text("other answer"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, question.id().toString())
                .withAccountAuth(account.id());

        FeedbackResponsesData result = execute(request);

        assertEquals(1, result.getResponses().size());
        assertEquals(response.id(), result.getResponses().get(0).getFeedbackResponseId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstructorFeedbackResponsesAction_questionDoesNotExist_throwsEntityNotFoundException() {
        var account = given.account("account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, NON_EXISTENT_FEEDBACK_QUESTION_ID)
                .withAccountAuth(account.id());

        assertActionThrows(EntityNotFoundException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstructorFeedbackResponsesAction_userNotLoggedIn_throwsUnauthorizedAccessException() {
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        var question = given.feedbackQuestion("question", q -> q.feedbackSession(session.alias()).instructorsToStudents());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, question.id().toString());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstructorFeedbackResponsesAction_studentAccessesInstructorEndpoint_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        given.student("student", s -> s.defaultCourse().account(account.alias()));
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        var question = given.feedbackQuestion("question", q -> q.feedbackSession(session.alias()).instructorsToStudents());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, question.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstructorFeedbackResponsesAction_instructorWithoutSubmitPrivilege_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        given.instructor("instructor", i -> i.defaultCourse().account(account.alias()).noPrivileges());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        var question = given.feedbackQuestion("question", q -> q.feedbackSession(session.alias()).instructorsToStudents());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, question.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
