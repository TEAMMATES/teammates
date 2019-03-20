package teammates.performance.scripts;

import static teammates.performance.util.TestProperties.JMETER_HOME;
import static teammates.performance.util.TestProperties.JMETER_PROPERTIES_PATH;

import java.io.File;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import teammates.common.exception.TeammatesException;

/**
 * Script to execute a JMeter performance test.
 */
public final class RunPerformanceTest {

    private RunPerformanceTest() {
        // Utility class
        // Intentional private constructor to prevent instantiation
    }

    public static void main(String[] args) {
        String filename = "studentProfile.jmx";

        try {
            runJmeterTest(filename);
        } catch (Exception ex) {
            TeammatesException.toStringWithStackTrace(ex);
        }
    }

    /**
     * Executes the JMeter test specified by {@code filename}.
     * @param filename Only the name of the `.jmx` JMeter file. The file is supposed to be in the
     *                 `src/jmeter/tests/` directory.
     */
    public static void runJmeterTest(String filename)throws Exception {
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

        // Create summariser for generating results file
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }

        String resultFile = "src/jmeter/results/" + filename + ".jtl";
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(resultFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run JMeter Test
        jmeter.configure(testPlanTree);
        jmeter.run();
    }
}
