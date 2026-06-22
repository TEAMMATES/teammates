package teammates.logic.statistics;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackConstantSumOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumOptionsResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackConstsumOptionsStatistics;
import teammates.common.datatransfer.statistics.FeedbackConstsumOptionsStatistics.ConstsumOptionRow;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatisticsView;

/**
 * Tests for {@link FeedbackConstsumOptionsQuestionStatisticsCalculator}.
 */
public class FeedbackConstsumOptionsQuestionStatisticsCalculatorTest extends BaseStatisticsTestCase {

    private final FeedbackConstsumOptionsQuestionStatisticsCalculator calculator =
            new FeedbackConstsumOptionsQuestionStatisticsCalculator();

    // ==================== Course-wide ====================

    @Test
    public void calculateCourseWide_multipleResponses_returnsRowsPerOption() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackConstsumOptionsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        assertEquals(statistics.getStatisticsView(), FeedbackQuestionResultsStatisticsView.COURSE_WIDE);
        List<ConstsumOptionRow> options = statistics.getOptions();
        assertEquals(options.size(), 3);

        // Options are in question-order: Alpha, Beta, Gamma
        ConstsumOptionRow alpha = options.get(0);
        assertEquals(alpha.getOption(), "Alpha");
        assertEquals(alpha.getTotal(), 150); // 80 + 70
        assertEquals(alpha.getAverage(), 75.0);
        assertEquals(alpha.getPointsReceived(), List.of(70, 80)); // sorted ascending

        ConstsumOptionRow beta = options.get(1);
        assertEquals(beta.getOption(), "Beta");
        assertEquals(beta.getTotal(), 50); // 20 + 30
        assertEquals(beta.getAverage(), 25.0);
        assertEquals(beta.getPointsReceived(), List.of(20, 30));

