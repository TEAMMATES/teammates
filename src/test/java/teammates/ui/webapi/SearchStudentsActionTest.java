package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.StudentsData;

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
    public void execute_invalidParameters_parameterFailure() {
        loginAsAdmin();
        verifyHttpParameterFailure();

        String[] notEnoughParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "dummy",
        };
        verifyHttpParameterFailure(notEnoughParams);

        String[] invalidEntityParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "dummy",
                Const.ParamsNames.ENTITY_TYPE, "dummy",
        };
        verifyHttpParameterFailure(invalidEntityParams);

        String[] adminParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "dummy",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };
        String[] instructorParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "dummy",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        loginAsAdmin();
        verifyHttpParameterFailure(instructorParams);

        loginAsInstructor("idOfInstructor1OfCourse1");
        verifyHttpParameterFailure(adminParams);
    }

    @Test
    public void execute_adminSearchName_success() {
        StudentAttributes acc = typicalBundle.students.get("student1InCourse1");
        loginAsAdmin();
        String[] accNameParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, acc.getName(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
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
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
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
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
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
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
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
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
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
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
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
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
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
