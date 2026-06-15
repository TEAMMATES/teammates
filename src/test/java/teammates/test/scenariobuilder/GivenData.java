package teammates.test.scenariobuilder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import teammates.common.datatransfer.DataBundle;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.BaseEntity;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Institute;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;

/**
 * Builder for test data. Provides methods to create entities and manage their
 * relationships.
 *
 * <p>
 * Usage example:
 * 1. Create a GivenData instance with a unique test name.
 * This ensures that generated IDs are consistent across test runs and do not
 * collide with other tests.
 *
 * <pre>
 * GivenData given = new GivenData("testName");
 * </pre>
 *
 * <p>
 * 2. Use the provided methods to create entities, providing only the
 * information that is relavant to the test.
 * For example, if the course and section of a team are not important for the
 * test, they will be automatically created with default values.
 *
 * <pre>
 * TeamRef team = given.team("teamAlias");
 * </pre>
 *
 * <p>
 * 3. If specific values are needed for the entities, use the options to
 * customize them.
 *
 * <pre>
 * CourseRef course = given.course("courseAlias", c -> c.name("Custom Course Name"));
 * CourseRef softDeletedCourse = given.course("softDeletedCourseAlias", c -> {
 *     c.name("Soft Deleted Course");
 *     c.softDeleted();
 * });
 * </pre>
 *
 * <p>
 * 4. To create relationships between entities, pass the appropriate aliases in
 * the options.
 * For example, to create a section that belongs to a particular course, use the
 * course alias in the section options.
 *
 * <pre>
 * CourseRef course = given.course("courseAlias");
 * SectionRef section = given.section("sectionAlias", s -> s.course(course.alias()));
 * </pre>
 *
 * <p>
 * 5. Call persistGivenData to save all created entities to the database.
 *
 * <pre>
 * persistGivenData(given);
 * </pre>
 *
 * <p>
 * 6. Use the generated IDs in the test assertions or further entity creation.
 */
public final class GivenData {
    final DataBundle dataBundle = new DataBundle();
    final Map<BaseEntity, String> entityToAlias = new HashMap<>();
    private final String testName;

    public GivenData(String testName) {
        this.testName = testName;
    }

    /**
     * Creates an institute with default values.
     */
    public InstituteRef institute(String alias) {
        return institute(alias, i -> {
        });
    }

    /**
     * Creates an institute and applies the provided options to customize it.
     */
    public InstituteRef institute(String alias, Consumer<GivenInstitute> options) {
        GivenInstitute instituteData = new GivenInstitute(this, uuid(alias));
        options.accept(instituteData);
        Institute institute = instituteData.build();
        registerEntity(alias, institute, dataBundle.institutes);
        return new InstituteRef(institute.getId(), alias);
    }

    /**
     * Creates an account with default values.
     */
    public AccountRef account(String alias) {
        return account(alias, a -> {
        });
    }

    /**
     * Creates an account and applies the provided options to customize it.
     */
    public AccountRef account(String alias, Consumer<GivenAccount> options) {
        GivenAccount accountData = new GivenAccount(this, uuid(alias));
        options.accept(accountData);
        Account account = accountData.build();
        registerEntity(alias, account, dataBundle.accounts);
        return new AccountRef(account.getId(), alias);
    }

    /**
     * Creates an account request with default values.
     */
    public AccountVerificationRequestRef accountVerificationRequest(String alias) {
        return accountVerificationRequest(alias, ar -> {
        });
    }

    /**
     * Creates an account request and applies the provided options to customize it.
     */
    public AccountVerificationRequestRef accountVerificationRequest(String alias, Consumer<GivenAccountVerificationRequest> options) {
        GivenAccountVerificationRequest accountVerificationRequestData = new GivenAccountVerificationRequest(this, uuid(alias));
        options.accept(accountVerificationRequestData);
        AccountVerificationRequest accountVerificationRequest = accountVerificationRequestData.build();
        registerEntity(alias, accountVerificationRequest, dataBundle.accountVerificationRequests);
        return new AccountVerificationRequestRef(accountVerificationRequest.getId(), alias);
    }

