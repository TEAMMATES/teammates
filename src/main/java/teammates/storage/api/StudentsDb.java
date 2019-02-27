package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Removes search document for the given student by using {@code unencryptedRegistrationKey}.
     *
     * <p>See {@link StudentSearchDocument#toDocument()} for more details.</p>
     */
    public void deleteDocumentByStudentKey(String unencryptedRegistrationKey) {
        deleteDocument(Const.SearchIndex.STUDENT, unencryptedRegistrationKey);
    }

    public void createStudent(StudentAttributes student)
            throws InvalidParametersException, EntityAlreadyExistsException {

        CourseStudent createdStudent = createEntity(student);
        putDocument(makeAttributes(createdStudent));

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
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updateOptions);

        CourseStudent student = getCourseStudentEntityForEmail(updateOptions.getCourseId(), updateOptions.getEmail());
        if (student == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_STUDENT + updateOptions);

        }

        StudentAttributes newAttributes = makeAttributes(student);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        boolean isEmailChanged = !student.getEmail().equals(newAttributes.email);

        if (isEmailChanged) {
            CourseStudent createdStudent = createEntity(newAttributes);
            // delete the old student
            deleteStudent(student.getCourseId(), student.getEmail());

            newAttributes = makeAttributes(createdStudent);
            putDocument(newAttributes);

            return newAttributes;
        } else {
            student.setName(newAttributes.name);
            student.setLastName(newAttributes.lastName);
            student.setComments(newAttributes.comments);
            student.setGoogleId(newAttributes.googleId);
            student.setTeamName(newAttributes.team);
            student.setSectionName(newAttributes.section);

            putDocument(newAttributes);

            // Set true to prevent changes to last update timestamp
            saveEntity(student, newAttributes);

            newAttributes = makeAttributes(student);
            putDocument(newAttributes);

            return newAttributes;
        }
    }

    /**
     * Fails silently if no such student. <br>
     * Preconditions: <br>
     *  * All parameters are non-null.
     *
     */
    public void deleteStudent(String courseId, String email) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);

        CourseStudent courseStudentToDelete = getCourseStudentEntityForEmail(courseId, email);
        if (courseStudentToDelete != null) {
            StudentAttributes courseStudentToDeleteAttributes = makeAttributes(courseStudentToDelete);
            deleteDocumentByStudentKey(courseStudentToDelete.getRegistrationKey());
            deleteEntityDirect(courseStudentToDelete, courseStudentToDeleteAttributes);
        }
    }

    /**
     * Fails silently if no such student. <br>
     * Preconditions: <br>
     *  * All parameters are non-null.
     *
     */

    public void deleteStudentsForGoogleId(String googleId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        deleteStudentsCascadeDocuments(getCourseStudentEntitiesForGoogleId(googleId));
    }

    /**
     * Fails silently if no such student or no such course. <br>
     * Preconditions: <br>
     *  * All parameters are non-null.
     *
     */

    public void deleteStudentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        deleteStudentsCascadeDocuments(getCourseStudentEntitiesForCourse(courseId));
    }

    public void deleteStudentsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

        ofy().delete().keys(getCourseStudentsForCoursesQuery(courseIds).keys());
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
            deleteDocumentByStudentKey(student.getRegistrationKey());
        }
        deleteEntitiesDirect(students, studentsAttributes);
    }

    @Override
    protected StudentAttributes makeAttributes(CourseStudent entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return StudentAttributes.valueOf(entity);
    }
}
