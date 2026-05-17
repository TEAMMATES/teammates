package teammates.logic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link DataBundleLogic}.
 */
public class DataBundleLogicTest extends BaseTestCase {

    private final DataBundleLogic dataBundleLogic = DataBundleLogic.inst();

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    private AccountsLogic accountsLogic;
    private AccountRequestsLogic accountRequestsLogic;
    private CoursesLogic coursesLogic;
    private NotificationsLogic notificationsLogic;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        accountsLogic = mock(AccountsLogic.class);
        accountRequestsLogic = mock(AccountRequestsLogic.class);
        coursesLogic = mock(CoursesLogic.class);
        notificationsLogic = mock(NotificationsLogic.class);

        dataBundleLogic.initLogicDependencies(
                accountsLogic, accountRequestsLogic, coursesLogic, notificationsLogic);
    }

    @AfterMethod
    public void tearDown() {
        mockHibernateUtil.close();
    }

    @Test
    public void testPersistDataBundle_nullBundle_throwsException() {
        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> dataBundleLogic.persistDataBundle(null));
        assertEquals("Null data bundle", ex.getMessage());
    }

    @Test
    public void testPersistDataBundle_emptyBundle_success()
            throws InvalidParametersException {
        DataBundle emptyBundle = new DataBundle();

        DataBundle result = dataBundleLogic.persistDataBundle(emptyBundle);

        assertNotNull(result);
        assertEquals(emptyBundle, result);
        assertTrue(result.accounts.isEmpty());
        assertTrue(result.courses.isEmpty());
        assertTrue(result.notifications.isEmpty());
    }

    @Test
    public void testPersistDataBundle_withCourse_createsCourse()
            throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        Course course = getTypicalCourse();
        dataBundle.courses.put("course1", course);

        DataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.courses.size());
        assertTrue(result.courses.containsKey("course1"));
        assertEquals(course, result.courses.get("course1"));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(course), times(1));
    }

    @Test
    public void testPersistDataBundle_withAccount_createsAccount()
            throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        Account account = getTypicalAccount();
        dataBundle.accounts.put("account1", account);

        DataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.accounts.size());
        assertEquals(account, result.accounts.get("account1"));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(account), times(1));
    }

    @Test
    public void testPersistDataBundle_withNotification_createsNotification()
            throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        Notification notification = getTypicalNotificationWithId();
        dataBundle.notifications.put("notification1", notification);

        DataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.notifications.size());
        assertTrue(result.notifications.containsKey("notification1"));
        assertEquals(notification, result.notifications.get("notification1"));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(notification), times(1));
    }

    @Test
    public void testPersistDataBundle_withAccountRequest_createsAccountRequest()
            throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        AccountRequest accountRequest = getTypicalAccountRequest();
        dataBundle.accountRequests.put("accountRequest1", accountRequest);

        DataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.accountRequests.size());
        assertEquals(accountRequest, result.accountRequests.get("accountRequest1"));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(accountRequest), times(1));
    }

    @Test
    public void testPersistDataBundle_withMultipleEntities_createsAllEntities()
            throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();

        Course course = getTypicalCourse();
        Account account = getTypicalAccount();
        Notification notification = getTypicalNotificationWithId();
        AccountRequest accountRequest = getTypicalAccountRequest();

        dataBundle.courses.put("course1", course);
        dataBundle.accounts.put("account1", account);
        dataBundle.notifications.put("notification1", notification);
        dataBundle.accountRequests.put("accountRequest1", accountRequest);

        DataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.courses.size());
        assertEquals(1, result.accounts.size());
        assertEquals(1, result.notifications.size());
        assertEquals(1, result.accountRequests.size());
        assertTrue(result.courses.containsKey("course1"));
        assertTrue(result.accounts.containsKey("account1"));
        assertTrue(result.notifications.containsKey("notification1"));
        assertTrue(result.accountRequests.containsKey("accountRequest1"));
        assertEquals(course, result.courses.get("course1"));
        assertEquals(account, result.accounts.get("account1"));
        assertEquals(notification, result.notifications.get("notification1"));
        assertEquals(accountRequest, result.accountRequests.get("accountRequest1"));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(course), times(1));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(account), times(1));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(notification), times(1));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(accountRequest), times(1));
    }

    @Test
    public void testPersistDataBundle_withSectionsAndTeams_createsHierarchy()
            throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        Course course = getTypicalCourse();
        Section section = new Section("Section 1");
        course.addSection(section);
        Team team = new Team("Team 1");
        section.addTeam(team);

        dataBundle.courses.put("course1", course);
        dataBundle.sections.put("section1", section);
        dataBundle.teams.put("team1", team);

        DataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.courses.size());
        assertEquals(1, result.sections.size());
        assertEquals(1, result.teams.size());
        assertTrue(result.courses.containsKey("course1"));
        assertTrue(result.sections.containsKey("section1"));
        assertTrue(result.teams.containsKey("team1"));
        assertEquals(course, result.courses.get("course1"));
        assertEquals(section, result.sections.get("section1"));
        assertEquals(team, result.teams.get("team1"));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(course), times(1));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(section), times(1));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(team), times(1));
    }

    @Test
    public void testPersistDataBundle_withStudentsAndInstructors_createsUsers()
            throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        Course course = getTypicalCourse();
        Section section = new Section("Section 1");
        course.addSection(section);
        Team team = new Team("Team 1");
        section.addTeam(team);
        Student student = getTypicalStudent();
        Instructor instructor = getTypicalInstructor();

        student.setCourse(course);
        student.setTeam(team);
        instructor.setCourse(course);

        dataBundle.courses.put("course1", course);
        dataBundle.sections.put("section1", section);
        dataBundle.teams.put("team1", team);
        dataBundle.students.put("student1", student);
        dataBundle.instructors.put("instructor1", instructor);

        DataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.students.size());
        assertEquals(1, result.instructors.size());
        assertTrue(result.students.containsKey("student1"));
        assertTrue(result.instructors.containsKey("instructor1"));
        assertEquals(student, result.students.get("student1"));
        assertEquals(instructor, result.instructors.get("instructor1"));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(student), times(1));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(instructor), times(1));
    }

    @Test
    public void testPersistDataBundle_withFeedbackSession_createsSession()
            throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);

        dataBundle.courses.put("course1", course);
        dataBundle.feedbackSessions.put("session1", session);

        DataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.feedbackSessions.size());
        assertEquals(session, result.feedbackSessions.get("session1"));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(session), times(1));
    }

    @Test
    public void testPersistDataBundle_withReadNotifications_updatesReadNotifications()
            throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        Account account = getTypicalAccount();
        Notification notification = getTypicalNotificationWithId();
        ReadNotification readNotification = new ReadNotification();
        account.addReadNotification(readNotification);
        notification.addReadNotification(readNotification);

        dataBundle.accounts.put("account1", account);
        dataBundle.notifications.put("notification1", notification);
        dataBundle.readNotifications.put("readNotification1", readNotification);

        DataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.accounts.size());
        assertEquals(1, result.notifications.size());
        assertEquals(1, result.readNotifications.size());
        assertTrue(result.accounts.containsKey("account1"));
        assertTrue(result.notifications.containsKey("notification1"));
        assertTrue(result.readNotifications.containsKey("readNotification1"));
        assertEquals(account, result.accounts.get("account1"));
        assertEquals(notification, result.notifications.get("notification1"));
        assertEquals(readNotification, result.readNotifications.get("readNotification1"));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(readNotification), times(1));
    }

    @Test
    public void testRemoveDataBundle_nullBundle_throwsException() {
        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> dataBundleLogic.removeDataBundle(null));
        assertEquals("Data bundle is null", ex.getMessage());
    }

    @Test
    public void testRemoveDataBundle_emptyBundle_success() throws InvalidParametersException {
        DataBundle emptyBundle = new DataBundle();

        // Should not throw any exception
        dataBundleLogic.removeDataBundle(emptyBundle);
    }

    @Test
    public void testRemoveDataBundle_withCourse_deletesCourse() throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        Course course = getTypicalCourse();
        dataBundle.courses.put("course1", course);

        dataBundleLogic.removeDataBundle(dataBundle);

        verify(coursesLogic, times(1)).deleteCourse(course.getId());
    }

    @Test
    public void testRemoveDataBundle_withMultipleEntities_deletesAllEntities() throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        Course course = getTypicalCourse();
        Notification notification = getTypicalNotificationWithId();
        Account account = getTypicalAccount();
        AccountRequest accountRequest = getTypicalAccountRequest();
        accountRequest.setId(UUID.randomUUID());

        dataBundle.courses.put("course1", course);
        dataBundle.notifications.put("notification1", notification);
        dataBundle.accounts.put("account1", account);
        dataBundle.accountRequests.put("accountRequest1", accountRequest);

        dataBundleLogic.removeDataBundle(dataBundle);

        verify(coursesLogic, times(1)).deleteCourse(course.getId());
        verify(notificationsLogic, times(1)).deleteNotification(notification.getId());
        verify(accountsLogic, times(1)).deleteAccount(account.getGoogleId());
        verify(accountRequestsLogic, times(1)).deleteAccountRequest(accountRequest.getId());
    }

    @Test
    public void testRemoveDataBundle_withNotification_deletesNotification() throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        Notification notification = getTypicalNotificationWithId();
        dataBundle.notifications.put("notification1", notification);

        dataBundleLogic.removeDataBundle(dataBundle);

        verify(notificationsLogic, times(1)).deleteNotification(notification.getId());
    }

    @Test
    public void testRemoveDataBundle_withAccount_deletesAccount() throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        Account account = getTypicalAccount();
        dataBundle.accounts.put("account1", account);

        dataBundleLogic.removeDataBundle(dataBundle);

        verify(accountsLogic, times(1)).deleteAccount(account.getGoogleId());
    }

    @Test
    public void testRemoveDataBundle_withAccountRequest_deletesAccountRequest() throws InvalidParametersException {
        DataBundle dataBundle = new DataBundle();
        AccountRequest accountRequest = getTypicalAccountRequest();
        accountRequest.setId(UUID.randomUUID());
        dataBundle.accountRequests.put("accountRequest1", accountRequest);

        dataBundleLogic.removeDataBundle(dataBundle);

        verify(accountRequestsLogic, times(1)).deleteAccountRequest(accountRequest.getId());
    }

    @Test
    public void testDeserializeDataBundle_validJson_success() {
        String jsonString = "{"
                + "\"accounts\": {},"
                + "\"accountRequests\": {},"
                + "\"courses\": {},"
                + "\"sections\": {},"
                + "\"teams\": {},"
                + "\"instructors\": {},"
                + "\"students\": {},"
                + "\"feedbackSessions\": {},"
                + "\"feedbackSessionLogs\": {},"
                + "\"feedbackQuestions\": {},"
                + "\"feedbackResponses\": {},"
                + "\"feedbackResponseComments\": {},"
                + "\"deadlineExtensions\": {},"
                + "\"notifications\": {},"
                + "\"readNotifications\": {}"
                + "}";

        DataBundle result = DataBundleLogic.deserializeDataBundle(jsonString);

        assertNotNull(result);
        assertTrue(result.accounts.isEmpty());
        assertTrue(result.accountRequests.isEmpty());
        assertTrue(result.courses.isEmpty());
        assertTrue(result.sections.isEmpty());
        assertTrue(result.teams.isEmpty());
        assertTrue(result.instructors.isEmpty());
        assertTrue(result.students.isEmpty());
        assertTrue(result.feedbackSessions.isEmpty());
        assertTrue(result.feedbackSessionLogs.isEmpty());
        assertTrue(result.feedbackQuestions.isEmpty());
        assertTrue(result.feedbackResponses.isEmpty());
        assertTrue(result.feedbackResponseComments.isEmpty());
        assertTrue(result.deadlineExtensions.isEmpty());
        assertTrue(result.notifications.isEmpty());
        assertTrue(result.readNotifications.isEmpty());
    }

    @Test
    public void testDeserializeDataBundle_withEntities_preservesCourseIds() {
        // This test verifies that course IDs are preserved during deserialization
        // as mentioned in the DataBundleLogic.deserializeDataBundle javadoc
        String jsonString = "{"
                + "\"accounts\": {},"
                + "\"accountRequests\": {},"
                + "\"courses\": {\"course1\": {\"id\": \"test-course-id\", \"name\": \"Test Course\"}},"
                + "\"sections\": {},"
                + "\"teams\": {},"
                + "\"instructors\": {},"
                + "\"students\": {},"
                + "\"feedbackSessions\": {},"
                + "\"feedbackSessionLogs\": {},"
                + "\"feedbackQuestions\": {},"
                + "\"feedbackResponses\": {},"
                + "\"feedbackResponseComments\": {},"
                + "\"deadlineExtensions\": {},"
                + "\"notifications\": {},"
                + "\"readNotifications\": {}"
                + "}";

        DataBundle result = DataBundleLogic.deserializeDataBundle(jsonString);

        assertNotNull(result);
        assertNotNull(result.courses);
        assertEquals(1, result.courses.size());
        assertTrue(result.courses.containsKey("course1"));
        Course course = result.courses.get("course1");
        assertNotNull(course);
        assertEquals("test-course-id", course.getId());
        assertEquals("Test Course", course.getName());
    }
}
