package teammates.client.scripts.sql;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import teammates.client.connector.DatastoreClient;
import teammates.client.scripts.GenerateUsageStatisticsObjects;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.logic.api.LogicExtension;
import teammates.logic.core.LogicStarter;
import teammates.storage.api.OfyHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.test.FileHelper;

/**
 * SeedDB class.
 */
@SuppressWarnings("PMD")
public class SeedDb extends DatastoreClient {
    private static final int MAX_FLUSH_SIZE = 200;
    private static final int MAX_ENTITY_SIZE = 100;
    private static final int MAX_ACCOUNT_REQUESTS = 0;
    private static final int MAX_NUM_COURSES = 5;
    private static final int MAX_STUDENT_PER_COURSE = 1000;
    private static final int MAX_TEAM_PER_SECTION = 10;
    private static final int MAX_SECTION_PER_COURSE = 10;
    private static final int MAX_FEEDBACK_SESSION_FOR_EACH_COURSE_SIZE = 5;
    private static final int MAX_QUESTION_PER_COURSE = 5;
    private static final int MAX_RESPONSES_PER_QUESTION = 5;
    private static final int MAX_COMMENTS_PER_RESPONSE = 1;
    private static final int NOTIFICATION_SIZE = 0;
    private static final int READ_NOTIFICATION_SIZE = 0;
    private static final double PERCENTAGE_STUDENTS_WITH_ACCOUNT = 0.1;
    private static final int MAX_INSTRUCTOR_PER_COURSE = 3;
    private static final double PERCENTAGE_INSTRUCTORS_WITH_ACCOUNT = 0.5;
    private static final int DEADLINE_EXTENSION_FREQ = 5;

    // chunks to divide entities. Used to determine how many entities are processed before logging
    private static final int logStep = 5;
    private Random rand = new Random();
    private final LogicExtension logic = new LogicExtension();
    private Closeable closeable;

    private Set<String> notificationsUuidSeen = new HashSet<String>();
    private ArrayList<String> notificationUuids = new ArrayList<>();
    private Map<String, Instant> notificationEndTimes = new HashMap<>();
    private Map<String, Account> googleIdToAccountForStudentsMap = new HashMap<>();

    /**
     * Sets up the dependencies needed for the DB layer.
     */
    public void setupDbLayer() throws Exception {
        LogicStarter.initializeDependencies();
    }

    /**
     * Sets up objectify service.
     */
    public void setupObjectify() {
        DatastoreOptions.Builder builder = DatastoreOptions.newBuilder().setProjectId(Config.APP_ID);
        ObjectifyService.init(new ObjectifyFactory(builder.build().getService()));
        OfyHelper.registerEntityClasses();

        closeable = ObjectifyService.begin();
    }

    /**
     * Closes objectify service.
     */
    public void tearDownObjectify() {
        closeable.close();
    }

    protected String getSrcFolder() {
        return "src/client/java/teammates/client/scripts/sql/";
    }

