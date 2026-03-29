package teammates.storage.sqlapi;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
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
     * <p>If a template with the same {@code templateKey} already exists, its
     * subject and body are updated. Otherwise a new template is persisted.
     */
    public EmailTemplate upsertEmailTemplate(EmailTemplate emailTemplate) throws InvalidParametersException {
        assert emailTemplate != null;

        if (!emailTemplate.isValid()) {
            throw new InvalidParametersException(emailTemplate.getInvalidityInfo());
        }

        EmailTemplate existingTemplate = getEmailTemplate(emailTemplate.getTemplateKey());
        if (existingTemplate == null) {
            HibernateUtil.persist(emailTemplate);
            return emailTemplate;
        }

        existingTemplate.setSubject(emailTemplate.getSubject());
        existingTemplate.setBody(emailTemplate.getBody());
        HibernateUtil.merge(existingTemplate);
        return existingTemplate;
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
