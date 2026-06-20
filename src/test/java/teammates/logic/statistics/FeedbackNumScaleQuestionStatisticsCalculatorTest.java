package teammates.logic.statistics;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackNumScaleStatistics;
import teammates.common.datatransfer.statistics.FeedbackNumScaleStatistics.NumScaleRecipientRow;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatisticsView;

/**
 * Tests for {@link FeedbackNumScaleQuestionStatisticsCalculator}.
 */
public class FeedbackNumScaleQuestionStatisticsCalculatorTest extends BaseStatisticsTestCase {

    private final FeedbackNumScaleQuestionStatisticsCalculator calculator =
            new FeedbackNumScaleQuestionStatisticsCalculator();

    // ==================== Course-wide ====================

    @Test
    public void calculateCourseWide_multipleRecipients_returnsRowsPerRecipient() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackNumScaleStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        assertEquals(statistics.getStatisticsView(), FeedbackQuestionResultsStatisticsView.COURSE_WIDE);
        List<NumScaleRecipientRow> rows = statistics.getRows();
        // alice, bob, charlie in alphabetical order within the same team
        assertEquals(rows.size(), 3);

        NumScaleRecipientRow aliceRow = findByName(rows, "Alice");
        assertNotNull(aliceRow);
        assertEquals(aliceRow.getAverage(), 3.0);
        assertEquals(aliceRow.getMin(), 3.0);
        assertEquals(aliceRow.getMax(), 3.0);
        assertNull(aliceRow.getAverageExcludingSelf()); // only self-response
    }

    @Test
    public void calculateCourseWide_selfResponseExcludedFromAverageExcludingSelf() {
        createScenarioWithCrossRatings();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3", "rSelf");

        FeedbackNumScaleStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // alice receives: bob→alice(4.0)=r2, alice→alice(3.0)=rSelf
        NumScaleRecipientRow aliceRow = findByName(statistics.getRows(), "Alice");
        assertNotNull(aliceRow);
        assertEquals(aliceRow.getAverage(), 3.5); // (3.0+4.0)/2
        assertEquals(aliceRow.getMin(), 3.0);
        assertEquals(aliceRow.getMax(), 4.0);
        assertEquals(aliceRow.getAverageExcludingSelf(), 4.0); // only bob's response
    }

    @Test
    public void calculateCourseWide_noResponses_returnsEmptyRows() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question");

        FeedbackNumScaleStatistics statistics = calculator.calculateCourseWide(
                question("question"), List.of(), bundle);

        assertEquals(statistics.getRows().size(), 0);
    }

    @Test
    public void calculateCourseWide_multipleResponsesPerRecipient_computesCorrectAggregates() {
        createScenarioWithCrossRatings();
        // r1: alice→bob(2.0), r2: bob→alice(4.0), r3: charlie→bob(3.0)
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackNumScaleStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // bob receives: alice→bob(2.0)=r1, charlie→bob(3.0)=r3
        NumScaleRecipientRow bobRow = findByName(statistics.getRows(), "Bob");
        assertNotNull(bobRow);
        assertEquals(bobRow.getAverage(), 2.5); // (2.0+3.0)/2
        assertEquals(bobRow.getMin(), 2.0);
        assertEquals(bobRow.getMax(), 3.0);
        assertEquals(bobRow.getAverageExcludingSelf(), 2.5); // both are non-self
    }

    @Test
    public void calculateCourseWide_rowsSortedByTeamThenName() {
        createScenarioTwoTeams();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackNumScaleStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        List<NumScaleRecipientRow> rows = statistics.getRows();
        assertEquals(rows.size(), 3);
        // teamA has alice and charlie, teamB has bob — sorted by team then name
        assertEquals(rows.get(0).getRecipientTeam(), "Team Alpha");
        assertEquals(rows.get(1).getRecipientTeam(), "Team Alpha");
        assertEquals(rows.get(2).getRecipientTeam(), "Team Beta");
    }

    // ==================== Recipient view ====================

    @Test
    public void calculateForRecipient_filtersToRecipientOnly() {
        createScenarioWithCrossRatings();
        // r1: alice→bob(2.0), r2: bob→alice(4.0), r3: charlie→bob(3.0), rSelf: alice→alice(3.0)
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3", "rSelf");

        FeedbackNumScaleStatistics statistics = calculator.calculateForRecipient(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle,
                student("alice"));

        assertEquals(statistics.getStatisticsView(), FeedbackQuestionResultsStatisticsView.RECIPIENT);
        List<NumScaleRecipientRow> rows = statistics.getRows();
        // only alice's row
        assertEquals(rows.size(), 1);
        NumScaleRecipientRow row = rows.get(0);
        assertEquals(row.getAverage(), 3.5); // (3.0+4.0)/2
        assertEquals(row.getAverageExcludingSelf(), 4.0); // only bob's response
    }

    @Test
    public void calculateForRecipient_noNonSelfResponses_averageExcludingSelfIsNull() {
        createScenario();
        // r1: alice→alice(3.0) — only self
        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackNumScaleStatistics statistics = calculator.calculateForRecipient(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle,
                student("alice"));

        List<NumScaleRecipientRow> rows = statistics.getRows();
        assertEquals(rows.size(), 1);
        assertNull(rows.get(0).getAverageExcludingSelf());
    }

    // ==================== Email and team fields ====================

    @Test
    public void calculateCourseWide_recipientEmailAndTeamPopulated() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackNumScaleStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        NumScaleRecipientRow row = statistics.getRows().get(0);
        assertEquals(row.getRecipientName(), "Alice");
        assertEquals(row.getRecipientEmail(), "alice@example.tmt");
        assertEquals(row.getRecipientTeam(), "Team Alpha");
    }

    // ==================== Rounding ====================

    @Test
    public void calculateCourseWide_averageRoundedToTwoDecimals() {
        createScenarioForRounding();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackNumScaleStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // answers: 1.0, 2.0, 3.0 → average = 6.0/3 = 2.0
        NumScaleRecipientRow row = statistics.getRows().get(0);
        assertEquals(row.getAverage(), 2.0);
        // non-self answers: 2.0 (bob), 3.0 (charlie) → averageExcludingSelf = 5.0/2 = 2.5
        assertEquals(row.getAverageExcludingSelf(), 2.5);
    }

    // ==================== Scenario builders ====================

    private void createScenario() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.student("charlie", s -> s.course("course").team("team").name("Charlie").email("charlie@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails())
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));
        numScaleResponse("r1", "alice", "alice", 3.0);
        numScaleResponse("r2", "bob", "bob", 4.0);
        numScaleResponse("r3", "charlie", "charlie", 5.0);
    }

    private void createScenarioWithCrossRatings() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.student("charlie", s -> s.course("course").team("team").name("Charlie").email("charlie@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails())
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));
        numScaleResponse("r1", "alice", "bob", 2.0);
        numScaleResponse("r2", "bob", "alice", 4.0);
        numScaleResponse("r3", "charlie", "bob", 3.0);
        numScaleResponse("rSelf", "alice", "alice", 3.0);
    }

    private void createScenarioTwoTeams() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("teamA", t -> t.section("section").name("Team Alpha"));
        given.team("teamB", t -> t.section("section").name("Team Beta"));
        given.student("alice", s -> s.course("course").team("teamA").name("Alice").email("alice@example.tmt"));
        given.student("charlie", s -> s.course("course").team("teamA").name("Charlie").email("charlie@example.tmt"));
        given.student("bob", s -> s.course("course").team("teamB").name("Bob").email("bob@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails())
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));
        numScaleResponse("r1", "alice", "alice", 3.0);
        numScaleResponse("r2", "charlie", "charlie", 4.0);
        numScaleResponse("r3", "bob", "bob", 5.0);
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
                .details(buildDetails())
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));
        // alice receives 3 responses: from alice(self), bob, charlie
        numScaleResponse("r1", "alice", "alice", 1.0);
        numScaleResponse("r2", "bob", "alice", 2.0);
        numScaleResponse("r3", "charlie", "alice", 3.0);
    }

    private FeedbackNumericalScaleQuestionDetails buildDetails() {
        FeedbackNumericalScaleQuestionDetails details = new FeedbackNumericalScaleQuestionDetails();
        details.setMinScale(1);
        details.setMaxScale(10);
        details.setStep(0.5);
        return details;
    }

    private void numScaleResponse(String alias, String giverAlias, String recipientAlias, double answer) {
        FeedbackNumericalScaleResponseDetails details = new FeedbackNumericalScaleResponseDetails();
        details.setAnswer(answer);
        given.feedbackResponse(alias, r -> r.feedbackQuestion("question")
                .giverStudent(giverAlias)
                .recipientStudent(recipientAlias)
                .details(details));
    }

    private static NumScaleRecipientRow findByName(List<NumScaleRecipientRow> rows, String name) {
        return rows.stream()
                .filter(r -> r.getRecipientName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
