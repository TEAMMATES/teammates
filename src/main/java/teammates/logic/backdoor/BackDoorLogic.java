package teammates.logic.backdoor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.logic.api.Logic;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.AdminEmailsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.EntitiesDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;

/**
 * Provides additional business logic for non-production usage (e.g. testing, client scripts).
 */
public class BackDoorLogic extends Logic {
    private static final AccountsDb accountsDb = new AccountsDb();
    private static final CoursesDb coursesDb = new CoursesDb();
    private static final StudentsDb studentsDb = new StudentsDb();
    private static final InstructorsDb instructorsDb = new InstructorsDb();
    private static final FeedbackSessionsDb fbDb = new FeedbackSessionsDb();
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private static final FeedbackResponseCommentsDb fcDb = new FeedbackResponseCommentsDb();
    private static final AdminEmailsDb adminEmailsDb = new AdminEmailsDb();

    /**
     * Persists data in the given {@link DataBundle} to the Datastore, including
     * accounts, courses, instructors, students, sessions, questions, responses, comments and admin emails.
     *
     * <p>Accounts are generated for students and instructors with Google IDs
     * if the corresponding accounts are not found in the data bundle.
     * For question ID injection in responses and comments to work properly, all questions
     * referenced by responses and comments must be included in the data bundle.
     * For session respondent lists to be properly populated, all instructors, questions and responses
     * relevant to each session must be included in the data bundle.</p>
     *
     * @return {@link Const.StatusCodes#BACKDOOR_STATUS_SUCCESS} if successful.
     * @throws InvalidParametersException if invalid data is encountered.
     */
    public String persistDataBundle(DataBundle dataBundle) throws InvalidParametersException {
        if (dataBundle == null) {
            throw new InvalidParametersException(Const.StatusCodes.NULL_PARAMETER, "Null data bundle");
        }

        Collection<AccountAttributes> accounts = dataBundle.accounts.values();
        Collection<CourseAttributes> courses = dataBundle.courses.values();
        Collection<InstructorAttributes> instructors = dataBundle.instructors.values();
        Collection<StudentAttributes> students = dataBundle.students.values();
        Collection<FeedbackSessionAttributes> sessions = dataBundle.feedbackSessions.values();
        Collection<FeedbackQuestionAttributes> questions = dataBundle.feedbackQuestions.values();
        Collection<FeedbackResponseAttributes> responses = dataBundle.feedbackResponses.values();
        Collection<FeedbackResponseCommentAttributes> responseComments = dataBundle.feedbackResponseComments.values();
        Collection<AdminEmailAttributes> adminEmails = dataBundle.adminEmails.values();

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

        accountsDb.createEntitiesDeferred(googleIdAccountMap.values());
        coursesDb.createEntitiesDeferred(courses);
        instructorsDb.createEntitiesDeferred(instructors);
        studentsDb.createEntitiesDeferred(students);
        fbDb.createEntitiesDeferred(sessions);

        // This also flushes all previously deferred operations
        List<FeedbackQuestionAttributes> createdQuestions = fqDb.createFeedbackQuestionsWithoutExistenceCheck(questions);

        injectRealIds(responses, responseComments, createdQuestions);

        frDb.createEntitiesDeferred(responses);
        fcDb.createEntitiesDeferred(responseComments);

        adminEmailsDb.createEntitiesDeferred(adminEmails);

        EntitiesDb.flush();

        return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
    }

    public String createFeedbackResponseAndUpdateSessionRespondents(FeedbackResponseAttributes response)
            throws InvalidParametersException, EntityDoesNotExistException {
        try {
            int questionNumber = Integer.parseInt(response.feedbackQuestionId);
            response.feedbackQuestionId = feedbackQuestionsLogic
                    .getFeedbackQuestion(response.feedbackSessionName, response.courseId, questionNumber)
                    .getId(); // inject real question ID
        } catch (NumberFormatException e) {
            // question ID already injected
        }
        frDb.createEntityWithoutExistenceCheck(response);
        updateRespondents(response.feedbackSessionName, response.courseId);
        return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
    }

