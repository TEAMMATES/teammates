package teammates.client.scripts.sql;

/**
 * Step 3 in the non course migration process.
 */
@SuppressWarnings("PMD")
public class NonCourseMigrationStep3 {

    public static void main(String[] args) {
        try {
            DataMigrationForUsageStatisticsSql.main(args);
            DataMigrationForAccountRequestSql.main(args);
            VerifyAccountRequestAttributes.main(args);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
