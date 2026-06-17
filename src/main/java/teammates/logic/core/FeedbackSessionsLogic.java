package teammates.logic.core;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackSessionSubmissionStatus;
import teammates.common.datatransfer.SessionLinksBundle;
import teammates.common.datatransfer.SessionResultLink;
import teammates.common.datatransfer.SessionSubmissionLink;
import teammates.common.datatransfer.SubmittedGiverSetBundle;
import teammates.common.datatransfer.UserType;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidFeedbackSessionStateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.FieldValidator;
import teammates.common.util.HibernateUtil;
import teammates.common.util.LinksUtil;
import teammates.common.util.Logger;
import teammates.common.util.RequestTracer;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.email.FeedbackSessionsEmailsLogic;
import teammates.logic.email.model.CourseSessionLinks;
import teammates.logic.email.model.EmailContact;
import teammates.logic.email.model.FeedbackSessionParticipantReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionPreviewReminderEmailContext;
import teammates.logic.email.model.FeedbackSessionSummaryEmailContext;
import teammates.logic.email.model.SessionAccessLink;
import teammates.logic.email.model.SessionLinksRecoveryContext;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.request.FeedbackSessionCreateRequest;
import teammates.ui.request.FeedbackSessionUpdateRequest;

/**
 * Handles operations related to feedback sessions.
 *
 * @see FeedbackSession
 * @see FeedbackSessionsDb
 */
public final class FeedbackSessionsLogic {

    private static final Logger log = Logger.getLogger();

    private static final int NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT = 24;
    private static final int NUMBER_OF_HOURS_BEFORE_OPENING_SOON_ALERT = 24;
    private static final long SESSION_LINK_RECOVERY_DURATION_IN_DAYS = 180;
    private static final FeedbackSessionsLogic instance = new FeedbackSessionsLogic();

    private FeedbackSessionsDb fsDb;
    private CoursesLogic coursesLogic;
    private FeedbackQuestionsLogic fqLogic;
    private FeedbackResponsesLogic frLogic;
    private UsersLogic usersLogic;
    private DeadlineExtensionsLogic deadlineExtensionsLogic;
    private FeedbackSessionsEmailsLogic feedbackSessionsEmailsLogic;

    private FeedbackSessionsLogic() {
        // prevent initialization
    }

    public static FeedbackSessionsLogic inst() {
        return instance;
    }

    void initLogicDependencies(FeedbackSessionsDb fsDb,
            FeedbackResponsesLogic frLogic, FeedbackQuestionsLogic fqLogic,
            UsersLogic usersLogic, CoursesLogic coursesLogic,
            DeadlineExtensionsLogic deadlineExtensionsLogic,
            FeedbackSessionsEmailsLogic feedbackSessionsEmailsLogic) {
        this.fsDb = fsDb;
        this.frLogic = frLogic;
        this.fqLogic = fqLogic;
        this.usersLogic = usersLogic;
        this.coursesLogic = coursesLogic;
        this.deadlineExtensionsLogic = deadlineExtensionsLogic;
        this.feedbackSessionsEmailsLogic = feedbackSessionsEmailsLogic;
    }

    /**
     * Gets a feedback session.
     *
     * @return null if not found.
     */
    public FeedbackSession getFeedbackSession(UUID id) {
        assert id != null;
        return fsDb.getFeedbackSession(id);
    }

    /**
     * Gets all feedback sessions of a course, except those that are soft-deleted.
     */
    public List<FeedbackSession> getFeedbackSessionsForCourse(String courseId) {
        return fsDb.getFeedbackSessionsForCourse(courseId);
    }

    /**
     * Gets all feedback session links for the user with {@code userId}.
     */
    public SessionLinksBundle getSessionLinks(UUID userId) throws EntityDoesNotExistException {
        User user = usersLogic.getUser(userId);
        if (user == null) {
            throw new EntityDoesNotExistException(String.format("User with id %s not found.", userId));
        }

        List<SessionSubmissionLink> submissionLinks = new ArrayList<>();
        List<SessionResultLink> resultsLinks = new ArrayList<>();
        String regKey = user.getRegKey();

        for (FeedbackSession feedbackSession : getFeedbackSessionsForCourse(user.getCourseId())) {
            submissionLinks.add(new SessionSubmissionLink(
                    feedbackSession.getId(),
                    feedbackSession.getName(),
                    feedbackSession.getStartTime().toEpochMilli(),
                    feedbackSession.getEndTime().toEpochMilli(),
                    feedbackSession.getCourse().getTimeZone(),
                    getSubmissionStatus(feedbackSession),
                    getSubmissionUrl(user.getUserType(), feedbackSession.getId(), regKey)));

            if (feedbackSession.isPublished()) {
                resultsLinks.add(new SessionResultLink(
                        feedbackSession.getId(),
                        feedbackSession.getName(),
                        feedbackSession.getStartTime().toEpochMilli(),
                        feedbackSession.getEndTime().toEpochMilli(),
                        feedbackSession.getCourse().getTimeZone(),
                        getResultUrl(user.getUserType(), feedbackSession.getId(), regKey)));
            }
        }

        return new SessionLinksBundle(getCourseJoinUrl(user.getUserType(), regKey), submissionLinks, resultsLinks);
    }

