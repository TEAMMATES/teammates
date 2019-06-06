package teammates.logic.core;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.SectionDetail;
import teammates.common.datatransfer.UserRole;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Const.SystemParams;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.api.FeedbackSessionsDb;

/**
 * Handles operations related to feedback sessions.
 *
 * @see FeedbackSessionAttributes
 * @see FeedbackSessionsDb
 */
public final class FeedbackSessionsLogic {

    private static final Logger log = Logger.getLogger();

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

    private static FeedbackSessionsLogic instance = new FeedbackSessionsLogic();

    private static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    private FeedbackSessionsLogic() {
        // prevent initialization
    }

    public static FeedbackSessionsLogic inst() {
        return instance;
    }

    /**
     * Creates a feedback session.
     *
     * @return created feedback session
     * @throws InvalidParametersException if the session is not valid
     * @throws EntityAlreadyExistsException if the session already exist
     */
    public FeedbackSessionAttributes createFeedbackSession(FeedbackSessionAttributes fsa)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return fsDb.createEntity(fsa);
    }

    public List<FeedbackSessionAttributes> getAllOngoingSessions(Instant rangeStart, Instant rangeEnd) {
        return fsDb.getAllOngoingSessions(rangeStart, rangeEnd);
    }

    /**
     * Gets a feedback session from the data storage.
     *
     * @return null if not found or in recycle bin.
     */
    public FeedbackSessionAttributes getFeedbackSession(String feedbackSessionName, String courseId) {
        return fsDb.getFeedbackSession(courseId, feedbackSessionName);
    }

    /**
     * Gets a feedback session from the recycle bin.
     *
     * @return null if not found.
     */
    public FeedbackSessionAttributes getFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId) {
        return fsDb.getSoftDeletedFeedbackSession(courseId, feedbackSessionName);
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(
            String courseId) {
        return fsDb.getFeedbackSessionsForCourse(courseId);
    }

    /**
     * Checks if the specified course exists, then gets the feedback sessions for
     * the specified user in the course if it does exist.
     *
     * @return a list of viewable feedback sessions for any user for his course.
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
     * @return a list of viewable feedback sessions for any user for his course.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForUserInCourseSkipCheck(
            String courseId, String userEmail) {
        List<FeedbackSessionAttributes> sessions =
                getFeedbackSessionsForCourse(courseId);
        List<FeedbackSessionAttributes> viewableSessions = new ArrayList<>();
        if (!sessions.isEmpty()) {
            InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
            boolean isInstructorOfCourse = instructor != null;
            for (FeedbackSessionAttributes session : sessions) {
                if (isFeedbackSessionViewableTo(session, isInstructorOfCourse)) {
                    viewableSessions.add(session);
                }
            }
        }

        return viewableSessions;
    }

    /**
     * Returns a list of feedback sessions within the time range or an empty list if nothing was found.
     */
    public List<FeedbackSessionAttributes> getAllFeedbackSessionsWithinTimeRange(Instant rangeStart, Instant rangeEnd) {
        return fsDb.getFeedbackSessionsWithinTimeRange(rangeStart, rangeEnd);
    }

    /**
     * Returns true if there is some open or published email sent for the course.
     *
     * @param courseId - ID of the course
     */
    public boolean isOpenOrPublishedEmailSentForTheCourse(String courseId) {
        List<FeedbackSessionAttributes> sessions = getFeedbackSessionsForCourse(courseId);

        for (FeedbackSessionAttributes session : sessions) {
            if (session.isSentOpenEmail() || session.isSentPublishedEmail()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a {@code List} of all feedback sessions bundled with their
     * response statistics for a instructor given by his googleId.<br>
     * Does not return private sessions unless the instructor is the creator.
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
     */
    public List<FeedbackSessionDetailsBundle> getFeedbackSessionDetailsForInstructor(
            String googleId, boolean omitArchived)
            throws EntityDoesNotExistException {

        List<FeedbackSessionDetailsBundle> fsDetails = new ArrayList<>();
        List<InstructorAttributes> instructors =
                instructorsLogic.getInstructorsForGoogleId(googleId, omitArchived);

        for (InstructorAttributes instructor : instructors) {
            fsDetails.addAll(getFeedbackSessionDetailsForCourse(instructor.courseId));
        }

        return fsDetails;
    }

    /**
     * Returns a {@code List} of all feedback sessions WITHOUT their response
     * statistics for a instructor given by his googleId.<br>
     * Does not return private sessions unless the instructor is the creator.
     * <br>
     * Omits sessions from archived courses if omitArchived == true
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(String googleId, boolean omitArchived) {

        List<InstructorAttributes> instructorList =
                instructorsLogic.getInstructorsForGoogleId(googleId, omitArchived);

        return getFeedbackSessionsListForInstructor(instructorList);
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(
            List<InstructorAttributes> instructorList) {

        List<InstructorAttributes> courseNotDeletedInstructorList = instructorList.stream()
                .filter(instructor -> !coursesLogic.getCourse(instructor.courseId).isCourseDeleted())
                .collect(Collectors.toList());

        List<FeedbackSessionAttributes> fsList = new ArrayList<>();

        for (InstructorAttributes instructor : courseNotDeletedInstructorList) {
            fsList.addAll(getFeedbackSessionsListForCourse(instructor.courseId));
        }

        return fsList;
    }

    /**
     * Returns a {@code List} of feedback sessions in the Recycle Bin for a specific instructor.
     * <br>
     * Omits sessions if the corresponding course is archived or in Recycle Bin
     */
    public List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsListForInstructor(InstructorAttributes instructor) {

        List<FeedbackSessionAttributes> fsList = new ArrayList<>();

        if (coursesLogic.getCourse(instructor.courseId).isCourseDeleted()) {
            return fsList;
        }

        fsList.addAll(getSoftDeletedFeedbackSessionsListForCourse(instructor.courseId));
        return fsList;
    }

    /**
     * Returns a {@code List} of feedback sessions in the Recycle Bin for the instructors.
     * <br>
     * Omits sessions if the corresponding courses are archived or in Recycle Bin
     */
    public List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsListForInstructors(
            List<InstructorAttributes> instructorList) {

        List<InstructorAttributes> courseNotDeletedInstructorList = instructorList.stream()
                .filter(instructor -> !coursesLogic.getCourse(instructor.courseId).isCourseDeleted())
                .collect(Collectors.toList());

        List<FeedbackSessionAttributes> fsList = new ArrayList<>();

        for (InstructorAttributes instructor : courseNotDeletedInstructorList) {
            fsList.addAll(getSoftDeletedFeedbackSessionsListForCourse(instructor.courseId));
        }

        return fsList;
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionListForInstructor(
            InstructorAttributes instructor) {
        if (coursesLogic.getCourse(instructor.courseId).isCourseDeleted()) {
            return null;
        }
        return getFeedbackSessionsListForCourse(instructor.courseId);
    }

    /**
     * Gets {@code FeedbackQuestions} and previously filled
     * {@code FeedbackResponses} that an instructor can view/submit as a
     * {@link FeedbackSessionQuestionsBundle}.
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
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle = new HashMap<>();
        Map<String, Map<String, String>> recipientList = new HashMap<>();

        List<FeedbackQuestionAttributes> questions =
                fqLogic.getFeedbackQuestionsForInstructor(feedbackSessionName,
                        courseId, userEmail);

        Map<String, List<FeedbackResponseCommentAttributes>> commentsForResponses = new HashMap<>();
        CourseRoster roster = new CourseRoster(studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));

        for (FeedbackQuestionAttributes question : questions) {

            updateBundleAndRecipientListWithResponsesForInstructor(courseId,
                    userEmail, fsa, instructor, bundle, recipientList,
                    question, instructor, null);
            updateBundleWithCommentsForResponses(bundle.get(question), commentsForResponses);
        }

        return new FeedbackSessionQuestionsBundle(fsa, bundle, recipientList, commentsForResponses, roster);
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
            recipients.entrySet().removeIf(studentEntry -> {
                StudentAttributes student = studentsLogic.getStudentForEmail(courseId, studentEntry.getKey());
                return !instructor.isAllowedForPrivilege(student.section,
                        fsa.getFeedbackSessionName(), Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            });
        }
        // instructor can only see teams in allowed sections for him/her
        if (question.recipientType.equals(FeedbackParticipantType.TEAMS)) {
            recipients.entrySet().removeIf(teamEntry -> {
                String teamSection = studentsLogic.getSectionForTeam(courseId, teamEntry.getKey());
                return !instructor.isAllowedForPrivilege(teamSection,
                        fsa.getFeedbackSessionName(), Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            });
        }
        normalizeMaximumResponseEntities(question, recipients);

        bundle.put(question, responses);
        recipientList.put(question.getId(), recipients);
    }

    /**
     * Gets {@code FeedbackQuestions} and previously filled
     * {@code FeedbackResponses} that a student can view/submit as a
     * {@link FeedbackSessionQuestionsBundle}.
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

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle = new HashMap<>();
        Map<String, Map<String, String>> recipientList = new HashMap<>();

        List<FeedbackQuestionAttributes> questions = fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName,
                courseId);

        Set<String> hiddenInstructorEmails = null;
        Map<String, List<FeedbackResponseCommentAttributes>> commentsForResponses =
                new HashMap<>();
        CourseRoster roster = new CourseRoster(studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));

        for (FeedbackQuestionAttributes question : questions) {
            if (question.getRecipientType() == FeedbackParticipantType.INSTRUCTORS) {
                hiddenInstructorEmails = getHiddenInstructorEmails(courseId);
                break;
            }
        }

        for (FeedbackQuestionAttributes question : questions) {

            updateBundleAndRecipientListWithResponsesForStudent(userEmail, student,
                    bundle, recipientList, question, hiddenInstructorEmails);
            updateBundleWithCommentsForResponses(bundle.get(question), commentsForResponses);

        }

        return new FeedbackSessionQuestionsBundle(fsa, bundle, recipientList, commentsForResponses, roster);
    }

    private void updateBundleWithCommentsForResponses(List<FeedbackResponseAttributes> responses,
                                                 Map<String, List<FeedbackResponseCommentAttributes>> commentsForResponses) {
        for (FeedbackResponseAttributes response : responses) {
            List<FeedbackResponseCommentAttributes> comments =
                    frcLogic.getFeedbackResponseCommentForResponse(response.getId());
            commentsForResponses.put(response.getId(), comments);
        }
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
            responses.removeIf(response -> response.recipient.equals(instructorEmail));
        }
    }

    /**
     * Returns a {@link Set} of emails of the instructors who are not displayed
     * to students in the course specified by {@code courseId}.
     *
     * @param courseId
     *            the ID of the course
     */
    private Set<String> getHiddenInstructorEmails(String courseId) {
        List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(courseId);
        Set<String> hiddenInstructorEmails = new HashSet<>();

        for (InstructorAttributes instructor : instructors) {
            if (!instructor.isDisplayedToStudents()) {
                hiddenInstructorEmails.add(instructor.email);
            }
        }

        return hiddenInstructorEmails;
    }

    /**
     * Gets the response rate status for a session.
     */
    public FeedbackSessionResponseStatus getFeedbackSessionResponseStatus(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        FeedbackSessionAttributes session = fsDb.getFeedbackSession(
                courseId, feedbackSessionName);

        if (session == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_VIEW + courseId + "/" + feedbackSessionName);
        }

        List<FeedbackQuestionAttributes> allQuestions = fqLogic.getFeedbackQuestionsForSession(feedbackSessionName,
                        courseId);

        CourseRoster roster = new CourseRoster(studentsLogic.getStudentsForCourse(courseId),
                                               instructorsLogic.getInstructorsForCourse(courseId));
        return getFeedbackSessionResponseStatus(session, roster, allQuestions);
    }

    private FeedbackSessionResponseStatus getFeedbackSessionResponseStatus(
            FeedbackSessionAttributes fsa, CourseRoster roster,
            List<FeedbackQuestionAttributes> questions) {

        FeedbackSessionResponseStatus responseStatus = new FeedbackSessionResponseStatus();
        List<StudentAttributes> students = roster.getStudents();
        List<InstructorAttributes> instructors = roster.getInstructors();
        List<FeedbackQuestionAttributes> studentQns = fqLogic
                .getFeedbackQuestionsForStudents(questions);

        List<String> studentNoResponses = new ArrayList<>();
        List<String> studentResponded = new ArrayList<>();
        List<String> instructorNoResponses = new ArrayList<>();

        if (!studentQns.isEmpty()) {
            for (StudentAttributes student : students) {
                studentNoResponses.add(student.email);
                responseStatus.emailNameTable.put(student.email, student.name);
                responseStatus.emailSectionTable.put(student.email, student.section);
                responseStatus.emailTeamNameTable.put(student.email, student.team);
            }
        }
        studentNoResponses.removeAll(fsa.getRespondingStudentList());
        studentResponded.addAll(fsa.getRespondingStudentList());

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

        responseStatus.studentsWhoDidNotRespond.addAll(studentNoResponses);
        responseStatus.studentsWhoResponded.addAll(studentResponded);
        responseStatus.studentsWhoDidNotRespond.addAll(instructorNoResponses);

        return responseStatus;
    }

    /**
     * Gets results of a feedback session to show to an instructor from an indicated question.
     * This will not retrieve the list of comments for this question.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromQuestion(
            String feedbackSessionName, String courseId, String userEmail, String questionId)
                    throws EntityDoesNotExistException {

        // Load details of students and instructors once and pass it to callee
        // methods
        // (rather than loading them many times).
        CourseRoster roster = new CourseRoster(
                studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_IN_SECTION, "false");
        params.put(PARAM_FROM_SECTION, "false");
        params.put(PARAM_TO_SECTION, "false");
        params.put(PARAM_QUESTION_ID, questionId);

        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail,
                                                          UserRole.INSTRUCTOR, roster, params, SectionDetail.NOT_APPLICABLE);
    }

    /**
     * Gets results of a feedback session to show to an instructor from an indicated question
     * and in a section.
     * This will not retrieve the list of comments for this question.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromQuestionInSection(
            String feedbackSessionName, String courseId, String userEmail, String questionId,
            String selectedSection, SectionDetail selectedSectionDetail)
            throws EntityDoesNotExistException {

        CourseRoster roster = new CourseRoster(
                studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));
        Map<String, String> params = initializeParamsWithSelectedSectionDetail(selectedSectionDetail);

        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_QUESTION_ID, questionId);
        params.put(PARAM_SECTION, selectedSection);

        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail,
                                                          UserRole.INSTRUCTOR, roster, params, selectedSectionDetail);
    }

    /**
     * Gets results of a feedback session to show to an instructor in an indicated range.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorWithinRangeFromView(
            String feedbackSessionName, String courseId, String userEmail, int range, String viewType)
            throws EntityDoesNotExistException {

        return getFeedbackSessionResultsForInstructorInSectionWithinRangeFromView(
                feedbackSessionName, courseId, userEmail, null, SectionDetail.NOT_APPLICABLE,
                range, viewType);
    }

    /**
     * Gets results of a feedback session to show to an instructor in a section in an indicated range.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorInSectionWithinRangeFromView(
            String feedbackSessionName, String courseId, String userEmail, String section, SectionDetail sectionDetail,
            int range, String viewType)
            throws EntityDoesNotExistException {

        CourseRoster roster = new CourseRoster(
                studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));

        Map<String, String> params = initializeParamsWithSelectedSectionDetail(sectionDetail);

        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_SECTION, section);

        if (range > 0) {
            params.put(PARAM_RANGE, String.valueOf(range));
        }
        params.put(PARAM_VIEW_TYPE, viewType);

        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail,
                                                          UserRole.INSTRUCTOR, roster, params, sectionDetail);
    }

    /**
     * Gets results of a feedback session to show to an instructor in a section in an indicated range.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromSectionWithinRange(
            String feedbackSessionName, String courseId, String userEmail, String section, int range)
            throws EntityDoesNotExistException {

        CourseRoster roster = new CourseRoster(
                studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_IN_SECTION, "false");
        params.put(PARAM_FROM_SECTION, "true");
        params.put(PARAM_TO_SECTION, "false");
        params.put(PARAM_SECTION, section);

        if (range > 0) {
            params.put(PARAM_RANGE, String.valueOf(range));
        }
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail,
                                                          UserRole.INSTRUCTOR, roster, params, SectionDetail.NOT_APPLICABLE);
    }

    /**
     * Gets results of a feedback session to show to an instructor in a section in an indicated range.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorToSectionWithinRange(
            String feedbackSessionName, String courseId, String userEmail, String section, int range)
            throws EntityDoesNotExistException {

        CourseRoster roster = new CourseRoster(
                studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_IN_SECTION, "false");
        params.put(PARAM_FROM_SECTION, "false");
        params.put(PARAM_TO_SECTION, "true");
        params.put(PARAM_SECTION, section);

        if (range > 0) {
            params.put(PARAM_RANGE, String.valueOf(range));
        }
        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName, courseId, userEmail,
                                                          UserRole.INSTRUCTOR, roster, params, SectionDetail.NOT_APPLICABLE);
    }

    /**
     * Gets results of a feedback session to show to an instructor.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructor(
            String feedbackSessionName, String courseId, String userEmail)
            throws EntityDoesNotExistException {

        return getFeedbackSessionResultsForInstructorInSection(feedbackSessionName, courseId, userEmail, null,
                SectionDetail.NOT_APPLICABLE);
    }

    /**
     * Gets results of a feedback session to show to an instructor for a specific section.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorInSection(
            String feedbackSessionName, String courseId, String userEmail,
            String section, SectionDetail sectionDetail)
            throws EntityDoesNotExistException {

        CourseRoster roster = new CourseRoster(
                studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));
        Map<String, String> params = initializeParamsWithSelectedSectionDetail(sectionDetail);

        params.put(PARAM_IS_INCLUDE_RESPONSE_STATUS, "true");
        params.put(PARAM_SECTION, section);

        return getFeedbackSessionResultsForUserWithParams(feedbackSessionName,
                courseId, userEmail, UserRole.INSTRUCTOR, roster, params, sectionDetail);
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

    public String getFeedbackSessionResultsSummaryAsCsv(
            String feedbackSessionName, String courseId, String userEmail,
            String questionId, boolean isMissingResponsesShown, boolean isStatsShown)
            throws EntityDoesNotExistException, ExceedingRangeException {

        return getFeedbackSessionResultsSummaryInSectionAsCsv(
                feedbackSessionName, courseId, userEmail, null, SectionDetail.NOT_APPLICABLE,
                questionId, isMissingResponsesShown, isStatsShown);
    }

    public String getFeedbackSessionResultsSummaryInSectionAsCsv(
            String feedbackSessionName, String courseId, String userEmail, String section,
            SectionDetail sectionDetail, String questionId, boolean isMissingResponsesShown, boolean isStatsShown)
            throws EntityDoesNotExistException, ExceedingRangeException {

        FeedbackSessionResultsBundle results;
        int indicatedRange = section == null ? Const.INSTRUCTOR_VIEW_RESPONSE_LIMIT : -1;

        if (questionId == null) {
            results = getFeedbackSessionResultsForInstructorInSectionWithinRangeFromView(
                feedbackSessionName, courseId, userEmail, section, sectionDetail,
                indicatedRange, Const.FeedbackSessionResults.GRQ_SORT_TYPE);
        } else if (section == null) {
            results = getFeedbackSessionResultsForInstructorFromQuestion(
                    feedbackSessionName, courseId, userEmail, questionId);
        } else {
            results = getFeedbackSessionResultsForInstructorFromQuestionInSection(
                    feedbackSessionName, courseId, userEmail, questionId, section, sectionDetail);
        }

        if (!results.isComplete) {
            throw new ExceedingRangeException(ERROR_NUMBER_OF_RESPONSES_EXCEEDS_RANGE);
        }
        // sort responses by giver > recipient > qnNumber
        results.responses.sort(results.compareByGiverRecipientQuestion);

        StringBuilder exportBuilder = new StringBuilder(100);

        exportBuilder.append(String.format("Course,%s",
                             SanitizationHelper.sanitizeForCsv(results.feedbackSession.getCourseId())))
                     .append(System.lineSeparator())
                     .append(String.format("Session Name,%s",
                             SanitizationHelper.sanitizeForCsv(results.feedbackSession.getFeedbackSessionName())))
                     .append(System.lineSeparator());

        if (section != null) {
            exportBuilder.append(String.format("Section Name,%s", SanitizationHelper.sanitizeForCsv(section)))
                         .append(System.lineSeparator());
        }
        if (sectionDetail != SectionDetail.NOT_APPLICABLE) {
            exportBuilder.append(String.format("Section View Detail,%s", SanitizationHelper.sanitizeForCsv(
                    sectionDetail.getSectionDetail()))).append(System.lineSeparator());
        }

        exportBuilder.append(System.lineSeparator()).append(System.lineSeparator());

        Set<Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> entrySet =
                results.getQuestionResponseMap().entrySet();

        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry : entrySet) {
            exportBuilder.append(getFeedbackSessionResultsForQuestionInCsvFormat(
                    results, entry, isMissingResponsesShown, isStatsShown, section));
        }

        return exportBuilder.toString();
    }

    private StringBuilder getFeedbackSessionResultsForQuestionInCsvFormat(
            FeedbackSessionResultsBundle fsrBundle,
            Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry,
            boolean isMissingResponsesShown, boolean isStatsShown, String section) {

        FeedbackQuestionAttributes question = entry.getKey();
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        List<FeedbackResponseAttributes> allResponses = entry.getValue();

        StringBuilder exportBuilder = new StringBuilder();

        exportBuilder.append("Question " + Integer.toString(question.questionNumber) + ","
                + SanitizationHelper.sanitizeForCsv(questionDetails.getQuestionText())
                + System.lineSeparator() + System.lineSeparator());

        String statistics = questionDetails.getQuestionResultStatisticsCsv(allResponses,
                                    question, fsrBundle);
        if (!statistics.isEmpty() && isStatsShown) {
            exportBuilder.append("Summary Statistics,").append(System.lineSeparator());
            exportBuilder.append(statistics).append(System.lineSeparator());
        }

        List<String> possibleGiversWithoutResponses = fsrBundle.getPossibleGiversInSection(question, section);
        List<String> possibleRecipientsForGiver = new ArrayList<>();
        String prevGiver = "";

        int maxNumOfInstructorComments = getMaxNumberOfInstructorComments(allResponses, fsrBundle.getResponseComments());
        exportBuilder.append(questionDetails.getCsvDetailedResponsesHeader(maxNumOfInstructorComments));

        for (FeedbackResponseAttributes response : allResponses) {

            if (!fsrBundle.isRecipientVisible(response) || !fsrBundle.isGiverVisible(response)) {
                possibleGiversWithoutResponses.clear();
                possibleRecipientsForGiver.clear();
            }

            // keep track of possible recipients with no responses
            removeParticipantIdentifierFromList(question.giverType,
                    possibleGiversWithoutResponses, response.giver, fsrBundle);

            boolean isNewGiver = !prevGiver.equals(response.giver);
            // print missing responses from the current giver
            if (isNewGiver && isMissingResponsesShown) {
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

            exportBuilder.append(questionDetails.getCsvDetailedResponsesRow(fsrBundle, response, question));
        }

        // add the rows for the possible givers and recipients who have missing responses
        if (isMissingResponsesShown) {
            exportBuilder.append(
                    getRemainingRowsInCsvFormat(
                            fsrBundle, entry, question, questionDetails,
                            possibleGiversWithoutResponses, possibleRecipientsForGiver, prevGiver));
        }

        exportBuilder.append(System.lineSeparator() + System.lineSeparator());
        return exportBuilder;
    }

    private int getMaxNumberOfInstructorComments(List<FeedbackResponseAttributes> allResponses,
            Map<String, List<FeedbackResponseCommentAttributes>> responseComments) {

        if (allResponses == null || allResponses.isEmpty()) {
            return 0;
        }

        int maxCommentsNum = 0;
        for (FeedbackResponseAttributes response : allResponses) {
            List<FeedbackResponseCommentAttributes> commentAttributes = responseComments.get(response.getId());
            if (commentAttributes != null) {
                commentAttributes = commentAttributes.stream()
                                            .filter(comment -> !comment.isCommentFromFeedbackParticipant)
                                            .collect(Collectors.toList());
                if (maxCommentsNum < commentAttributes.size()) {
                    maxCommentsNum = commentAttributes.size();
                }
            }
        }

        return maxCommentsNum;
    }

    /**
     * Given a participantIdentifier, remove it from participantIdentifierList.
     *
     * <p>Before removal, {@link FeedbackSessionResultsBundle#getFullNameFromRoster} is used to
     * convert the identifier into a canonical form if the participantIdentifierType is TEAMS.
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
     * <p>If for the prevGiver, possibleRecipientsForGiver is not empty,
     * the remaining missing responses for the prevGiver will be generated first.
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
     * of missing responses between the giver and the possible recipients.
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
                exportBuilder.append(SanitizationHelper.sanitizeForCsv(results.getTeamNameFromRoster(giver))
                        + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverName))
                        + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverLastName))
                        + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverEmail))
                        + "," + SanitizationHelper.sanitizeForCsv(results.getTeamNameFromRoster(possibleRecipient))
                        + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(possibleRecipientName))
                        + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(possibleRecipientLastName))
                        + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(possibleRecipientEmail))
                        + "," + questionDetails.getNoResponseTextInCsv(giver, possibleRecipient, results, question)
                        + System.lineSeparator());
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
        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsPossiblyNeedingPublishedEmail();
        List<FeedbackSessionAttributes> sessionsToSendEmailsFor = new ArrayList<>();

        for (FeedbackSessionAttributes session : sessions) {
            // automated emails are required only for custom publish times
            if (!coursesLogic.getCourse(session.getCourseId()).isCourseDeleted()
                    && session.isPublished()
                    && !TimeHelper.isSpecialTime(session.getResultsVisibleFromTime())) {
                sessionsToSendEmailsFor.add(session);
            }
        }
        return sessionsToSendEmailsFor;
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedOpenEmailsToBeSent() {
        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsPossiblyNeedingOpenEmail();
        List<FeedbackSessionAttributes> sessionsToSendEmailsFor = new ArrayList<>();

        for (FeedbackSessionAttributes session : sessions) {
            if (!coursesLogic.getCourse(session.getCourseId()).isCourseDeleted() && session.isOpened()) {
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

    /**
     * Updates the details of a feedback session by {@link FeedbackSessionAttributes.UpdateOptions}.
     *
     * <p>Adjust email sending status if necessary.
     *
     * @return updated feedback session
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSessionAttributes updateFeedbackSession(FeedbackSessionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSessionAttributes oldSession =
                fsDb.getFeedbackSession(updateOptions.getCourseId(), updateOptions.getFeedbackSessionName());

        if (oldSession == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + updateOptions.getCourseId()
                    + "/" + updateOptions.getFeedbackSessionName());
        }

        FeedbackSessionAttributes newSession = oldSession.getCopy();
        newSession.update(updateOptions);
        FeedbackSessionAttributes.UpdateOptions.Builder newUpdateOptions =
                FeedbackSessionAttributes.updateOptionsBuilder(updateOptions);

        // adjust email sending status

        // reset sentOpenEmail if the session has opened but is being un-opened
        // now, or else leave it as sent if so.
        if (oldSession.isSentOpenEmail()) {
            newUpdateOptions.withSentOpenEmail(newSession.isOpened());
        }

        // reset sentClosedEmail if the session has closed but is being un-closed
        // now, or else leave it as sent if so.
        if (oldSession.isSentClosedEmail()) {
            newUpdateOptions.withSentClosedEmail(newSession.isClosed());

            // also reset sentClosingEmail
            newUpdateOptions.withSentClosingEmail(
                    newSession.isClosed()
                            || newSession.isClosedAfter(SystemParams.NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT));
        }

        // reset sentPublishedEmail if the session has been published but is
        // going to be unpublished now, or else leave it as sent if so.
        if (oldSession.isSentPublishedEmail()) {
            newUpdateOptions.withSentPublishedEmail(newSession.isPublished());
        }

        return fsDb.updateFeedbackSession(newUpdateOptions.build());
    }

    /**
     * Updates all feedback sessions of {@code courseId} to have be in {@code courseTimeZone}.
     */
    public void updateFeedbackSessionsTimeZoneForCourse(String courseId, ZoneId courseTimeZone) {
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseTimeZone);

        List<FeedbackSessionAttributes> fsForCourse = fsDb.getFeedbackSessionsForCourse(courseId);
        fsForCourse.forEach(fs -> {
            try {
                fsDb.updateFeedbackSession(
                        FeedbackSessionAttributes.updateOptionsBuilder(fs.getFeedbackSessionName(), fs.getCourseId())
                                .withTimeZone(courseTimeZone)
                                .build());
            } catch (EntityDoesNotExistException | InvalidParametersException e) {
                log.severe("Cannot adjust timezone of courses: " + e.getMessage());
            }
        });
    }

    /**
     * Updates the instructor with {@code oldEmail} to {@code newEmail} in the instructor respondent list
     * in all feedback session of course {@code courseId}.
     */
    public void updateRespondentsForInstructor(String oldEmail, String newEmail, String courseId)
            throws InvalidParametersException, EntityDoesNotExistException {

        List<FeedbackSessionAttributes> feedbackSessions = getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes session : feedbackSessions) {
            fsDb.updateFeedbackSession(
                    FeedbackSessionAttributes.updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                            .withUpdatingInstructorRespondent(oldEmail, newEmail)
                            .build());
        }
    }

    /**
     * Updates the student with {@code oldEmail} to {@code newEmail} in the student respondent list
     * in all feedback session of course {@code courseId}.
     */
    public void updateRespondentsForStudent(String oldEmail, String newEmail, String courseId)
            throws InvalidParametersException, EntityDoesNotExistException {

        List<FeedbackSessionAttributes> feedbackSessions = getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes session : feedbackSessions) {
            fsDb.updateFeedbackSession(
                    FeedbackSessionAttributes.updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                            .withUpdatingStudentRespondent(oldEmail, newEmail)
                            .build());
        }
    }

    /**
     * Deletes the instructor's email from the instructor respondents set of all feedback sessions
     * in the corresponding course.
     */
    public void deleteInstructorFromRespondentsList(String courseId, String email) {
        List<FeedbackSessionAttributes> sessionsToUpdate =
                fsDb.getFeedbackSessionsForCourse(courseId);

        for (FeedbackSessionAttributes session : sessionsToUpdate) {
            try {
                deleteInstructorRespondent(email, session.getFeedbackSessionName(), session.getCourseId());
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                Assumption.fail(ASSUMPTION_FAIL_DELETE_INSTRUCTOR + session.getFeedbackSessionName());
            }
        }
    }

    /**
     * Deletes the student's email from the student respondents set of all feedback sessions
     * in the corresponding course.
     */
    public void deleteStudentFromRespondentsList(String courseId, String email) {
        List<FeedbackSessionAttributes> sessionsToUpdate =
                fsDb.getFeedbackSessionsForCourse(courseId);

        for (FeedbackSessionAttributes session : sessionsToUpdate) {
            try {
                deleteStudentFromRespondentList(email, session.getFeedbackSessionName(), session.getCourseId());
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                Assumption.fail(ASSUMPTION_FAIL_DELETE_INSTRUCTOR + session.getFeedbackSessionName());
            }
        }
    }

    /**
     * Adds an instructor in the instructor respondent set of a feedback session.
     */
    public void addInstructorRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, email);

        fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                        .withAddingInstructorRespondent(email)
                        .build());
    }

    /**
     * Adds a student in the instructor respondent set of a feedback session.
     */
    public void addStudentRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, email);

        fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                        .withAddingStudentRespondent(email)
                        .build());
    }

    /**
     * Deletes an instructor in the instructor respondent set of a feedback session.
     */
    public void deleteInstructorRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, email);

        fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                        .withRemovingInstructorRespondent(email)
                        .build());
    }

    /**
     * Deletes a student in the instructor respondent set of a feedback session.
     */
    public void deleteStudentFromRespondentList(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, email);

        fsDb.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                        .withRemovingStudentRespondent(email)
                        .build());
    }

    /**
     * Publishes a feedback session.
     *
     * @return the published feedback session
     * @throws InvalidParametersException if session is already published
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSessionAttributes publishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        FeedbackSessionAttributes sessionToPublish = getFeedbackSession(feedbackSessionName, courseId);

        if (sessionToPublish == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }
        if (sessionToPublish.isPublished()) {
            throw new InvalidParametersException(ERROR_FS_ALREADY_PUBLISH);
        }

        return updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(sessionToPublish.getFeedbackSessionName(), sessionToPublish.getCourseId())
                        .withResultsVisibleFromTime(Instant.now())
                        .build());
    }

    /**
     * Unpublishes a feedback session.
     *
     * @return the unpublished feedback session
     * @throws InvalidParametersException if session is already unpublished
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSessionAttributes unpublishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        FeedbackSessionAttributes sessionToUnpublish = getFeedbackSession(feedbackSessionName, courseId);

        if (sessionToUnpublish == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_UPDATE + courseId + "/" + feedbackSessionName);
        }
        if (!sessionToUnpublish.isPublished()) {
            throw new InvalidParametersException(ERROR_FS_ALREADY_UNPUBLISH);
        }

        return updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(sessionToUnpublish.getFeedbackSessionName(), sessionToUnpublish.getCourseId())
                        .withResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER)
                        .build());
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsClosingWithinTimeLimit() {
        List<FeedbackSessionAttributes> requiredSessions = new ArrayList<>();

        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsPossiblyNeedingClosingEmail();

        for (FeedbackSessionAttributes session : sessions) {
            if (!coursesLogic.getCourse(session.getCourseId()).isCourseDeleted()
                    && session.isClosingWithinTimeLimit(SystemParams.NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT)) {
                requiredSessions.add(session);
            }
        }

        return requiredSessions;
    }

    /**
     * Returns returns a list of sessions that were closed within past hour.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsClosedWithinThePastHour() {
        List<FeedbackSessionAttributes> requiredSessions = new ArrayList<>();
        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsPossiblyNeedingClosedEmail();

        for (FeedbackSessionAttributes session : sessions) {
            // is session closed in the past 1 hour
            if (!coursesLogic.getCourse(session.getCourseId()).isCourseDeleted() && session.isClosedWithinPastHour()) {
                requiredSessions.add(session);
            }
        }
        return requiredSessions;
    }

    /**
     * Deletes a feedback session cascade to its associated questions, responses and comments.
     */
    public void deleteFeedbackSessionCascade(String feedbackSessionName, String courseId) {
        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .withFeedbackSessionName(feedbackSessionName)
                .build();
        frcLogic.deleteFeedbackResponseComments(query);
        frLogic.deleteFeedbackResponses(query);
        fqLogic.deleteFeedbackQuestions(query);

        fsDb.deleteFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Deletes sessions using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackSessions(AttributesDeletionQuery query) {
        fsDb.deleteFeedbackSessions(query);
    }

    /**
     * Soft-deletes a specific feedback session to Recycle Bin.
     * @return the time when the feedback session is moved to the recycle bin
     */
    public Instant moveFeedbackSessionToRecycleBin(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        return fsDb.softDeleteFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Restores a specific feedback session from Recycle Bin.
     */
    public void restoreFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
        fsDb.restoreDeletedFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Restores all feedback sessions from Recycle Bin.
     */
    public void restoreAllFeedbackSessionsFromRecycleBin(List<InstructorAttributes> instructorList)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull("Supplied parameter was null", instructorList);

        List<FeedbackSessionAttributes> feedbackSessionsList =
                getSoftDeletedFeedbackSessionsListForInstructors(instructorList);
        for (FeedbackSessionAttributes session : feedbackSessionsList) {
            restoreFeedbackSessionFromRecycleBin(session.getFeedbackSessionName(), session.getCourseId());
        }
    }

    public FeedbackSessionDetailsBundle getFeedbackSessionDetails(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
        FeedbackSessionAttributes fsa = getFeedbackSession(feedbackSessionName, courseId);
        if (fsa == null) {
            throw new EntityDoesNotExistException("Feedback session does not exist");
        }
        return getFeedbackSessionDetails(fsa);
    }

    public FeedbackSessionDetailsBundle getFeedbackSessionDetails(FeedbackSessionAttributes fsa) {

        FeedbackSessionDetailsBundle details =
                new FeedbackSessionDetailsBundle(fsa);

        details.stats.expectedTotal = 0;
        details.stats.submittedTotal = 0;

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
                studentsLogic.getStudentsForCourse(courseId),
                instructorsLogic.getInstructorsForCourse(courseId));

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
        List<FeedbackResponseAttributes> responses = new ArrayList<>();
        Map<String, FeedbackQuestionAttributes> relevantQuestions = new HashMap<>();
        Map<String, String> emailNameTable = new HashMap<>();
        Map<String, String> emailLastNameTable = new HashMap<>();
        Map<String, String> emailTeamNameTable = new HashMap<>();
        Map<String, Set<String>> sectionTeamNameTable = new HashMap<>();
        Map<String, boolean[]> visibilityTable = new HashMap<>();
        Map<String, List<FeedbackResponseCommentAttributes>> responseComments = new HashMap<>();

        FeedbackSessionResponseStatus responseStatus = new FeedbackSessionResponseStatus();

        List<FeedbackQuestionAttributes> allQuestions = fqLogic.getFeedbackQuestionsForSession(
                                                                    feedbackSessionName, courseId);
        Map<String, FeedbackResponseAttributes> relevantResponse = new HashMap<>();
        for (FeedbackQuestionAttributes question : allQuestions) {

            List<FeedbackResponseAttributes> responsesForThisQn;

            responsesForThisQn = frLogic.getViewableFeedbackResponsesForQuestionInSection(
                    question, userEmail, role, section, null);

            boolean hasResponses = !responsesForThisQn.isEmpty();
            if (hasResponses) {
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
        Set<String> studentsEmailInTeam = new HashSet<>();
        if (isStudent(role)) {
            student = studentsLogic.getStudentForEmail(courseId, userEmail);
            List<StudentAttributes> studentsInTeam = studentsLogic
                    .getStudentsForTeam(student.team, courseId);
            for (StudentAttributes teammates : studentsInTeam) {
                studentsEmailInTeam.add(teammates.email);
            }
        }

        List<FeedbackResponseCommentAttributes> allResponseComments =
                frcLogic.getFeedbackResponseCommentForSession(courseId,
                        feedbackSessionName);
        for (FeedbackResponseCommentAttributes frc : allResponseComments) {
            FeedbackResponseAttributes relatedResponse = relevantResponse.get(frc.feedbackResponseId);
            FeedbackQuestionAttributes relatedQuestion = relevantQuestions.get(frc.feedbackQuestionId);
            boolean isVisibleResponseComment = frcLogic.isResponseCommentVisibleForUser(userEmail,
                    role, student, studentsEmailInTeam, relatedResponse, relatedQuestion, frc);
            if (isVisibleResponseComment) {
                if (!frcLogic.isNameVisibleToUser(frc, relatedResponse, userEmail, roster)) {
                    frc.commentGiver = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
                }

                responseComments.computeIfAbsent(frc.feedbackResponseId, string -> new ArrayList<>())
                        .add(frc);
            }
        }

        for (List<FeedbackResponseCommentAttributes> responseCommentList : responseComments
                .values()) {
            sortByCreatedDate(responseCommentList);
        }

        addSectionTeamNamesToTable(sectionTeamNameTable, roster, courseId, userEmail, role, feedbackSessionName, section);

        return new FeedbackSessionResultsBundle(
                        session, responses, relevantQuestions, emailNameTable,
                        emailLastNameTable, emailTeamNameTable, sectionTeamNameTable,
                        visibilityTable, responseStatus, roster, responseComments);
    }

    private Map<String, String> initializeParamsWithSelectedSectionDetail(SectionDetail sectionDetail) {

        Map<String, String> params = new HashMap<>();

        switch(sectionDetail) {
        // default for 'all' section selected will be EITHER case
        case NOT_APPLICABLE:
        case EITHER:
            // either giver or recipient needs to be in the selected section
            params.put(PARAM_IN_SECTION, "true");
            params.put(PARAM_FROM_SECTION, "false");
            params.put(PARAM_TO_SECTION, "false");
            break;
        case GIVER:
            params.put(PARAM_IN_SECTION, "false");
            params.put(PARAM_FROM_SECTION, "true");
            params.put(PARAM_TO_SECTION, "false");
            break;
        case EVALUEE:
            params.put(PARAM_IN_SECTION, "false");
            params.put(PARAM_FROM_SECTION, "false");
            params.put(PARAM_TO_SECTION, "true");
            break;
        case BOTH:
            // both giver and recipient must be from the selected section
            params.put(PARAM_IN_SECTION, "false");
            params.put(PARAM_FROM_SECTION, "true");
            params.put(PARAM_TO_SECTION, "true");
            break;
        default:
            Assumption.fail("Invalid section detail entered.");
            break;
        }

        return params;
    }

    private FeedbackSessionResultsBundle getFeedbackSessionResultsForUserWithParams(
            String feedbackSessionName, String courseId, String userEmail,
            UserRole role, CourseRoster roster, Map<String, String> params, SectionDetail sectionDetail)
            throws EntityDoesNotExistException {

        FeedbackSessionAttributes session = fsDb.getFeedbackSession(courseId, feedbackSessionName);

        if (session == null) {
            throw new EntityDoesNotExistException(ERROR_NON_EXISTENT_FS_VIEW + courseId + "/" + feedbackSessionName);
        }

        List<FeedbackQuestionAttributes> allQuestions =
                fqLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);

        //Show all questions even if no responses, unless is an ajax request for a specific question.
        Map<String, FeedbackQuestionAttributes> relevantQuestions = getAllQuestions(role, params, allQuestions);

        boolean isIncludeResponseStatus = Boolean.parseBoolean(params.get(PARAM_IS_INCLUDE_RESPONSE_STATUS));

        String section = params.get(PARAM_SECTION);
        String questionId = params.get(PARAM_QUESTION_ID);

        if (questionId != null) {
            return getFeedbackSessionResultsForQuestionId(feedbackSessionName, courseId, userEmail, role, roster, session,
                    allQuestions, relevantQuestions, isIncludeResponseStatus, section, sectionDetail, questionId);
        }

        Map<String, FeedbackQuestionAttributes> allQuestionsMap = new HashMap<>();
        putQuestionsIntoMap(allQuestions, allQuestionsMap);

        List<FeedbackResponseAttributes> allResponses = getAllResponses(feedbackSessionName, courseId, params, section);

        String rangeString = params.get(PARAM_RANGE);
        boolean isComplete = rangeString == null || allResponses.size() <= Integer.parseInt(rangeString);

        if (!isComplete) {
            putQuestionsIntoMap(allQuestions, relevantQuestions);
        }

        // create empty data containers to store results
        List<FeedbackResponseAttributes> responses = new ArrayList<>();
        Map<String, String> emailNameTable = new HashMap<>();
        Map<String, String> emailLastNameTable = new HashMap<>();
        Map<String, String> emailTeamNameTable = new HashMap<>();
        Map<String, Set<String>> sectionTeamNameTable = new HashMap<>();
        Map<String, boolean[]> visibilityTable = new HashMap<>();
        FeedbackSessionResponseStatus responseStatus = section == null && isIncludeResponseStatus
                                                     ? getFeedbackSessionResponseStatus(session, roster, allQuestions)
                                                     : null;

        StudentAttributes student = getStudent(courseId, userEmail, role);
        Set<String> studentsEmailInTeam = getTeammateEmails(courseId, student);

        InstructorAttributes instructor = getInstructor(courseId, userEmail, role);

        Map<String, FeedbackResponseAttributes> relevantResponse = new HashMap<>();
        for (FeedbackResponseAttributes response : allResponses) {
            FeedbackQuestionAttributes relatedQuestion = allQuestionsMap.get(response.feedbackQuestionId);
            if (relatedQuestion != null) {
                boolean isVisibleResponse = isResponseVisibleForUser(
                        userEmail, role, student, studentsEmailInTeam, response, relatedQuestion, instructor);
                if (isVisibleResponse) {
                    responses.add(response);
                    relevantResponse.put(response.getId(), response);
                    relevantQuestions.put(relatedQuestion.getId(), relatedQuestion);
                    addEmailNamePairsToTable(emailNameTable, response, relatedQuestion, roster);
                    addEmailLastNamePairsToTable(emailLastNameTable, response, relatedQuestion, roster);
                    addEmailTeamNamePairsToTable(emailTeamNameTable, response, relatedQuestion, roster);
                    addVisibilityToTable(visibilityTable, relatedQuestion, response, userEmail, role, roster);
                }
            }
        }
        Map<String, List<FeedbackResponseCommentAttributes>> responseComments = getResponseComments(
                feedbackSessionName, courseId, userEmail, role, roster, relevantQuestions, section, student,
                studentsEmailInTeam, relevantResponse);

        addSectionTeamNamesToTable(sectionTeamNameTable, roster, courseId, userEmail, role, feedbackSessionName, section);

        return new FeedbackSessionResultsBundle(
                session, responses, relevantQuestions, emailNameTable,
                emailLastNameTable, emailTeamNameTable, sectionTeamNameTable,
                visibilityTable, responseStatus, roster, responseComments, isComplete);
    }

    private Map<String, List<FeedbackResponseCommentAttributes>> getResponseComments(
            String feedbackSessionName, String courseId, String userEmail, UserRole role, CourseRoster roster,
            Map<String, FeedbackQuestionAttributes> relevantQuestions, String section, StudentAttributes student,
            Set<String> studentsEmailInTeam, Map<String, FeedbackResponseAttributes> relevantResponse) {

        Map<String, List<FeedbackResponseCommentAttributes>> responseComments = new HashMap<>();
        List<FeedbackResponseCommentAttributes> allResponseComments =
                frcLogic.getFeedbackResponseCommentForSessionInSection(courseId, feedbackSessionName, section);
        for (FeedbackResponseCommentAttributes frc : allResponseComments) {
            FeedbackResponseAttributes relatedResponse = relevantResponse.get(frc.feedbackResponseId);
            FeedbackQuestionAttributes relatedQuestion = relevantQuestions.get(frc.feedbackQuestionId);
            boolean isVisibleResponseComment = frcLogic.isResponseCommentVisibleForUser(
                    userEmail, role, student, studentsEmailInTeam, relatedResponse, relatedQuestion, frc);
            if (isVisibleResponseComment) {
                if (!frcLogic.isNameVisibleToUser(frc, relatedResponse, userEmail, roster)) {
                    frc.commentGiver = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
                }
                List<FeedbackResponseCommentAttributes> frcList = responseComments.get(frc.feedbackResponseId);
                if (frcList == null) {
                    frcList = new ArrayList<>();
                    frcList.add(frc);
                    responseComments.put(frc.feedbackResponseId, frcList);
                } else {
                    frcList.add(frc);
                }
            }
        }

        for (List<FeedbackResponseCommentAttributes> responseCommentList : responseComments.values()) {
            sortByCreatedDate(responseCommentList);
        }
        return responseComments;
    }

    private void putQuestionsIntoMap(
            List<FeedbackQuestionAttributes> questions, Map<String, FeedbackQuestionAttributes> questionMap) {
        for (FeedbackQuestionAttributes qn : questions) {
            questionMap.put(qn.getId(), qn);
        }
    }

    private InstructorAttributes getInstructor(String courseId, String userEmail, UserRole role) {
        if (isInstructor(role)) {
            return instructorsLogic.getInstructorForEmail(courseId, userEmail);
        }
        return null;
    }

    /*
    * Gets emails of student's teammates if student is not null, else returns an empty Set<String>
    */
    private Set<String> getTeammateEmails(String courseId, StudentAttributes student) {
        Set<String> studentsEmailInTeam = new HashSet<>();
        if (student != null) {
            List<StudentAttributes> studentsInTeam = studentsLogic.getStudentsForTeam(student.team, courseId);
            for (StudentAttributes teammates : studentsInTeam) {
                studentsEmailInTeam.add(teammates.email);
            }
        }
        return studentsEmailInTeam;
    }

    private StudentAttributes getStudent(String courseId, String userEmail, UserRole role) {
        if (isStudent(role)) {
            return studentsLogic.getStudentForEmail(courseId, userEmail);
        }
        return null;
    }

    private FeedbackSessionResultsBundle getFeedbackSessionResultsForQuestionId(String feedbackSessionName,
                String courseId, String userEmail, UserRole role, CourseRoster roster, FeedbackSessionAttributes session,
                List<FeedbackQuestionAttributes> allQuestions, Map<String, FeedbackQuestionAttributes> relevantQuestions,
                boolean isIncludeResponseStatus, String section, SectionDetail sectionDetail, String questionId) {

        List<FeedbackResponseAttributes> responses = new ArrayList<>();
        Map<String, String> emailNameTable = new HashMap<>();
        Map<String, String> emailLastNameTable = new HashMap<>();
        Map<String, String> emailTeamNameTable = new HashMap<>();
        Map<String, Set<String>> sectionTeamNameTable = new HashMap<>();
        Map<String, boolean[]> visibilityTable = new HashMap<>();
        Map<String, List<FeedbackResponseCommentAttributes>> responseComments = new HashMap<>();
        FeedbackSessionResponseStatus responseStatus = new FeedbackSessionResponseStatus();
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

                responsesForThisQn = frLogic.getViewableFeedbackResponsesForQuestionInSection(
                                                question, userEmail, UserRole.INSTRUCTOR, section, sectionDetail);
                StudentAttributes student = getStudent(courseId, userEmail, role);
                Set<String> studentsEmailInTeam = getTeammateEmails(courseId, student);
                boolean hasResponses = !responsesForThisQn.isEmpty();
                if (hasResponses) {
                    Map<String, FeedbackResponseAttributes> relevantResponse = new HashMap<>();
                    for (FeedbackResponseAttributes response : responsesForThisQn) {
                        InstructorAttributes instructor = getInstructor(courseId, userEmail, role);
                        boolean isVisibleResponse = isResponseVisibleForUser(userEmail, role, null, null, response,
                                                                             question, instructor);
                        if (isVisibleResponse) {
                            relevantResponse.put(response.getId(), response);
                            relevantQuestions.put(question.getId(), question);
                            responses.add(response);
                            addEmailNamePairsToTable(emailNameTable, response, question, roster);
                            addEmailLastNamePairsToTable(emailLastNameTable, response, question, roster);
                            addEmailTeamNamePairsToTable(emailTeamNameTable, response, question, roster);
                            addVisibilityToTable(visibilityTable, question, response, userEmail, role, roster);
                        }
                    }
                    responseComments = getResponseComments(
                            feedbackSessionName, courseId, userEmail, role, roster, relevantQuestions, section, student,
                            studentsEmailInTeam, relevantResponse);
                }
            }
        }
        addSectionTeamNamesToTable(
                sectionTeamNameTable, roster, courseId, userEmail, role, feedbackSessionName, section);

        return new FeedbackSessionResultsBundle(
                session, responses, relevantQuestions, emailNameTable,
                emailLastNameTable, emailTeamNameTable, sectionTeamNameTable,
                visibilityTable, responseStatus, roster, responseComments, true);
    }

    private Map<String, FeedbackQuestionAttributes> getAllQuestions(
            UserRole role, Map<String, String> params, List<FeedbackQuestionAttributes> allQuestions) {
        Map<String, FeedbackQuestionAttributes> relevantQuestions = new HashMap<>();

        if (isInstructor(role) && !params.containsKey(PARAM_QUESTION_ID)) {
            putQuestionsIntoMap(allQuestions, relevantQuestions);
        }
        return relevantQuestions;
    }

    private boolean isStudent(UserRole role) {
        return role == UserRole.STUDENT;
    }

    private boolean isInstructor(UserRole role) {
        return role == UserRole.INSTRUCTOR;
    }

    private List<FeedbackResponseAttributes> getAllResponses(String feedbackSessionName, String courseId,
            Map<String, String> params, String section) {
        boolean isInSection = Boolean.parseBoolean(params.get(PARAM_IN_SECTION));
        boolean isToSection = Boolean.parseBoolean(params.get(PARAM_TO_SECTION));
        boolean isFromSection = Boolean.parseBoolean(params.get(PARAM_FROM_SECTION));

        if (params.get(PARAM_RANGE) == null) {
            if (isFromSection && isToSection) {
                return frLogic.getFeedbackResponsesForSessionInGiverAndRecipientSection(feedbackSessionName,
                        courseId, section);
            } else if (isInSection) {
                return frLogic.getFeedbackResponsesForSessionInSection(feedbackSessionName, courseId, section);
            } else if (isFromSection) {
                return frLogic.getFeedbackResponsesForSessionFromSection(feedbackSessionName, courseId, section);
            } else if (isToSection) {
                return frLogic.getFeedbackResponsesForSessionToSection(feedbackSessionName, courseId, section);
            } else {
                Assumption.fail(ASSUMPTION_FAIL_RESPONSE_ORIGIN);
            }
        } else {
            int range = Integer.parseInt(params.get(PARAM_RANGE));
            if (isInSection) {
                return frLogic.getFeedbackResponsesForSessionInSectionWithinRange(
                        feedbackSessionName, courseId, section, range);
            } else if (isFromSection) {
                return frLogic.getFeedbackResponsesForSessionFromSectionWithinRange(
                        feedbackSessionName, courseId, section, range);
            } else if (isToSection) {
                return frLogic.getFeedbackResponsesForSessionToSectionWithinRange(
                        feedbackSessionName, courseId, section, range);
            } else {
                Assumption.fail(ASSUMPTION_FAIL_RESPONSE_ORIGIN);
            }
        }
        return new ArrayList<>();
    }

    private void addSectionTeamNamesToTable(Map<String, Set<String>> sectionTeamNameTable,
                                    CourseRoster roster, String courseId, String userEmail, UserRole role,
                                    String feedbackSessionName, String sectionToView) {
        InstructorAttributes instructor = getInstructor(courseId, userEmail, role);
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

                    sectionTeamNameTable.computeIfAbsent(section, key -> new HashSet<>())
                                        .add(student.team);

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
        if (isInstructor(role) && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)
                || response.recipient.equals(userEmail)
                        && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                || response.giver.equals(userEmail)
                || isStudent(role) && relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
            isVisibleResponse = true;
        } else if (studentsEmailInTeam != null && isStudent(role)) {
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
        responseCommentList.sort(Comparator.comparing(responseComment -> responseComment.createdAt));
    }

    private void addVisibilityToTable(Map<String, boolean[]> visibilityTable,
            FeedbackQuestionAttributes question,
            FeedbackResponseAttributes response,
            String userEmail,
            UserRole role,
            CourseRoster roster) {
        boolean[] visibility = new boolean[2];
        visibility[Const.VISIBILITY_TABLE_GIVER] = frLogic.isNameVisibleToUser(
                question, response, userEmail, role, true, roster);
        visibility[Const.VISIBILITY_TABLE_RECIPIENT] = frLogic.isNameVisibleToUser(
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

    private void addEmailNamePairsToTable(Map<String, String> emailNameTable,
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes question, CourseRoster roster,
            int pairType) {
        if (question.giverType == FeedbackParticipantType.TEAMS
                && roster.isStudentInCourse(response.giver)) {
            emailNameTable.putIfAbsent(
                        response.giver + Const.TEAM_OF_EMAIL_OWNER,
                        getNameTeamNamePairForEmail(question.giverType,
                                response.giver, roster)[pairType]);

            StudentAttributes studentGiver = roster.getStudentForEmail(response.giver);
            if (studentGiver != null) {
                emailNameTable.putIfAbsent(studentGiver.team, getNameTeamNamePairForEmail(
                        question.giverType,
                        response.giver, roster)[pairType]);
            }
        } else {
            emailNameTable.putIfAbsent(
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

        emailNameTable.putIfAbsent(
                    response.recipient,
                    getNameTeamNamePairForEmail(recipientType,
                            response.recipient, roster)[pairType]);
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

    private List<FeedbackSessionDetailsBundle> getFeedbackSessionDetailsForCourse(String courseId)
            throws EntityDoesNotExistException {
        List<FeedbackSessionDetailsBundle> fsDetails = new ArrayList<>();
        List<FeedbackSessionAttributes> fsInCourse =
                fsDb.getFeedbackSessionsForCourse(courseId);

        for (FeedbackSessionAttributes fsa : fsInCourse) {
            fsDetails.add(getFeedbackSessionDetails(fsa));
        }

        return fsDetails;
    }

    private List<FeedbackSessionAttributes> getFeedbackSessionsListForCourse(String courseId) {

        return fsDb.getFeedbackSessionsForCourse(courseId);
    }

    private List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsListForCourse(String courseId) {

        return fsDb.getSoftDeletedFeedbackSessionsForCourse(courseId);
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

    /**
     * Checks whether the feedback session is viewable to the specified user.
     */
    private boolean isFeedbackSessionViewableTo(
            FeedbackSessionAttributes session,
            boolean isInstructorOfCourse) {

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
        List<FeedbackQuestionAttributes> questionsWithVisibleResponses = new ArrayList<>();
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

}
