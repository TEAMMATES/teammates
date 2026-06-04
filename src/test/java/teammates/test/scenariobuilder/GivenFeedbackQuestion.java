package teammates.test.scenariobuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.datatransfer.questions.FeedbackConstantSumOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumRecipientsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;

/**
 * Builder for FeedbackQuestion entities used in test scenarios.
 */
public final class GivenFeedbackQuestion extends GivenBase<FeedbackQuestion> {
    public GivenFeedbackQuestion(GivenData given, UUID feedbackQuestionId) {
        super(given);
        this.entity = defaultFeedbackQuestion(feedbackQuestionId);
    }

    /**
     * Sets the feedback session for the feedback question.
     */
    public GivenFeedbackQuestion feedbackSession(String feedbackSessionAlias) {
        assert entity.getFeedbackSession() == null : "Feedback session has already been set for this feedback question";
        FeedbackSession feedbackSession = given.getOrCreate(
                feedbackSessionAlias, given.dataBundle.feedbackSessions, given::feedbackSession);
        entity.setFeedbackSession(feedbackSession);
        return this;
    }

    /**
     * Sets the question number for the feedback question.
     */
    public GivenFeedbackQuestion number(int questionNumber) {
        entity.setQuestionNumber(questionNumber);
        return this;
    }

    /**
     * Sets the description for the feedback question.
     */
    public GivenFeedbackQuestion description(String description) {
        entity.setDescription(description);
        return this;
    }

    /**
     * Sets the question text for a text feedback question.
     */
    public GivenFeedbackQuestion text(String questionText) {
        return details(new FeedbackTextQuestionDetails(questionText));
    }

    /**
     * Sets the question as a text question with default details.
     */
    public GivenFeedbackQuestion text() {
        return type(FeedbackQuestionType.TEXT);
    }

    /**
     * Sets the question as an MCQ question with default details.
     */
    public GivenFeedbackQuestion mcq() {
        return type(FeedbackQuestionType.MCQ);
    }

    /**
     * Sets the question as an MSQ question with default details.
     */
    public GivenFeedbackQuestion msq() {
        return type(FeedbackQuestionType.MSQ);
    }

    /**
     * Sets the question as a numerical scale question with default details.
     */
    public GivenFeedbackQuestion numScale() {
        return type(FeedbackQuestionType.NUMSCALE);
    }

    /**
     * Sets the question as a constant sum options question with default details.
     */
    public GivenFeedbackQuestion constSumOptions() {
        return type(FeedbackQuestionType.CONSTSUM_OPTIONS);
    }

    /**
     * Sets the question as a constant sum recipients question with default details.
     */
    public GivenFeedbackQuestion constSumRecipients() {
        return type(FeedbackQuestionType.CONSTSUM_RECIPIENTS);
    }

    /**
     * Sets the question as a contribution question with default details.
     */
    public GivenFeedbackQuestion contribution() {
        return type(FeedbackQuestionType.CONTRIB);
    }

    /**
     * Sets the question as a rubric question with default details.
     */
    public GivenFeedbackQuestion rubric() {
        return type(FeedbackQuestionType.RUBRIC);
    }

    /**
     * Sets the question as a rank options question with default details.
     */
    public GivenFeedbackQuestion rankOptions() {
        return type(FeedbackQuestionType.RANK_OPTIONS);
    }

    /**
     * Sets the question as a rank recipients question with default details.
     */
    public GivenFeedbackQuestion rankRecipients() {
        return type(FeedbackQuestionType.RANK_RECIPIENTS);
    }

    /**
     * Sets the question type and populates the details with representative defaults.
     */
    public GivenFeedbackQuestion type(FeedbackQuestionType questionType) {
        return details(getDefaultQuestionDetails(questionType, entity.getId()));
    }

    /**
     * Sets the details, and therefore the type, for the feedback question.
     */
    public GivenFeedbackQuestion details(FeedbackQuestionDetails questionDetails) {
        UUID feedbackQuestionId = entity.getId();
        FeedbackSession feedbackSession = entity.getFeedbackSession();
        this.entity = FeedbackQuestion.makeQuestion(
                entity.getQuestionNumber(), entity.getDescription(), entity.getGiverType(), entity.getRecipientType(),
                entity.getNumOfEntitiesToGiveFeedbackTo(), new ArrayList<>(entity.getShowResponsesTo()),
                new ArrayList<>(entity.getShowGiverNameTo()), new ArrayList<>(entity.getShowRecipientNameTo()),
                questionDetails);
        entity.setId(feedbackQuestionId);
        entity.setFeedbackSession(feedbackSession);
        return this;
    }

    /**
     * Sets the giver type for the feedback question.
     */
    public GivenFeedbackQuestion giverType(QuestionGiverType giverType) {
        entity.setGiverType(giverType);
        return this;
    }

    /**
     * Sets the recipient type for the feedback question.
     */
    public GivenFeedbackQuestion recipientType(QuestionRecipientType recipientType) {
        entity.setRecipientType(recipientType);
        return this;
    }

