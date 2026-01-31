package teammates.sqllogic.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link DataBundleLogic}.
 */
public class DataBundleLogicTest extends BaseTestCase {

    private final DataBundleLogic dataBundleLogic = DataBundleLogic.inst();

    private AccountsLogic accountsLogic;
    private AccountRequestsLogic accountRequestsLogic;
    private CoursesLogic coursesLogic;
    private FeedbackSessionsLogic fsLogic;
    private NotificationsLogic notificationsLogic;
    private UsersLogic usersLogic;

    @BeforeMethod
    public void setUpMethod() {
        accountsLogic = mock(AccountsLogic.class);
        accountRequestsLogic = mock(AccountRequestsLogic.class);
        coursesLogic = mock(CoursesLogic.class);
        DeadlineExtensionsLogic deadlineExtensionsLogic = mock(DeadlineExtensionsLogic.class);
        fsLogic = mock(FeedbackSessionsLogic.class);
        FeedbackSessionLogsLogic fslLogic = mock(FeedbackSessionLogsLogic.class);
        FeedbackQuestionsLogic fqLogic = mock(FeedbackQuestionsLogic.class);
        FeedbackResponsesLogic frLogic = mock(FeedbackResponsesLogic.class);
        FeedbackResponseCommentsLogic frcLogic = mock(FeedbackResponseCommentsLogic.class);
        notificationsLogic = mock(NotificationsLogic.class);
        usersLogic = mock(UsersLogic.class);

        dataBundleLogic.initLogicDependencies(
                accountsLogic, accountRequestsLogic, coursesLogic, deadlineExtensionsLogic,
                fsLogic, fslLogic, fqLogic, frLogic, frcLogic, notificationsLogic, usersLogic);
    }

