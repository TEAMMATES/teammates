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
  answersSum: number[];
  percentages: number[][];
  percentagesAverage: number[];
  weightsAverage: number[];
  subQuestionTotalChosenWeight: number[];
  subQuestionWeightAverage: number[];
  overallWeightedSum: number;
  overallWeightAverage: number;
}

/**
 * Class to calculate stats for rubric question.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export class RubricQuestionStatisticsCalculation
    extends QuestionStatistics<FeedbackRubricQuestionDetails, FeedbackRubricResponseDetails> {

  subQuestions: string[] = [];
  choices: string[] = [];
  hasWeights: boolean = false;
  weights: number[][] = [];
  answers: number[][] = [];
  isWeightStatsVisible: boolean = false;

  percentages: number[][] = [];
  subQuestionWeightAverage: number[] = [];
  answersExcludeSelf: number[][] = [];
  percentagesExcludeSelf: number[][] = [];
  subQuestionWeightAverageExcludeSelf: number[] = [];

  perRecipientStatsMap: Record<string, PerRecipientStats> = {};

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
    this.isWeightStatsVisible =
      this.hasWeights && this.weights.length > 0 && this.weights[0].length > 0;

    const emptyAnswers: number[][] = [];
    for (let i = 0; i < this.question.rubricSubQuestions.length; i += 1) {
      const subQuestionAnswers: number[] = [];
      for (let j = 0; j < this.question.rubricChoices.length; j += 1) {
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

    // only apply weights average if applicable
    if (!this.isWeightStatsVisible) {
      return;
    }

    this.subQuestionWeightAverage = this.calculateSubQuestionWeightAverage(this.answers);
    this.subQuestionWeightAverageExcludeSelf = this.calculateSubQuestionWeightAverage(this.answersExcludeSelf);

    // calculate per recipient stats
    for (const response of this.responses) {
      this.perRecipientStatsMap[response.recipientEmail || response.recipient] =
        this.perRecipientStatsMap[
          response.recipientEmail || response.recipient
        ] || {
          recipientName: response.recipient,
          recipientEmail: response.recipientEmail,
          recipientTeam: response.recipientTeam,
          answers: JSON.parse(JSON.stringify(emptyAnswers)),
          answersSum: [],
          percentages: [],
          percentagesAverage: [],
          weightsAverage: [],
          subQuestionTotalChosenWeight: this.subQuestions.map(() => 0),
          subQuestionWeightAverage: [],
        };
      for (let i: number = 0; i < response.responseDetails.answer.length; i += 1) {
        const subAnswer: number = response.responseDetails.answer[i];
        if (subAnswer === RUBRIC_ANSWER_NOT_CHOSEN) {
          continue;
        }
        this.perRecipientStatsMap[response.recipientEmail || response.recipient].answers[i][subAnswer] += 1;
        this.perRecipientStatsMap[response.recipientEmail || response.recipient].subQuestionTotalChosenWeight[i] +=
            +this.weights[i][subAnswer].toFixed(5);
      }
    }

    for (const recipient of Object.keys(this.perRecipientStatsMap)) {
      const perRecipientStats: PerRecipientStats = this.perRecipientStatsMap[recipient];

      // Answers sum = number of answers in each column
      perRecipientStats.answersSum = this.calculateAnswersSum(perRecipientStats.answers);
      perRecipientStats.percentages = this.calculatePercentages(perRecipientStats.answers);
      perRecipientStats.percentagesAverage = this.calculatePercentagesAverage(perRecipientStats.answersSum);
      perRecipientStats.subQuestionWeightAverage =
          this.calculateSubQuestionWeightAverage(perRecipientStats.answers);
      perRecipientStats.weightsAverage = this.calculateWeightsAverage(this.weights);
      // Overall weighted sum = sum of total chosen weight for all sub questions
      perRecipientStats.overallWeightedSum =
        +(perRecipientStats.subQuestionTotalChosenWeight.reduce((a, b) => a + b)).toFixed(2);
      // Overall weighted average = overall weighted sum / total number of responses
      perRecipientStats.overallWeightAverage = +(perRecipientStats.overallWeightedSum
          / this.calculateNumResponses(perRecipientStats.answersSum)).toFixed(2);
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

  // Calculate sum of answers for each column
  private calculateAnswersSum(answers: number[][]): number[] {
    const sums: number[] = [];
    for (let i: number = 0; i < answers[0].length; i += 1) {
      let sum: number = 0;
      for (let j: number = 0; j < answers.length; j += 1) {
        sum += answers[j][i];
      }
      sums[i] = sum;
    }
    return sums;
  }

  // Calculate weight average for each column
  private calculateWeightsAverage(weights: number[][]): number[] {
    // Calculate sum of weights for each column
    const sums: number[] = this.calculateAnswersSum(weights);
    const averages: number[] = [];
    // Divide each weight sum by number of weights
    for (let i: number = 0; i < sums.length; i += 1) {
      averages[i] = +(sums[i] / weights.length).toFixed(2);
    }
    return averages;
  }

  // Calculate percentage average for each column
  private calculatePercentagesAverage(answersSum: number[]): number[] {
    // Calculate total number of responses
    const numResponses = this.calculateNumResponses(answersSum);
    const averages: number[] = [];
    // Divide each column sum by total number of responses, then convert to percentage
    for (let i: number = 0; i < answersSum.length; i += 1) {
      averages[i] = numResponses === 0 ? 0 : +(answersSum[i] * 100 / numResponses).toFixed(2);
    }
    return averages;
  }

  // Calculate total number of responses
  private calculateNumResponses(answersSum: number[]): number {
    return answersSum.reduce((a, b) => a + b);
  }
}
