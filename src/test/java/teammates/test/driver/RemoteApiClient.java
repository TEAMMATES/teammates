package teammates.test.driver;

import java.io.IOException;
import java.util.List;

import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import teammates.storage.entity.Account;

public class RemoteApiClient {

	private static PersistenceManager pm = JDOHelper
			.getPersistenceManagerFactory("transactions-optional")
			.getPersistenceManager();

	public static void main(String[] args) throws IOException {
		doOperationRemotely();
	}

	private static void doOperationRemotely() throws IOException {
		TestProperties testProperties = TestProperties.inst();

		System.out.println("--- Starting remote operation ---");
		System.out.println("Going to connect to:"
				+ testProperties.TEAMMATES_REMOTEAPI_APP_DOMAIN + ":"
				+ testProperties.TEAMMATES_REMOTEAPI_APP_PORT);

		RemoteApiOptions options = new RemoteApiOptions().server(
				testProperties.TEAMMATES_REMOTEAPI_APP_DOMAIN,
				testProperties.TEAMMATES_REMOTEAPI_APP_PORT).credentials(
				testProperties.TEST_ADMIN_ACCOUNT,
				testProperties.TEST_ADMIN_PASSWORD);

		RemoteApiInstaller installer = new RemoteApiInstaller();
		installer.install(options);
		try {
			doOperation();
		} finally {
			installer.uninstall();
		}

		System.out.println("--- Remote operation completed ---");
	}

	/**
	 * This operation is meant to be overridden by child classes.
	 */
	protected static void doOperation() {
		String query = "SELECT FROM " + Account.class.getName();

		@SuppressWarnings("unchecked")
		List<Account> accountsList = (List<Account>) pm.newQuery(query)
				.execute();

		System.out.println("Account count: " + accountsList.size());
	}
}
