package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.TestProperties;
import teammates.ui.output.InstructorsData;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link SearchInstructorsAction}.
 */
public class SearchInstructorsActionTest extends BaseActionTest<SearchInstructorsAction> {

    private final InstructorAttributes acc = typicalBundle.instructors.get("instructor1OfCourse1");

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
        putDocuments(dataBundle);
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
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, acc.getCourseId() };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(i -> i.getName().equals(acc.getName()))
                .findAny()
                .isPresent());
    }

    @Test
    protected void testExecute_searchDisplayedName_shouldSucceed() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, acc.getDisplayedName() };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(i -> i.getName().equals(acc.getName()))
                .findAny()
                .isPresent());
    }

    @Test
    protected void testExecute_searchEmail_shouldSucceed() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, acc.getEmail() };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(i -> i.getName().equals(acc.getName()))
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
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, acc.getGoogleId() };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(i -> i.getName().equals(acc.getName()))
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
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, acc.getName() };
        SearchInstructorsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        InstructorsData response = (InstructorsData) result.getOutput();
        assertTrue(response.getInstructors().stream()
                .filter(i -> i.getName().equals(acc.getName()))
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
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
