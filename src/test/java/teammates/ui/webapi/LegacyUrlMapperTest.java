package teammates.ui.webapi;

import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.test.MockHttpServletResponse;

/**
 * SUT: {@link LegacyUrlMapper}.
 */
public class LegacyUrlMapperTest extends BaseTestCase {

    private static final LegacyUrlMapper MAPPER = new LegacyUrlMapper();

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;

    private void setupMocks(String requestUrl) {
        mockRequest = new MockHttpServletRequest(HttpGet.METHOD_NAME, requestUrl);
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    public void allTests() throws Exception {

        ______TS("Legacy instructor course join URL");

        setupMocks(Const.LegacyURIs.INSTRUCTOR_COURSE_JOIN);
        mockRequest.addParam(Const.ParamsNames.REGKEY, "regkey");

        MAPPER.doGet(mockRequest, mockResponse);

        String newInstructorJoinUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey("regkey")
                .withEntityType(Const.EntityType.INSTRUCTOR)
                .toString();
        assertEquals(newInstructorJoinUrl, mockResponse.getRedirectUrl());

        ______TS("Legacy student course join URL");

        setupMocks(Const.LegacyURIs.STUDENT_COURSE_JOIN_NEW);
        mockRequest.addParam(Const.ParamsNames.REGKEY, "regkey");

        MAPPER.doGet(mockRequest, mockResponse);

        String newStudentJoinUrl = Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey("regkey")
                .withEntityType(Const.EntityType.STUDENT)
                .toString();
        assertEquals(newStudentJoinUrl, mockResponse.getRedirectUrl());

        ______TS("Invalid legacy URL: redirect to home page");

        setupMocks("/page/invalidPage");

        MAPPER.doGet(mockRequest, mockResponse);

        assertEquals("/", mockResponse.getRedirectUrl());

    }

}
