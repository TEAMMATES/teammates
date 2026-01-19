package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
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
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
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
    private NotificationsLogic notificationsLogic;
    private UsersLogic usersLogic;

    @BeforeMethod
    public void setUpMethod() {
        accountsLogic = mock(AccountsLogic.class);
        accountRequestsLogic = mock(AccountRequestsLogic.class);
        coursesLogic = mock(CoursesLogic.class);
        DeadlineExtensionsLogic deadlineExtensionsLogic = mock(DeadlineExtensionsLogic.class);
        FeedbackSessionsLogic fsLogic = mock(FeedbackSessionsLogic.class);
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
    }

    @Test
    public void testPersistDataBundle_withCourse_createsCourse()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Course course = getTypicalCourse();
        dataBundle.courses.put("course1", course);

        when(coursesLogic.createCourse(course)).thenReturn(course);

        dataBundleLogic.persistDataBundle(dataBundle);

        verify(coursesLogic, times(1)).createCourse(course);
    }

    @Test
    public void testPersistDataBundle_withAccount_createsAccount()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Account account = getTypicalAccount();
        dataBundle.accounts.put("account1", account);

        dataBundleLogic.persistDataBundle(dataBundle);

        verify(accountsLogic, times(1)).createAccount(account);
    }

    @Test
    public void testPersistDataBundle_withNotification_createsNotification()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Notification notification = getTypicalNotificationWithId();
        dataBundle.notifications.put("notification1", notification);

        when(notificationsLogic.createNotification(notification)).thenReturn(notification);

        dataBundleLogic.persistDataBundle(dataBundle);

        verify(notificationsLogic, times(1)).createNotification(notification);
    }

    @Test
    public void testPersistDataBundle_withAccountRequest_createsAccountRequest()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        AccountRequest accountRequest = getTypicalAccountRequest();
        dataBundle.accountRequests.put("accountRequest1", accountRequest);

        when(accountRequestsLogic.createAccountRequest(accountRequest)).thenReturn(accountRequest);

        dataBundleLogic.persistDataBundle(dataBundle);

        verify(accountRequestsLogic, times(1)).createAccountRequest(accountRequest);
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
        Student student = getTypicalStudent();
        student.setCourse(course);
        student.setTeam(team);
        dataBundle.students.put("student1", student);

        dataBundleLogic.putDocuments(dataBundle);

        verify(usersLogic, times(1)).putStudentDocument(student);
    }

    @Test
    public void testPutDocuments_withInstructors_putsInstructorDocuments()
            throws SearchServiceException, InvalidParametersException, EntityAlreadyExistsException,
            EntityDoesNotExistException {
        SqlDataBundle dataBundle = new SqlDataBundle();
        Instructor instructor = getTypicalInstructor();
        dataBundle.instructors.put("instructor1", instructor);

        dataBundleLogic.putDocuments(dataBundle);

        verify(usersLogic, times(1)).putInstructorDocument(instructor);
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

        verify(usersLogic, times(0)).putStudentDocument(org.mockito.ArgumentMatchers.any());
        verify(usersLogic, times(0)).putInstructorDocument(org.mockito.ArgumentMatchers.any());
        verify(accountRequestsLogic, times(0)).putDocument(org.mockito.ArgumentMatchers.any());
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
        assertTrue(result.courses.isEmpty());
    }
}
