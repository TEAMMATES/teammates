package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.Cookie;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.UserInfo;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;
import teammates.ui.output.AuthInfo;

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
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {

        ______TS("Normal case: No logged in user");

        gaeSimulation.logoutUser();

        GetAuthInfoAction a = getAction();
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        AuthInfo output = (AuthInfo) r.getOutput();
        assertEquals(gateKeeper.getLoginUrl(Const.WebPageURIs.STUDENT_HOME_PAGE), output.getStudentLoginUrl());
        assertEquals(gateKeeper.getLoginUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE), output.getInstructorLoginUrl());
        assertEquals(gateKeeper.getLoginUrl(Const.WebPageURIs.ADMIN_HOME_PAGE), output.getAdminLoginUrl());
        assertNull(output.getUser());
        assertNull(output.getInstitute());
        assertFalse(output.isMasquerade());

        ______TS("Normal case: No logged in user, has nextUrl parameter");

        gaeSimulation.logoutUser();
        String nextUrl = "/web/join";

        a = getAction(new String[] { "nextUrl", nextUrl });
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (AuthInfo) r.getOutput();
        assertEquals(gateKeeper.getLoginUrl(nextUrl), output.getStudentLoginUrl());
        assertEquals(gateKeeper.getLoginUrl(nextUrl), output.getInstructorLoginUrl());
        assertEquals(gateKeeper.getLoginUrl(nextUrl), output.getAdminLoginUrl());
        assertNull(output.getUser());
        assertNull(output.getInstitute());
        assertFalse(output.isMasquerade());

        ______TS("Normal case: With logged in user");

        loginAsInstructor("idOfInstructor1OfCourse1");

        a = getAction();
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (AuthInfo) r.getOutput();
        assertNull(output.getStudentLoginUrl());
        assertNull(output.getInstructorLoginUrl());
        assertNull(output.getAdminLoginUrl());
        assertFalse(output.isMasquerade());

        UserInfo user = output.getUser();
        assertFalse(user.isAdmin);
        assertTrue(user.isInstructor);
        assertFalse(user.isStudent);
        assertEquals("idOfInstructor1OfCourse1", user.id);

        assertEquals("TEAMMATES Test Institute 1", output.getInstitute());

        ______TS("Normal case: Admin masquerading as user");

        loginAsAdmin();

        a = getAction(new String[] {
                Const.ParamsNames.USER_ID, "idOfInstructor1OfCourse1",
        });
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (AuthInfo) r.getOutput();
        assertNull(output.getStudentLoginUrl());
        assertNull(output.getInstructorLoginUrl());
        assertNull(output.getAdminLoginUrl());
        assertTrue(output.isMasquerade());

        assertEquals("TEAMMATES Test Institute 1", output.getInstitute());

        user = output.getUser();
        assertFalse(user.isAdmin);
        assertTrue(user.isInstructor);
        assertFalse(user.isStudent);
        assertEquals("idOfInstructor1OfCourse1", user.id);

        ______TS("Normal case: Logged in unregistered user");
        loginAsUnregistered("unregisteredId");

        a = getAction();
        r = getJsonResult(a);
        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (AuthInfo) r.getOutput();
        assertNull(output.getStudentLoginUrl());
        assertNull(output.getInstructorLoginUrl());
        assertNull(output.getAdminLoginUrl());
        assertFalse(output.isMasquerade());

        user = output.getUser();
        assertFalse(user.isAdmin);
        assertFalse(user.isInstructor);
        assertFalse(user.isStudent);
        assertEquals("unregisteredId", user.id);

        assertNull(output.getInstitute());

        ______TS("Failure case: Non-admin cannot masquerade");

        loginAsInstructor("idOfInstructor1OfCourse1");

        assertThrows(UnauthorizedAccessException.class, () -> getAction(new String[] {
                Const.ParamsNames.USER_ID, "idOfAnotherInstructor",
        }));
    }

    @Test
    public void testExecute_addCsrfTokenCookies_shouldAddToResponseAccordingToExistingCsrfToken() {

        String expectedCsrfToken = StringHelper.encrypt("1234");
        String[] emptyParams = new String[] {};

        ______TS("No logged in user");

        gaeSimulation.logoutUser();

        GetAuthInfoAction a = getAction(emptyParams);
        JsonResult r = getJsonResult(a);

        assertEquals(expectedCsrfToken, r.getCookies().get(0).getValue());

        ______TS("User logged in with fake csrf token");

        loginAsInstructor("idOfInstructor1OfCourse1");

        Cookie cookieToAdd = new Cookie(Const.CsrfConfig.TOKEN_COOKIE_NAME, "someFakeCsrfToken");

        a = getActionWithCookie(new ArrayList<>(Arrays.asList(cookieToAdd)), emptyParams);
        r = getJsonResult(a);

        assertEquals(expectedCsrfToken, r.getCookies().get(0).getValue());

        ______TS("User logged in with non existing csrf token");

        loginAsInstructor("idOfInstructor1OfCourse1");

        a = getAction(emptyParams);
        r = getJsonResult(a);

        assertEquals(expectedCsrfToken, r.getCookies().get(0).getValue());

        ______TS("User logged in with matched CSRF token cookies");

        loginAsInstructor("idOfInstructor1OfCourse1");

        cookieToAdd = new Cookie(Const.CsrfConfig.TOKEN_COOKIE_NAME,
                StringHelper.encrypt("1234"));

        a = getActionWithCookie(new ArrayList<>(Arrays.asList(cookieToAdd)), emptyParams);
        r = getJsonResult(a);

        assertEquals(0, r.getCookies().size());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }

}
