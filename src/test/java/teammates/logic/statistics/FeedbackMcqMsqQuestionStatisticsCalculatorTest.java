package teammates.logic.statistics;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackMcqMsqCourseWideStatistics;
import teammates.common.datatransfer.statistics.FeedbackMcqMsqRecipientStatistics;

/**
 * Tests for {@link FeedbackMcqMsqQuestionStatisticsCalculator}.
 */
public class FeedbackMcqMsqQuestionStatisticsCalculatorTest extends BaseStatisticsTestCase {

    private final FeedbackMcqMsqQuestionStatisticsCalculator calculator =
            new FeedbackMcqMsqQuestionStatisticsCalculator();

    // ==================== MCQ tests ====================

    @Test
    public void calculateCourseWideMcq_noWeights_returnsSummaryRowsOnly() {
        createMcqScenario(false, false);

        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackMcqMsqCourseWideStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        assertFalse(statistics.isHasWeights());
        assertTrue(statistics.isHasAnswers());
        assertEquals(statistics.getPerRecipientRows().size(), 0);
        assertEquals(statistics.getRows().size(), 3);

        // optionA: 2 out of 3 responses → 66.67%
        FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow rowA = statistics.getRows().get(0);
        assertEquals(rowA.getOption(), "optionA");
        assertEquals(rowA.getCount(), 2);
        assertEquals(rowA.getPercentage(), 66.67);
        assertNull(rowA.getWeight());
        assertNull(rowA.getWeightedPercentage());
    }

    @Test
    public void calculateCourseWideMcq_withWeights_returnsWeightedStatsAndPerRecipientRows() {
        createMcqScenario(true, false);

        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackMcqMsqCourseWideStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        assertTrue(statistics.isHasWeights());
        assertEquals(statistics.getRows().size(), 3);
        assertEquals(statistics.getPerRecipientRows().size(), 3);

        // optionA: weight=1.0 count=2; optionB: weight=2.0 count=1; optionC: weight=3.0 count=0
        // totalWeighted = 1*2 + 2*1 + 3*0 = 4 → A weighted% = 1*2/4*100 = 50
        FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow rowA = statistics.getRows().get(0);
        assertEquals(rowA.getWeight(), 1.0);
        assertEquals(rowA.getWeightedPercentage(), 50.0);
    }

    @Test
    public void calculateCourseWideMcq_otherEnabled_countsOtherResponses() {
        createMcqScenario(false, true);

        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackMcqMsqCourseWideStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // 3 choices + "Other" = 4 rows; Other is always appended last
        assertEquals(statistics.getRows().size(), 4);
        assertEquals(statistics.getRows().get(3).getOption(), "Other");
        assertEquals(statistics.getRows().get(3).getCount(), 1);
    }

    @Test
    public void calculateCourseWideMcq_noResponses_hasAnswersFalseAndZeroCounts() {
        createMcqScenario(false, false);

        SessionResultsBundle bundle = bundleForQuestion("question");

        FeedbackMcqMsqCourseWideStatistics statistics = calculator.calculateCourseWide(
                question("question"), List.of(), bundle);

        assertFalse(statistics.isHasAnswers());
        for (FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow row : statistics.getRows()) {
            assertEquals(row.getCount(), 0);
            assertEquals(row.getPercentage(), 0.0);
        }
    }

    @Test
    public void calculateForRecipientMcq_withWeights_returnsRowsWithoutPerRecipientBreakdown() {
        createMcqScenario(true, false);

        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackMcqMsqRecipientStatistics statistics = calculator.calculateForRecipient(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle,
                null);

        assertTrue(statistics.isHasWeights());
        assertEquals(statistics.getRows().size(), 3);
    }

    @Test(dataProvider = "optionRowCases")
    public void buildOptionRows_counts_returnsExpectedPercentages(
            String caseName,
            List<String> optionLabels,
            Map<String, Double> weightMap,
            Map<String, Integer> counts,
            int totalAnswerCount,
            double expectedPercentage0,
            Double expectedWeightedPercentage0) {
        List<FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow> rows =
                FeedbackMcqMsqQuestionStatisticsCalculator.buildOptionRows(
                        optionLabels, weightMap, counts, totalAnswerCount);

        assertEquals(rows.get(0).getPercentage(), expectedPercentage0);
        assertEquals(rows.get(0).getWeightedPercentage(), expectedWeightedPercentage0);
    }

