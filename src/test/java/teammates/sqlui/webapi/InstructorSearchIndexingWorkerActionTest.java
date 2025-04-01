package teammates.sqlui.webapi;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.http.HttpStatus;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.InstructorSearchIndexingWorkerAction;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link InstructorSearchIndexingWorkerAction}.
 */
public class InstructorSearchIndexingWorkerActionTest extends BaseActionTest<InstructorSearchIndexingWorkerAction> {

    private Instructor typicalInstructor;

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.INSTRUCTOR_SEARCH_INDEXING_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUpMethod() {
        typicalInstructor = getTypicalInstructor();
    }

    @AfterMethod
    void tearDownMethod() {
        Mockito.reset(mockLogic);
    }

    @Test
    void testExecute_instructorIndexed_success() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalInstructor.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, typicalInstructor.getEmail(),
        };

        when(mockLogic.getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail()))
                .thenReturn(typicalInstructor);

        InstructorSearchIndexingWorkerAction action = getAction(params);
        JsonResult r = getJsonResult(action);
        MessageOutput output = (MessageOutput) r.getOutput();

        assertEquals("Successful", output.getMessage());

        verify(mockLogic, times(1))
                .getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail());
        verify(mockLogic, times(1)).putInstructorDocument(typicalInstructor);
    }

    @Test
    void testExecute_instructorIndexed_failure() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalInstructor.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, typicalInstructor.getEmail(),
        };

        when(mockLogic.getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail()))
                .thenReturn(typicalInstructor);
        doThrow(SearchServiceException.class).when(mockLogic).putInstructorDocument(typicalInstructor);

        InstructorSearchIndexingWorkerAction action = getAction(params);
        JsonResult r = getJsonResult(action, HttpStatus.SC_BAD_GATEWAY);
        MessageOutput output = (MessageOutput) r.getOutput();

        assertEquals("Failure", output.getMessage());

        verify(mockLogic, times(1))
                .getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail());
        verify(mockLogic, times(1)).putInstructorDocument(typicalInstructor);
    }

    @Test
    void testExecute_invalidParameters_throwsInvalidHttpParameterException() {

        ______TS("Null Course Id");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, null,
                Const.ParamsNames.INSTRUCTOR_EMAIL, typicalInstructor.getEmail(),
        };

        InvalidHttpParameterException e = verifyHttpParameterFailure(params);
        assertEquals("The [courseid] HTTP parameter is null.", e.getMessage());

        ______TS("Null Instructor Email");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalInstructor.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, null,
        };

        e = verifyHttpParameterFailure(params);
        assertEquals("The [instructoremail] HTTP parameter is null.", e.getMessage());

    }

    @Test
    void testAccessControl_onlyAdminCanAccess() {

        ______TS("Non-logged-in users cannot access");

        logoutUser();
        verifyCannotAccess();

        ______TS("Non-registered users cannot access");

        loginAsUnregistered("unregistered user");
        verifyCannotAccess();

        ______TS("Students cannot access");

        loginAsStudent(getTypicalStudent().getGoogleId());
        verifyCannotAccess();

        ______TS("Instructors cannot access");

        loginAsInstructor(getTypicalInstructor().getGoogleId());
        verifyCannotAccess();

        ______TS("Admin can access");

        loginAsAdmin();
        verifyCanAccess();

    }

}
