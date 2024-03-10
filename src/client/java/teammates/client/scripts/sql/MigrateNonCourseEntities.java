package teammates.client.scripts.sql;

/**
 * Migrate non course entities (for first time use).
 */
@SuppressWarnings("PMD")
public class MigrateNonCourseEntities {

    public static void main(String[] args) {
        try {
            // SeedDb.main(args);

            DataMigrationForNotificationSql.main(args);
            // DataMigrationForUsageStatisticsSql.main(args);
            // DataMigrationForAccountRequestSql.main(args);

            // If the script is terminated during this call,
            // please run PatchDataMigrationForAccountAndReadNotificationSql
            // instead of rerunning this.
            DataMigrationForAccountAndReadNotificationSql.main(args);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
