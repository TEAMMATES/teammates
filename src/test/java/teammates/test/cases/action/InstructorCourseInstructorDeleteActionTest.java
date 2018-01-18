package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseInstructorDeleteAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorCourseInstructorDeleteAction}.
 */
public class InstructorCourseInstructorDeleteActionTest extends BaseActionTest {

    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_DELETE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes loginInstructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String loginInstructorId = loginInstructor.googleId;
        String courseId = loginInstructor.courseId;
        String adminUserId = "admin.user";

        gaeSimulation.loginAsInstructor(loginInstructorId);

        ______TS("Typical case: Delete other instructor successfully, redirect back to edit page");

        InstructorAttributes instructorToDelete = typicalBundle.instructors.get("instructor2OfCourse1");
        String instructorEmailToDelete = instructorToDelete.email;

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmailToDelete
        };

        InstructorCourseInstructorDeleteAction deleteAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(deleteAction);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                        false,
                        "idOfInstructor1OfCourse1",
                        "idOfTypicalCourse1"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED, redirectResult.getStatusMessage());

        assertFalse(instructorsLogic.isEmailOfInstructorOfCourse(instructorEmailToDelete, courseId));

        String expectedLogSegment = "Instructor <span class=\"bold\"> " + instructorEmailToDelete + "</span>"
                + " in Course <span class=\"bold\">[" + courseId + "]</span> deleted.<br>";
        AssertHelper.assertContains(expectedLogSegment, deleteAction.getLogMessage());

        ______TS("Success: delete own instructor role from course, redirect back to courses page");

        instructorEmailToDelete = loginInstructor.email;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmailToDelete
        };

        deleteAction = getAction(submissionParams);
        redirectResult = getRedirectResult(deleteAction);

        assertEquals(
                getPageResultDestination(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, false, "idOfInstructor1OfCourse1"),
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED, redirectResult.getStatusMessage());

        assertFalse(instructorsLogic.isGoogleIdOfInstructorOfCourse(loginInstructor.googleId, courseId));

        expectedLogSegment = "Instructor <span class=\"bold\"> " + instructorEmailToDelete + "</span>"
                + " in Course <span class=\"bold\">[" + courseId + "]</span> deleted.<br>";
        AssertHelper.assertContains(expectedLogSegment, deleteAction.getLogMessage());

        ______TS("Masquerade mode: delete instructor failed due to last instructor in course");

        instructorToDelete = typicalBundle.instructors.get("instructor4");
        instructorEmailToDelete = instructorToDelete.email;
        courseId = instructorToDelete.courseId;

        gaeSimulation.loginAsAdmin(adminUserId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmailToDelete
        };

        deleteAction = getAction(addUserIdToParams(instructorToDelete.googleId, submissionParams));
        redirectResult = getRedirectResult(deleteAction);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                        true,
                        "idOfInstructor4",
                        "idOfCourseNoEvals"),
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_DELETE_NOT_ALLOWED, redirectResult.getStatusMessage());

        assertTrue(instructorsLogic.isGoogleIdOfInstructorOfCourse(instructorToDelete.googleId, courseId));

        expectedLogSegment = "Instructor <span class=\"bold\"> " + instructorEmailToDelete + "</span>"
                + " in Course <span class=\"bold\">[" + courseId + "]</span> could not be deleted "
                + "as there is only one instructor left to be able to modify instructors.<br>";
        AssertHelper.assertContains(expectedLogSegment, deleteAction.getLogMessage());
    }

    @Override
    protected InstructorCourseInstructorDeleteAction getAction(String... params) {
        return (InstructorCourseInstructorDeleteAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    protected String getPageResultDestination(String parentUri, boolean isError, String userId, String courseId) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.ERROR, Boolean.toString(isError));
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.COURSE_ID, courseId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.USER_ID, userId);
        return pageDestination;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor2OfCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.email
        };

        verifyUnaccessibleWithoutModifyInstructorPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
