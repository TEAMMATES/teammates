package teammates.it.storage.sqlapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
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

    
    private Course getTypicalCourse() {
        return new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "teammates");
    }
}
