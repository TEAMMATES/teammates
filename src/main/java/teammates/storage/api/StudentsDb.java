package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.RegenerateStudentException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.storage.entity.CourseStudent;
import teammates.storage.search.SearchDocument;
import teammates.storage.search.StudentSearchDocument;
import teammates.storage.search.StudentSearchQuery;

/**
 * Handles CRUD operations for students.
 *
 * @see CourseStudent
 * @see StudentAttributes
 */
public class StudentsDb extends EntitiesDb<CourseStudent, StudentAttributes> {

    private static final Logger log = Logger.getLogger();

    private static final int MAX_KEY_REGENERATION_TRIES = 5;

    /**
     * Creates or updates search document for the given student.
     */
    public void putDocument(StudentAttributes student) {
        putDocument(Const.SearchIndex.STUDENT, new StudentSearchDocument(student));
    }

    /**
     * Batch creates or updates search documents for the given students.
     */
    public void putDocuments(List<StudentAttributes> students) {
        List<SearchDocument> studentDocuments = new ArrayList<>();
        for (StudentAttributes student : students) {
            studentDocuments.add(new StudentSearchDocument(student));
        }
        putDocument(Const.SearchIndex.STUDENT, studentDocuments.toArray(new SearchDocument[0]));
    }

    /**
     * Searches for students.
     *
     * @param instructors the constraint that restricts the search result
     */
    public StudentSearchResultBundle search(String queryString, List<InstructorAttributes> instructors) {
        if (queryString.trim().isEmpty()) {
            return new StudentSearchResultBundle();
        }

        Results<ScoredDocument> results = searchDocuments(Const.SearchIndex.STUDENT,
                new StudentSearchQuery(instructors, queryString));

        return StudentSearchDocument.fromResults(results, instructors);
    }

    /**
     * Searches all students in the system.
     *
     * <p>This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search instructors in the whole system.
     */
    public StudentSearchResultBundle searchStudentsInWholeSystem(String queryString) {
        if (queryString.trim().isEmpty()) {
            return new StudentSearchResultBundle();
        }

        Results<ScoredDocument> results = searchDocuments(Const.SearchIndex.STUDENT,
                new StudentSearchQuery(queryString));

        return StudentSearchDocument.fromResults(results);
    }

    /**
     * Removes search document for the given student by using {@code unencryptedRegistrationKey}.
     *
     * <p>See {@link StudentSearchDocument#toDocument()} for more details.</p>
     */
    public void deleteDocumentByStudentKey(String unencryptedRegistrationKey) {
        deleteDocument(Const.SearchIndex.STUDENT, unencryptedRegistrationKey);
    }

    /**
     * Creates a student.
     *
     * @return the created student
     * @throws InvalidParametersException if the student is not valid
     * @throws EntityAlreadyExistsException if the student already exists in the Datastore
     */
    @Override
    public StudentAttributes createEntity(StudentAttributes student)
            throws InvalidParametersException, EntityAlreadyExistsException {

        StudentAttributes createdStudent = super.createEntity(student);
        putDocument(createdStudent);

        return createdStudent;
    }

    /**
     * Regenerates the registration key of a student in a course.
     *
     * @return the updated student
     * @throws RegenerateStudentException if a new registration key could not be generated
     */
    public StudentAttributes regenerateEntityKey(StudentAttributes originalStudent) throws RegenerateStudentException {
        int numTries = 0;

        while (numTries < MAX_KEY_REGENERATION_TRIES) {
            CourseStudent updatedEntity = originalStudent.toEntity();

            if (!updatedEntity.getRegistrationKey().equals(originalStudent.getKey())) {
                saveEntity(updatedEntity);

                StudentAttributes updatedStudent = makeAttributes(updatedEntity);
                putDocument(updatedStudent);

                return updatedStudent;
            }

            numTries++;
        }

        throw new RegenerateStudentException("Could not regenerate a new course registration key for the student.");
    }

    /**
     * Gets a student by unique ID courseId-email.
     */
    public StudentAttributes getStudentForEmail(String courseId, String email) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(email);

