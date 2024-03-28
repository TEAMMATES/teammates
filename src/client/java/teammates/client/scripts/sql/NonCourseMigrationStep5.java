package teammates.client.scripts.sql;

/**
 * Step 3 in the non course migration process.
 */
@SuppressWarnings("PMD")
public class NonCourseMigrationStep5 {

    public static void main(String[] args) {
        try {
            DataMigrationForAccountAndReadNotificationSql.main(args);
            VerifyAccountAttributes.main(args);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
