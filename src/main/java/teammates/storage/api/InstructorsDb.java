package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchNotImplementedException;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Instructor;
import teammates.storage.search.InstructorSearchManager;
import teammates.storage.search.SearchManagerFactory;

/**
 * Handles CRUD operations for instructors.
 *
 * @see Instructor
 * @see InstructorAttributes
 */
public class InstructorsDb extends EntitiesDb<Instructor, InstructorAttributes> {

    private InstructorSearchManager getSearchManager() {
        return SearchManagerFactory.getInstructorSearchManager();
    }

    /**
     * Creates or updates search document for the given instructor.
     */
    public void putDocument(InstructorAttributes instructorParam) {
        InstructorAttributes instructor = instructorParam;
        if (instructor.key == null) {
            instructor = this.getInstructorForEmail(instructor.courseId, instructor.email);
        }
        getSearchManager().putDocuments(Collections.singletonList(instructor));
    }

    /**
     * Batch creates or updates search documents for the given instructors.
     */
    public void putDocuments(List<InstructorAttributes> instructorParams) {
        List<InstructorAttributes> instructors = instructorParams.stream()
                .map(instructor -> instructor.key == null
                        ? getInstructorForEmail(instructor.courseId, instructor.email)
                        : instructor)
                .collect(Collectors.toList());

        getSearchManager().putDocuments(instructors);
    }

    /**
     * Removes search document for the given instructor by using {@code instructorUniqueId}.
     */
    public void deleteDocumentByInstructorId(String instructorUniqueId) {
        getSearchManager().deleteDocuments(Collections.singletonList(instructorUniqueId));
    }

    /**
     * Searches all instructors in the system.
     *
     * <p>This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search instructors in the whole system.
     */
    public List<InstructorAttributes> searchInstructorsInWholeSystem(String queryString)
            throws SearchNotImplementedException {

        if (queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return getSearchManager().searchInstructors(queryString);
    }

    /**
     * Creates an instructor.
     *
     * @return the created instructor
     * @throws InvalidParametersException if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists in the Datastore
     */
    @Override
    public InstructorAttributes createEntity(InstructorAttributes instructorToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        InstructorAttributes createdInstructor = super.createEntity(instructorToAdd);
        putDocument(createdInstructor);

        return createdInstructor;
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
     * Gets an instructor by unique constraint encryptedKey.
     */
    public InstructorAttributes getInstructorForRegistrationKey(String encryptedKey) {
        assert encryptedKey != null;

        String decryptedKey;
        try {
            decryptedKey = StringHelper.decrypt(encryptedKey.trim());
        } catch (InvalidParametersException e) {
            return null;
        }

        return makeAttributesOrNull(getInstructorEntityForRegistrationKey(decryptedKey));
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
                        instructor.getInstructorPrivilegesAsText(), newAttributes.getTextFromInstructorPrivileges());
        if (hasSameAttributes) {
            log.info(String.format(
                    OPTIMIZED_SAVING_POLICY_APPLIED, Instructor.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        instructor.setName(newAttributes.name);
        instructor.setEmail(newAttributes.email);
        instructor.setIsArchived(newAttributes.isArchived);
        instructor.setRole(newAttributes.role);
        instructor.setIsDisplayedToStudents(newAttributes.isDisplayedToStudents);
        instructor.setDisplayedName(newAttributes.displayedName);
        instructor.setInstructorPrivilegeAsText(newAttributes.getTextFromInstructorPrivileges());

        saveEntity(instructor);

        newAttributes = makeAttributes(instructor);
        putDocument(newAttributes);

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
                        instructor.getInstructorPrivilegesAsText(), newAttributes.getTextFromInstructorPrivileges());
        if (hasSameAttributes) {
            log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, Instructor.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        instructor.setGoogleId(newAttributes.googleId);
        instructor.setName(newAttributes.name);
        instructor.setIsArchived(newAttributes.isArchived);
        instructor.setRole(newAttributes.role);
        instructor.setIsDisplayedToStudents(newAttributes.isDisplayedToStudents);
        instructor.setDisplayedName(newAttributes.displayedName);
        instructor.setInstructorPrivilegeAsText(newAttributes.getTextFromInstructorPrivileges());

        saveEntity(instructor);

        newAttributes = makeAttributes(instructor);
        putDocument(newAttributes);

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
        return load()
                .filter("courseId =", courseId)
                .filter("googleId =", googleId)
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
        return load().filter("registrationKey =", key).first().now();
    }

    private List<Instructor> getInstructorEntitiesForGoogleId(String googleId) {
        return load().filter("googleId =", googleId).list();
    }

    /**
     * Omits instructors with isArchived == omitArchived.
     * This means that the corresponding course is archived by the instructor.
     */
    private List<Instructor> getInstructorEntitiesForGoogleId(String googleId, boolean omitArchived) {
        if (omitArchived) {
            return load()
                    .filter("googleId =", googleId)
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

}
