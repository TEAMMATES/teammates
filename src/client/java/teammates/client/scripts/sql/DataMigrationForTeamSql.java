package teammates.client.scripts.sql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import com.googlecode.objectify.cmd.Query;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;

/**
 * Data migration class for team entity.
 */
public class DataMigrationForTeamSql extends
        DataMigrationEntitiesBaseScriptSql<Course, teammates.storage.sqlentity.Team> {

    public static void main(String[] args) {
        new DataMigrationForTeamSql().doOperationRemotely();
    }

    @Override
    protected Query<Course> getFilterQuery() {
        return ofy().load().type(Course.class);
    }

    @Override
    protected boolean isPreview() {
        return false;
    }

    /*
     * Sets the migration criteria used in isMigrationNeeded.
     */
    @Override
    protected void setMigrationCriteria() {
        // No migration criteria currently needed.
    }

    @Override
    protected boolean isMigrationNeeded(Course entity) {
        return true;
    }

    @Override
    protected void migrateEntity(Course oldCourse) throws Exception {
        HibernateUtil.beginTransaction();
        teammates.storage.sqlentity.Course newCourse = getNewCourse(oldCourse.getUniqueId());
        Map<String, Set<String>> sectionNameToTeamNames = getSectionNameToTeamNames(oldCourse);
        TeamMigrator.migrate(newCourse, sectionNameToTeamNames, HibernateUtil::persist);
        HibernateUtil.commitTransaction();
    }

    private teammates.storage.sqlentity.Course getNewCourse(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.Course> cr = cb.createQuery(teammates.storage.sqlentity.Course.class);
        Root<teammates.storage.sqlentity.Course> courseRoot = cr.from(teammates.storage.sqlentity.Course.class);
        courseRoot.fetch("sections", JoinType.LEFT); // Fetch sections to avoid lazy-loading

        cr.select(courseRoot).where(cb.equal(courseRoot.get("id"), courseId));

        return HibernateUtil.createQuery(cr).getSingleResult();
    }

    private Map<String, Set<String>> getSectionNameToTeamNames(Course course) {
        Map<String, Set<String>> sectionNameToTeamNames = new HashMap<>();
        ofy()
                .load()
                .type(CourseStudent.class)
                .filter("courseId", course.getUniqueId())
                .list()
                .stream()
                .forEach(cs -> sectionNameToTeamNames
                        .computeIfAbsent(normalizeSectionName(cs.getSectionName()), k -> new HashSet<>())
                        .add(normalizeTeamName(cs.getTeamName())));
        return sectionNameToTeamNames;
    }

    /**
     * Normalizes null/empty section names to {@link Const#DEFAULT_SECTION}.
     */
    private static String normalizeSectionName(String name) {
        return name == null || name.isEmpty() ? Const.DEFAULT_SECTION : name;
    }

    /**
     * Normalizes null/empty team names to {@link Const#DEFAULT_TEAM}.
     */
    private static String normalizeTeamName(String name) {
        return name == null || name.isEmpty() ? Const.DEFAULT_TEAM : name;
    }
}
