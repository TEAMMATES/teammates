package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertTrue;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.test.cases.BaseComponentTest;
import teammates.ui.controller.Action;
import teammates.ui.controller.ActionFactory;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

/**
 * Parent class for *ActionTest classes.
 */
public class BaseActionTest extends BaseComponentTest {

	/** 
	 * @param parameters Parameters that appear in a HttpServletRequest 
	 * received by the app.
	 * @return an {@link Action} object that matches the parameters given.
	 */
	protected Action getActionObject(String... parameters)
			throws IOException,
			MalformedURLException {
		WebRequest request = new PostMethodWebRequest(
				"http://localhost:8888" + URI);
		for (int i = 0; i < parameters.length; i = i + 2) {
			request.setParameter(parameters[i], parameters[i + 1]);
		}

		InvocationContext ic = sc.newInvocation(request);
		HttpServletRequest req = ic.getRequest();
		return ActionFactory.getAction(req);
	}

	/**
	 * Verifies that the {@code parameters} violates an assumption of the 
	 * matching {@link Action}. e.g., missing a compulsory parameter.
	 */
	protected void verifyAssumptionFailure(String... parameters) throws Exception {
		try {
			Action c = getActionObject(parameters);
			c.executeAndPostProcess();
			signalFailureToDetectException();
		} catch (AssertionError e) {
			ignoreExpectedException();
		}
	}

	/**
	 * Verifies that the {@link Action} matching the {@code params} is not
	 * accessible to the logged in user. 
	 */
	protected void verifyCannotAccess(String... params) throws Exception {
		try {
			Action c = getActionObject(params);
			c.executeAndPostProcess();
			signalFailureToDetectException();
		} catch (UnauthorizedAccessException e) {
			ignoreExpectedException();
		}
	}
	
	/**
	 * Verifies that the {@link Action} matching the {@code params} is 
	 * redirected to {@code expectedRedirectUrl}. Note that only the base 
	 * URI is matched and parameters are ignored. E.g. "/page/studentHome" 
	 * matches "/page/studentHome?user=abc". 
	 */
	protected void verifyRedirectTo(String expectedRedirectUrl,	String... params) throws Exception {
		Action c = getActionObject(params);
		RedirectResult r = (RedirectResult) c.executeAndPostProcess();
		assertContains(expectedRedirectUrl, r.destination);
	}

	/**
	 * Verifies that the {@link Action} matching the {@code params} is not
	 * accessible to the logged in user masquerading as another user. 
	 */
	protected void verifyCannotMasquerade(String... params) throws Exception {
		try {
			Action c = getActionObject(params);
			c.executeAndPostProcess();
			signalFailureToDetectException();
		} catch (UnauthorizedAccessException e) {
			ignoreExpectedException();
		}
	}

	/**
	 * Verifies that the {@link Action} matching the {@code params} is 
	 * accessible to the logged in user. 
	 */
	protected void verifyCanAccess(String... params) throws Exception {
		Action c = getActionObject(params);
		c.executeAndPostProcess();
	}

	/**
	 * Verifies that the {@link Action} matching the {@code params} is
	 * accessible to the logged in user masquerading as another user. 
	 */
	protected void verifyCanMasquerade(String... params) throws Exception {
		Action c = getActionObject(params);
		c.executeAndPostProcess();
	}

	/**
	 * @return The {@code params} array with the {@code userId} inserted at the beginning.
	 */
	protected String[] addUserIdToParams(String userId, String[] params) {
		List<String> list = new ArrayList<String>();
		list.add(Common.PARAM_USER_ID);
		list.add(userId);
		for (String s : params) {
			list.add(s);
		}
		return list.toArray(new String[list.size()]);
	}

	protected ShowPageResult getShowPageResult(Action a)
			throws EntityDoesNotExistException, InvalidParametersException {
		return (ShowPageResult) a.executeAndPostProcess();
	}

}
