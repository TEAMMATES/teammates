package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackSession;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.DeadlineExtensionsData;
import teammates.ui.request.DeadlineExtensionsUpdateRequest;

/**
 * Tests for {@link UpdateDeadlineExtensionsAction}.
 */
public class UpdateDeadlineExtensionsActionTest
        extends BaseActionTest<UpdateDeadlineExtensionsAction, DeadlineExtensionsData> {

    @Test(groups = GroupNames.ACTION)
    public void updateDeadlineExtensionsAction_existingSessionWithNotify_updatesDeadlineExtensionsData() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("instructor", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        var studentToUpdate = given.student("student-to-update", s -> s.defaultCourse());
        var studentToCreate = given.student("student-to-create", s -> s.defaultCourse());
        var studentToDelete = given.student("student-to-delete", s -> s.defaultCourse());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse());
        given.deadlineExtension("deadline-extension",
                d -> d.student(studentToUpdate.alias()).feedbackSession(session.alias()));
        given.deadlineExtension("deadline-extension-to-delete",
                d -> d.student(studentToDelete.alias()).feedbackSession(session.alias()));
        persistGivenData(given);

        FeedbackSession persistedSession = getEntityInTransaction(FeedbackSession.class, session.id());
        Instant updatedDeadline = persistedSession.getEndTime().plusSeconds(3600);
        Instant createdDeadline = persistedSession.getEndTime().plusSeconds(7200);

        DeadlineExtensionsUpdateRequest requestBody = new DeadlineExtensionsUpdateRequest();
        requestBody.setUserDeadlines(Map.of(
                studentToUpdate.id(), updatedDeadline.toEpochMilli(),
                studentToCreate.id(), createdDeadline.toEpochMilli()));

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withParam(Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, "true")
                .withAccountAuth(instructorAccount.id())
                .withRequest(requestBody);

        DeadlineExtensionsData result = execute(request);

        assertEquals(updatedDeadline.toEpochMilli(), result.getUserDeadlines().get(studentToUpdate.id()));
        assertEquals(createdDeadline.toEpochMilli(), result.getUserDeadlines().get(studentToCreate.id()));
        assertNull(result.getUserDeadlines().get(studentToDelete.id()));

        DeadlineExtension persistedCreatedDeadline = inTransaction(() ->
                Logic.inst().getDeadlineExtension(session.id(), studentToCreate.id()));
        assertEquals(createdDeadline.toEpochMilli(), persistedCreatedDeadline.getEndTime().toEpochMilli());
    }

    @Test(groups = GroupNames.ACTION)
    public void updateDeadlineExtensionsAction_instructorWithoutModifyPrivilege_throwsUnauthorizedAccessException() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("instructor", i -> i.defaultCourse().account(instructorAccount.alias()).noPrivileges());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse());
        var student = given.student("student", s -> s.defaultCourse());
        persistGivenData(given);

        FeedbackSession persistedSession = getEntityInTransaction(FeedbackSession.class, session.id());

        DeadlineExtensionsUpdateRequest requestBody = new DeadlineExtensionsUpdateRequest();
        requestBody.setUserDeadlines(Map.of(student.id(), persistedSession.getEndTime().plusSeconds(3600).toEpochMilli()));

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(instructorAccount.id())
                .withRequest(requestBody);

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
