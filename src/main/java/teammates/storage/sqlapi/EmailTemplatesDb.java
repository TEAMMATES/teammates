package teammates.storage.sqlapi;

import java.time.Instant;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.EmailTemplate;

/**
 * Generates CRUD operations for EmailTemplate.
 *
 * @see EmailTemplate
 */
public final class EmailTemplatesDb {
    private static final EmailTemplatesDb instance = new EmailTemplatesDb();

    private EmailTemplatesDb() {
        // prevent instantiation
    }

    public static EmailTemplatesDb inst() {
        return instance;
    }

    /**
     * Gets an EmailTemplate by its {@code templateKey} from the database,
     * or {@code null} if no template with that key exists.
     */
    public EmailTemplate getEmailTemplate(String templateKey) {
        assert templateKey != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<EmailTemplate> cr = cb.createQuery(EmailTemplate.class);
        Root<EmailTemplate> root = cr.from(EmailTemplate.class);
        cr.select(root).where(cb.equal(root.get("templateKey"), templateKey));

        TypedQuery<EmailTemplate> query = HibernateUtil.createQuery(cr);
        return query.getResultStream().findFirst().orElse(null);
    }

    /**
     * Creates or updates an EmailTemplate in the database.
     *
     * <p>Attempts a bulk {@code CriteriaUpdate} first. If no row is matched
     * (i.e. the template does not yet exist), falls back to a {@code persist}.
     */
    public EmailTemplate upsertEmailTemplate(EmailTemplate emailTemplate) throws InvalidParametersException {
        assert emailTemplate != null;

        if (!emailTemplate.isValid()) {
            throw new InvalidParametersException(emailTemplate.getInvalidityInfo());
        }

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaUpdate<EmailTemplate> update = cb.createCriteriaUpdate(EmailTemplate.class);
        Root<EmailTemplate> root = update.from(EmailTemplate.class);
        Instant now = Instant.now();
        update.set(root.get("subject"), emailTemplate.getSubject());
        update.set(root.get("body"), emailTemplate.getBody());
        update.set(root.get("updatedAt"), now);
        update.where(cb.equal(root.get("templateKey"), emailTemplate.getTemplateKey()));

        int rowsUpdated = HibernateUtil.createMutationQuery(update).executeUpdate();
        if (rowsUpdated == 0) {
            HibernateUtil.persist(emailTemplate);
            emailTemplate.setUpdatedAt(now);
        } else {
            emailTemplate.setUpdatedAt(now);
        }

        return emailTemplate;
    }

    /**
     * Deletes the EmailTemplate with the given {@code templateKey}, reverting to
     * the static file fallback. Does nothing if no such template exists.
     */
    public void deleteEmailTemplate(String templateKey) {
        assert templateKey != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaDelete<EmailTemplate> cd = cb.createCriteriaDelete(EmailTemplate.class);
        Root<EmailTemplate> root = cd.from(EmailTemplate.class);
        cd.where(cb.equal(root.get("templateKey"), templateKey));
        HibernateUtil.createMutationQuery(cd).executeUpdate();
    }

    /**
     * Deletes an EmailTemplate, reverting to the static file fallback.
     * Does nothing if {@code emailTemplate} is {@code null}.
     */
    public void deleteEmailTemplate(EmailTemplate emailTemplate) {
        if (emailTemplate != null) {
            HibernateUtil.remove(emailTemplate);
        }
    }
}
