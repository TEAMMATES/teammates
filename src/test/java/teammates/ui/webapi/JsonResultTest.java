package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletResponse;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link JsonResult}.
 */
public class JsonResultTest extends BaseTestCase {

    @Test
    public void testConstructorAndSendResponse() throws Exception {

        ______TS("json result with output message only");

        JsonResult result = new JsonResult("output message");

        MessageOutput output = (MessageOutput) result.getOutput();
        assertEquals("output message", output.getMessage());
        assertEquals(0, result.getCookies().size());

        MockHttpServletResponse resp = new MockHttpServletResponse();
        result.send(resp);
        assertEquals(HttpStatus.SC_OK, resp.getStatus());
        assertEquals(0, resp.getCookies().size());

        ______TS("json result with output message and cookies");

        List<Cookie> cookies = new ArrayList<>();
        cookies.add(new Cookie("cookieName", "cookieValue"));
        result = new JsonResult(new MessageOutput("output message"), cookies);

        output = (MessageOutput) result.getOutput();
        assertEquals("output message", output.getMessage());
        assertEquals(1, result.getCookies().size());

        MockHttpServletResponse respWithCookie = new MockHttpServletResponse();
        result.send(respWithCookie);
        assertEquals(HttpStatus.SC_OK, respWithCookie.getStatus());
        assertEquals(1, respWithCookie.getCookies().size());
    }
}
