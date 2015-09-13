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

    @Test
    public void checkTestsInTestng() throws FileNotFoundException {
        HashMap<String, String> tests = new HashMap<String, String>(); // <class name, package name>     
        String testngXmlAsString = FileHelper.readFile("./src/test/testng.xml");
        
        addFilesToTestsRecursively(tests, "./src/test/java/teammates/test/cases", true, "", 
                                       testngXmlAsString); // BaseComponentTestCase, BaseTestCase (files in current directory) excluded
        
        tests = excludeFile(tests, "BaseUiTestCase");
        tests = excludeFile(tests, "FeedbackQuestionUiTest");
        tests = excludeFile(tests, "GodModeTest");

        verifyTestngContainsTests(tests, testngXmlAsString);
    }
    
    private HashMap<String, String> excludeFile(HashMap<String, String> tests, String file) {
        tests.remove(file);
        return tests;
    }

    private void verifyTestngContainsTests(HashMap<String, String> tests, String testngXmlAsString) {
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
     * @param testngXmlAsString 
     */
    private void addFilesToTestsRecursively(HashMap<String, String> tests, String path, boolean areFilesInCurrentDirExcluded, 
                                                String packageName, String testngXmlAsString) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();      

        for (File file : listOfFiles) {
            String name = file.getName();
            
            if (file.isFile() && name.endsWith(".java") && !areFilesInCurrentDirExcluded) {
                testFiles.put(name.replace(".java", ""), packageName);
                
            } else if (file.isDirectory()) {
                testFiles = addFilesToTestsRecursively(testFiles, path + "/" + name, 
                                                       isPackageNameinTestng(packageName + "." + name, testNgXml),
                                                       packageName + "." + name, testNgXml);
            }
        }
        
        return testFiles;
    }
    
    private boolean isPackageNameinTestng(String packageName, String testNgXml) {
        return testNgXml.contains("<package name=\"teammates.test.cases" + packageName + "\" />");
    }
    
}
