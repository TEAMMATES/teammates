package teammates.storage.api;

import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Utils;
import teammates.storage.entity.Account;
import teammates.storage.entity.StudentProfile;

/**
 * Handles CRUD Operations for profiles.
 * The API uses data transfer classes (i.e. *Attributes) instead of persistable classes.
 * 
 */
public class ProfilesDb extends EntitiesDb {
    
    @SuppressWarnings("unused")
    private static final Logger log = Utils.getLogger();
    
    /**
     * Gets the datatransfer (*Attributes) version of the profile
     * corresponding to the googleId given. Returns null if the
     * profile was not found
     * 
     * @param accountGoogleId
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
     * 
     * TODO: update the profile with whatever given values are valid and 
     * ignore those that are not valid.
     * @param newSpa
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     */
    public void updateStudentProfile(StudentProfileAttributes newSpa) 
            throws InvalidParametersException, EntityDoesNotExistException {        
        
        validateNewProfile(newSpa);
        
        StudentProfile profileToUpdate = getCurrentProfileFromDb(newSpa.googleId);
        if(hasNoNewChangesToProfile(newSpa, profileToUpdate)) return;

        updateProfileWithNewValues(newSpa, profileToUpdate);
        closePM();
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
    }
    
    /**
     * Udates the pictureKey of the profile with given GoogleId.
     * Deletes existing picture if key is different and updates
     * modifiedDate
     * 
     * @param googleId
     * @param newPictureKey
     * @throws EntityDoesNotExistException
     */
    
    public void updateStudentProfilePicture(String googleId,
            String newPictureKey) throws EntityDoesNotExistException {
        
        validateParametersForUpdatePicture(googleId, newPictureKey);
        StudentProfile profileToUpdate = getCurrentProfileFromDb(googleId);
        
        boolean hasNewNonEmptyPictureKey = !newPictureKey.isEmpty()
                && !newPictureKey.equals(profileToUpdate.getPictureKey().getKeyString());
        
        if (hasNewNonEmptyPictureKey) {
            profileToUpdate.setPictureKey(new BlobKey(newPictureKey));
            profileToUpdate.setModifiedDate(new Date());
        }
        
        closePM();
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
     *     empties the key and updates the modifiedDate 
     * 
     * @param googleId
     * @throws BlobstoreFailureException
     * @throws EntityDoesNotExistException 
     */
    public void deleteStudentProfilePicture(String googleId) throws BlobstoreFailureException, EntityDoesNotExistException {
        StudentProfile sp = getCurrentProfileFromDb(googleId);
        
        if (!sp.getPictureKey().equals(new BlobKey(""))) {
            try {
                deletePicture(sp.getPictureKey());
                sp.setPictureKey(new BlobKey(""));
                sp.setModifiedDate(new Date());
            } catch (BlobstoreFailureException bfe) {
                // this branch is not tested as it is 
                //      => difficult to reproduce during testing
                //      => properly handled higher up
                throw bfe;
            }
        }
        
        closePM();
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
     * 
     * TODO: remove this function once legacy data have been ported over
     * @param googleId
     * @return
     */
    private StudentProfile getStudentProfileEntityForLegacyData (String googleId) {
        Key key = KeyFactory.createKey(Account.class.getSimpleName(), googleId);
        try {
            // This method is not testable as loading legacy data into 
            // current database is restricted by new validity checks
            Account account = getPM().getObjectById(Account.class, key);
            if (account == null
                    || JDOHelper.isDeleted(account)) {
                return null;
            }
            
            account.setStudentProfile(new StudentProfile(account.getGoogleId()));
            return account.getStudentProfile();
            
        } catch(JDOObjectNotFoundException je) {
            return null;
        }
    }
    
    /**
     * Gets the profile entity associated with given googleId.
     * If the profile does not exist, it tries to get the
     * profile from the function 
     * 'getStudentProfileEntityForLegacyData'.
     * 
     * TODO: update this function once legacy data have been ported over
     * @param googleId
     */
    private StudentProfile getStudentProfileEntityFromDb(String googleId) {
        Key childKey = KeyFactory.createKey(Account.class.getSimpleName(), googleId)
                                 .getChild(StudentProfile.class.getSimpleName(), googleId);
        
        try {
            StudentProfile profile = getPM().getObjectById(StudentProfile.class, childKey);
            if (profile == null
                    || JDOHelper.isDeleted(profile)) {
                return null;
            }
            
            return profile;
        } catch (JDOObjectNotFoundException je) {
            return getStudentProfileEntityForLegacyData(googleId);
        }
    }
    
    @Override
    protected Object getEntity(EntityAttributes attributes) {
        // this method is never used and is here only for future expansion and completeness
        return getStudentProfileEntityFromDb(((StudentProfileAttributes) attributes).googleId);
    }
}
