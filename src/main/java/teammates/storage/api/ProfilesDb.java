package teammates.storage.api;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.QueryKeys;

import static com.googlecode.objectify.ObjectifyService.ofy;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.StudentProfile;

/**
 * Handles CRUD operations for student profiles.
 *
 * @see StudentProfile
 * @see StudentProfileAttributes
 */
public class ProfilesDb extends OfyEntitiesDb<StudentProfile, StudentProfileAttributes> {

    public void createStudentProfile(StudentProfileAttributes newSpa)
            throws InvalidParametersException, EntityAlreadyExistsException {
        createEntity(newSpa);
    }

    /**
     * Gets the datatransfer (*Attributes) version of the profile
     * corresponding to the googleId given. Returns null if the
     * profile was not found
     */
    public StudentProfileAttributes getStudentProfile(String accountGoogleId) {
        StudentProfile sp = getStudentProfileEntityFromDb(accountGoogleId);
        if (sp == null) {
            return null;
        }

        return new StudentProfileAttributes(sp);
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

    private void validateNewProfile(StudentProfileAttributes newSpa)
            throws InvalidParametersException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newSpa);
        if (!newSpa.isValid()) {
            throw new InvalidParametersException(newSpa.getInvalidityInfo());
        }
    }

    private boolean hasNoNewChangesToProfile(StudentProfileAttributes newSpa,
            StudentProfile profileToUpdate) {
        StudentProfileAttributes existingProfile = new StudentProfileAttributes(profileToUpdate);

        newSpa.modifiedDate = existingProfile.modifiedDate;
        return existingProfile.toString().equals(newSpa.toString());
    }

    private void updateProfileWithNewValues(StudentProfileAttributes newSpa,
            StudentProfile profileToUpdate) {

        newSpa.sanitizeForSaving();
        profileToUpdate.setShortName(newSpa.shortName);
        profileToUpdate.setEmail(newSpa.email);
        profileToUpdate.setInstitute(newSpa.institute);
        profileToUpdate.setNationality(newSpa.nationality);
        profileToUpdate.setGender(newSpa.gender);
        profileToUpdate.setMoreInfo(new Text(newSpa.moreInfo));
        profileToUpdate.setModifiedDate(new Date());

        boolean hasNewNonEmptyPictureKey = !newSpa.pictureKey.isEmpty()
                && !newSpa.pictureKey.equals(profileToUpdate.getPictureKey().getKeyString());

        if (hasNewNonEmptyPictureKey) {
            profileToUpdate.setPictureKey(new BlobKey(newSpa.pictureKey));
        }

        ofy().save().entity(profileToUpdate).now();
    }

    /**
     * Updates the pictureKey of the profile with given GoogleId.
     * Deletes existing picture if key is different and updates
     * modifiedDate
     */
    public void updateStudentProfilePicture(String googleId,
            String newPictureKey) throws EntityDoesNotExistException {

        validateParametersForUpdatePicture(googleId, newPictureKey);
        StudentProfile profileToUpdate = getCurrentProfileFromDb(googleId);

        boolean hasNewNonEmptyPictureKey = !newPictureKey.isEmpty()
                && !newPictureKey.equals(profileToUpdate.getPictureKey().getKeyString());

        if (!hasNewNonEmptyPictureKey) {
            return;
        }

        profileToUpdate.setPictureKey(new BlobKey(newPictureKey));
        profileToUpdate.setModifiedDate(new Date());

        ofy().save().entity(profileToUpdate).now();
    }

    private void validateParametersForUpdatePicture(String googleId,
            String newPictureKey) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newPictureKey);
        Assumption.assertNotEmpty("GoogleId is empty", googleId);
        Assumption.assertNotEmpty("PictureKey is empty", newPictureKey);
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
            sp.setModifiedDate(new Date());
        }

        ofy().save().entity(sp).now();
    }

    /**
     * This method is not scalable. Not to be used unless for admin features.
     *
     * @return the list of all student profiles in the database.
     */
    @Deprecated
    public List<StudentProfileAttributes> getAllStudentProfiles() {
        List<StudentProfileAttributes> list = new LinkedList<>();
        List<StudentProfile> entities = getStudentProfileEntities();

        for (StudentProfile student : entities) {
            list.add(new StudentProfileAttributes(student));
        }
        return list;
    }

    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------- Helper Functions -----------------------------------------------
    //-------------------------------------------------------------------------------------------------------

    private StudentProfile getCurrentProfileFromDb(String googleId)
            throws EntityDoesNotExistException {
        StudentProfile profileToUpdate = getStudentProfileEntityFromDb(googleId);
        ensureUpdatingProfileExists(googleId, profileToUpdate);

        return profileToUpdate;
    }

    private void ensureUpdatingProfileExists(String googleId,
            StudentProfile profileToUpdate) throws EntityDoesNotExistException {
        if (profileToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_STUDENT_PROFILE + googleId
                    + ThreadHelper.getCurrentThreadStack());
        }
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
    protected StudentProfile getEntity(StudentProfileAttributes attributes) {
        // this method is never used and is here only for future expansion and completeness
        return getStudentProfileEntityFromDb(attributes.googleId);
    }

    @Override
    public boolean hasEntity(StudentProfileAttributes attributes) {
        Key<StudentProfile> keyToFind = Key.create(StudentProfile.class, attributes.googleId);
        QueryKeys<StudentProfile> keysOnlyQuery = ofy().load().type(StudentProfile.class).filterKey(keyToFind).keys();
        return keysOnlyQuery.first().now() != null;
    }

    /**
     * Retrieves all student profile entities. This function is not scalable.
     */
    @Deprecated
    private List<StudentProfile> getStudentProfileEntities() {
        return ofy().load().type(StudentProfile.class).list();
    }
}
