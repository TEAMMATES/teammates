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
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Logger;
import teammates.storage.entity.CourseStudent;
import teammates.storage.search.SearchManagerFactory;
import teammates.storage.search.StudentSearchManager;

/**
 * Handles CRUD operations for students.
 *
 * @see CourseStudent
 * @see StudentAttributes
 */
public final class StudentsDb extends EntitiesDb<CourseStudent, StudentAttributes> {

    private static final Logger log = Logger.getLogger();

    private static final int MAX_KEY_REGENERATION_TRIES = 10;

    private static final StudentsDb instance = new StudentsDb();

    private StudentsDb() {
        // prevent initialization
    }

    public static StudentsDb inst() {
        return instance;
    }

    private StudentSearchManager getSearchManager() {
        return SearchManagerFactory.getStudentSearchManager();
    }

    /**
     * Creates or updates search document for the given student.
     */
    public void putDocument(StudentAttributes student) throws SearchServiceException {
        getSearchManager().putDocument(student);
    }

    /**
     * Searches for students.
     *
     * @param instructors the constraint that restricts the search result
     */
    public List<StudentAttributes> search(String queryString, List<InstructorAttributes> instructors)
            throws SearchServiceException {
        if (queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return getSearchManager().searchStudents(queryString, instructors);
    }

    /**
     * Searches all students in the system.
     *
     * <p>This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search instructors in the whole system.
     */
    public List<StudentAttributes> searchStudentsInWholeSystem(String queryString)
            throws SearchServiceException {
        if (queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return getSearchManager().searchStudents(queryString, null);
    }

    /**
     * Removes search document for the given student by using {@code studentUniqueId}.
     */
    public void deleteDocumentByStudentId(String studentUniqueId) {
        getSearchManager().deleteDocuments(Collections.singletonList(studentUniqueId));
    }

    /**
     * Regenerates the registration key of a student in a course.
     *
     * @return the updated student
     * @throws EntityAlreadyExistsException if a new registration key could not be generated
     */
    public StudentAttributes regenerateEntityKey(StudentAttributes originalStudent) throws EntityAlreadyExistsException {
        int numTries = 0;
        while (numTries < MAX_KEY_REGENERATION_TRIES) {
            CourseStudent updatedEntity = convertToEntityForSaving(originalStudent);
            if (!updatedEntity.getRegistrationKey().equals(originalStudent.getKey())) {
                saveEntity(updatedEntity);
                return makeAttributes(updatedEntity);
            }
            numTries++;
        }
        log.severe("Failed to generate new registration key for student after " + MAX_KEY_REGENERATION_TRIES + " tries");
        throw new EntityAlreadyExistsException("Could not regenerate a new course registration key for the student.");
    }

    /**
     * Checks if the given students exist in the given course.
     */
    public boolean hasExistingStudentsInCourse(String courseId, Collection<String> studentEmailAddresses) {
        if (studentEmailAddresses.isEmpty()) {
            return true;
        }
        Set<String> existingStudentEmailAddresses = load().filter("courseId =", courseId)
                .project("email")
                .list()
                .stream()
                .map(CourseStudent::getEmail)
                .collect(Collectors.toSet());
        return existingStudentEmailAddresses.containsAll(studentEmailAddresses);
    }

    /**
     * Gets a student by unique ID courseId-email.
     */
    public StudentAttributes getStudentForEmail(String courseId, String email) {
        assert courseId != null;
        assert email != null;

        return makeAttributesOrNull(getCourseStudentEntityForEmail(courseId, email));
    }

    /**
     * Gets list of students by email.
     */
    public List<StudentAttributes> getAllStudentsForEmail(String email) {
        assert email != null;

        List<CourseStudent> students = getAllCourseStudentEntitiesForEmail(email);
        return students.stream().map(this::makeAttributes).collect(Collectors.toList());
    }

    /**
     * Gets a student by unique constraint courseId-googleId.
     */
    public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
        assert googleId != null;
        assert courseId != null;

        CourseStudent student = load()
                    .filter("courseId =", courseId)
                    .filter("googleId =", googleId)
                    .first().now();

        return makeAttributesOrNull(student);
    }

    /**
     * Gets a student by unique constraint registrationKey.
     */
    public StudentAttributes getStudentForRegistrationKey(String registrationKey) {
        assert registrationKey != null;

        return makeAttributesOrNull(getCourseStudentEntityForRegistrationKey(registrationKey.trim()));
    }

    /**
     * Gets all students associated with a googleId.
     */
    public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
        assert googleId != null;

        return makeAttributes(getCourseStudentEntitiesForGoogleId(googleId));
    }

    /**
     * Gets the total number of students of a course.
     */
    public int getNumberOfStudentsForCourse(String courseId) {
        assert courseId != null;

        return getCourseStudentsForCourseQuery(courseId).count();
    }

    /**
     * Gets all students of a course.
     */
    public List<StudentAttributes> getStudentsForCourse(String courseId) {
        assert courseId != null;

        return makeAttributes(getCourseStudentEntitiesForCourse(courseId));
    }

    /**
     * Gets the first {@code batchSize} students of the course.
     */
    public List<StudentAttributes> getStudentsForCourse(String courseId, int batchSize) {
        assert courseId != null;

        return makeAttributes(getCourseStudentEntitiesForCourse(courseId, batchSize));
    }

    /**
     * Gets all students of a section of a course.
     */
    public List<StudentAttributes> getStudentsForSection(String sectionName, String courseId) {
        assert sectionName != null;
        assert courseId != null;

        return makeAttributes(getCourseStudentEntitiesForSection(sectionName, courseId));
    }

    /**
     * Gets all students of a team of a course.
     */
    public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
        assert teamName != null;
        assert courseId != null;

