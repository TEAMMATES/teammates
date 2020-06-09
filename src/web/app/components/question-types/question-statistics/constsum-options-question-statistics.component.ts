import { Component, OnChanges, OnInit } from '@angular/core';
import { FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails } from '../../../../types/api-output';
import { DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for constsum options questions.
 */
@Component({
  selector: 'tm-constsum-options-question-statistics',
  templateUrl: './constsum-options-question-statistics.component.html',
  styleUrls: ['./constsum-options-question-statistics.component.scss'],
})
export class ConstsumOptionsQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails>
    implements OnInit, OnChanges {

  pointsPerOption: Record<string, number[]> = {};
  totalPointsPerOption: Record<string, number> = {};
  averagePointsPerOption: Record<string, number> = {};

  constructor() {
    super(DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS());
  }

  ngOnInit(): void {
    this.calculateStatistics();
  }

  ngOnChanges(): void {
    this.calculateStatistics();
  }

  private calculateStatistics(): void {
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
      const answers: number[] = this.pointsPerOption[option];
      const sum: number = answers.reduce((a: number, b: number) => a + b, 0);
      this.totalPointsPerOption[option] = sum;
      this.averagePointsPerOption[option] = +(answers.length === 0 ? 0 : sum / answers.length).toFixed(2);
    }
  }

}
