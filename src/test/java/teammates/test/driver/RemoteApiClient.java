package teammates.test.driver;

import java.io.IOException;
import java.util.List;

import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import teammates.storage.entity.Account;

public class RemoteApiClient {
	
	private static PersistenceManager pm = JDOHelper.getPersistenceManagerFactory("transactions-optional").getPersistenceManager();
	
	public static void main(String[] args) throws IOException {
		TestProperties testProperties = TestProperties.inst();
		
		System.out.println("AppID: " + testProperties.TEAMMATES_APP_ID + 
							", username: " + testProperties.TEST_ADMIN_ACCOUNT +
							", password: " + testProperties.TEST_ADMIN_PASSWORD);
		
		RemoteApiOptions options = new RemoteApiOptions()
	    .server(testProperties.TEAMMATES_APP_ID + ".appspot.com", 443)
	    .credentials(testProperties.TEST_ADMIN_ACCOUNT, testProperties.TEST_ADMIN_PASSWORD);
	
		RemoteApiInstaller installer = new RemoteApiInstaller();
		installer.install(options);
		try {	
			//========================================================
			// Execute required code here
			testClientAPI();
			//========================================================
	    } finally {
	        installer.uninstall();
	    }
	}
	
	private static void testClientAPI() {
		String query = "SELECT FROM " + Account.class.getName();
		
		@SuppressWarnings("unchecked")
		List<Account> accountsList = (List<Account>) pm.newQuery(query).execute();
		
		System.out.println("TEST: " + accountsList.size());	
	}
}
