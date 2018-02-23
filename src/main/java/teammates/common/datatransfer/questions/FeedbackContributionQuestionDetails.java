package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.StudentResultSummary;
import teammates.common.datatransfer.TeamEvalResult;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestion.FormTemplates;
import teammates.common.util.Templates.FeedbackQuestion.Slots;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

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

    private void setContributionQuestionDetails(boolean isNotSureAllowed) {
        this.isNotSureAllowed = isNotSureAllowed;
    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        String isNotSureAllowedString = HttpRequestHelper.getValueFromParamMap(
                requestParameters,
                Const.ParamsNames.FEEDBACK_QUESTION_CONTRIBISNOTSUREALLOWED);
        boolean isNotSureAllowed = "on".equals(isNotSureAllowedString);
        this.setContributionQuestionDetails(isNotSureAllowed);
        return true;
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
    public boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackContributionQuestionDetails newContribDetails = (FeedbackContributionQuestionDetails) newDetails;
        return newContribDetails.isNotSureAllowed != this.isNotSureAllowed;
    }

    @Override
    public boolean isIndividualResponsesShownToStudents() {
        return false;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, int totalNumRecipients, FeedbackResponseDetails existingResponseDetails) {

        FeedbackContributionResponseDetails frd = (FeedbackContributionResponseDetails) existingResponseDetails;
        int points = frd.getAnswer();
        String optionSelectFragmentsHtml = getContributionOptionsHtml(points);

        return Templates.populateTemplate(
                FormTemplates.CONTRIB_SUBMISSION_FORM,
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                Slots.CONTRIB_SELECT_FRAGMENTS_HTML, optionSelectFragmentsHtml,
                Slots.CONTRIB_EQUAL_SHARE_HELP, getEqualShareHelpLinkIfNeeded(responseIdx));
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients) {

        String optionSelectHtml = getContributionOptionsHtml(Const.INT_UNINITIALIZED);

        return Templates.populateTemplate(
                FormTemplates.CONTRIB_SUBMISSION_FORM,
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                Slots.DISABLED, sessionIsOpen ? "" : "disabled",
                Slots.CONTRIB_SELECT_FRAGMENTS_HTML, optionSelectHtml,
                Slots.CONTRIB_EQUAL_SHARE_HELP, getEqualShareHelpLinkIfNeeded(responseIdx));
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        return Templates.populateTemplate(
                FormTemplates.CONTRIB_EDIT_FORM,
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.CONTRIB_IS_NOT_SURE_ALLOWED_CHECKED, isNotSureAllowed ? "checked" : "",
                Slots.CONTRIB_PARAM_IS_NOT_SURE_ALLOWED_CHECKED,
                        Const.ParamsNames.FEEDBACK_QUESTION_CONTRIBISNOTSUREALLOWED);
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        isNotSureAllowed = true;

        return "<div id=\"contribForm\">"
                  + getQuestionSpecificEditFormHtml(-1)
             + "</div>";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        String additionalInfo = this.getQuestionTypeDisplayName();

        return Templates.populateTemplate(
                FormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
                Slots.MORE, "[more]",
                Slots.LESS, "[less]",
                Slots.QUESTION_NUMBER, Integer.toString(questionNumber),
                Slots.ADDITIONAL_INFO_ID, additionalInfoId,
                Slots.QUESTION_ADDITIONAL_INFO, additionalInfo);
    }

    /**
     * Uses classes from evaluations to calculate statistics.
     * Uses actualResponses from FeedbackSessionResultsBundle - need to hide data that should be hidden.
     *      Hide name and teamName if recipient should not be visible.
     */
    @Override
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            String studentEmail,
            FeedbackSessionResultsBundle bundle,
            String view) {
        if ("question".equals(view)) { //for instructor, only question view has stats.
            return getQuestionResultsStatisticsHtmlQuestionView(responses, question, bundle);
        } else if ("student".equals(view)) { //Student view of stats.
            return getQuestionResultStatisticsHtmlStudentView(responses, question, studentEmail, bundle);
        } else {
            return "";
        }
    }

    private String getQuestionResultStatisticsHtmlStudentView(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            String studentEmail,
            FeedbackSessionResultsBundle bundle) {

        if (responses.isEmpty()) {
            return "";
        }

        String currentUserTeam = bundle.emailTeamNameTable.get(studentEmail);

        List<FeedbackResponseAttributes> actualResponses = getActualResponses(question, bundle);

        //List of teams with at least one response
        List<String> teamNames = getTeamsWithAtLeastOneResponse(actualResponses, bundle);

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

        //Each team's contribution question results.
        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray, teamMembersEmail);

        TeamEvalResult currentUserTeamResults = teamResults.get(currentUserTeam);
        if (currentUserTeamResults == null) {
            return "";
        }

        int currentUserIndex = teamMembersEmail.get(currentUserTeam).indexOf(studentEmail);
        int selfClaim = currentUserTeamResults.claimed[currentUserIndex][currentUserIndex];
        int teamClaim = currentUserTeamResults.denormalizedAveragePerceived[currentUserIndex][currentUserIndex];

        String contribAdditionalInfo = Templates.populateTemplate(
                FormTemplates.CONTRIB_ADDITIONAL_INFO,
                Slots.MORE, "[how to interpret, etc..]",
                Slots.LESS, "[less]",
                Slots.QUESTION_NUMBER, Integer.toString(question.questionNumber),
                Slots.ADDITIONAL_INFO_ID, "contributionInfo",
                Slots.QUESTION_ADDITIONAL_INFO, FormTemplates.CONTRIB_RESULT_STATS_STUDENT_INFO);

        return Templates.populateTemplate(
                FormTemplates.CONTRIB_RESULT_STATS_STUDENT,
                Slots.CONTRIB_ADDITIONAL_INFO, contribAdditionalInfo,
                Slots.CONTRIB_MY_VIEW_OF_ME, getPointsAsColorizedHtml(selfClaim),
                Slots.CONTRIB_MY_VIEW_OF_OTHERS,
                        getNormalizedPointsListColorizedDescending(currentUserTeamResults.claimed[currentUserIndex],
                        currentUserIndex),
                Slots.CONTRIB_TEAM_VIEW_OF_ME, getPointsAsColorizedHtml(teamClaim),
                Slots.CONTRIB_TEAM_VIEW_OF_OTHERS,
                getNormalizedPointsListColorizedDescending(
                        currentUserTeamResults.denormalizedAveragePerceived[currentUserIndex], currentUserIndex));
    }

    private String getQuestionResultsStatisticsHtmlQuestionView(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {

        if (responses.isEmpty()) {
            return "";
        }

        List<FeedbackResponseAttributes> actualResponses = getActualResponses(question, bundle);

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

        for (Map.Entry<String, StudentResultSummary> entry : studentResults.entrySet()) {
            StudentResultSummary summary = entry.getValue();
            String email = entry.getKey();
            String name = bundle.roster.getStudentForEmail(email).name;
            String team = bundle.roster.getStudentForEmail(email).team;

            List<String> teamEmails = teamMembersEmail.get(team);
            TeamEvalResult teamResult = teamResults.get(team);
            int studentIndx = teamEmails.indexOf(email);

            String displayName = name;
            String displayTeam = team;

            if (hideRecipient) {
                displayName = FeedbackSessionResultsBundle.getAnonName(type, name);
                displayTeam = displayName + Const.TEAM_OF_EMAIL_OWNER;
            }
            int[] incomingPoints = new int[teamResult.normalizedPeerContributionRatio.length];
            for (int i = 0; i < incomingPoints.length; i++) {
                incomingPoints[i] = teamResult.normalizedPeerContributionRatio[i][studentIndx];
            }
            contribFragments.append(Templates.populateTemplate(
                    FormTemplates.CONTRIB_RESULT_STATS_FRAGMENT,
                    Slots.CONTRIB_STUDENT_TEAM, SanitizationHelper.sanitizeForHtml(displayTeam),
                    Slots.CONTRIB_STUDENT_NAME, SanitizationHelper.sanitizeForHtml(displayName),
                    Slots.CONTRIB_CC, getPointsAsColorizedHtml(summary.claimedToInstructor),
                    Slots.CONTRIB_PC, getPointsAsColorizedHtml(summary.perceivedToInstructor),
                    Slots.CONTRIB_DIFF, getPointsDiffAsHtml(summary),
                    Slots.CONTRIB_RR, getNormalizedPointsListColorizedDescending(incomingPoints, studentIndx),
                    Slots.CONTRIB_PARAM_STUDENT_NAME, Const.ParamsNames.STUDENT_NAME));
        }

        return Templates.populateTemplate(
                FormTemplates.CONTRIB_RESULT_STATS,
                Slots.CONTRIB_FRAGMENTS, contribFragments.toString(),
                Slots.CONTRIB_TOOLTIPS_CLAIMED, SanitizationHelper.sanitizeForHtml(Const.Tooltips.CLAIMED),
                Slots.CONTRIB_TOOLTIPS_PERCEIVED, Const.Tooltips.PERCEIVED,
                Slots.CONTRIB_TOOLTIPS_POINTS_RECEIVED, Const.Tooltips.FEEDBACK_CONTRIBUTION_POINTS_RECEIVED,
                Slots.CONTRIB_TOOLTIPS_DIFF, Const.Tooltips.FEEDBACK_CONTRIBUTION_DIFF);
    }

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {

        if (responses.isEmpty()) {
            return "";
        }

        List<FeedbackResponseAttributes> actualResponses = getActualResponses(question, bundle);

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
                    + SanitizationHelper.sanitizeForCsv(getNormalizedPointsListDescending(incomingPoints, studentIndx))
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
                "In the points given below, an equal share is equal to 100 points. "
                + "e.g. 80 means \"Equal share - 20%\" and 110 means \"Equal share + 10%\"." + System.lineSeparator()
                + "Claimed Contribution (CC) = the contribution claimed by the student." + System.lineSeparator()
                + "Perceived Contribution (PC) = the average value of student's contribution "
                + "as perceived by the team members." + System.lineSeparator()
                + "Team, Name, Email, CC, PC, Ratings Recieved" + System.lineSeparator();
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

        List<FeedbackResponseAttributes> responses = getActualResponses(question, bundle);

        List<String> teamNames = getTeamsWithAtLeastOneResponse(responses, bundle);

        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);

        Map<String, List<FeedbackResponseAttributes>> teamResponses = getTeamResponses(
                responses, bundle, teamNames);

        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArray(
                teamNames, teamMembersEmail, teamResponses);

        Map<String, TeamEvalResult> teamResults = getTeamResults(teamNames, teamSubmissionArray, teamMembersEmail);

        return getStudentResults(teamMembersEmail, teamResults);
    }

    /**
     * Returns A Map with student email as key and TeamEvalResult as value for the specified question.
     */
    Map<String, TeamEvalResult> getTeamEvalResults(FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question) {

        List<FeedbackResponseAttributes> responses = getActualResponses(question, bundle);

        List<String> teamNames = getTeamsWithAtLeastOneResponse(responses, bundle);

        Map<String, List<String>> teamMembersEmail = getTeamMembersEmail(bundle, teamNames);

        Map<String, List<FeedbackResponseAttributes>> teamResponses = getTeamResponses(
                responses, bundle, teamNames);

        Map<String, int[][]> teamSubmissionArray = getTeamSubmissionArray(
                teamNames, teamMembersEmail, teamResponses);

        return getTeamResults(teamNames, teamSubmissionArray, teamMembersEmail);
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
            teamResponses.put(teamName, new ArrayList<FeedbackResponseAttributes>());
        }
        for (FeedbackResponseAttributes response : responses) {
            String team = bundle.emailTeamNameTable.get(response.giver);
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

    private List<FeedbackResponseAttributes> getActualResponses(
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        List<FeedbackResponseAttributes> responses;
        String questionId = question.getId();
        //Get all actual responses for this question.
        responses = new ArrayList<>();
        for (FeedbackResponseAttributes response : bundle.actualResponses) {
            if (response.feedbackQuestionId.equals(questionId)) {
                responses.add(response);
            }
        }
        responses.sort(bundle.compareByGiverRecipientQuestion);
        return responses;
    }

    private static String getNormalizedPointsListColorizedDescending(int[] subs, int index) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < subs.length; i++) {
            if (i == index) {
                continue;
            }
            result.add(getPointsAsColorizedHtml(subs[i]));
        }

        if (result.isEmpty()) {
            return getPointsAsColorizedHtml(Const.POINTS_NOT_SUBMITTED);
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

    /**
     * Method to color the points by adding <code>span</code> tag with appropriate
     * class (posDiff and negDiff).
     * Positive points will be green, negative will be red, 0 will be black.
     * This will also put N/A or Not Sure for respective points representation.
     * The output will be E+x% for positive points, E-x% for negative points,
     * and just E for equal share.
     * Zero contribution will be printed as 0%
     * @param points
     *         In terms of full percentage, so equal share will be 100, 20% more
     *         from equal share will be 120, etc.
     */
    private static String getPointsAsColorizedHtml(int points) {
        if (points == Const.POINTS_NOT_SUBMITTED || points == Const.INT_UNINITIALIZED) {
            return "<span class=\"color-neutral\" data-toggle=\"tooltip\" data-placement=\"top\" title=\""
                   + Const.Tooltips.FEEDBACK_CONTRIBUTION_NOT_AVAILABLE + "\">N/A</span>";
        } else if (points == Const.POINTS_NOT_SURE) {
            return "<span class=\"color-negative\" data-toggle=\"tooltip\" data-placement=\"top\" title=\""
                   + Const.Tooltips.FEEDBACK_CONTRIBUTION_NOT_SURE + "\">N/S</span>";
        } else if (points == 0) {
            return "<span class=\"color-negative\">0%</span>";
        } else if (points > 100) {
            return "<span class=\"color-positive\">E +" + (points - 100) + "%</span>";
        } else if (points < 100) {
            return "<span class=\"color-negative\">E -" + (100 - points) + "%</span>";
        } else {
            return "<span class=\"color-neutral\">E</span>";
        }
    }

    private static String getPointsDiffAsHtml(StudentResultSummary summary) {
        int claimed = summary.claimedToInstructor;
        int perceived = summary.perceivedToInstructor;
        int diff = perceived - claimed;
        if (perceived == Const.POINTS_NOT_SUBMITTED || perceived == Const.INT_UNINITIALIZED
                || claimed == Const.POINTS_NOT_SUBMITTED || claimed == Const.INT_UNINITIALIZED) {
            return "<span class=\"color-neutral\" data-toggle=\"tooltip\" data-placement=\"top\" "
                   + "data-container=\"body\" title=\"" + Const.Tooltips.FEEDBACK_CONTRIBUTION_NOT_AVAILABLE
                   + "\">N/A</span>";
        } else if (perceived == Const.POINTS_NOT_SURE || claimed == Const.POINTS_NOT_SURE) {
            return "<span class=\"color-negative\" data-toggle=\"tooltip\" data-placement=\"top\" "
                   + "data-container=\"body\" title=\"" + Const.Tooltips.FEEDBACK_CONTRIBUTION_NOT_SURE + "\">N/S"
                   + "</span>";
        } else if (diff > 0) {
            return "<span class=\"color-positive\"> + " + diff + "%</span>";
        } else if (diff < 0) {
            return "<span class=\"color-negative\">" + diff + "%</span>";
        } else {
            return "<span>" + diff + "</span>";
        }
    }

    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<li data-questiontype = \"CONTRIB\"><a href=\"javascript:;\">"
               + Const.FeedbackQuestionTypeNames.CONTRIB + "</a></li>";
    }

    @Override
    public List<String> validateQuestionDetails() {
        return new ArrayList<>();
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        List<String> errors = new ArrayList<>();
        for (FeedbackResponseAttributes response : responses) {
            boolean validAnswer = false;
            FeedbackContributionResponseDetails frd = (FeedbackContributionResponseDetails) response.getResponseDetails();

            // Valid answers: 0, 10, 20, .... 190, 200
            boolean isValidRange = frd.getAnswer() >= 0 && frd.getAnswer() <= 200;
            boolean isMultipleOf10 = frd.getAnswer() % 10 == 0;
            if (isValidRange && isMultipleOf10) {
                validAnswer = true;
            }
            if (frd.getAnswer() == Const.POINTS_NOT_SURE || frd.getAnswer() == Const.POINTS_NOT_SUBMITTED) {
                validAnswer = true;
            }
            if (!validAnswer) {
                errors.add(Const.FeedbackQuestion.CONTRIB_ERROR_INVALID_OPTION);
            }
        }
        return errors;
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

    static String getPerceivedContributionInEqualShareFormatHtml(int i) {
        return "<span>&nbsp;&nbsp;["
                + "Perceived Contribution: "
                + convertToEqualShareFormatHtml(i)
                + "]</span>";
    }

    private String getPerceivedContributionHtml(FeedbackQuestionAttributes question,
            String targetEmail, FeedbackSessionResultsBundle bundle) {

        if (hasPerceivedContribution(targetEmail, question, bundle)) {
            Map<String, StudentResultSummary> stats =
                    FeedbackContributionResponseDetails.getContribQnStudentResultSummary(question, bundle);
            StudentResultSummary studentResult = stats.get(targetEmail);
            int pc = studentResult.perceivedToInstructor;

            return FeedbackContributionQuestionDetails.getPerceivedContributionInEqualShareFormatHtml(pc);
        }
        return "";
    }

    private boolean hasPerceivedContribution(String email, FeedbackQuestionAttributes question,
                                             FeedbackSessionResultsBundle bundle) {
        Map<String, StudentResultSummary> stats =
                FeedbackContributionResponseDetails.getContribQnStudentResultSummary(question, bundle);
        return stats.containsKey(email);
    }

    /**
     * Used to display missing responses between a possible giver and a possible recipient.
     * Returns "No Response" with the Perceived Contribution if the giver is the recipient.
     * Otherwise, returns "No Response".
     */
    @Override
    public String getNoResponseTextInHtml(String giverEmail, String recipientEmail,
                                          FeedbackSessionResultsBundle bundle,
                                          FeedbackQuestionAttributes question) {
        boolean isPerceivedContributionShown = giverEmail.equals(recipientEmail)
                                               && hasPerceivedContribution(recipientEmail, question, bundle);

        // in the row for the student's self response,
        // show the perceived contribution if the student has one
        return "<i>" + Const.INSTRUCTOR_FEEDBACK_RESULTS_MISSING_RESPONSE + "</i>"
               + (isPerceivedContributionShown ? getPerceivedContributionHtml(question, recipientEmail, bundle)
                                               : "");
    }

    /*
     * The functions below are taken and modified from EvalSubmissionEditPageData.java
     * -------------------------------------------------------------------------------
     */

    /**
     * Returns the options for contribution share in a team.
     */
    private String getContributionOptionsHtml(int pointsParam) {
        int points = pointsParam;
        if (points == Const.INT_UNINITIALIZED) {
            points = Const.POINTS_NOT_SUBMITTED;
        }

        StringBuilder result = new StringBuilder(200);
        result.append("<option class=\""
                     + getContributionOptionsColor(Const.POINTS_NOT_SUBMITTED)
                     + "\" value=\"" + Const.POINTS_NOT_SUBMITTED + "\""
                     + (points == Const.POINTS_NOT_SUBMITTED ? " selected" : "") + ">"
                     + convertToEqualShareFormat(Const.POINTS_NOT_SUBMITTED) + "</option>");
        for (int i = 200; i >= 0; i -= 10) {
            result.append("<option class=\""
                        + getContributionOptionsColor(i)
                        + "\" value=\"" + i + "\""
                        + (i == points ? "selected" : "")
                        + ">" + convertToEqualShareFormat(i)
                        + "</option>\r\n");
        }
        if (isNotSureAllowed) {
            result.append("<option class=\""
                          + getContributionOptionsColor(Const.POINTS_NOT_SURE)
                          + "\" value=\"" + Const.POINTS_NOT_SURE + "\""
                          + (points == Const.POINTS_NOT_SURE ? " selected" : "")
                          + ">Not Sure</option>");
        }
        return result.toString();
    }

    /**
     * Returns the CSS color of different point.
     */
    private String getContributionOptionsColor(int points) {
        if (points == Const.POINTS_NOT_SURE
                || points == Const.POINTS_EQUAL_SHARE
                || points == Const.POINTS_NOT_SUBMITTED) {
            // Not sure, Equal Share, Not Submitted
            return "color-neutral";
        } else if (points < Const.POINTS_EQUAL_SHARE) {
            // Negative share
            return "color-negative";
        } else {
            // Positive share
            return "color-positive";
        }
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

    /**
     * Converts points in integer to String for HTML display.
     * @return points in text form "Equal Share..." with html formatting for colors.
     */
    static String convertToEqualShareFormatHtml(int i) {
        if (i == Const.INT_UNINITIALIZED) {
            return "<span class=\"color-neutral\">N/A</span>";
        } else if (i == Const.POINTS_NOT_SUBMITTED) {
            return "<span class=\"color-neutral\"></span>";
        } else if (i == Const.POINTS_NOT_SURE) {
            return "<span class=\"color-negative\">Not Sure</span>";
        } else if (i == 0) {
            return "<span class=\"color-negative\">0%</span>";
        } else if (i > 100) {
            return "<span class=\"color-positive\">Equal Share +" + (i - 100) + "%</span>";
        } else if (i < 100) {
            return "<span class=\"color-negative\">Equal Share -" + (100 - i) + "%</span>";
        } else {
            return "<span class=\"color-neutral\">Equal Share</span>";
        }
    }

    @Override
    public boolean isQuestionSkipped(String[] answer) {
        if (answer == null) {
            return true;
        }
        for (String ans : answer) {
            if (!ans.trim().isEmpty() && Integer.parseInt(ans) != Const.POINTS_NOT_SUBMITTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Comparator<InstructorFeedbackResultsResponseRow> getResponseRowsSortOrder() {
        return null;
    }

    private String getEqualShareHelpLinkIfNeeded(int responseIdx) {
        return responseIdx == 0
                ? "<span class=\"glyphicon glyphicon-info-sign\"></span>"
                      + " More info about the <code>Equal Share</code> scale"
                : "";
    }

    @Override
    public boolean isCommentsOnResponsesAllowed() {
        return false;
    }
}
