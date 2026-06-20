package teammates.logic.statistics;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRankRecipientsResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackRankRecipientsStatistics;
import teammates.common.datatransfer.statistics.FeedbackRankRecipientsStatistics.RankRecipientsRow;

/**
 * Tests for {@link FeedbackRankRecipientsQuestionStatisticsCalculator}.
 */
public class FeedbackRankRecipientsQuestionStatisticsCalculatorTest extends BaseStatisticsTestCase {

    private final FeedbackRankRecipientsQuestionStatisticsCalculator calculator =
            new FeedbackRankRecipientsQuestionStatisticsCalculator();

    // ==================== Course-wide ====================

    @Test
    public void calculateCourseWide_multipleRecipients_assignsOverallRanks() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackRankRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        List<RankRecipientsRow> rows = statistics.getRows();
        assertEquals(rows.size(), 2);

        // alice gets rank 1 from giver (avg 1.0), bob gets rank 2 (avg 2.0)
        RankRecipientsRow aliceRow = findByName(rows, "Alice");
        assertEquals(aliceRow.getRanksReceived(), List.of(1));
        assertEquals(aliceRow.getOverallRank(), Integer.valueOf(1));

        RankRecipientsRow bobRow = findByName(rows, "Bob");
        assertEquals(bobRow.getRanksReceived(), List.of(2));
        assertEquals(bobRow.getOverallRank(), Integer.valueOf(2));
    }

    @Test
    public void calculateCourseWide_selfRankTrackedSeparately() {
        createScenarioWithSelfRating();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3", "r4");

        FeedbackRankRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        RankRecipientsRow aliceRow = findByName(statistics.getRows(), "Alice");
        // alice→alice=1 (self), bob→alice=2 (non-self)
        assertEquals(aliceRow.getSelfRank(), Integer.valueOf(1));
        assertEquals(aliceRow.getRanksReceived(), List.of(1, 2));
        // rankExcludingSelf: alice receives 2 from bob, bob receives 2 from alice → tied at rank 1
        assertEquals(aliceRow.getRankExcludingSelf(), Integer.valueOf(1));
    }

    @Test
    public void calculateCourseWide_noNonSelfResponses_rankExcludingSelfIsNull() {
        createScenarioWithSelfRating();
        // r1: alice→alice=1 only (self-response, no others rating alice)
        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackRankRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        RankRecipientsRow aliceRow = findByName(statistics.getRows(), "Alice");
        assertEquals(aliceRow.getSelfRank(), Integer.valueOf(1));
        assertNull(aliceRow.getRankExcludingSelf());
    }

    @Test
    public void calculateCourseWide_noResponses_returnsEmptyRows() {
        createScenario();

        FeedbackRankRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"), List.of(),
                bundleForQuestion("question"));

        assertEquals(statistics.getRows().size(), 0);
    }

    @Test
    public void calculateCourseWide_sortedByTeamThenName() {
        createScenarioTwoTeams();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3");

        FeedbackRankRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        List<RankRecipientsRow> rows = statistics.getRows();
        assertEquals(rows.size(), 3);
        assertEquals(rows.get(0).getRecipientName(), "Alice");
        assertEquals(rows.get(0).getRecipientTeam(), "Team Alpha");
        assertEquals(rows.get(1).getRecipientName(), "Charlie");
        assertEquals(rows.get(1).getRecipientTeam(), "Team Alpha");
        assertEquals(rows.get(2).getRecipientName(), "Bob");
        assertEquals(rows.get(2).getRecipientTeam(), "Team Beta");
    }

    @Test
    public void calculateCourseWide_ownTeamMembers_computesTeamRank() {
        createScenarioOwnTeamMembers();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2", "r3", "r4");

        FeedbackRankRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // alice rates alice=1, bob=2; bob rates alice=1, bob=2
        // all in same team → team rank matches overall rank
        RankRecipientsRow aliceRow = findByName(statistics.getRows(), "Alice");
        assertEquals(aliceRow.getRankInTeam(), Integer.valueOf(1));

        RankRecipientsRow bobRow = findByName(statistics.getRows(), "Bob");
        assertEquals(bobRow.getRankInTeam(), Integer.valueOf(2));
    }

    @Test
    public void calculateCourseWide_recipientsInSameTeam_computesTeamRank() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1", "r2");

        FeedbackRankRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        // alice gets rank 1 from giver (avg 1.0), bob gets rank 2 (avg 2.0) — same within team
        RankRecipientsRow aliceRow = findByName(statistics.getRows(), "Alice");
        assertEquals(aliceRow.getRankInTeam(), Integer.valueOf(1));

        RankRecipientsRow bobRow = findByName(statistics.getRows(), "Bob");
        assertEquals(bobRow.getRankInTeam(), Integer.valueOf(2));
    }

    @Test
    public void calculateCourseWide_recipientEmailAndTeamPopulated() {
        createScenario();
        SessionResultsBundle bundle = bundleForQuestion("question", "r1");

        FeedbackRankRecipientsStatistics statistics = calculator.calculateCourseWide(
                question("question"),
                bundle.getQuestionResponseMap().get(question("question")),
                bundle);

        RankRecipientsRow row = statistics.getRows().get(0);
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
        given.student("giver", s -> s.course("course").team("team").name("Giver").email("giver@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails())
                .recipientType(QuestionRecipientType.STUDENTS));
        // giver ranks: alice=1, bob=2
        rankResponse("r1", "giver", "alice", 1);
        rankResponse("r2", "giver", "bob", 2);
    }

    private void createScenarioWithSelfRating() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails())
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF));
        // alice rates: alice=1 (self), bob=2
        rankResponse("r1", "alice", "alice", 1);
        rankResponse("r2", "alice", "bob", 2);
        // bob rates: alice=2, bob=1 (self)
        rankResponse("r3", "bob", "alice", 2);
        rankResponse("r4", "bob", "bob", 1);
    }

    private void createScenarioTwoTeams() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("teamA", t -> t.section("section").name("Team Alpha"));
        given.team("teamB", t -> t.section("section").name("Team Beta"));
        given.student("alice", s -> s.course("course").team("teamA").name("Alice").email("alice@example.tmt"));
        given.student("charlie", s -> s.course("course").team("teamA").name("Charlie").email("charlie@example.tmt"));
        given.student("bob", s -> s.course("course").team("teamB").name("Bob").email("bob@example.tmt"));
        given.student("giver", s -> s.course("course").team("teamA").name("Giver").email("giver@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails())
                .recipientType(QuestionRecipientType.STUDENTS));
        rankResponse("r1", "giver", "alice", 1);
        rankResponse("r2", "giver", "charlie", 2);
        rankResponse("r3", "giver", "bob", 3);
    }

    private void createScenarioOwnTeamMembers() {
        given.course("course", c -> c.name("Course"));
        given.section("section", s -> s.course("course").name("Section A"));
        given.team("team", t -> t.section("section").name("Team Alpha"));
        given.student("alice", s -> s.course("course").team("team").name("Alice").email("alice@example.tmt"));
        given.student("bob", s -> s.course("course").team("team").name("Bob").email("bob@example.tmt"));
        given.feedbackSession("session", fs -> fs.course("course"));
        given.feedbackQuestion("question", q -> q.feedbackSession("session")
                .details(buildDetails())
                .recipientType(QuestionRecipientType.OWN_TEAM_MEMBERS));
        // alice rates: alice=1, bob=2
        rankResponse("r1", "alice", "alice", 1);
        rankResponse("r2", "alice", "bob", 2);
        // bob rates: alice=1, bob=2
        rankResponse("r3", "bob", "alice", 1);
        rankResponse("r4", "bob", "bob", 2);
    }

    private FeedbackRankRecipientsQuestionDetails buildDetails() {
        return new FeedbackRankRecipientsQuestionDetails();
    }

    private void rankResponse(String alias, String giverAlias, String recipientAlias, int answer) {
        FeedbackRankRecipientsResponseDetails details = new FeedbackRankRecipientsResponseDetails();
        details.setAnswer(answer);
        given.feedbackResponse(alias, r -> r.feedbackQuestion("question")
                .giverStudent(giverAlias)
                .recipientStudent(recipientAlias)
                .details(details));
    }

    private static RankRecipientsRow findByName(List<RankRecipientsRow> rows, String name) {
        return rows.stream()
                .filter(r -> r.getRecipientName().equals(name))
                .findFirst()
                .orElseThrow();
    }
}
