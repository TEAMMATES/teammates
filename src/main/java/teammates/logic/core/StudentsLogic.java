package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;

import com.google.gson.Gson;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.storage.api.StudentsDb;

/**
 * Handles  operations related to student roles.
 */
public class StudentsLogic {
    //The API of this class doesn't have header comments because it sits behind
    //  the API of the logic class. Those who use this class is expected to be
    //  familiar with the its code and Logic's code. Hence, no need for header 
    //  comments.
    
    private static int SECTION_SIZE_LIMIT = 100;
    private static int SIZE_LIMIT_PER_ENROLLMENT = 150;

    private static StudentsLogic instance = null;
    private StudentsDb studentsDb = new StudentsDb();
    
    private CoursesLogic coursesLogic = CoursesLogic.inst();
    private FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private AccountsLogic accLogic = AccountsLogic.inst();
    private CommentsLogic commentsLogic = CommentsLogic.inst();
    
    @SuppressWarnings("unused")
    // it is used, just not in here, do not remove
    private static Logger log = Utils.getLogger();
    
    public static StudentsLogic inst() {
        if (instance == null)
            instance = new StudentsLogic();
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
    
    public void deleteDocument(StudentAttributes student){
        studentsDb.deleteDocument(student);
    }

    public StudentSearchResultBundle searchStudents(String queryString, String googleId, String cursorString){
        return studentsDb.search(queryString, googleId, cursorString);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the 
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search students in the whole system.
     * @param queryString
     * @param cursorString
     * @return null if no result found
     */
    public StudentSearchResultBundle searchStudentsInWholeSystem(String queryString, String cursorString){
        return studentsDb.searchStudentsInWholeSystem(queryString, cursorString);
    }
    
    public StudentProfileAttributes getStudentProfile(String googleId) {
        Assumption.assertNotNull(googleId);
        
        return accLogic.getStudentProfile(googleId);
    }
    
    public String getKeyForStudent(String courseId, String email) throws EntityDoesNotExistException {
        
        StudentAttributes studentData = getStudentForEmail(courseId, email);
    
        if (studentData == null) {
            throw new EntityDoesNotExistException("Student does not exist: [" + courseId + "/" + email + "]");
        }
    
        return studentData.key;
    }
    
    public String getEncryptedKeyForStudent(String courseId, String email) throws EntityDoesNotExistException {
        
        StudentAttributes studentData = getStudentForEmail(courseId, email);
        
        if (studentData == null) {
            throw new EntityDoesNotExistException("Student does not exist: [" + courseId + "/" + email + "]");
        }
    
        return StringHelper.encrypt(studentData.key);
    }

    public boolean isStudentInAnyCourse(String googleId) {
        return studentsDb.getStudentsForGoogleId(googleId).size()!=0;
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
        for(StudentAttributes teammate : teammates) {
            if (teammate.email.equals(student.email)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isStudentsInSameTeam(String courseId, String student1Email, String student2Email) {
        StudentAttributes student1 = getStudentForEmail(courseId, student1Email);
        if(student1 == null) {
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
        String finalEmail = (student.email == null || !validator
                .getInvalidityInfo(FieldType.EMAIL, student.email).isEmpty()) ?
                originalEmail : student.email;
        
        // cascade email changes to comments
        if (!originalStudent.email.equals(finalEmail)) {
            commentsLogic.updateStudentEmail(student.course, originalStudent.email, finalEmail);
        }
        
        // adjust submissions if moving to a different team
        if (isTeamChanged(originalStudent.team, student.team)) {
            frLogic.updateFeedbackResponsesForChangingTeam(student.course, finalEmail, originalStudent.team, student.team);
        }

        if(isSectionChanged(originalStudent.section, student.section)) {
            frLogic.updateFeedbackResponsesForChangingSection(student.course, finalEmail, originalStudent.section, student.section);
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
        
        if(!student.isValid()) {
            throw new InvalidParametersException(student.getInvalidityInfo());
        }
        
        studentsDb.updateStudent(student.course, originalEmail, student.name, student.team, student.section, student.email, student.googleId, student.comments, hasDocument);    
        
        // cascade email change, if any
        if (!originalEmail.equals(student.email)) {
            frLogic.updateFeedbackResponsesForChangingEmail(student.course, originalEmail, student.email);
            fsLogic.updateRespondantsForStudent(originalEmail, student.email, student.course);
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
        
        if(!originalStudent.isValid()) {
            throw new InvalidParametersException(originalStudent.getInvalidityInfo());
        }     
        studentsDb.updateStudent(originalStudent.course, originalEmail, originalStudent.name, 
                                 originalStudent.team, originalStudent.section, originalStudent.email, 
                                 originalStudent.googleId, originalStudent.comments, hasDocument);  
    }

    public List<StudentAttributes> enrollStudents(String enrollLines,
            String courseId)
            throws EntityDoesNotExistException, EnrollException, InvalidParametersException, EntityAlreadyExistsException {

        return enrollStudents(enrollLines, courseId, true);
    }

    public List<StudentAttributes> enrollStudentsWithoutDocument(String enrollLines,
            String courseId)
            throws EntityDoesNotExistException, EnrollException, InvalidParametersException, EntityAlreadyExistsException {

        return enrollStudents(enrollLines, courseId, false);
    }

    public List<StudentAttributes> enrollStudents(String enrollLines,
            String courseId, boolean hasDocument)
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
        validateSections(studentList, courseId);

        // TODO: can we use a batch persist operation here?
        // enroll all students
        for (StudentAttributes student : studentList) {
            StudentEnrollDetails enrollmentDetails;
            
            enrollmentDetails = enrollStudent(student, hasDocument);
            student.updateStatus = enrollmentDetails.updateStatus;
            
            enrollmentList.add(enrollmentDetails);
            returnList.add(student);
        }
        
        //Adjust submissions for all feedback responses within the course
        List<FeedbackSessionAttributes> feedbackSessions = FeedbackSessionsLogic.inst()
                .getFeedbackSessionsForCourse(courseId);
        
        for (FeedbackSessionAttributes session : feedbackSessions) {
            //Schedule adjustment of submissions for feedback session in course
            scheduleSubmissionAdjustmentForFeedbackInCourse(enrollmentList,courseId,
                    session.feedbackSessionName);
        }

        // add to return list students not included in the enroll list.
        List<StudentAttributes> studentsInCourse = getStudentsForCourse(courseId);
        for (StudentAttributes student : studentsInCourse) {
            if (!isInEnrollList(student, returnList)) {
                student.updateStatus = StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST;
                returnList.add(student);
            }
        }

        return returnList;
    }

    private void verifyIsWithinSizeLimitPerEnrollment(List<StudentAttributes> students) throws EnrollException {
        if(students.size() > SIZE_LIMIT_PER_ENROLLMENT) {
            throw new EnrollException(Const.StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED);
        }
    }

    public void validateSections(List<StudentAttributes> studentList, String courseId) throws EntityDoesNotExistException, EnrollException {

        List<StudentAttributes> mergedList = new ArrayList<StudentAttributes>();
        List<StudentAttributes> studentsInCourse = getStudentsForCourse(courseId);
        
        for(StudentAttributes student : studentList) {
            mergedList.add(student);
        }

        for(StudentAttributes student : studentsInCourse) {
            if(!isInEnrollList(student, mergedList)){
                mergedList.add(student);
            }
        }

        if(mergedList.size() < 2){ // no conflicts
            return;
        }
        
        String errorMessage = "";
        errorMessage += getSectionInvalidityInfo(mergedList);
        errorMessage += getTeamInvalidityInfo(mergedList);

        if(!errorMessage.equals("")){
            throw new EnrollException(errorMessage);
        }

    }

    public String getSectionForTeam(String courseId, String teamName){

        List<StudentAttributes> students = getStudentsForTeam(teamName, courseId);
        if(students.isEmpty()){
            return Const.DEFAULT_SECTION;
        } else {
            return students.get(0).section;
        }
    }

    private String getSectionInvalidityInfo(List<StudentAttributes> mergedList) {
        
        StudentAttributes.sortBySectionName(mergedList);

        List<String> invalidSectionList = new ArrayList<String>();
        int studentsCount = 1;
        for(int i = 1; i < mergedList.size(); i++){
            StudentAttributes currentStudent = mergedList.get(i);
            StudentAttributes previousStudent = mergedList.get(i-1);
            if(currentStudent.section.equals(previousStudent.section)){
                studentsCount++;
            } else {
                if(studentsCount > SECTION_SIZE_LIMIT){
                    invalidSectionList.add(previousStudent.section);
                }
                studentsCount = 1;
            }

            if(i == mergedList.size() - 1 && studentsCount > SECTION_SIZE_LIMIT){
                invalidSectionList.add(currentStudent.section);
            }
        }

        String errorMessage = "";
        for(String section: invalidSectionList){
            errorMessage += String.format(Const.StatusMessages.SECTION_QUOTA_EXCEED, section);
        }

        return errorMessage;
    }

    private String getTeamInvalidityInfo(List<StudentAttributes> mergedList) {

        StudentAttributes.sortByTeamName(mergedList);

        List<String> invalidTeamList = new ArrayList<String>();
        for(int i = 1; i < mergedList.size(); i++){
            StudentAttributes currentStudent = mergedList.get(i);
            StudentAttributes previousStudent = mergedList.get(i-1);
            if(currentStudent.team.equals(previousStudent.team) && !currentStudent.section.equals(previousStudent.section)){
                if(!invalidTeamList.contains(currentStudent.team)){
                    invalidTeamList.add(currentStudent.team);    
                }
            }
        }

        String errorMessage = "";
        for(String team : invalidTeamList){
            errorMessage += String.format(Const.StatusMessages.TEAM_INVALID_SECTION_EDIT, team);
        }
        if(!errorMessage.equals("")){
            errorMessage += "Please use the enroll page to edit multiple students";
        }

        return errorMessage;
    }

    private void scheduleSubmissionAdjustmentForFeedbackInCourse(
            ArrayList<StudentEnrollDetails> enrollmentList, String courseId, String sessionName) {
        // private methods -- should I test this?
        HashMap<String, String> paramMap = new HashMap<String, String>();
        
        paramMap.put(ParamsNames.COURSE_ID, courseId);
        paramMap.put(ParamsNames.FEEDBACK_SESSION_NAME, sessionName);
        
        Gson gsonBuilder = Utils.getTeammatesGson();
        String enrollmentDetails = gsonBuilder.toJson(enrollmentList);
        paramMap.put(ParamsNames.ENROLLMENT_DETAILS, enrollmentDetails);
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        taskQueueLogic.createAndAddTask(SystemParams.FEEDBACK_SUBMISSION_ADJUSTMENT_TASK_QUEUE,
                Const.ActionURIs.FEEDBACK_SUBMISSION_ADJUSTMENT_WORKER, paramMap);
        
    }

    public MimeMessage sendRegistrationInviteToStudent(String courseId, String studentEmail) 
            throws EntityDoesNotExistException {
        
        CourseAttributes course = coursesLogic.getCourse(courseId);
        if (course == null) {
            throw new EntityDoesNotExistException(
                    "Course does not exist [" + courseId + "], trying to send invite email to student [" + studentEmail + "]");
        }
        
        StudentAttributes studentData = getStudentForEmail(courseId, studentEmail);
        if (studentData == null) {
            throw new EntityDoesNotExistException(
                    "Student [" + studentEmail + "] does not exist in course [" + courseId + "]");
        }
        
        Emails emailMgr = new Emails();
        try {
            MimeMessage email = emailMgr.generateStudentCourseJoinEmail(course, studentData);
            emailMgr.sendEmailWithLogging(email);
            return email;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while sending email", e);
        }
        
    }
    
    public MimeMessage sendRegistrationInviteToStudentAfterGoogleIdReset(String courseId, String studentEmail) 
            throws EntityDoesNotExistException {
        
        CourseAttributes course = coursesLogic.getCourse(courseId);
        if (course == null) {
            throw new EntityDoesNotExistException(
                    "Course does not exist [" + courseId + "], trying to send invite email to student [" + studentEmail + "]");
        }
        
        StudentAttributes studentData = getStudentForEmail(courseId, studentEmail);
        if (studentData == null) {
            throw new EntityDoesNotExistException(
                    "Student [" + studentEmail + "] does not exist in course [" + courseId + "]");
        }
        
        Emails emailMgr = new Emails();
        try {
            MimeMessage email = emailMgr.generateStudentCourseRejoinEmailAfterGoogleIdReset(course, studentData);
            emailMgr.sendEmailWithLogging(email);
            return email;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while sending email", e);
        }
        
    }
    
    public List<MimeMessage> sendRegistrationInviteForCourse(String courseId) {
        List<StudentAttributes> studentDataList = getUnregisteredStudentsForCourse(courseId);
        
        ArrayList<MimeMessage> emailsSent = new ArrayList<MimeMessage>();
    
        //TODO: sending mail should be moved to somewhere else.
        for (StudentAttributes s : studentDataList) {
            try {
                MimeMessage email = sendRegistrationInviteToStudent(courseId, s.email);
                emailsSent.add(email);
            } catch (EntityDoesNotExistException e) {
                Assumption
                        .fail("Unexpected EntitiyDoesNotExistException thrown when sending registration email"
                                + TeammatesException.toStringWithStackTrace(e));
            }
        }
        return emailsSent;
    }

    public void deleteStudentCascade(String courseId, String studentEmail){
        deleteStudentCascade(courseId, studentEmail, true);
    }

    public void deleteStudentCascadeWithoutDocument(String courseId, String studentEmail){
        deleteStudentCascade(courseId, studentEmail, false);
    }

    public void deleteStudentCascade(String courseId, String studentEmail, boolean hasDocument) {
        // delete responses before deleting the student as we need to know the student's team.
        frLogic.deleteFeedbackResponsesForStudentAndCascade(courseId, studentEmail);
        commentsLogic.deleteCommentsForStudent(courseId, studentEmail);
        fsLogic.deleteStudentFromRespondantsList(getStudentForEmail(courseId, studentEmail));
        studentsDb.deleteStudent(courseId, studentEmail, hasDocument);
    }

    public void deleteStudentsForGoogleId(String googleId) {
        List<StudentAttributes> students = studentsDb.getStudentsForGoogleId(googleId);
        for(StudentAttributes student : students) {
            fsLogic.deleteStudentFromRespondantsList(student);
        }
        studentsDb.deleteStudentsForGoogleId(googleId);
    }

    public void deleteStudentsForGoogleIdWithoutDocument(String googleId) {
        List<StudentAttributes> students = studentsDb.getStudentsForGoogleId(googleId);
        for(StudentAttributes student : students) {
            fsLogic.deleteStudentFromRespondantsList(student);
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
            ArrayList<StudentEnrollDetails> enrollmentList,
            FeedbackResponseAttributes response) throws InvalidParametersException, EntityDoesNotExistException {
        for(StudentEnrollDetails enrollment : enrollmentList) {
            boolean isResponseDeleted = false;
            if(enrollment.updateStatus == UpdateStatus.MODIFIED &&
                    isTeamChanged(enrollment.oldTeam, enrollment.newTeam)) {
                isResponseDeleted = frLogic.updateFeedbackResponseForChangingTeam(enrollment, response);
            }
        
            if(!isResponseDeleted && enrollment.updateStatus == UpdateStatus.MODIFIED &&
                    isSectionChanged(enrollment.oldSection, enrollment.newSection)){
                frLogic.updateFeedbackResponseForChangingSection(enrollment, response);
            }
        }
    }
    
    public void putDocument(StudentAttributes student){
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

        if (validStudentAttributes.isEnrollInfoSameAs(originalStudentAttributes)) {
            enrollmentDetails.updateStatus = UpdateStatus.UNMODIFIED;
        } else if (originalStudentAttributes != null) {
            updateStudentCascadeWithSubmissionAdjustmentScheduled(originalStudentAttributes.email, validStudentAttributes, true);
            enrollmentDetails.updateStatus = UpdateStatus.MODIFIED;
            
            if(!originalStudentAttributes.team.equals(validStudentAttributes.team)) {
                enrollmentDetails.oldTeam = originalStudentAttributes.team;
            }
            if(!originalStudentAttributes.section.equals(validStudentAttributes.section)) {
                enrollmentDetails.oldSection = originalStudentAttributes.section;
            }
        } else {
            createStudentCascade(validStudentAttributes, hasDocument);
            enrollmentDetails.updateStatus = UpdateStatus.NEW;
        }

        return enrollmentDetails;
    }
    
    /* All empty lines or lines with only white spaces will be skipped.
     * The invalidity info returned are in HTML format.
     */
    private List<String> getInvalidityInfoInEnrollLines(String lines, String courseId) throws EnrollException {
        List<String> invalidityInfo = new ArrayList<String>();
        String[] linesArray = lines.split(Const.EOL);
        ArrayList<String>  studentEmailList = new ArrayList<String>();
    
        StudentAttributesFactory saf = new StudentAttributesFactory(linesArray[0]);
        
        for (int i = 1; i < linesArray.length; i++) {
            String line = linesArray[i];
            try {
                if (StringHelper.isWhiteSpace(line)) {
                    continue;
                }
                StudentAttributes student = saf.makeStudent(line, courseId);
                
                if (!student.isValid()) {
                    String info = StringHelper.toString(student.getInvalidityInfo(),
                                                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
                    invalidityInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, line, info));
                }
                
                if (isStudentEmailDuplicated(student.email, studentEmailList)){
                    String info = StringHelper.toString(getInvalidityInfoInDuplicatedEmail(student.email, studentEmailList,linesArray), 
                                                    "<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
                    invalidityInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, line, info));
                }
                
                studentEmailList.add(student.email);
            } catch (EnrollException e) {
                String info = String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, line, e.getMessage());
                invalidityInfo.add(info);
            }
        }
        
        return invalidityInfo;
    }
    
    private List<String> getInvalidityInfoInDuplicatedEmail(String email,
            ArrayList<String> studentEmailList,String[] linesArray){
        List<String> info = new ArrayList<String>();
        info.add("Same email address as the student in line \"" + linesArray[studentEmailList.indexOf(email) + 1]+ "\"");
        return info;
    }
    
    private boolean isStudentEmailDuplicated(String email, 
            ArrayList<String> studentEmailList){
        boolean isEmailDuplicated = studentEmailList.contains(email);
        return isEmailDuplicated;
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
        return (newTeam != null) && (originalTeam != null)
                && (!originalTeam.equals(newTeam));
    }

    private boolean isSectionChanged(String originalSection, String newSection) {
        return (newSection != null) && (originalSection != null)
                && (!originalSection.equals(newSection));
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