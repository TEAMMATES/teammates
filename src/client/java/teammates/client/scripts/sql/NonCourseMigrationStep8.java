package teammates.client.scripts.sql;

/**
 * Step 3 in the non course migration process.
 */
@SuppressWarnings("PMD")
public class NonCourseMigrationStep8 {

    public static void main(String[] args) {
        try {
            DataMigrationForUsageStatisticsSql.main(args);
            VerifyUsageStatisticsAttributes.main(args);
            VerifyNonCourseEntityCounts.main(args);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
