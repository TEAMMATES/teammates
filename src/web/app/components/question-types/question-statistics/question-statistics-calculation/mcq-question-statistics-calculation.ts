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

  b: Record<number, boolean> = {};// Object.fromEntries([...new Array(10)].map((_, i) => [i, false]));

  calculateStatistics(): void {
    this.answerFrequency = {};
    this.percentagePerOption = {};
    this.weightPerOption = {};
    this.weightedPercentagePerOption = {};
    this.perRecipientResponses = {};

    for (const answer of this.question.mcqChoices) {
      this.b[1] = true;
      this.answerFrequency[answer] = 0;
    }

    if (this.question.otherEnabled) {
      this.b[2] = true;
      this.answerFrequency['Other'] = 0;
    } else {
      this.b[3] = true;
    }

    for (const response of this.responses) {
      this.b[4] = true;
      const isOther: boolean = response.responseDetails.isOther;

      // const key: string = isOther ? 'Other' : response.responseDetails.answer;
      let key: string;
      if (isOther) {
        this.b[5] = true;
        key = 'Other';
      } else {
        this.b[6] = true;
        key = response.responseDetails.answer
      }

      // this.answerFrequency[key] = (this.answerFrequency[key] || 0) + 1;
      if (this.answerFrequency[key]) {
        this.b[7] = true;
        this.answerFrequency[key]++;
      } else {
        this.b[8] = true;
        this.answerFrequency[key] = 1;
      }
    }

    if (this.question.hasAssignedWeights) {
      this.b[9] = true;
      for (let i: number = 0; i < this.question.mcqChoices.length; i += 1) {
        this.b[10] = true;
        const option: string = this.question.mcqChoices[i];
        const weight: number = this.question.mcqWeights[i];
        this.weightPerOption[option] = weight;
      }
      if (this.question.otherEnabled) {
        this.b[11] = true;
        this.weightPerOption['Other'] = this.question.mcqOtherWeight;
      } else {
        this.b[12] = true;
      }

      let totalWeightedResponseCount: number = 0;
      for (const answer of Object.keys(this.answerFrequency)) {
        this.b[13] = true;
        const weight: number = this.weightPerOption[answer];
        const weightedAnswer: number = weight * this.answerFrequency[answer];
        totalWeightedResponseCount += weightedAnswer;
      }

      for (const answer of Object.keys(this.weightPerOption)) {
        this.b[14] = true;
        const weight: number = this.weightPerOption[answer];
        const frequency: number = this.answerFrequency[answer];

        // const weightedPercentage: number = totalWeightedResponseCount === 0 ? 0 : 100 * ((frequency * weight) / totalWeightedResponseCount);
        let weightedPercentage;
        if (totalWeightedResponseCount === 0) {
          this.b[15] = true;
          weightedPercentage = 0;
        } else {
          this.b[16] = true;
          weightedPercentage = 100 * ((frequency * weight) / totalWeightedResponseCount);
        }

        this.weightedPercentagePerOption[answer] = +weightedPercentage.toFixed(2);
      }
    } else {
      this.b[17] = true;
    }

    for (const answer of Object.keys(this.answerFrequency)) {
      this.b[18] = true;
      const percentage: number = 100 * this.answerFrequency[answer] / this.responses.length;
      this.percentagePerOption[answer] = +percentage.toFixed(2);
    }

    if (this.question.hasAssignedWeights) {
      this.b[19] = true;
      const perRecipientResponse: Record<string, Record<string, number>> = {};
      const recipientToTeam: Record<string, string> = {};

      for (const response of this.responses) {
        this.b[20] = true;

        // perRecipientResponse[response.recipient] = perRecipientResponse[response.recipient] || {};
        if (perRecipientResponse[response.recipient]) {
          this.b[21] = true;
          perRecipientResponse[response.recipient] = perRecipientResponse[response.recipient];
        } else {
          this.b[22] = true;
          perRecipientResponse[response.recipient] = {};
        }

        for (const choice of this.question.mcqChoices) {
          this.b[23] = true;
          perRecipientResponse[response.recipient][choice] = 0;
        }

        if (this.question.otherEnabled) {
          this.b[24] = true;
          perRecipientResponse[response.recipient]['Other'] = 0;
        } else {
          this.b[25] = true;
        }

        recipientToTeam[response.recipient] = response.recipientTeam;
      }
      for (const response of this.responses) {
        this.b[26] = true;
        const isOther: boolean = response.responseDetails.isOther;

        // const answer: string = isOther ? 'Other' : response.responseDetails.answer;
        let answer;
        if (isOther) {
          this.b[27] = true;
          answer = 'Other';
        } else {
          this.b[28] = true;
          answer = response.responseDetails.answer;
        }

        perRecipientResponse[response.recipient][answer] += 1;
      }

      for (const recipient of Object.keys(perRecipientResponse)) {
        this.b[29] = true;
        const responses: Record<string, number> = perRecipientResponse[recipient];
        let total: number = 0;
        let average: number = 0;
        let numOfResponsesForRecipient: number = 0;
        for (const answer of Object.keys(responses)) {
          this.b[30] = true;
          const responseCount: number = responses[answer];
          const weight: number = this.weightPerOption[answer];
          total += responseCount * weight;
          numOfResponsesForRecipient += responseCount;
        }

        // average = numOfResponsesForRecipient ? total / numOfResponsesForRecipient : 0;
        if (numOfResponsesForRecipient) {
          this.b[31] = true;
          average = total / numOfResponsesForRecipient;
        } else {
          this.b[32] = true;
          average = 0
        }

        this.perRecipientResponses[recipient] = {
          recipient,
          total: +total.toFixed(5),
          average: +average.toFixed(2),
          recipientTeam: recipientToTeam[recipient],
          responses: perRecipientResponse[recipient],
        };
      }
    } else {
      this.b[33] = true;
    }
  }
}
