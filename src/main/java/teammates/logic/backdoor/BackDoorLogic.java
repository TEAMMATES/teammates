package teammates.logic.backdoor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import teammates.common.datatransfer.attributes.CommentAttributes;
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
import teammates.storage.api.CommentsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;

/**
 * Provides additional business logic for non-production usage (e.g. testing,
 * client scripts).
 */
public class BackDoorLogic extends Logic {

    private static final AccountsDb accountsDb = new AccountsDb();
    private static final CoursesDb coursesDb = new CoursesDb();
    private static final CommentsDb commentsDb = new CommentsDb();
    private static final StudentsDb studentsDb = new StudentsDb();
    private static final InstructorsDb instructorsDb = new InstructorsDb();
    private static final FeedbackSessionsDb fbDb = new FeedbackSessionsDb();
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private static final FeedbackResponseCommentsDb fcDb = new FeedbackResponseCommentsDb();
    private static final AdminEmailsDb adminEmailsDb = new AdminEmailsDb();

    /**
     * Persists given data in the datastore Works ONLY if the data is correct.
     * //Any existing copies of the data in the datastore will be overwritten. -
     * edit: use removeDataBundle to remove. made this change for speed when
     * deletion is not necessary.
     *
     * @return status of the request in the form 'status meassage'+'additional
     * info (if any)' e.g., "[BACKEND_STATUS_SUCCESS]" e.g.,
     * "[BACKEND_STATUS_FAILURE]NullPointerException at ..."
     */
    public String persistDataBundle(DataBundle dataBundle)
            throws InvalidParametersException, EntityDoesNotExistException {

        if (dataBundle == null) {
            throw new InvalidParametersException(
                    Const.StatusCodes.NULL_PARAMETER, "Null data bundle");
        }

        createAccounts(dataBundle);
        createCourses(dataBundle);
        createInstructors(dataBundle);
        createStudents(dataBundle);
        createFeedbackSessions(dataBundle);
        createFeedbackQuestions(dataBundle);
        createFeedbackResponses(dataBundle);
        createFeedbackResponseComments(dataBundle);
        createComments(dataBundle);
        createAdminEmail(dataBundle);

        // any Db can be used to commit the changes.
        // accountsDb is used as it is already used in the file
        accountsDb.commitOutstandingChanges();

        return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
    }

    private void createAdminEmail(DataBundle dataBundle) {
        Map<String, AdminEmailAttributes> adminEmails = dataBundle.adminEmails;
        adminEmails.values().forEach((email) -> {
            adminEmailsDb.createAdminEmail(email);
        });
    }

    private void createComments(DataBundle dataBundle) {
        Map<String, CommentAttributes> comments = dataBundle.comments;
        commentsDb.createComments(comments.values());
    }

    private void createFeedbackResponseComments(DataBundle dataBundle) throws EntityDoesNotExistException {
        Map<String, FeedbackResponseCommentAttributes> responseComments = dataBundle.feedbackResponseComments;
        responseComments.values().forEach((responseComment) -> {
            injectRealIds(responseComment);
        });
        fcDb.createFeedbackResponseComments(responseComments.values());
    }

    private void createFeedbackResponses(DataBundle dataBundle) throws EntityDoesNotExistException {
        Map<String, FeedbackResponseAttributes> responses = dataBundle.feedbackResponses;
        responses.values().forEach((response) -> {
            response = injectRealIds(response);
        });
        frDb.createFeedbackResponses(responses.values());
        
        Set<String> sessionIds = new HashSet<>();
        
        responses.values().forEach((response) -> {
            String sessionId = response.feedbackSessionName + "%" + response.courseId;
            if (!sessionIds.contains(sessionId)) {
                updateRespondents(response.feedbackSessionName, response.courseId);
                sessionIds.add(sessionId);
            }
        });
    }

    private void createFeedbackQuestions(DataBundle dataBundle) {
        Map<String, FeedbackQuestionAttributes> questions = dataBundle.feedbackQuestions;
        List<FeedbackQuestionAttributes> questionList = new ArrayList<FeedbackQuestionAttributes>(questions.values());
        
        questionList.forEach((question) -> {
            question.removeIrrelevantVisibilityOptions();
        });
        fqDb.createFeedbackQuestions(questionList);
    }

    private void createFeedbackSessions(DataBundle dataBundle) {
        Map<String, FeedbackSessionAttributes> sessions = dataBundle.feedbackSessions;
        sessions.values().forEach((session) -> {
            cleanSessionData(session);
        });
        fbDb.createFeedbackSessions(sessions.values());
    }

