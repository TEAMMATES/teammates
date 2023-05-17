package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.UpdateInstructorAction;

/**
 * SUT: {@link UpdateInstructorAction}.
 */
public class UpdateInstructorActionIT extends BaseActionIT<UpdateInstructorAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() {
        Instructor instructorToEdit = typicalBundle.instructors.get("instructor2OfCourse1");
        String instructorId = instructorToEdit.getGoogleId();
        String courseId = instructorToEdit.getCourseId();
        String instructorDisplayName = instructorToEdit.getDisplayName();

        loginAsInstructor(instructorId);

        ______TS("Typical case: edit instructor successfully");

        final String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        String newInstructorName = "newName";
        String newInstructorEmail = "newEmail@email.com";
        String newInstructorRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;

        InstructorCreateRequest reqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                newInstructorEmail, newInstructorRole,
                instructorDisplayName, false);

        UpdateInstructorAction updateInstructorAction = getAction(reqBody, submissionParams);
        JsonResult actionOutput = getJsonResult(updateInstructorAction);

        InstructorData response = (InstructorData) actionOutput.getOutput();

        Instructor editedInstructor = logic.getInstructorByGoogleId(courseId, instructorId);
        assertEquals(newInstructorName, editedInstructor.getName());
        assertEquals(newInstructorName, response.getName());
        assertEquals(newInstructorEmail, editedInstructor.getEmail());
        assertEquals(newInstructorEmail, response.getEmail());
        assertFalse(editedInstructor.isDisplayedToStudents());
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertTrue(editedInstructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT));

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        ______TS("Failure case: edit failed due to invalid parameters");

        String invalidEmail = "wrongEmail.com";
        reqBody = new InstructorCreateRequest(instructorId, instructorToEdit.getName(),
                invalidEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                instructorDisplayName, true);

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(reqBody, submissionParams);
        String expectedErrorMessage = FieldValidator.getInvalidityInfoForEmail(invalidEmail);
        assertEquals(expectedErrorMessage, ihrbe.getMessage());

        verifyNoTasksAdded();

        ______TS("Failure case: after editing instructor, no instructors are displayed");

        instructorToEdit = typicalBundle.instructors.get("instructor1OfCourse3");

        loginAsInstructor(instructorToEdit.getGoogleId());

        reqBody = new InstructorCreateRequest(instructorToEdit.getGoogleId(), instructorToEdit.getName(),
                instructorToEdit.getEmail(), Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                null, false);

        InvalidOperationException ioe = verifyInvalidOperation(reqBody, new String[] {
                Const.ParamsNames.COURSE_ID, instructorToEdit.getCourseId(),
        });

        assertEquals("At least one instructor must be displayed to students", ioe.getMessage());

        verifyNoTasksAdded();

        ______TS("Masquerade mode: edit instructor successfully");

        loginAsAdmin();

        newInstructorName = "newName2";
        newInstructorEmail = "newEmail2@email.com";

        reqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                instructorDisplayName, true);

        updateInstructorAction = getAction(reqBody, submissionParams);
        actionOutput = getJsonResult(updateInstructorAction);

        response = (InstructorData) actionOutput.getOutput();

        editedInstructor = logic.getInstructorByGoogleId(courseId, instructorId);
        assertEquals(newInstructorEmail, editedInstructor.getEmail());
        assertEquals(newInstructorEmail, response.getEmail());
        assertEquals(newInstructorName, editedInstructor.getName());
        assertEquals(newInstructorName, response.getName());

        //remove the new instructor entity that was created
        logic.deleteCourseCascade("icieat.courseId");

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);

        ______TS("Unsuccessful case: test null course id parameter");

        String[] emptySubmissionParams = new String[0];
        InstructorCreateRequest newReqBody = new InstructorCreateRequest(instructorId, newInstructorName,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                instructorDisplayName, true);

        verifyHttpParameterFailure(newReqBody, emptySubmissionParams);

        verifyNoTasksAdded();

        ______TS("Unsuccessful case: test null instructor name parameter");

        InstructorCreateRequest nullNameReq = new InstructorCreateRequest(instructorId, null,
                newInstructorEmail, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                instructorDisplayName, true);

        verifyHttpRequestBodyFailure(nullNameReq, submissionParams);

        verifyNoTasksAdded();

        ______TS("Unsuccessful case: test null instructor email parameter");

        InstructorCreateRequest nullEmailReq = new InstructorCreateRequest(instructorId, newInstructorName,
                null, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                instructorDisplayName, true);

        verifyHttpRequestBodyFailure(nullEmailReq, submissionParams);

        verifyNoTasksAdded();
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        Instructor instructor = typicalBundle.instructors.get("instructor2OfCourse1");

        ______TS("only instructors of the same course can access");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(course,
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, submissionParams);
        ______TS("instructors of other courses cannot access");

        verifyInaccessibleForInstructorsOfOtherCourses(course, submissionParams);
    }
}

