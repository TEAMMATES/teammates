package teammates.client.scripts;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.SanitizationHelper;
import teammates.logic.core.AdminEmailsLogic;
import teammates.storage.api.AdminEmailsDb;

import java.io.IOException;
import java.util.List;

import com.google.appengine.api.datastore.Text;

public class DataMigrationForSanitizedDataInAdminEmailAttributes extends RemoteApiClient {
    private static final boolean isPreview = true;
    private AdminEmailsDb adminEmailsDb = new AdminEmailsDb();
    private AdminEmailsLogic adminEmailsLogic = AdminEmailsLogic.inst();
    private int numberOfAffectedEmails;
    private int numberOfUpdatedEmails;

    public static void main(String[] args) throws IOException {
        DataMigrationForSanitizedDataInAdminEmailAttributes migrator =
                new DataMigrationForSanitizedDataInAdminEmailAttributes();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<AdminEmailAttributes> allEmails = adminEmailsLogic.getAllAdminEmails();
        numberOfAffectedEmails = 0;
        numberOfUpdatedEmails = 0;
        DataMigrationForSanitizedDataHelper.LoopHelper loopHelper =
                new DataMigrationForSanitizedDataHelper.LoopHelper(100, "admin emails");
        System.out.println("Running data migration for sanitization on admin emails...");
        System.out.println("Preview: " + isPreview);
        for (AdminEmailAttributes email : allEmails) {
            loopHelper.recordLoop();
            fixSanitizedDataForEmail(email);
        }
        System.out.println("There are/is " + loopHelper.getCount() + " email(s).");
        System.out.println("There are/is " + numberOfAffectedEmails + " affected email(s).");
        System.out.println(numberOfUpdatedEmails + " email(s) are/is successfully updated.");
    }

    private boolean isEmailSanitized(AdminEmailAttributes email) {
        return DataMigrationForSanitizedDataHelper.isSanitizedHtml(email.getContentValue());
    }

    /**
     * Checks if email data is sanitized.
     * In preview mode, it prints the original and desanitized data if the email has sanitized data.
     * In actual mode, it desanitizes and updates the email in the database if the email has sanitized data.
     * If there is no sanitized data, the method does nothing.
     */
    private void fixSanitizedDataForEmail(AdminEmailAttributes email) {
        if (!isEmailSanitized(email)) {
            return;
        }
        numberOfAffectedEmails++;
        String content = email.getContentValue();
        String desanitizedContent = SanitizationHelper.desanitizeFromHtml(content);
        try {
            if (isPreview) {
                System.out.println("Previewing email having subject: " + email.getSubject());
                System.out.println("contents:\n" + content);
                System.out.println("new contents:\n" + desanitizedContent);
                System.out.println();
            } else {
                email.content = new Text(desanitizedContent);
                adminEmailsDb.updateAdminEmail(email);
                numberOfUpdatedEmails++;
            }
        } catch (InvalidParametersException e) {
            System.out.println("Email " + email.getSubject() + " invalid!");
            e.printStackTrace();
        } catch (EntityDoesNotExistException e) {
            System.out.println("Email " + email.getSubject() + " does not exist!");
            e.printStackTrace();
        }

    }
}
