package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.SqlSessionResultsBundle;
import teammates.common.datatransfer.TeamEvalResult;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Student;

/**
 * Contains specific structure and processing logic for contribution feedback questions.
 */
public class FeedbackContributionQuestionDetails extends FeedbackQuestionDetails {

    static final String QUESTION_TYPE_NAME = "Team contribution question";
    static final String CONTRIB_ERROR_INVALID_OPTION =
            "Invalid option for the " + QUESTION_TYPE_NAME + ".";
    static final String CONTRIB_ERROR_INVALID_FEEDBACK_PATH =
            QUESTION_TYPE_NAME + " must have "
                    + "\"Students in this course\" and \"Giver's team members and Giver\" "
                    + "as the feedback giver and recipient respectively. "
                    + "These values will be used instead.";
    static final String CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS =
            QUESTION_TYPE_NAME + " must use one of the common visibility options. The "
                    + "\"Shown anonymously to recipient and team members, visible to instructors\" "
                    + "option will be used instead.";

    private static final int SUMMARY_INDEX_CLAIMED = 0;
    private static final int SUMMARY_INDEX_PERCEIVED = 1;

    private static final Logger log = Logger.getLogger();

    private boolean isZeroSum;
    private boolean isNotSureAllowed;

    public FeedbackContributionQuestionDetails() {
        this(null);
    }

    public FeedbackContributionQuestionDetails(String questionText) {
        super(FeedbackQuestionType.CONTRIB, questionText);
        isZeroSum = true;
        isNotSureAllowed = false;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackContributionQuestionDetails newContribDetails = (FeedbackContributionQuestionDetails) newDetails;
        return newContribDetails.isZeroSum != this.isZeroSum
                || newContribDetails.isNotSureAllowed != this.isNotSureAllowed;
    }

    @Override
    public boolean isIndividualResponsesShownToStudents() {
        return false;
    }

    @Override
    public String getQuestionResultStatisticsJson(
            FeedbackQuestion question, String studentEmail, SqlSessionResultsBundle bundle) {
        List<FeedbackResponse> responses = bundle.getQuestionResponseMap().get(question);

        boolean isStudent = studentEmail != null;

        List<String> teamNames;
        if (isStudent) {
            teamNames = getTeamsWithAtLeastOneResponse(responses, bundle);
        } else {
            teamNames = new ArrayList<>(bundle.getRoster().getTeamToMembersTable().keySet());
        }

        // Each team's member (email) list
        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);

        // Each team's responses
        Map<String, List<FeedbackResponse>> teamResponses = getTeamResponses(responses, bundle, teamNames);

        // Get each team's submission array. -> int[teamSize][teamSize]
        // Where int[0][1] refers points from student 0 to student 1
        // Where student 0 is the 0th student in the list in teamMembersEmail
        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArraySql(teamNames, teamMembersEmail, teamResponses);

