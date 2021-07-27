package teammates.ui.servlets;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.test.MockHttpServletResponse;

/**
 * SUT: {@link WebPageServlet}.
 */
public class WebPageServletTest extends BaseTestCase {

    @Test
    public void allTests() throws Exception {

        // Nothing to test; just make sure that the response is 200

        WebPageServlet servlet = new WebPageServlet();
        MockHttpServletRequest mockRequest = new MockHttpServletRequest(HttpGet.METHOD_NAME, "http://localhost:4200");
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        servlet.doGet(mockRequest, mockResponse);

        assertEquals(HttpStatus.SC_OK, mockResponse.getStatus());

    }

}
