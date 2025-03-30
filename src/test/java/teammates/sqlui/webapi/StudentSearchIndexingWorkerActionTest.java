package teammates.sqlui.webapi;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.TaskQueue;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.StudentSearchIndexingWorkerAction;

/**
 * SUT: {@link StudentSearchIndexingWorkerAction}.
 */
public class StudentSearchIndexingWorkerActionTest extends BaseActionTest<StudentSearchIndexingWorkerAction> {

    private Student typicalStudent;

    @Override
     String getActionUri() {
        return TaskQueue.STUDENT_SEARCH_INDEXING_WORKER_URL;
    }

    @BeforeMethod
    void setUpMethod() {
        typicalStudent = getTypicalStudent();
        reset(mockLogic);
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @Test
    void textExecute_nullParams_throwsInvalidHttpParameterException() {
        String[][] testCases = {
                { Const.ParamsNames.COURSE_ID, null, Const.ParamsNames.STUDENT_EMAIL, "email" },
                { Const.ParamsNames.COURSE_ID, "course-id", Const.ParamsNames.STUDENT_EMAIL, null },
        };

        for (String[] params : testCases) {
            verifyHttpParameterFailure(params);
        }
    }

    @Test
    void testExecute_typicalCase_success() throws Exception {
        String[] submissionParams = new String[] {
                ParamsNames.COURSE_ID, typicalStudent.getCourseId(),
                ParamsNames.STUDENT_EMAIL, typicalStudent.getEmail(),
        };

        when(mockLogic.getStudentForEmail(typicalStudent.getCourseId(), typicalStudent.getEmail()))
                .thenReturn(typicalStudent);

        StudentSearchIndexingWorkerAction action = getAction(submissionParams);
        JsonResult res = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, res.getStatusCode());
        assertEquals("Successful", ((MessageOutput) res.getOutput()).getMessage());

        verify(mockLogic, times(1))
                .getStudentForEmail(typicalStudent.getCourseId(), typicalStudent.getEmail());
        verify(mockLogic, times(1)).putStudentDocument(typicalStudent);
    }

    @Test
    void testExecute_putDocument_throwsException() throws Exception {
        String[] submissionParams = new String[] {
                ParamsNames.COURSE_ID, typicalStudent.getCourseId(),
                ParamsNames.STUDENT_EMAIL, typicalStudent.getEmail(),
        };

        when(mockLogic.getStudentForEmail(typicalStudent.getCourseId(),
                typicalStudent.getEmail())).thenReturn(typicalStudent);
        doThrow(new SearchServiceException("Failure", HttpStatus.SC_BAD_GATEWAY))
                .when(mockLogic).putStudentDocument(typicalStudent);

        StudentSearchIndexingWorkerAction action = getAction(submissionParams);
        JsonResult res = getJsonResult(action, HttpStatus.SC_BAD_GATEWAY);

        assertEquals(HttpStatus.SC_BAD_GATEWAY, res.getStatusCode());
        assertEquals("Failure", ((MessageOutput) res.getOutput()).getMessage());

        verify(mockLogic, times(1))
                .getStudentForEmail(typicalStudent.getCourseId(), typicalStudent.getEmail());
        verify(mockLogic, times(1)).putStudentDocument(typicalStudent);
    }

    @Test
    void testSpecificAccessControl_onlyAdmin_canAccess() {
        logoutUser();
        verifyCannotAccess();

        loginAsUnregistered("unregistered user");
        verifyCannotAccess();

        loginAsStudent(getTypicalStudent().getGoogleId());
        verifyCannotAccess();

        loginAsInstructor(getTypicalInstructor().getGoogleId());
        verifyCannotAccess();

        loginAsAdmin();
        verifyCanAccess();
    }
}
