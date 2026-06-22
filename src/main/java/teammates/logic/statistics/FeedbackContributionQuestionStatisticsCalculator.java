package teammates.logic.statistics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.TeamEvalResult;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackContributionCourseWideStatistics;
import teammates.common.datatransfer.statistics.FeedbackContributionRecipientStatistics;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;

/**
 * Calculates contribution question statistics for results pages.
 */
public class FeedbackContributionQuestionStatisticsCalculator implements
        FeedbackQuestionStatisticsCalculator<
                FeedbackContributionCourseWideStatistics, FeedbackContributionRecipientStatistics> {

    @Override
    public FeedbackContributionCourseWideStatistics calculateCourseWide(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle) {
        ContributionComputationContext context = buildContext(bundle, responses, null);
        FeedbackContributionCourseWideStatistics statistics = new FeedbackContributionCourseWideStatistics();
        List<FeedbackContributionCourseWideStatistics.CourseWideRow> rows = new ArrayList<>();

        context.teamResults.forEach((teamId, teamResult) -> {
            List<Student> teamMembers = context.teamIdToMembers.getOrDefault(teamId, List.of());
            for (int i = 0; i < teamMembers.size(); i++) {
                Student student = teamMembers.get(i);
                FeedbackContributionCourseWideStatistics.CourseWideRow row =
                        new FeedbackContributionCourseWideStatistics.CourseWideRow();
                row.setTeamName(student.getTeamName());
                row.setRecipientName(student.getName());
                row.setRecipientEmail(student.getEmail());

                int claimed = teamResult.normalizedClaimed[i][i];
                int perceived = teamResult.normalizedAveragePerceived[i];
                row.setClaimed(claimed);
                row.setPerceived(perceived);
                row.setDiff(claimed < 0 || perceived < 0 ? Const.POINTS_NOT_SUBMITTED : perceived - claimed);

                List<Integer> ratingsReceived = new ArrayList<>();
                for (int giverIndex = 0; giverIndex < teamResult.normalizedPeerContributionRatio.length; giverIndex++) {
                    if (giverIndex != i) {
                        ratingsReceived.add(teamResult.normalizedPeerContributionRatio[giverIndex][i]);
                    }
                }
                ratingsReceived.sort(Comparator.reverseOrder());
                row.setRatingsReceived(ratingsReceived);
                rows.add(row);
            }
        });

        rows.sort(Comparator.comparing(FeedbackContributionCourseWideStatistics.CourseWideRow::getTeamName)
                .thenComparing(FeedbackContributionCourseWideStatistics.CourseWideRow::getRecipientName)
                .thenComparing(row -> Objects.toString(row.getRecipientEmail(), "")));
        statistics.setRows(rows);
        return statistics;
    }

    @Override
    public FeedbackContributionRecipientStatistics calculateForRecipient(
            FeedbackQuestion question, List<FeedbackResponse> responses, SessionResultsBundle bundle, User recipient) {
        Student recipientStudent = recipient instanceof Student s ? s : null;
        ContributionComputationContext context = buildContext(bundle, responses, recipientStudent);
        FeedbackContributionRecipientStatistics statistics = new FeedbackContributionRecipientStatistics();

        if (recipientStudent == null) {
            // no recipient means we are calculating for the entire course,
            // so we don't have recipient-specific stats to compute
            return statistics;
        }

        TeamEvalResult teamResult = context.teamResults.get(recipientStudent.getTeamId());
        List<Student> teamMembers = context.teamIdToMembers.get(recipientStudent.getTeamId());
        int recipientIndex = teamMembers == null ? -1 : indexOfStudent(teamMembers, recipientStudent.getId());
        if (teamResult == null || recipientIndex < 0) {
            return statistics;
        }

        FeedbackContributionRecipientStatistics.RecipientView myView =
                new FeedbackContributionRecipientStatistics.RecipientView();
        myView.setOfMe(teamResult.claimed[recipientIndex][recipientIndex]);
        List<Integer> myViewOfOthers = new ArrayList<>();
        for (int i = 0; i < teamResult.claimed[recipientIndex].length; i++) {
            if (i != recipientIndex) {
                myViewOfOthers.add(teamResult.claimed[recipientIndex][i]);
            }
        }
        myViewOfOthers.sort(Comparator.reverseOrder());
        myView.setOfOthers(myViewOfOthers);

        FeedbackContributionRecipientStatistics.RecipientView teamView =
                new FeedbackContributionRecipientStatistics.RecipientView();
        teamView.setOfMe(teamResult.denormalizedAveragePerceived[recipientIndex][recipientIndex]);
        List<Integer> teamViewOfOthers = new ArrayList<>();
        for (int i = 0; i < teamResult.denormalizedAveragePerceived[recipientIndex].length; i++) {
            if (i != recipientIndex) {
                teamViewOfOthers.add(teamResult.denormalizedAveragePerceived[recipientIndex][i]);
            }
        }
        teamViewOfOthers.sort(Comparator.reverseOrder());
        teamView.setOfOthers(teamViewOfOthers);

        statistics.setMyView(myView);
        statistics.setTeamView(teamView);
        return statistics;
    }

    /**
     * Returns normalized contribution values for each response in the given result context.
     */
    public Map<UUID, Integer> calculateNormalizedResponseValues(
            List<FeedbackResponse> responses, SessionResultsBundle bundle, Student recipient) {
        ContributionComputationContext context = buildContext(bundle, responses, recipient);
        Map<UUID, Integer> normalizedValues = new HashMap<>();

        for (FeedbackResponse response : responses) {
            UUID responseId = response.getId();
            UUID giverUserId = response.getGiver().getGiverUserId();
            UUID recipientUserId = response.getRecipient().getRecipientUserId();
            if (responseId == null || giverUserId == null || recipientUserId == null) {
                continue;
            }

            UUID teamId = response.getGiver().getTeamId();
            List<Student> teamMembers = context.teamIdToMembers.get(teamId);
            TeamEvalResult teamResult = context.teamResults.get(teamId);
            int giverIndex = teamMembers == null ? -1 : indexOfStudent(teamMembers, giverUserId);
            int responseRecipientIndex = teamMembers == null ? -1 : indexOfStudent(teamMembers, recipientUserId);
            if (teamResult == null || giverIndex < 0 || responseRecipientIndex < 0) {
                continue;
            }

            boolean isSelfResponse = giverUserId.equals(recipientUserId);
            int normalizedValue = isSelfResponse
                    ? teamResult.normalizedClaimed[giverIndex][responseRecipientIndex]
                    : teamResult.normalizedPeerContributionRatio[giverIndex][responseRecipientIndex];
            normalizedValues.put(responseId, normalizedValue);
        }
        return normalizedValues;
    }

    private ContributionComputationContext buildContext(
            SessionResultsBundle bundle, List<FeedbackResponse> responses, Student recipient) {
        Map<UUID, List<Student>> teamIdToMembers = getTeamIdToMembers(bundle);
        List<UUID> teamIds = recipient == null
                ? new ArrayList<>(teamIdToMembers.keySet())
                : List.of(recipient.getTeamId());
        Map<UUID, List<FeedbackResponse>> teamResponses = getTeamResponses(responses, teamIds);
        Map<UUID, int[][]> claimedValuesByTeam = getClaimedValuesByTeam(teamIds, teamIdToMembers, teamResponses);
        Map<UUID, TeamEvalResult> teamResults = getTeamResults(teamIds, claimedValuesByTeam);
        return new ContributionComputationContext(teamIdToMembers, teamResults);
    }

    private Map<UUID, List<Student>> getTeamIdToMembers(SessionResultsBundle bundle) {
        Map<UUID, List<Student>> teamIdToMembers = new LinkedHashMap<>();
        for (Student student : bundle.getRoster().getStudents()) {
            teamIdToMembers.computeIfAbsent(student.getTeamId(), key -> new ArrayList<>()).add(student);
        }
        return teamIdToMembers;
    }

    private int indexOfStudent(List<Student> students, UUID studentId) {
        for (int i = 0; i < students.size(); i++) {
            if (Objects.equals(students.get(i).getId(), studentId)) {
                return i;
            }
        }
        return -1;
    }

    private Map<UUID, TeamEvalResult> getTeamResults(List<UUID> teamIds, Map<UUID, int[][]> claimedValuesByTeam) {
        Map<UUID, TeamEvalResult> teamResults = new LinkedHashMap<>();
        for (UUID teamId : teamIds) {
            teamResults.put(teamId, calculateTeamResult(claimedValuesByTeam.get(teamId)));
        }
        return teamResults;
    }

    /**
     * Calculates the normalized team contribution result from raw claimed values.
     */
    static TeamEvalResult calculateTeamResult(int[][] claimedValues) {
        return new TeamEvalResult(claimedValues);
    }

    private Map<UUID, int[][]> getClaimedValuesByTeam(
            List<UUID> teamIds,
            Map<UUID, List<Student>> teamIdToMembers,
            Map<UUID, List<FeedbackResponse>> teamResponses) {
        Map<UUID, int[][]> claimedValuesByTeam = new LinkedHashMap<>();
        for (UUID teamId : teamIds) {
            List<Student> teamMembers = teamIdToMembers.getOrDefault(teamId, List.of());
            claimedValuesByTeam.put(teamId, buildClaimedValues(teamMembers, teamResponses.getOrDefault(teamId, List.of())));
        }
        return claimedValuesByTeam;
    }

    private int[][] buildClaimedValues(List<Student> teamMembers, List<FeedbackResponse> teamResponses) {
        int teamSize = teamMembers.size();
        int[][] claimedValues = new int[teamSize][teamSize];
        for (int i = 0; i < teamSize; i++) {
            for (int j = 0; j < teamSize; j++) {
                claimedValues[i][j] = Const.POINTS_NOT_SUBMITTED;
            }
        }

        for (FeedbackResponse response : teamResponses) {
            UUID giverUserId = response.getGiver().getGiverUserId();
            UUID recipientUserId = response.getRecipient().getRecipientUserId();
            if (giverUserId == null || recipientUserId == null) {
                continue;
            }
            int giverIndex = indexOfStudent(teamMembers, giverUserId);
            int recipientIndex = indexOfStudent(teamMembers, recipientUserId);
            if (giverIndex == -1 || recipientIndex == -1) {
                continue;
            }
            int points = ((FeedbackContributionResponseDetails) response.getFeedbackResponseDetailsCopy()).getAnswer();
            claimedValues[giverIndex][recipientIndex] = points;
        }
        return claimedValues;
    }

    private Map<UUID, List<FeedbackResponse>> getTeamResponses(List<FeedbackResponse> responses, List<UUID> teamIds) {
        Map<UUID, List<FeedbackResponse>> teamResponses = new LinkedHashMap<>();
        for (UUID teamId : teamIds) {
            teamResponses.put(teamId, new ArrayList<>());
        }
        for (FeedbackResponse response : responses) {
            UUID teamId = response.getGiver().getTeamId();
            if (teamResponses.containsKey(teamId)) {
                teamResponses.get(teamId).add(response);
            }
        }
        return teamResponses;
    }

    private static final class ContributionComputationContext {
        private final Map<UUID, List<Student>> teamIdToMembers;
        private final Map<UUID, TeamEvalResult> teamResults;

        private ContributionComputationContext(
                Map<UUID, List<Student>> teamIdToMembers,
                Map<UUID, TeamEvalResult> teamResults) {
            this.teamIdToMembers = teamIdToMembers;
            this.teamResults = teamResults;
        }
    }
}
