package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import org.testng.annotations.Test;

import teammates.common.datatransfer.UserInfo;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.AuthInfo;

/**
 * SUT: {@link GetAuthInfoAction}.
 */
public class GetAuthInfoActionTest extends BaseActionTest<GetAuthInfoAction> {

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

        logoutUser();

        GetAuthInfoAction a = getAction();
        JsonResult r = getJsonResult(a);

        AuthInfo output = (AuthInfo) r.getOutput();
        assertEquals(a.createLoginUrl("", Const.WebPageURIs.STUDENT_HOME_PAGE), output.getStudentLoginUrl());
        assertEquals(a.createLoginUrl("", Const.WebPageURIs.INSTRUCTOR_HOME_PAGE), output.getInstructorLoginUrl());
        assertEquals(a.createLoginUrl("", Const.WebPageURIs.ADMIN_HOME_PAGE), output.getAdminLoginUrl());
        assertEquals(a.createLoginUrl("", Const.WebPageURIs.MAINTAINER_HOME_PAGE), output.getMaintainerLoginUrl());
        assertNull(output.getUser());
        assertFalse(output.isMasquerade());

        ______TS("Normal case: No logged in user, has nextUrl parameter");

        logoutUser();
        String nextUrl = "/web/join";

        a = getAction(new String[] { "nextUrl", nextUrl });
        r = getJsonResult(a);

        output = (AuthInfo) r.getOutput();
        assertEquals(a.createLoginUrl("", nextUrl), output.getStudentLoginUrl());
        assertEquals(a.createLoginUrl("", nextUrl), output.getInstructorLoginUrl());
        assertEquals(a.createLoginUrl("", nextUrl), output.getAdminLoginUrl());
        assertEquals(a.createLoginUrl("", nextUrl), output.getMaintainerLoginUrl());
        assertNull(output.getUser());
        assertFalse(output.isMasquerade());

        ______TS("Normal case: With logged in user");

        loginAsInstructor("idOfInstructor1OfCourse1");

        a = getAction();
        r = getJsonResult(a);

        output = (AuthInfo) r.getOutput();
        assertNull(output.getStudentLoginUrl());
        assertNull(output.getInstructorLoginUrl());
        assertNull(output.getAdminLoginUrl());
        assertNull(output.getMaintainerLoginUrl());
        assertFalse(output.isMasquerade());

        UserInfo user = output.getUser();
        assertFalse(user.isAdmin);
        assertTrue(user.isInstructor);
        assertFalse(user.isStudent);
        assertEquals("idOfInstructor1OfCourse1", user.id);

        ______TS("Normal case: Admin masquerading as user");

        loginAsAdmin();

        a = getAction(new String[] {
                Const.ParamsNames.USER_ID, "idOfInstructor1OfCourse1",
        });
        r = getJsonResult(a);

        output = (AuthInfo) r.getOutput();
        assertNull(output.getStudentLoginUrl());
        assertNull(output.getInstructorLoginUrl());
        assertNull(output.getAdminLoginUrl());
        assertNull(output.getMaintainerLoginUrl());
        assertTrue(output.isMasquerade());

        user = output.getUser();
        assertFalse(user.isAdmin);
        assertTrue(user.isInstructor);
        assertFalse(user.isStudent);
        assertEquals("idOfInstructor1OfCourse1", user.id);

        ______TS("Normal case: Logged in unregistered user");
        loginAsUnregistered("unregisteredId");

        a = getAction();
        r = getJsonResult(a);

        output = (AuthInfo) r.getOutput();
        assertNull(output.getStudentLoginUrl());
        assertNull(output.getInstructorLoginUrl());
        assertNull(output.getAdminLoginUrl());
        assertNull(output.getMaintainerLoginUrl());
        assertFalse(output.isMasquerade());

        user = output.getUser();
        assertFalse(user.isAdmin);
        assertFalse(user.isInstructor);
        assertFalse(user.isStudent);
        assertEquals("unregisteredId", user.id);
    }

    @Test
    public void testExecute_addCsrfTokenCookies_shouldAddToResponseAccordingToExistingCsrfToken() {

        String expectedCsrfToken = StringHelper.encrypt("1234");
        String[] emptyParams = new String[] {};

        ______TS("No logged in user");

        logoutUser();

        GetAuthInfoAction a = getAction(emptyParams);
        JsonResult r = getJsonResult(a);

        assertEquals(expectedCsrfToken, r.getCookies().get(0).getValue());

        ______TS("User logged in with fake csrf token");

        loginAsInstructor("idOfInstructor1OfCourse1");

        Cookie cookieToAdd = new Cookie(Const.SecurityConfig.CSRF_COOKIE_NAME, "someFakeCsrfToken");

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

        cookieToAdd = new Cookie(Const.SecurityConfig.CSRF_COOKIE_NAME,
                StringHelper.encrypt("1234"));

        a = getActionWithCookie(new ArrayList<>(Arrays.asList(cookieToAdd)), emptyParams);
        r = getJsonResult(a);

        assertEquals(0, r.getCookies().size());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyUserCanAccess();

        ______TS("Failure case: Non-admin cannot masquerade");

        loginAsInstructor("idOfInstructor1OfCourse1");
        verifyCannotMasquerade("idOfAnotherInstructor");
    }

}
