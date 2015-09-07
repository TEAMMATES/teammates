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
    List<String> directoriesTested = new ArrayList<String>();
    String testngXmlAsString;

    @Test
    public void checksTestsInTestNg() throws FileNotFoundException {
        
        // Verify that testng.xml exists
        File f = new File("./src/test/testng.xml");   
        assertTrue(f.exists() && !f.isDirectory());
        testngXmlAsString = FileHelper.readFile("./src/test/testng.xml");
        
        updateDirectoriesTested();
        getTestFiles("./src/test/java/teammates/test");
        excludeFilesNotInTestng();

        for (String test : tests) {
            assertTrue(testngXmlAsString.contains(test));
        }
    }

    /**
     * Get all Java test files from directories which are included in directories tested
     * @param path  path to search in
     */
    private void getTestFiles(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();      

        for (File file : listOfFiles) {
            String name = file.getName();
            
            if (file.isFile() && name.endsWith(".java")) {
                tests.add(name.replace(".java", ""));
                
            } else if (file.isDirectory()) {
            
                if (directoriesTested.contains(name)) {
                    getTestFiles(path + "/" + name);
                } else if (containsNestedDirectory(name)) {
                    
                    List<String> nestedDirs = getAllNestedDirectories(name);
                    
                    for (String nestedDir : nestedDirs) {
                        getTestFiles(path + "/" + nestedDir);
                    }
                }
            }
        }
    }

    
    private void updateDirectoriesTested() {
        directoriesTested.add("cases");
        directoriesTested.add("testdriver");
        directoriesTested.add("ui/browsertests"); // ui not tested but browsertests is tested
    }
    
    private void excludeFilesNotInTestng() {
        tests.remove("BaseComponentTestCase");
        tests.remove("BaseTestCase");
        tests.remove("BaseUiTestCase");
        tests.remove("FeedbackQuestionUiTest");
        tests.remove("GodModeTest");
    }
    
    /**
     * If the current directory is not to be tested but the current directory 
     * contains a sub-directory which should be tested
     */
    private boolean containsNestedDirectory(String currentDirectory) {
        for (String dir : directoriesTested) {
            if (dir.contains(currentDirectory+"/")) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Return a list of nested sub-directories inside the current directory
     * which should be tested
     */
    private List<String> getAllNestedDirectories(String currentDirectory) {
        List<String> nestedDirs = new ArrayList<String>();
        
        for (String dir : directoriesTested) {     
            if (dir.contains(currentDirectory + "/")) {
                nestedDirs.add(dir);
            }
        }
        
        return nestedDirs;
    }
}
