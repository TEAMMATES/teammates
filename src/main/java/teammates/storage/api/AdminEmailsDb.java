package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.ThreadHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.AdminEmail;

/**
 * Handles CRUD operations for emails sent by the admin.
 *
 * @see AdminEmail
 * @see AdminEmailAttributes
 */
public class AdminEmailsDb extends EntitiesDb<AdminEmail, AdminEmailAttributes> {

    public Instant createAdminEmail(AdminEmailAttributes adminEmailToAdd) throws InvalidParametersException {
        try {
            AdminEmail ae = createEntity(adminEmailToAdd);
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

        saveEntity(adminEmailToUpdate, ae);
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

        saveEntity(adminEmailToUpdate, newAdminEmail);
    }

    /**
     * This method is not scalable. Not to be used unless for admin features.
     * @return the list of all adminEmails in the database.
     */
    @Deprecated
    public List<AdminEmailAttributes> getAllAdminEmails() {
        return makeAttributes(getAdminEmailEntities());
    }

    /**
     * Gets an admin email by email id.
     * @return null if no matched email found
     */
    public AdminEmailAttributes getAdminEmailById(String emailId) {
        return makeAttributesOrNull(getAdminEmailEntity(emailId));
    }

    /**
     * Gets an admin email by subject and createDate.
     * @return null if no matched email found
     */
    public AdminEmailAttributes getAdminEmail(String subject, Instant createDate) {
        return makeAttributesOrNull(getAdminEmailEntity(subject, createDate));
    }

    /**
     * Gets an admin email based on subject.
     * @return null if no matched email found
     */
    public AdminEmailAttributes getAdminEmailBySubject(String subject) {
        return makeAttributesOrNull(getAdminEmailEntityBySubject(subject));
    }

    /**
     * Gets all admin email drafts that have NOT been sent and NOT in trash bin.
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getAdminEmailDrafts() {
        return makeAttributes(
                load()
                .filter("isInTrashBin =", false)
                .filter("sendDate =", null)
                .list());
    }

    /**
     * Gets all admin emails that have been sent and not in trash bin.
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getSentAdminEmails() {
        return makeAttributes(
                load()
                .filter("isInTrashBin =", false)
                .filter("sendDate !=", null)
                .list());
    }

    /**
     * Gets all admin emails (including sent and draft mails) that have been moved into trash bin.
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getAdminEmailsInTrashBin() {
        return makeAttributes(
                load().filter("isInTrashBin =", true).list());
    }

    private List<AdminEmail> getAdminEmailEntities() {
        return load().list();
    }

    private AdminEmail getAdminEmailEntity(String adminEmailId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, adminEmailId);

        Key<AdminEmail> key = makeKeyOrNullFromWebSafeString(adminEmailId);
        if (key == null) {
            return null;
        }

        return ofy().load().key(key).now();
    }

    private AdminEmail getAdminEmailEntity(String subject, Instant createDate) {
        return load()
                .filter("subject =", subject)
                .filter("createDate =", TimeHelper.convertInstantToDate(createDate))
                .first().now();
    }

    private AdminEmail getAdminEmailEntityBySubject(String subject) {
        return load().filter("subject =", subject).first().now();
    }

    @Override
    protected LoadType<AdminEmail> load() {
        return ofy().load().type(AdminEmail.class);
    }

    @Override
    protected AdminEmail getEntity(AdminEmailAttributes adminEmailToGet) {
        if (adminEmailToGet.getEmailId() != null) {
            return getAdminEmailEntity(adminEmailToGet.getEmailId());
        }

        return getAdminEmailEntity(adminEmailToGet.getSubject(), adminEmailToGet.getCreateDate());
    }

    @Override
    protected QueryKeys<AdminEmail> getEntityQueryKeys(AdminEmailAttributes attributes) {
        Key<AdminEmail> key = makeKeyOrNullFromWebSafeString(attributes.emailId);

        Query<AdminEmail> query;
        if (key == null) {
            query = load()
                    .filter("subject =", attributes.subject)
                    .filter("createDate =", TimeHelper.convertInstantToDate(attributes.createDate));
        } else {
            query = load().filterKey(key);
        }

        return query.keys();
    }

    @Override
    protected AdminEmailAttributes makeAttributes(AdminEmail entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return AdminEmailAttributes.valueOf(entity);
    }
}
