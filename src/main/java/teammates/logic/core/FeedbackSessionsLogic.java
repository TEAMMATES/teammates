package teammates.logic.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.datatransfer.UserType.Role;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.NotImplementedException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.SystemParams;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Utils;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;
import teammates.storage.entity.FeedbackResponse;

public class FeedbackSessionsLogic {

    private static FeedbackSessionsLogic instance = null;

    @SuppressWarnings("unused")
    private static Logger log = Utils.getLogger();
    // Used by the FeedbackSessionsLogicTest for logging

    private static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final int QUESTION_NUM_FOR_RESPONSE_RATE = -1;
    private static final int EMAIL_NAME_PAIR = 0;
    private static final int EMAIL_LASTNAME_PAIR = 1;
    private static final int EMAIL_TEAMNAME_PAIR = 2;

    public static FeedbackSessionsLogic inst() {
        if (instance == null)
            instance = new FeedbackSessionsLogic();
        return instance;
    }

    // TODO: in general, try to reduce method length and nesting-level in
    // Feedback*Logic classes.

    public void createFeedbackSession(FeedbackSessionAttributes fsa)
            throws InvalidParametersException, EntityAlreadyExistsException {
        fsDb.createEntity(fsa);
    }

    

    public List<FeedbackSessionAttributes> getAllOpenFeedbackSessions(Date start, Date end, double zone) {
        
        return fsDb.getAllOpenFeedbackSessions(start, end, zone);
    }
    
    /**
     * This method returns a single feedback session. Returns null if not found.
     */
    public FeedbackSessionAttributes getFeedbackSession(String feedbackSessionName, String courseId) {
        return fsDb.getFeedbackSession(courseId, feedbackSessionName);
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(
            String courseId) {
        return fsDb.getFeedbackSessionsForCourse(courseId);
    }

    public FeedbackSessionAttributes copyFeedbackSession(String newFeedbackSessionName,
            String newCourseId, String feedbackSessionName, String courseId, String instructorEmail)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        FeedbackSessionAttributes copiedFeedbackSession = getFeedbackSession(feedbackSessionName, courseId);
        copiedFeedbackSession.creatorEmail = instructorEmail;
        copiedFeedbackSession.feedbackSessionName = newFeedbackSessionName;
        copiedFeedbackSession.courseId = newCourseId;
        copiedFeedbackSession.createdTime = new Date();
        copiedFeedbackSession.respondingInstructorList = new HashSet<String>();
        copiedFeedbackSession.respondingStudentList = new HashSet<String>();
        fsDb.createEntity(copiedFeedbackSession);
        
        List<FeedbackQuestionAttributes> feedbackQuestions =
                fqLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        for (FeedbackQuestionAttributes question : feedbackQuestions){
            question.courseId = newCourseId;
            question.feedbackSessionName = newFeedbackSessionName;
            question.creatorEmail = instructorEmail;
            fqLogic.createFeedbackQuestionNoIntegrityCheck(question, question.questionNumber);
        }
        
        return copiedFeedbackSession;
    }

    /**
     * Checks if the specified course exists, then gets the feedback sessions for
     * the specified user in the course if it does exist.
     * 
     * @param courseId
     * @param userEmail
     * @return a list of viewable feedback sessions for any user for his course.
     * @throws EntityDoesNotExistException
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForUserInCourse(
            String courseId, String userEmail)
            throws EntityDoesNotExistException {

        if (coursesLogic.isCoursePresent(courseId) == false) {
            throw new EntityDoesNotExistException(
                    "Trying to get feedback sessions for a course that does not exist.");
        }
        return getFeedbackSessionsForUserInCourseSkipCheck(courseId, userEmail);
    }

    /**
     * Gets the feedback sessions for the specified user in the specified course
     * without checking for the course's existence.<br>
     * This method is usually called after the course's existence is assumed or
     * has been verified.
     * 
     * @param courseId
     * @param userEmail
     * @return a list of viewable feedback sessions for any user for his course.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForUserInCourseSkipCheck(
            String courseId, String userEmail) {
        List<FeedbackSessionAttributes> sessions =
                getFeedbackSessionsForCourse(courseId);
        List<FeedbackSessionAttributes> viewableSessions = new ArrayList<FeedbackSessionAttributes>();
        if (!sessions.isEmpty()) {
            InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
            boolean isInstructorOfCourse = instructor != null;
            for (FeedbackSessionAttributes session : sessions) {
                if (isFeedbackSessionViewableTo(session, userEmail, isInstructorOfCourse)) {
                    viewableSessions.add(session);
                }
            }
        }

        return viewableSessions;
    }

    /**
     * Returns a {@code List} of all feedback sessions bundled with their
     * response statistics for a instructor given by his googleId.<br>
     * Does not return private sessions unless the instructor is the creator.
     * 
     * @param googleId
     * @return
     * @throws EntityDoesNotExistException
     */
    public List<FeedbackSessionDetailsBundle> getFeedbackSessionDetailsForInstructor(
            String googleId)
            throws EntityDoesNotExistException {

        return getFeedbackSessionDetailsForInstructor(googleId, false);
    }
    
    /**
     * Returns a {@code List} of all feedback sessions bundled with their
     * response statistics for a instructor given by his googleId.<br>
     * Does not return private sessions unless the instructor is the creator.
     * <br>
     * Omits archived sessions if omitArchived == true
     * 
     * @param googleId
     * @return
     * @throws EntityDoesNotExistException
     */
    public List<FeedbackSessionDetailsBundle> getFeedbackSessionDetailsForInstructor(
            String googleId, boolean omitArchived)
            throws EntityDoesNotExistException {

        List<FeedbackSessionDetailsBundle> fsDetails = new ArrayList<FeedbackSessionDetailsBundle>();
        List<InstructorAttributes> instructors =
                instructorsLogic.getInstructorsForGoogleId(googleId, omitArchived);

        for (InstructorAttributes instructor : instructors) {
            fsDetails.addAll(getFeedbackSessionDetailsForCourse(
                    instructor.courseId, instructor.email));
        }

        return fsDetails;
    }

    /**
     * Returns a {@code List} of all feedback sessions WITHOUT their response
     * statistics for a instructor given by his googleId.<br>
     * Does not return private sessions unless the instructor is the creator.
     * 
     * @param googleId
     * @return
     * @throws EntityDoesNotExistException
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(
            String googleId)
            throws EntityDoesNotExistException {

        return getFeedbackSessionsListForInstructor(googleId, false);
    }
    
    /**
     * Returns a {@code List} of all feedback sessions WITHOUT their response
     * statistics for a instructor given by his googleId.<br>
     * Does not return private sessions unless the instructor is the creator.
     * <br>
     * Omits sessions from archived courses if omitArchived == true
     * 
     * @param googleId
     * @return
     * @throws EntityDoesNotExistException
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(
            String googleId, boolean omitArchived)
            throws EntityDoesNotExistException {

        List<InstructorAttributes> instructorList =
                instructorsLogic.getInstructorsForGoogleId(googleId, omitArchived);


        return getFeedbackSessionsListForInstructor(instructorList);
    }
    
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(
            List<InstructorAttributes> instructorList) throws EntityDoesNotExistException {

        List<FeedbackSessionAttributes> fsList = new ArrayList<FeedbackSessionAttributes>();
        
        for (InstructorAttributes instructor : instructorList) {
            fsList.addAll(getFeedbackSessionsListForCourse(instructor.courseId, instructor.email));
        }

        return fsList;
    }
    
    public List<FeedbackSessionAttributes> getFeedbackSessionListForInstructor(
            InstructorAttributes instructor) throws EntityDoesNotExistException {
        return getFeedbackSessionsListForCourse(instructor.courseId, instructor.email);
    }

    /**
     * Gets {@code FeedbackQuestions} and previously filled
     * {@code FeedbackResponses} that an instructor can view/submit as a
     * {@link FeedbackSessionQuestionsBundle}
     */
    public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsForInstructor(
            String feedbackSessionName, String courseId, String userEmail)
            throws EntityDoesNotExistException {

        FeedbackSessionAttributes fsa = fsDb.getFeedbackSession(
                courseId, feedbackSessionName);

        if (fsa == null) {
            throw new EntityDoesNotExistException(
                    "Trying to get a feedback session that does not exist.");
        }

        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle
            = new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>(); 
        Map<String, Map<String,String>> recipientList
            = new HashMap<String, Map<String,String>>();
        
        List<FeedbackQuestionAttributes> questions =
                fqLogic.getFeedbackQuestionsForInstructor(feedbackSessionName,
                        courseId, userEmail);
        
      InstructorAttributes instructorGiver = instructor;
      StudentAttributes studentGiver = null;

        for (FeedbackQuestionAttributes question : questions) {

            updateBundleAndRecipientListWithResponsesForInstructor(courseId, 
                    userEmail, fsa, instructor, bundle, recipientList,
                    question, instructorGiver, studentGiver);
        }

        return new FeedbackSessionQuestionsBundle(fsa, bundle, recipientList);
    }
    
    public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsForInstructor(
            String feedbackSessionName, String courseId, String feedbackQuestionId, String userEmail)
            throws EntityDoesNotExistException {

        FeedbackSessionAttributes fsa = fsDb.getFeedbackSession(
                courseId, feedbackSessionName);

        if (fsa == null) {
            throw new EntityDoesNotExistException(
                    "Trying to get a feedback session that does not exist.");
        }

        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle
            = new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>(); 
        Map<String, Map<String,String>> recipientList
            = new HashMap<String, Map<String,String>>();
        
        
        FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(feedbackQuestionId);
        
        InstructorAttributes instructorGiver = instructor;
        StudentAttributes studentGiver = null;

        updateBundleAndRecipientListWithResponsesForInstructor(courseId,
                userEmail, fsa, instructor, bundle, recipientList,
                question, instructorGiver, studentGiver);
    

        return new FeedbackSessionQuestionsBundle(fsa, bundle, recipientList);
    }

