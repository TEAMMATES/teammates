package teammates.it.storage.api;

import java.util.List;

import org.testng.annotations.Test;

import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.storage.api.CoursesDb;
import teammates.storage.entity.Course;
import teammates.storage.entity.Section;
import teammates.storage.entity.Team;

/**
 * SUT: {@link CoursesDb}.
 */
public class CoursesDbIT extends BaseTestCaseWithDatabaseAccess {

    private final CoursesDb coursesDb = CoursesDb.inst();

    @Test
    public void testGetCourse() {
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
    public void testCreateCourse() {
        ______TS("success: create course that does not exist");
        Course course = getTypicalCourse();
        coursesDb.createCourse(course);
        Course actualCourse = coursesDb.getCourse("course-id");
        verifyEquals(course, actualCourse);

        ______TS("failure: null course assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.createCourse(null));
    }

    @Test
    public void testDeleteCourse() {
        Course course = getTypicalCourse();
        coursesDb.createCourse(course);

        coursesDb.deleteCourse(course);
        Course actualCourse = coursesDb.getCourse(course.getId());
        assertNull(actualCourse);
    }

    @Test
    public void testCreateSection() {
        Course course = getTypicalCourse();
        Section section = getTypicalSection();
        coursesDb.createCourse(course);

        ______TS("success: create section that does not exist");
        coursesDb.createSection(section);
        Section actualSection = coursesDb.getSectionByName(course.getId(), section.getName());
        verifyEquals(section, actualSection);

        ______TS("failure: null section assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.createSection(null));
    }

    @Test
    public void testGetSectionByName() {
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
    public void testGetSectionByCourseIdAndTeam() {
        Course course = getTypicalCourse();
        coursesDb.createCourse(course);
        Section section = new Section(course, "section-name");
        coursesDb.createSection(section);
        course.addSection(section);
        Team team = new Team(section, "team-name");
        coursesDb.createTeam(team);
        section.addTeam(team);

        ______TS("failure: null courseId assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.getSectionByCourseIdAndTeam(null, team.getName()));

        ______TS("failure: null teamName assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.getSectionByCourseIdAndTeam(course.getId(), null));

        ______TS("success: typical case");
        Section actualSection = coursesDb.getSectionByCourseIdAndTeam(course.getId(), team.getName());
        verifyEquals(section, actualSection);
    }

    @Test
    public void testGetTeamsForCourse() {
        Course course = getTypicalCourse();
        coursesDb.createCourse(course);

        Section section1 = new Section(course, "section-name1");
        coursesDb.createSection(section1);
        course.addSection(section1);
        Team team1 = new Team(section1, "team-name1");
        coursesDb.createTeam(team1);
        section1.addTeam(team1);
        Team team2 = new Team(section1, "team-name2");
        coursesDb.createTeam(team2);
        section1.addTeam(team2);

        Section section2 = new Section(course, "section-name2");
        coursesDb.createSection(section2);
        course.addSection(section2);
        Team team3 = new Team(section2, "team-name3");
        coursesDb.createTeam(team3);
        section2.addTeam(team3);
        Team team4 = new Team(section2, "team-name4");
        coursesDb.createTeam(team4);
        section2.addTeam(team4);

        List<Team> expectedTeams = List.of(team1, team2, team3, team4);

        ______TS("failure: null courseId assertion exception thrown");
        assertThrows(AssertionError.class, () -> coursesDb.getTeamsForCourse(null));

        ______TS("success: typical case");
        List<Team> actualTeams = coursesDb.getTeamsForCourse(course.getId());
        assertEquals(expectedTeams.size(), actualTeams.size());
        assertTrue(expectedTeams.containsAll(actualTeams));
    }

    @Test
    public void testCreateTeam() {
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
    }

    @Test
    public void testGetTeamByName() {
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
}
