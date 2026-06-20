package teammates.logic.statistics;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankOptionsResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackRankOptionsStatistics;
import teammates.common.datatransfer.statistics.FeedbackRankOptionsStatistics.RankOptionsOptionRow;
import teammates.common.util.Const;

/**
 * Tests for {@link FeedbackRankOptionsQuestionStatisticsCalculator}.
 */
public class FeedbackRankOptionsQuestionStatisticsCalculatorTest extends BaseStatisticsTestCase {

    private final FeedbackRankOptionsQuestionStatisticsCalculator calculator =
            new FeedbackRankOptionsQuestionStatisticsCalculator();

    // ==================== Course-wide ====================

    @Test
    public void calculateCourseWide_multipleResponses_collectsRanksPerOption() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackRankOptionsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        List<RankOptionsOptionRow> options = statistics.getOptions();
        assertEquals(options.size(), 3);

        // r1: alice ranks Alpha=1, Beta=2, Gamma=3
        // r2: bob ranks Alpha=1, Beta=2, Gamma=3
        // Averages: Alpha=1.0, Beta=2.0, Gamma=3.0 → overall ranks 1, 2, 3
        RankOptionsOptionRow alpha = options.get(0);
        assertEquals(alpha.getOption(), "Alpha");
        assertEquals(alpha.getRanksReceived(), List.of(1, 1));
        assertEquals(alpha.getOverallRank(), Integer.valueOf(1));

        RankOptionsOptionRow beta = options.get(1);
        assertEquals(beta.getOption(), "Beta");
        assertEquals(beta.getRanksReceived(), List.of(2, 2));
        assertEquals(beta.getOverallRank(), Integer.valueOf(2));

        RankOptionsOptionRow gamma = options.get(2);
        assertEquals(gamma.getOption(), "Gamma");
        assertEquals(gamma.getRanksReceived(), List.of(3, 3));
        assertEquals(gamma.getOverallRank(), Integer.valueOf(3));
    }

    @Test
    public void calculateCourseWide_noResponses_returnsEmptyRanksAndNullOverallRank() {
        createScenario();

        FeedbackRankOptionsStatistics statistics = calculator.calculateCourseWide(
                question("question"), List.of(),
                bundleForQuestion("question"));

        List<RankOptionsOptionRow> options = statistics.getOptions();
        assertEquals(options.size(), 3);
        for (RankOptionsOptionRow row : options) {
            assertEquals(row.getRanksReceived().size(), 0);
            assertNull(row.getOverallRank());
        }
    }

    @Test
    public void calculateCourseWide_tieInAverageRank_assignsSameOverallRank() {
        createScenarioWithTie();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackRankOptionsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // r1: Alpha=1, Beta=2; r2: Alpha=2, Beta=1
        // Averages: Alpha=1.5, Beta=1.5 → tied at rank 1
        RankOptionsOptionRow alpha = statistics.getOptions().get(0);
        RankOptionsOptionRow beta = statistics.getOptions().get(1);
        assertEquals(alpha.getOverallRank(), beta.getOverallRank());
        assertEquals(alpha.getOverallRank(), Integer.valueOf(1));
    }

    @Test
    public void calculateCourseWide_optionOrderPreservedFromQuestion() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackRankOptionsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        List<RankOptionsOptionRow> options = statistics.getOptions();
        assertEquals(options.get(0).getOption(), "Alpha");
        assertEquals(options.get(1).getOption(), "Beta");
        assertEquals(options.get(2).getOption(), "Gamma");
    }

    @Test
    public void calculateCourseWide_nonConsecutiveRanks_normalizes() {
        createScenarioWithNonConsecutiveRanks();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackRankOptionsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // r1: Alpha=1, Beta=3, Gamma=5 → normalized to Alpha=1, Beta=2, Gamma=3
        List<RankOptionsOptionRow> options = statistics.getOptions();
        assertEquals(options.get(0).getRanksReceived(), List.of(1));
        assertEquals(options.get(1).getRanksReceived(), List.of(2));
        assertEquals(options.get(2).getRanksReceived(), List.of(3));
    }

    @Test
    public void calculateCourseWide_notSubmittedRanksSkipped() {
        createScenarioWithPartialRanking();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackRankOptionsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // r1: Alpha=1, Beta=NOT_SUBMITTED → Alpha gets rank, Beta has no ranks
        List<RankOptionsOptionRow> options = statistics.getOptions();
        assertEquals(options.get(0).getRanksReceived(), List.of(1));
        assertEquals(options.get(1).getRanksReceived(), List.of());
        assertNull(options.get(1).getOverallRank());
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
        // alice: Alpha=1, Beta=2, Gamma=3
        rankOptionsResponse("r1", "alice", List.of(1, 2, 3));
        // bob: Alpha=1, Beta=2, Gamma=3
        rankOptionsResponse("r2", "bob", List.of(1, 2, 3));
    }

    private void createScenarioWithTie() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails(List.of("Alpha", "Beta")))
                .recipientType(QuestionRecipientType.NONE));
        rankOptionsResponse("r1", "alice", List.of(1, 2));
        rankOptionsResponse("r2", "bob", List.of(2, 1));
    }

    private void createScenarioWithNonConsecutiveRanks() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails(List.of("Alpha", "Beta", "Gamma")))
                .recipientType(QuestionRecipientType.NONE));
        rankOptionsResponse("r1", "alice", List.of(1, 3, 5));
    }

    private void createScenarioWithPartialRanking() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails(List.of("Alpha", "Beta")))
                .recipientType(QuestionRecipientType.NONE));
        rankOptionsResponse("r1", "alice", List.of(1, Const.POINTS_NOT_SUBMITTED));
    }

    private FeedbackRankOptionsQuestionDetails buildDetails(List<String> options) {
        FeedbackRankOptionsQuestionDetails details = new FeedbackRankOptionsQuestionDetails();
        details.setOptions(options);
        return details;
    }

    private void rankOptionsResponse(String alias, String giverAlias, List<Integer> answers) {
        FeedbackRankOptionsResponseDetails details = new FeedbackRankOptionsResponseDetails();
        details.setAnswers(answers);
        given.feedbackResponse(alias, r -> r.feedbackQuestion("question")
                .giverStudent(giverAlias)
                .noSpecificRecipient()
                .details(details));
    }
}
