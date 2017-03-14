package teammates.client.scripts;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.client.scripts.helper.DataMigrationForSanitizedDataHelper;
import teammates.client.scripts.helper.LoopHelper;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.SanitizationHelper;
import teammates.logic.core.AdminEmailsLogic;
import teammates.storage.api.AdminEmailsDb;

import java.io.IOException;
import java.util.List;

import com.google.appengine.api.datastore.Text;

/**
 * Script to desanitize content of AdminEmailAttributes if it is sanitized.
 * Html sanitization of content before saving is removed and content is expected to be in its unsanitized form.
 * This script helps to desanitize content of exisiting AdminEmailAttributes so that they follow the new standard.
 */
public class DataMigrationForSanitizedDataInAdminEmailAttributes extends RemoteApiClient {
    private static final boolean isPreview = true;
    private AdminEmailsDb adminEmailsDb = new AdminEmailsDb();
    private AdminEmailsLogic adminEmailsLogic = AdminEmailsLogic.inst();
    private int numberOfAffectedEmails;
    private int numberOfUpdatedEmails;

    public static void main(String[] args) throws IOException {
        new DataMigrationForSanitizedDataInAdminEmailAttributes().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<AdminEmailAttributes> allEmails = adminEmailsLogic.getAllAdminEmails();
        numberOfAffectedEmails = 0;
        numberOfUpdatedEmails = 0;
        LoopHelper loopHelper = new LoopHelper(100, "admin emails processed.");
        println("Running data migration for sanitization on admin emails...");
        println("Preview: " + isPreview);
        for (AdminEmailAttributes email : allEmails) {
            loopHelper.recordLoop();
            fixSanitizedDataForEmail(email);
        }
        println("There are/is " + loopHelper.getCount() + " email(s).");
        println("There are/is " + numberOfAffectedEmails + " affected email(s).");
        println((isPreview ? 0 : numberOfUpdatedEmails) + " email(s) are/is successfully updated.");
    }

    private boolean isEmailSanitized(AdminEmailAttributes email) {
        return DataMigrationForSanitizedDataHelper.isSanitizedHtml(email.getContentValue());
    }

    /**
     * Desanitizes and updates the {@code email} in the database if the email has sanitized data.
     * If there is no sanitized data, the method does nothing.
     * Updates the counters {@link #numberOfAffectedEmails}, {@link #numberOfUpdatedEmails} accordingly.
     */
    private void fixSanitizedDataForEmail(AdminEmailAttributes email) {
        if (!isEmailSanitized(email)) {
            return;
        }
        numberOfAffectedEmails++;
        printingForPreview(email);
        desanitizeEmail(email);
        try {
            if (!isPreview) {
                adminEmailsDb.updateAdminEmail(email);
            }
            numberOfUpdatedEmails++;
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            println("Problem sanitizing email " + email.getSubject());
            e.printStackTrace();
        }
    }

    /**
     * Desanitizes the {@code email} content.
     */
    private void desanitizeEmail(AdminEmailAttributes email) {
        String desanitizedContent = SanitizationHelper.desanitizeFromHtml(email.getContentValue());
        email.content = new Text(desanitizedContent);
    }

    /**
     * Prints information of the {@code email} in preview mode.
     */
    private void printingForPreview(AdminEmailAttributes email) {
        if (!isPreview) {
            return;
        }
        println("Email to be sanitized: " + email.getSubject());
    }

    /**
     * Prints the {@code string} on system output, followed by a newline.
     */
    private void println(String string) {
        System.out.println(string);
    }
}
