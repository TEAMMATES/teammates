package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertEquals;

import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.logic.api.Logic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class AccountsLogicTest extends BaseComponentTestCase {

    private AccountsLogic accountsLogic = AccountsLogic.inst();
    private InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private CoursesLogic coursesLogic = CoursesLogic.inst();
    private Logic logic = new Logic();
    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(AccountsLogic.class);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetInstructorAccounts() throws Exception{
        
        restoreTypicalDataInDatastore();

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
    public void testCreateAccount() throws Exception {

        ______TS("typical success case");

        AccountAttributes accountToCreate = new AccountAttributes("id", "name",
                true, "test@email", "dev");
        accountsLogic.createAccount(accountToCreate);
        TestHelper.verifyPresentInDatastore(accountToCreate);
        
        ______TS("invalid parameters exception case");

        accountToCreate = new AccountAttributes("", "name",
                true, "test@email", "dev");
        try{
            accountsLogic.createAccount(accountToCreate);
            signalFailureToDetectException();
        } catch (InvalidParametersException e){
            ignoreExpectedException();
        }
        
    }

    @Test
    public void testCreateInstructorAccount() throws Exception {

        restoreTypicalDataInDatastore();

        ______TS("success case");

        // Delete any existing
        CourseAttributes cd = dataBundle.courses.get("typicalCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2 = dataBundle.instructors.get("instructor2OfCourse1");
        coursesLogic.deleteCourseCascade(cd.id);
        TestHelper.verifyAbsentInDatastore(cd);
        TestHelper.verifyAbsentInDatastore(instructor);
        TestHelper.verifyAbsentInDatastore(instructor2);
        
        // Create fresh
        coursesLogic.createCourseAndInstructor(instructor.googleId, cd.id, cd.name);
        try {
            AccountAttributes instrAcc = dataBundle.accounts.get("instructor1OfCourse1");
            //the email of instructor and the email of the account are different in test data,
            //hence to test for EntityAlreadyExistsException we need to use the email of the account
            accountsLogic.createInstructorAccount(instructor.googleId, instructor.courseId, instructor.name, instrAcc.email, "National University of Singapore");
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException eaee) {
            // Course must be created with a creator. `instructor` here is our creator, so recreating it should give us EAEE
        }
        // Here we create another INSTRUCTOR for testing our createInstructor() method
        String googleIdWithGmailDomain = instructor2.googleId+"@GMAIL.COM"; //to check if "@GMAIL.COM" is stripped out correctly
        accountsLogic.createInstructorAccount(googleIdWithGmailDomain, instructor2.courseId, instructor2.name, instructor2.email, "National University of Singapore");
        
        // `instructor` here is created with NAME and EMAIL field obtain from his AccountData
        AccountAttributes creator = dataBundle.accounts.get("instructor1OfCourse1");
        instructor.name = creator.name;
        instructor.email = creator.email; 
        TestHelper.verifyPresentInDatastore(cd);
        TestHelper.verifyPresentInDatastore(instructor);
        TestHelper.verifyPresentInDatastore(instructor2);
        
        // Delete fresh
        coursesLogic.deleteCourseCascade(cd.id);
        // read deleted course
        TestHelper.verifyAbsentInDatastore(cd);
        // check for cascade delete
        TestHelper.verifyAbsentInDatastore(instructor);
        TestHelper.verifyAbsentInDatastore(instructor2);
        
        // Delete non-existent (fails silently)
        coursesLogic.deleteCourseCascade(cd.id);
        instructorsLogic.deleteInstructor(instructor.courseId, instructor.googleId);
        instructorsLogic.deleteInstructor(instructor2.courseId, instructor2.googleId);

        ______TS("invalid parameters");

        String googleId = "valid-id";
        
        //ensure no account exist for this instructor
        assertNull(logic.getAccount(googleId));
        
        // Ensure the exception is thrown at logic level
        try {
            accountsLogic.createInstructorAccount(googleId, "invalid courseId", "Valid name", "valid@email.com", "National University of Singapore");
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("invalid courseId",e.getMessage());
        }
        
        //ensure no account exist for this instructor because the operation above failed 
        assertNull(logic.getAccount(googleId));

        ______TS("null parameters");
        
        try {
            logic.createInstructorAccount(null, "valid.courseId", "Valid Name", "valid@email.com", "National University of Singapore");
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
        
        try {
            logic.createInstructorAccount("valid.id", null, "Valid Name", "valid@email.com", "National University of Singapore");
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Logic.ERROR_NULL_PARAMETER, a.getMessage());
        }
    }
    
    @Test
    public void testAccountFunctions() throws Exception {
        restoreTypicalDataInDatastore();
        
        ______TS("test isAccountPresent");

        assertTrue(accountsLogic.isAccountPresent("idOfInstructor1OfCourse1"));
        assertTrue(accountsLogic.isAccountPresent("student1InCourse1"));
        
        assertFalse(accountsLogic.isAccountPresent("id-does-not-exist"));
        
        ______TS("test isAccountAnInstructor");

        assertTrue(accountsLogic.isAccountAnInstructor("idOfInstructor1OfCourse1"));
     
        assertFalse(accountsLogic.isAccountAnInstructor("student1InCourse1"));
        assertFalse(accountsLogic.isAccountAnInstructor("id-does-not-exist"));
        
        ______TS("test getInstructorAccounts");
        
        assertEquals(10, accountsLogic.getInstructorAccounts().size());
        
        ______TS("test updateAccount");
        
        AccountAttributes account = new AccountAttributes("idOfInstructor1OfCourse1", "name",
                true, "test2@email", "dev");
        accountsLogic.updateAccount(account);
        TestHelper.verifyPresentInDatastore(account);
        
        account = new AccountAttributes("id-does-not-exist", "name",
                true, "test2@email", "dev");
        try {
            accountsLogic.updateAccount(account);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            ignoreExpectedException();
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
        
        accountsLogic.makeAccountInstructor("id-does-not-exist");
        assertFalse(accountsLogic.isAccountPresent("id-does-not-exist"));

    }

    @Test
    public void testJoinCourseForStudent() throws Exception {
        restoreTypicalDataInDatastore();
        
        String correctStudentId = "correctStudentId";
        String courseId = "idOfTypicalCourse1";
        String originalEmail = "original@email.com";

        // Create correct student with original@email.com
        StudentAttributes studentData = new StudentAttributes(null,
                originalEmail, "name", "", courseId, "teamName");
        logic.createStudent(studentData);
        studentData = StudentsLogic.inst().getStudentForEmail(courseId,
                originalEmail);
        
        TestHelper.verifyPresentInDatastore(studentData);

        ______TS("failure: wrong key");

        try {
            accountsLogic.joinCourseForStudent("wrongkey", correctStudentId);
            signalFailureToDetectException();
        } catch (JoinCourseException e) {
            assertEquals(
                    "You have used an invalid join link: "
                    + "/page/studentCourseJoin?regkey=wrongkey",
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
                "differentEmail@email.com", "name", "", courseId, "teamName");
        logic.createStudent(existingStudent);
        
        try {
            accountsLogic.joinCourseForStudent(studentData.key, existingId);
            signalFailureToDetectException();
        } catch (JoinCourseException e) {
            assertEquals(String.format(Const.StatusMessages.JOIN_COURSE_GOOGLE_ID_BELONGS_TO_DIFFERENT_USER,
                    existingId), e.getMessage());
        }

        ______TS("success: without encryption and account already exists");

        AccountAttributes accountData = new AccountAttributes(correctStudentId,
                "nameABC", false, "real@gmail.com", "nus");
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
            assertEquals(correctStudentId + " has already joined this course",
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
                courseId, "teamName");
        logic.createStudent(studentData);
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
        logic.createInstructorAccount(correctStudentId, courseId,
                studentData.name, studentData.email, "nus");

        // make the student 'unregistered' again
        studentData.googleId = "";
        logic.updateStudent(studentData.email, studentData);
        assertEquals("",
                logic.getStudentForEmail(studentData.course, studentData.email).googleId);

        // rejoin
        logic.joinCourseForStudent(encryptedKey, correctStudentId);
        assertEquals(correctStudentId,
                logic.getStudentForEmail(studentData.course, studentData.email).googleId);

        // check if still instructor
        assertTrue(logic.isInstructor(correctStudentId));
    }
    
    @Test
    public void testJoinCourseForInstructor() throws Exception {
        restoreTypicalDataInDatastore();
        
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
        
        ______TS("success: instructor joined but account already exists");
        
        AccountAttributes nonInstrAccount = dataBundle.accounts.get("student1InCourse1");
        
        instructorsLogic.addInstructor(instructor.courseId, nonInstrAccount.name, nonInstrAccount.email);
        key = instructorsLogic.getKeyForInstructor(instructor.courseId, nonInstrAccount.email);
        encryptedKey = StringHelper.encrypt(key);
        
        accountsLogic.joinCourseForInstructor(encryptedKey, nonInstrAccount.googleId);
        
        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.courseId, nonInstrAccount.email);
        assertEquals(nonInstrAccount.googleId, joinedInstructor.googleId);
        instructorsLogic.verifyInstructorExists(nonInstrAccount.googleId);
        
        ______TS("failure: instructor already joined");

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
                    + "/page/instructorCourseJoin?regkey=" + invalidKey,
                    e.getMessage());
        }
        
    }

    @Test
    public void testDeleteAccountCascade() throws Exception {

        ______TS("typical success case");

        logic.createInstructorAccount("googleId", "courseId", "name",
                "email@com", "institute");
        InstructorAttributes instructor = logic.getInstructorForGoogleId(
                "courseId", "googleId");
        AccountAttributes account = logic.getAccount("googleId");

        // Make instructor account id a student too.
        StudentAttributes student = new StudentAttributes("googleId",
                "email@com", "name", "",
                "courseId", "team");
        logic.createStudent(student);

        TestHelper.verifyPresentInDatastore(account);
        TestHelper.verifyPresentInDatastore(instructor);
        TestHelper.verifyPresentInDatastore(student);

        accountsLogic.deleteAccountCascade("googleId");

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
