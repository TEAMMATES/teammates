package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.test.GroupNames;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.SendEmailRequest;
import teammates.ui.request.StudentUpdateRequest;

/**
 * Tests for {@link UpdateStudentAction}.
 */
public class UpdateStudentActionTest extends BaseActionTest<UpdateStudentAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void updateStudentAction_sendSummaryEmailTrue_updatesStudentAndQueuesSummaryEmail() {
        var section = given.section("section", s -> s.defaultCourse().name("Test Section"));
        var team = given.team("team", t -> t.section(section.alias()).name("Test Team"));
        given.student("student", s -> s.defaultCourse()
                .team(team.alias()).name("Test Student").comments("Original comments"));
        var instructorAccount = given.account("instructor-account");
        given.instructor("instructor", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        persistGivenData(given);

        StudentUpdateRequest requestBody = new StudentUpdateRequest(
                "Test Student",
                "updated.student@teammates.tmt",
                "Test Team",
                "Test Section",
                "Updated comments",
                true);

        MessageOutput result = execute(new RequestContext()
                .withAccountAuth(given.uuid("instructor-account"))
                .withParam(Const.ParamsNames.USER_ID, given.uuid("student").toString())
                .withRequest(requestBody));

        assertEquals(UpdateStudentAction.SUCCESSFUL_UPDATE_WITH_EMAIL, result.getMessage());
        List<EmailWrapper> queuedEmails = getQueuedEmails();
        assertEquals(1, queuedEmails.size());
        assertEquals(EmailType.STUDENT_EMAIL_CHANGED, queuedEmails.get(0).getType());
        assertEquals("updated.student@teammates.tmt", queuedEmails.get(0).getRecipient());
    }

    @Test(groups = GroupNames.ACTION)
    public void updateStudentAction_sendSummaryEmailFalse_updatesStudentWithoutQueueingEmail() {
        var section = given.section("section", s -> s.defaultCourse().name("Test Section"));
        var team = given.team("team", t -> t.section(section.alias()).name("Test Team"));
        given.student("student", s -> s.defaultCourse()
                .team(team.alias()).name("Test Student").comments("Original comments"));
        var instructorAccount = given.account("instructor-account");
        given.instructor("instructor", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        persistGivenData(given);

        StudentUpdateRequest requestBody = new StudentUpdateRequest(
                "Test Student",
                "test.student@teammates.tmt",
                "Test Team",
                "Test Section",
                "Original comments",
                false);

        MessageOutput result = execute(new RequestContext()
                .withAccountAuth(given.uuid("instructor-account"))
                .withParam(Const.ParamsNames.USER_ID, given.uuid("student").toString())
                .withRequest(requestBody));

        assertEquals(UpdateStudentAction.SUCCESSFUL_UPDATE, result.getMessage());
        assertEquals(0, getQueuedEmails().size());
    }

    private List<EmailWrapper> getQueuedEmails() {
        return mockTaskQueuer.getTasksAdded().stream()
                .map(TaskWrapper::getRequestBody)
                .map(SendEmailRequest.class::cast)
                .map(SendEmailRequest::getEmail)
                .toList();
    }
}
