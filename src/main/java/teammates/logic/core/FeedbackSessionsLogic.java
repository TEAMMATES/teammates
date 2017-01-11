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
import java.util.Map.Entry;
import java.util.Set;

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
import teammates.common.datatransfer.UserRole;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Const.SystemParams;
import teammates.common.util.Logger;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;

public class FeedbackSessionsLogic {
    
    private static FeedbackSessionsLogic instance;

    private static final Logger log = Logger.getLogger();

    private static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final String QUESTION_ID_FOR_RESPONSE_RATE = "-1";
    private static final int EMAIL_NAME_PAIR = 0;
    private static final int EMAIL_LASTNAME_PAIR = 1;
    private static final int EMAIL_TEAMNAME_PAIR = 2;

    private static final String PARAM_FROM_SECTION = "fromSection";
    private static final String PARAM_IN_SECTION = "inSection";
    private static final String PARAM_IS_INCLUDE_RESPONSE_STATUS = "isIncludeResponseStatus";
    private static final String PARAM_QUESTION_ID = "questionId";
    private static final String PARAM_RANGE = "range";
    private static final String PARAM_SECTION = "section";
    private static final String PARAM_TO_SECTION = "toSection";
    private static final String PARAM_VIEW_TYPE = "viewType";
    
    private static final String ASSUMPTION_FAIL_DELETE_INSTRUCTOR = "Fail to delete instructor respondent for ";
    private static final String ASSUMPTION_FAIL_RESPONSE_ORIGIN = "Client did not indicate the origin of the response(s)";
    private static final String ERROR_NUMBER_OF_RESPONSES_EXCEEDS_RANGE = "Number of responses exceeds the limited range";
    private static final String ERROR_NON_EXISTENT_COURSE = "Error getting feedback session(s): Course does not exist.";
    private static final String ERROR_NON_EXISTENT_STUDENT = "Error getting feedback session(s): Student does not exist.";
    private static final String ERROR_NON_EXISTENT_FS_STRING_FORMAT = "Trying to %s a non-existent feedback session: ";
    private static final String ERROR_NON_EXISTENT_FS_GET = String.format(ERROR_NON_EXISTENT_FS_STRING_FORMAT, "get");
    private static final String ERROR_NON_EXISTENT_FS_UPDATE = String.format(ERROR_NON_EXISTENT_FS_STRING_FORMAT, "update");
    private static final String ERROR_NON_EXISTENT_FS_CHECK = String.format(ERROR_NON_EXISTENT_FS_STRING_FORMAT, "check");
    private static final String ERROR_NON_EXISTENT_FS_VIEW = String.format(ERROR_NON_EXISTENT_FS_STRING_FORMAT, "view");
    private static final String ERROR_FS_ALREADY_PUBLISH = "Error publishing feedback session: "
                                                           + "Session has already been published.";
    private static final String ERROR_FS_ALREADY_UNPUBLISH = "Error unpublishing feedback session: "
                                                             + "Session has already been unpublished.";
    private static final String ERROR_FS_PRIVATE_PUBLISH = "Error publishing feedback session: "
                                                           + "Session is private and can't be published.";
    private static final String ERROR_FS_PRIVATE_UNPUBLISH = "Error unpublishing feedback session: "
                                                             + "Session is private and can't be unpublished.";

