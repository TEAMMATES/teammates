package teammates.logic.core;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CourseEnrollmentResult;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.storage.api.StudentsDb;

/**
 * Handles operations related to students.
 * 
 * @see {@link StudentAttributes}
 * @see {@link StudentsDb}
 */
public final class StudentsLogic {
    
    private static final int SECTION_SIZE_LIMIT = 100;
    private static final int SIZE_LIMIT_PER_ENROLLMENT = 150;

    private static StudentsLogic instance = new StudentsLogic();
    
    private static final StudentsDb studentsDb = new StudentsDb();
    
    private static final CommentsLogic commentsLogic = CommentsLogic.inst();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final ProfilesLogic profilesLogic = ProfilesLogic.inst();
    
    private StudentsLogic() {
        // prevent initialization
    }
    
    public static StudentsLogic inst() {
        return instance;
    }

    public void createStudentCascade(StudentAttributes studentData)
            throws InvalidParametersException, EntityAlreadyExistsException,
            EntityDoesNotExistException {
        createStudentCascade(studentData, true);
    }

    public void createStudentCascadeWithoutDocument(
            StudentAttributes studentData) throws InvalidParametersException,
            EntityAlreadyExistsException, EntityDoesNotExistException {
        createStudentCascade(studentData, false);
    }
    
    public void createStudentCascade(StudentAttributes studentData, boolean hasDocument)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        studentsDb.createStudent(studentData, hasDocument);
        
