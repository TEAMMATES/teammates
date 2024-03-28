package teammates.client.scripts.sql;

// CHECKSTYLE.OFF:ImportOrder
import java.time.Instant;
import java.util.UUID;

import com.googlecode.objectify.cmd.Query;

import teammates.common.util.HibernateUtil;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import teammates.storage.sqlentity.UsageStatistics;
// CHECKSTYLE.ON:ImportOrder

/**
 * Data migration class for usage statistics.
 */
@SuppressWarnings("PMD")
public class DataMigrationForUsageStatisticsSql extends
        DataMigrationEntitiesBaseScriptSql<teammates.storage.entity.UsageStatistics, UsageStatistics> {

    // Runs the migration only for newly-created SQL entities since the initial migration.
    private static final boolean IS_PATCHING_MIGRATION = true;

    private Instant patchingStartTime;

    public static void main(String[] args) {
        new DataMigrationForUsageStatisticsSql().doOperationRemotely();
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
     * Queries for the latest SQL entity created, so that patching will only migrate newly created Datastore entities.
     */
    @Override
    protected void setMigrationCriteria() {
        if (!IS_PATCHING_MIGRATION) {
            return;
        }

        HibernateUtil.beginTransaction();
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instant> cq = cb.createQuery(Instant.class);
        Root<UsageStatistics> root = cq.from(UsageStatistics.class);
        cq.select(cb.greatest(root.<Instant>get("startTime")));

        // If no entity found, Hibernate will return null for Instant instead of throwing NoResultException.
        patchingStartTime = HibernateUtil.createQuery(cq).getSingleResult();
        HibernateUtil.commitTransaction();

        if (patchingStartTime == null) {
            System.out.println(this.getClass().getSimpleName() + " Patching enabled, but unable to find SQL entity");
            System.exit(1);
        }

        System.out.println(this.getClass().getSimpleName() + " Patching migration, with time " + patchingStartTime);
    }

    /**
     * Always returns true, as the migration is needed for all entities from
     * Datastore to CloudSQL.
     */
    @SuppressWarnings("unused")
    @Override
    protected boolean isMigrationNeeded(teammates.storage.entity.UsageStatistics entity) {
        if (patchingStartTime == null) {
            return true;
        }
        return entity.getStartTime().isAfter(patchingStartTime);
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
                oldEntity.getNumSubmissions()
        );

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
