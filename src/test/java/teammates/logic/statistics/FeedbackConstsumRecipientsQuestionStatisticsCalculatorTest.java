package teammates.logic.statistics;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackConstantSumRecipientsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumRecipientsResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackConstsumRecipientsStatistics;
import teammates.common.datatransfer.statistics.FeedbackConstsumRecipientsStatistics.ConstsumRecipientRow;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatisticsView;

/**
 * Tests for {@link FeedbackConstsumRecipientsQuestionStatisticsCalculator}.
 */
public class FeedbackConstsumRecipientsQuestionStatisticsCalculatorTest extends BaseStatisticsTestCase {

    private final FeedbackConstsumRecipientsQuestionStatisticsCalculator calculator =
            new FeedbackConstsumRecipientsQuestionStatisticsCalculator();

    // ==================== Course-wide ====================

    @Test
    public void calculateCourseWide_multipleRecipients_returnsRowsPerRecipient() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackConstsumRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        assertEquals(statistics.getStatisticsView(), FeedbackQuestionResultsStatisticsView.COURSE_WIDE);
        List<ConstsumRecipientRow> rows = statistics.getRows();
        // alice and bob, same team → sorted by display name: alice then bob
        assertEquals(rows.size(), 2);

        ConstsumRecipientRow aliceRow = findByName(rows, "Alice");
        assertEquals(aliceRow.getTotal(), 60);
        assertEquals(aliceRow.getAverage(), 60.0);
        assertEquals(aliceRow.getPointsReceived(), List.of(60));
        assertNull(aliceRow.getAverageExcludingSelf()); // no non-self response

