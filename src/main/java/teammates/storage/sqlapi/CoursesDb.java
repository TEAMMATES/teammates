package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Team;

/**
 * Handles CRUD operations for courses.
 *
 * @see Course
 */
public final class CoursesDb extends EntitiesDb {

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
        assert courseId != null;

        return HibernateUtil.get(Course.class, courseId);
    }

    /**
     * Creates a course.
     */
    public Course createCourse(Course course) throws InvalidParametersException, EntityAlreadyExistsException {
        assert course != null;

        if (!course.isValid()) {
            throw new InvalidParametersException(course.getInvalidityInfo());
        }

        if (getCourse(course.getId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, course.toString()));
        }

        persist(course);
        return course;
    }

    /**
     * Saves an updated {@code Course} to the db.
     */
    public Course updateCourse(Course course) throws InvalidParametersException, EntityDoesNotExistException {
        assert course != null;

        if (!course.isValid()) {
            throw new InvalidParametersException(course.getInvalidityInfo());
        }

        if (getCourse(course.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        return merge(course);
    }

    /**
     * Deletes a course.
     */
    public void deleteCourse(Course course) {
        if (course != null) {
            delete(course);
        }
    }

    /**
     * Creates a section.
     */
    public Section createSection(Section section) throws InvalidParametersException, EntityAlreadyExistsException {
        assert section != null;

        if (!section.isValid()) {
            throw new InvalidParametersException(section.getInvalidityInfo());
        }

        if (getSectionByName(section.getCourse().getId(), section.getName()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, section.toString()));
        }

        persist(section);
        return section;
    }

    /**
     * Get section by name.
     */
    public Section getSectionByName(String courseId, String sectionName) {
        assert courseId != null;
        assert sectionName != null;

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
        assert courseId != null;
        assert teamName != null;

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
     * Deletes all sections by {@code courseId}.
     */
    public void deleteSectionsByCourseId(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaDelete<Section> cd = cb.createCriteriaDelete(Section.class);
        Root<Section> sRoot = cd.from(Section.class);
        Subquery<UUID> subquery = cd.subquery(UUID.class);
        Root<Section> subqueryRoot = subquery.from(Section.class);
        Join<Section, Course> sqJoin = subqueryRoot.join("course");
        subquery.select(subqueryRoot.get("id"));
        subquery.where(cb.equal(sqJoin.get("id"), courseId));
        cd.where(cb.in(sRoot.get("id")).value(subquery));
        HibernateUtil.createMutationQuery(cd).executeUpdate();
    }

    /**
     * Get teams by {@code section}.
     */
    public List<Team> getTeamsForSection(Section section) {
        assert section != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Team> cr = cb.createQuery(Team.class);
        Root<Team> teamRoot = cr.from(Team.class);
        Join<Team, Section> teamJoin = teamRoot.join("section");

        cr.select(teamRoot).where(
                cb.equal(teamJoin.get("id"), section.getId()));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Get teams by {@code course}.
     */
    public List<Team> getTeamsForCourse(String courseId) {
        assert courseId != null;

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
     * Creates a team.
     */
    public Team createTeam(Team team) throws InvalidParametersException, EntityAlreadyExistsException {
        assert team != null;

        if (!team.isValid()) {
            throw new InvalidParametersException(team.getInvalidityInfo());
        }

        if (getTeamByName(team.getSection().getId(), team.getName()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, team.toString()));
        }

        persist(team);
        return team;
    }

    /**
     * Gets a team by name.
     */
    public Team getTeamByName(UUID sectionId, String teamName) {
        assert sectionId != null;
        assert teamName != null;

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