    /**
     * Enqueues a session links recovery email for the given email address.
     */
    public void enqueueSessionLinksRecoveryEmail(String recoveryEmailAddress) {
        SessionLinksRecoveryContext context = buildSessionLinksRecoveryContext(recoveryEmailAddress);
        feedbackSessionsEmailsLogic.enqueueSessionLinksRecoveryEmail(context);
    }

    /**
     * Enqueues a feedback session summary email for the given user and email type.
     */
    public void enqueueFeedbackSessionSummaryEmail(User user, EmailType emailType) {
        feedbackSessionsEmailsLogic.enqueueFeedbackSessionSummaryEmail(
                buildFeedbackSessionSummaryEmailContext(user, emailType), emailType);
    }

    /**
     * Enqueues opened reminder emails for all eligible sessions and marks them as sent.
     */
    public void enqueueOpenedReminderEmailsForEligibleSessions() {
        for (FeedbackSession session : getFeedbackSessionsWhichNeedOpenedEmailsToBeSent()) {
            RequestTracer.checkRemainingTime();
            try {
                feedbackSessionsEmailsLogic.enqueueOpenedEmails(
                        buildOpenedParticipantReminderEmailContexts(session),
                        buildReminderPreviewEmailContexts(session));
                session.setOpenedEmailSent(true);
            } catch (Exception e) {
                log.severe("Unexpected error", e);
            }
        }
    }

    /**
     * Enqueues closing soon reminder emails for all eligible sessions and
     * deadline extensions, and marks them as sent.
     */
    public void enqueueClosingSoonReminderEmailsForEligibleSessions() {
        for (FeedbackSession session : getFeedbackSessionsClosingWithinTimeLimit()) {
            RequestTracer.checkRemainingTime();
            try {
                feedbackSessionsEmailsLogic.enqueueClosingSoonEmails(
                        buildClosingSoonParticipantReminderEmailContexts(session),
                        buildReminderPreviewEmailContexts(session));
                session.setClosingSoonEmailSent(true);
            } catch (Exception e) {
                log.severe("Unexpected error", e);
            }
        }

        Map<UUID, List<DeadlineExtension>> deadlineExtensionsBySessionId =
                deadlineExtensionsLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail().stream()
                        .collect(Collectors.groupingBy(deadlineExtension -> deadlineExtension.getFeedbackSession().getId()));

        for (List<DeadlineExtension> deadlineExtensions : deadlineExtensionsBySessionId.values()) {
            RequestTracer.checkRemainingTime();
            FeedbackSession session = deadlineExtensions.get(0).getFeedbackSession();
            if (!session.isClosingSoonEmailEnabled()) {
                continue;
            }

            try {
                feedbackSessionsEmailsLogic.enqueueClosingSoonEmails(
                        buildClosingSoonDeadlineExtensionReminderEmailContexts(session, deadlineExtensions),
                        List.of());
                deadlineExtensions.forEach(deadlineExtension -> deadlineExtension.setClosingSoonEmailSent(true));
            } catch (Exception e) {
                log.severe("Unexpected error", e);
            }
        }
    }

    /**
     * Gets all feedback sessions of a course started after time, except those that are soft-deleted.
     */
    public List<FeedbackSession> getFeedbackSessionsForCourseStartingAfter(String courseId, Instant after) {
        return fsDb.getFeedbackSessionsForCourseStartingAfter(courseId, after);
    }

    private SessionLinksRecoveryContext buildSessionLinksRecoveryContext(String recoveryEmailAddress) {
        List<Student> studentsForEmail = usersLogic.getAllStudentsForEmail(recoveryEmailAddress);
        String recipientName = studentsForEmail.isEmpty() ? null : studentsForEmail.get(0).getName();
        Instant searchStartTime = TimeHelper.getInstantDaysOffsetBeforeNow(SESSION_LINK_RECOVERY_DURATION_IN_DAYS);

        Map<String, CourseSessionLinks> recoverableCourseLinksMap = new LinkedHashMap<>();

        for (Student student : studentsForEmail) {
            Course course = student.getCourse();
            List<FeedbackSession> sessions = getFeedbackSessionsForCourseStartingAfter(course.getId(), searchStartTime)
                    .stream()
                    .filter(session -> session.isOpened() || session.isClosed() || session.isPublished())
                    .toList();
            List<SessionAccessLink> recoverableSessionLinks = buildSessionAccessLinks(student, sessions);

            if (recoverableSessionLinks.isEmpty()) {
                continue;
            }

            recoverableCourseLinksMap.put(course.getId(), new CourseSessionLinks(
                    course.getId(), course.getName(), course.getTimeZone(), recoverableSessionLinks));
        }

        return new SessionLinksRecoveryContext(
                recoveryEmailAddress,
                recipientName,
                studentsForEmail.isEmpty(),
                new ArrayList<>(recoverableCourseLinksMap.values()));
    }

