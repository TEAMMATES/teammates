package teammates.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.Part;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalModulesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalSearchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

import teammates.common.datatransfer.UserInfo;
import teammates.common.exception.ActionMappingException;
import teammates.common.util.RecaptchaVerifier;
import teammates.logic.api.GateKeeper;
import teammates.ui.webapi.Action;
import teammates.ui.webapi.ActionFactory;

/**
 * Provides a Singleton in-memory simulation of the GAE for unit testing.
 *
 * <p>This is not the same as testing against the dev server.
 * When testing against the GAE simulation, there is no need for the dev server to be running.
 */
public class GaeSimulation {

    // This can be any valid URL; it is not used beyond validation
    private static final String SIMULATION_BASE_URL = "http://localhost:8080";

    private static GateKeeper gateKeeper = new GateKeeper();
    private static GaeSimulation instance = new GaeSimulation();

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

            LocalUserServiceTestConfig localUserServices = new LocalUserServiceTestConfig();
            LocalDatastoreServiceTestConfig localDatastore = new LocalDatastoreServiceTestConfig();
            LocalMailServiceTestConfig localMail = new LocalMailServiceTestConfig();
            LocalSearchServiceTestConfig localSearch = new LocalSearchServiceTestConfig();
            localSearch.setPersistent(false);
            LocalModulesServiceTestConfig localModules = new LocalModulesServiceTestConfig();
            helper = new LocalServiceTestHelper(localDatastore, localMail, localUserServices, localSearch, localModules);

            helper.setEnvAttributes(getEnvironmentAttributesWithApplicationHostname());
            helper.setUp();
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
     * Returns an {@link Action} object that matches the parameters given.
     *
     * @param uri The request URI
     * @param method The request method
     * @param body The request body
     * @param parts The request parts
     * @param cookies The request's list of Cookies
     * @param params Parameters that appear in a HttpServletRequest received by the app
     */
    public Action getActionObject(String uri, String method, String body, Map<String, Part> parts,
                                  List<Cookie> cookies, String... params) {
        try {
            MockHttpServletRequest req = new MockHttpServletRequest(method, uri);
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
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    req.addCookie(cookie);
                }
            }
            Action action = new ActionFactory().getAction(req, method);
            action.setTaskQueuer(new MockTaskQueuer());
            action.setEmailSender(new MockEmailSender());
            action.setFileStorage(new MockFileStorage());
            action.setRecaptchaVerifier(new RecaptchaVerifier(null));
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
