package teammates.it.sqllogic.core;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.HibernateUtil;
import teammates.common.util.StringHelper;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.sqllogic.core.AccountsLogic;
import teammates.sqllogic.core.CoursesLogic;
import teammates.sqllogic.core.NotificationsLogic;
import teammates.sqllogic.core.UsersLogic;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.storage.sqlentity.Student;
import teammates.test.AssertHelper;

/**
 * SUT: {@link AccountsLogic}.
 */
public class AccountsLogicIT extends BaseTestCaseWithSqlDatabaseAccess {

    private AccountsLogic accountsLogic = AccountsLogic.inst();
    private NotificationsLogic notificationsLogic = NotificationsLogic.inst();
    private UsersLogic usersLogic = UsersLogic.inst();
    private CoursesLogic coursesLogic = CoursesLogic.inst();

    private AccountsDb accountsDb = AccountsDb.inst();

    private SqlDataBundle typicalDataBundle;

    @Override
    @BeforeClass
    public void setupClass() {
        super.setupClass();
        typicalDataBundle = getTypicalSqlDataBundle();
    }

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalDataBundle);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
    }

    @Test
    public void testUpdateReadNotifications()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        ______TS("success: mark notification as read");
        Account account = new Account("google-id", "name", "email@teammates.com");
        Notification notification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");
        accountsDb.createAccount(account);
        notificationsLogic.createNotification(notification);

        String googleId = account.getGoogleId();
        UUID notificationId = notification.getId();
        accountsLogic.updateReadNotifications(googleId, notificationId, notification.getEndTime());

        Account actualAccount = accountsDb.getAccountByGoogleId(googleId);
        List<ReadNotification> accountReadNotifications = actualAccount.getReadNotifications();
        assertEquals(1, accountReadNotifications.size());
        assertSame(actualAccount, accountReadNotifications.get(0).getAccount());
        assertSame(notification, accountReadNotifications.get(0).getNotification());
    }

    @Test
    public void testJoinCourseForStudent()
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {

        Student student2YetToJoinCourse = typicalDataBundle.students.get("student2YetToJoinCourse4");
        Student student3YetToJoinCourse = typicalDataBundle.students.get("student3YetToJoinCourse4");
        Student studentInCourse = typicalDataBundle.students.get("student1InCourse1");

        String loggedInGoogleId = "AccLogicT.student.id";

        ______TS("failure: wrong key");

        String wrongKey = StringHelper.encrypt("wrongkey");
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourseForStudent(wrongKey, loggedInGoogleId));
        assertEquals("No student with given registration key: " + wrongKey, ednee.getMessage());

        ______TS("failure: invalid parameters");

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> accountsLogic.joinCourseForStudent(student2YetToJoinCourse.getRegKey(), "wrong student"));
        AssertHelper.assertContains(FieldValidator.REASON_INCORRECT_FORMAT, ipe.getMessage());

        ______TS("failure: googleID belongs to an existing student in the course");

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForStudent(student2YetToJoinCourse.getRegKey(),
                studentInCourse.getGoogleId()));
        assertEquals("Student has already joined course", eaee.getMessage());

        ______TS("success: with encryption and new account to be created");

        accountsLogic.joinCourseForStudent(student2YetToJoinCourse.getRegKey(), loggedInGoogleId);
        Account accountCreated = accountsLogic.getAccountForGoogleId(loggedInGoogleId);

        assertEquals(loggedInGoogleId, usersLogic.getStudentForEmail(
                student2YetToJoinCourse.getCourseId(), student2YetToJoinCourse.getEmail()).getGoogleId());
        assertNotNull(accountCreated);

        ______TS("success: student joined but account already exists");

        String existingAccountId = "existingAccountId";
        Account existingAccount = new Account(existingAccountId, "accountName", student3YetToJoinCourse.getEmail());
        accountsDb.createAccount(existingAccount);

        accountsLogic.joinCourseForStudent(student3YetToJoinCourse.getRegKey(), existingAccountId);

        assertEquals(existingAccountId, usersLogic.getStudentForEmail(
                student3YetToJoinCourse.getCourseId(), student3YetToJoinCourse.getEmail()).getGoogleId());

        ______TS("failure: already joined");

        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForStudent(student2YetToJoinCourse.getRegKey(), loggedInGoogleId));
        assertEquals("Student has already joined course", eaee.getMessage());

        ______TS("failure: course is deleted");

        Course originalCourse = usersLogic.getStudentForEmail(
                student2YetToJoinCourse.getCourseId(), student2YetToJoinCourse.getEmail()).getCourse();
        coursesLogic.moveCourseToRecycleBin(originalCourse.getId());

        ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourseForStudent(student2YetToJoinCourse.getRegKey(),
                        loggedInGoogleId));
        assertEquals("The course you are trying to join has been deleted by an instructor", ednee.getMessage());
    }

    @Test
    public void testJoinCourseForInstructor() throws Exception {
        String instructorIdAlreadyJoinedCourse = "instructor1";
        Instructor instructor2YetToJoinCourse = typicalDataBundle.instructors.get("instructor2YetToJoinCourse4");
        Instructor instructor3YetToJoinCourse = typicalDataBundle.instructors.get("instructor3YetToJoinCourse4");

        String loggedInGoogleId = "AccLogicT.instr.id";
        String[] key = new String[] {
                getRegKeyForInstructor(instructor2YetToJoinCourse.getCourseId(), instructor2YetToJoinCourse.getEmail()),
                getRegKeyForInstructor(instructor2YetToJoinCourse.getCourseId(), instructor3YetToJoinCourse.getEmail()),
        };

        ______TS("failure: googleID belongs to an existing instructor in the course");

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForInstructor(
                        key[0], instructorIdAlreadyJoinedCourse));
        assertEquals("Instructor has already joined course", eaee.getMessage());

        ______TS("success: instructor joined and new account be created");

        accountsLogic.joinCourseForInstructor(key[0], loggedInGoogleId);

        Instructor joinedInstructor = usersLogic.getInstructorForEmail(
                        instructor2YetToJoinCourse.getCourseId(), instructor2YetToJoinCourse.getEmail());
        assertEquals(loggedInGoogleId, joinedInstructor.getGoogleId());

        Account accountCreated = accountsLogic.getAccountForGoogleId(loggedInGoogleId);
        assertNotNull(accountCreated);

        ______TS("success: instructor joined but account already exists");

        String existingAccountId = "existingAccountId";
        Account existingAccount = new Account(existingAccountId, "accountName", instructor3YetToJoinCourse.getEmail());
        accountsDb.createAccount(existingAccount);

        accountsLogic.joinCourseForInstructor(key[1], existingAccount.getGoogleId());

        joinedInstructor = usersLogic.getInstructorForEmail(
                        instructor3YetToJoinCourse.getCourseId(), existingAccount.getEmail());
        assertEquals(existingAccountId, joinedInstructor.getGoogleId());

        ______TS("failure: instructor already joined");

        eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> accountsLogic.joinCourseForInstructor(key[0], loggedInGoogleId));
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

        Course originalCourse = usersLogic.getInstructorForEmail(
                instructor2YetToJoinCourse.getCourseId(), instructor2YetToJoinCourse.getEmail()).getCourse();
        coursesLogic.moveCourseToRecycleBin(originalCourse.getId());

        ednee = assertThrows(EntityDoesNotExistException.class,
                () -> accountsLogic.joinCourseForInstructor(instructor2YetToJoinCourse.getRegKey(),
                    instructor2YetToJoinCourse.getGoogleId()));
        assertEquals("The course you are trying to join has been deleted by an instructor", ednee.getMessage());
    }

    private String getRegKeyForInstructor(String courseId, String email) {
        return usersLogic.getInstructorForEmail(courseId, email).getRegKey();
    }
}
