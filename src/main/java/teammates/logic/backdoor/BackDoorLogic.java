package teammates.logic.backdoor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
    public String persistDataBundle(DataBundle dataBundle) throws InvalidParametersException, EntityDoesNotExistException {
        if (dataBundle == null) {
            throw new InvalidParametersException(Const.StatusCodes.NULL_PARAMETER, "Null data bundle");
        }

        Map<String, CourseAttributes> courses = dataBundle.courses;
        coursesDb.createEntitiesDeferred(courses.values());

        Map<String, InstructorAttributes> instructors = dataBundle.instructors;
        List<AccountAttributes> instructorAccounts = new ArrayList<>();
        for (InstructorAttributes instructor : instructors.values()) {
            validateInstructorPrivileges(instructor);

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

        Map<String, FeedbackSessionAttributes> sessions = dataBundle.feedbackSessions;
        for (FeedbackSessionAttributes session : sessions.values()) {
            cleanSessionData(session);
        }
        fbDb.createEntitiesDeferred(sessions.values());

        Map<String, FeedbackQuestionAttributes> questions = dataBundle.feedbackQuestions;
        for (FeedbackQuestionAttributes question : questions.values()) {
            question.removeIrrelevantVisibilityOptions();
        }
        fqDb.createEntitiesDeferred(questions.values());

        EntitiesDb.flush();

        Map<String, FeedbackResponseAttributes> responses = dataBundle.feedbackResponses;
        Map<List<String>, String> cachedRealQuestionIds = new HashMap<>();
        injectRealIdsIntoResponses(responses.values(), cachedRealQuestionIds);
        frDb.createEntitiesDeferred(responses.values());

        Map<String, FeedbackResponseCommentAttributes> responseComments = dataBundle.feedbackResponseComments;
        injectRealIdsIntoResponseComments(responseComments.values(), cachedRealQuestionIds);
        fcDb.createEntitiesDeferred(responseComments.values());

        Map<String, AdminEmailAttributes> adminEmails = dataBundle.adminEmails;
        adminEmailsDb.createEntitiesDeferred(adminEmails.values());

        EntitiesDb.flush();

        Set<String> sessionIds = new HashSet<>();
        for (FeedbackResponseAttributes response : responses.values()) {
            String sessionId = makeSessionKey(response.feedbackSessionName, response.courseId);
            if (sessionIds.contains(sessionId)) {
                continue;
            }
            updateRespondents(response.feedbackSessionName, response.courseId);
            sessionIds.add(sessionId);
        }

        return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
    }

    private String makeSessionKey(String feedbackSessionName, String courseId) {
        return feedbackSessionName + "%" + courseId;
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
            Map<List<String>, String> cachedRealQuestionIds) {
        for (FeedbackResponseAttributes response : responses) {
            response.feedbackQuestionId = getRealFeedbackQuestionId(
                    response.feedbackSessionName, response.courseId, response.feedbackQuestionId, cachedRealQuestionIds);
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
            Map<List<String>, String> cachedRealQuestionIds) {
        for (FeedbackResponseCommentAttributes comment : responseComments) {
            String realQuestionId = getRealFeedbackQuestionId(
                    comment.feedbackSessionName, comment.courseId, comment.feedbackQuestionId, cachedRealQuestionIds);
            if (comment.feedbackQuestionId.equals(realQuestionId)) {
                continue;
            }

            comment.feedbackQuestionId = realQuestionId;

            String[] responseIdParam = comment.feedbackResponseId.split("%");
            comment.feedbackResponseId = comment.feedbackQuestionId + "%" + responseIdParam[1] + "%" + responseIdParam[2];
        }
    }

    private String getRealFeedbackQuestionId(String feedbackSessionName, String courseId, String originalFeedbackQuestionId,
            Map<List<String>, String> cachedRealQuestionIds) {
        int qnNumber;
        try {
            qnNumber = Integer.parseInt(originalFeedbackQuestionId);
        } catch (NumberFormatException e) {
            // Correct question ID was already attached to response.
            return originalFeedbackQuestionId;
        }

        List<String> questionKey = Collections.unmodifiableList(
                Arrays.asList(feedbackSessionName, courseId, originalFeedbackQuestionId));

        String realQuestionId = cachedRealQuestionIds.get(questionKey);

        if (realQuestionId == null) {
            realQuestionId = feedbackQuestionsLogic.getFeedbackQuestion(feedbackSessionName, courseId, qnNumber).getId();
            cachedRealQuestionIds.put(questionKey, realQuestionId);
        }

        return realQuestionId;
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
