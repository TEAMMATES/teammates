package teammates.logic.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.api.AccountRequestsDb;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.DeadlineExtensionsDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.NotificationsDb;
import teammates.storage.api.StudentsDb;

/**
 * Handles operations related to data bundles.
 *
 * @see DataBundle
 */
public final class DataBundleLogic {

    private static final DataBundleLogic instance = new DataBundleLogic();

    private final AccountsDb accountsDb = AccountsDb.inst();
    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();
    private final CoursesDb coursesDb = CoursesDb.inst();
    private final DeadlineExtensionsDb deadlineExtensionsDb = DeadlineExtensionsDb.inst();
    private final StudentsDb studentsDb = StudentsDb.inst();
    private final InstructorsDb instructorsDb = InstructorsDb.inst();
    private final FeedbackSessionsDb fbDb = FeedbackSessionsDb.inst();
    private final FeedbackQuestionsDb fqDb = FeedbackQuestionsDb.inst();
    private final FeedbackResponsesDb frDb = FeedbackResponsesDb.inst();
    private final FeedbackResponseCommentsDb fcDb = FeedbackResponseCommentsDb.inst();
    private final NotificationsDb nfDb = NotificationsDb.inst();

    private DataBundleLogic() {
        // prevent initialization
    }

    public static DataBundleLogic inst() {
        return instance;
    }

    /**
     * Persists data in the given {@link DataBundle} to the database, including
     * accounts, account requests, courses, deadline extensions, instructors, students, sessions,
     * questions, responses, and comments.
     *
     * <p>Accounts are generated for students and instructors with Google IDs
     * if the corresponding accounts are not found in the data bundle.
     * For question ID injection in responses and comments to work properly, all questions
     * referenced by responses and comments must be included in the data bundle.
     *
     * @throws InvalidParametersException if invalid data is encountered.
     */
    public DataBundle persistDataBundle(DataBundle dataBundle) throws InvalidParametersException {
        if (dataBundle == null) {
            throw new InvalidParametersException("Null data bundle");
        }

        Collection<AccountAttributes> accounts = dataBundle.accounts.values();
        Collection<AccountRequestAttributes> accountRequests = dataBundle.accountRequests.values();
        Collection<CourseAttributes> courses = dataBundle.courses.values();
        Collection<InstructorAttributes> instructors = dataBundle.instructors.values();
        Collection<StudentAttributes> students = dataBundle.students.values();
        Collection<FeedbackSessionAttributes> sessions = dataBundle.feedbackSessions.values();
        Collection<FeedbackQuestionAttributes> questions = dataBundle.feedbackQuestions.values();
        Collection<FeedbackResponseAttributes> responses = dataBundle.feedbackResponses.values();
        Collection<FeedbackResponseCommentAttributes> responseComments = dataBundle.feedbackResponseComments.values();
        Collection<DeadlineExtensionAttributes> deadlineExtensions = dataBundle.deadlineExtensions.values();
        Collection<NotificationAttributes> notifications = dataBundle.notifications.values();

        // For ensuring only one account per Google ID is created
        Map<String, AccountAttributes> googleIdAccountMap = new HashMap<>();
        for (AccountAttributes account : accounts) {
            googleIdAccountMap.put(account.getGoogleId(), account);
        }

        processInstructors(instructors, googleIdAccountMap);
        processStudents(students, googleIdAccountMap);
        processQuestions(questions);

        List<AccountAttributes> newAccounts = accountsDb.putEntities(googleIdAccountMap.values());
        List<AccountRequestAttributes> newAccountRequests = accountRequestsDb.putEntities(accountRequests);

        List<CourseAttributes> newCourses = coursesDb.putEntities(courses);
        List<InstructorAttributes> newInstructors = instructorsDb.putEntities(instructors);
        List<StudentAttributes> newStudents = studentsDb.putEntities(students);
        List<FeedbackSessionAttributes> newFeedbackSessions = fbDb.putEntities(sessions);
        List<DeadlineExtensionAttributes> newDeadlineExtensions = deadlineExtensionsDb.putEntities(deadlineExtensions);

        List<FeedbackQuestionAttributes> createdQuestions = fqDb.putEntities(questions);
        injectRealIds(responses, responseComments, createdQuestions);

        List<FeedbackResponseAttributes> newFeedbackResponses = frDb.putEntities(responses);
        List<FeedbackResponseCommentAttributes> newFeedbackResponseComments = fcDb.putEntities(responseComments);
        List<NotificationAttributes> newNotifications = nfDb.putEntities(notifications);

        updateDataBundleValue(newAccounts, dataBundle.accounts);
        updateDataBundleValue(newAccountRequests, dataBundle.accountRequests);
        updateDataBundleValue(newCourses, dataBundle.courses);
        updateDataBundleValue(newDeadlineExtensions, dataBundle.deadlineExtensions);
        updateDataBundleValue(newInstructors, dataBundle.instructors);
        updateDataBundleValue(newStudents, dataBundle.students);
        updateDataBundleValue(newFeedbackSessions, dataBundle.feedbackSessions);
        updateDataBundleValue(createdQuestions, dataBundle.feedbackQuestions);
        updateDataBundleValue(newFeedbackResponses, dataBundle.feedbackResponses);
        updateDataBundleValue(newFeedbackResponseComments, dataBundle.feedbackResponseComments);
        updateDataBundleValue(newNotifications, dataBundle.notifications);

        return dataBundle;

    }

