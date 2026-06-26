package teammates.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import java.net.URI;
import java.util.UUID;

import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link LinksUtil}.
 */
public class LinksUtilTest extends BaseTestCase {

    private static final String TEST_BASE_URL = "http://teammates.tmt";
    private static final UUID SAMPLE_SESSION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SAMPLE_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final String SAMPLE_REG_KEY = "sampleRegKey";

    private MockedStatic<Config> mockConfig;

    @BeforeMethod
    void setUp() {
        mockConfig = mockStatic(Config.class, Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));
        mockConfig.when(() -> Config.getFrontEndAppUrl(Mockito.anyString()))
                .thenAnswer(inv -> AppUrl.fromParts(TEST_BASE_URL, inv.getArgument(0, String.class)));
    }

    @AfterMethod
    void tearDown() {
        mockConfig.close();
    }

    // -------------------------------------------------------------------------
    // Session pages
    // -------------------------------------------------------------------------

    @Test
    public void getStudentSessionSubmitUrl_studentUser_returnsCorrectAbsoluteUrl() {
        String url = LinksUtil.getStudentSessionSubmitUrl(SAMPLE_SESSION_ID, SAMPLE_USER_ID, SAMPLE_REG_KEY);
        assertEquals(TEST_BASE_URL + "/web/sessions/" + SAMPLE_SESSION_ID + "/submission",
                URI.create(url).getScheme() + "://" + URI.create(url).getAuthority() + URI.create(url).getPath());
    }

    @Test
    public void getInstructorSessionSubmitUrl_instructorUser_returnsAbsoluteUrlOnInstructorPath() {
        String expected = TEST_BASE_URL + "/web/instructor/sessions/" + SAMPLE_SESSION_ID + "/submission";
        assertEquals(expected, LinksUtil.getInstructorSessionSubmitUrl(SAMPLE_SESSION_ID));
    }

    @Test
    public void getStudentSessionResultsUrl_studentUser_returnsCorrectAbsoluteUrl() {
        String url = LinksUtil.getStudentSessionResultsUrl(SAMPLE_SESSION_ID, SAMPLE_USER_ID, SAMPLE_REG_KEY);
        assertEquals(TEST_BASE_URL + "/web/sessions/" + SAMPLE_SESSION_ID + "/result",
                URI.create(url).getScheme() + "://" + URI.create(url).getAuthority() + URI.create(url).getPath());
    }

    @Test
    public void getInstructorSessionResultsUrl_instructorUser_returnsAbsoluteUrlOnInstructorPath() {
        String expected = TEST_BASE_URL + "/web/instructor/sessions/" + SAMPLE_SESSION_ID + "/result";
        assertEquals(expected, LinksUtil.getInstructorSessionResultsUrl(SAMPLE_SESSION_ID));
    }

    @Test
    public void getInstructorSessionEditUrl_validSessionId_returnsCorrectAbsoluteUrl() {
        String expected = TEST_BASE_URL + "/web/instructor/sessions/" + SAMPLE_SESSION_ID + "/edit";
        assertEquals(expected, LinksUtil.getInstructorSessionEditUrl(SAMPLE_SESSION_ID));
    }

    @Test
    public void getInstructorSessionReportUrl_validSessionId_returnsCorrectAbsoluteUrl() {
        String expected = TEST_BASE_URL + "/web/instructor/sessions/" + SAMPLE_SESSION_ID + "/report";
        assertEquals(expected, LinksUtil.getInstructorSessionReportUrl(SAMPLE_SESSION_ID));
    }

    // -------------------------------------------------------------------------
    // Simple page URLs
    // -------------------------------------------------------------------------

    @Test
    public void getSessionLinkRecoveryUrl_returnsCorrectAbsoluteUrl() {
        String expected = TEST_BASE_URL + Const.WebPageURIs.SESSIONS_LINK_RECOVERY_PAGE;
        assertEquals(expected, LinksUtil.getSessionLinkRecoveryUrl());
    }

    @Test
    public void getHomePageUrl_returnsCorrectAbsoluteUrl() {
        String expected = TEST_BASE_URL + "/";
        assertEquals(expected, LinksUtil.getHomePageUrl());
    }

    @Test
    public void getAdminHomePageUrl_returnsCorrectAbsoluteUrl() {
        String expected = TEST_BASE_URL + Const.WebPageURIs.ADMIN_HOME_PAGE;
        assertEquals(expected, LinksUtil.getAdminHomePageUrl());
    }

    @Test
    public void getInstructorHomePageUrl_returnsCorrectAbsoluteUrl() {
        String expected = TEST_BASE_URL + Const.WebPageURIs.INSTRUCTOR_HOME_PAGE;
        assertEquals(expected, LinksUtil.getInstructorHomePageUrl());
    }

    @Test
    public void getStudentHomePageUrl_returnsCorrectAbsoluteUrl() {
        String expected = TEST_BASE_URL + Const.WebPageURIs.STUDENT_HOME_PAGE;
        assertEquals(expected, LinksUtil.getStudentHomePageUrl());
    }

    // -------------------------------------------------------------------------
    // Join / registration URLs
    // -------------------------------------------------------------------------

    @Test
    public void getStudentCourseJoinUrl_studentUser_returnsAbsoluteUrlWithStudentEntityType() {
        String expected = TEST_BASE_URL + Const.WebPageURIs.JOIN_PAGE
                + "?key=" + SAMPLE_REG_KEY + "&entityType=student";
        assertEquals(expected, LinksUtil.getStudentCourseJoinUrl(SAMPLE_REG_KEY));
    }

    @Test
    public void getInstructorCourseJoinUrl_instructorUser_returnsAbsoluteUrlWithInstructorEntityType() {
        String expected = TEST_BASE_URL + Const.WebPageURIs.JOIN_PAGE
                + "?key=" + SAMPLE_REG_KEY + "&entityType=instructor";
        assertEquals(expected, LinksUtil.getInstructorCourseJoinUrl(SAMPLE_REG_KEY));
    }

    @Test
    public void getInstructorWelcomeUrl_accountVerificationRequest_returnsAbsoluteUrlWithAccountVerificationRequestId() {
        UUID sampleAccountVerificationRequestId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        String expected = TEST_BASE_URL + "/web/instructor/welcome/" + sampleAccountVerificationRequestId;
        assertEquals(expected, LinksUtil.getInstructorWelcomeUrl(sampleAccountVerificationRequestId));
    }

}
