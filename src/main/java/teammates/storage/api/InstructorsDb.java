package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.storage.entity.Instructor;
import teammates.storage.search.InstructorSearchManager;
import teammates.storage.search.SearchManagerFactory;

/**
 * Handles CRUD operations for instructors.
 *
 * @see Instructor
 * @see InstructorAttributes
 */
public final class InstructorsDb extends EntitiesDb<Instructor, InstructorAttributes> {

    private static final int MAX_KEY_REGENERATION_TRIES = 10;

    private static final InstructorsDb instance = new InstructorsDb();

    private InstructorsDb() {
        // prevent initialization
    }

    public static InstructorsDb inst() {
        return instance;
    }

    private InstructorSearchManager getSearchManager() {
        return SearchManagerFactory.getInstructorSearchManager();
    }

    /**
     * Creates or updates search document for the given instructor.
     */
    public void putDocument(InstructorAttributes instructor) throws SearchServiceException {
        getSearchManager().putDocument(instructor);
    }

    /**
     * Removes search document for the given instructor by using {@code instructorUniqueId}.
     */
    public void deleteDocumentByInstructorId(String instructorUniqueId) {
        getSearchManager().deleteDocuments(Collections.singletonList(instructorUniqueId));
    }

    /**
     * Regenerates the registration key of an instructor in a course.
     *
     * @return the updated instructor
     * @throws EntityAlreadyExistsException if a new registration key could not be generated
     */
    public InstructorAttributes regenerateEntityKey(InstructorAttributes originalInstructor)
            throws EntityAlreadyExistsException {
        int numTries = 0;
        while (numTries < MAX_KEY_REGENERATION_TRIES) {
            Instructor updatedEntity = convertToEntityForSaving(originalInstructor);
            if (!updatedEntity.getRegistrationKey().equals(originalInstructor.getKey())) {
                saveEntity(updatedEntity);
                return makeAttributes(updatedEntity);
            }
            numTries++;
        }
        log.severe("Failed to generate new registration key for instructor after " + MAX_KEY_REGENERATION_TRIES + " tries");
        throw new EntityAlreadyExistsException("Could not regenerate a new course registration key for the instructor.");
    }

    /**
     * Searches all instructors in the system.
     *
     * <p>This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search instructors in the whole system.
     */
    public List<InstructorAttributes> searchInstructorsInWholeSystem(String queryString)
            throws SearchServiceException {

        if (queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return getSearchManager().searchInstructors(queryString);
    }

    /**
     * Checks if the given instructors exist in the given course.
     */
    public boolean hasExistingInstructorsInCourse(String courseId, Collection<String> instructorEmailAddresses) {
        if (instructorEmailAddresses.isEmpty()) {
            return true;
        }
        Set<String> existingInstructorEmailAddresses = load().filter("courseId =", courseId)
                .project("email")
                .list()
                .stream()
                .map(Instructor::getEmail)
                .collect(Collectors.toSet());
        return existingInstructorEmailAddresses.containsAll(instructorEmailAddresses);
    }

    /**
     * Gets an instructor by unique constraint courseId-email.
     */
    public InstructorAttributes getInstructorForEmail(String courseId, String email) {
        assert email != null;
        assert courseId != null;

        return makeAttributesOrNull(getInstructorEntityForEmail(courseId, email));
    }

    /**
     * Gets an instructor by unique ID.
     */
    public InstructorAttributes getInstructorById(String courseId, String email) {
        assert email != null;
        assert courseId != null;

        return makeAttributesOrNull(getInstructorEntityById(courseId, email));
    }

    /**
     * Gets an instructor by unique constraint courseId-googleId.
     */
    public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {
        assert googleId != null;
        assert courseId != null;

        return makeAttributesOrNull(getInstructorEntityForGoogleId(courseId, googleId));
    }

    /**
     * Gets an instructor by unique constraint registrationKey.
     */
    public InstructorAttributes getInstructorForRegistrationKey(String registrationKey) {
        assert registrationKey != null;

        return makeAttributesOrNull(getInstructorEntityForRegistrationKey(registrationKey.trim()));
    }

    /**
     * Gets all instructors associated with a googleId.
     *
     * @param omitArchived whether archived instructors should be omitted or not
     */
    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId, boolean omitArchived) {
        assert googleId != null;

        return makeAttributes(getInstructorEntitiesForGoogleId(googleId, omitArchived));
    }

    /**
     * Gets the emails of all instructors of a course.
     */
    public List<String> getInstructorEmailsForCourse(String courseId) {
        assert courseId != null;

        return load()
                .filter("courseId =", courseId)
                .list()
                .stream()
                .map(Instructor::getEmail)
                .collect(Collectors.toList());
    }

