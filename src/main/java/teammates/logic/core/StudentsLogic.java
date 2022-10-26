package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.common.util.RequestTracer;
import teammates.storage.api.StudentsDb;

/**
 * Handles operations related to students.
 *
 * @see StudentAttributes
 * @see StudentsDb
 */
public final class StudentsLogic {

    static final String ERROR_INVALID_TEAM_NAME =
            "Team \"%s\" is detected in both Section \"%s\" and Section \"%s\".";
    static final String ERROR_INVALID_TEAM_NAME_INSTRUCTION =
            "Please use different team names in different sections.";
    static final String ERROR_ENROLL_EXCEED_SECTION_LIMIT =
            "You are trying enroll more than %s students in section \"%s\".";
    static final String ERROR_ENROLL_EXCEED_SECTION_LIMIT_INSTRUCTION =
            "To avoid performance problems, please do not enroll more than %s students in a single section.";

    private static final StudentsLogic instance = new StudentsLogic();

    private final StudentsDb studentsDb = StudentsDb.inst();

    private FeedbackResponsesLogic frLogic;
    private FeedbackSessionsLogic fsLogic;
    private DeadlineExtensionsLogic deLogic;

    private StudentsLogic() {
        // prevent initialization
    }

    public static StudentsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        frLogic = FeedbackResponsesLogic.inst();
        fsLogic = FeedbackSessionsLogic.inst();
        deLogic = DeadlineExtensionsLogic.inst();
    }

    /**
     * Creates a student.
     *
     * @return the created student
     * @throws InvalidParametersException if the student is not valid
     * @throws EntityAlreadyExistsException if the student already exists in the database
     */
    public StudentAttributes createStudent(StudentAttributes studentData)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return studentsDb.createEntity(studentData);
    }

    /**
     * Gets a student by unique constraint courseId-email.
     */
    public StudentAttributes getStudentForEmail(String courseId, String email) {
        return studentsDb.getStudentForEmail(courseId, email);
    }

    /**
     * Gets list of students by email.
     */
    public List<StudentAttributes> getAllStudentsForEmail(String email) {
        return studentsDb.getAllStudentsForEmail(email);
    }

    /**
     * Gets a student by unique constraint courseId-googleId.
     */
    public StudentAttributes getStudentForCourseIdAndGoogleId(String courseId, String googleId) {
        return studentsDb.getStudentForGoogleId(courseId, googleId);
    }

    /**
     * Gets a student by unique constraint registrationKey.
     */
    public StudentAttributes getStudentForRegistrationKey(String registrationKey) {
        return studentsDb.getStudentForRegistrationKey(registrationKey);
    }

    /**
     * Gets all students associated with a googleId.
     */
    public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
        return studentsDb.getStudentsForGoogleId(googleId);
    }

    /**
     * Gets the total number of students of a course.
     */
    public int getNumberOfStudentsForCourse(String courseId) {
        return studentsDb.getNumberOfStudentsForCourse(courseId);
    }

    /**
     * Gets all students of a course.
     */
    public List<StudentAttributes> getStudentsForCourse(String courseId) {
        return studentsDb.getStudentsForCourse(courseId);
    }

    /**
     * Gets the first {@code batchSize} students of a course.
     */
    public List<StudentAttributes> getStudentsForCourse(String courseId, int batchSize) {
        return studentsDb.getStudentsForCourse(courseId, batchSize);
    }

    /**
     * Gets all students of a section.
     */
    public List<StudentAttributes> getStudentsForSection(String sectionName, String courseId) {
        return studentsDb.getStudentsForSection(sectionName, courseId);
    }

    /**
     * Gets all students of a team.
     */
    public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
        return studentsDb.getStudentsForTeam(teamName, courseId);
    }

    /**
     * Gets all unregistered students of a course.
     */
    public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
        return studentsDb.getUnregisteredStudentsForCourse(courseId);
    }

    /**
     * Searches for students.
     *
     * @param instructors the constraint that restricts the search result
     */
    public List<StudentAttributes> searchStudents(String queryString, List<InstructorAttributes> instructors)
            throws SearchServiceException {
        return studentsDb.search(queryString, instructors);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search students in the whole system.
     * @return null if no result found
     */
    public List<StudentAttributes> searchStudentsInWholeSystem(String queryString)
            throws SearchServiceException {
        return studentsDb.searchStudentsInWholeSystem(queryString);
    }

    /**
     * Checks if all the given students exist in the given course.
     *
     * @throws EntityDoesNotExistException If some student does not exist in the course.
     */
    public void verifyAllStudentsExistInCourse(String courseId, Collection<String> studentEmailAddresses)
            throws EntityDoesNotExistException {
        boolean hasOnlyExistingStudents = studentsDb.hasExistingStudentsInCourse(courseId, studentEmailAddresses);
        if (!hasOnlyExistingStudents) {
            throw new EntityDoesNotExistException("There are students that do not exist in the course.");
        }
    }

    /**
     * Returns true if the user associated with the googleId is a student in any course in the system.
     */
    public boolean isStudentInAnyCourse(String googleId) {
        return studentsDb.hasStudentsForGoogleId(googleId);
    }

    /**
     * Returns true if the given student is in the given team of course.
     */
    boolean isStudentInTeam(String courseId, String teamName, String studentEmail) {

        StudentAttributes student = getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            return false;
        }

        List<StudentAttributes> teammates = getStudentsForTeam(teamName, courseId);
        for (StudentAttributes teammate : teammates) {
            if (teammate.getEmail().equals(student.getEmail())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the two given emails belong to the same team in the given course.
     */
    public boolean isStudentsInSameTeam(String courseId, String student1Email, String student2Email) {
        StudentAttributes student1 = getStudentForEmail(courseId, student1Email);
        if (student1 == null) {
            return false;
        }
        return isStudentInTeam(courseId, student1.getTeam(), student2Email);
    }

    /**
     * Updates a student by {@link StudentAttributes.UpdateOptions}.
     *
     * <p>If email changed, update by recreating the student and cascade update all responses
     * the student gives/receives as well as any deadline extensions given to the student.
     *
     * <p>If team changed, cascade delete all responses the student gives/receives within that team.
     *
     * <p>If section changed, cascade update all responses the student gives/receives.
     *
     * @return updated student
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the student cannot be found
     * @throws EntityAlreadyExistsException if the student cannot be updated
     *         by recreation because of an existent student
     */
    public StudentAttributes updateStudentCascade(StudentAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        StudentAttributes originalStudent = getStudentForEmail(updateOptions.getCourseId(), updateOptions.getEmail());
        StudentAttributes updatedStudent = studentsDb.updateStudent(updateOptions);

        // cascade email change, if any
        if (!originalStudent.getEmail().equals(updatedStudent.getEmail())) {
            frLogic.updateFeedbackResponsesForChangingEmail(
                    updatedStudent.getCourse(), originalStudent.getEmail(), updatedStudent.getEmail());
            fsLogic.updateFeedbackSessionsStudentDeadlinesWithNewEmail(originalStudent.getCourse(),
                    originalStudent.getEmail(), updatedStudent.getEmail());
            deLogic.updateDeadlineExtensionsWithNewEmail(
                    originalStudent.getCourse(), originalStudent.getEmail(), updatedStudent.getEmail(), false);
        }

        // adjust submissions if moving to a different team
        if (isTeamChanged(originalStudent.getTeam(), updatedStudent.getTeam())) {
            frLogic.updateFeedbackResponsesForChangingTeam(updatedStudent.getCourse(), updatedStudent.getEmail(),
                    originalStudent.getTeam(), updatedStudent.getTeam());
        }

        // update the new section name in responses
        if (isSectionChanged(originalStudent.getSection(), updatedStudent.getSection())) {
            frLogic.updateFeedbackResponsesForChangingSection(updatedStudent.getCourse(), updatedStudent.getEmail(),
                    originalStudent.getSection(), updatedStudent.getSection());
        }

        // TODO: check to delete comments for this section/team if the section/team is no longer existent in the course

        return updatedStudent;
    }

    /**
     * Resets the googleId associated with the student.
     */
    public void resetStudentGoogleId(String originalEmail, String courseId)
            throws EntityDoesNotExistException {
        try {
            updateStudentCascade(
                    StudentAttributes.updateOptionsBuilder(courseId, originalEmail)
                            .withGoogleId(null)
                            .build());
        } catch (InvalidParametersException | EntityAlreadyExistsException e) {
            assert false : "Resetting google ID shall not cause: " + e.getMessage();
        }
    }

    /**
     * Regenerates the registration key for the student with email address {@code email} in course {@code courseId}.
     *
     * @return the student attributes with the new registration key.
     * @throws EntityAlreadyExistsException if the newly generated course student has the same registration key as the
     *          original one.
     * @throws EntityDoesNotExistException if the student does not exist.
     */
    public StudentAttributes regenerateStudentRegistrationKey(String courseId, String email)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {

        StudentAttributes originalStudent = studentsDb.getStudentForEmail(courseId, email);
        if (originalStudent == null) {
            String errorMessage = String.format(
                    "The student with the email %s could not be found for the course with ID [%s].", email, courseId);
            throw new EntityDoesNotExistException(errorMessage);
        }

        return studentsDb.regenerateEntityKey(originalStudent);
    }

    /**
     * Validates sections for any limit violations and teams for any team name violations.
     */
    public void validateSectionsAndTeams(List<StudentAttributes> studentList, String courseId) throws EnrollException {

        List<StudentAttributes> mergedList = getMergedList(studentList, courseId);

        if (mergedList.size() < 2) { // no conflicts
            return;
        }

        String errorMessage = getSectionInvalidityInfo(mergedList) + getTeamInvalidityInfo(mergedList);

        if (!errorMessage.isEmpty()) {
            throw new EnrollException(errorMessage);
        }

    }

    private List<StudentAttributes> getMergedList(List<StudentAttributes> studentList, String courseId) {

        List<StudentAttributes> mergedList = new ArrayList<>();
        List<StudentAttributes> studentsInCourse = getStudentsForCourse(courseId);

        for (StudentAttributes student : studentList) {
            mergedList.add(student);
        }

        for (StudentAttributes student : studentsInCourse) {
            if (!isInEnrollList(student, mergedList)) {
                mergedList.add(student);
            }
        }
        return mergedList;
    }

    /**
     * Returns the section name for the given team name for the given course.
     */
    public String getSectionForTeam(String courseId, String teamName) {

        List<StudentAttributes> students = getStudentsForTeam(teamName, courseId);
        if (students.isEmpty()) {
            return Const.DEFAULT_SECTION;
        }
        return students.get(0).getSection();
    }

    private String getSectionInvalidityInfo(List<StudentAttributes> mergedList) {

        StudentAttributes.sortBySectionName(mergedList);

        List<String> invalidSectionList = new ArrayList<>();
        int studentsCount = 1;
        for (int i = 1; i < mergedList.size(); i++) {
            StudentAttributes currentStudent = mergedList.get(i);
            StudentAttributes previousStudent = mergedList.get(i - 1);
            if (currentStudent.getSection().equals(previousStudent.getSection())) {
                studentsCount++;
            } else {
                if (studentsCount > Const.SECTION_SIZE_LIMIT) {
                    invalidSectionList.add(previousStudent.getSection());
                }
                studentsCount = 1;
            }

            if (i == mergedList.size() - 1 && studentsCount > Const.SECTION_SIZE_LIMIT) {
                invalidSectionList.add(currentStudent.getSection());
            }
        }

        StringJoiner errorMessage = new StringJoiner(" ");
        for (String section : invalidSectionList) {
            errorMessage.add(String.format(
                    ERROR_ENROLL_EXCEED_SECTION_LIMIT,
                    Const.SECTION_SIZE_LIMIT, section));
        }

        if (!invalidSectionList.isEmpty()) {
            errorMessage.add(String.format(
                    ERROR_ENROLL_EXCEED_SECTION_LIMIT_INSTRUCTION,
                    Const.SECTION_SIZE_LIMIT));
        }

        return errorMessage.toString();
    }

    private String getTeamInvalidityInfo(List<StudentAttributes> mergedList) {
        StringJoiner errorMessage = new StringJoiner(" ");
        StudentAttributes.sortByTeamName(mergedList);

        List<String> invalidTeamList = new ArrayList<>();
        for (int i = 1; i < mergedList.size(); i++) {
            StudentAttributes currentStudent = mergedList.get(i);
            StudentAttributes previousStudent = mergedList.get(i - 1);
            if (currentStudent.getTeam().equals(previousStudent.getTeam())
                    && !currentStudent.getSection().equals(previousStudent.getSection())
                    && !invalidTeamList.contains(currentStudent.getTeam())) {

                errorMessage.add(String.format(ERROR_INVALID_TEAM_NAME,
                        currentStudent.getTeam(),
                        previousStudent.getSection(),
                        currentStudent.getSection()));

                invalidTeamList.add(currentStudent.getTeam());
            }
        }

        if (!invalidTeamList.isEmpty()) {
            errorMessage.add(ERROR_INVALID_TEAM_NAME_INSTRUCTION);
        }

        return errorMessage.toString();
    }

    /**
     * Deletes the first {@code batchSize} of the remaining students in the course cascade their
     * associated responses, deadline extensions, and comments.
     */
    public void deleteStudentsInCourseCascade(String courseId, int batchSize) {
        var studentsInCourse = getStudentsForCourse(courseId, batchSize);
        for (var student : studentsInCourse) {
            RequestTracer.checkRemainingTime();
            deleteStudentCascade(courseId, student.getEmail());
        }
    }

    /**
     * Deletes a student cascade its associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
     */
    public void deleteStudentCascade(String courseId, String studentEmail) {
        StudentAttributes student = getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            return;
        }

        frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(courseId, studentEmail);
        if (studentsDb.getStudentCountForTeam(student.getTeam(), student.getCourse()) == 1) {
            // the student is the only student in the team, delete responses related to the team
            frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(student.getCourse(), student.getTeam());
        }
        studentsDb.deleteStudent(courseId, studentEmail);
        fsLogic.deleteFeedbackSessionsDeadlinesForStudent(courseId, studentEmail);
        deLogic.deleteDeadlineExtensions(courseId, studentEmail, false);

        updateStudentResponsesAfterDeletion(courseId);
    }

    private void updateStudentResponsesAfterDeletion(String courseId) {
        frLogic.updateFeedbackResponsesForDeletingStudent(courseId);
    }

    /**
     * Deletes all students associated a googleId and cascade
     * its associated feedback responses, deadline extensions and comments.
     */
    public void deleteStudentsForGoogleIdCascade(String googleId) {
        List<StudentAttributes> students = getStudentsForGoogleId(googleId);

        // Cascade delete students
        for (StudentAttributes student : students) {
            deleteStudentCascade(student.getCourse(), student.getEmail());
        }
    }

    /**
     * Deletes students using {@link AttributesDeletionQuery}.
     */
    public void deleteStudents(AttributesDeletionQuery query) {
        studentsDb.deleteStudents(query);
        updateStudentResponsesAfterDeletion(query.getCourseId());
    }

    /**
     * Creates or updates search document for the given student.
     *
     * @param student the student to be put into documents
     */
    public void putDocument(StudentAttributes student) throws SearchServiceException {
        studentsDb.putDocument(student);
    }

    private boolean isInEnrollList(StudentAttributes student,
            List<StudentAttributes> studentInfoList) {
        for (StudentAttributes studentInfo : studentInfoList) {
            if (studentInfo.getEmail().equalsIgnoreCase(student.getEmail())) {
                return true;
            }
        }
        return false;
    }

    private boolean isTeamChanged(String originalTeam, String newTeam) {
        return newTeam != null && originalTeam != null
                && !originalTeam.equals(newTeam);
    }

    private boolean isSectionChanged(String originalSection, String newSection) {
        return newSection != null && originalSection != null
                && !originalSection.equals(newSection);
    }

    /**
     * Gets the number of students created within a specified time range.
     */
    int getNumStudentsByTimeRange(Instant startTime, Instant endTime) {
        return studentsDb.getNumStudentsByTimeRange(startTime, endTime);
    }

}
