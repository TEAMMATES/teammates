package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Team;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code CoursesLogic}.
 */
public class CoursesLogicTest extends BaseTestCase {

    private CoursesLogic coursesLogic = CoursesLogic.inst();

    private UsersLogic usersLogic;

    private FeedbackSessionsLogic fsLogic;

    private CoursesDb coursesDb;

    @BeforeMethod
    public void setUp() {
        coursesDb = mock(CoursesDb.class);
        fsLogic = mock(FeedbackSessionsLogic.class);
        usersLogic = mock(UsersLogic.class);
        coursesLogic.initLogicDependencies(coursesDb, fsLogic, usersLogic);
    }

    @Test
    public void testMoveCourseToRecycleBin_shouldReturnBinnedCourse_success()
            throws EntityDoesNotExistException {
        Course course = getTypicalCourse();
        String courseId = course.getId();

        when(coursesDb.getCourse(courseId)).thenReturn(course);

        Course binnedCourse = coursesLogic.moveCourseToRecycleBin(courseId);

        verify(coursesDb, times(1)).getCourse(courseId);
        assertNotNull(binnedCourse);
    }

    @Test
    public void testMoveCourseToRecycleBin_courseDoesNotExist_throwEntityDoesNotExistException() {
        String courseId = getTypicalCourse().getId();

        when(coursesDb.getCourse(courseId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.moveCourseToRecycleBin(courseId));

        assertEquals("Trying to move a non-existent course to recycling bin.", ex.getMessage());
    }

    @Test
    public void testRestoreCourseFromRecycleBin_shouldSetDeletedAtToNull_success()
            throws EntityDoesNotExistException {
        Course course = getTypicalCourse();
        String courseId = course.getId();
        course.setDeletedAt(Instant.parse("2021-01-01T00:00:00Z"));

        when(coursesDb.getCourse(courseId)).thenReturn(course);

        coursesLogic.restoreCourseFromRecycleBin(courseId);

        verify(coursesDb, times(1)).getCourse(courseId);
        assertNull(course.getDeletedAt());
    }

    @Test
    public void testRestoreCourseFromRecycleBin_courseDoesNotExist_throwEntityDoesNotExistException() {
        String courseId = getTypicalCourse().getId();

        when(coursesDb.getCourse(courseId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.restoreCourseFromRecycleBin(courseId));

        assertEquals("Trying to restore a non-existent course from recycling bin.", ex.getMessage());
    }

    @Test
    public void testGetSectionNamesForCourse_shouldReturnListOfSectionNames_success() throws EntityDoesNotExistException {
        Course course = getTypicalCourse();
        String courseId = course.getId();

        Section s1 = getTypicalSection();
        s1.setName("test-sectionName1");

        Section s2 = getTypicalSection();
        s2.setName("test-sectionName2");

        List<Section> sections = new ArrayList<>();
        sections.add(s1);
        sections.add(s2);

        course.setSections(sections);

        when(coursesDb.getCourse(courseId)).thenReturn(course);

        List<String> sectionNames = coursesLogic.getSectionNamesForCourse(courseId);

        verify(coursesDb, times(1)).getCourse(courseId);

        List<String> expectedSectionNames = List.of("test-sectionName1", "test-sectionName2");

        assertEquals(expectedSectionNames, sectionNames);
    }

    @Test
    public void testGetSectionNamesForCourse_courseDoesNotExist_throwEntityDoesNotExistException()
            throws EntityDoesNotExistException {
        String courseId = getTypicalCourse().getId();

        when(coursesDb.getCourse(courseId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.getSectionNamesForCourse(courseId));

        assertEquals("Trying to get section names for a non-existent course.", ex.getMessage());
    }

    @Test
    public void testCreateCourse_shouldReturnCreatedCourse_success()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Course course = getTypicalCourse();

        when(coursesDb.createCourse(course)).thenReturn(course);

        Course createdCourse = coursesLogic.createCourse(course);

        verify(coursesDb, times(1)).createCourse(course);
        assertNotNull(createdCourse);
    }

    @Test
    public void testCreateDuplicateCourse_throwEntityAlreadyExistsException()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = getTypicalCourse();

        when(coursesDb.createCourse(course))
                .thenThrow(new EntityAlreadyExistsException(
                    String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, course.toString())));

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> coursesLogic.createCourse(course));

