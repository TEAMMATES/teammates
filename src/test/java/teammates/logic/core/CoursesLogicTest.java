package teammates.logic.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import teammates.storage.api.CoursesDb;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Team;
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
        AccountsLogic accountsLogic = mock(AccountsLogic.class);
        coursesLogic.initLogicDependencies(coursesDb, fsLogic, usersLogic, accountsLogic);
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
        course.addSection(s1);
        Section s2 = getTypicalSection();
        s2.setName("test-sectionName2");
        course.addSection(s2);

        when(coursesDb.getCourse(courseId)).thenReturn(course);

        List<String> sectionNames = coursesLogic.getSectionNamesForCourse(courseId);

        verify(coursesDb, times(1)).getCourse(courseId);

        List<String> expectedSectionNames = List.of("test-sectionName1", "test-sectionName2");

        assertEquals(expectedSectionNames, sectionNames);
    }

    @Test
    public void testGetSectionNamesForCourse_courseDoesNotExist_throwEntityDoesNotExistException() {
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
    public void testCreateDuplicateCourse_throwEntityAlreadyExistsException() {
        Course course = getTypicalCourse();

        when(coursesDb.getCourse(course.getId())).thenReturn(course);

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> coursesLogic.createCourse(course));

        assertEquals(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, course.toString()), ex.getMessage());
        verify(coursesDb, never()).createCourse(course);
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

        FeedbackSession fs = new FeedbackSession("test-fs", "test@email.com",
                "test", Instant.now(), Instant.now(), Instant.now(), Instant.now(), Duration.ofSeconds(60),
                false, false);
        course.addFeedbackSession(fs);

        FeedbackSession softDeletedFs = new FeedbackSession("soft-deleted-fs", "test@email.com",
                "test", Instant.now(), Instant.now(), Instant.now(), Instant.now(), Duration.ofSeconds(60),
                false, false);
        softDeletedFs.setDeletedAt(Instant.now());
        course.addFeedbackSession(softDeletedFs);
        instructors.add(getTypicalInstructor());

        when(usersLogic.getInstructorsForCourse(course.getId())).thenReturn(instructors);
        when(coursesDb.getCourse(course.getId())).thenReturn(course);

        coursesLogic.deleteCourseCascade(course.getId());

        verify(usersLogic, times(1)).getInstructorsForCourse(course.getId());
        verify(usersLogic, times(1)).deleteInstructorCascade(course.getId(), instructors.get(0).getEmail());
        verify(fsLogic, times(1)).deleteFeedbackSessionCascade(fs.getId());
        verify(fsLogic, times(1)).deleteFeedbackSessionCascade(softDeletedFs.getId());
        verify(coursesDb, times(1)).deleteCourse(course);
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
    public void testUpdateCourse_throwEntityDoesNotExistException() {
        Course course = getTypicalCourse();
        String courseId = course.getId();

        when(coursesDb.getCourse(courseId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.updateCourse(courseId, course.getName(), "Asia/Singapore"));

        assertEquals(ERROR_UPDATE_NON_EXISTENT + Course.class, ex.getMessage());
    }

    @Test
    public void testUpdateCourse_throwInvalidParametersException() {
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
        Course course = getTypicalCourse();

        doAnswer(invocation -> invocation.getArgument(0))
                .when(coursesDb)
                .createSection(any(Section.class));
        when(coursesDb.getSectionByName(course.getId(), "section-name")).thenReturn(null);

        Section createdSection = coursesLogic.createSection(course, "section-name");

        verify(coursesDb, times(1)).createSection(any(Section.class));
        assertNotNull(createdSection);
        assertEquals("section-name", createdSection.getName());
    }

    @Test
    public void testCreateDuplicateSection_throwEntityAlreadyExistsException() {
        Course course = getTypicalCourse();

        when(coursesDb.getSectionByName(course.getId(), "section-name")).thenReturn(getTypicalSection());

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> coursesLogic.createSection(course, "section-name"));

        assertEquals(String.format("Section with name %s already exists in course %s",
                "section-name", course.getId()), ex.getMessage());
    }

    @Test
    public void testCreateSectionInvalidName_throwInvalidParametersException() {
        Course course = getTypicalCourse();

        when(coursesDb.getSectionByName(course.getId(), "")).thenReturn(null);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> coursesLogic.createSection(course, null));

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
    public void testCreateTeam_shouldReturnCreatedTeam_success()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Section section = getTypicalSection();

        doAnswer(invocation -> invocation.getArgument(0))
                .when(coursesDb)
                .createTeam(any(Team.class));
        when(coursesDb.getTeamByName(section.getId(), "team-name")).thenReturn(null);

        Team createdTeam = coursesLogic.createTeam(section, "team-name");

        verify(coursesDb, times(1)).createTeam(any(Team.class));
        assertNotNull(createdTeam);
        assertEquals("team-name", createdTeam.getName());
    }

    @Test
    public void testCreateDuplicateTeam_throwEntityAlreadyExistsException() {
        Section section = getTypicalSection();

        when(coursesDb.getTeamByName(section.getId(), "team-name")).thenReturn(getTypicalTeam());

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> coursesLogic.createTeam(section, "team-name"));

        assertEquals("Team with name team-name already exists in section test-section", ex.getMessage());
    }

    @Test
    public void testCreateTeamInvalidName_throwInvalidParametersException() {
        Section section = getTypicalSection();

        when(coursesDb.getTeamByName(section.getId(), "team-name")).thenReturn(null);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> coursesLogic.createTeam(section, null));

        assertEquals("The provided team name is not acceptable to TEAMMATES as it cannot be empty.", ex.getMessage());
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
