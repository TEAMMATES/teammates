package teammates.test.cases.logic;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertEquals;

import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.blobstore.BlobKey;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.StringHelper;
import teammates.logic.api.Logic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.AccountsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.Priority;
import teammates.test.util.TestHelper;

public class AccountsLogicTest extends BaseComponentTestCase {

    private AccountsLogic accountsLogic = AccountsLogic.inst();
    private InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private StudentsLogic studentsLogic = StudentsLogic.inst();
    private Logic logic = new Logic();
    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(AccountsLogic.class);
        removeAndRestoreTypicalDataInDatastore();
        
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetInstructorAccounts() throws Exception{
        
        ______TS("success case");
        
        List<AccountAttributes> instructorAccounts = logic.getInstructorAccounts();
        int size = instructorAccounts.size();
        
        logic.createAccount("test.account", "Test Account", true, "test@account.com", "Foo University");
        instructorAccounts = logic.getInstructorAccounts();
        assertEquals(instructorAccounts.size(), size + 1);
        
        logic.deleteAccount("test.account");
        instructorAccounts = logic.getInstructorAccounts();
        assertEquals(instructorAccounts.size(), size);
    }
 
    @Test
    public void testStudentProfileFunctions() throws Exception {
        
        // 4 functions are tested together as:
        //      => The functions are very simple (one-liners)
        //      => They are fundamentally related and easily tested together
        //      => It saves time during tests
        
        ______TS("get SP");
        StudentProfileAttributes expectedSpa = new StudentProfileAttributes("id", "shortName", "personal@email.com", 
                "institute", "countryName", "female", "moreInfo", "");
        AccountAttributes accountWithStudentProfile = new AccountAttributes("id", "name",
                true, "test@email.com", "dev", expectedSpa);
        
        accountsLogic.createAccount(accountWithStudentProfile);
        
        StudentProfileAttributes actualSpa = accountsLogic.getStudentProfile(accountWithStudentProfile.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;        
        assertEquals(expectedSpa.toString(), actualSpa.toString());
        
        ______TS("update SP");
        
        expectedSpa.pictureKey = "non-empty";
        accountWithStudentProfile.studentProfile.pictureKey = expectedSpa.pictureKey;
        accountsLogic.updateStudentProfile(accountWithStudentProfile.studentProfile);
        
        actualSpa = accountsLogic.getStudentProfile(accountWithStudentProfile.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;        
        assertEquals(expectedSpa.toString(), actualSpa.toString());
        
        ______TS("update picture");
        
        expectedSpa.pictureKey = GoogleCloudStorageHelper
                .writeFileToGcs(expectedSpa.googleId, "src/test/resources/images/profile_pic.png", "");
        accountsLogic.updateStudentProfilePicture(expectedSpa.googleId, expectedSpa.pictureKey);
        actualSpa = accountsLogic.getStudentProfile(accountWithStudentProfile.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;
        assertEquals(expectedSpa.toString(), actualSpa.toString());
        
        ______TS("delete profile picture");
        
        accountsLogic.deleteStudentProfilePicture(expectedSpa.googleId);
        assertFalse(GoogleCloudStorageHelper.doesFileExistInGcs(new BlobKey(expectedSpa.pictureKey)));
        
        actualSpa = accountsLogic.getStudentProfile(accountWithStudentProfile.googleId);
        expectedSpa.modifiedDate = actualSpa.modifiedDate;
        expectedSpa.pictureKey = "";
        assertEquals(expectedSpa.toString(), actualSpa.toString());
        
        // remove the account that was created
        accountsLogic.deleteAccountCascade("id");
    }
    
    @Test
    public void testDeletePicture() throws Exception {
        String keyString = GoogleCloudStorageHelper.writeFileToGcs("accountsLogicTestid", "src/test/resources/images/profile_pic.png", "");
        BlobKey key = new BlobKey(keyString);
        accountsLogic.deletePicture(key);
        assertFalse(GoogleCloudStorageHelper.doesFileExistInGcs(key));
    }

    @Test
    public void testCreateAccount() throws Exception {

        ______TS("typical success case");
        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.googleId = "id";
        spa.shortName = "test acc na";
        spa.email = "test@personal.com";
        spa.gender = Const.GenderTypes.MALE;
        spa.nationality = "test.nationality";
        spa.institute = "institute";
        spa.moreInfo = "this is more info";
        
        AccountAttributes accountToCreate = new AccountAttributes("id", "name",
                true, "test@email", "dev", spa);
        
        accountsLogic.createAccount(accountToCreate);
        TestHelper.verifyPresentInDatastore(accountToCreate);
        
        accountsLogic.deleteAccountCascade("id");
        
        ______TS("invalid parameters exception case");

        accountToCreate = new AccountAttributes("", "name",
                true, "test@email", "dev", spa);
        try{
            accountsLogic.createAccount(accountToCreate);
            signalFailureToDetectException();
        } catch (InvalidParametersException e){
            ignoreExpectedException();
        }
        
    }
    
    @Priority(-1)
    @Test
    public void testAccountFunctions() throws Exception {
        
        ______TS("test isAccountPresent");

        assertTrue(accountsLogic.isAccountPresent("idOfInstructor1OfCourse1"));
        assertTrue(accountsLogic.isAccountPresent("student1InCourse1"));
        
        assertFalse(accountsLogic.isAccountPresent("id-does-not-exist"));
        
        ______TS("test isAccountAnInstructor");

        assertTrue(accountsLogic.isAccountAnInstructor("idOfInstructor1OfCourse1"));
     
        assertFalse(accountsLogic.isAccountAnInstructor("student1InCourse1"));
        assertFalse(accountsLogic.isAccountAnInstructor("id-does-not-exist"));
        
        ______TS("test getInstructorAccounts");
        
        
        for(AccountAttributes aa : accountsLogic.getInstructorAccounts()){
            ______TS(aa.toString());
        }
        
        assertEquals(11, accountsLogic.getInstructorAccounts().size());
        
        ______TS("test updateAccount");
        
        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.googleId = "idOfInstructor1OfCourse1";
        spa.institute = "dev";
        spa.shortName = "nam";
        
        AccountAttributes expectedAccount = new AccountAttributes("idOfInstructor1OfCourse1", "name",
                true, "test2@email", "dev", spa);
        
        // updates the profile
        accountsLogic.updateAccount(expectedAccount, true);
        AccountAttributes actualAccount = accountsLogic.getAccount(expectedAccount.googleId, true);
        expectedAccount.studentProfile.modifiedDate = actualAccount.studentProfile.modifiedDate;
        expectedAccount.createdAt = actualAccount.createdAt;
        assertEquals(expectedAccount.toString(), actualAccount.toString());
        
        // does not update the profile
        expectedAccount.studentProfile.shortName = "newNam";
        accountsLogic.updateAccount(expectedAccount);
        actualAccount = accountsLogic.getAccount(expectedAccount.googleId, true);
        
        // no change in the name
        assertEquals("nam", actualAccount.studentProfile.shortName);
        
        expectedAccount = new AccountAttributes("id-does-not-exist", "name",
                true, "test2@email", "dev", spa);
        try {
            accountsLogic.updateAccount(expectedAccount);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            AssertHelper.assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT_ACCOUNT, edne.getMessage());
        }
        
        ______TS("test downgradeInstructorToStudentCascade");
        
        accountsLogic.downgradeInstructorToStudentCascade("idOfInstructor2OfCourse1");
        assertFalse(accountsLogic.isAccountAnInstructor("idOfInstructor2OfCourse1"));
        
        accountsLogic.downgradeInstructorToStudentCascade("student1InCourse1");
        assertFalse(accountsLogic.isAccountAnInstructor("student1InCourse1"));
        
        accountsLogic.downgradeInstructorToStudentCascade("id-does-not-exist");
        assertFalse(accountsLogic.isAccountPresent("id-does-not-exist"));
        
        ______TS("test makeAccountInstructor");
        
        accountsLogic.makeAccountInstructor("student2InCourse1");
        assertTrue(accountsLogic.isAccountAnInstructor("student2InCourse1"));
        accountsLogic.downgradeInstructorToStudentCascade("student2InCourse1");
        
        accountsLogic.makeAccountInstructor("id-does-not-exist");
        assertFalse(accountsLogic.isAccountPresent("id-does-not-exist"));

    }

    @Test
    public void testJoinCourseForStudent() throws Exception {
        
        String correctStudentId = "correctStudentId";
        String courseId = "idOfTypicalCourse1";
        String originalEmail = "original@email.com";

        // Create correct student with original@email.com
        StudentAttributes studentData = new StudentAttributes(null,
                originalEmail, "name", "", courseId, "teamName", "sectionName");
        studentsLogic.createStudentCascadeWithoutDocument(studentData);
        studentData = StudentsLogic.inst().getStudentForEmail(courseId,
                originalEmail);
        
        TestHelper.verifyPresentInDatastore(studentData);

        ______TS("failure: wrong key");

        try {
            accountsLogic.joinCourseForStudent("wrongkey", correctStudentId);
            signalFailureToDetectException();
        } catch (JoinCourseException e) {
            assertEquals(
                    "You have used an invalid join link: %s",
                    e.getMessage());
        }

        ______TS("failure: invalid parameters");

        try {
            accountsLogic.joinCourseForStudent(studentData.key, "wrong student");
            signalFailureToDetectException();
        } catch (JoinCourseException e) {
            AssertHelper.assertContains(FieldValidator.REASON_INCORRECT_FORMAT,
                    e.getMessage());
        }
        
        ______TS("failure: googleID belongs to an existing student in the course");
        
        String existingId = "AccLogicT.existing.studentId";
        StudentAttributes existingStudent = new StudentAttributes(existingId,
                "differentEmail@email.com", "name", "", courseId, "teamName", "sectionName");
        studentsLogic.createStudentCascadeWithoutDocument(existingStudent);
        
        try {
            accountsLogic.joinCourseForStudent(studentData.key, existingId);
            signalFailureToDetectException();
        } catch (JoinCourseException e) {
            assertEquals(String.format(Const.StatusMessages.JOIN_COURSE_GOOGLE_ID_BELONGS_TO_DIFFERENT_USER,
                    existingId), e.getMessage());
        }

        ______TS("success: without encryption and account already exists");

        StudentProfileAttributes spa = new StudentProfileAttributes(correctStudentId,
                "ABC", "personal@gmail.com", "nus", "Singapore", "male", "", "");
        
        AccountAttributes accountData = new AccountAttributes(correctStudentId,
                "nameABC", false, "real@gmail.com", "nus", spa);
        
        accountsLogic.createAccount(accountData);
        accountsLogic.joinCourseForStudent(studentData.key, correctStudentId);

        studentData.googleId = accountData.googleId;
        TestHelper.verifyPresentInDatastore(studentData);
        assertEquals(
                correctStudentId,
                logic.getStudentForEmail(studentData.course, studentData.email).googleId);

        ______TS("failure: already joined");

        try {
            accountsLogic.joinCourseForStudent(studentData.key, correctStudentId);
            signalFailureToDetectException();
        } catch (JoinCourseException e) {
            assertEquals("You (" + correctStudentId + ") have already joined this course",
                    e.getMessage());
        }

        ______TS("failure: valid key belongs to a different user");

        try {
            accountsLogic.joinCourseForStudent(studentData.key, "wrongstudent");
            signalFailureToDetectException();
        } catch (JoinCourseException e) {
            assertEquals("The join link used belongs to a different user whose "
                    + "Google ID is corre..dentId (only part of the Google ID is "
                    + "shown to protect privacy). If that Google ID is owned by you, "
                    + "please logout and re-login using that Google account. "
                    + "If it doesn’t belong to you, please "
                    + "<a href=\"mailto:teammates@comp.nus.edu.sg?"
                    + "body=Your name:%0AYour course:%0AYour university:\">"
                    + "contact us</a> so that we can investigate.",
                    e.getMessage());
        }

        ______TS("success: with encryption and new account to be created");

        logic.deleteAccount(correctStudentId);
        
        originalEmail = "email2@gmail.com";
        studentData = new StudentAttributes(null, originalEmail, "name", "",
                courseId, "teamName", "sectionName");
        studentsLogic.createStudentCascadeWithoutDocument(studentData);
        studentData = StudentsLogic.inst().getStudentForEmail(courseId,
                originalEmail);

        String encryptedKey = StringHelper.encrypt(studentData.key);
        accountsLogic.joinCourseForStudent(encryptedKey, correctStudentId);
        studentData.googleId = correctStudentId;
        TestHelper.verifyPresentInDatastore(studentData);
        assertEquals(correctStudentId,
                logic.getStudentForEmail(studentData.course, studentData.email).googleId);

        // check that we have the corresponding new account created.
        accountData.googleId = correctStudentId;
        accountData.email = originalEmail;
        accountData.name = "name";
        accountData.isInstructor = false;
        TestHelper.verifyPresentInDatastore(accountData);
        
        ______TS("success: join course as student does not revoke instructor status");

        // promote account to instructor
        accountsLogic.makeAccountInstructor(correctStudentId);

        // make the student 'unregistered' again
        studentData.googleId = "";
        studentsLogic.updateStudentCascadeWithoutDocument(studentData.email, studentData);
        assertEquals("",
                logic.getStudentForEmail(studentData.course, studentData.email).googleId);

        // rejoin
        logic.joinCourseForStudent(encryptedKey, correctStudentId);
        assertEquals(correctStudentId,
                logic.getStudentForEmail(studentData.course, studentData.email).googleId);

        // check if still instructor
        assertTrue(logic.isInstructor(correctStudentId));
        
        accountsLogic.deleteAccountCascade(correctStudentId);
        accountsLogic.deleteAccountCascade(existingId);
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testJoinCourseForInstructor() throws Exception {
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructorNotYetJoinCourse");
        String loggedInGoogleId = "AccLogicT.instr.id";
        String key = instructorsLogic.getKeyForInstructor(instructor.courseId, instructor.email);
        String encryptedKey = StringHelper.encrypt(key);
        
        ______TS("failure: googleID belongs to an existing instructor in the course");

        try {
            accountsLogic.joinCourseForInstructor(encryptedKey, "idOfInstructorWithOnlyOneSampleCourse");
            signalFailureToDetectException();
        } catch (JoinCourseException e) {
            assertEquals(String.format(Const.StatusMessages.JOIN_COURSE_GOOGLE_ID_BELONGS_TO_DIFFERENT_USER,
                    "idOfInstructorWithOnlyOneSampleCourse"), e.getMessage());
        }
        
        ______TS("success: instructor joined and new account be created");
        
        accountsLogic.joinCourseForInstructor(encryptedKey, loggedInGoogleId);
        
        InstructorAttributes joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.courseId, instructor.email);
        assertEquals(loggedInGoogleId, joinedInstructor.googleId);
        
        AccountAttributes accountCreated = accountsLogic.getAccount(loggedInGoogleId);
        Assumption.assertNotNull(accountCreated);
        
        
        ______TS("success: instructor joined but Account object creation goes wrong");
        
        //Delete account to simulate Account object creation goes wrong
        AccountsDb accountsDb = new AccountsDb();
        accountsDb.deleteAccount(loggedInGoogleId);
        
        //Try to join course again, Account object should be recreated
        accountsLogic.joinCourseForInstructor(encryptedKey, loggedInGoogleId);
        
        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.courseId, instructor.email);
        assertEquals(loggedInGoogleId, joinedInstructor.googleId);
        
        accountCreated = accountsLogic.getAccount(loggedInGoogleId);
        Assumption.assertNotNull(accountCreated);
        
        accountsLogic.deleteAccountCascade(loggedInGoogleId);
        
        ______TS("success: instructor joined but account already exists");
        
        AccountAttributes nonInstrAccount = dataBundle.accounts.get("student1InCourse1");
        InstructorAttributes newIns = new InstructorAttributes (null, instructor.courseId, nonInstrAccount.name, nonInstrAccount.email);
        
        instructorsLogic.createInstructor(newIns);
        key = instructorsLogic.getKeyForInstructor(instructor.courseId, nonInstrAccount.email);
        encryptedKey = StringHelper.encrypt(key);
        assertFalse(accountsLogic.getAccount(nonInstrAccount.googleId).isInstructor);
        
        accountsLogic.joinCourseForInstructor(encryptedKey, nonInstrAccount.googleId);
        
        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.courseId, nonInstrAccount.email);
        assertEquals(nonInstrAccount.googleId, joinedInstructor.googleId);
        assertTrue(accountsLogic.getAccount(nonInstrAccount.googleId).isInstructor);
        instructorsLogic.verifyInstructorExists(nonInstrAccount.googleId);
        
        
        ______TS("success: instructor join and assigned institute when some instructors have not joined course");
        
        instructor = dataBundle.instructors.get("instructor4");
        newIns = new InstructorAttributes (null, instructor.courseId, "anInstructorWithoutGoogleId", "anInstructorWithoutGoogleId@gmail.com");
        
        instructorsLogic.createInstructor(newIns);  
        
        nonInstrAccount = dataBundle.accounts.get("student2InCourse1");
        nonInstrAccount.email = "newInstructor@gmail.com";
        nonInstrAccount.name = " newInstructor";
        nonInstrAccount.googleId = "newInstructorGoogleId";
        newIns = new InstructorAttributes (null, instructor.courseId, nonInstrAccount.name, nonInstrAccount.email);
        
        instructorsLogic.createInstructor(newIns);
        key = instructorsLogic.getKeyForInstructor(instructor.courseId, nonInstrAccount.email);
        encryptedKey = StringHelper.encrypt(key);
        
        accountsLogic.joinCourseForInstructor(encryptedKey, nonInstrAccount.googleId);
        
        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.courseId, nonInstrAccount.email);
        assertEquals(nonInstrAccount.googleId, joinedInstructor.googleId);
        instructorsLogic.verifyInstructorExists(nonInstrAccount.googleId);
        
