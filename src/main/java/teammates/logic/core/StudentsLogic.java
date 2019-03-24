package teammates.logic.core;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.CourseEnrollmentResult;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.storage.api.StudentsDb;

/**
 * Handles operations related to students.
 *
 * @see StudentAttributes
 * @see StudentsDb
 */
public final class StudentsLogic {

    private static final int SECTION_SIZE_LIMIT = 100;

    private static StudentsLogic instance = new StudentsLogic();

    private static final StudentsDb studentsDb = new StudentsDb();

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    private StudentsLogic() {
        // prevent initialization
    }

    public static StudentsLogic inst() {
        return instance;
    }

    /**
     * Creates a student.
     *
     * @return the created student
     * @throws InvalidParametersException if the student is not valid
     * @throws EntityAlreadyExistsException if the student already exists in the Datastore
     */
    public StudentAttributes createStudent(StudentAttributes studentData)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return studentsDb.createEntity(studentData);
    }

    public StudentAttributes getStudentForEmail(String courseId, String email) {
        return studentsDb.getStudentForEmail(courseId, email);
    }

    public List<StudentAttributes> getAllStudentsForEmail(String email) {
        return studentsDb.getAllStudentsForEmail(email);
    }

    public StudentAttributes getStudentForCourseIdAndGoogleId(String courseId, String googleId) {
        return studentsDb.getStudentForGoogleId(courseId, googleId);
    }

    public StudentAttributes getStudentForRegistrationKey(String registrationKey) {
        return studentsDb.getStudentForRegistrationKey(registrationKey);
    }

    public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
        return studentsDb.getStudentsForGoogleId(googleId);
    }

    public List<StudentAttributes> getStudentsForCourse(String courseId) {
        return studentsDb.getStudentsForCourse(courseId);
    }

    /**
     * Gets all students of a team.
     */
    public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
        return studentsDb.getStudentsForTeam(teamName, courseId);
    }

    public List<StudentAttributes> getStudentsForSection(String sectionName, String courseId) {
        return studentsDb.getStudentsForSection(sectionName, courseId);
    }

    public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
        return studentsDb.getUnregisteredStudentsForCourse(courseId);
    }

    public StudentSearchResultBundle searchStudents(String queryString, List<InstructorAttributes> instructors) {
        return studentsDb.search(queryString, instructors);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search students in the whole system.
     * @return null if no result found
     */
    public StudentSearchResultBundle searchStudentsInWholeSystem(String queryString) {
        return studentsDb.searchStudentsInWholeSystem(queryString);
    }

    public String getEncryptedKeyForStudent(String courseId, String email) throws EntityDoesNotExistException {

        StudentAttributes studentData = getStudentForEmail(courseId, email);

        if (studentData == null) {
            throw new EntityDoesNotExistException("Student does not exist: [" + courseId + "/" + email + "]");
        }

        return StringHelper.encrypt(studentData.key);
    }

    public boolean isStudentInAnyCourse(String googleId) {
        return studentsDb.getStudentsForGoogleId(googleId).size() != 0;
    }

    public boolean isStudentInCourse(String courseId, String studentEmail) {
        return studentsDb.getStudentForEmail(courseId, studentEmail) != null;
    }

    public boolean isStudentInTeam(String courseId, String teamName, String studentEmail) {

        StudentAttributes student = getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            return false;
        }

        List<StudentAttributes> teammates = getStudentsForTeam(teamName, courseId);
        for (StudentAttributes teammate : teammates) {
            if (teammate.email.equals(student.email)) {
                return true;
            }
        }
        return false;
    }

    public boolean isStudentsInSameTeam(String courseId, String student1Email, String student2Email) {
        StudentAttributes student1 = getStudentForEmail(courseId, student1Email);
        if (student1 == null) {
            return false;
        }
        return isStudentInTeam(courseId, student1.team, student2Email);
    }

    /**
     * Updates a student by {@link StudentAttributes.UpdateOptions}.
     *
     * <p>If email changed, update by recreating the student and cascade update all responses the student gives/receives.
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
        if (!originalStudent.email.equals(updatedStudent.email)) {
            frLogic.updateFeedbackResponsesForChangingEmail(
                    updatedStudent.course, originalStudent.email, updatedStudent.email);
            fsLogic.updateRespondentsForStudent(originalStudent.email, updatedStudent.email, updatedStudent.course);
        }

        // adjust submissions if moving to a different team
        if (isTeamChanged(originalStudent.team, updatedStudent.team)) {
            frLogic.updateFeedbackResponsesForChangingTeam(updatedStudent.course, updatedStudent.email,
                    originalStudent.team, updatedStudent.team);
        }

        // update the new section name in responses
        if (isSectionChanged(originalStudent.section, updatedStudent.section)) {
            frLogic.updateFeedbackResponsesForChangingSection(updatedStudent.course, updatedStudent.email,
                    originalStudent.section, updatedStudent.section);
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
            Assumption.fail("Resting google ID shall not cause: " + e.getMessage());
        }
    }

    public CourseEnrollmentResult enrollStudents(String enrollLines, String courseId)
            throws EntityDoesNotExistException, EnrollException, InvalidParametersException, EntityAlreadyExistsException {

        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException("Course does not exist :"
                    + courseId);
        }

        if (enrollLines.isEmpty()) {
            throw new EnrollException(Const.StatusMessages.ENROLL_LINE_EMPTY);
        }

        List<StudentAttributes> studentList = buildStudents(enrollLines, courseId);
        ArrayList<StudentAttributes> returnList = new ArrayList<>();
        ArrayList<StudentEnrollDetails> enrollmentList = new ArrayList<>();

        verifyIsWithinSizeLimitPerEnrollment(studentList);
        validateSectionsAndTeams(studentList, courseId);

        // TODO: can we use a batch persist operation here?
        // enroll all students
        for (StudentAttributes student : studentList) {
            StudentEnrollDetails enrollmentDetails;

            enrollmentDetails = enrollStudent(student);
            student.updateStatus = enrollmentDetails.updateStatus;

            enrollmentList.add(enrollmentDetails);
            returnList.add(student);
        }

        // add to return list students not included in the enroll list.
        List<StudentAttributes> studentsInCourse = getStudentsForCourse(courseId);
        for (StudentAttributes student : studentsInCourse) {
            if (!isInEnrollList(student, returnList)) {
                student.updateStatus = StudentUpdateStatus.NOT_IN_ENROLL_LIST;
                returnList.add(student);
            }
        }

        return new CourseEnrollmentResult(returnList, enrollmentList);
    }

    private void verifyIsWithinSizeLimitPerEnrollment(List<StudentAttributes> students) throws EnrollException {
        if (students.size() > Const.SIZE_LIMIT_PER_ENROLLMENT) {
            throw new EnrollException(Const.StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED);
        }
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

    /**
     * Validates teams for any team name violations.
     */
    public void validateTeams(List<StudentAttributes> studentList, String courseId) throws EnrollException {

        List<StudentAttributes> mergedList = getMergedList(studentList, courseId);

        if (mergedList.size() < 2) { // no conflicts
            return;
        }

        String errorMessage = getTeamInvalidityInfo(mergedList);

        if (errorMessage.length() > 0) {
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

    public String getSectionForTeam(String courseId, String teamName) {

        List<StudentAttributes> students = getStudentsForTeam(teamName, courseId);
        if (students.isEmpty()) {
            return Const.DEFAULT_SECTION;
        }
        return students.get(0).section;
    }

    private String getSectionInvalidityInfo(List<StudentAttributes> mergedList) {

        StudentAttributes.sortBySectionName(mergedList);

        List<String> invalidSectionList = new ArrayList<>();
        int studentsCount = 1;
        for (int i = 1; i < mergedList.size(); i++) {
            StudentAttributes currentStudent = mergedList.get(i);
            StudentAttributes previousStudent = mergedList.get(i - 1);
            if (currentStudent.section.equals(previousStudent.section)) {
                studentsCount++;
            } else {
                if (studentsCount > SECTION_SIZE_LIMIT) {
                    invalidSectionList.add(previousStudent.section);
                }
                studentsCount = 1;
            }

            if (i == mergedList.size() - 1 && studentsCount > SECTION_SIZE_LIMIT) {
                invalidSectionList.add(currentStudent.section);
            }
        }

        StringBuilder errorMessage = new StringBuilder();
        for (String section : invalidSectionList) {
            errorMessage.append(String.format(Const.StatusMessages.SECTION_QUOTA_EXCEED, section));
        }

        return errorMessage.toString();
    }

    private String getTeamInvalidityInfo(List<StudentAttributes> mergedList) {

        StudentAttributes.sortByTeamName(mergedList);

        List<String> invalidTeamList = new ArrayList<>();
        for (int i = 1; i < mergedList.size(); i++) {
            StudentAttributes currentStudent = mergedList.get(i);
            StudentAttributes previousStudent = mergedList.get(i - 1);
            if (currentStudent.team.equals(previousStudent.team)
                    && !currentStudent.section.equals(previousStudent.section)
                    && !invalidTeamList.contains(currentStudent.team)) {
                invalidTeamList.add(currentStudent.team);
            }
        }

        StringBuilder errorMessage = new StringBuilder(100);
        for (String team : invalidTeamList) {
            errorMessage.append(String.format(Const.StatusMessages.TEAM_INVALID_SECTION_EDIT,
                                              SanitizationHelper.sanitizeForHtml(team)));
        }

        if (errorMessage.length() != 0) {
            errorMessage.append("Please use the enroll page to edit multiple students");
        }

        return errorMessage.toString();
    }

    /**
     * Deletes all the students in the course cascade their associated responses and comments.
     */
    public void deleteStudentsInCourseCascade(String courseId) {
        List<StudentAttributes> studentsInCourse = getStudentsForCourse(courseId);
        for (StudentAttributes student : studentsInCourse) {
            deleteStudentCascade(courseId, student.email);
        }
    }

    /**
     * Deletes a student cascade its associated feedback responses and comments.
     *
     * <p>Fails silently if the student does not exist.
     */
    public void deleteStudentCascade(String courseId, String studentEmail) {
        StudentAttributes student = getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            return;
        }

        frLogic.deleteFeedbackResponsesInvolvedStudentOfCourseCascade(courseId, studentEmail);
        if (studentsDb.getStudentsForTeam(student.getTeam(), student.getCourse()).size() == 1) {
            // the student is the only student in the team
            frLogic.deleteFeedbackResponsesInvolvedTeamOfCourseCascade(student.getCourse(), student.getTeam());
        }
        studentsDb.deleteStudent(courseId, studentEmail);
    }

    /**
     * Deletes all students associated a googleId and cascade its associated feedback responses and comments.
     */
    public void deleteStudentsForGoogleIdCascade(String googleId) {
        List<StudentAttributes> students = studentsDb.getStudentsForGoogleId(googleId);

        // Cascade delete students
        for (StudentAttributes student : students) {
            deleteStudentCascade(student.course, student.email);
        }
    }

    /**
     * Deletes students using {@link AttributesDeletionQuery}.
     */
    public void deleteStudents(AttributesDeletionQuery query) {
        studentsDb.deleteStudents(query);
    }

    /**
     * Batch creates or updates documents for the given students.
     */
    public void putDocuments(List<StudentAttributes> students) {
        studentsDb.putDocuments(students);
    }

    private StudentEnrollDetails enrollStudent(StudentAttributes validStudentAttributes)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        StudentAttributes originalStudentAttributes = getStudentForEmail(
                validStudentAttributes.course, validStudentAttributes.email);

        StudentEnrollDetails enrollmentDetails = new StudentEnrollDetails();
        enrollmentDetails.course = validStudentAttributes.course;
        enrollmentDetails.email = validStudentAttributes.email;
        enrollmentDetails.newTeam = validStudentAttributes.team;
        enrollmentDetails.newSection = validStudentAttributes.section;

        boolean isModifyingExistingStudent = originalStudentAttributes != null;
        if (validStudentAttributes.isEnrollInfoSameAs(originalStudentAttributes)) {
            enrollmentDetails.updateStatus = StudentUpdateStatus.UNMODIFIED;
        } else if (isModifyingExistingStudent) {
            updateStudentCascade(
                    StudentAttributes.updateOptionsBuilder(originalStudentAttributes.course, originalStudentAttributes.email)
                            .withName(validStudentAttributes.name)
                            .withTeamName(validStudentAttributes.team)
                            .withSectionName(validStudentAttributes.section)
                            .withComment(validStudentAttributes.comments)
                            .build());
            enrollmentDetails.updateStatus = StudentUpdateStatus.MODIFIED;

            if (!originalStudentAttributes.team.equals(validStudentAttributes.team)) {
                enrollmentDetails.oldTeam = originalStudentAttributes.team;
            }
            if (!originalStudentAttributes.section.equals(validStudentAttributes.section)) {
                enrollmentDetails.oldSection = originalStudentAttributes.section;
            }
        } else {
            createStudent(validStudentAttributes);
            enrollmentDetails.updateStatus = StudentUpdateStatus.NEW;
        }

        return enrollmentDetails;
    }

    /**
     * Builds {@code studentList} from user input {@code lines}. All empty lines or lines with only white spaces will
     * be skipped.
     *
     * @param lines the enrollment lines entered by the instructor.
     * @throws EnrollException if some of the student instances created are invalid. The exception message contains
     *         invalidity info for all invalid student instances in HTML format.
     */
    public List<StudentAttributes> buildStudents(String lines, String courseId) throws EnrollException {
        List<String> invalidityInfo = new ArrayList<>();
        String[] linesArray = lines.split(System.lineSeparator());
        List<StudentAttributes> studentList = new ArrayList<>();

        StudentAttributesFactory saf = new StudentAttributesFactory(linesArray[0]);

        for (int i = 1; i < linesArray.length; i++) {
            String line = linesArray[i];
            String sanitizedLine = SanitizationHelper.sanitizeForHtml(line);
            if (StringHelper.isWhiteSpace(line)) {
                continue;
            }
            try {
                StudentAttributes student = saf.makeStudent(line, courseId);

                if (!student.isValid()) {
                    invalidityInfo.add(invalidStudentInfo(sanitizedLine, student));
                }

                int duplicateEmailIndex = getDuplicateEmailIndex(student.email, studentList);
                if (duplicateEmailIndex != -1) {
                    invalidityInfo.add(duplicateEmailInfo(sanitizedLine, linesArray[duplicateEmailIndex + 1]));
                }

                studentList.add(student);
            } catch (EnrollException e) {
                invalidityInfo.add(enrollExceptionInfo(sanitizedLine, e.getMessage()));
            }
        }

        if (!invalidityInfo.isEmpty()) {
            throw new EnrollException(StringHelper.toString(invalidityInfo, "<br>"));
        }

        return studentList;
    }

    /**
     * Returns a {@code String} containing the invalid information of the {@code student}
     * and the corresponding sanitized invalid {@code userInput}.
     */
    private String invalidStudentInfo(String userInput, StudentAttributes student) {
        String info = StringHelper.toString(SanitizationHelper.sanitizeForHtml(student.getInvalidityInfo()),
                "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
        return String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, userInput, info);
    }

    /**
     * Returns the index of the first occurrence of the duplicate {@code email} in
     * {@code studentList}, or -1 if {@code email} is not a duplicate in {@code studentList}.
     */
    private int getDuplicateEmailIndex(String email, List<StudentAttributes> studentList) {
        for (int index = 0; index < studentList.size(); index++) {
            if (studentList.get(index).email.equals(email)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Returns a {@code String} containing the duplicate email information in {@code duplicateEmailInfo} and
     * the corresponding sanitized invalid {@code userInput}.
     */
    private String duplicateEmailInfo(String userInput, String duplicateEmailInfo) {
        String info =
                Const.StatusMessages.DUPLICATE_EMAIL_INFO + " \"" + duplicateEmailInfo + "\""
                + "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ";
        return String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, userInput, info);
    }

    /**
     * Returns a {@code String} containing the enrollment exception information using the {@code errorMessage}
     * and the corresponding sanitized invalid {@code userInput}.
     */
    private String enrollExceptionInfo(String userInput, String errorMessage) {
        return String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, userInput, errorMessage);
    }

    private boolean isInEnrollList(StudentAttributes student,
            List<StudentAttributes> studentInfoList) {
        for (StudentAttributes studentInfo : studentInfoList) {
            if (studentInfo.email.equalsIgnoreCase(student.email)) {
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

    public TeamDetailsBundle getTeamDetailsForStudent(StudentAttributes student) {
        if (student != null) {
            TeamDetailsBundle teamResult = new TeamDetailsBundle();
            teamResult.name = student.team;
            teamResult.students = getStudentsForTeam(student.team, student.course);
            StudentAttributes.sortByNameAndThenByEmail(teamResult.students);
            return teamResult;
        }
        return null;
    }

}
