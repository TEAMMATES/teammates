package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.StringHelper;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.ProfilesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class AccountsDbTest extends BaseComponentTestCase {

    private AccountsDb accountsDb = new AccountsDb();
    private ProfilesDb profilesDb = new ProfilesDb();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(AccountsDb.class);
    }
    
    @Test
    public void testGetAccount() throws Exception {
        AccountAttributes a = createNewAccount();
        
        ______TS("typical success case without");
        AccountAttributes retrieved = accountsDb.getAccount(a.googleId);
        assertNotNull(retrieved);
        assertNull(retrieved.studentProfile);
        
        ______TS("typical success with student profile");
        retrieved = accountsDb.getAccount(a.googleId, true);
        assertNotNull(retrieved);
        assertNotNull(a.studentProfile);
        
        ______TS("expect null for non-existent account");
        retrieved = accountsDb.getAccount("non.existent");
        assertNull(retrieved);
        
        ______TS("failure: null parameter");
        try {
            accountsDb.getAccount(null);
            signalFailureToDetectException(" - AssertionError");
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }
    
    @Test
    public void testGetInstructorAccounts() throws Exception {
        int numOfInstructors = 3;
        
        // a non-instructor account
        createNewAccount();
        
        List<AccountAttributes> instructorAccountsExpected = createInstructorAccounts(numOfInstructors);
        List<AccountAttributes> instructorAccountsActual = accountsDb.getInstructorAccounts();
        
        assertEquals(numOfInstructors, instructorAccountsActual.size());
        TestHelper.isSameContentIgnoreOrder(instructorAccountsExpected, instructorAccountsActual);
        
        deleteInstructorAccounts(numOfInstructors);
    }
    
    private List<AccountAttributes> createInstructorAccounts(
            int numOfInstructors) throws Exception {
        AccountAttributes a;
        List<AccountAttributes> result = new ArrayList<AccountAttributes>();
        for (int i = 0; i < numOfInstructors ; i++) {
            a = getNewAccountAttributes();
            a.googleId = "id." + i;
            a.isInstructor = true;
            accountsDb.createAccount(a);
            result.add(a);
        }
        return result;
    }
    
    private void deleteInstructorAccounts(int numOfInstructors) {
        String googleId;
        for (int i = 0; i < numOfInstructors ; i++) {
            googleId = "id." + i;
            accountsDb.deleteAccount(googleId);
        }
    }

    @Test
    public void testCreateAccount() throws Exception {
        
        ______TS("typical success case (legacy data)");
        AccountAttributes a = new AccountAttributes();
        
        a.googleId = "test.account";
        a.name = "Test account Name";
        a.isInstructor = false;
        a.email = "fresh-account@email.com";
        a.institute = "TEAMMATES Test Institute 1";
        a.studentProfile = null;
        
        accountsDb.createAccount(a);
            
        ______TS("success case: duplicate account");
        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.shortName = "test acc na";
        spa.email = "test@personal.com";
        spa.gender = Const.GenderTypes.MALE;
        spa.nationality = "test.nationality";
        spa.institute = "institute";
        spa.moreInfo = "this is more info";
        spa.googleId = a.googleId;
        
        a.studentProfile = spa;
        
        accountsDb.createAccount(a);
        
        ______TS("test persistence of latest entry");
        AccountAttributes accountDataTest = accountsDb.getAccount(a.googleId, true);
        
        assertEquals(spa.shortName, accountDataTest.studentProfile.shortName);
        assertEquals(spa.gender, accountDataTest.studentProfile.gender);
        assertEquals(spa.institute, accountDataTest.studentProfile.institute);
        assertEquals(a.institute, accountDataTest.institute);
        assertEquals(spa.email, accountDataTest.studentProfile.email);
        
        assertFalse(accountDataTest.isInstructor);
        // Change a field
        accountDataTest.isInstructor = true;
        accountDataTest.studentProfile.gender = Const.GenderTypes.FEMALE;
        accountsDb.createAccount(accountDataTest);
        // Re-retrieve
        accountDataTest = accountsDb.getAccount(a.googleId, true);
        assertTrue(accountDataTest.isInstructor);
        assertEquals(Const.GenderTypes.FEMALE, accountDataTest.studentProfile.gender);
        
        accountsDb.deleteAccount(a.googleId);

        // Should we not allow empty fields?
        ______TS("failure case: invalid parameter");
        a.email = "invalid email";
        try {
            accountsDb.createAccount(a);
            signalFailureToDetectException(" - InvalidParametersException");
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    String.format(FieldValidator.EMAIL_ERROR_MESSAGE,
                    "invalid email",
                    FieldValidator.REASON_INCORRECT_FORMAT),
                    e.getMessage());
        }
        
        ______TS("failure: null parameter");
        try {
            accountsDb.createAccount(null);
            signalFailureToDetectException(" - AssertionError");
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }
    
    @Test
    public void testEditAccount() throws Exception {
        AccountAttributes a = createNewAccount();
        
        ______TS("typical edit success case (legacy data)");
        a.name = "Edited name";
        a.studentProfile = null;
        accountsDb.updateAccount(a);
        
        AccountAttributes actualAccount = accountsDb.getAccount(a.googleId, true);
        
        assertEquals(a.name, actualAccount.name);
        
        ______TS("typical success case (with profile)");
        
        a.studentProfile.shortName = "Edite";
        accountsDb.updateAccount(a, true);
        
        actualAccount = accountsDb.getAccount(a.googleId, true);        
        assertEquals(a.studentProfile.shortName, actualAccount.studentProfile.shortName);
        
        ______TS("success: profile not modified in the default case");
        
        Date expectedModifiedDate = actualAccount.studentProfile.modifiedDate;
        
        String expectedNationality = actualAccount.studentProfile.nationality;
        actualAccount.studentProfile.nationality = "New Nationality";
        actualAccount.institute = "newer institute";
        
        accountsDb.updateAccount(actualAccount);
        a = accountsDb.getAccount(a.googleId, true);
        
        // ensure update was successful
        assertEquals(actualAccount.institute, a.institute);
        // ensure profile was not updated
        assertEquals(expectedModifiedDate, a.studentProfile.modifiedDate);
        assertEquals(expectedNationality, a.studentProfile.nationality);
        
        
        ______TS("success: modified date does not change if profile is not changed");
        
        actualAccount = accountsDb.getAccount(a.googleId, true);
        actualAccount.institute = "new institute";
        
        accountsDb.updateAccount(actualAccount);
        a = accountsDb.getAccount(a.googleId, true);
        
        // ensure update was successful
        assertEquals(actualAccount.institute, a.institute);
        // ensure modified date was not updated
        assertEquals(expectedModifiedDate, a.studentProfile.modifiedDate);
        
        ______TS("non-existent account");
        
        try {
            a.googleId = "non.existent";
            accountsDb.updateAccount(a);
            signalFailureToDetectException(" - EntityDoesNotExistException");
        } catch (EntityDoesNotExistException edne) {
            AssertHelper.assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT_ACCOUNT, edne.getMessage());
        }
        
        ______TS("failure: invalid parameters");
        
        a.googleId = "";
        a.email = "test-no-at-funny.com";
        a.name = "%asdf";
        a.institute = StringHelper.generateStringOfLength(65);
        a.studentProfile.shortName = "??";
        
        try {
            accountsDb.updateAccount(a);
            signalFailureToDetectException(" - InvalidParametersException");
        } catch (InvalidParametersException ipe) {
            assertEquals(StringHelper.toString(a.getInvalidityInfo()), ipe.getMessage());
        }
        
        // Only check first 2 parameters (course & email) which are used to identify the student entry. The rest are actually allowed to be null.
        ______TS("failure: null parameter");
        try {
            accountsDb.updateAccount(null);
            signalFailureToDetectException(" - AssertionError");
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }
    
    @Test
    public void testDeleteAccount() throws Exception {
        AccountAttributes a = createNewAccount();
        a.studentProfile.pictureKey = GoogleCloudStorageHelper.writeFileToGcs(a.googleId, "src/test/resources/images/profile_pic_default.png", "");
        profilesDb.updateStudentProfilePicture(a.googleId, a.studentProfile.pictureKey);
        
        ______TS("typical success case");
        AccountAttributes newAccount = accountsDb.getAccount(a.googleId);
        assertNotNull(newAccount);
        
        accountsDb.deleteAccount(a.googleId);
        
        AccountAttributes newAccountdeleted = accountsDb.getAccount(a.googleId);
        assertNull(newAccountdeleted);
        
        StudentProfileAttributes deletedProfile = profilesDb.getStudentProfile(a.googleId);
        assertNull(deletedProfile);
        
        assertFalse(GoogleCloudStorageHelper.doesFileExistInGcs(a.googleId, true));
        
        ______TS("silent deletion of same account");
        accountsDb.deleteAccount(a.googleId);
        
        ______TS("failure null paramter");
        
        try {
            accountsDb.deleteAccount(null);
            signalFailureToDetectException(" - AssertionError");
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }

    private AccountAttributes createNewAccount() throws Exception {
        AccountAttributes a = getNewAccountAttributes();
        accountsDb.createAccount(a);
        return a;
    }
    
    private AccountAttributes getNewAccountAttributes() throws Exception {
        AccountAttributes a = new AccountAttributes();
        a.googleId = "valid.googleId";
        a.name = "Valid Fresh Account";
        a.isInstructor = false;
        a.email = "valid@email.com";
        a.institute = "TEAMMATES Test Institute 1";
        a.studentProfile = new StudentProfileAttributes();
        a.studentProfile.googleId = a.googleId;
        a.studentProfile.institute = "TEAMMATES Test Institute 1";
        
        return a;
    }
}