        // Each team's contribution question results.
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray);
        ContributionStatistics output = new ContributionStatistics();

        if (isStudent) {
            String currentUserTeam = bundle.getRoster().getInfoForIdentifier(studentEmail).getTeamName();
            TeamEvalResult currentUserTeamResults = teamResults.get(currentUserTeam);
            if (currentUserTeamResults != null) {
                List<String> teamEmails = teamMembersEmail.get(currentUserTeam);
                int currentUserIndex = teamEmails.indexOf(studentEmail);
                int[] claimedNumbers = currentUserTeamResults.claimed[currentUserIndex];
                int[] perceivedNumbers = currentUserTeamResults.denormalizedAveragePerceived[currentUserIndex];

                int claimed = 0;
                int perceived = 0;
                Map<String, Integer> claimedOthers = new HashMap<>();
                List<Integer> perceivedOthers = new ArrayList<>();

                for (int i = 0; i < claimedNumbers.length; i++) {
                    if (i == currentUserIndex) {
                        claimed = claimedNumbers[i];
                    } else {
                        claimedOthers.put(teamEmails.get(i), claimedNumbers[i]);
                    }
                }

                for (int i = 0; i < perceivedNumbers.length; i++) {
                    if (i == currentUserIndex) {
                        perceived = perceivedNumbers[i];
                    } else {
                        perceivedOthers.add(perceivedNumbers[i]);
                    }
                }
                perceivedOthers.sort(Comparator.reverseOrder());

                output.results.put(studentEmail, new ContributionStatisticsEntry(claimed, perceived,
                        claimedOthers,
                        perceivedOthers.stream().mapToInt(i -> i).toArray()));
            }
        } else {
            Map<String, int[]> studentResults = getStudentResults(teamMembersEmail, teamResults);

            for (Map.Entry<String, int[]> entry : studentResults.entrySet()) {
                int[] summary = entry.getValue();
                String email = entry.getKey();
                String team = bundle.getRoster().getStudentForEmail(email).getTeam().getName();
                List<String> teamEmails = teamMembersEmail.get(team);
                TeamEvalResult teamResult = teamResults.get(team);
                int studentIndex = teamEmails.indexOf(email);
                Map<String, Integer> claimedOthers = new HashMap<>();
                List<Integer> perceivedOthers = new ArrayList<>();
                for (int i = 0; i < teamResult.normalizedPeerContributionRatio.length; i++) {
                    if (i != studentIndex) {
                        claimedOthers.put(teamEmails.get(i), teamResult.normalizedPeerContributionRatio[studentIndex][i]);
                        perceivedOthers.add(teamResult.normalizedPeerContributionRatio[i][studentIndex]);
                    }
                }
                perceivedOthers.sort(Comparator.reverseOrder());

                output.results.put(email, new ContributionStatisticsEntry(summary[SUMMARY_INDEX_CLAIMED],
                        summary[SUMMARY_INDEX_PERCEIVED],
                        claimedOthers, perceivedOthers.stream().mapToInt(i -> i).toArray()));
            }
        }

        return JsonUtils.toJson(output);
    }

    @Override
    public String getQuestionResultStatisticsJson(
            FeedbackQuestionAttributes question, String studentEmail, SessionResultsBundle bundle) {
        List<FeedbackResponseAttributes> responses = bundle.getQuestionResponseMap().get(question.getId());

        boolean isStudent = studentEmail != null;

        List<String> teamNames;
        if (isStudent) {
            teamNames = getTeamsWithAtLeastOneResponse(responses, bundle);
        } else {
            teamNames = new ArrayList<>(bundle.getRoster().getTeamToMembersTable().keySet());
        }

        // Each team's member (email) list
        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);

        // Each team's responses
        Map<String, List<FeedbackResponseAttributes>> teamResponses = getTeamResponses(responses, bundle, teamNames);

        // Get each team's submission array. -> int[teamSize][teamSize]
        // Where int[0][1] refers points from student 0 to student 1
        // Where student 0 is the 0th student in the list in teamMembersEmail
        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArray(teamNames, teamMembersEmail, teamResponses);

        // Each team's contribution question results.
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray);
        ContributionStatistics output = new ContributionStatistics();

        if (isStudent) {
            String currentUserTeam = bundle.getRoster().getInfoForIdentifier(studentEmail).getTeamName();
            TeamEvalResult currentUserTeamResults = teamResults.get(currentUserTeam);
            if (currentUserTeamResults != null) {
                List<String> teamEmails = teamMembersEmail.get(currentUserTeam);
                int currentUserIndex = teamEmails.indexOf(studentEmail);
                int[] claimedNumbers = currentUserTeamResults.claimed[currentUserIndex];
                int[] perceivedNumbers = currentUserTeamResults.denormalizedAveragePerceived[currentUserIndex];

                int claimed = 0;
                int perceived = 0;
                Map<String, Integer> claimedOthers = new HashMap<>();
                List<Integer> perceivedOthers = new ArrayList<>();

                for (int i = 0; i < claimedNumbers.length; i++) {
                    if (i == currentUserIndex) {
                        claimed = claimedNumbers[i];
                    } else {
                        claimedOthers.put(teamEmails.get(i), claimedNumbers[i]);
                    }
                }

                for (int i = 0; i < perceivedNumbers.length; i++) {
                    if (i == currentUserIndex) {
                        perceived = perceivedNumbers[i];
                    } else {
                        perceivedOthers.add(perceivedNumbers[i]);
                    }
                }
                perceivedOthers.sort(Comparator.reverseOrder());

                output.results.put(studentEmail, new ContributionStatisticsEntry(claimed, perceived,
                        claimedOthers,
                        perceivedOthers.stream().mapToInt(i -> i).toArray()));
            }
        } else {
            Map<String, int[]> studentResults = getStudentResults(teamMembersEmail, teamResults);

            for (Map.Entry<String, int[]> entry : studentResults.entrySet()) {
                int[] summary = entry.getValue();
                String email = entry.getKey();
                String team = bundle.getRoster().getStudentForEmail(email).getTeam();
                List<String> teamEmails = teamMembersEmail.get(team);
                TeamEvalResult teamResult = teamResults.get(team);
                int studentIndex = teamEmails.indexOf(email);
                Map<String, Integer> claimedOthers = new HashMap<>();
                List<Integer> perceivedOthers = new ArrayList<>();
                for (int i = 0; i < teamResult.normalizedPeerContributionRatio.length; i++) {
                    if (i != studentIndex) {
                        claimedOthers.put(teamEmails.get(i), teamResult.normalizedPeerContributionRatio[studentIndex][i]);
                        perceivedOthers.add(teamResult.normalizedPeerContributionRatio[i][studentIndex]);
                    }
                }
                perceivedOthers.sort(Comparator.reverseOrder());

                output.results.put(email, new ContributionStatisticsEntry(summary[SUMMARY_INDEX_CLAIMED],
                        summary[SUMMARY_INDEX_PERCEIVED],
                        claimedOthers, perceivedOthers.stream().mapToInt(i -> i).toArray()));
            }
        }

        return JsonUtils.toJson(output);
    }

    private Map<String, int[]> getStudentResults(
            Map<String, List<String>> teamMembersEmail,
            Map<String, TeamEvalResult> teamResults) {
        Map<String, int[]> studentResults = new LinkedHashMap<>();
        teamResults.forEach((key, teamResult) -> {
            List<String> teamEmails = teamMembersEmail.get(key);
            for (int i = 0; i < teamEmails.size(); i++) {
                String studentEmail = teamEmails.get(i);
                int[] summary = new int[2];
                summary[SUMMARY_INDEX_CLAIMED] = teamResult.normalizedClaimed[i][i];
                summary[SUMMARY_INDEX_PERCEIVED] = teamResult.normalizedAveragePerceived[i];

                studentResults.put(studentEmail, summary);
            }
        });
        return studentResults;
    }

    private Map<String, TeamEvalResult> getTeamResults(List<String> teamNames,
            Map<String, int[][]> teamSubmissionArray) {
        Map<String, TeamEvalResult> teamResults = new LinkedHashMap<>();
        for (String team : teamNames) {
            TeamEvalResult teamEvalResult = new TeamEvalResult(teamSubmissionArray.get(team));
            teamResults.put(team, teamEvalResult);
        }
        return teamResults;
    }

    private Map<String, int[][]> getTeamSubmissionArray(List<String> teamNames,
            Map<String, List<String>> teamMembersEmail,
            Map<String, List<FeedbackResponseAttributes>> teamResponses) {
        Map<String, int[][]> teamSubmissionArray = new LinkedHashMap<>();
        for (String team : teamNames) {
            int teamSize = teamMembersEmail.get(team).size();
            teamSubmissionArray.put(team, new int[teamSize][teamSize]);
            //Initialize all as not submitted.
            for (int i = 0; i < teamSize; i++) {
                for (int j = 0; j < teamSize; j++) {
                    teamSubmissionArray.get(team)[i][j] = Const.POINTS_NOT_SUBMITTED;
                }
            }
            //Fill in submitted points
            List<FeedbackResponseAttributes> teamResponseList = teamResponses.get(team);
            List<String> memberEmailList = teamMembersEmail.get(team);
            for (FeedbackResponseAttributes response : teamResponseList) {
                int giverIndx = memberEmailList.indexOf(response.getGiver());
                int recipientIndx = memberEmailList.indexOf(response.getRecipient());
                if (giverIndx == -1 || recipientIndx == -1) {
                    continue;
                }
                int points = ((FeedbackContributionResponseDetails) response.getResponseDetailsCopy()).getAnswer();
                teamSubmissionArray.get(team)[giverIndx][recipientIndx] = points;
            }
        }
        return teamSubmissionArray;
    }

    private Map<String, int[][]> getTeamSubmissionArraySql(List<String> teamNames,
                                                        Map<String, List<String>> teamMembersEmail,
                                                        Map<String, List<FeedbackResponse>> teamResponses) {
        Map<String, int[][]> teamSubmissionArray = new LinkedHashMap<>();
        for (String team : teamNames) {
            int teamSize = teamMembersEmail.get(team).size();
            teamSubmissionArray.put(team, new int[teamSize][teamSize]);
            //Initialize all as not submitted.
            for (int i = 0; i < teamSize; i++) {
                for (int j = 0; j < teamSize; j++) {
                    teamSubmissionArray.get(team)[i][j] = Const.POINTS_NOT_SUBMITTED;
                }
            }
            //Fill in submitted points
            List<FeedbackResponse> teamResponseList = teamResponses.get(team);
            List<String> memberEmailList = teamMembersEmail.get(team);
            for (FeedbackResponse response : teamResponseList) {
                int giverIndx = memberEmailList.indexOf(response.getGiver());
                int recipientIndx = memberEmailList.indexOf(response.getRecipient());
                if (giverIndx == -1 || recipientIndx == -1) {
                    continue;
                }
                int points = ((FeedbackContributionResponseDetails) response.getFeedbackResponseDetailsCopy()).getAnswer();
                teamSubmissionArray.get(team)[giverIndx][recipientIndx] = points;
            }
        }
        return teamSubmissionArray;
    }

    private Map<String, List<FeedbackResponseAttributes>> getTeamResponses(
            List<FeedbackResponseAttributes> responses, SessionResultsBundle bundle, List<String> teamNames) {
        Map<String, List<FeedbackResponseAttributes>> teamResponses = new LinkedHashMap<>();
        for (String teamName : teamNames) {
            teamResponses.put(teamName, new ArrayList<>());
        }
        for (FeedbackResponseAttributes response : responses) {
            String team = bundle.getRoster().getInfoForIdentifier(response.getGiver()).getTeamName();
            if (teamResponses.containsKey(team)) {
                teamResponses.get(team).add(response);
            }
        }
        return teamResponses;
    }

    private Map<String, List<FeedbackResponse>> getTeamResponses(
            List<FeedbackResponse> responses, SqlSessionResultsBundle bundle, List<String> teamNames) {
        Map<String, List<FeedbackResponse>> teamResponses = new LinkedHashMap<>();
        for (String teamName : teamNames) {
            teamResponses.put(teamName, new ArrayList<>());
        }
        for (FeedbackResponse response : responses) {
            String team = bundle.getRoster().getInfoForIdentifier(response.getGiver()).getTeamName();
            if (teamResponses.containsKey(team)) {
                teamResponses.get(team).add(response);
            }
        }
        return teamResponses;
    }

    private Map<String, List<String>> getTeamMembersEmail(
            SessionResultsBundle bundle, List<String> teamNames) {
        Map<String, List<String>> teamMembersEmail = new LinkedHashMap<>();
        for (String teamName : teamNames) {
            List<String> memberEmails = bundle.getRoster().getTeamToMembersTable().get(teamName)
                    .stream().map(StudentAttributes::getEmail)
                    .collect(Collectors.toList());
            teamMembersEmail.put(teamName, memberEmails);
        }
        return teamMembersEmail;
    }

    private Map<String, List<String>> getTeamMembersEmail(
            SqlSessionResultsBundle bundle, List<String> teamNames) {
        Map<String, List<String>> teamMembersEmail = new LinkedHashMap<>();
        for (String teamName : teamNames) {
            List<String> memberEmails = bundle.getRoster().getTeamToMembersTable().get(teamName)
                    .stream().map(Student::getEmail)
                    .collect(Collectors.toList());
            teamMembersEmail.put(teamName, memberEmails);
        }
        return teamMembersEmail;
    }

    private List<String> getTeamsWithAtLeastOneResponse(
            List<FeedbackResponseAttributes> responses, SessionResultsBundle bundle) {
        Set<String> teamNames = new HashSet<>();
        for (FeedbackResponseAttributes response : responses) {
            String teamNameOfResponseGiver = bundle.getRoster().getInfoForIdentifier(response.getGiver()).getTeamName();
            teamNames.add(teamNameOfResponseGiver);
        }
        return new ArrayList<>(teamNames);
    }

    private List<String> getTeamsWithAtLeastOneResponse(
            List<FeedbackResponse> responses, SqlSessionResultsBundle bundle) {
        Set<String> teamNames = new HashSet<>();
        for (FeedbackResponse response : responses) {
            String teamNameOfResponseGiver = bundle.getRoster().getInfoForIdentifier(response.getGiver()).getTeamName();
            teamNames.add(teamNameOfResponseGiver);
        }
        return new ArrayList<>(teamNames);
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();

        if (isZeroSum && isNotSureAllowed) {
            errors.add(CONTRIB_ERROR_INVALID_OPTION);
        }

        return errors;
    }

    @Override
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses, int numRecipients) {
        List<String> errors = new ArrayList<>();

        // Nothing to validate if there are no responses
        boolean isAllNotSubmitted = responses
                .stream()
                .allMatch(r -> ((FeedbackContributionResponseDetails) r).getAnswer() == Const.POINTS_NOT_SUBMITTED);
        if (isAllNotSubmitted) {
            return errors;
        }

        int actualTotal = 0;
        for (FeedbackResponseDetails response : responses) {
            FeedbackContributionResponseDetails details = (FeedbackContributionResponseDetails) response;
            boolean validAnswer = false;

            // Valid answers: 0, 5, 10, 15, .... 190, 195, 200
            boolean isValidRange = details.getAnswer() >= 0 && details.getAnswer() <= 200;
            boolean isMultipleOf5 = details.getAnswer() % 5 == 0;
            if (isValidRange && isMultipleOf5) {
                validAnswer = true;
            }

            boolean isValidNotSure = details.getAnswer() == Const.POINTS_NOT_SURE && isNotSureAllowed;
            boolean isValidNotSubmitted = details.getAnswer() == Const.POINTS_NOT_SUBMITTED && !isZeroSum;
            if (isValidNotSure || isValidNotSubmitted) {
                validAnswer = true;
            }

            if (!validAnswer) {
                errors.add(CONTRIB_ERROR_INVALID_OPTION);
            }

            actualTotal += details.getAnswer();
        }

        int expectedTotal = numRecipients * 100;
        if (actualTotal != expectedTotal && isZeroSum) {
            errors.add(CONTRIB_ERROR_INVALID_OPTION);
        }

        return errors;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestion feedbackQuestion) {
        String errorMsg = "";

        // giver type can only be STUDENTS
        if (feedbackQuestion.getGiverType() != FeedbackParticipantType.STUDENTS) {
            log.severe("Unexpected giverType for contribution question: " + feedbackQuestion.getGiverType()
                       + " (forced to :" + FeedbackParticipantType.STUDENTS + ")");
            feedbackQuestion.setGiverType(FeedbackParticipantType.STUDENTS);
            errorMsg = CONTRIB_ERROR_INVALID_FEEDBACK_PATH;
        }

        // recipient type can only be OWN_TEAM_MEMBERS_INCLUDING_SELF
        if (feedbackQuestion.getRecipientType() != FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF) {
            log.severe("Unexpected recipientType for contribution question: "
                       + feedbackQuestion.getRecipientType()
                       + " (forced to :" + FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF + ")");
            feedbackQuestion.setRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
            errorMsg = CONTRIB_ERROR_INVALID_FEEDBACK_PATH;
        }

        // restrictions on visibility options
        if (!(feedbackQuestion.getShowResponsesTo().contains(FeedbackParticipantType.RECEIVER)
                == feedbackQuestion.getShowResponsesTo().contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                && feedbackQuestion.getShowResponsesTo().contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                == feedbackQuestion.getShowResponsesTo().contains(FeedbackParticipantType.OWN_TEAM_MEMBERS))) {
            log.severe("Unexpected showResponsesTo for contribution question: "
                       + feedbackQuestion.getShowResponsesTo() + " (forced to :"
                       + "Shown anonymously to recipient and team members, visible to instructors"
                       + ")");
            feedbackQuestion.setShowResponsesTo(Arrays.asList(FeedbackParticipantType.RECEIVER,
                                                                       FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                                                                       FeedbackParticipantType.OWN_TEAM_MEMBERS,
                                                                       FeedbackParticipantType.INSTRUCTORS));
            errorMsg = CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS;
        }

        return errorMsg;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        String errorMsg = "";

        // giver type can only be STUDENTS
        if (feedbackQuestionAttributes.getGiverType() != FeedbackParticipantType.STUDENTS) {
            log.severe("Unexpected giverType for contribution question: " + feedbackQuestionAttributes.getGiverType()
                    + " (forced to :" + FeedbackParticipantType.STUDENTS + ")");
            feedbackQuestionAttributes.setGiverType(FeedbackParticipantType.STUDENTS);
            errorMsg = CONTRIB_ERROR_INVALID_FEEDBACK_PATH;
        }

        // recipient type can only be OWN_TEAM_MEMBERS_INCLUDING_SELF
        if (feedbackQuestionAttributes.getRecipientType() != FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF) {
            log.severe("Unexpected recipientType for contribution question: "
                    + feedbackQuestionAttributes.getRecipientType()
                    + " (forced to :" + FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF + ")");
            feedbackQuestionAttributes.setRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
            errorMsg = CONTRIB_ERROR_INVALID_FEEDBACK_PATH;
        }

        // restrictions on visibility options
        if (!(feedbackQuestionAttributes.getShowResponsesTo().contains(FeedbackParticipantType.RECEIVER)
                == feedbackQuestionAttributes.getShowResponsesTo().contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                && feedbackQuestionAttributes.getShowResponsesTo().contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                == feedbackQuestionAttributes.getShowResponsesTo().contains(FeedbackParticipantType.OWN_TEAM_MEMBERS))) {
            log.severe("Unexpected showResponsesTo for contribution question: "
                    + feedbackQuestionAttributes.getShowResponsesTo() + " (forced to :"
                    + "Shown anonymously to recipient and team members, visible to instructors"
                    + ")");
            feedbackQuestionAttributes.setShowResponsesTo(Arrays.asList(FeedbackParticipantType.RECEIVER,
                    FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                    FeedbackParticipantType.OWN_TEAM_MEMBERS,
                    FeedbackParticipantType.INSTRUCTORS));
            errorMsg = CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS;
        }

        return errorMsg;
    }

    @Override
    public boolean isInstructorCommentsOnResponsesAllowed() {
        return false;
    }

    public boolean isZeroSum() {
        return isZeroSum;
    }

    public boolean isNotSureAllowed() {
        return isNotSureAllowed;
    }

    public void setZeroSum(boolean zeroSum) {
        isZeroSum = zeroSum;
    }

    public void setNotSureAllowed(boolean notSureAllowed) {
        isNotSureAllowed = notSureAllowed;
    }

    /**
     * Represents a list of participants to their question statistics for one contribution question.
     */
    public static class ContributionStatistics {
        private final Map<String, ContributionStatisticsEntry> results = new HashMap<>();

        public Map<String, ContributionStatisticsEntry> getResults() {
            return results;
        }
    }

    /**
     * Represents the statistics of one feedback participant in one contribution question.
     *
     * <p>This class is a container for some representative values from {@link TeamEvalResult}.
     *
     * @see TeamEvalResult
     */
    public static class ContributionStatisticsEntry {
        private final int claimed;
        private final int perceived;
        private final Map<String, Integer> claimedOthers;
        private final int[] perceivedOthers;

        public ContributionStatisticsEntry(int claimed, int perceived, Map<String, Integer> claimedOthers,
                                           int[] perceivedOthers) {
            this.claimed = claimed;
            this.perceived = perceived;
            this.claimedOthers = claimedOthers;
            this.perceivedOthers = perceivedOthers;
        }

        public int getClaimed() {
            return claimed;
        }

        public int getPerceived() {
            return perceived;
        }

        public Map<String, Integer> getClaimedOthers() {
            return claimedOthers;
        }

        public int[] getPerceivedOthers() {
            return perceivedOthers;
        }
    }
}
