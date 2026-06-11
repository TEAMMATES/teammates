package teammates.storage.api;

import java.util.UUID;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Institute;

/**
 * Generates CRUD operations for Institute.
 *
 * @see Institute
 */
public final class InstitutesDb {
    private static final InstitutesDb instance = new InstitutesDb();

    private InstitutesDb() {
        // prevent instantiation
    }

    public static InstitutesDb inst() {
        return instance;
    }

    /**
     * Persists an Institute in the database.
     */
    public Institute persistInstitute(Institute institute) {
        HibernateUtil.persist(institute);
        return institute;
    }

    /**
     * Gets an Institute by {@code id} from the database.
     */
    public Institute getInstitute(UUID id) {
        return HibernateUtil.get(Institute.class, id);
    }

    /**
     * Gets the Institute with the given {@code name} and {@code country}, or null if none exists.
     */
    public Institute getInstituteByNameAndCountry(String name, String country) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Institute> cr = cb.createQuery(Institute.class);
        Root<Institute> root = cr.from(Institute.class);
        cr.select(root).where(cb.and(
                cb.equal(root.get("name"), name),
                cb.equal(root.get("country"), country)));

        TypedQuery<Institute> query = HibernateUtil.createQuery(cr);
        return query.getResultStream().findFirst().orElse(null);
    }

    /**
     * Removes an Institute.
     */
    public void removeInstitute(Institute institute) {
        if (institute != null) {
            HibernateUtil.remove(institute);
        }
    }
}
