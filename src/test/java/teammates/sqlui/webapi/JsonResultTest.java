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
    public void testConstructor_sendStringMessageReceivesMessage_shouldSucceed() throws Exception {
        JsonResult result = new JsonResult(MESSAGE);
        verifyJsonResult(result, MESSAGE, 0, 0);
    }

    @Test
    public void testConstructor_sendMessageOutputReceivesMessage_shouldSucceed() throws Exception {
        JsonResult result = new JsonResult(messageOutput);
        verifyJsonResult(result, MESSAGE, 0, 0);
    }

    @Test
    public void testConstructor_sendMessageOutputCookieReceiveMessageAndCookies_shouldSucceed() throws Exception {
        cookies.add(testCookie);
        JsonResult result = new JsonResult(messageOutput, cookies);
        verifyJsonResult(result, MESSAGE, 1, 1);
    }

    @Test
    public void testConstructor_emptyMessageAndCookie_shouldSucceed() throws Exception {
        JsonResult result = new JsonResult(emptyMessageOutput, emptyCookies);
        verifyJsonResult(result, EMPTY_MESSAGE, 0, 0);
    }

    @Test
    public void testConstructor_sendNullMessage_shouldGetNullAndFailResponse() {
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

    private void verifyJsonResult(
            JsonResult result,
            String expectedMessage,
            int expectedMessageCookieSize,
            int expectedResponseCookieSize) throws Exception {
        MessageOutput output = (MessageOutput) result.getOutput();
        assertEquals(expectedMessage, output.getMessage());
        assertEquals(expectedMessageCookieSize, result.getCookies().size());

        MockHttpServletResponse resp = new MockHttpServletResponse();
        result.send(resp);
        assertEquals(expectedResponseCookieSize, resp.getCookies().size());
    }
}
