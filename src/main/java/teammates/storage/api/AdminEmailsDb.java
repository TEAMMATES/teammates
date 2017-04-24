package teammates.storage.api;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import com.google.appengine.api.blobstore.BlobKey;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.Logger;
import teammates.common.util.ThreadHelper;
import teammates.storage.entity.AdminEmail;

/**
 * Handles CRUD operations for emails sent by the admin.
 *
 * @see AdminEmail
 * @see AdminEmailAttributes
 */
public class AdminEmailsDb extends EntitiesDb {

    private static final Logger log = Logger.getLogger();

    public Date createAdminEmail(AdminEmailAttributes adminEmailToAdd) throws InvalidParametersException {
        try {
            AdminEmail ae = (AdminEmail) createEntity(adminEmailToAdd);
            return ae.getCreateDate();
        } catch (EntityAlreadyExistsException e) {
            try {
                updateAdminEmail(adminEmailToAdd);
                return adminEmailToAdd.getCreateDate();
            } catch (EntityDoesNotExistException ednee) {
                Assumption.fail("Entity found be already existing and not existing simultaneously");
                return null;
            }
        }

    }

    public void updateAdminEmail(AdminEmailAttributes ae) throws InvalidParametersException, EntityDoesNotExistException {
        if (!ae.isValid()) {
            throw new InvalidParametersException(ae.getInvalidityInfo());
        }

        AdminEmail adminEmailToUpdate = getAdminEmailEntity(ae.emailId);

        if (adminEmailToUpdate == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT_ACCOUNT + ae.getSubject()
                    + "/" + ae.getSendDate()
                    + ThreadHelper.getCurrentThreadStack());
        }

        ae.sanitizeForSaving();

        adminEmailToUpdate.setContent(ae.content);
        adminEmailToUpdate.setAddressReceiver(ae.addressReceiver);
        adminEmailToUpdate.setGroupReceiver(ae.groupReceiver);
        adminEmailToUpdate.setSubject(ae.subject);
        adminEmailToUpdate.setIsInTrashBin(ae.isInTrashBin);
        adminEmailToUpdate.setSendDate(ae.sendDate);

