package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
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
     * Updates/Creates the profile using {@link StudentProfileAttributes.UpdateOptions}.
     *
     * @return updated student profile
     * @throws InvalidParametersException if attributes to update are not valid
     */
    public StudentProfileAttributes updateOrCreateStudentProfile(StudentProfileAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException {
        Assumption.assertNotNull(updateOptions);

        StudentProfile studentProfile = getStudentProfileEntityFromDb(updateOptions.getGoogleId());
        boolean shouldCreateEntity = studentProfile == null; // NOPMD
        if (studentProfile == null) {
            studentProfile = new StudentProfile(updateOptions.getGoogleId());
        }

        StudentProfileAttributes newAttributes = makeAttributes(studentProfile);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes =
                this.<String>hasSameValue(studentProfile.getEmail(), newAttributes.getEmail())
                && this.<String>hasSameValue(studentProfile.getShortName(), newAttributes.getShortName())
                && this.<String>hasSameValue(studentProfile.getInstitute(), newAttributes.getInstitute())
                && this.<String>hasSameValue(studentProfile.getNationality(), newAttributes.getNationality())
                && this.<String>hasSameValue(studentProfile.getGender(), newAttributes.getGender().name().toLowerCase())
                && this.<String>hasSameValue(studentProfile.getMoreInfo(), newAttributes.getMoreInfo());
        if (!shouldCreateEntity && hasSameAttributes) {
            log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, StudentProfile.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        studentProfile.setShortName(newAttributes.shortName);
        studentProfile.setEmail(newAttributes.email);
        studentProfile.setInstitute(newAttributes.institute);
        studentProfile.setNationality(newAttributes.nationality);
        studentProfile.setGender(newAttributes.gender.name().toLowerCase());
        studentProfile.setMoreInfo(newAttributes.moreInfo);
        studentProfile.setModifiedDate(Instant.now());

        saveEntity(studentProfile);

        return makeAttributes(studentProfile);
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
        Key<Account> parentKey = Key.create(Account.class, googleId);
        Key<StudentProfile> profileKey = Key.create(parentKey, StudentProfile.class, googleId);
        deleteEntity(profileKey);
    }

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
    LoadType<StudentProfile> load() {
        return ofy().load().type(StudentProfile.class);
    }

    @Override
    boolean hasExistingEntities(StudentProfileAttributes entityToCreate) {
        Key<Account> parentKey = Key.create(Account.class, entityToCreate.getGoogleId());
        Key<StudentProfile> childKey = Key.create(parentKey, StudentProfile.class, entityToCreate.getGoogleId());
        return !load().filterKey(childKey).keys().list().isEmpty();
    }

    @Override
    StudentProfileAttributes makeAttributes(StudentProfile entity) {
        Assumption.assertNotNull(entity);

        return StudentProfileAttributes.valueOf(entity);
    }
}
