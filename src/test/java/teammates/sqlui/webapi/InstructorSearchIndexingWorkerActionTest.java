package teammates.sqlui.webapi;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.webapi.InstructorSearchIndexingWorkerAction;

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

    @Test
    void testExecute_instructorIndexed_success() throws Exception {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, typicalInstructor.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, typicalInstructor.getEmail(),
        };

        when(mockLogic.getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail()))
                .thenReturn(typicalInstructor);

        InstructorSearchIndexingWorkerAction action = getAction(params);
        getJsonResult(action);

        verify(mockLogic, times(1))
                .getInstructorForEmail(typicalInstructor.getCourseId(), typicalInstructor.getEmail());
        verify(mockLogic, times(1)).putInstructorDocument(typicalInstructor);
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
