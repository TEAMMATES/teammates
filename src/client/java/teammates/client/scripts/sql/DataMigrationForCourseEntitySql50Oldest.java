package teammates.client.scripts.sql;

import java.util.Optional;

import com.googlecode.objectify.cmd.Query;

/**
 * Migrates the 50 oldest unmigrated courses from Datastore to SQL.
 * Uses the same migration logic as {@link DataMigrationForCourseEntitySql} (SectionMigrator,
 * TeamMigrator, FeedbackChainMigrator, instructors, deadline extensions).
 */
public class DataMigrationForCourseEntitySql50Oldest extends DataMigrationForCourseEntitySql {

    private static final long MAX_COURSES = 50;

    public static void main(String[] args) {
        new DataMigrationForCourseEntitySql50Oldest().doOperationRemotely();
    }

    @Override
    protected Query<teammates.storage.entity.Course> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.Course.class)
                .filter("isMigrated", false)
                .order("createdAt");
    }

    @Override
    protected Optional<Long> getMaxCoursesToMigrate() {
        return Optional.of(MAX_COURSES);
    }
}