        if (!coursesLogic.isCoursePresent(studentData.course)) {
            throw new EntityDoesNotExistException(
                    "Course does not exist [" + studentData.course + "]");
        }
        
    }

    @SuppressWarnings("deprecation")
    public List<StudentAttributes> getAllStudents() {
        return studentsDb.getAllStudents();
    }

    public StudentAttributes getStudentForEmail(String courseId, String email) {
        return studentsDb.getStudentForEmail(courseId, email);
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
    
    public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
        return studentsDb.getStudentsForTeam(teamName, courseId);
    }

    public List<StudentAttributes> getStudentsForSection(String sectionName, String courseId) {
        return studentsDb.getStudentsForSection(sectionName, courseId);
    }

    public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
        return studentsDb.getUnregisteredStudentsForCourse(courseId);
    }
    
    public void deleteDocument(StudentAttributes student) {
        studentsDb.deleteDocument(student);
    }

    public StudentSearchResultBundle searchStudents(String queryString, List<InstructorAttributes> instructors) {
        return studentsDb.search(queryString, instructors);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search students in the whole system.
     * @param queryString
     * @return null if no result found
     */
    public StudentSearchResultBundle searchStudentsInWholeSystem(String queryString) {
        return studentsDb.searchStudentsInWholeSystem(queryString);
    }
    
    public StudentProfileAttributes getStudentProfile(String googleId) {
        Assumption.assertNotNull(googleId);
        
        return profilesLogic.getStudentProfile(googleId);
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
    
    public void updateStudentCascade(String originalEmail,
            StudentAttributes student) throws InvalidParametersException,
            EntityDoesNotExistException {
        updateStudentCascade(originalEmail, student, true);
    }

    public void updateStudentCascadeWithoutDocument(String originalEmail,
            StudentAttributes student) throws InvalidParametersException,
            EntityDoesNotExistException {
        updateStudentCascade(originalEmail, student, false);
    }

    public void updateStudentCascade(String originalEmail, StudentAttributes student, boolean hasDocument)
            throws InvalidParametersException, EntityDoesNotExistException {
        StudentAttributes originalStudent = getStudentForEmail(student.course, originalEmail);
        updateStudentCascadeWithSubmissionAdjustmentScheduled(originalEmail, student, hasDocument);
        
        /* finalEmail is the string to be used to represent a student's email.
         * This is because:
         *  - originalEmail cannot be used when student's email is being updated with a new valid email
         *  - student.email cannot be used always because it is null when non-email attributes
         *    of a student are being updated or when the new email to be updated is invalid
         */
        FieldValidator validator = new FieldValidator();
        //Untested case: The deletion is not persisted immediately (i.e. persistence delay)
        //       Reason: Difficult to reproduce a persistence delay during testing
        String finalEmail = student.email == null
                                || !validator.getInvalidityInfoForEmail(student.email).isEmpty()
                            ? originalEmail
                            : student.email;
        
        // cascade email changes to comments
        if (!originalStudent.email.equals(finalEmail)) {
            commentsLogic.updateStudentEmail(student.course, originalStudent.email, finalEmail);
        }
        
        // adjust submissions if moving to a different team
        if (isTeamChanged(originalStudent.team, student.team)) {
            frLogic.updateFeedbackResponsesForChangingTeam(student.course, finalEmail, originalStudent.team, student.team);
        }

        if (isSectionChanged(originalStudent.section, student.section)) {
            frLogic.updateFeedbackResponsesForChangingSection(student.course, finalEmail, originalStudent.section,
                                                              student.section);
        }
        
        // TODO: check to delete comments for this section/team if the section/team is no longer existent in the course
    }
    
    public void updateStudentCascadeWithSubmissionAdjustmentScheduled(String originalEmail,
            StudentAttributes student, boolean hasDocument)
            throws EntityDoesNotExistException, InvalidParametersException {
        // Edit student uses KeepOriginal policy, where unchanged fields are set
        // as null. Hence, we can't do isValid() for student here.
        // After updateWithReferenceToExistingStudentRecord method called,
        // the student should be valid
    
        // here is like a db access that can be avoided if we really want to optimize the code
        studentsDb.verifyStudentExists(student.course, originalEmail);
        
        StudentAttributes originalStudent = getStudentForEmail(student.course, originalEmail);
        
        // prepare new student
        student.updateWithExistingRecord(originalStudent);
        
        if (!student.isValid()) {
            throw new InvalidParametersException(student.getInvalidityInfo());
        }
        
        studentsDb.updateStudent(student.course, originalEmail, student.name, student.team, student.section,
                                 student.email, student.googleId, student.comments, hasDocument, false);
        
        // cascade email change, if any
        if (!originalEmail.equals(student.email)) {
            frLogic.updateFeedbackResponsesForChangingEmail(student.course, originalEmail, student.email);
            fsLogic.updateRespondentsForStudent(originalEmail, student.email, student.course);
        }
    }
    
    public void resetStudentGoogleId(String originalEmail, String courseId, boolean hasDocument)
            throws EntityDoesNotExistException, InvalidParametersException {
        // Edit student uses KeepOriginal policy, where unchanged fields are set
        // as null. Hence, we can't do isValid() for student here.
        // After updateWithExistingRecordWithGoogleIdReset method called,
        // the student should be valid
    
        studentsDb.verifyStudentExists(courseId, originalEmail);
        StudentAttributes originalStudent = getStudentForEmail(courseId, originalEmail);
        originalStudent.googleId = null;
        
        if (!originalStudent.isValid()) {
            throw new InvalidParametersException(originalStudent.getInvalidityInfo());
        }
        studentsDb.updateStudent(originalStudent.course, originalEmail, originalStudent.name,
                                 originalStudent.team, originalStudent.section, originalStudent.email,
                                 originalStudent.googleId, originalStudent.comments, hasDocument, false);
    }

    public CourseEnrollmentResult enrollStudents(String enrollLines, String courseId)
            throws EntityDoesNotExistException, EnrollException, InvalidParametersException, EntityAlreadyExistsException {
        return enrollStudents(enrollLines, courseId, true);
    }

    public CourseEnrollmentResult enrollStudentsWithoutDocument(String enrollLines, String courseId)
            throws EntityDoesNotExistException, EnrollException, InvalidParametersException, EntityAlreadyExistsException {
        return enrollStudents(enrollLines, courseId, false);
    }

    private CourseEnrollmentResult enrollStudents(String enrollLines, String courseId, boolean hasDocument)
            throws EntityDoesNotExistException, EnrollException, InvalidParametersException, EntityAlreadyExistsException {
        
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException("Course does not exist :"
                    + courseId);
        }
        
        if (enrollLines.isEmpty()) {
            throw new EnrollException(Const.StatusMessages.ENROLL_LINE_EMPTY);
        }
        
        List<String> invalidityInfo = getInvalidityInfoInEnrollLines(enrollLines, courseId);
        if (!invalidityInfo.isEmpty()) {
            throw new EnrollException(StringHelper.toString(invalidityInfo, "<br>"));
        }
        
        ArrayList<StudentAttributes> returnList = new ArrayList<StudentAttributes>();
        ArrayList<StudentEnrollDetails> enrollmentList = new ArrayList<StudentEnrollDetails>();
        ArrayList<StudentAttributes> studentList = new ArrayList<StudentAttributes>();
        
        String[] linesArray = enrollLines.split(Const.EOL);

        StudentAttributesFactory saf = new StudentAttributesFactory(linesArray[0]);
        
        for (int i = 1; i < linesArray.length; i++) {
            String line = linesArray[i];
            
            if (StringHelper.isWhiteSpace(line)) {
                continue;
            }
            
            StudentAttributes student = saf.makeStudent(line, courseId);
            studentList.add(student);
        }

        verifyIsWithinSizeLimitPerEnrollment(studentList);
        validateSectionsAndTeams(studentList, courseId);

        // TODO: can we use a batch persist operation here?
        // enroll all students
        for (StudentAttributes student : studentList) {
            StudentEnrollDetails enrollmentDetails;
            
            enrollmentDetails = enrollStudent(student, hasDocument);
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
        if (students.size() > SIZE_LIMIT_PER_ENROLLMENT) {
            throw new EnrollException(Const.StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED);
        }
    }

    /**
     * Validates sections for any limit violations and teams for any team name violations.
     * @param studentList
     * @param courseId
     * @throws EnrollException
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
     * Validates teams for any team name violations
     * @param studentList
     * @param courseId
     * @throws EnrollException
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

        List<StudentAttributes> mergedList = new ArrayList<StudentAttributes>();
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

        List<String> invalidSectionList = new ArrayList<String>();
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

        List<String> invalidTeamList = new ArrayList<String>();
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
                                              Sanitizer.sanitizeForHtml(team)));
        }

        if (errorMessage.length() != 0) {
            errorMessage.append("Please use the enroll page to edit multiple students");
        }

        return errorMessage.toString();
    }

    public void deleteStudentCascade(String courseId, String studentEmail) {
        deleteStudentCascade(courseId, studentEmail, true);
    }

    public void deleteStudentCascadeWithoutDocument(String courseId, String studentEmail) {
        deleteStudentCascade(courseId, studentEmail, false);
    }

    public void deleteStudentCascade(String courseId, String studentEmail, boolean hasDocument) {
        // delete responses before deleting the student as we need to know the student's team.
        frLogic.deleteFeedbackResponsesForStudentAndCascade(courseId, studentEmail);
        commentsLogic.deleteCommentsForStudent(courseId, studentEmail);
        fsLogic.deleteStudentFromRespondentsList(getStudentForEmail(courseId, studentEmail));
        studentsDb.deleteStudent(courseId, studentEmail, hasDocument);
    }

    public void deleteStudentsForGoogleId(String googleId) {
        List<StudentAttributes> students = studentsDb.getStudentsForGoogleId(googleId);
        for (StudentAttributes student : students) {
            fsLogic.deleteStudentFromRespondentsList(student);
        }
        studentsDb.deleteStudentsForGoogleId(googleId);
    }

    public void deleteStudentsForGoogleIdWithoutDocument(String googleId) {
        List<StudentAttributes> students = studentsDb.getStudentsForGoogleId(googleId);
        for (StudentAttributes student : students) {
            fsLogic.deleteStudentFromRespondentsList(student);
        }
        studentsDb.deleteStudentsForGoogleIdWithoutDocument(googleId);
    }
    
    public void deleteStudentsForGoogleIdAndCascade(String googleId) {
        List<StudentAttributes> students = studentsDb.getStudentsForGoogleId(googleId);
        
        // Cascade delete students
        for (StudentAttributes student : students) {
            deleteStudentCascade(student.course, student.email);
        }
    }

    public void deleteStudentsForCourse(String courseId) {
        studentsDb.deleteStudentsForCourse(courseId);
    }

    public void deleteStudentsForCourseWithoutDocument(String courseId) {
        studentsDb.deleteStudentsForCourseWithoutDocument(courseId);
    }
    
    public void adjustFeedbackResponseForEnrollments(
            List<StudentEnrollDetails> enrollmentList,
            FeedbackResponseAttributes response) throws InvalidParametersException, EntityDoesNotExistException {
        for (StudentEnrollDetails enrollment : enrollmentList) {
            if (enrollment.updateStatus != StudentUpdateStatus.MODIFIED) {
                continue;
            }

            boolean isResponseDeleted = false;
            if (isTeamChanged(enrollment.oldTeam, enrollment.newTeam)) {
                isResponseDeleted = frLogic.updateFeedbackResponseForChangingTeam(enrollment, response);
            }
        
            if (!isResponseDeleted && isSectionChanged(enrollment.oldSection, enrollment.newSection)) {
                frLogic.updateFeedbackResponseForChangingSection(enrollment, response);
            }
        }
    }
    
    public void putDocument(StudentAttributes student) {
        studentsDb.putDocument(student);
    }
    
    private StudentEnrollDetails enrollStudent(StudentAttributes validStudentAttributes, Boolean hasDocument)
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
            updateStudentCascadeWithSubmissionAdjustmentScheduled(originalStudentAttributes.email,
                                                                  validStudentAttributes, true);
            enrollmentDetails.updateStatus = StudentUpdateStatus.MODIFIED;
            
            if (!originalStudentAttributes.team.equals(validStudentAttributes.team)) {
                enrollmentDetails.oldTeam = originalStudentAttributes.team;
            }
            if (!originalStudentAttributes.section.equals(validStudentAttributes.section)) {
                enrollmentDetails.oldSection = originalStudentAttributes.section;
            }
        } else {
            createStudentCascade(validStudentAttributes, hasDocument);
            enrollmentDetails.updateStatus = StudentUpdateStatus.NEW;
        }

        return enrollmentDetails;
    }
    
    /* All empty lines or lines with only white spaces will be skipped.
     * The invalidity info returned are in HTML format.
     */
    private List<String> getInvalidityInfoInEnrollLines(String lines, String courseId) throws EnrollException {
        List<String> invalidityInfo = new ArrayList<String>();
        String[] linesArray = lines.split(Const.EOL);
        ArrayList<String> studentEmailList = new ArrayList<String>();
    
        StudentAttributesFactory saf = new StudentAttributesFactory(linesArray[0]);
        
        for (int i = 1; i < linesArray.length; i++) {
            String line = linesArray[i];
            String sanitizedLine = Sanitizer.sanitizeForHtml(line);
            try {
                if (StringHelper.isWhiteSpace(line)) {
                    continue;
                }
                StudentAttributes student = saf.makeStudent(line, courseId);
                
                if (!student.isValid()) {
                    String info = StringHelper.toString(Sanitizer.sanitizeForHtml(student.getInvalidityInfo()),
                                                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
                    invalidityInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, sanitizedLine, info));
                }
                
                if (isStudentEmailDuplicated(student.email, studentEmailList)) {
                    String info =
                            StringHelper.toString(
                                    getInvalidityInfoInDuplicatedEmail(student.email, studentEmailList, linesArray),
                                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
                    invalidityInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, sanitizedLine, info));
                }
                
                studentEmailList.add(student.email);
            } catch (EnrollException e) {
                String info = String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, sanitizedLine, e.getMessage());
                invalidityInfo.add(info);
            }
        }
        
        return invalidityInfo;
    }
    
    private List<String> getInvalidityInfoInDuplicatedEmail(String email,
            ArrayList<String> studentEmailList, String[] linesArray) {
        List<String> info = new ArrayList<String>();
        info.add("Same email address as the student in line \"" + linesArray[studentEmailList.indexOf(email) + 1] + "\"");
        return info;
    }
    
    private boolean isStudentEmailDuplicated(String email,
            ArrayList<String> studentEmailList) {
        return studentEmailList.contains(email);
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
