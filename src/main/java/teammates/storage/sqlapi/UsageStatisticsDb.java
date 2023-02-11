package teammates.storage.sqlapi;

import java.time.Instant;
import java.util.List;

import org.hibernate.Session;

import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.UsageStatistics;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Handles CRUD operations for usage statistics.
 *
 * @see UsageStatistics
 */
public final class UsageStatisticsDb extends EntitiesDb<UsageStatistics> {

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
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<UsageStatistics> cr = cb.createQuery(UsageStatistics.class);
        Root<UsageStatistics> root = cr.from(UsageStatistics.class);

        cr.select(root).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("startTime"), startTime),
                cb.lessThan(root.get("startTime"), endTime)));

        return session.createQuery(cr).getResultList();
    }
}
