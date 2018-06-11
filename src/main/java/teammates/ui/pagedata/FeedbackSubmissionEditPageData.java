package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.ui.template.FeedbackSubmissionEditQuestion;
import teammates.ui.template.FeedbackSubmissionEditResponse;
import teammates.ui.template.StudentFeedbackSubmissionEditQuestionsWithResponses;

public class FeedbackSubmissionEditPageData extends PageData {
    public FeedbackSessionQuestionsBundle bundle;
    private Map<String, StudentAttributes> studentCourseDetails;
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

    public FeedbackSubmissionEditPageData(AccountAttributes account, StudentAttributes student, String sessionToken) {
        super(account, student, sessionToken);
        isPreview = false;
        isModeration = false;
        isShowRealQuestionNumber = false;
        isHeaderHidden = false;
        studentCourseDetails = new HashMap<>();
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

    public Map<String, StudentAttributes> getStudentCourseDetails() {
        return studentCourseDetails;
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

    public void setCourseStudentDetails(List<StudentAttributes> studentList) {
        for (StudentAttributes student : studentList) {
            studentCourseDetails.put(student.email, student);
        }
    }

    public List<String> getRecipientOptionsForQuestion(String feedbackQuestionId, int numOfResponseBoxes,
                                                       String currentlySelectedOption) {

        if (this.bundle == null) {
            return null;
        }
        int maxResponsesPossible = bundle.recipientList.get(feedbackQuestionId).size();
        final boolean isNumResponsesMax = numOfResponseBoxes == maxResponsesPossible;

        Map<String, String> emailNamePair = this.bundle.getSortedRecipientList(feedbackQuestionId);

        List<String> result = new ArrayList<>();
        // Add an empty option first.
        result.add("<option value=\"\" " + (currentlySelectedOption == null ? "selected>" : ">")
                   + "</option>");

        emailNamePair.forEach((email, name) -> {
            boolean isSelected = SanitizationHelper.desanitizeFromHtml(email)
                                             .equals(currentlySelectedOption);
            String section = "";
            String team = "";
            if (!isNumResponsesMax && studentCourseDetails.containsKey(email)) {
                StudentAttributes student = studentCourseDetails.get(email);
                if (student.section != null) {
                    section = student.section + ": ";
                }
                if (student.team != null) {
                    team = student.team + ": ";
                }
            }
            result.add("<option value=\"" + sanitizeForHtml(email) + "\"" + (isSelected ? " selected" : "")
                       + "section-team-info=\"" + sanitizeForHtml(section) + sanitizeForHtml(team) + "\"" + ">"
                       + sanitizeForHtml(name) + "</option>"
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

            questionsWithResponses.add(new StudentFeedbackSubmissionEditQuestionsWithResponses(
                    question, responses, numOfResponseBoxes, maxResponsesPossible));
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
        int responseIndx = 0;

        for (FeedbackResponseAttributes existingResponse : existingResponses) {
            if (!isResponseRecipientValid(existingResponse)) {
                // A response recipient can be invalid due to submission adjustment failure
                continue;
            }
            List<String> recipientOptionsForQuestion = getRecipientOptionsForQuestion(
                                            questionAttributes.getId(), numOfResponseBoxes,
                                                existingResponse.recipient);

            String submissionFormHtml = questionAttributes.getQuestionDetails()
                                            .getQuestionWithExistingResponseSubmissionFormHtml(
                                                isSessionOpenForSubmission, qnIndx, responseIndx,
                                                questionAttributes.courseId, numOfResponseBoxes,
                                                existingResponse.getResponseDetails(), student);

            responses.add(new FeedbackSubmissionEditResponse(responseIndx, true, recipientOptionsForQuestion,
                                                                 submissionFormHtml, existingResponse.getId()));
            responseIndx++;
        }

        while (responseIndx < numOfResponseBoxes) {
            List<String> recipientOptionsForQuestion = getRecipientOptionsForQuestion(questionAttributes.getId(),
                                            numOfResponseBoxes, null);
            String submissionFormHtml = questionAttributes.getQuestionDetails()
                                            .getQuestionWithoutExistingResponseSubmissionFormHtml(
                                                isSessionOpenForSubmission, qnIndx, responseIndx,
                                                questionAttributes.courseId, numOfResponseBoxes, student);

            responses.add(new FeedbackSubmissionEditResponse(responseIndx, false, recipientOptionsForQuestion,
                                                             submissionFormHtml, ""));
            responseIndx++;
        }

        return responses;
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
