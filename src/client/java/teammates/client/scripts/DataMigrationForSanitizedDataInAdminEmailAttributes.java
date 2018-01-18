package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.client.scripts.util.LoopHelper;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.SanitizationHelper;
import teammates.logic.core.AdminEmailsLogic;
import teammates.storage.api.AdminEmailsDb;

/**
 * Script to desanitize content of AdminEmailAttributes if it is sanitized.
 * Html sanitization of content before saving is removed and content is expected to be in its unsanitized form.
 * This script desanitizes content of exisiting AdminEmailAttributes so that
 * all emails will have unsanitized content.
 */
public class DataMigrationForSanitizedDataInAdminEmailAttributes extends RemoteApiClient {
    private static final boolean isPreview = true;
    private AdminEmailsDb adminEmailsDb = new AdminEmailsDb();
    private AdminEmailsLogic adminEmailsLogic = AdminEmailsLogic.inst();

    public static void main(String[] args) throws IOException {
        new DataMigrationForSanitizedDataInAdminEmailAttributes().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<AdminEmailAttributes> allEmails = adminEmailsLogic.getAllAdminEmails();
        int numberOfAffectedEmails = 0;
        int numberOfUpdatedEmails = 0;
        LoopHelper loopHelper = new LoopHelper(100, "admin emails processed.");
        println("Running data migration for sanitization on admin emails...");
        println("Preview: " + isPreview);
        for (AdminEmailAttributes email : allEmails) {
            loopHelper.recordLoop();
            boolean isEmailSanitized = SanitizationHelper.isSanitizedHtml(email.getContentValue());
            if (!isEmailSanitized) {
                // skip the update if email is not sanitized
                continue;
            }
            numberOfAffectedEmails++;
            try {
                desanitizeAndUpdateEmail(email);
                numberOfUpdatedEmails++;
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                println("Problem sanitizing email id " + email.getEmailId());
                println(e.getMessage());
            }
        }
        println("Total number of emails: " + loopHelper.getCount());
        println("Number of affected emails: " + numberOfAffectedEmails);
        println("Number of updated emails: " + numberOfUpdatedEmails);
    }

    /**
     * Desanitizes the {@code email} content and updates it in the database.
     */
    private void desanitizeAndUpdateEmail(AdminEmailAttributes email)
            throws InvalidParametersException, EntityDoesNotExistException {
        String desanitizedContent = SanitizationHelper.desanitizeFromHtml(email.getContentValue());
        email.content = new Text(desanitizedContent);
        if (!isPreview) {
            adminEmailsDb.updateAdminEmail(email);
        }
    }
}
