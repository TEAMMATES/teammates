package teammates.ui.webapi.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.Const;

/**
 * API output format for session results, including statistics.
 */
public class SessionResultsData extends ApiOutput {

    private static final String REGEX_ANONYMOUS_PARTICIPANT_HASH = "[0-9]{1,10}";

    private final List<QuestionOutput> questions = new ArrayList<>();

    public SessionResultsData(FeedbackSessionResultsBundle bundle, InstructorAttributes instructor) {
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses =
                bundle.getQuestionResponseMapSortedByRecipient();

        questionsWithResponses.forEach((question, responses) -> {
            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
            QuestionOutput qnOutput = new QuestionOutput(question.getId(), question.questionNumber, questionDetails,
                    questionDetails.getQuestionResultStatisticsJson(responses, question, instructor.email, bundle, false));

            List<ResponseOutput> allResponses = buildResponses(responses, bundle);
            for (ResponseOutput respOutput : allResponses) {
                qnOutput.allResponses.add(respOutput);
            }

            questions.add(qnOutput);
        });
    }

    public SessionResultsData(FeedbackSessionResultsBundle bundle, StudentAttributes student) {
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses =
                bundle.getQuestionResponseMapSortedByRecipient();

        questionsWithResponses.forEach((question, responses) -> {
            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
            QuestionOutput qnOutput = new QuestionOutput(question.getId(), question.questionNumber, questionDetails,
                    questionDetails.getQuestionResultStatisticsJson(responses, question, student.email, bundle, true));

            Map<String, List<ResponseOutput>> otherResponsesMap = new HashMap<>();
            if (questionDetails.isIndividualResponsesShownToStudents()) {
                List<ResponseOutput> allResponses = buildResponses(question, responses, bundle, student);
                for (ResponseOutput respOutput : allResponses) {
                    if ("You".equals(respOutput.giver)) {
                        qnOutput.responsesFromSelf.add(respOutput);
                    } else if ("You".equals(respOutput.recipient)) {
                        qnOutput.responsesToSelf.add(respOutput);
                    } else {
                        String recipientNameWithHash = respOutput.recipient;
                        respOutput.recipient = removeAnonymousHash(respOutput.recipient);
                        otherResponsesMap.computeIfAbsent(recipientNameWithHash, k -> new ArrayList<>()).add(respOutput);
                    }
                }
            }
            qnOutput.otherResponses = new ArrayList<>(otherResponsesMap.values());

            questions.add(qnOutput);
        });
    }

    public List<QuestionOutput> getQuestions() {
        return questions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        SessionResultsData other = (SessionResultsData) obj;
        List<QuestionOutput> thisQuestions = this.getQuestions();
        List<QuestionOutput> otherQuestions = other.getQuestions();
        if (thisQuestions.size() != otherQuestions.size()) {
            return false;
        }
        for (int i = 0; i < thisQuestions.size(); i++) {
            QuestionOutput thisQuestion = thisQuestions.get(i);
            QuestionOutput otherQuestion = otherQuestions.get(i);
            if (!thisQuestion.equals(otherQuestion)) {
                return false;
            }
        }
        return true;
    }

    private static String removeAnonymousHash(String identifier) {
        return identifier.replaceAll(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " (student|instructor|team) "
                + REGEX_ANONYMOUS_PARTICIPANT_HASH, Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " $1");
    }

    private List<ResponseOutput> buildResponses(
            FeedbackQuestionAttributes question, List<FeedbackResponseAttributes> responses,
            FeedbackSessionResultsBundle bundle, StudentAttributes student) {
        Map<String, List<FeedbackResponseAttributes>> responsesMap = new HashMap<>();

        for (FeedbackResponseAttributes response : responses) {
            responsesMap.computeIfAbsent(response.recipient, k -> new ArrayList<>()).add(response);
        }

        List<ResponseOutput> output = new ArrayList<>();

        responsesMap.forEach((recipient, responsesForRecipient) -> {
            boolean isUserRecipient = student.email.equals(recipient);
            boolean isUserTeamRecipient = question.recipientType == FeedbackParticipantType.TEAMS
                    && student.team.equals(recipient);
            String recipientName;
            if (isUserRecipient) {
                recipientName = "You";
            } else if (isUserTeamRecipient) {
                recipientName = String.format("Your Team (%s)", bundle.getNameForEmail(recipient));
            } else {
                recipientName = bundle.getNameForEmail(recipient);
            }

            for (FeedbackResponseAttributes response : responsesForRecipient) {
                String giverName = bundle.getGiverNameForResponse(response);
                String displayedGiverName;

                boolean isUserGiver = student.email.equals(response.giver);
                boolean isUserPartOfGiverTeam = student.team.equals(giverName);
                if (question.giverType == FeedbackParticipantType.TEAMS && isUserPartOfGiverTeam) {
                    displayedGiverName = "Your Team (" + giverName + ")";
                } else if (isUserGiver) {
                    displayedGiverName = "You";
                } else {
                    displayedGiverName = removeAnonymousHash(giverName);
                }

                if (isUserGiver && !isUserRecipient) {
                    // If the giver is the user, show the real name of the recipient
                    // since the giver would know which recipient he/she gave the response to
                    recipientName = bundle.getNameForEmail(response.recipient);
                }

                // TODO fetch feedback response comments

                // Student does not need to know the teams for giver and/or recipient
                output.add(new ResponseOutput(displayedGiverName, null, null, response.giverSection,
                        recipientName, null, response.recipientSection, response.responseDetails));
            }

        });
        return output;
    }

