package teammates.test.driver;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.log.dev.LocalLogService;
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

import teammates.common.util.Const;
import teammates.common.util.CryptoHelper;
import teammates.logic.api.GateKeeper;
import teammates.ui.automated.AutomatedAction;
import teammates.ui.automated.AutomatedActionFactory;
import teammates.ui.controller.Action;
import teammates.ui.controller.ActionFactory;

/**
 * Provides a Singleton in-memory simulation of the GAE for unit testing.
 *
 * <p>This is not the same as testing against the dev server.
 * When testing against the GAE simulation, there is no need for the dev server to be running.
 *
 * <p>The GAE simulation does not support JSP and can only be used to test up to Servlets level.
 */
public class GaeSimulation {

    private static final String QUEUE_XML_PATH = "src/main/webapp/WEB-INF/queue.xml";

    private static GaeSimulation instance = new GaeSimulation();

    /** This is used only to generate an HttpServletRequest for given parameters. */
    private ServletUnitClient sc;

    private LocalServiceTestHelper helper;

    private LocalLogService localLogService;

    /**
     * Gets the GAE simulation instance.
     */
    public static GaeSimulation inst() {
        return instance;
    }

    /**
     * Sets up the GAE simulation.
     */
    public synchronized void setup() {
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
        helper.setUp();

        sc = new ServletRunner().newClient();
        localLogService = LocalLogServiceTestConfig.getLocalLogService();
    }

    /**
     * Logs in the user to the GAE simulation environment without admin rights.
     */
    public void loginUser(String userId) {
        helper.setEnvIsLoggedIn(true);
        helper.setEnvEmail(userId);
        helper.setEnvAuthDomain("gmail.com");
        helper.setEnvIsAdmin(false);
    }

    /**
     * Logs the current user out of the GAE simulation environment.
     */
    public void logoutUser() {
        helper.setEnvIsLoggedIn(false);
        helper.setEnvIsAdmin(false);
    }

    /**
     * Logs in the user to the GAE simulation environment as an admin.
     */
    public void loginAsAdmin(String userId) {
        loginUser(userId);
        helper.setEnvIsAdmin(true);
    }

    /**
     * Logs in the user to the GAE simulation environment as an instructor
     * (without admin rights).
     */
    public void loginAsInstructor(String userId) {
        loginUser(userId);
        GateKeeper gateKeeper = new GateKeeper();
        assertTrue(gateKeeper.getCurrentUser().isInstructor);
        assertFalse(gateKeeper.getCurrentUser().isAdmin);
    }

    /**
     * Logs in the user to the GAE simulation environment as a student
     * (without admin rights or instructor rights).
     */
    public void loginAsStudent(String userId) {
        loginUser(userId);
        GateKeeper gateKeeper = new GateKeeper();
        assertTrue(gateKeeper.getCurrentUser().isStudent);
        assertFalse(gateKeeper.getCurrentUser().isInstructor);
        assertFalse(gateKeeper.getCurrentUser().isAdmin);
    }

    /**
     * Clears all logs in GAE.
     */
    public void clearLogs() {
        localLogService.clear();
    }

    /**
     * Adds a request info log to the simulated environment.
     */
    public void addLogRequestInfo(String appId, String versionId, String requestId, String ip, String nickname,
                                  long startTimeUsec, long endTimeUsec, String method, String resource,
                                  String httpVersion, String userAgent, boolean complete, Integer status,
                                  String referrer) {
        localLogService.addRequestInfo(appId, versionId, requestId, ip, nickname, startTimeUsec, endTimeUsec,
                                       method, resource, httpVersion, userAgent, complete, status, referrer);
    }

    /**
     * Adds an application log line to the simulated environment.
     */
    public void addAppLogLine(String requestId, long time, int level, String message) {
        localLogService.addAppLogLine(requestId, time, level, message);
    }

    /**
     * Returns an {@link Action} object that matches the parameters given.
     *
     * @param parameters Parameters that appear in a HttpServletRequest received by the app.
     */
    public Action getActionObject(String uri, String... parameters) {
        HttpServletRequest req = createWebRequest(uri, parameters);
        Action action = new ActionFactory().getAction(req);
        action.setTaskQueuer(new MockTaskQueuer());
        action.setEmailSender(new MockEmailSender());
        return action;
    }

    /**
     * Returns an {@link AutomatedAction} object that matches the parameters given.
     *
     * @param parameters Parameters that appear in a HttpServletRequest received by the app.
     */
    public AutomatedAction getAutomatedActionObject(String uri, String... parameters) {
        HttpServletRequest req = createWebRequest(uri, parameters);
        AutomatedAction action = new AutomatedActionFactory().getAction(req, null);
        action.setTaskQueuer(new MockTaskQueuer());
        action.setEmailSender(new MockEmailSender());
        return action;
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

    private HttpServletRequest createWebRequest(String uri, String... parameters) {

        WebRequest request = new PostMethodWebRequest("http://localhost" + uri);

        if (Const.SystemParams.PAGES_REQUIRING_ORIGIN_VALIDATION.contains(uri)) {
            request.setHeaderField("referer", "http://localhost");

            String sessionId = sc.getSession(true).getId();
            String token = CryptoHelper.computeSessionToken(sessionId);
            request.setParameter(Const.ParamsNames.SESSION_TOKEN, token);
        }

        Map<String, List<String>> paramMultiMap = new HashMap<>();
        for (int i = 0; i < parameters.length; i = i + 2) {
            String key = parameters[i];
            if (paramMultiMap.get(key) == null) {
                paramMultiMap.put(key, new ArrayList<String>());
            }
            paramMultiMap.get(key).add(parameters[i + 1]);
        }

        paramMultiMap.forEach((key, values) -> request.setParameter(key, values.toArray(new String[0])));

        try {
            InvocationContext ic = sc.newInvocation(request);
            return ic.getRequest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
