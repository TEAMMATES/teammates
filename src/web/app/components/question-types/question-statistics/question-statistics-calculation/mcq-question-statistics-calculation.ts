import { Directive } from '@angular/core';
import { FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails } from '../../../../../types/api-output';
import { QuestionStatistics } from '../question-statistics';
import { McqMsqQuestionStatisticsCalculation } from './mcq-msq-question-statistics-calculation';

/**
 * Class to calculate stats for mcq question.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export class McqQuestionStatisticsCalculation
    extends QuestionStatistics<FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails>
    implements McqMsqQuestionStatisticsCalculation {

  answerFrequency: Record<string, number> = {};
  percentagePerOption: Record<string, number> = {};
  weightPerOption: Record<string, number> = {};
  weightedPercentagePerOption: Record<string, number> = {};
  perRecipientResponses: Record<string, any> = {};

  calculateStatistics(): void {
    this.answerFrequency = {};
    this.percentagePerOption = {};
    this.weightPerOption = {};
    this.weightedPercentagePerOption = {};
    this.perRecipientResponses = {};

    for (const answer of this.question.mcqChoices) {
      this.answerFrequency[answer] = 0;
    }
    if (this.question.otherEnabled) {
      this.answerFrequency['Other'] = 0;
    }
    for (const response of this.responses) {
      const isOther: boolean = response.responseDetails.isOther;
      const key: string = isOther ? 'Other' : response.responseDetails.answer;
      this.answerFrequency[key] = (this.answerFrequency[key] || 0) + 1;
    }

    if (this.question.hasAssignedWeights) {
      for (let i: number = 0; i < this.question.mcqChoices.length; i += 1) {
        const option: string = this.question.mcqChoices[i];
        const weight: number = this.question.mcqWeights[i];
        this.weightPerOption[option] = weight;
      }
      if (this.question.otherEnabled) {
        this.weightPerOption['Other'] = this.question.mcqOtherWeight;
      }

      let totalWeightedResponseCount: number = 0;
      for (const answer of Object.keys(this.answerFrequency)) {
        const weight: number = this.weightPerOption[answer];
        const weightedAnswer: number = weight * this.answerFrequency[answer];
        totalWeightedResponseCount += weightedAnswer;
      }

      for (const answer of Object.keys(this.weightPerOption)) {
        const weight: number = this.weightPerOption[answer];
        const frequency: number = this.answerFrequency[answer];
        const weightedPercentage: number = totalWeightedResponseCount === 0 ? 0
            : 100 * ((frequency * weight) / totalWeightedResponseCount);
        this.weightedPercentagePerOption[answer] = +weightedPercentage.toFixed(2);
      }
    }

    for (const answer of Object.keys(this.answerFrequency)) {
      const percentage: number = 100 * this.answerFrequency[answer] / this.responses.length;
      this.percentagePerOption[answer] = +percentage.toFixed(2);
    }

    if (this.question.hasAssignedWeights) {
      const perRecipientResponse: Record<string, Record<string, number>> = {};
      const recipientEmails: Record<string, string> = {};
      const recipientToTeam: Record<string, string> = {};
      for (const response of this.responses) {
        perRecipientResponse[response.recipient] = perRecipientResponse[response.recipient] || {};
        recipientEmails[response.recipient] = recipientEmails[response.recipient] || response.recipientEmail || '';
        for (const choice of this.question.mcqChoices) {
          perRecipientResponse[response.recipient][choice] = 0;
        }
        if (this.question.otherEnabled) {
          perRecipientResponse[response.recipient]['Other'] = 0;
        }
        recipientToTeam[response.recipient] = response.recipientTeam;
      }
      for (const response of this.responses) {
        const isOther: boolean = response.responseDetails.isOther;
        const answer: string = isOther ? 'Other' : response.responseDetails.answer;
        perRecipientResponse[response.recipient][answer] += 1;
      }

      for (const recipient of Object.keys(perRecipientResponse)) {
        const responses: Record<string, number> = perRecipientResponse[recipient];
        let total: number = 0;
        let average: number = 0;
        let numOfResponsesForRecipient: number = 0;
        for (const answer of Object.keys(responses)) {
          const responseCount: number = responses[answer];
          const weight: number = this.weightPerOption[answer];
          total += responseCount * weight;
          numOfResponsesForRecipient += responseCount;
        }
        average = numOfResponsesForRecipient ? total / numOfResponsesForRecipient : 0;

        this.perRecipientResponses[recipient] = {
          recipient,
          recipientEmail: recipientEmails[recipient],
          total: +total.toFixed(5),
          average: +average.toFixed(2),
          recipientTeam: recipientToTeam[recipient],
          responses: perRecipientResponse[recipient],
        };
      }
    }
  }
}
