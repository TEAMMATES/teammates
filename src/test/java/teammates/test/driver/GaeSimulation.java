package teammates.test.driver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalLogServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalModulesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalSearchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

import teammates.common.datatransfer.UserInfo;
import teammates.common.exception.ActionMappingException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;
import teammates.ui.automated.AutomatedAction;
import teammates.ui.automated.AutomatedActionFactory;
import teammates.ui.webapi.action.Action;
import teammates.ui.webapi.action.ActionFactory;

/**
 * Provides a Singleton in-memory simulation of the GAE for unit testing.
 *
 * <p>This is not the same as testing against the dev server.
 * When testing against the GAE simulation, there is no need for the dev server to be running.
 *
 * <p>The GAE simulation does not support JSP and can only be used to test up to Servlets level.
 */
public class GaeSimulation {

    // This can be any valid URL; it is not used beyond validation
    private static final String SIMULATION_BASE_URL = "http://localhost:8080";

    private static final String QUEUE_XML_PATH = "src/main/webapp/WEB-INF/queue.xml";

    private static GateKeeper gateKeeper = new GateKeeper();
    private static GaeSimulation instance = new GaeSimulation();

    /** This is used only to generate an HttpServletRequest for given parameters. */
    private ServletUnitClient sc;

    private LocalServiceTestHelper helper;

    /**
     * Gets the GAE simulation instance.
     */
    public static GaeSimulation inst() {
        return instance;
    }

    /**
     * Sets up the GAE simulation.
     */
    public void setup() {
        synchronized (this) {
            System.out.println("Setting up GAE simulation");

            LocalTaskQueueTestConfig localTasks = new LocalTaskQueueTestConfig();
            localTasks.setQueueXmlPath(QUEUE_XML_PATH);

            LocalUserServiceTestConfig localUserServices = new LocalUserServiceTestConfig();
            LocalDatastoreServiceTestConfig localDatastore = new LocalDatastoreServiceTestConfig();
            LocalMailServiceTestConfig localMail = new LocalMailServiceTestConfig();
            LocalSearchServiceTestConfig localSearch = new LocalSearchServiceTestConfig();
            localSearch.setPersistent(false);
            LocalModulesServiceTestConfig localModules = new LocalModulesServiceTestConfig();
            LocalLogServiceTestConfig localLog = new LocalLogServiceTestConfig();
            helper = new LocalServiceTestHelper(localDatastore, localMail, localUserServices,
                                                localTasks, localSearch, localModules, localLog);

            helper.setEnvAttributes(getEnvironmentAttributesWithApplicationHostname());
            helper.setUp();

            sc = new ServletRunner().newClient();
        }
    }

    private UserInfo loginUser(String userId, boolean isAdmin) {
        helper.setEnvIsLoggedIn(true);
        helper.setEnvEmail(userId);
        helper.setEnvAuthDomain("gmail.com");
        helper.setEnvIsAdmin(isAdmin);
        return gateKeeper.getCurrentUser();
    }

    /**
     * Logs in the user to the GAE simulation environment without admin rights.
     *
     * @return The user info after login process
     */
    public UserInfo loginUser(String userId) {
        return loginUser(userId, false);
    }

    /**
     * Logs in the user to the GAE simulation environment as an admin.
     *
     * @return The user info after login process
     */
    public UserInfo loginAsAdmin(String userId) {
        return loginUser(userId, true);
    }

    /**
     * Logs the current user out of the GAE simulation environment.
     */
    public void logoutUser() {
        helper.setEnvIsLoggedIn(false);
        helper.setEnvIsAdmin(false);
    }

