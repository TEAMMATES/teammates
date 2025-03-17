package teammates.sqlui.webapi;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.Cookie;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletResponse;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link JsonResult}.
 */
public class JsonResultTest extends BaseTestCase {
    private static final String MESSAGE = "output message";
    private static final String EMPTY_MESSAGE = "";
    private final MessageOutput messageOutput = new MessageOutput(MESSAGE);
    private final MessageOutput emptyMessageOutput = new MessageOutput(EMPTY_MESSAGE);
    private final List<Cookie> cookies = new ArrayList<>();
    private final List<Cookie> emptyCookies = new ArrayList<>();
    private final Cookie testCookie = new Cookie("cookieName", "cookieValue");

    @Test
    public void testExecute_sendStringMessageReceivesMessage_shouldSucceed() throws Exception {
        JsonResult result = new JsonResult(MESSAGE);
        executeSuccessfulJsonResultTest(result);
    }

    @Test
    public void testExecute_sendMessageOutputReceivesMessage_shouldSucceed() throws Exception {
        JsonResult result = new JsonResult(messageOutput);
        executeSuccessfulJsonResultTest(result);
    }

    private void executeSuccessfulJsonResultTest(JsonResult result) throws Exception {
        MessageOutput output = (MessageOutput) result.getOutput();
        assertEquals(MESSAGE, output.getMessage());
        assertEquals(0, result.getCookies().size());

        MockHttpServletResponse resp = new MockHttpServletResponse();
        result.send(resp);
        assertEquals(0, resp.getCookies().size());
    }

    @Test
    public void testExecute_sendMessageOutputCookieReceiveMessageAndCookies_shouldSucceed() throws Exception {
        cookies.add(testCookie);
        JsonResult result = new JsonResult(messageOutput, cookies);

        MessageOutput output = (MessageOutput) result.getOutput();
        assertEquals(MESSAGE, output.getMessage());
        assertEquals(1, result.getCookies().size());

        MockHttpServletResponse resp = new MockHttpServletResponse();
        result.send(resp);
        assertEquals(1, resp.getCookies().size());
    }

    @Test
    public void testExecute_sendNullMessage_shouldGetNullAndFailResponse() {
        JsonResult result = new JsonResult((String) null);
        MessageOutput output = (MessageOutput) result.getOutput();
        assertEquals(null, output.getMessage());

        result = new JsonResult((MessageOutput) null);
        output = (MessageOutput) result.getOutput();
        assertEquals(null, output);

        JsonResult nullTestJsonResult = new JsonResult((MessageOutput) null, (List<Cookie>) null);
        output = (MessageOutput) nullTestJsonResult.getOutput();
        assertEquals(null, output);
        assertEquals(null, nullTestJsonResult.getCookies());

        MockHttpServletResponse resp = new MockHttpServletResponse();
        assertThrows(NullPointerException.class, () -> nullTestJsonResult.send(resp));
    }

    @Test
    public void testExecute_emptyMessageAndCookie_shouldSucceed() throws Exception {
        JsonResult result = new JsonResult(emptyMessageOutput, emptyCookies);
        MessageOutput output = (MessageOutput) result.getOutput();
        assertEquals(EMPTY_MESSAGE, output.getMessage());
        assertEquals(0, result.getCookies().size());

        MockHttpServletResponse resp = new MockHttpServletResponse();
        result.send(resp);
        assertEquals(0, resp.getCookies().size());
    }
}
