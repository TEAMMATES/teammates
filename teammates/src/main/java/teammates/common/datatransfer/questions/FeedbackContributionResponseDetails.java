package teammates.common.datatransfer.questions;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.StudentResultSummary;
import teammates.common.datatransfer.TeamEvalResult;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;

public class FeedbackContributionResponseDetails extends FeedbackResponseDetails {

    private static final Logger log = Logger.getLogger();

    /**This is the claimed points from giver to recipient.
    */
    private int answer;

    public FeedbackContributionResponseDetails() {
        super(FeedbackQuestionType.CONTRIB);
        answer = Const.POINTS_NOT_SUBMITTED;
    }

    public FeedbackContributionResponseDetails(int answer) {
        super(FeedbackQuestionType.CONTRIB);
        this.answer = answer;
    }

    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
                                       FeedbackQuestionDetails questionDetails, String[] answer) {
        try {
            int contribAnswer = Integer.parseInt(answer[0]);
            setAnswer(contribAnswer);
        } catch (NumberFormatException e) {
            log.severe("Failed to parse contrib answer to integer - " + answer[0]);
            throw e;
        }
    }

    /**
     * Gets answer in integer form.
     */
    public int getAnswer() {
        return answer;
    }

    @Override
    public String getAnswerString() {
        return Integer.toString(answer);
    }

    // Not used for contribution question, due to calculations required. See corresponding function below.
    @Override
    public String getAnswerHtmlInstructorView(FeedbackQuestionDetails questionDetails) {
        return FeedbackContributionQuestionDetails.convertToEqualShareFormatHtml(getAnswer());
    }

    // Not used for contribution question, due to calculations required. See corresponding function below.
    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        return SanitizationHelper.sanitizeForCsv(FeedbackContributionQuestionDetails.convertToEqualShareFormat(getAnswer()));
    }

    @Override
    public String getAnswerHtml(FeedbackResponseAttributes response, FeedbackQuestionAttributes question,
                                FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        return getContributionQuestionResponseAnswerHtml(response, question, feedbackSessionResultsBundle);
    }

    @Override
    public String getAnswerCsv(FeedbackResponseAttributes response, FeedbackQuestionAttributes question,
                               FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        return getContributionQuestionResponseAnswerCsv(response, question, feedbackSessionResultsBundle);
    }

    private void setAnswer(int answer) {
        this.answer = answer;
    }

    private String getContributionQuestionResponseAnswerHtml(
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        Map<String, TeamEvalResult> teamResults = getContribQnTeamEvalResult(question, feedbackSessionResultsBundle);

        // Need to get actual team name and giver/recipient emails here,
        // only for getting the responseAnswer.
        FeedbackResponseAttributes actualResponse = feedbackSessionResultsBundle.getActualResponse(response);
        String giverTeamName = feedbackSessionResultsBundle.emailTeamNameTable.get(actualResponse.giver);
        TeamEvalResult teamResult = teamResults.get(giverTeamName);

        int giverIndex = teamResult.studentEmails.indexOf(actualResponse.giver);
        int recipientIndex = teamResult.studentEmails.indexOf(actualResponse.recipient);

        if (giverIndex == -1 || recipientIndex == -1) {
            if (giverIndex == -1) {
                log.severe("getContributionQuestionResponseAnswerHtml - giverIndex is -1\n"
                        + "Cannot find giver: " + actualResponse.giver + "\n"
                        + "CourseId: " + feedbackSessionResultsBundle.feedbackSession.getCourseId() + "\n"
                        + "Session Name: " + feedbackSessionResultsBundle.feedbackSession.getFeedbackSessionName() + "\n"
                        + "Response Id: " + actualResponse.getId());
            }
            if (recipientIndex == -1) {
                log.severe("getContributionQuestionResponseAnswerHtml - recipientIndex is -1\n"
                        + "Cannot find recipient: " + actualResponse.recipient + "\n"
                        + "CourseId: " + feedbackSessionResultsBundle.feedbackSession.getCourseId() + "\n"
                        + "Session Name: " + feedbackSessionResultsBundle.feedbackSession.getFeedbackSessionName() + "\n"
                        + "Response Id: " + actualResponse.getId());
            }

            return "";
        }

        Map<String, StudentResultSummary> stats = getContribQnStudentResultSummary(question, feedbackSessionResultsBundle);

        if (response.giver.equals(response.recipient)) {
            StudentResultSummary studentResult = stats.get(response.giver);
            String responseAnswerHtml = FeedbackContributionQuestionDetails.convertToEqualShareFormatHtml(
                                              studentResult.claimedToInstructor);

            //For CONTRIB qns, We want to show PC if giver == recipient.
            int pc = studentResult.perceivedToInstructor;
            return responseAnswerHtml
                 + FeedbackContributionQuestionDetails.getPerceivedContributionInEqualShareFormatHtml(pc);
        }
        return FeedbackContributionQuestionDetails.convertToEqualShareFormatHtml(
                                        teamResult.normalizedPeerContributionRatio[giverIndex][recipientIndex]);
    }

    private String getContributionQuestionResponseAnswerCsv(
            FeedbackResponseAttributes response,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        Map<String, TeamEvalResult> teamResults = getContribQnTeamEvalResult(question, feedbackSessionResultsBundle);
        Map<String, StudentResultSummary> stats = getContribQnStudentResultSummary(question, feedbackSessionResultsBundle);

        // Need to get actual team name and giver/recipient emails here,
        // only for getting the responseAnswer.
        FeedbackResponseAttributes actualResponse = feedbackSessionResultsBundle.getActualResponse(response);
        String giverTeamName = feedbackSessionResultsBundle.emailTeamNameTable.get(actualResponse.giver);
        TeamEvalResult teamResult = teamResults.get(giverTeamName);

        int giverIndex = teamResult.studentEmails.indexOf(actualResponse.giver);
        int recipientIndex = teamResult.studentEmails.indexOf(actualResponse.recipient);

        String responseAnswerCsv = "";

        if (giverIndex == -1 || recipientIndex == -1) {
            if (giverIndex == -1) {
                log.severe("getContributionQuestionResponseAnswerCsv - giverIndex is -1\n"
                        + "Cannot find giver: " + actualResponse.giver + "\n"
                        + "CourseId: " + feedbackSessionResultsBundle.feedbackSession.getCourseId() + "\n"
                        + "Session Name: " + feedbackSessionResultsBundle.feedbackSession.getFeedbackSessionName() + "\n"
                        + "Response Id: " + actualResponse.getId());
            }
            if (recipientIndex == -1) {
                log.severe("getContributionQuestionResponseAnswerCsv - recipientIndex is -1\n"
                        + "Cannot find recipient: " + actualResponse.recipient + "\n"
                        + "CourseId: " + feedbackSessionResultsBundle.feedbackSession.getCourseId() + "\n"
                        + "Session Name: " + feedbackSessionResultsBundle.feedbackSession.getFeedbackSessionName() + "\n"
                        + "Response Id: " + actualResponse.getId());
            }
        } else {
            responseAnswerCsv = SanitizationHelper.sanitizeForCsv(
                    FeedbackContributionQuestionDetails.convertToEqualShareFormat(
                            teamResult.normalizedPeerContributionRatio[giverIndex][recipientIndex]));

            if (response.giver.equals(response.recipient)) {
                StudentResultSummary studentResult = stats.get(response.giver);
                responseAnswerCsv = SanitizationHelper.sanitizeForCsv(
                        FeedbackContributionQuestionDetails.convertToEqualShareFormat(
                                studentResult.claimedToInstructor));
            }
        }
        return responseAnswerCsv;
    }

    // TODO: check if this can be made non-static
    public static Map<String, StudentResultSummary> getContribQnStudentResultSummary(FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle feedbackSessionResultsBundle) {

        return feedbackSessionResultsBundle.contributionQuestionStudentResultSummary.computeIfAbsent(
                question.getId(), key -> {
                    FeedbackContributionQuestionDetails fqcd =
                            (FeedbackContributionQuestionDetails) question.getQuestionDetails();
                    Map<String, StudentResultSummary> contribQnStats =
                            fqcd.getStudentResults(feedbackSessionResultsBundle, question);

                    new HashMap<>(contribQnStats).forEach((contribQnStatsKey, value) -> contribQnStats.putIfAbsent(
                            feedbackSessionResultsBundle.getAnonEmailFromStudentEmail(contribQnStatsKey), value));

                    return contribQnStats;
                }
        );

    }

    public Map<String, TeamEvalResult> getContribQnTeamEvalResult(FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        Map<String, TeamEvalResult> contribQnStats =
                feedbackSessionResultsBundle.contributionQuestionTeamEvalResults.get(question.getId());
        if (contribQnStats == null) {
            FeedbackContributionQuestionDetails fqcd = (FeedbackContributionQuestionDetails) question.getQuestionDetails();
            contribQnStats = fqcd.getTeamEvalResults(feedbackSessionResultsBundle, question);
            feedbackSessionResultsBundle.contributionQuestionTeamEvalResults.put(question.getId(), contribQnStats);
        }

        return contribQnStats;
    }
}
