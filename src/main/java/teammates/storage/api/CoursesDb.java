package teammates.storage.api;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.Section;
import teammates.storage.entity.Team;

/**
 * Handles CRUD operations for courses.
 *
 * @see Course
 */
public final class CoursesDb {

    private static final CoursesDb instance = new CoursesDb();

    private CoursesDb() {
        // prevent initialization
    }

    public static CoursesDb inst() {
        return instance;
    }

    /**
     * Returns a course with the {@code courseID} or null if it does not exist.
     */
    public Course getCourse(String courseId) {
        return HibernateUtil.get(Course.class, courseId);
    }

    /**
     * Persists a course.
     */
    public Course persistCourse(Course course) {
        HibernateUtil.persist(course);
        return course;
    }

    /**
     * Removes a course.
     */
    public void removeCourse(Course course) {
        HibernateUtil.remove(course);
    }

    /**
     * Persists a section.
     */
    public Section persistSection(Section section) {
        HibernateUtil.persist(section);
        return section;
    }

    /**
     * Get section by name.
     */
    public Section getSectionByName(String courseId, String sectionName) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Section> cr = cb.createQuery(Section.class);
        Root<Section> sectionRoot = cr.from(Section.class);
        Join<Section, Course> courseJoin = sectionRoot.join("course");

        cr.select(sectionRoot).where(cb.and(
                cb.equal(courseJoin.get("id"), courseId),
                cb.equal(sectionRoot.get("name"), sectionName)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Get section by {@code courseId} and {@code teamName}.
     */
    public Section getSectionByCourseIdAndTeam(String courseId, String teamName) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Section> cr = cb.createQuery(Section.class);
        Root<Section> sectionRoot = cr.from(Section.class);
        Join<Section, Course> courseJoin = sectionRoot.join("course");
        Join<Section, Team> teamJoin = sectionRoot.join("teams");

        cr.select(sectionRoot).where(cb.and(
                cb.equal(courseJoin.get("id"), courseId),
                cb.equal(teamJoin.get("name"), teamName)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Get teams by {@code course}.
     */
    public List<Team> getTeamsForCourse(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Team> cr = cb.createQuery(Team.class);
        Root<Team> teamRoot = cr.from(Team.class);
        Join<Team, Section> sectionJoin = teamRoot.join("section");
        Join<Section, Course> courseJoin = sectionJoin.join("course");

        cr.select(teamRoot).where(
                cb.equal(courseJoin.get("id"), courseId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Persists a team.
     */
    public Team persistTeam(Team team) {
        HibernateUtil.persist(team);
        return team;
    }

    /**
     * Gets a team by name.
     */
    public Team getTeamByName(UUID sectionId, String teamName) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Team> cr = cb.createQuery(Team.class);
        Root<Team> teamRoot = cr.from(Team.class);
        Join<Team, Section> sectionJoin = teamRoot.join("section");

        cr.select(teamRoot).where(cb.and(
                cb.equal(sectionJoin.get("id"), sectionId),
                cb.equal(teamRoot.get("name"), teamName)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

}
