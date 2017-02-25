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

    public static void main(String[] args) throws IOException {
        DataMigrationForSanitizedDataInAdminEmailAttributes migrator =
                new DataMigrationForSanitizedDataInAdminEmailAttributes();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<AdminEmailAttributes> allEmails = adminEmailsLogic.getAllAdminEmails();
        if (isPreview) {
            System.out.println("Checking Sanitization for admin emails...");
        }
        int numberOfAffectedEmails = 0;
        for (AdminEmailAttributes email : allEmails) {
            if (isPreview) {
                if (previewSanitizedDataForAdminEmail(email)) {
                    numberOfAffectedEmails++;
                }
            } else {
                fixSanitizedDataForEmail(email);
            }
        }
        if (isPreview) {
            System.out.println("There are/is " + numberOfAffectedEmails + " affected email(s)!");
        } else {
            System.out.println("Sanitization fixing done!");
        }
    }

    private boolean previewSanitizedDataForAdminEmail(AdminEmailAttributes email) {
        boolean hasSanitizedData = checkEmailHasSanitizedData(email);
        if (hasSanitizedData) {
            System.out.println("Checking email having subject: " + email.getSubject());

            String content = email.getContentForDisplay();
            System.out.println("contents:\n" + content);
            System.out.println("new contents:\n" + SanitizationHelper.desanitizeFromHtml(content));

            System.out.println();
        }
        return hasSanitizedData;
    }

    private boolean checkEmailHasSanitizedData(AdminEmailAttributes email) {
        return SanitizationHelper.isSanitizedHtml(email.getContentForDisplay());
    }

    private void fixSanitizedDataForEmail(AdminEmailAttributes email) {
        try {
            boolean hasSanitizedData = checkEmailHasSanitizedData(email);
            if (hasSanitizedData) {
                email.content =
                        new Text(SanitizationHelper.desanitizeFromHtml(email.getContentForDisplay()));
                adminEmailsDb.updateAdminEmail(email);
            }
        } catch (InvalidParametersException e) {
            System.out.println("Email " + email.getSubject() + " invalid!");
            e.printStackTrace();
        } catch (EntityDoesNotExistException e) {
            System.out.println("Student " + email.getSubject() + " does not exist!");
            e.printStackTrace();
        }
    }
}
