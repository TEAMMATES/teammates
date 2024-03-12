package teammates.it.storage.sqlapi;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Team;

/**
 * SUT: {@link CoursesDb}.
 */
public class CoursesDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final CoursesDb coursesDb = CoursesDb.inst();

    @Test
    public void testGetCourse() throws Exception {
        ______TS("failure: get course that does not exist");
        Course actual = coursesDb.getCourse("non-existent-course-id");
        assertNull(actual);

        ______TS("failure: null assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.getCourse(null));

        ______TS("success: get course that already exists");
        Course expected = getTypicalCourse();
        coursesDb.createCourse(expected);

        actual = coursesDb.getCourse(expected.getId());
        verifyEquals(expected, actual);
    }

    @Test
    public void testCreateCourse() throws Exception {
        ______TS("success: create course that does not exist");
        Course course = getTypicalCourse();
        coursesDb.createCourse(course);
        Course actualCourse = coursesDb.getCourse("course-id");
        verifyEquals(course, actualCourse);

        ______TS("failure: null course assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.createCourse(null));

        ______TS("failure: invalid course details");
        Course invalidCourse = new Course("course-id", "!@#!@#", "Asia/Singapore", "institute");
        assertThrows(InvalidParametersException.class, () -> coursesDb.createCourse(invalidCourse));

        ______TS("failure: create course that already exist, execption thrown");
        Course identicalCourse = getTypicalCourse();
        assertNotSame(course, identicalCourse);

        assertThrows(EntityAlreadyExistsException.class, () -> coursesDb.createCourse(identicalCourse));
    }

    @Test
    public void testUpdateCourse() throws Exception {
        ______TS("failure: update course that does not exist, exception thrown");
        Course course = getTypicalCourse();
        assertThrows(EntityDoesNotExistException.class, () -> coursesDb.updateCourse(course));

        ______TS("failure: null course assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.updateCourse(null));

        ______TS("success: update course that already exists");
        coursesDb.createCourse(course);
        course.setName("new course name");

        coursesDb.updateCourse(course);
        Course actual = coursesDb.getCourse("course-id");
        verifyEquals(course, actual);

        ______TS("success: update detached course that already exists");

        // same id, different name
        Course detachedCourse = getTypicalCourse();
        detachedCourse.setName("different-name");

        coursesDb.updateCourse(detachedCourse);
        verifyEquals(course, detachedCourse);
    }

    @Test
    public void testDeleteCourse() throws Exception {
        ______TS("success: delete course that already exists");
        Course course = getTypicalCourse();
        coursesDb.createCourse(course);

        coursesDb.deleteCourse(course);
        Course actualCourse = coursesDb.getCourse(course.getId());
        assertNull(actualCourse);
    }

    @Test
    public void testCreateSection() throws Exception {
        Course course = getTypicalCourse();
        Section section = getTypicalSection();
        coursesDb.createCourse(course);

        ______TS("success: create section that does not exist");
        coursesDb.createSection(section);
        Section actualSection = coursesDb.getSectionByName(course.getId(), section.getName());
        verifyEquals(section, actualSection);

        ______TS("failure: null section assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.createSection(null));

        ______TS("failure: invalid section details");
        Section invalidSection = new Section(course, null);
        assertThrows(InvalidParametersException.class, () -> coursesDb.createSection(invalidSection));

        ______TS("failure: create section that already exist, execption thrown");
        assertThrows(EntityAlreadyExistsException.class, () -> coursesDb.createSection(section));
    }

    @Test
    public void testGetSectionByName() throws Exception {
        Course course = getTypicalCourse();
        Section section = getTypicalSection();
        coursesDb.createCourse(course);
        coursesDb.createSection(section);

        ______TS("failure: null courseId assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.getSectionByName(null, section.getName()));

        ______TS("failure: null sectionName assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.getSectionByName(course.getId(), null));

        ______TS("success: get section that already exists");
        Section actualSection = coursesDb.getSectionByName(course.getId(), section.getName());
        verifyEquals(section, actualSection);

        ______TS("failure: get section that does not exist");
        Section nonExistentSection = coursesDb.getSectionByName(course.getId(), "non-existent-section-name");
        assertNull(nonExistentSection);
    }

    @Test
    public void testGetSectionByCourseIdAndTeam() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = getTypicalCourse();
        Section section = new Section(course, "section-name");
        course.addSection(section);
        Team team = new Team(section, "team-name");
        section.addTeam(team);
        coursesDb.createCourse(course);

        ______TS("failure: null courseId assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.getSectionByCourseIdAndTeam(null, team.getName()));

        ______TS("failure: null teamName assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.getSectionByCourseIdAndTeam(course.getId(), null));

        ______TS("success: typical case");
        Section actualSection = coursesDb.getSectionByCourseIdAndTeam(course.getId(), team.getName());
        verifyEquals(section, actualSection);
    }

    @Test
    public void testGetTeamsForSection() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = getTypicalCourse();
        Section section = new Section(course, "section-name");
        course.addSection(section);
        Team team1 = new Team(section, "team-name1");
        section.addTeam(team1);
        Team team2 = new Team(section, "team-name2");
        section.addTeam(team2);

        List<Team> expectedTeams = List.of(team1, team2);

        coursesDb.createCourse(course);

        ______TS("failure: null section assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.getTeamsForSection(null));

        ______TS("success: typical case");
        List<Team> actualTeams = coursesDb.getTeamsForSection(section);
        assertEquals(expectedTeams.size(), actualTeams.size());
        assertTrue(expectedTeams.containsAll(actualTeams));
    }

    @Test
    public void testDeleteSectionsByCourseId() throws Exception {
        Course course = getTypicalCourse();
        coursesDb.createCourse(course);
        List<Section> expectedSections = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Section newSection = new Section(course, "section-name" + i);
            expectedSections.add(newSection);
            course.addSection(newSection);
            assertNotNull(coursesDb.getSectionByName(course.getId(), newSection.getName()));
        }

        ______TS("success: delete sections by course id");
        coursesDb.deleteSectionsByCourseId(course.getId());
        for (Section section : expectedSections) {
            Section actualSection = coursesDb.getSectionByName(course.getId(), section.getName());
            assertNull(actualSection);
        }
    }

    @Test
    public void testGetTeamsForCourse() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = getTypicalCourse();

        Section section1 = new Section(course, "section-name1");
        course.addSection(section1);
        Team team1 = new Team(section1, "team-name1");
        section1.addTeam(team1);
        Team team2 = new Team(section1, "team-name2");
        section1.addTeam(team2);

        Section section2 = new Section(course, "section-name2");
        course.addSection(section2);
        Team team3 = new Team(section2, "team-name3");
        section2.addTeam(team3);
        Team team4 = new Team(section2, "team-name4");
        section2.addTeam(team4);

        List<Team> expectedTeams = List.of(team1, team2, team3, team4);

        coursesDb.createCourse(course);

        ______TS("failure: null courseId assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.getTeamsForCourse(null));

        ______TS("success: typical case");
        List<Team> actualTeams = coursesDb.getTeamsForCourse(course.getId());
        assertEquals(expectedTeams.size(), actualTeams.size());
        assertTrue(expectedTeams.containsAll(actualTeams));
    }

    @Test
    public void testCreateTeam() throws Exception {
        Course course = getTypicalCourse();
        Section section = getTypicalSection();
        Team team = new Team(section, "team-name1");
        coursesDb.createCourse(course);
        coursesDb.createSection(section);

        assertNotNull(coursesDb.getSectionByName(course.getId(), section.getName()));

        ______TS("failure: null team assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.createTeam(null));

        ______TS("success: create team that does not exist");
        coursesDb.createTeam(team);
        Team actualTeam = coursesDb.getTeamByName(section.getId(), team.getName());
        verifyEquals(team, actualTeam);

        ______TS("failure: invalid team details");
        Team invalidTeam = new Team(section, null);
        assertThrows(InvalidParametersException.class, () -> coursesDb.createTeam(invalidTeam));

        ______TS("failure: create team that already exist, execption thrown");
        assertThrows(EntityAlreadyExistsException.class, () -> coursesDb.createTeam(team));
    }

    @Test
    public void testGetTeamByName() throws Exception {
        Course course = getTypicalCourse();
        Section section = getTypicalSection();
        Team team = new Team(section, "team-name1");
        coursesDb.createCourse(course);
        coursesDb.createSection(section);
        coursesDb.createTeam(team);

        ______TS("success: get team that already exists");
        Team actualTeam = coursesDb.getTeamByName(section.getId(), team.getName());
        verifyEquals(team, actualTeam);

        ______TS("failure: null sectionId assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.getTeamByName(null, team.getName()));

        ______TS("failure: null teamName assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.getTeamByName(section.getId(), null));

        ______TS("success: null return");
        Team nonExistentTeam = coursesDb.getTeamByName(section.getId(), "non-existent-team-name");
        assertNull(nonExistentTeam);
    }

    @Test
    public void testSqlInjectionInCreateCourse() throws Exception {
        ______TS("SQL Injection test in createCourse");

        // Attempt to use SQL commands in name field
        String courseName = "test'; DROP TABLE courses; --";
        Course course = new Course("course-id", courseName, "UTC", "teammates");

        // The system should treat the input as a plain text string
        coursesDb.createCourse(course);
        Course actual = coursesDb.getCourse("course-id");
        assertEquals(courseName, actual.getName());
    }

    @Test
    public void testSqlInjectionInGetCourse() throws Exception {
        ______TS("SQL Injection test in getCourse");

        Course course = new Course("course-id", "course-name", "UTC", "teammates");
        coursesDb.createCourse(course);

        // Attempt to use SQL commands in courseId field
        String courseId = "test' OR 1 = 1; --";
        Course actual = coursesDb.getCourse(courseId);
        assertEquals(null, actual);
    }

    @Test
    public void testSqlInjectionInUpdateCourse() throws Exception {
        ______TS("SQL Injection test in updateCourse");

        Course course = new Course("course-id", "name", "UTC", "institute");
        coursesDb.createCourse(course);

        // The system should treat the input as a plain text string
        String newName = "newName'; DROP TABLE courses; --";
        course.setName(newName);
        coursesDb.updateCourse(course);
        Course actual = coursesDb.getCourse("course-id");
        assertEquals(newName, actual.getName());
    }

    @Test
    public void testSqlInjectionInDeleteCourse() throws Exception {
        ______TS("SQL Injection test in deleteCourse");

        Course course = new Course("course-id", "name", "UTC", "institute");
        coursesDb.createCourse(course);

        String name = "newName'; DELETE FROM courses; --";
        Course injectionCourse = new Course("course-id-injection", name, "UTC", "institute");
        coursesDb.createCourse(injectionCourse);

        coursesDb.deleteCourse(injectionCourse);
        Course actualInjectionCourse = coursesDb.getCourse("course-id-injection");

        // The course should be deleted
        assertEquals(null, actualInjectionCourse);

        // All other courses should not be deleted
        Course actualCourse = coursesDb.getCourse("course-id");
        assertEquals(course, actualCourse);
    }

    @Test
    public void testSqlInjectionInCreateSection() throws Exception {
        ______TS("SQL Injection test in createSection");

        // Attempt to use SQL commands in sectionName fields
        Course course = new Course("course-id", "name", "UTC", "institute");
        coursesDb.createCourse(course);
        String sectionName = "section'; DROP TABLE courses; --";
        Section section = new Section(course, sectionName);

        // The system should treat the input as a plain text string
        coursesDb.createSection(section);

        // Check that we are still able to get courses
        Course actualCourse = coursesDb.getCourse("course-id");
        assertEquals(course, actualCourse);
    }

    @Test
    public void testSqlInjectionInGetSectionByName() throws Exception {
        ______TS("SQL Injection test in getSectionByName");

        Course course = new Course("course-id", "course-name", "UTC", "institute");
        coursesDb.createCourse(course);
        String sectionName = "section-name";
        Section section = new Section(course, sectionName);

        coursesDb.createSection(section);
        Section actual = coursesDb.getSectionByName("course-id", "section-name'; DROP TABLE courses; --");
        assertEquals(null, actual);
        Section actualSection = coursesDb.getSectionByName("course-id", sectionName);
        assertEquals(sectionName, actualSection.getName());
    }

    @Test
    public void testSqlInjectionInGetSectionByCourseIdAndTeam() throws Exception {
        ______TS("SQL Injection test in getSectionByCourseIdAndTeam");

        Course course = new Course("course-id", "course-name", "UTC", "institute");
        Section section = new Section(course, "section-name");
        course.addSection(section);
        Team team = new Team(section, "team-name");
        section.addTeam(team);
        coursesDb.createCourse(course);

        // The system should treat the input as a plain text string
        String teamNameInjection = "team-name'; DROP TABLE courses; --";
        Section actual = coursesDb.getSectionByCourseIdAndTeam("course-id", teamNameInjection);
        assertEquals(null, actual);
        Section actualSection = coursesDb.getSectionByCourseIdAndTeam("course-id", "team-name");
        assertEquals("team-name", actualSection.getTeams().get(0).getName());
    }

    @Test
    public void testSqlInjectionInDeleteSectionsByCourseId() throws Exception {
        ______TS("SQL Injection test in deleteSectionsByCourseId");

        Course course = new Course("course-id", "name", "UTC", "institute");
        Section section = new Section(course, "section-name");
        course.addSection(section);
        coursesDb.createCourse(course);

        String courseId = "course-id'; DELETE FROM courses; --";
        coursesDb.deleteSectionsByCourseId(courseId);

        // The sections should not be deleted
        Section actualSection = coursesDb.getSectionByName("course-id", "section-name");
        assertEquals(section, actualSection);
    }

    @Test
    public void testSqlInjectionInGetTeamsForSection() throws Exception {
        ______TS("SQL Injection test in getTeamsForSection");

        Course course = new Course("course-id", "course-name", "UTC", "institute");
        Section section = new Section(course, "section-name");
        course.addSection(section);
        Team team = new Team(section, "team-name");
        section.addTeam(team);
        coursesDb.createCourse(course);

        String sectionName = "section-name' OR 1 = 1; --";
        Section sectionInjection = new Section(course, sectionName);
        List<Team> actual = coursesDb.getTeamsForSection(sectionInjection);
        assertEquals(0, actual.size());
    }

    @Test
    public void testSqlInjectionInGetTeamsForCourse() throws Exception {
        ______TS("SQL Injection test in getTeamsForCourse");

        Course course = new Course("course-id", "course-name", "UTC", "institute");
        Section section = new Section(course, "section-name");
        course.addSection(section);
        Team team = new Team(section, "team-name");
        section.addTeam(team);
        coursesDb.createCourse(course);

        String courseId = "course-id' OR 1 = 1; --";
        List<Team> actual = coursesDb.getTeamsForCourse(courseId);
        assertEquals(0, actual.size());
    }

    @Test
    public void testSqlInjectionInCreateTeam() throws Exception {
        ______TS("SQL Injection test in createTeam");

        Course course = new Course("course-id", "course-name", "UTC", "institute");
        Section section = new Section(course, "section-name");
        course.addSection(section);
        coursesDb.createCourse(course);

        String teamName = "team'; DROP TABLE courses; --";
        Team team = new Team(section, teamName);
        coursesDb.createTeam(team);

        List<Team> actual = coursesDb.getTeamsForSection(section);
        assertEquals(1, actual.size());
        assertEquals(teamName, actual.get(0).getName());
    }

    @Test
    public void testSqlInjectionInGetTeamByName() throws Exception {
        ______TS("SQL Injection test in getTeamByName");

        Course course = new Course("course-id", "course-name", "UTC", "institute");
        Section section = new Section(course, "section-name");
        course.addSection(section);
        Team team = new Team(section, "team-name");
        section.addTeam(team);
        coursesDb.createCourse(course);

        String teamName = "team-name'; DROP TABLE courses; --";
        Team actual = coursesDb.getTeamByName(section.getId(), teamName);
        assertEquals(null, actual);
        Team actualTeam = coursesDb.getTeamByName(section.getId(), "team-name");
        assertEquals(team, actualTeam);
    }
}
