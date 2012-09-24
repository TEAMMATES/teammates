package teammates.test.scripts;

import java.io.FileNotFoundException;

import org.mortbay.log.Log;

import teammates.common.Common;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

public class WebpageCompilation {

public static void main(String args[]) throws FileNotFoundException{
		BrowserInstance bi;
	
		String appURL = TestProperties.inst().TEAMMATES_URL;
		String jsonStr = Common.readFile(Common.TEST_DATA_FOLDER+"/WebpageCompilationTest.json");
		BackDoor.deleteCoordinators(jsonStr);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonStr);
		if (backDoorOperationStatus != Common.BACKEND_STATUS_SUCCESS){
			Log.warn("Error persisting data bundle");
		}
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin("teammates.test", "makeitright");
		
		bi.goToUrl(appURL+Common.WEBPAGE_COMPILATION);
	}
}