    /**
     * Creates a course with default values.
     */
    public CourseRef course(String alias) {
        return course(alias, c -> {
        });
    }

    /**
     * Creates a course and applies the provided options to customize it.
     */
    public CourseRef course(String alias, Consumer<GivenCourse> options) {
        GivenCourse courseData = new GivenCourse(this, stringId(alias));
        options.accept(courseData);
        Course course = courseData.build();
        registerEntity(alias, course, dataBundle.courses);
        return new CourseRef(course.getId(), alias);
    }

    /**
     * Creates a section with default values.
     */
    public SectionRef section(String alias) {
        return section(alias, s -> {
        });
    }

    /**
     * Creates a section and applies the provided options to customize it.
     */
    public SectionRef section(String alias, Consumer<GivenSection> options) {
        GivenSection sectionData = new GivenSection(this, uuid(alias));
        options.accept(sectionData);
        Section section = sectionData.build();
        registerEntity(alias, section, dataBundle.sections);
        return new SectionRef(section.getId(), alias);
    }

    /**
     * Creates a team with default values.
     */
    public TeamRef team(String alias) {
        return team(alias, t -> {
        });
    }

    /**
     * Creates a team and applies the provided options to customize it.
     */
    public TeamRef team(String alias, Consumer<GivenTeam> options) {
        GivenTeam teamData = new GivenTeam(this, uuid(alias));
        options.accept(teamData);
        Team team = teamData.build();
        registerEntity(alias, team, dataBundle.teams);
        return new TeamRef(team.getId(), alias);
    }

    /**
     * Creates a student with default values.
     */
    public StudentRef student(String alias) {
        return student(alias, s -> {
        });
    }

    /**
     * Creates a student and applies the provided options to customize it.
     */
    public StudentRef student(String alias, Consumer<GivenStudent> options) {
        GivenStudent studentData = new GivenStudent(this, uuid(alias));
        options.accept(studentData);
        Student student = studentData.build();
        registerEntity(alias, student, dataBundle.students);
        return new StudentRef(student.getId(), alias, student.getRegKey());
    }

    /**
     * Creates an instructor with default values.
     */
    public InstructorRef instructor(String alias) {
        return instructor(alias, i -> {
        });
    }

    /**
     * Creates an instructor and applies the provided options to customize it.
     */
    public InstructorRef instructor(String alias, Consumer<GivenInstructor> options) {
        GivenInstructor instructorData = new GivenInstructor(this, uuid(alias));
        options.accept(instructorData);
        Instructor instructor = instructorData.build();
        registerEntity(alias, instructor, dataBundle.instructors);
        return new InstructorRef(instructor.getId(), alias, instructor.getRegKey());
    }

    /**
     * Creates a feedback session with default values.
     */
    public FeedbackSessionRef feedbackSession(String alias) {
        return feedbackSession(alias, fs -> {
        });
    }

    /**
     * Creates a feedback session and applies the provided options to customize it.
     */
    public FeedbackSessionRef feedbackSession(String alias, Consumer<GivenFeedbackSession> options) {
        GivenFeedbackSession feedbackSessionData = new GivenFeedbackSession(this, uuid(alias));
        options.accept(feedbackSessionData);
        FeedbackSession feedbackSession = feedbackSessionData.build();
        registerEntity(alias, feedbackSession, dataBundle.feedbackSessions);
        return new FeedbackSessionRef(feedbackSession.getId(), alias);
    }

    /**
     * Creates a feedback question with default values.
     */
    public FeedbackQuestionRef feedbackQuestion(String alias) {
        return feedbackQuestion(alias, fq -> {
        });
    }

