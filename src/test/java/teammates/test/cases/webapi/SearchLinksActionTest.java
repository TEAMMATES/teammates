package teammates.test.cases.webapi;

import org.junit.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.SearchLinksAction;
import teammates.ui.webapi.output.SearchLinksResult;

/**
 * SUT:{@link SearchLinksAction}.
 */
public class SearchLinksActionTest extends BaseActionTest<SearchLinksAction> {
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
        return Const.ResourceURIs.LINKS_SEARCH;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void testExecute() {
        // TODO: Remove
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
        SearchLinksAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchLinksResult response = (SearchLinksResult) result.getOutput();
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
        SearchLinksAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchLinksResult response = (SearchLinksResult) result.getOutput();
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
        SearchLinksAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchLinksResult response = (SearchLinksResult) result.getOutput();
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
        SearchLinksAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchLinksResult response = (SearchLinksResult) result.getOutput();
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
        SearchLinksAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchLinksResult response = (SearchLinksResult) result.getOutput();
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
        SearchLinksAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchLinksResult response = (SearchLinksResult) result.getOutput();
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
        SearchLinksAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchLinksResult response = (SearchLinksResult) result.getOutput();
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
        SearchLinksAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchLinksResult response = (SearchLinksResult) result.getOutput();
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
        SearchLinksAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchLinksResult response = (SearchLinksResult) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(s -> s.getEmail().equals(instructor.getEmail()))
                .findAny()
                .isPresent());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAccessibleForAdmin();
    }
}
