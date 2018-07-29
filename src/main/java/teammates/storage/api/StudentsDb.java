package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
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

    public static final String ERROR_UPDATE_EMAIL_ALREADY_USED = "Trying to update to an email that is already used by: ";

    private static final Logger log = Logger.getLogger();

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
        putDocuments(Const.SearchIndex.STUDENT, studentDocuments);
    }

    /**
     * Searches for students.
     * @return {@link StudentSearchResultBundle}
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
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search students in the whole system.
     * @return null if no result found
     */
    public StudentSearchResultBundle searchStudentsInWholeSystem(String queryString) {
        if (queryString.trim().isEmpty()) {
            return new StudentSearchResultBundle();
        }

        Results<ScoredDocument> results = searchDocuments(Const.SearchIndex.STUDENT,
                new StudentSearchQuery(queryString));

        return StudentSearchDocument.fromResults(results);
    }

    public void deleteDocument(StudentAttributes studentToDelete) {
        String key = studentToDelete.key;
        if (key == null) {
            StudentAttributes student = getStudentForEmail(studentToDelete.course, studentToDelete.email);
            if (student == null) {
                return;
            }
            key = student.key;
        }
        deleteDocument(Const.SearchIndex.STUDENT, key);
    }

    public void createStudentWithoutDocument(StudentAttributes student)
            throws InvalidParametersException, EntityAlreadyExistsException {
        createStudent(student, false);
    }

    public void createStudent(StudentAttributes student, boolean hasDocument)
            throws InvalidParametersException, EntityAlreadyExistsException {

        CourseStudent createdStudent = createEntity(student);
        if (hasDocument) {
            putDocument(makeAttributes(createdStudent));
        }
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return The data for Student with the courseId and email. Returns null if
     *         there is no such student.
     */
    public StudentAttributes getStudentForEmail(String courseId, String email) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);

        return makeAttributesOrNull(getCourseStudentEntityForEmail(courseId, email));
    }

    /**
     * Preconditions:
     * <br> * All parameters are non-null.
     * @return null if no such student is found.
     */
    public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        CourseStudent student = load()
                .filter("courseId =", courseId)
                .filter("googleId =", googleId)
                .first().now();

        return makeAttributesOrNull(student);
    }

    /**
     * Works only for encrypted keys.
     *
     * <p>Preconditions: <br>
     * All parameters are non-null.
     *
     * @return null if no matching student.
     */
    public StudentAttributes getStudentForRegistrationKey(String encryptedRegistrationKey) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, encryptedRegistrationKey);

        try {
            String decryptedKey = StringHelper.decrypt(encryptedRegistrationKey.trim());
            return makeAttributesOrNull(getCourseStudentEntityForRegistrationKey(decryptedKey));
        } catch (InvalidParametersException e) {
            return null; // invalid registration key cannot be decrypted
        } catch (Exception e) {
            // TODO change this to an Assumption.fail
            log.severe("Exception thrown trying to retrieve CourseStudent \n"
                    + TeammatesException.toStringWithStackTrace(e));
            return null;
        }

    }

    /**
     * Preconditions:
     * <br> * All parameters are non-null.
     * @return an empty list if no such students are found.
     */
    public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);

        return makeAttributes(getCourseStudentEntitiesForGoogleId(googleId));
    }

    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return an empty list if no students in the course.
     */
    public List<StudentAttributes> getStudentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getCourseStudentEntitiesForCourse(courseId));
    }

    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return an empty list if no students in the course.
     */
    public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, teamName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getCourseStudentEntitiesForTeam(teamName, courseId));
    }

    /**
     * Preconditions: <br>
     * All parameters are non-null.
     * @return an empty list if no students in this section
     */
    public List<StudentAttributes> getStudentsForSection(String sectionName, String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, sectionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getCourseStudentEntitiesForSection(sectionName, courseId));
    }

    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return an empty list if no students in the course.
     */
    public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<StudentAttributes> allStudents = getStudentsForCourse(courseId);
        ArrayList<StudentAttributes> unregistered = new ArrayList<>();

        for (StudentAttributes s : allStudents) {
            if (s.googleId == null || s.googleId.trim().isEmpty()) {
                unregistered.add(s);
            }
        }
        return unregistered;
    }

    /**
     * This method is not scalable. Not to be used unless for admin features.
     * @return the list of all students in the database.
     */
    // TODO remove this method once all Students have been migrated to CourseStudents
    @Deprecated
    public List<StudentAttributes> getAllStudents() {
        Map<String, StudentAttributes> result = new LinkedHashMap<>();

        for (StudentAttributes student : getAllCourseStudents()) {
            result.put(student.getId(), student);
        }
        return new ArrayList<>(result.values());
    }

    /**
     * This method is not scalable. Not to be used unless for admin features.
     * @return the list of all students in the database.
     */
    @Deprecated
    public List<StudentAttributes> getAllCourseStudents() {
        return makeAttributes(getCourseStudentEntities());
    }

    /**
     * Updates the student identified by {@code courseId} and {@code email}.
     * For the remaining parameters, the existing value is preserved
     *   if the parameter is null (due to 'keep existing' policy)<br>
     * Preconditions: <br>
     * * {@code courseId} and {@code email} are non-null and correspond to an existing student. <br>
     * @param keepUpdateTimestamp Set true to prevent changes to updatedAt. Use when updating entities with scripts.
     */
    public void updateStudent(String courseId, String email, String newName,
                                    String newTeamName, String newSectionName, String newEmail,
                                    String newGoogleId,
                                    String newComments,
                                    boolean keepUpdateTimestamp) throws InvalidParametersException,
                                    EntityDoesNotExistException {
        updateStudent(courseId, email, newName, newTeamName, newSectionName,
                newEmail, newGoogleId, newComments, true, keepUpdateTimestamp);
    }

    public void updateStudent(String courseId, String email, String newName,
            String newTeamName, String newSectionName, String newEmail,
            String newGoogleId,
            String newComments) throws InvalidParametersException,
            EntityDoesNotExistException {
        updateStudent(courseId, email, newName, newTeamName, newSectionName,
                newEmail, newGoogleId, newComments, true, false);
    }

    /**
     * Update student's record without searchability
     * This function is only used for testing, its purpose is to not create document if not necessary.
     * @param keepUpdateTimestamp Set true to prevent changes to updatedAt. Use when updating entities with scripts.
     */
    public void updateStudentWithoutSearchability(String courseId, String email,
            String newName,
            String newTeamName, String newSectionName, String newEmail,
            String newGoogleId,
            String newComments,
            boolean keepUpdateTimestamp) throws InvalidParametersException,
            EntityDoesNotExistException {
        updateStudent(courseId, email, newName, newTeamName, newSectionName,
                                        newEmail, newGoogleId, newComments, false, keepUpdateTimestamp);
    }

    public void updateStudentWithoutSearchability(String courseId, String email,
            String newName,
            String newTeamName, String newSectionName, String newEmail,
            String newGoogleId,
            String newComments) throws InvalidParametersException,
            EntityDoesNotExistException {
        updateStudent(courseId, email, newName, newTeamName, newSectionName,
                newEmail, newGoogleId, newComments, false, false);
    }

    public void updateStudent(String courseId, String email, String newName,
            String newTeamName, String newSectionName, String newEmail, String newGoogleId,
            String newComments, boolean hasDocument, boolean keepUpdateTimestamp)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);

        verifyStudentExists(courseId, email);

        // Update CourseStudent if it exists.
        CourseStudent courseStudent = getCourseStudentEntityForEmail(courseId, email);
        if (courseStudent != null) {
            boolean isEmailChanged = !email.equals(newEmail);
            String lastName = StringHelper.splitName(newName)[1];

            if (isEmailChanged) {
                CourseStudent newCourseStudent = new CourseStudent(newEmail, newName, newGoogleId, newComments,
                                                                   courseId, newTeamName, newSectionName);
                recreateStudentWithNewEmail(newCourseStudent, lastName, courseStudent, hasDocument,
                                            keepUpdateTimestamp, courseId, email);
            } else {
                updateStudentDetails(newName, newTeamName, newSectionName, newGoogleId,
                                     newComments, hasDocument, keepUpdateTimestamp, courseStudent, lastName);
            }
        }
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    private void recreateStudentWithNewEmail(
            CourseStudent newCourseStudent, String lastName, CourseStudent courseStudent,
            boolean hasDocument, boolean keepUpdateTimestamp, String courseId, String email)
            throws InvalidParametersException {
        newCourseStudent.setLastName(lastName);
        newCourseStudent.setCreatedAt(courseStudent.getCreatedAt());
        if (keepUpdateTimestamp) {
            newCourseStudent.setLastUpdate(courseStudent.getUpdatedAt());
        }

        StudentAttributes newCourseStudentAttributes = makeAttributes(newCourseStudent);
        try {
            createStudent(newCourseStudentAttributes, hasDocument);
        } catch (EntityAlreadyExistsException e) {
            CourseStudent existingStudent = getEntity(newCourseStudentAttributes);
            String error = ERROR_UPDATE_EMAIL_ALREADY_USED + existingStudent.getName() + "/" + existingStudent.getEmail();
            throw new InvalidParametersException(error);
        }

        deleteStudent(courseId, email);
    }

    private void updateStudentDetails(String newName, String newTeamName, String newSectionName,
            String newGoogleId, String newComments, boolean hasDocument,
            boolean keepUpdateTimestamp, CourseStudent courseStudent, String lastName) {
        courseStudent.setName(newName);
        courseStudent.setLastName(lastName);
        courseStudent.setComments(newComments);
        courseStudent.setGoogleId(newGoogleId);
        courseStudent.setTeamName(newTeamName);
        courseStudent.setSectionName(newSectionName);

        StudentAttributes attributes = makeAttributes(courseStudent);

        if (hasDocument) {
            putDocument(attributes);
        }

        // Set true to prevent changes to last update timestamp
        courseStudent.keepUpdateTimestamp = keepUpdateTimestamp;
        saveEntity(courseStudent, attributes);
    }

    //TODO: add an updateStudent(StudentAttributes) version and make the above private

    /**
     * Fails silently if no such student. <br>
     * Preconditions: <br>
     *  * All parameters are non-null.
     *
     */

    public void deleteStudent(String courseId, String email) {
        deleteStudent(courseId, email, true);
    }

    public void deleteStudentWithoutDocument(String courseId, String email) {
        deleteStudent(courseId, email, false);
    }

    public void deleteStudent(String courseId, String email, boolean hasDocument) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);

        if (hasDocument) {
            CourseStudent courseStudentToDelete = getCourseStudentEntityForEmail(courseId, email);
            if (courseStudentToDelete != null) {
                StudentAttributes courseStudentToDeleteAttributes = makeAttributes(courseStudentToDelete);
                deleteDocument(courseStudentToDeleteAttributes);
                deleteEntityDirect(courseStudentToDelete, courseStudentToDeleteAttributes);
            }
        } else {
            ofy().delete().keys(getCourseStudentForEmailQuery(courseId, email).keys()).now();
        }
    }

    /**
     * Fails silently if no such student. <br>
     * Preconditions: <br>
     *  * All parameters are non-null.
     *
     */

    public void deleteStudentsForGoogleId(String googleId) {
        deleteStudentsForGoogleId(googleId, true);
    }

    public void deleteStudentsForGoogleIdWithoutDocument(String googleId) {
        deleteStudentsForGoogleId(googleId, false);
    }

    public void deleteStudentsForGoogleId(String googleId, boolean hasDocument) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);

        if (hasDocument) {
            deleteStudentsCascadeDocuments(getCourseStudentEntitiesForGoogleId(googleId));
        } else {
            ofy().delete().keys(getCourseStudentsForGoogleIdQuery(googleId).keys());
        }
    }

    /**
     * Fails silently if no such student or no such course. <br>
     * Preconditions: <br>
     *  * All parameters are non-null.
     *
     */

    public void deleteStudentsForCourse(String courseId) {
        deleteStudentsForCourse(courseId, true);
    }

    public void deleteStudentsForCourseWithoutDocument(String courseId) {
        deleteStudentsForCourse(courseId, false);
    }

    public void deleteStudentsForCourse(String courseId, boolean hasDocument) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        if (hasDocument) {
            deleteStudentsCascadeDocuments(getCourseStudentEntitiesForCourse(courseId));
        } else {
            ofy().delete().keys(getCourseStudentsForCourseQuery(courseId).keys());
        }
    }

    public void deleteStudentsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

        ofy().delete().keys(getCourseStudentsForCoursesQuery(courseIds).keys());
    }

    /**
     * Verifies that the student with the specified {@code email} exists in the course {@code courseId}.
     *
     * @throws EntityDoesNotExistException if the student specified by courseId and email does not exist,
     */
    public void verifyStudentExists(String courseId, String email)
            throws EntityDoesNotExistException {
        if (getStudentForEmail(courseId, email) == null) {
            String error = ERROR_UPDATE_NON_EXISTENT_STUDENT + courseId + "/" + email;
            throw new EntityDoesNotExistException(error);
        }
    }

    private Query<CourseStudent> getCourseStudentForEmailQuery(String courseId, String email) {
        return load()
                .filter("courseId =", courseId)
                .filter("email =", email);
    }

    private CourseStudent getCourseStudentEntityForEmail(String courseId, String email) {
        return load().id(email + '%' + courseId).now();
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

    public List<CourseStudent> getCourseStudentEntitiesForCourse(String courseId) {
        return getCourseStudentsForCourseQuery(courseId).list();
    }

    private Query<CourseStudent> getCourseStudentsForCoursesQuery(List<String> courseIds) {
        return load().filter("courseId in", courseIds);
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

    private List<CourseStudent> getCourseStudentEntitiesForSection(String sectionName, String courseId) {
        return load()
                .filter("sectionName =", sectionName)
                .filter("courseId =", courseId)
                .list();
    }

    @Deprecated
    /**
     * Retrieves all course student entities. This function is not scalable.
     */
    public List<CourseStudent> getCourseStudentEntities() {
        return load().list();
    }

    @Override
    protected LoadType<CourseStudent> load() {
        return ofy().load().type(CourseStudent.class);
    }

    @Override
    protected CourseStudent getEntity(StudentAttributes studentToGet) {
        return getCourseStudentEntityForEmail(studentToGet.course, studentToGet.email);
    }

    @Override
    protected QueryKeys<CourseStudent> getEntityQueryKeys(StudentAttributes attributes) {
        return getCourseStudentForEmailQuery(attributes.course, attributes.email).keys();
    }

    private void deleteStudentsCascadeDocuments(List<CourseStudent> students) {
        List<StudentAttributes> studentsAttributes = new ArrayList<>();
        for (CourseStudent student : students) {
            StudentAttributes studentAttributes = makeAttributes(student);
            studentsAttributes.add(studentAttributes);
            deleteDocument(studentAttributes);
        }
        deleteEntitiesDirect(students, studentsAttributes);
    }

    @Override
    protected StudentAttributes makeAttributes(CourseStudent entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return StudentAttributes.valueOf(entity);
    }
}
