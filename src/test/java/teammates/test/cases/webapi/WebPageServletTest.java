package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Test;

import teammates.test.cases.BaseTestCase;
import teammates.test.driver.MockHttpServletRequest;
import teammates.test.driver.MockHttpServletResponse;
import teammates.ui.webapi.WebPageServlet;

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
