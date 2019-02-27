package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Instructor;
import teammates.storage.search.InstructorSearchDocument;
import teammates.storage.search.InstructorSearchQuery;
import teammates.storage.search.SearchDocument;

/**
 * Handles CRUD operations for instructors.
 *
 * @see Instructor
 * @see InstructorAttributes
 */
public class InstructorsDb extends EntitiesDb<Instructor, InstructorAttributes> {

    /* =========================================================================
     * Methods related to Google Search API
     * =========================================================================
     */

    public void putDocument(InstructorAttributes instructorParam) {
        InstructorAttributes instructor = instructorParam;
        if (instructor.key == null) {
            instructor = this.getInstructorForEmail(instructor.courseId, instructor.email);
        }
        // defensive coding for legacy data
        if (instructor.key != null) {
            putDocument(Const.SearchIndex.INSTRUCTOR, new InstructorSearchDocument(instructor));
        }
    }

    /**
     * Batch creates or updates documents for the given instructors.
     */
    public void putDocuments(List<InstructorAttributes> instructorParams) {
        List<SearchDocument> instructorDocuments = new ArrayList<>();
        for (InstructorAttributes instructor : instructorParams) {
            InstructorAttributes inst = instructor.key == null
                    ? getInstructorForEmail(instructor.courseId, instructor.email)
                    : instructor;
            // defensive coding for legacy data
            if (inst.key != null) {
                instructorDocuments.add(new InstructorSearchDocument(inst));
            }
        }
        putDocuments(Const.SearchIndex.INSTRUCTOR, instructorDocuments);
    }

    /**
     * Removes search document for the given instructor by using {@code encryptedRegistrationKey}.
     *
     * <p>See {@link InstructorSearchDocument#toDocument()} for more details.</p>
     */
    public void deleteDocumentByEncryptedInstructorKey(String encryptedRegistrationKey) {
        deleteDocument(Const.SearchIndex.INSTRUCTOR, encryptedRegistrationKey);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search instructors in the whole system.
     * @return null if no result found
     */

    public InstructorSearchResultBundle searchInstructorsInWholeSystem(String queryString) {

        if (queryString.trim().isEmpty()) {
            return new InstructorSearchResultBundle();
        }

        Results<ScoredDocument> results = searchDocuments(Const.SearchIndex.INSTRUCTOR,
                                                          new InstructorSearchQuery(queryString));

        return InstructorSearchDocument.fromResults(results);
    }

    public InstructorAttributes createInstructor(InstructorAttributes instructorToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        Instructor instructor = createEntity(instructorToAdd);
        if (instructor == null) {
            throw new InvalidParametersException("Created instructor is null.");
        }
        InstructorAttributes createdInstructor = makeAttributes(instructor);
        putDocument(createdInstructor);
        return createdInstructor;
    }

    /**
     * Returns null if no matching objects.
     */
    public InstructorAttributes getInstructorForEmail(String courseId, String email) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributesOrNull(getInstructorEntityForEmail(courseId, email),
                "Trying to get non-existent Instructor: " + courseId + "/" + email);
    }

