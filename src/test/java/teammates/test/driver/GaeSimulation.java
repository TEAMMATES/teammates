package teammates.test.driver;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import teammates.logic.api.GateKeeper;
import teammates.ui.automated.AutomatedAction;
import teammates.ui.automated.AutomatedActionFactory;
import teammates.ui.controller.Action;
import teammates.ui.controller.ActionFactory;

import com.google.appengine.api.log.dev.LocalLogService;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueueCallback;
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

/** Provides a Singleton in-memory simulation of the GAE for unit testing.
 * This is not the same as testing against the dev server. When testing
 * against the GAE simulation, there is no need for the dev server to be up.
 * However, the GAE simulation does not support JSP and can only be used to
 * test up to Servlets level.
 */
public class GaeSimulation {

    private static final String QUEUE_XML_PATH = "src/main/webapp/WEB-INF/queue.xml";

    private static GaeSimulation instance = new GaeSimulation();

    /** This is used only to generate an HttpServletRequest for given parameters */
    protected ServletUnitClient sc;

    protected LocalServiceTestHelper helper;

    private LocalLogService localLogService;

    public static GaeSimulation inst() {
        return instance;
    }

    public synchronized void setup() {
        setupWithTaskQueueCallbackClass(null);
    }

    public synchronized void setupWithTaskQueueCallbackClass(Class<? extends LocalTaskQueueCallback> callbackClass) {
        System.out.println("Setting up GAE simulation");

        LocalTaskQueueTestConfig localTasks = new LocalTaskQueueTestConfig();
        localTasks.setQueueXmlPath(QUEUE_XML_PATH);
        if (callbackClass != null) {
            localTasks.setCallbackClass(callbackClass)
                      .setDisableAutoTaskExecution(false);
        }

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

    /**Logs in the user to the GAE simulation environment without admin rights.
     */
    public void loginUser(String userId) {
        helper.setEnvIsLoggedIn(true);
        helper.setEnvEmail(userId);
        helper.setEnvAuthDomain("gmail.com");
        helper.setEnvIsAdmin(false);
    }

    /**Logs the current user out of the GAE simulation environment.
     */
    public void logoutUser() {
        helper.setEnvIsLoggedIn(false);
        helper.setEnvIsAdmin(false);
    }

    /**Logs in the user to the GAE simulation environment as an admin.
     */
    public void loginAsAdmin(String userId) {
        loginUser(userId);
        helper.setEnvIsAdmin(true);
    }

    /**Logs in the user to the GAE simulation environment as an instructor
     * (without admin rights).
     */
    public void loginAsInstructor(String userId) {
        loginUser(userId);
        GateKeeper gateKeeper = new GateKeeper();
        assertTrue(gateKeeper.getCurrentUser().isInstructor);
        assertFalse(gateKeeper.getCurrentUser().isAdmin);
    }

    /**Logs in the user to the GAE simulation environment as a student
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

    public void addLogRequestInfo(String appId, String versionId, String requestId, String ip, String nickname,
                                  long startTimeUsec, long endTimeUsec, String method, String resource,
                                  String httpVersion, String userAgent, boolean complete, Integer status,
                                  String referrer) {
        localLogService.addRequestInfo(appId, versionId, requestId, ip, nickname, startTimeUsec, endTimeUsec,
                                       method, resource, httpVersion, userAgent, complete, status, referrer);
    }

    public void addAppLogLine(String requestId, long time, int level, String message) {
        localLogService.addAppLogLine(requestId, time, level, message);
    }

    /**
     * @param parameters Parameters that appear in a HttpServletRequest received by the app.
     * @return an {@link Action} object that matches the parameters given.
     */
    public Action getActionObject(String uri, String... parameters) {
        HttpServletRequest req = createWebRequest(uri, parameters);
        Action action = new ActionFactory().getAction(req);
        action.setTaskQueuer(new MockTaskQueuer());
        action.setEmailSender(new MockEmailSender());
        return action;
    }

    /**
     * @param parameters Parameters that appear in a HttpServletRequest received by the app.
     * @return an {@link AutomatedAction} object that matches the parameters given.
     */
    public AutomatedAction getAutomatedActionObject(String uri, String... parameters) {
        HttpServletRequest req = createWebRequest(uri, parameters);
        AutomatedAction action = new AutomatedActionFactory().getAction(req, null);
        action.setTaskQueuer(new MockTaskQueuer());
        action.setEmailSender(new MockEmailSender());
        return action;
    }

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

        WebRequest request = new PostMethodWebRequest("http://localhost:8888" + uri);

        Map<String, List<String>> paramMultiMap = new HashMap<String, List<String>>();
        for (int i = 0; i < parameters.length; i = i + 2) {
            String key = parameters[i];
            if (paramMultiMap.get(key) == null) {
                paramMultiMap.put(key, new ArrayList<String>());
            }
            paramMultiMap.get(key).add(parameters[i + 1]);
        }

        for (Map.Entry<String, List<String>> entry : paramMultiMap.entrySet()) {
            List<String> values = entry.getValue();
            request.setParameter(entry.getKey(), values.toArray(new String[values.size()]));
        }

        try {
            InvocationContext ic = sc.newInvocation(request);
            return ic.getRequest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