    public static FeedbackSessionsLogic inst() {
        if (instance == null) {
            instance = new FeedbackSessionsLogic();
        }
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
        copiedFeedbackSession.setCreatorEmail(instructorEmail);
        copiedFeedbackSession.setFeedbackSessionName(newFeedbackSessionName);
        copiedFeedbackSession.setCourseId(newCourseId);
        copiedFeedbackSession.setCreatedTime(new Date());
        copiedFeedbackSession.setRespondingInstructorList(new HashSet<String>());
        copiedFeedbackSession.setRespondingStudentList(new HashSet<String>());
        fsDb.createEntity(copiedFeedbackSession);
        
        List<FeedbackQuestionAttributes> feedbackQuestions =
                fqLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        for (FeedbackQuestionAttributes question : feedbackQuestions) {
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

        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_COURSE);
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
     * <br>
     * Omits sessions from archived courses if omitArchived == true
     * 
     * @param googleId
     * @return
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(String googleId, boolean omitArchived) {

        List<InstructorAttributes> instructorList =
                instructorsLogic.getInstructorsForGoogleId(googleId, omitArchived);

        return getFeedbackSessionsListForInstructor(instructorList);
    }
    
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(
            List<InstructorAttributes> instructorList) {

        List<FeedbackSessionAttributes> fsList = new ArrayList<FeedbackSessionAttributes>();
        
        for (InstructorAttributes instructor : instructorList) {
            fsList.addAll(getFeedbackSessionsListForCourse(instructor.courseId, instructor.email));
        }

        return fsList;
    }
    
    public List<FeedbackSessionAttributes> getFeedbackSessionListForInstructor(
            InstructorAttributes instructor) {
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
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_GET + courseId + "/" + feedbackSessionName);
        }

        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle =
                new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        Map<String, Map<String, String>> recipientList = new HashMap<String, Map<String, String>>();
        
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
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_GET + courseId + "/" + feedbackSessionName);
        }

        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle =
                new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        Map<String, Map<String, String>> recipientList = new HashMap<String, Map<String, String>>();

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
                        fsa.getFeedbackSessionName(), Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {
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
                        fsa.getFeedbackSessionName(), Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {
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

        if (fsa == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_GET + courseId + "/" + feedbackSessionName);
        }
        StudentAttributes student = studentsLogic.getStudentForEmail(courseId, userEmail);
        if (student == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_STUDENT);
        }

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle =
                new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        Map<String, Map<String, String>> recipientList = new HashMap<String, Map<String, String>>();

        List<FeedbackQuestionAttributes> questions = fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName,
                courseId);

        Set<String> hiddenInstructorEmails = null;
        
        for (FeedbackQuestionAttributes question : questions) {
            if (question.getRecipientType() == FeedbackParticipantType.INSTRUCTORS) {
                hiddenInstructorEmails = getHiddenInstructorEmails(courseId);
                break;
            }
        }

        for (FeedbackQuestionAttributes question : questions) {

            updateBundleAndRecipientListWithResponsesForStudent(userEmail, student,
                    bundle, recipientList, question, hiddenInstructorEmails);
        }

        return new FeedbackSessionQuestionsBundle(fsa, bundle, recipientList);
    }
    
    public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsForStudent(
            String feedbackSessionName, String courseId, String feedbackQuestionId, String userEmail)
            throws EntityDoesNotExistException {

        FeedbackSessionAttributes fsa = fsDb.getFeedbackSession(
                courseId, feedbackSessionName);
        
        if (fsa == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_GET + courseId + "/" + feedbackSessionName);
        }
        
        StudentAttributes student = studentsLogic.getStudentForEmail(courseId, userEmail);
        if (student == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_STUDENT);
        }

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle =
                new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        Map<String, Map<String, String>> recipientList = new HashMap<String, Map<String, String>>();

        FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(feedbackQuestionId);

        Set<String> hiddenInstructorEmails = null;

        if (question.getRecipientType() == FeedbackParticipantType.INSTRUCTORS) {
            hiddenInstructorEmails = getHiddenInstructorEmails(courseId);
        }

        updateBundleAndRecipientListWithResponsesForStudent(userEmail, student,
                bundle, recipientList, question, hiddenInstructorEmails);

        return new FeedbackSessionQuestionsBundle(fsa, bundle, recipientList);
    }

    private void updateBundleAndRecipientListWithResponsesForStudent(
            String userEmail,
            StudentAttributes student,
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle,
            Map<String, Map<String, String>> recipientList,
            FeedbackQuestionAttributes question,
            Set<String> hiddenInstructorEmails)
            throws EntityDoesNotExistException {
        List<FeedbackResponseAttributes> responses =
                frLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(
                        question, student);
        Map<String, String> recipients =
                fqLogic.getRecipientsForQuestion(question, userEmail, null, student);

        removeHiddenInstructors(question, responses, recipients, hiddenInstructorEmails);

        normalizeMaximumResponseEntities(question, recipients);

        bundle.put(question, responses);
        recipientList.put(question.getId(), recipients);
    }

    /**
     * Removes instructors who are not displayed to students from
     * {@code recipients}. Responses to the hidden instructors are also removed
     * from {@code responses}.
     * 
     * @param question
     *            the feedback question
     * @param responses
     *            a {@link List} of feedback responses to the {@code question}
     * @param recipients
     *            a {@link Map} that maps the emails of the recipients to their
     *            names
     * @param hiddenInstructorEmails
     *            a {@link Set} of emails of the instructors who are not
     *            displayed to students
     */
    private void removeHiddenInstructors(FeedbackQuestionAttributes question,
                                         List<FeedbackResponseAttributes> responses,
                                         Map<String, String> recipients,
                                         Set<String> hiddenInstructorEmails) {
 
        boolean isNoChangeRequired = hiddenInstructorEmails == null
                                   || hiddenInstructorEmails.isEmpty()
                                   || question.getRecipientType() != FeedbackParticipantType.INSTRUCTORS;

        if (isNoChangeRequired) {
            return;
        }

        for (String instructorEmail : hiddenInstructorEmails) {

            if (recipients.containsKey(instructorEmail)) {
                recipients.remove(instructorEmail);
            }

            // Remove responses to the hidden instructors if they have been stored already
            Iterator<FeedbackResponseAttributes> iterResponse = responses.iterator();

            while (iterResponse.hasNext()) {

                FeedbackResponseAttributes response = iterResponse.next();

                if (response.recipient.equals(instructorEmail)) {
                    iterResponse.remove();
                }
            }
        }
    }

    /**
     * @param courseId
     *            the ID of the course
     * @return a {@link Set} of emails of the instructors who are not displayed
     *         to students in the course specified by {@code courseId}
     */
    private Set<String> getHiddenInstructorEmails(String courseId) {

        InstructorsLogic instructorLogic = InstructorsLogic.inst();

        List<InstructorAttributes> instructors = instructorLogic.getInstructorsForCourse(courseId);
        Set<String> hiddenInstructorEmails = new HashSet<>();

        for (InstructorAttributes instructor : instructors) {
            if (!instructor.isDisplayedToStudents()) {
                hiddenInstructorEmails.add(instructor.email);
            }
        }

        return hiddenInstructorEmails;
    }

    public FeedbackSessionResponseStatus getFeedbackSessionResponseStatus(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        FeedbackSessionAttributes session = fsDb.getFeedbackSession(
                courseId, feedbackSessionName);

        if (session == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_VIEW + courseId + "/" + feedbackSessionName);
        }

        List<FeedbackQuestionAttributes> allQuestions = fqLogic.getFeedbackQuestionsForSession(feedbackSessionName,
                        courseId);
        
        CourseRoster roster = new CourseRoster(new StudentsDb().getStudentsForCourse(courseId),
                                               new InstructorsDb().getInstructorsForCourse(courseId));
        return getFeedbackSessionResponseStatus(session, roster, allQuestions);
    }

    /**
     * Gets results of a feedback session to show to an instructor from an indicated question
     * This will not retrieve the list of comments for this question
     * @throws ExceedingRangeException if the results are beyond the range
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromQuestion(
            String feedbackSessionName, String courseId, String userEmail, String questionId)
                    throws EntityDoesNotExistException {

        // Load details of students and instructors once and pass it to callee
        // methods
        // (rather than loading them many times).
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_IN_SECTION, "false");
        params.put(PARAM_FROM_SECTION, "false");
        params.put(PARAM_TO_SECTION, "false");
        params.put(PARAM_QUESTION_ID, questionId);
        
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail,
                                                          UserRole.INSTRUCTOR, roster, params);
    }
    
    /**
     * Gets results of a feedback session to show to an instructor from an indicated question
     * and in a section
     * This will not retrieve the list of comments for this question
     * @throws ExceedingRangeException if the results are beyond the range
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromQuestionInSection(
                                                String feedbackSessionName, String courseId, String userEmail,
                                                String questionId, String selectedSection)
                                        throws EntityDoesNotExistException {

        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_IN_SECTION, "true");
        params.put(PARAM_FROM_SECTION, "false");
        params.put(PARAM_TO_SECTION, "false");
        params.put(PARAM_QUESTION_ID, questionId);
        params.put(PARAM_SECTION, selectedSection);
        
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail,
                                                          UserRole.INSTRUCTOR, roster, params);
    }

    /**
     * Gets results of a feedback session to show to an instructor in an indicated range
     * @throws ExceedingRangeException if the results are beyond the range
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorWithinRangeFromView(
            String feedbackSessionName, String courseId, String userEmail, long range, String viewType)
            throws EntityDoesNotExistException {
        
        return getFeedbackSessionResultsForInstructorInSectionWithinRangeFromView(
                feedbackSessionName, courseId, userEmail, null, range, viewType);
    }

    /**
     * Gets results of a feedback session to show to an instructor in a section in an indicated range
     * @throws ExceedingRangeException if the results are beyond the range
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorInSectionWithinRangeFromView(
            String feedbackSessionName, String courseId, String userEmail, String section, long range, String viewType)
            throws EntityDoesNotExistException {
        
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_IN_SECTION, "true");
        params.put(PARAM_FROM_SECTION, "false");
        params.put(PARAM_TO_SECTION, "false");
        params.put(PARAM_SECTION, section);
        if (range > 0) {
            params.put(PARAM_RANGE, String.valueOf(range));
        }
        params.put(PARAM_VIEW_TYPE, viewType);

        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail,
                                                          UserRole.INSTRUCTOR, roster, params);
    }

    /**
     * Gets results of a feedback session to show to an instructor in a section in an indicated range
     * @throws ExceedingRangeException if the results are beyond the range
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromSectionWithinRange(
            String feedbackSessionName, String courseId, String userEmail, String section, long range)
            throws EntityDoesNotExistException {
        
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_IN_SECTION, "false");
        params.put(PARAM_FROM_SECTION, "true");
        params.put(PARAM_TO_SECTION, "false");
        params.put(PARAM_SECTION, section);
        if (range > 0) {
            params.put(PARAM_RANGE, String.valueOf(range));
        }
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail,
                                                          UserRole.INSTRUCTOR, roster, params);
    }

    /**
     * Gets results of a feedback session to show to an instructor in a section in an indicated range
     * @throws ExceedingRangeException if the results are beyond the range
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorToSectionWithinRange(
            String feedbackSessionName, String courseId, String userEmail, String section, long range)
            throws EntityDoesNotExistException {
        
        CourseRoster roster = new CourseRoster(
                new StudentsDb().getStudentsForCourse(courseId),
                new InstructorsDb().getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<String, String>();
        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_IN_SECTION, "false");
        params.put(PARAM_FROM_SECTION, "false");
        params.put(PARAM_TO_SECTION, "true");
        params.put(PARAM_SECTION, section);
        if (range > 0) {
            params.put(PARAM_RANGE, String.valueOf(range));
        }
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail,
                                                          UserRole.INSTRUCTOR, roster, params);
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
        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_IN_SECTION, "true");
        params.put(PARAM_FROM_SECTION, "false");
        params.put(PARAM_TO_SECTION, "false");
        params.put(PARAM_SECTION, section);
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName,
                courseId, userEmail, UserRole.INSTRUCTOR, roster, params);
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
        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "false");
        params.put(PARAM_IN_SECTION, "false");
        params.put(PARAM_FROM_SECTION, "true");
        params.put(PARAM_TO_SECTION, "false");
        params.put(PARAM_SECTION, section);
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName,
                courseId, userEmail, UserRole.INSTRUCTOR, roster, params);
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
        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_IN_SECTION, "false");
        params.put(PARAM_FROM_SECTION, "false");
        params.put(PARAM_TO_SECTION, "true");
        params.put(PARAM_SECTION, section);
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName,
                courseId, userEmail, UserRole.INSTRUCTOR, roster, params);
    }

    /**
     * Gets results of a feedback session in a roster to show to an instructor.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructor(
            String feedbackSessionName, String courseId, String userEmail,
            CourseRoster roster, Boolean isIncludeResponseStatus)
            throws EntityDoesNotExistException {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, String.valueOf(isIncludeResponseStatus));
        params.put(PARAM_IN_SECTION, "true");
        params.put(PARAM_FROM_SECTION, "false");
        params.put(PARAM_TO_SECTION, "false");
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName,
                courseId, userEmail,
                UserRole.INSTRUCTOR, roster, params);
    }

    /**
     * Gets results of a feedback session to show to a student.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForStudent(
            String feedbackSessionName, String courseId, String userEmail)
            throws EntityDoesNotExistException {
        return getFeedbackSessionResultsForUserInSectionByQuestions(
                feedbackSessionName, courseId, userEmail,
                UserRole.STUDENT, null);
    }
    
    /**
     * Gets results of a feedback session to show to a student.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForStudent(
            String feedbackSessionName, String courseId, String userEmail, CourseRoster roster)
            throws EntityDoesNotExistException {
        return getFeedbackSessionResultsForUserInSectionByQuestions(
                feedbackSessionName, courseId, userEmail,
                UserRole.STUDENT, null, roster);
    }

    public String getFeedbackSessionResultsSummaryAsCsv(
            String feedbackSessionName, String courseId,
            String userEmail, String filterText, boolean isMissingResponsesShown, boolean isStatsShown)
            throws EntityDoesNotExistException, ExceedingRangeException {
        
        return getFeedbackSessionResultsSummaryInSectionAsCsv(
                feedbackSessionName, courseId, userEmail, null, filterText, isMissingResponsesShown, isStatsShown);
    }

    public String getFeedbackSessionResultsSummaryInSectionAsCsv(
            String feedbackSessionName, String courseId, String userEmail,
            String section, String filterText, boolean isMissingResponsesShown, boolean isStatsShown)
            throws EntityDoesNotExistException, ExceedingRangeException {
        
        long indicatedRange = section == null ? 2000 : -1;
        FeedbackSessionResultsBundle results = getFeedbackSessionResultsForInstructorInSectionWithinRangeFromView(
                feedbackSessionName, courseId, userEmail, section,
                indicatedRange, Const.FeedbackSessionResults.QUESTION_SORT_TYPE);
        
        if (!results.isComplete) {
            throw new ExceedingRangeException(ERROR_NUMBER_OF_RESPONSES_EXCEEDS_RANGE);
        }
        // sort responses by giver > recipient > qnNumber
        Collections.sort(results.responses,
                results.compareByGiverRecipientQuestion);
        
        StringBuilder exportBuilder = new StringBuilder(100);

        exportBuilder.append(String.format("Course,%s", Sanitizer.sanitizeForCsv(results.feedbackSession.getCourseId())))
                     .append(Const.EOL)
                     .append(String.format("Session Name,%s",
                             Sanitizer.sanitizeForCsv(results.feedbackSession.getFeedbackSessionName())))
                     .append(Const.EOL);
        
        if (section != null) {
            exportBuilder.append(String.format("Section Name,%s", Sanitizer.sanitizeForCsv(section)))
                         .append(Const.EOL);
        }

        exportBuilder.append(Const.EOL).append(Const.EOL);
        
        Set<Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> entrySet =
                results.getQuestionResponseMap().entrySet();
        
        if (filterText != null && !filterText.isEmpty()) {
            entrySet = filterQuestions(entrySet, filterText.toLowerCase());
        }
        
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry : entrySet) {
            exportBuilder.append(getFeedbackSessionResultsForQuestionInCsvFormat(
                    results, entry, isMissingResponsesShown, isStatsShown));
        }
        return exportBuilder.toString();
        
    }

    private Set<Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> filterQuestions(
            Set<Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> entrySet,
            String filterText) {
        
        for (Iterator<Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> it =
                entrySet.iterator(); it.hasNext(); ) {
            Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry = it.next();
            if (!entry.getKey().getQuestionMetaData().getValue().toLowerCase().contains(filterText)) {
                it.remove();
            }
        }
       
        return entrySet;
    }

    private StringBuilder getFeedbackSessionResultsForQuestionInCsvFormat(
            FeedbackSessionResultsBundle fsrBundle,
            Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry,
            boolean isMissingResponsesShown, boolean isStatsShown) {
        
        FeedbackQuestionAttributes question = entry.getKey();
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        List<FeedbackResponseAttributes> allResponses = entry.getValue();
        
        StringBuilder exportBuilder = new StringBuilder();
        
        exportBuilder.append("Question " + Integer.toString(question.questionNumber) + ","
                + Sanitizer.sanitizeForCsv(questionDetails.getQuestionText())
                + Const.EOL + Const.EOL);
        
        String statistics = questionDetails.getQuestionResultStatisticsCsv(allResponses,
                                    question, fsrBundle);
        if (!statistics.isEmpty() && isStatsShown) {
            exportBuilder.append("Summary Statistics,").append(Const.EOL);
            exportBuilder.append(statistics).append(Const.EOL);
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
                    possibleGiversWithoutResponses, response.giver, fsrBundle);
            
            boolean isNewGiver = !prevGiver.equals(response.giver);
            // print missing responses from the current giver
            if (isNewGiver) {
                exportBuilder.append(getRowsOfPossibleRecipientsInCsvFormat(fsrBundle,
                        question, questionDetails,
                        possibleRecipientsForGiver, prevGiver));
                String giverIdentifier = question.giverType == FeedbackParticipantType.TEAMS
                                             ? fsrBundle.getFullNameFromRoster(response.giver)
                                             : response.giver;
                
                possibleRecipientsForGiver = fsrBundle.getPossibleRecipients(question, giverIdentifier);
            }
            
            removeParticipantIdentifierFromList(question.recipientType, possibleRecipientsForGiver,
                                                response.recipient, fsrBundle);
            prevGiver = response.giver;
            
            // Append row(s)
            exportBuilder.append(questionDetails.getCsvDetailedResponsesRow(fsrBundle, response, question));
        }
        
        // add the rows for the possible givers and recipients who have missing responses
        if (isMissingResponsesShown) {
            exportBuilder.append(
                    getRemainingRowsInCsvFormat(
                            fsrBundle, entry, question, questionDetails,
                            possibleGiversWithoutResponses, possibleRecipientsForGiver, prevGiver));
        }
        
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
            List<String> possibleRecipientsForRemainingGiver =
                    results.getPossibleRecipients(entry.getKey(), possibleGiverWithNoResponses);
            
            exportBuilder.append(getRowsOfPossibleRecipientsInCsvFormat(results,
                    question, questionDetails, possibleRecipientsForRemainingGiver,
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
            
            if (questionDetails.shouldShowNoResponseText(question)) {
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
                fsDb.getFeedbackSessionsPossiblyNeedingPublishedEmail();
        List<FeedbackSessionAttributes> sessionsToSendEmailsFor =
                new ArrayList<FeedbackSessionAttributes>();

        for (FeedbackSessionAttributes session : sessions) {
            // automated emails are required only for custom publish times
            if (session.isPublished() && !TimeHelper.isSpecialTime(session.getResultsVisibleFromTime())) {
                sessionsToSendEmailsFor.add(session);
            }
        }
        return sessionsToSendEmailsFor;
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedOpenEmailsToBeSent() {
        List<FeedbackSessionAttributes> sessions =
                fsDb.getFeedbackSessionsPossiblyNeedingOpenEmail();
        List<FeedbackSessionAttributes> sessionsToSendEmailsFor =
                new ArrayList<FeedbackSessionAttributes>();

        for (FeedbackSessionAttributes session : sessions) {
            if (session.getFeedbackSessionType() != FeedbackSessionType.PRIVATE && session.isOpened()) {
                sessionsToSendEmailsFor.add(session);
            }
        }
        return sessionsToSendEmailsFor;
    }

    public boolean isCreatorOfSession(String feedbackSessionName, String courseId, String userEmail) {
        FeedbackSessionAttributes fs = getFeedbackSession(feedbackSessionName, courseId);
        return fs.getCreatorEmail().equals(userEmail);
    }

    public boolean isFeedbackSessionExists(String feedbackSessionName, String courseId) {
        return fsDb.getFeedbackSession(courseId, feedbackSessionName) != null;
    }

    public boolean isFeedbackSessionHasQuestionForStudents(
            String feedbackSessionName,
            String courseId) throws EntityDoesNotExistException {
        if (!isFeedbackSessionExists(feedbackSessionName, courseId)) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_CHECK + courseId + "/" + feedbackSessionName);
        }

        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName,
                        courseId);

        return !allQuestions.isEmpty();
    }

    public boolean isFeedbackSessionCompletedByStudent(FeedbackSessionAttributes fsa, String userEmail) {
        if (fsa.getRespondingStudentList().contains(userEmail)) {
            return true;
        }
        
        String feedbackSessionName = fsa.getFeedbackSessionName();
        String courseId = fsa.getCourseId();
        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName, courseId);
        // if there is no question for students, session is complete
        return allQuestions.isEmpty();
    }

    public boolean isFeedbackSessionCompletedByInstructor(FeedbackSessionAttributes fsa, String userEmail)
            throws EntityDoesNotExistException {
        if (fsa.getRespondingInstructorList().contains(userEmail)) {
            return true;
        }
        
        String feedbackSessionName = fsa.getFeedbackSessionName();
        String courseId = fsa.getCourseId();
        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForInstructor(feedbackSessionName, courseId, userEmail);
        // if there is no question for instructor, session is complete
        return allQuestions.isEmpty();
    }

    public void updateFeedbackSession(FeedbackSessionAttributes newSession)
            throws InvalidParametersException, EntityDoesNotExistException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, newSession);

        FeedbackSessionAttributes oldSession =
                fsDb.getFeedbackSession(newSession.getCourseId(),
                        newSession.getFeedbackSessionName());

        if (oldSession == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + newSession.getCourseId()
                                                  + "/" + newSession.getFeedbackSessionName());
        }

        // These can't be changed anyway. Copy values to defensively avoid
        // invalid parameters.
        newSession.setCreatorEmail(oldSession.getCreatorEmail());
        newSession.setCreatedTime(oldSession.getCreatedTime());

        if (newSession.getInstructions() == null) {
            newSession.setInstructions(oldSession.getInstructions());
        }
        if (newSession.getStartTime() == null) {
            newSession.setStartTime(oldSession.getStartTime());
        }
        if (newSession.getEndTime() == null) {
            newSession.setEndTime(oldSession.getEndTime());
        }
        if (newSession.getFeedbackSessionType() == null) {
            newSession.setFeedbackSessionType(oldSession.getFeedbackSessionType());
        }
        if (newSession.getSessionVisibleFromTime() == null) {
            newSession.setSessionVisibleFromTime(oldSession.getSessionVisibleFromTime());
        }
        if (newSession.getResultsVisibleFromTime() == null) {
            newSession.setResultsVisibleFromTime(oldSession.getResultsVisibleFromTime());
        }

        makeEmailStateConsistent(oldSession, newSession);

        fsDb.updateFeedbackSession(newSession);
    }
    
    public void updateRespondentsForInstructor(String oldEmail, String newEmail, String courseId)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        List<FeedbackSessionAttributes> feedbackSessions = getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes session : feedbackSessions) {
            fsDb.updateInstructorRespondent(oldEmail, newEmail, session);
        }
    }

    public void updateRespondentsForStudent(String oldEmail, String newEmail, String courseId)
            throws InvalidParametersException, EntityDoesNotExistException {

        List<FeedbackSessionAttributes> feedbackSessions = getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes session : feedbackSessions) {
            fsDb.updateStudentRespondent(oldEmail, newEmail, session);
        }
    }
    
    public void updateRespondentsForSession(String feedbackSessionName, String courseId)
            throws InvalidParametersException, EntityDoesNotExistException {

        clearInstructorRespondents(feedbackSessionName, courseId);
        clearStudentRespondents(feedbackSessionName, courseId);
        
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
                for (FeedbackQuestionAttributes question : instructorQns) {
                    questionIds.add(question.getId());
                }
                instructorQuestionsMap.put(instructor.email, questionIds);
            }
        }
        
        Set<String> respondingStudentList = new HashSet<String>();
        Set<String> respondingInstructorList = new HashSet<String>();
        List<FeedbackResponseAttributes> responses = frLogic.getFeedbackResponsesForSession(feedbackSessionName, courseId);
        for (FeedbackResponseAttributes response : responses) {
            List<String> instructorQuestions = instructorQuestionsMap.get(response.giver);
            if (instructorQuestions != null && instructorQuestions.contains(response.feedbackQuestionId)) {
                respondingInstructorList.add(response.giver);
            } else {
                respondingStudentList.add(response.giver);
            }
        }
        
        addInstructorRespondents(new ArrayList<String>(respondingInstructorList), feedbackSessionName, courseId);
        addStudentRespondents(new ArrayList<String>(respondingStudentList), feedbackSessionName, courseId);
    }

    public void deleteInstructorFromRespondentsList(InstructorAttributes instructor) {
        if (instructor == null || instructor.email == null) {
            return;
        }
        List<FeedbackSessionAttributes> sessionsToUpdate =
                fsDb.getFeedbackSessionsForCourse(instructor.courseId);

        for (FeedbackSessionAttributes session : sessionsToUpdate) {
            try {
                deleteInstructorRespondent(instructor.email, session.getFeedbackSessionName(), session.getCourseId());
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                Assumption.fail(ASSUMPTION_FAIL_DELETE_INSTRUCTOR + session.getFeedbackSessionName());
            }
        }
    }

    public void deleteStudentFromRespondentsList(StudentAttributes student) {
        if (student == null || student.email == null) {
            return;
        }
        List<FeedbackSessionAttributes> sessionsToUpdate =
                fsDb.getFeedbackSessionsForCourse(student.course);

        for (FeedbackSessionAttributes session : sessionsToUpdate) {
            try {
                deleteStudentFromRespondentList(student.email, session.getFeedbackSessionName(), session.getCourseId());
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                Assumption.fail(ASSUMPTION_FAIL_DELETE_INSTRUCTOR + session.getFeedbackSessionName());
            }
        }
    }

    public void addInstructorRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, email);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if (sessionToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }

        fsDb.addInstructorRespondent(email, sessionToUpdate);
    }

    public void addInstructorRespondents(List<String> emails, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, emails);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if (sessionToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }

        fsDb.addInstructorRespondents(emails, sessionToUpdate);
    }

    public void clearInstructorRespondents(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if (sessionToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }

        fsDb.clearInstructorRespondents(sessionToUpdate);
    }

    public void addStudentRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, email);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if (sessionToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }

        fsDb.addStudentRespondent(email, sessionToUpdate);
    }

    public void addStudentRespondents(List<String> emails, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, emails);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if (sessionToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }

        fsDb.addStudentRespondents(emails, sessionToUpdate);
    }

    public void clearStudentRespondents(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if (sessionToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }

        fsDb.clearStudentRespondents(sessionToUpdate);
    }

    public void deleteInstructorRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, email);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if (sessionToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }

        fsDb.deleteInstructorRespondent(email, sessionToUpdate);
    }

    public void deleteStudentFromRespondentList(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, email);

        FeedbackSessionAttributes sessionToUpdate = getFeedbackSession(feedbackSessionName, courseId);
        if (sessionToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }

        fsDb.deleteStudentRespondent(email, sessionToUpdate);
    }

    /**
     * This method is called when the user publishes a feedback session
     * manually. Preconditions: * The feedback session has to be set as
     * manually/automatically published. The feedback session can't be private
     */
    public void publishFeedbackSession(FeedbackSessionAttributes sessionToPublish)
            throws EntityDoesNotExistException, InvalidParametersException {
        if (sessionToPublish.isPrivateSession()) {
            throw new InvalidParametersException(ERROR_FS_PRIVATE_PUBLISH);
        }

        if (sessionToPublish.isPublished()) {
            throw new InvalidParametersException(ERROR_FS_ALREADY_PUBLISH);
        }

        sessionToPublish.setResultsVisibleFromTime(currentDateTime(sessionToPublish));
        updateFeedbackSession(sessionToPublish);
    }

    private Date currentDateTime(FeedbackSessionAttributes sessionToPublish) {
        Calendar now = TimeHelper.now(sessionToPublish.getTimeZone());
        return now.getTime();
    }

    /**
     * This method is called when the user unpublishes a feedback session
     * manually. Preconditions: * The feedback session has to be set as manually
     * published.
     */
    public void unpublishFeedbackSession(FeedbackSessionAttributes sessionToUnpublish)
            throws EntityDoesNotExistException, InvalidParametersException {
        if (sessionToUnpublish.isPrivateSession()) {
            throw new InvalidParametersException(ERROR_FS_PRIVATE_UNPUBLISH);
        }

        if (!sessionToUnpublish.isPublished()) {
            throw new InvalidParametersException(ERROR_FS_ALREADY_UNPUBLISH);
        }

        sessionToUnpublish.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        updateFeedbackSession(sessionToUnpublish);
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsClosingWithinTimeLimit() {
        ArrayList<FeedbackSessionAttributes> requiredSessions = new
                ArrayList<FeedbackSessionAttributes>();

        List<FeedbackSessionAttributes> nonPrivateSessions =
                fsDb.getFeedbackSessionsPossiblyNeedingClosingEmail();

        for (FeedbackSessionAttributes session : nonPrivateSessions) {
            if (session.getFeedbackSessionType() != FeedbackSessionType.PRIVATE
                    && session.isClosingWithinTimeLimit(SystemParams.NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT)) {
                requiredSessions.add(session);
            }
        }

        return requiredSessions;
    }
    
    /**
     * @return returns a list of sessions that were closed within past hour
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsClosedWithinThePastHour() {
        List<FeedbackSessionAttributes> requiredSessions = new ArrayList<FeedbackSessionAttributes>();
        List<FeedbackSessionAttributes> nonPrivateSessions =
                fsDb.getFeedbackSessionsPossiblyNeedingClosedEmail();

        for (FeedbackSessionAttributes session : nonPrivateSessions) {
            // is session closed in the past 1 hour
            if (session.getFeedbackSessionType() != FeedbackSessionType.PRIVATE
                    && session.isClosedWithinPastHour()) {
                requiredSessions.add(session);
            }
        }
        return requiredSessions;
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
            log.warning(TeammatesException.toStringWithStackTrace(e));
        }

        FeedbackSessionAttributes sessionToDelete = new FeedbackSessionAttributes();
        sessionToDelete.setFeedbackSessionName(feedbackSessionName);
        sessionToDelete.setCourseId(courseId);

        fsDb.deleteEntity(sessionToDelete);

    }

    public FeedbackSessionDetailsBundle getFeedbackSessionDetails(
            FeedbackSessionAttributes fsa) throws EntityDoesNotExistException {

        FeedbackSessionDetailsBundle details =
                new FeedbackSessionDetailsBundle(fsa);

        details.stats.expectedTotal = 0;
        details.stats.submittedTotal = 0;
        
        switch (fsa.getFeedbackSessionType()) {
        case STANDARD:
            List<StudentAttributes> students = studentsLogic.getStudentsForCourse(fsa.getCourseId());
            List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(fsa.getCourseId());
            List<FeedbackQuestionAttributes> questions =
                    fqLogic.getFeedbackQuestionsForSession(fsa.getFeedbackSessionName(), fsa.getCourseId());
            List<FeedbackQuestionAttributes> studentQns = fqLogic.getFeedbackQuestionsForStudents(questions);

            if (!studentQns.isEmpty()) {
                details.stats.expectedTotal += students.size();
            }
        
            for (InstructorAttributes instructor : instructors) {
                List<FeedbackQuestionAttributes> instructorQns =
                        fqLogic.getFeedbackQuestionsForInstructor(questions, fsa.isCreator(instructor.email));
                if (!instructorQns.isEmpty()) {
                    details.stats.expectedTotal += 1;
                }
            }
            
            details.stats.submittedTotal += fsa.getRespondingStudentList().size() + fsa.getRespondingInstructorList().size();

            break;

        case PRIVATE:
            List<FeedbackQuestionAttributes> instuctorQuestions =
                    fqLogic.getFeedbackQuestionsForInstructor(fsa.getFeedbackSessionName(),
                                                              fsa.getCourseId(),
                                                              fsa.getCreatorEmail());
            List<FeedbackQuestionAttributes> validQuestions =
                    fqLogic.getQuestionsWithRecipients(instuctorQuestions, fsa.getCreatorEmail());
            if (validQuestions.isEmpty()) {
                break;
            }
            details.stats.expectedTotal = 1;
            if (isFeedbackSessionFullyCompletedByInstructor(fsa.getFeedbackSessionName(),
                                                            fsa.getCourseId(),
                                                            fsa.getCreatorEmail())) {
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
            UserRole role, String section)
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
            UserRole role, String section, CourseRoster roster)
            throws EntityDoesNotExistException {

        FeedbackSessionAttributes session = fsDb.getFeedbackSession(
                courseId, feedbackSessionName);

        if (session == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_VIEW + courseId + "/" + feedbackSessionName);
        }

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

        List<FeedbackQuestionAttributes> allQuestions = fqLogic.getFeedbackQuestionsForSession(
                                                                    feedbackSessionName, courseId);
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

            boolean thisQuestionHasResponses = !responsesForThisQn.isEmpty();
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
        if (role == UserRole.STUDENT) {
            student = studentsLogic.getStudentForEmail(courseId, userEmail);
            List<StudentAttributes> studentsInTeam = studentsLogic
                    .getStudentsForTeam(student.team, courseId);
            for (StudentAttributes teammates : studentsInTeam) {
                studentsEmailInTeam.add(teammates.email);
            }
        }
        
        InstructorAttributes instructor = null;
        if (role == UserRole.INSTRUCTOR) {
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
            if (isVisibleResponseComment) {
                if (!frcLogic.isNameVisibleTo(frc, relatedResponse, userEmail, roster)) {
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
            sortByCreatedDate(responseCommentList);
        }
        
        addSectionTeamNamesToTable(sectionTeamNameTable, roster, courseId, userEmail, role, feedbackSessionName, section);

        FeedbackSessionResultsBundle results =
                new FeedbackSessionResultsBundle(
                        session, responses, relevantQuestions, emailNameTable,
                        emailLastNameTable, emailTeamNameTable, sectionTeamNameTable,
                        visibilityTable, responseStatus, roster, responseComments);

        return results;
    }

    private FeedbackSessionResultsBundle getFeedbackSessionResultsForUserWithParams(
            String feedbackSessionName, String courseId, String userEmail,
            UserRole role, CourseRoster roster, Map<String, String> params)
            throws EntityDoesNotExistException {
        

        FeedbackSessionAttributes session = fsDb.getFeedbackSession(
                courseId, feedbackSessionName);

        if (session == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_VIEW + courseId + "/" + feedbackSessionName);
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
        if (role == UserRole.INSTRUCTOR && !params.containsKey(PARAM_QUESTION_ID)) {
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
        
        boolean isIncludeResponseStatus = Boolean.parseBoolean(params.get(PARAM_IS_INCLUDE_RESPONSE_STATUS));
        
        String section = params.get(PARAM_SECTION);
        
        if (params.get(PARAM_QUESTION_ID) != null) {
            String questionId = params.get(PARAM_QUESTION_ID);
            boolean isQueryingResponseRateStatus = questionId.equals(QUESTION_ID_FOR_RESPONSE_RATE);
            
            if (isQueryingResponseRateStatus) {
                responseStatus = section == null && isIncludeResponseStatus
                               ? getFeedbackSessionResponseStatus(session, roster, allQuestions)
                               : null;
            } else {
                FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(questionId);
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
                                        question, userEmail, UserRole.INSTRUCTOR, section);
                    }
    
                    boolean thisQuestionHasResponses = !responsesForThisQn.isEmpty();
                    if (thisQuestionHasResponses) {
                        for (FeedbackResponseAttributes response : responsesForThisQn) {
                            boolean isVisibleResponse = false;
                            if (response.giver.equals(userEmail)
                                    || response.recipient.equals(userEmail)
                                            && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                                    || role == UserRole.INSTRUCTOR
                                            && question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)
                                    || role == UserRole.STUDENT
                                            && question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
                                isVisibleResponse = true;
                            }
                            InstructorAttributes instructor = null;
                            if (role == UserRole.INSTRUCTOR) {
                                instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
                            }
                            if (isVisibleResponse && instructor != null) {
                                boolean isGiverSectionRestricted =
                                        !instructor.isAllowedForPrivilege(
                                                response.giverSection, response.feedbackSessionName,
                                                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
                                // If instructors are not restricted to view the giver's section,
                                // they are allowed to view responses to GENERAL, subject to visibility options
                                boolean isRecipientSectionRestricted =
                                        question.recipientType != FeedbackParticipantType.NONE
                                        && !instructor.isAllowedForPrivilege(
                                                   response.recipientSection, response.feedbackSessionName,
                                                   Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
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
            }

            addSectionTeamNamesToTable(sectionTeamNameTable, roster, courseId, userEmail, role,
                                       feedbackSessionName, section);
            
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
        
        boolean isInSection = Boolean.parseBoolean(params.get(PARAM_IN_SECTION));
        boolean isToSection = Boolean.parseBoolean(params.get(PARAM_TO_SECTION));
        boolean isFromSection = Boolean.parseBoolean(params.get(PARAM_FROM_SECTION));
        boolean isComplete = params.get(PARAM_RANGE) == null;
        
        List<FeedbackResponseAttributes> allResponses = new ArrayList<FeedbackResponseAttributes>();
        if (params.get(PARAM_RANGE) == null) {
            if (isInSection) {
                allResponses = frLogic.getFeedbackResponsesForSessionInSection(feedbackSessionName,
                                                                               courseId, section);
            } else if (isFromSection) {
                allResponses = frLogic.getFeedbackResponsesForSessionFromSection(feedbackSessionName,
                                                                                 courseId, section);
            } else if (isToSection) {
                allResponses = frLogic.getFeedbackResponsesForSessionToSection(feedbackSessionName,
                                                                               courseId, section);
            } else {
                Assumption.fail(ASSUMPTION_FAIL_RESPONSE_ORIGIN);
            }
        } else {
            long range = Long.parseLong(params.get(PARAM_RANGE));
            if (isInSection) {
                allResponses = frLogic.getFeedbackResponsesForSessionInSectionWithinRange(feedbackSessionName,
                                                                                          courseId, section, range);
            } else if (isFromSection) {
                allResponses = frLogic.getFeedbackResponsesForSessionFromSectionWithinRange(feedbackSessionName,
                                                                                            courseId, section, range);
            } else if (isToSection) {
                allResponses = frLogic.getFeedbackResponsesForSessionToSectionWithinRange(feedbackSessionName,
                                                                                          courseId, section, range);
            } else {
                Assumption.fail(ASSUMPTION_FAIL_RESPONSE_ORIGIN);
            }
            if (allResponses.size() <= range) {
                isComplete = true;
            } else {
                for (FeedbackQuestionAttributes qn : allQuestions) {
                    relevantQuestions.put(qn.getId(), qn);
                }
                
            }
        }
        
        responseStatus = section == null && isIncludeResponseStatus
                       ? getFeedbackSessionResponseStatus(session, roster, allQuestions)
                       : null;

        StudentAttributes student = null;
        Set<String> studentsEmailInTeam = new HashSet<String>();
        if (role == UserRole.STUDENT) {
            student = studentsLogic.getStudentForEmail(courseId, userEmail);
            List<StudentAttributes> studentsInTeam = studentsLogic
                    .getStudentsForTeam(student.team, courseId);
            for (StudentAttributes teammates : studentsInTeam) {
                studentsEmailInTeam.add(teammates.email);
            }
        }
        
        InstructorAttributes instructor = null;
        if (role == UserRole.INSTRUCTOR) {
            instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
        }
        
        Map<String, FeedbackResponseAttributes> relevantResponse = new HashMap<String, FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response : allResponses) {
            FeedbackQuestionAttributes relatedQuestion = allQuestionsMap
                    .get(response.feedbackQuestionId);
            if (relatedQuestion != null) {
                boolean isVisibleResponse = isResponseVisibleForUser(userEmail,
                        role, student, studentsEmailInTeam, response,
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

        if (params.get(PARAM_VIEW_TYPE) == null
                || Const.FeedbackSessionResults.GRQ_SORT_TYPE.equals(params.get(PARAM_VIEW_TYPE))
                || Const.FeedbackSessionResults.RGQ_SORT_TYPE.equals(params.get(PARAM_VIEW_TYPE))) {
            List<FeedbackResponseCommentAttributes> allResponseComments =
                    frcLogic.getFeedbackResponseCommentForSessionInSection(courseId,
                            feedbackSessionName, section);
            for (FeedbackResponseCommentAttributes frc : allResponseComments) {
                FeedbackResponseAttributes relatedResponse = relevantResponse.get(frc.feedbackResponseId);
                FeedbackQuestionAttributes relatedQuestion = relevantQuestions.get(frc.feedbackQuestionId);
                boolean isVisibleResponseComment = frcLogic.isResponseCommentVisibleForUser(userEmail, courseId,
                        role, section, student, studentsEmailInTeam, relatedResponse,
                        relatedQuestion, frc, instructor);
                if (isVisibleResponseComment) {
                    if (!frcLogic.isNameVisibleTo(frc, relatedResponse, userEmail, roster)) {
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
                sortByCreatedDate(responseCommentList);
            }
        }
        
        addSectionTeamNamesToTable(sectionTeamNameTable, roster, courseId, userEmail, role, feedbackSessionName, section);
        
        FeedbackSessionResultsBundle results =
                new FeedbackSessionResultsBundle(
                        session, responses, relevantQuestions, emailNameTable,
                        emailLastNameTable, emailTeamNameTable, sectionTeamNameTable,
                        visibilityTable, responseStatus, roster, responseComments, isComplete);

        return results;
    }

    private void addSectionTeamNamesToTable(Map<String, Set<String>> sectionTeamNameTable,
                                    CourseRoster roster, String courseId, String userEmail, UserRole role,
                                    String feedbackSessionName, String sectionToView) {
        InstructorAttributes instructor = null;
        if (role == UserRole.INSTRUCTOR) {
            instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
        }
        if (instructor != null) {
            for (StudentAttributes student : roster.getStudents()) {
                boolean isVisibleResponse =
                        instructor.isAllowedForPrivilege(
                                           student.section,
                                           feedbackSessionName,
                                           Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
                boolean isStudentInSelectedSection = student.section.equals(sectionToView);
                boolean isViewingAllSections = sectionToView == null;
                
                if (isVisibleResponse && (isViewingAllSections
                                          || isStudentInSelectedSection)) {
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

    private boolean isResponseVisibleForUser(String userEmail,
            UserRole role, StudentAttributes student,
            Set<String> studentsEmailInTeam,
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes relatedQuestion, InstructorAttributes instructor) {
        
        boolean isVisibleResponse = false;
        if (role == UserRole.INSTRUCTOR && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)
                || response.recipient.equals(userEmail)
                        && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                || response.giver.equals(userEmail)
                || role == UserRole.STUDENT && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
            isVisibleResponse = true;
        } else if (role == UserRole.STUDENT) {
            if (relatedQuestion.recipientType == FeedbackParticipantType.TEAMS
                    && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                    && response.recipient.equals(student.team)) {
                isVisibleResponse = true;
            } else if (relatedQuestion.giverType == FeedbackParticipantType.TEAMS
                       && studentsEmailInTeam.contains(response.giver)) {
                isVisibleResponse = true;
            } else if (relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                       && studentsEmailInTeam.contains(response.giver)) {
                isVisibleResponse = true;
            } else if (relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                       && studentsEmailInTeam.contains(response.recipient)) {
                isVisibleResponse = true;
            }
        }
        if (isVisibleResponse && instructor != null) {
            boolean isGiverSectionRestricted =
                    !instructor.isAllowedForPrivilege(response.giverSection,
                                                      response.feedbackSessionName,
                                                      Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
            // If instructors are not restricted to view the giver's section,
            // they are allowed to view responses to GENERAL, subject to visibility options
            boolean isRecipientSectionRestricted =
                    relatedQuestion.recipientType != FeedbackParticipantType.NONE
                    && !instructor.isAllowedForPrivilege(response.recipientSection,
                                                         response.feedbackSessionName,
                                                         Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
            
            boolean isNotAllowedForInstructor = isGiverSectionRestricted || isRecipientSectionRestricted;
            if (isNotAllowedForInstructor) {
                isVisibleResponse = false;
            }
        }
        return isVisibleResponse;
    }

    private void sortByCreatedDate(List<FeedbackResponseCommentAttributes> responseCommentList) {
        Collections.sort(responseCommentList, new Comparator<FeedbackResponseCommentAttributes>() {
            @Override
            public int compare(FeedbackResponseCommentAttributes frc1,
                               FeedbackResponseCommentAttributes frc2) {
                return frc1.createdAt.compareTo(frc2.createdAt);
            }
        });
    }

    protected void addVisibilityToTable(Map<String, boolean[]> visibilityTable,
            FeedbackQuestionAttributes question,
            FeedbackResponseAttributes response,
            String userEmail,
            UserRole role,
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
            FeedbackQuestionAttributes question, CourseRoster roster) {
        // keys of emailNameTable are participantIdentifiers,
        // which consists of students' email, instructors' email, team names, or %GENERAL%.
        // participants identifiers of anonymous responses are not anonymised in the tables
        addEmailNamePairsToTable(emailNameTable, response, question, roster,
                EMAIL_NAME_PAIR);
    }
    
    private void addEmailLastNamePairsToTable(Map<String, String> emailLastNameTable,
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes question, CourseRoster roster) {
        addEmailNamePairsToTable(emailLastNameTable, response, question, roster,
                EMAIL_LASTNAME_PAIR);
    }

    private void addEmailTeamNamePairsToTable(
            Map<String, String> emailTeamNameTable,
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes question, CourseRoster roster) {
        addEmailNamePairsToTable(emailTeamNameTable, response, question,
                roster, EMAIL_TEAMNAME_PAIR);
    }

    private void addEmailNamePairsToTable(Map<String, String> emailNameTable,
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes question, CourseRoster roster,
            int pairType) {
        if (question.giverType == FeedbackParticipantType.TEAMS
                && roster.isStudentInCourse(response.giver)) {
            if (!emailNameTable.containsKey(response.giver + Const.TEAM_OF_EMAIL_OWNER)) {
                emailNameTable.put(
                        response.giver + Const.TEAM_OF_EMAIL_OWNER,
                        getNameTeamNamePairForEmail(question.giverType,
                                response.giver, roster)[pairType]);
            }
            
            StudentAttributes studentGiver = roster.getStudentForEmail(response.giver);
            if (studentGiver != null && !emailNameTable.containsKey(studentGiver.team)) {
                emailNameTable.put(studentGiver.team, getNameTeamNamePairForEmail(
                                                        question.giverType,
                                                        response.giver, roster)[pairType]);
            }
        } else if (!emailNameTable.containsKey(response.giver)) {
            emailNameTable.put(
                    response.giver,
                    getNameTeamNamePairForEmail(question.giverType,
                            response.giver, roster)[pairType]);
        }

        FeedbackParticipantType recipientType = null;
        if (question.recipientType == FeedbackParticipantType.SELF) {
            recipientType = question.giverType;
        } else {
            recipientType = question.recipientType;
        }
        if (!emailNameTable.containsKey(response.recipient)) {
            emailNameTable.put(
                    response.recipient,
                    getNameTeamNamePairForEmail(recipientType,
                                                response.recipient, roster)[pairType]);
            
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
            if (!fsa.isPrivateSession() || fsa.isCreator(instructorEmail)) {
                fsDetailsWithoutPrivate.add(getFeedbackSessionDetails(fsa));
            }
        }

        return fsDetailsWithoutPrivate;
    }

    private List<FeedbackSessionAttributes> getFeedbackSessionsListForCourse(
            String courseId, String instructorEmail) {
        
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
            List<FeedbackQuestionAttributes> questions) {

        FeedbackSessionResponseStatus responseStatus = new FeedbackSessionResponseStatus();
        List<StudentAttributes> students = roster.getStudents();
        List<InstructorAttributes> instructors = roster.getInstructors();
        List<FeedbackQuestionAttributes> studentQns = fqLogic
                .getFeedbackQuestionsForStudents(questions);

        List<String> studentNoResponses = new ArrayList<String>();
        List<String> instructorNoResponses = new ArrayList<String>();

        if (!studentQns.isEmpty()) {
            for (StudentAttributes student : students) {
                studentNoResponses.add(student.email);
                responseStatus.emailNameTable.put(student.email, student.name);
                responseStatus.emailSectionTable.put(student.email, student.section);
                responseStatus.emailTeamNameTable.put(student.email, student.team);
            }
        }
        studentNoResponses.removeAll(fsa.getRespondingStudentList());

        for (InstructorAttributes instructor : instructors) {
            List<FeedbackQuestionAttributes> instructorQns = fqLogic
                    .getFeedbackQuestionsForInstructor(questions,
                            fsa.isCreator(instructor.email));
            if (!instructorQns.isEmpty() && responseStatus.emailNameTable.get(instructor.email) == null) {
                instructorNoResponses.add(instructor.email);
                responseStatus.emailNameTable.put(instructor.email, instructor.name);
            }
        }
        instructorNoResponses.removeAll(fsa.getRespondingInstructorList());

        responseStatus.noResponse.addAll(studentNoResponses);
        responseStatus.noResponse.addAll(instructorNoResponses);
        
        return responseStatus;
    }

    // return a pair of String that contains Giver/Recipient'sName (at index 0)
    // and TeamName (at index 1)
    private String[] getNameTeamNamePairForEmail(FeedbackParticipantType type,
            String email, CourseRoster roster) {
        String giverRecipientName = null;
        String giverRecipientLastName = null;
        String teamName = null;
        String name = null;
        String lastName = null;
        String team = null;

        StudentAttributes student = roster.getStudentForEmail(email);
        boolean isStudent = student != null;
        if (isStudent) {
            name = student.name;
            team = student.team;
            lastName = student.lastName;
        } else {
            InstructorAttributes instructor = roster
                    .getInstructorForEmail(email);
            boolean isInstructor = instructor != null;
            if (isInstructor) {
                name = instructor.name;
                lastName = instructor.name;
                team = Const.USER_TEAM_FOR_INSTRUCTOR;
            } else {
                if (email.equals(Const.GENERAL_QUESTION)) {
                    // Email represents that there is no specific recipient.
                    name = Const.USER_IS_NOBODY;
                    lastName = Const.USER_IS_NOBODY;
                    team = email;
                } else {
                    // The email represents a missing *Attribute.
                    // It might be a team name or the *Attribute has been deleted.
                    name = Const.USER_IS_MISSING;
                    lastName = Const.USER_IS_MISSING;
                    team = email;
                }
            }
        }

        if (type == FeedbackParticipantType.TEAMS || type == FeedbackParticipantType.OWN_TEAM) {
            giverRecipientName = team;
            giverRecipientLastName = team;
            teamName = "";
        } else {
            giverRecipientName = name;
            giverRecipientLastName = lastName;
            if (name.equals(Const.USER_IS_NOBODY) || name.equals(Const.USER_IS_MISSING)) {
                teamName = "";
            } else {
                teamName = team;
            }
        }
        return new String[] { giverRecipientName, giverRecipientLastName, teamName };
    }

    public boolean isFeedbackSessionFullyCompletedByStudent(
            String feedbackSessionName,
            String courseId, String userEmail)
            throws EntityDoesNotExistException {

        if (!isFeedbackSessionExists(feedbackSessionName, courseId)) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_CHECK + courseId + "/" + feedbackSessionName);
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

        if (!isFeedbackSessionExists(feedbackSessionName, courseId)) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_CHECK + courseId + "/" + feedbackSessionName);
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
        if (session.getFeedbackSessionType() == FeedbackSessionType.PRIVATE) {
            return session.getCreatorEmail().equals(userEmail);
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
                        session.getFeedbackSessionName(), session.getCourseId());
        
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
     */
    public boolean isFeedbackSessionForStudentsToAnswer(FeedbackSessionAttributes session) {
        
        List<FeedbackQuestionAttributes> questionsToAnswer =
                fqLogic.getFeedbackQuestionsForStudents(
                        session.getFeedbackSessionName(), session.getCourseId());
        
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

        // reset sentOpenEmail if the session has opened but is being un-opened
        // now, or else leave it as sent if so.
        if (oldSession.isSentOpenEmail()) {
            newSession.setSentOpenEmail(newSession.isOpened());
        }

        // reset sentClosedEmail if the session has closed but is being un-closed
        // now, or else leave it as sent if so.
        if (oldSession.isSentClosedEmail()) {
            newSession.setSentClosedEmail(newSession.isClosed());
            
            // also reset sentClosingEmail
            newSession.setSentClosingEmail(
                    newSession.isClosed()
                    || !newSession.isClosedAfter(SystemParams.NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT));
        }

        // reset sentPublishedEmail if the session has been published but is
        // going to be unpublished now, or else leave it as sent if so.
        if (oldSession.isSentPublishedEmail()) {
            newSession.setSentPublishedEmail(newSession.isPublished());
        }
    }

}
