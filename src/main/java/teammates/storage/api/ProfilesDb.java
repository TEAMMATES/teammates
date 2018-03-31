package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.ThreadHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.StudentProfile;

/**
 * Handles CRUD operations for student profiles.
 *
 * @see StudentProfile
 * @see StudentProfileAttributes
 */
public class ProfilesDb extends EntitiesDb<StudentProfile, StudentProfileAttributes> {

    private static final Logger log = Logger.getLogger();

    /**
     * Gets the datatransfer (*Attributes) version of the profile
     * corresponding to the googleId given. Returns null if the
     * profile was not found
     */
    public StudentProfileAttributes getStudentProfile(String accountGoogleId) {
        return makeAttributesOrNull(getStudentProfileEntityFromDb(accountGoogleId));
    }

    /**
     * Updates the entire profile based on the given new profile attributes.
     * Assumes that the googleId remains the same and so updates the profile
     * with the given googleId.
     */
    // TODO: update the profile with whatever given values are valid and ignore those that are not valid.
    public void updateStudentProfile(StudentProfileAttributes newSpa)
            throws InvalidParametersException, EntityDoesNotExistException {
        validateNewProfile(newSpa);

        StudentProfile profileToUpdate = getCurrentProfileFromDb(newSpa.googleId);
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
        profileToUpdate.setMoreInfo(new Text(newSpa.moreInfo));
        profileToUpdate.setModifiedDate(Instant.now());

        boolean hasNewNonEmptyPictureKey = !newSpa.pictureKey.isEmpty()
                && !newSpa.pictureKey.equals(profileToUpdate.getPictureKey().getKeyString());
        if (hasNewNonEmptyPictureKey) {
            profileToUpdate.setPictureKey(new BlobKey(newSpa.pictureKey));
        }

        saveEntity(profileToUpdate);
    }

    /**
     * Updates the pictureKey of the profile with given GoogleId.
     * Deletes existing picture if key is different and updates
     * modifiedDate
     */
    public void updateStudentProfilePicture(String googleId, String newPictureKey) throws EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newPictureKey);
        Assumption.assertNotEmpty("GoogleId is empty", googleId);
        Assumption.assertNotEmpty("PictureKey is empty", newPictureKey);

        StudentProfile profileToUpdate = getCurrentProfileFromDb(googleId);

        boolean hasNewNonEmptyPictureKey = !newPictureKey.isEmpty()
                && !newPictureKey.equals(profileToUpdate.getPictureKey().getKeyString());
        if (hasNewNonEmptyPictureKey) {
            profileToUpdate.setPictureKey(new BlobKey(newPictureKey));
            profileToUpdate.setModifiedDate(Instant.now());
        }

        saveEntity(profileToUpdate);
    }

    @Override
    public void deleteEntity(StudentProfileAttributes entityToDelete) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entityToDelete);

        Key<StudentProfile> keyToDelete = getEntityQueryKeys(entityToDelete).first().now();
        if (keyToDelete == null) {
            ofy().delete().keys(getEntityQueryKeysForLegacyData(entityToDelete)).now();
        } else {
            ofy().delete().key(keyToDelete).now();
        }

        log.info(entityToDelete.getBackupIdentifier());
    }

    @Override
    public void deleteEntities(Collection<StudentProfileAttributes> entitiesToDelete) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entitiesToDelete);

        ArrayList<Key<StudentProfile>> keysToDelete = new ArrayList<>();
        for (StudentProfileAttributes entityToDelete : entitiesToDelete) {
            Key<StudentProfile> keyToDelete = getEntityQueryKeys(entityToDelete).first().now();
            if (keyToDelete == null) {
                keyToDelete = getEntityQueryKeysForLegacyData(entityToDelete).first().now();
            }
            if (keyToDelete == null) {
                continue;
            }
            keysToDelete.add(keyToDelete);
            log.info(entityToDelete.getBackupIdentifier());
        }

        ofy().delete().keys(keysToDelete).now();
    }

    /**
     * Deletes the profile picture from GCS and
     * updates the profile entity:
     *     empties the key and updates the modifiedDate.
     */
    public void deleteStudentProfilePicture(String googleId) throws EntityDoesNotExistException {
        StudentProfile sp = getCurrentProfileFromDb(googleId);

        if (!sp.getPictureKey().equals(new BlobKey(""))) {
            deletePicture(sp.getPictureKey());
            sp.setPictureKey(new BlobKey(""));
            sp.setModifiedDate(Instant.now());
        }

        saveEntity(sp);
    }

    /**
     * This method is not scalable. Not to be used unless for admin features.
     *
     * @return the list of all student profiles in the database.
     */
    @Deprecated
    public List<StudentProfileAttributes> getAllStudentProfiles() {
        return makeAttributes(getStudentProfileEntities());
    }

    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------- Helper Functions -----------------------------------------------
    //-------------------------------------------------------------------------------------------------------

    private StudentProfile getCurrentProfileFromDb(String googleId) throws EntityDoesNotExistException {
        StudentProfile profileToUpdate = getStudentProfileEntityFromDb(googleId);

        if (profileToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_STUDENT_PROFILE + googleId
                    + ThreadHelper.getCurrentThreadStack());
        }

        return profileToUpdate;
    }

    /**
     * Checks if an account entity exists for the given googleId and creates
     * a profile entity for this account. This is only used for porting
     * legacy account entities on the fly.
     */
    // TODO: remove this function once legacy data have been ported over
    private StudentProfile getStudentProfileEntityForLegacyData(String googleId) {
        Account account = ofy().load().type(Account.class).id(googleId).now();

        if (account == null) {
            return null;
        }

        StudentProfile profile = new StudentProfile(account.getGoogleId());
        account.setStudentProfile(profile);

        return profile;
    }

    /**
     * Gets the profile entity associated with given googleId.
     * If the profile does not exist, it tries to get the
     * profile from the function
     * 'getStudentProfileEntityForLegacyData'.
     */
    // TODO: update this function once legacy data have been ported over
    private StudentProfile getStudentProfileEntityFromDb(String googleId) {
        Key<Account> parentKey = Key.create(Account.class, googleId);
        Key<StudentProfile> childKey = Key.create(parentKey, StudentProfile.class, googleId);
        StudentProfile profile = ofy().load().key(childKey).now();

        if (profile == null) {
            return getStudentProfileEntityForLegacyData(googleId);
        }

        return profile;
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

    private QueryKeys<StudentProfile> getEntityQueryKeysForLegacyData(StudentProfileAttributes attributes) {
        Key<StudentProfile> legacyKey = Key.create(StudentProfile.class, attributes.googleId);
        return load().filterKey(legacyKey).keys();
    }

    @Override
    public boolean hasEntity(StudentProfileAttributes attributes) {
        if (getEntityQueryKeys(attributes).first().now() == null) {
            return getEntityQueryKeysForLegacyData(attributes).first().now() != null;
        }
        return true;
    }

    /**
     * Retrieves all student profile entities. This function is not scalable.
     */
    @Deprecated
    private List<StudentProfile> getStudentProfileEntities() {
        return load().list();
    }

    @Override
    protected StudentProfileAttributes makeAttributes(StudentProfile entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return StudentProfileAttributes.valueOf(entity);
    }
}
