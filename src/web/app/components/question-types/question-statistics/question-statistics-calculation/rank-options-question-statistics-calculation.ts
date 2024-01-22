import { Directive } from '@angular/core';
import {
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
} from '../../../../../types/api-output';
import { RANK_OPTIONS_ANSWER_NOT_SUBMITTED } from '../../../../../types/feedback-response-details';
import { QuestionStatistics } from '../question-statistics';

/**
 * Class to calculate stats for rank options question.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export class RankOptionsQuestionStatisticsCalculation
    extends QuestionStatistics<FeedbackRankOptionsQuestionDetails, FeedbackRankOptionsResponseDetails> {

  ranksReceivedPerOption: Record<string, number[]> = {};
  rankPerOption: Record<string, number> = {};

  // eslint-disable-next-line @typescript-eslint/no-useless-constructor
  constructor(question: FeedbackRankOptionsQuestionDetails) {
    super(question);
  }

  calculateStatistics(): void {
    this.ranksReceivedPerOption = {};
    this.rankPerOption = {};

    const options: string[] = this.question.options;
    for (const option of options) {
      this.ranksReceivedPerOption[option] = [];
    }
    for (const response of this.responses) {
      const answers: number[] = this.normalizeRanks(response.responseDetails.answers);
      for (let i: number = 0; i < options.length; i += 1) {
        const option: string = options[i];
        const answer: number = answers[i];
        if (answer === RANK_OPTIONS_ANSWER_NOT_SUBMITTED) {
          // skip option not ranked
          continue;
        }
        this.ranksReceivedPerOption[option].push(answer);
      }
    }

    const averageRanksReceivedPerOptions: Record<string, number> = {};
    for (const option of Object.keys(this.ranksReceivedPerOption)) {
      this.ranksReceivedPerOption[option].sort((a: number, b: number) => a - b);
      const answers: number[] = this.ranksReceivedPerOption[option];
      const sum: number = answers.reduce((a: number, b: number) => a + b, 0);
      if (answers.length === 0) {
        // skip options which has no answer collected
        continue;
      }
      averageRanksReceivedPerOptions[option] = sum / answers.length;
    }

    const optionsOrderedByRank: string[] = Object.keys(averageRanksReceivedPerOptions).sort(
        (a: string, b: string) => {
          return averageRanksReceivedPerOptions[a] - averageRanksReceivedPerOptions[b];
        });

    for (let i: number = 0; i < optionsOrderedByRank.length; i += 1) {
      const option: string = optionsOrderedByRank[i];
      if (i === 0) {
        this.rankPerOption[option] = 1;
        continue;
      }
      const rank: number = averageRanksReceivedPerOptions[option];
      const optionBefore: string = optionsOrderedByRank[i - 1];
      const rankBefore: number = averageRanksReceivedPerOptions[optionBefore];
      if (rank === rankBefore) {
        // If the average rank is the same, the overall rank will be the same
        this.rankPerOption[option] = this.rankPerOption[optionBefore];
      } else {
        // Otherwise, the rank is as determined by the order
        this.rankPerOption[option] = i + 1;
      }
    }
  }

  private normalizeRanks(ranks: number[]): number[] {
    const rankMapping: Record<number, number> = {};
    rankMapping[RANK_OPTIONS_ANSWER_NOT_SUBMITTED] = RANK_OPTIONS_ANSWER_NOT_SUBMITTED;

    const rankCopy: number[] = JSON.parse(JSON.stringify(ranks));
    rankCopy.sort((a: number, b: number) => a - b);

    let normalizedRank: number = 1;
    for (const rank of rankCopy) {
      if (!rankMapping[rank]) {
        rankMapping[rank] = normalizedRank;
        normalizedRank += 1;
      }
    }
    return ranks.map((rank: number) => rankMapping[rank]);
  }

}