    @Test
    public void testPersistDataBundle_nullBundle_throwsException() {
        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> dataBundleLogic.persistDataBundle(null));
        assertEquals("Null data bundle", ex.getMessage());
    }

    @Test
    public void testPersistDataBundle_emptyBundle_success()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle emptyBundle = new SqlDataBundle();

        SqlDataBundle result = dataBundleLogic.persistDataBundle(emptyBundle);

        assertNotNull(result);
        assertEquals(emptyBundle, result);
        assertTrue(result.accounts.isEmpty());
        assertTrue(result.courses.isEmpty());
        assertTrue(result.notifications.isEmpty());
    }

    @Test
    public void testPersistDataBundle_withCourse_createsCourse()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Course course = getTypicalCourse();
        dataBundle.courses.put("course1", course);

        when(coursesLogic.createCourse(course)).thenReturn(course);

        SqlDataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.courses.size());
        assertTrue(result.courses.containsKey("course1"));
        assertEquals(course, result.courses.get("course1"));
        verify(coursesLogic, times(1)).createCourse(course);
    }

    @Test
    public void testPersistDataBundle_withAccount_createsAccount()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Account account = getTypicalAccount();
        dataBundle.accounts.put("account1", account);

        SqlDataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.accounts.size());
        assertEquals(account, result.accounts.get("account1"));
        verify(accountsLogic, times(1)).createAccount(account);
    }

    @Test
    public void testPersistDataBundle_withNotification_createsNotification()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Notification notification = getTypicalNotificationWithId();
        dataBundle.notifications.put("notification1", notification);

        when(notificationsLogic.createNotification(notification)).thenReturn(notification);

        SqlDataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.notifications.size());
        assertTrue(result.notifications.containsKey("notification1"));
        assertEquals(notification, result.notifications.get("notification1"));
        verify(notificationsLogic, times(1)).createNotification(notification);
    }

    @Test
    public void testPersistDataBundle_withAccountRequest_createsAccountRequest()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        AccountRequest accountRequest = getTypicalAccountRequest();
        dataBundle.accountRequests.put("accountRequest1", accountRequest);

        when(accountRequestsLogic.createAccountRequest(accountRequest)).thenReturn(accountRequest);

        SqlDataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.accountRequests.size());
        assertEquals(accountRequest, result.accountRequests.get("accountRequest1"));
        verify(accountRequestsLogic, times(1)).createAccountRequest(accountRequest);
    }

    @Test
    public void testPersistDataBundle_withMultipleEntities_createsAllEntities()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();

        Course course = getTypicalCourse();
        Account account = getTypicalAccount();
        Notification notification = getTypicalNotificationWithId();
        AccountRequest accountRequest = getTypicalAccountRequest();

        dataBundle.courses.put("course1", course);
        dataBundle.accounts.put("account1", account);
        dataBundle.notifications.put("notification1", notification);
        dataBundle.accountRequests.put("accountRequest1", accountRequest);

        when(coursesLogic.createCourse(course)).thenReturn(course);
        when(notificationsLogic.createNotification(notification)).thenReturn(notification);
        when(accountRequestsLogic.createAccountRequest(accountRequest)).thenReturn(accountRequest);

        SqlDataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

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

        verify(coursesLogic, times(1)).createCourse(course);
        verify(accountsLogic, times(1)).createAccount(account);
        verify(notificationsLogic, times(1)).createNotification(notification);
        verify(accountRequestsLogic, times(1)).createAccountRequest(accountRequest);
    }

    @Test
    public void testPersistDataBundle_withSectionsAndTeams_createsHierarchy()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Course course = getTypicalCourse();
        Section section = new Section(course, "Section 1");
        Team team = new Team(section, "Team 1");

        dataBundle.courses.put("course1", course);
        dataBundle.sections.put("section1", section);
        dataBundle.teams.put("team1", team);

        when(coursesLogic.createCourse(course)).thenReturn(course);
        when(coursesLogic.createSection(section)).thenReturn(section);
        when(coursesLogic.createTeam(team)).thenReturn(team);

        SqlDataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

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

        verify(coursesLogic, times(1)).createCourse(course);
        verify(coursesLogic, times(1)).createSection(section);
        verify(coursesLogic, times(1)).createTeam(team);
    }

    @Test
    public void testPersistDataBundle_withStudentsAndInstructors_createsUsers()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Course course = getTypicalCourse();
        Section section = new Section(course, "Section 1");
        Team team = new Team(section, "Team 1");
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

        when(coursesLogic.createCourse(course)).thenReturn(course);
        when(coursesLogic.createSection(section)).thenReturn(section);
        when(coursesLogic.createTeam(team)).thenReturn(team);
        when(usersLogic.createStudent(student)).thenReturn(student);
        when(usersLogic.createInstructor(instructor)).thenReturn(instructor);

        SqlDataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.students.size());
        assertEquals(1, result.instructors.size());
        assertTrue(result.students.containsKey("student1"));
        assertTrue(result.instructors.containsKey("instructor1"));
        assertEquals(student, result.students.get("student1"));
        assertEquals(instructor, result.instructors.get("instructor1"));

        verify(usersLogic, times(1)).createStudent(student);
        verify(usersLogic, times(1)).createInstructor(instructor);
    }

    @Test
    public void testPersistDataBundle_withFeedbackSession_createsSession()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);

        dataBundle.courses.put("course1", course);
        dataBundle.feedbackSessions.put("session1", session);

        when(coursesLogic.createCourse(course)).thenReturn(course);
        when(fsLogic.createFeedbackSession(session)).thenReturn(session);

        SqlDataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

        assertNotNull(result);
        assertEquals(dataBundle, result);
        assertEquals(1, result.feedbackSessions.size());
        assertEquals(session, result.feedbackSessions.get("session1"));
        verify(fsLogic, times(1)).createFeedbackSession(session);
    }

    @Test
    public void testPersistDataBundle_withReadNotifications_updatesReadNotifications()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Account account = getTypicalAccount();
        Notification notification = getTypicalNotificationWithId();
        ReadNotification readNotification = new ReadNotification(account, notification);

        dataBundle.accounts.put("account1", account);
        dataBundle.notifications.put("notification1", notification);
        dataBundle.readNotifications.put("readNotification1", readNotification);

        when(notificationsLogic.createNotification(notification)).thenReturn(notification);

        SqlDataBundle result = dataBundleLogic.persistDataBundle(dataBundle);

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
        verify(accountsLogic, times(1)).updateReadNotifications(
                eq(account.getGoogleId()), eq(notification.getId()), eq(notification.getEndTime()));
    }

    @Test
    public void testRemoveDataBundle_nullBundle_throwsException() {
        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> dataBundleLogic.removeDataBundle(null));
        assertEquals("Data bundle is null", ex.getMessage());
    }

    @Test
    public void testRemoveDataBundle_emptyBundle_success() throws InvalidParametersException {
        SqlDataBundle emptyBundle = new SqlDataBundle();

        // Should not throw any exception
        dataBundleLogic.removeDataBundle(emptyBundle);
    }

    @Test
    public void testRemoveDataBundle_withCourse_deletesCourse() throws InvalidParametersException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Course course = getTypicalCourse();
        dataBundle.courses.put("course1", course);

        dataBundleLogic.removeDataBundle(dataBundle);

        verify(coursesLogic, times(1)).deleteCourseCascade(course.getId());
    }

    @Test
    public void testRemoveDataBundle_withMultipleEntities_deletesAllEntities() throws InvalidParametersException {
        SqlDataBundle dataBundle = new SqlDataBundle();
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

        verify(coursesLogic, times(1)).deleteCourseCascade(course.getId());
        verify(notificationsLogic, times(1)).deleteNotification(notification.getId());
        verify(accountsLogic, times(1)).deleteAccount(account.getGoogleId());
        verify(accountRequestsLogic, times(1)).deleteAccountRequest(accountRequest.getId());
    }

    @Test
    public void testRemoveDataBundle_withNotification_deletesNotification() throws InvalidParametersException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Notification notification = getTypicalNotificationWithId();
        dataBundle.notifications.put("notification1", notification);

        dataBundleLogic.removeDataBundle(dataBundle);

        verify(notificationsLogic, times(1)).deleteNotification(notification.getId());
    }

    @Test
    public void testRemoveDataBundle_withAccount_deletesAccount() throws InvalidParametersException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Account account = getTypicalAccount();
        dataBundle.accounts.put("account1", account);

        dataBundleLogic.removeDataBundle(dataBundle);

        verify(accountsLogic, times(1)).deleteAccount(account.getGoogleId());
    }

    @Test
    public void testRemoveDataBundle_withAccountRequest_deletesAccountRequest() throws InvalidParametersException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        AccountRequest accountRequest = getTypicalAccountRequest();
        accountRequest.setId(UUID.randomUUID());
        dataBundle.accountRequests.put("accountRequest1", accountRequest);

        dataBundleLogic.removeDataBundle(dataBundle);

        verify(accountRequestsLogic, times(1)).deleteAccountRequest(accountRequest.getId());
    }

    @Test
    public void testPutDocuments_withStudents_putsStudentDocuments()
            throws SearchServiceException, InvalidParametersException, EntityAlreadyExistsException,
            EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Course course = getTypicalCourse();
        Section section = new Section(course, "Section 1");
        Team team = new Team(section, "Team 1");
        Student student1 = getTypicalStudent();
        Student student2 = getTypicalStudent();
        student1.setCourse(course);
        student1.setTeam(team);
        student2.setCourse(course);
        student2.setTeam(team);
        dataBundle.students.put("student1", student1);
        dataBundle.students.put("student2", student2);

        dataBundleLogic.putDocuments(dataBundle);

        verify(usersLogic, times(1)).putStudentDocument(student1);
        verify(usersLogic, times(1)).putStudentDocument(student2);
        verify(usersLogic, times(2)).putStudentDocument(any(Student.class));
        verify(usersLogic, never()).putInstructorDocument(any());
        verify(accountRequestsLogic, never()).putDocument(any());
    }

    @Test
    public void testPutDocuments_withInstructors_putsInstructorDocuments()
            throws SearchServiceException, InvalidParametersException, EntityAlreadyExistsException,
            EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Instructor instructor1 = getTypicalInstructor();
        Instructor instructor2 = getTypicalInstructor();
        dataBundle.instructors.put("instructor1", instructor1);
        dataBundle.instructors.put("instructor2", instructor2);

        dataBundleLogic.putDocuments(dataBundle);

        verify(usersLogic, times(1)).putInstructorDocument(instructor1);
        verify(usersLogic, times(1)).putInstructorDocument(instructor2);
        verify(usersLogic, times(2)).putInstructorDocument(any(Instructor.class));
        verify(usersLogic, never()).putStudentDocument(any());
        verify(accountRequestsLogic, never()).putDocument(any());
    }

    @Test
    public void testPutDocuments_withAccountRequests_putsAccountRequestDocuments()
            throws SearchServiceException, InvalidParametersException, EntityAlreadyExistsException,
            EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        AccountRequest accountRequest = getTypicalAccountRequest();
        dataBundle.accountRequests.put("accountRequest1", accountRequest);

        dataBundleLogic.putDocuments(dataBundle);

        verify(accountRequestsLogic, times(1)).putDocument(accountRequest);
    }

    @Test
    public void testPutDocuments_emptyBundle_noExceptions() throws SearchServiceException {
        SqlDataBundle emptyBundle = new SqlDataBundle();

        // Should not throw any exception
        dataBundleLogic.putDocuments(emptyBundle);

        verify(usersLogic, times(0)).putStudentDocument(any());
        verify(usersLogic, times(0)).putInstructorDocument(any());
        verify(accountRequestsLogic, times(0)).putDocument(any());
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

        SqlDataBundle result = DataBundleLogic.deserializeDataBundle(jsonString);

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

        SqlDataBundle result = DataBundleLogic.deserializeDataBundle(jsonString);

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
