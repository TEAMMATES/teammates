package teammates.logic.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.ProfilesDb;
import teammates.storage.api.StudentsDb;

/**
 * Handles operations related to data bundles.
 *
 * @see DataBundle
 */
public final class DataBundleLogic {

    private static final AccountsDb accountsDb = new AccountsDb();
    private static final ProfilesDb profilesDb = new ProfilesDb();
    private static final CoursesDb coursesDb = new CoursesDb();
    private static final StudentsDb studentsDb = new StudentsDb();
    private static final InstructorsDb instructorsDb = new InstructorsDb();
    private static final FeedbackSessionsDb fbDb = new FeedbackSessionsDb();
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private static final FeedbackResponseCommentsDb fcDb = new FeedbackResponseCommentsDb();

    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();

    private static DataBundleLogic instance = new DataBundleLogic();

    private DataBundleLogic() {
        // prevent initialization
    }

    public static DataBundleLogic inst() {
        return instance;
    }

    /**
     * Persists data in the given {@link DataBundle} to the Datastore, including
     * accounts, courses, instructors, students, sessions, questions, responses, and comments.
     *
     * <p>Accounts are generated for students and instructors with Google IDs
     * if the corresponding accounts are not found in the data bundle.
     * For question ID injection in responses and comments to work properly, all questions
     * referenced by responses and comments must be included in the data bundle.
     * For session respondent lists to be properly populated, all instructors, questions and responses
     * relevant to each session must be included in the data bundle.</p>
     *
     * @throws InvalidParametersException if invalid data is encountered.
     */
    public DataBundle persistDataBundle(DataBundle dataBundle) throws InvalidParametersException {
        if (dataBundle == null) {
            throw new InvalidParametersException("Null data bundle");
        }

        Collection<AccountAttributes> accounts = dataBundle.accounts.values();
        Collection<StudentProfileAttributes> profiles = dataBundle.profiles.values();
        Collection<CourseAttributes> courses = dataBundle.courses.values();
        Collection<InstructorAttributes> instructors = dataBundle.instructors.values();
        Collection<StudentAttributes> students = dataBundle.students.values();
        Collection<FeedbackSessionAttributes> sessions = dataBundle.feedbackSessions.values();
        Collection<FeedbackQuestionAttributes> questions = dataBundle.feedbackQuestions.values();
        Collection<FeedbackResponseAttributes> responses = dataBundle.feedbackResponses.values();
        Collection<FeedbackResponseCommentAttributes> responseComments = dataBundle.feedbackResponseComments.values();

        // For ensuring only one account per Google ID is created
        Map<String, AccountAttributes> googleIdAccountMap = new HashMap<>();

        // For updating the student and instructor respondent lists in sessions before they are persisted
        SetMultimap<String, InstructorAttributes> courseInstructorsMap = HashMultimap.create();
        SetMultimap<String, FeedbackQuestionAttributes> sessionQuestionsMap = HashMultimap.create();
        SetMultimap<String, FeedbackResponseAttributes> sessionResponsesMap = HashMultimap.create();

        processAccountsAndPopulateAccountsMap(accounts, googleIdAccountMap);
        processInstructorsAndPopulateMapAndAccounts(instructors, courseInstructorsMap, googleIdAccountMap);
        processStudentsAndPopulateAccounts(students, googleIdAccountMap);
        processQuestionsAndPopulateMap(questions, sessionQuestionsMap);
        processResponsesAndPopulateMap(responses, sessionResponsesMap);
        processSessionsAndUpdateRespondents(sessions, courseInstructorsMap, sessionQuestionsMap, sessionResponsesMap);

        List<AccountAttributes> newAccounts = accountsDb.putEntities(googleIdAccountMap.values());

        List<StudentProfileAttributes> newProfiles = profilesDb.putEntities(profiles);
        List<CourseAttributes> newCourses = coursesDb.putEntities(courses);
        List<InstructorAttributes> newInstructors = instructorsDb.putEntities(instructors);
        List<StudentAttributes> newStudents = studentsDb.putEntities(students);
        List<FeedbackSessionAttributes> newFeedbackSessions = fbDb.putEntities(sessions);

        List<FeedbackQuestionAttributes> createdQuestions = fqDb.putEntities(questions);
        injectRealIds(responses, responseComments, createdQuestions);

        List<FeedbackResponseAttributes> newFeedbackResponses = frDb.putEntities(responses);
        List<FeedbackResponseCommentAttributes> newFeedbackResponseComments = fcDb.putEntities(responseComments);

        updateDataBundleAccounts(dataBundle, newAccounts);

        updateDataBundleProfiles(dataBundle, newProfiles);
        updateDataBundleCourses(dataBundle, newCourses);
        updateDataBundleInstructors(dataBundle, newInstructors);

        updateDataBundleStudents(dataBundle, newStudents);
        updateDataBundleFeedbackSessions(dataBundle, newFeedbackSessions);
        updateDataBundleFeedbackQuestions(dataBundle, createdQuestions);
        updateDataBundleFeedbackResponses(dataBundle, newFeedbackResponses);
        updateDataBundleFeedbackResponseComments(dataBundle, newFeedbackResponseComments);

        return dataBundle;

    }

