package teammates.logic.core;

import com.google.appengine.api.blobstore.BlobKey;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.ProfilesDb;

/**
 * Handles the logic related to student profiles.
 */
public final class ProfilesLogic {

    private static ProfilesLogic instance = new ProfilesLogic();

    private static final ProfilesDb profilesDb = new ProfilesDb();

    private ProfilesLogic() {
        // prevent initialization
    }

    public static ProfilesLogic inst() {
        return instance;
    }

    /**
     * Gets student profile associated with the {@code googleId}.
     *
     * @return null if no match found.
     */
    public StudentProfileAttributes getStudentProfile(String googleId) {
        return profilesDb.getStudentProfile(googleId);
    }

    /**
     * Updates/Creates student profile based on the given new profile attributes.
     *
     * @throws InvalidParametersException if attributes in {@code newStudentProfileAttributes} are not valid
     */
    public void updateOrCreateStudentProfile(StudentProfileAttributes newStudentProfileAttributes)
            throws InvalidParametersException {
        profilesDb.updateOrCreateStudentProfile(newStudentProfileAttributes);
    }

    /**
     * Deletes the student profile associated with the {@code googleId}.
     *
     * <p>Fails silently if the student profile doesn't exist.</p>
     */
    public void deleteStudentProfile(String googleId) {
        profilesDb.deleteStudentProfile(googleId);
    }

    /**
     * Deletes picture associated with the {@code key}.
     *
     * <p>Fails silently if the {@code key} doesn't exist.</p>
     */
    public void deletePicture(BlobKey key) {
        profilesDb.deletePicture(key);
    }

    /**
     * Updates {@code pictureKey} for the student profile associated with {@code googleId}.
     *
     * <p>If the associated profile doesn't exist, create a new one.</p>
     */
    public void updateStudentProfilePicture(String googleId, String newPictureKey) {
        profilesDb.updateStudentProfilePicture(googleId, newPictureKey);
    }

}