    private void updateBundleAndRecipientListWithResponsesForInstructor(
            String courseId,
            String userEmail,
            FeedbackSessionAttributes fsa,
            InstructorAttributes instructor,
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle,
            Map<String, Map<String, String>> recipientList,
            FeedbackQuestionAttributes question,
            InstructorAttributes instructorGiver, StudentAttributes studentGiver)
            throws EntityDoesNotExistException {
        List<FeedbackResponseAttributes> responses =
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                        question.getId(), userEmail);
        Map<String, String> recipients =
                fqLogic.getRecipientsForQuestion(question, userEmail, instructorGiver, studentGiver);
        // instructor can only see students in allowed sections for him/her
        if (question.recipientType.equals(FeedbackParticipantType.STUDENTS)) {
            Iterator<Map.Entry<String, String>> iter = recipients.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> studentEntry = iter.next();
                StudentAttributes student = studentsLogic.getStudentForEmail(courseId, studentEntry.getKey());
                if (!instructor.isAllowedForPrivilege(student.section, 
                        fsa.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {
                    iter.remove();
                }
            }
        }
        // instructor can only see teams in allowed sections for him/her
        if (question.recipientType.equals(FeedbackParticipantType.TEAMS)) {
            Iterator<Map.Entry<String, String>> iter = recipients.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> teamEntry = iter.next();
                String teamSection = studentsLogic.getSectionForTeam(courseId, teamEntry.getKey());
                if (!instructor.isAllowedForPrivilege(teamSection,
                        fsa.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {
                    iter.remove();
                }
            }
        }
        normalizeMaximumResponseEntities(question, recipients);

        bundle.put(question, responses);
        recipientList.put(question.getId(), recipients);
    }

    /**
     * Gets {@code FeedbackQuestions} and previously filled
     * {@code FeedbackResponses} that a student can view/submit as a
     * {@link FeedbackSessionQuestionsBundle}
     */
    public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsForStudent(
            String feedbackSessionName, String courseId, String userEmail)
            throws EntityDoesNotExistException {

        FeedbackSessionAttributes fsa = fsDb.getFeedbackSession(
                courseId, feedbackSessionName);
        StudentAttributes student = studentsLogic.getStudentForEmail(courseId,
                userEmail);

        if (fsa == null) {
            throw new EntityDoesNotExistException(
                    "Trying to get a feedback session that does not exist.");
        }
        if (student == null) {
            throw new EntityDoesNotExistException(
                    "Trying to get a feedback session for student that does not exist.");
        }

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle = new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        Map<String, Map<String, String>> recipientList = new HashMap<String, Map<String, String>>();

        List<FeedbackQuestionAttributes> questions = fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName,
                courseId);


        InstructorAttributes instructorGiver = null;
        StudentAttributes studentGiver = student;

        for (FeedbackQuestionAttributes question : questions) {

            updateBundleAndRecipientListWithResponses(userEmail, student,
                    bundle, recipientList, question, instructorGiver,
                    studentGiver);
        }

        return new FeedbackSessionQuestionsBundle(fsa, bundle, recipientList);
    }
    
    public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsForStudent(
            String feedbackSessionName, String courseId, String feedbackQuestionId, String userEmail)
            throws EntityDoesNotExistException {

        FeedbackSessionAttributes fsa = fsDb.getFeedbackSession(
                courseId, feedbackSessionName);
        StudentAttributes student = studentsLogic.getStudentForEmail(courseId,
                userEmail);

        if (fsa == null) {
            throw new EntityDoesNotExistException(
                    "Trying to get a feedback session that does not exist.");
        }
        if (student == null) {
            throw new EntityDoesNotExistException(
                    "Trying to get a feedback session for student that does not exist.");
        }

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle = new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        Map<String, Map<String, String>> recipientList = new HashMap<String, Map<String, String>>();

        FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(feedbackQuestionId);

        InstructorAttributes instructorGiver = null;
        StudentAttributes studentGiver = student;

        
        updateBundleAndRecipientListWithResponses(userEmail, student,
                bundle, recipientList, question, instructorGiver,
                studentGiver);
        

        return new FeedbackSessionQuestionsBundle(fsa, bundle, recipientList);
    }

    private void updateBundleAndRecipientListWithResponses(
            String userEmail,
            StudentAttributes student,
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle,
            Map<String, Map<String, String>> recipientList,
            FeedbackQuestionAttributes question,
            InstructorAttributes instructorGiver, StudentAttributes studentGiver)
            throws EntityDoesNotExistException {
        List<FeedbackResponseAttributes> responses =
                frLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(
                        question, student);
        Map<String, String> recipients =
                fqLogic.getRecipientsForQuestion(question, userEmail, instructorGiver, studentGiver);
        normalizeMaximumResponseEntities(question, recipients);

        bundle.put(question, responses);
        recipientList.put(question.getId(), recipients);
    }
    
    public FeedbackSessionResponseStatus getFeedbackSessionResponseStatus(String feedbackSessionName, String courseId) throws EntityDoesNotExistException{
        
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        FeedbackSessionAttributes session = fsDb.getFeedbackSession(
                courseId, feedbackSessionName);

        if (session == null) {
            throw new EntityDoesNotExistException(
                    "Trying to view non-existent feedback session.");
        }

        List<FeedbackQuestionAttributes> allQuestions = fqLogic.getFeedbackQuestionsForSession(feedbackSessionName,
                        courseId);
        
        return getFeedbackSessionResponseStatus(session, roster, allQuestions);
    }
    
    
    /**
     * Gets results of a feedback session to show to an instructor from an indicated question
     * This will not retrieve the list of comments for this question
     * @throws ExceedingRangeException if the results are beyond the range
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromQuestion(
            String feedbackSessionName, String courseId, String userEmail, int questionNumber)
                    throws EntityDoesNotExistException{

        // Load details of students and instructors once and pass it to callee
        // methods
        // (rather than loading them many times).
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put("isIncludeResponseStatus", "true");
        params.put("inSection", "false");
        params.put("fromSection", "fasle");
        params.put("toSection", "false");
        params.put("questionNum", String.valueOf(questionNumber));
        
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail, UserType.Role.INSTRUCTOR, roster, params);
    }
    
    /**
     * Gets results of a feedback session to show to an instructor from an indicated question 
     * and in a section
     * This will not retrieve the list of comments for this question
     * @throws ExceedingRangeException if the results are beyond the range
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromQuestionInSection(
                                                String feedbackSessionName, String courseId, String userEmail, 
                                                int questionNumber, String selectedSection)
                                        throws EntityDoesNotExistException{

        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put("isIncludeResponseStatus", "true");
        params.put("inSection", "true");
        params.put("fromSection", "false");
        params.put("toSection", "false");
        params.put("questionNum", String.valueOf(questionNumber));
        params.put("section", selectedSection);
        
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail, UserType.Role.INSTRUCTOR, roster, params);
    }

    /**
     * Gets results of a feedback session to show to an instructor in an indicated range
     * @throws ExceedingRangeException if the results are beyond the range
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorWithinRangeFromView(
            String feedbackSessionName, String courseId, String userEmail, long range, String viewType)
            throws EntityDoesNotExistException{
        
       return getFeedbackSessionResultsForInstructorInSectionWithinRangeFromView(feedbackSessionName, courseId, userEmail, null , range, viewType);
    }

    /**
     * Gets results of a feedback session to show to an instructor in a section in an indicated range
     * @throws ExceedingRangeException if the results are beyond the range
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorInSectionWithinRangeFromView(
            String feedbackSessionName, String courseId, String userEmail, String section, long range, String viewType)
            throws EntityDoesNotExistException{
        
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put("isIncludeResponseStatus", "true");
        params.put("inSection", "true");
        params.put("fromSection", "false");
        params.put("toSection", "false");
        params.put("section", section);
        if(range > 0){
            params.put("range", String.valueOf(range));
        }
        params.put("viewType", viewType);

        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail, UserType.Role.INSTRUCTOR, roster, params);
    }

    /**
     * Gets results of a feedback session to show to an instructor in a section in an indicated range
     * @throws ExceedingRangeException if the results are beyond the range
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromSectionWithinRange(
            String feedbackSessionName, String courseId, String userEmail, String section, long range)
            throws EntityDoesNotExistException{
        
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put("isIncludeResponseStatus", "true");
        params.put("inSection", "false");
        params.put("fromSection", "true");
        params.put("toSection", "false");
        params.put("section", section);
        if(range > 0){
            params.put("range", String.valueOf(range));
        }
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail, UserType.Role.INSTRUCTOR, roster, params);
    }

    /**
     * Gets results of a feedback session to show to an instructor in a section in an indicated range
     * @throws ExceedingRangeException if the results are beyond the range
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorToSectionWithinRange(
            String feedbackSessionName, String courseId, String userEmail, String section, long range)
            throws EntityDoesNotExistException{
        
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put("isIncludeResponseStatus", "true");
        params.put("inSection", "false");
        params.put("fromSection", "false");
        params.put("toSection", "true");
        params.put("section", section);
        if(range > 0){
            params.put("range", String.valueOf(range));
        }
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail, UserType.Role.INSTRUCTOR, roster, params);
    }
    
    /**
     * Gets results of a feedback session to show to an instructor.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructor(
            String feedbackSessionName, String courseId, String userEmail)
            throws EntityDoesNotExistException {
        
       return getFeedbackSessionResultsForInstructorInSection(feedbackSessionName, courseId, userEmail, null);
    }

    /**
     * Gets results of a feedback session to show to an instructor for a
     * specific section
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorInSection(
            String feedbackSessionName, String courseId, String userEmail,
            String section)
            throws EntityDoesNotExistException {
        
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put("isIncludeResponseStatus", "true");
        params.put("inSection", "true");
        params.put("fromSection", "false");
        params.put("toSection", "false");
        params.put("section", section);
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName,
                courseId, userEmail, UserType.Role.INSTRUCTOR, roster, params);
    }

    /**
     *  Gets results of  a feedback session to show to an instructor from a 
     *  specific section
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromSection(
            String feedbackSessionName, String courseId, String userEmail,
            String section)
            throws EntityDoesNotExistException {

        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put("isIncludeResponseStatus", "false");
        params.put("inSection", "false");
        params.put("fromSection", "true");
        params.put("toSection", "false");
        params.put("section", section);
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName,
                courseId, userEmail, UserType.Role.INSTRUCTOR, roster, params);
    }

    /**
     *  Gets results of  a feedback session to show to an instructor to a 
     *  specific section
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorToSection(
            String feedbackSessionName, String courseId, String userEmail,
            String section)
            throws EntityDoesNotExistException {

        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put("isIncludeResponseStatus", "true");
        params.put("inSection", "false");
        params.put("fromSection", "false");
        params.put("toSection", "true");
        params.put("section", section);
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName,
                courseId, userEmail, UserType.Role.INSTRUCTOR, roster, params);
    }

    /**
     * Gets results of a feedback session in a roster to show to an instructor.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructor(
            String feedbackSessionName, String courseId, String userEmail,
            CourseRoster roster, Boolean isIncludeResponseStatus)
            throws EntityDoesNotExistException {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("isIncludeResponseStatus", String.valueOf(isIncludeResponseStatus));
        params.put("inSection", "true");
        params.put("fromSection", "false");
        params.put("toSection", "false");
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName,
                courseId, userEmail,
                UserType.Role.INSTRUCTOR, roster, params);
    }

    /**
     * Gets results of a feedback session to show to a student.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForStudent(
            String feedbackSessionName, String courseId, String userEmail)
            throws EntityDoesNotExistException {
        return getFeedbackSessionResultsForUserInSectionByQuestions(
                feedbackSessionName, courseId, userEmail,
                UserType.Role.STUDENT, null);
    }
    
    /**
     * Gets results of a feedback session to show to a student.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForStudent(
            String feedbackSessionName, String courseId, String userEmail, CourseRoster roster)
            throws EntityDoesNotExistException {
        return getFeedbackSessionResultsForUserInSectionByQuestions(
                feedbackSessionName, courseId, userEmail,
                UserType.Role.STUDENT, null, roster);
    }

    public String getFeedbackSessionResultsSummaryAsCsv(
            String feedbackSessionName, String courseId, String userEmail)
            throws UnauthorizedAccessException, EntityDoesNotExistException, ExceedingRangeException {
        
        return getFeedbackSessionResultsSummaryInSectionAsCsv(feedbackSessionName, courseId, userEmail, null);
    }

    public String getFeedbackSessionResultsSummaryInSectionAsCsv(
            String feedbackSessionName, String courseId, String userEmail, String section)
            throws UnauthorizedAccessException, EntityDoesNotExistException, ExceedingRangeException {
        
        long indicatedRange = (section == null) ? 10000 : -1;
        FeedbackSessionResultsBundle results = getFeedbackSessionResultsForInstructorInSectionWithinRangeFromView(
                feedbackSessionName, courseId, userEmail, section,
                indicatedRange, "question");
        
        if(!results.isComplete){
            throw new ExceedingRangeException("Number of responses exceeds the limited range");
        }
        // sort responses by giver > recipient > qnNumber
        Collections.sort(results.responses,
                results.compareByGiverRecipientQuestion);
        
        StringBuilder exportBuilder = new StringBuilder();

        exportBuilder.append("Course" + "," + Sanitizer.sanitizeForCsv(results.feedbackSession.courseId) + Const.EOL
                + "Session Name" + "," + Sanitizer.sanitizeForCsv(results.feedbackSession.feedbackSessionName) + Const.EOL);
        
        if(section != null){
            exportBuilder.append("Section Name" + "," + Sanitizer.sanitizeForCsv(section) + Const.EOL);
        }

        exportBuilder.append(Const.EOL + Const.EOL);

        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry : results
                .getQuestionResponseMap().entrySet()) {
            exportBuilder.append(getFeedbackSessionResultsForQuestionInCsvFormat(results, entry));
        }
        return exportBuilder.toString();
        
    }

    private StringBuilder getFeedbackSessionResultsForQuestionInCsvFormat(
            FeedbackSessionResultsBundle fsrBundle,
            Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry) {
        
        FeedbackQuestionAttributes question = entry.getKey();
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        List<FeedbackResponseAttributes> allResponses = entry.getValue();
        
        StringBuilder exportBuilder = new StringBuilder();
        
        exportBuilder.append("Question " + Integer.toString(question.questionNumber) + "," 
                + Sanitizer.sanitizeForCsv(questionDetails.questionText)
                + Const.EOL + Const.EOL);
        
        String statistics = questionDetails.getQuestionResultStatisticsCsv(allResponses,
                                    question, fsrBundle);
        if(statistics != ""){
            exportBuilder.append("Summary Statistics," + Const.EOL);
            exportBuilder.append(statistics + Const.EOL);
        }
        
        exportBuilder.append(questionDetails.getCsvDetailedResponsesHeader());

        List<String> possibleGiversWithoutResponses = fsrBundle.getPossibleGivers(question);
        List<String> possibleRecipientsForGiver = new ArrayList<String>();
        String prevGiver = "";
        
        for (FeedbackResponseAttributes response : allResponses) {

            // do not show all possible givers and recipients if there are anonymous givers and recipients 
            if (!fsrBundle.isRecipientVisible(response) || !fsrBundle.isGiverVisible(response)) {
                possibleGiversWithoutResponses.clear();
                possibleRecipientsForGiver.clear();
            }
            
            // keep track of possible recipients with no responses
            removeParticipantIdentifierFromList(question.giverType,
                    possibleGiversWithoutResponses, response.giverEmail, fsrBundle);
            
            boolean isNewGiver = !prevGiver.equals(response.giverEmail);
            // print missing responses from the current giver
            if (isNewGiver) {
                exportBuilder.append(getRowsOfPossibleRecipientsInCsvFormat(fsrBundle,
                        question, questionDetails,
                        possibleRecipientsForGiver, prevGiver));
                
                
                String giverIdentifier = (question.giverType == FeedbackParticipantType.TEAMS)? 
                                    fsrBundle.getFullNameFromRoster(response.giverEmail):
                                    response.giverEmail;
                
                possibleRecipientsForGiver = fsrBundle.getPossibleRecipients(question, giverIdentifier);
            }
            
            removeParticipantIdentifierFromList(question.recipientType, possibleRecipientsForGiver, response.recipientEmail, fsrBundle);
            prevGiver = response.giverEmail;
            
            // Append row(s)
            exportBuilder.append(questionDetails.getCsvDetailedResponsesRow(fsrBundle, response, question));
        }
        
        // add the rows for the possible givers and recipients who have missing responses
        exportBuilder.append(getRemainingRowsInCsvFormat(fsrBundle, entry, question, questionDetails,
                possibleGiversWithoutResponses, possibleRecipientsForGiver, prevGiver));
        exportBuilder.append(Const.EOL + Const.EOL);
        
        return exportBuilder;
    }

    /**
     * Given a participantIdentifier, remove it from participantIdentifierList. 
     * 
     * Before removal, FeedbackSessionResultsBundle.getNameFromRoster is used to 
     * convert the identifier into a canonical form if the participantIdentifierType is TEAMS. 
     *  
     * @param participantIdentifierType
     * @param participantIdentifierList
     * @param participantIdentifier
     * @param bundle
     */
    private void removeParticipantIdentifierFromList(
            FeedbackParticipantType participantIdentifierType,
            List<String> participantIdentifierList, String participantIdentifier,
            FeedbackSessionResultsBundle bundle) {
        if (participantIdentifierType == FeedbackParticipantType.TEAMS) {
            participantIdentifierList.remove(bundle.getFullNameFromRoster(participantIdentifier)); 
        } else {
            participantIdentifierList.remove(participantIdentifier);
        }
    }

