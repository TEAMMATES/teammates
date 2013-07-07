package teammates.test.cases.common;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;
import static teammates.common.util.Const.EOL;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;

public class AccountAttributesTest extends BaseTestCase {
	
	//TODO: test toString() method
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
	}
	
	@Test
	public void testGetInvalidStateInfo(){
		AccountAttributes account = createValidAccountAttributesObject();
		assertTrue("all valid values", account.isValid());
		
		account = createInvalidAccountAttributesObject();
		String expectedError = "\"\" is not acceptable to TEAMMATES as a person name because it is empty. The value of a person name should be no longer than 40 characters. It should not be empty."+ EOL +
				"\"invalid google id\" is not acceptable to TEAMMATES as a Google ID because it is not in the correct format. A Google ID must be a valid id already registered with Google. It cannot be longer than 45 characters. It cannot be empty."+ EOL +
				"\"invalid@email@com\" is not acceptable to TEAMMATES as an email because it is not in the correct format. An email address contains some text followed by one '@' sign followed by some more text. It cannot be longer than 45 characters. It cannot be empty and it cannot have spaces."+ EOL +
				"\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\" is not acceptable to TEAMMATES as an institute name because it is too long. The value of an institute name should be no longer than 64 characters. It should not be empty.";
		assertEquals("all valid values",false, account.isValid());
		assertEquals("all valid values",expectedError, StringHelper.toString(account.getInvalidityInfo()));
		
	}
	
	@Test
	public void testIsValid(){
		//already tested in testGetInvalidStateInfo()
	}

	private AccountAttributes createInvalidAccountAttributesObject() {
		AccountAttributes account = new AccountAttributes();
		account.googleId = "invalid google id";
		account.name = ""; //invalid name
		account.email = "invalid@email@com";
		account.institute = StringHelper.generateStringOfLength(FieldValidator.INSTITUTE_NAME_MAX_LENGTH+1);
		return account;
	}

	private AccountAttributes createValidAccountAttributesObject() {
		AccountAttributes account = new AccountAttributes();
		account.googleId = "valid.google.id";
		account.name = "valid name";
		account.email = "valid@email.com";
		account.institute = "valid institute name";
		return account;
	}
	
	@AfterClass
	public static void tearDown() {
		printTestClassFooter();
	}


}
