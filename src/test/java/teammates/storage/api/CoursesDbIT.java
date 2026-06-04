package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import teammates.storage.entity.Course;
import teammates.storage.entity.Section;
import teammates.storage.entity.Team;
import teammates.test.BaseTestCaseWithDatabaseAccess;

/**
 * SUT: {@link CoursesDb}.
 */
public class CoursesDbIT extends BaseTestCaseWithDatabaseAccess {

    private final CoursesDb coursesDb = CoursesDb.inst();

    @Test
    public void testGetCourse() {
        ______TS("failure: get course that does not exist");
        Course actual = inTransaction(() -> coursesDb.getCourse("non-existent-course-id"));
        assertNull(actual);

        ______TS("success: get course that already exists");
        Course expected = getTypicalCourse();
        inTransaction(() -> coursesDb.createCourse(expected));

        actual = inTransaction(() -> coursesDb.getCourse(expected.getId()));
        assertEquals(expected, actual);
    }

    @Test
    public void testCreateCourse() {
        ______TS("success: create course that does not exist");
        Course course = getTypicalCourse();
        inTransaction(() -> coursesDb.createCourse(course));
        Course actualCourse = inTransaction(() -> coursesDb.getCourse("course-id"));
        assertEquals(course, actualCourse);
    }

    @Test
    public void testDeleteCourse() {
        Course course = getTypicalCourse();
        inTransaction(() -> coursesDb.createCourse(course));

        inTransaction(() -> coursesDb.deleteCourse(course));
        Course actualCourse = inTransaction(() -> coursesDb.getCourse(course.getId()));
        assertNull(actualCourse);
    }

    @Test
    public void testCreateSection() {
        Course course = getTypicalCourse();
        Section section = getTypicalSection();
        inTransaction(() -> coursesDb.createCourse(course));

        ______TS("success: create section that does not exist");
        inTransaction(() -> coursesDb.createSection(section));
        Section actualSection = inTransaction(() -> coursesDb.getSectionByName(course.getId(), section.getName()));
        assertEquals(section, actualSection);
    }

    @Test
    public void testGetSectionByName() {
        Course course = getTypicalCourse();
        Section section = getTypicalSection();
        inTransaction(() -> {
            coursesDb.createCourse(course);
            coursesDb.createSection(section);
        });

        ______TS("success: get section that already exists");
        Section actualSection = inTransaction(() -> coursesDb.getSectionByName(course.getId(), section.getName()));
        assertEquals(section, actualSection);

        ______TS("failure: get section that does not exist");
        Section nonExistentSection =
                inTransaction(() -> coursesDb.getSectionByName(course.getId(), "non-existent-section-name"));
        assertNull(nonExistentSection);
    }

    @Test
    public void testGetSectionByCourseIdAndTeam() {
        Course course = getTypicalCourse();
        Section section = new Section("section-name");
        Team team = new Team("team-name");
        inTransaction(() -> {
            coursesDb.createCourse(course);
            coursesDb.createSection(section);
            course.addSection(section);
            coursesDb.createTeam(team);
            section.addTeam(team);
        });

        ______TS("success: typical case");
        Section actualSection =
                inTransaction(() -> coursesDb.getSectionByCourseIdAndTeam(course.getId(), team.getName()));
        assertEquals(section, actualSection);
    }

    @Test
    public void testGetTeamsForCourse() {
        Course course = getTypicalCourse();
        Section section1 = new Section("section-name1");
        Team team1 = new Team("team-name1");
        Team team2 = new Team("team-name2");

        Section section2 = new Section("section-name2");
        Team team3 = new Team("team-name3");
        Team team4 = new Team("team-name4");
        inTransaction(() -> {
            coursesDb.createCourse(course);
            coursesDb.createSection(section1);
            course.addSection(section1);
            coursesDb.createTeam(team1);
            section1.addTeam(team1);
            coursesDb.createTeam(team2);
            section1.addTeam(team2);

            coursesDb.createSection(section2);
            course.addSection(section2);
            coursesDb.createTeam(team3);
            section2.addTeam(team3);
            coursesDb.createTeam(team4);
            section2.addTeam(team4);
        });

        List<Team> expectedTeams = List.of(team1, team2, team3, team4);

        ______TS("success: typical case");
        List<Team> actualTeams = inTransaction(() -> coursesDb.getTeamsForCourse(course.getId()));
        assertEquals(expectedTeams.size(), actualTeams.size());
        assertTrue(expectedTeams.containsAll(actualTeams));
    }

    @Test
    public void testCreateTeam() {
        Course course = getTypicalCourse();
        Section section = getTypicalSection();
        Team team = new Team("team-name1");
        section.addTeam(team);
        inTransaction(() -> {
            coursesDb.createCourse(course);
            coursesDb.createSection(section);
        });

        assertNotNull(inTransaction(() -> coursesDb.getSectionByName(course.getId(), section.getName())));

        ______TS("success: create team that does not exist");
        inTransaction(() -> coursesDb.createTeam(team));
        Team actualTeam = inTransaction(() -> coursesDb.getTeamByName(section.getId(), team.getName()));
        assertEquals(team, actualTeam);
    }

    @Test
    public void testGetTeamByName() {
        Course course = getTypicalCourse();
        Section section = getTypicalSection();
        Team team = new Team("team-name1");
        section.addTeam(team);
        inTransaction(() -> {
            coursesDb.createCourse(course);
            coursesDb.createSection(section);
            coursesDb.createTeam(team);
        });

        ______TS("success: get team that already exists");
        Team actualTeam = inTransaction(() -> coursesDb.getTeamByName(section.getId(), team.getName()));
        assertEquals(team, actualTeam);

        ______TS("success: null return");
        Team nonExistentTeam =
                inTransaction(() -> coursesDb.getTeamByName(section.getId(), "non-existent-team-name"));
        assertNull(nonExistentTeam);
    }
}