    @DataProvider(name = "optionRowCases")
    public Object[][] optionRowCases() {
        return new Object[][] {
                // Equal split, 2 options with 2 responses each → 50% each.
                {
                        "equal split no weights",
                        List.of("A", "B"),
                        Map.of(),
                        Map.of("A", 2, "B", 2),
                        4,
                        50.0,
                        null,
                },
                // Zero total responses → 0% everywhere.
                {
                        "zero responses",
                        List.of("A", "B"),
                        Map.of(),
                        Map.of("A", 0, "B", 0),
                        0,
                        0.0,
                        null,
                },
                // Weights present, all responses to A (weight=1, count=2), B (weight=3, count=0)
                // → totalWeighted=2, A weightedPercentage=100.
                {
                        "weighted all in A",
                        List.of("A", "B"),
                        Map.of("A", 1.0, "B", 3.0),
                        Map.of("A", 2, "B", 0),
                        2,
                        100.0,
                        100.0,
                },
        };
    }

    // ==================== MSQ tests ====================

    @Test
    public void calculateCourseWideMsq_noWeights_returnsSummaryRowsOnly() {
        createMsqScenario(false);

        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackMcqMsqCourseWideStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        assertFalse(statistics.isHasWeights());
        assertTrue(statistics.isHasAnswers());
        assertEquals(statistics.getPerRecipientRows().size(), 0);
        assertEquals(statistics.getRows().size(), 3);

        // optionA selected in both responses; optionB selected in r2 only; optionC never
        // total answer count = 1+1+1 = 3 (r1: [A], r2: [A, B])
        FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow rowA = statistics.getRows().get(0);
        assertEquals(rowA.getOption(), "optionA");
        assertEquals(rowA.getCount(), 2);
        assertEquals(rowA.getPercentage(), 66.67);
        assertNull(rowA.getWeight());
        assertNull(rowA.getWeightedPercentage());
    }

    @Test
    public void calculateCourseWideMsq_withWeights_returnsWeightedStatsAndPerRecipientRows() {
        createMsqScenario(true);

        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackMcqMsqCourseWideStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        assertTrue(statistics.isHasWeights());
        assertTrue(statistics.isHasAnswers());
        assertEquals(statistics.getRows().size(), 3);
        assertEquals(statistics.getPerRecipientRows().size(), 2);

        // optionA: weight=1.0, count=2; optionB: weight=2.0, count=1; optionC: weight=3.0, count=0
        // totalWeighted = 1*2 + 2*1 + 3*0 = 4 → A weighted% = 1*2/4*100 = 50
        FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow rowA = statistics.getRows().get(0);
        assertEquals(rowA.getWeight(), 1.0);
        assertEquals(rowA.getWeightedPercentage(), 50.0);
    }

    @Test
    public void calculateCourseWideMsq_allNoneOfTheAbove_hasAnswersFalse() {
        createMsqScenarioWithNoneOfTheAbove();

        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackMcqMsqCourseWideStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        assertFalse(statistics.isHasAnswers());
        for (FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow row : statistics.getRows()) {
            assertEquals(row.getCount(), 0);
        }
    }

    @Test
    public void calculateCourseWideMsq_noResponses_hasAnswersFalseAndZeroCounts() {
        createMsqScenario(false);

        SessionResultsBundle bundle = bundleForQuestion("question");

        FeedbackMcqMsqCourseWideStatistics statistics = calculator.calculateCourseWide(
                question("question"), List.of(), bundle);

        assertFalse(statistics.isHasAnswers());
        for (FeedbackMcqMsqCourseWideStatistics.McqMsqOptionRow row : statistics.getRows()) {
            assertEquals(row.getCount(), 0);
        }
    }

    @Test
    public void calculateForRecipientMsq_withWeights_returnsRowsWithoutPerRecipientBreakdown() {
        createMsqScenario(true);

        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackMcqMsqRecipientStatistics statistics = calculator.calculateForRecipient(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle,
                null);

        assertTrue(statistics.isHasWeights());
        assertEquals(statistics.getRows().size(), 3);
    }

    @Test(dataProvider = "accumulateCases")
    public void accumulateMsqCounts_answers_updatesCountsCorrectly(
            String caseName,
            List<String> choices,
            List<String> answers,
            boolean isOther,
            String otherFieldContent,
            Map<String, Integer> expectedDelta) {
        FeedbackMsqQuestionDetails details = new FeedbackMsqQuestionDetails();
        details.setMsqChoices(choices);
        details.setOtherEnabled(true);

        FeedbackMsqResponseDetails responseDetails = new FeedbackMsqResponseDetails();
        responseDetails.setAnswers(answers);
        responseDetails.setOther(isOther);
        responseDetails.setOtherFieldContent(otherFieldContent);

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String choice : choices) {
            counts.put(choice, 0);
        }
        counts.put("Other", 0);