    private void updateDataBundleAccounts(DataBundle dataBundle, List<AccountAttributes> newAccounts) {
        Map<String, AccountAttributes> oldAccounts = dataBundle.accounts;
        Map<AccountAttributes, Integer> newAccountsMap = new HashMap<>();
        Map<String, AccountAttributes> accounts = new LinkedHashMap<>();

        for (int i = 0; i < newAccounts.size(); i++) {
            newAccountsMap.put(newAccounts.get(i), i);
        }

        for (Map.Entry<String, AccountAttributes> entry : oldAccounts.entrySet()) {
            String key = entry.getKey();
            AccountAttributes value = entry.getValue();

            if (newAccountsMap.containsKey(value)) {
                int index = newAccountsMap.get(value);
                accounts.put(key, newAccounts.get(index));
            }
        }
        dataBundle.accounts = accounts;
    }

    private void updateDataBundleProfiles(DataBundle dataBundle, List<StudentProfileAttributes> newProfiles) {
        Map<String, StudentProfileAttributes> oldProfiles = dataBundle.profiles;
        Map<StudentProfileAttributes, Integer> newProfilesMap = new HashMap<>();
        Map<String, StudentProfileAttributes> profiles = new LinkedHashMap<>();

        for (int i = 0; i < newProfiles.size(); i++) {
            newProfilesMap.put(newProfiles.get(i), i);
        }

        for (Map.Entry<String, StudentProfileAttributes> entry : oldProfiles.entrySet()) {
            String key = entry.getKey();
            StudentProfileAttributes value = entry.getValue();
            if (newProfilesMap.containsKey(value)) {
                int index = newProfilesMap.get(value);
                profiles.put(key, newProfiles.get(index));
            }
        }
        dataBundle.profiles = profiles;
    }

    private void updateDataBundleCourses(DataBundle dataBundle, List<CourseAttributes> newCourses) {
        Map<String, CourseAttributes> oldCourses = dataBundle.courses;
        Map<CourseAttributes, Integer> newCoursesMap = new HashMap<>();
        Map<String, CourseAttributes> courses = new LinkedHashMap<>();

        for (int i = 0; i < newCourses.size(); i++) {
            newCoursesMap.put(newCourses.get(i), i);
        }

        for (Map.Entry<String, CourseAttributes> entry : oldCourses.entrySet()) {
            String key = entry.getKey();
            CourseAttributes value = entry.getValue();
            if (newCoursesMap.containsKey(value)) {
                int index = newCoursesMap.get(value);
                courses.put(key, newCourses.get(index));
            }
        }
        dataBundle.courses = courses;
    }

