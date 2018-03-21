package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackQuestionEditForm;
import teammates.ui.template.FeedbackQuestionFeedbackPathSettings;
import teammates.ui.template.FeedbackQuestionVisibilitySettings;
import teammates.ui.template.FeedbackSessionPreviewForm;
import teammates.ui.template.FeedbackSessionsAdditionalSettingsFormSegment;
import teammates.ui.template.FeedbackSessionsForm;

public class InstructorFeedbackEditPageData extends PageData {

    private FeedbackSessionsForm fsForm;
    private List<FeedbackQuestionEditForm> qnForms;
    private FeedbackQuestionEditForm newQnForm;
    private FeedbackSessionPreviewForm previewForm;
    private boolean shouldLoadInEditMode;
    private boolean hasError;
    private CourseDetailsBundle courseDetails;
    private int numOfInstructors;
    private FeedbackSessionAttributes feedbackSession;
    private Map<String, String> resolvedTimeFields = new HashMap<>();

    public InstructorFeedbackEditPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    public void init(FeedbackSessionAttributes feedbackSession, List<FeedbackQuestionAttributes> questions,
                     Map<String, Boolean> questionHasResponses, List<StudentAttributes> studentList,
                     List<InstructorAttributes> instructorList, InstructorAttributes instructor,
                     boolean shouldLoadInEditMode, int numOfInstructors, CourseDetailsBundle courseDetails) {
        Assumption.assertNotNull(feedbackSession);

        buildFsForm(feedbackSession);

        qnForms = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            FeedbackQuestionAttributes question = questions.get(i);
            buildExistingQuestionForm(feedbackSession.getFeedbackSessionName(),
                                      questions.size(), questionHasResponses,
                                      instructor.courseId, question, i + 1);
        }

        this.courseDetails = courseDetails;
        // numOfInstructors can be different from instructorList.size()
        this.numOfInstructors = numOfInstructors;

        this.feedbackSession = feedbackSession;

        buildNewQuestionForm(feedbackSession, questions.size() + 1);

