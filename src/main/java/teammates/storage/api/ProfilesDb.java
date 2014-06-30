package teammates.storage.api;

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

public class ProfilesDb extends EntitiesDb {
    
    private static final Logger log = Utils.getLogger();
    
    @Override
    protected Object getEntity(EntityAttributes attributes) {
        return null;
    }

    private Account getAccountEntity(String googleId, boolean retrieveStudentProfile) {
        
        Key key = KeyFactory.createKey(Account.class.getSimpleName(), googleId);
        try {
            Account account = getPM().getObjectById(Account.class, key);
            
            if (JDOHelper.isDeleted(account)) {
                return null;
            } else if (retrieveStudentProfile) {
                if (account.getStudentProfile() == null) {
                    // This situation cannot be reproduced and hence not tested
                    // This only happens when existing data in the store do not have a profile 
                    account.setStudentProfile(new StudentProfile(account.getGoogleId()));
                }
            }
            
            return account;
        } catch(JDOObjectNotFoundException je) {
            return null;
        }
    }
    
    private StudentProfile getStudentProfileEntity(String googleId) {
        Key childKey = KeyFactory.createKey(Account.class.getSimpleName(), googleId)
                .getChild(StudentProfile.class.getSimpleName(), googleId);
        
        try {
            return getPM().getObjectById(StudentProfile.class, childKey);
        } catch (JDOObjectNotFoundException je) {
            
            Account a = getAccountEntity(googleId, true);
            if (a == null) {
                return null;
            } else {
                // This situation cannot be reproduced and hence not tested
                // This only happens when existing data in the store do not have a profile
                return a.getStudentProfile();
            }
        }
                
    }
    
    public StudentProfileAttributes getStudentProfile(String accountGoogleId) {        
        StudentProfile sp = getStudentProfileEntity(accountGoogleId);
        
        if (sp == null 
                || JDOHelper.isDeleted(sp)) {
            return null;
        }

        if (sp.getPictureKey() == null) {
            // This situation cannot be reproduced and hence not tested
            // This only happens when existing data in the store do not have a picture
            sp.setPictureKey(new BlobKey(""));
        }
        
        return new StudentProfileAttributes(sp);
    }
    
    public void updateStudentProfile(StudentProfileAttributes newSpa) 
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newSpa);
        
        // TODO: update the profile with the valid values regardless of the validity of 
        //       the entire profile attributes entity
        
        if (!newSpa.isValid()) {
            throw new InvalidParametersException(newSpa.getInvalidityInfo());
        }
        
        StudentProfile profileToUpdate = getStudentProfileEntity(newSpa.googleId);
        
        if (profileToUpdate == null 
                || JDOHelper.isDeleted(profileToUpdate)) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_STUDENT_PROFILE + newSpa.googleId
                    + ThreadHelper.getCurrentThreadStack());
        }
        
        // return if no changes have been made
        StudentProfileAttributes existingProfile = new StudentProfileAttributes(profileToUpdate);
        newSpa.modifiedDate = existingProfile.modifiedDate;
        if(existingProfile.toString().equals(newSpa.toString())) return;

        newSpa.sanitizeForSaving();
        profileToUpdate.setShortName(newSpa.shortName);
        profileToUpdate.setEmail(newSpa.email);
        profileToUpdate.setInstitute(newSpa.institute);
        profileToUpdate.setNationality(newSpa.nationality);
        profileToUpdate.setGender(newSpa.gender);
        profileToUpdate.setMoreInfo(new Text(newSpa.moreInfo));
        if (!newSpa.pictureKey.isEmpty() 
                && !newSpa.pictureKey.equals(profileToUpdate.getPictureKey().getKeyString())) {
            if (! profileToUpdate.getPictureKey().equals(new BlobKey(""))) {
                deletePicture(profileToUpdate.getPictureKey());
            }
            profileToUpdate.setPictureKey(new BlobKey(newSpa.pictureKey));
        }
        
        closePM();
    }
    
    public void updateStudentProfilePicture(String googleId,
            String newPictureKey) throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newPictureKey);
        Assumption.assertNotEmpty("GoogleId is empty", googleId);
        Assumption.assertNotEmpty("PictureKey is empty", newPictureKey);
        
        StudentProfile profileToUpdate = getStudentProfileEntity(googleId);
        
        if (profileToUpdate == null 
                || JDOHelper.isDeleted(profileToUpdate)) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_STUDENT_PROFILE + googleId
                    + ThreadHelper.getCurrentThreadStack());
        }
        
        boolean newKeyGiven = !newPictureKey.equals(profileToUpdate.getPictureKey().getKeyString());
        
        if (newKeyGiven) {
            if (!profileToUpdate.getPictureKey().equals(new BlobKey(""))) {
                deletePicture(profileToUpdate.getPictureKey());
            }
            profileToUpdate.setPictureKey(new BlobKey(newPictureKey));
        }
        
        closePM();
    }
    
    public void deleteStudentProfilePicture(String googleId) throws BlobstoreFailureException {
        StudentProfile sp = getStudentProfileEntity(googleId);
        if (!sp.getPictureKey().equals(new BlobKey(""))) {
            try {
                deletePicture(sp.getPictureKey());
                sp.setPictureKey(new BlobKey(""));
            } catch (BlobstoreFailureException bfe) {
                // this branch is not tested as it is 
                //      => difficult to reproduce during testing
                //      => properly handled higher up
                throw bfe;
            }
        }
        
        closePM();
    }
}