    private void updateDataBundleInstructors(DataBundle dataBundle, List<InstructorAttributes> newInstructors) {
        Map<String, InstructorAttributes> oldInstructors = dataBundle.instructors;
        Map<InstructorAttributes, Integer> newInstructorsMap = new HashMap<>();
        Map<String, InstructorAttributes> instructors = new LinkedHashMap<>();

        for (int i = 0; i < newInstructors.size(); i++) {
            newInstructorsMap.put(newInstructors.get(i), i);
        }

        for (Map.Entry<String, InstructorAttributes> entry : oldInstructors.entrySet()) {
            String key = entry.getKey();
            InstructorAttributes value = entry.getValue();

            if (newInstructorsMap.containsKey(value)) {
                int index = newInstructorsMap.get(value);
                instructors.put(key, newInstructors.get(index));
            }
        }
        dataBundle.instructors = instructors;
    }

    private void updateDataBundleStudents(DataBundle dataBundle, List<StudentAttributes> newStudents) {
        Map<String, StudentAttributes> oldStudents = dataBundle.students;
        Map<StudentAttributes, Integer> newStudentsMap = new HashMap<>();
        Map<String, StudentAttributes> students = new LinkedHashMap<>();

        for (int i = 0; i < newStudents.size(); i++) {
            newStudentsMap.put(newStudents.get(i), i);
        }

        for (Map.Entry<String, StudentAttributes> entry : oldStudents.entrySet()) {
            String key = entry.getKey();
            StudentAttributes value = entry.getValue();

            if (newStudentsMap.containsKey(value)) {
                int index = newStudentsMap.get(value);
                students.put(key, newStudents.get(index));
            }
        }
        dataBundle.students = students;
    }

    private void updateDataBundleFeedbackSessions(DataBundle dataBundle,
            List<FeedbackSessionAttributes> newFeedbackSessions) {
        Map<String, FeedbackSessionAttributes> oldFeedbackSessions = dataBundle.feedbackSessions;
        Map<FeedbackSessionAttributes, Integer> newFeedbackSessionsMap = new HashMap<>();
        Map<String, FeedbackSessionAttributes> feedbackSessions = new LinkedHashMap<>();

        for (int i = 0; i < newFeedbackSessions.size(); i++) {
            newFeedbackSessionsMap.put(newFeedbackSessions.get(i), i);
        }

        for (Map.Entry<String, FeedbackSessionAttributes> entry : oldFeedbackSessions.entrySet()) {
            String key = entry.getKey();
            FeedbackSessionAttributes value = entry.getValue();

            if (newFeedbackSessionsMap.containsKey(value)) {
                int index = newFeedbackSessionsMap.get(value);
                feedbackSessions.put(key, newFeedbackSessions.get(index));
            }
        }
        dataBundle.feedbackSessions = feedbackSessions;
    }

    private void updateDataBundleFeedbackQuestions(DataBundle dataBundle,
                List<FeedbackQuestionAttributes> createdQuestions) {
        Map<String, FeedbackQuestionAttributes> oldFeedbackQuestions = dataBundle.feedbackQuestions;
        Map<FeedbackQuestionAttributes, Integer> newFeedbackQuestionsMap = new HashMap<>();
        Map<String, FeedbackQuestionAttributes> feedbackQuestions = new LinkedHashMap<>();

        for (int i = 0; i < createdQuestions.size(); i++) {
            newFeedbackQuestionsMap.put(createdQuestions.get(i), i);
        }

        for (Map.Entry<String, FeedbackQuestionAttributes> entry : oldFeedbackQuestions.entrySet()) {
            String key = entry.getKey();
            FeedbackQuestionAttributes value = entry.getValue();

            if (newFeedbackQuestionsMap.containsKey(value)) {
                int index = newFeedbackQuestionsMap.get(value);
                feedbackQuestions.put(key, createdQuestions.get(index));
            }
        }
        dataBundle.feedbackQuestions = feedbackQuestions;
    }

