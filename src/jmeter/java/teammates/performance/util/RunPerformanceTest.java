package teammates.performance.util;

import static teammates.performance.util.TestProperties.JMETER_HOME;
import static teammates.performance.util.TestProperties.JMETER_PROPERTIES_PATH;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.File;

public class RunPerformanceTest {

    public static void main(String[] args) throws Exception {
        String filename = "studentProfile.jmx";

        // JMeter Engine
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        // Initialize Properties, logging, locale, etc.
        JMeterUtils.loadJMeterProperties(JMETER_PROPERTIES_PATH);
        JMeterUtils.setJMeterHome(JMETER_HOME);
        JMeterUtils.initLocale();

        // Initialize JMeter SaveService
        SaveService.loadProperties();

        // Load existing .jmx Test Plan
        File testFile = new File("src/jmeter/tests/" + filename);
        HashTree testPlanTree = SaveService.loadTree(testFile);

        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");//$NON-NLS-1$
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }

        String logFile = "src/jmeter/results/file.jtl";
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(logFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run JMeter Test
        jmeter.configure(testPlanTree);
        jmeter.run();
    }
}
