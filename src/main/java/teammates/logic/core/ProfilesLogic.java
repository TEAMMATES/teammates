package teammates.logic.core;

import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.ProfilesDb;

import com.google.appengine.api.blobstore.BlobKey;

/**
 * Handles the logic related to student profiles.
 */
public class ProfilesLogic {
    
    private static ProfilesLogic instance;
    
    private static final ProfilesDb profilesDb = new ProfilesDb();
    
    public static ProfilesLogic inst() {
        if (instance == null) {
            instance = new ProfilesLogic();
        }
        return instance;
    }
    
    public StudentProfileAttributes getStudentProfile(String googleId) {
        return profilesDb.getStudentProfile(googleId);
    }
    
    public void updateStudentProfile(StudentProfileAttributes newStudentProfileAttributes)
            throws InvalidParametersException, EntityDoesNotExistException {
        profilesDb.updateStudentProfile(newStudentProfileAttributes);
    }
    
    public void deleteStudentProfilePicture(String googleId) throws EntityDoesNotExistException {
        profilesDb.deleteStudentProfilePicture(googleId);
    }
    
    public void deletePicture(BlobKey key) {
        profilesDb.deletePicture(key);
    }
    
    public void updateStudentProfilePicture(String googleId, String newPictureKey) throws EntityDoesNotExistException {
        profilesDb.updateStudentProfilePicture(googleId, newPictureKey);
    }
    
}
