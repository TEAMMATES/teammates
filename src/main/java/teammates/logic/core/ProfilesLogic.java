package teammates.logic.core;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.ProfilesDb;

/**
 * Handles the logic related to student profiles.
 */
public final class ProfilesLogic {

    private static final ProfilesLogic instance = new ProfilesLogic();

    private final ProfilesDb profilesDb = ProfilesDb.inst();

    private ProfilesLogic() {
        // prevent initialization
    }

    public static ProfilesLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        // No dependency to other logic class
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
     * Updates/Creates the profile using {@link StudentProfileAttributes.UpdateOptions}.
     *
     * @return updated student profile
     * @throws InvalidParametersException if attributes to update are not valid
     */
    public StudentProfileAttributes updateOrCreateStudentProfile(StudentProfileAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException {
        return profilesDb.updateOrCreateStudentProfile(updateOptions);
    }

    /**
     * Deletes the student profile associated with the {@code googleId}.
     *
     * <p>Fails silently if the student profile doesn't exist.</p>
     */
    public void deleteStudentProfile(String googleId) {
        profilesDb.deleteStudentProfile(googleId);
    }

}