    private <T extends EntityAttributes<?>> void updateDataBundleValue(List<T> newValues, Map<String, T> oldValues) {
        Map<T, Integer> newValuesMap = new HashMap<>();
        Map<String, T> values = new LinkedHashMap<>();

        for (int i = 0; i < newValues.size(); i++) {
            newValuesMap.put(newValues.get(i), i);
        }

        for (Map.Entry<String, T> entry : oldValues.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue();

            if (newValuesMap.containsKey(value)) {
                int index = newValuesMap.get(value);
                values.put(key, newValues.get(index));
            }
        }

        oldValues.clear();
        oldValues.putAll(values);
    }

    /**
     * Creates document for entities that have document, i.e. searchable.
     */
    public void putDocuments(DataBundle dataBundle) throws SearchServiceException {
        // query the entity in db first to get the actual data and create document for actual entity

        Map<String, StudentAttributes> students = dataBundle.students;
        for (StudentAttributes student : students.values()) {
            StudentAttributes studentInDb = studentsDb.getStudentForEmail(student.getCourse(), student.getEmail());
            studentsDb.putDocument(studentInDb);
        }

        Map<String, InstructorAttributes> instructors = dataBundle.instructors;
        for (InstructorAttributes instructor : instructors.values()) {
            InstructorAttributes instructorInDb =
                    instructorsDb.getInstructorForEmail(instructor.getCourseId(), instructor.getEmail());
            instructorsDb.putDocument(instructorInDb);
        }

        Map<String, AccountRequestAttributes> accountRequests = dataBundle.accountRequests;
        for (AccountRequestAttributes accountRequest : accountRequests.values()) {
            AccountRequestAttributes accountRequestInDb =
                    accountRequestsDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
            accountRequestsDb.putDocument(accountRequestInDb);
        }
    }

    private void processInstructors(
            Collection<InstructorAttributes> instructors, Map<String, AccountAttributes> googleIdAccountMap) {
        for (InstructorAttributes instructor : instructors) {
            validateInstructorPrivileges(instructor);

            // create adhoc account to maintain data integrity
            if (!StringHelper.isEmpty(instructor.getGoogleId())) {
                googleIdAccountMap.putIfAbsent(instructor.getGoogleId(), makeAccount(instructor));
            }
        }
    }

    private void processStudents(
            Collection<StudentAttributes> students, Map<String, AccountAttributes> googleIdAccountMap) {
        for (StudentAttributes student : students) {
            populateNullSection(student);

            // create adhoc account to maintain data integrity
            if (!StringHelper.isEmpty(student.getGoogleId())) {
                googleIdAccountMap.putIfAbsent(student.getGoogleId(), makeAccount(student));
            }
        }
    }

    private void processQuestions(Collection<FeedbackQuestionAttributes> questions) {
        for (FeedbackQuestionAttributes question : questions) {
            question.removeIrrelevantVisibilityOptions();
        }
    }

    private void injectRealIds(
            Collection<FeedbackResponseAttributes> responses, Collection<FeedbackResponseCommentAttributes> responseComments,
            List<FeedbackQuestionAttributes> createdQuestions) {
        Map<String, String> questionIdMap = makeQuestionIdMap(createdQuestions);

        injectRealIdsIntoResponses(responses, questionIdMap);
        injectRealIdsIntoResponseComments(responseComments, questionIdMap);
    }

    private Map<String, String> makeQuestionIdMap(List<FeedbackQuestionAttributes> createdQuestions) {
        Map<String, String> questionIdMap = new HashMap<>();
        for (FeedbackQuestionAttributes createdQuestion : createdQuestions) {
            String sessionKey = makeSessionKey(createdQuestion.getFeedbackSessionName(), createdQuestion.getCourseId());
            String questionKey = makeQuestionKey(sessionKey, createdQuestion.getQuestionNumber());
            questionIdMap.put(questionKey, createdQuestion.getId());
        }
        return questionIdMap;
    }

    /**
     * This method is necessary to generate the feedbackQuestionId of the
     * question the response is for.<br>
     * Normally, the ID is already generated on creation,
     * but the json file does not contain the actual response ID. <br>
     * Therefore the question number corresponding to the created response
     * should be inserted in the json file in place of the actual response ID.<br>
     * This method will then generate the correct ID and replace the field.
     */
    private void injectRealIdsIntoResponses(Collection<FeedbackResponseAttributes> responses,
            Map<String, String> questionIdMap) {
        for (FeedbackResponseAttributes response : responses) {
            int questionNumber;
            try {
                questionNumber = Integer.parseInt(response.getFeedbackQuestionId());
            } catch (NumberFormatException e) {
                // question ID already injected
                continue;
            }
            String sessionKey = makeSessionKey(response.getFeedbackSessionName(), response.getCourseId());
            String questionKey = makeQuestionKey(sessionKey, questionNumber);
            response.setFeedbackQuestionId(questionIdMap.get(questionKey));
        }
    }

