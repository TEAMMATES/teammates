package teammates.client.scripts.sql;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.googlecode.objectify.cmd.Query;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;

/**
 * Data migration class for section entity.
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
        teammates.storage.sqlentity.Course newCourse = HibernateUtil.getReference(
                teammates.storage.sqlentity.Course.class, oldCourse.getUniqueId());
        SectionMigrator.migrate(newCourse, getAllSectionNames(oldCourse).collect(Collectors.toList()),
                this::saveEntityDeferred);
    }
}