        ConstsumRecipientRow bobRow = findByName(rows, "Bob");
        assertEquals(bobRow.getTotal(), 40);
        assertEquals(bobRow.getAverage(), 40.0);
        assertEquals(bobRow.getPointsReceived(), List.of(40));
        assertEquals(bobRow.getAverageExcludingSelf(), 40.0); // alice→bob is non-self
    }

    @Test
    public void calculateCourseWide_selfExcludedFromAverageExcludingSelf() {
        createScenarioWithCrossRatings();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3", "r4");

        FeedbackConstsumRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        ConstsumRecipientRow aliceRow = findByName(statistics.getRows(), "Alice");
        // alice receives: alice→alice(30, self), bob→alice(70, non-self)
        assertEquals(aliceRow.getTotal(), 100);
        assertEquals(aliceRow.getAverage(), 50.0);
        assertEquals(aliceRow.getAverageExcludingSelf(), 70.0);
        assertEquals(aliceRow.getPointsReceived(), List.of(30, 70));

        ConstsumRecipientRow bobRow = findByName(statistics.getRows(), "Bob");
        // bob receives: alice→bob(70, non-self), bob→bob(30, self)
        assertEquals(bobRow.getTotal(), 100);
        assertEquals(bobRow.getAverage(), 50.0);
        assertEquals(bobRow.getAverageExcludingSelf(), 70.0);
        assertEquals(bobRow.getPointsReceived(), List.of(30, 70));
    }

    @Test
    public void calculateCourseWide_sortedByTeamThenName() {
        createScenarioTwoTeams();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackConstsumRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        List<ConstsumRecipientRow> rows = statistics.getRows();
        assertEquals(rows.size(), 3);
        // Team Alpha: Alice, Charlie; Team Beta: Bob
        assertEquals(rows.get(0).getRecipientName(), "Alice");
        assertEquals(rows.get(0).getRecipientTeam(), "Team Alpha");
        assertEquals(rows.get(1).getRecipientName(), "Charlie");
        assertEquals(rows.get(1).getRecipientTeam(), "Team Alpha");
        assertEquals(rows.get(2).getRecipientName(), "Bob");
        assertEquals(rows.get(2).getRecipientTeam(), "Team Beta");
    }

    @Test
    public void calculateCourseWide_noResponses_returnsEmptyRows() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question");

        FeedbackConstsumRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"), List.of(), bundle);

        assertEquals(statistics.getRows().size(), 0);
    }

    @Test
    public void calculateCourseWide_multipleResponsesFromDifferentGivers_aggregated() {
        createScenarioMultipleGivers();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3", "r4");

        FeedbackConstsumRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        ConstsumRecipientRow aliceRow = findByName(statistics.getRows(), "Alice");
        // alice receives: charlie→alice(30), dave→alice(40) = 2 non-self responses
        assertEquals(aliceRow.getTotal(), 70);
        assertEquals(aliceRow.getAverage(), 35.0);
        assertEquals(aliceRow.getAverageExcludingSelf(), 35.0); // same, no self response
        assertEquals(aliceRow.getPointsReceived(), List.of(30, 40));
    }

    @Test
    public void calculateCourseWide_averageRoundedToTwoDecimals() {
        createScenarioForRounding();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackConstsumRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        ConstsumRecipientRow aliceRow = findByName(statistics.getRows(), "Alice");
        // alice receives: 10 + 20 + 31 = 61 from 3 givers → avg = 61/3 = 20.33...
        assertEquals(aliceRow.getTotal(), 61);
        assertEquals(aliceRow.getAverage(), 20.33);
    }

    // ==================== Recipient view ====================

    @Test
    public void calculateForRecipient_returnsOnlyRowsForCurrentRecipient() {
        createScenarioWithCrossRatings();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3", "r4");

        FeedbackConstsumRecipientsStatistics statistics = calculator.calculateForRecipient(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle,
                student("alice"));

        assertEquals(statistics.getStatisticsView(), FeedbackQuestionResultsStatisticsView.RECIPIENT);
        List<ConstsumRecipientRow> rows = statistics.getRows();
        assertEquals(rows.size(), 1);

        ConstsumRecipientRow row = rows.get(0);
        assertEquals(row.getRecipientName(), "Alice");
        assertTrue(row.isCurrentRecipient());
        assertEquals(row.getTotal(), 100);
        assertEquals(row.getAverageExcludingSelf(), 70.0);
    }

    @Test
    public void calculateForRecipient_noNonSelfResponses_averageExcludingSelfIsNull() {
        createScenario();
        // r1: alice→alice(60), r2: alice→bob(40)
        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackConstsumRecipientsStatistics statistics = calculator.calculateForRecipient(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle,
                student("alice"));

        List<ConstsumRecipientRow> rows = statistics.getRows();
        assertEquals(rows.size(), 1);
        assertNull(rows.get(0).getAverageExcludingSelf());
    }

    // ==================== Email and team fields ====================

    @Test
    public void calculateCourseWide_recipientEmailAndTeamPopulated() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackConstsumRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        ConstsumRecipientRow row = statistics.getRows().get(0);
        assertEquals(row.getRecipientName(), "Alice");
        assertEquals(row.getRecipientEmail(), "alice@example.tmt");
        assertEquals(row.getRecipientTeam(), "Team Alpha");
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
                .details(buildDetails())
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));
        // alice rates alice=60, bob=40
        constsumResponse("r1", "alice", "alice", 60);
        constsumResponse("r2", "alice", "bob", 40);
    }

    private void createScenarioWithCrossRatings() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails())
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));
        // alice rates: alice=30, bob=70
        constsumResponse("r1", "alice", "alice", 30);
        constsumResponse("r2", "alice", "bob", 70);
        // bob rates: alice=70, bob=30
        constsumResponse("r3", "bob", "alice", 70);
        constsumResponse("r4", "bob", "bob", 30);
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
        constsumResponse("r1", "alice", "alice", 60);
        constsumResponse("r2", "alice", "charlie", 40);
        constsumResponse("r3", "bob", "bob", 100);
    }

    private void createScenarioMultipleGivers() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.student("charlie", s -> s.course("course").team("team").name("Charlie").email("charlie@example.tmt"));
        given.student("dave", s -> s.course("course").team("team").name("Dave").email("dave@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails())
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));
        // charlie rates: alice=30, bob=40, dave=30
        constsumResponse("r1", "charlie", "alice", 30);
        constsumResponse("r2", "charlie", "bob", 40);
        // dave rates: alice=40, bob=60
        constsumResponse("r3", "dave", "alice", 40);
        constsumResponse("r4", "dave", "bob", 60);
    }

    private void createScenarioForRounding() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.student("charlie", s -> s.course("course").team("team").name("Charlie").email("charlie@example.tmt"));
        given.student("dave", s -> s.course("course").team("team").name("Dave").email("dave@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails())
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));
        // 3 givers rate alice different amounts → alice: total=61, avg=61/3=20.33...
        constsumResponse("r1", "bob", "alice", 10);
        constsumResponse("r2", "charlie", "alice", 20);
        constsumResponse("r3", "dave", "alice", 31);
    }

    private FeedbackConstantSumRecipientsQuestionDetails buildDetails() {
        FeedbackConstantSumRecipientsQuestionDetails details = new FeedbackConstantSumRecipientsQuestionDetails();
        details.setPoints(100);
        details.setPointsPerOption(false);
        return details;
    }

    private void constsumResponse(String alias, String giverAlias, String recipientAlias, int answer) {
        FeedbackConstantSumRecipientsResponseDetails details = new FeedbackConstantSumRecipientsResponseDetails();
        details.setAnswers(List.of(answer));
        given.feedbackResponse(alias, r -> r.feedbackQuestion("question")
                .giverStudent(giverAlias)
                .recipientStudent(recipientAlias)
                .details(details));
    }

    private static ConstsumRecipientRow findByName(List<ConstsumRecipientRow> rows, String name) {
        return rows.stream()
                .filter(r -> r.getRecipientName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
