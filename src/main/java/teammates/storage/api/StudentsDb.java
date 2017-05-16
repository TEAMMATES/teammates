package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.common.util.ThreadHelper;
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
public class StudentsDb extends EntitiesDb {

    public static final String ERROR_UPDATE_EMAIL_ALREADY_USED = "Trying to update to an email that is already used by: ";

    private static final Logger log = Logger.getLogger();

    public void putDocument(StudentAttributes student) {
        putDocument(Const.SearchIndex.STUDENT, new StudentSearchDocument(student));
    }

    /**
     * Batch creates or updates search documents for the given students.
     */
    public void putDocuments(List<StudentAttributes> students) {
        List<SearchDocument> studentDocuments = new ArrayList<SearchDocument>();
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

    /**
     * Creates students' records without searchability.
     */
    public void createStudentsWithoutSearchability(Collection<StudentAttributes> studentsToAdd)
            throws InvalidParametersException {

        List<EntityAttributes> studentsToUpdate = createEntities(studentsToAdd);
        for (EntityAttributes entity : studentsToUpdate) {
            StudentAttributes student = (StudentAttributes) entity;
            try {
                updateStudentWithoutSearchability(student.course, student.email, student.name, student.team,
                                                  student.section, student.email, student.googleId, student.comments);
            } catch (EntityDoesNotExistException e) {
                // This situation is not tested as replicating such a situation is
                // difficult during testing
                Assumption.fail("Entity found be already existing and not existing simultaneously");
            }
        }
    }

    public void createStudent(StudentAttributes student)
            throws InvalidParametersException, EntityAlreadyExistsException {

        createStudent(student, true);
    }

    public void createStudentWithoutDocument(StudentAttributes student)
            throws InvalidParametersException, EntityAlreadyExistsException {
        createStudent(student, false);
    }

    public void createStudent(StudentAttributes student, boolean hasDocument)
            throws InvalidParametersException, EntityAlreadyExistsException {
        StudentAttributes createdStudent = new StudentAttributes((CourseStudent) createEntity(student));
        if (hasDocument) {
            putDocument(createdStudent);
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

        CourseStudent cs = getCourseStudentEntityForEmail(courseId, email);
        if (cs == null) {
            return null;
        }
        return new StudentAttributes(cs);
    }

    /**
     * Preconditions:
     * <br> * All parameters are non-null.
     * @return null if no such student is found.
     */
    public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        Query q = getPm().newQuery(CourseStudent.class);
        q.declareParameters("String googleIdParam, String courseIdParam");
        q.setFilter("googleId == googleIdParam && courseId == courseIdParam");

        @SuppressWarnings("unchecked")
        List<CourseStudent> courseStudentList = (List<CourseStudent>) q.execute(googleId, courseId);

        if (courseStudentList.isEmpty() || JDOHelper.isDeleted(courseStudentList.get(0))) {
            return null;
        }

        return new StudentAttributes(courseStudentList.get(0));
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
            CourseStudent courseStudent = getCourseStudentEntityForRegistrationKey(decryptedKey);
            if (courseStudent == null) {
                return null;
            }
            return new StudentAttributes(courseStudent);
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

        List<StudentAttributes> studentDataList = new ArrayList<StudentAttributes>();

        List<CourseStudent> courseStudents = getCourseStudentEntitiesForGoogleId(googleId);
        for (CourseStudent student : courseStudents) {
            if (!JDOHelper.isDeleted(student)) {
                studentDataList.add(new StudentAttributes(student));
            }
        }

        return studentDataList;
    }

    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return an empty list if no students in the course.
     */
    public List<StudentAttributes> getStudentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<StudentAttributes> studentDataList = new ArrayList<StudentAttributes>();

        List<CourseStudent> courseStudentEntities = getCourseStudentEntitiesForCourse(courseId);
        for (CourseStudent student : courseStudentEntities) {
            if (!JDOHelper.isDeleted(student)) {
                studentDataList.add(new StudentAttributes(student));
            }
        }

        return studentDataList;
    }

    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return an empty list if no students in the course.
     */
    public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, teamName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<StudentAttributes> studentDataList = new ArrayList<StudentAttributes>();
        List<CourseStudent> courseStudentList = getCourseStudentEntitiesForTeam(teamName, courseId);

        //  e.g., convertToAttributes(entityList, new ArrayList<StudentAttributes>())
        for (CourseStudent student : courseStudentList) {
            if (!JDOHelper.isDeleted(student)) {
                studentDataList.add(new StudentAttributes(student));
            }
        }

