package teammates.test.scenariobuilder;

import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.questions.FeedbackConstantSumOptionsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumRecipientsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRankOptionsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;

/**
 * Builder for FeedbackResponse entities used in test scenarios.
 */
public final class GivenFeedbackResponse extends GivenBase<FeedbackResponse> {
    public GivenFeedbackResponse(GivenData given, UUID feedbackResponseId) {
        super(given);
        this.entity = defaultFeedbackResponse(feedbackResponseId);
    }

    /**
     * Sets the feedback question for the response.
     */
    public GivenFeedbackResponse feedbackQuestion(String feedbackQuestionAlias) {
        assert entity.getFeedbackQuestion() == null : "Feedback question has already been set for this response";
        FeedbackQuestion feedbackQuestion = given.getOrCreate(
                feedbackQuestionAlias, given.dataBundle.feedbackQuestions, given::feedbackQuestion);
        entity.setFeedbackQuestion(feedbackQuestion);
        return this;
    }

    /**
     * Sets the feedback question for the response with a specified feedback session.
     */
    public GivenFeedbackResponse feedbackSession(String feedbackSessionAlias) {
        assert entity.getFeedbackQuestion() == null : "Feedback question has already been set for this response";
        String feedbackQuestionAlias = GivenFeedbackQuestion.getDefaultAlias(feedbackSessionAlias);
        FeedbackQuestion feedbackQuestion = given.getOrCreate(
                feedbackQuestionAlias, given.dataBundle.feedbackQuestions,
                (String qAlias) -> given.feedbackQuestion(qAlias, q -> q.feedbackSession(feedbackSessionAlias)));
        entity.setFeedbackQuestion(feedbackQuestion);
        return this;
    }

    /**
     * Sets the course for the response through a default feedback session and question.
     */
    public GivenFeedbackResponse course(String courseAlias) {
        String feedbackSessionAlias = GivenFeedbackSession.getDefaultAlias(courseAlias);
        given.getOrCreate(
                feedbackSessionAlias, given.dataBundle.feedbackSessions,
                (String fsAlias) -> given.feedbackSession(fsAlias, fs -> fs.course(courseAlias)));
        return feedbackSession(feedbackSessionAlias);
    }

    /**
     * Sets a student as the giver.
     */
    public GivenFeedbackResponse giverStudent(String studentAlias) {
        assert entity.getGiver() == null : "Giver has already been set for this response";
        Student student = given.getOrCreate(
                studentAlias, given.dataBundle.students, (String sAlias) -> given.student(sAlias,
                        s -> s.course(getFeedbackQuestionCourseAlias())));
        ResponseGiver giver = new ResponseGiver(student);
        entity.setGiver(giver);
        return this;
    }

    /**
     * Sets an instructor as the giver.
     */
    public GivenFeedbackResponse giverInstructor(String instructorAlias) {
        assert entity.getGiver() == null : "Giver has already been set for this response";
        Instructor instructor = given.getOrCreate(
                instructorAlias, given.dataBundle.instructors, (String iAlias) -> given.instructor(iAlias,
                        i -> i.course(getFeedbackQuestionCourseAlias())));
        ResponseGiver giver = new ResponseGiver(instructor);
        entity.setGiver(giver);
        return this;
    }

    /**
     * Sets a team as the giver.
     */
    public GivenFeedbackResponse giverTeam(String teamAlias) {
        assert entity.getGiver() == null : "Giver has already been set for this response";
        Team team = given.getOrCreate(
                teamAlias, given.dataBundle.teams, (String tAlias) -> given.team(tAlias,
                        t -> t.course(getFeedbackQuestionCourseAlias())));
        ResponseGiver giver = new ResponseGiver(team);
        entity.setGiver(giver);
        return this;
    }

    /**
     * Sets a student as the recipient.
     */
    public GivenFeedbackResponse recipientStudent(String studentAlias) {
        assert entity.getRecipient() == null : "Recipient has already been set for this response";
        Student student = given.getOrCreate(
                studentAlias, given.dataBundle.students, (String sAlias) -> given.student(sAlias,
                        s -> s.course(getFeedbackQuestionCourseAlias())));
        ResponseRecipient recipient = new ResponseRecipient(student);
        entity.setRecipient(recipient);
        return this;
    }