    /**
     * Returns null if no matching objects.
     */
    public InstructorAttributes getInstructorById(String courseId, String email) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributesOrNull(getInstructorEntityById(courseId, email),
                "Trying to get non-existent Instructor: " + courseId + "/" + email);
    }

    /**
     * Returns null if no matching objects.
     */
    public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributesOrNull(getInstructorEntityForGoogleId(courseId, googleId),
                "Trying to get non-existent Instructor: " + googleId);
    }

    /**
     * Returns null if no matching instructor.
     */
    public InstructorAttributes getInstructorForRegistrationKey(String encryptedKey) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, encryptedKey);

        String decryptedKey;
        try {
            decryptedKey = StringHelper.decrypt(encryptedKey.trim());
        } catch (InvalidParametersException e) {
            return null;
        }

        return makeAttributesOrNull(getInstructorEntityForRegistrationKey(decryptedKey));
    }

    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     *
     * @return empty list if no matching objects.
     */
    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId, boolean omitArchived) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);

        return makeAttributes(getInstructorEntitiesForGoogleId(googleId, omitArchived));
    }

    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return empty list if no matching objects.
     */
    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getInstructorEntitiesForCourse(courseId));
    }

    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return empty list if no matching objects.
     */
    public List<InstructorAttributes> getInstructorsDisplayedToStudents(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

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
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updateOptions);

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

        instructor.setName(newAttributes.name);
        instructor.setEmail(newAttributes.email);
        instructor.setIsArchived(newAttributes.isArchived);
        instructor.setRole(newAttributes.role);
        instructor.setIsDisplayedToStudents(newAttributes.isDisplayedToStudents);
        instructor.setDisplayedName(newAttributes.displayedName);
        instructor.setInstructorPrivilegeAsText(newAttributes.getTextFromInstructorPrivileges());

        saveEntity(instructor, newAttributes);

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
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updateOptions);

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

        instructor.setGoogleId(newAttributes.googleId);
        instructor.setName(newAttributes.name);
        instructor.setIsArchived(newAttributes.isArchived);
        instructor.setRole(newAttributes.role);
        instructor.setIsDisplayedToStudents(newAttributes.isDisplayedToStudents);
        instructor.setDisplayedName(newAttributes.displayedName);
        instructor.setInstructorPrivilegeAsText(newAttributes.getTextFromInstructorPrivileges());

        saveEntity(instructor, newAttributes);

        newAttributes = makeAttributes(instructor);
        putDocument(newAttributes);

        return newAttributes;
    }

    /**
     * Deletes the instructor specified by courseId and email.
     */
    public void deleteInstructor(String courseId, String email) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        Instructor instructorToDelete = getInstructorEntityForEmail(courseId, email);

        if (instructorToDelete == null) {
            return;
        }

        InstructorAttributes instructorToDeleteAttributes = makeAttributes(instructorToDelete);

        deleteDocumentByEncryptedInstructorKey(StringHelper.encrypt(instructorToDelete.getRegistrationKey()));
        deleteEntityDirect(instructorToDelete, instructorToDeleteAttributes);

        Instructor instructorCheck = getInstructorEntityForEmail(courseId, email);
        if (instructorCheck != null) {
            putDocument(makeAttributes(instructorCheck));
        }

        //TODO: reuse the method in the parent class instead
    }

    public void deleteInstructorsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

        deleteInstructors(getInstructorEntitiesForCourses(courseIds));
    }

    /**
     * Deletes all instructors with the given googleId.
     */
    public void deleteInstructorsForGoogleId(String googleId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);

        deleteInstructors(getInstructorEntitiesForGoogleId(googleId));
    }

    /**
     * Deletes all instructors for the course specified by courseId.
     */
    public void deleteInstructorsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        deleteInstructors(getInstructorEntitiesForCourse(courseId));
    }

    private void deleteInstructors(List<Instructor> instructors) {
        for (Instructor instructor : instructors) {
            deleteDocumentByEncryptedInstructorKey(StringHelper.encrypt(instructor.getRegistrationKey()));
        }
        ofy().delete().entities(instructors).now();
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
        return load().id(email + '%' + courseId).now();
    }

    private List<Instructor> getInstructorEntitiesForCourses(List<String> courseIds) {
        return load().filter("courseId in", courseIds).list();
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
                    .filter("isArchived !=", true)
                    .list();
        }
        return getInstructorEntitiesForGoogleId(googleId);
    }

    private List<Instructor> getInstructorEntitiesForCourse(String courseId) {
        return load().filter("courseId =", courseId).list();
    }

    @Override
    protected LoadType<Instructor> load() {
        return ofy().load().type(Instructor.class);
    }

    @Override
    protected Instructor getEntity(InstructorAttributes instructorToGet) {
        return getInstructorEntityForEmail(instructorToGet.courseId, instructorToGet.email);
    }

    @Override
    protected QueryKeys<Instructor> getEntityQueryKeys(InstructorAttributes attributes) {
        return load()
                .filter("courseId =", attributes.courseId)
                .filter("email =", attributes.email)
                .keys();
    }

    @Override
    protected InstructorAttributes makeAttributes(Instructor entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return InstructorAttributes.valueOf(entity);
    }

}
