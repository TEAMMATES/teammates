package teammates.test.cases.testdriver;

import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.testng.annotations.Test;

import teammates.common.util.FileHelper;
import teammates.test.cases.BaseTestCase;

public class TestngTest extends BaseTestCase {

    @Test
    public void checkTestsInTestng() throws FileNotFoundException {        
        String testNgXml = getFileContent("./src/test/testng.xml");
        HashMap<String, String> testFiles = getTestFiles(testNgXml); // <class name, package name>
        
        testFiles = excludeFiles(testFiles);
        
        for (Entry<String, String> testFileName : testFiles.entrySet()) {
            assertTrue(isTestFileIncluded(testNgXml, testFileName));
        }
    }
    
    private String getFileContent(String file) throws FileNotFoundException {
        return FileHelper.readFile(file);
    }
    
    /**
     * Files to be checked in testng.xml are added to testFiles
     * 
     * @param testNgXml    Contents of testng.xml
     * @return             HashMap containing <class name, package name>
     */
    private HashMap<String, String> getTestFiles(String testNgXml) {       
        // BaseComponentTestCase, BaseTestCase (files in current directory) excluded because 
        // base classes are extended by the actual tests
        
        return addFilesToTestsRecursively(new HashMap<String, String>(), 
                                              "./src/test/java/teammates/test/cases", true, "", testNgXml); 
    }
    
    /**
     * Exclude files which do not have tests
     * 
     * @param testFiles    Files to be checked before excluding tests
     * @return             Files to be checked after excluding tests
     */
    private HashMap<String, String> excludeFiles(HashMap<String, String> testFiles) {
        // Base*TestCase are base classes to be extended by the actual tests
        testFiles.remove("BaseUiTestCase");
        
        // FeedbackQuestionUiTest is the base class for all Feedback*QuestionUiTest (different question types)
        testFiles.remove("FeedbackQuestionUiTest");
        
        // Needs to be run only when changes are made to GodMode
        testFiles.remove("GodModeTest");
        
        return testFiles;
    }
    
    private boolean isTestFileIncluded(String testNgXml, Entry<String, String> testFileName) {
        return testNgXml.contains("<class name=\"teammates.test.cases" 
                                       + testFileName.getValue() + "." + testFileName.getKey() + "\" />");
    }

    /**
     * Recursively add files from testng.xml which are to be checked
     * 
     * @param testFiles                       HashMap containing <class name, package name>
     * @param path                            Check files and directories in the current path
     * 
     * @param areFilesInCurrentDirExcluded    If true, files in the current path are not
     *                                        added to tests but sub-directories are checked
     *                                        
     * @param packageName                     Package name of the current file
     * @param testNgXml                       Contents of testng.xml
     * 
     * @return                                HashMap containing <class name, package name> including 
     *                                        current file or tests in the current directory
     */
    private HashMap<String, String> addFilesToTestsRecursively(HashMap<String, String> testFiles, String path, 
                                                               boolean areFilesInCurrentDirExcluded, 
                                                               String packageName, String testNgXml) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();      

        for (File file : listOfFiles) {
            String name = file.getName();
            
            if (file.isFile() && name.endsWith(".java") && !areFilesInCurrentDirExcluded) {
                testFiles.put(name.replace(".java", ""), packageName);
                
            } else if (file.isDirectory()) {
                // If package name is in testng in the form <package name="teammates.test.cases.packageName" />
                // then files in the current directory are excluded
                
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
