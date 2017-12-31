package teammates.test.cases.testdriver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.testng.annotations.Test;

import teammates.test.cases.BaseTestCase;
import teammates.test.driver.FileHelper;

/**
 * Verifies that the TestNG configuration files contains all the test cases in the project.
 */
public class TestNgTest extends BaseTestCase {

    @Test
    public void checkTestsInTestNg() throws IOException {
        String testNgXml = FileHelper.readFile("./src/test/testng-ci.xml")
                           + FileHelper.readFile("./src/test/testng-local.xml");
        // <class name, package name>
        HashMap<String, String> testFiles = getTestFiles(testNgXml, "./src/test/java/teammates/test/cases");

        testFiles = excludeFilesNotInTestNg(testFiles,

                                            // Base*TestCase are base classes to be extended by the actual tests
                                            "BaseUiTestCase",

                                            // Base class for all Feedback*QuestionUiTest (different question types)
                                            "FeedbackQuestionUiTest",

                                            // Needs to be run only when changes are made to GodMode
                                            "GodModeTest"
                                            );

        testFiles.forEach((key, value) -> assertTrue(isTestFileIncluded(testNgXml, value, key)));
    }

    /**
     * Files to be checked in testng.xml are added to testFiles.
     *
     * @param testNgXml    Contents of testng.xml
     * @param rootPath     Root path of test files
     * @return             HashMap containing {@code <class name, package name>}
     */
    private HashMap<String, String> getTestFiles(String testNgXml, String rootPath) {
        // BaseComponentTestCase, BaseTestCase (files in current directory) excluded because
        // base classes are extended by the actual tests

        return addFilesToTestsRecursively(rootPath, true, "teammates.test.cases", testNgXml);
    }

    /**
     * Excludes files which do not have tests in TestNG.
     *
     * @param testFiles                  Files to be checked before excluding tests
     * @param filesExcludedFromTestNg    Files to be excluded
     * @return                           Files to be checked after excluding tests
     */
    private HashMap<String, String> excludeFilesNotInTestNg(HashMap<String, String> testFiles,
                                                            String... filesExcludedFromTestNg) {
        for (String test : filesExcludedFromTestNg) {
            testFiles.remove(test);
        }

        return testFiles;
    }

    private boolean isTestFileIncluded(String testNgXml, String packageName, String testClassName) {
        return testNgXml.contains("<class name=\"" + packageName + "." + testClassName + "\" />");
    }

    /**
     * Recursively adds files from testng.xml which are to be checked.
     *
     * @param path                            Check files and directories in the current path
     *
     * @param areFilesInCurrentDirExcluded    If true, files in the current path are not
     *                                        added to tests but sub-directories are still checked
     *
     * @param packageName                     Package name of the current file
     * @param testNgXml                       Contents of testng.xml
     *
     * @return                                HashMap containing {@code <class name, package name>} including
     *                                        current file or tests in the current directory
     */
    private HashMap<String, String> addFilesToTestsRecursively(String path,
                                                               boolean areFilesInCurrentDirExcluded,
                                                               String packageName, String testNgXml) {

        HashMap<String, String> testFiles = new HashMap<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            return testFiles;
        }

        for (File file : listOfFiles) {
            String name = file.getName();

            if (file.isFile() && name.endsWith(".java") && !name.startsWith("package-info")
                    && !areFilesInCurrentDirExcluded) {
                testFiles.put(name.replace(".java", ""), packageName);

            } else if (file.isDirectory()) {
                // If the package name is in TestNG in the form of <package name="teammates.test.cases.package.name" />
                // then files in the current directory are excluded because the whole package would be tested by TestNG.

                testFiles.putAll(
                        addFilesToTestsRecursively(path + "/" + name,
                                                   isPackageNameInTestNg(packageName + "." + name, testNgXml),
                                                   packageName + "." + name, testNgXml));
            }
        }

        return testFiles;
    }

    private boolean isPackageNameInTestNg(String packageName, String testNgXml) {
        return testNgXml.contains("<package name=\"" + packageName + "\" />");
    }

}
