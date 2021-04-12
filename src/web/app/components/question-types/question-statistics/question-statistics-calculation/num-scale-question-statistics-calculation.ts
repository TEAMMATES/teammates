import { Directive } from '@angular/core';
import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
} from '../../../../../types/api-output';
import { QuestionStatistics } from '../question-statistics';

/**
 * Class to calculate stats for num scale question.
 */
@Directive()
// tslint:disable-next-line:directive-class-suffix
export class NumScaleQuestionStatisticsCalculation
    extends QuestionStatistics<FeedbackNumericalScaleQuestionDetails, FeedbackNumericalScaleResponseDetails> {

  teamToRecipientToScores: Record<string, Record<string, any>> = {};

  constructor(question: FeedbackNumericalScaleQuestionDetails) {
    super(question);
  }

  calculateStatistics(): void {
    this.teamToRecipientToScores = {};

    for (const response of this.responses) {
      const { giver }: { giver: string } = response;
      const { recipient }: { recipient: string } = response;
      const { recipientTeam }: { recipientTeam: string } = response;
      this.teamToRecipientToScores[recipientTeam] = this.teamToRecipientToScores[recipientTeam] || {};
      this.teamToRecipientToScores[recipientTeam][recipient] =
          this.teamToRecipientToScores[recipientTeam][recipient] || { responses: [] };
      this.teamToRecipientToScores[recipientTeam][recipient].responses.push({
        answer: response.responseDetails.answer,
        isSelf: giver === recipient,
      });
    }

    for (const team of Object.keys(this.teamToRecipientToScores)) {
      for (const recipient of Object.keys(this.teamToRecipientToScores[team])) {
        const stats: any = this.teamToRecipientToScores[team][recipient];
        const answersAsArray: number[] = stats.responses.map((resp: any) => resp.answer);
        stats.max = Math.max(...answersAsArray);
        stats.min = Math.min(...answersAsArray);
        const average: number = answersAsArray.reduce((a: number, b: number) => a + b, 0) / answersAsArray.length;
        stats.average = +average.toFixed(2); // Show integers without dp, truncate fractions to 2dp

        const answersExcludingSelfAsArray: number[] = stats.responses.filter((resp: any) => !resp.isSelf)
            .map((resp: any) => resp.answer);
        if (answersExcludingSelfAsArray.length) {
          const averageExcludingSelf: number = answersExcludingSelfAsArray.reduce((a: number, b: number) => a + b, 0)
              / answersExcludingSelfAsArray.length;
          stats.averageExcludingSelf = +averageExcludingSelf.toFixed(2);
        } else {
          stats.averageExcludingSelf = 0;
        }
      }
    }
  }

}
