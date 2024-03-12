package teammates.client.scripts.sql;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.sqlentity.UsageStatistics;

/**
 * Data migration class for usage statistics.
 */
public class DataMigrationForUsageStatisticsSql extends
        DataMigrationEntitiesBaseScriptSql<teammates.storage.entity.UsageStatistics, UsageStatistics> {

    // Set the default start time to resume the migration from.
    // Set it to null if want to migrate all entities.
    private static final String START_TIME_STRING = "2024-03-12 06:00:00.000 +0800";

    private static final Instant START_TIME = parseStartTime(START_TIME_STRING);

    public static void main(String[] args) {
        new DataMigrationForUsageStatisticsSql().doOperationRemotely();
    }

    private static Instant parseStartTime(String startTimeString) {
        if (startTimeString == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");
        return Instant.from(formatter.parse(startTimeString));
    }

    @Override
    protected Query<teammates.storage.entity.UsageStatistics> getFilterQuery() {
        // returns all UsageStatistics entities
        return ofy().load().type(teammates.storage.entity.UsageStatistics.class);
    }

    /**
     * Set to true to preview the migration without actually performing it.
     */
    @Override
    protected boolean isPreview() {
        return false;
    }

    /**
     * Always returns true, as the migration is needed for all entities from
     * Datastore to CloudSQL .
     */
    @SuppressWarnings("unused")
    @Override
    protected boolean isMigrationNeeded(teammates.storage.entity.UsageStatistics entity) {
        if (START_TIME == null) {
            return true;
        }
        return entity.getStartTime().isAfter(START_TIME);
    }

    @Override
    protected void migrateEntity(teammates.storage.entity.UsageStatistics oldEntity) throws Exception {
        UsageStatistics newEntity = new UsageStatistics(
                oldEntity.getStartTime(),
                oldEntity.getTimePeriod(),
                oldEntity.getNumResponses(),
                oldEntity.getNumCourses(),
                oldEntity.getNumStudents(),
                oldEntity.getNumInstructors(),
                oldEntity.getNumAccountRequests(),
                oldEntity.getNumEmails(),
                oldEntity.getNumSubmissions());

        try {
            UUID oldUuid = UUID.fromString(oldEntity.getId());
            newEntity.setId(oldUuid);
        } catch (IllegalArgumentException iae) {
            // Auto-generated UUID from entity is created in newEntity constructor.
            // Do nothing.
        }
        saveEntityDeferred(newEntity);
    }
}
