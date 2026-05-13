import { Component, Input, OnChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { StringHelper } from '../../../../services/string-helper';
import { NO_VALUE } from '../../../../types/feedback-response-details';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import { FeedbackRubricQuestionDetails, FeedbackRubricResponseDetails } from '../../../../types/api-output';
import { DEFAULT_RUBRIC_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import {
  Response,
  RubricPerRecipientStats,
  RubricQuestionStatistics,
} from '../../../../types/question-statistics.model';
import { calculateRubricQuestionStatistics } from '../../../utils/question-statistics.util';

/**
 * Statistics for rubric questions.
 */
@Component({
  selector: 'tm-rubric-question-statistics',
  templateUrl: './rubric-question-statistics.component.html',
  imports: [NgbTooltip, FormsModule, SortableTableComponent],
})
export class RubricQuestionStatisticsComponent implements OnChanges {
  @Input()
  question: FeedbackRubricQuestionDetails = DEFAULT_RUBRIC_QUESTION_DETAILS();
  @Input()
  responses: Response<FeedbackRubricResponseDetails>[] = [];
  @Input()
  isStudent = false;

  excludeSelf = false;

  summaryColumnsData: ColumnData[] = [];
  summaryRowsData: SortableTableCellData[][] = [];
  perRecipientPerCriterionColumnsData: ColumnData[] = [];
  perRecipientPerCriterionRowsData: SortableTableCellData[][] = [];
  perRecipientOverallColumnsData: ColumnData[] = [];
  perRecipientOverallRowsData: SortableTableCellData[][] = [];

  stats: RubricQuestionStatistics = {
    subQuestions: [],
    choices: [],
    hasWeights: false,
    weights: [],
    answers: [],
    isWeightStatsVisible: false,
    percentages: [],
    subQuestionWeightAverage: [],
    answersExcludeSelf: [],
    percentagesExcludeSelf: [],
    subQuestionWeightAverageExcludeSelf: [],
    perRecipientStatsMap: {},
  };

  ngOnChanges(): void {
    this.stats = calculateRubricQuestionStatistics(this.question, this.responses, this.isStudent);
    this.getTableData(this.stats);
  }

  getTableData(stats: RubricQuestionStatistics): void {
    this.summaryColumnsData = [
      { header: 'Question', sortBy: SortBy.RUBRIC_SUBQUESTION },
      ...stats.choices.map((choice: string) => ({ header: choice, sortBy: SortBy.RUBRIC_CHOICE })),
    ];
    if (stats.isWeightStatsVisible) {
      this.summaryColumnsData.push({ header: 'Average', sortBy: SortBy.RUBRIC_WEIGHT_AVERAGE });
    }

    this.summaryRowsData = stats.subQuestions.map((subQuestion: string, questionIndex: number) => {
      const currRow: SortableTableCellData[] = [
        { value: `${StringHelper.integerToLowerCaseAlphabeticalIndex(questionIndex + 1)}) ${subQuestion}` },
        ...stats.choices.map((_: string, choiceIndex: number) => {
          if (this.excludeSelf) {
            return {
              value:
                `${stats.percentagesExcludeSelf[questionIndex][choiceIndex]}%` +
                ` (${stats.answersExcludeSelf[questionIndex][choiceIndex]})` +
                `${
                  stats.isWeightStatsVisible
                    ? ` [${this.getDisplayWeight(stats.weights[questionIndex][choiceIndex])}]`
                    : ''
                }`,
            };
          }
          return {
            value:
              `${stats.percentages[questionIndex][choiceIndex]}%` +
              ` (${stats.answers[questionIndex][choiceIndex]})` +
              `${
                stats.isWeightStatsVisible
                  ? ` [${this.getDisplayWeight(stats.weights[questionIndex][choiceIndex])}]`
                  : ''
              }`,
          };
        }),
      ];
      if (stats.isWeightStatsVisible) {
        if (this.excludeSelf) {
          currRow.push({
            value: this.getDisplayWeight(stats.subQuestionWeightAverageExcludeSelf[questionIndex]),
          });
        } else {
          currRow.push({
            value: this.getDisplayWeight(stats.subQuestionWeightAverage[questionIndex]),
          });
        }
      }

      return currRow;
    });

    if (!stats.isWeightStatsVisible) {
      return;
    }

    // generate per recipient tables if weight is enabled
    this.perRecipientPerCriterionColumnsData = [
      { header: 'Team', sortBy: SortBy.TEAM_NAME },
      { header: 'Recipient Name', sortBy: SortBy.RECIPIENT_NAME },
      { header: 'Sub Question', sortBy: SortBy.QUESTION_TEXT },
      ...stats.choices.map((choice: string) => ({ header: choice, sortBy: SortBy.RUBRIC_CHOICE })),
      { header: 'Total', sortBy: SortBy.RUBRIC_TOTAL_CHOSEN_WEIGHT },
      { header: 'Average', sortBy: SortBy.RUBRIC_WEIGHT_AVERAGE },
    ];

    this.perRecipientPerCriterionRowsData = [];
    Object.values(stats.perRecipientStatsMap).forEach((perRecipientStats: RubricPerRecipientStats) => {
      stats.subQuestions.forEach((subQuestion: string, questionIndex: number) => {
        this.perRecipientPerCriterionRowsData.push([
          { value: perRecipientStats.recipientTeam },
          {
            value:
              perRecipientStats.recipientName +
              (perRecipientStats.recipientEmail ? ` (${perRecipientStats.recipientEmail})` : ''),
          },
          { value: `${StringHelper.integerToLowerCaseAlphabeticalIndex(questionIndex + 1)}) ${subQuestion}` },
          ...stats.choices.map((_: string, choiceIndex: number) => {
            return {
              value:
                `${perRecipientStats.percentages[questionIndex][choiceIndex]}%` +
                ` (${perRecipientStats.answers[questionIndex][choiceIndex]})` +
                ` [${this.getDisplayWeight(stats.weights[questionIndex][choiceIndex])}]`,
            };
          }),
          { value: this.getDisplayWeight(perRecipientStats.subQuestionTotalChosenWeight[questionIndex]) },
          { value: this.getDisplayWeight(perRecipientStats.subQuestionWeightAverage[questionIndex]) },
        ]);
      });
    });

    this.perRecipientOverallColumnsData = [
      { header: 'Team', sortBy: SortBy.TEAM_NAME },
      { header: 'Recipient Name', sortBy: SortBy.RECIPIENT_NAME },
      { header: 'Recipient Email', sortBy: SortBy.RECIPIENT_EMAIL },
      ...stats.choices.map((choice: string) => ({ header: choice, sortBy: SortBy.RUBRIC_CHOICE })),
      { header: 'Total', sortBy: SortBy.RUBRIC_OVERALL_TOTAL_WEIGHT },
      { header: 'Average', sortBy: SortBy.RUBRIC_OVERALL_WEIGHT_AVERAGE },
      { header: 'Per Criterion Average', sortBy: SortBy.RUBRIC_OVERALL_WEIGHT_AVERAGE },
    ];

    this.perRecipientOverallRowsData = [];
    Object.values(stats.perRecipientStatsMap).forEach((perRecipientStats: RubricPerRecipientStats) => {
      const perCriterionAverage: string = perRecipientStats.subQuestionWeightAverage
        .map((val: number) => this.getDisplayWeight(val))
        .toString();
      this.perRecipientOverallRowsData.push([
        { value: perRecipientStats.recipientTeam },
        { value: perRecipientStats.recipientName },
        { value: perRecipientStats.recipientEmail },
        ...stats.choices.map((_: string, choiceIndex: number) => {
          return {
            value:
              `${perRecipientStats.percentagesAverage[choiceIndex]}%` +
              ` (${perRecipientStats.answersSum[choiceIndex]})` +
              ` [${this.getDisplayWeight(perRecipientStats.weightsAverage[choiceIndex])}]`,
          };
        }),
        { value: this.getDisplayWeight(perRecipientStats.overallWeightedSum) },
        { value: this.getDisplayWeight(perRecipientStats.overallWeightAverage) },
        { value: perCriterionAverage },
      ]);
    });
  }

  private getDisplayWeight(weight: number): string {
    return weight === null || weight === NO_VALUE ? '-' : String(weight);
  }
}