    /**
     * Logs in the user to the GAE simulation environment as an unregistered user
     * (without any right).
     */
    @Deprecated
    public void loginAsUnregistered(String userId) {
        loginUser(userId);
        UserInfo user = gateKeeper.getCurrentUser();
        assertFalse(user.isStudent);
        assertFalse(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the GAE simulation environment as an instructor
     * (without admin rights or student rights).
     */
    @Deprecated
    public void loginAsInstructor(String userId) {
        loginUser(userId);
        UserInfo user = gateKeeper.getCurrentUser();
        assertFalse(user.isStudent);
        assertTrue(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the GAE simulation environment as a student
     * (without admin rights or instructor rights).
     */
    @Deprecated
    public void loginAsStudent(String userId) {
        loginUser(userId);
        UserInfo user = gateKeeper.getCurrentUser();
        assertTrue(user.isStudent);
        assertFalse(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Logs in the user to the GAE simulation environment as a student-instructor
     * (without admin rights).
     */
    @Deprecated
    public void loginAsStudentInstructor(String userId) {
        loginUser(userId);
        UserInfo user = gateKeeper.getCurrentUser();
        assertTrue(user.isStudent);
        assertTrue(user.isInstructor);
        assertFalse(user.isAdmin);
    }

    /**
     * Returns an {@link teammates.ui.controller.Action} object that matches the parameters given.
     *
     * @param parameters Parameters that appear in a HttpServletRequest received by the app.
     */
    public teammates.ui.controller.Action getLegacyActionObject(String uri, String... parameters) {
        InvocationContext ic = invokeWebRequest(uri, parameters);
        HttpServletRequest req = ic.getRequest();
        try {
            teammates.ui.controller.Action action = new teammates.ui.controller.ActionFactory().getAction(req);
            action.setTaskQueuer(new MockTaskQueuer());
            action.setEmailSender(new MockEmailSender());
            return action;
        } catch (ActionMappingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an {@link Action} object that matches the parameters given.
     *
     * @param uri The request URI
     * @param method The request method
     * @param body The request body
     * @param parts The request parts
     * @param params Parameters that appear in a HttpServletRequest received by the app
     */
    public Action getActionObject(String uri, String method, String body, Map<String, Part> parts, String... params) {
        try {
            MockHttpServletRequest req = new MockHttpServletRequest(method, Const.ResourceURIs.URI_PREFIX + uri);
            for (int i = 0; i < params.length; i = i + 2) {
                req.addParam(params[i], params[i + 1]);
            }
            if (body != null) {
                req.setBody(body);
            }
            if (parts != null) {
                parts.forEach((key, part) -> {
                    try {
                        req.addPart(key, part);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            MockHttpServletResponse resp = new MockHttpServletResponse();
            Action action = new ActionFactory().getAction(req, method, resp);
            action.setTaskQueuer(new MockTaskQueuer());
            action.setEmailSender(new MockEmailSender());
            return action;
        } catch (ActionMappingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an {@link AutomatedAction} object that matches the parameters given.
     *
     * @param parameters Parameters that appear in a HttpServletRequest received by the app.
     */
    public AutomatedAction getAutomatedActionObject(String uri, String... parameters) {
        try {
            // HTTP method is not used here
            MockHttpServletRequest req = new MockHttpServletRequest(null, uri);
            for (int i = 0; i < parameters.length; i = i + 2) {
                req.addParam(parameters[i], parameters[i + 1]);
            }
            MockHttpServletResponse resp = new MockHttpServletResponse();
            AutomatedAction action = new AutomatedActionFactory().getAction(req, resp);
            action.setTaskQueuer(new MockTaskQueuer());
            action.setEmailSender(new MockEmailSender());
            return action;
        } catch (ActionMappingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tears down the GAE simulation.
     */
    public void tearDown() {
        try {
            if (helper != null) {
                helper.tearDown();
            }
        } catch (Exception e) {
            //TODO: eliminate this exception
            System.out.println("Ignoring exception during teardown...");
        }
    }

    @Deprecated
    private InvocationContext invokeWebRequest(String uri, String... parameters) {
        // This is not testing servlet, so any HTTP method suffices
        WebRequest request = new PostMethodWebRequest(SIMULATION_BASE_URL + uri);

        // TODO remove this portion once front-end migration is finished
        // Reason: CSRF protection is not part of action tests
        if (Const.SystemParams.PAGES_REQUIRING_ORIGIN_VALIDATION.contains(uri)) {
            request.setHeaderField("referer", SIMULATION_BASE_URL);

            String sessionId = sc.getSession(true).getId();
            String token = StringHelper.encrypt(sessionId);
            request.setParameter(Const.ParamsNames.SESSION_TOKEN, token);
        }

        Map<String, List<String>> paramMultiMap = new HashMap<>();
        for (int i = 0; i < parameters.length; i = i + 2) {
            paramMultiMap.computeIfAbsent(parameters[i], k -> new ArrayList<>()).add(parameters[i + 1]);
        }

        paramMultiMap.forEach((key, values) -> request.setParameter(key, values.toArray(new String[0])));

        try {
            return sc.newInvocation(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an environment attribute with application host name.
     */
    public static Map<String, Object> getEnvironmentAttributesWithApplicationHostname() {
        Map<String, Object> attributes = new HashMap<>();
        try {
            attributes.put("com.google.appengine.runtime.default_version_hostname",
                    new URL(SIMULATION_BASE_URL).getAuthority());
            attributes.put("com.google.appengine.runtime.request_log_id", "samplerequestid123");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return attributes;
    }

}
