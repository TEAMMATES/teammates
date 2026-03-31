package teammates.sqlui.webapi;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.ui.webapi.Action;
import teammates.ui.webapi.ActionFactory;
import teammates.ui.webapi.ActionMappingException;
import teammates.ui.webapi.GetAuthInfoAction;
import teammates.ui.webapi.GetEmailTemplateAction;
import teammates.ui.webapi.GetEmailTemplatesAction;
import teammates.ui.webapi.UpdateEmailTemplateAction;

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
        assertTrue(("Method [" + HttpPost.METHOD_NAME + "] is not allowed for URI " + Const.ResourceURIs.AUTH + ".")
                .equals(nonExistentMethodOnActionException.getMessage()));
    }

    @Test
    void testGetAction_getEmailTemplate_success() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(
                HttpGet.METHOD_NAME, Const.ResourceURIs.EMAIL_TEMPLATE);
        request.addHeader(Const.HeaderNames.BACKDOOR_KEY, Config.BACKDOOR_KEY);
        Action action = ActionFactory.getAction(request, HttpGet.METHOD_NAME);
        assertTrue(action instanceof GetEmailTemplateAction);
    }

    @Test
    void testGetAction_putEmailTemplate_success() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(
                HttpPut.METHOD_NAME, Const.ResourceURIs.EMAIL_TEMPLATE);
        request.addHeader(Const.HeaderNames.BACKDOOR_KEY, Config.BACKDOOR_KEY);
        Action action = ActionFactory.getAction(request, HttpPut.METHOD_NAME);
        assertTrue(action instanceof UpdateEmailTemplateAction);
    }

    @Test
    void testGetAction_getEmailTemplates_success() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(
                HttpGet.METHOD_NAME, Const.ResourceURIs.EMAIL_TEMPLATES);
        request.addHeader(Const.HeaderNames.BACKDOOR_KEY, Config.BACKDOOR_KEY);
        Action action = ActionFactory.getAction(request, HttpGet.METHOD_NAME);
        assertTrue(action instanceof GetEmailTemplatesAction);
    }
}