    /**
     * This method is necessary to generate the feedbackQuestionId
     * and feedbackResponseId of the question and response the comment is for.<br>
     * Normally, the ID is already generated on creation,
     * but the json file does not contain the actual response ID. <br>
     * Therefore the question number and questionNumber%giverEmail%recipient
     * corresponding to the created comment should be inserted in the json
     * file in place of the actual ID.<br>
     * This method will then generate the correct ID and replace the field.
     */
    private void injectRealIdsIntoResponseComments(Collection<FeedbackResponseCommentAttributes> responseComments,
            Map<String, String> questionIdMap) {
        for (FeedbackResponseCommentAttributes comment : responseComments) {
            int questionNumber;
            try {
                questionNumber = Integer.parseInt(comment.getFeedbackQuestionId());
            } catch (NumberFormatException e) {
                // question ID already injected
                continue;
            }
            String sessionKey = makeSessionKey(comment.getFeedbackSessionName(), comment.getCourseId());
            String questionKey = makeQuestionKey(sessionKey, questionNumber);
            comment.setFeedbackQuestionId(questionIdMap.get(questionKey));

            // format of feedbackResponseId: questionNumber%giverEmail%recipient
            String[] responseIdParam = comment.getFeedbackResponseId().split("%", 3);
            comment.setFeedbackResponseId(comment.getFeedbackQuestionId() + "%" + responseIdParam[1]
                    + "%" + responseIdParam[2]);
        }
    }

    /**
     * Checks if the role of {@code instructor} matches its privileges.
     *
     * @param instructor
     *            the {@link InstructorAttributes} of an instructor, cannot be
     *            {@code null}
     */
    private void validateInstructorPrivileges(InstructorAttributes instructor) {

        if (instructor.getRole() == null) {
            return;
        }

        InstructorPrivileges privileges = instructor.getPrivileges();

        switch (instructor.getRole()) {

        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER:
            assert privileges.hasCoownerPrivileges();
            break;

        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER:
            assert privileges.hasManagerPrivileges();
            break;

        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER:
            assert privileges.hasObserverPrivileges();
            break;

        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR:
            assert privileges.hasTutorPrivileges();
            break;

        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM:
            break;

        default:
            assert false : "Invalid instructor permission role name";
            break;
        }
    }

    private void populateNullSection(StudentAttributes student) {
        student.setSection(student.getSection() == null ? "None" : student.getSection());
    }

    private AccountAttributes makeAccount(InstructorAttributes instructor) {
        return AccountAttributes.builder(instructor.getGoogleId())
                .withName(instructor.getName())
                .withEmail(instructor.getEmail())
                .build();
    }

    private AccountAttributes makeAccount(StudentAttributes student) {
        return AccountAttributes.builder(student.getGoogleId())
                .withName(student.getName())
                .withEmail(student.getEmail())
                .build();
    }

    private String makeSessionKey(String feedbackSessionName, String courseId) {
        return feedbackSessionName + "%" + courseId;
    }

    private String makeQuestionKey(String sessionKey, int questionNumber) {
        return makeQuestionKey(sessionKey, String.valueOf(questionNumber));
    }

    private String makeQuestionKey(String sessionKey, String questionNumber) {
        return sessionKey + "%" + questionNumber;
    }

    /**
     * Removes the items in the data bundle from the database.
     */
    public void removeDataBundle(DataBundle dataBundle) {

        // Questions, responses and deadline extensions will be deleted automatically.
        // We don't attempt to delete them again, to save time.
        deleteCourses(dataBundle.courses.values());

        dataBundle.accounts.values().forEach(account -> {
            accountsDb.deleteAccount(account.getGoogleId());
        });
        dataBundle.accountRequests.values().forEach(accountRequest -> {
            accountRequestsDb.deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        });
        dataBundle.notifications.values().forEach(notification -> {
            nfDb.deleteNotification(notification.getNotificationId());
        });
    }

    private void deleteCourses(Collection<CourseAttributes> courses) {
        List<String> courseIds = new ArrayList<>();
        for (CourseAttributes course : courses) {
            courseIds.add(course.getId());
        }
        if (!courseIds.isEmpty()) {
            courseIds.forEach(courseId -> {
                AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                        .withCourseId(courseId)
                        .build();
                fcDb.deleteFeedbackResponseComments(query);
                frDb.deleteFeedbackResponses(query);
                fqDb.deleteFeedbackQuestions(query);
                fbDb.deleteFeedbackSessions(query);
                studentsDb.deleteStudents(query);
                instructorsDb.deleteInstructors(query);
                deadlineExtensionsDb.deleteDeadlineExtensions(query);

                coursesDb.deleteCourse(courseId);
            });
        }
    }

}