        return makeAttributesOrNull(getCourseStudentEntityForEmail(courseId, email));
    }

    /**
     * Gets list of students by email.
     */
    public List<StudentAttributes> getAllStudentsForEmail(String email) {
        Assumption.assertNotNull(email);

        List<CourseStudent> students = getAllCourseStudentEntitiesForEmail(email);
        return students.stream().map(this::makeAttributes).collect(Collectors.toList());
    }

    /**
     * Gets a student by unique constraint courseId-googleId.
     */
    public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
        Assumption.assertNotNull(googleId);
        Assumption.assertNotNull(courseId);

        CourseStudent student = load()
                .filter("courseId =", courseId)
                .filter("googleId =", googleId)
                .first().now();

        return makeAttributesOrNull(student);
    }

    /**
     * Gets a student by unique constraint encryptedKey.
     */
    public StudentAttributes getStudentForRegistrationKey(String encryptedRegistrationKey) {
        Assumption.assertNotNull(encryptedRegistrationKey);

        try {
            String decryptedKey = StringHelper.decrypt(encryptedRegistrationKey.trim());
            return makeAttributesOrNull(getCourseStudentEntityForRegistrationKey(decryptedKey));
        } catch (InvalidParametersException e) {
            return null; // invalid registration key cannot be decrypted
        }
    }

    /**
     * Gets all students associated with a googleId.
     */
    public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
        Assumption.assertNotNull(googleId);

        return makeAttributes(getCourseStudentEntitiesForGoogleId(googleId));
    }

    /**
     * Gets all students of a course.
     */
    public List<StudentAttributes> getStudentsForCourse(String courseId) {
        Assumption.assertNotNull(courseId);

        return makeAttributes(getCourseStudentEntitiesForCourse(courseId));
    }

    /**
     * Gets all students of a team of a course.
     */
    public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
        Assumption.assertNotNull(teamName);
        Assumption.assertNotNull(courseId);

        return makeAttributes(getCourseStudentEntitiesForTeam(teamName, courseId));
    }

    /**
     * Gets all unregistered students of a course.
     */
    public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
        Assumption.assertNotNull(courseId);

        List<StudentAttributes> allStudents = getStudentsForCourse(courseId);
        List<StudentAttributes> unregistered = new ArrayList<>();

        for (StudentAttributes s : allStudents) {
            if (s.googleId == null || s.googleId.trim().isEmpty()) {
                unregistered.add(s);
            }
        }
        return unregistered;
    }

    /**
     * Updates a student by {@link StudentAttributes.UpdateOptions}.
     *
     * <p>If the student's email is changed, the student is re-created.
     *
     * @return updated student
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the student cannot be found
     * @throws EntityAlreadyExistsException if the student cannot be updated
     *         by recreation because of an existent student
     */
    public StudentAttributes updateStudent(StudentAttributes.UpdateOptions updateOptions)
            throws EntityDoesNotExistException, InvalidParametersException, EntityAlreadyExistsException {
        Assumption.assertNotNull(updateOptions);

        CourseStudent student = getCourseStudentEntityForEmail(updateOptions.getCourseId(), updateOptions.getEmail());
        if (student == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        StudentAttributes newAttributes = makeAttributes(student);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        boolean isEmailChanged = !student.getEmail().equals(newAttributes.email);

        if (isEmailChanged) {
            newAttributes = createEntity(newAttributes);
            // delete the old student
            deleteStudent(student.getCourseId(), student.getEmail());

            putDocument(newAttributes);
            return newAttributes;
        } else {
            // update only if change
            boolean hasSameAttributes =
                    this.<String>hasSameValue(student.getName(), newAttributes.getName())
                    && this.<String>hasSameValue(student.getLastName(), newAttributes.getLastName())
                    && this.<String>hasSameValue(student.getComments(), newAttributes.getComments())
                    && this.<String>hasSameValue(student.getGoogleId(), newAttributes.getGoogleId())
                    && this.<String>hasSameValue(student.getTeamName(), newAttributes.getTeam())
                    && this.<String>hasSameValue(student.getSectionName(), newAttributes.getSection());
            if (hasSameAttributes) {
                log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, CourseStudent.class.getSimpleName(), updateOptions));
                return newAttributes;
            }

            student.setName(newAttributes.name);
            student.setLastName(newAttributes.lastName);
            student.setComments(newAttributes.comments);
            student.setGoogleId(newAttributes.googleId);
            student.setTeamName(newAttributes.team);
            student.setSectionName(newAttributes.section);

            putDocument(newAttributes);

            saveEntity(student);

            newAttributes = makeAttributes(student);
            putDocument(newAttributes);

            return newAttributes;
        }
    }

    /**
     * Deletes a student in a course with email.
     *
     * <p>Fails silently if there is no such student.
     */
    public void deleteStudent(String courseId, String email) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(email);

        CourseStudent courseStudentToDelete = getCourseStudentEntityForEmail(courseId, email);
        if (courseStudentToDelete != null) {
            deleteDocumentByStudentKey(courseStudentToDelete.getRegistrationKey());
            deleteEntity(Key.create(CourseStudent.class, courseStudentToDelete.getUniqueId()));
        }
    }

    /**
     * Deletes students using {@link AttributesDeletionQuery}.
     */
    public void deleteStudents(AttributesDeletionQuery query) {
        if (query.isCourseIdPresent()) {
            List<CourseStudent> studentsToDelete = getCourseStudentsForCourseQuery(query.getCourseId()).list();
            deleteDocument(Const.SearchIndex.STUDENT,
                    studentsToDelete.stream().map(CourseStudent::getRegistrationKey).toArray(String[]::new));

            deleteEntity(studentsToDelete.stream()
                    .map(s -> Key.create(CourseStudent.class, s.getUniqueId()))
                    .toArray(Key[]::new));
        }
    }

    private CourseStudent getCourseStudentEntityForEmail(String courseId, String email) {
        return load().id(CourseStudent.generateId(email, courseId)).now();
    }

    private List<CourseStudent> getAllCourseStudentEntitiesForEmail(String email) {
        return load().filter("email =", email).list();
    }

    private CourseStudent getCourseStudentEntityForRegistrationKey(String registrationKey) {
        List<CourseStudent> studentList = load().filter("registrationKey =", registrationKey).list();

        // If registration key detected is not unique, something is wrong
        if (studentList.size() > 1) {
            StringBuilder duplicatedStudentsUniqueIds = new StringBuilder();
            for (CourseStudent s : studentList) {
                duplicatedStudentsUniqueIds.append(s.getUniqueId() + '\n');
            }
            log.severe("Duplicate registration keys detected for: \n" + duplicatedStudentsUniqueIds);
        }

        if (studentList.isEmpty()) {
            return null;
        }

        return studentList.get(0);
    }

    private Query<CourseStudent> getCourseStudentsForCourseQuery(String courseId) {
        return load().filter("courseId =", courseId);
    }

    private List<CourseStudent> getCourseStudentEntitiesForCourse(String courseId) {
        return getCourseStudentsForCourseQuery(courseId).list();
    }

    private Query<CourseStudent> getCourseStudentsForGoogleIdQuery(String googleId) {
        return load().filter("googleId =", googleId);
    }

    private List<CourseStudent> getCourseStudentEntitiesForGoogleId(String googleId) {
        return getCourseStudentsForGoogleIdQuery(googleId).list();
    }

    private List<CourseStudent> getCourseStudentEntitiesForTeam(String teamName, String courseId) {
        return load()
                .filter("teamName =", teamName)
                .filter("courseId =", courseId)
                .list();
    }

    @Override
    LoadType<CourseStudent> load() {
        return ofy().load().type(CourseStudent.class);
    }

    @Override
    boolean hasExistingEntities(StudentAttributes entityToCreate) {
        return !load()
                .filterKey(Key.create(CourseStudent.class,
                        CourseStudent.generateId(entityToCreate.getEmail(), entityToCreate.getCourse())))
                .list()
                .isEmpty();
    }

    @Override
    StudentAttributes makeAttributes(CourseStudent entity) {
        Assumption.assertNotNull(entity);

        return StudentAttributes.valueOf(entity);
    }
}
