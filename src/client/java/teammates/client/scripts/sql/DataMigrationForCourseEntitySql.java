package teammates.client.scripts.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.Student;

/**
 * Data migration class for course entity.
 */
@SuppressWarnings("PMD")
public class DataMigrationForCourseEntitySql extends
        DataMigrationEntitiesBaseScriptSql<teammates.storage.entity.Course, teammates.storage.sqlentity.BaseEntity> {

    public static void main(String[] args) {
        new DataMigrationForCourseEntitySql().doOperationRemotely();
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

    @Override
    protected void migrateEntity(Course oldCourse) throws Exception {
        teammates.storage.sqlentity.Course newCourse = createCourse(oldCourse);
        // TODO: add shutdown hook to save the entity
        // Runnable shutdownScript = () -> { cascadeDelete(newCourse)};
        // Runtime.getRuntime().addShutdownHook(new Thread(shutdownScript));

        migrateCourseEntity(newCourse);
        // verifyCourseEntity(newCourse);
        // markOldCourseAsMigrated(courseId)
        // Runtime.getRuntime().removeShutDownHook(new Thread(shutdownScript));
    }

    private void migrateCourseEntity(teammates.storage.sqlentity.Course newCourse) {
        // Map<String, teammates.storage.sqlentity.Section> sectionNameToSectionMap =
        // migrateSectionChain(newCourse);
        // System.out.println(sectionNameToSectionMap); // To stop lint from complaining
        // TODO: Add mirgrateFeedbackChain
        // migrateFeedbackChain(sectionNameToSectionMap);
    }


    private teammates.storage.sqlentity.Course createCourse(Course oldCourse) {
        teammates.storage.sqlentity.Course newCourse = new teammates.storage.sqlentity.Course(
                oldCourse.getUniqueId(),
                oldCourse.getName(),
                oldCourse.getTimeZone(),
                oldCourse.getInstitute());
        newCourse.setDeletedAt(oldCourse.getDeletedAt());
        // newCourse.setCreatedAt(oldCourse.getCreatedAt());
        saveEntityDeferred(newCourse);
        return newCourse;
    }
}
