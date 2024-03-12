package teammates.client.scripts.sql;

/**
 * Verify non course entities.
 */
@SuppressWarnings("PMD")
public class VerifyNonCourseEntities {

    public static void main(String[] args) {
        try {
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
