package teammates.ui.pagedata;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.ui.template.FeedbackResponseCommentRow;
import teammates.ui.template.FeedbackSubmissionEditQuestion;
import teammates.ui.template.FeedbackSubmissionEditResponse;
import teammates.ui.template.StudentFeedbackSubmissionEditQuestionsWithResponses;

public class FeedbackSubmissionEditPageData extends PageData {
    public FeedbackSessionQuestionsBundle bundle;
    private String moderatedQuestionId;
    private boolean isSessionOpenForSubmission;
    private boolean isPreview;
    private boolean isModeration;
    private boolean isShowRealQuestionNumber;
    private boolean isHeaderHidden;
    private StudentAttributes studentToViewPageAs;
    private InstructorAttributes previewInstructor;
    private String registerMessage;
    private String submitAction;
    private List<StudentFeedbackSubmissionEditQuestionsWithResponses> questionsWithResponses;
    private boolean isFeedbackSessionForInstructor;

    public FeedbackSubmissionEditPageData(AccountAttributes account, StudentAttributes student, String sessionToken) {
        super(account, student, sessionToken);
        isPreview = false;
        isModeration = false;
        isShowRealQuestionNumber = false;
        isHeaderHidden = false;
        isFeedbackSessionForInstructor = false;
    }

    /**
     * Generates the register message with join URL containing course ID
     * if the student is unregistered. Also loads the questions with responses.
     * @param courseId the course ID
     */
    public void init(String courseId) {
        init("", "", courseId);
    }

    /**
     * Generates the register message with join URL containing registration key,
     * email and course ID if the student is unregistered. Also loads the questions and responses.
     * @param regKey the registration key
     * @param email the email
     * @param courseId the course ID
     */
    public void init(String regKey, String email, String courseId) {
        String joinUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
                                        .withRegistrationKey(regKey)
                                        .withStudentEmail(email)
                                        .withCourseId(courseId)
                                        .toString();

        registerMessage = student == null
                        ? ""
                        : String.format(Const.StatusMessages.UNREGISTERED_STUDENT, student.name, joinUrl);
        createQuestionsWithResponses();
    }

    public FeedbackSessionQuestionsBundle getBundle() {
        return bundle;
    }

    public String getModeratedQuestionId() {
        return moderatedQuestionId;
    }

