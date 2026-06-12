package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityExistsException;

import org.testng.annotations.Test;

import teammates.storage.entity.Course;
import teammates.storage.entity.Institute;
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
        var course = given.course("course");
        persistGivenData(given);

        Course actual = inTransaction(() -> coursesDb.getCourse(course.id()));

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
        var institute = given.institute("institute");
        persistGivenData(given);
        Course course = buildDefaultCourse("new-course");

        Course actual = inTransaction(() -> {
            getEntity(Institute.class, institute.id()).addCourse(course);
            return coursesDb.persistCourse(course);
        });

        assertEquals(course.getId(), actual.getId());
        verifyPresentInDatabase(Course.class, course.getId());
    }

    @Test(groups = GroupNames.DB)
    public void persistCourse_courseIdExists_throwsException() {
        var institute = given.institute("institute");
        var existingCourse = given.course("existing-course", c -> c.institute(institute.alias()));
        persistGivenData(given);
        Course course = buildDefaultCourse(existingCourse.id());

        assertThrowsInTransaction(EntityExistsException.class, () -> {
            getEntity(Institute.class, institute.id()).addCourse(course);
            coursesDb.persistCourse(course);
        });
    }

    @Test(groups = GroupNames.DB)
    public void removeCourse_courseExists_courseIsRemoved() {
        var existingCourse = given.course("existing-course");
        persistGivenData(given);

        inTransaction(() -> coursesDb.removeCourse(coursesDb.getCourse(existingCourse.id())));

        verifyAbsentInDatabase(Course.class, existingCourse.id());
    }

    @Test(groups = GroupNames.DB)
    public void persistSection_newSection_sectionIsPersisted() {
        var courseRef = given.course("course");
        persistGivenData(given);

        Section actual = inTransaction(() -> {
            Course course = getEntity(Course.class, courseRef.id());
            Section section = buildDefaultSection(course, given.uuid("section"));
            coursesDb.persistSection(section);
            return section;
        });

        verifyPresentInDatabase(Section.class, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getSectionByName_sectionExists_returnsSection() {
        var course = given.course("course");
        var section = given.section("section", s -> s.name("section-name").course(course.alias()));
        persistGivenData(given);

        Section actual = inTransaction(() -> coursesDb.getSectionByName(course.id(), "section-name"));

        assertNotNull(actual);
        assertEquals(section.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getSectionByName_sectionDoesNotExist_returnsNull() {
        var course = given.course("course");
        given.section("another-section", s -> s.course(course.alias()));
        persistGivenData(given);

        Section actual = inTransaction(() -> coursesDb.getSectionByName(course.id(), "non-existent-section"));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void persistTeam_newTeam_teamIsPersisted() {
        var sectionRef = given.section("section");
        persistGivenData(given);

        Team actual = inTransaction(() -> {
            Section section = getEntity(Section.class, sectionRef.id());
            Team team = buildDefaultTeam(section, given.uuid("team"));
            coursesDb.persistTeam(team);
            return team;
        });

        verifyPresentInDatabase(Team.class, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getTeamByName_teamExists_returnsTeam() {
        var section = given.section("section");
        var team = given.team("team", t -> t.name("team-name").section(section.alias()));
        persistGivenData(given);

        Team actual = inTransaction(() -> coursesDb.getTeamByName(section.id(), "team-name"));

        assertEquals(team.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getTeamByName_teamDoesNotExist_returnsNull() {
        var section = given.section("section");
        given.team("another-team", t -> t.name("another-team-name").section(section.alias()));
        persistGivenData(given);

        Team actual = inTransaction(() -> coursesDb.getTeamByName(section.id(), "non-existent-team"));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void getTeamsForCourse_teamsExist_returnsTeams() {
        // Teams in same course that should be returned
        var course = given.course("course");
        var team1 = given.team("team1", t -> t.course(course.alias()));
        var team2 = given.team("team2", t -> t.course(course.alias()));
        // Teams in other course that should not be returned
        var anotherCourse = given.course("another-course");
        given.team("team3", t -> t.course(anotherCourse.alias()));
        persistGivenData(given);

        List<Team> actual = inTransaction(() -> coursesDb.getTeamsForCourse(course.id()));

        assertEquals(2, actual.size());
        assertEquals(Set.of(team1.id(), team2.id()), actual.stream().map(Team::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getCreatedAtTimestampsForTimeRange_excludesSoftDeletedCourses() {
        given.course("active-course");
        given.course("deleted-course", c -> c.softDeleted());
        persistGivenData(given);

        Instant start = Instant.now().minus(1, ChronoUnit.HOURS);
        Instant end = Instant.now().plus(1, ChronoUnit.HOURS);

        List<Instant> actual = inTransaction(
                () -> coursesDb.getCreatedAtTimestampsForTimeRange(start, end));

        assertEquals(1, actual.size());
    }

    private static Course buildDefaultCourse(String courseId) {
        return new Course(courseId, "Course Name", "UTC");
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
