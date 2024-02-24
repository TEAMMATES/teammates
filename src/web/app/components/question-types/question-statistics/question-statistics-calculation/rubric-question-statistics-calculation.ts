import { Directive } from '@angular/core';
import {
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
} from '../../../../../types/api-output';
import { NO_VALUE, RUBRIC_ANSWER_NOT_CHOSEN } from '../../../../../types/feedback-response-details';
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
  areSubQuestionChosenWeightsAllNull: boolean[];
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

  // eslint-disable-next-line @typescript-eslint/no-useless-constructor
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
          areSubQuestionChosenWeightsAllNull: this.subQuestions.map(() => true),
          subQuestionTotalChosenWeight: this.subQuestions.map(() => 0),
          subQuestionWeightAverage: [],
        };
      for (let i: number = 0; i < response.responseDetails.answer.length; i += 1) {
        const subAnswer: number = response.responseDetails.answer[i];
        if (subAnswer === RUBRIC_ANSWER_NOT_CHOSEN) {
          continue;
        }
        this.perRecipientStatsMap[response.recipientEmail || response.recipient].answers[i][subAnswer] += 1;
        if (this.weights[i][subAnswer] !== null) {
            this.perRecipientStatsMap[response.recipientEmail || response.recipient].subQuestionTotalChosenWeight[i] +=
                +this.weights[i][subAnswer].toFixed(5);
            this.perRecipientStatsMap[
                response.recipientEmail || response.recipient].areSubQuestionChosenWeightsAllNull[i] = false;
        }
      }
    }

    for (const recipient of Object.keys(this.perRecipientStatsMap)) {
      const perRecipientStats: PerRecipientStats = this.perRecipientStatsMap[recipient];

      // Answers sum = number of answers in each column
      perRecipientStats.answersSum = this.sumValidValuesByColumn(perRecipientStats.answers);
      perRecipientStats.percentages = this.calculatePercentages(perRecipientStats.answers);
      perRecipientStats.percentagesAverage = this.calculatePercentagesAverage(perRecipientStats.answersSum);
      perRecipientStats.subQuestionTotalChosenWeight =
          perRecipientStats.subQuestionTotalChosenWeight.map((val: number, i: number) =>
              (perRecipientStats.areSubQuestionChosenWeightsAllNull[i] ? NO_VALUE : val));
      perRecipientStats.subQuestionWeightAverage =
          this.calculateSubQuestionWeightAverage(perRecipientStats.answers);
      perRecipientStats.weightsAverage = this.calculateWeightsAverage(this.weights);
      perRecipientStats.overallWeightedSum = this.calculateOverallWeightedSum(
          perRecipientStats.areSubQuestionChosenWeightsAllNull, perRecipientStats.subQuestionTotalChosenWeight);
      // Overall weighted average = overall weighted sum / total number of responses with non-null weights
      perRecipientStats.overallWeightAverage = perRecipientStats.overallWeightedSum === NO_VALUE
          ? NO_VALUE
          : +(perRecipientStats.overallWeightedSum
              / this.calculateNumResponses(this.countResponsesByRowWithValidWeight(perRecipientStats.answers)))
              .toFixed(2);
    }
  }

  // Number of responses for each sub question with non-null weights
  private countResponsesByRowWithValidWeight(answers: number[][]): number[] {
    const sums: number[] = [];
    for (let r: number = 0; r < answers.length; r += 1) {
        let sum: number = 0;
        for (let c: number = 0; c < answers[0].length; c += 1) {
            if (this.weights[r][c] === null) {
                continue;
            }
            sum += answers[r][c];
        }
        sums[r] = sum;
    }
    return sums;
  }

  private calculateSubQuestionWeightAverage(answers: number[][]): number[] {
    const sums: number[] = this.countResponsesByRowWithValidWeight(answers);

    return answers.map((subQuestionAnswer: number[], subQuestionIdx: number): number => {
      if (sums[subQuestionIdx] === 0) {
        return NO_VALUE;
      }
      const weightAverage: number =
          subQuestionAnswer.reduce((prevValue: number, currValue: number, currentIndex: number): number =>
              (this.weights[subQuestionIdx][currentIndex] === null
                  ? prevValue
                  : prevValue + currValue * this.weights[subQuestionIdx][currentIndex]), 0) / sums[subQuestionIdx];
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

  // Calculate sum of non-null values for each column
  private sumValidValuesByColumn(matrix: number[][]): number[] {
    const sums: number[] = [];
    for (let c: number = 0; c < matrix[0].length; c += 1) {
      let sum: number = 0;
      for (let r: number = 0; r < matrix.length; r += 1) {
        sum += matrix[r][c] === null ? 0 : matrix[r][c];
      }
      sums[c] = sum;
    }
    return sums;
  }

  // Count number of non-null values for each column
  private countValidValuesByColumn(matrix: number[][]): number[] {
    const counts: number[] = [];
    for (let c: number = 0; c < matrix[0].length; c += 1) {
      let count: number = 0;
      for (let r: number = 0; r < matrix.length; r += 1) {
        count += matrix[r][c] === null ? 0 : 1;
      }
      counts[c] = count;
    }
    return counts;
  }

  // Calculate non-null weight average for each column
  private calculateWeightsAverage(weights: number[][]): number[] {
    const sums: number[] = this.sumValidValuesByColumn(weights);
    const counts: number[] = this.countValidValuesByColumn(weights);
    const averages: number[] = [];
    // Divide each weight sum by number of non-null weights
    for (let i: number = 0; i < sums.length; i += 1) {
      averages[i] = counts[i] ? +(sums[i] / counts[i]).toFixed(2) : NO_VALUE;
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

  // Overall weighted sum is sum of total chosen non-null weight for all sub questions
  private calculateOverallWeightedSum(areChosenWeightsAllNull: boolean[], totalChosenWeights: number[]): number {
    if (areChosenWeightsAllNull.every(Boolean)) {
        return NO_VALUE;
    }
    let sum: number = 0;
    for (const totalChosenWeight of totalChosenWeights) {
        sum += totalChosenWeight === NO_VALUE ? 0 : totalChosenWeight;
    }
    return +(sum).toFixed(2);
  }
}
