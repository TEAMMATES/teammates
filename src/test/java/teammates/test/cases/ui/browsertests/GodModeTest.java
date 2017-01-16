package teammates.test.cases.ui.browsertests;

import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.test.driver.HtmlHelper;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.util.FileHelper;

public class GodModeTest extends BaseUiTestCase {
    
    private static final String PLACEHOLDER_CONTENT = "<div id=\"mainContent\">test</div>";
    private static final String OUTPUT_FILENAME = "/godmodeOutput.html";
    private static final String OUTPUT_FILEPATH = TestProperties.TEST_PAGES_FOLDER + OUTPUT_FILENAME;
    private static final String ACTUAL_FILENAME = "/godmode.html";
    private static final String ACTUAL_FILEPATH = TestProperties.TEST_PAGES_FOLDER + ACTUAL_FILENAME;
    private static final String EXPECTED_FILEPATH = TestProperties.TEST_PAGES_FOLDER + "/godmodeExpectedOutput.html";
    private static final String EXPECTED_PART_FILEPATH =
            TestProperties.TEST_PAGES_FOLDER + "/godmodeExpectedPartOutput.html";
    
    private static Browser browser;
    private static AppPage page;
    private static String initialContent;

    @BeforeClass
    public void classSetup() throws Exception {
        printTestClassHeader();
        TestProperties.verifyReadyForGodMode();
        injectContextDependentValuesIntoActualFile();
        browser = BrowserPool.getBrowser();
        page = AppPage.getNewPageInstance(browser).navigateTo(createLocalUrl(ACTUAL_FILENAME));
    }
    
    private static void injectContextDependentValuesIntoActualFile() throws Exception {
        initialContent = FileHelper.readFile(ACTUAL_FILEPATH);
        String changedContent = HtmlHelper.injectContextDependentValuesForTest(initialContent);
        FileHelper.saveFile(ACTUAL_FILEPATH, changedContent);
    }

    @Test
    public void testGodMode() throws Exception {
        
        System.clearProperty("godmode");
        assertNull(System.getProperty("godmode"));
        
        ______TS("test verifyHtml");
        
        testGodMode(false);
        
        ______TS("test verifyHtmlMainContent");
        
        testGodMode(true);
        
    }
    
    private void testGodMode(boolean isPart) throws Exception {
        
        try {
            // should fail as the expected output file does not exist
            verifyHtml(OUTPUT_FILENAME, isPart);
            signalFailureToDetectException();
        } catch (IOException e) {
            ignoreExpectedException();
        }
        
        // run the God mode with non-existent expected file
        runGodModeRoutine(isPart);
        
        FileHelper.saveFile(OUTPUT_FILEPATH, PLACEHOLDER_CONTENT);
        
        try {
            // should fail as the expected output file has the wrong content
            verifyHtml(OUTPUT_FILENAME, isPart);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            ignoreExpectedException();
        }
        
        // run the God mode with wrong content in expected file
        runGodModeRoutine(isPart);
        
        // delete the output file generated
        FileHelper.deleteFile(OUTPUT_FILEPATH);
    }
    
    private void runGodModeRoutine(boolean isPart) throws Exception {
        
        System.setProperty("godmode", "true");
        // automatically generates the file and hence passes
        verifyHtml(OUTPUT_FILENAME, isPart);
        
        System.clearProperty("godmode");
        assertNull(System.getProperty("godmode"));
        
        // should pass without need for godmode as the file has already been generated
        verifyHtml(OUTPUT_FILENAME, isPart);
        
        String expectedOutputPage = FileHelper.readFile(isPart ? EXPECTED_PART_FILEPATH : EXPECTED_FILEPATH);
        String actualOutputPage = FileHelper.readFile(OUTPUT_FILEPATH);
        
        // ensure that the generated file is as expected
        HtmlHelper.assertSameHtml(expectedOutputPage, actualOutputPage, isPart);
        
    }

    private void verifyHtml(String filePath, boolean isPart) throws IOException {
        if (isPart) {
            page.verifyHtmlMainContent(filePath);
        } else {
            page.verifyHtml(filePath);
        }
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
        System.clearProperty("godmode");
        FileHelper.saveFile(ACTUAL_FILEPATH, initialContent);
    }

}
