package teammates.client.scripts.sql;

import java.util.Optional;

import com.googlecode.objectify.cmd.Query;

/**
 * Migrates a single course (by ID) from Datastore to SQL.
 * Uses the same migration logic as {@link DataMigrationForCourseEntitySql}.
 *
 * <p>Usage: Pass the course ID as the first command-line argument.
 * Example: {@code java ... DataMigrationForSingleCourseSql cs101}
 */
public class DataMigrationForSingleCourseSql extends DataMigrationForCourseEntitySql {

    private final String courseId;

    public DataMigrationForSingleCourseSql(String courseId) {
        super();
        this.courseId = courseId;
    }

    public static void main(String[] args) {
        if (args.length < 1 || args[0].isBlank()) {
            System.err.println("Usage: DataMigrationForSingleCourseSql <courseId>");
            System.err.println("Example: DataMigrationForSingleCourseSql cs101");
            return;
        }
        new DataMigrationForSingleCourseSql(args[0].trim()).doOperationRemotely();
    }

    @Override
    protected Query<teammates.storage.entity.Course> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.Course.class)
                .filter("id", courseId);
    }

    @Override
    protected Optional<Long> getMaxCoursesToMigrate() {
        return Optional.of(1L);
    }
}
