import { Directive } from '@angular/core';
import {
  FeedbackNumericalRangeQuestionDetails,
  FeedbackNumericalRangeResponseDetails,
} from '../../../../../types/api-output';
import { QuestionStatistics } from '../question-statistics';

/**
 * Class to calculate stats for num range question.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export class NumRangeQuestionStatisticsCalculation
    extends QuestionStatistics<FeedbackNumericalRangeQuestionDetails, FeedbackNumericalRangeResponseDetails> {

  teamToRecipientToScores: Record<string, Record<string, any>> = {};

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
        start: response.responseDetails.start,
        end: response.responseDetails.end,
        isSelf: giver === recipient,
      });
    }

    for (const team of Object.keys(this.teamToRecipientToScores)) {
      for (const recipient of Object.keys(this.teamToRecipientToScores[team])) {
        const stats: any = this.teamToRecipientToScores[team][recipient];

        const startsAsArray: number[] = stats.responses.map((resp: any) => resp.start);
        stats.maxStart = Math.max(...startsAsArray);
        stats.minStart = Math.min(...startsAsArray);
        const averageStart: number = startsAsArray.reduce((a: number, b: number) => a + b, 0) / startsAsArray.length;
        stats.averageStart = +averageStart.toFixed(2); // Show integers without dp, truncate fractions to 2dp


        const endsAsArray: number[] = stats.responses.map((resp: any) => resp.end);
        stats.maxEnd = Math.max(...endsAsArray);
        stats.minEnd = Math.min(...endsAsArray);
        const averageEnd: number = endsAsArray.reduce((a: number, b: number) => a + b, 0) / startsAsArray.length;
        stats.averageEnd = +averageEnd.toFixed(2); // Show integers without dp, truncate fractions to 2dp

        const startsExcludingSelfAsArray: number[] = stats.responses.filter((resp: any) => !resp.isSelf)
            .map((resp: any) => resp.start);
        if (startsExcludingSelfAsArray.length) {
          const averageStartExcludingSelf: number = startsExcludingSelfAsArray.reduce((a: number, b: number) => a + b, 0)
              / startsExcludingSelfAsArray.length;
          stats.averageStartExcludingSelf = +averageStartExcludingSelf.toFixed(2);
        } else {
          stats.averageStartExcludingSelf = 0;
        }

        const endsExcludingSelfAsArray: number[] = stats.responses.filter((resp: any) => !resp.isSelf)
        .map((resp: any) => resp.end);
        if (endsExcludingSelfAsArray.length) {
          const averageEndExcludingSelf: number = endsExcludingSelfAsArray.reduce((a: number, b: number) => a + b, 0)
              / endsExcludingSelfAsArray.length;
          stats.averageEndExcludingSelf = +averageEndExcludingSelf.toFixed(2);
        } else {
          stats.averageEndExcludingSelf = 0;
        }
      }
    }
  }

}
