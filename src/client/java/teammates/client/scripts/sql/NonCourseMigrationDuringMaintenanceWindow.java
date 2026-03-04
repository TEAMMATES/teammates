package teammates.client.scripts.sql;

/**
 * Step 5 in the non course migration process.
 */
@SuppressWarnings("PMD")
public class NonCourseMigrationDuringMaintenanceWindow {

    public static void main(String[] args) {
        try {
            DataMigrationForAccountAndReadNotificationSql.main(args);
            VerifyAccountAttributes.main(args);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