    private List<ResponseOutput> buildResponses(
            List<FeedbackResponseAttributes> responses, FeedbackSessionResultsBundle bundle) {
        Map<String, List<FeedbackResponseAttributes>> responsesMap = new HashMap<>();

        for (FeedbackResponseAttributes response : responses) {
            responsesMap.computeIfAbsent(response.recipient, k -> new ArrayList<>()).add(response);
        }

        List<ResponseOutput> output = new ArrayList<>();

        responsesMap.forEach((recipient, responsesForRecipient) -> {
            String recipientName = removeAnonymousHash(bundle.getNameForEmail(recipient));
            String recipientTeam = bundle.getTeamNameForEmail(recipient);

            for (FeedbackResponseAttributes response : responsesForRecipient) {
                String giverName = removeAnonymousHash(bundle.getGiverNameForResponse(response));
                Map<String, Set<String>> teamNameToMembersEmailTable = bundle.rosterTeamNameMembersTable;
                String relatedGiverEmail = teamNameToMembersEmailTable.containsKey(response.giver)
                        ? teamNameToMembersEmailTable.get(response.giver).iterator().next() : response.giver;

                String giverTeam = bundle.getTeamNameForEmail(response.giver);

                // TODO fetch feedback response comments

                output.add(new ResponseOutput(giverName, giverTeam, relatedGiverEmail, response.giverSection,
                        recipientName, recipientTeam, response.recipientSection, response.responseDetails));
            }

        });
        return output;
    }

    private static class QuestionOutput {

        private final String questionId;
        private final FeedbackQuestionDetails questionDetails;
        private final int questionNumber;
        private final String questionStatistics;

        // For instructor view
        private List<ResponseOutput> allResponses = new ArrayList<>();

        // For student view
        private List<ResponseOutput> responsesToSelf = new ArrayList<>();
        private List<ResponseOutput> responsesFromSelf = new ArrayList<>();
        private List<List<ResponseOutput>> otherResponses = new ArrayList<>();

        QuestionOutput(String questionId, int questionNumber,
                       FeedbackQuestionDetails questionDetails, String questionStatistics) {
            this.questionId = questionId;
            this.questionNumber = questionNumber;
            this.questionDetails = questionDetails;
            this.questionStatistics = questionStatistics;
        }

        public String getQuestionId() {
            return questionId;
        }

        public FeedbackQuestionDetails getQuestionDetails() {
            return questionDetails;
        }

        public int getQuestionNumber() {
            return questionNumber;
        }

        public String getQuestionStatistics() {
            return questionStatistics;
        }

        public List<ResponseOutput> getAllResponses() {
            return allResponses;
        }

        public List<ResponseOutput> getResponsesFromSelf() {
            return responsesFromSelf;
        }

        public List<ResponseOutput> getResponsesToSelf() {
            return responsesToSelf;
        }

        public List<List<ResponseOutput>> getOtherResponses() {
            return otherResponses;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            QuestionOutput other = (QuestionOutput) obj;
            if (!this.getQuestionId().equals(other.getQuestionId())
                    || this.getQuestionNumber() != other.getQuestionNumber()
                    || !this.getQuestionDetails().equals(other.getQuestionDetails())
                    || !this.getQuestionStatistics().equals(other.getQuestionStatistics())) {
                return false;
            }
            List<ResponseOutput> thisResponses;
            List<ResponseOutput> otherResponses;
            thisResponses = this.getAllResponses();
            otherResponses = other.getAllResponses();
            if (thisResponses.size() != otherResponses.size()) {
                return false;
            }
            for (int j = 0; j < thisResponses.size(); j++) {
                if (!thisResponses.get(j).equals(otherResponses.get(j))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    this.questionId,
                    this.questionNumber,
                    this.questionDetails,
                    this.questionStatistics,
                    this.allResponses
            );
        }
    }

    private static class ResponseOutput {

        private final String giver;
        /**
         * Depending on the question giver type, {@code giverIdentifier} may contain the giver's email, any team member's
         * email or "anonymous".
         */
        private final String relatedGiverEmail;
        private final String giverTeam;
        private final String giverSection;
        private String recipient;
        private final String recipientTeam;
        private final String recipientSection;
        private final FeedbackResponseDetails responseDetails;

        ResponseOutput(String giver, String giverTeam, String relatedGiverEmail, String giverSection, String recipient,
                       String recipientTeam, String recipientSection, FeedbackResponseDetails responseDetails) {
            this.giver = giver;
            this.relatedGiverEmail = relatedGiverEmail;
            this.giverTeam = giverTeam;
            this.giverSection = giverSection;
            this.recipient = recipient;
            this.recipientTeam = recipientTeam;
            this.recipientSection = recipientSection;
            this.responseDetails = responseDetails;
        }

        public String getGiver() {
            return giver;
        }

        public String getGiverTeam() {
            return giverTeam;
        }

        public String getGiverSection() {
            return giverSection;
        }

        public String getRecipient() {
            return recipient;
        }

        public String getRecipientTeam() {
            return recipientTeam;
        }

        public String getRecipientSection() {
            return recipientSection;
        }

        public FeedbackResponseDetails getResponseDetails() {
            return responseDetails;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            ResponseOutput other = (ResponseOutput) obj;
            return this.giver.equals(other.giver)
                    && this.giverTeam.equals(other.giverTeam)
                    && this.giverSection.equals(other.giverSection)
                    && this.recipient.equals(other.recipient)
                    && this.recipientTeam.equals(other.recipientTeam)
                    && this.recipientSection.equals(other.recipientSection)
                    && this.responseDetails.getJsonString().equals(other.responseDetails.getJsonString());
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    this.giver,
                    this.giverSection,
                    this.giverTeam,
                    this.recipient,
                    this.recipientSection,
                    this.recipientTeam,
                    this.responseDetails
            );
        }

    }

}
