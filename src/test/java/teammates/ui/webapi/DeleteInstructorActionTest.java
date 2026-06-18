package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link DeleteInstructorAction}.
 */
public class DeleteInstructorActionTest extends BaseActionTest<DeleteInstructorAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void deleteInstructorAction_instructorDeletesOtherInstructor_deletesInstructor() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("course");
        given.instructor("requester", i -> i.course(course.alias()).account(requesterAccount.alias()).coOwner());
        var target = given.instructor("target", i -> i.course(course.alias()));
        persistGivenData(given);

        verifyPresentInDatabase(Instructor.class, target.id());

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, target.id().toString())
                .withAccountAuth(requesterAccount.id());

        execute(request);

        verifyAbsentInDatabase(Instructor.class, target.id());
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteInstructorAction_instructorNotInCourse_throwsUnauthorizedAccessException() {
        var requesterAccount = given.account("requester-account");
        var otherCourse = given.course("other-course");
        given.instructor("requester", i -> i.course(otherCourse.alias()).account(requesterAccount.alias()).coOwner());
        var target = given.instructor("target", i -> i.defaultCourse());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, target.id().toString())
                .withAccountAuth(requesterAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
