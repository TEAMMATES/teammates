package teammates.client.scripts.sql;

/**
 * Migrate non course entities.
 */
@SuppressWarnings("PMD")
public class MigrateNonCourseEntities {

    public static void main(String[] args) {
        try {
            DataMigrationForNotificationSql.main(args);
            DataMigrationForUsageStatisticsSql.main(args);
            DataMigrationForAccountRequestSql.main(args);
            DataMigrationForAccountAndReadNotificationSql.main(args);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
