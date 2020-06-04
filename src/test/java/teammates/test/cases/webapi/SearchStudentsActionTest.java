package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.SearchStudentsAction;
import teammates.ui.webapi.output.StudentsData;

/**
 * SUT:{@link SearchStudentsAction}.
 */
public class SearchStudentsActionTest extends BaseActionTest<SearchStudentsAction> {

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
        putDocuments(dataBundle);
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void testExecute() {
        // See individual test cases below
    }

    @Test
    public void execute_notEnoughParameters_parameterFailure() {
        loginAsAdmin();
        verifyHttpParameterFailure();
    }

    @Test
    public void execute_adminSearchName_success() {
        StudentAttributes acc = typicalBundle.students.get("student1InCourse1");
        loginAsAdmin();
        String[] accNameParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, acc.getName(),
        };
        SearchStudentsAction a = getAction(accNameParams);
        JsonResult result = getJsonResult(a);
        StudentsData response = (StudentsData) result.getOutput();
        assertEquals(11, response.getStudents().size());
    }

    @Test
    public void execute_adminSearchCourseId_success() {
        StudentAttributes acc = typicalBundle.students.get("student1InCourse1");
        loginAsAdmin();
        String[] accCourseIdParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, acc.getCourse(),
        };
        SearchStudentsAction a = getAction(accCourseIdParams);
        JsonResult result = getJsonResult(a);
        StudentsData response = (StudentsData) result.getOutput();
        assertEquals(5, response.getStudents().size());
    }

    @Test
    public void execute_adminSearchAccountsGeneral_success() {
        loginAsAdmin();
        String[] accNameParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "Course2",
        };
        SearchStudentsAction a = getAction(accNameParams);
        JsonResult result = getJsonResult(a);
        StudentsData response = (StudentsData) result.getOutput();

        assertEquals(2, response.getStudents().size());
    }

    @Test
    public void execute_adminSearchEmail_success() {
        loginAsAdmin();
        StudentAttributes acc = typicalBundle.students.get("student1InCourse1");
        String[] emailParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, acc.getEmail(),
        };

        SearchStudentsAction a = getAction(emailParams);
        JsonResult result = getJsonResult(a);
        StudentsData response = (StudentsData) result.getOutput();

        assertEquals(1, response.getStudents().size());
    }

    @Test
    public void execute_adminSearchNoMatch_noMatch() {
        loginAsAdmin();
        String[] accNameParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "minuscoronavirus",
        };
        SearchStudentsAction a = getAction(accNameParams);
        JsonResult result = getJsonResult(a);
        StudentsData response = (StudentsData) result.getOutput();

        assertEquals(0, response.getStudents().size());
    }

    @Test
    public void execute_adminSearchGoogleId_success() {
        loginAsAdmin();
        String[] googleIdParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "Course",
        };
        SearchStudentsAction a = getAction(googleIdParams);
        JsonResult result = getJsonResult(a);
        StudentsData response = (StudentsData) result.getOutput();

        assertEquals(11, response.getStudents().size());
    }

    @Test
    public void execute_instructorSearchGoogleId_matchOnlyStudentsInCourse() {
        loginAsInstructor("idOfInstructor1OfCourse1");
        String[] googleIdParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "Course",
        };

        SearchStudentsAction a = getAction(googleIdParams);
        JsonResult result = getJsonResult(a);
        StudentsData response = (StudentsData) result.getOutput();

        assertEquals(5, response.getStudents().size());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAccessibleForAdmin();
        verifyOnlyInstructorsCanAccess();
    }
}
