import { Directive } from '@angular/core';
import {
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
} from '../../../../../types/api-output';
import { QuestionStatistics } from '../question-statistics';

/**
 * Class to calculate stats for constsum options question.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export class ConstsumOptionsQuestionStatisticsCalculation
    extends QuestionStatistics<FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails> {

  pointsPerOption: Record<string, number[]> = {};
  totalPointsPerOption: Record<string, number> = {};
  averagePointsPerOption: Record<string, number> = {};

  // eslint-disable-next-line @typescript-eslint/no-useless-constructor
  constructor(question: FeedbackConstantSumQuestionDetails) {
    super(question);
  }

  calculateStatistics(): void {
    this.pointsPerOption = {};
    this.totalPointsPerOption = {};
    this.averagePointsPerOption = {};

    const options: string[] = this.question.constSumOptions;
    for (const option of options) {
      this.pointsPerOption[option] = [];
    }
    for (const response of this.responses) {
      const answers: number[] = response.responseDetails.answers;
      for (let i: number = 0; i < options.length; i += 1) {
        const option: string = options[i];
        const answer: number = answers[i];
        this.pointsPerOption[option].push(answer);
      }
    }
    for (const option of Object.keys(this.pointsPerOption)) {
      this.pointsPerOption[option].sort((a: number, b: number) => a - b);
      const answers: number[] = this.pointsPerOption[option];
      const sum: number = answers.reduce((a: number, b: number) => a + b, 0);
      this.totalPointsPerOption[option] = sum;
      this.averagePointsPerOption[option] = +(answers.length === 0 ? 0 : sum / answers.length).toFixed(2);
    }
  }

}