    /**
     * Generate rows of missing responses for the remaining possible givers and recipients.
     * 
     * If for the prevGiver, possibleRecipientsForGiver is not empty,
     * the remaining missing responses for the prevGiver will be generated first.
     * 
     * @param results
     * @param entry
     * @param question
     * @param questionDetails
     * @param remainingPossibleGivers
     * @param possibleRecipientsForGiver
     * @param prevGiver
     * @return the remaining rows of missing responses in csv format
     */
    private StringBuilder getRemainingRowsInCsvFormat(
            FeedbackSessionResultsBundle results,
            Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry,
            FeedbackQuestionAttributes question,
            FeedbackQuestionDetails questionDetails,
            List<String> remainingPossibleGivers,
            List<String> possibleRecipientsForGiver, String prevGiver) {
        StringBuilder exportBuilder = new StringBuilder();
        
        if (possibleRecipientsForGiver != null) {
            exportBuilder.append(getRowsOfPossibleRecipientsInCsvFormat(results,
                    question, questionDetails, possibleRecipientsForGiver,
                    prevGiver));
            
        }
        
        removeParticipantIdentifierFromList(question.giverType, remainingPossibleGivers, prevGiver, results);
            
        
        for (String possibleGiverWithNoResponses : remainingPossibleGivers) {
            possibleRecipientsForGiver = results.getPossibleRecipients(entry.getKey(), possibleGiverWithNoResponses);
            
            exportBuilder.append(getRowsOfPossibleRecipientsInCsvFormat(results,
                    question, questionDetails, possibleRecipientsForGiver,
                    possibleGiverWithNoResponses));
        }
        
        return exportBuilder;
    }


