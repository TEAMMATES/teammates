package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.SearchCoursesAction;
import teammates.ui.webapi.output.SearchCoursesData;

/**
 * SUT: {@link SearchCoursesAction}.
 */
public class SearchCoursesActionTest extends BaseActionTest<SearchCoursesAction> {

    private final StudentAttributes student = typicalBundle.students.get("student1InCourse1");
    private final InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
        putDocuments(dataBundle);
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_COURSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        // See test cases below.
    }

    @Test
    public void execute_notEnoughParameters_parameterFailure() {
        loginAsAdmin();
        verifyHttpParameterFailure();
    }

    @Test
    public void execute_searchEmailStudent_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] {Const.ParamsNames.ADMIN_SEARCH_KEY,
                student.getEmail()};
        SearchCoursesAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchCoursesData response = (SearchCoursesData) result.getOutput();
        assertTrue(response.getStudents().stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .findAny()
                .isPresent());
    }

    @Test
    public void execute_searchNameStudent_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] {Const.ParamsNames.ADMIN_SEARCH_KEY,
                student.getName()};
        SearchCoursesAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchCoursesData response = (SearchCoursesData) result.getOutput();
        assertTrue(response.getStudents().stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .findAny()
                .isPresent());
    }

    @Test
    public void execute_searchCourseStudent_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] {Const.ParamsNames.ADMIN_SEARCH_KEY,
                student.getCourse()};
        SearchCoursesAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchCoursesData response = (SearchCoursesData) result.getOutput();
        assertTrue(response.getStudents().stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .findAny()
                .isPresent());
    }

    @Test
    public void execute_searchGoogleIdStudent_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] {Const.ParamsNames.ADMIN_SEARCH_KEY,
                student.getGoogleId()};
        SearchCoursesAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchCoursesData response = (SearchCoursesData) result.getOutput();
        assertTrue(response.getStudents().stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .findAny()
                .isPresent());
    }

    @Test
    public void execute_searchSectionStudent_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] {Const.ParamsNames.ADMIN_SEARCH_KEY,
                student.getSection()};
        SearchCoursesAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchCoursesData response = (SearchCoursesData) result.getOutput();
        assertTrue(response.getStudents().stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .findAny()
                .isPresent());
    }

    @Test
    public void execute_searchTeamStudent_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] {Const.ParamsNames.ADMIN_SEARCH_KEY,
                student.getTeam()};
        SearchCoursesAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchCoursesData response = (SearchCoursesData) result.getOutput();
        assertTrue(response.getStudents().stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .findAny()
                .isPresent());
    }

    @Test
    public void execute_searchEmailInstructor_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] {Const.ParamsNames.ADMIN_SEARCH_KEY,
                instructor.getEmail()};
        SearchCoursesAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchCoursesData response = (SearchCoursesData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(s -> s.getEmail().equals(instructor.getEmail()))
                .findAny()
                .isPresent());
    }

    @Test
    public void execute_searchNameInstructor_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] {Const.ParamsNames.ADMIN_SEARCH_KEY,
                instructor.getName()};
        SearchCoursesAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchCoursesData response = (SearchCoursesData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(s -> s.getEmail().equals(instructor.getEmail()))
                .findAny()
                .isPresent());
    }

    @Test
    public void execute_searchGoogleIdInstructor_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] {Const.ParamsNames.ADMIN_SEARCH_KEY,
                instructor.getGoogleId()};
        SearchCoursesAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchCoursesData response = (SearchCoursesData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(s -> s.getEmail().equals(instructor.getEmail()))
                .findAny()
                .isPresent());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }
}

