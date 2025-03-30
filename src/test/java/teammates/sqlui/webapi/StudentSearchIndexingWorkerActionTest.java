package teammates.sqlui.webapi;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.TaskQueue;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.StudentSearchIndexingWorkerAction;

public class StudentSearchIndexingWorkerActionTest extends BaseActionTest<StudentSearchIndexingWorkerAction> {
    
    @Override
     String getActionUri() {
        return TaskQueue.STUDENT_SEARCH_INDEXING_WORKER_URL;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @Test
    protected void textExecute_nullParams_throwsInvalidHttpParameterException() {
        String[] params2 = {
            Const.ParamsNames.COURSE_ID, null,
            Const.ParamsNames.STUDENT_EMAIL, "email",
        };
        verifyHttpParameterFailure(params2);

        String[] params3 = {
            Const.ParamsNames.COURSE_ID, "course-id",
            Const.ParamsNames.STUDENT_EMAIL, null,
        };
        verifyHttpParameterFailure(params3);


        String[] params4 = {
            Const.ParamsNames.COURSE_ID, "course-id",
            Const.ParamsNames.STUDENT_EMAIL, null,
        };
        verifyHttpParameterFailure(params4);
    }


    @Test
    void testExecute_typicalSuccessCase() throws Exception {
        Student student = getTypicalStudent();


        String[] submissionParams = new String[] {
            ParamsNames.COURSE_ID, student.getCourseId(),
            ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        when(mockLogic.getStudentForEmail(student.getCourseId(), student.getEmail())).thenReturn(student);
        
        StudentSearchIndexingWorkerAction action = getAction(submissionParams);
        JsonResult res = getJsonResult(action);

        assertEquals(HttpStatus.SC_ACCEPTED, res.getStatusCode());
        assertEquals("Successful", ((MessageOutput) res.getOutput()).getMessage());
    }

    @Test
    void testExecute_putDocument_throwsException() throws Exception {
        Student student = getTypicalStudent();


        String[] submissionParams = new String[] {
            ParamsNames.COURSE_ID, student.getCourseId(),
            ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        when(mockLogic.getStudentForEmail(student.getCourseId(), student.getEmail())).thenReturn(student);
        doThrow(new SearchServiceException("Failure", HttpStatus.SC_BAD_GATEWAY))
            .when(mockLogic).putStudentDocument(student);
        
        StudentSearchIndexingWorkerAction action = getAction(submissionParams);
        JsonResult res = getJsonResult(action, HttpStatus.SC_BAD_GATEWAY);

        assertEquals(HttpStatus.SC_BAD_GATEWAY, res.getStatusCode());
        assertEquals("Failure", ((MessageOutput) res.getOutput()).getMessage());
    }

    @Test
    void testSpecificAccessControl_onlyAdmin_canAccess() {
        verifyCannotAccess();

        loginAsInstructor("user-googleId");
        verifyCannotAccess();

        loginAsStudent("user-googleId");
        verifyCannotAccess();

        loginAsMaintainer();
        verifyCannotAccess();

        loginAsAdmin();
        verifyCanAccess();
    }
}
