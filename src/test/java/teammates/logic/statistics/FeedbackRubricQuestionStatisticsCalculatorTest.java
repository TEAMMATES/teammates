package teammates.logic.statistics;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatisticsView;
import teammates.common.datatransfer.statistics.FeedbackRubricStatistics;
import teammates.common.datatransfer.statistics.FeedbackRubricStatistics.RubricChoiceCell;
import teammates.common.datatransfer.statistics.FeedbackRubricStatistics.RubricPerRecipientStats;
import teammates.common.datatransfer.statistics.FeedbackRubricStatistics.RubricSubQuestionRow;

/**
 * Tests for {@link FeedbackRubricQuestionStatisticsCalculator}.
 */
public class FeedbackRubricQuestionStatisticsCalculatorTest extends BaseStatisticsTestCase {

    private final FeedbackRubricQuestionStatisticsCalculator calculator =
            new FeedbackRubricQuestionStatisticsCalculator();

    // ==================== Course-wide, no weights ====================

    @Test
    public void calculateCourseWide_noWeights_returnsSummaryRowsWithCountsAndPercentages() {
        createScenario(false);
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackRubricStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        assertEquals(statistics.getStatisticsView(), FeedbackQuestionResultsStatisticsView.COURSE_WIDE);
        assertFalse(statistics.isHasWeights());
        assertEquals(statistics.getSubQuestions(), List.of("SQ1", "SQ2"));
        assertEquals(statistics.getChoices(), List.of("Strongly Agree", "Agree", "Disagree"));
        assertEquals(statistics.getPerRecipientStats().size(), 0);

        // SQ1: alice→choice0, bob→choice1, charlie→choice2 → 33.33% each
        List<RubricSubQuestionRow> rows = statistics.getRows();
        assertEquals(rows.size(), 2);
        RubricSubQuestionRow sq1 = rows.get(0);
        assertEquals(sq1.getSubQuestion(), "SQ1");
        assertEquals(sq1.getCells().size(), 3);
        assertEquals(sq1.getCells().get(0).getCount(), 1);
        assertEquals(sq1.getCells().get(0).getPercentage(), 33.33);
        assertEquals(sq1.getCells().get(1).getCount(), 1);
        assertEquals(sq1.getCells().get(2).getCount(), 1);
        assertNull(sq1.getCells().get(0).getWeight());
        assertNull(sq1.getWeightAverage());
    }

    @Test
    public void calculateCourseWide_noWeights_rowsExcludeSelfOmitsSelfResponse() {
        createScenarioWithSelfResponse(false);
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "rSelf");

        FeedbackRubricStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // rows includes self; rowsExcludeSelf does not
        RubricSubQuestionRow sq1All = statistics.getRows().get(0);
        RubricSubQuestionRow sq1Excl = statistics.getRowsExcludeSelf().get(0);