    /**
     * Gets all instructors of a course.
     */
    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
        assert courseId != null;

        return makeAttributes(getInstructorEntitiesForCourse(courseId));
    }

    /**
     * Gets all instructors that will be displayed to students of a course.
     */
    public List<InstructorAttributes> getInstructorsDisplayedToStudents(String courseId) {
        assert courseId != null;

        return makeAttributes(getInstructorEntitiesThatAreDisplayedInCourse(courseId));
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithGoogleId}.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructorByGoogleId(InstructorAttributes.UpdateOptionsWithGoogleId updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        Instructor instructor = getInstructorEntityForGoogleId(updateOptions.getCourseId(), updateOptions.getGoogleId());
        if (instructor == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        InstructorAttributes newAttributes = makeAttributes(instructor);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes =
                this.<String>hasSameValue(instructor.getName(), newAttributes.getName())
                && this.<String>hasSameValue(instructor.getEmail(), newAttributes.getEmail())
                && this.<Boolean>hasSameValue(instructor.getIsArchived(), newAttributes.isArchived())
                && this.<String>hasSameValue(instructor.getRole(), newAttributes.getRole())
                && this.<Boolean>hasSameValue(instructor.isDisplayedToStudents(), newAttributes.isDisplayedToStudents())
                && this.<String>hasSameValue(instructor.getDisplayedName(), newAttributes.getDisplayedName())
                && this.<String>hasSameValue(
                        instructor.getInstructorPrivilegesAsText(), newAttributes.getInstructorPrivilegesAsText());
        if (hasSameAttributes) {
            log.info(String.format(
                    OPTIMIZED_SAVING_POLICY_APPLIED, Instructor.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        instructor.setName(newAttributes.getName());
        instructor.setEmail(newAttributes.getEmail());
        instructor.setIsArchived(newAttributes.isArchived());
        instructor.setRole(newAttributes.getRole());
        instructor.setIsDisplayedToStudents(newAttributes.isDisplayedToStudents());
        instructor.setDisplayedName(newAttributes.getDisplayedName());
        instructor.setInstructorPrivilegeAsText(newAttributes.getInstructorPrivilegesAsText());

        saveEntity(instructor);

        newAttributes = makeAttributes(instructor);

        return newAttributes;
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithEmail}.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructorByEmail(InstructorAttributes.UpdateOptionsWithEmail updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        Instructor instructor = getInstructorEntityForEmail(updateOptions.getCourseId(), updateOptions.getEmail());
        if (instructor == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        InstructorAttributes newAttributes = makeAttributes(instructor);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes =
                this.<String>hasSameValue(instructor.getName(), newAttributes.getName())
                && this.<String>hasSameValue(instructor.getGoogleId(), newAttributes.getGoogleId())
                && this.<Boolean>hasSameValue(instructor.getIsArchived(), newAttributes.isArchived())
                && this.<String>hasSameValue(instructor.getRole(), newAttributes.getRole())
                && this.<Boolean>hasSameValue(instructor.isDisplayedToStudents(), newAttributes.isDisplayedToStudents())
                && this.<String>hasSameValue(instructor.getDisplayedName(), newAttributes.getDisplayedName())
                && this.<String>hasSameValue(
                        instructor.getInstructorPrivilegesAsText(), newAttributes.getInstructorPrivilegesAsText());
        if (hasSameAttributes) {
            log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, Instructor.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        instructor.setGoogleId(newAttributes.getGoogleId());
        instructor.setName(newAttributes.getName());
        instructor.setIsArchived(newAttributes.isArchived());
        instructor.setRole(newAttributes.getRole());
        instructor.setIsDisplayedToStudents(newAttributes.isDisplayedToStudents());
        instructor.setDisplayedName(newAttributes.getDisplayedName());
        instructor.setInstructorPrivilegeAsText(newAttributes.getInstructorPrivilegesAsText());

        saveEntity(instructor);

        newAttributes = makeAttributes(instructor);

        return newAttributes;
    }

    /**
     * Deletes the instructor specified by courseId and email.
     *
     * <p>Fails silently if the student does not exist.
     */
    public void deleteInstructor(String courseId, String email) {
        assert email != null;
        assert courseId != null;

        Instructor instructorToDelete = getInstructorEntityForEmail(courseId, email);

        if (instructorToDelete == null) {
            return;
        }

        deleteDocumentByInstructorId(instructorToDelete.getUniqueId());

        deleteEntity(Key.create(Instructor.class, instructorToDelete.getUniqueId()));
    }

    /**
     * Deletes instructors using {@link AttributesDeletionQuery}.
     */
    public void deleteInstructors(AttributesDeletionQuery query) {
        assert query != null;

        if (query.isCourseIdPresent()) {
            List<Instructor> instructorsToDelete = load().filter("courseId =", query.getCourseId()).list();
            getSearchManager().deleteDocuments(
                    instructorsToDelete.stream()
                            .map(Instructor::getUniqueId)
                            .collect(Collectors.toList()));

            deleteEntity(instructorsToDelete.stream()
                    .map(s -> Key.create(Instructor.class, s.getUniqueId()))
                    .collect(Collectors.toList()));
        }
    }

    private Instructor getInstructorEntityForGoogleId(String courseId, String googleId) {
        return getInstructorsForGoogleIdQuery(googleId)
                .filter("courseId =", courseId)
                .first().now();
    }

    private Instructor getInstructorEntityForEmail(String courseId, String email) {
        return load()
                .filter("courseId =", courseId)
                .filter("email =", email)
                .first().now();
    }

    private Instructor getInstructorEntityById(String courseId, String email) {
        return load().id(Instructor.generateId(email, courseId)).now();
    }

    private List<Instructor> getInstructorEntitiesThatAreDisplayedInCourse(String courseId) {
        return load()
                .filter("courseId =", courseId)
                .filter("isDisplayedToStudents =", true)
                .list();
    }

    private Instructor getInstructorEntityForRegistrationKey(String key) {
        List<Instructor> instructorList = load().filter("registrationKey =", key).list();

        // If registration key detected is not unique, something is wrong
        if (instructorList.size() > 1) {
            log.severe("Duplicate registration keys detected for: "
                    + instructorList.stream().map(i -> i.getUniqueId()).collect(Collectors.joining(", ")));
        }

        if (instructorList.isEmpty()) {
            return null;
        }

        return instructorList.get(0);
    }

    /**
     * Returns true if there are any instructor entities associated with the googleId.
     */
    public boolean hasInstructorsForGoogleId(String googleId) {
        return !getInstructorsForGoogleIdQuery(googleId).keys().list().isEmpty();
    }

    private Query<Instructor> getInstructorsForGoogleIdQuery(String googleId) {
        return load().filter("googleId =", googleId);
    }

    private List<Instructor> getInstructorEntitiesForGoogleId(String googleId) {
        return getInstructorsForGoogleIdQuery(googleId).list();
    }

    /**
     * Omits instructors with isArchived == omitArchived.
     * This means that the corresponding course is archived by the instructor.
     */
    private List<Instructor> getInstructorEntitiesForGoogleId(String googleId, boolean omitArchived) {
        if (omitArchived) {
            return getInstructorsForGoogleIdQuery(googleId)
                    .filter("isArchived =", false)
                    .list();
        }
        return getInstructorEntitiesForGoogleId(googleId);
    }

    private List<Instructor> getInstructorEntitiesForCourse(String courseId) {
        return load().filter("courseId =", courseId).list();
    }

    @Override
    LoadType<Instructor> load() {
        return ofy().load().type(Instructor.class);
    }

    @Override
    boolean hasExistingEntities(InstructorAttributes entityToCreate) {
        // cannot use direct key query as email of an instructor can be changed
        return !load()
                .filter("courseId =", entityToCreate.getCourseId())
                .filter("email =", entityToCreate.getEmail())
                .keys()
                .list()
                .isEmpty();
    }

    @Override
    InstructorAttributes makeAttributes(Instructor entity) {
        assert entity != null;

        return InstructorAttributes.valueOf(entity);
    }

    @Override
    Instructor convertToEntityForSaving(InstructorAttributes attributes) throws EntityAlreadyExistsException {
        int numTries = 0;
        while (numTries < MAX_KEY_REGENERATION_TRIES) {
            Instructor instructor = attributes.toEntity();
            Key<Instructor> existingInstructor =
                    load().filter("registrationKey =", instructor.getRegistrationKey()).keys().first().now();
            if (existingInstructor == null) {
                return instructor;
            }
            numTries++;
        }
        log.severe("Failed to generate new registration key for instructor after "
                + MAX_KEY_REGENERATION_TRIES + " tries");
        throw new EntityAlreadyExistsException("Unable to create new instructor");
    }

    /**
     * Gets the number of instructors created within a specified time range.
     */
    public int getNumInstructorsByTimeRange(Instant startTime, Instant endTime) {
        return load()
                .filter("createdAt >=", startTime)
                .filter("createdAt <", endTime)
                .count();
    }

}
