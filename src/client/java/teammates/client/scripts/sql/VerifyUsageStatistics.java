package teammates.client.scripts.sql;

import teammates.storage.sqlentity.UsageStatistics;

public class VerifyUsageStatistics extends VerifyNonCourseEntityAttributesBaseScript<
    teammates.storage.entity.UsageStatistics,
    UsageStatistics> {

    public VerifyUsageStatistics() {
        super(teammates.storage.entity.UsageStatistics.class,
            UsageStatistics.class);
    }

    public static void main(String[] args) {
        VerifyUsageStatistics script = new VerifyUsageStatistics();
        script.doOperationRemotely();
    }

    @Override
    protected String generateID(UsageStatistics sqlEntity) {
        return teammates.storage.entity.UsageStatistics.generateId(
            sqlEntity.getStartTime(), sqlEntity.getTimePeriod());
    }
}
