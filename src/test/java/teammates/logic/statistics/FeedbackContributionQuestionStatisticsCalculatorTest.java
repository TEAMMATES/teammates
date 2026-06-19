package teammates.logic.statistics;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.TeamEvalResult;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackContributionCourseWideStatistics;
import teammates.common.datatransfer.statistics.FeedbackContributionRecipientStatistics;
import teammates.common.util.Const;

/**
 * Tests for {@link FeedbackContributionQuestionStatisticsCalculator}.
 */
public class FeedbackContributionQuestionStatisticsCalculatorTest extends BaseStatisticsTestCase {

    private final FeedbackContributionQuestionStatisticsCalculator calculator =
            new FeedbackContributionQuestionStatisticsCalculator();

    @Test
    public void calculateCourseWide_completeTeamResponses_returnsRenderReadyRows() {
        createContributionScenario();

        SessionResultsBundle bundle = bundleForQuestion("question",
                "alice-self", "alice-bob", "alice-charlie",
                "bob-alice", "bob-self", "bob-charlie",
                "charlie-alice", "charlie-bob", "charlie-self");

        FeedbackContributionCourseWideStatistics statistics =
                calculator.calculateCourseWide(
                        question("question"),
                        bundle.getQuestionResponseMap().get(question("question")),
                        bundle);

        assertEquals(statistics.getRows().size(), 3);

        FeedbackContributionCourseWideStatistics.CourseWideRow aliceRow = statistics.getRows().get(0);
        assertEquals(aliceRow.getTeamName(), "Team Alpha");
        assertEquals(aliceRow.getRecipientName(), "Alice");
        assertEquals(aliceRow.getRecipientEmail(), "alice@example.tmt");
        assertEquals(aliceRow.getClaimed(), 120);
        assertEquals(aliceRow.getPerceived(), 92);
        assertEquals(aliceRow.getDiff(), -28);
        assertEquals(aliceRow.getRatingsReceived(), List.of(100, 84));
    }

    @Test
    public void calculateForRecipient_knownRecipient_returnsMyViewAndTeamView() {
        createContributionScenario();

        SessionResultsBundle bundle = bundleForQuestion("question",
                "alice-self", "alice-bob", "alice-charlie",
                "bob-alice", "bob-self", "bob-charlie",
                "charlie-alice", "charlie-bob", "charlie-self");

        FeedbackContributionRecipientStatistics statistics =
                calculator.calculateForRecipient(question("question"),
                        bundle.getQuestionResponseMap().get(question("question")),
                        bundle,
                        student("alice").getId());

        assertEquals(statistics.getMyView().getOfMe(), 120);
        assertEquals(statistics.getMyView().getOfOthers(), List.of(90, 90));
        assertEquals(statistics.getTeamView().getOfMe(), 92);
        assertEquals(statistics.getTeamView().getOfOthers(), List.of(108, 100));
    }

    @Test
    public void calculateNormalizedResponseValues_partialTeamResponses_skipsMissingRows() {
        createContributionScenario();

        SessionResultsBundle bundle = bundleForQuestion("question",
                "alice-self", "alice-bob", "alice-charlie",
                "bob-alice", "bob-self", "bob-charlie");

        Map<UUID, Integer> normalizedValues =
                calculator.calculateNormalizedResponseValues(
                        bundle.getQuestionResponseMap().get(question("question")),
                        bundle,
                        student("alice").getId());

        assertEquals(normalizedValues.get(response("alice-self").getId()).intValue(), 120);
        assertEquals(normalizedValues.get(response("bob-charlie").getId()).intValue(), 110);
        assertFalse(normalizedValues.containsKey(response("charlie-self").getId()));
    }

    @Test
    public void calculateForRecipient_unknownRecipient_returnsEmptyStatistics() {
        createContributionScenario();

        SessionResultsBundle bundle = bundleForQuestion("question",
                "alice-self", "alice-bob", "alice-charlie",
                "bob-alice", "bob-self", "bob-charlie",
                "charlie-alice", "charlie-bob", "charlie-self");

        FeedbackContributionRecipientStatistics statistics =
                calculator.calculateForRecipient(question("question"),
                        bundle.getQuestionResponseMap().get(question("question")),
                        bundle,
                        UUID.randomUUID());

        assertEquals(statistics.getMyView().getOfMe(), 0);
        assertTrue(statistics.getMyView().getOfOthers().isEmpty());
        assertEquals(statistics.getTeamView().getOfMe(), 0);
        assertTrue(statistics.getTeamView().getOfOthers().isEmpty());
    }

