package teammates.ui.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackPathAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.StringHelper;
import teammates.ui.pagedata.PageData;

public class InstructorFeedbackQuestionEditAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, account.googleId),
                                    logic.getFeedbackSession(feedbackSessionName, courseId),
                                    false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        String editType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE);
        Assumption.assertNotNull("Null editType", editType);

        FeedbackQuestionAttributes updatedQuestion = extractFeedbackQuestionData();

        try {
            if ("edit".equals(editType)) {
                String questionText = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
                Assumption.assertNotNull("Null question text", questionText);
                Assumption.assertNotEmpty("Empty question text", questionText);

                editQuestion(updatedQuestion);
            } else if ("delete".equals(editType)) {
                // branch not tested because if it's not edit or delete, Assumption.fail will cause test failure
                deleteQuestion(updatedQuestion);
            } else {
                // Assumption.fails are not tested
                Assumption.fail("Invalid editType");
            }
        } catch (InvalidParametersException e) {
            // This part is not tested because GateKeeper handles if this happens, would be
            // extremely difficult to replicate a situation whereby it gets past GateKeeper
            setStatusForException(e);
        }

        return createRedirectResult(new PageData(account)
                                            .getInstructorFeedbackEditLink(courseId, feedbackSessionName));
    }

    private void deleteQuestion(FeedbackQuestionAttributes updatedQuestion) {
        logic.deleteFeedbackQuestion(updatedQuestion.getId());
        statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, StatusMessageColor.SUCCESS));
        statusToAdmin = "Feedback Question " + updatedQuestion.questionNumber + " for session:<span class=\"bold\">("
                        + updatedQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">["
                        + updatedQuestion.courseId + "]</span> deleted.<br>";
    }

    private void editQuestion(FeedbackQuestionAttributes updatedQuestion) throws InvalidParametersException,
                                                                                 EntityDoesNotExistException {
        String err = validateQuestionGiverRecipientVisibility(updatedQuestion);

        if (!err.isEmpty()) {
            statusToUser.add(new StatusMessage(err, StatusMessageColor.DANGER));
            isError = true;
        }

        FeedbackQuestionDetails updatedQuestionDetails = updatedQuestion.getQuestionDetails();
        List<String> questionDetailsErrors = updatedQuestionDetails.validateQuestionDetails();
        List<StatusMessage> questionDetailsErrorsMessages = new ArrayList<StatusMessage>();

        for (String error : questionDetailsErrors) {
            questionDetailsErrorsMessages.add(new StatusMessage(error, StatusMessageColor.DANGER));
        }

        List<StudentAttributes> studentsInCourse = logic.getStudentsForCourse(updatedQuestion.getCourseId());
        List<InstructorAttributes> instructorsInCourse = logic.getInstructorsForCourse(updatedQuestion.getCourseId());
        String feedbackPathsParticipantsError =
                validateQuestionFeedbackPathsParticipants(
                        updatedQuestion, studentsInCourse, instructorsInCourse);
        StatusMessage feedbackPathsParticipantsErrorMessage =
                new StatusMessage(feedbackPathsParticipantsError, StatusMessageColor.DANGER);

        if (questionDetailsErrors.isEmpty() && feedbackPathsParticipantsError.isEmpty()) {
            logic.updateFeedbackQuestionNumber(updatedQuestion);

            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Feedback Question " + updatedQuestion.questionNumber
                          + " for session:<span class=\"bold\">("
                          + updatedQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">["
                          + updatedQuestion.courseId + "]</span> edited.<br>"
                          + "<span class=\"bold\">"
                          + updatedQuestionDetails.getQuestionTypeDisplayName() + ":</span> "
                          + updatedQuestionDetails.getQuestionText();
        } else {
            statusToUser.addAll(questionDetailsErrorsMessages);
            statusToUser.add(feedbackPathsParticipantsErrorMessage);
            statusToAdmin = feedbackPathsParticipantsError;
            isError = true;
        }
    }

    /**
     * Validates that the giver and recipient for the given FeedbackQuestionAttributes is valid for its question type.
     * Validates that the visibility for the given FeedbackQuestionAttributes is valid for its question type.
     *
     * @return error message detailing the error, or an empty string if valid.
     */
    public static String validateQuestionGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        String errorMsg = "";

        FeedbackQuestionDetails questionDetails = null;
        Class<? extends FeedbackQuestionDetails> questionDetailsClass = feedbackQuestionAttributes
                                                                            .questionType.getQuestionDetailsClass();
        Constructor<? extends FeedbackQuestionDetails> questionDetailsClassConstructor;

        try {
            questionDetailsClassConstructor = questionDetailsClass.getConstructor();
            questionDetails = questionDetailsClassConstructor.newInstance();
            Method m = questionDetailsClass.getMethod("validateGiverRecipientVisibility",
                                                      FeedbackQuestionAttributes.class);
            errorMsg = (String) m.invoke(questionDetails, feedbackQuestionAttributes);

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException | InstantiationException e) {
            log.severe(TeammatesException.toStringWithStackTrace(e));
            // Assumption.fails are not tested
            Assumption.fail("Failed to instantiate Feedback*QuestionDetails instance for "
                            + feedbackQuestionAttributes.questionType.toString() + " question type.");
        }

        return errorMsg;
    }

    /**
     * Checks that the feedback path participants for the given FeedbackQuestionAttributes are valid.
     *
     * @return error message detailing the error, or an empty string if valid.
     */
    public static String validateQuestionFeedbackPathsParticipants(
            FeedbackQuestionAttributes question, List<StudentAttributes> students,
            List<InstructorAttributes> instructors) {
        Set<String> studentEmails = new HashSet<String>();
        Set<String> instructorEmails = new HashSet<String>();
        Set<String> teamNames = new HashSet<String>();
        Map<String, String> studentEmailToTeamNameMap = new HashMap<String, String>();
        Map<String, Set<String>> teamNameToStudentEmailsMap = new HashMap<String, Set<String>>();

        populateCourseData(students, instructors, studentEmails, instructorEmails, teamNames,
                           studentEmailToTeamNameMap, teamNameToStudentEmailsMap);

        // Check for non-existent participants
        Set<String> nonExistentParticipants =
                getNonExistentParticipantsFromFeedbackPaths(question, studentEmails, instructorEmails, teamNames);

        if (!nonExistentParticipants.isEmpty()) {
            return "Unable to save question as the following feedback path participants do not exist: "
                    + StringHelper.removeEnclosingSquareBrackets(
                            SanitizationHelper.sanitizeForHtml(nonExistentParticipants).toString()) + ".";
        }

        // Check validity of feedback paths for contrib questions
        // Both the giver and recipient must be a student
        // If a student is a giver, all the student's team members must also be givers
        // Each giver should have all the members in his/her team as a recipient
        StringBuilder errorMsg = new StringBuilder(200);

        if (question.getQuestionType() == FeedbackQuestionType.CONTRIB) {
            Map<String, Set<String>> teamNameToGiverIdsMap = new HashMap<String, Set<String>>();
            Map<String, Set<String>> giverIdToRecipientIdsMap = new HashMap<String, Set<String>>();

            if (question.isFeedbackPathsGiverTypeStudents()
                    && question.isFeedbackPathsRecipientTypeStudents()) {
                populateFeedbackPathsMappings(
                        question, studentEmailToTeamNameMap, teamNameToGiverIdsMap, giverIdToRecipientIdsMap);

                if (!isAllStudentsInTeamGivers(teamNameToStudentEmailsMap, teamNameToGiverIdsMap)) {
                    errorMsg.append("All the students in a team must be a giver. ");
                }

                if (!isAllGiversTeamMembersRecipients(
                        studentEmailToTeamNameMap, teamNameToStudentEmailsMap, giverIdToRecipientIdsMap)) {
                    errorMsg.append("The student must give feedback to all his/her team members"
                                    + " including himself/herself. ");
                }
            } else {
                errorMsg.append("Both the giver and recipient must be a student. ");
            }
        }

        return errorMsg.toString().trim();
    }

    private FeedbackQuestionAttributes extractFeedbackQuestionData() {
        FeedbackQuestionAttributes newQuestion = new FeedbackQuestionAttributes();

        newQuestion.setId(getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID));
        Assumption.assertNotNull("Null question id", newQuestion.getId());

        newQuestion.courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("Null course id", newQuestion.courseId);

        newQuestion.feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull("Null feedback session name", newQuestion.feedbackSessionName);

        // TODO thoroughly investigate when and why these parameters can be null
        // and check all possibilities in the tests
        // should only be null when deleting. might be good to separate the delete action from this class

        // When editing, usually the following fields are not null. If they are null somehow(edit from browser),
        // Then the field will not update and take on its old value.
        // When deleting, the following fields are null.
        // numofrecipients
        // questiontext
        // numofrecipientstype
        // recipienttype
        // receiverLeaderCheckbox
        // givertype

        // Can be null
        String giverType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE);
        if (giverType != null) {
            newQuestion.giverType = FeedbackParticipantType.valueOf(giverType);
        }

        // Can be null
        String recipientType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE);
        if (recipientType != null) {
            newQuestion.recipientType = FeedbackParticipantType.valueOf(recipientType);
        }

        String questionNumber = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER);
        Assumption.assertNotNull("Null question number", questionNumber);
        newQuestion.questionNumber = Integer.parseInt(questionNumber);
        Assumption.assertTrue("Invalid question number", newQuestion.questionNumber >= 1);

        // Can be null
        String nEntityTypes = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE);

        if (numberOfEntitiesIsUserDefined(newQuestion.recipientType, nEntityTypes)) {
            String nEntities = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES);
            Assumption.assertNotNull(nEntities);
            newQuestion.numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(nEntities);
        } else {
            newQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        }

        if (newQuestion.giverType == FeedbackParticipantType.CUSTOM
                && newQuestion.recipientType == FeedbackParticipantType.CUSTOM) {
            String customFeedbackPathsSpreadsheetData =
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SPREADSHEETDATA);

            newQuestion.feedbackPaths =
                    FeedbackQuestionAttributes.getFeedbackPathsFromSpreadsheetData(
                            newQuestion.courseId, customFeedbackPathsSpreadsheetData);
        }

        newQuestion.showResponsesTo = FeedbackParticipantType.getParticipantListFromCommaSeparatedValues(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO));
        newQuestion.showGiverNameTo = FeedbackParticipantType.getParticipantListFromCommaSeparatedValues(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO));
        newQuestion.showRecipientNameTo = FeedbackParticipantType.getParticipantListFromCommaSeparatedValues(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO));

        String questionType = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_TYPE);
        Assumption.assertNotNull(questionType);
        newQuestion.questionType = FeedbackQuestionType.valueOf(questionType);

        // Can be null
        String questionText = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
        if (questionText != null && !questionText.isEmpty()) {
            FeedbackQuestionDetails questionDetails = FeedbackQuestionDetails.createQuestionDetails(
                    requestParameters, newQuestion.questionType);
            newQuestion.setQuestionDetails(questionDetails);
        }

        String questionDescription = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION);

        newQuestion.setQuestionDescription(new Text(questionDescription));

        return newQuestion;
    }

    private static boolean numberOfEntitiesIsUserDefined(FeedbackParticipantType recipientType, String nEntityTypes) {
        if (recipientType != FeedbackParticipantType.STUDENTS
                && recipientType != FeedbackParticipantType.TEAMS) {
            return false;
        }

        return "custom".equals(nEntityTypes);
    }

    private static void populateCourseData(
            List<StudentAttributes> students, List<InstructorAttributes> instructors,
            Set<String> studentEmails, Set<String> instructorEmails, Set<String> teamNames,
            Map<String, String> studentEmailToTeamNameMap, Map<String, Set<String>> teamNameToStudentEmailsMap) {
        for (StudentAttributes student : students) {
            studentEmails.add(student.getEmail());
            teamNames.add(student.getTeam());
            studentEmailToTeamNameMap.put(student.getEmail(), student.getTeam());
            Set<String> teamMembers = teamNameToStudentEmailsMap.get(student.getTeam());
            if (teamMembers == null) {
                teamMembers = new HashSet<String>();
            }
            teamMembers.add(student.getEmail());
            teamNameToStudentEmailsMap.put(student.getTeam(), teamMembers);
        }

        for (InstructorAttributes instructor : instructors) {
            instructorEmails.add(instructor.getEmail());
        }
    }

    private static Set<String> getNonExistentParticipantsFromFeedbackPaths(
            FeedbackQuestionAttributes question, Set<String> studentEmails,
            Set<String> instructorEmails, Set<String> teamNames) {
        Set<String> nonExistentParticipants = new HashSet<String>();

        for (FeedbackPathAttributes feedbackPath : question.feedbackPaths) {
            boolean isFeedbackPathGiverTypeNonExistent =
                    feedbackPath.getFeedbackPathGiverType().isEmpty();
            boolean isFeedbackPathGiverStudentNonExistent =
                    feedbackPath.isFeedbackPathGiverAStudent()
                    && !studentEmails.contains(feedbackPath.getGiverId());
            boolean isFeedbackPathGiverInstructorNonExistent =
                    feedbackPath.isFeedbackPathGiverAnInstructor()
                    && !instructorEmails.contains(feedbackPath.getGiverId());
            boolean isFeedbackPathGiverTeamNonExistent =
                    feedbackPath.isFeedbackPathGiverATeam()
                    && !teamNames.contains(feedbackPath.getGiverId());

            boolean isFeedbackPathRecipientTypeNonExistent =
                    feedbackPath.getFeedbackPathRecipientType().isEmpty();
            boolean isFeedbackPathRecipientStudentNonExistent =
                    feedbackPath.isFeedbackPathRecipientAStudent()
                    && !studentEmails.contains(feedbackPath.getRecipientId());
            boolean isFeedbackPathRecipientInstructorNonExistent =
                    feedbackPath.isFeedbackPathRecipientAnInstructor()
                    && !instructorEmails.contains(feedbackPath.getRecipientId());
            boolean isFeedbackPathRecipientTeamNonExistent =
                    feedbackPath.isFeedbackPathRecipientATeam()
                    && !teamNames.contains(feedbackPath.getRecipientId());

            if (isFeedbackPathGiverTypeNonExistent
                    || isFeedbackPathGiverStudentNonExistent
                    || isFeedbackPathGiverInstructorNonExistent
                    || isFeedbackPathGiverTeamNonExistent) {
                nonExistentParticipants.add(feedbackPath.getGiver());
            }

            if (isFeedbackPathRecipientTypeNonExistent
                    || isFeedbackPathRecipientStudentNonExistent
                    || isFeedbackPathRecipientInstructorNonExistent
                    || isFeedbackPathRecipientTeamNonExistent) {
                nonExistentParticipants.add(feedbackPath.getRecipient());
            }
        }

        return nonExistentParticipants;
    }

    private static void populateFeedbackPathsMappings(
            FeedbackQuestionAttributes question, Map<String, String> studentEmailToTeamNameMap,
            Map<String, Set<String>> teamNameToGiverIdsMap, Map<String, Set<String>> giverIdToRecipientIdsMap) {
        for (FeedbackPathAttributes feedbackPath : question.feedbackPaths) {
            String giverId = feedbackPath.getGiverId();
            String giverTeam = studentEmailToTeamNameMap.get(giverId);
            Set<String> giverIds = teamNameToGiverIdsMap.get(giverTeam);
            if (giverIds == null) {
                giverIds = new HashSet<String>();
            }
            giverIds.add(giverId);
            teamNameToGiverIdsMap.put(giverTeam, giverIds);

            Set<String> recipientsOfGiver = giverIdToRecipientIdsMap.get(giverId);
            if (recipientsOfGiver == null) {
                recipientsOfGiver = new HashSet<String>();
            }
            recipientsOfGiver.add(feedbackPath.getRecipientId());
            giverIdToRecipientIdsMap.put(giverId, recipientsOfGiver);
        }
    }

    private static boolean isAllStudentsInTeamGivers(
            Map<String, Set<String>> teamNameToStudentEmailsMap, Map<String, Set<String>> teamNameToGiverIdsMap) {
        for (String teamName : teamNameToGiverIdsMap.keySet()) {
            if (!teamNameToStudentEmailsMap.get(teamName).equals(teamNameToGiverIdsMap.get(teamName))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAllGiversTeamMembersRecipients(
            Map<String, String> studentEmailToTeamNameMap, Map<String, Set<String>> teamNameToStudentEmailsMap,
            Map<String, Set<String>> giverIdToRecipientIdsMap) {
        for (String giverId : giverIdToRecipientIdsMap.keySet()) {
            String giverTeam = studentEmailToTeamNameMap.get(giverId);
            if (!teamNameToStudentEmailsMap.get(giverTeam).equals(giverIdToRecipientIdsMap.get(giverId))) {
                return false;
            }
        }
        return true;
    }
}
