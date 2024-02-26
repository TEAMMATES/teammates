package teammates.client.scripts.sql;

import teammates.storage.entity.UsageStatistics;

public class VerifyUsageStatisticsAttributes extends VerifyNonCourseEntityAttributesBaseScript<UsageStatistics, 
    teammates.storage.sqlentity.UsageStatistics> {
    
    public VerifyUsageStatisticsAttributes() {
        super(UsageStatistics.class, 
            teammates.storage.sqlentity.UsageStatistics.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.UsageStatistics sqlEntity) {
        return sqlEntity.getId().toString();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.UsageStatistics sqlEntity, UsageStatistics datastoreEntity) {
        // UUID for account is not checked, as datastore ID is startTime%timePeriod
        return sqlEntity.getStartTime() == datastoreEntity.getStartTime()
            && sqlEntity.getTimePeriod() == datastoreEntity.getTimePeriod()
            && sqlEntity.getNumResponses() == datastoreEntity.getNumResponses()
            && sqlEntity.getNumCourses() == datastoreEntity.getNumCourses()
            && sqlEntity.getNumStudents() == datastoreEntity.getNumStudents()
            && sqlEntity.getNumInstructors() == datastoreEntity.getNumInstructors()
            && sqlEntity.getNumAccountRequests() == datastoreEntity.getNumAccountRequests()
            && sqlEntity.getNumEmails() == datastoreEntity.getNumEmails()
            && sqlEntity.getNumSubmissions() == datastoreEntity.getNumSubmissions();
    }    

    public static void main(String[] args) {
        VerifyUsageStatisticsAttributes script = new VerifyUsageStatisticsAttributes();
        script.doOperationRemotely();
    }
}