    private FeedbackSessionSummaryEmailContext buildFeedbackSessionSummaryEmailContext(User user, EmailType emailType) {
        if (emailType != EmailType.STUDENT_EMAIL_CHANGED
                && emailType != EmailType.STUDENT_COURSE_LINKS_REGENERATED
                && emailType != EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED) {
            throw new IllegalArgumentException("Unsupported email type: " + emailType);
        }

        Course course = user.getCourse();
        List<FeedbackSession> sessions = getFeedbackSessionsForCourse(user.getCourseId()).stream()
                .filter(session -> session.isOpenedEmailSent() || session.isPublishedEmailSent())
                .toList();
        List<SessionAccessLink> sessionLinks = buildSessionAccessLinks(user, sessions);
        return new FeedbackSessionSummaryEmailContext(
                user.getEmail(),
                user.getName(),
                course.getId(),
                course.getName(),
                usersLogic.getCoOwnerContacts(course.getId()),
                user instanceof Instructor,
                user.getAccount() == null,
                getCourseJoinUrl(user.getUserType(), user.getRegKey()),
                sessionLinks.isEmpty()
                        ? List.of()
                        : List.of(new CourseSessionLinks(
                                course.getId(), course.getName(), course.getTimeZone(), sessionLinks)));
    }

    private List<SessionAccessLink> buildSessionAccessLinks(User user, List<FeedbackSession> sessions) {
        return sessions.stream()
                .sorted(Comparator.comparing(FeedbackSession::getName))
                .map(session -> new SessionAccessLink(
                        session.getName(),
                        session.getEndTime(),
                        session.isOpened() || session.isClosed()
                                ? getSubmissionUrl(user.getUserType(), session.getId(), user.getRegKey())
                                : null,
                        session.isPublished()
                                ? getResultUrl(user.getUserType(), session.getId(), user.getRegKey())
                                : null))
                .toList();
    }

    private List<FeedbackSessionParticipantReminderEmailContext> buildOpenedParticipantReminderEmailContexts(
            FeedbackSession session) {
        Course course = session.getCourse();
        List<EmailContact> coOwnerContacts = usersLogic.getCoOwnerContacts(course.getId());
        List<FeedbackSessionParticipantReminderEmailContext> contexts = new ArrayList<>();

        if (isFeedbackSessionForUserTypeToAnswer(session, false)) {
            for (Student student : usersLogic.getStudentsForCourse(course.getId())) {
                Instant deadline = deadlineExtensionsLogic.getDeadlineForUser(session, student);
                contexts.add(buildParticipantReminderEmailContext(session, student, deadline, coOwnerContacts));
            }
        }

        if (isFeedbackSessionForUserTypeToAnswer(session, true)) {
            for (Instructor instructor : usersLogic.getInstructorsForCourse(course.getId())) {
                Instant deadline = deadlineExtensionsLogic.getDeadlineForUser(session, instructor);
                contexts.add(buildParticipantReminderEmailContext(session, instructor, deadline, coOwnerContacts));
            }
        }

        return contexts;
    }

    private List<FeedbackSessionParticipantReminderEmailContext> buildClosingSoonParticipantReminderEmailContexts(
            FeedbackSession session) {
        Course course = session.getCourse();
        Set<UUID> usersWithDeadlineExtensions = session.getDeadlineExtensions().stream()
                .map(DeadlineExtension::getUserId)
                .collect(Collectors.toSet());
        List<EmailContact> coOwnerContacts = usersLogic.getCoOwnerContacts(course.getId());
        List<FeedbackSessionParticipantReminderEmailContext> contexts = new ArrayList<>();

        if (isFeedbackSessionForUserTypeToAnswer(session, false)) {
            for (Student student : usersLogic.getStudentsForCourse(course.getId())) {
                if (usersWithDeadlineExtensions.contains(student.getId())) {
                    continue;
                }
                contexts.add(buildParticipantReminderEmailContext(
                        session, student, session.getEndTime(), coOwnerContacts));
            }
        }

        if (isFeedbackSessionForUserTypeToAnswer(session, true)) {
            for (Instructor instructor : usersLogic.getInstructorsForCourse(course.getId())) {
                if (usersWithDeadlineExtensions.contains(instructor.getId())) {
                    continue;
                }
                contexts.add(buildParticipantReminderEmailContext(
                        session, instructor, session.getEndTime(), coOwnerContacts));
            }
        }

        return contexts;
    }

    private List<FeedbackSessionParticipantReminderEmailContext> buildClosingSoonDeadlineExtensionReminderEmailContexts(
            FeedbackSession session, List<DeadlineExtension> deadlineExtensions) {
        List<EmailContact> coOwnerContacts = usersLogic.getCoOwnerContacts(session.getCourseId());
        List<FeedbackSessionParticipantReminderEmailContext> contexts = new ArrayList<>();

        for (DeadlineExtension deadlineExtension : deadlineExtensions) {
            User user = deadlineExtension.getUser();
            if (user instanceof Student && !isFeedbackSessionForUserTypeToAnswer(session, false)) {
                continue;
            }
            if (user instanceof Instructor && !isFeedbackSessionForUserTypeToAnswer(session, true)) {
                continue;
            }
            contexts.add(buildParticipantReminderEmailContext(
                    session, user, deadlineExtension.getEndTime(), coOwnerContacts));
        }

        return contexts;
    }