        ConstsumOptionRow gamma = options.get(2);
        assertEquals(gamma.getOption(), "Gamma");
        assertEquals(gamma.getTotal(), 0); // 0 + 0
        assertEquals(gamma.getAverage(), 0.0);
        assertEquals(gamma.getPointsReceived(), List.of(0, 0));
    }

    @Test
    public void calculateCourseWide_noResponses_returnsEmptyPoints() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question");

        FeedbackConstsumOptionsStatistics statistics = calculator.calculateCourseWide(
                question("question"), List.of(), bundle);

        List<ConstsumOptionRow> options = statistics.getOptions();
        assertEquals(options.size(), 3);
        for (ConstsumOptionRow row : options) {
            assertEquals(row.getTotal(), 0);
            assertEquals(row.getAverage(), 0.0);
            assertEquals(row.getPointsReceived().size(), 0);
        }
    }

    @Test
    public void calculateCourseWide_optionOrderPreservedFromQuestion() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackConstsumOptionsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        List<ConstsumOptionRow> options = statistics.getOptions();
        assertEquals(options.get(0).getOption(), "Alpha");
        assertEquals(options.get(1).getOption(), "Beta");
        assertEquals(options.get(2).getOption(), "Gamma");
    }

    @Test
    public void calculateCourseWide_averageRoundedToTwoDecimals() {
        createScenarioForRounding();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackConstsumOptionsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        ConstsumOptionRow alpha = statistics.getOptions().get(0);
        // Alpha receives: 10, 20, 30 → total=60, avg=20.0
        assertEquals(alpha.getTotal(), 60);
        assertEquals(alpha.getAverage(), 20.0);
    }

    @Test
    public void calculateCourseWide_averageRoundedToTwoDecimalsNonInteger() {
        createScenarioForRoundingNonInteger();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackConstsumOptionsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        ConstsumOptionRow alpha = statistics.getOptions().get(0);
        // Alpha receives: 10, 20, 31 → total=61, avg=61/3=20.33...
        assertEquals(alpha.getTotal(), 61);
        assertEquals(alpha.getAverage(), 20.33);
    }

    // ==================== Recipient ====================

    @Test
    public void calculateForRecipient_filtersToRecipientResponsesOnly() {
        createScenarioWithSelfRecipient();
        // Pass both alice and bob responses; only alice's should be included
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackConstsumOptionsStatistics statistics = calculator.calculateForRecipient(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle,
                student("alice"));

        assertEquals(statistics.getStatisticsView(), FeedbackQuestionResultsStatisticsView.RECIPIENT);
        List<ConstsumOptionRow> options = statistics.getOptions();
        assertEquals(options.size(), 3);

        ConstsumOptionRow alpha = options.get(0);
        assertEquals(alpha.getTotal(), 80); // only alice's response (80), not bob's (70)
        assertEquals(alpha.getPointsReceived(), List.of(80));
    }

    // ==================== Scenario builders ====================

    private void createScenario() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails(List.of("Alpha", "Beta", "Gamma")))
                .recipientType(QuestionRecipientType.NONE));
        // alice: Alpha=80, Beta=20, Gamma=0
        constsumResponseNoRecipient("r1", "alice", List.of(80, 20, 0));
        // bob: Alpha=70, Beta=30, Gamma=0
        constsumResponseNoRecipient("r2", "bob", List.of(70, 30, 0));
    }

    private void createScenarioForRounding() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.student("charlie", s -> s.course("course").team("team").name("Charlie").email("charlie@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails(List.of("Alpha", "Beta")))
                .recipientType(QuestionRecipientType.NONE));
        constsumResponseNoRecipient("r1", "alice", List.of(10, 90));
        constsumResponseNoRecipient("r2", "bob", List.of(20, 80));
        constsumResponseNoRecipient("r3", "charlie", List.of(30, 70));
    }

    private void createScenarioForRoundingNonInteger() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.student("charlie", s -> s.course("course").team("team").name("Charlie").email("charlie@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails(List.of("Alpha", "Beta")))
                .recipientType(QuestionRecipientType.NONE));
        constsumResponseNoRecipient("r1", "alice", List.of(10, 90));
        constsumResponseNoRecipient("r2", "bob", List.of(20, 80));
        constsumResponseNoRecipient("r3", "charlie", List.of(31, 69));
    }

    private FeedbackConstantSumOptionsQuestionDetails buildDetails(List<String> options) {
        FeedbackConstantSumOptionsQuestionDetails details = new FeedbackConstantSumOptionsQuestionDetails();
        details.setConstSumOptions(options);
        details.setPoints(100);
        details.setPointsPerOption(false);
        return details;
    }

    private void createScenarioWithSelfRecipient() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails(List.of("Alpha", "Beta", "Gamma")))
                .recipientType(QuestionRecipientType.SELF));
        // alice rates herself: Alpha=80, Beta=20, Gamma=0
        constsumResponse("r1", "alice", "alice", List.of(80, 20, 0));
        // bob rates himself: Alpha=70, Beta=30, Gamma=0
        constsumResponse("r2", "bob", "bob", List.of(70, 30, 0));
    }

    private void constsumResponse(String alias, String giverAlias, String recipientAlias, List<Integer> answers) {
        FeedbackConstantSumOptionsResponseDetails details = new FeedbackConstantSumOptionsResponseDetails();
        details.setAnswers(answers);
        given.feedbackResponse(alias, r -> r.feedbackQuestion("question")
                .giverStudent(giverAlias)
                .recipientStudent(recipientAlias)
                .details(details));
    }

    private void constsumResponseNoRecipient(String alias, String giverAlias, List<Integer> answers) {
        FeedbackConstantSumOptionsResponseDetails details = new FeedbackConstantSumOptionsResponseDetails();
        details.setAnswers(answers);
        given.feedbackResponse(alias, r -> r.feedbackQuestion("question")
                .giverStudent(giverAlias)
                .noSpecificRecipient()
                .details(details));
    }
}