        log.info(ae.getBackupIdentifier());
        closePm();

    }

    /**
     * Deletes files uploaded in admin email compose page.
     * @param key the GCS blobkey used to fetch the file in Google Cloud Storage
     */
    public void deleteAdminEmailUploadedFile(BlobKey key) {
        GoogleCloudStorageHelper.deleteFile(key);
    }

    /**
     * Deletes all emails in trash bin, related group receiver text file will be removed from
     * Google Cloud Storage.
     */
    public void deleteAllEmailsInTrashBin() {

        List<AdminEmailAttributes> emailsInTrashBin = getAdminEmailsInTrashBin();

        for (AdminEmailAttributes a : emailsInTrashBin) {
            if (a.getGroupReceiver() != null) {
                for (String key : a.getGroupReceiver()) {
                    BlobKey blobKey = new BlobKey(key);
                    deleteAdminEmailUploadedFile(blobKey);
                }
            }
        }
        deleteEntities(emailsInTrashBin);
    }

    public void updateAdminEmailById(AdminEmailAttributes newAdminEmail, String emailId)
            throws InvalidParametersException, EntityDoesNotExistException {
        if (!newAdminEmail.isValid()) {
            throw new InvalidParametersException(newAdminEmail.getInvalidityInfo());
        }

        AdminEmail adminEmailToUpdate = getAdminEmailEntity(emailId);
        if (adminEmailToUpdate == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT_ACCOUNT + "with Id : " + emailId
                    + ThreadHelper.getCurrentThreadStack());
        }

        newAdminEmail.sanitizeForSaving();

        adminEmailToUpdate.setContent(newAdminEmail.content);
        adminEmailToUpdate.setAddressReceiver(newAdminEmail.addressReceiver);
        adminEmailToUpdate.setGroupReceiver(newAdminEmail.groupReceiver);
        adminEmailToUpdate.setSubject(newAdminEmail.subject);
        adminEmailToUpdate.setIsInTrashBin(newAdminEmail.isInTrashBin);
        adminEmailToUpdate.setSendDate(newAdminEmail.sendDate);

        log.info(newAdminEmail.getBackupIdentifier());
        closePm();

    }

    /**
     * This method is not scalable. Not to be used unless for admin features.
     * @return the list of all adminEmails in the database.
     */
    @Deprecated
    public List<AdminEmailAttributes> getAllAdminEmails() {
        List<AdminEmailAttributes> list = new LinkedList<AdminEmailAttributes>();
        List<AdminEmail> entities = getAdminEmailEntities();
        Iterator<AdminEmail> it = entities.iterator();
        while (it.hasNext()) {
            list.add(new AdminEmailAttributes(it.next()));
        }

        return list;
    }

    /**
     * Gets an admin email by email id.
     * @return null if no matched email found
     */
    public AdminEmailAttributes getAdminEmailById(String emailId) {

        AdminEmail matched = getAdminEmailEntity(emailId);

        if (matched == null) {
            return null;
        }

        return new AdminEmailAttributes(matched);
    }

    /**
     * Gets an admin email by subject and createDate.
     * @return null if no matched email found
     */
    public AdminEmailAttributes getAdminEmail(String subject, Date createDate) {

        AdminEmail matched = getAdminEmailEntity(subject, createDate);

        if (matched == null) {
            return null;
        }

        return new AdminEmailAttributes(matched);
    }

    /**
     * Gets an admin email based on subject.
     * @return null if no matched email found
     */
    public AdminEmailAttributes getAdminEmailBySubject(String subject) {
        AdminEmail matchedEmail = getAdminEmailEntityBySubject(subject);

        if (matchedEmail == null) {
            return null;
        }

        return new AdminEmailAttributes(matchedEmail);
    }

    /**
     * Gets all admin email drafts that have NOT been sent and NOT in trash bin.
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getAdminEmailDrafts() {
        List<AdminEmailAttributes> list = new LinkedList<AdminEmailAttributes>();

        Query q = getPm().newQuery(AdminEmail.class);
        q.declareParameters("boolean isInTrashBinParam");
        q.setFilter("isInTrashBin == isInTrashBinParam && sendDate == null");

        @SuppressWarnings("unchecked")
        List<AdminEmail> adminEmailList = (List<AdminEmail>) q.execute(false);

        if (adminEmailList.isEmpty() || JDOHelper.isDeleted(adminEmailList.get(0))) {
            return list;
        }

        Iterator<AdminEmail> it = adminEmailList.iterator();
        while (it.hasNext()) {
            AdminEmail adminEmail = it.next();

            if (!JDOHelper.isDeleted(adminEmail)) {
                list.add(new AdminEmailAttributes(adminEmail));
            }
        }

        return list;
    }

    /**
     * Gets all admin emails that have been sent and not in trash bin.
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getSentAdminEmails() {
        List<AdminEmailAttributes> list = new LinkedList<AdminEmailAttributes>();

        Query q = getPm().newQuery(AdminEmail.class);
        q.declareParameters("boolean isInTrashBinParam");
        q.setFilter("isInTrashBin == isInTrashBinParam && sendDate != null");

        @SuppressWarnings("unchecked")
        List<AdminEmail> adminEmailList = (List<AdminEmail>) q.execute(false);

        if (adminEmailList.isEmpty()) {
            return list;
        }

        Iterator<AdminEmail> it = adminEmailList.iterator();
        while (it.hasNext()) {
            AdminEmail adminEmail = it.next();

            if (!JDOHelper.isDeleted(adminEmail)) {
                list.add(new AdminEmailAttributes(adminEmail));
            }
        }

        return list;
    }

    /**
     * Gets all admin emails (including sent and draft mails) that have been moved into trash bin.
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getAdminEmailsInTrashBin() {
        List<AdminEmailAttributes> list = new LinkedList<AdminEmailAttributes>();

        Query q = getPm().newQuery(AdminEmail.class);
        q.declareParameters("boolean isInTrashBinParam");
        q.setFilter("isInTrashBin == isInTrashBinParam");

        @SuppressWarnings("unchecked")
        List<AdminEmail> adminEmailList = (List<AdminEmail>) q.execute(true);

        if (adminEmailList.isEmpty() || JDOHelper.isDeleted(adminEmailList.get(0))) {
            return list;
        }

        Iterator<AdminEmail> it = adminEmailList.iterator();
        while (it.hasNext()) {
            AdminEmail adminEmail = it.next();

            if (!JDOHelper.isDeleted(adminEmail)) {
                list.add(new AdminEmailAttributes(adminEmail));
            }
        }

        return list;
    }

    private List<AdminEmail> getAdminEmailEntities() {
        Query q = getPm().newQuery(AdminEmail.class);

        @SuppressWarnings("unchecked")
        List<AdminEmail> adminEmailList = (List<AdminEmail>) q.execute();

        return adminEmailList;
    }

    private AdminEmail getAdminEmailEntity(String adminEmailId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, adminEmailId);

        Query q = getPm().newQuery(AdminEmail.class);
        q.declareParameters("String adminEmailIdParam");
        q.setFilter("emailId == adminEmailIdParam");

        @SuppressWarnings("unchecked")
        List<AdminEmail> adminEmailList = (List<AdminEmail>) q.execute(adminEmailId);

        if (adminEmailList.isEmpty() || JDOHelper.isDeleted(adminEmailList.get(0))) {
            return null;
        }
        return adminEmailList.get(0);
    }

    private AdminEmail getAdminEmailEntity(String subject, Date createDate) {

        Query q = getPm().newQuery(AdminEmail.class);
        q.declareParameters("String subjectParam, java.util.Date createDateParam");
        q.setFilter("subject == subjectParam && " + "createDate == createDateParam");

        @SuppressWarnings("unchecked")
        List<AdminEmail> adminEmailList = (List<AdminEmail>) q.execute(subject, createDate);

        if (adminEmailList.isEmpty() || JDOHelper.isDeleted(adminEmailList.get(0))) {
            return null;
        }
        return adminEmailList.get(0);
    }

    private AdminEmail getAdminEmailEntityBySubject(String subject) {

        Query q = getPm().newQuery(AdminEmail.class);
        q.declareParameters("String subjectParam");
        q.setFilter("subject == subjectParam");
        q.setRange(0, 1);

        @SuppressWarnings("unchecked")
        List<AdminEmail> adminEmailList = (List<AdminEmail>) q.execute(subject);

        if (adminEmailList.isEmpty() || JDOHelper.isDeleted(adminEmailList.get(0))) {
            return null;
        }
        return adminEmailList.get(0);
    }

    @Override
    protected Object getEntity(EntityAttributes attributes) {
        AdminEmailAttributes adminEmailToGet = (AdminEmailAttributes) attributes;

        if (adminEmailToGet.getEmailId() != null) {
            return getAdminEmailEntity(adminEmailToGet.getEmailId());
        }

        return getAdminEmailEntity(adminEmailToGet.getSubject(),
                                   adminEmailToGet.getCreateDate());
    }

    @Override
    protected QueryWithParams getEntityKeyOnlyQuery(EntityAttributes attributes) {
        Class<?> entityClass = AdminEmail.class;
        String primaryKeyName = AdminEmail.PRIMARY_KEY_NAME;
        AdminEmailAttributes aea = (AdminEmailAttributes) attributes;
        String id = aea.emailId;

        Query q = getPm().newQuery(entityClass);
        Object[] params;

        if (id == null) {
            q.declareParameters("String subjectParam, java.util.Date createDateParam");
            q.setFilter("subject == subjectParam && " + "createDate == createDateParam");
            params = new Object[] {aea.subject, aea.createDate};
        } else {
            q.declareParameters("String idParam");
            q.setFilter(primaryKeyName + " == idParam");
            params = new Object[] {id};
        }

        return new QueryWithParams(q, params, primaryKeyName);
    }

}
