package teammates.logic.core;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.storage.api.AccountsDb;
import teammates.test.AssertHelper;

/**
 * SUT: {@link AccountsLogic}.
 */
public class AccountsLogicTest extends BaseLogicTest {

    private static final AccountsLogic accountsLogic = AccountsLogic.inst();
    private static final AccountsDb accountsDb = new AccountsDb();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final ProfilesLogic profilesLogic = ProfilesLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
    }

    private String getEncryptedKeyForInstructor(String courseId, String email)
            throws EntityDoesNotExistException {
        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, email);
        return StringHelper.encrypt(instructor.key);
    }

    @Test
    public void testCreateAccount() throws Exception {

        ______TS("typical success case");

        AccountAttributes accountToCreate = AccountAttributes.builder("id")
                .withName("name")
                .withEmail("test@email.com")
                .withInstitute("dev")
                .withIsInstructor(true)
                .build();

        accountsLogic.createAccount(accountToCreate);
        verifyPresentInDatastore(accountToCreate);

        accountsLogic.deleteAccountCascade("id");

        ______TS("invalid parameters exception case");

        accountToCreate = AccountAttributes.builder("")
                .withName("name")
                .withEmail("test@email.com")
                .withInstitute("dev")
                .withIsInstructor(true)
                .build();
        AccountAttributes[] finalAccount = new AccountAttributes[] { accountToCreate };
        assertThrows(InvalidParametersException.class, () -> accountsLogic.createAccount(finalAccount[0]));

    }

    @Test
    public void testAccountFunctions() throws Exception {

        ______TS("test isAccountAnInstructor");

        assertTrue(accountsLogic.isAccountAnInstructor("idOfInstructor1OfCourse1"));

        assertFalse(accountsLogic.isAccountAnInstructor("student1InCourse1"));
        assertFalse(accountsLogic.isAccountAnInstructor("id-does-not-exist"));

        ______TS("test downgradeInstructorToStudentCascade");

        accountsLogic.downgradeInstructorToStudentCascade("idOfInstructor2OfCourse1");
        assertFalse(accountsLogic.isAccountAnInstructor("idOfInstructor2OfCourse1"));

        accountsLogic.downgradeInstructorToStudentCascade("student1InCourse1");
        assertFalse(accountsLogic.isAccountAnInstructor("student1InCourse1"));

        assertThrows(EntityDoesNotExistException.class, () -> {
            accountsLogic.downgradeInstructorToStudentCascade("id-does-not-exist");
        });

        ______TS("test makeAccountInstructor");

        accountsLogic.makeAccountInstructor("student2InCourse1");
        assertTrue(accountsLogic.isAccountAnInstructor("student2InCourse1"));
        accountsLogic.downgradeInstructorToStudentCascade("student2InCourse1");

        assertThrows(EntityDoesNotExistException.class, () -> {
            accountsLogic.makeAccountInstructor("id-does-not-exist");
        });
    }

    @Test
    public void testJoinCourseForStudent() throws Exception {

        String correctStudentId = "correctStudentId";
        String courseId = "idOfTypicalCourse1";
        String originalEmail = "original@email.com";

        // Create correct student with original@email.com
        StudentAttributes studentData = StudentAttributes
                .builder(courseId, originalEmail)
                .withName("name")
                .withSectionName("sectionName")
                .withTeamName("teamName")
                .withComment("")
                .build();
        studentsLogic.createStudent(studentData);
        studentData = studentsLogic.getStudentForEmail(courseId,
                originalEmail);
        StudentAttributes finalStudent = studentData;

        verifyPresentInDatastore(studentData);

        ______TS("failure: wrong key");

        String wrongKey = StringHelper.encrypt("wrongkey");
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourseForStudent(wrongKey, correctStudentId));
        assertEquals("No student with given registration key: " + wrongKey, ednee.getMessage());

        ______TS("failure: invalid parameters");

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountsLogic.joinCourseForStudent(StringHelper.encrypt(finalStudent.key), "wrong student"));
        AssertHelper.assertContains(FieldValidator.REASON_INCORRECT_FORMAT, ipe.getMessage());

        ______TS("failure: googleID belongs to an existing student in the course");

        String existingId = "AccLogicT.existing.studentId";
        StudentAttributes existingStudent = StudentAttributes
                .builder(courseId, "differentEmail@email.com")
                .withName("name")
                .withSectionName("sectionName")
                .withTeamName("teamName")
                .withComment("")
                .withGoogleId(existingId)
                .build();
        studentsLogic.createStudent(existingStudent);

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForStudent(StringHelper.encrypt(finalStudent.key), existingId));
        assertEquals("Student has already joined course", eaee.getMessage());

        ______TS("success: without encryption and account already exists");

        AccountAttributes accountData = AccountAttributes.builder(correctStudentId)
                .withName("nameABC")
                .withEmail("real@gmail.com")
                .withInstitute("TEAMMATES Test Institute 1")
                .withIsInstructor(true)
                .build();

        accountsLogic.createAccount(accountData);
        accountsLogic.joinCourseForStudent(StringHelper.encrypt(studentData.key), correctStudentId);

        studentData.googleId = accountData.googleId;
        verifyPresentInDatastore(studentData);
        assertEquals(
                correctStudentId,
                studentsLogic.getStudentForEmail(studentData.course, studentData.email).googleId);

        ______TS("failure: already joined");

        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForStudent(StringHelper.encrypt(finalStudent.key), correctStudentId));
        assertEquals("Student has already joined course", eaee.getMessage());

        ______TS("failure: valid key belongs to a different user");

        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForStudent(StringHelper.encrypt(finalStudent.key), "wrongstudent"));
        assertEquals("Student has already joined course", eaee.getMessage());

        ______TS("success: with encryption and new account to be created");

        accountsLogic.deleteAccountCascade(correctStudentId);

        originalEmail = "email2@gmail.com";
        studentData = StudentAttributes
                .builder(courseId, originalEmail)
                .withName("name")
                .withSectionName("sectionName")
                .withTeamName("teamName")
                .withComment("")
                .build();
        studentsLogic.createStudent(studentData);
        studentData = studentsLogic.getStudentForEmail(courseId,
                originalEmail);

        String encryptedKey = StringHelper.encrypt(studentData.key);
        accountsLogic.joinCourseForStudent(encryptedKey, correctStudentId);
        studentData.googleId = correctStudentId;
        verifyPresentInDatastore(studentData);
        assertEquals(correctStudentId,
                studentsLogic.getStudentForEmail(studentData.course, studentData.email).googleId);

        // check that we have the corresponding new account created.
        accountData.googleId = correctStudentId;
        accountData.email = originalEmail;
        accountData.name = "name";
        accountData.isInstructor = false;
        verifyPresentInDatastore(accountData);

        ______TS("success: join course as student does not revoke instructor status");

        // promote account to instructor
        accountsLogic.makeAccountInstructor(correctStudentId);

        // make the student 'unregistered' again
        studentData.googleId = "";
        studentsLogic.updateStudentCascade(
                StudentAttributes.updateOptionsBuilder(studentData.course, studentData.email)
                        .withGoogleId(studentData.googleId)
                        .build()
        );
        assertEquals("",
                studentsLogic.getStudentForEmail(studentData.course, studentData.email).googleId);

        // rejoin
        accountsLogic.joinCourseForStudent(encryptedKey, correctStudentId);
        assertEquals(correctStudentId,
                studentsLogic.getStudentForEmail(studentData.course, studentData.email).googleId);

        // check if still instructor
        assertTrue(accountsLogic.isAccountAnInstructor(correctStudentId));

        accountsLogic.deleteAccountCascade(correctStudentId);
        accountsLogic.deleteAccountCascade(existingId);
    }

    @Test
    public void testJoinCourseForInstructor() throws Exception {

        InstructorAttributes instructor = dataBundle.instructors.get("instructorNotYetJoinCourse");
        String loggedInGoogleId = "AccLogicT.instr.id";
        String[] encryptedKey = new String[] {
                getEncryptedKeyForInstructor(instructor.courseId, instructor.email),
        };

        ______TS("failure: googleID belongs to an existing instructor in the course");

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForInstructor(
                        encryptedKey[0], "idOfInstructorWithOnlyOneSampleCourse", null, null));
        assertEquals("Instructor has already joined course", eaee.getMessage());

        ______TS("success: instructor joined and new account be created");

        accountsLogic.joinCourseForInstructor(encryptedKey[0], loggedInGoogleId, null, null);

        InstructorAttributes joinedInstructor =
                instructorsLogic.getInstructorForEmail(instructor.courseId, instructor.email);
        assertEquals(loggedInGoogleId, joinedInstructor.googleId);

        AccountAttributes accountCreated = accountsLogic.getAccount(loggedInGoogleId);
        assertNotNull(accountCreated);

        ______TS("success: instructor joined but Account object creation goes wrong");

        //Delete account to simulate Account object creation goes wrong
        accountsDb.deleteAccount(loggedInGoogleId);

        //Try to join course again, Account object should be recreated
        accountsLogic.joinCourseForInstructor(encryptedKey[0], loggedInGoogleId, null, null);

        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.courseId, instructor.email);
        assertEquals(loggedInGoogleId, joinedInstructor.googleId);

        accountCreated = accountsLogic.getAccount(loggedInGoogleId);
        assertNotNull(accountCreated);

        accountsLogic.deleteAccountCascade(loggedInGoogleId);

        ______TS("success: instructor joined but account already exists");

        AccountAttributes nonInstrAccount = dataBundle.accounts.get("student1InCourse1");
        InstructorAttributes newIns = InstructorAttributes
                .builder(instructor.courseId, nonInstrAccount.email)
                .withName(nonInstrAccount.name)
                .build();

        instructorsLogic.createInstructor(newIns);
        encryptedKey[0] = getEncryptedKeyForInstructor(instructor.courseId, nonInstrAccount.email);
        assertFalse(accountsLogic.getAccount(nonInstrAccount.googleId).isInstructor);

        accountsLogic.joinCourseForInstructor(encryptedKey[0], nonInstrAccount.googleId, null, null);

        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.courseId, nonInstrAccount.email);
        assertEquals(nonInstrAccount.googleId, joinedInstructor.googleId);
        assertTrue(accountsLogic.getAccount(nonInstrAccount.googleId).isInstructor);
        assertTrue(accountsLogic.isAccountAnInstructor(nonInstrAccount.googleId));

        ______TS("success: instructor join and assigned institute when some instructors have not joined course");

        instructor = dataBundle.instructors.get("instructor4");
        newIns = InstructorAttributes
                .builder(instructor.courseId, "anInstructorWithoutGoogleId@gmail.com")
                .withName("anInstructorWithoutGoogleId")
                .build();

        instructorsLogic.createInstructor(newIns);

        nonInstrAccount = dataBundle.accounts.get("student2InCourse1");
        nonInstrAccount.email = "newInstructor@gmail.com";
        nonInstrAccount.name = " newInstructor";
        nonInstrAccount.googleId = "newInstructorGoogleId";
        newIns = InstructorAttributes.builder(instructor.courseId, nonInstrAccount.email)
                .withName(nonInstrAccount.name)
                .build();

        instructorsLogic.createInstructor(newIns);
        encryptedKey[0] = getEncryptedKeyForInstructor(instructor.courseId, nonInstrAccount.email);

        accountsLogic.joinCourseForInstructor(encryptedKey[0], nonInstrAccount.googleId, null, null);

        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.courseId, nonInstrAccount.email);
        assertEquals(nonInstrAccount.googleId, joinedInstructor.googleId);
        assertTrue(accountsLogic.isAccountAnInstructor(nonInstrAccount.googleId));

        AccountAttributes instructorAccount = accountsLogic.getAccount(nonInstrAccount.googleId);
        assertEquals("TEAMMATES Test Institute 1", instructorAccount.institute);

        accountsLogic.deleteAccountCascade(nonInstrAccount.googleId);

        ______TS("failure: instructor already joined");

        nonInstrAccount = dataBundle.accounts.get("student1InCourse1");
        instructor = dataBundle.instructors.get("instructorNotYetJoinCourse");

        encryptedKey[0] = getEncryptedKeyForInstructor(instructor.courseId, nonInstrAccount.email);
        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.courseId, nonInstrAccount.email);
        InstructorAttributes[] finalInstructor = new InstructorAttributes[] { joinedInstructor };
        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForInstructor(encryptedKey[0], finalInstructor[0].googleId, null, null));
        assertEquals("Instructor has already joined course", eaee.getMessage());

        ______TS("failure: key belongs to a different user");

        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForInstructor(encryptedKey[0], "otherUserId", null, null));
        assertEquals("Instructor has already joined course", eaee.getMessage());

        ______TS("failure: invalid key");
        String invalidKey = StringHelper.encrypt("invalidKey");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourseForInstructor(invalidKey, loggedInGoogleId, null, null));
        assertEquals("No instructor with given registration key: " + invalidKey,
                ednee.getMessage());
    }

    @Test
    public void testJoinCourseForInstructor_validInstitute_shouldPass()
            throws EntityDoesNotExistException, InvalidParametersException, EntityAlreadyExistsException {
        InstructorAttributes instructor = dataBundle.instructors.get("instructorNotYetJoinCourse");
        String loggedInGoogleId = "AccLogicT.instr.id";
        String institute = "National University of Singapore";
        String[] encryptedKey = new String[] {
                getEncryptedKeyForInstructor(instructor.courseId, instructor.email),
        };

        ______TS("success: instructor with institute joined and new account created");

        accountsLogic.joinCourseForInstructor(encryptedKey[0], loggedInGoogleId,
                institute, StringHelper.generateSignature(institute));

        InstructorAttributes joinedInstructor =
                instructorsLogic.getInstructorForEmail(instructor.courseId, instructor.email);
        assertEquals(loggedInGoogleId, joinedInstructor.googleId);

        AccountAttributes accountCreated = accountsLogic.getAccount(loggedInGoogleId);
        assertNotNull(accountCreated);
    }

    @Test
    public void testJoinCourseForInstructor_invalidInstituteMac_shouldFail() throws EntityDoesNotExistException {
        InstructorAttributes instructor = dataBundle.instructors.get("instructorNotYetJoinCourse");
        String loggedInGoogleId = "AccLogicT.instr.id";
        String institute = "National University of Singapore";
        String[] encryptedKey = new String[] {
                getEncryptedKeyForInstructor(instructor.courseId, instructor.email),
        };

        ______TS("failure: institute signature does not match institute provided");

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountsLogic.joinCourseForInstructor(
                        encryptedKey[0], loggedInGoogleId, institute, StringHelper.generateSignature("NUS")));
        assertEquals("Institute authentication failed.", ipe.getMessage());

        AccountAttributes accountCreated = accountsLogic.getAccount(loggedInGoogleId);
        assertNull(accountCreated);
    }

    @Test
    public void testJoinCourseForInstructor_missingInstituteMac_shouldFail() throws EntityDoesNotExistException {
        InstructorAttributes instructor = dataBundle.instructors.get("instructorNotYetJoinCourse");
        String loggedInGoogleId = "AccLogicT.instr.id";
        String institute = "National University of Singapore";
        String[] encryptedKey = new String[] {
                getEncryptedKeyForInstructor(instructor.courseId, instructor.email),
        };

        ______TS("failure: institute signature missing");

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountsLogic.joinCourseForInstructor(
                        encryptedKey[0], loggedInGoogleId, institute, null));
        assertEquals("Institute authentication failed.", ipe.getMessage());

        AccountAttributes accountCreated = accountsLogic.getAccount(loggedInGoogleId);
        assertNull(accountCreated);
    }

    @Test
    public void testDeleteAccountCascade_lastInstructorInCourse_shouldDeleteOrphanCourse() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor5");
        AccountAttributes account = dataBundle.accounts.get("instructor5");
        // create a profile for the account
        StudentProfileAttributes studentProfile = StudentProfileAttributes.builder(account.googleId)
                .withShortName("Test")
                .build();
        profilesLogic.updateOrCreateStudentProfile(
                StudentProfileAttributes.updateOptionsBuilder(account.googleId)
                        .withShortName(studentProfile.shortName)
                        .build());

        // verify the instructor is the last instructor of a course
        assertEquals(1, instructorsLogic.getInstructorsForCourse(instructor.getCourseId()).size());

        // Make instructor account id a student too.
        StudentAttributes student = StudentAttributes
                .builder(instructor.courseId, "email@test.com")
                .withName(instructor.name)
                .withSectionName("section")
                .withTeamName("team")
                .withComment("")
                .withGoogleId(instructor.googleId)
                .build();
        studentsLogic.createStudent(student);
        verifyPresentInDatastore(account);
        verifyPresentInDatastore(studentProfile);
        verifyPresentInDatastore(instructor);
        verifyPresentInDatastore(student);

        accountsLogic.deleteAccountCascade(instructor.googleId);

        verifyAbsentInDatastore(account);
        verifyAbsentInDatastore(studentProfile);
        verifyAbsentInDatastore(instructor);
        verifyAbsentInDatastore(student);
        // course is deleted because it is the last instructor of the course
        assertNull(coursesLogic.getCourse(instructor.getCourseId()));
    }

    @Test
    public void testDeleteAccountCascade_notLastInstructorInCourse_shouldNotDeleteCourse() {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        // verify the instructor is not the last instructor of a course
        assertTrue(instructorsLogic.getInstructorsForCourse(instructor1OfCourse1.getCourseId()).size() > 1);

        assertNotNull(instructor1OfCourse1.getGoogleId());
        accountsLogic.deleteAccountCascade(instructor1OfCourse1.getGoogleId());

        // course is not deleted
        assertNotNull(coursesLogic.getCourse(instructor1OfCourse1.getCourseId()));
    }

    @Test
    public void testDeleteAccountCascade_instructorArchivedAsLastInstructor_shouldDeleteCourseAlso() throws Exception {
        InstructorAttributes instructor5 = dataBundle.instructors.get("instructor5");

        assertNotNull(instructor5.getGoogleId());
        instructorsLogic.setArchiveStatusOfInstructor(instructor5.getGoogleId(), instructor5.getCourseId(), true);

        // verify the instructor is the last instructor of a course
        assertEquals(1, instructorsLogic.getInstructorsForCourse(instructor5.getCourseId()).size());

        assertTrue(
                instructorsLogic.getInstructorForEmail(instructor5.getCourseId(), instructor5.getEmail()).isArchived);

        accountsLogic.deleteAccountCascade(instructor5.getGoogleId());

        // the archived instructor is also deleted
        assertNull(instructorsLogic.getInstructorForEmail(instructor5.getCourseId(), instructor5.getEmail()));
        // the course is also deleted
        assertNull(coursesLogic.getCourse(instructor5.getCourseId()));
    }

    @Test
    public void testDeleteAccountCascade_nonExistentAccount_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        accountsLogic.deleteAccountCascade("not_exist");

        // other irrelevant instructors remain
        assertNotNull(instructorsLogic.getInstructorForEmail(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail()));
    }
}
