package teammates.it.storage.sqlapi;

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
    public void testCreateCourse() throws Exception {
        ______TS("success: create course that does not exist");
        Course course = getTypicalCourse();
        coursesDb.createCourse(course);

        Course actualCourse = coursesDb.getCourse("course-id");
        verifyEquals(course, actualCourse);

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
    public void testGetSectionByCourseIdAndTeam() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = getTypicalCourse();
        Section section = new Section(course, "section-name");
        course.addSection(section);
        Team team = new Team(section, "team-name");
        section.addTeam(team);

        coursesDb.createCourse(course);

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

        ______TS("success: typical case");
        List<Team> actualTeams = coursesDb.getTeamsForSection(section);
        assertEquals(expectedTeams.size(), actualTeams.size());
        assertTrue(expectedTeams.containsAll(actualTeams));
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

        ______TS("success: typical case");
        List<Team> actualTeams = coursesDb.getTeamsForCourse(course.getId());
        assertEquals(expectedTeams.size(), actualTeams.size());
        assertTrue(expectedTeams.containsAll(actualTeams));
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
