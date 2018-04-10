package teammates.client.scripts;

import static teammates.common.util.SanitizationHelper.desanitizeIfHtmlSanitized;
import static teammates.common.util.SanitizationHelper.isSanitizedHtml;

import java.io.IOException;
import java.util.List;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.ProfilesDb;

/**
 * Script to desanitize content of {@link StudentProfileAttributes} if it is sanitized.
 *
 * <p>Fields {@link StudentProfileAttributes#shortName}, {@link StudentProfileAttributes#email},
 * {@link StudentProfileAttributes#institute}, {@link StudentProfileAttributes#nationality},
 * {@link StudentProfileAttributes#gender}, {@link StudentProfileAttributes#moreInfo}
 * are no longer sanitized before saving and these fields are expected to be in their unsanitized form.</p>
 *
 * <p>This script desanitizes these fields of exisiting StudentProfileAttributes if they are sanitized so that
 * all profiles will have unsanitized values in these fields.</p>
 */
public class DataMigrationForSanitizedDataInStudentProfileAttributes
        extends DataMigrationForEntities<StudentProfileAttributes> {

    private ProfilesDb profilesDb = new ProfilesDb();

    public static void main(String[] args) throws IOException {
        new DataMigrationForSanitizedDataInStudentProfileAttributes().doOperationRemotely();
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
    protected List<StudentProfileAttributes> getEntities() {
        return profilesDb.getAllStudentProfiles();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isMigrationNeeded(StudentProfileAttributes profile) {
        return isSanitizedHtml(profile.shortName) || isSanitizedHtml(profile.email)
                || isSanitizedHtml(profile.institute) || isSanitizedHtml(profile.nationality)
                || isSanitizedHtml(profile.gender) || isSanitizedHtml(profile.moreInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void printPreviewInformation(StudentProfileAttributes profile) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void migrate(StudentProfileAttributes profile)
            throws InvalidParametersException, EntityDoesNotExistException {
        profile.shortName = desanitizeIfHtmlSanitized(profile.shortName);
        profile.email = desanitizeIfHtmlSanitized(profile.email);
        profile.institute = desanitizeIfHtmlSanitized(profile.institute);
        profile.nationality = desanitizeIfHtmlSanitized(profile.nationality);
        profile.gender = desanitizeIfHtmlSanitized(profile.gender);
        profile.moreInfo = desanitizeIfHtmlSanitized(profile.moreInfo);

        if (!profile.isValid()) {
            throw new InvalidParametersException(profile.getInvalidityInfo());
        }

        profilesDb.updateStudentProfile(profile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postAction() {
        // nothing to do
    }

}