    @Test(dataProvider = "teamResultCases")
    public void calculateTeamResult_claimedValues_returnsExpectedNormalizedResult(
            String caseName,
            int[][] claimedValues,
            int expectedNormalizedClaimed00,
            int expectedNormalizedAveragePerceived0,
            int expectedNormalizedPeerContributionRatio,
            int expectedDenormalizedAveragePerceived) {
        TeamEvalResult teamResult = FeedbackContributionQuestionStatisticsCalculator.calculateTeamResult(claimedValues);

        assertEquals(teamResult.normalizedClaimed[0][0], expectedNormalizedClaimed00);
        assertEquals(teamResult.normalizedAveragePerceived[0], expectedNormalizedAveragePerceived0);
        assertEquals(teamResult.normalizedPeerContributionRatio[1][2], expectedNormalizedPeerContributionRatio);
        assertEquals(teamResult.denormalizedAveragePerceived[0][1], expectedDenormalizedAveragePerceived);
    }

    @DataProvider(name = "teamResultCases")
    public Object[][] teamResultCases() {
        return new Object[][] {
                // Uneven claims from one teammate exercise the common normalization path.
                {
                        "uneven full submissions",
                        new int[][] {
                                { 120, 90, 90 },
                                { 80, 110, 110 },
                                { 100, 100, 100 },
                        },
                        120,
                        92,
                        116,
                        100,
                },
                // Missing third-row submissions should still produce stable normalized values.
                {
                        "missing giver row",
                        new int[][] {
                                { 120, 90, 90 },
                                { 80, 110, 110 },
                                {
                                        Const.POINTS_NOT_SUBMITTED,
                                        Const.POINTS_NOT_SUBMITTED,
                                        Const.POINTS_NOT_SUBMITTED,
                                },
                        },
                        120,
                        86,
                        119,
                        103,
                },
                // Equal-share submissions should remain stable through normalization.
                {
                        "equal shares",
                        new int[][] {
                                { 100, 100, 100 },
                                { 100, 100, 100 },
                                { 100, 100, 100 },
                        },
                        100,
                        100,
                        100,
                        100,
                },
                // Heavy self-ratings should not affect perceived peer contribution after self-removal.
                {
                        "self ratings ignored for perception",
                        new int[][] {
                                { 200, 50, 50 },
                                { 50, 200, 50 },
                                { 50, 50, 200 },
                        },
                        200,
                        100,
                        100,
                        100,
                },
                // Zero is a valid contribution value and should be preserved through normalization when the row sums match.
                {
                        "zero claimed contribution",
                        new int[][] {
                                { 0, 150, 150 },
                                { 150, 0, 150 },
                                { 150, 150, 0 },
                        },
                        0,
                        100,
                        100,
                        100,
                },
                // POINTS_NOT_SURE is a valid special value for non-zero-sum questions and should propagate consistently.
                {
                        "not sure contribution",
                        new int[][] {
                                { 100, 100, 100 },
                                { 100, 100, Const.POINTS_NOT_SURE },
                                { 100, 100, 100 },
                        },
                        100,
                        100,
                        TeamEvalResult.NSU,
                        100,
                },
                // POINTS_NOT_SUBMITTED should be preserved as a special value in the affected peer-rating path.
                {
                        "not submitted contribution",
                        new int[][] {
                                { 100, 100, 100 },
                                { 100, 100, Const.POINTS_NOT_SUBMITTED },
                                { 100, 100, 100 },
                        },
                        100,
                        100,
                        TeamEvalResult.NA,
                        100,
                },
                // One missing peer rating should only affect the corresponding denormalized student view.
                {
                        "single missing peer rating",
                        new int[][] {
                                { 100, 100, 100 },
                                { 100, 100, Const.POINTS_NOT_SUBMITTED },
                                { 100, 100, 100 },
                        },
                        100,
                        100,
                        TeamEvalResult.NA,
                        100,
                },
        };
    }

    /**
     * Creates a single-team contribution scenario with three students.
     *
     * <p>Alice over-claims herself, Bob underrates Alice and favors himself,
     * and Charlie gives an even 100/100/100 split. This produces stable,
     * non-trivial normalization behavior for course-wide rows, recipient views,
     * and per-response normalized values.
     */
    private void createContributionScenario() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));

        given.student("alice", s -> s.course("course").team("team")
                .name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team")
                .name("Bob").email("bob@example.tmt"));
        given.student("charlie", s -> s.course("course").team("team")
                .name("Charlie").email("charlie@example.tmt"));

        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .contribution()
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));

        contributionResponse("alice-self", "alice", "alice", 120);
        contributionResponse("alice-bob", "alice", "bob", 90);
        contributionResponse("alice-charlie", "alice", "charlie", 90);
        contributionResponse("bob-alice", "bob", "alice", 80);
        contributionResponse("bob-self", "bob", "bob", 110);
        contributionResponse("bob-charlie", "bob", "charlie", 110);
        contributionResponse("charlie-alice", "charlie", "alice", 100);
        contributionResponse("charlie-bob", "charlie", "bob", 100);
        contributionResponse("charlie-self", "charlie", "charlie", 100);
    }

    private void contributionResponse(String alias, String giverAlias, String recipientAlias, int answer) {
        FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
        details.setAnswer(answer);
        given.feedbackResponse(alias, response -> response.feedbackQuestion("question")
                .giverStudent(giverAlias)
                .recipientStudent(recipientAlias)
                .details(details));
    }
}
