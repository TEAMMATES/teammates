package teammates.it.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;
import teammates.test.FileHelper;

/**
 * Verifies that the testng-it.xml configuration file contains all the integration test cases in the project.
 */
public class TestNgXmlTest extends BaseTestCase {

    @Test
    public void checkTestsInTestNg() throws Exception {
        String testNgXml = FileHelper.readFile("./src/it/resources/testng-it.xml");

        // <class name, package name>
        Map<String, String> testFiles = getTestFiles(testNgXml, "./src/it/java/teammates");

        testFiles.forEach((key, value) -> assertTrue(isTestFileIncluded(testNgXml, value, key)));
    }

    /**
     * Files to be checked in testng-it.xml are added to testFiles.
     *
     * @param testNgXml    Contents of testng-it.xml
     * @param rootPath     Root path of test files
     * @return             Map containing {@code <class name, package name>}
     */
    private Map<String, String> getTestFiles(String testNgXml, String rootPath) {
        // BaseComponentTestCase, BaseTestCase (files in current directory) excluded because
        // base classes are extended by the actual tests

        return addFilesToTestsRecursively(rootPath, true, "teammates", testNgXml);
    }

    private boolean isTestFileIncluded(String testNgXml, String packageName, String testClassName) {
        return testNgXml.contains("<class name=\"" + packageName + "." + testClassName + "\" />");
    }

    /**
     * Recursively adds files from testng-it.xml which are to be checked.
     *
     * @param path                            Check files and directories in the current path
     *
     * @param areFilesInCurrentDirExcluded    If true, files in the current path are not
     *                                        added to tests but sub-directories are still checked
     *
     * @param packageName                     Package name of the current file
     * @param testNgXml                       Contents of testng-component.xml
     *
     * @return                                Map containing {@code <class name, package name>} including
     *                                        current file or tests in the current directory
     */
    private Map<String, String> addFilesToTestsRecursively(String path, boolean areFilesInCurrentDirExcluded,
            String packageName, String testNgXml) {

        Map<String, String> testFiles = new HashMap<>();
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

            } else if (file.isDirectory() && !name.endsWith("browsertests") && !name.endsWith("pageobjects")
                    && !name.endsWith("architecture")) {
                // If the package name is in TestNG in the form of <package name="teammates.package.name" />
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
