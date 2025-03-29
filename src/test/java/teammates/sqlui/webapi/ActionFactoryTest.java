package teammates.sqlui.webapi;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.ui.webapi.Action;
import teammates.ui.webapi.ActionFactory;
import teammates.ui.webapi.ActionMappingException;
import teammates.ui.webapi.GetAuthInfoAction;

/**
 * SUT: {@link ActionFactory}.
 */
public class ActionFactoryTest extends BaseTestCase {

    @Test
    void testGetAction_existingAction_success() throws Exception {
        MockHttpServletRequest existingActionServletRequest = new MockHttpServletRequest(
                HttpGet.METHOD_NAME, Const.ResourceURIs.AUTH);
        existingActionServletRequest.addHeader(Const.HeaderNames.BACKDOOR_KEY, Config.BACKDOOR_KEY);
        Action existingAction = ActionFactory.getAction(existingActionServletRequest, HttpGet.METHOD_NAME);
        assertTrue(existingAction instanceof GetAuthInfoAction);
    }

    @Test
    void testGetAction_nonExistentAction_throwsActionMappingException() {
        MockHttpServletRequest nonExistentActionServletRequest = new MockHttpServletRequest(
                HttpGet.METHOD_NAME, "/blahblahblah");
        nonExistentActionServletRequest.addHeader(Const.HeaderNames.BACKDOOR_KEY, Config.BACKDOOR_KEY);
        ActionMappingException nonExistentActionException = assertThrows(ActionMappingException.class,
                () -> ActionFactory.getAction(nonExistentActionServletRequest, HttpGet.METHOD_NAME));
        assertEquals("Resource with URI /blahblahblah is not found.", nonExistentActionException.getMessage());
    }

    @Test
    void testGetAction_methodDoesNotExistOnAction_throwsActionMappingException() {
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
