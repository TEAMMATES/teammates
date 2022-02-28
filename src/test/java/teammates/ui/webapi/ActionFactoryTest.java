package teammates.ui.webapi;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;

/**
 * SUT: {@link ActionFactory}.
 */
public class ActionFactoryTest extends BaseTestCase {

    @Test
    public void testGetAction() throws Exception {
        ______TS("Action exists and is retrieved");

        MockHttpServletRequest existingActionServletRequest = new MockHttpServletRequest(
                HttpGet.METHOD_NAME, Const.ResourceURIs.AUTH);
        existingActionServletRequest.addHeader(Const.HeaderNames.BACKDOOR_KEY, Config.BACKDOOR_KEY);
        Action existingAction = ActionFactory.getAction(existingActionServletRequest, HttpGet.METHOD_NAME);
        assertTrue(existingAction instanceof GetAuthInfoAction);

        ______TS("Action does not exist and ActionMappingException is thrown");

        MockHttpServletRequest nonExistentActionServletRequest = new MockHttpServletRequest(
                HttpGet.METHOD_NAME, "/blahblahblah");
        nonExistentActionServletRequest.addHeader(Const.HeaderNames.BACKDOOR_KEY, Config.BACKDOOR_KEY);
        ActionMappingException nonExistentActionException = assertThrows(ActionMappingException.class,
                () -> ActionFactory.getAction(nonExistentActionServletRequest, HttpGet.METHOD_NAME));
        assertEquals("Resource with URI /blahblahblah is not found.", nonExistentActionException.getMessage());

        ______TS("Method does not exist on action and ActionMappingException is thrown");

        MockHttpServletRequest nonExistentMethodOnActionServletRequest = new MockHttpServletRequest(
                HttpGet.METHOD_NAME, Const.ResourceURIs.AUTH);
        nonExistentMethodOnActionServletRequest.addHeader(Const.HeaderNames.BACKDOOR_KEY, Config.BACKDOOR_KEY);
        ActionMappingException nonExistentMethodOnActionException = assertThrows(ActionMappingException.class,
                () -> ActionFactory.getAction(nonExistentMethodOnActionServletRequest, HttpPost.METHOD_NAME));
        assertTrue(nonExistentMethodOnActionException.getMessage()
                .equals("Method [" + HttpPost.METHOD_NAME + "] is not allowed for URI "
                + Const.ResourceURIs.AUTH + "."));
    }
}