    private void updateDataBundleFeedbackResponses(DataBundle dataBundle,
                List<FeedbackResponseAttributes> newFeedbackResponses) {
        Map<String, FeedbackResponseAttributes> oldFeedbackResponses = dataBundle.feedbackResponses;
        Map<FeedbackResponseAttributes, Integer> newFeedbackResponsesMap = new HashMap<>();
        Map<String, FeedbackResponseAttributes> feedbackResponses = new LinkedHashMap<>();

        for (int i = 0; i < newFeedbackResponses.size(); i++) {
            newFeedbackResponsesMap.put(newFeedbackResponses.get(i), i);
        }

        for (Map.Entry<String, FeedbackResponseAttributes> entry : oldFeedbackResponses.entrySet()) {
            String key = entry.getKey();
            FeedbackResponseAttributes value = entry.getValue();

            if (newFeedbackResponsesMap.containsKey(value)) {
                int index = newFeedbackResponsesMap.get(value);
                feedbackResponses.put(key, newFeedbackResponses.get(index));
            }
        }
        dataBundle.feedbackResponses = feedbackResponses;
    }

    private void updateDataBundleFeedbackResponseComments(DataBundle dataBundle,
                List<FeedbackResponseCommentAttributes> newFeedbackResponseComments) {
        Map<String, FeedbackResponseCommentAttributes> oldFeedbackResponseComments =
                dataBundle.feedbackResponseComments;
        Map<FeedbackResponseCommentAttributes, Integer> newFeedbackResponseCommentsMap = new HashMap<>();
        Map<String, FeedbackResponseCommentAttributes> feedbackResponseComments = new LinkedHashMap<>();

        for (int i = 0; i < newFeedbackResponseComments.size(); i++) {
            newFeedbackResponseCommentsMap.put(newFeedbackResponseComments.get(i), i);
        }

        for (Map.Entry<String, FeedbackResponseCommentAttributes> entry : oldFeedbackResponseComments.entrySet()) {
            String key = entry.getKey();
            FeedbackResponseCommentAttributes value = entry.getValue();

            if (newFeedbackResponseCommentsMap.containsKey(value)) {
                int index = newFeedbackResponseCommentsMap.get(value);
                feedbackResponseComments.put(key, newFeedbackResponseComments.get(index));
            }
        }
        dataBundle.feedbackResponseComments = feedbackResponseComments;
    }

    /**
     * Creates document for entities that have document, i.e. searchable.
     */
    public void putDocuments(DataBundle dataBundle) {
        // query the entity in db first to get the actual data and create document for actual entity

        Map<String, StudentAttributes> students = dataBundle.students;
        for (StudentAttributes student : students.values()) {
            StudentAttributes studentInDb = studentsDb.getStudentForEmail(student.course, student.email);
            studentsDb.putDocument(studentInDb);
        }

        Map<String, InstructorAttributes> instructors = dataBundle.instructors;
        for (InstructorAttributes instructor : instructors.values()) {
            InstructorAttributes instructorInDb =
                    instructorsDb.getInstructorForEmail(instructor.courseId, instructor.email);
            instructorsDb.putDocument(instructorInDb);
        }

        Map<String, FeedbackResponseCommentAttributes> responseComments = dataBundle.feedbackResponseComments;
        for (FeedbackResponseCommentAttributes responseComment : responseComments.values()) {
            FeedbackResponseCommentAttributes fcInDb = fcDb.getFeedbackResponseComment(
                    responseComment.courseId, responseComment.createdAt, responseComment.commentGiver);
            fcDb.putDocument(fcInDb);
        }
    }

    private void processAccountsAndPopulateAccountsMap(Collection<AccountAttributes> accounts,
            Map<String, AccountAttributes> googleIdAccountMap) {
        for (AccountAttributes account : accounts) {
            googleIdAccountMap.put(account.googleId, account);
        }
    }

    private void processInstructorsAndPopulateMapAndAccounts(Collection<InstructorAttributes> instructors,
            SetMultimap<String, InstructorAttributes> courseInstructorsMap,
            Map<String, AccountAttributes> googleIdAccountMap) {
        for (InstructorAttributes instructor : instructors) {
            validateInstructorPrivileges(instructor);

            courseInstructorsMap.put(instructor.courseId, instructor);

            if (!StringHelper.isEmpty(instructor.googleId)) {
                googleIdAccountMap.putIfAbsent(instructor.googleId, makeAccount(instructor));
            }
        }
    }