        FeedbackMcqMsqQuestionStatisticsCalculator.accumulateMsqCounts(details, responseDetails, counts);

        for (Map.Entry<String, Integer> entry : expectedDelta.entrySet()) {
            assertEquals(counts.get(entry.getKey()), entry.getValue(), "mismatch for option: " + entry.getKey());
        }
    }

    @DataProvider(name = "accumulateCases")
    public Object[][] accumulateCases() {
        return new Object[][] {
                // Normal choice selection increments the matching option.
                {
                        "single choice",
                        List.of("A", "B"),
                        List.of("A"),
                        false,
                        "",
                        Map.of("A", 1, "B", 0, "Other", 0),
                },
                // Multiple choices each increment their option.
                {
                        "multiple choices",
                        List.of("A", "B"),
                        List.of("A", "B"),
                        false,
                        "",
                        Map.of("A", 1, "B", 1, "Other", 0),
                },
                // "None of the above" (empty string) should not increment any option.
                {
                        "none of the above",
                        List.of("A", "B"),
                        List.of(""),
                        false,
                        "",
                        Map.of("A", 0, "B", 0, "Other", 0),
                },
                // "Other" flag increments only "Other" bucket.
                {
                        "other selected",
                        List.of("A", "B"),
                        List.of("someCustomText"),
                        true,
                        "someCustomText",
                        Map.of("A", 0, "B", 0, "Other", 1),
                },
        };
    }

    // ==================== Scenario builders ====================

    private void createMcqScenario(boolean hasWeights, boolean otherEnabled) {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.student("charlie", s -> s.course("course").team("team").name("Charlie").email("charlie@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));

        FeedbackMcqQuestionDetails details = new FeedbackMcqQuestionDetails();
        details.setMcqChoices(Arrays.asList("optionA", "optionB", "optionC"));
        details.setOtherEnabled(otherEnabled);
        if (hasWeights) {
            details.setHasAssignedWeights(true);
            details.setMcqWeights(Arrays.asList(1.0, 2.0, 3.0));
        }

        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(details)
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));

        if (otherEnabled) {
            // Two responses: alice → optionA, bob → Other
            mcqResponse("r1", "alice", "alice", "optionA", false);
            mcqResponse("r2", "bob", "bob", "someText", true);
        } else {
            // Three responses: alice → optionA, bob → optionA, charlie → optionB
            mcqResponse("r1", "alice", "alice", "optionA", false);
            mcqResponse("r2", "bob", "bob", "optionA", false);
            mcqResponse("r3", "charlie", "charlie", "optionB", false);
        }
    }

    private void mcqResponse(String alias, String giverAlias, String recipientAlias, String answer, boolean isOther) {
        FeedbackMcqResponseDetails details = new FeedbackMcqResponseDetails();
        details.setAnswer(answer);
        details.setOther(isOther);
        given.feedbackResponse(alias, r -> r.feedbackQuestion("question")
                .giverStudent(giverAlias)
                .recipientStudent(recipientAlias)
                .details(details));
    }

    private void createMsqScenario(boolean hasWeights) {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));

        FeedbackMsqQuestionDetails details = new FeedbackMsqQuestionDetails();
        details.setMsqChoices(Arrays.asList("optionA", "optionB", "optionC"));
        if (hasWeights) {
            details.setHasAssignedWeights(true);
            details.setMsqWeights(Arrays.asList(1.0, 2.0, 3.0));
        }

        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(details)
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));

        // r1: alice → [optionA]; r2: bob → [optionA, optionB]
        msqResponse("r1", "alice", "alice", Arrays.asList("optionA"), false, "");
        msqResponse("r2", "bob", "bob", Arrays.asList("optionA", "optionB"), false, "");
    }

    private void createMsqScenarioWithNoneOfTheAbove() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));

        FeedbackMsqQuestionDetails details = new FeedbackMsqQuestionDetails();
        details.setMsqChoices(Arrays.asList("optionA", "optionB"));

        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(details)
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));

        // "None of the above" is represented by an empty string answer
        msqResponse("r1", "alice", "alice", Arrays.asList(""), false, "");
    }

    private void msqResponse(String alias, String giverAlias, String recipientAlias,
            List<String> answers, boolean isOther, String otherFieldContent) {
        FeedbackMsqResponseDetails details = new FeedbackMsqResponseDetails();
        details.setAnswers(answers);
        details.setOther(isOther);
        details.setOtherFieldContent(otherFieldContent);
        given.feedbackResponse(alias, r -> r.feedbackQuestion("question")
                .giverStudent(giverAlias)
                .recipientStudent(recipientAlias)
                .details(details));
    }
}
