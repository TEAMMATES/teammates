package teammates.client.scripts.sql;

import com.googlecode.objectify.cmd.Query;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;

/**
 * Data migration class for course entity.
 */
@SuppressWarnings("PMD")
public class DataMigrationForCourseSql extends
        DataMigrationEntitiesBaseScriptSql<teammates.storage.entity.Course, teammates.storage.sqlentity.Course> {

    public static void main(String[] args) {
        new DataMigrationForCourseSql().doOperationRemotely();
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
        HibernateUtil.beginTransaction();
        teammates.storage.sqlentity.Course course = HibernateUtil.get(
                teammates.storage.sqlentity.Course.class, entity.getUniqueId());
        HibernateUtil.commitTransaction();
        return course == null;
    }

    @Override
    protected void migrateEntity(Course oldCourse) throws Exception {
        teammates.storage.sqlentity.Course newCourse = new teammates.storage.sqlentity.Course(
                oldCourse.getUniqueId(),
                oldCourse.getName(),
                oldCourse.getTimeZone(),
                oldCourse.getInstitute());

        saveEntityDeferred(newCourse);
    }
}
