package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.StudentProfile;

/**
 * Handles CRUD operations for student profiles.
 *
 * @see StudentProfile
 * @see StudentProfileAttributes
 */
public class ProfilesDb extends EntitiesDb<StudentProfile, StudentProfileAttributes> {

    /**
     * Gets the student profile associated with {@code accountGoogleId}.
     *
     * @return null if the profile was not found
     */
    public StudentProfileAttributes getStudentProfile(String accountGoogleId) {
        return makeAttributesOrNull(getStudentProfileEntityFromDb(accountGoogleId));
    }

    /**
     * Updates/Creates the profile based on the given new profile attributes.
     *
     * <p>Will not update {@link StudentProfileAttributes#pictureKey} if it is empty.</p>
     *
     * @throws InvalidParametersException if attributes in {@code newSpa} are not valid
     */
    public void updateOrCreateStudentProfile(StudentProfileAttributes newSpa) throws InvalidParametersException {
        // TODO: update the profile with whatever given values are valid and ignore those that are not valid.
        validateNewProfile(newSpa);

        StudentProfile profileToUpdate = getStudentProfileEntityFromDb(newSpa.googleId);
        if (profileToUpdate == null) {
            profileToUpdate = new StudentProfile(newSpa.googleId);
        }
        if (hasNoNewChangesToProfile(newSpa, profileToUpdate)) {
            return;
        }

        updateProfileWithNewValues(newSpa, profileToUpdate);
    }

    private void validateNewProfile(StudentProfileAttributes newSpa) throws InvalidParametersException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newSpa);

        if (!newSpa.isValid()) {
            throw new InvalidParametersException(newSpa.getInvalidityInfo());
        }
    }

    private boolean hasNoNewChangesToProfile(StudentProfileAttributes newSpa, StudentProfile profileToUpdate) {
        StudentProfileAttributes newSpaCopy = newSpa.getCopy();
        StudentProfileAttributes existingProfile = StudentProfileAttributes.valueOf(profileToUpdate);

        newSpaCopy.modifiedDate = existingProfile.modifiedDate;
        return existingProfile.toString().equals(newSpaCopy.toString());
    }

    private void updateProfileWithNewValues(StudentProfileAttributes newSpa, StudentProfile profileToUpdate) {
        newSpa.sanitizeForSaving();

        profileToUpdate.setShortName(newSpa.shortName);
        profileToUpdate.setEmail(newSpa.email);
        profileToUpdate.setInstitute(newSpa.institute);
        profileToUpdate.setNationality(newSpa.nationality);
        profileToUpdate.setGender(newSpa.gender);
        profileToUpdate.setMoreInfo(newSpa.moreInfo);
        profileToUpdate.setModifiedDate(Instant.now());

        boolean hasNewNonEmptyPictureKey = !newSpa.pictureKey.isEmpty()
                && !newSpa.pictureKey.equals(profileToUpdate.getPictureKey().getKeyString());
        if (hasNewNonEmptyPictureKey) {
            profileToUpdate.setPictureKey(new BlobKey(newSpa.pictureKey));
        }

        saveEntity(profileToUpdate);
    }

    /**
     * Deletes the student profile associated with the {@code googleId}.
     *
     * <p>Fails silently if the student profile doesn't exist.</p>
     */
    public void deleteStudentProfile(String googleId) {
        StudentProfile sp = getStudentProfileEntityFromDb(googleId);
        if (sp == null) {
            return;
        }
        if (!sp.getPictureKey().equals(new BlobKey(""))) {
            deletePicture(sp.getPictureKey());
        }
        deleteEntityDirect(sp);
    }

    /**
     * Updates the {@code pictureKey} of the profile with given {@code googleId}.
     *
     * <p>If there is no stored profile entity, create a new one.</p>
     */
    public void updateStudentProfilePicture(String googleId, String newPictureKey) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newPictureKey);
        Assumption.assertNotEmpty("GoogleId is empty", googleId);
        Assumption.assertNotEmpty("PictureKey is empty", newPictureKey);

        StudentProfile profileToUpdate = getStudentProfileEntityFromDb(googleId);
        if (profileToUpdate == null) {
            profileToUpdate = new StudentProfile(googleId);
        }

        boolean hasNewNonEmptyPictureKey = !newPictureKey.isEmpty()
                && !newPictureKey.equals(profileToUpdate.getPictureKey().getKeyString());
        if (hasNewNonEmptyPictureKey) {
            profileToUpdate.setPictureKey(new BlobKey(newPictureKey));
            profileToUpdate.setModifiedDate(Instant.now());
        }

        saveEntity(profileToUpdate);
    }

    /**
     * Deletes picture associated with the {@code key}.
     *
     * <p>Fails silently if the {@code key} doesn't exist.</p>
     */
    public void deletePicture(BlobKey key) {
        GoogleCloudStorageHelper.deleteFile(key);
    }

    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------- Helper Functions -----------------------------------------------
    //-------------------------------------------------------------------------------------------------------

    /**
     * Gets the profile entity associated with the {@code googleId}.
     *
     * @return null if entity is not found
     */
    private StudentProfile getStudentProfileEntityFromDb(String googleId) {
        Key<Account> parentKey = Key.create(Account.class, googleId);
        Key<StudentProfile> childKey = Key.create(parentKey, StudentProfile.class, googleId);
        return ofy().load().key(childKey).now();
    }

    @Override
    protected LoadType<StudentProfile> load() {
        return ofy().load().type(StudentProfile.class);
    }

    @Override
    protected StudentProfile getEntity(StudentProfileAttributes attributes) {
        // this method is never used and is here only for future expansion and completeness
        return getStudentProfileEntityFromDb(attributes.googleId);
    }

    @Override
    protected QueryKeys<StudentProfile> getEntityQueryKeys(StudentProfileAttributes attributes) {
        Key<Account> parentKey = Key.create(Account.class, attributes.googleId);
        Key<StudentProfile> childKey = Key.create(parentKey, StudentProfile.class, attributes.googleId);
        return load().filterKey(childKey).keys();
    }

    @Override
    protected StudentProfileAttributes makeAttributes(StudentProfile entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return StudentProfileAttributes.valueOf(entity);
    }
}
