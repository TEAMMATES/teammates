package teammates.storage.sqlapi;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
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
     * <p>Uses a native PostgreSQL {@code INSERT ... ON CONFLICT DO UPDATE} to
     * atomically upsert the record, avoiding TOCTOU race conditions that would
     * arise from a read-check-write or delete-then-insert approach.
     */
    public EmailTemplate upsertEmailTemplate(EmailTemplate emailTemplate) throws InvalidParametersException {
        assert emailTemplate != null;

        if (!emailTemplate.isValid()) {
            throw new InvalidParametersException(emailTemplate.getInvalidityInfo());
        }

        HibernateUtil.createNativeMutationQuery(
                "INSERT INTO email_templates (id, created_at, template_key, subject, body, updated_at) "
                        + "VALUES (:id, :createdAt, :templateKey, :subject, :body, NOW()) "
                        + "ON CONFLICT (template_key) DO UPDATE SET "
                        + "subject = EXCLUDED.subject, "
                        + "body = EXCLUDED.body, "
                        + "updated_at = EXCLUDED.updated_at")
                .setParameter("id", emailTemplate.getId())
                .setParameter("createdAt", emailTemplate.getCreatedAt())
                .setParameter("templateKey", emailTemplate.getTemplateKey())
                .setParameter("subject", emailTemplate.getSubject())
                .setParameter("body", emailTemplate.getBody())
                .executeUpdate();

        return getEmailTemplate(emailTemplate.getTemplateKey());
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