    /**
     * Sets an instructor as the recipient.
     */
    public GivenFeedbackResponse recipientInstructor(String instructorAlias) {
        assert entity.getRecipient() == null : "Recipient has already been set for this response";
        Instructor instructor = given.getOrCreate(
                instructorAlias, given.dataBundle.instructors, (String iAlias) -> given.instructor(iAlias,
                        i -> i.course(getFeedbackQuestionCourseAlias())));
        ResponseRecipient recipient = new ResponseRecipient(instructor);
        entity.setRecipient(recipient);
        return this;
    }

    /**
     * Sets a team as the recipient.
     */
    public GivenFeedbackResponse recipientTeam(String teamAlias) {
        assert entity.getRecipient() == null : "Recipient has already been set for this response";
        Team team = given.getOrCreate(
                teamAlias, given.dataBundle.teams, (String tAlias) -> given.team(tAlias,
                        t -> t.course(getFeedbackQuestionCourseAlias())));
        ResponseRecipient recipient = new ResponseRecipient(team);
        entity.setRecipient(recipient);
        return this;
    }

    /**
     * Sets no specific recipient.
     */
    public GivenFeedbackResponse noSpecificRecipient() {
        assert entity.getRecipient() == null : "Recipient has already been set for this response";
        ResponseRecipient recipient = new ResponseRecipient();
        entity.setRecipient(recipient);
        return this;
    }

    /**
     * Sets text response details.
     */
    public GivenFeedbackResponse text(String answer) {
        return details(new FeedbackTextResponseDetails(answer));
    }

    /**
     * Sets text response details with a representative answer.
     */
    public GivenFeedbackResponse text() {
        return type(FeedbackQuestionType.TEXT);
    }

    /**
     * Sets MCQ response details with a representative answer.
     */
    public GivenFeedbackResponse mcq() {
        return type(FeedbackQuestionType.MCQ);
    }

    /**
     * Sets MSQ response details with a representative answer.
     */
    public GivenFeedbackResponse msq() {
        return type(FeedbackQuestionType.MSQ);
    }

    /**
     * Sets numerical scale response details with a representative answer.
     */
    public GivenFeedbackResponse numScale() {
        return type(FeedbackQuestionType.NUMSCALE);
    }

    /**
     * Sets constant sum options response details with a representative answer.
     */
    public GivenFeedbackResponse constSumOptions() {
        return type(FeedbackQuestionType.CONSTSUM_OPTIONS);
    }

    /**
     * Sets constant sum recipients response details with a representative answer.
     */
    public GivenFeedbackResponse constSumRecipients() {
        return type(FeedbackQuestionType.CONSTSUM_RECIPIENTS);
    }

    /**
     * Sets contribution response details with a representative answer.
     */
    public GivenFeedbackResponse contribution() {
        return type(FeedbackQuestionType.CONTRIB);
    }

    /**
     * Sets rubric response details with a representative answer.
     */
    public GivenFeedbackResponse rubric() {
        return type(FeedbackQuestionType.RUBRIC);
    }

    /**
     * Sets rank options response details with a representative answer.
     */
    public GivenFeedbackResponse rankOptions() {
        return type(FeedbackQuestionType.RANK_OPTIONS);
    }

    /**
     * Sets rank recipients response details with a representative answer.
     */
    public GivenFeedbackResponse rankRecipients() {
        return type(FeedbackQuestionType.RANK_RECIPIENTS);
    }

    /**
     * Sets response details for the specified type with a representative answer.
     */
    public GivenFeedbackResponse type(FeedbackQuestionType questionType) {
        return details(getDefaultResponseDetails(questionType, entity.getId()));
    }

    /**
     * Sets the response details, and therefore the response type.
     */
    public GivenFeedbackResponse details(FeedbackResponseDetails responseDetails) {
        UUID feedbackResponseId = entity.getId();
        FeedbackQuestion feedbackQuestion = entity.getFeedbackQuestion();
        this.entity = FeedbackResponse.makeResponse(
                entity.getGiver(), entity.getRecipient(), responseDetails, entity.getGiverComment());
        entity.setId(feedbackResponseId);
        entity.setFeedbackQuestion(feedbackQuestion);
        return this;
    }

    /**
     * Sets the giver comment.
     */
    public GivenFeedbackResponse giverComment(String giverComment) {
        entity.setGiverComment(giverComment);
        return this;
    }

    @Override
    void ensureConsistent() {
        if (entity.getFeedbackQuestion() == null) {
            this.defaultFeedbackQuestion();
        }

        if (entity.getGiver() == null) {
            this.giverStudent("default:feedback-response-giver:" + entity.getId());
        }

        if (entity.getRecipient() == null) {
            entity.setRecipient(getSelfRecipient());
        }

        if (entity.getFeedbackResponseDetailsCopy().getQuestionType() != entity.getFeedbackQuestion().getQuestionType()) {
            details(getDefaultResponseDetails(entity.getFeedbackQuestion().getQuestionType(), entity.getId()));
        }

        entity.getFeedbackQuestion().addFeedbackResponse(entity);
    }

