package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import teammates.test.cases.BaseTestCase;
import teammates.common.util.Const;
import teammates.test.driver.MockHttpServletRequest;
import teammates.ui.webapi.action.Action;
import teammates.ui.webapi.action.ActionFactory;
import teammates.ui.webapi.action.GetAuthInfoAction;

public class ActionFactoryTest extends BaseTestCase {

    @Test
    public void testGetAction() throws Exception {
        ActionFactory actionFactory = new ActionFactory();
        
        ______TS("Action exists and is retrieved");

        MockHttpServletRequest existingActionServletRequest = new MockHttpServletRequest(
            HttpGet.METHOD_NAME, Const.ResourceURIs.URI_PREFIX + Const.ResourceURIs.AUTH);
        Action existingAction = actionFactory.getAction(existingActionServletRequest, HttpGet.METHOD_NAME);
        assertTrue(existingAction instanceof GetAuthInfoAction);
    }
}