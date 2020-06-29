package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails.MultipleOptionStatistics;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

public class FeedbackMsqQuestionDetails extends FeedbackQuestionDetails {
    private List<String> msqChoices;
    private boolean otherEnabled;
    private boolean hasAssignedWeights;
    private List<Double> msqWeights;
    private double msqOtherWeight;
    private FeedbackParticipantType generateOptionsFor;
    private int maxSelectableChoices;
    private int minSelectableChoices;
    private transient int numOfGeneratedMsqChoices;

    public FeedbackMsqQuestionDetails() {
        super(FeedbackQuestionType.MSQ);

        this.msqChoices = new ArrayList<>();
        this.otherEnabled = false;
        this.generateOptionsFor = FeedbackParticipantType.NONE;
        this.maxSelectableChoices = Integer.MIN_VALUE;
        this.minSelectableChoices = Integer.MIN_VALUE;
        this.hasAssignedWeights = false;
        this.msqWeights = new ArrayList<>();
        this.msqOtherWeight = 0;
    }

    public List<Double> getMsqWeights() {
        return msqWeights;
    }

    public double getMsqOtherWeight() {
        return msqOtherWeight;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.MSQ;
    }

    @Override
    public List<String> getInstructions() {
        return null;
    }

    public boolean getOtherEnabled() {
        return otherEnabled;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackMsqQuestionDetails newMsqDetails = (FeedbackMsqQuestionDetails) newDetails;

        if (this.msqChoices.size() != newMsqDetails.msqChoices.size()
                || !this.msqChoices.containsAll(newMsqDetails.msqChoices)
                || !newMsqDetails.msqChoices.containsAll(this.msqChoices)) {
            return true;
        }

        if (this.generateOptionsFor != newMsqDetails.generateOptionsFor) {
            return true;
        }

        if (this.maxSelectableChoices == Integer.MIN_VALUE && newMsqDetails.maxSelectableChoices != Integer.MIN_VALUE) {
            // Delete responses if max selectable restriction is newly added
            return true;
        }

        if (this.minSelectableChoices == Integer.MIN_VALUE && newMsqDetails.minSelectableChoices != Integer.MIN_VALUE) {
            // Delete responses if min selectable restriction is newly added
            return true;
        }

        if (this.minSelectableChoices != Integer.MIN_VALUE && newMsqDetails.minSelectableChoices != Integer.MIN_VALUE
                && this.minSelectableChoices < newMsqDetails.minSelectableChoices) {
            // A more strict min selectable choices restriction is placed
            return true;
        }

        if (this.maxSelectableChoices != Integer.MIN_VALUE && newMsqDetails.maxSelectableChoices != Integer.MIN_VALUE
                && this.maxSelectableChoices > newMsqDetails.maxSelectableChoices) {
            // A more strict max selectable choices restriction is placed
            return true;
        }

        return this.otherEnabled != newMsqDetails.otherEnabled;
    }

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        if (responses.isEmpty()) {
            return "";
        }

        MSQStatistics msqStats = new MSQStatistics(this);
        Map<String, Integer> answerFrequency = msqStats.collateAnswerFrequency(responses);
        int numChoicesSelected = getNumberOfResponses(answerFrequency);
        if (numChoicesSelected == -1) {
            return "";
        }
        StringBuilder csv = new StringBuilder();

        csv.append(msqStats.getResponseSummaryStatsCsv(answerFrequency, numChoicesSelected));