    public boolean isSessionOpenForSubmission() {
        return isSessionOpenForSubmission;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public boolean isModeration() {
        return isModeration;
    }

    public boolean isShowRealQuestionNumber() {
        return isShowRealQuestionNumber;
    }

    public boolean isHeaderHidden() {
        return isHeaderHidden;
    }

    public StudentAttributes getStudentToViewPageAs() {
        return studentToViewPageAs;
    }

    public StudentAttributes getStudent() {
        return student;
    }

    public InstructorAttributes getPreviewInstructor() {
        return previewInstructor;
    }

    public String getRegisterMessage() {
        return registerMessage;
    }

    public String getSubmitAction() {
        return submitAction;
    }

    public boolean isSubmittable() {
        return isSessionOpenForSubmission || isModeration;
    }

    public List<StudentFeedbackSubmissionEditQuestionsWithResponses> getQuestionsWithResponses() {
        return questionsWithResponses;
    }

    public void setModeratedQuestionId(String moderatedQuestionId) {
        this.moderatedQuestionId = moderatedQuestionId;
    }

    public void setSessionOpenForSubmission(boolean isSessionOpenForSubmission) {
        this.isSessionOpenForSubmission = isSessionOpenForSubmission;
    }

    public void setFeedbackSessionForInstructor(boolean isFeedbackSessionForInstructor) {
        this.isFeedbackSessionForInstructor = isFeedbackSessionForInstructor;
    }

    public void setPreview(boolean isPreview) {
        this.isPreview = isPreview;
    }

    public void setModeration(boolean isModeration) {
        this.isModeration = isModeration;
    }

    public void setShowRealQuestionNumber(boolean isShowRealQuestionNumber) {
        this.isShowRealQuestionNumber = isShowRealQuestionNumber;
    }

    public void setHeaderHidden(boolean isHeaderHidden) {
        this.isHeaderHidden = isHeaderHidden;
    }

    public void setStudentToViewPageAs(StudentAttributes studentToViewPageAs) {
        this.studentToViewPageAs = studentToViewPageAs;
    }

    public void setPreviewInstructor(InstructorAttributes previewInstructor) {
        this.previewInstructor = previewInstructor;
    }

    public void setRegisterMessage(String registerMessage) {
        this.registerMessage = registerMessage;
    }

    public void setSubmitAction(String submitAction) {
        this.submitAction = submitAction;
    }

    public List<String> getRecipientOptionsForQuestion(String feedbackQuestionId, String currentlySelectedOption) {

        if (this.bundle == null) {
            return null;
        }

        Map<String, String> emailNamePair = this.bundle.getSortedRecipientList(feedbackQuestionId);

        List<String> result = new ArrayList<>();
        // Add an empty option first.
        result.add("<option value=\"\" " + (currentlySelectedOption == null ? "selected>" : ">")
                   + "</option>");

        emailNamePair.forEach((key, value) -> {
            boolean isSelected = SanitizationHelper.desanitizeFromHtml(key)
                                             .equals(currentlySelectedOption);
            result.add("<option value=\"" + sanitizeForHtml(key) + "\"" + (isSelected ? " selected" : "") + ">"
                           + sanitizeForHtml(value)
                       + "</option>"
            );
        });

        return result;
    }

    private boolean isResponseRecipientValid(FeedbackResponseAttributes existingResponse) {
        Map<String, String> emailNamePair =
                this.bundle.getSortedRecipientList(existingResponse.feedbackQuestionId);

        return emailNamePair.containsKey(existingResponse.recipient);
    }

    public String getEncryptedRegkey() {
        return StringHelper.encrypt(student.key);
    }

    private void createQuestionsWithResponses() {
        questionsWithResponses = new ArrayList<>();
        int qnIndx = 1;

        for (FeedbackQuestionAttributes questionAttributes : bundle.getSortedQuestions()) {
            int numOfResponseBoxes = questionAttributes.numberOfEntitiesToGiveFeedbackTo;
            int maxResponsesPossible = bundle.recipientList.get(questionAttributes.getId()).size();

            if (numOfResponseBoxes == Const.MAX_POSSIBLE_RECIPIENTS || numOfResponseBoxes > maxResponsesPossible) {
                numOfResponseBoxes = maxResponsesPossible;
            }
            FeedbackSubmissionEditQuestion question = createQuestion(questionAttributes, qnIndx);
            List<FeedbackSubmissionEditResponse> responses =
                    createResponses(questionAttributes, qnIndx, numOfResponseBoxes);

            boolean isInstructorCommentsOnResponsesAllowed =
                                        questionAttributes.getQuestionDetails().isCommentsOnResponsesAllowed();
            boolean isStudentCommentsOnResponsesAllowed =
                                        questionAttributes.getQuestionDetails().isStudentCommentsOnResponsesAllowed();
            questionsWithResponses.add(new StudentFeedbackSubmissionEditQuestionsWithResponses(
                    question, responses, numOfResponseBoxes, maxResponsesPossible,
                    isInstructorCommentsOnResponsesAllowed, isStudentCommentsOnResponsesAllowed));
            qnIndx++;
        }
    }

    private FeedbackSubmissionEditQuestion createQuestion(FeedbackQuestionAttributes questionAttributes, int qnIndx) {
        boolean isModeratedQuestion = String.valueOf(questionAttributes.getId()).equals(getModeratedQuestionId());

        return new FeedbackSubmissionEditQuestion(questionAttributes, qnIndx, isModeratedQuestion);
    }

    private List<FeedbackSubmissionEditResponse> createResponses(
                                    FeedbackQuestionAttributes questionAttributes, int qnIndx, int numOfResponseBoxes) {
        List<FeedbackSubmissionEditResponse> responses = new ArrayList<>();

        List<FeedbackResponseAttributes> existingResponses = bundle.questionResponseBundle.get(questionAttributes);
        List<String> responseSubmittedRecipient = new ArrayList<>();
        int responseIndx = 0;

        for (FeedbackResponseAttributes existingResponse : existingResponses) {
            if (!isResponseRecipientValid(existingResponse)) {
                // A response recipient can be invalid due to submission adjustment failure
                continue;
            }
            List<String> recipientOptionsForQuestion = getRecipientOptionsForQuestion(
                                                           questionAttributes.getId(), existingResponse.recipient);

            String submissionFormHtml = questionAttributes.getQuestionDetails()
                                            .getQuestionWithExistingResponseSubmissionFormHtml(
                                                isSessionOpenForSubmission, qnIndx, responseIndx,
                                                questionAttributes.courseId, numOfResponseBoxes,
                                                existingResponse.getResponseDetails(), student);

            FeedbackResponseCommentRow comment;
            String commentGiverName = bundle.getNameForEmail(existingResponse.giver);
            String commentRecipientName = bundle.getNameForEmail(existingResponse.recipient);
            Map<FeedbackParticipantType, Boolean> responseVisibilityMap =
                    getResponseVisibilityMap(questionAttributes, false);
            Map<String, String> commentGiverEmailToNameTable = bundle.commentGiverEmailToNameTable;
            if (questionAttributes.getQuestionDetails().isStudentCommentsOnResponsesAllowed()
                    && bundle.commentsForResponses.containsKey(existingResponse.getId())) {
                comment = getResponseCommentsForQuestion(questionAttributes,
                        bundle.commentsForResponses.get(existingResponse.getId()), commentGiverName, commentRecipientName,
                        responseVisibilityMap, commentGiverEmailToNameTable);
                ZoneId sessionTimeZone = bundle.feedbackSession.getTimeZone();
                FeedbackResponseCommentRow frcForAdding = buildFeedbackResponseCommentAddFormTemplate(
                        questionAttributes, existingResponse.getId(), responseVisibilityMap, commentGiverName,
                        commentRecipientName, isFeedbackSessionForInstructor, sessionTimeZone);
                responses.add(new FeedbackSubmissionEditResponse(responseIndx,
                        true, recipientOptionsForQuestion, submissionFormHtml,
                        existingResponse.getId(), comment, frcForAdding));
                responseSubmittedRecipient.add(commentRecipientName);
            } else {
                responses.add(new FeedbackSubmissionEditResponse(responseIndx,
                        true, recipientOptionsForQuestion,
                        submissionFormHtml, existingResponse.getId(), null, null));
            }
            responseIndx++;
        }
        int recipientIndxForUnsubmittedResponse = 0;
        while (responseIndx < numOfResponseBoxes) {
            List<String> recipientOptionsForQuestion = getRecipientOptionsForQuestion(questionAttributes.getId(), null);
            String submissionFormHtml = questionAttributes.getQuestionDetails()
                    .getQuestionWithoutExistingResponseSubmissionFormHtml(
                            isSessionOpenForSubmission, qnIndx, responseIndx,
                            questionAttributes.courseId, numOfResponseBoxes, student);

            if (questionAttributes.getQuestionDetails().isStudentCommentsOnResponsesAllowed()) {
                List<String> recipientListForUnsubmittedResponse = getRecipientList(responseSubmittedRecipient,
                        bundle.getSortedRecipientList(questionAttributes.getId()));
                String recipientName = recipientListForUnsubmittedResponse.get(recipientIndxForUnsubmittedResponse);
                String giverName = account.name;
                ZoneId sessionTimeZone = bundle.feedbackSession.getTimeZone();
                FeedbackResponseCommentRow frcForAdding = buildFeedbackResponseCommentAddFormTemplate(
                        questionAttributes, "",
                        getResponseVisibilityMap(questionAttributes, !isFeedbackSessionForInstructor), giverName,
                        recipientName, isFeedbackSessionForInstructor, sessionTimeZone);
                if (isPreview()) {
                    if (previewInstructor == null) {
                        giverName = getStudentToViewPageAs().name;
                        frcForAdding = buildFeedbackResponseCommentAddFormTemplate(
                                questionAttributes, "", getResponseVisibilityMap(questionAttributes, true), giverName,
                                recipientName, false, sessionTimeZone);
                    } else {
                        giverName = getPreviewInstructor().name;
                        frcForAdding = buildFeedbackResponseCommentAddFormTemplate(
                                questionAttributes, "", getResponseVisibilityMap(questionAttributes, false), giverName,
                                recipientName, true, sessionTimeZone);
                    }
                }
                responses.add(new FeedbackSubmissionEditResponse(responseIndx, false, recipientOptionsForQuestion,
                        submissionFormHtml, "", null, frcForAdding));
                recipientIndxForUnsubmittedResponse++;
            } else {
                responses.add(new FeedbackSubmissionEditResponse(responseIndx, false, recipientOptionsForQuestion,
                        submissionFormHtml, "", null, null));
            }
            responseIndx++;
        }

        return responses;
    }

    private List<String> getRecipientList(List<String> responseSubmittedRecipient, Map<String, String> sortedRecipientList) {
        List<String> recipientList = new ArrayList<String>();
        for (String recipient : sortedRecipientList.values()) {
            if (!responseSubmittedRecipient.contains(recipient)) {
                recipientList.add(recipient);
            }
        }
        return recipientList;
    }

    private FeedbackResponseCommentRow getResponseCommentsForQuestion(
            FeedbackQuestionAttributes questionAttributes,
            List<FeedbackResponseCommentAttributes> frcList, String giverName,
            String recipientName, Map<FeedbackParticipantType, Boolean> responseVisibilityMap, Map<String,
            String> commentGiverEmailToNameTable) {
        ZoneId sessionTimeZone = bundle.feedbackSession.getTimeZone();
        String commentGiver;
        if (isFeedbackSessionForInstructor) {
            commentGiver = account.email;
        } else {
            if (questionAttributes.giverType.isTeam()) {
                commentGiver = student.team;
            } else {
                commentGiver = student.email;
            }
        }

        for (FeedbackResponseCommentAttributes frcAttributes : frcList) {
            if (frcAttributes.commentGiver.equals(commentGiver)) {
                FeedbackResponseCommentRow frcRow = new FeedbackResponseCommentRow(frcAttributes,
                        frcAttributes.commentGiver, giverName, recipientName,
                        getResponseCommentVisibilityString(frcAttributes, questionAttributes),
                        getResponseCommentGiverNameVisibilityString(frcAttributes, questionAttributes),
                        responseVisibilityMap, commentGiverEmailToNameTable, sessionTimeZone);
                setEditDeleteCommentOptionForUser(frcAttributes, frcRow);
                return frcRow;
            }
        }
        return null;
    }

    private void setEditDeleteCommentOptionForUser(FeedbackResponseCommentAttributes frcAttributes,
            FeedbackResponseCommentRow frcRow) {
        String commentGiver;
        if (isModeration) {
            if (isFeedbackSessionForInstructor) {
                commentGiver = previewInstructor.email;
            } else {
                if (frcAttributes.commentGiverType.isTeam()) {
                    commentGiver = student.team;
                } else {
                    commentGiver = student.email;
                }
            }
        } else if (isFeedbackSessionForInstructor) {
            commentGiver = account.email;
        } else {
            if (frcAttributes.commentGiverType.isTeam()) {
                commentGiver = student.team;
            } else {
                commentGiver = student.email;
            }
        }
        if (frcAttributes.commentGiver.equals(commentGiver)) {
            frcRow.enableEditDelete();
        }
    }

    /**
     * Returns true if there is an existing response in the form.
     */
    public boolean getIsResponsePresent() {
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>
                entry : bundle.questionResponseBundle.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
