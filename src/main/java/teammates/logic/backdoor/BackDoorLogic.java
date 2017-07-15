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

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionType;
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
     * Persists given data in the datastore Works ONLY if the data is correct.
     *  //Any existing copies of the data in the datastore will be overwritten.
     *      - edit: use removeDataBundle to remove.
     *              made this change for speed when deletion is not necessary.
     * @return status of the request in the form 'status meassage'+'additional
     *         info (if any)' e.g., "[BACKEND_STATUS_SUCCESS]" e.g.,
     *         "[BACKEND_STATUS_FAILURE]NullPointerException at ..."
     */
    public String persistDataBundle(DataBundle dataBundle) throws InvalidParametersException {
        if (dataBundle == null) {
            throw new InvalidParametersException(Const.StatusCodes.NULL_PARAMETER, "Null data bundle");
        }

        Map<String, CourseAttributes> courses = dataBundle.courses;
        coursesDb.createEntitiesDeferred(courses.values());

        Map<String, InstructorAttributes> instructors = dataBundle.instructors;
        Map<String, List<InstructorAttributes>> courseInstructorsMap = new HashMap<>();
        List<AccountAttributes> instructorAccounts = new ArrayList<>();
        for (InstructorAttributes instructor : instructors.values()) {
            validateInstructorPrivileges(instructor);

            if (!courseInstructorsMap.containsKey(instructor.courseId)) {
                courseInstructorsMap.put(instructor.courseId, new ArrayList<InstructorAttributes>());
            }
            courseInstructorsMap.get(instructor.courseId).add(instructor);

            if (instructor.googleId == null || instructor.googleId.isEmpty()) {
                continue;
            }

            AccountAttributes account = new AccountAttributes(
                    instructor.googleId, instructor.name, true, instructor.email, "TEAMMATES Test Institute 1");
            instructorAccounts.add(account);
        }
        accountsDb.createAccountsDeferred(instructorAccounts);
        instructorsDb.createEntitiesDeferred(instructors.values());

        Map<String, StudentAttributes> students = dataBundle.students;
        List<AccountAttributes> studentAccounts = new ArrayList<>();
        for (StudentAttributes student : students.values()) {
            student.section = student.section == null ? "None" : student.section;

            if (student.googleId == null || student.googleId.isEmpty()) {
                continue;
            }

            AccountAttributes account = new AccountAttributes(
                    student.googleId, student.name, false, student.email, "TEAMMATES Test Institute 1");
            studentAccounts.add(account);
        }
        accountsDb.createAccountsDeferred(studentAccounts);
        studentsDb.createEntitiesDeferred(students.values());

        Map<String, AccountAttributes> accounts = dataBundle.accounts;
        for (AccountAttributes account : accounts.values()) {
            if (account.studentProfile == null) {
                account.studentProfile = StudentProfileAttributes.builder().build();
                account.studentProfile.googleId = account.googleId;
            }
        }
        accountsDb.createAccountsDeferred(accounts.values());

        Map<String, FeedbackQuestionAttributes> questions = dataBundle.feedbackQuestions;
        Map<String, List<FeedbackQuestionAttributes>> sessionQuestionsMap = new HashMap<>();
        for (FeedbackQuestionAttributes question : questions.values()) {
            question.removeIrrelevantVisibilityOptions();

            String sessionKey = makeSessionKey(question.feedbackSessionName, question.courseId);
            if (!sessionQuestionsMap.containsKey(sessionKey)) {
                sessionQuestionsMap.put(sessionKey, new ArrayList<FeedbackQuestionAttributes>());
            }
            sessionQuestionsMap.get(sessionKey).add(question);
        }

        Map<String, FeedbackResponseAttributes> responses = dataBundle.feedbackResponses;
        Map<String, List<FeedbackResponseAttributes>> sessionResponsesMap = new HashMap<>();
        for (FeedbackResponseAttributes response : responses.values()) {
            String sessionKey = makeSessionKey(response.feedbackSessionName, response.courseId);
            if (!sessionResponsesMap.containsKey(sessionKey)) {
                sessionResponsesMap.put(sessionKey, new ArrayList<FeedbackResponseAttributes>());
            }
            sessionResponsesMap.get(sessionKey).add(response);
        }

        Map<String, FeedbackSessionAttributes> sessions = dataBundle.feedbackSessions;
        for (FeedbackSessionAttributes session : sessions.values()) {
            cleanSessionData(session);
            String sessionKey = makeSessionKey(session.getFeedbackSessionName(), session.getCourseId());

            List<InstructorAttributes> courseInstructors = courseInstructorsMap.get(session.getCourseId());
            List<FeedbackQuestionAttributes> sessionQuestions = sessionQuestionsMap.get(sessionKey);
            List<FeedbackResponseAttributes> sessionResponses = sessionResponsesMap.get(sessionKey);

            courseInstructors = courseInstructors == null ? new ArrayList<InstructorAttributes>() : courseInstructors;
            sessionQuestions = sessionQuestions == null ? new ArrayList<FeedbackQuestionAttributes>() : sessionQuestions;
            sessionResponses = sessionResponses == null ? new ArrayList<FeedbackResponseAttributes>() : sessionResponses;

            updateRespondents(session, courseInstructors, sessionQuestions, sessionResponses);
        }
        fbDb.createEntitiesDeferred(sessions.values());

        // This also flushes all previously deferred operations
        List<FeedbackQuestionAttributes> createdQuestions =
                fqDb.createFeedbackQuestionsWithoutExistenceCheck(questions.values());

        Map<String, String> questionRealQuestionIdMap = new HashMap<>();
        for (FeedbackQuestionAttributes createdQuestion : createdQuestions) {
            String sessionKey = makeSessionKey(createdQuestion.feedbackSessionName, createdQuestion.courseId);
            String questionKey = makeQuestionKey(sessionKey, createdQuestion.questionNumber);
            questionRealQuestionIdMap.put(questionKey, createdQuestion.getId());
        }

        injectRealIdsIntoResponses(responses.values(), questionRealQuestionIdMap);
        frDb.createEntitiesDeferred(responses.values());

        Map<String, FeedbackResponseCommentAttributes> responseComments = dataBundle.feedbackResponseComments;
        injectRealIdsIntoResponseComments(responseComments.values(), questionRealQuestionIdMap);
        fcDb.createEntitiesDeferred(responseComments.values());

        Map<String, AdminEmailAttributes> adminEmails = dataBundle.adminEmails;
        adminEmailsDb.createEntitiesDeferred(adminEmails.values());

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

    private void updateRespondents(FeedbackSessionAttributes session, List<InstructorAttributes> courseInstructors,
            List<FeedbackQuestionAttributes> sessionQuestions, List<FeedbackResponseAttributes> sessionResponses) {
        String sessionKey = makeSessionKey(session.getFeedbackSessionName(), session.getCourseId());

        Map<String, List<String>> instructorQuestionKeysMap = new HashMap<>();
        for (InstructorAttributes instructor : courseInstructors) {
            List<FeedbackQuestionAttributes> questionsForInstructor = feedbackQuestionsLogic
                    .getFeedbackQuestionsForInstructor(sessionQuestions, session.isCreator(instructor.email));

            List<String> questionKeys = makeQuestionKeys(questionsForInstructor, sessionKey);
            instructorQuestionKeysMap.put(instructor.email, questionKeys);
        }

        Set<String> respondingInstructors = new HashSet<>();
        Set<String> respondingStudents = new HashSet<>();

        for (FeedbackResponseAttributes response : sessionResponses) {
            String respondent = response.giver;
            String responseQuestionNumber = response.feedbackQuestionId; // contains question number before injection
            String responseQuestionKey = makeQuestionKey(sessionKey, responseQuestionNumber);

            List<String> instructorQuestionKeys = instructorQuestionKeysMap.get(respondent);
            if (instructorQuestionKeys != null && instructorQuestionKeys.contains(responseQuestionKey)) {
                respondingInstructors.add(respondent);
            } else {
                respondingStudents.add(respondent);
            }
        }

        session.setRespondingInstructorList(respondingInstructors);
        session.setRespondingStudentList(respondingStudents);
    }

    private List<String> makeQuestionKeys(List<FeedbackQuestionAttributes> questions, String sessionKey) {
        List<String> questionKeys = new ArrayList<>();
        for (FeedbackQuestionAttributes question : questions) {
            String questionKey = makeQuestionKey(sessionKey, question.questionNumber);
            questionKeys.add(questionKey);
        }
        return questionKeys;
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

    public void editStudentAsJson(String originalEmail, String newValues)
            throws InvalidParametersException, EntityDoesNotExistException {
        StudentAttributes student = JsonUtils.fromJson(newValues, StudentAttributes.class);
        student.section = student.section == null ? "None" : student.section;
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

    /**
     * This method ensures consistency for private feedback sessions
     * between the type and visibility times. This allows easier creation
     * of private sessions by setting the feedbackSessionType field as PRIVATE
     * in the json file.
     */
    private void cleanSessionData(FeedbackSessionAttributes session) {
        if (session.getFeedbackSessionType().equals(FeedbackSessionType.PRIVATE)) {
            session.setSessionVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
            session.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
        }
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
            Map<String, String> questionRealQuestionIdMap) {
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
            response.feedbackQuestionId = questionRealQuestionIdMap.get(questionKey);
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
            Map<String, String> questionRealQuestionIdMap) {
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
            comment.feedbackQuestionId = questionRealQuestionIdMap.get(questionKey);

            String[] responseIdParam = comment.feedbackResponseId.split("%");
            comment.feedbackResponseId = comment.feedbackQuestionId + "%" + responseIdParam[1] + "%" + responseIdParam[2];
        }
    }

    public void removeDataBundle(DataBundle dataBundle) {

        // Questions and responses will be deleted automatically.
        // We don't attempt to delete them again, to save time.
        deleteCourses(dataBundle.courses.values());

        for (AccountAttributes account : dataBundle.accounts.values()) {
            if (account.studentProfile == null) {
                account.studentProfile = StudentProfileAttributes.builder().build();
                account.studentProfile.googleId = account.googleId;
            }
        }
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
