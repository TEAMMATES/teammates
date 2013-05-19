package teammates.test.cases.storage;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountsDb;
import teammates.storage.datastore.Datastore;
import teammates.test.cases.BaseTestCase;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class AccountsDbTest extends BaseTestCase {

	private AccountsDb accountsDb = new AccountsDb();
	private static LocalServiceTestHelper helper;
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(AccountsDb.class);
		Datastore.initialize();
		LocalDatastoreServiceTestConfig localDatastore = new LocalDatastoreServiceTestConfig();
		helper = new LocalServiceTestHelper(localDatastore);
		helper.setUp();
	}
	
	@Test
	public void testCreateAccount() throws InvalidParametersException {
		// SUCCESS
		AccountAttributes a = new AccountAttributes();
		a.googleId = "test.account";
		a.name = "Test account Name";
		a.isInstructor = false;
		a.email = "fresh-account@email.com";
		a.institute = "National University of Singapore";
		accountsDb.createAccount(a);
			
		// SUCCESS : duplicate
		accountsDb.createAccount(a);
		
		// Test for latest entry persistence
		AccountAttributes accountDataTest = accountsDb.getAccount(a.googleId);
		AssertJUnit.assertFalse(accountDataTest.isInstructor);
		// Change a field
		accountDataTest.isInstructor = true;
		accountsDb.updateAccount(accountDataTest);
		// Re-retrieve
		accountDataTest = accountsDb.getAccount(a.googleId);
		AssertJUnit.assertTrue(accountDataTest.isInstructor);
		
		// FAIL : invalid parameters
		// Should we not allow empty fields?
		/*
		a.email = "invalid email";
		try {
			accountsDb.createAccount(a);
			fail();
		} catch (AssertionError a) {
			assertEquals(a.getMessage(), AccountData.ERROR_FIELD_EMAIL);
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
		*/
		
		// Null parameters check:
		try {
			accountsDb.createAccount(null);
			Assert.fail();
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, ae.getMessage());
		}
	}
	
	@Test
	public void testGetAccount() throws InvalidParametersException {
		AccountAttributes a = createNewAccount();
		
		// Get existent
		AccountAttributes retrieved = accountsDb.getAccount(a.googleId);
		AssertJUnit.assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = accountsDb.getAccount("non.existent");
		AssertJUnit.assertNull(retrieved);
		
		// Null params check:
		try {
			accountsDb.getAccount(null);
			Assert.fail();
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, ae.getMessage());
		}
	}
	
	@Test
	public void testEditAccount() throws InvalidParametersException {
		AccountAttributes a = createNewAccount();
		
		// Edit existent
		a.name = "Edited name";
		accountsDb.updateAccount(a);
		
		// Edit non-existent
		try {
			a.googleId = "non.existent";
			accountsDb.updateAccount(a);
			Assert.fail();
		} catch (AssertionError ae) {
			assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT_ACCOUNT, ae.getMessage());
		}
		
		// Null parameters check:
		// Only check first 2 parameters (course & email) which are used to identify the student entry. The rest are actually allowed to be null.
		try {
			accountsDb.updateAccount(null);
			Assert.fail();
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, ae.getMessage());
		}
	}
	
	@Test
	public void testDeleteAccount() throws InvalidParametersException {
		AccountAttributes a = createNewAccount();
		
		// Delete
		accountsDb.deleteAccount(a.googleId);
		
		AccountAttributes deleted = accountsDb.getAccount(a.googleId);
		AssertJUnit.assertNull(deleted);
		
		// delete again - should fail silently
		accountsDb.deleteAccount(a.googleId);
		
		// Null parameters check:
		try {
			accountsDb.deleteAccount(null);
			Assert.fail();
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, ae.getMessage());
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(AccountsDb.class);
		helper.tearDown();
	}
	
	private AccountAttributes createNewAccount() throws InvalidParametersException {
		AccountAttributes a = new AccountAttributes();
		a.googleId = "valid.googleId";
		a.name = "Valid Fresh Account";
		a.isInstructor = false;
		a.email = "valid@email.com";
		a.institute = "National University of Singapore";
		
		accountsDb.createAccount(a);
		return a;
	}
}
