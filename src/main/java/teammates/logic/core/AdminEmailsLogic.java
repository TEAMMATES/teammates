package teammates.logic.core;

import java.util.Date;
import java.util.List;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.storage.api.AdminEmailsDb;

import com.google.appengine.api.blobstore.BlobKey;

/**
 * Handles operations related to emails sent by the admin.
 *
 * @see {@link AdminEmailAttributes}
 * @see {@link AdminEmailsDb}
 */
public final class AdminEmailsLogic {

    private static AdminEmailsLogic instance = new AdminEmailsLogic();

    private static final AdminEmailsDb adminEmailsDb = new AdminEmailsDb();

    private AdminEmailsLogic() {
        // prevent initialization
    }

    public static AdminEmailsLogic inst() {
        return instance;
    }

    /**
     * This method is not scalable. Not to be used unless for admin features.
     * @return the list of all adminEmails in the database.
     */
    @Deprecated
    public List<AdminEmailAttributes> getAllAdminEmails() {
        return adminEmailsDb.getAllAdminEmails();
    }

    /**
     * get an admin email by email id
     * @return null if no matched email found
     */
    public AdminEmailAttributes getAdminEmailById(String emailId) {
        Assumption.assertNotNull(emailId);
        return adminEmailsDb.getAdminEmailById(emailId);
    }

    /**
     * get an admin email by subject and createDate
     * @return null if no matched email found
     */
    public AdminEmailAttributes getAdminEmail(String subject, Date createDate) {
        Assumption.assertNotNull(subject);
        Assumption.assertNotNull(createDate);

        return adminEmailsDb.getAdminEmail(subject, createDate);
    }

    /**
     * Move an admin email to trash bin.<br>
     * After this the attribute isInTrashBin will be set to true
     * @param adminEmailId
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     */
    public void moveAdminEmailToTrashBin(String adminEmailId)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(adminEmailId);

        AdminEmailAttributes adminEmailToUpdate = getAdminEmailById(adminEmailId);

        if (adminEmailToUpdate != null) {
            adminEmailToUpdate.isInTrashBin = true;
            adminEmailsDb.updateAdminEmail(adminEmailToUpdate);
        }
    }

    /**
     * Move an admin email out of trash bin.<br>
     * After this the attribute isInTrashBin will be set to false
     * @param adminEmailId
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     */
    public void moveAdminEmailOutOfTrashBin(String adminEmailId)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(adminEmailId);

        AdminEmailAttributes adminEmailToUpdate = getAdminEmailById(adminEmailId);

        if (adminEmailToUpdate != null) {
            adminEmailToUpdate.isInTrashBin = false;
            adminEmailsDb.updateAdminEmail(adminEmailToUpdate);
        }
    }

    /**
     * Get all admin emails that have been sent and not in trash bin
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getSentAdminEmails() {
        return adminEmailsDb.getSentAdminEmails();
    }

    /**
     * Get all admin email drafts that have NOT been sent and NOT in trash bin
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getAdminEmailDrafts() {
        return adminEmailsDb.getAdminEmailDrafts();
    }

    /**
     * Get all admin emails that have been moved into trash bin
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getAdminEmailsInTrashBin() {
        return adminEmailsDb.getAdminEmailsInTrashBin();
    }

    public Date createAdminEmail(AdminEmailAttributes newAdminEmail) throws InvalidParametersException {
        return adminEmailsDb.creatAdminEmail(newAdminEmail);
    }

    public void updateAdminEmailById(AdminEmailAttributes newAdminEmail, String emailId)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(emailId);
        Assumption.assertNotNull(newAdminEmail);

        adminEmailsDb.updateAdminEmailById(newAdminEmail, emailId);
    }

    /**
     * deletes all emails in trash bin
     */
    public void deleteAllEmailsInTrashBin() {
        adminEmailsDb.deleteAllEmailsInTrashBin();
    }

    /**
     * deletes files uploaded in admin email compose page
     * @param key, the GCS blobkey used to fetch the file in Google Cloud Storage
     */
    public void deleteAdminEmailUploadedFile(BlobKey key) {
        adminEmailsDb.deleteAdminEmailUploadedFile(key);
    }
}