        assertEquals(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, course.toString()), ex.getMessage());
    }

    @Test
    public void testGetCourse_shouldReturnCourse_success() {
        Course course = getTypicalCourse();
        String courseId = course.getId();

        when(coursesDb.getCourse(courseId)).thenReturn(course);

        Course returnedCourse = coursesLogic.getCourse(courseId);

        verify(coursesDb, times(1)).getCourse(courseId);
        assertNotNull(returnedCourse);
    }

    @Test
    public void testDeleteCourseCascade_shouldDeleteCourse_success() {
        Course course = getTypicalCourse();
        List<Instructor> instructors = new ArrayList<>();
        List<FeedbackSession> feedbackSessions = new ArrayList<>();

        FeedbackSession fs = new FeedbackSession("test-fs", course, "test@email.com",
                "test", Instant.now(), Instant.now(), Instant.now(), Instant.now(), Duration.ofSeconds(60),
                false, false, false);
        feedbackSessions.add(fs);
        instructors.add(getTypicalInstructor());

        when(fsLogic.getFeedbackSessionsForCourse(course.getId())).thenReturn(feedbackSessions);
        when(usersLogic.getInstructorsForCourse(course.getId())).thenReturn(instructors);
        when(coursesDb.getCourse(course.getId())).thenReturn(course);

        coursesLogic.deleteCourseCascade(course.getId());

        verify(usersLogic, times(1)).deleteStudentsInCourseCascade(course.getId());
        verify(usersLogic, times(1)).getInstructorsForCourse(course.getId());
        verify(usersLogic, times(1)).deleteInstructorCascade(course.getId(), instructors.get(0).getEmail());
        verify(fsLogic, times(1)).deleteFeedbackSessionCascade(fs.getName(), course.getId());
        verify(fsLogic, times(1)).getFeedbackSessionsForCourse(course.getId());
        verify(coursesDb, times(1)).deleteCourse(course);
        verify(coursesDb, times(1)).deleteSectionsByCourseId(course.getId());
    }

    @Test
    public void testUpdateCourse_shouldReturnUpdatedCourse_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        Course course = getTypicalCourse();
        String courseId = course.getId();

        when(coursesDb.getCourse(courseId)).thenReturn(course);

        Course updatedCourse = coursesLogic.updateCourse(courseId, "Test Course 1", "Asia/India");

        verify(coursesDb, times(1)).getCourse(courseId);
        assertNotNull(updatedCourse);
        assertEquals("Test Course 1", updatedCourse.getName());
        assertEquals("Asia/India", updatedCourse.getTimeZone());
    }

    @Test
    public void testUpdateCourse_throwEntityDoesNotExistException()
            throws InvalidParametersException, EntityDoesNotExistException {
        Course course = getTypicalCourse();
        String courseId = course.getId();

        when(coursesDb.getCourse(courseId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.updateCourse(courseId, course.getName(), "Asia/Singapore"));

        assertEquals(ERROR_UPDATE_NON_EXISTENT + Course.class, ex.getMessage());
    }

    @Test
    public void testUpdateCourse_throwInvalidParametersException()
            throws InvalidParametersException, EntityDoesNotExistException {
        Course course = getTypicalCourse();
        String courseId = course.getId();

        when(coursesDb.getCourse(courseId)).thenReturn(course);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> coursesLogic.updateCourse(courseId, "", "Asia/Singapore"));

        String expectedMessage = "The field 'course name' is empty."
                + " The value of a/an course name should be no longer than 80 characters."
                + " It should not be empty.";

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    public void testCreateSection_shouldReturnCreatedSection_success()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Section section = getTypicalSection();

        when(coursesDb.createSection(section)).thenReturn(section);

        Section createdSection = coursesLogic.createSection(section);

        verify(coursesDb, times(1)).createSection(section);
        assertNotNull(createdSection);
    }

    @Test
    public void testCreateDuplicateSection_throwEntityAlreadyExistsException()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Section section = getTypicalSection();

        when(coursesDb.createSection(section))
                .thenThrow(new EntityAlreadyExistsException(
                    String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, section.toString())));

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> coursesLogic.createSection(section));

        assertEquals(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, section.toString()), ex.getMessage());
    }

    @Test
    public void testCreateSectionInvalidName_throwInvalidParametersException()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Section section = getTypicalSection();
        section.setName(null);

        when(coursesDb.createSection(section)).thenThrow(new InvalidParametersException(section.getInvalidityInfo()));

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> coursesLogic.createSection(section));

        assertEquals("The provided section name is not acceptable to TEAMMATES as it cannot be empty.", ex.getMessage());
    }

    @Test
    public void testGetSectionByCourseIdAndTeam_shouldReturnSection_success() {
        Section section = getTypicalSection();
        String courseId = section.getCourse().getId();
        String teamName = section.getName();

        when(coursesDb.getSectionByCourseIdAndTeam(courseId, teamName)).thenReturn(section);

        Section returnedSection = coursesLogic.getSectionByCourseIdAndTeam(courseId, teamName);

        verify(coursesDb, times(1)).getSectionByCourseIdAndTeam(courseId, teamName);
        assertNotNull(returnedSection);
    }

    @Test
    public void testGetSectionByCourseIdAndTeam_sectionDoesNotExist_returnNull() {
        String courseId = getTypicalCourse().getId();
        String teamName = getTypicalSection().getName();

        when(coursesDb.getSectionByCourseIdAndTeam(courseId, teamName)).thenReturn(null);

        Section returnedSection = coursesLogic.getSectionByCourseIdAndTeam(courseId, teamName);

        verify(coursesDb, times(1)).getSectionByCourseIdAndTeam(courseId, teamName);
        assertNull(returnedSection);
    }

    @Test
    public void testGetCourseInstitute_shouldReturnInstitute_success() {
        Course course = getTypicalCourse();
        String courseId = course.getId();

        when(coursesDb.getCourse(courseId)).thenReturn(course);

        String institute = coursesLogic.getCourseInstitute(courseId);

        verify(coursesDb, times(1)).getCourse(courseId);
        assertNotNull(institute);
    }

    @Test
    public void testGetCourseInstituteNonExistentCourse_throwAssertionError() {
        Course course = getTypicalCourse();
        String courseId = course.getId();

        when(coursesDb.getCourse(courseId)).thenReturn(null);

        AssertionError ex = assertThrows(AssertionError.class,
                () -> coursesLogic.getCourseInstitute(courseId));

        assertEquals("Trying to getCourseInstitute for inexistent course with id " + courseId, ex.getMessage());
    }

    @Test
    public void testCreateTeam_shouldReturnCreatedTeam_success()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Team team = getTypicalTeam();

        when(coursesDb.createTeam(team)).thenReturn(team);

        Team createdTeam = coursesLogic.createTeam(team);

        verify(coursesDb, times(1)).createTeam(team);
        assertNotNull(createdTeam);
    }

    @Test
    public void testCreateDuplicateTeam_throwEntityAlreadyExistsException()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Team team = getTypicalTeam();

        when(coursesDb.createTeam(team)).thenThrow(
                new EntityAlreadyExistsException(
                    String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, team.toString())));

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> coursesLogic.createTeam(team));

        assertEquals(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, team.toString()), ex.getMessage());
    }

    @Test
    public void testCreateTeamInvalidName_throwInvalidParametersException()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Team team = getTypicalTeam();
        team.setName(null);

        when(coursesDb.createTeam(team)).thenThrow(new InvalidParametersException(team.getInvalidityInfo()));

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> coursesLogic.createTeam(team));

        assertEquals("The provided team name is not acceptable to TEAMMATES as it cannot be empty.", ex.getMessage());
    }

    @Test
    public void testGetTeamsForSection_shouldReturnListOfTeams_success() {
        Section section = getTypicalSection();

        Team t1 = getTypicalTeam();
        t1.setName("test-teamName1");

        Team t2 = getTypicalTeam();
        t2.setName("test-teamName2");

        List<Team> teams = new ArrayList<>();
        teams.add(t1);
        teams.add(t2);

        section.setTeams(teams);

        when(coursesDb.getTeamsForSection(section)).thenReturn(teams);

        List<Team> returnedTeams = coursesLogic.getTeamsForSection(section);

        verify(coursesDb, times(1)).getTeamsForSection(section);

        List<Team> expectedTeams = List.of(t1, t2);

        assertEquals(expectedTeams, returnedTeams);
    }

    @Test
    public void testGetTeamsForCourse_shouldReturnListOfTeams_success() {
        Course course = getTypicalCourse();

        Team t1 = getTypicalTeam();
        t1.setName("test-teamName1");

        Team t2 = getTypicalTeam();
        t2.setName("test-teamName2");

        List<Team> teams = new ArrayList<>();
        teams.add(t1);
        teams.add(t2);

        when(coursesDb.getTeamsForCourse(course.getId())).thenReturn(teams);

        List<Team> returnedTeams = coursesLogic.getTeamsForCourse(course.getId());

        verify(coursesDb, times(1)).getTeamsForCourse(course.getId());

        List<Team> expectedTeams = List.of(t1, t2);

        assertEquals(expectedTeams, returnedTeams);
    }
}
