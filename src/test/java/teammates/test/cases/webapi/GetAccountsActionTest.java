package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetAccountsAction;
import teammates.ui.webapi.action.JsonResult;


/**
 * SUT: {@link GetAccountsAction}.
 */
public class GetAccountsActionTest extends BaseActionTest<GetAccountsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        AccountAttributes instructor1OfCourse1 = typicalBundle.accounts.get("instructor1OfCourse1");
        CourseAttributes course1 = typicalBundle.courses.get("typicalCourse1");

        loginAsAdmin();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("typical success case");

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.getGoogleId(),
        };

        GetAccountsAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        GetAccountsAction.AccountInfoData response = (GetAccountsAction.AccountInfoData) r.getOutput();

        assertEquals(response.getAccountInfo().getGoogleId(), instructor1OfCourse1.getGoogleId());
        assertEquals(response.getAccountInfo().getName(), instructor1OfCourse1.getName());
        assertEquals(response.getAccountInfo().getEmail(), instructor1OfCourse1.getEmail());
        assertEquals(response.getAccountInfo().getInstitute(), instructor1OfCourse1.getInstitute());
        assertTrue(response.getAccountInfo().isInstructor);

        assertTrue(response.getStudentCourses().isEmpty());
        assertEquals(1, response.getInstructorCourses().size());

        CourseAttributes actualCourse = response.getInstructorCourses().get(0);
        assertEquals(actualCourse.getId(), course1.getId());
        assertEquals(actualCourse.getName(), course1.getName());
        assertEquals(actualCourse.getTimeZone(), course1.getTimeZone());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