        buildPreviewForm(feedbackSession, studentList, instructorList);
        this.shouldLoadInEditMode = shouldLoadInEditMode;

    }

    private void buildPreviewForm(FeedbackSessionAttributes feedbackSession,
                                    List<StudentAttributes> studentList,
                                    List<InstructorAttributes> instructorList) {
        previewForm = new FeedbackSessionPreviewForm(feedbackSession.getCourseId(), feedbackSession.getFeedbackSessionName(),
                                                     getPreviewAsStudentOptions(studentList),
                                                     getPreviewAsInstructorOptions(instructorList));
    }

    private void buildFsForm(FeedbackSessionAttributes feedbackSession) {
        buildBasicFsForm(feedbackSession, buildFsFormAdditionalSettings(feedbackSession));
    }

    private void buildBasicFsForm(FeedbackSessionAttributes fsa,
                                  FeedbackSessionsAdditionalSettingsFormSegment additionalSettings) {
        String fsDeleteLink = getInstructorFeedbackDeleteLink(fsa.getCourseId(), fsa.getFeedbackSessionName(),
                                                              Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
        String copyToLink = getInstructorFeedbackEditCopyLink();

        fsForm = FeedbackSessionsForm.getFsFormForExistingFs(fsa, additionalSettings,
                                                             fsDeleteLink, copyToLink);
    }

    private FeedbackSessionsAdditionalSettingsFormSegment
            buildFsFormAdditionalSettings(FeedbackSessionAttributes newFeedbackSession) {
        return FeedbackSessionsAdditionalSettingsFormSegment.getFormSegmentWithExistingValues(newFeedbackSession);
    }

    private void buildExistingQuestionForm(String feedbackSessionName,
                                           int questionsSize,
                                           Map<String, Boolean> questionHasResponses,
                                           String courseId, FeedbackQuestionAttributes question,
                                           int questionIndex) {
        FeedbackQuestionEditForm qnForm = new FeedbackQuestionEditForm();
        qnForm.setAction(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT);
        qnForm.setCourseId(courseId);

        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        qnForm.setFeedbackSessionName(feedbackSessionName);
        qnForm.setQuestionText(questionDetails.getQuestionText());
        Text questionDescription = question.getQuestionDescription();
        qnForm.setQuestionDescription(questionDescription == null ? null : questionDescription.getValue());
        qnForm.setQuestionIndex(questionIndex);
        qnForm.setQuestionId(question.getId());
        qnForm.setQuestionTypeDisplayName(questionDetails.getQuestionTypeDisplayName());
        qnForm.setQuestionType(question.questionType);

        qnForm.setQuestionNumberOptions(getQuestionNumberOptions(questionsSize));

        FeedbackQuestionFeedbackPathSettings feedbackPathSettings =
                configureFeedbackPathSettings(question);
        qnForm.setFeedbackPathSettings(feedbackPathSettings);

        FeedbackQuestionVisibilitySettings visibilitySettings = configureVisibilitySettings(question);
        qnForm.setVisibilitySettings(visibilitySettings);

        qnForm.setQuestionHasResponses(questionHasResponses.get(question.getId()));

        qnForm.setQuestionSpecificEditFormHtml(questionDetails.getQuestionSpecificEditFormHtml(questionIndex));
        qnForm.setEditable(false);

        qnForms.add(qnForm);
    }

    private FeedbackQuestionVisibilitySettings configureVisibilitySettings(FeedbackQuestionAttributes question) {
        Map<String, Boolean> isGiverNameVisibleFor = new HashMap<>();
        for (FeedbackParticipantType giverType : question.showGiverNameTo) {
            isGiverNameVisibleFor.put(giverType.name(), true);
        }

        Map<String, Boolean> isRecipientNameVisibleFor = new HashMap<>();
        for (FeedbackParticipantType recipientType : question.showRecipientNameTo) {
            isRecipientNameVisibleFor.put(recipientType.name(), true);
        }

        Map<String, Boolean> isResponsesVisibleFor = new HashMap<>();
        for (FeedbackParticipantType participantType : question.showResponsesTo) {
            isResponsesVisibleFor.put(participantType.name(), true);
        }

        String dropdownMenuLabel = getDropdownMenuLabel(question);

        return new FeedbackQuestionVisibilitySettings(question.getVisibilityMessage(), isResponsesVisibleFor,
                                                      isGiverNameVisibleFor, isRecipientNameVisibleFor, dropdownMenuLabel);
    }

    private String getDropdownMenuLabel(FeedbackQuestionAttributes question) {
        if (isVisibilitySetToAnonymousToRecipientAndInstructors(question)) {
            return Const.FeedbackQuestion.COMMON_VISIBILITY_OPTIONS.get("ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS");
        }

        if (isVisibilitySetToAnonymousToRecipientVisibleToInstructors(question)) {
            return Const.FeedbackQuestion.COMMON_VISIBILITY_OPTIONS.get("ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS");
        }

        if (isVisibilitySetToAnonymousToRecipientAndTeamVisibleToInstructors(question)) {
            return Const.FeedbackQuestion.COMMON_VISIBILITY_OPTIONS
                                         .get("ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS");
        }

        if (isVisibilitySetToVisibleToInstructorsOnly(question)) {
            return Const.FeedbackQuestion.COMMON_VISIBILITY_OPTIONS.get("VISIBLE_TO_INSTRUCTORS_ONLY");
        }

        if (isVisibilitySetToVisibleToRecipientAndInstructors(question)) {
            return Const.FeedbackQuestion.COMMON_VISIBILITY_OPTIONS.get("VISIBLE_TO_RECIPIENT_AND_INSTRUCTORS");
        }

        return "Custom visibility option:";
    }

    private boolean isVisibilitySetToAnonymousToRecipientAndInstructors(FeedbackQuestionAttributes question) {
        boolean isResponsesVisibleOnlyToRecipientAndInstructors = question.showResponsesTo.size() == 2
                && question.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS)
                && question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER);
        boolean isGiverNameVisibleToNoOne = question.showGiverNameTo.isEmpty();

        return isResponsesVisibleOnlyToRecipientAndInstructors && isGiverNameVisibleToNoOne;
    }

    private boolean isVisibilitySetToAnonymousToRecipientVisibleToInstructors(FeedbackQuestionAttributes question) {
        boolean isResponsesVisibleOnlyToRecipientAndInstructors = question.showResponsesTo.size() == 2
                && question.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS)
                && question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER);
        boolean isGiverNameVisibleOnlyToInstructors = question.showGiverNameTo.size() == 1
                && question.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS);

        return isResponsesVisibleOnlyToRecipientAndInstructors && isGiverNameVisibleOnlyToInstructors;
    }

    private boolean isVisibilitySetToAnonymousToRecipientAndTeamVisibleToInstructors(FeedbackQuestionAttributes question) {
        boolean isResponsesVisibleOnlyToRecipientTeamAndInstructors = question.showResponsesTo.size() == 4
                && question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER)
                && question.showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                && question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                && question.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isGiverNameVisibleOnlyToInstructors = question.showGiverNameTo.size() == 1
                && question.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS);

        return isResponsesVisibleOnlyToRecipientTeamAndInstructors && isGiverNameVisibleOnlyToInstructors;
    }

    private boolean isVisibilitySetToVisibleToInstructorsOnly(FeedbackQuestionAttributes question) {
        boolean isResponsesVisibleOnlyToInstructors = question.showResponsesTo.size() == 1
                && question.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isGiverNameVisibleOnlyToInstructors = question.showGiverNameTo.size() == 1
                && question.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isRecipientNameVisibleOnlyToInstructors = question.showRecipientNameTo.size() == 1
                && question.showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS);

        return isResponsesVisibleOnlyToInstructors && isGiverNameVisibleOnlyToInstructors
                && isRecipientNameVisibleOnlyToInstructors;
    }

    private boolean isVisibilitySetToVisibleToRecipientAndInstructors(FeedbackQuestionAttributes question) {
        boolean isResponsesVisibleOnlyToRecipientAndInstructors = question.showResponsesTo.size() == 2
                && question.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS)
                && question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER);
        boolean isGiverNameVisibleOnlyToRecipientAndInstructors = question.showGiverNameTo.size() == 2
                && question.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS)
                && question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER);
        boolean isRecipientNameVisibleOnlyToRecipientAndInstructors = question.showResponsesTo.size() == 2
                && question.showRecipientNameTo.size() == 2
                && question.showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS)
                && question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER);

        return isResponsesVisibleOnlyToRecipientAndInstructors && isGiverNameVisibleOnlyToRecipientAndInstructors
                && isRecipientNameVisibleOnlyToRecipientAndInstructors;
    }

    private FeedbackQuestionFeedbackPathSettings configureFeedbackPathSettings(
            FeedbackQuestionAttributes question) {
        FeedbackQuestionFeedbackPathSettings settings = new FeedbackQuestionFeedbackPathSettings();
        settings.setSelectedGiver(question.giverType);
        settings.setSelectedRecipient(question.recipientType);

        boolean isNumberOfEntitiesToGiveFeedbackToChecked =
                question.numberOfEntitiesToGiveFeedbackTo != Const.MAX_POSSIBLE_RECIPIENTS;
        settings.setNumberOfEntitiesToGiveFeedbackToChecked(isNumberOfEntitiesToGiveFeedbackToChecked);
        settings.setNumOfEntitiesToGiveFeedbackToValue(isNumberOfEntitiesToGiveFeedbackToChecked
                                                       ? question.numberOfEntitiesToGiveFeedbackTo
                                                       : 1);

        boolean isCommonGiver = Const.FeedbackQuestion.COMMON_FEEDBACK_PATHS.containsKey(question.giverType);
        boolean isCommonPath =
                    isCommonGiver && Const.FeedbackQuestion.COMMON_FEEDBACK_PATHS.get(question.giverType)
                                                           .contains(question.recipientType);
        settings.setCommonPathSelected(isCommonPath);

        return settings;
    }

    private void buildNewQuestionForm(FeedbackSessionAttributes feedbackSession, int nextQnNum) {

        String doneEditingLink = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE)
                                .withUserId(account.googleId)
                                .withCourseId(feedbackSession.getCourseId())
                                .withSessionName(feedbackSession.getFeedbackSessionName())
                                .toString();
        newQnForm = FeedbackQuestionEditForm.getNewQnForm(doneEditingLink, feedbackSession,
                                                          getQuestionTypeChoiceOptions(),
                                                          getQuestionNumberOptions(nextQnNum),
                                                          getNewQuestionSpecificEditFormHtml());
    }

    private List<ElementTag> getQuestionNumberOptions(int numQuestions) {
        List<ElementTag> options = new ArrayList<>();

        for (int opt = 1; opt < numQuestions + 1; opt++) {
            ElementTag option = createOption(String.valueOf(opt), String.valueOf(opt), false);
            options.add(option);
        }

        return options;
    }

    /**
     * Returns String of HTML containing a list of options for selecting question type.
     * Used in instructorFeedbackEdit.jsp for selecting the question type for a new question.
     */
    public String getQuestionTypeChoiceOptions() {
        StringBuilder options = new StringBuilder();
        for (FeedbackQuestionType type : FeedbackQuestionType.values()) {
            options.append(type.getFeedbackQuestionDetailsInstance().getQuestionTypeChoiceOption());
        }
        return options.toString();
    }

    /**
     * Get all question specific edit forms.
     */
    public String getNewQuestionSpecificEditFormHtml() {
        StringBuilder newQuestionSpecificEditForms = new StringBuilder();
        for (FeedbackQuestionType type : FeedbackQuestionType.values()) {
            newQuestionSpecificEditForms.append(
                    type.getFeedbackQuestionDetailsInstance().getNewQuestionSpecificEditFormHtml());
        }
        return newQuestionSpecificEditForms.toString();
    }

    private List<ElementTag> getPreviewAsInstructorOptions(List<InstructorAttributes> instructorList) {
        List<ElementTag> results = new ArrayList<>();

        for (InstructorAttributes instructor : instructorList) {
            ElementTag option = createOption(instructor.name, instructor.email);
            results.add(option);
        }

        return results;
    }

    private List<ElementTag> getPreviewAsStudentOptions(List<StudentAttributes> studentList) {
        List<ElementTag> results = new ArrayList<>();

        for (StudentAttributes student : studentList) {
            ElementTag option = createOption("[" + student.team + "] " + student.name, student.email);
            results.add(option);
        }

        return results;
    }

    public CourseDetailsBundle getCourseDetails() {
        return courseDetails;
    }

    public int getNumOfInstructors() {
        return numOfInstructors;
    }

    public FeedbackSessionsForm getFsForm() {
        return fsForm;
    }

    public List<FeedbackQuestionEditForm> getQnForms() {
        return qnForms;
    }

    public FeedbackQuestionEditForm getNewQnForm() {
        return newQnForm;
    }

    public FeedbackSessionPreviewForm getPreviewForm() {
        return previewForm;
    }

    /**
     * Retrieves the link to submit the request for copy of session.
     * Also contains feedback page link to return after the action.
     * @return form submit action link
     */
    public String getEditCopyActionLink() {
        return getInstructorFeedbackEditCopyActionLink(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
    }

    public boolean getHasError() {
        return hasError;
    }

    public void setHasError(boolean value) {
        this.hasError = value;
    }

    public boolean getShouldLoadInEditMode() {
        return shouldLoadInEditMode;
    }

    public FeedbackSessionAttributes getFeedbackSession() {
        return feedbackSession;
    }

    public void putResolvedTimeField(String fieldInputId, String fieldInputValue) {
        resolvedTimeFields.put(fieldInputId, fieldInputValue);
    }

    public Map<String, String> getResolvedTimeFields() {
        return resolvedTimeFields;
    }
}
