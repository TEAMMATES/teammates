package teammates.test.driver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

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
			//doOperation();
			appendInstitutionForAccounts();
			//undoAppendInstitutionForAccounts();
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
	
	protected static void testManyWriteOperations() {
		long start = System.currentTimeMillis();
		int count = 0;
		
		System.out.println("Adding entitites...");
		while ((System.currentTimeMillis() - start) < 120000) {
			Account newAccount = new Account("account" + count, "test" + count, false, "account" + count + "@gmail.com", "Foo university");
			pm.makePersistent(newAccount);
			count++;
		}
		
		count--;
		
		System.out.println("Deleting entitites...");
		while (count != -1) {
			String query = "SELECT FROM " + Account.class.getName() + " WHERE googleId == 'account" + count + "'";
			@SuppressWarnings("unchecked")
			List<Account> accountsList = (List<Account>) pm.newQuery(query).execute();
			if (accountsList.size() > 0) {
				pm.deletePersistent(accountsList.get(0));
			}
			count--;
		}
		
		System.out.println("Total time of execution: " + (System.currentTimeMillis() - start));
	}
	
	protected static void testLongWriteOperation() {
		List<Account> accountsList = new ArrayList<Account>();
		
		System.out.println("Adding entitites...");
		for (int count = 0; count < 10000; count++) {
			Account newAccount = new Account("account" + count, "test" + count, false, "account" + count + "@gmail.com", "Foo university");
			accountsList.add(newAccount);
		}

		long start = System.currentTimeMillis();
		pm.makePersistentAll(accountsList);
		System.out.println("Total time of execution: " + (System.currentTimeMillis() - start));
	}
	
	protected static void testLongReadOperation() {
		String query = "SELECT FROM " + Account.class.getName() + " WHERE institute == 'Foo university'";
		
		long start = System.currentTimeMillis();
		@SuppressWarnings("unchecked")
		List<Account> accountsList = (List<Account>) pm.newQuery(query).execute();
		System.out.println("Read execution: " + (System.currentTimeMillis() - start) + ", returned " + accountsList.size() + " entities");
		
		// Note: DELETION consumes Datastore WRITE operation QUOTA
		pm.deletePersistentAll(accountsList);
		System.out.println("Total execution: " + (System.currentTimeMillis() - start));
	}
	
	public static void undoAppendInstitutionForAccounts() {
		String query = "SELECT FROM " + Account.class.getName();
		@SuppressWarnings("unchecked")
		List<Account> accountsList = (List<Account>) pm.newQuery(query).execute();
		
		int count = 0;
		for (Account a : accountsList) {
			if ((a.getInstitute() != null) && (!a.getInstitute().equals(""))) {
				a.setInstitute("");
				pm.deletePersistent(a);
				pm.makePersistent(a);
				count++;
			}
		}
		System.out.println("Undid " + count + " entitites from " + accountsList.size() + " entities");
	}

	public static void appendInstitutionForAccounts() {
		// Instructor Accounts get Institute for an Instructor
		String query = "select from " + Account.class.getName()
				+ " where isInstructor == true";
		
		@SuppressWarnings("unchecked")
		List<Account> instructorAccounts = (List<Account>) pm.newQuery(query).execute();
		
		HashMap<String, String> instructorInstitutions = new HashMap<String, String>();
		
		for (Account a : instructorAccounts) {
			if (a.getInstitute() == null || a.getInstitute().isEmpty()) {
				a.setInstitute("National University of Singapore");
			}
			instructorInstitutions.put(a.getGoogleId(), a.getInstitute());
		}
		
		System.out.println("Finished mapping instructor-institutions: " + instructorInstitutions.size());
		
		//======================================================================
		// Given Institute for Instructor create Course-Institute pair
		query = "select from " + Instructor.class.getName();
		
		@SuppressWarnings("unchecked")
		List<Instructor> instructors = (List<Instructor>) pm.newQuery(query).execute();
		
		HashMap<String, String> courseInstitutions = new HashMap<String, String>();
		
		for (Instructor i : instructors) {
			courseInstitutions.put(i.getCourseId(), instructorInstitutions.get(i.getGoogleId()));
		}
		
		System.out.println("Finished mapping course-institutions: " + courseInstitutions.size());
		
		//======================================================================
		// Given Course-Institute Pair create Student-Institute Pair
		query = "select from " + Student.class.getName()
				+ " where ID != null";
		
		@SuppressWarnings("unchecked")
		List<Student> students = (List<Student>) pm.newQuery(query).execute();
		
		HashMap<String, String> studentInstitutions = new HashMap<String, String>();
		
		for (Student s : students) {
			studentInstitutions.put(s.getID(), courseInstitutions.get(s.getCourseID()));
		}
		
		System.out.println("Finished mapping student-institutions: " + studentInstitutions.size());
		
		//======================================================================
		// *******************************************************************
		// Can test the functionality up to this point to check correctness of mapping
		// Only the following loop does the appending
		// *******************************************************************
		//======================================================================
		// Student Accounts append Institute from Student-Institute pair		
		int count = 0;
		for (String id : studentInstitutions.keySet()) {
			query = "select from " + Account.class.getName()
					+ " where googleId == \"" + id + "\"";
			
			@SuppressWarnings("unchecked")
			List<Account> studentAccounts = (List<Account>) pm.newQuery(query).execute();
			if (studentAccounts.size() > 0) {
				Account a = studentAccounts.get(0);
				if (a.getInstitute() == null || a.getInstitute().equals("")) {
					Account newA = new Account(a.getGoogleId(), a.getName(), false, a.getEmail(), studentInstitutions.get(a.getGoogleId()));
					pm.deletePersistent(a);
					pm.makePersistent(newA);
					count++;
				}
			}
		}
		
		System.out.println("Appended for " + count + " entities");
	}
}