    private FeedbackSessionParticipantReminderEmailContext buildParticipantReminderEmailContext(
            FeedbackSession session, User user, Instant deadline, List<EmailContact> coOwnerContacts) {
        Course course = session.getCourse();
        return new FeedbackSessionParticipantReminderEmailContext(
                user.getEmail(),
                user.getName(),
                course.getId(),
                course.getName(),
                course.getTimeZone(),
                session.getName(),
                deadline,
                !session.getEndTime().equals(deadline),
                session.getInstructionsString(),
                getSubmissionUrl(user.getUserType(), session.getId(), user.getRegKey()),
                user instanceof Instructor,
                coOwnerContacts);
    }

    private List<FeedbackSessionPreviewReminderEmailContext> buildReminderPreviewEmailContexts(FeedbackSession session) {
        if (!isFeedbackSessionForUserTypeToAnswer(session, false)) {
            return List.of();
        }

        Course course = session.getCourse();
        List<EmailContact> coOwnerContacts = usersLogic.getCoOwnerContacts(course.getId());
        return usersLogic.getCoOwnersForCourse(course.getId()).stream()
                .map(coOwner -> new FeedbackSessionPreviewReminderEmailContext(
                        coOwner.getEmail(),
                        coOwner.getName(),
                        course.getId(),
                        course.getName(),
                        course.getTimeZone(),
                        session.getName(),
                        session.getEndTime(),
                        session.getInstructionsString(),
                        coOwnerContacts))
                .toList();
    }

    private String getCourseJoinUrl(UserType userType, String regKey) {
        return switch (userType) {
        case STUDENT -> LinksUtil.getStudentCourseJoinUrl(regKey);
        case INSTRUCTOR -> LinksUtil.getInstructorCourseJoinUrl(regKey);
        };
    }

    private String getSubmissionUrl(UserType userType, UUID feedbackSessionId, String regKey) {
        return switch (userType) {
        case STUDENT -> LinksUtil.getStudentSessionSubmitUrl(feedbackSessionId, regKey);
        case INSTRUCTOR -> LinksUtil.getInstructorSessionSubmitUrl(feedbackSessionId, regKey);
        };
    }

    private String getResultUrl(UserType userType, UUID feedbackSessionId, String regKey) {
        return switch (userType) {
        case STUDENT -> LinksUtil.getStudentSessionResultsUrl(feedbackSessionId, regKey);
        case INSTRUCTOR -> LinksUtil.getInstructorSessionResultsUrl(feedbackSessionId, regKey);
        };
    }

    private FeedbackSessionSubmissionStatus getSubmissionStatus(FeedbackSession feedbackSession) {
        if (!feedbackSession.isVisible()) {
            return FeedbackSessionSubmissionStatus.NOT_VISIBLE;
        }
        if (feedbackSession.isInGracePeriod()) {
            return FeedbackSessionSubmissionStatus.GRACE_PERIOD;
        }
        if (feedbackSession.isOpened()) {
            return FeedbackSessionSubmissionStatus.OPEN;
        }
        if (feedbackSession.isClosed()) {
            return FeedbackSessionSubmissionStatus.CLOSED;
        }

        return FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN;
    }

    /**
     * Gets a list of feedback sessions for instructors.
     */
    public List<FeedbackSession> getFeedbackSessionsForInstructors(
            List<Instructor> instructorList) {

        List<String> courseIds = instructorList.stream()
                .map(Instructor::getCourseId)
                .distinct()
                .collect(Collectors.toList());
        return fsDb.getFeedbackSessionsForCourses(courseIds);
    }

    /**
     * Returns a {@code List} of feedback sessions in the Recycle Bin for the instructors.
     * <br>
     * Omits sessions if the corresponding courses are in Recycle Bin
     */
    public List<FeedbackSession> getSoftDeletedFeedbackSessionsForInstructors(
            List<Instructor> instructorList) {

        List<String> courseIds = instructorList.stream()
                .map(Instructor::getCourseId)
                .distinct()
                .collect(Collectors.toList());
        return fsDb.getSoftDeletedFeedbackSessionsForCourses(courseIds);
    }

    /**
     * Gets all and only the feedback sessions ongoing within a range of time.
     */
    public List<FeedbackSession> getOngoingSessions(Instant rangeStart, Instant rangeEnd) {
        return fsDb.getOngoingSessions(rangeStart, rangeEnd);
    }

