package teammates.client.scripts.sql;

import teammates.storage.entity.UsageStatistics;

/**
 * Class for verifying usage statistics.
 */
@SuppressWarnings("PMD")
public class VerifyUsageStatisticsAttributes extends
        VerifyNonCourseEntityAttributesBaseScript<UsageStatistics, teammates.storage.sqlentity.UsageStatistics> {

    public VerifyUsageStatisticsAttributes() {
        super(UsageStatistics.class,
                teammates.storage.sqlentity.UsageStatistics.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.UsageStatistics sqlEntity) {
        return teammates.storage.entity.UsageStatistics.generateId(
                sqlEntity.getStartTime(), sqlEntity.getTimePeriod());
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.UsageStatistics sqlEntity, UsageStatistics datastoreEntity) {
        if (datastoreEntity instanceof teammates.storage.entity.UsageStatistics) {
            teammates.storage.entity.UsageStatistics stat = (teammates.storage.entity.UsageStatistics) datastoreEntity;
            // UUID for account is not checked, as datastore ID is startTime%timePeriod
            return sqlEntity.getStartTime().equals(stat.getStartTime())
                    && sqlEntity.getTimePeriod() == stat.getTimePeriod()
                    && sqlEntity.getNumResponses() == stat.getNumResponses()
                    && sqlEntity.getNumCourses() == stat.getNumCourses()
                    && sqlEntity.getNumStudents() == stat.getNumStudents()
                    && sqlEntity.getNumInstructors() == stat.getNumInstructors()
                    && sqlEntity.getNumAccountRequests() == stat.getNumAccountRequests()
                    && sqlEntity.getNumEmails() == stat.getNumEmails()
                    && sqlEntity.getNumSubmissions() == stat.getNumSubmissions();
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        VerifyUsageStatisticsAttributes script = new VerifyUsageStatisticsAttributes();
        script.doOperationRemotely();
    }
}