        return studentDataList;
    }

    /**
     * Preconditions: <br>
     * All parameters are non-null.
     * @return an empty list if no students in this section
     */
    public List<StudentAttributes> getStudentsForSection(String sectionName, String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, sectionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<StudentAttributes> studentDataList = new ArrayList<StudentAttributes>();

        List<CourseStudent> courseStudentEntities = getCourseStudentEntitiesForSection(sectionName, courseId);

        for (CourseStudent student : courseStudentEntities) {
            if (!JDOHelper.isDeleted(student)) {
                studentDataList.add(new StudentAttributes(student));
            }
        }

        return studentDataList;
    }

    /**
     * Preconditions: <br>
     *  * All parameters are non-null.
     * @return an empty list if no students in the course.
     */
    public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<StudentAttributes> allStudents = getStudentsForCourse(courseId);
        ArrayList<StudentAttributes> unregistered = new ArrayList<StudentAttributes>();

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
        Map<String, StudentAttributes> result = new LinkedHashMap<String, StudentAttributes>();

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
        List<StudentAttributes> list = new LinkedList<StudentAttributes>();
        List<CourseStudent> entities = getCourseStudentEntities();

        for (CourseStudent student : entities) {
            if (!JDOHelper.isDeleted(student)) {
                list.add(new StudentAttributes(student));
            }
        }
        return list;
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

        log.info(Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId);
        getPm().close();
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

        StudentAttributes newCourseStudentAttributes = new StudentAttributes(newCourseStudent);
        try {
            createStudent(newCourseStudentAttributes, hasDocument);
        } catch (EntityAlreadyExistsException e) {
            CourseStudent existingStudent = (CourseStudent) getEntity(newCourseStudentAttributes);
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

        if (hasDocument) {
            putDocument(new StudentAttributes(courseStudent));
        }

        // Set true to prevent changes to last update timestamp
        courseStudent.keepUpdateTimestamp = keepUpdateTimestamp;
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
                deleteDocument(new StudentAttributes(courseStudentToDelete));
                getPm().deletePersistent(courseStudentToDelete);
                getPm().flush();
            }
        } else {
            getCourseStudentForEmailQuery(courseId, email)
                .deletePersistentAll();
        }

        // Check delete operation persisted
        if (Config.PERSISTENCE_CHECK_DURATION > 0) {
            int elapsedTime = 0;
            while (hasCourseStudentForEmail(courseId, email)
                    && elapsedTime < Config.PERSISTENCE_CHECK_DURATION) {
                ThreadHelper.waitBriefly();
                //check before incrementing to avoid boundary case problem
                if (hasCourseStudentForEmail(courseId, email)) {
                    elapsedTime += ThreadHelper.WAIT_DURATION;
                }
            }
            if (elapsedTime >= Config.PERSISTENCE_CHECK_DURATION) {
                log.info("Operation did not persist in time: deleteStudent->"
                        + courseId + "/" + email);
            }
        }
        //TODO: use the method in the parent class instead.
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
            List<CourseStudent> courseStudents = getCourseStudentEntitiesForGoogleId(googleId);
            for (CourseStudent student : courseStudents) {
                deleteDocument(new StudentAttributes(student));
            }
            getPm().deletePersistentAll(courseStudents);
            getPm().flush();
        } else {
            getCourseStudentsForGoogleIdQuery(googleId)
                .deletePersistentAll();
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
            List<CourseStudent> courseStudentList = getCourseStudentEntitiesForCourse(courseId);
            for (CourseStudent student : courseStudentList) {
                deleteDocument(new StudentAttributes(student));
            }
            getPm().deletePersistentAll(courseStudentList);
            getPm().flush();
        } else {
            getCourseStudentsForCourseQuery(courseId)
                .deletePersistentAll();
        }
    }

    public void deleteStudentsForCourses(List<String> courseIds) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

        getCourseStudentsForCoursesQuery(courseIds)
            .deletePersistentAll();
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

    private QueryWithParams getCourseStudentForEmailQuery(String courseId, String email) {
        Query q = getPm().newQuery(CourseStudent.class);
        q.declareParameters("String courseIdParam, String emailParam");
        q.setFilter("courseId == courseIdParam && email == emailParam");
        return new QueryWithParams(q, new Object[] {courseId, email});
    }

    private QueryWithParams getCourseStudentForEmailKeyOnlyQuery(String courseId, String email) {
        QueryWithParams query = getCourseStudentForEmailQuery(courseId, email);
        query.setExpectedResultField("id");
        return query;
    }

    private boolean hasCourseStudentForEmail(String courseId, String email) {
        QueryWithParams q = getCourseStudentForEmailKeyOnlyQuery(courseId, email);
        List<?> results = q.execute();
        return !results.isEmpty();
    }

    private CourseStudent getCourseStudentEntityForEmail(String courseId, String email) {
        @SuppressWarnings("unchecked")
        List<CourseStudent> studentList = (List<CourseStudent>) getCourseStudentForEmailQuery(courseId, email).execute();

        if (studentList.isEmpty() || JDOHelper.isDeleted(studentList.get(0))) {
            return null;
        }

        return studentList.get(0);
    }

    @SuppressWarnings("unchecked")
    private CourseStudent getCourseStudentEntityForRegistrationKey(String registrationKey) {

        Query query = getPm().newQuery(CourseStudent.class);
        query.declareParameters("String registrationKeyParam");
        query.setFilter("registrationKey == registrationKeyParam");

        try {
            List<CourseStudent> studentList = new ArrayList<CourseStudent>();
            studentList.addAll((List<CourseStudent>) query.execute(registrationKey));

            // If registration key detected is not unique, something is wrong
            if (studentList.size() > 1) {
                StringBuilder duplicatedStudentsUniqueIds = new StringBuilder();
                for (CourseStudent s : studentList) {
                    duplicatedStudentsUniqueIds.append(s.getUniqueId() + '\n');
                }
                log.severe("Duplicate registration keys detected for: \n" + duplicatedStudentsUniqueIds);
            }

            if (studentList.isEmpty() || JDOHelper.isDeleted(studentList.get(0))) {
                return null;
            }

            return studentList.get(0);
        } catch (Exception e) {
            log.severe("Exception: " + TeammatesException.toStringWithStackTrace(e));
            return null;
        }
    }

    private QueryWithParams getCourseStudentsForCourseQuery(String courseId) {
        Query q = getPm().newQuery(CourseStudent.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        return new QueryWithParams(q, new Object[] {courseId});
    }

    @SuppressWarnings("unchecked")
    public List<CourseStudent> getCourseStudentEntitiesForCourse(String courseId) {
        return (List<CourseStudent>) getCourseStudentsForCourseQuery(courseId).execute();
    }

    private QueryWithParams getCourseStudentsForCoursesQuery(List<String> courseIds) {
        Query q = getPm().newQuery(CourseStudent.class);
        q.setFilter(":p.contains(courseId)");
        return new QueryWithParams(q, new Object[] {courseIds});
    }

    private QueryWithParams getCourseStudentsForGoogleIdQuery(String googleId) {
        Query q = getPm().newQuery(CourseStudent.class);
        q.declareParameters("String googleIdParam");
        q.setFilter("googleId == googleIdParam");
        return new QueryWithParams(q, new Object[] {googleId});
    }

    @SuppressWarnings("unchecked")
    private List<CourseStudent> getCourseStudentEntitiesForGoogleId(String googleId) {
        return (List<CourseStudent>) getCourseStudentsForGoogleIdQuery(googleId).execute();
    }

    @SuppressWarnings("unchecked")
    private List<CourseStudent> getCourseStudentEntitiesForTeam(String teamName, String courseId) {
        Query q = getPm().newQuery(CourseStudent.class);
        q.declareParameters("String teamNameParam, String courseIDParam");
        q.setFilter("teamName == teamNameParam && courseId == courseIDParam");

        return (List<CourseStudent>) q.execute(teamName, courseId);
    }

    @SuppressWarnings("unchecked")
    private List<CourseStudent> getCourseStudentEntitiesForSection(String sectionName, String courseId) {
        Query q = getPm().newQuery(CourseStudent.class);
        q.declareParameters("String sectionNameParam, String courseIDParam");
        q.setFilter("sectionName == sectionNameParam && courseId == courseIDParam");

        return (List<CourseStudent>) q.execute(sectionName, courseId);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    /**
     * Retrieves all course student entities. This function is not scalable.
     */
    public List<CourseStudent> getCourseStudentEntities() {

        Query q = getPm().newQuery(CourseStudent.class);

        return (List<CourseStudent>) q.execute();
    }

    @Override
    protected Object getEntity(EntityAttributes entity) {
        StudentAttributes studentToGet = (StudentAttributes) entity;
        return getCourseStudentEntityForEmail(studentToGet.course, studentToGet.email);
    }

    @Override
    protected QueryWithParams getEntityKeyOnlyQuery(EntityAttributes attributes) {
        StudentAttributes sa = (StudentAttributes) attributes;
        return getCourseStudentForEmailKeyOnlyQuery(sa.course, sa.email);
    }

}