    /**
     * Sets the number of entities each giver gives feedback to.
     */
    public GivenFeedbackQuestion numOfEntitiesToGiveFeedbackTo(int numOfEntitiesToGiveFeedbackTo) {
        entity.setNumOfEntitiesToGiveFeedbackTo(numOfEntitiesToGiveFeedbackTo);
        return this;
    }

    /**
     * Sets who can see responses to the feedback question.
     */
    public GivenFeedbackQuestion showResponsesTo(ViewerType... viewerTypes) {
        entity.setShowResponsesTo(viewerTypesList(viewerTypes));
        return this;
    }

    /**
     * Sets who can see giver names for responses to the feedback question.
     */
    public GivenFeedbackQuestion showGiverNameTo(ViewerType... viewerTypes) {
        entity.setShowGiverNameTo(viewerTypesList(viewerTypes));
        return this;
    }

    /**
     * Sets who can see recipient names for responses to the feedback question.
     */
    public GivenFeedbackQuestion showRecipientNameTo(ViewerType... viewerTypes) {
        entity.setShowRecipientNameTo(viewerTypesList(viewerTypes));
        return this;
    }

    @Override
    void ensureConsistent() {
        if (entity.getFeedbackSession() == null) {
            String courseAlias = GivenCourse.getDefaultAlias();
            String feedbackSessionAlias = GivenFeedbackSession.getDefaultAlias(courseAlias);
            this.feedbackSession(feedbackSessionAlias);
        }

        if (entity.getQuestionNumber() == null) {
            entity.setQuestionNumber(getNextQuestionNumber());
        }

        entity.getFeedbackSession().addFeedbackQuestion(entity);
    }

    private int getNextQuestionNumber() {
        return given.dataBundle.feedbackQuestions.values().stream()
                .filter(question -> Objects.equals(question.getFeedbackSession(), entity.getFeedbackSession()))
                .map(FeedbackQuestion::getQuestionNumber)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private static List<ViewerType> viewerTypesList(ViewerType... viewerTypes) {
        return new ArrayList<>(Arrays.asList(viewerTypes));
    }

    private static List<ViewerType> defaultViewerTypes() {
        return viewerTypesList(ViewerType.GIVER, ViewerType.RECEIVER, ViewerType.INSTRUCTORS);
    }

    private FeedbackQuestionDetails getDefaultQuestionDetails(FeedbackQuestionType questionType, UUID feedbackQuestionId) {
        String questionText = "question:" + feedbackQuestionId;
        switch (questionType) {
        case TEXT:
            return new FeedbackTextQuestionDetails(questionText);
        case MCQ:
            FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails(questionText);
            mcqDetails.setMcqChoices(defaultOptions());
            return mcqDetails;
        case MSQ:
            FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails(questionText);
            msqDetails.setMsqChoices(defaultOptions());
            return msqDetails;
        case NUMSCALE:
            return new FeedbackNumericalScaleQuestionDetails(questionText);
        case CONSTSUM_OPTIONS:
            FeedbackConstantSumOptionsQuestionDetails constSumOptionsDetails =
                    new FeedbackConstantSumOptionsQuestionDetails(questionText);
            constSumOptionsDetails.setConstSumOptions(defaultOptions());
            return constSumOptionsDetails;
        case CONSTSUM_RECIPIENTS:
            return new FeedbackConstantSumRecipientsQuestionDetails(questionText);
        case CONTRIB:
            return new FeedbackContributionQuestionDetails(questionText);
        case RUBRIC:
            FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails(questionText);
            rubricDetails.setRubricChoices(defaultOptions());
            rubricDetails.setRubricSubQuestions(List.of("Sub-question"));
            rubricDetails.setRubricDescriptions(List.of(List.of("", "")));
            return rubricDetails;
        case RANK_OPTIONS:
            FeedbackRankOptionsQuestionDetails rankOptionsDetails = new FeedbackRankOptionsQuestionDetails(questionText);
            rankOptionsDetails.setOptions(defaultOptions());
            return rankOptionsDetails;
        case RANK_RECIPIENTS:
            return new FeedbackRankRecipientsQuestionDetails(questionText);
        default:
            throw new AssertionError("Unsupported feedback question type: " + questionType);
        }
    }

    private static List<String> defaultOptions() {
        return List.of("Option 1", "Option 2");
    }

    private FeedbackQuestion defaultFeedbackQuestion(UUID feedbackQuestionId) {
        FeedbackQuestion feedbackQuestion = FeedbackQuestion.makeQuestion(
                null, "description:" + feedbackQuestionId, QuestionGiverType.SESSION_CREATOR,
                QuestionRecipientType.SELF, Const.MAX_POSSIBLE_RECIPIENTS, defaultViewerTypes(),
                defaultViewerTypes(), defaultViewerTypes(),
                getDefaultQuestionDetails(FeedbackQuestionType.TEXT, feedbackQuestionId));
        feedbackQuestion.setId(feedbackQuestionId);
        return feedbackQuestion;
    }

    /**
     * Generates a default alias for a feedback question in the specified feedback session.
     */
    public static String getDefaultAlias(String feedbackSessionAlias) {
        return "default:" + feedbackSessionAlias;
    }
}
