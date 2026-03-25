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
     * <p>Any existing record with the same {@code templateKey} is deleted before
     * the new template is inserted, keeping both operations within the same
     * transaction and avoiding a unique-constraint race on concurrent saves.
     */
    public EmailTemplate upsertEmailTemplate(EmailTemplate emailTemplate) throws InvalidParametersException {
        assert emailTemplate != null;

        if (!emailTemplate.isValid()) {
            throw new InvalidParametersException(emailTemplate.getInvalidityInfo());
        }

        deleteEmailTemplate(emailTemplate.getTemplateKey());
        HibernateUtil.persist(emailTemplate);
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
