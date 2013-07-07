package teammates.test.cases.storage;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.api.AccountsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

public class AccountsDbTest extends BaseComponentTestCase {
	
	//TODO: add missing test cases, refine existing ones. Follow the example
	//  of CoursesDbTest::testCreateCourse().

	private AccountsDb accountsDb = new AccountsDb();
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(AccountsDb.class);
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
		
		a.email = "invalid email";
		try {
			accountsDb.createAccount(a);
			signalFailureToDetectException();
		} catch (InvalidParametersException e) {
			AssertHelper.assertContains(
					String.format(FieldValidator.EMAIL_ERROR_MESSAGE,
					"invalid email",
					FieldValidator.REASON_INCORRECT_FORMAT),
					e.getMessage());
		}
		
		
		// Null parameters check:
		try {
			accountsDb.createAccount(null);
			Assert.fail();
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
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
			AssertJUnit.assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
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
			AssertHelper.assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT_ACCOUNT, ae.getMessage());
		}
		
		// Null parameters check:
		// Only check first 2 parameters (course & email) which are used to identify the student entry. The rest are actually allowed to be null.
		try {
			accountsDb.updateAccount(null);
			Assert.fail();
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
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
			AssertJUnit.assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
		}
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
