package teammates.client.scripts.sql;

/**
 * Migrate and verify non course entities.
 */
@SuppressWarnings("PMD")
public class MigrateAndVerifyNonCourseEntities {

    public static void main(String[] args) {
        try {
            // SeedDb.main(args);

            DataMigrationForNotificationSql.main(args);
            // DataMigrationForUsageStatisticsSql.main(args);
            // DataMigrationForAccountRequestSql.main(args);
            DataMigrationForAccountAndReadNotificationSql.main(args);

            VerifyNonCourseEntityCounts.main(args);

            VerifyAccountRequestAttributes.main(args);
            VerifyUsageStatisticsAttributes.main(args);
            VerifyAccountAttributes.main(args);
            VerifyNotificationAttributes.main(args);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