    /**
     * Creates a feedback question and applies the provided options to customize it.
     */
    public FeedbackQuestionRef feedbackQuestion(String alias, Consumer<GivenFeedbackQuestion> options) {
        GivenFeedbackQuestion feedbackQuestionData = new GivenFeedbackQuestion(this, uuid(alias));
        options.accept(feedbackQuestionData);
        FeedbackQuestion feedbackQuestion = feedbackQuestionData.build();
        registerEntity(alias, feedbackQuestion, dataBundle.feedbackQuestions);
        return new FeedbackQuestionRef(feedbackQuestion.getId(), alias);
    }

    /**
     * Creates a feedback response with default values.
     */
    public FeedbackResponseRef feedbackResponse(String alias) {
        return feedbackResponse(alias, fr -> {
        });
    }

    /**
     * Creates a feedback response and applies the provided options to customize it.
     */
    public FeedbackResponseRef feedbackResponse(String alias, Consumer<GivenFeedbackResponse> options) {
        GivenFeedbackResponse feedbackResponseData = new GivenFeedbackResponse(this, uuid(alias));
        options.accept(feedbackResponseData);
        FeedbackResponse feedbackResponse = feedbackResponseData.build();
        registerEntity(alias, feedbackResponse, dataBundle.feedbackResponses);
        return new FeedbackResponseRef(feedbackResponse.getId(), alias);
    }

    /**
     * Creates a response instructor comment with default values.
     */
    public ResponseInstructorCommentRef responseInstructorComment(String alias) {
        return responseInstructorComment(alias, ric -> {
        });
    }

    /**
     * Creates a response instructor comment and applies the provided options to customize it.
     */
    public ResponseInstructorCommentRef responseInstructorComment(
            String alias, Consumer<GivenResponseInstructorComment> options) {
        GivenResponseInstructorComment responseInstructorCommentData =
                new GivenResponseInstructorComment(this, uuid(alias));
        options.accept(responseInstructorCommentData);
        ResponseInstructorComment responseInstructorComment = responseInstructorCommentData.build();
        registerEntity(alias, responseInstructorComment, dataBundle.responseInstructorComments);
        return new ResponseInstructorCommentRef(responseInstructorComment.getId(), alias);
    }

    /**
     * Creates a deadline extension with default values.
     */
    public DeadlineExtensionRef deadlineExtension(String alias) {
        return deadlineExtension(alias, de -> {
        });
    }

    /**
     * Creates a deadline extension and applies the provided options to customize it.
     */
    public DeadlineExtensionRef deadlineExtension(String alias, Consumer<GivenDeadlineExtension> options) {
        GivenDeadlineExtension deadlineExtensionData = new GivenDeadlineExtension(this, uuid(alias));
        options.accept(deadlineExtensionData);
        DeadlineExtension deadlineExtension = deadlineExtensionData.build();
        registerEntity(alias, deadlineExtension, dataBundle.deadlineExtensions);
        return new DeadlineExtensionRef(deadlineExtension.getId(), alias);
    }

    /**
     * Creates a feedback session log with default values.
     */
    public FeedbackSessionLogRef feedbackSessionLog(String alias) {
        return feedbackSessionLog(alias, fsl -> {
        });
    }

    /**
     * Creates a feedback session log and applies the provided options to customize it.
     */
    public FeedbackSessionLogRef feedbackSessionLog(String alias, Consumer<GivenFeedbackSessionLog> options) {
        GivenFeedbackSessionLog feedbackSessionLogData = new GivenFeedbackSessionLog(this, uuid(alias));
        options.accept(feedbackSessionLogData);
        FeedbackSessionLog feedbackSessionLog = feedbackSessionLogData.build();
        registerEntity(alias, feedbackSessionLog, dataBundle.feedbackSessionLogs);
        return new FeedbackSessionLogRef(feedbackSessionLog.getId(), alias);
    }

    /**
     * Creates a notification with default values.
     */
    public NotificationRef notification(String alias) {
        return notification(alias, n -> {
        });
    }

    /**
     * Creates a notification and applies the provided options to customize it.
     */
    public NotificationRef notification(String alias, Consumer<GivenNotification> options) {
        GivenNotification notificationData = new GivenNotification(this, uuid(alias));
        options.accept(notificationData);
        Notification notification = notificationData.build();
        registerEntity(alias, notification, dataBundle.notifications);
        return new NotificationRef(notification.getId(), alias);
    }

