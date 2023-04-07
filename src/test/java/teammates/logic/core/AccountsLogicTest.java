package teammates.logic.core;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
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

    private final AccountsLogic accountsLogic = AccountsLogic.inst();
    private final AccountsDb accountsDb = AccountsDb.inst();
    private final CoursesLogic coursesLogic = CoursesLogic.inst();
    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private final StudentsLogic studentsLogic = StudentsLogic.inst();

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
    }

    private String getKeyForInstructor(String courseId, String email) {
        return instructorsLogic.getInstructorForEmail(courseId, email).getKey();
    }

    @Test
    public void testCreateAccount() throws Exception {

        ______TS("typical success case");

        AccountAttributes accountToCreate = AccountAttributes.builder("id")
                .withName("name")
                .withEmail("test@email.com")
                .build();

        accountsLogic.createAccount(accountToCreate);
        verifyPresentInDatabase(accountToCreate);

        accountsLogic.deleteAccountCascade("id");

        ______TS("invalid parameters exception case");

        accountToCreate = AccountAttributes.builder("")
                .withName("name")
                .withEmail("test@email.com")
                .build();
        AccountAttributes[] finalAccount = new AccountAttributes[] { accountToCreate };
        assertThrows(InvalidParametersException.class, () -> accountsLogic.createAccount(finalAccount[0]));

    }

    @Test
    public void testGetAccountsForEmail() throws Exception {
        ______TS("typical success case: no accounts with email");
        List<AccountAttributes> accounts = accountsDb.getAccountsForEmail("test@email.com");

        assertTrue(accounts.isEmpty());

        ______TS("typical success case: one account with email");
        AccountAttributes firstAccount = AccountAttributes.builder("first.googleId")
                .withName("name")
                .withEmail("test@email.com")
                .build();
        accountsDb.createEntity(firstAccount);

        accounts = accountsDb.getAccountsForEmail("test@email.com");

        assertEquals(List.of(firstAccount), accounts);

        ______TS("typical success case: multiple accounts with email");
        AccountAttributes secondAccount = AccountAttributes.builder("second.googleId")
                .withName("name")
                .withEmail("test@email.com")
                .build();
        accountsDb.createEntity(secondAccount);
        AccountAttributes thirdAccount = AccountAttributes.builder("third.googleId")
                .withName("name")
                .withEmail("test@email.com")
                .build();
        accountsDb.createEntity(thirdAccount);

        accounts = accountsDb.getAccountsForEmail("test@email.com");

        assertEquals(3, accounts.size());
        assertTrue(List.of(firstAccount, secondAccount, thirdAccount).containsAll(accounts));

        // delete created accounts
        accountsDb.deleteAccount(firstAccount.getGoogleId());
        accountsDb.deleteAccount(secondAccount.getGoogleId());
        accountsDb.deleteAccount(thirdAccount.getGoogleId());
    }

    @Test
    public void testJoinCourseForStudent() throws Exception {

        String correctStudentId = "correctStudentId";
        String courseId = "idOfTypicalCourse1";
        String deletedCourseId = "idOfTypicalCourse3";
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

        verifyPresentInDatabase(studentData);

        ______TS("failure: wrong key");

        String wrongKey = StringHelper.encrypt("wrongkey");
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourseForStudent(wrongKey, correctStudentId));
        assertEquals("No student with given registration key: " + wrongKey, ednee.getMessage());

        ______TS("failure: invalid parameters");

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountsLogic.joinCourseForStudent(finalStudent.getKey(), "wrong student"));
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
                () -> accountsLogic.joinCourseForStudent(finalStudent.getKey(), existingId));
        assertEquals("Student has already joined course", eaee.getMessage());

        ______TS("success: without encryption and account already exists");

        AccountAttributes accountData = AccountAttributes.builder(correctStudentId)
                .withName("nameABC")
                .withEmail("real@gmail.com")
                .build();

        accountsLogic.createAccount(accountData);
        accountsLogic.joinCourseForStudent(studentData.getKey(), correctStudentId);

        studentData.setGoogleId(accountData.getGoogleId());
        verifyPresentInDatabase(studentData);
        assertEquals(
                correctStudentId,
                studentsLogic.getStudentForEmail(studentData.getCourse(), studentData.getEmail()).getGoogleId());

        ______TS("failure: already joined");

        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForStudent(finalStudent.getKey(), correctStudentId));
        assertEquals("Student has already joined course", eaee.getMessage());

        ______TS("failure: valid key belongs to a different user");

        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForStudent(finalStudent.getKey(), "wrongstudent"));
        assertEquals("Student has already joined course", eaee.getMessage());

        ______TS("failure: course deleted");

        studentData.setCourse(deletedCourseId);
        studentData = studentsLogic.createStudent(studentData);
        StudentAttributes finalDeletedCourseStudent = studentData;

        ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourseForStudent(finalDeletedCourseStudent.getKey(),
                        finalDeletedCourseStudent.getGoogleId()));
        assertEquals("The course you are trying to join has been deleted by an instructor", ednee.getMessage());

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

        String key = studentData.getKey();
        accountsLogic.joinCourseForStudent(key, correctStudentId);
        studentData.setGoogleId(correctStudentId);
        verifyPresentInDatabase(studentData);
        assertEquals(correctStudentId,
                studentsLogic.getStudentForEmail(studentData.getCourse(), studentData.getEmail()).getGoogleId());

        // check that we have the corresponding new account created.
        accountData.setGoogleId(correctStudentId);
        accountData.setEmail(originalEmail);
        accountData.setName("name");
        verifyPresentInDatabase(accountData);

        accountsLogic.deleteAccountCascade(correctStudentId);
        accountsLogic.deleteAccountCascade(existingId);
    }

    @Test
    public void testJoinCourseForInstructor() throws Exception {
        String deletedCourseId = "idOfTypicalCourse3";
        InstructorAttributes instructor = dataBundle.instructors.get("instructorNotYetJoinCourse");
        String loggedInGoogleId = "AccLogicT.instr.id";
        String[] key = new String[] {
                getKeyForInstructor(instructor.getCourseId(), instructor.getEmail()),
        };

        ______TS("failure: googleID belongs to an existing instructor in the course");

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForInstructor(
                        key[0], "idOfInstructorWithOnlyOneSampleCourse"));
        assertEquals("Instructor has already joined course", eaee.getMessage());

        ______TS("success: instructor joined and new account be created");

        accountsLogic.joinCourseForInstructor(key[0], loggedInGoogleId);

        InstructorAttributes joinedInstructor =
                instructorsLogic.getInstructorForEmail(instructor.getCourseId(), instructor.getEmail());
        assertEquals(loggedInGoogleId, joinedInstructor.getGoogleId());

        AccountAttributes accountCreated = accountsLogic.getAccount(loggedInGoogleId);
        assertNotNull(accountCreated);

        ______TS("success: instructor joined but Account object creation goes wrong");

        //Delete account to simulate Account object creation goes wrong
        accountsDb.deleteAccount(loggedInGoogleId);

        //Try to join course again, Account object should be recreated
        accountsLogic.joinCourseForInstructor(key[0], loggedInGoogleId);

        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.getCourseId(), instructor.getEmail());
        assertEquals(loggedInGoogleId, joinedInstructor.getGoogleId());

        accountCreated = accountsLogic.getAccount(loggedInGoogleId);
        assertNotNull(accountCreated);

        accountsLogic.deleteAccountCascade(loggedInGoogleId);

        ______TS("success: instructor joined but account already exists");

        AccountAttributes nonInstrAccount = dataBundle.accounts.get("student1InCourse1");
        InstructorAttributes newIns = InstructorAttributes
                .builder(instructor.getCourseId(), nonInstrAccount.getEmail())
                .withName(nonInstrAccount.getName())
                .build();

        instructorsLogic.createInstructor(newIns);
        key[0] = getKeyForInstructor(instructor.getCourseId(), nonInstrAccount.getEmail());

        accountsLogic.joinCourseForInstructor(key[0], nonInstrAccount.getGoogleId());

        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.getCourseId(), nonInstrAccount.getEmail());
        assertEquals(nonInstrAccount.getGoogleId(), joinedInstructor.getGoogleId());

        ______TS("success: instructor join and assigned institute when some instructors have not joined course");

        instructor = dataBundle.instructors.get("instructor4");
        newIns = InstructorAttributes
                .builder(instructor.getCourseId(), "anInstructorWithoutGoogleId@gmail.com")
                .withName("anInstructorWithoutGoogleId")
                .build();

        instructorsLogic.createInstructor(newIns);

        nonInstrAccount = dataBundle.accounts.get("student2InCourse1");
        nonInstrAccount.setEmail("newInstructor@gmail.com");
        nonInstrAccount.setName(" newInstructor");
        nonInstrAccount.setGoogleId("newInstructorGoogleId");
        newIns = InstructorAttributes.builder(instructor.getCourseId(), nonInstrAccount.getEmail())
                .withName(nonInstrAccount.getName())
                .build();

        instructorsLogic.createInstructor(newIns);
        key[0] = getKeyForInstructor(instructor.getCourseId(), nonInstrAccount.getEmail());

        accountsLogic.joinCourseForInstructor(key[0], nonInstrAccount.getGoogleId());

        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.getCourseId(), nonInstrAccount.getEmail());
        assertEquals(nonInstrAccount.getGoogleId(), joinedInstructor.getGoogleId());

        accountsLogic.deleteAccountCascade(nonInstrAccount.getGoogleId());

        ______TS("failure: instructor already joined");

        nonInstrAccount = dataBundle.accounts.get("student1InCourse1");
        instructor = dataBundle.instructors.get("instructorNotYetJoinCourse");

        key[0] = getKeyForInstructor(instructor.getCourseId(), nonInstrAccount.getEmail());
        joinedInstructor = instructorsLogic.getInstructorForEmail(instructor.getCourseId(), nonInstrAccount.getEmail());
        InstructorAttributes[] finalInstructor = new InstructorAttributes[] { joinedInstructor };
        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForInstructor(key[0], finalInstructor[0].getGoogleId()));
        assertEquals("Instructor has already joined course", eaee.getMessage());

        ______TS("failure: key belongs to a different user");

        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForInstructor(key[0], "otherUserId"));
        assertEquals("Instructor has already joined course", eaee.getMessage());

        ______TS("failure: invalid key");
        String invalidKey = StringHelper.encrypt("invalidKey");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourseForInstructor(invalidKey, loggedInGoogleId));
        assertEquals("No instructor with given registration key: " + invalidKey,
                ednee.getMessage());

        ______TS("failure: course deleted");

        instructor.setCourseId(deletedCourseId);
        instructor = instructorsLogic.createInstructor(instructor);
        InstructorAttributes finalDeletedCourseInstructor = instructor;

        ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourseForInstructor(finalDeletedCourseInstructor.getKey(),
                        finalDeletedCourseInstructor.getGoogleId()));
        assertEquals("The course you are trying to join has been deleted by an instructor", ednee.getMessage());
    }

    @Test
    public void testDeleteAccountCascade_lastInstructorInCourse_shouldDeleteOrphanCourse() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor5");
        AccountAttributes account = dataBundle.accounts.get("instructor5");

        // verify the instructor is the last instructor of a course
        assertEquals(1, instructorsLogic.getInstructorsForCourse(instructor.getCourseId()).size());

        // Make instructor account id a student too.
        StudentAttributes student = StudentAttributes
                .builder(instructor.getCourseId(), "email@test.com")
                .withName(instructor.getName())
                .withSectionName("section")
                .withTeamName("team")
                .withComment("")
                .withGoogleId(instructor.getGoogleId())
                .build();
        studentsLogic.createStudent(student);
        verifyPresentInDatabase(account);
        verifyPresentInDatabase(instructor);
        verifyPresentInDatabase(student);

        accountsLogic.deleteAccountCascade(instructor.getGoogleId());

        verifyAbsentInDatabase(account);
        verifyAbsentInDatabase(instructor);
        verifyAbsentInDatabase(student);
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
                instructorsLogic.getInstructorForEmail(instructor5.getCourseId(), instructor5.getEmail()).isArchived());

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

    @Test
    public void testUpdateReadNotifications() throws Exception {
        AccountAttributes instructor2OfCourse1 = dataBundle.accounts.get("instructor2OfCourse1");
        NotificationAttributes notificationAttributes = dataBundle.notifications.get("notification4");

        ______TS("success: mark notification as read and remove expired ones from read status");

        List<String> readNotificationIds = accountsLogic.updateReadNotifications(
                instructor2OfCourse1.getGoogleId(),
                notificationAttributes.getNotificationId(),
                notificationAttributes.getEndTime());

        assertTrue(readNotificationIds.contains("notification4"));

        AccountAttributes accountAttributes = accountsLogic.getAccount(instructor2OfCourse1.getGoogleId());
        Map<String, Instant> readNotifications = accountAttributes.getReadNotifications();

        assertEquals(notificationAttributes.getEndTime(), readNotifications.get("notification4"));

        for (Map.Entry<String, Instant> notification : readNotifications.entrySet()) {
            assertTrue(notification.getValue().isAfter(Instant.now()));
        }

        ______TS("failure: update read notifications with invalid parameter");
        // invalid googleId
        assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.updateReadNotifications(
                        "not_exist",
                        notificationAttributes.getNotificationId(),
                        notificationAttributes.getEndTime()));

        // invalid notificationId
        assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.updateReadNotifications(
                        instructor2OfCourse1.getGoogleId(),
                        "invalid_notification_id",
                        notificationAttributes.getEndTime()));

        // expired notification
        NotificationAttributes expiredNotification = dataBundle.notifications.get("expiredNotification1");

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountsLogic.updateReadNotifications(
                        instructor2OfCourse1.getGoogleId(),
                        notificationAttributes.getNotificationId(),
                        expiredNotification.getEndTime()));

        assertEquals("Trying to mark an expired notification as read.", ipe.getMessage());
    }
}