    private String getFeedbackQuestionCourseAlias() {
        if (entity.getFeedbackQuestion() == null) {
            this.defaultFeedbackQuestion();
        }

        return given.getAlias(entity.getFeedbackQuestion().getFeedbackSession().getCourse());
    }

    private void defaultFeedbackQuestion() {
        FeedbackQuestionType questionType = entity.getFeedbackResponseDetailsCopy().getQuestionType();
        FeedbackQuestion feedbackQuestion = given.getOrCreate(
                getFeedbackQuestionAlias(), given.dataBundle.feedbackQuestions,
                (String qAlias) -> given.feedbackQuestion(qAlias, q -> q.type(questionType)));
        entity.setFeedbackQuestion(feedbackQuestion);
    }

    private String getFeedbackQuestionAlias() {
        String courseAlias = GivenCourse.getDefaultAlias();
        String feedbackSessionAlias = GivenFeedbackSession.getDefaultAlias(courseAlias);
        return GivenFeedbackQuestion.getDefaultAlias(feedbackSessionAlias);
    }

    private ResponseRecipient getSelfRecipient() {
        ResponseGiver giver = entity.getGiver();
        if (giver.isGiverTeam()) {
            return new ResponseRecipient(giver.getGiverTeam());
        }

        return new ResponseRecipient(giver.getGiverUser());
    }

    private FeedbackResponseDetails getDefaultResponseDetails(FeedbackQuestionType questionType, UUID feedbackResponseId) {
        switch (questionType) {
        case TEXT:
            return new FeedbackTextResponseDetails("answer:" + feedbackResponseId);
        case MCQ:
            FeedbackMcqResponseDetails mcqDetails = new FeedbackMcqResponseDetails();
            mcqDetails.setAnswer("Option 1");
            return mcqDetails;
        case MSQ:
            FeedbackMsqResponseDetails msqDetails = new FeedbackMsqResponseDetails();
            msqDetails.setAnswers(List.of("Option 1"));
            return msqDetails;
        case NUMSCALE:
            FeedbackNumericalScaleResponseDetails numScaleDetails = new FeedbackNumericalScaleResponseDetails();
            numScaleDetails.setAnswer(3);
            return numScaleDetails;
        case CONSTSUM_OPTIONS:
            FeedbackConstantSumOptionsResponseDetails constSumOptionsDetails =
                    new FeedbackConstantSumOptionsResponseDetails();
            constSumOptionsDetails.setAnswers(List.of(50, 50));
            return constSumOptionsDetails;
        case CONSTSUM_RECIPIENTS:
            FeedbackConstantSumRecipientsResponseDetails constSumRecipientsDetails =
                    new FeedbackConstantSumRecipientsResponseDetails();
            constSumRecipientsDetails.setAnswers(List.of(100));
            return constSumRecipientsDetails;
        case CONTRIB:
            FeedbackContributionResponseDetails contributionDetails = new FeedbackContributionResponseDetails();
            contributionDetails.setAnswer(100);
            return contributionDetails;
        case RUBRIC:
            FeedbackRubricResponseDetails rubricDetails = new FeedbackRubricResponseDetails();
            rubricDetails.setAnswer(List.of(0));
            return rubricDetails;
        case RANK_OPTIONS:
            FeedbackRankOptionsResponseDetails rankOptionsDetails = new FeedbackRankOptionsResponseDetails();
            rankOptionsDetails.setAnswers(List.of(1, 2));
            return rankOptionsDetails;
        case RANK_RECIPIENTS:
            FeedbackRankRecipientsResponseDetails rankRecipientsDetails = new FeedbackRankRecipientsResponseDetails();
            rankRecipientsDetails.setAnswer(1);
            return rankRecipientsDetails;
        default:
            throw new AssertionError("Unsupported feedback question type: " + questionType);
        }
    }

    private FeedbackResponse defaultFeedbackResponse(UUID feedbackResponseId) {
        FeedbackResponse feedbackResponse = FeedbackResponse.makeResponse(
                null, null, getDefaultResponseDetails(FeedbackQuestionType.TEXT, feedbackResponseId), null);
        feedbackResponse.setId(feedbackResponseId);
        return feedbackResponse;
    }
}
