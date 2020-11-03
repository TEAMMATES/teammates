import { Directive } from '@angular/core';
import {
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
} from '../../../../../types/api-output';
import { RUBRIC_ANSWER_NOT_CHOSEN } from '../../../../../types/feedback-response-details';
import { QuestionStatistics } from '../question-statistics';

/**
 * Type for per recipient statistics.
 */
export interface PerRecipientStats {
  recipientName: string;
  recipientEmail?: string;
  recipientTeam: string;
  answers: number[][];
  percentages: number[][];
  subQuestionTotalChosenWeight: number[];
  subQuestionWeightAverage: number[];
}

/**
 * Class to calculate stats for rubric question.
 */
@Directive()
// tslint:disable-next-line:directive-class-suffix
export class RubricQuestionStatisticsCalculation
    extends QuestionStatistics<FeedbackRubricQuestionDetails, FeedbackRubricResponseDetails> {

  subQuestions: string[] = [];
  choices: string[] = [];
  hasWeights: boolean = false;
  weights: number[][] = [];
  answers: number[][] = [];

  percentages: number[][] = [];
  subQuestionWeightAverage: number[] = [];
  answersExcludeSelf: number[][] = [];
  percentagesExcludeSelf: number[][] = [];
  subQuestionWeightAverageExcludeSelf: number[] = [];

  perRecipientStatsMap: Record<string, PerRecipientStats> = {};

  constructor(question: FeedbackRubricQuestionDetails) {
    super(question);
  }

  calculateStatistics(): void {
    this.answers = [];
    this.percentages = [];
    this.answersExcludeSelf = [];
    this.percentagesExcludeSelf = [];
    this.subQuestionWeightAverage = [];
    this.subQuestionWeightAverageExcludeSelf = [];
    this.perRecipientStatsMap = {};

    this.subQuestions = this.question.rubricSubQuestions;
    this.choices = this.question.rubricChoices;
    this.hasWeights = this.question.hasAssignedWeights;
    this.weights = this.question.rubricWeightsForEachCell;

    const emptyAnswers: number[][] = [];
    for (const _ of this.question.rubricSubQuestions) {
      const subQuestionAnswers: number[] = [];
      for (const __ of this.question.rubricChoices) {
        subQuestionAnswers.push(0);
      }
      emptyAnswers.push(subQuestionAnswers);
    }
    this.answers = JSON.parse(JSON.stringify(emptyAnswers));
    this.answersExcludeSelf = JSON.parse(JSON.stringify(emptyAnswers));

    for (const response of this.responses) {
      for (let i: number = 0; i < response.responseDetails.answer.length; i += 1) {
        const subAnswer: number = response.responseDetails.answer[i];
        if (subAnswer === RUBRIC_ANSWER_NOT_CHOSEN || (this.isStudent && response.recipient !== 'You')) {
          continue;
        }
        this.answers[i][subAnswer] += 1;

        if (response.recipient !== response.giver) {
          this.answersExcludeSelf[i][subAnswer] += 1;
        }
      }
    }

    this.percentages = this.calculatePercentages(this.answers);
    this.percentagesExcludeSelf = this.calculatePercentages(this.answersExcludeSelf);
    // apply weights average if applicable
    if (this.hasWeights) {
      this.subQuestionWeightAverage = this.calculateSubQuestionWeightAverage(this.answers);
      this.subQuestionWeightAverageExcludeSelf = this.calculateSubQuestionWeightAverage(this.answersExcludeSelf);
    }

    if (!this.hasWeights) {
      return;
    }

    // calculate per recipient stats
    for (const response of this.responses) {
      this.perRecipientStatsMap[response.recipient] = this.perRecipientStatsMap[response.recipient] || {
        recipientName: response.recipient,
        recipientEmail: response.recipientEmail,
        recipientTeam: response.recipientTeam,
        answers: JSON.parse(JSON.stringify(emptyAnswers)),
        percentages: [],
        subQuestionTotalChosenWeight: this.subQuestions.map(() => 0),
        subQuestionWeightAverage: [],
      };
      for (let i: number = 0; i < response.responseDetails.answer.length; i += 1) {
        const subAnswer: number = response.responseDetails.answer[i];
        if (subAnswer === RUBRIC_ANSWER_NOT_CHOSEN) {
          continue;
        }
        this.perRecipientStatsMap[response.recipient].answers[i][subAnswer] += 1;
        this.perRecipientStatsMap[response.recipient].subQuestionTotalChosenWeight[i] +=
            +this.weights[i][subAnswer].toFixed(5);
      }
    }

    for (const recipient of Object.keys(this.perRecipientStatsMap)) {
      const perRecipientStats: PerRecipientStats = this.perRecipientStatsMap[recipient];

      perRecipientStats.percentages = this.calculatePercentages(perRecipientStats.answers);
      perRecipientStats.subQuestionWeightAverage =
          this.calculateSubQuestionWeightAverage(perRecipientStats.answers);
    }
  }

  private calculateSubQuestionWeightAverage(answers: number[][]): number[] {
    const sums: number[] = answers.map((weightedAnswers: number[]) =>
        weightedAnswers.reduce((a: number, b: number) => a + b, 0));

    return answers.map((subQuestionAnswer: number[], subQuestionIdx: number): number => {
      const weightAverage: number = sums[subQuestionIdx] === 0 ? 0
          : subQuestionAnswer.reduce((prevValue: number, currValue: number, currentIndex: number): number =>
              prevValue + currValue * this.weights[subQuestionIdx][currentIndex], 0) / sums[subQuestionIdx];
      return +weightAverage.toFixed(2);
    });
  }

  private calculatePercentages(answers: number[][]): number[][] {
    // Deep-copy the answers
    const percentages: number[][] = JSON.parse(JSON.stringify(answers));

    // Calculate sums for each row
    const sums: number[] = percentages.map((weightedAnswers: number[]) =>
        weightedAnswers.reduce((a: number, b: number) => a + b, 0));

    // Calculate the percentages based on the entry of each cell and the sum of each row
    for (let i: number = 0; i < answers.length; i += 1) {
      for (let j: number = 0; j < answers[i].length; j += 1) {
        percentages[i][j] = sums[i] === 0 ? 0 : +(percentages[i][j] / sums[i] * 100).toFixed(2);
      }
    }

    return percentages;
  }

}