        AccountAttributes instructorAccount = accountsLogic.getAccount(nonInstrAccount.googleId);
        assertEquals("TEAMMATES Test Institute 1", instructorAccount.institute);
        
        accountsLogic.deleteAccountCascade(nonInstrAccount.googleId);
        
        
        ______TS("failure: instructor already joined");
        
        nonInstrAccount = dataBundle.accounts.get("student1InCourse1");
        instructor = dataBundle.instructors.get("instructorNotYetJoinCourse");
        
        key = instructorsLogic.getKeyForInstructor(instructor.courseId, nonInstrAccount.email);
        encryptedKey = StringHelper.encrypt(key);
        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.courseId, nonInstrAccount.email);
        
        try {
            accountsLogic.joinCourseForInstructor(encryptedKey, joinedInstructor.googleId);
            signalFailureToDetectException();
        } catch (JoinCourseException e) {
            assertEquals(joinedInstructor.googleId + " has already joined this course",
                    e.getMessage());
        }
        
        ______TS("failure: key belongs to a different user");

        try {
            accountsLogic.joinCourseForInstructor(encryptedKey, "otherUserId");
            signalFailureToDetectException();
        } catch (JoinCourseException e) {
            assertEquals("The join link used belongs to a different user whose "
                    + "Google ID is stude..ourse1 (only part of the Google ID is "
                    + "shown to protect privacy). If that Google ID is owned by you, "
                    + "please logout and re-login using that Google account. "
                    + "If it doesn’t belong to you, please "
                    + "<a href=\"mailto:teammates@comp.nus.edu.sg?"
                    + "body=Your name:%0AYour course:%0AYour university:\">"
                    + "contact us</a> so that we can investigate.",
                    e.getMessage());
        }
        
        ______TS("failure: invalid key");
        String invalidKey = StringHelper.encrypt("invalidKey");
        
        try {
            accountsLogic.joinCourseForInstructor(invalidKey, loggedInGoogleId);
            signalFailureToDetectException();
        } catch (JoinCourseException e) {
            assertEquals(
                    "You have used an invalid join link: "
                    + "/page/instructorCourseJoin?key=" + invalidKey,
                    e.getMessage());
        }        
    }

    @Test
    public void testDeleteAccountCascade() throws Exception {
        
        ______TS("typical success case");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor5");
        AccountAttributes account = dataBundle.accounts.get("instructor5");

        // Make instructor account id a student too.
        StudentAttributes student = new StudentAttributes(instructor.googleId,
                "email@com", instructor.name, "", instructor.courseId, "team", "section");
        studentsLogic.createStudentCascadeWithoutDocument(student);
        TestHelper.verifyPresentInDatastore(account);
        TestHelper.verifyPresentInDatastore(instructor);
        TestHelper.verifyPresentInDatastore(student);

        accountsLogic.deleteAccountCascade(instructor.googleId);

        TestHelper.verifyAbsentInDatastore(account);
        TestHelper.verifyAbsentInDatastore(instructor);
        TestHelper.verifyAbsentInDatastore(student);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        turnLoggingDown(AccountsLogic.class);
    }
    
    //TODO: add missing test cases
}
