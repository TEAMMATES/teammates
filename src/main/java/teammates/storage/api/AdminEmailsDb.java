package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
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
public class AdminEmailsDb extends OfyEntitiesDb<AdminEmail, AdminEmailAttributes> {

    private static final Logger log = Logger.getLogger();

    public Date createAdminEmail(AdminEmailAttributes adminEmailToAdd) throws InvalidParametersException {
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

        log.info(ae.getBackupIdentifier());
        ofy().save().entity(adminEmailToUpdate).now();

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
        ofy().save().entity(adminEmailToUpdate).now();

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
        return makeAttributes(ofy().load().type(AdminEmail.class)
                .filter("isInTrashBin =", false)
                .filter("sendDate =", null)
                .list());
    }

    /**
     * Gets all admin emails that have been sent and not in trash bin.
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getSentAdminEmails() {
        return makeAttributes(ofy().load().type(AdminEmail.class)
                .filter("isInTrashBin =", false)
                .filter("sendDate !=", null)
                .list());
    }

    /**
     * Gets all admin emails (including sent and draft mails) that have been moved into trash bin.
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getAdminEmailsInTrashBin() {
        return makeAttributes(ofy().load().type(AdminEmail.class).filter("isInTrashBin =", true).list());
    }

    private List<AdminEmail> getAdminEmailEntities() {
        return ofy().load().type(AdminEmail.class).list();
    }

    private AdminEmail getAdminEmailEntity(String adminEmailId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, adminEmailId);
        try {
            return ofy().load().type(AdminEmail.class).id(Long.valueOf(adminEmailId)).now();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private AdminEmail getAdminEmailEntity(String subject, Date createDate) {
        return ofy().load().type(AdminEmail.class)
                .filter("subject =", subject)
                .filter("createDate =", createDate)
                .first().now();
    }

    private AdminEmail getAdminEmailEntityBySubject(String subject) {
        return ofy().load().type(AdminEmail.class).filter("subject =", subject).first().now();
    }

    @Override
    protected AdminEmail getEntity(AdminEmailAttributes adminEmailToGet) {
        if (adminEmailToGet.getEmailId() != null) {
            return getAdminEmailEntity(adminEmailToGet.getEmailId());
        }

        return getAdminEmailEntity(adminEmailToGet.getSubject(),
                                   adminEmailToGet.getCreateDate());
    }

    @Override
    protected QueryKeys<AdminEmail> getEntityQueryKeys(AdminEmailAttributes attributes) {
        String id = attributes.emailId;
        Query<AdminEmail> query;

        if (id == null) {
            query = ofy().load().type(AdminEmail.class)
                    .filter("subject =", attributes.subject)
                    .filter("createDate =", attributes.createDate);
        } else {
            query = ofy().load().type(AdminEmail.class)
                    .filterKey(Key.create(AdminEmail.class, Long.valueOf(id)));
        }

        return query.keys();
    }

    private List<AdminEmailAttributes> makeAttributes(List<AdminEmail> adminEmails) {
        List<AdminEmailAttributes> adminEmailAttributesList = new LinkedList<AdminEmailAttributes>();
        for (AdminEmail adminEmail : adminEmails) {
            adminEmailAttributesList.add(new AdminEmailAttributes(adminEmail));
        }
        return adminEmailAttributesList;
    }

}