    private void createStudents(DataBundle dataBundle) {
        Map<String, StudentAttributes> students = dataBundle.students;
        List<AccountAttributes> studentAccounts = new ArrayList<AccountAttributes>();
        students.values().stream().map((student) -> {
            student.section = student.section == null ? "None" : student.section;
            return student;
        }).filter((student) -> (student.googleId != null && !student.googleId.isEmpty())).map((student) -> new AccountAttributes(student.googleId, student.name, false,
                student.email, "TEAMMATES Test Institute 1")).map((account) -> {
                    if (account.studentProfile == null) {
                        account.studentProfile = new StudentProfileAttributes();
                        account.studentProfile.googleId = account.googleId;
                    }
            return account;
        }).forEachOrdered((account) -> {
            studentAccounts.add(account);
        });
        accountsDb.createAccounts(studentAccounts, false);
        studentsDb.createStudentsWithoutSearchability(students.values());
    }

    private void createInstructors(DataBundle dataBundle) {
        Map<String, InstructorAttributes> instructors = dataBundle.instructors;
        List<AccountAttributes> instructorAccounts = new ArrayList<AccountAttributes>();
        instructors.values().stream().map((instructor) -> {
            validateInstructorPrivileges(instructor);
            return instructor;
        }).filter((instructor) -> (instructor.googleId != null && !instructor.googleId.isEmpty())).map((instructor) -> new AccountAttributes(instructor.googleId, instructor.name, true,
                instructor.email, "TEAMMATES Test Institute 1")).map((account) -> {
                    if (account.studentProfile == null) {
                        account.studentProfile = new StudentProfileAttributes();
                        account.studentProfile.googleId = account.googleId;
                    }
            return account;
        }).forEachOrdered((account) -> {
            instructorAccounts.add(account);
        });
        accountsDb.createAccounts(instructorAccounts, false);
        instructorsDb.createInstructorsWithoutSearchability(instructors.values());
    }

    private void createCourses(DataBundle dataBundle) {
        Map<String, CourseAttributes> courses = dataBundle.courses;
        coursesDb.createCourses(courses.values());
    }

    private void createAccounts(DataBundle dataBundle) {
        Map<String, AccountAttributes> accounts = dataBundle.accounts;
        accounts.values().stream().filter((account) -> (account.studentProfile == null)).map((account) -> {
            account.studentProfile = new StudentProfileAttributes();
            return account;
        }).forEachOrdered((account) -> {
            account.studentProfile.googleId = account.googleId;
        });
        accountsDb.createAccounts(accounts.values(), true);
    }

    /**
     * Checks if the role of {@code instructor} matches its privileges.
     *
     * @param instructor the {@link InstructorAttributes} of an instructor,
     * cannot be {@code null}
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
     *
     * @return status of the request in the form 'status meassage'+'additional
     * info (if any)' e.g., "[BACKEND_STATUS_SUCCESS]" e.g.,
     * "[BACKEND_STATUS_FAILURE]NullPointerException at ..."
     */
    public String putDocuments(DataBundle dataBundle) {
        // query the entity in db first to get the actual data and create document for actual entity

        Map<String, StudentAttributes> students = dataBundle.students;
        students.values().stream().map((student) -> studentsDb.getStudentForEmail(student.course, student.email)).forEachOrdered((studentInDb) -> {
            studentsDb.putDocument(studentInDb);
        });

        Map<String, InstructorAttributes> instructors = dataBundle.instructors;
        instructors.values().stream().map((instructor) -> instructorsDb.getInstructorForEmail(instructor.courseId, instructor.email)).forEachOrdered((instructorInDb) -> {
            instructorsDb.putDocument(instructorInDb);
        });

        Map<String, FeedbackResponseCommentAttributes> responseComments = dataBundle.feedbackResponseComments;
        responseComments.values().stream().map((responseComment) -> fcDb.getFeedbackResponseComment(
                responseComment.courseId, responseComment.createdAt, responseComment.giverEmail)).forEachOrdered((fcInDb) -> {
                    fcDb.putDocument(fcInDb);
        });

        Map<String, CommentAttributes> comments = dataBundle.comments;
        comments.values().stream().map((comment) -> commentsDb.getComment(comment)).forEachOrdered((commentInDb) -> {
            commentsDb.putDocument(commentInDb);
        });

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
        FeedbackQuestionAttributes fq
                = feedbackQuestionsLogic.getFeedbackQuestion(feedbackSessionName, courseId, qnNumber);
        return JsonUtils.toJson(fq);
    }

    public String getFeedbackQuestionForIdAsJson(String questionId) {
        FeedbackQuestionAttributes fq = feedbackQuestionsLogic.getFeedbackQuestion(questionId);
        return JsonUtils.toJson(fq);
    }

    public String getFeedbackResponseAsJson(String feedbackQuestionId, String giverEmail, String recipient) {
        FeedbackResponseAttributes fq
                = feedbackResponsesLogic.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
        return JsonUtils.toJson(fq);
    }

