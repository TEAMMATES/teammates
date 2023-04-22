import { Directive } from '@angular/core';
import {
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
  FeedbackParticipantType,
} from '../../../../../types/api-output';
import { MSQ_ANSWER_NONE_OF_THE_ABOVE } from '../../../../../types/feedback-response-details';
import { QuestionStatistics } from '../question-statistics';
import { McqMsqQuestionStatisticsCalculation } from './mcq-msq-question-statistics-calculation';

/**
 * Class to calculate stats for msq question.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export class MsqQuestionStatisticsCalculation
    extends QuestionStatistics<FeedbackMsqQuestionDetails, FeedbackMsqResponseDetails>
    implements McqMsqQuestionStatisticsCalculation {

  answerFrequency: Record<string, number> = {};
  percentagePerOption: Record<string, number> = {};
  weightPerOption: Record<string, number> = {};
  weightedPercentagePerOption: Record<string, number> = {};
  perRecipientResponses: Record<string, any> = {};
  hasAnswers: boolean = false;

  calculateStatistics(): void {
    this.answerFrequency = {};
    this.percentagePerOption = {};
    this.weightPerOption = {};
    this.weightedPercentagePerOption = {};
    this.perRecipientResponses = {};

    for (const answer of this.question.msqChoices) {
      this.answerFrequency[answer] = 0;
    }
    if (this.question.otherEnabled) {
      this.answerFrequency['Other'] = 0;
    }
    for (const response of this.responses) {
      this.updateResponseCountPerOptionForResponse(response.responseDetails, this.answerFrequency);
    }
    const numOfAnswers: number =
        Object.values(this.answerFrequency).reduce((prev: number, curr: number) => prev + curr, 0);
    this.hasAnswers = numOfAnswers !== 0;
    if (!this.hasAnswers) {
      return;
    }

    if (this.question.hasAssignedWeights) {
      for (let i: number = 0; i < this.question.msqChoices.length; i += 1) {
        const option: string = this.question.msqChoices[i];
        const weight: number = this.question.msqWeights[i];
        this.weightPerOption[option] = weight;
      }
      if (this.question.otherEnabled) {
        this.weightPerOption['Other'] = this.question.msqOtherWeight;
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
      const percentage: number = numOfAnswers ? 100 * this.answerFrequency[answer] / numOfAnswers : 0;
      this.percentagePerOption[answer] = +percentage.toFixed(2);
    }

    // per recipient stats is only available when weights are enabled
    if (!this.question.hasAssignedWeights) {
      return;
    }

    const perRecipientResponse: Record<string, Record<string, number>> = {};
    const recipientToTeam: Record<string, string> = {};
    const recipientEmails: Record<string, string> = {};
    for (const response of this.responses) {
      perRecipientResponse[response.recipient] = perRecipientResponse[response.recipient] || {};
      recipientEmails[response.recipient] = recipientEmails[response.recipient] || response.recipientEmail || '';
      for (const choice of this.question.msqChoices) {
        perRecipientResponse[response.recipient][choice] = 0;
      }
      if (this.question.otherEnabled) {
        perRecipientResponse[response.recipient]['Other'] = 0;
      }
      recipientToTeam[response.recipient] = response.recipientTeam;
    }
    for (const response of this.responses) {
      this.updateResponseCountPerOptionForResponse(response.responseDetails, perRecipientResponse[response.recipient]);
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

  /**
   * Updates the number of responses per option for each response in responseCountPerOption map.
   */
  private updateResponseCountPerOptionForResponse(responseDetails: FeedbackMsqResponseDetails,
                                                  responseCountPerOption: Record<string, number>): void {
    if (responseDetails.isOther) {
      responseCountPerOption['Other'] = (responseCountPerOption['Other'] || 0) + 1;
    }

    for (const answer of responseDetails.answers) {
      if (answer === MSQ_ANSWER_NONE_OF_THE_ABOVE) {
        // ignore 'None of the above' answer
        continue;
      }
      if (this.question.msqChoices.indexOf(answer) === -1
          && this.question.generateOptionsFor === FeedbackParticipantType.NONE) {
        // ignore other answer if any
        continue;
      }
      responseCountPerOption[answer] = (responseCountPerOption[answer] || 0) + 1;
    }
  }

}
