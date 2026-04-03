package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;
import static teammates.ui.webapi.GetAuthInfoAction.createLoginUrl;

import java.util.ArrayList;
import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import org.testng.annotations.Test;

import teammates.common.datatransfer.UserInfo;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.sqlentity.AccountIdentity;
import teammates.ui.output.AuthInfo;
import teammates.ui.webapi.GetAuthInfoAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetAuthInfoAction}.
 */
public class GetAuthInfoActionTest extends BaseActionTest<GetAuthInfoAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.AUTH;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    void testExecute_noLoggedInUser() {
        logoutUser();

        GetAuthInfoAction a = getAction();
        JsonResult r = getJsonResult(a);

        AuthInfo output = (AuthInfo) r.getOutput();
        assertEquals(createLoginUrl("", Const.WebPageURIs.STUDENT_HOME_PAGE), output.getStudentLoginUrl());
        assertEquals(createLoginUrl("", Const.WebPageURIs.INSTRUCTOR_HOME_PAGE), output.getInstructorLoginUrl());
        assertEquals(createLoginUrl("", Const.WebPageURIs.ADMIN_HOME_PAGE), output.getAdminLoginUrl());
        assertEquals(createLoginUrl("", Const.WebPageURIs.MAINTAINER_HOME_PAGE), output.getMaintainerLoginUrl());
        assertNull(output.getUser());
        assertFalse(output.isMasquerade());
    }

    @Test
    void testExecute_noLoggedInUser_hasNextUrlParameter() {
        logoutUser();
        String nextUrl = "/web/join";

        String[] params = new String[] {
                "nextUrl", nextUrl,
        };

        GetAuthInfoAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        AuthInfo output = (AuthInfo) r.getOutput();
        assertEquals(createLoginUrl("", nextUrl), output.getStudentLoginUrl());
        assertEquals(createLoginUrl("", nextUrl), output.getInstructorLoginUrl());
        assertEquals(createLoginUrl("", nextUrl), output.getAdminLoginUrl());
        assertEquals(createLoginUrl("", nextUrl), output.getMaintainerLoginUrl());
        assertNull(output.getUser());
        assertFalse(output.isMasquerade());
    }

    @Test
    void testExecute_loggedInAsInstructor() {
        String instructorAccountId = TYPICAL_INSTRUCTOR_ACCOUNT_ID.toString();
        String instructorLoginIdentifier = "instructor@example.com";
        loginAsInstructor(instructorAccountId);
        when(mockLogic.getFirstIdentityForAccount(instructorAccountId)).thenReturn(
                new AccountIdentity("https://securetoken.google.com/project", "subject-instructor",
                        instructorLoginIdentifier, Const.LoginProviders.GOOGLE));

        GetAuthInfoAction a = getAction();
        JsonResult r = getJsonResult(a);

        AuthInfo output = (AuthInfo) r.getOutput();
        assertNull(output.getStudentLoginUrl());
        assertNull(output.getInstructorLoginUrl());
        assertNull(output.getAdminLoginUrl());
        assertNull(output.getMaintainerLoginUrl());
        assertFalse(output.isMasquerade());

        UserInfo user = output.getUser();
        assertFalse(user.isAdmin);
        assertTrue(user.isInstructor);
        assertFalse(user.isStudent);
        assertEquals(instructorAccountId, user.id);
        assertEquals(instructorLoginIdentifier, user.loginIdentifier);
    }

    @Test
    void testExecute_loggedInAsUnregisteredUser() {
        String unregisteredAccountId = TEST_UNREGISTERED_ACCOUNT_ID.toString();
        loginAsUnregistered(unregisteredAccountId);
        when(mockLogic.getFirstIdentityForAccount(unregisteredAccountId)).thenReturn(null);

        GetAuthInfoAction a = getAction();
        JsonResult r = getJsonResult(a);

        AuthInfo output = (AuthInfo) r.getOutput();
        assertNull(output.getStudentLoginUrl());
        assertNull(output.getInstructorLoginUrl());
        assertNull(output.getAdminLoginUrl());
        assertNull(output.getMaintainerLoginUrl());
        assertFalse(output.isMasquerade());

        UserInfo user = output.getUser();
        assertFalse(user.isAdmin);
        assertFalse(user.isInstructor);
        assertFalse(user.isStudent);
        assertEquals(unregisteredAccountId, user.id);
        assertEquals("", user.loginIdentifier);
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

        Cookie cookieToAdd = new Cookie(Const.SecurityConfig.CSRF_COOKIE_NAME, "someFakeCsrfToken");

        a = getActionWithCookie(new ArrayList<>(Arrays.asList(cookieToAdd)), emptyParams);
        r = getJsonResult(a);

        assertEquals(expectedCsrfToken, r.getCookies().get(0).getValue());

        ______TS("User logged in with non existing csrf token");

        loginAsInstructor(TYPICAL_INSTRUCTOR_ACCOUNT_ID.toString());

        a = getAction(emptyParams);
        r = getJsonResult(a);

        assertEquals(expectedCsrfToken, r.getCookies().get(0).getValue());

        ______TS("User logged in with matched CSRF token cookies");

        loginAsInstructor(TYPICAL_INSTRUCTOR_ACCOUNT_ID.toString());

        cookieToAdd = new Cookie(Const.SecurityConfig.CSRF_COOKIE_NAME,
                StringHelper.encrypt("1234"));

        a = getActionWithCookie(new ArrayList<>(Arrays.asList(cookieToAdd)), emptyParams);
        r = getJsonResult(a);

        assertEquals(0, r.getCookies().size());
    }

    @Test
    void testAccessControl() {
        verifyAnyUserCanAccess();
    }

}
