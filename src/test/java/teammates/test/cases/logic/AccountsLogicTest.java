package teammates.test.cases.logic;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.logic.api.Logic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

public class AccountsLogicTest extends BaseComponentTestCase {

    private AccountsLogic accountsLogic = AccountsLogic.inst();
    private InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private Logic logic = new Logic();
    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(AccountsLogic.class);
    }


    @Test
    public void testCreateAccount() throws Exception {

        ______TS("typical success case");

        AccountAttributes accountToCreate = new AccountAttributes("id", "name",
                true, "test@email", "dev");
        accountsLogic.createAccount(accountToCreate);
        LogicTestHelper.verifyPresentInDatastore(accountToCreate);
    }

    @Test
    public void testJoinCourseForStudent() throws Exception {
        String correctStudentId = "correctStudentId";
        String courseId = "courseId";
        String originalEmail = "original@email.com";

        // Create correct student with original@email.com
        StudentAttributes studentData = new StudentAttributes(null,
                originalEmail, "name", "", courseId, "teamName");
        logic.createStudent(studentData);
        studentData = StudentsLogic.inst().getStudentForEmail(courseId,
                originalEmail);

        LogicTestHelper.verifyPresentInDatastore(studentData);

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
        LogicTestHelper.verifyPresentInDatastore(studentData);
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

        logic.deleteStudent(courseId, originalEmail);
        
        originalEmail = "email2@gmail.com";
        studentData = new StudentAttributes(null, originalEmail, "name", "",
                courseId, "teamName");
        logic.createStudent(studentData);
        studentData = StudentsLogic.inst().getStudentForEmail(courseId,
                originalEmail);

        String encryptedKey = StringHelper.encrypt(studentData.key);
        accountsLogic.joinCourseForStudent(encryptedKey, correctStudentId);
        studentData.googleId = correctStudentId;
        LogicTestHelper.verifyPresentInDatastore(studentData);
        assertEquals(correctStudentId,
                logic.getStudentForEmail(studentData.course, studentData.email).googleId);

        // check that we have the corresponding new account created.
        accountData.googleId = correctStudentId;
        accountData.email = originalEmail;
        accountData.name = "name";
        accountData.isInstructor = false;
        LogicTestHelper.verifyPresentInDatastore(accountData);

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

        LogicTestHelper.verifyPresentInDatastore(account);
        LogicTestHelper.verifyPresentInDatastore(instructor);
        LogicTestHelper.verifyPresentInDatastore(student);

        accountsLogic.deleteAccountCascade("googleId");

        LogicTestHelper.verifyAbsentInDatastore(account);
        LogicTestHelper.verifyAbsentInDatastore(instructor);
        LogicTestHelper.verifyAbsentInDatastore(student);

    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        turnLoggingDown(AccountsLogic.class);
    }
    
}
