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
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.StudentResultSummary;
import teammates.common.datatransfer.TeamEvalResult;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;

public class FeedbackContributionQuestionDetails extends FeedbackQuestionDetails {

    private static final Logger log = Logger.getLogger();

    private boolean isNotSureAllowed;

    public FeedbackContributionQuestionDetails() {
        super(FeedbackQuestionType.CONTRIB);
        isNotSureAllowed = true;
    }

    public FeedbackContributionQuestionDetails(String questionText) {
        super(FeedbackQuestionType.CONTRIB, questionText);
        isNotSureAllowed = true;
    }

    public boolean isNotSureAllowed() {
        return isNotSureAllowed;
    }

    public void setNotSureAllowed(boolean notSureAllowed) {
        isNotSureAllowed = notSureAllowed;
    }

    @Override
    public List<String> getInstructions() {
        return null;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.CONTRIB;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackContributionQuestionDetails newContribDetails = (FeedbackContributionQuestionDetails) newDetails;
        return newContribDetails.isNotSureAllowed != this.isNotSureAllowed;
    }

    @Override
    public boolean isIndividualResponsesShownToStudents() {
        return false;
    }

    @Override
    public String getQuestionResultStatisticsJson(
            FeedbackQuestionAttributes question, String studentEmail, SessionResultsBundle bundle) {
        List<FeedbackResponseAttributes> responses = bundle.getQuestionResponseMap().get(question.getId());
        if (responses.isEmpty()) {
            return "";
        }

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
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray, teamMembersEmail);
        ContributionStatistics output = new ContributionStatistics();

        if (isStudent) {
            String currentUserTeam = bundle.getRoster().getInfoForIdentifier(studentEmail).getTeamName();
            TeamEvalResult currentUserTeamResults = teamResults.get(currentUserTeam);
            if (currentUserTeamResults != null) {
                int currentUserIndex = teamMembersEmail.get(currentUserTeam).indexOf(studentEmail);
                int[] claimedNumbers = currentUserTeamResults.claimed[currentUserIndex];
                int[] perceivedNumbers = currentUserTeamResults.denormalizedAveragePerceived[currentUserIndex];

                int claimed = 0;
                int perceived = 0;
                List<Integer> claimedOthers = new ArrayList<>();
                List<Integer> perceivedOthers = new ArrayList<>();

                for (int i = 0; i < claimedNumbers.length; i++) {
                    if (i == currentUserIndex) {
                        claimed = claimedNumbers[i];
                    } else {
                        claimedOthers.add(claimedNumbers[i]);
                    }
                }
                claimedOthers.sort(Comparator.reverseOrder());

                for (int i = 0; i < perceivedNumbers.length; i++) {
                    if (i == currentUserIndex) {
                        perceived = perceivedNumbers[i];
                    } else {
                        perceivedOthers.add(perceivedNumbers[i]);
                    }
                }
                perceivedOthers.sort(Comparator.reverseOrder());

                output.results.put(studentEmail, new ContributionStatisticsEntry(claimed, perceived,
                        claimedOthers.stream().mapToInt(i -> i).toArray(),
                        perceivedOthers.stream().mapToInt(i -> i).toArray()));
            }
        } else {
            Map<String, StudentResultSummary> studentResults = getStudentResults(teamMembersEmail, teamResults);

            for (Map.Entry<String, StudentResultSummary> entry : studentResults.entrySet()) {
                StudentResultSummary summary = entry.getValue();
                String email = entry.getKey();
                String team = bundle.getRoster().getStudentForEmail(email).getTeam();
                List<String> teamEmails = teamMembersEmail.get(team);
                TeamEvalResult teamResult = teamResults.get(team);
                int studentIndex = teamEmails.indexOf(email);
                List<Integer> perceivedOthers = new ArrayList<>();
                for (int i = 0; i < teamResult.normalizedPeerContributionRatio.length; i++) {
                    if (i != studentIndex) {
                        perceivedOthers.add(teamResult.normalizedPeerContributionRatio[i][studentIndex]);
                    }
                }
                perceivedOthers.sort(Comparator.reverseOrder());

                output.results.put(email, new ContributionStatisticsEntry(summary.claimedToInstructor,
                        summary.perceivedToInstructor,
                        new int[] {}, perceivedOthers.stream().mapToInt(i -> i).toArray()));
            }
        }

