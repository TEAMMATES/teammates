package teammates.lnp.util;

import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.control.ForeachController;
import org.apache.jmeter.control.GenericController;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.OnceOnlyController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.OnceOnlyControllerGui;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.extractor.RegexExtractor;
import org.apache.jmeter.extractor.gui.RegexExtractorGui;
import org.apache.jmeter.protocol.http.config.gui.HttpDefaultsGui;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.CookiePanel;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jmeter.threads.SetupThreadGroup;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;

/**
 * Creates the JMeter elements needed to build the L&P test.
 *
 * <p>@see <a href="https://jmeter.apache.org/usermanual/component_reference.html">Apache JMeter: Component Reference</a></p>
 */
public final class JMeterElements {

    private JMeterElements() {
        // Utility class
        // Intentional private constructor to prevent instantiation
    }

    /**
     * Returns the top-level Test Plan element.
     */
    public static TestPlan testPlan() {
        TestPlan testPlan = new TestPlan();

        testPlan.setName("L&P Test Plan");
        testPlan.setUserDefinedVariables(new Arguments());
        testPlan.setEnabled(true);
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());

        return testPlan;
    }

    /**
     * Returns a Thread Group element.
     * @param numThreads The number of concurrent threads that will run the test plan and independently of the other threads
     * @param rampUpPeriod Time (in seconds) to take to "ramp-up" to the full number of threads
     * @param numLoops The number of times to execute the entire test plan
     */
    public static ThreadGroup threadGroup(int numThreads, int rampUpPeriod, int numLoops) {
        ThreadGroup threadGroup = new SetupThreadGroup();

        threadGroup.setName("Thread Group");
        threadGroup.setNumThreads(numThreads);
        threadGroup.setRampUp(rampUpPeriod);
        threadGroup.setProperty(new StringProperty(ThreadGroup.ON_SAMPLE_ERROR, ThreadGroup.ON_SAMPLE_ERROR_CONTINUE));
        threadGroup.setSamplerController(loopController(numLoops));
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

        return threadGroup;
    }

    /**
     * Returns a Loop Controller element that configures the number of times its sub-elements are iterated in a test run.
     * @param loopCount The number of iterations in a test run
     */
    public static LoopController loopController(int loopCount) {
        LoopController loopController = new LoopController();

        loopController.setLoops(loopCount);
        loopController.setEnabled(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());

        return loopController;
    }

    /**
     * Returns a CSV Data Set Config element that reads lines from a file and splits them into variables.
     * @param configFilePath Path to the CSV config file
     */
    public static CSVDataSet csvDataSet(String configFilePath) {
        CSVDataSet csvDataSet = new CSVDataSet();

        csvDataSet.setName("CSV Data Config");
        csvDataSet.setProperty(new StringProperty("filename", configFilePath));
        csvDataSet.setProperty(new StringProperty("delimiter", "|"));
        csvDataSet.setProperty(new StringProperty("shareMode", "shareMode.all"));
        csvDataSet.setProperty("ignoreFirstLine", true);
        csvDataSet.setProperty("quoted", true);
        csvDataSet.setProperty("quotedData", true);
        csvDataSet.setProperty("recycle", false);
        csvDataSet.setProperty("stopThread", true);
        csvDataSet.setProperty(TestElement.TEST_CLASS, CSVDataSet.class.getName());
        csvDataSet.setProperty(TestElement.GUI_CLASS, TestBeanGUI.class.getName());

        return csvDataSet;
    }

    /**
     * Returns a HTTP Cookie Manager element.
     */
    public static CookieManager cookieManager() {
        CookieManager cookieManager = new CookieManager();

        cookieManager.setName("HTTP Cookie Manager");
        cookieManager.setClearEachIteration(false);
        cookieManager.setCookiePolicy("standard");
        cookieManager.setProperty(TestElement.TEST_CLASS, CookieManager.class.getName());
        cookieManager.setProperty(TestElement.GUI_CLASS, CookiePanel.class.getName());

        return cookieManager;
    }

    /**
     * Returns a HTTP Request Defaults element that sets the default values for the HTTP Request elements.
     * @param domain Domain name of the server
     * @param port Port that the server is listening to
     */
    public static ConfigTestElement defaultSampler(String domain, String port) {
        ConfigTestElement defaultSampler = new ConfigTestElement();

        defaultSampler.setName("HTTP Request Defaults");
        defaultSampler.setProperty(new TestElementProperty(HTTPSampler.ARGUMENTS, new Arguments()));
        defaultSampler.setProperty(HTTPSampler.DOMAIN, domain);
        defaultSampler.setProperty(HTTPSampler.PORT, port);
        defaultSampler.setEnabled(true);
        defaultSampler.setProperty(TestElement.TEST_CLASS, ConfigTestElement.class.getName());
        defaultSampler.setProperty(TestElement.GUI_CLASS, HttpDefaultsGui.class.getName());

        return defaultSampler;
    }

    /**
     * Returns a HTTP Request Defaults element that sets the default values for the HTTP Request elements.
     * @param argumentsMap parameters of the request
     */
    public static ConfigTestElement defaultSampler(Map<String, String> argumentsMap) {
        ConfigTestElement defaultSampler = new ConfigTestElement();

        defaultSampler.setName("HTTP Request Defaults");

        Arguments arguments = new Arguments();
        argumentsMap.forEach((String k, String v) -> {
            arguments.addArgument(new HTTPArgument(k, v));
        });

        defaultSampler.setProperty(new TestElementProperty(HTTPSampler.ARGUMENTS, arguments));

        defaultSampler.setEnabled(true);
        defaultSampler.setProperty(TestElement.TEST_CLASS, ConfigTestElement.class.getName());
        defaultSampler.setProperty(TestElement.GUI_CLASS, HttpDefaultsGui.class.getName());

        return defaultSampler;
    }

    /**
     * Overloaded method that returns a HTTP Request Defaults element that listens to the server at "localhost:8080".
     */
    public static ConfigTestElement defaultSampler() {
        return defaultSampler("localhost", "8080");
    }

    /**
     * Returns a HTTP Request element that is configured to login to a TEAMMATES instance.
     *
     * <p>This element uses data from the "loginId" and "isAdmin" fields of the CSV config file.</p>
     */
    public static HTTPSamplerProxy loginSampler() {
        HTTPSamplerProxy loginSampler = new HTTPSamplerProxy();

        loginSampler.setName("Login");
        loginSampler.setPath(
                "_ah/login?action=Log+In&email=${loginId}&isAdmin=${isAdmin}&continue=http://localhost:8080/webapi/auth");
        loginSampler.setMethod("POST");
        loginSampler.setFollowRedirects(true);
        loginSampler.setUseKeepAlive(true);
        loginSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        loginSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        return loginSampler;
    }

    /**
     * Returns a Generic Controller element that processes the controller(s) inside it without additional effects.
     */
    public static GenericController genericController() {
        GenericController genericController = new GenericController();

        genericController.setName("Generic Controller");
        genericController.setProperty(TestElement.TEST_CLASS, GenericController.class.getName());
        genericController.setProperty(TestElement.GUI_CLASS, GenericController.class.getName());

        return genericController;
    }

    /**
     * Returns a Once Only Controller element that processes the controller(s) inside it only once per thread.
     */
    public static OnceOnlyController onceOnlyController() {
        OnceOnlyController onceOnlyController = new OnceOnlyController();

        onceOnlyController.setName("Once Only Controller");
        onceOnlyController.setProperty(TestElement.TEST_CLASS, OnceOnlyController.class.getName());
        onceOnlyController.setProperty(TestElement.GUI_CLASS, OnceOnlyControllerGui.class.getName());

        return onceOnlyController;
    }

    /**
     * Returns a For Each Controller element that processes the controller(s) inside it for each list variables.
     */
    public static ForeachController foreachController(String inputVarible, String returnVal) {
        ForeachController foreachController = new ForeachController();

        foreachController.setName("For Each Controller");
        foreachController.setProperty(TestElement.TEST_CLASS, GenericController.class.getName());
        foreachController.setProperty(TestElement.GUI_CLASS, GenericController.class.getName());
        foreachController.setInputVal(inputVarible);
        foreachController.setReturnVal(returnVal);

        return foreachController;
    }

    /**
     * Returns a Regular Expression Extractor element that extracts values from a server response header.
     * @param varName Name of the variable in which the result is stored
     * @param regex The regular expression used to parse the response data
     */
    public static RegexExtractor regexExtractor(String varName, String regex) {
        RegexExtractor regexExtractor = new RegexExtractor();

        regexExtractor.setName("Regular Expression Extractor");
        regexExtractor.setUseField("true"); // Find regex matches in response headers
        regexExtractor.setRefName(varName);
        regexExtractor.setRegex(regex);
        regexExtractor.setTemplate("$1$");
        regexExtractor.setProperty(TestElement.TEST_CLASS, RegexExtractor.class.getName());
        regexExtractor.setProperty(TestElement.GUI_CLASS, RegexExtractorGui.class.getName());

        return regexExtractor;
    }

    /**
     * Returns a Regular Expression Extractor element that extracts the CSRF token from a server response header.
     * @param varName Name of the variable which stores the value of the extracted CSRF token
     */
    public static RegexExtractor csrfExtractor(String varName) {
        return regexExtractor(varName, "CSRF-TOKEN=(.+?);");
    }

    /**
     * Returns a HTTP Header Manager element.
     * @param headers A map consisting of {header name -> header value} pairs (eg. {"Content-Type" -> "application/json"})
     */
    public static HeaderManager headerManager(Map<String, String> headers) {
        HeaderManager headerManager = new HeaderManager();

        headerManager.setName("HTTP Header Manager");
        headers.forEach((key, value) -> {
            headerManager.add(new Header(key, value));
        });
        headerManager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        headerManager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());

        return headerManager;
    }

    /**
     * Return a HTTP Request element.
     * @param path The path to the resource with the query string parameters (eg. /webapi/student?courseid=cs101)
     * @param method HTTP request method type (eg. GET, POST, PUT)
     * @param body The HTTP request body for methods like POST, PUT, PATCH
     */
    public static HTTPSamplerProxy httpSampler(String path, String method, String body) {
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();

        httpSampler.setName("HTTP Request Sampler");
        httpSampler.setPath(path);
        httpSampler.setMethod(method);

        if (body != null) {
            httpSampler.addNonEncodedArgument("", body, "");
            httpSampler.setPostBodyRaw(true);
        }

        httpSampler.setEnabled(true);
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        return httpSampler;
    }

    /**
     * Returns a HTTP Request element with a GET method to the endpoint specified by {@code path}.
     */
    public static HTTPSamplerProxy httpGetSampler(String path) {
        return httpSampler(path, HttpGet.METHOD_NAME, null);
    }

}