    /**
     * Gets submitted givers partitioned by giver type under a feedback session.
     *
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public SubmittedGiverSetBundle getSubmittedGiverSet(
            UUID feedbackSessionId) throws EntityDoesNotExistException {
        FeedbackSession feedbackSession = fsDb.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityDoesNotExistException(
                String.format("Feedback session with id %s not found.", feedbackSessionId));
        }

        return getSubmittedGiverSet(feedbackSession);
    }

    private SubmittedGiverSetBundle getSubmittedGiverSet(FeedbackSession feedbackSession) {
        Set<FeedbackQuestion> questions = feedbackSession.getFeedbackQuestions();
        boolean hasQuestionsForStudents = fqLogic.hasFeedbackQuestionsForStudents(questions);
        boolean hasQuestionsForIndividualStudents = fqLogic.hasFeedbackQuestionsForGiverType(
                questions, QuestionGiverType.STUDENTS);
        boolean shouldCountTeamResponsesAsStudentSubmissions = hasQuestionsForStudents
                && !hasQuestionsForIndividualStudents;
        boolean hasQuestionsForInstructors = fqLogic.hasFeedbackQuestionsForInstructors(questions, false);

        List<Student> students = usersLogic.getStudentsForCourse(feedbackSession.getCourseId());
        List<Instructor> instructors = usersLogic.getInstructorsForCourse(feedbackSession.getCourseId());
        Map<UUID, Set<UUID>> studentIdsByTeamId = students.stream()
                .filter(student -> student.getTeam() != null)
                .collect(Collectors.groupingBy(student -> student.getTeam().getId(),
                        Collectors.mapping(Student::getId, Collectors.toCollection(HashSet::new))));

        Set<UUID> studentGiverIds = new TreeSet<>();
        Set<UUID> instructorGiverIds = new TreeSet<>();
        Set<UUID> studentNonGiverIds = new TreeSet<>();
        Set<UUID> instructorNonGiverIds = new TreeSet<>();

        // Populate giver sets
        for (FeedbackQuestion question : feedbackSession.getFeedbackQuestions()) {
            for (FeedbackResponse response : question.getFeedbackResponses()) {
                ResponseGiver responseGiver = response.getGiver();

                if (responseGiver.isGiverStudent()) {
                    studentGiverIds.add(responseGiver.getGiverUserId());
                } else if (responseGiver.isGiverInstructor()) {
                    instructorGiverIds.add(responseGiver.getGiverUserId());
                } else if (responseGiver.isGiverTeam()) {
                    // For team-only sessions, one team response marks all team members as submitted.
                    if (shouldCountTeamResponsesAsStudentSubmissions) {
                        addStudentGiversForTeamResponse(response, studentIdsByTeamId, studentGiverIds);
                    }
                } else {
                    log.warning("Unknown giver type for response: " + response.getId());
                }
            }
        }

        // Populate non-giver sets
        if (hasQuestionsForStudents) {
            Set<UUID> allStudentIds = students.stream()
                    .map(Student::getId)
                    .collect(Collectors.toSet());
            studentNonGiverIds.addAll(allStudentIds);
            studentNonGiverIds.removeAll(studentGiverIds);
        }

        if (hasQuestionsForInstructors) {
            Set<UUID> allInstructorIds = instructors.stream()
                    .map(Instructor::getId)
                    .collect(Collectors.toSet());
            instructorNonGiverIds.addAll(allInstructorIds);
            instructorNonGiverIds.removeAll(instructorGiverIds);
        }

        return new SubmittedGiverSetBundle(studentGiverIds, instructorGiverIds, studentNonGiverIds, instructorNonGiverIds);
    }

    private void addStudentGiversForTeamResponse(FeedbackResponse response, Map<UUID, Set<UUID>> studentIdsByTeamId,
            Set<UUID> studentGiverIds) {
        UUID giverTeamId = response.getGiver().getGiverTeamId();
        Set<UUID> memberStudentIds = studentIdsByTeamId.get(giverTeamId);
        if (memberStudentIds == null || memberStudentIds.isEmpty()) {
            log.warning("No students found for team giver response: " + response.getId());
            return;
        }

        studentGiverIds.addAll(memberStudentIds);
    }

    /**
     * Creates a feedback session from a create request.
     *
     * @return created feedback session
     * @throws EntityDoesNotExistException if the course does not exist
     * @throws InvalidParametersException if the session timing is invalid
     */
    public FeedbackSession createFeedbackSession(String courseId, Instructor instructor,
            FeedbackSessionCreateRequest createRequest)
            throws InvalidParametersException, EntityDoesNotExistException {
        Course course = coursesLogic.getCourse(courseId);
        if (course == null) {
            throw new EntityDoesNotExistException("Failed to find course with the given course id.");
        }

        String feedbackSessionName = SanitizationHelper.sanitizeTitle(createRequest.getFeedbackSessionName());
        String timeZone = course.getTimeZone();
        Instant startTime = createRequest.getSubmissionStartTime();
        Instant endTime = createRequest.getSubmissionEndTime();
        Instant sessionVisibleTime = createRequest.getSessionVisibleFromTime();

        validateNewFeedbackSessionTiming(null, timeZone, startTime, endTime, sessionVisibleTime);

        FeedbackSession feedbackSession = new FeedbackSession(
                feedbackSessionName,
                instructor,
                createRequest.getInstructions(),
                startTime,
                endTime,
                sessionVisibleTime,
                createRequest.getResultsVisibleFromTime(),
                createRequest.getGracePeriod(),
                createRequest.isClosingSoonEmailEnabled(),
                createRequest.isPublishedEmailEnabled()
        );
        course.addFeedbackSession(feedbackSession);
        validateFeedbackSession(feedbackSession);
        feedbackSession = fsDb.persistFeedbackSession(feedbackSession);

        if (createRequest.getToCopySessionId() != null) {
            copyFeedbackQuestions(createRequest.getToCopySessionId(), feedbackSession);
        }

        HibernateUtil.flushSession();
        return feedbackSession;
    }

