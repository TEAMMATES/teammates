package teammates.client.scripts.sql;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
 *
 * <p>Exposes {@link #migrateTeams} as a reusable static helper for migrating team entities.
 * Used by both this script and {@link DataMigrationForCourseEntitySql}.
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
        migrateTeams(newCourse, sectionNameToTeamNames, HibernateUtil::persist);
        HibernateUtil.commitTransaction();
    }

    /**
     * Creates and saves Team entities for the given course. Course must have sections loaded.
     * Used by both this script and {@link DataMigrationForCourseEntitySql}.
     *
     * @param newCourse the SQL course with sections loaded
     * @param sectionNameToTeamNames map from section name to set of team names in that section
     * @param saveAction called for each created team (e.g. {@code HibernateUtil::persist})
     */
    public static void migrateTeams(teammates.storage.sqlentity.Course newCourse,
            Map<String, Set<String>> sectionNameToTeamNames,
            Consumer<teammates.storage.sqlentity.Team> saveAction) {
        Map<String, teammates.storage.sqlentity.Section> sectionByName = newCourse.getSections().stream()
                .collect(Collectors.toMap(teammates.storage.sqlentity.Section::getName, s -> s));
        for (Map.Entry<String, Set<String>> entry : sectionNameToTeamNames.entrySet()) {
            teammates.storage.sqlentity.Section section = sectionByName.get(entry.getKey());
            if (section == null) {
                continue;
            }
            for (String teamName : entry.getValue()) {
                String normalizedName = normalizeTeamName(teamName);
                String truncatedName = truncateTeamName(normalizedName);
                teammates.storage.sqlentity.Team team = new teammates.storage.sqlentity.Team(section, truncatedName);
                team.setCreatedAt(Instant.now());
                saveAction.accept(team);
            }
        }
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
                        .computeIfAbsent(DataMigrationForSectionSql.normalizeSectionName(cs.getSectionName()),
                                k -> new HashSet<>())
                        .add(normalizeTeamName(cs.getTeamName())));
        return sectionNameToTeamNames;
    }

    /**
     * Normalizes null/empty team names to {@link Const#DEFAULT_TEAM}.
     * Used by both this script and {@link DataMigrationForCourseEntitySql} when gathering team names.
     */
    public static String normalizeTeamName(String name) {
        return name == null || name.isEmpty() ? Const.DEFAULT_TEAM : name;
    }

    private static String truncateTeamName(String str) {
        if (str == null) {
            return null;
        }
        int maxLength = 255;
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
