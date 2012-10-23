package teammates.test.cases;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.Common;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

public class WebpageCompilationUiTest extends BaseTestCase {
        private static BrowserInstance bi;
        
        private static String appURL = TestProperties.inst().TEAMMATES_URL;

        @BeforeClass
        public static void classSetup() throws Exception {
                printTestClassHeader();
                
                startRecordingTimeForDataImport();
                String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/WebpageCompilationTest.json");
                BackDoor.deleteCoordinators(jsonString);
                String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
                assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
                reportTimeForDataImport();
                
                bi = BrowserInstancePool.getBrowserInstance();
                
                bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
        }
        
        @AfterClass
        public static void classTearDown() throws Exception {
                BrowserInstancePool.release(bi);
                printTestClassFooter();
        }

        @Test   
        public void ViewWebpageCompilation() throws Exception{   
                bi.goToUrl(appURL+Common.WEBPAGE_COMPILATION);
        }
        
        
}