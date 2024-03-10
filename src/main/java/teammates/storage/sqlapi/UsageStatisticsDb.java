package teammates.storage.sqlapi;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.UsageStatistics;

/**
 * Handles CRUD operations for usage statistics.
 *
 * @see UsageStatistics
 */
public final class UsageStatisticsDb extends EntitiesDb {

    private static final UsageStatisticsDb instance = new UsageStatisticsDb();

    private UsageStatisticsDb() {
        // prevent initialization
    }

    public static UsageStatisticsDb inst() {
        return instance;
    }

    /**
     * Gets a list of statistics objects between start time and end time.
     */
    public List<UsageStatistics> getUsageStatisticsForTimeRange(Instant startTime, Instant endTime) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<UsageStatistics> cr = cb.createQuery(UsageStatistics.class);
        Root<UsageStatistics> root = cr.from(UsageStatistics.class);

        cr.select(root).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("startTime"), startTime),
                cb.lessThan(root.get("startTime"), endTime)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Creates a usage statistics object.
     */
    public UsageStatistics createUsageStatistics(UsageStatistics usageStatistics) {
        assert usageStatistics != null;

        persist(usageStatistics);

        return usageStatistics;
    }

}