    private void processStudentsAndPopulateAccounts(Collection<StudentAttributes> students,
            Map<String, AccountAttributes> googleIdAccountMap) {
        for (StudentAttributes student : students) {
            populateNullSection(student);

            if (!StringHelper.isEmpty(student.googleId)) {
                googleIdAccountMap.putIfAbsent(student.googleId, makeAccount(student));
            }
        }
    }

    private void processQuestionsAndPopulateMap(Collection<FeedbackQuestionAttributes> questions,
            SetMultimap<String, FeedbackQuestionAttributes> sessionQuestionsMap) {
        for (FeedbackQuestionAttributes question : questions) {
            question.removeIrrelevantVisibilityOptions();

            String sessionKey = makeSessionKey(question.feedbackSessionName, question.courseId);
            sessionQuestionsMap.put(sessionKey, question);
        }
    }

    private void processResponsesAndPopulateMap(Collection<FeedbackResponseAttributes> responses,
            SetMultimap<String, FeedbackResponseAttributes> sessionResponsesMap) {
        for (FeedbackResponseAttributes response : responses) {
            String sessionKey = makeSessionKey(response.feedbackSessionName, response.courseId);
            sessionResponsesMap.put(sessionKey, response);
        }
    }

    private void processSessionsAndUpdateRespondents(Collection<FeedbackSessionAttributes> sessions,
            SetMultimap<String, InstructorAttributes> courseInstructorsMap,
            SetMultimap<String, FeedbackQuestionAttributes> sessionQuestionsMap,
            SetMultimap<String, FeedbackResponseAttributes> sessionResponsesMap) {
        for (FeedbackSessionAttributes session : sessions) {
            String sessionKey = makeSessionKey(session.getFeedbackSessionName(), session.getCourseId());

            Set<InstructorAttributes> courseInstructors = courseInstructorsMap.get(session.getCourseId());
            Set<FeedbackQuestionAttributes> sessionQuestions = sessionQuestionsMap.get(sessionKey);
            Set<FeedbackResponseAttributes> sessionResponses = sessionResponsesMap.get(sessionKey);

            updateRespondents(session, courseInstructors, sessionQuestions, sessionResponses);
        }
    }

