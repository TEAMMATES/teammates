package teammates.it.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.test.TestProperties;
import teammates.ui.output.InstructorsData;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.SearchInstructorsAction;

/**
 * SUT: {@link SearchInstructorsAction}.
 */
public class SearchInstructorsActionIT extends BaseActionIT<SearchInstructorsAction> {

    private final Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        putDocuments(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_INSTRUCTORS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void testExecute() {
        // See test cases below.
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        loginAsAdmin();
        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_searchCourseId_shouldSucceed() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, instructor.getCourseId() };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(i -> i.getName().equals(instructor.getName()))
                .findAny()
                .isPresent());
    }

    @Test
    protected void testExecute_searchDisplayedName_shouldSucceed() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, instructor.getDisplayName() };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(i -> i.getName().equals(instructor.getName()))
                .findAny()
                .isPresent());
    }

    @Test
    protected void testExecute_searchEmail_shouldSucceed() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, instructor.getEmail() };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(i -> i.getName().equals(instructor.getName()))
                .findAny()
                .isPresent());
        assertTrue(response.getInstructors().get(0).getKey() != null);
        assertTrue(response.getInstructors().get(0).getInstitute() != null);
    }

    @Test
    protected void testExecute_searchGoogleId_shouldSucceed() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, instructor.getGoogleId() };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(i -> i.getName().equals(instructor.getName()))
                .findAny()
                .isPresent());
        assertTrue(response.getInstructors().get(0).getKey() != null);
        assertTrue(response.getInstructors().get(0).getInstitute() != null);
    }

    @Test
    protected void testExecute_searchName_shouldSucceed() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, instructor.getName() };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(i -> i.getName().equals(instructor.getName()))
                .findAny()
                .isPresent());
        assertTrue(response.getInstructors().get(0).getKey() != null);
        assertTrue(response.getInstructors().get(0).getInstitute() != null);
    }

    @Test
    protected void testExecute_searchNoMatch_shouldBeEmpty() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, "noMatch" };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertEquals(0, response.getInstructors().size());
    }

    @Test
    public void testExecute_noSearchService_shouldReturn501() {
        if (TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] params = new String[] {
                Const.ParamsNames.SEARCH_KEY, "anything",
        };
        SearchInstructorsAction a = getAction(params);
        JsonResult result = getJsonResult(a, HttpStatus.SC_NOT_IMPLEMENTED);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals("Full-text search is not available.", output.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }

}
