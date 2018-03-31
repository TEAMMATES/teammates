package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.SanitizationHelper;
import teammates.storage.api.AdminEmailsDb;

/**
 * Script to desanitize content of AdminEmailAttributes if it is sanitized.
 * Html sanitization of content before saving is removed and content is expected to be in its unsanitized form.
 * This script desanitizes content of exisiting AdminEmailAttributes so that
 * all emails will have unsanitized content.
 */
public class DataMigrationForSanitizedDataInAdminEmailAttributes
        extends DataMigrationForEntities<AdminEmailAttributes> {

    private AdminEmailsDb adminEmailsDb = new AdminEmailsDb();

    public static void main(String[] args) throws IOException {
        new DataMigrationForSanitizedDataInAdminEmailAttributes().doOperationRemotely();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isPreview() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    protected List<AdminEmailAttributes> getEntities() {
        return adminEmailsDb.getAllAdminEmails();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isMigrationNeeded(AdminEmailAttributes email) {
        return SanitizationHelper.isSanitizedHtml(email.getContentValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void printPreviewInformation(AdminEmailAttributes email) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void migrate(AdminEmailAttributes email) throws InvalidParametersException, EntityDoesNotExistException {
        String desanitizedContent = SanitizationHelper.desanitizeFromHtml(email.getContentValue());
        email.content = new Text(desanitizedContent);
        adminEmailsDb.updateAdminEmail(email);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postAction() {
        // nothing to do
    }

}