    /**
     * Creates a read notification with default values.
     */
    public ReadNotificationRef readNotification(String alias) {
        return readNotification(alias, rn -> {
        });
    }

    /**
     * Creates a read notification and applies the provided options to customize it.
     */
    public ReadNotificationRef readNotification(String alias, Consumer<GivenReadNotification> options) {
        GivenReadNotification readNotificationData = new GivenReadNotification(this, uuid(alias));
        options.accept(readNotificationData);
        ReadNotification readNotification = readNotificationData.build();
        registerEntity(alias, readNotification, dataBundle.readNotifications);
        return new ReadNotificationRef(readNotification.getId(), alias);
    }

    /**
     * Returns the data bundle containing all created entities.
     */
    public DataBundle getDataBundle() {
        return dataBundle;
    }

    /**
     * Stores an entity in the data bundle and records its alias.
     */
    private <E extends BaseEntity> E registerEntity(String alias, E entity, Map<String, E> map) {
        assert !map.containsKey(alias) : "Alias '" + alias + "' is already used for another entity";
        map.put(alias, entity);
        entityToAlias.put(entity, alias);
        return entity;
    }

    /**
     * Helper method to get an entity from a map by alias, or create it if it does
     * not exist.
     */
    <T> T getOrCreate(String alias, Map<String, T> map, Consumer<String> create) {
        T entity = map.get(alias);
        if (entity != null) {
            return entity;
        }
        create.accept(alias);
        return map.get(alias);
    }

    String getAlias(BaseEntity entity) {
        return entityToAlias.get(entity);
    }

    /**
     * Generates a string ID based on the alias and test name. The ID is
     * deterministic
     * and will be the same across test runs for the same alias and test name.
     */
    public String stringId(String alias) {
        String prefix = alias.substring(0, Math.min(alias.length(), 27));
        UUID uuid = uuid(alias);
        return prefix + "-" + uuid.toString();
    }

    /**
     * Generates a UUID based on the alias and test name. The UUID is deterministic
     * and will be the same across test runs for the same alias and test name.
     */
    public UUID uuid(String alias) {
        return UUID.nameUUIDFromBytes((testName + ":" + alias).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Reference to an account created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record AccountRef(UUID id, String alias) {}

    /**
     * Reference to an institute created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record InstituteRef(UUID id, String alias) {}

    /**
     * Reference to an account request created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record AccountVerificationRequestRef(UUID id, String alias) {}

    /**
     * Reference to a course created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record CourseRef(String id, String alias) {}

    /**
     * Reference to a section created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record SectionRef(UUID id, String alias) {}

    /**
     * Reference to a team created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record TeamRef(UUID id, String alias) {}

    /**
     * Reference to a student created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     * @param regKey registration key
     */
    public record StudentRef(UUID id, String alias, String regKey) {}

    /**
     * Reference to an instructor created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     * @param regKey registration key
     */
    public record InstructorRef(UUID id, String alias, String regKey) {}

    /**
     * Reference to a feedback session created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record FeedbackSessionRef(UUID id, String alias) {}

    /**
     * Reference to a feedback question created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record FeedbackQuestionRef(UUID id, String alias) {}

    /**
     * Reference to a feedback response created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record FeedbackResponseRef(UUID id, String alias) {}

    /**
     * Reference to a response instructor comment created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record ResponseInstructorCommentRef(UUID id, String alias) {}

    /**
     * Reference to a deadline extension created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record DeadlineExtensionRef(UUID id, String alias) {}

    /**
     * Reference to a feedback session log created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record FeedbackSessionLogRef(UUID id, String alias) {}

    /**
     * Reference to a notification created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record NotificationRef(UUID id, String alias) {}

    /**
     * Reference to a read notification created by GivenData.
     *
     * @param id generated entity ID
     * @param alias GivenData alias
     */
    public record ReadNotificationRef(UUID id, String alias) {}
}