    private void updateRespondents(FeedbackSessionAttributes session,
            Set<InstructorAttributes> courseInstructors,
            Set<FeedbackQuestionAttributes> sessionQuestions,
            Set<FeedbackResponseAttributes> sessionResponses) {
        String sessionKey = makeSessionKey(session.getFeedbackSessionName(), session.getCourseId());

        SetMultimap<String, String> instructorQuestionKeysMap = HashMultimap.create();
        for (InstructorAttributes instructor : courseInstructors) {
            List<FeedbackQuestionAttributes> questionsForInstructor =
                    fqLogic.getFeedbackQuestionsForInstructor(
                            new ArrayList<>(sessionQuestions), session.isCreator(instructor.email));

            List<String> questionKeys = makeQuestionKeys(questionsForInstructor, sessionKey);
            instructorQuestionKeysMap.putAll(instructor.email, questionKeys);
        }

        Set<String> respondingInstructors = new HashSet<>();
        Set<String> respondingStudents = new HashSet<>();

        for (FeedbackResponseAttributes response : sessionResponses) {
            String respondent = response.giver;
            String responseQuestionNumber = response.feedbackQuestionId; // contains question number before injection
            String responseQuestionKey = makeQuestionKey(sessionKey, responseQuestionNumber);

            Set<String> instructorQuestionKeys = instructorQuestionKeysMap.get(respondent);
            if (instructorQuestionKeys.contains(responseQuestionKey)) {
                respondingInstructors.add(respondent);
            } else {
                respondingStudents.add(respondent);
            }
        }

        session.setRespondingInstructorList(respondingInstructors);
        session.setRespondingStudentList(respondingStudents);
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
            String sessionKey = makeSessionKey(createdQuestion.feedbackSessionName, createdQuestion.courseId);
            String questionKey = makeQuestionKey(sessionKey, createdQuestion.questionNumber);
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
                questionNumber = Integer.parseInt(response.feedbackQuestionId);
            } catch (NumberFormatException e) {
                // question ID already injected
                continue;
            }
            String sessionKey = makeSessionKey(response.feedbackSessionName, response.courseId);
            String questionKey = makeQuestionKey(sessionKey, questionNumber);
            response.feedbackQuestionId = questionIdMap.get(questionKey);
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
                questionNumber = Integer.parseInt(comment.feedbackQuestionId);
            } catch (NumberFormatException e) {
                // question ID already injected
                continue;
            }
            String sessionKey = makeSessionKey(comment.feedbackSessionName, comment.courseId);
            String questionKey = makeQuestionKey(sessionKey, questionNumber);
            comment.feedbackQuestionId = questionIdMap.get(questionKey);

            // format of feedbackResponseId: questionNumber%giverEmail%recipient
            String[] responseIdParam = comment.feedbackResponseId.split("%", 3);
            comment.feedbackResponseId = comment.feedbackQuestionId + "%" + responseIdParam[1] + "%" + responseIdParam[2];
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

        InstructorPrivileges privileges = instructor.privileges;

        switch (instructor.getRole()) {

        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER:
            Assumption.assertTrue(privileges.hasCoownerPrivileges());
            break;

        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER:
            Assumption.assertTrue(privileges.hasManagerPrivileges());
            break;

        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER:
            Assumption.assertTrue(privileges.hasObserverPrivileges());
            break;

        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR:
            Assumption.assertTrue(privileges.hasTutorPrivileges());
            break;

        case Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM:
            break;

        default:
            Assumption.fail("Invalid instructor permission role name");
            break;
        }
    }

    private void populateNullSection(StudentAttributes student) {
        student.section = student.section == null ? "None" : student.section;
    }

    private AccountAttributes makeAccount(InstructorAttributes instructor) {
        return AccountAttributes.builder(instructor.googleId)
                .withName(instructor.name)
                .withEmail(instructor.email)
                .withInstitute("TEAMMATES Test Institute 1")
                .withIsInstructor(true)
                .build();
    }

    private AccountAttributes makeAccount(StudentAttributes student) {
        return AccountAttributes.builder(student.googleId)
                .withName(student.name)
                .withEmail(student.email)
                .withInstitute("TEAMMATES Test Institute 1")
                .withIsInstructor(false)
                .build();
    }

    private String makeSessionKey(String feedbackSessionName, String courseId) {
        return feedbackSessionName + "%" + courseId;
    }

    private List<String> makeQuestionKeys(List<FeedbackQuestionAttributes> questions, String sessionKey) {
        List<String> questionKeys = new ArrayList<>();
        for (FeedbackQuestionAttributes question : questions) {
            String questionKey = makeQuestionKey(sessionKey, question.questionNumber);
            questionKeys.add(questionKey);
        }
        return questionKeys;
    }

    private String makeQuestionKey(String sessionKey, int questionNumber) {
        return makeQuestionKey(sessionKey, String.valueOf(questionNumber));
    }

    private String makeQuestionKey(String sessionKey, String questionNumber) {
        return sessionKey + "%" + questionNumber;
    }

    public void removeDataBundle(DataBundle dataBundle) {

        // Questions and responses will be deleted automatically.
        // We don't attempt to delete them again, to save time.
        deleteCourses(dataBundle.courses.values());

        dataBundle.accounts.values().forEach(account -> {
            accountsDb.deleteAccount(account.getGoogleId());
        });
        dataBundle.profiles.values().forEach(profile -> {
            profilesDb.deleteStudentProfile(profile.googleId);
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

                coursesDb.deleteCourse(courseId);
            });
        }
    }

}