    /**
     * For a giver and a list of possibleRecipientsForGiver, generate rows 
     * of missing responses between the giver and the possible recipients
     * 
     * @param results
     * @param question
     * @param questionDetails
     * @param possibleRecipientsForGiver
     * @param giver
     * @return
     */
    private StringBuilder getRowsOfPossibleRecipientsInCsvFormat(
            FeedbackSessionResultsBundle results, 
            FeedbackQuestionAttributes question,
            FeedbackQuestionDetails questionDetails,
            List<String> possibleRecipientsForGiver, String giver) {
        StringBuilder exportBuilder = new StringBuilder();
        for (String possibleRecipient : possibleRecipientsForGiver) {
            String giverName = results.getFullNameFromRoster(giver);
            String giverLastName = results.getLastNameFromRoster(giver);
            String giverEmail = results.getDisplayableEmailFromRoster(giver);
            String possibleRecipientName = results.getFullNameFromRoster(possibleRecipient);
            String possibleRecipientLastName = results.getLastNameFromRoster(possibleRecipient);
            String possibleRecipientEmail = results.getDisplayableEmailFromRoster(possibleRecipient);
            
            if (questionDetails.shouldShowNoResponseText(giver, possibleRecipient, question)) {
                exportBuilder.append(Sanitizer.sanitizeForCsv(results.getTeamNameFromRoster(giver)) 
                        + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(giverName))
                        + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(giverLastName))
                        + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(giverEmail))
                        + "," + Sanitizer.sanitizeForCsv(results.getTeamNameFromRoster(possibleRecipient))
                        + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(possibleRecipientName))
                        + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(possibleRecipientLastName))
                        + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(possibleRecipientEmail))
                        + "," + questionDetails.getNoResponseTextInCsv(giver, possibleRecipient, results, question)
                        + Const.EOL);
            }
        }
        return exportBuilder;
    }

    /**
     * Criteria: must be published, publishEmail must be enabled and
     * resultsVisibleTime must be custom.
     * 
     * @return returns a list of sessions that require automated emails to be
     *         sent as they are published
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent() {
        List<FeedbackSessionAttributes> sessions =
                fsDb.getFeedbackSessionsWithUnsentPublishedEmail();
        List<FeedbackSessionAttributes> sessionsToSendEmailsFor =
                new ArrayList<FeedbackSessionAttributes>();

        for (FeedbackSessionAttributes session : sessions) {
            // automated emails are required only for custom publish times
            if (session.isPublished() && session.isPublishedEmailEnabled
                  && !TimeHelper.isSpecialTime(session.resultsVisibleFromTime)) {
                sessionsToSendEmailsFor.add(session);
            }
        }
        return sessionsToSendEmailsFor;
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedOpenEmailsToBeSent() {
        List<FeedbackSessionAttributes> sessions =
                fsDb.getFeedbackSessionsWithUnsentOpenEmail();
        List<FeedbackSessionAttributes> sessionsToSendEmailsFor =
                new ArrayList<FeedbackSessionAttributes>();

        for (FeedbackSessionAttributes session : sessions) {
            if (session.isOpened()) {
                sessionsToSendEmailsFor.add(session);
            }
        }
        return sessionsToSendEmailsFor;
    }

    public boolean isCreatorOfSession(String feedbackSessionName, String courseId, String userEmail) {
        FeedbackSessionAttributes fs = getFeedbackSession(feedbackSessionName, courseId);
        return (fs.creatorEmail.equals(userEmail));
    }

    public boolean isFeedbackSessionExists(String feedbackSessionName, String courseId) {
        return fsDb.getFeedbackSession(courseId, feedbackSessionName) != null;
    }

    public boolean isFeedbackSessionHasQuestionForStudents(
            String feedbackSessionName,
            String courseId) throws EntityDoesNotExistException {
        if (isFeedbackSessionExists(feedbackSessionName, courseId) == false) {
            throw new EntityDoesNotExistException(
                    "Trying to check a feedback session that does not exist.");
        }

        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName,
                        courseId);

        return !allQuestions.isEmpty();
    }

    public boolean isFeedbackSessionCompletedByStudent(FeedbackSessionAttributes fsa,
                                                       String userEmail)
                   throws EntityDoesNotExistException {
        Assumption.assertNotNull(fsa);
        if (fsa.respondingStudentList.contains(userEmail)) {
            return true;
        }
        
        String feedbackSessionName = fsa.feedbackSessionName;
        String courseId = fsa.courseId;
        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName, courseId);
        // if there is no question for students, session is complete
        return allQuestions.isEmpty();
    }

    public boolean isFeedbackSessionCompletedByInstructor(
            String feedbackSessionName,
            String courseId, String userEmail)
            throws EntityDoesNotExistException {

        FeedbackSessionAttributes  fsa = this.getFeedbackSession(feedbackSessionName, courseId);
        if (fsa == null) {
            throw new EntityDoesNotExistException(
                    "Trying to check a feedback session that does not exist.");
        }
        
        if (fsa.respondingInstructorList.contains(userEmail)) {
            return true;
        }

        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForInstructor(feedbackSessionName,
                        courseId, userEmail);

        // if there is no question for instructor, session is complete
        return allQuestions.isEmpty();
    }

    // This method is for manual adding of additional responses to a FS.
    public void addResponsesToFeedbackSession(List<FeedbackResponse> responses,
            String feedbackSessionName, String courseId)
            throws NotImplementedException {
        throw new NotImplementedException(
                "Can't do manual adding of responses yet");
    }

    public void updateFeedbackSession(FeedbackSessionAttributes newSession)
            throws InvalidParametersException, EntityDoesNotExistException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, newSession);

        FeedbackSessionAttributes oldSession =
                fsDb.getFeedbackSession(newSession.courseId,
                        newSession.feedbackSessionName);

        if (oldSession == null) {
            throw new EntityDoesNotExistException(
                    "Trying to update a feedback session that does not exist.");
        }

        // These can't be changed anyway. Copy values to defensively avoid
        // invalid parameters.
        newSession.creatorEmail = oldSession.creatorEmail;
        newSession.createdTime = oldSession.createdTime;

        if (newSession.instructions == null) {
            newSession.instructions = oldSession.instructions;
        }
        if (newSession.startTime == null) {
            newSession.startTime = oldSession.startTime;
        }
        if (newSession.endTime == null) {
            newSession.endTime = oldSession.endTime;
        }
        if (newSession.feedbackSessionType == null) {
            newSession.feedbackSessionType = oldSession.feedbackSessionType;
        }
        if (newSession.sessionVisibleFromTime == null) {
            newSession.sessionVisibleFromTime = oldSession.sessionVisibleFromTime;
        }
        if (newSession.resultsVisibleFromTime == null) {
            newSession.resultsVisibleFromTime = oldSession.resultsVisibleFromTime;
        }

        makeEmailStateConsistent(oldSession, newSession);

        fsDb.updateFeedbackSession(newSession);
    }
    
    public void updateRespondantsForInstructor(String oldEmail, String newEmail, String courseId) throws InvalidParametersException, EntityDoesNotExistException {
        
        List<FeedbackSessionAttributes> feedbackSessions = getFeedbackSessionsForCourse(courseId);
        for(FeedbackSessionAttributes session : feedbackSessions) {
            fsDb.updateInstructorRespondant(oldEmail, newEmail, session);
        }
    }

    public void updateRespondantsForStudent(String oldEmail, String newEmail, String courseId) throws InvalidParametersException, EntityDoesNotExistException {

        List<FeedbackSessionAttributes> feedbackSessions = getFeedbackSessionsForCourse(courseId);
        for(FeedbackSessionAttributes session : feedbackSessions) {
            fsDb.updateStudentRespondant(oldEmail, newEmail, session);
        }
    }
    
    public void updateRespondantsForSession(String feedbackSessionName, String courseId) throws InvalidParametersException, EntityDoesNotExistException {

        clearInstructorRespondants(feedbackSessionName, courseId);
        clearStudentRespondants(feedbackSessionName, courseId);
        
        FeedbackSessionAttributes fsa = getFeedbackSession(feedbackSessionName, courseId);
        List<FeedbackQuestionAttributes> questions = fqLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(courseId);
        
        Map<String, List<String>> instructorQuestionsMap = new HashMap<String, List<String>>();
        
        for (InstructorAttributes instructor : instructors) {
            List<FeedbackQuestionAttributes> instructorQns = fqLogic
                    .getFeedbackQuestionsForInstructor(questions,
                            fsa.isCreator(instructor.email));
            
            if (!instructorQns.isEmpty()) {
                List<String> questionIds = new ArrayList<String>();
                for(FeedbackQuestionAttributes question : instructorQns){
                    questionIds.add(question.getId());
                }
                instructorQuestionsMap.put(instructor.email, questionIds);
            }
        }
        
        Set<String> respondingStudentList = new HashSet<String>();
        Set<String> respondingInstructorList = new HashSet<String>();
        List<FeedbackResponseAttributes> responses = frLogic.getFeedbackResponsesForSession(feedbackSessionName, courseId);
        for(FeedbackResponseAttributes response : responses) {
            List<String> instructorQuestions = instructorQuestionsMap.get(response.giverEmail);
            if(instructorQuestions != null && instructorQuestions.contains(response.feedbackQuestionId)){
                    respondingInstructorList.add(response.giverEmail);
            } else {
                    respondingStudentList.add(response.giverEmail);
            }
        }
        
        addInstructorRespondants(new ArrayList<String>(respondingInstructorList), feedbackSessionName, courseId);
        addStudentRespondants(new ArrayList<String>(respondingStudentList), feedbackSessionName, courseId);
    }

    public void deleteInstructorFromRespondantsList(InstructorAttributes instructor) {
        if(instructor == null || instructor.email == null){
            return;
        }
        List<FeedbackSessionAttributes> sessionsToUpdate =
                fsDb.getFeedbackSessionsForCourse(instructor.courseId);

        for(FeedbackSessionAttributes session : sessionsToUpdate){
            try {
                deleteInstructorRespondant(instructor.email, session.feedbackSessionName, session.courseId);
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                Assumption.fail("Fail to delete instructor respondant for " + session.feedbackSessionName);
            }
        }
    }

    public void deleteStudentFromRespondantsList(StudentAttributes student) {  
        if(student == null || student.email == null){
            return;
        }
        List<FeedbackSessionAttributes> sessionsToUpdate =
                fsDb.getFeedbackSessionsForCourse(student.course);

        for(FeedbackSessionAttributes session : sessionsToUpdate) {
            try {
                deleteStudentRespondant(student.email, session.feedbackSessionName, session.courseId);
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                Assumption.fail("Fail to delete instructor respondant for " + session.feedbackSessionName);
            }
        }
    }

    public void addInstructorRespondant(String email, String feedbackSessionName, String courseId) throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, email);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if (sessionToUpdate == null) {
            throw new EntityDoesNotExistException(
                    "Trying to update a feedback session that does not exist.");
        }

        fsDb.addInstructorRespondant(email, sessionToUpdate);
    }

    public void addInstructorRespondants(List<String> emails, String feedbackSessionName, String courseId) throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, emails);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if(sessionToUpdate == null) {
            throw new EntityDoesNotExistException(
                    "Trying to update a feedback session that does not exist.");
        }

        fsDb.addInstructorRespondants(emails, sessionToUpdate);
    }

    public void clearInstructorRespondants(String feedbackSessionName, String courseId) throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if(sessionToUpdate == null) {
            throw new EntityDoesNotExistException(
                    "Trying to update a feedback session that does not exist.");
        }

        fsDb.clearInstructorRespondants(sessionToUpdate);        
    }

    public void addStudentRespondant(String email, String feedbackSessionName, String courseId) throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, email);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if (sessionToUpdate == null) {
            throw new EntityDoesNotExistException(
                    "Trying to update a feedback session that does not exist.");
        }

        fsDb.addStudentRespondant(email, sessionToUpdate);
    }

    public void addStudentRespondants(List<String> emails, String feedbackSessionName, String courseId) throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, emails);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if(sessionToUpdate == null) {
            throw new EntityDoesNotExistException(
                    "Trying to update a feedback session that does not exist.");
        }

        fsDb.addStudentRespondants(emails, sessionToUpdate);
    }

    public void clearStudentRespondants(String feedbackSessionName, String courseId) throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if(sessionToUpdate == null) {
            throw new EntityDoesNotExistException(
                    "Trying to update a feedback session that does not exist.");
        }

        fsDb.clearStudentRespondants(sessionToUpdate);        
    }

    public void deleteInstructorRespondant(String email, String feedbackSessionName, String courseId) throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, email);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if (sessionToUpdate == null) {
            throw new EntityDoesNotExistException(
                    "Trying to update a feedback session that does not exist.");
        }

        fsDb.deleteInstructorRespondant(email, sessionToUpdate);
    }

    public void deleteStudentRespondant(String email, String feedbackSessionName, String courseId) throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, email);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if (sessionToUpdate == null) {
            throw new EntityDoesNotExistException(
                    "Trying to update a feedback session that does not exist.");
        }

        fsDb.deleteStudentRespondant(email, sessionToUpdate);
    }

    /**
     * This method is called when the user publishes a feedback session
     * manually. Preconditions: * The feedback session has to be set as
     * manually/automatically published. The feedback session can't be private
     */
    public void publishFeedbackSession(String feedbackSessionName,
            String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        FeedbackSessionAttributes sessionToPublish =
                getFeedbackSession(feedbackSessionName, courseId);

        if (sessionToPublish == null) {
            throw new EntityDoesNotExistException(
                    "Trying to publish a non-existant session.");
        }

        if (sessionToPublish.isPrivateSession()) {
            throw new InvalidParametersException(
                    "Private session can't be published.");
        }

        if (sessionToPublish.isPublished()) {
            throw new InvalidParametersException(
                    "Session is already published.");
        }

        sessionToPublish.resultsVisibleFromTime = currentDateTime(sessionToPublish);
        updateFeedbackSession(sessionToPublish);
        if (sessionToPublish.isPublishedEmailEnabled) {
            sendFeedbackSessionPublishedEmail(sessionToPublish);
        }
    }

    private Date currentDateTime(FeedbackSessionAttributes sessionToPublish) {
        Calendar now = TimeHelper.now(sessionToPublish.timeZone);
        return now.getTime();
    }

    /**
     * This method is called when the user unpublishes a feedback session
     * manually. Preconditions: * The feedback session has to be set as manually
     * published.
     */
    public void unpublishFeedbackSession(String feedbackSessionName,
            String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        FeedbackSessionAttributes sessionToUnpublish =
                getFeedbackSession(feedbackSessionName, courseId);

        if (sessionToUnpublish == null) {
            throw new EntityDoesNotExistException(
                    "Trying to unpublish a non-existant session.");
        }

        if (sessionToUnpublish.isPrivateSession()) {
            throw new InvalidParametersException(
                    "Private session can't be unpublished.");
        }

        if (!sessionToUnpublish.isPublished()) {
            throw new InvalidParametersException(
                    "Session is already unpublished.");
        }

        sessionToUnpublish.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;

        updateFeedbackSession(sessionToUnpublish);
    }

    public List<MimeMessage> sendReminderForFeedbackSession(String courseId,
            String feedbackSessionName) throws EntityDoesNotExistException {
        if (!isFeedbackSessionExists(feedbackSessionName, courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to remind non-existent feedback session "
                            + courseId + "/" + feedbackSessionName);
        }

        FeedbackSessionAttributes session = getFeedbackSession(
                feedbackSessionName, courseId);
        List<StudentAttributes> studentList = studentsLogic
                .getStudentsForCourse(courseId);
        List<InstructorAttributes> instructorList = instructorsLogic
                .getInstructorsForCourse(courseId);

        // Filter out students who have submitted the feedback session
        List<StudentAttributes> studentsToRemindList = new ArrayList<StudentAttributes>();
        for (StudentAttributes student : studentList) {
            if (!isFeedbackSessionCompletedByStudent(session, student.email)) {
                studentsToRemindList.add(student);
            }
        }

        // Filter out instructors who have submitted the feedback session
        List<InstructorAttributes> instructorsToRemindList = new ArrayList<InstructorAttributes>();
        for (InstructorAttributes instructor : instructorList) {
            if (!isFeedbackSessionCompletedByInstructor(
                    session.feedbackSessionName, session.courseId,
                    instructor.email)) {
                instructorsToRemindList.add(instructor);
            }
        }

        CourseAttributes course = coursesLogic.getCourse(courseId);
        List<MimeMessage> emails;
        Emails emailMgr = new Emails();
        try {
            emails = emailMgr.generateFeedbackSessionReminderEmails(course,
                    session, studentsToRemindList, instructorsToRemindList,
                    instructorList);
            emailMgr.sendEmails(emails);
        } catch (Exception e) {
            throw new RuntimeException("Error while sending emails :", e);
        }

        return emails;
    }
    
    public List<MimeMessage> sendReminderForFeedbackSessionParticularUsers(String courseId,
            String feedbackSessionName, String[] usersToRemind) throws EntityDoesNotExistException {
        if (!isFeedbackSessionExists(feedbackSessionName, courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to remind non-existent feedback session "
                            + courseId + "/" + feedbackSessionName);
        }

        FeedbackSessionAttributes session = getFeedbackSession(
                feedbackSessionName, courseId);
        
        List<InstructorAttributes> instructorList = instructorsLogic
                .getInstructorsForCourse(courseId);
        List<StudentAttributes> studentsToRemindList = new ArrayList<StudentAttributes>();
        List<InstructorAttributes> instructorsToRemindList = new ArrayList<InstructorAttributes>();

        for (String userEmail : usersToRemind) {
            StudentAttributes student = studentsLogic
                    .getStudentForEmail(courseId, userEmail);
            if (student != null) {
                studentsToRemindList.add(student);
            }

            InstructorAttributes instructor = instructorsLogic
                    .getInstructorForEmail(courseId, userEmail);
            if (instructor != null) {
                instructorsToRemindList.add(instructor);
            }
        }

        CourseAttributes course = coursesLogic.getCourse(courseId);
        List<MimeMessage> emails;
        Emails emailMgr = new Emails();
        try {
            emails = emailMgr.generateFeedbackSessionReminderEmails(course,
                    session, studentsToRemindList, instructorsToRemindList,
                    instructorList);
            emailMgr.sendEmails(emails);
        } catch (Exception e) {
            throw new RuntimeException("Error while sending emails :", e);
        }

        return emails;
    }

    public void scheduleFeedbackRemindEmails(String courseId, String feedbackSessionName) {
        
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(ParamsNames.SUBMISSION_FEEDBACK, feedbackSessionName);
        paramMap.put(ParamsNames.SUBMISSION_COURSE, courseId);
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        taskQueueLogic.createAndAddTask(SystemParams.FEEDBACK_REMIND_EMAIL_TASK_QUEUE,
                Const.ActionURIs.FEEDBACK_REMIND_EMAIL_WORKER, paramMap);
    }
    
    public void scheduleFeedbackRemindEmailsForParticularUsers(String courseId,
            String feedbackSessionName, String[] usersToRemind) {
    
        HashMap<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.put(ParamsNames.SUBMISSION_FEEDBACK, new String[]{feedbackSessionName});
        paramMap.put(ParamsNames.SUBMISSION_COURSE, new String[]{courseId});
        paramMap.put(ParamsNames.SUBMISSION_REMIND_USERLIST, usersToRemind);
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        taskQueueLogic.createAndAddTaskMultisetParam(SystemParams.FEEDBACK_REMIND_EMAIL_PARTICULAR_USERS_TASK_QUEUE,
                Const.ActionURIs.FEEDBACK_REMIND_EMAIL_PARTICULAR_USERS_WORKER, paramMap);
    }

    public void scheduleFeedbackSessionOpeningEmails() {
        List<FeedbackSessionAttributes> sessions = getFeedbackSessionsWhichNeedOpenEmailsToBeSent();

        for (FeedbackSessionAttributes session : sessions) {
            Emails emails = new Emails();
            emails.addFeedbackSessionReminderToEmailsQueue(session,
                    Emails.EmailType.FEEDBACK_OPENING);
        }
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsClosingWithinTimeLimit() {
        ArrayList<FeedbackSessionAttributes> requiredSessions = new
                ArrayList<FeedbackSessionAttributes>();

        List<FeedbackSessionAttributes> nonPrivateSessions = fsDb
                .getNonPrivateFeedbackSessions();

        for (FeedbackSessionAttributes session : nonPrivateSessions) {
            if (session.isClosingWithinTimeLimit(SystemParams.NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT)
                    && session.isClosingEmailEnabled) {
                requiredSessions.add(session);
            }
        }

        return requiredSessions;
    }

    public void scheduleFeedbackSessionClosingEmails() {

        List<FeedbackSessionAttributes> sessions = getFeedbackSessionsClosingWithinTimeLimit();

        for (FeedbackSessionAttributes session : sessions) {
            Emails emails = new Emails();
            emails.addFeedbackSessionReminderToEmailsQueue(session, Emails.EmailType.FEEDBACK_CLOSING);
        }
    }

    public void scheduleFeedbackSessionPublishedEmails() {
        List<FeedbackSessionAttributes> sessions = getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();

        for (FeedbackSessionAttributes session : sessions) {
            sendFeedbackSessionPublishedEmail(session);
        }
    }

    /**
     * Deletes the feedback sessions in the course specified. The delete 
     * is cascaded, and feedback questions, feedback responses, and 
     * feedback response comments in the course are deleted.
     * @param courseId
     */
    public void deleteFeedbackSessionsForCourseCascade(String courseId) {
        frcLogic.deleteFeedbackResponseCommentsForCourse(courseId);
        frLogic.deleteFeedbackResponsesForCourse(courseId);
        fqLogic.deleteFeedbackQuestionsForCourse(courseId);
        deleteFeedbackSessionsForCourse(courseId);
    }
    
    /**
     * Deletes all feedback sessions the course specified. This is 
     * a non-cascade delete.
     *  
     * The responses, questions and the comments of the responses
     * should be handled.
     */
    public void deleteFeedbackSessionsForCourse(String courseId) {
        fsDb.deleteFeedbackSessionsForCourse(courseId);
    }

    /**
     * This method deletes a specific feedback session, and all it's question
     * and responses
     */
    public void deleteFeedbackSessionCascade(String feedbackSessionName, String courseId) {

        try {
            fqLogic.deleteFeedbackQuestionsForSession(feedbackSessionName, courseId);
        } catch (EntityDoesNotExistException e) {
            // Silently fail if session does not exist
        }

        FeedbackSessionAttributes sessionToDelete = new FeedbackSessionAttributes();
        sessionToDelete.feedbackSessionName = feedbackSessionName;
        sessionToDelete.courseId = courseId;

        fsDb.deleteEntity(sessionToDelete);

    }

    public FeedbackSessionDetailsBundle getFeedbackSessionDetails(
            FeedbackSessionAttributes fsa) throws EntityDoesNotExistException {

        FeedbackSessionDetailsBundle details =
                new FeedbackSessionDetailsBundle(fsa);

        details.stats.expectedTotal = 0;
        details.stats.submittedTotal = 0;
        
        switch (fsa.feedbackSessionType) {
        case STANDARD:
            List<StudentAttributes> students = studentsLogic
                    .getStudentsForCourse(fsa.courseId);
            List<InstructorAttributes> instructors = instructorsLogic
                    .getInstructorsForCourse(fsa.courseId);
            List<FeedbackQuestionAttributes> questions = fqLogic
                    .getFeedbackQuestionsForSession(fsa.feedbackSessionName,
                            fsa.courseId);
            List<FeedbackQuestionAttributes> studentQns = fqLogic
                    .getFeedbackQuestionsForStudents(questions);

            if(!studentQns.isEmpty()){
                details.stats.expectedTotal += students.size();
            }
        
            for (InstructorAttributes instructor : instructors) {
                List<FeedbackQuestionAttributes> instructorQns = fqLogic
                        .getFeedbackQuestionsForInstructor(questions,
                                fsa.isCreator(instructor.email));
                if (!instructorQns.isEmpty()) {
                    details.stats.expectedTotal += 1;
                }
            }
            
            details.stats.submittedTotal += fsa.respondingStudentList.size() + fsa.respondingInstructorList.size();

            break;

        case PRIVATE:
            List<FeedbackQuestionAttributes> instuctorQuestions =
                    fqLogic.getFeedbackQuestionsForInstructor(
                            fsa.feedbackSessionName,
                            fsa.courseId,
                            fsa.creatorEmail);
            List<FeedbackQuestionAttributes> validQuestions = fqLogic
                    .getQuestionsWithRecipients(instuctorQuestions,
                            fsa.creatorEmail);
            if (validQuestions.isEmpty()) {
                break;
            }
            details.stats.expectedTotal = 1;
            if (this.isFeedbackSessionFullyCompletedByInstructor(
                    fsa.feedbackSessionName, fsa.courseId, fsa.creatorEmail)) {
                details.stats.submittedTotal = 1;
            }
            break;

        default:
            break;
        }
        
        return details;
    }
    
    /* Get the feedback results for user in a section iterated by questions */
    private FeedbackSessionResultsBundle getFeedbackSessionResultsForUserInSectionByQuestions(
            String feedbackSessionName, String courseId, String userEmail,
            UserType.Role role, String section)
            throws EntityDoesNotExistException {
        // Load details of students and instructors once and pass it to callee
        // methods
        // (rather than loading them many times).
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        
        return getFeedbackSessionResultsForUserInSectionByQuestions(
                feedbackSessionName, courseId, userEmail, role, section, roster);
    }

    /* Get the feedback results for user in a section iterated by questions */
    private FeedbackSessionResultsBundle getFeedbackSessionResultsForUserInSectionByQuestions(
            String feedbackSessionName, String courseId, String userEmail,
            UserType.Role role, String section, CourseRoster roster)
            throws EntityDoesNotExistException {

        FeedbackSessionAttributes session = fsDb.getFeedbackSession(
                courseId, feedbackSessionName);

        if (session == null) {
            throw new EntityDoesNotExistException(
                    "Trying to view non-existent feedback session.");
        }

        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForSession(feedbackSessionName,
                        courseId);

        // create empty data containers to store results
        List<FeedbackResponseAttributes> responses =
                new ArrayList<FeedbackResponseAttributes>();
        Map<String, FeedbackQuestionAttributes> relevantQuestions =
                new HashMap<String, FeedbackQuestionAttributes>();
        Map<String, String> emailNameTable =
                new HashMap<String, String>();
        Map<String, String> emailLastNameTable =
                new HashMap<String, String>();
        Map<String, String> emailTeamNameTable =
                new HashMap<String, String>();
        Map<String, Set<String>> sectionTeamNameTable = 
                new HashMap<String, Set<String>>();
        Map<String, boolean[]> visibilityTable =
                new HashMap<String, boolean[]>();
        Map<String, List<FeedbackResponseCommentAttributes>> responseComments =
                new HashMap<String, List<FeedbackResponseCommentAttributes>>();
        
        FeedbackSessionResponseStatus responseStatus = new FeedbackSessionResponseStatus();

        boolean isPrivateSessionNotCreatedByThisUser = session
                .isPrivateSession() && !session.isCreator(userEmail);
        if (isPrivateSessionNotCreatedByThisUser) {
            // return empty result set
            return new FeedbackSessionResultsBundle(
                    session, responses, relevantQuestions, emailNameTable, 
                    emailLastNameTable, emailTeamNameTable, sectionTeamNameTable,
                    visibilityTable, responseStatus, roster, responseComments);
        }

        Map<String, FeedbackResponseAttributes> relevantResponse = new HashMap<String, FeedbackResponseAttributes>();
        for (FeedbackQuestionAttributes question : allQuestions) {

            List<FeedbackResponseAttributes> responsesForThisQn;

            boolean isPrivateSessionCreatedByThisUser = session
                    .isCreator(userEmail) && session.isPrivateSession();
            if (isPrivateSessionCreatedByThisUser) {
                responsesForThisQn = frLogic
                        .getFeedbackResponsesForQuestion(question.getId());
            } else {
                responsesForThisQn = frLogic
                        .getViewableFeedbackResponsesForQuestionInSection(
                                question, userEmail, role, section);
            }

            boolean thisQuestionHasResponses = (!responsesForThisQn.isEmpty());
            if (thisQuestionHasResponses) {
                relevantQuestions.put(question.getId(), question);
                responses.addAll(responsesForThisQn);
                for (FeedbackResponseAttributes response : responsesForThisQn) {
                    relevantResponse.put(response.getId(), response);
                    addEmailNamePairsToTable(emailNameTable, response,
                            question, roster);
                    addEmailLastNamePairsToTable(emailLastNameTable, response,
                            question, roster);
                    addEmailTeamNamePairsToTable(emailTeamNameTable, response,
                            question, roster);
                    addVisibilityToTable(visibilityTable, question, response,
                            userEmail, role, roster);
                }
            }
        }

        StudentAttributes student = null;
        Set<String> studentsEmailInTeam = new HashSet<String>();
        if (role == Role.STUDENT) {
            student = studentsLogic.getStudentForEmail(courseId, userEmail);
            List<StudentAttributes> studentsInTeam = studentsLogic
                    .getStudentsForTeam(student.team, courseId);
            for (StudentAttributes teammates : studentsInTeam) {
                studentsEmailInTeam.add(teammates.email);
            }
        }
        
        InstructorAttributes instructor = null;
        if (role == Role.INSTRUCTOR) {
            instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
        }
        
        List<FeedbackResponseCommentAttributes> allResponseComments =
                frcLogic.getFeedbackResponseCommentForSession(courseId,
                        feedbackSessionName);
        for (FeedbackResponseCommentAttributes frc : allResponseComments) {
            FeedbackResponseAttributes relatedResponse = relevantResponse.get(frc.feedbackResponseId);
            FeedbackQuestionAttributes relatedQuestion = relevantQuestions.get(frc.feedbackQuestionId);
            boolean isVisibleResponseComment = frcLogic.isResponseCommentVisibleForUser(userEmail, courseId,
                    role, section, student, studentsEmailInTeam, relatedResponse,
                    relatedQuestion, frc, instructor);
            if(isVisibleResponseComment){
                if(!frcLogic.isNameVisibleTo(frc, relatedResponse, userEmail, roster)){
                    frc.giverEmail = "Anonymous";
                }
                
                if (responseComments.get(frc.feedbackResponseId) == null) {
                    responseComments.put(frc.feedbackResponseId,
                            new ArrayList<FeedbackResponseCommentAttributes>());
                }
                responseComments.get(frc.feedbackResponseId).add(frc);
            }
        }

        for (List<FeedbackResponseCommentAttributes> responseCommentList : responseComments
                .values()) {
            Collections.sort(responseCommentList,
                    new ResponseCommentCreationDateComparator());
        }
        
        addSectionTeamNamesToTable(sectionTeamNameTable, roster, courseId, userEmail, role, feedbackSessionName);

        FeedbackSessionResultsBundle results =
                new FeedbackSessionResultsBundle(
                        session, responses, relevantQuestions, emailNameTable, 
                        emailLastNameTable, emailTeamNameTable, sectionTeamNameTable,
                        visibilityTable, responseStatus, roster, responseComments);

        return results;
    }

    private FeedbackSessionResultsBundle getFeedbackSessionResultsForUserWithParams(
            String feedbackSessionName, String courseId, String userEmail,
            UserType.Role role, CourseRoster roster, Map<String, String> params)
            throws EntityDoesNotExistException {
        
        boolean isIncludeResponseStatus = Boolean.parseBoolean(params.get("isIncludeResponseStatus"));
        boolean isComplete = (params.get("range") != null) ? false : true;
        boolean isFromSection = Boolean.parseBoolean(params.get("fromSection"));
        boolean isToSection = Boolean.parseBoolean(params.get("toSection"));
        boolean isInSection = Boolean.parseBoolean(params.get("inSection"));
        String section = params.get("section");
        
        FeedbackSessionAttributes session = fsDb.getFeedbackSession(
                courseId, feedbackSessionName);

        if (session == null) {
            throw new EntityDoesNotExistException(
                    "Trying to view non-existent feedback session.");
        }

        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForSession(feedbackSessionName,
                        courseId);
        
        // create empty data containers to store results
        List<FeedbackResponseAttributes> responses =
                new ArrayList<FeedbackResponseAttributes>();
        Map<String, FeedbackQuestionAttributes> relevantQuestions =
                new HashMap<String, FeedbackQuestionAttributes>();
        Map<String, String> emailNameTable =
                new HashMap<String, String>();
        Map<String, String> emailLastNameTable =
                new HashMap<String, String>();
        Map<String, String> emailTeamNameTable =
                new HashMap<String, String>();
        Map<String, Set<String>> sectionTeamNameTable = 
                new HashMap<String, Set<String>>();
        Map<String, boolean[]> visibilityTable =
                new HashMap<String, boolean[]>();
        Map<String, List<FeedbackResponseCommentAttributes>> responseComments =
                new HashMap<String, List<FeedbackResponseCommentAttributes>>();
        
        //Show all questions even if no responses, unless is an ajax request for a specific question.
        if(role == UserType.Role.INSTRUCTOR && !params.containsKey("questionNum")){
            for (FeedbackQuestionAttributes question : allQuestions) {
                relevantQuestions.put(question.getId(), question);
            }
        }
        
        FeedbackSessionResponseStatus responseStatus = new FeedbackSessionResponseStatus();

        boolean isPrivateSessionNotCreatedByThisUser = session
                .isPrivateSession() && !session.isCreator(userEmail);
        if (isPrivateSessionNotCreatedByThisUser) {
            // return empty result set
            return new FeedbackSessionResultsBundle(
                    session, responses, relevantQuestions, emailNameTable, 
                    emailLastNameTable, emailTeamNameTable, sectionTeamNameTable,
                    visibilityTable, responseStatus, roster, responseComments);
        }
        
        if (params.get("questionNum") != null) {
            int questionNumber = Integer.parseInt(params.get("questionNum"));
            FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(
                    feedbackSessionName, courseId, questionNumber);
            if (question != null) {
                relevantQuestions.put(question.getId(), question);
                
                List<FeedbackResponseAttributes> responsesForThisQn;

                boolean isPrivateSessionCreatedByThisUser = session
                        .isCreator(userEmail) && session.isPrivateSession();
                if (isPrivateSessionCreatedByThisUser) {
                    responsesForThisQn = frLogic
                            .getFeedbackResponsesForQuestion(question.getId());
                } else {
                    responsesForThisQn = frLogic
                            .getViewableFeedbackResponsesForQuestionInSection(
                                    question, userEmail, Role.INSTRUCTOR, section);
                }

                boolean thisQuestionHasResponses = (!responsesForThisQn
                        .isEmpty());
                if (thisQuestionHasResponses) {
                    for (FeedbackResponseAttributes response : responsesForThisQn) {
                        boolean isVisibleResponse = false;
                        if ((response.giverEmail.equals(userEmail))
                                || (response.recipientEmail.equals(userEmail) && question
                                        .isResponseVisibleTo(FeedbackParticipantType.RECEIVER))
                                || (role == Role.INSTRUCTOR && question
                                        .isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS))
                                || (role == Role.STUDENT && question
                                        .isResponseVisibleTo(FeedbackParticipantType.STUDENTS))) {
                            isVisibleResponse = true;
                        }
                        InstructorAttributes instructor = null;
                        if (role == Role.INSTRUCTOR) {
                            instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
                        }
                        if (isVisibleResponse && instructor != null) {
                            boolean isGiverSectionRestricted 
                                    = !(instructor.isAllowedForPrivilege(response.giverSection,
                                                                         response.feedbackSessionName, 
                                                                         Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
                            // If instructors are not restricted to view the giver's section,
                            // they are allowed to view responses to GENERAL, subject to visibility options
                            boolean isRecipientSectionRestricted 
                                    = !(question.recipientType == FeedbackParticipantType.NONE)
                                   && !(instructor.isAllowedForPrivilege(response.recipientSection,
                                                                         response.feedbackSessionName, 
                                                                         Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
                            boolean isNotAllowedForInstructor = isGiverSectionRestricted || isRecipientSectionRestricted;
                            if (isNotAllowedForInstructor) {
                                isVisibleResponse = false;
                            }
                        }
                        if (isVisibleResponse) {
                            responses.add(response);
                            addEmailNamePairsToTable(emailNameTable, response,
                                    question, roster);
                            addEmailLastNamePairsToTable(emailLastNameTable, response,
                                    question, roster);
                            addEmailTeamNamePairsToTable(emailTeamNameTable,
                                    response,
                                    question, roster);
                            addVisibilityToTable(visibilityTable, question,
                                    response, userEmail, role, roster);
                        }
                        isVisibleResponse = false;
                    }
                }
            }
            boolean needResponseStatus = questionNumber == QUESTION_NUM_FOR_RESPONSE_RATE;
            if (needResponseStatus) {
              responseStatus = (section == null && isIncludeResponseStatus) 
                              ? getFeedbackSessionResponseStatus(session, roster, allQuestions) 
                              : null;
            }
            
            addSectionTeamNamesToTable(sectionTeamNameTable, roster, courseId, userEmail, role, feedbackSessionName);
            
            FeedbackSessionResultsBundle results =
                    new FeedbackSessionResultsBundle(
                            session, responses, relevantQuestions, emailNameTable, 
                            emailLastNameTable, emailTeamNameTable, sectionTeamNameTable, 
                            visibilityTable, responseStatus, roster, responseComments, true);

            return results;
        }
        
        Map<String, FeedbackQuestionAttributes> allQuestionsMap = new HashMap<String, FeedbackQuestionAttributes>();
        for (FeedbackQuestionAttributes qn : allQuestions) {
            allQuestionsMap.put(qn.getId(), qn);
        }
        List<FeedbackResponseAttributes> allResponses = new ArrayList<FeedbackResponseAttributes>();
        if(params.get("range") != null){
            long range = Long.parseLong(params.get("range"));
            if(isInSection){
                allResponses = frLogic.getFeedbackResponsesForSessionInSectionWithinRange(feedbackSessionName,
                            courseId, section, range);
            } else if(isFromSection){
                allResponses = frLogic.getFeedbackResponsesForSessionFromSectionWithinRange(feedbackSessionName,
                            courseId, section, range);
            } else if(isToSection) {
                allResponses = frLogic.getFeedbackResponsesForSessionToSectionWithinRange(feedbackSessionName,
                            courseId, section, range);
            } else {
                Assumption.fail("Client did not indicate the origin of the responses");
            }
            if(allResponses.size() <= range){
                isComplete = true;
            } else {
                for (FeedbackQuestionAttributes qn : allQuestions){
                    relevantQuestions.put(qn.getId(), qn);
                }
                
            }
        } else {
            if(isInSection){
                allResponses = frLogic.getFeedbackResponsesForSessionInSection(feedbackSessionName,
                        courseId, section);
            } else if(isFromSection){
                allResponses = frLogic.getFeedbackResponsesForSessionFromSection(feedbackSessionName,
                        courseId, section);
            } else if(isToSection){
                allResponses = frLogic.getFeedbackResponsesForSessionToSection(feedbackSessionName,
                        courseId, section);
            } else {
                Assumption.fail("Client did not indicate the origin of the response");
            }
        }
        
        responseStatus = (section == null && isIncludeResponseStatus) 
                        ? getFeedbackSessionResponseStatus(session, roster, allQuestions) 
                        : null;

        StudentAttributes student = null;
        Set<String> studentsEmailInTeam = new HashSet<String>();
        if (role == Role.STUDENT) {
            student = studentsLogic.getStudentForEmail(courseId, userEmail);
            List<StudentAttributes> studentsInTeam = studentsLogic
                    .getStudentsForTeam(student.team, courseId);
            for (StudentAttributes teammates : studentsInTeam) {
                studentsEmailInTeam.add(teammates.email);
            }
        }
        
        InstructorAttributes instructor = null;
        if (role == Role.INSTRUCTOR) {
            instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
        }
        
        Map<String, FeedbackResponseAttributes> relevantResponse = new HashMap<String, FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response : allResponses) {
            FeedbackQuestionAttributes relatedQuestion = allQuestionsMap
                    .get(response.feedbackQuestionId);
            if (relatedQuestion != null) {
                boolean isVisibleResponse = isResponseVisibleForUser(userEmail, courseId,
                        role, section, student, studentsEmailInTeam, response,
                        relatedQuestion, instructor);
                if (isVisibleResponse) {
                    responses.add(response);
                    relevantResponse.put(response.getId(), response);
                    relevantQuestions.put(relatedQuestion.getId(),
                            relatedQuestion);
                    addEmailNamePairsToTable(emailNameTable, response,
                            relatedQuestion, roster);
                    addEmailLastNamePairsToTable(emailLastNameTable, response,
                            relatedQuestion, roster);                
                    addEmailTeamNamePairsToTable(emailTeamNameTable, response,
                            relatedQuestion, roster);
                    addVisibilityToTable(visibilityTable, relatedQuestion,
                            response, userEmail, role, roster);
                }
                isVisibleResponse = false;
            }
        }

        if (params.get("viewType") == null
                || params.get("viewType").equals("giver-recipient-question")
                || params.get("viewType").equals("recipient-giver-question")) {
            List<FeedbackResponseCommentAttributes> allResponseComments =
                    frcLogic.getFeedbackResponseCommentForSessionInSection(courseId,
                            feedbackSessionName, section);
            for (FeedbackResponseCommentAttributes frc : allResponseComments) {
                FeedbackResponseAttributes relatedResponse = relevantResponse.get(frc.feedbackResponseId);
                FeedbackQuestionAttributes relatedQuestion = relevantQuestions.get(frc.feedbackQuestionId);
                boolean isVisibleResponseComment = frcLogic.isResponseCommentVisibleForUser(userEmail, courseId,
                        role, section, student, studentsEmailInTeam, relatedResponse,
                        relatedQuestion, frc, instructor);
                if(isVisibleResponseComment){
                    if(!frcLogic.isNameVisibleTo(frc, relatedResponse, userEmail, roster)){
                        frc.giverEmail = "Anonymous";
                    }
                    
                    List<FeedbackResponseCommentAttributes> frcList = responseComments
                            .get(frc.feedbackResponseId);
                    if (frcList == null) {
                        frcList = new ArrayList<FeedbackResponseCommentAttributes>();
                        frcList.add(frc);
                        responseComments.put(frc.feedbackResponseId, frcList);
                    } else {
                        frcList.add(frc);
                    }
                }
            }

            for (List<FeedbackResponseCommentAttributes> responseCommentList : responseComments
                    .values()) {
                Collections.sort(responseCommentList,
                        new ResponseCommentCreationDateComparator());
            }
        }
        
        addSectionTeamNamesToTable(sectionTeamNameTable, roster, courseId, userEmail, role, feedbackSessionName);
        
        FeedbackSessionResultsBundle results =
                new FeedbackSessionResultsBundle(
                        session, responses, relevantQuestions, emailNameTable, 
                        emailLastNameTable, emailTeamNameTable, sectionTeamNameTable,
                        visibilityTable, responseStatus, roster, responseComments, isComplete);

        return results;
    }

    private void addSectionTeamNamesToTable(Map<String, Set<String>> sectionTeamNameTable,
                                    CourseRoster roster, String courseId, String userEmail, Role role,
                                    String feedbackSessionName) {
        InstructorAttributes instructor = null;
        if (role == Role.INSTRUCTOR) {
            instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
        }
        if (instructor != null) {
            for (StudentAttributes student : roster.getStudents()) {
                boolean isVisibleResponse = 
                        instructor.isAllowedForPrivilege(
                                           student.section,
                                           feedbackSessionName, 
                                           Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
                if (isVisibleResponse) {
                    String section = student.section;
                    if (!sectionTeamNameTable.containsKey(section)) {
                        Set<String> teamNames = new HashSet<String>();
                        sectionTeamNameTable.put(section, teamNames);
                    }
                    
                    sectionTeamNameTable.get(section).add(student.team);
                }
            }
        }
    }

    private boolean isResponseVisibleForUser(String userEmail, String courseId,
            UserType.Role role, String section, StudentAttributes student,
            Set<String> studentsEmailInTeam,
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes relatedQuestion, InstructorAttributes instructor) {
        
        boolean isVisibleResponse = false;
        if ((role == Role.INSTRUCTOR && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS))
                || (response.recipientEmail.equals(userEmail) && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER))
                || (response.giverEmail.equals(userEmail))
                || (role == Role.STUDENT && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.STUDENTS))) {
            isVisibleResponse = true;
        } else if (role == Role.STUDENT 
                && ((relatedQuestion.recipientType == FeedbackParticipantType.TEAMS
                        && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                        && response.recipientEmail.equals(student.team))
                    || ((relatedQuestion.giverType == FeedbackParticipantType.TEAMS
                        || relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS))
                            && studentsEmailInTeam.contains(response.giverEmail))
                    || (relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                && studentsEmailInTeam.contains(response.recipientEmail)))) {
            isVisibleResponse = true;
        }
        if (isVisibleResponse && instructor != null) {
            boolean isGiverSectionRestricted 
            = !(instructor.isAllowedForPrivilege(response.giverSection,
                                                 response.feedbackSessionName, 
                                                 Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
            // If instructors are not restricted to view the giver's section,
            // they are allowed to view responses to GENERAL, subject to visibility options
            boolean isRecipientSectionRestricted 
                    = !(relatedQuestion.recipientType == FeedbackParticipantType.NONE) 
                   && !(instructor.isAllowedForPrivilege(response.recipientSection,
                                                         response.feedbackSessionName, 
                                                         Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
            
            boolean isNotAllowedForInstructor = isGiverSectionRestricted || isRecipientSectionRestricted;
            if (isNotAllowedForInstructor) {
                isVisibleResponse = false;
            }
        }
        return isVisibleResponse;
    }

    private class ResponseCommentCreationDateComparator implements
            Comparator<FeedbackResponseCommentAttributes> {
        @Override
        public int compare(FeedbackResponseCommentAttributes frc1,
                FeedbackResponseCommentAttributes frc2) {
            return frc1.createdAt.compareTo(frc2.createdAt);
        }
    }

    private void addVisibilityToTable(Map<String, boolean[]> visibilityTable,
            FeedbackQuestionAttributes question,
            FeedbackResponseAttributes response,
            String userEmail,
            UserType.Role role,
            CourseRoster roster) {
        boolean[] visibility = new boolean[2];
        visibility[Const.VISIBILITY_TABLE_GIVER] = frLogic.isNameVisibleTo(
                question, response, userEmail, role, true, roster);
        visibility[Const.VISIBILITY_TABLE_RECIPIENT] = frLogic.isNameVisibleTo(
                question, response, userEmail, role, false, roster);
        visibilityTable.put(response.getId(), visibility);
    }

    private void addEmailNamePairsToTable(Map<String, String> emailNameTable,
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes question, CourseRoster roster)
            throws EntityDoesNotExistException {
        addEmailNamePairsToTable(emailNameTable, response, question, roster,
                EMAIL_NAME_PAIR);
    }
    
    private void addEmailLastNamePairsToTable(Map<String, String> emailLastNameTable,
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes question, CourseRoster roster)
            throws EntityDoesNotExistException {
        addEmailNamePairsToTable(emailLastNameTable, response, question, roster,
                EMAIL_LASTNAME_PAIR);
    }

    private void addEmailTeamNamePairsToTable(
            Map<String, String> emailTeamNameTable,
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes question, CourseRoster roster)
            throws EntityDoesNotExistException {
        addEmailNamePairsToTable(emailTeamNameTable, response, question,
                roster, EMAIL_TEAMNAME_PAIR);
    }

    private void addEmailNamePairsToTable(Map<String, String> emailNameTable,
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes question, CourseRoster roster,
            int pairType) throws EntityDoesNotExistException {
        if (question.giverType == FeedbackParticipantType.TEAMS) {
            if (emailNameTable.containsKey(response.giverEmail
                    + Const.TEAM_OF_EMAIL_OWNER) == false) {
                emailNameTable.put(
                        response.giverEmail + Const.TEAM_OF_EMAIL_OWNER,
                        getNameTeamNamePairForEmail(question.giverType,
                                response.giverEmail, roster)[pairType]);
            }
        } else if (emailNameTable.containsKey(response.giverEmail) == false) {
            emailNameTable.put(
                    response.giverEmail,
                    getNameTeamNamePairForEmail(question.giverType,
                            response.giverEmail, roster)[pairType]);
        }

        FeedbackParticipantType recipientType = null;
        if (question.recipientType == FeedbackParticipantType.SELF) {
            recipientType = question.giverType;
        } else {
            recipientType = question.recipientType;
        }
        if (emailNameTable.containsKey(response.recipientEmail) == false) {
            emailNameTable.put(
                    response.recipientEmail,
                    getNameTeamNamePairForEmail(recipientType,
                                                response.recipientEmail, roster)[pairType]);
            
        }
    }

    private List<FeedbackSessionDetailsBundle> getFeedbackSessionDetailsForCourse(
            String courseId, String instructorEmail)
            throws EntityDoesNotExistException {

        List<FeedbackSessionDetailsBundle> fsDetailsWithoutPrivate =
                new ArrayList<FeedbackSessionDetailsBundle>();
        List<FeedbackSessionAttributes> fsInCourse =
                fsDb.getFeedbackSessionsForCourse(courseId);

        for (FeedbackSessionAttributes fsa : fsInCourse) {
            if ((fsa.isPrivateSession() && !fsa.isCreator(instructorEmail)) == false)
                fsDetailsWithoutPrivate.add(getFeedbackSessionDetails(fsa));
        }

        return fsDetailsWithoutPrivate;
    }

    private List<FeedbackSessionAttributes> getFeedbackSessionsListForCourse(
            String courseId, String instructorEmail) throws EntityDoesNotExistException {
        
        List<FeedbackSessionAttributes> fsInCourseWithoutPrivate = new ArrayList<FeedbackSessionAttributes>(); 
        List<FeedbackSessionAttributes> fsInCourse = fsDb.getFeedbackSessionsForCourse(courseId);
        
        for (FeedbackSessionAttributes fsa : fsInCourse) {
            if (!fsa.isPrivateSession() || fsa.isCreator(instructorEmail)) {
                fsInCourseWithoutPrivate.add(fsa);
            }
        }

        return fsInCourseWithoutPrivate;
    }

    private FeedbackSessionResponseStatus getFeedbackSessionResponseStatus(
            FeedbackSessionAttributes fsa, CourseRoster roster,
            List<FeedbackQuestionAttributes> questions)
            throws EntityDoesNotExistException {

        FeedbackSessionResponseStatus responseStatus = new FeedbackSessionResponseStatus();
        List<StudentAttributes> students = roster.getStudents();
        List<InstructorAttributes> instructors = roster.getInstructors();
        List<FeedbackQuestionAttributes> studentQns = fqLogic
                .getFeedbackQuestionsForStudents(questions);

        List<String> studentNoResponses = new ArrayList<String>();
        List<String> instructorNoResponses = new ArrayList<String>();

        if(!studentQns.isEmpty()){
            for(StudentAttributes student : students){
                studentNoResponses.add(student.email);
                responseStatus.emailNameTable.put(student.email, student.name);
                responseStatus.emailTeamNameTable.put(student.email, student.team);
            }
        }
        studentNoResponses.removeAll(fsa.respondingStudentList);


        for (InstructorAttributes instructor : instructors) {
            List<FeedbackQuestionAttributes> instructorQns = fqLogic
                    .getFeedbackQuestionsForInstructor(questions,
                            fsa.isCreator(instructor.email));
            if (!instructorQns.isEmpty()) {
                if(responseStatus.emailNameTable.get(instructor.email) == null){
                    instructorNoResponses.add(instructor.email);
                    responseStatus.emailNameTable.put(instructor.email, instructor.name);
                }
            }
        }
        instructorNoResponses.removeAll(fsa.respondingInstructorList);

        responseStatus.noResponse.addAll(studentNoResponses);
        responseStatus.noResponse.addAll(instructorNoResponses);
        
        return responseStatus;
    }

    // return a pair of String that contains Giver/Recipient'sName (at index 0)
    // and TeamName (at index 1)
    private String[] getNameTeamNamePairForEmail(FeedbackParticipantType type,
            String email, CourseRoster roster)
            throws EntityDoesNotExistException {
        String giverRecipientName = null;
        String giverRecipientLastName = null;
        String teamName = null;
        String name = null;
        String lastName = null;
        String team = null;

        StudentAttributes student = roster.getStudentForEmail(email);
        if (student != null) {
            name = student.name;
            team = student.team;
            lastName = student.lastName;
        } else {
            InstructorAttributes instructor = roster
                    .getInstructorForEmail(email);
            if (instructor == null) {
                if (email.equals(Const.GENERAL_QUESTION)) {
                    // Email represents that there is no specific recipient.
                    name = Const.USER_IS_NOBODY;
                    lastName = Const.USER_IS_NOBODY;
                    team = email;
                } else {
                    // Assume that the email is actually a team name.
                    name = Const.USER_IS_TEAM;
                    lastName = Const.USER_IS_TEAM;
                    team = email;
                }
            } else {
                name = instructor.name;
                lastName = instructor.name;
                team = Const.USER_TEAM_FOR_INSTRUCTOR;
            }
        }

        if (type == FeedbackParticipantType.TEAMS
                || type == FeedbackParticipantType.OWN_TEAM) {
            giverRecipientName = team;
            giverRecipientLastName = team;
            teamName = "";
        } else if (name != Const.USER_IS_NOBODY && name != Const.USER_IS_TEAM) {
            giverRecipientName = name;
            giverRecipientLastName = lastName;
            teamName = team;
        } else {
            giverRecipientName = name;
            giverRecipientLastName = lastName;
            teamName = "";
        }
        return new String[] { giverRecipientName, giverRecipientLastName, teamName };
    }

    public boolean isFeedbackSessionFullyCompletedByStudent(
            String feedbackSessionName,
            String courseId, String userEmail)
            throws EntityDoesNotExistException {

        if (isFeedbackSessionExists(feedbackSessionName, courseId) == false) {
            throw new EntityDoesNotExistException(
                    "Trying to check a feedback session that does not exist.");
        }

        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName,
                        courseId);

        for (FeedbackQuestionAttributes question : allQuestions) {
            if (!fqLogic.isQuestionFullyAnsweredByUser(question, userEmail)) {
                // If any question is not completely answered, session is not
                // completed
                return false;
            }
        }
        return true;
    }

    private boolean isFeedbackSessionFullyCompletedByInstructor(
            String feedbackSessionName,
            String courseId, String userEmail)
            throws EntityDoesNotExistException {

        if (isFeedbackSessionExists(feedbackSessionName, courseId) == false) {
            throw new EntityDoesNotExistException(
                    "Trying to check a feedback session that does not exist.");
        }

        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForInstructor(feedbackSessionName,
                        courseId,
                        userEmail);

        for (FeedbackQuestionAttributes question : allQuestions) {
            if (!fqLogic.isQuestionFullyAnsweredByUser(question, userEmail)) {
                // If any question is not completely answered, session is not
                // completed
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the feedback session is viewable to the specified user.
     */
    private boolean isFeedbackSessionViewableTo(
            FeedbackSessionAttributes session,
            String userEmail,
            boolean isInstructorOfCourse) {

        // If the session is a private session created by the same user, it is viewable to the user
        if (session.feedbackSessionType == FeedbackSessionType.PRIVATE) {
            return session.creatorEmail.equals(userEmail);
        }
        
        // Allow all instructors to view always
        if (isInstructorOfCourse) {
            return true;
        }

        // Allow viewing if session is viewable to students
        return isFeedbackSessionViewableToStudents(session);
    }

    public boolean isFeedbackSessionViewableToStudents(
            FeedbackSessionAttributes session) {
        // Allow students to view the feedback session if there are questions for them
        List<FeedbackQuestionAttributes> questionsToAnswer =
                fqLogic.getFeedbackQuestionsForStudents(
                        session.feedbackSessionName, session.courseId);
        
        if (session.isVisible() && !questionsToAnswer.isEmpty()) {
            return true;
        }
        
        // Allow students to view the feedback session 
        // if there are any questions for instructors to answer
        // where the responses of the questions are visible to the students
        List<FeedbackQuestionAttributes> questionsWithVisibleResponses = new ArrayList<FeedbackQuestionAttributes>();
        List<FeedbackQuestionAttributes> questionsForInstructors =
                                        fqLogic.getFeedbackQuestionsForCreatorInstructor(session);
        for (FeedbackQuestionAttributes question : questionsForInstructors) {
            if (frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question)) {
                questionsWithVisibleResponses.add(question);
            }
        }
        
        return session.isVisible() && !questionsWithVisibleResponses.isEmpty();
    }
    
    /**
     * Returns true if there are any questions for students to answer.
     * @param session
     * @throws EntityDoesNotExistException
     */
    public boolean isFeedbackSessionForStudentsToAnswer(
                                    FeedbackSessionAttributes session)
                                    throws EntityDoesNotExistException {
        
        List<FeedbackQuestionAttributes> questionsToAnswer =
                fqLogic.getFeedbackQuestionsForStudents(
                        session.feedbackSessionName, session.courseId);
        
        return session.isVisible() && !questionsToAnswer.isEmpty(); 
    }

    private void normalizeMaximumResponseEntities(
            FeedbackQuestionAttributes question,
            Map<String, String> recipients) {

        // change constant to actual maximum size.
        if (question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS) {
            question.numberOfEntitiesToGiveFeedbackTo = recipients.size();
        }
    }

    private void makeEmailStateConsistent(FeedbackSessionAttributes oldSession,
            FeedbackSessionAttributes newSession) {

        // reset sentOpenEmail if the session has opened but is being closed
        // now.
        if (oldSession.sentOpenEmail && !newSession.isOpened()) {
            newSession.sentOpenEmail = false;
        } else if (oldSession.sentOpenEmail) {
            // or else leave it as sent if so.
            newSession.sentOpenEmail = true;
        }

        // reset sentPublishedEmail if the session has been published but is
        // going to be unpublished now.
        if (oldSession.sentPublishedEmail && !newSession.isPublished()) {
            newSession.sentPublishedEmail = false;
        } else if (oldSession.sentPublishedEmail) {
            // or else leave it as sent if so.
            newSession.sentPublishedEmail = true;
        }
    }

    private void sendFeedbackSessionPublishedEmail(
            FeedbackSessionAttributes session) {
        Emails emails = new Emails();
        emails.addFeedbackSessionReminderToEmailsQueue(session,
                Emails.EmailType.FEEDBACK_PUBLISHED);
    }
    
    
}
