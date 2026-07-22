package teammates.storage.api;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.MagicLink;

/**
 * Handles CRUD operations for magic links.
 *
 * @see MagicLink
 */
public final class MagicLinksDb {
    private static final MagicLinksDb instance = new MagicLinksDb();

    private MagicLinksDb() {
        // prevent initialization
    }

    public static MagicLinksDb inst() {
        return instance;
    }

    /**
     * Atomically creates or updates a MagicLink by email and returns the persisted row.
     */
    public MagicLink upsertMagicLink(MagicLink magicLink) {
        String sql = """
                INSERT INTO magic_links (id, created_at, email, token_hash, expires_at, updated_at)
                VALUES (:id, CURRENT_TIMESTAMP, :email, :tokenHash, :expiresAt, CURRENT_TIMESTAMP)
                ON CONFLICT (email)
                DO UPDATE SET token_hash = :tokenHash,
                              expires_at = :expiresAt,
                              updated_at = CURRENT_TIMESTAMP
                RETURNING *
                """;

        return HibernateUtil.createNativeQuery(sql, MagicLink.class)
                .setParameter("id", magicLink.getId())
                .setParameter("email", magicLink.getEmail())
                .setParameter("tokenHash", magicLink.getTokenHash())
                .setParameter("expiresAt", magicLink.getExpiresAt())
                .getSingleResult();
    }

    /**
     * Returns a MagicLink with the given token hash or null if it does not exist.
     */
    public MagicLink getMagicLinkByTokenHash(String tokenHash) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<MagicLink> cr = cb.createQuery(MagicLink.class);
        Root<MagicLink> root = cr.from(MagicLink.class);

        cr.select(root).where(cb.equal(root.get("tokenHash"), tokenHash));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Deletes a MagicLink.
     */
    public void deleteMagicLink(MagicLink magicLink) {
        HibernateUtil.remove(magicLink);
    }

}