    private void copyFeedbackQuestions(UUID oldSessionId, FeedbackSession newFeedbackSession) {
        FeedbackSession oldFeedbackSession = getFeedbackSession(oldSessionId);
        fqLogic.getFeedbackQuestionsForSession(oldFeedbackSession).forEach(question -> {
            FeedbackQuestion feedbackQuestion = question.makeDeepCopy();
            newFeedbackSession.addFeedbackQuestion(feedbackQuestion);
            try {
                fqLogic.createFeedbackQuestion(feedbackQuestion);
            } catch (InvalidParametersException | EntityAlreadyExistsException e) {
                log.severe("Error when copying feedback question: " + e.getMessage());
            }
        });
    }

    /**
     * Updates a feedback session.
     *
     * @return updated feedback session
     * @throws EntityDoesNotExistException if the feedback session does not exist
     * @throws InvalidParametersException if the new fields for feedback session are invalid
     */
    public FeedbackSession updateFeedbackSession(UUID feedbackSessionId, FeedbackSessionUpdateRequest updateRequest)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSession session = getFeedbackSession(feedbackSessionId);
        if (session == null) {
            throw new EntityDoesNotExistException(
                String.format("Feedback session with id %s not found.", feedbackSessionId));
        }

        String timeZone = session.getCourse().getTimeZone();
        Instant startTime = updateRequest.getSubmissionStartTime();
        Instant endTime = updateRequest.getSubmissionEndTime();
        Instant sessionVisibleTime = updateRequest.getSessionVisibleFromTime();
        Instant resultsVisibleTime = updateRequest.getResultsVisibleFromTime();

        validateNewFeedbackSessionTiming(session, timeZone, startTime, endTime, sessionVisibleTime);

        session.setInstructions(updateRequest.getInstructions());
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setGracePeriod(updateRequest.getGracePeriod());
        session.setSessionVisibleFromTime(sessionVisibleTime);
        session.setResultsVisibleFromTime(resultsVisibleTime);
        session.setClosingSoonEmailEnabled(updateRequest.isClosingSoonEmailEnabled());
        session.setPublishedEmailEnabled(updateRequest.isPublishedEmailEnabled());

        validateFeedbackSession(session);