    /**
     * Loads the data bundle from JSON file.
     */
    protected DataBundle loadDataBundle(String jsonFileName) {
        try {
            String pathToJsonFile = getSrcFolder() + jsonFileName;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the typical data bundle.
     */
    protected DataBundle getTypicalDataBundle() {
        return loadDataBundle("typicalDataBundle.json");
    }

    /**
     * Gets a random instant.
     */
    protected Instant getRandomInstant() {
        return Instant.now();
    }

    /**
     * Persists additional data.
     */
    protected void persistAdditionalData() {
        // Each account will have this amount of read notifications
        assert NOTIFICATION_SIZE >= READ_NOTIFICATION_SIZE;
        log("Seeding Notifications and Account Request");

        seedNotifications(notificationUuids, notificationsUuidSeen, notificationEndTimes); 
        seedAccountRequests();

        log("Seeding courses");
        for (int i = 0; i < MAX_NUM_COURSES; i++) {
            if (MAX_NUM_COURSES >= logStep && i % (MAX_NUM_COURSES / logStep) == 0) {
                log(String.format("Seeded %d %% of new sets of entities",
                        (int) (100 * ((float) i / (float) MAX_NUM_COURSES))));
            }

            String courseId = String.format("Course-ID-%s", i);
            try {
                seedCourseWithCourseId(i, courseId);
                seedStudents(i, courseId);
                seedFeedbackSession(i, courseId);
                seedFeedbackQuestions(i, courseId);
                seedInstructors(i, courseId);
            } catch (Exception e) {
                log(e.toString());
            }
        }

        // GenerateUsageStatisticsObjects.main(args);
    }

    protected void saveEntityDeferred(List<teammates.storage.entity.BaseEntity> buffer, teammates.storage.entity.BaseEntity entity) {
        buffer.add(entity);
        if (buffer.size() == MAX_FLUSH_SIZE) {
            log("Flushing entities...");
            flushEntityBuffer(buffer);
        }
    }

    protected void flushEntityBuffer(List<teammates.storage.entity.BaseEntity> buffer) {
        ofy().save().entities(buffer).now();
        buffer.clear();
    }

    private void seedCourseWithCourseId(int i, String courseId) {
        String courseName = String.format("Course %s", i);
        String courseInstitute = String.format("Institute %s", i);
        String courseTimeZone = String.format("Time Zone %s", i);
        Course course = new Course(courseId, courseName, courseTimeZone, courseInstitute,
                getRandomInstant(),
                rand.nextInt(4) > 0 ? null : getRandomInstant(), // set deletedAt randomly at 25% chance
                false);
        ofy().save().entities(course).now();
    }

    private void seedStudents(int courseNumber, String courseId) {
        assert MAX_SECTION_PER_COURSE <= MAX_STUDENT_PER_COURSE;

        log("Seeding students for course " + courseNumber);
        int currSection = -1;
        int currTeam = -1;

        List<teammates.storage.entity.BaseEntity> buffer = new ArrayList<>();
        for (int i = 0; i < MAX_STUDENT_PER_COURSE; i++) {

            if (i % (MAX_STUDENT_PER_COURSE / MAX_SECTION_PER_COURSE) == 0) {
                currSection++;
                currTeam = -1; // Reset team number for each section
            }

            if (i % (MAX_STUDENT_PER_COURSE / (MAX_SECTION_PER_COURSE * MAX_TEAM_PER_SECTION)) == 0) {
                currTeam++;
            }

            try {
                String studentEmail = String.format("Course %s Student %s Email", courseNumber, i);
                String studentName = String.format("Student %s in Course %s", i, courseNumber);
                String studentGoogleId = null;
                String studentComments = String.format("Comments for student %s in course %s", i, courseNumber);
                String studentTeamName = String.format("Course %s Section %s Team %s", courseNumber, currSection, currTeam);
                String studentSectionName = String.format("Course %s Section %s", courseNumber, currSection);
                String studentRegistrationKey = String.format("Student %s in Course %s Registration Key", i,
                        courseNumber);
                
                if (rand.nextDouble() <= PERCENTAGE_STUDENTS_WITH_ACCOUNT) {
                    int googleIdNumber = courseNumber * MAX_STUDENT_PER_COURSE + i;
                    studentGoogleId = String.format("Account Google ID %s", googleIdNumber);
                    Account account = createAccount(studentGoogleId, studentName, studentEmail);
                    saveEntityDeferred(buffer, account);
                    googleIdToAccountForStudentsMap.put(studentGoogleId, account);
                }

                CourseStudent student = new CourseStudent(studentEmail, studentName, studentGoogleId, studentComments,
                courseId, studentTeamName, studentSectionName);
                student.setCreatedAt(getRandomInstant());
                student.setLastUpdate(rand.nextInt(4) > 0 ? null : getRandomInstant());
                student.setRegistrationKey(studentRegistrationKey);
                
                saveEntityDeferred(buffer, student);

                if (i % DEADLINE_EXTENSION_FREQ == 0) { // every n-th student has deadline extensions
                    seedDeadlineExtensions(courseNumber, courseId, studentEmail, false);
                }
            } catch (Exception e) {
                log("Students " + e.toString());
            }
        }
        flushEntityBuffer(buffer);
    }

    private void seedFeedbackSession(int courseNumber, String courseId) {
        Random rand = new Random();

        log("Seeding feedback chain for course " + courseNumber);
        List<teammates.storage.entity.BaseEntity> buffer = new ArrayList<>();
        for (int i = 0; i < MAX_FEEDBACK_SESSION_FOR_EACH_COURSE_SIZE; i++) {
            try {
                String feedbackSessionName = String.format("Course %s Feedback Session %s", courseNumber, i);
                String feedbackSessionCreatorEmail = String.format("Creator Email %s", i);
                String feedbackSessionInstructions = String.format("Instructions %s", i);
                String timezone = String.format("Time Zone %s", i);
                Instant feedbackSessionStartTime = getRandomInstant();
                Instant feedbackSessionEndTime = getRandomInstant();
                Instant feedbackSessionSessionVisibleFromTime = getRandomInstant();
                Instant feedbackSessionResultsVisibleFromTime = getRandomInstant();
                int feedbackSessionGracePeriod = rand.nextInt(3600);

                FeedbackSession feedbackSession = new FeedbackSession(feedbackSessionName, courseId,
                        feedbackSessionCreatorEmail, feedbackSessionInstructions, getRandomInstant(),
                        rand.nextInt(4) > 0 ? null : getRandomInstant(), // set deletedAt randomly at 25% chance
                        feedbackSessionStartTime, feedbackSessionEndTime,
                        feedbackSessionSessionVisibleFromTime, feedbackSessionResultsVisibleFromTime,
                        timezone, feedbackSessionGracePeriod,
                        rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean(),
                        rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean(), rand.nextBoolean(),
                        new HashMap<String, Instant>(), new HashMap<String, Instant>());

                saveEntityDeferred(buffer, feedbackSession);
            } catch (Exception e) {
                log("Feedback session " + e.toString());
            }
        }
        flushEntityBuffer(buffer);
    }

    private void seedFeedbackQuestions(int courseNumber, String courseId) {
        assert MAX_FEEDBACK_SESSION_FOR_EACH_COURSE_SIZE <= MAX_QUESTION_PER_COURSE;

        int currSession = -1;
        List<teammates.storage.entity.BaseEntity> listOfCreatedFeedbackQuestions = new ArrayList<>();
        for (int i = 0; i < MAX_QUESTION_PER_COURSE; i++) {
            try {
                if (i % (MAX_QUESTION_PER_COURSE / MAX_FEEDBACK_SESSION_FOR_EACH_COURSE_SIZE) == 0) {
                    currSession++;
                }
    
                String feedbackSessionName = String.format("Course %s Feedback Session %s", courseNumber, currSession);
                String questionDescription = String.format("Course %s Session %s Question %s Description",
                        courseNumber, currSession, i);
                String questionText = new FeedbackTextQuestionDetails(
                        String.format("Session %s Question %s Text", currSession, i)).getJsonString();
                int questionNumber = i;
                FeedbackQuestionType feedbackQuestionType = FeedbackQuestionType.TEXT;
                int numberOfEntitiesToGiveFeedbackTo = 1;
                FeedbackParticipantType giverType;
                FeedbackParticipantType recipientType;
                if (currSession % 2 == 0) {
                    giverType = FeedbackParticipantType.STUDENTS;
                    recipientType = FeedbackParticipantType.INSTRUCTORS;
                } else {
                    giverType = FeedbackParticipantType.INSTRUCTORS;
                    recipientType = FeedbackParticipantType.STUDENTS;
                }
    
                FeedbackQuestion feedbackQuestion = new FeedbackQuestion(feedbackSessionName, courseId,
                        questionText, questionDescription, questionNumber, feedbackQuestionType, giverType, recipientType,
                        numberOfEntitiesToGiveFeedbackTo, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                feedbackQuestion.setCreatedAt(getRandomInstant());
                feedbackQuestion.setLastUpdate(getRandomInstant());
    
                listOfCreatedFeedbackQuestions.add(feedbackQuestion);
                
            } catch (Exception e) {
                log("Feedback questions " + e.toString());
            }
        }

        ofy().save().entities(listOfCreatedFeedbackQuestions).now();

        List<teammates.storage.entity.BaseEntity> listOfCreatedFeedbackResponses = new ArrayList<>();
        for (teammates.storage.entity.BaseEntity fq : listOfCreatedFeedbackQuestions) {
            FeedbackQuestion feedbackQuestion = (FeedbackQuestion) fq;
            String feedbackQuestionId = feedbackQuestion.getId();
            FeedbackQuestionType feedbackQuestionType = feedbackQuestion.getQuestionType();
            assert feedbackQuestionId != null;
            List<teammates.storage.entity.FeedbackResponse> feedbackResponses = createFeedbackResponses(courseNumber, courseId, feedbackQuestionId, feedbackQuestionType);    
            listOfCreatedFeedbackResponses.addAll(feedbackResponses);
        }
        ofy().save().entities(listOfCreatedFeedbackResponses).now();

        List<teammates.storage.entity.FeedbackResponseComment> listOfCreatedFeedbackComments = new ArrayList<>();
        for (teammates.storage.entity.BaseEntity fr : listOfCreatedFeedbackResponses) {
            FeedbackResponse feedbackResponse = (FeedbackResponse) fr;
            String giverSection = feedbackResponse.getGiverSection();
            String recipientSection = feedbackResponse.getRecipientSection();
            String feedbackResponseId = feedbackResponse.getId();
            List<teammates.storage.entity.FeedbackResponseComment> feedbackComments = createFeedbackResponseComments(courseNumber, courseId, feedbackResponse.getFeedbackQuestionId(), feedbackResponseId, giverSection,
                    recipientSection);
            listOfCreatedFeedbackComments.addAll(feedbackComments);
        }

        ofy().save().entities(listOfCreatedFeedbackComments).now();        
    }

    private List<teammates.storage.entity.FeedbackResponse> createFeedbackResponses(int courseNumber, String courseId, String feedbackQuestionId,
            FeedbackQuestionType feedbackQuestionType){
        int currGiverSection = -1;
        int currRecipientSection = 0;

        List<teammates.storage.entity.FeedbackResponse> listOfCreatedFeedbackResponses = new ArrayList<>();
        for (int i = 0; i < MAX_RESPONSES_PER_QUESTION; i++) {
            try {
                currGiverSection = (currGiverSection + 1) % MAX_SECTION_PER_COURSE;
                currRecipientSection = (currRecipientSection + 1) % MAX_SECTION_PER_COURSE;
    
                String feedbackSessionName = String.format("Course %s Feedback Session %s", courseNumber, i);
                String giverEmail = String.format("Giver Email %s", i);
                String giverSection = String.format("Course %s Section %s", courseNumber, currGiverSection);
                String recipient = String.format("Recipient %s", i);
                String recipientSection = String.format("Course %s Section %s", courseNumber, currRecipientSection);
                String answer = new FeedbackTextResponseDetails(
                        String.format("Response %s for Question Id: %s", i, feedbackQuestionId)).getJsonString();
    
                teammates.storage.entity.FeedbackResponse feedbackResponse = new FeedbackResponse(feedbackSessionName, courseId, feedbackQuestionId,
                        feedbackQuestionType, giverEmail, giverSection, recipient, recipientSection, answer);
                feedbackResponse.setCreatedAt(getRandomInstant());
    
                listOfCreatedFeedbackResponses.add(feedbackResponse);
    
            } catch (Exception e) {
                log("Feedback response " + e.toString());
            }
        }
        return listOfCreatedFeedbackResponses;
    }

    private List<teammates.storage.entity.FeedbackResponseComment> createFeedbackResponseComments(int courseNumber, String courseId, String feedbackQuestionId, String feedbackResponseId,
            String giverSection, String receiverSection) {
        int currGiverSection = -1;
        int currRecipientSection = 0;

        List<teammates.storage.entity.FeedbackResponseComment> listOfCreatedFeedbackComments = new ArrayList<>();
        for (int i = 0; i < MAX_COMMENTS_PER_RESPONSE; i++) {
            try {
                currGiverSection = (currGiverSection + 1) % MAX_SECTION_PER_COURSE;
                currRecipientSection = (currRecipientSection + 1) % MAX_SECTION_PER_COURSE;
    
                String feedbackSessionName = String.format("Course %s Feedback Session %s", courseNumber, i);
                String giverEmail = String.format("Giver Email %s", i);
                FeedbackParticipantType commentGiverType = FeedbackParticipantType.STUDENTS;
                Instant createdAt = getRandomInstant();
                String commentText = String.format("Comment %s for Response Id: %s", i, feedbackResponseId);
                String lastEditorEmail = String.format("Last Editor Email %s", i);
                Instant lastEditedAt = getRandomInstant();
    
                FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(courseId,
                        feedbackSessionName, feedbackQuestionId, giverEmail, commentGiverType, feedbackResponseId,
                        createdAt, commentText, giverSection, receiverSection, new ArrayList<>(), new ArrayList<>(),
                        lastEditorEmail, lastEditedAt, rand.nextBoolean(), rand.nextBoolean());
                feedbackResponseComment.setCreatedAt(createdAt);
                listOfCreatedFeedbackComments.add(feedbackResponseComment);
                        
            } catch (Exception e) {
                log("Feedback response " + e.toString());
            }
        }
        return listOfCreatedFeedbackComments;
    }

    private void seedNotifications(ArrayList<String> notificationUuids,
            Set<String> notificationsUuidSeen, Map<String, Instant> notificationEndTimes) {

        List<teammates.storage.entity.BaseEntity> buffer = new ArrayList<>();
        for (int j = 0; j < NOTIFICATION_SIZE; j++) {
            UUID notificationUuid = UUID.randomUUID();
            while (notificationsUuidSeen.contains(notificationUuid.toString())) {
                notificationUuid = UUID.randomUUID();
            }
            notificationUuids.add(notificationUuid.toString());
            notificationsUuidSeen.add(notificationUuid.toString());
            // Since we are not using logic class, referencing
            // MarkNotificationAsReadAction.class and CreateNotificationAction.class
            // endTime is to nearest milli not nanosecond
            Instant endTime = getRandomInstant().truncatedTo(ChronoUnit.MILLIS);
            Notification notification = new Notification(
                    notificationUuid.toString(),
                    getRandomInstant(),
                    endTime,
                    NotificationStyle.PRIMARY,
                    NotificationTargetUser.INSTRUCTOR,
                    notificationUuid.toString(),
                    notificationUuid.toString(),
                    false,
                    getRandomInstant(),
                    getRandomInstant());
            try {
                saveEntityDeferred(buffer, notification);
                notificationEndTimes.put(notificationUuid.toString(), notification.getEndTime());
            } catch (Exception e) {
                log("Notifications " + e.toString());
            }
        }
        flushEntityBuffer(buffer);
    }

    private void seedAccountRequests() {
        List<teammates.storage.entity.BaseEntity> buffer = new ArrayList<>();
        for (int i = 0; i < MAX_ACCOUNT_REQUESTS; i++) {
            if (MAX_ACCOUNT_REQUESTS >= logStep && i % (MAX_ACCOUNT_REQUESTS / logStep) == 0) {
                log(String.format("Seeded %d %% of account requests",
                        (int) (100 * ((float) i / (float) MAX_ACCOUNT_REQUESTS))));
            }
            try {
                String accountRequestName = String.format("Account Request %s", i);
                String accountRequestEmail = String.format("Account Email %s", i);
                String accountRequestInstitute = String.format("Account Institute %s", i);
                AccountRequest accountRequest = AccountRequestAttributes
                        .builder(accountRequestEmail, accountRequestInstitute, accountRequestName)
                        .withRegisteredAt(Instant.now()).build().toEntity();
                saveEntityDeferred(buffer, accountRequest);
            } catch (Exception e) {
                log("Account and account request" + e.toString());
            }
        }
        flushEntityBuffer(buffer);
    }

    private Account createAccount(String googleId, String accountName, String email) {
        Map<String, Instant> readNotificationsToCreate = new HashMap<>();
        for (int j = 0; j < READ_NOTIFICATION_SIZE; j++) {
            int randIndex = rand.nextInt(NOTIFICATION_SIZE);
            String notificationUuid = notificationUuids.get(randIndex);
            assert notificationEndTimes.get(notificationUuid) != null;
            readNotificationsToCreate.put(notificationUuid, notificationEndTimes.get(notificationUuid));
        }

        Account account = new Account(googleId, accountName,
                email, readNotificationsToCreate, false);

        return account;
    }

    private void seedInstructors(int courseNumber, String courseId) {
        Random rand = new Random();

        log("Seeding instructors for course " + courseNumber);
        for (int i = 0; i < MAX_INSTRUCTOR_PER_COURSE; i++) {
            String instructorGoogleId = null;
            String instructorName = String.format("Course %s Instructor %s", courseNumber, i);
            String instructorEmail = String.format("Course %s Instructor %s Email", courseNumber, i);
            String role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
            String displayedName = String.format("Display Name %s", i);
            String instructorRegistrationKey = String.format("Course %s Instructor %s Registration Key", i, courseNumber);
            String instructorPrivilegesAsText = null; // not sure about legacy instructor privileges

            if (rand.nextDouble() <= PERCENTAGE_INSTRUCTORS_WITH_ACCOUNT) {
                int googleIdNumber = courseNumber * MAX_INSTRUCTOR_PER_COURSE + i
                        + MAX_NUM_COURSES * MAX_STUDENT_PER_COURSE; // to avoid clashes with students
                instructorGoogleId = String.format("Account Google ID %s", googleIdNumber);
                Account account = createAccount(instructorGoogleId, instructorName, instructorEmail);
                ofy().save().entities(account).now();
            }

            Instructor instructor = new Instructor(instructorGoogleId, courseId, rand.nextBoolean(), instructorName,
                    instructorEmail, role, rand.nextBoolean(), displayedName, instructorPrivilegesAsText);
            instructor.setCreatedAt(getRandomInstant());
            instructor.setLastUpdate(rand.nextInt(4) > 0 ? null : getRandomInstant());
            instructor.setRegistrationKey(instructorRegistrationKey);

            ofy().save().entities(instructor).now();

            if (i % DEADLINE_EXTENSION_FREQ == 0) { // every n-th instructor has deadline extensions
                seedDeadlineExtensions(courseNumber, courseId, instructorEmail, true);
            }
        }
    }

    private void seedDeadlineExtensions(int courseNumber, String courseId, String userEmail, boolean isInstructor) {
        Random rand = new Random();

        int i = isInstructor ? 1 : 0; // matches if session's questions' giverType are students or instructors
        for (; i < MAX_FEEDBACK_SESSION_FOR_EACH_COURSE_SIZE; i+=2) {
            String feedbackSessionName = String.format("Course %s Feedback Session %s", courseNumber, i);
            boolean sentClosingEmail = rand.nextBoolean();
            Instant endTime = getRandomInstant();

            DeadlineExtension de = new DeadlineExtension(courseId, feedbackSessionName, userEmail,
                    isInstructor, sentClosingEmail, endTime);
            de.setCreatedAt(getRandomInstant());
            de.setUpdatedAt(getRandomInstant());

            ofy().save().entities(de).now();
        }
    }
    
    private void log(String logLine) {
        System.out.println(String.format("Seeding database: %s", logLine));
    }

    /**
     * Persists the data to database.
     */
    protected void persistData() {
        // Persisting basic data bundle
        DataBundle dataBundle = getTypicalDataBundle();
        try {
            // logic.persistDataBundle(dataBundle);
            persistAdditionalData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new SeedDb().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        try {
            // LogicStarter.initializeDependencies();
            this.persistData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

