package teammates.test.cases.testdriver;

import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.FileHelper;
import teammates.test.cases.BaseTestCase;

public class TestngTest extends BaseTestCase {

    List<String> tests = new ArrayList<String>();
    String testngXmlAsString;

    @Test
    public void checkTestsInTestng() throws FileNotFoundException {
        verifyTestngXmlExists();
        testngXmlAsString = getTestngAsString();
        
        addFilesToTestsRecursively();
        excludeFilesNotInTestng();

        verifyTestngContainsTests();
    }
    
    private void verifyTestngXmlExists() {
        File f = new File("./src/test/testng.xml");   
        assertTrue(f.exists() && !f.isDirectory());
    }
    
    private String getTestngAsString() throws FileNotFoundException {
        return FileHelper.readFile("./src/test/testng.xml");
    }

    /**
     * Files to be checked in testng.xml are stored in tests
     */
    private void addFilesToTestsRecursively() {
        addFilesToTestsRecursively("./src/test/java/teammates/test/cases", true); // BaseComponentTestCase, BaseTestCase excluded
    }
    
    private void excludeFilesNotInTestng() {
        tests.remove("BaseUiTestCase");
        tests.remove("FeedbackQuestionUiTest");
        tests.remove("GodModeTest");
    }

    private void verifyTestngContainsTests() {
        for (String test : tests) {
            assertTrue(testngXmlAsString.contains(test));
        }
    }

    /**
     * Files to be checked in testng.xml are stored in tests
     * 
     * @param path  check files and directories in the current path
     * @param areFilesInCurrentDirExcluded  if true, files in the current path are not
     *                                      added to tests but sub-directories are checked
     */
    private void addFilesToTestsRecursively(String path, boolean areFilesInCurrentDirExcluded) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();      

        for (File file : listOfFiles) {
            String name = file.getName();
            
            if (file.isFile() && name.endsWith(".java") && !areFilesInCurrentDirExcluded) {
                tests.add(name.replace(".java", ""));
                
            } else if (file.isDirectory()) {
                addFilesToTestsRecursively(path + "/" + name, isPackageNameinTestng(name));
            }
        }
    }
    
    private boolean isPackageNameinTestng(String dir) {
        return getPackagesInTestng().contains("." + dir + "\" />");
    }
    
    private String getPackagesInTestng() {
        return testngXmlAsString.substring(testngXmlAsString.indexOf("<packages>") + "<packages>".length(), 
                                           testngXmlAsString.indexOf("</packages>"));
    }
}