        return session;
    }

    /**
     * Validates that the new timing fields of the feedback session are valid.
     */
    private void validateNewFeedbackSessionTiming(FeedbackSession session, String timeZone,
            Instant newStartTime, Instant newEndTime, Instant newSessionVisibleTime) throws InvalidParametersException {
        boolean isStartTimeChanged = session == null || !newStartTime.equals(session.getStartTime());
        boolean isEndTimeChanged = session == null || !newEndTime.equals(session.getEndTime());
        boolean isSessionVisibleTimeChanged = session == null
                || !newSessionVisibleTime.equals(session.getSessionVisibleFromTime());

        if (isStartTimeChanged) {
            String startTimeError = FieldValidator.getInvalidityInfoForNewStartTime(newStartTime, timeZone);
            if (!startTimeError.isEmpty()) {
                throw new InvalidParametersException("Invalid submission opening time: " + startTimeError);
            }
        }

        if (isEndTimeChanged) {
            String endTimeError = FieldValidator.getInvalidityInfoForNewEndTime(newEndTime, timeZone);
            if (!endTimeError.isEmpty()) {
                throw new InvalidParametersException("Invalid submission closing time: " + endTimeError);
            }
        }

        if (isSessionVisibleTimeChanged) {
            String visibilityStartAndSessionStartTimeError = FieldValidator
                    .getInvalidityInfoForTimeForNewVisibilityStart(newSessionVisibleTime, newStartTime);
            if (!visibilityStartAndSessionStartTimeError.isEmpty()) {
                throw new InvalidParametersException("Invalid session visible time: "
                        + visibilityStartAndSessionStartTimeError);
            }
        }
    }

    /**
     * Unpublishes a feedback session.
     *
     * @return the unpublished feedback session
     * @throws InvalidFeedbackSessionStateException if session is already unpublished
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSession unpublishFeedbackSession(UUID feedbackSessionId)
            throws EntityDoesNotExistException, InvalidFeedbackSessionStateException {

        FeedbackSession sessionToUnpublish = getFeedbackSession(feedbackSessionId);
        if (sessionToUnpublish == null) {
            throw new EntityDoesNotExistException(
                String.format("Feedback session with id %s not found.", feedbackSessionId));
        }

        if (!sessionToUnpublish.isPublished()) {
            throw new InvalidFeedbackSessionStateException("Feedback Session is already unpublished.");
        }

        sessionToUnpublish.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        sessionToUnpublish.setPublishedEmailSent(false);

        return sessionToUnpublish;
    }

    /**
     * Publishes a feedback session.
     *
     * @return the published feedback session
     * @throws InvalidFeedbackSessionStateException if session is already published
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSession publishFeedbackSession(UUID feedbackSessionId)
            throws EntityDoesNotExistException, InvalidFeedbackSessionStateException {

        FeedbackSession sessionToPublish = getFeedbackSession(feedbackSessionId);

        if (sessionToPublish == null) {
            throw new EntityDoesNotExistException(
                String.format("Feedback session with id %s not found.", feedbackSessionId));
        }

        if (sessionToPublish.isPublished()) {
            throw new InvalidFeedbackSessionStateException("Feedback Session is already published.");
        }

        sessionToPublish.setResultsVisibleFromTime(Instant.now());
        sessionToPublish.setPublishedEmailSent(false);

        return sessionToPublish;
    }

    /**
     * Deletes a feedback session cascade to its associated questions, responses, deadline extensions and comments.
     *
     * <p>Fails silently if the feedback session doesn't exist.</p>
     */
    public void deleteFeedbackSessionCascade(UUID feedbackSessionId) {
        FeedbackSession feedbackSession = fsDb.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            return;
        }

        fsDb.removeFeedbackSession(feedbackSession);
    }

    /**
     * Soft-deletes a specific feedback session to Recycle Bin.
     * @return the feedback session
     */
    public FeedbackSession moveFeedbackSessionToRecycleBin(UUID feedbackSessionId)
            throws EntityDoesNotExistException {
        FeedbackSession feedbackSession = getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityDoesNotExistException(
                String.format("Feedback Session with id %s does not exist.", feedbackSessionId));
        }

        feedbackSession.setDeletedAt(Instant.now());

        return feedbackSession;
    }

    /**
     * Restores a specific feedback session from Recycle Bin.
     */
    public FeedbackSession restoreFeedbackSessionFromRecycleBin(UUID feedbackSessionId)
            throws EntityDoesNotExistException {
        FeedbackSession feedbackSession = getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityDoesNotExistException(
                String.format("Feedback Session with id %s does not exist.", feedbackSessionId));
        }

        feedbackSession.setDeletedAt(null);

        return feedbackSession;
    }

    /**
     * Returns true if there are any questions for the specified user type (students/instructors) to answer.
     */
    public boolean isFeedbackSessionForUserTypeToAnswer(FeedbackSession session, boolean isInstructor) {
        if (!session.isVisible()) {
            return false;
        }

        return isInstructor
                ? fqLogic.hasFeedbackQuestionsForInstructors(session.getFeedbackQuestions(), false)
                : fqLogic.hasFeedbackQuestionsForStudents(session.getFeedbackQuestions());
    }

    /**
     * Returns true if the feedback session is viewable by the given user type (students/instructors).
     */
    public boolean isFeedbackSessionViewableToUserType(FeedbackSession session, boolean isInstructor) {
        // Allow user to view the feedback session if there are questions for them
        if (isFeedbackSessionForUserTypeToAnswer(session, isInstructor)) {
            return true;
        }

        // Allow user to view the feedback session if there are any question whose responses are visible to the user
        Set<FeedbackQuestion> questionsWithVisibleResponses = new HashSet<>();
        Set<FeedbackQuestion> questionsForUser = session.getFeedbackQuestions();
        for (FeedbackQuestion question : questionsForUser) {
            if (!isInstructor && frLogic.isResponseOfFeedbackQuestionVisibleToStudent(question)
                    || isInstructor && frLogic.isResponseOfFeedbackQuestionVisibleToInstructor(question)) {
                // We only need one question with visible responses for the entire session to be visible
                questionsWithVisibleResponses.add(question);
                break;
            }
        }

        return session.isVisible() && !questionsWithVisibleResponses.isEmpty();
    }

    /**
     * Checks whether a student has attempted a feedback session.
     *
     * <p>If feedback session consists of all team questions, session is attempted by student only
     * if someone from the team has responded. If feedback session has some individual questions,
     * session is attempted only if the student has responded to any of the individual questions
     * (regardless of the completion status of the team questions).</p>
     */
    public boolean isFeedbackSessionAttemptedByStudent(FeedbackSession session, String userEmail, String userTeam) {
        assert session != null;
        assert userEmail != null;
        assert userTeam != null;

        if (!fqLogic.hasFeedbackQuestionsForStudents(session.getFeedbackQuestions())) {
            // if there are no questions for student, session is attempted
            return true;
        } else if (fqLogic.hasFeedbackQuestionsForGiverType(
                session.getFeedbackQuestions(), QuestionGiverType.STUDENTS)) {
            // case where there are some individual questions
            return frLogic.hasGiverRespondedForSession(userEmail, session.getFeedbackQuestions());
        } else {
            // case where all are team questions
            return frLogic.hasGiverRespondedForSession(userTeam, session.getFeedbackQuestions());
        }
    }

    /**
     * After an update to feedback session's fields, may need to adjust the email status of the session.
     * @param session recently updated session.
     */
    public void adjustFeedbackSessionEmailStatusAfterUpdate(FeedbackSession session) {
        // reset isOpenedEmailSent if the session has opened but is being un-opened
        // now, or else leave it as sent if so.
        if (session.isOpenedEmailSent()) {
            session.setOpenedEmailSent(session.isOpened());

            // also reset isOpeningSoonEmailSent
            session.setOpeningSoonEmailSent(
                    session.isOpened() || session.isOpeningInHours(NUMBER_OF_HOURS_BEFORE_OPENING_SOON_ALERT));
        }

        // reset isClosedEmailSent if the session has closed but is being un-closed
        // now, or else leave it as sent if so.
        if (session.isClosedEmailSent()) {
            session.setClosedEmailSent(session.isClosed());

            // also reset isClosingSoonEmailSent
            session.setClosingSoonEmailSent(
                    session.isClosed() || session.isClosedAfter(NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT));
        }
    }

    /**
     * Criteria: must be published, publishEmail must be enabled and
     * resultsVisibleTime must be custom.
     *
     * @return returns a list of sessions that require automated emails to be
     *         sent as they are published
     */
    public List<FeedbackSession> getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent() {
        List<FeedbackSession> sessions = fsDb.getFeedbackSessionsPossiblyNeedingPublishedEmail();
        log.info(String.format("Number of sessions to send published emails for: %d", sessions.size()));
        return sessions;
    }

    /**
     * Returns sessions in the reminder window before the closing time.
     */
    public List<FeedbackSession> getFeedbackSessionsClosingWithinTimeLimit() {
        List<FeedbackSession> sessions = fsDb.getFeedbackSessionsPossiblyNeedingClosingSoonEmail();
        log.info(String.format("Number of sessions under consideration: %d", sessions.size()));

        List<FeedbackSession> filteredSessions = new ArrayList<>();
        for (FeedbackSession session : sessions) {
            if (Duration.between(session.getStartTime(), session.getEndTime())
                    .compareTo(Duration.ofHours(NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT)) > 0) {
                filteredSessions.add(session);
            }
        }

        log.info(String.format("Number of sessions under consideration after filtering: %d",
                filteredSessions.size()));
        return filteredSessions;
    }

    /**
     * Returns sessions in the reminder window before the opening time.
     */
    public List<FeedbackSession> getFeedbackSessionsOpeningWithinTimeLimit() {
        List<FeedbackSession> sessions = fsDb.getFeedbackSessionsPossiblyNeedingOpeningSoonEmail();
        log.info(String.format("Number of sessions to send opening soon emails for: %d", sessions.size()));
        return sessions;
    }

    /**
     * Returns a list of sessions that were closed recently.
     */
    public List<FeedbackSession> getFeedbackSessionsClosedRecently() {
        List<FeedbackSession> filteredSessions = new ArrayList<>();
        List<FeedbackSession> sessions = fsDb.getFeedbackSessionsPossiblyNeedingClosedEmail();
        log.info(String.format("Number of sessions under consideration: %d", sessions.size()));

        for (FeedbackSession session : sessions) {
            if (session.isClosedWithin(Const.FEEDBACK_SESSION_REMINDER_EMAIL_REDUNDANCY_WINDOW)) {
                filteredSessions.add(session);
            }
        }
        log.info(String.format("Number of sessions under consideration after filtering: %d",
                filteredSessions.size()));
        return filteredSessions;
    }

    /**
     * Gets a list of undeleted feedback sessions which opened recently
     * and need an open email to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsWhichNeedOpenedEmailsToBeSent() {
        List<FeedbackSession> sessions = fsDb.getFeedbackSessionsPossiblyNeedingOpenedEmail();
        log.info(String.format("Number of sessions to send opened emails for: %d", sessions.size()));
        return sessions;
    }

    /**
     * Gets the expected number of submissions for a feedback session.
     */
    public int getExpectedTotalSubmission(FeedbackSession fs) {
        int expectedTotal = 0;
        Set<FeedbackQuestion> questions = fs.getFeedbackQuestions();
        if (fqLogic.hasFeedbackQuestionsForStudents(questions)) {
            expectedTotal += usersLogic.getStudentsForCourse(fs.getCourseId()).size();
        }

        // Pre-flight check to ensure there are questions for instructors.
        if (!fqLogic.hasFeedbackQuestionsForInstructors(questions, true)) {
            return expectedTotal;
        }

        List<Instructor> instructors = usersLogic.getInstructorsForCourse(fs.getCourseId());
        if (instructors.isEmpty()) {
            return expectedTotal;
        }

        // Check presence of questions for instructors.
        if (fqLogic.hasFeedbackQuestionsForInstructors(questions, false)) {
            expectedTotal += instructors.size();
        } else {
            // No questions for instructors. There must be questions for creator.
            List<Instructor> creators = instructors.stream()
                    .filter(fs::isCreator)
                    .toList();
            expectedTotal += creators.size();
        }
        return expectedTotal;
    }

    /**
     * Gets the actual number of submissions for a feedback session.
     */
    public int getActualTotalSubmission(FeedbackSession fs) {
        SubmittedGiverSetBundle submittedGiverSetBundle = getSubmittedGiverSet(fs);
        return submittedGiverSetBundle.studentGiverIds().size()
                + submittedGiverSetBundle.instructorGiverIds().size();
    }

    private void validateFeedbackSession(FeedbackSession feedbackSession)
            throws InvalidParametersException {
        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }
    }
}
