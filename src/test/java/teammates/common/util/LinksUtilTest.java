package teammates.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

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
    private static final UUID SAMPLE_MASQUERADE_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final String SAMPLE_REG_KEY = "sampleRegKey";

    private MockedStatic<Config> mockConfig;

    @BeforeMethod
    void setUp() {
        mockConfig = mockStatic(Config.class, Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS));
        mockConfig.when(() -> Config.getFrontEndAppUrl(Mockito.anyString()))
                .thenAnswer(inv -> new AppUrl(TEST_BASE_URL + inv.getArgument(0, String.class)));
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
        String expected = TEST_BASE_URL + Const.WebPageURIs.SESSION_SUBMISSION_PAGE
                + "?fsid=" + SAMPLE_SESSION_ID + "&key=" + SAMPLE_REG_KEY;
        assertEquals(expected, LinksUtil.getStudentSessionSubmitUrl(SAMPLE_SESSION_ID, SAMPLE_REG_KEY));
    }

    @Test
    public void getInstructorSessionSubmitUrl_instructorUser_returnsAbsoluteUrlWithInstructorEntityType() {
        String expected = TEST_BASE_URL + Const.WebPageURIs.SESSION_SUBMISSION_PAGE
                + "?fsid=" + SAMPLE_SESSION_ID + "&key=" + SAMPLE_REG_KEY + "&entitytype=instructor";
        assertEquals(expected, LinksUtil.getInstructorSessionSubmitUrl(SAMPLE_SESSION_ID, SAMPLE_REG_KEY));
    }

    @Test
    public void getStudentSessionResultsUrl_studentUser_returnsCorrectAbsoluteUrl() {
        String expected = TEST_BASE_URL + Const.WebPageURIs.SESSION_RESULTS_PAGE
                + "?fsid=" + SAMPLE_SESSION_ID + "&key=" + SAMPLE_REG_KEY;
        assertEquals(expected, LinksUtil.getStudentSessionResultsUrl(SAMPLE_SESSION_ID, SAMPLE_REG_KEY));
    }

    @Test
    public void getInstructorSessionResultsUrl_instructorUser_returnsAbsoluteUrlWithInstructorEntityType() {
        String expected = TEST_BASE_URL + Const.WebPageURIs.SESSION_RESULTS_PAGE
                + "?fsid=" + SAMPLE_SESSION_ID + "&key=" + SAMPLE_REG_KEY + "&entitytype=instructor";
        assertEquals(expected, LinksUtil.getInstructorSessionResultsUrl(SAMPLE_SESSION_ID, SAMPLE_REG_KEY));
    }

    @Test
    public void getInstructorSessionEditUrl_validSessionId_returnsCorrectAbsoluteUrl() {
        String expected = TEST_BASE_URL + Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE
                + "?fsid=" + SAMPLE_SESSION_ID;
        assertEquals(expected, LinksUtil.getInstructorSessionEditUrl(SAMPLE_SESSION_ID));
    }

    @Test
    public void getInstructorSessionReportUrl_validSessionId_returnsCorrectAbsoluteUrl() {
        String expected = TEST_BASE_URL + Const.WebPageURIs.INSTRUCTOR_SESSION_REPORT_PAGE
                + "?fsid=" + SAMPLE_SESSION_ID;
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
                + "?key=" + SAMPLE_REG_KEY + "&entitytype=student";
        assertEquals(expected, LinksUtil.getStudentCourseJoinUrl(SAMPLE_REG_KEY));
    }

    @Test
    public void getInstructorCourseJoinUrl_instructorUser_returnsAbsoluteUrlWithInstructorEntityType() {
        String expected = TEST_BASE_URL + Const.WebPageURIs.JOIN_PAGE
                + "?key=" + SAMPLE_REG_KEY + "&entitytype=instructor";
        assertEquals(expected, LinksUtil.getInstructorCourseJoinUrl(SAMPLE_REG_KEY));
    }

    @Test
    public void getInstructorWelcomeUrl_accountVerificationRequest_returnsAbsoluteUrlWithAccountVerificationRequestId() {
        UUID sampleAccountVerificationRequestId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        String expected = TEST_BASE_URL + Const.WebPageURIs.INSTRUCTOR_WELCOME_PAGE
                + "/" + sampleAccountVerificationRequestId;
        assertEquals(expected, LinksUtil.getInstructorWelcomeUrl(sampleAccountVerificationRequestId));
    }

    // -------------------------------------------------------------------------
    // Relative URLs
    // -------------------------------------------------------------------------

    @Test
    public void getInstructorHomePageRelativeUrl_withMasqueradeId_returnsRelativeUrl() {
        String expected = Const.WebPageURIs.INSTRUCTOR_HOME_PAGE
                + "?masqueradeaccountid=" + SAMPLE_MASQUERADE_ID;
        assertEquals(expected, LinksUtil.getInstructorHomePageRelativeUrl(SAMPLE_MASQUERADE_ID));
    }
}
