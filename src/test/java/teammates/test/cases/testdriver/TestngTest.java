package teammates.test.cases.testdriver;

import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.util.FileHelper;
import teammates.test.cases.BaseTestCase;

public class TestngTest extends BaseTestCase {

    HashMap<String, String> tests = new HashMap<String, String>(); // <class name, package name>
    String testngXmlAsString;

    @Test
    public void checkTestsInTestng() throws FileNotFoundException {
        assertTrue(FileHelper.isFileExists("./src/test/testng.xml"));
        testngXmlAsString = FileHelper.readFile("./src/test/testng.xml");
        
        addFilesToTestsRecursively();
        excludeFilesNotInTestng();

        verifyTestngContainsTests();
    }
    
    /**
     * Files to be checked in testng.xml are stored in tests
     */
    private void addFilesToTestsRecursively() {
        addFilesToTestsRecursively("./src/test/java/teammates/test/cases", true, ""); // BaseComponentTestCase, BaseTestCase excluded
    }
    
    private void excludeFilesNotInTestng() {
        tests.remove("BaseUiTestCase");
        tests.remove("FeedbackQuestionUiTest");
        tests.remove("GodModeTest");
    }

    private void verifyTestngContainsTests() {
        for (Map.Entry<String, String> test : tests.entrySet()) {
            assertTrue(testngXmlAsString.contains("<class name=\"teammates.test.cases" + test.getValue() 
                                                      + "." + test.getKey() + "\" />"));
        }
    }

    /**
     * Files to be checked in testng.xml are stored in tests
     * 
     * @param path  check files and directories in the current path
     * @param areFilesInCurrentDirExcluded  if true, files in the current path are not
     *                                      added to tests but sub-directories are checked
     * @param packageName   package name of the current file                                     
     */
    private void addFilesToTestsRecursively(String path, boolean areFilesInCurrentDirExcluded, String packageName) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();      

        for (File file : listOfFiles) {
            String name = file.getName();
            
            if (file.isFile() && name.endsWith(".java") && !areFilesInCurrentDirExcluded) {
                tests.put(name.replace(".java", ""), packageName);
                
            } else if (file.isDirectory()) {
                addFilesToTestsRecursively(path + "/" + name, isPackageNameinTestng(packageName + "." + name),
                                                packageName + "." + name);
            }
        }
    }
    
    private boolean isPackageNameinTestng(String packageName) {
        return testngXmlAsString.contains("<package name=\"teammates.test.cases" + packageName + "\" />");
    }
    
}
