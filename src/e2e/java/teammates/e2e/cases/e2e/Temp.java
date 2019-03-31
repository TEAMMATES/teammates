package teammates.e2e.cases.e2e;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.OnceOnlyController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.java.control.gui.JavaTestSamplerGui;
import org.apache.jmeter.protocol.java.sampler.JavaSampler;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.threads.SetupThreadGroup;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jorphan.collections.HashTree;

/**
 * temp.
 */
public class Temp {

    protected HashTree getTestPlan() throws IOException {
        // JMeter Test Plan, basically JOrphan HashTree
        HashTree testPlanTree = new HashTree();

        // First HTTP Sampler - open example.com
        HTTPSamplerProxy examplecomSampler = new HTTPSamplerProxy();
        examplecomSampler.setDomain("example.com");
        examplecomSampler.setPort(80);
        examplecomSampler.setPath("/");
        examplecomSampler.setMethod("GET");
        examplecomSampler.setName("Open example.com");
        examplecomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        examplecomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        // Second HTTP Sampler - open blazemeter.com
        HTTPSamplerProxy blazemetercomSampler = new HTTPSamplerProxy();
        blazemetercomSampler.setDomain("blazemeter.com");
        blazemetercomSampler.setPort(80);
        blazemetercomSampler.setPath("/");
        blazemetercomSampler.setMethod("GET");
        blazemetercomSampler.setName("Open blazemeter.com");
        blazemetercomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        blazemetercomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(1);
        loopController.setFirst(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();

        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Example Thread Group");
        threadGroup.setNumThreads(1);
        threadGroup.setRampUp(1);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

        // Test Plan
        TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        // Construct Test Plan from previously initialized elements
        testPlanTree.add(testPlan);
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(blazemetercomSampler);
        threadGroupHashTree.add(examplecomSampler);

        // save generated test plan to JMeter's .jmx file format
        SaveService.saveTree(testPlanTree, new FileOutputStream("aaaaaa.jmx"));

        return testPlanTree;
    }

    // WIP
    protected HashTree getTree(String jmxFile) throws IOException {
        // TestPlan
        TestPlan testPlan = new TestPlan();
        testPlan.setName("Test Plan");
        testPlan.setEnabled(true);
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());

        HashTree hashTree = new HashTree();

        // ThreadGroup controller
        LoopController loopController = new LoopController();
        loopController.setEnabled(true);
        loopController.setLoops(1);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        // controller.setContinueForever(false);
        // controller.setProperty(new BooleanProperty(TestElement.ENABLED, true));

        // Thread Group
        SetupThreadGroup threadGroup = new SetupThreadGroup();
        threadGroup.setNumThreads(100);
        threadGroup.setRampUp(2);
        threadGroup.setProperty(new StringProperty(ThreadGroup.ON_SAMPLE_ERROR, ThreadGroup.ON_SAMPLE_ERROR_CONTINUE));
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

        // CSVConfig
        CSVDataSet csvDataSet = new CSVDataSet();
        csvDataSet.setProperty(new StringProperty("filename", "src/e2e/resources/data/studentProfileConfig.csv"));
        // csvDataSet.setProperty(new StringProperty("variableNames", "USER_NAME"));
        csvDataSet.setProperty(new StringProperty("delimiter", "|"));
        csvDataSet.setProperty(new StringProperty("shareMode", "shareMode.all"));
        csvDataSet.setProperty("ignoreFirstLine", true);
        csvDataSet.setProperty("quoted", true);
        csvDataSet.setProperty("recycle", true);
        csvDataSet.setProperty("stopThread", false);

        // CookieManager
        CookieManager cookieManager = new CookieManager();
        cookieManager.setClearEachIteration(false);
        cookieManager.setCookiePolicy("standard");

        // HTTP Sampler
        HTTPSampler httpSampler = new HTTPSampler();

        // HTTP Request Defaults
        httpSampler.setDomain("localhost");
        httpSampler.setPort(8080);

        // Login HTTP Request
        OnceOnlyController onceOnlyController = new OnceOnlyController();

        httpSampler.setPath("_ah/login?action=Log+In&email=${email}&isAdmin=${isAdmin}&continue=http://localhost:8080/webapi/auth?frontendUrl=http://localhost:4200");
        httpSampler.setMethod("POST");
        httpSampler.setFollowRedirects(true);
        httpSampler.setUseKeepAlive(true);

        // JavaSampler
        JavaSampler javaSampler = new JavaSampler();
        javaSampler.setClassname("my.example.java.sampler");
        javaSampler.setEnabled(true);
        javaSampler.setProperty(TestElement.TEST_CLASS, JavaSampler.class.getName());
        javaSampler.setProperty(TestElement.GUI_CLASS, JavaTestSamplerGui.class.getName());

        // Test plan
        // TestPlan testPlan = new TestPlan("MY TEST PLAN");
        // hashTree.add("testPlan", testPlan);
        // hashTree.add("loopCtrl", loopCtrl);
        // hashTree.add("threadGroup", threadGroup);
        // hashTree.add("httpSampler", httpSampler);

        // Create TestPlan hash tree
        HashTree testPlanHashTree = new HashTree();
        testPlanHashTree.add(testPlan);
        testPlanHashTree.add(csvDataSet);

        // Add ThreadGroup to TestPlan hash tree
        HashTree threadGroupHashTree = new HashTree();
        threadGroupHashTree = testPlanHashTree.add(testPlan, threadGroup);

        // Add Java Sampler to ThreadGroup hash tree
        HashTree javaSamplerHashTree = new HashTree();
        javaSamplerHashTree = threadGroupHashTree.add(javaSampler);

        // Save to jmx file
        SaveService.saveTree(testPlanHashTree, new FileOutputStream("TEAMMATES.jmx"));

        return hashTree;
    }

}
