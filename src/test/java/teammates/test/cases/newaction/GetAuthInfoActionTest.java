package teammates.test.cases.newaction;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Test;

import teammates.common.datatransfer.UserInfo;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;
import teammates.ui.newcontroller.GetAuthInfoAction;
import teammates.ui.newcontroller.JsonResult;

/**
 * SUT: {@link GetAuthInfoAction}.
 */
public class GetAuthInfoActionTest extends BaseActionTest<GetAuthInfoAction> {

    private GateKeeper gateKeeper = new GateKeeper();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.AUTH;
    }

    @Override
    protected String getRequestMethod() {
        return HttpGet.METHOD_NAME;
    }

    @Override
    @Test
    protected void testExecute() {

        ______TS("Normal case: No logged in user");

        gaeSimulation.logoutUser();

        GetAuthInfoAction a = getAction();
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        Map<String, Object> output = (Map<String, Object>) r.getOutput();
        assertEquals(gateKeeper.getLoginUrl(Const.WebPageURIs.STUDENT_HOME_PAGE),
                output.get("studentLoginUrl"));
        assertEquals(gateKeeper.getLoginUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE),
                output.get("instructorLoginUrl"));
        assertEquals(gateKeeper.getLoginUrl(Const.WebPageURIs.ADMIN_HOME_PAGE),
                output.get("adminLoginUrl"));
        assertNull(output.get("user"));
        assertNull(output.get("logoutUrl"));

        ______TS("Normal case: With logged in user");

        loginAsInstructor("idOfInstructor1OfCourse1");

        a = getAction();
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (Map<String, Object>) r.getOutput();
        assertNull(output.get("studentLoginUrl"));
        assertNull(output.get("instructorLoginUrl"));
        assertNull(output.get("adminLoginUrl"));
        assertEquals(gateKeeper.getLogoutUrl("/web"),
                output.get("logoutUrl"));

        UserInfo user = (UserInfo) output.get("user");
        assertFalse(user.isAdmin);
        assertTrue(user.isInstructor);
        assertFalse(user.isStudent);
        assertEquals("idOfInstructor1OfCourse1", user.id);

        // TODO test CSRF token cookies

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }

}