    /**
     * Creates document for entities that have document, i.e. searchable.
     * @return status of the request in the form 'status meassage'+'additional
     *         info (if any)' e.g., "[BACKEND_STATUS_SUCCESS]" e.g.,
     *         "[BACKEND_STATUS_FAILURE]NullPointerException at ..."
     */
    public String putDocuments(DataBundle dataBundle) {
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
                    responseComment.courseId, responseComment.createdAt, responseComment.giverEmail);
            fcDb.putDocument(fcInDb);
        }

        return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
    }

    public String getAccountAsJson(String googleId) {
        AccountAttributes accountData = getAccount(googleId, true);
        return JsonUtils.toJson(accountData);
    }

    public String getStudentProfileAsJson(String googleId) {
        StudentProfileAttributes profileData = getStudentProfile(googleId);
        return JsonUtils.toJson(profileData);
    }

    public String getInstructorAsJsonById(String instructorId, String courseId) {
        InstructorAttributes instructorData = getInstructorForGoogleId(courseId, instructorId);
        return JsonUtils.toJson(instructorData);
    }

    public String getInstructorAsJsonByEmail(String instructorEmail, String courseId) {
        InstructorAttributes instructorData = getInstructorForEmail(courseId, instructorEmail);
        return JsonUtils.toJson(instructorData);
    }

    public String getCourseAsJson(String courseId) {
        CourseAttributes course = getCourse(courseId);
        return JsonUtils.toJson(course);
    }

    public String getStudentAsJson(String courseId, String email) {
        StudentAttributes student = getStudentForEmail(courseId, email);
        return JsonUtils.toJson(student);
    }

    public String getAllStudentsAsJson(String courseId) {
        List<StudentAttributes> studentList = studentsLogic.getStudentsForCourse(courseId);
        return JsonUtils.toJson(studentList);
    }

    public String getFeedbackSessionAsJson(String feedbackSessionName, String courseId) {
        FeedbackSessionAttributes fs = getFeedbackSession(feedbackSessionName, courseId);
        return JsonUtils.toJson(fs);
    }

    public String getFeedbackQuestionAsJson(String feedbackSessionName, String courseId, int qnNumber) {
        FeedbackQuestionAttributes fq =
                feedbackQuestionsLogic.getFeedbackQuestion(feedbackSessionName, courseId, qnNumber);
        return JsonUtils.toJson(fq);
    }

    public String getFeedbackQuestionForIdAsJson(String questionId) {
        FeedbackQuestionAttributes fq = feedbackQuestionsLogic.getFeedbackQuestion(questionId);
        return JsonUtils.toJson(fq);
    }

    public String getFeedbackResponseAsJson(String feedbackQuestionId, String giverEmail, String recipient) {
        FeedbackResponseAttributes fq =
                feedbackResponsesLogic.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
        return JsonUtils.toJson(fq);
    }

    public String getFeedbackResponsesForGiverAsJson(String courseId, String giverEmail) {
        List<FeedbackResponseAttributes> responseList =
                feedbackResponsesLogic.getFeedbackResponsesFromGiverForCourse(courseId, giverEmail);
        return JsonUtils.toJson(responseList);
    }

    public String getFeedbackResponsesForReceiverAsJson(String courseId, String recipient) {
        List<FeedbackResponseAttributes> responseList =
                feedbackResponsesLogic.getFeedbackResponsesForReceiverForCourse(courseId, recipient);
        return JsonUtils.toJson(responseList);
    }

    public void editAccountAsJson(String newValues)
            throws InvalidParametersException, EntityDoesNotExistException {
        AccountAttributes account = JsonUtils.fromJson(newValues, AccountAttributes.class);
        updateAccount(account);
    }

    public void editCourseAsJson(String newValues)
            throws InvalidParametersException, EntityDoesNotExistException {
        CourseAttributes course = JsonUtils.fromJson(newValues, CourseAttributes.class);
        updateCourse(course.getId(), course.getName(), course.getTimeZone().getId());
    }

    public void editStudentAsJson(String originalEmail, String newValues)
            throws InvalidParametersException, EntityDoesNotExistException {
        StudentAttributes student = JsonUtils.fromJson(newValues, StudentAttributes.class);
        populateNullSection(student);
        updateStudentWithoutDocument(originalEmail, student);
    }

    public void editFeedbackSessionAsJson(String feedbackSessionJson)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackSessionAttributes feedbackSession =
                JsonUtils.fromJson(feedbackSessionJson, FeedbackSessionAttributes.class);
        updateFeedbackSession(feedbackSession);
    }

    public void editFeedbackQuestionAsJson(String feedbackQuestionJson)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackQuestionAttributes feedbackQuestion =
                JsonUtils.fromJson(feedbackQuestionJson, FeedbackQuestionAttributes.class);
        updateFeedbackQuestion(feedbackQuestion);
    }

    private void processAccountsAndPopulateAccountsMap(Collection<AccountAttributes> accounts,
            Map<String, AccountAttributes> googleIdAccountMap) {
        populateNullStudentProfiles(accounts);
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

            if (StringHelper.isEmpty(instructor.googleId) || googleIdAccountMap.containsKey(instructor.googleId)) {
                // No account, or account already exists in the data bundle
                continue;
            }
            googleIdAccountMap.put(instructor.googleId, makeAccount(instructor));
        }
    }

    private void processStudentsAndPopulateAccounts(Collection<StudentAttributes> students,
            Map<String, AccountAttributes> googleIdAccountMap) {
        for (StudentAttributes student : students) {
            populateNullSection(student);

            if (StringHelper.isEmpty(student.googleId) || googleIdAccountMap.containsKey(student.googleId)) {
                // No account, account already exists in the data bundle,
                // or instructor account already exists (i.e. instructor is also a student)
                continue;
            }
            googleIdAccountMap.put(student.googleId, makeAccount(student));
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
                    feedbackQuestionsLogic.getFeedbackQuestionsForInstructor(
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
    **/
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
    **/
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

            String[] responseIdParam = comment.feedbackResponseId.split("%");
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

    private void populateNullStudentProfiles(Collection<AccountAttributes> accounts) {
        for (AccountAttributes account : accounts) {
            if (account.studentProfile == null) {
                account.studentProfile = StudentProfileAttributes.builder(account.googleId).build();
            }
        }
    }

    private void populateNullSection(StudentAttributes student) {
        student.section = student.section == null ? "None" : student.section;
    }

    private AccountAttributes makeAccount(InstructorAttributes instructor) {
        return AccountAttributes.builder()
                .withGoogleId(instructor.googleId)
                .withName(instructor.name)
                .withEmail(instructor.email)
                .withInstitute("TEAMMATES Test Institute 1")
                .withIsInstructor(true)
                .withDefaultStudentProfileAttributes(instructor.googleId)
                .build();
    }

    private AccountAttributes makeAccount(StudentAttributes student) {
        return AccountAttributes.builder()
                .withGoogleId(student.googleId)
                .withName(student.name)
                .withEmail(student.email)
                .withInstitute("TEAMMATES Test Institute 1")
                .withIsInstructor(false)
                .withDefaultStudentProfileAttributes(student.googleId)
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

        populateNullStudentProfiles(dataBundle.accounts.values());
        accountsDb.deleteAccounts(dataBundle.accounts.values());

        for (AdminEmailAttributes email : dataBundle.adminEmails.values()) {
            // Retrieve email by subject as fields emailId, createDate cannot be specified by dataBundle.
            AdminEmailAttributes emailInDb = adminEmailsDb.getAdminEmailBySubject(email.subject);
            // It is expected that email may not be in datastore yet, should fail silently.
            if (emailInDb == null) {
                continue;
            }
            adminEmailsDb.deleteEntity(emailInDb);
        }
    }

    private void deleteCourses(Collection<CourseAttributes> courses) {
        List<String> courseIds = new ArrayList<>();
        for (CourseAttributes course : courses) {
            courseIds.add(course.getId());
        }
        if (!courseIds.isEmpty()) {
            coursesDb.deleteEntities(courses);
            instructorsDb.deleteInstructorsForCourses(courseIds);
            studentsDb.deleteStudentsForCourses(courseIds);
            fbDb.deleteFeedbackSessionsForCourses(courseIds);
            fqDb.deleteFeedbackQuestionsForCourses(courseIds);
            frDb.deleteFeedbackResponsesForCourses(courseIds);
            fcDb.deleteFeedbackResponseCommentsForCourses(courseIds);
        }
    }

    public boolean isPicturePresentInGcs(String pictureKey) {
        return GoogleCloudStorageHelper.doesFileExistInGcs(new BlobKey(pictureKey));
    }

    public void uploadAndUpdateStudentProfilePicture(String googleId,
            byte[] pictureData) throws EntityDoesNotExistException, IOException {
        String pictureKey = GoogleCloudStorageHelper.writeImageDataToGcs(googleId, pictureData);
        updateStudentProfilePicture(googleId, pictureKey);
    }

    public boolean isGroupListFilePresentInGcs(String groupListKey) {
        return GoogleCloudStorageHelper.doesFileExistInGcs(new BlobKey(groupListKey));
    }

    public void deleteGroupListFile(String groupListFileKey) {
        GoogleCloudStorageHelper.deleteFile(new BlobKey(groupListFileKey));
    }
}