    public String getFeedbackResponsesForGiverAsJson(String courseId, String giverEmail) {
        List<FeedbackResponseAttributes> responseList
                = feedbackResponsesLogic.getFeedbackResponsesFromGiverForCourse(courseId, giverEmail);
        return JsonUtils.toJson(responseList);
    }

    public String getFeedbackResponsesForReceiverAsJson(String courseId, String recipient) {
        List<FeedbackResponseAttributes> responseList
                = feedbackResponsesLogic.getFeedbackResponsesForReceiverForCourse(courseId, recipient);
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
        FeedbackSessionAttributes feedbackSession
                = JsonUtils.fromJson(feedbackSessionJson, FeedbackSessionAttributes.class);
        updateFeedbackSession(feedbackSession);
    }

    public void editFeedbackQuestionAsJson(String feedbackQuestionJson)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackQuestionAttributes feedbackQuestion
                = JsonUtils.fromJson(feedbackQuestionJson, FeedbackQuestionAttributes.class);
        updateFeedbackQuestion(feedbackQuestion);
    }

    /**
     * This method ensures consistency for private feedback sessions between the
     * type and visibility times. This allows easier creation of private
     * sessions by setting the feedbackSessionType field as PRIVATE in the json
     * file.
     */
    private FeedbackSessionAttributes cleanSessionData(FeedbackSessionAttributes session) {
        if (session.getFeedbackSessionType().equals(FeedbackSessionType.PRIVATE)) {
            session.setSessionVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
            session.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
        }
        return session;
    }

    /**
     * This method is necessary to generate the feedbackQuestionId of the
     * question the response is for.<br>
     * Normally, the ID is already generated on creation, but the json file does
     * not contain the actual response ID. <br>
     * Therefore the question number corresponding to the created response
     * should be inserted in the json file in place of the actual response
     * ID.<br>
     * This method will then generate the correct ID and replace the field.
     *
     * @throws EntityDoesNotExistException
    *
     */
    private FeedbackResponseAttributes injectRealIds(FeedbackResponseAttributes response)
            throws EntityDoesNotExistException {
        try {
            int qnNumber = Integer.parseInt(response.feedbackQuestionId);

            FeedbackQuestionAttributes question = feedbackQuestionsLogic.getFeedbackQuestion(
                    response.feedbackSessionName, response.courseId, qnNumber);
            if (question == null) {
                throw new EntityDoesNotExistException("question has not persisted yet");
            }
            response.feedbackQuestionId = question.getId();

        } catch (NumberFormatException e) {
            // Correct question ID was already attached to response.
        }

        return response;
    }

    /**
     * This method is necessary to generate the feedbackQuestionId and
     * feedbackResponseId of the question and response the comment is for.<br>
     * Normally, the ID is already generated on creation, but the json file does
     * not contain the actual response ID. <br>
     * Therefore the question number and questionNumber%giverEmail%recipient
     * corresponding to the created comment should be inserted in the json file
     * in place of the actual ID.<br>
     * This method will then generate the correct ID and replace the field.
     *
     * @throws EntityDoesNotExistException
    *
     */
    private void injectRealIds(FeedbackResponseCommentAttributes responseComment) {
        try {
            int qnNumber = Integer.parseInt(responseComment.feedbackQuestionId);

            responseComment.feedbackQuestionId
                    = feedbackQuestionsLogic.getFeedbackQuestion(
                            responseComment.feedbackSessionName,
                            responseComment.courseId,
                            qnNumber).getId();
        } catch (NumberFormatException e) {
            // Correct question ID was already attached to response.
        }

        String[] responseIdParam = responseComment.feedbackResponseId.split("%");

        responseComment.feedbackResponseId
                = responseComment.feedbackQuestionId
                + "%" + responseIdParam[1] + "%" + responseIdParam[2];
    }

    public void removeDataBundle(DataBundle dataBundle) {

        // Questions and responses will be deleted automatically.
        // We don't attempt to delete them again, to save time.
        deleteCourses(dataBundle.courses.values());

        for (AccountAttributes account : dataBundle.accounts.values()) {
            if (account.studentProfile == null) {
                account.studentProfile = new StudentProfileAttributes();
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
        List<String> courseIds = new ArrayList<String>();
        courses.forEach((course) -> {
            courseIds.add(course.getId());
        });
        if (!courseIds.isEmpty()) {
            coursesDb.deleteEntities(courses);
            instructorsDb.deleteInstructorsForCourses(courseIds);
            studentsDb.deleteStudentsForCourses(courseIds);
            commentsDb.deleteCommentsForCourses(courseIds);
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
}
