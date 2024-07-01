package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code UsersDb}.
 */
public class UsersDbTest extends BaseTestCase {

    private UsersDb usersDb;

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUp() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        usersDb = spy(UsersDb.class);
    }

    @AfterMethod
    public void teardown() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateInstructor_validInstructorDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Instructor newInstructor = getTypicalInstructor();

        usersDb.createInstructor(newInstructor);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newInstructor));
    }

    @Test
    public void testCreateStudent_studentDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Student newStudent = getTypicalStudent();

        usersDb.createStudent(newStudent);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newStudent));
    }

    @Test
    public void testCreateStudent_studentWithInvalidEmail_throwsInvalidParametersException() {
        Student newStudent = getTypicalStudent();
        newStudent.setEmail("invalid-email");

        assertThrows(InvalidParametersException.class, () -> usersDb.createStudent(newStudent));

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newStudent), never());
    }

    @Test
    public void testGetInstructor_instructorIdPresent_success() {
        Instructor instructor = getTypicalInstructor();

        mockHibernateUtil
                .when(() -> HibernateUtil.get(Instructor.class, instructor.getId()))
                .thenReturn(instructor);

        Instructor actualInstructor = usersDb.getInstructor(instructor.getId());

        assertEquals(instructor, actualInstructor);
    }

    @Test
    public void testGetStudent_studentIdPresent_success() {
        Student student = getTypicalStudent();

        mockHibernateUtil
                .when(() -> HibernateUtil.get(Student.class, student.getId()))
                .thenReturn(student);

        Student actualStudent = usersDb.getStudent(student.getId());

        assertEquals(student, actualStudent);
    }

    @Test
    public void testDeleteUser_userNotNull_success() {
        Student student = mock(Student.class);

        usersDb.deleteUser(student);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(student));
    }

    @Test
    public void testDeleteUser_userNull_shouldFailSilently() {
        usersDb.deleteUser(null);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(any()), never());
    }

    @Test
    public void testUpdateStudent_invalidStudent_throwsInvalidParametersException() {
        Student invalidStudent = getTypicalStudent();
        invalidStudent.setEmail("");

        assertThrows(InvalidParametersException.class,
                () -> usersDb.updateStudent(invalidStudent));

        mockHibernateUtil.verify(() -> HibernateUtil.merge(invalidStudent), never());
    }

    @Test
    public void testUpdateStudent_studentDoesNotExist_throwsEntityDoesNotExistException() {
        Student student = getTypicalStudent();

        doReturn(null).when(usersDb).getStudent(any());

        assertThrows(EntityDoesNotExistException.class,
                () -> usersDb.updateStudent(student));

        mockHibernateUtil.verify(() -> HibernateUtil.merge(student), never());
    }

    @Test
    public void testUpdateStudent_success()
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Student existingStudent = getTypicalStudent();

        doReturn(existingStudent).when(usersDb).getStudent(any());
        mockHibernateUtil.when(() -> HibernateUtil.merge(existingStudent)).thenReturn(existingStudent);

        usersDb.updateStudent(existingStudent);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(existingStudent));
    }

    @Test
    public void testGetSectionOrCreate_noSection_sectionIsCreated() {
        doReturn(null).when(usersDb).getSection(anyString(), anyString());

        usersDb.getSectionOrCreate("test-course", "test-section");

        mockHibernateUtil.verify(() -> HibernateUtil.persist(any()));
    }

    @Test
    public void testGetSectionOrCreate_sectionExists_sectionIsReturned() {
        Section s = getTypicalSection();
        doReturn(s).when(usersDb).getSection(anyString(), anyString());

        Section section = usersDb.getSectionOrCreate("test-course", "test-section");

        assertEquals(s, section);
        mockHibernateUtil.verify(() -> HibernateUtil.persist(any()), never());
    }

    @Test
    public void testGetTeamOrCreate_noSection_sectionIsCreated() {
        doReturn(null).when(usersDb).getTeam(any(), any());

        usersDb.getTeamOrCreate(getTypicalSection(), "test-team");

        mockHibernateUtil.verify(() -> HibernateUtil.persist(any()));
    }

    @Test
    public void testGetTeamOrCreate_sectionExists_sectionIsReturned() {
        Team t = getTypicalTeam();
        doReturn(t).when(usersDb).getTeam(any(), any());

        Team team = usersDb.getTeamOrCreate(getTypicalSection(), "test-team");

        assertEquals(t, team);
        mockHibernateUtil.verify(() -> HibernateUtil.persist(any()), never());
    }
}
