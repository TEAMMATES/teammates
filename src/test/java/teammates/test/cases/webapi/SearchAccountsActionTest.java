package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.SearchAccountsAction;
import teammates.ui.webapi.output.AdminSearchResultData;

/**
 * SUT: {@link SearchAccountsAction}.
 */
public class SearchAccountsActionTest extends BaseActionTest<SearchAccountsAction> {

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
        putDocuments(dataBundle);
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNTS_SEARCH;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes acc = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsAdmin();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case: search google id");

        String[] googleIdParams = new String[] {
                Const.ParamsNames.ADMIN_SEARCH_KEY, acc.getGoogleId(),
        };

        SearchAccountsAction a = getAction(googleIdParams);
        JsonResult result = getJsonResult(a);
        AdminSearchResultData response = (AdminSearchResultData) result.getOutput();

        assertTrue(response.getStudents().isEmpty());
        assertEquals(1, response.getInstructors().size());

        ______TS("Typical case: search course id");
        String[] courseIdParams = new String[] {
                Const.ParamsNames.ADMIN_SEARCH_KEY, acc.getCourseId(),
        };

        a = getAction(courseIdParams);
        result = getJsonResult(a);
        response = (AdminSearchResultData) result.getOutput();

        assertEquals(5, response.getInstructors().size());
        assertEquals(5, response.getStudents().size());

        ______TS("Typical case: full text search accounts that contains 'Course2'");
        String[] accNameParams = new String[] {
                Const.ParamsNames.ADMIN_SEARCH_KEY, "Course2",
        };

        a = getAction(accNameParams);
        result = getJsonResult(a);
        response = (AdminSearchResultData) result.getOutput();

        assertEquals(3, response.getInstructors().size());
        assertEquals(2, response.getStudents().size());

        ______TS("Typical case: search email");
        String[] emailParams = new String[] {
                Const.ParamsNames.ADMIN_SEARCH_KEY, "@course1.tmt",
        };

        a = getAction(emailParams);
        result = getJsonResult(a);
        response = (AdminSearchResultData) result.getOutput();

        assertEquals(4, response.getInstructors().size());
        assertEquals(0, response.getStudents().size());

        ______TS("Typical case: search has no match");
        String[] noMatchParams = new String[] {
                Const.ParamsNames.ADMIN_SEARCH_KEY, "nomatch",
        };

        a = getAction(noMatchParams);
        result = getJsonResult(a);
        response = (AdminSearchResultData) result.getOutput();

        assertEquals(0, response.getInstructors().size());
        assertEquals(0, response.getStudents().size());

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