        return makeAttributes(getCourseStudentEntitiesForTeam(teamName, courseId));
    }

    /**
     * Gets count of students of a team of a course.
     */
    public int getStudentCountForTeam(String teamName, String courseId) {
        assert teamName != null;
        assert courseId != null;

        return getCourseStudentCountForTeam(teamName, courseId);
    }

    /**
     * Gets all unregistered students of a course.
     */
    public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
        assert courseId != null;

        List<StudentAttributes> allStudents = getStudentsForCourse(courseId);
        List<StudentAttributes> unregistered = new ArrayList<>();

        for (StudentAttributes s : allStudents) {
            if (s.getGoogleId() == null || s.getGoogleId().trim().isEmpty()) {
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
        assert updateOptions != null;

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

        boolean isEmailChanged = !student.getEmail().equals(newAttributes.getEmail());

        if (isEmailChanged) {
            newAttributes = createEntity(newAttributes);
            // delete the old student
            deleteStudent(student.getCourseId(), student.getEmail());

            return newAttributes;
        } else {
            // update only if change
            boolean hasSameAttributes =
                    this.<String>hasSameValue(student.getName(), newAttributes.getName())
                    && this.<String>hasSameValue(student.getComments(), newAttributes.getComments())
                    && this.<String>hasSameValue(student.getGoogleId(), newAttributes.getGoogleId())
                    && this.<String>hasSameValue(student.getTeamName(), newAttributes.getTeam())
                    && this.<String>hasSameValue(student.getSectionName(), newAttributes.getSection());
            if (hasSameAttributes) {
                log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, CourseStudent.class.getSimpleName(), updateOptions));
                return newAttributes;
            }

            student.setName(newAttributes.getName());
            student.setComments(newAttributes.getComments());
            student.setGoogleId(newAttributes.getGoogleId());
            student.setTeamName(newAttributes.getTeam());
            student.setSectionName(newAttributes.getSection());

            saveEntity(student);

            return makeAttributes(student);
        }
    }

    /**
     * Deletes a student in a course with email.
     *
     * <p>Fails silently if there is no such student.
     */
    public void deleteStudent(String courseId, String email) {
        assert courseId != null;
        assert email != null;

        CourseStudent courseStudentToDelete = getCourseStudentEntityForEmail(courseId, email);
        if (courseStudentToDelete != null) {
            deleteDocumentByStudentId(courseStudentToDelete.getUniqueId());
            deleteEntity(Key.create(CourseStudent.class, courseStudentToDelete.getUniqueId()));
        }
    }

    /**
     * Deletes students using {@link AttributesDeletionQuery}.
     */
    public void deleteStudents(AttributesDeletionQuery query) {
        if (query.isCourseIdPresent()) {
            List<CourseStudent> studentsToDelete = getCourseStudentsForCourseQuery(query.getCourseId()).list();
            getSearchManager().deleteDocuments(
                    studentsToDelete.stream().map(CourseStudent::getUniqueId).collect(Collectors.toList()));

            deleteEntity(studentsToDelete.stream()
                    .map(s -> Key.create(CourseStudent.class, s.getUniqueId()))
                    .collect(Collectors.toList()));
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
            log.severe("Duplicate registration keys detected for: "
                    + studentList.stream().map(s -> s.getUniqueId()).collect(Collectors.joining(", ")));
        }

        if (studentList.isEmpty()) {
            return null;
        }

        return studentList.get(0);
    }

    private Query<CourseStudent> getCourseStudentsForCourseQuery(String courseId) {
        return load().filter("courseId =", courseId);
    }

    private Query<CourseStudent> getCourseStudentsForCourseQuery(String courseId, int batchSize) {
        return load()
                .filter("courseId =", courseId)
                .limit(batchSize);
    }

    private List<CourseStudent> getCourseStudentEntitiesForCourse(String courseId) {
        return getCourseStudentsForCourseQuery(courseId).list();
    }

    private List<CourseStudent> getCourseStudentEntitiesForCourse(String courseId, int batchSize) {
        return getCourseStudentsForCourseQuery(courseId, batchSize).list();
    }

    /**
     * Returns true if there are any student entities associated with the googleId.
     */
    public boolean hasStudentsForGoogleId(String googleId) {
        return !getCourseStudentsForGoogleIdQuery(googleId).keys().list().isEmpty();
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

    private int getCourseStudentCountForTeam(String teamName, String courseId) {
        return load()
                .filter("teamName =", teamName)
                .filter("courseId =", courseId)
                .count();
    }

    private List<CourseStudent> getCourseStudentEntitiesForSection(String sectionName, String courseId) {
        return load()
                .filter("sectionName =", sectionName)
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
                .keys()
                .list()
                .isEmpty();
    }

    @Override
    StudentAttributes makeAttributes(CourseStudent entity) {
        assert entity != null;

        return StudentAttributes.valueOf(entity);
    }

    @Override
    CourseStudent convertToEntityForSaving(StudentAttributes attributes) throws EntityAlreadyExistsException {
        int numTries = 0;
        while (numTries < MAX_KEY_REGENERATION_TRIES) {
            CourseStudent student = attributes.toEntity();
            Key<CourseStudent> existingStudent =
                    load().filter("registrationKey =", student.getRegistrationKey()).keys().first().now();
            if (existingStudent == null) {
                return student;
            }
            numTries++;
        }
        log.severe("Failed to generate new registration key for student after " + MAX_KEY_REGENERATION_TRIES + " tries");
        throw new EntityAlreadyExistsException("Unable to create new student");
    }

    /**
     * Gets the number of students created within a specified time range.
     */
    public int getNumStudentsByTimeRange(Instant startTime, Instant endTime) {
        return load()
                .filter("createdAt >=", startTime)
                .filter("createdAt <", endTime)
                .count();
    }

}