        return JsonUtils.toJson(output);
    }

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {

        if (responses.isEmpty()) {
            return "";
        }

        List<FeedbackResponseAttributes> actualResponses = bundle.getActualResponsesSortedByGqr(question);

        //List of teams visible to the instructor and in the selected section
        List<String> teamNames = getTeamNames(bundle);

        //Each team's member(email) list
        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);

        //Each team's responses
        Map<String, List<FeedbackResponseAttributes>> teamResponses = getTeamResponses(
                actualResponses, bundle, teamNames);

        //Get each team's submission array. -> int[teamSize][teamSize]
        //Where int[0][1] refers points from student 0 to student 1
        //Where student 0 is the 0th student in the list in teamMembersEmail
        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArray(
                teamNames, teamMembersEmail, teamResponses);

        //Each team's eval results.
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray, teamMembersEmail);

        //Each person's results summary
        Map<String, StudentResultSummary> studentResults = getStudentResults(
                teamMembersEmail, teamResults);

        //Check visibility of recipient
        boolean hideRecipient = false;

        FeedbackParticipantType type = question.recipientType;
        for (FeedbackResponseAttributes response : actualResponses) {
            if (!bundle.visibilityTable.get(response.getId())[1]
                    && type != FeedbackParticipantType.SELF
                    && type != FeedbackParticipantType.NONE) {
                hideRecipient = true;
            }
        }

        StringBuilder contribFragments = new StringBuilder();

        Map<String, String> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<String, StudentResultSummary> entry : studentResults.entrySet()) {
            StudentResultSummary summary = entry.getValue();
            String email = entry.getKey();
            String name = bundle.roster.getStudentForEmail(email).name;
            String team = bundle.roster.getStudentForEmail(email).team;

            List<String> teamEmails = teamMembersEmail.get(team);
            TeamEvalResult teamResult = teamResults.get(team);
            int studentIndx = teamEmails.indexOf(email);

            String displayName;
            String displayTeam;
            String displayEmail;
            if (hideRecipient) {
                displayName = FeedbackSessionResultsBundle.getAnonName(type, name);
                displayTeam = displayName + Const.TEAM_OF_EMAIL_OWNER;
                displayEmail = Const.USER_NOBODY_TEXT;
            } else {
                displayName = name;
                displayTeam = team;
                displayEmail = email;
            }

            int[] incomingPoints = new int[teamResult.normalizedPeerContributionRatio.length];
            for (int i = 0; i < incomingPoints.length; i++) {
                incomingPoints[i] = teamResult.normalizedPeerContributionRatio[i][studentIndx];
            }

            String contribFragmentString =
                    SanitizationHelper.sanitizeForCsv(displayTeam) + ","
                    + SanitizationHelper.sanitizeForCsv(displayName) + ","
                    + SanitizationHelper.sanitizeForCsv(displayEmail) + ","
                    + SanitizationHelper.sanitizeForCsv(Integer.toString(summary.claimedToInstructor)) + ","
                    + SanitizationHelper.sanitizeForCsv(Integer.toString(summary.perceivedToInstructor)) + ","
                    + getNormalizedPointsListDescending(incomingPoints, studentIndx)
                    + System.lineSeparator();

            // Replace all Unset values
            contribFragmentString = contribFragmentString.replaceAll(Integer.toString(Const.INT_UNINITIALIZED), "N/A");
            contribFragmentString = contribFragmentString.replaceAll(Integer.toString(Const.POINTS_NOT_SURE), "Not Sure");
            contribFragmentString =
                    contribFragmentString.replaceAll(Integer.toString(Const.POINTS_NOT_SUBMITTED), "Not Submitted");

            //For sorting purposes
            sortedMap.put(displayTeam + "-%-" + displayName, contribFragmentString);

        }

        sortedMap.forEach((key, value) -> contribFragments.append(value));

        String csvPointsExplanation =
                SanitizationHelper.sanitizeForCsv("In the points given below, an equal share is equal to 100 points. "
                + "e.g. 80 means \"Equal share - 20%\" and 110 means \"Equal share + 10%\".") + System.lineSeparator()
                + "Claimed Contribution (CC) = the contribution claimed by the student." + System.lineSeparator()
                + "Perceived Contribution (PC) = the average value of student's contribution "
                + "as perceived by the team members." + System.lineSeparator()
                + "Team, Name, Email, CC, PC, Ratings Received" + System.lineSeparator();
        return csvPointsExplanation + contribFragments + System.lineSeparator();
    }

    private List<String> getTeamNames(FeedbackSessionResultsBundle bundle) {
        List<String> teamNames = new ArrayList<>();
        for (Set<String> teamNamesForSection : bundle.sectionTeamNameTable.values()) {
            teamNames.addAll(teamNamesForSection);
        }
        teamNames.sort(null);
        return teamNames;
    }

    /**
     * Returns A Map with student email as key and StudentResultSummary as value for the specified question.
     */
    Map<String, StudentResultSummary> getStudentResults(FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question) {

        List<FeedbackResponseAttributes> responses = bundle.getActualResponsesSortedByGqr(question);

        List<String> teamNames = getTeamsWithAtLeastOneResponse(responses, bundle);

        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);

        Map<String, List<FeedbackResponseAttributes>> teamResponses = getTeamResponses(
                responses, bundle, teamNames);

        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArray(
                teamNames, teamMembersEmail, teamResponses);

        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray, teamMembersEmail);

        return getStudentResults(teamMembersEmail, teamResults);
    }

    private Map<String, StudentResultSummary> getStudentResults(
            Map<String, List<String>> teamMembersEmail,
            Map<String, TeamEvalResult> teamResults) {
        Map<String, StudentResultSummary> studentResults = new LinkedHashMap<>();
        teamResults.forEach((key, teamResult) -> {
            List<String> teamEmails = teamMembersEmail.get(key);
            int i = 0;
            for (String studentEmail : teamEmails) {
                StudentResultSummary summary = new StudentResultSummary();
                summary.claimedToInstructor = teamResult.normalizedClaimed[i][i];
                summary.perceivedToInstructor = teamResult.normalizedAveragePerceived[i];

                studentResults.put(studentEmail, summary);

                i++;
            }
        });
        return studentResults;
    }

    /**
     * Returns A Map with student email as key and TeamEvalResult as value for the specified question.
     */
    Map<String, TeamEvalResult> getTeamEvalResults(FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question) {

        List<FeedbackResponseAttributes> responses = bundle.getActualResponsesSortedByGqr(question);

        List<String> teamNames = getTeamsWithAtLeastOneResponse(responses, bundle);

        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);

        Map<String, List<FeedbackResponseAttributes>> teamResponses = getTeamResponses(
                responses, bundle, teamNames);

        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArray(
                teamNames, teamMembersEmail, teamResponses);

        return getTeamResults(teamNames, teamSubmissionArray, teamMembersEmail);
    }

    private Map<String, TeamEvalResult> getTeamResults(List<String> teamNames,
            Map<String, int[][]> teamSubmissionArray, Map<String, List<String>> teamMembersEmail) {
        Map<String, TeamEvalResult> teamResults = new LinkedHashMap<>();
        for (String team : teamNames) {
            TeamEvalResult teamEvalResult = new TeamEvalResult(teamSubmissionArray.get(team));
            teamEvalResult.studentEmails = teamMembersEmail.get(team);
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
                int giverIndx = memberEmailList.indexOf(response.giver);
                int recipientIndx = memberEmailList.indexOf(response.recipient);
                if (giverIndx == -1 || recipientIndx == -1) {
                    continue;
                }
                int points = ((FeedbackContributionResponseDetails) response.getResponseDetails()).getAnswer();
                teamSubmissionArray.get(team)[giverIndx][recipientIndx] = points;
            }
        }
        return teamSubmissionArray;
    }

    private Map<String, List<FeedbackResponseAttributes>> getTeamResponses(
            List<FeedbackResponseAttributes> responses,
            FeedbackSessionResultsBundle bundle, List<String> teamNames) {
        Map<String, List<FeedbackResponseAttributes>> teamResponses = new LinkedHashMap<>();
        for (String teamName : teamNames) {
            teamResponses.put(teamName, new ArrayList<>());
        }
        for (FeedbackResponseAttributes response : responses) {
            String team = bundle.emailTeamNameTable.get(response.giver);
            if (teamResponses.containsKey(team)) {
                teamResponses.get(team).add(response);
            }
        }
        return teamResponses;
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

    private Map<String, List<String>> getTeamMembersEmail(
            FeedbackSessionResultsBundle bundle, List<String> teamNames) {
        Map<String, List<String>> teamMembersEmail = new LinkedHashMap<>();
        for (String teamName : teamNames) {
            if (Const.USER_TEAM_FOR_INSTRUCTOR.equals(teamName)) {
                // skip instructors team (contrib questions should only have responses from student teams)
                continue;
            }
            List<String> memberEmails = new ArrayList<>(bundle.rosterTeamNameMembersTable.get(teamName));
            memberEmails.sort(null);
            teamMembersEmail.put(teamName, memberEmails);
        }
        return teamMembersEmail;
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
            List<FeedbackResponseAttributes> responses,
            FeedbackSessionResultsBundle bundle) {
        List<String> teamNames = new ArrayList<>();
        for (FeedbackResponseAttributes response : responses) {
            String teamNameOfResponseGiver = bundle.getTeamNameForEmail(response.giver);
            if (!teamNames.contains(teamNameOfResponseGiver)) {
                teamNames.add(teamNameOfResponseGiver);
            }
        }
        return teamNames;
    }

    private static String getNormalizedPointsListDescending(int[] subs, int index) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < subs.length; i++) {
            if (i == index) {
                continue;
            }
            result.add(Integer.toString(subs[i]));
        }
        if (result.isEmpty()) {
            return Integer.toString(Const.INT_UNINITIALIZED);
        }
        result.sort(Comparator.reverseOrder());

        StringBuilder resultString = new StringBuilder();
        for (String s : result) {
            if (resultString.length() != 0) {
                resultString.append(", ");
            }
            resultString.append(s);
        }
        return resultString.toString();
    }

    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    @Override
    public List<String> validateQuestionDetails() {
        return new ArrayList<>();
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        String errorMsg = "";

        // giver type can only be STUDENTS
        if (feedbackQuestionAttributes.giverType != FeedbackParticipantType.STUDENTS) {
            log.severe("Unexpected giverType for contribution question: " + feedbackQuestionAttributes.giverType
                       + " (forced to :" + FeedbackParticipantType.STUDENTS + ")");
            feedbackQuestionAttributes.giverType = FeedbackParticipantType.STUDENTS;
            errorMsg = Const.FeedbackQuestion.CONTRIB_ERROR_INVALID_FEEDBACK_PATH;
        }

        // recipient type can only be OWN_TEAM_MEMBERS_INCLUDING_SELF
        if (feedbackQuestionAttributes.recipientType != FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF) {
            log.severe("Unexpected recipientType for contribution question: "
                       + feedbackQuestionAttributes.recipientType
                       + " (forced to :" + FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF + ")");
            feedbackQuestionAttributes.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;
            errorMsg = Const.FeedbackQuestion.CONTRIB_ERROR_INVALID_FEEDBACK_PATH;
        }

        // restrictions on visibility options
        if (!(feedbackQuestionAttributes.showResponsesTo.contains(FeedbackParticipantType.RECEIVER)
                == feedbackQuestionAttributes.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                && feedbackQuestionAttributes.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                == feedbackQuestionAttributes.showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS))) {
            log.severe("Unexpected showResponsesTo for contribution question: "
                       + feedbackQuestionAttributes.showResponsesTo + " (forced to :"
                       + Const.FeedbackQuestion.COMMON_VISIBILITY_OPTIONS
                                               .get("ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS")
                       + ")");
            feedbackQuestionAttributes.showResponsesTo = Arrays.asList(FeedbackParticipantType.RECEIVER,
                                                                       FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                                                                       FeedbackParticipantType.OWN_TEAM_MEMBERS,
                                                                       FeedbackParticipantType.INSTRUCTORS);
            errorMsg = Const.FeedbackQuestion.CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS;
        }

        return errorMsg;
    }

    /**
     * Converts points in integer to String.
     * @return points in text form "Equal Share..."
     */
    static String convertToEqualShareFormat(int i) {
        if (i > 100) {
            return "Equal share + " + (i - 100) + "%"; // Do more
        } else if (i == 100) {
            return "Equal share"; // Do same
        } else if (i > 0) {
            return "Equal share - " + (100 - i) + "%"; // Do less
        } else if (i == 0) {
            return "0%"; // Do none
        } else if (i == Const.POINTS_NOT_SURE) {
            return "Not Sure";
        } else {
            return "";
        }
    }

    @Override
    public boolean isInstructorCommentsOnResponsesAllowed() {
        return false;
    }

    @Override
    public boolean isFeedbackParticipantCommentsOnResponsesAllowed() {
        return false;
    }

    public static class ContributionStatistics {
        public final Map<String, ContributionStatisticsEntry> results = new HashMap<>();

        public Map<String, ContributionStatisticsEntry> getResults() {
            return results;
        }
    }

    public static class ContributionStatisticsEntry {
        public final int claimed;
        public final int perceived;
        public final int[] claimedOthers;
        public final int[] perceivedOthers;

        public ContributionStatisticsEntry(int claimed, int perceived, int[] claimedOthers, int[] perceivedOthers) {
            this.claimed = claimed;
            this.perceived = perceived;
            this.claimedOthers = claimedOthers;
            this.perceivedOthers = perceivedOthers;
        }
    }

}
