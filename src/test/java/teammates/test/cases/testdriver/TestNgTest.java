package teammates.test.cases.testdriver;

import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.testng.annotations.Test;

import teammates.common.util.FileHelper;
import teammates.test.cases.BaseTestCase;

public class TestNgTest extends BaseTestCase {

    @Test
    public void checkTestsInTestng() throws FileNotFoundException {        
        String testNgXml = FileHelper.readFile("./src/test/testng.xml");
        HashMap<String, String> testFiles = getTestFiles(testNgXml, "./src/test/java/teammates/test/cases"); // <class name, package name>
        
        testFiles = excludeFilesNotInTestNg(testFiles);
        
        for (Entry<String, String> testFileName : testFiles.entrySet()) {
            assertTrue(isTestFileIncluded(testNgXml, testFileName.getValue(), testFileName.getKey()));
        }
    }
    
    /**
     * Files to be checked in testng.xml are added to testFiles
     * 
     * @param testNgXml    Contents of testng.xml
     * @param rootPath     Root path of test files
     * @return             HashMap containing <class name, package name>
     */
    private HashMap<String, String> getTestFiles(String testNgXml, String rootPath) {       
        // BaseComponentTestCase, BaseTestCase (files in current directory) excluded because 
        // base classes are extended by the actual tests
        
        return addFilesToTestsRecursively(rootPath, true, "", testNgXml); 
    }
    
    /**
     * Exclude files which do not have tests in TestNG
     * 
     * @param testFiles    Files to be checked before excluding tests
     * @return             Files to be checked after excluding tests
     */
    private HashMap<String, String> excludeFilesNotInTestNg(HashMap<String, String> testFiles) {
        // Base*TestCase are base classes to be extended by the actual tests
        testFiles.remove("BaseUiTestCase");
        
        // FeedbackQuestionUiTest is the base class for all Feedback*QuestionUiTest (different question types)
        testFiles.remove("FeedbackQuestionUiTest");
        
        // Needs to be run only when changes are made to GodMode
        testFiles.remove("GodModeTest");
        
        return testFiles;
    }
    
    private boolean isTestFileIncluded(String testNgXml, String packageName, String testClassName) {
        return testNgXml.contains("<class name=\"teammates.test.cases" 
                                       + packageName + "." + testClassName + "\" />");
    }

    /**
     * Recursively add files from testng.xml which are to be checked
     * 
     * @param path                            Check files and directories in the current path
     * 
     * @param areFilesInCurrentDirExcluded    If true, files in the current path are not
     *                                        added to tests but sub-directories are still checked
     *                                        
     * @param packageName                     Package name of the current file
     * @param testNgXml                       Contents of testng.xml
     * 
     * @return                                HashMap containing <class name, package name> including 
     *                                        current file or tests in the current directory
     */
    private HashMap<String, String> addFilesToTestsRecursively(String path, 
                                                               boolean areFilesInCurrentDirExcluded, 
                                                               String packageName, String testNgXml) {
        
        HashMap<String, String> testFiles = new HashMap<String, String>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();      

        for (File file : listOfFiles) {
            String name = file.getName();
            
            if (file.isFile() && name.endsWith(".java") && !areFilesInCurrentDirExcluded) {
                testFiles.put(name.replace(".java", ""), packageName);
                
            } else if (file.isDirectory()) {
                // If the package name is in TestNG in the form of <package name="teammates.test.cases.package.name" />
                // then files in the current directory are excluded because the whole package would be tested by TestNG.
                
                testFiles.putAll(addFilesToTestsRecursively(path + "/" + name, isPackageNameInTestNg(packageName + "." + name, testNgXml),
                                                            packageName + "." + name, testNgXml));
            }
        }
        
        return testFiles;
    }
    
    private boolean isPackageNameInTestNg(String packageName, String testNgXml) {
        return testNgXml.contains("<package name=\"teammates.test.cases" + packageName + "\" />");
    }
    
}
