package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.testng.annotations.Test;

import teammates.storage.entity.Course;
import teammates.storage.entity.Section;
import teammates.storage.entity.Team;
import teammates.test.GroupNames;

/**
 * Tests for {@link CoursesDb}.
 */
public class CoursesDbTest extends BaseDbTestcase {
    CoursesDb coursesDb = CoursesDb.inst();

    @Test(groups = GroupNames.DB)
    public void getCourse_courseExists_returnsCourse() {
        String courseId = given.course("course");
        persistGivenData(given);

        Course actual = inTransaction(() -> coursesDb.getCourse(courseId));

        assertNotNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void getCourse_courseDoesNotExist_returnsNull() {
        given.course("another-course");
        persistGivenData(given);

        Course actual = inTransaction(() -> coursesDb.getCourse("non-existent-course"));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void persistCourse_courseIsNew_courseIsPersisted() {
        Course course = buildDefaultCourse("new-course");

        Course actual = inTransaction(() -> coursesDb.persistCourse(course));

        assertEquals(course.getId(), actual.getId());
        verifyPresentInDatabase(Course.class, course.getId());
    }

    @Test(groups = GroupNames.DB)
    public void persistCourse_courseIdExists_throwsException() {
        String existingCourseId = given.course("existing-course");
        persistGivenData(given);
        Course course = buildDefaultCourse(existingCourseId);

        assertThrowsInTransaction(ConstraintViolationException.class, () -> coursesDb.persistCourse(course));
    }

    @Test(groups = GroupNames.DB)
    public void removeCourse_courseExists_courseIsRemoved() {
        String existingCourseId = given.course("existing-course");
        persistGivenData(given);

        inTransaction(() -> coursesDb.removeCourse(coursesDb.getCourse(existingCourseId)));

        verifyAbsentInDatabase(Course.class, existingCourseId);
    }

    @Test(groups = GroupNames.DB)
    public void persistSection_newSection_sectionIsPersisted() {
        String courseId = given.course("course");
        persistGivenData(given);

        Section actual = inTransaction(() -> {
            Course course = getEntity(Course.class, courseId);
            Section section = buildDefaultSection(course, given.uuid("section"));
            coursesDb.persistSection(section);
            return section;
        });

        verifyPresentInDatabase(Section.class, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getSectionByName_sectionExists_returnsSection() {
        String courseId = given.course("course");
        UUID sectionId = given.section("section", s -> s.name("section-name").course("course"));
        persistGivenData(given);

        Section actual = inTransaction(() -> coursesDb.getSectionByName(courseId, "section-name"));

        assertNotNull(actual);
        assertEquals(sectionId, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getSectionByName_sectionDoesNotExist_returnsNull() {
        String courseId = given.course("course");
        given.section("another-section", s -> s.course("course"));
        persistGivenData(given);

        Section actual = inTransaction(() -> coursesDb.getSectionByName(courseId, "non-existent-section"));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void persistTeam_newTeam_teamIsPersisted() {
        UUID sectionId = given.section("section");
        persistGivenData(given);

        Team actual = inTransaction(() -> {
            Section section = getEntity(Section.class, sectionId);
            Team team = buildDefaultTeam(section, given.uuid("team"));
            coursesDb.persistTeam(team);
            return team;
        });

        verifyPresentInDatabase(Team.class, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getTeamByName_teamExists_returnsTeam() {
        UUID sectionId = given.section("section");
        UUID teamId = given.team("team", t -> t.name("team-name").section("section"));
        persistGivenData(given);

        Team actual = inTransaction(() -> coursesDb.getTeamByName(sectionId, "team-name"));

        assertEquals(teamId, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getTeamByName_teamDoesNotExist_returnsNull() {
        UUID sectionId = given.section("section");
        given.team("another-team", t -> t.name("another-team-name").section("section"));
        persistGivenData(given);

        Team actual = inTransaction(() -> coursesDb.getTeamByName(sectionId, "non-existent-team"));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void getTeamsForCourse_teamsExist_returnsTeams() {
        // Teams in same course that should be returned
        String courseId = given.course("course");
        UUID teamId1 = given.team("team1", t -> t.course("course"));
        UUID teamId2 = given.team("team2", t -> t.course("course"));
        // Teams in other course that should not be returned
        given.course("another-course");
        given.team("team3", t -> t.course("another-course"));
        persistGivenData(given);

        List<Team> actual = inTransaction(() -> coursesDb.getTeamsForCourse(courseId));

        assertEquals(2, actual.size());
        assertEquals(Set.of(teamId1, teamId2), actual.stream().map(Team::getId).collect(Collectors.toSet()));
    }

    private static Course buildDefaultCourse(String courseId) {
        return new Course(courseId, "Course Name", "UTC", "Institute");
    }

    private static Team buildDefaultTeam(Section section, UUID teamId) {
        assertNotNull(section);
        Team team = new Team("Team Name");
        team.setId(teamId);
        section.addTeam(team);
        return team;
    }

    private static Section buildDefaultSection(Course course, UUID sectionId) {
        assertNotNull(course);
        Section section = new Section("Section Name");
        section.setId(sectionId);
        section.setCourse(course);
        return section;
    }
}