        // rSelf responds choice0 for SQ1 → rows has 2 votes for choice0, excludeSelf has 1
        assertEquals(sq1All.getCells().get(0).getCount(), 2);
        assertEquals(sq1Excl.getCells().get(0).getCount(), 1);
    }

    @Test
    public void calculateCourseWide_notChosenAnswer_isSkipped() {
        createScenario(false);
        // r1 has RUBRIC_ANSWER_NOT_CHOSEN (-1) for SQ2
        rubricResponse("rSkip", "alice", "charlie",
                Arrays.asList(0, -1)); // SQ1→choice0, SQ2→not chosen
        SessionResultsBundle bundle = bundleForQuestion("question", "rSkip");

        FeedbackRubricStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // SQ2 total should be 0 since -1 is skipped
        RubricSubQuestionRow sq2 = statistics.getRows().get(1);
        int total = sq2.getCells().stream().mapToInt(RubricChoiceCell::getCount).sum();
        assertEquals(total, 0);
    }

    // ==================== Course-wide, with weights ====================

    @Test
    public void calculateCourseWide_withWeights_cellsHaveWeightAndRowHasWeightAverage() {
        createScenario(true);
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackRubricStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        assertTrue(statistics.isHasWeights());

        // Weights: SQ1=[1.0, 2.0, 3.0], SQ2=[2.0, 3.0, 1.0]
        // SQ1: alice→choice0(w=1), bob→choice1(w=2), charlie→choice2(w=3)
        // average = (1+2+3)/3 = 2.0
        RubricSubQuestionRow sq1 = statistics.getRows().get(0);
        assertEquals(sq1.getCells().get(0).getWeight(), 1.0);
        assertEquals(sq1.getCells().get(1).getWeight(), 2.0);
        assertEquals(sq1.getCells().get(2).getWeight(), 3.0);
        assertEquals(sq1.getWeightAverage(), 2.0);
    }

    @Test
    public void calculateCourseWide_withWeights_perRecipientStatsPopulated() {
        createScenario(true);
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackRubricStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // Each response targets a different recipient (alice, bob, charlie)
        List<RubricPerRecipientStats> perRecipient = statistics.getPerRecipientStats();
        assertEquals(perRecipient.size(), 3);

        // alice receives r1 only: SQ1→choice0(w=1), SQ2→choice1(w=3)
        RubricPerRecipientStats aliceStats = findByName(perRecipient, "Alice");
        assertNotNull(aliceStats);
        assertEquals(aliceStats.getPerCriterionRows().size(), 2);
        assertEquals(aliceStats.getPerCriterionRows().get(0).getCells().get(0).getCount(), 1);
        // SQ1 total = 1.0 (one response to choice0, weight=1.0)
        assertEquals(aliceStats.getPerCriterionRows().get(0).getTotal(), 1.0);
        assertEquals(aliceStats.getPerCriterionRows().get(0).getAverage(), 1.0);
    }

    @Test
    public void calculateCourseWide_withWeights_overallCellsUseAverageWeightsAcrossSubQuestions() {
        createScenario(true);
        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackRubricStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // r1: alice→alice; SQ1→choice0(w=1.0), SQ2→choice0(w=2.0)
        // overallCell[0].weight = average of SQ1[0] and SQ2[0] = (1.0+2.0)/2 = 1.5
        RubricPerRecipientStats aliceStats = findByName(statistics.getPerRecipientStats(), "Alice");
        assertNotNull(aliceStats);
        assertEquals(aliceStats.getOverallCells().get(0).getWeight(), 1.5);
        // overallTotal = sum of chosen weights = 1.0 (SQ1→choice0) + 2.0 (SQ2→choice0) = 3.0
        assertEquals(aliceStats.getOverallTotal(), 3.0);
        // overallAverage = 3.0 / 2 responses = 1.5
        assertEquals(aliceStats.getOverallAverage(), 1.5);
    }

    @Test
    public void calculateCourseWide_withWeights_subQuestionAveragesMatchPerCriterionAverages() {
        createScenario(true);
        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackRubricStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // r1: alice→alice; SQ1→choice0(w=1.0), SQ2→choice0(w=2.0)
        RubricPerRecipientStats aliceStats = findByName(statistics.getPerRecipientStats(), "Alice");
        assertNotNull(aliceStats);
        List<Double> subQAverages = aliceStats.getSubQuestionAverages();
        assertEquals(subQAverages.size(), 2);
        assertEquals(subQAverages.get(0), 1.0); // SQ1 average
        assertEquals(subQAverages.get(1), 2.0); // SQ2 average
    }

    @Test
    public void calculateCourseWide_noWeights_perRecipientStatsEmpty() {
        createScenario(false);
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackRubricStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        assertEquals(statistics.getPerRecipientStats().size(), 0);
    }

    @Test
    public void calculateCourseWide_noResponses_allCountsAreZeroAndPercentagesAreZero() {
        createScenario(false);
        SessionResultsBundle bundle = bundleForQuestion("question");

        FeedbackRubricStatistics statistics = calculator.calculateCourseWide(
                question("question"), List.of(), bundle);

        for (RubricSubQuestionRow row : statistics.getRows()) {
            for (RubricChoiceCell cell : row.getCells()) {
                assertEquals(cell.getCount(), 0);
                assertEquals(cell.getPercentage(), 0.0);
            }
        }
    }

    // ==================== Recipient view ====================

    @Test
    public void calculateForRecipient_noWeights_returnsRowsAndExcludeSelfRowsButNoPerRecipient() {
        createScenaro_recipientView(false);
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackRubricStatistics statistics = calculator.calculateForRecipient(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle,
                student("alice"));

        assertEquals(statistics.getStatisticsView(), FeedbackQuestionResultsStatisticsView.RECIPIENT);
        assertEquals(statistics.getRows().size(), 2);
        assertEquals(statistics.getRowsExcludeSelf().size(), 2);
        assertEquals(statistics.getPerRecipientStats().size(), 0);
        // rows should include r1 (choice0) and r2 (choice1) → total 2
        assertEquals(statistics.getRows().get(0).getCells().get(0).getCount(), 1);
        assertEquals(statistics.getRows().get(0).getCells().get(1).getCount(), 1);
    }

    @Test
    public void calculateForRecipient_withWeights_perRecipientStatsStillEmpty() {
        createScenaro_recipientView(true);
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackRubricStatistics statistics = calculator.calculateForRecipient(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle,
                student("alice"));

        // perRecipientStats only built for COURSE_WIDE view
        assertEquals(statistics.getPerRecipientStats().size(), 0);
    }

    @Test
    public void calculateForRecipient_withSelfResponse_excludeSelfRowsOmitSelfResponse() {
        createScenaro_recipientView(false);
        // rSelf: alice rates herself (choice0) — second self-response alongside r1 (also alice→alice)
        rubricResponse("rSelf", "alice", "alice", Arrays.asList(0, 0));
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "rSelf");

        // Pass alice's UUID; responses to alice: r1(alice→alice), rSelf(alice→alice), r2(bob→alice)
        FeedbackRubricStatistics statistics = calculator.calculateForRecipient(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle,
                student("alice"));

        // rows includes all to alice: r1(choice0) + rSelf(choice0) + r2(choice1)
        // → SQ1 choice0 count = 2, SQ1 choice1 count = 1
        assertEquals(statistics.getRows().get(0).getCells().get(0).getCount(), 2);
        assertEquals(statistics.getRows().get(0).getCells().get(1).getCount(), 1);

        // rowsExcludeSelf omits all alice→alice responses (r1 and rSelf); only r2 remains
        // → SQ1 choice0 count = 0, SQ1 choice1 count = 1
        assertEquals(statistics.getRowsExcludeSelf().get(0).getCells().get(0).getCount(), 0);
        assertEquals(statistics.getRowsExcludeSelf().get(0).getCells().get(1).getCount(), 1);
    }

    // ==================== Team giver/recipient ====================

    @Test
    public void calculateCourseWide_teamGiverAndRecipient_selfResponseExcludedFromRowsExcludeSelf() {
        createScenarioTeamGiverRecipient();
        SessionResultsBundle bundle = bundleForQuestion("question", "rSelf", "rOther");

        FeedbackRubricStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // rSelf: teamA rates teamA (SQ1→choice0) — should be excluded from rowsExcludeSelf
        // rOther: teamB rates teamA (SQ1→choice1) — should remain
        assertEquals(statistics.getRows().get(0).getCells().get(0).getCount(), 1); // rSelf in rows
        assertEquals(statistics.getRows().get(0).getCells().get(1).getCount(), 1); // rOther in rows
        assertEquals(statistics.getRowsExcludeSelf().get(0).getCells().get(0).getCount(), 0); // rSelf excluded
        assertEquals(statistics.getRowsExcludeSelf().get(0).getCells().get(1).getCount(), 1); // rOther kept
    }

    // ==================== Scenario builders ====================

    private void createScenario(boolean hasWeights) {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.student("charlie", s -> s.course("course").team("team").name("Charlie").email("charlie@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));

        FeedbackRubricQuestionDetails details = buildDetails(hasWeights);
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(details)
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));

        // r1: alice rates alice (SQ1→choice0, SQ2→choice0)
        // r2: bob   rates bob   (SQ1→choice1, SQ2→choice1)
        // r3: charlie rates charlie (SQ1→choice2, SQ2→choice2)
        rubricResponse("r1", "alice", "alice", Arrays.asList(0, 0));
        rubricResponse("r2", "bob", "bob", Arrays.asList(1, 1));
        rubricResponse("r3", "charlie", "charlie", Arrays.asList(2, 2));
    }

    private void createScenarioWithSelfResponse(boolean hasWeights) {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));

        FeedbackRubricQuestionDetails details = buildDetails(hasWeights);
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(details)
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));

        // r1: alice rates bob (SQ1→choice0)
        // r2: bob rates alice (SQ1→choice1)
        // rSelf: alice rates alice (SQ1→choice0) ← self-response
        rubricResponse("r1", "alice", "bob", Arrays.asList(0, 0));
        rubricResponse("r2", "bob", "alice", Arrays.asList(1, 1));
        rubricResponse("rSelf", "alice", "alice", Arrays.asList(0, 0));
    }

    private void createScenaro_recipientView(boolean hasWeights) {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));

        FeedbackRubricQuestionDetails details = buildDetails(hasWeights);
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(details)
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));

        // r1: alice rates alice (SQ1→choice0, SQ2→choice0)
        // r2: bob   rates alice (SQ1→choice1, SQ2→choice1)
        rubricResponse("r1", "alice", "alice", Arrays.asList(0, 0));
        rubricResponse("r2", "bob", "alice", Arrays.asList(1, 1));
    }

    private FeedbackRubricQuestionDetails buildDetails(boolean hasWeights) {
        FeedbackRubricQuestionDetails details = new FeedbackRubricQuestionDetails();
        details.setRubricSubQuestions(Arrays.asList("SQ1", "SQ2"));
        details.setRubricChoices(Arrays.asList("Strongly Agree", "Agree", "Disagree"));
        details.setRubricDescriptions(Arrays.asList(
                Arrays.asList("", "", ""),
                Arrays.asList("", "", "")));
        if (hasWeights) {
            details.setHasAssignedWeights(true);
            details.setRubricWeightsForEachCell(Arrays.asList(
                    Arrays.asList(1.0, 2.0, 3.0),
                    Arrays.asList(2.0, 3.0, 1.0)));
        }
        return details;
    }

    private void rubricResponse(String alias, String giverAlias, String recipientAlias, List<Integer> answers) {
        FeedbackRubricResponseDetails details = new FeedbackRubricResponseDetails();
        details.setAnswer(answers);
        given.feedbackResponse(alias, r -> r.feedbackQuestion("question")
                .giverStudent(giverAlias)
                .recipientStudent(recipientAlias)
                .details(details));
    }

    private void createScenarioTeamGiverRecipient() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("teamA", t -> t.section("section").name("Team Alpha"));
        given.team("teamB", t -> t.section("section").name("Team Beta"));
        given.feedbackSession("session", fs -> fs.course("course"));

        FeedbackRubricQuestionDetails details = buildDetails(false);
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(details)
                .recipientType(QuestionRecipientType.TEAMS));

        // rSelf: teamA rates teamA (SQ1→choice0, SQ2→choice0)
        // rOther: teamB rates teamA (SQ1→choice1, SQ2→choice1)
        rubricResponseTeamGiver("rSelf", "teamA", "teamA", Arrays.asList(0, 0));
        rubricResponseTeamGiver("rOther", "teamB", "teamA", Arrays.asList(1, 1));
    }

    private void rubricResponseTeamGiver(String alias, String giverTeamAlias, String recipientTeamAlias,
            List<Integer> answers) {
        FeedbackRubricResponseDetails details = new FeedbackRubricResponseDetails();
        details.setAnswer(answers);
        given.feedbackResponse(alias, r -> r.feedbackQuestion("question")
                .giverTeam(giverTeamAlias)
                .recipientTeam(recipientTeamAlias)
                .details(details));
    }

    private static RubricPerRecipientStats findByName(
            List<RubricPerRecipientStats> stats, String name) {
        return stats.stream()
                .filter(s -> s.getRecipientName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
