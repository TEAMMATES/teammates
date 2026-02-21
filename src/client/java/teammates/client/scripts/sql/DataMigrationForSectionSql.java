package teammates.client.scripts.sql;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.googlecode.objectify.cmd.Query;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.Section;

/**
 * Data migration class for section entity.
 *
 * <p>Exposes {@link #migrateSections} as a reusable static helper for migrating section entities.
 * Used by both this script and {@link DataMigrationForCourseEntitySql}.
 */
@SuppressWarnings("PMD")
public class DataMigrationForSectionSql extends
        DataMigrationEntitiesBaseScriptSql<teammates.storage.entity.Course, teammates.storage.sqlentity.Section> {

    public static void main(String[] args) {
        new DataMigrationForSectionSql().doOperationRemotely();
    }

    @Override
    protected Query<Course> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.Course.class);
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

    private Stream<String> getAllSectionNames(Course course) {
        return ofy()
                .load()
                .type(CourseStudent.class)
                .filter("courseId", course.getUniqueId())
                .list()
                .stream()
                .map(CourseStudent::getSectionName)
                .distinct();
    }

    @Override
    protected void migrateEntity(Course oldCourse) throws Exception {
        HibernateUtil.beginTransaction();
        try {
            teammates.storage.sqlentity.Course newCourse = HibernateUtil.getReference(
                    teammates.storage.sqlentity.Course.class, oldCourse.getUniqueId());
            migrateSections(newCourse, getAllSectionNames(oldCourse).collect(Collectors.toList()),
                    this::saveEntityDeferred);
            HibernateUtil.commitTransaction();
        } catch (Exception e) {
            HibernateUtil.rollbackTransaction();
            throw e;
        }
    }

    /**
     * Creates and saves Section entities for the given course and section names.
     * Used by both this script and {@link DataMigrationForCourseEntitySql}.
     *
     * @param newCourse the SQL course (must already exist)
     * @param sectionNames distinct section names (e.g. from CourseStudent data)
     * @param saveAction called for each created section (e.g. {@code HibernateUtil::persist})
     */
    public static void migrateSections(teammates.storage.sqlentity.Course newCourse,
            Iterable<String> sectionNames, Consumer<Section> saveAction) {
        for (String sectionName : sectionNames) {
            String normalizedName = normalizeSectionName(sectionName);
            String truncatedName = truncateSectionName(normalizedName);
            Section section = new Section(newCourse, truncatedName);
            section.setCreatedAt(Instant.now());
            saveAction.accept(section);
        }
    }

    /**
     * Normalizes null/empty section names to {@link Const#DEFAULT_SECTION}.
     * Used by {@link DataMigrationForCourseEntitySql} when gathering section names.
     */
    public static String normalizeSectionName(String name) {
        return name == null || name.isEmpty() ? Const.DEFAULT_SECTION : name;
    }

    private static String truncateSectionName(String str) {
        if (str == null) {
            return null;
        }
        int maxLength = 255;
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
