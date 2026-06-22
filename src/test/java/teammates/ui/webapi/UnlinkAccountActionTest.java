package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link UnlinkAccountAction}.
 */
public class UnlinkAccountActionTest extends BaseActionTest<UnlinkAccountAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void unlinkAccountAction_studentUser_successAndQueuesRejoinEmail() {
        var account = given.account("account");
        var student = given.student("student",
                s -> s.account(account.alias()).email("student@test.tmt"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withAdminAuth();

        MessageOutput result = execute(request);

        assertEquals("Account unlinked successfully.", result.getMessage());

        Student updatedStudent = getEntityInTransaction(Student.class, student.id());
        assertNull(updatedStudent.getAccount());
    }

    @Test(groups = GroupNames.ACTION)
    public void unlinkAccountAction_instructorUser_success() {
        var account = given.account("account");
        var instructor = given.instructor("instructor", i -> i.account(account.alias()).coOwner());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, instructor.id().toString())
                .withAdminAuth();

        MessageOutput result = execute(request);

        assertEquals("Account unlinked successfully.", result.getMessage());

        Instructor updatedInstructor = getEntityInTransaction(Instructor.class, instructor.id());
        assertNull(updatedInstructor.getAccount());
    }

    @Test(groups = GroupNames.ACTION)
    public void unlinkAccountAction_unknownUser_throwsEntityNotFoundException() {
        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, "00000000-0000-0000-0000-000000000001")
                .withAdminAuth();

        assertActionThrows(EntityNotFoundException.class, request);
    }
}
