package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.TeamEvalResult;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.visibility.FeedbackVisibilityType;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Student;

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
            FeedbackQuestion question, UUID currentUserId, SessionResultsBundle bundle) {
        List<FeedbackResponse> responses = bundle.getQuestionResponseMap().get(question);
        boolean isStudent = currentUserId != null;

        Map<UUID, List<Student>> teamIdToMembers = getTeamIdToMembers(bundle);
        List<UUID> teamIds = isStudent
                ? getTeamsWithAtLeastOneResponse(responses)
                : new ArrayList<>(teamIdToMembers.keySet());
        Map<UUID, List<FeedbackResponse>> teamResponses = getTeamResponses(responses, teamIds);
        Map<UUID, int[][]> teamSubmissionArray = getTeamSubmissionArray(teamIds, teamIdToMembers, teamResponses);
        Map<UUID, TeamEvalResult> teamResults = getTeamResults(teamIds, teamSubmissionArray);
        ContributionStatistics output = new ContributionStatistics();

        if (isStudent) {
            Student currentStudent = getStudentByUserId(bundle, currentUserId);
            if (currentStudent == null) {
                return JsonUtils.toJson(output);
            }

            UUID currentTeamId = currentStudent.getTeamId();
            TeamEvalResult currentUserTeamResults = teamResults.get(currentTeamId);
            List<Student> teamMembers = teamIdToMembers.get(currentTeamId);
            int currentUserIndex = teamMembers == null ? -1 : indexOfStudent(teamMembers, currentUserId);
            if (currentUserTeamResults == null || currentUserIndex < 0) {
                return JsonUtils.toJson(output);
            }

            int[] claimedNumbers = currentUserTeamResults.claimed[currentUserIndex];
            int[] perceivedNumbers = currentUserTeamResults.denormalizedAveragePerceived[currentUserIndex];

            int claimed = 0;
            int perceived = 0;
            Map<String, Integer> claimedOthers = new LinkedHashMap<>();
            List<Integer> perceivedOthers = new ArrayList<>();

            for (int i = 0; i < claimedNumbers.length; i++) {
                if (i == currentUserIndex) {
                    claimed = claimedNumbers[i];
                } else {
                    claimedOthers.put(teamMembers.get(i).getId().toString(), claimedNumbers[i]);
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

            output.results.put(currentUserId.toString(), new ContributionStatisticsEntry(
                    claimed, perceived, claimedOthers, perceivedOthers.stream().mapToInt(i -> i).toArray()));
        } else {
            Map<UUID, int[]> studentResults = getStudentResults(teamIdToMembers, teamResults);

            for (Map.Entry<UUID, int[]> entry : studentResults.entrySet()) {
                int[] summary = entry.getValue();
                UUID studentId = entry.getKey();
                Student student = getStudentByUserId(bundle, studentId);
                if (student == null) {
                    continue;
                }
                List<Student> teamMembers = teamIdToMembers.get(student.getTeamId());
                TeamEvalResult teamResult = teamResults.get(student.getTeamId());
                int studentIndex = teamMembers == null ? -1 : indexOfStudent(teamMembers, studentId);
                if (teamResult == null || studentIndex < 0) {
                    continue;
                }

                Map<String, Integer> claimedOthers = new LinkedHashMap<>();
                List<Integer> perceivedOthers = new ArrayList<>();
                for (int i = 0; i < teamResult.normalizedPeerContributionRatio.length; i++) {
                    if (i != studentIndex) {
                        claimedOthers.put(teamMembers.get(i).getId().toString(),
                                teamResult.normalizedPeerContributionRatio[studentIndex][i]);
                        perceivedOthers.add(teamResult.normalizedPeerContributionRatio[i][studentIndex]);
                    }
                }
                perceivedOthers.sort(Comparator.reverseOrder());

                output.results.put(studentId.toString(), new ContributionStatisticsEntry(summary[SUMMARY_INDEX_CLAIMED],
                        summary[SUMMARY_INDEX_PERCEIVED],
                        claimedOthers, perceivedOthers.stream().mapToInt(i -> i).toArray()));
            }
        }

        return JsonUtils.toJson(output);
    }

    private Map<UUID, List<Student>> getTeamIdToMembers(SessionResultsBundle bundle) {
        Map<UUID, List<Student>> teamIdToMembers = new LinkedHashMap<>();
        for (Student student : bundle.getRoster().getStudents()) {
            teamIdToMembers.computeIfAbsent(student.getTeamId(), key -> new ArrayList<>()).add(student);
        }
        return teamIdToMembers;
    }

    private Student getStudentByUserId(SessionResultsBundle bundle, UUID studentId) {
        return bundle.getRoster().getStudents().stream()
                .filter(student -> Objects.equals(student.getId(), studentId))
                .findFirst()
                .orElse(null);
    }

    private int indexOfStudent(List<Student> students, UUID studentId) {
        for (int i = 0; i < students.size(); i++) {
            if (Objects.equals(students.get(i).getId(), studentId)) {
                return i;
            }
        }
        return -1;
    }

    private Map<UUID, int[]> getStudentResults(
            Map<UUID, List<Student>> teamIdToMembers,
            Map<UUID, TeamEvalResult> teamResults) {
        Map<UUID, int[]> studentResults = new LinkedHashMap<>();
        teamResults.forEach((teamId, teamResult) -> {
            List<Student> teamMembers = teamIdToMembers.get(teamId);
            for (int i = 0; i < teamMembers.size(); i++) {
                int[] summary = new int[2];
                summary[SUMMARY_INDEX_CLAIMED] = teamResult.normalizedClaimed[i][i];
                summary[SUMMARY_INDEX_PERCEIVED] = teamResult.normalizedAveragePerceived[i];

                studentResults.put(teamMembers.get(i).getId(), summary);
            }
        });
        return studentResults;
    }

    private Map<UUID, TeamEvalResult> getTeamResults(List<UUID> teamIds,
            Map<UUID, int[][]> teamSubmissionArray) {
        Map<UUID, TeamEvalResult> teamResults = new LinkedHashMap<>();
        for (UUID teamId : teamIds) {
            TeamEvalResult teamEvalResult = new TeamEvalResult(teamSubmissionArray.get(teamId));
            teamResults.put(teamId, teamEvalResult);
        }
        return teamResults;
    }

    private Map<UUID, int[][]> getTeamSubmissionArray(List<UUID> teamIds,
                                                      Map<UUID, List<Student>> teamIdToMembers,
                                                      Map<UUID, List<FeedbackResponse>> teamResponses) {
        Map<UUID, int[][]> teamSubmissionArray = new LinkedHashMap<>();
        for (UUID teamId : teamIds) {
            List<Student> teamMembers = teamIdToMembers.getOrDefault(teamId, List.of());
            int teamSize = teamMembers.size();
            teamSubmissionArray.put(teamId, new int[teamSize][teamSize]);
            //Initialize all as not submitted.
            for (int i = 0; i < teamSize; i++) {
                for (int j = 0; j < teamSize; j++) {
                    teamSubmissionArray.get(teamId)[i][j] = Const.POINTS_NOT_SUBMITTED;
                }
            }
            //Fill in submitted points
            List<FeedbackResponse> teamResponseList = teamResponses.getOrDefault(teamId, List.of());
            for (FeedbackResponse response : teamResponseList) {
                UUID giverUserId = response.getGiver().getGiverUserId();
                UUID recipientUserId = response.getRecipient().getRecipientUserId();
                if (giverUserId == null || recipientUserId == null) {
                    continue;
                }
                int giverIndx = indexOfStudent(teamMembers, giverUserId);
                int recipientIndx = indexOfStudent(teamMembers, recipientUserId);
                if (giverIndx == -1 || recipientIndx == -1) {
                    continue;
                }
                int points = ((FeedbackContributionResponseDetails) response.getFeedbackResponseDetailsCopy()).getAnswer();
                teamSubmissionArray.get(teamId)[giverIndx][recipientIndx] = points;
            }
        }
        return teamSubmissionArray;
    }

    private Map<UUID, List<FeedbackResponse>> getTeamResponses(
            List<FeedbackResponse> responses, List<UUID> teamIds) {
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

    private List<UUID> getTeamsWithAtLeastOneResponse(List<FeedbackResponse> responses) {
        Set<UUID> teamIds = new HashSet<>();
        for (FeedbackResponse response : responses) {
            teamIds.add(response.getGiver().getTeamId());
        }
        return new ArrayList<>(teamIds);
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
        if (feedbackQuestion.getGiverType() != QuestionGiverType.STUDENTS) {
            log.severe("Unexpected giverType for contribution question: " + feedbackQuestion.getGiverType()
                       + " (forced to :" + QuestionGiverType.STUDENTS + ")");
            feedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);
            errorMsg = CONTRIB_ERROR_INVALID_FEEDBACK_PATH;
        }

        // recipient type can only be OWN_TEAM_MEMBERS_INCLUDING_SELF
        if (feedbackQuestion.getRecipientType() != QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF) {
            log.severe("Unexpected recipientType for contribution question: "
                       + feedbackQuestion.getRecipientType()
                       + " (forced to :" + QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF + ")");
            feedbackQuestion.setRecipientType(QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
            errorMsg = CONTRIB_ERROR_INVALID_FEEDBACK_PATH;
        }

        // restrictions on visibility options: RECIPIENT and GIVER_TEAM_MEMBERS must appear together
        if (feedbackQuestion.getShowResponsesTo().contains(FeedbackVisibilityType.RECIPIENT)
                != feedbackQuestion.getShowResponsesTo().contains(FeedbackVisibilityType.GIVER_TEAM_MEMBERS)) {
            log.severe("Unexpected showResponsesTo for contribution question: "
                       + feedbackQuestion.getShowResponsesTo() + " (forced to :"
                       + "Shown anonymously to recipient and team members, visible to instructors"
                       + ")");
            feedbackQuestion.setShowResponsesTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT,
                                                               FeedbackVisibilityType.GIVER_TEAM_MEMBERS,
                                                               FeedbackVisibilityType.INSTRUCTORS));
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FeedbackContributionQuestionDetails other)) {
            return false;
        }
        return getQuestionType() == other.getQuestionType()
                && Objects.equals(getQuestionText(), other.getQuestionText())
                && isZeroSum == other.isZeroSum
                && isNotSureAllowed == other.isNotSureAllowed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuestionType(), getQuestionText(), isZeroSum, isNotSureAllowed);
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