        // Create 'Per recipient Stats' for csv if weights are enabled.
        if (hasAssignedWeights) {
            String header = msqStats.getPerRecipientResponseStatsHeaderCsv();
            // Get the response attributes sorted based on Recipient Team name and recipient name.
            List<FeedbackResponseAttributes> sortedResponses = msqStats.getResponseAttributesSorted(responses, bundle);
            String body = msqStats.getPerRecipientResponseStatsBodyCsv(sortedResponses, bundle);
            String perRecipientStatsCsv = header + body;

            // Add per recipient stats to csv string
            csv.append(System.lineSeparator())
                    .append("Per Recipient Statistics").append(System.lineSeparator())
                    .append(perRecipientStatsCsv);
        }
        return csv.toString();
    }

    @Override
    public String getCsvHeader() {
        List<String> sanitizedChoices = SanitizationHelper.sanitizeListForCsv(msqChoices);
        return "Feedbacks:," + StringHelper.toString(sanitizedChoices, ",");
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();
        if (generateOptionsFor == FeedbackParticipantType.NONE) {

            if (msqChoices.size() < Const.FeedbackQuestion.MSQ_MIN_NUM_OF_CHOICES) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_NOT_ENOUGH_CHOICES
                           + Const.FeedbackQuestion.MSQ_MIN_NUM_OF_CHOICES + ".");
            }

            // If there are Empty Msq options entered trigger this error
            boolean isEmptyMsqOptionEntered = msqChoices.stream().anyMatch(msqText -> msqText.trim().equals(""));
            if (isEmptyMsqOptionEntered) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_EMPTY_MSQ_OPTION);
            }

            // If weights are enabled, number of choices and weights should be same.
            // If a user enters an invalid weight for a valid choice,
            // the msqChoices.size() will be greater than msqWeights.size(), in that case
            // trigger this error.
            if (hasAssignedWeights && msqChoices.size() != msqWeights.size()) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are not enabled, but weight list is not empty or otherWeight is not 0
            // In that case, trigger this error.
            if (!hasAssignedWeights && (!msqWeights.isEmpty() || msqOtherWeight != 0)) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
            }

            // If weight is enabled, but other option is disabled, and msqOtherWeight is not 0
            // In that case, trigger this error.
            if (hasAssignedWeights && !otherEnabled && msqOtherWeight != 0) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
            }

            // If weights are negative, trigger this error.
            if (hasAssignedWeights && !msqWeights.isEmpty()) {
                msqWeights.stream()
                        .filter(weight -> weight < 0)
                        .forEach(weight -> errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT));
            }

            // If 'Other' option is enabled, and other weight has negative value,
            // trigger this error.
            if (hasAssignedWeights && otherEnabled && msqOtherWeight < 0) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT);
            }

            //If there are duplicate mcq options trigger this error
            boolean isDuplicateOptionsEntered = msqChoices.stream().map(String::trim).distinct().count()
                                                != msqChoices.size();
            if (isDuplicateOptionsEntered) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_DUPLICATE_MSQ_OPTION);
            }
        }

        boolean isMaxSelectableChoicesEnabled = maxSelectableChoices != Integer.MIN_VALUE;
        boolean isMinSelectableChoicesEnabled = minSelectableChoices != Integer.MIN_VALUE;

        int numOfMsqChoices = numOfGeneratedMsqChoices;
        if (generateOptionsFor == FeedbackParticipantType.NONE) {
            numOfMsqChoices = msqChoices.size() + (otherEnabled ? 1 : 0);
        }
        if (isMaxSelectableChoicesEnabled) {
            if (numOfMsqChoices < maxSelectableChoices) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_MAX_SELECTABLE_EXCEEDED_TOTAL);
            } else if (maxSelectableChoices < 2) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_MIN_FOR_MAX_SELECTABLE_CHOICES);
            }
        }

        if (isMinSelectableChoicesEnabled) {
            if (minSelectableChoices < 1) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_MIN_FOR_MIN_SELECTABLE_CHOICES);
            }
            if (minSelectableChoices > numOfMsqChoices) {
                errors.add(Const.FeedbackQuestion.MSQ_ERROR_MIN_SELECTABLE_MORE_THAN_NUM_CHOICES);
            }
        }

        if (isMaxSelectableChoicesEnabled && isMinSelectableChoicesEnabled
                && minSelectableChoices > maxSelectableChoices) {
            errors.add(Const.FeedbackQuestion.MSQ_ERROR_MIN_SELECTABLE_EXCEEDED_MAX_SELECTABLE);
        }

        return errors;
    }

    @Override
    public boolean isFeedbackParticipantCommentsOnResponsesAllowed() {
        return false;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

    public List<String> getMsqChoices() {
        return msqChoices;
    }

    public FeedbackParticipantType getGenerateOptionsFor() {
        return generateOptionsFor;
    }

    public void setGenerateOptionsFor(FeedbackParticipantType generateOptionsFor) {
        this.generateOptionsFor = generateOptionsFor;
    }

    public void setMsqChoices(List<String> msqChoices) {
        this.msqChoices = msqChoices;
    }

    public boolean hasAssignedWeights() {
        return hasAssignedWeights;
    }

    /**
     * Returns maximum selectable choices for this MSQ question.
     * @return Integer.MIN_VALUE if not set by instructor.
     */
    public int getMaxSelectableChoices() {
        return maxSelectableChoices;
    }

    /**
     * Returns minimum selectable choices for this MSQ question.
     */
    public int getMinSelectableChoices() {
        return minSelectableChoices;
    }

    public void setOtherEnabled(boolean otherEnabled) {
        this.otherEnabled = otherEnabled;
    }

    public void setHasAssignedWeights(boolean hasAssignedWeights) {
        this.hasAssignedWeights = hasAssignedWeights;
    }

    public void setMsqWeights(List<Double> msqWeights) {
        this.msqWeights = msqWeights;
    }

    public void setMsqOtherWeight(double msqOtherWeight) {
        this.msqOtherWeight = msqOtherWeight;
    }

    public void setMaxSelectableChoices(int maxSelectableChoices) {
        this.maxSelectableChoices = maxSelectableChoices;
    }

    public void setMinSelectableChoices(int minSelectableChoices) {
        this.minSelectableChoices = minSelectableChoices;
    }

    public void setNumOfGeneratedMsqChoices(int numOfGeneratedMsqChoices) {
        this.numOfGeneratedMsqChoices = numOfGeneratedMsqChoices;
    }

    /**
     * Returns number of non-empty responses.<br>
     * <p>
     * <em>Note:</em> Response can be empty when <b>'None of the above'</b> option is selected.
     * We don't count responses that select 'None of the above' option.</p>
     */
    private int getNumberOfResponses(Map<String, Integer> answerFrequency) {
        int numChoicesSelected = answerFrequency.values().stream().mapToInt(Integer::intValue).sum();

        // we will only show stats if there is at least one nonempty response
        if (numChoicesSelected == 0) {
            return -1;
        }

        return numChoicesSelected;
    }

    /**
     * Calculates the Response Statistics for MSQ questions.
     */
    private static class MSQStatistics extends MultipleOptionStatistics {

        MSQStatistics(FeedbackMsqQuestionDetails msqDetails) {
            this.choices = msqDetails.getMsqChoices();
            this.numOfChoices = choices.size();
            this.weights = msqDetails.getMsqWeights();
            this.otherEnabled = msqDetails.getOtherEnabled();
            this.hasAssignedWeights = msqDetails.hasAssignedWeights();
            this.otherWeight = msqDetails.getMsqOtherWeight();
        }

        /**
         * Calculates the answer frequency for each option based on the received responses for a question.
         * <p>
         *   <strong>Note:</strong> Empty answers which denotes the <code>None of the above</code> option are ignored.
         * </p>
         * @param responses The list of response attributes.
         */
        @Override
        protected Map<String, Integer> collateAnswerFrequency(List<FeedbackResponseAttributes> responses) {
            Map<String, Integer> answerFrequency = new LinkedHashMap<>();

            for (String option : choices) {
                answerFrequency.put(option, 0);
            }
            if (otherEnabled) {
                answerFrequency.put("Other", 0);
            }

            for (FeedbackResponseAttributes response : responses) {
                FeedbackMsqResponseDetails responseDetails = (FeedbackMsqResponseDetails) response.getResponseDetails();
                updateResponseCountPerOptionForResponse(responseDetails, answerFrequency);
            }
            return answerFrequency;
        }

        /**
         * Updates the number of responses per option for each response in responseCountPerOption map.
         */
        private void updateResponseCountPerOptionForResponse(FeedbackMsqResponseDetails responseDetails,
                Map<String, Integer> responseCountPerOption) {
            List<String> answerStrings = responseDetails.getAnswerStrings();
            boolean isOtherOptionAnswer = responseDetails.isOtherOptionAnswer();
            String otherAnswer = "";

            if (isOtherOptionAnswer) {
                responseCountPerOption.put("Other", responseCountPerOption.get("Other") + 1);

                // remove other answer temporarily to calculate stats for other options
                otherAnswer = responseDetails.getOtherFieldContent();
                answerStrings.remove(otherAnswer);
            }

            for (String answerString : answerStrings) {
                // Answer string is empty when 'None of the above' option is selected,
                // in that case, don't count that response.
                if (answerString.isEmpty()) {
                    continue;
                }
                responseCountPerOption.put(answerString, responseCountPerOption.getOrDefault(answerString, 0) + 1);
            }

            // restore other answer if any
            if (isOtherOptionAnswer) {
                answerStrings.add(otherAnswer);
            }
        }

        /**
         * Returns a Map containing response counts for each option for every recipient.
         */
        @Override
        protected Map<String, Map<String, Integer>> calculatePerRecipientResponseCount(
                List<FeedbackResponseAttributes> responses) {
            Map<String, Map<String, Integer>> perRecipientResponse = new LinkedHashMap<>();

            responses.forEach(response -> {
                perRecipientResponse.computeIfAbsent(response.recipient, key -> {
                    // construct default value for responseCount
                    Map<String, Integer> responseCountPerOption = new LinkedHashMap<>();
                    for (String choice : choices) {
                        responseCountPerOption.put(choice, 0);
                    }
                    if (otherEnabled) {
                        responseCountPerOption.put("Other", 0);
                    }
                    return responseCountPerOption;
                });
                perRecipientResponse.computeIfPresent(response.recipient, (key, responseCountPerOption) -> {
                    // update responseCount here
                    FeedbackMsqResponseDetails frd = (FeedbackMsqResponseDetails) response.getResponseDetails();
                    updateResponseCountPerOptionForResponse(frd, responseCountPerOption);
                    return responseCountPerOption;
                });
            });
            return perRecipientResponse;
        }

    }
}
