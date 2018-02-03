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
        extends DataMigrationBaseScript<StudentProfileAttributes> {

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

    protected void doOperation() {
        @SuppressWarnings("deprecation")
        List<StudentProfileAttributes> allStudentProfiles = profilesLogic.getAllStudentProfiles();
        int numberOfAffectedProfiles = 0;
        int numberOfUpdatedProfiles = 0;
        LoopHelper loopHelper = new LoopHelper(100, "student profiles processed.");
        println("Running data migration for sanitization on student profiles...");
        println("Preview: " + isPreview);
        for (StudentProfileAttributes profile : allStudentProfiles) {
            loopHelper.recordLoop();
            boolean hasAllUnsanitizedFields = !hasAnySanitizedField(profile);
            if (hasAllUnsanitizedFields) {
                // skip the update if all fields are already unsanitized
                continue;
            }
            numberOfAffectedProfiles++;
            try {
                desanitizeAndUpdateProfile(profile);
                numberOfUpdatedProfiles++;
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                println("Problem sanitizing profile with google id " + profile.googleId);
                println(e.getMessage());
            }
        }
        println("Total number of profiles: " + loopHelper.getCount());
        println("Number of affected profiles: " + numberOfAffectedProfiles);
        println("Number of updated profiles: " + numberOfUpdatedProfiles);

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
        profile.moreInfo = desanitizeIfHtmlSanitized(profile.moreInfo);

        if (!profile.isValid()) {
            throw new InvalidParametersException(profile.getInvalidityInfo());
        }

        profilesDb.updateStudentProfile(profile);
    }

    /**
     * {@inheritDoc}
     */

    private boolean hasAnySanitizedField(StudentProfileAttributes profile) {
        return isSanitizedHtml(profile.shortName) || isSanitizedHtml(profile.email)
                || isSanitizedHtml(profile.institute) || isSanitizedHtml(profile.nationality)
                || isSanitizedHtml(profile.moreInfo);

    @Override
    protected void postAction() {
        // nothing to do

    }

}
