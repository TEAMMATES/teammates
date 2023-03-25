import { Component, OnChanges } from '@angular/core';
import { StringHelper } from '../../../../services/string-helper';
import { DEFAULT_RUBRIC_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';
import {
  PerRecipientStats,
  RubricQuestionStatisticsCalculation,
} from './question-statistics-calculation/rubric-question-statistics-calculation';

/**
 * Statistics for rubric questions.
 */
@Component({
  selector: 'tm-rubric-question-statistics',
  templateUrl: './rubric-question-statistics.component.html',
  styleUrls: ['./rubric-question-statistics.component.scss'],
})
export class RubricQuestionStatisticsComponent extends RubricQuestionStatisticsCalculation
    implements OnChanges {

  excludeSelf: boolean = false;

  summaryColumnsData: ColumnData[] = [];
  summaryRowsData: SortableTableCellData[][] = [];
  perRecipientPerCriterionColumnsData: ColumnData[] = [];
  perRecipientPerCriterionRowsData: SortableTableCellData[][] = [];
  perRecipientOverallColumnsData: ColumnData[] = [];
  perRecipientOverallRowsData: SortableTableCellData[][] = [];

  constructor() {
    super(DEFAULT_RUBRIC_QUESTION_DETAILS());
  }

  ngOnChanges(): void {
    this.calculateStatistics();
    this.getTableData();
  }

  getTableData(): void {
    this.summaryColumnsData = [
        { header: 'Question', sortBy: SortBy.RUBRIC_SUBQUESTION },
      ...this.choices.map((choice: string) => ({ header: choice, sortBy: SortBy.RUBRIC_CHOICE })),
    ];
    if (this.isWeightStatsVisible) {
      this.summaryColumnsData.push({ header: 'Average', sortBy: SortBy.RUBRIC_WEIGHT_AVERAGE });
    }

    this.summaryRowsData = this.subQuestions.map((subQuestion: string, questionIndex: number) => {
      const currRow: SortableTableCellData[] = [
        { value: `${StringHelper.integerToLowerCaseAlphabeticalIndex(questionIndex + 1)}) ${subQuestion}` },
        ...this.choices.map((_: string, choiceIndex: number) => {
          if (this.excludeSelf) {
            return {
              value: `${this.percentagesExcludeSelf[questionIndex][choiceIndex]}%`
                  + ` (${this.answersExcludeSelf[questionIndex][choiceIndex]})`
                  + `${this.isWeightStatsVisible ? ` [${this.weights[questionIndex][choiceIndex]}]` : ''}`,
            };
          }
          return {
            value: `${this.percentages[questionIndex][choiceIndex]}%`
                + ` (${this.answers[questionIndex][choiceIndex]})`
                + `${this.isWeightStatsVisible ? ` [${this.weights[questionIndex][choiceIndex]}]` : ''}`,
          };
        }),
      ];
      if (this.isWeightStatsVisible) {
        if (this.excludeSelf) {
          currRow.push({ value: this.subQuestionWeightAverageExcludeSelf[questionIndex] });
        } else {
          currRow.push({ value: this.subQuestionWeightAverage[questionIndex] });
        }
      }

      return currRow;
    });

    if (!this.isWeightStatsVisible) {
      return;
    }

    // generate per recipient tables if weight is enabled
    this.perRecipientPerCriterionColumnsData = [
      { header: 'Team', sortBy: SortBy.TEAM_NAME },
      { header: 'Recipient Name', sortBy: SortBy.RECIPIENT_NAME },
      { header: 'Sub Question', sortBy: SortBy.QUESTION_TEXT },
      ...this.choices.map((choice: string) => ({ header: choice, sortBy: SortBy.RUBRIC_CHOICE })),
      { header: 'Total', sortBy: SortBy.RUBRIC_TOTAL_CHOSEN_WEIGHT },
      { header: 'Average', sortBy: SortBy.RUBRIC_WEIGHT_AVERAGE },
    ];

    this.perRecipientPerCriterionRowsData = [];
    Object.values(this.perRecipientStatsMap).forEach((perRecipientStats: PerRecipientStats) => {
      this.subQuestions.forEach((subQuestion: string, questionIndex: number) => {
        this.perRecipientPerCriterionRowsData.push([
          { value: perRecipientStats.recipientTeam },
          {
            value: perRecipientStats.recipientName
            + (perRecipientStats.recipientEmail ? ` (${perRecipientStats.recipientEmail})` : ''),
          },
          { value: `${StringHelper.integerToLowerCaseAlphabeticalIndex(questionIndex + 1)}) ${subQuestion}` },
          ...this.choices.map((_: string, choiceIndex: number) => {
            return {
              value: `${perRecipientStats.percentages[questionIndex][choiceIndex]}%`
                  + ` (${perRecipientStats.answers[questionIndex][choiceIndex]})`
                  + ` [${this.weights[questionIndex][choiceIndex]}]`,
            };
          }),
          { value: perRecipientStats.subQuestionTotalChosenWeight[questionIndex] },
          { value: perRecipientStats.subQuestionWeightAverage[questionIndex] },
        ]);
      });
    });

    this.perRecipientOverallColumnsData = [
      { header: 'Team', sortBy: SortBy.TEAM_NAME },
      { header: 'Recipient Name', sortBy: SortBy.RECIPIENT_NAME },
      { header: 'Recipient Email', sortBy: SortBy.RECIPIENT_EMAIL },
      ...this.choices.map((choice: string) => ({ header: choice, sortBy: SortBy.RUBRIC_CHOICE })),
      { header: 'Total', sortBy: SortBy.RUBRIC_OVERALL_TOTAL_WEIGHT },
      { header: 'Average', sortBy: SortBy.RUBRIC_OVERALL_WEIGHT_AVERAGE },
      { header: 'Per Criterion Average', sortBy: SortBy.RUBRIC_OVERALL_WEIGHT_AVERAGE },
    ];

    this.perRecipientOverallRowsData = [];
    Object.values(this.perRecipientStatsMap).forEach((perRecipientStats: PerRecipientStats) => {
      this.perRecipientOverallRowsData.push([
        { value: perRecipientStats.recipientTeam },
        { value: perRecipientStats.recipientName },
        { value: perRecipientStats.recipientEmail },
        ...this.choices.map((_: string, choiceIndex: number) => {
          return {
            value: `${perRecipientStats.percentagesAverage[choiceIndex]}%`
                + ` (${perRecipientStats.answersSum[choiceIndex]})`
                + ` [${perRecipientStats.weightsAverage[choiceIndex]}]`,
          };
        }),
        { value: perRecipientStats.overallWeightedSum },
        { value: perRecipientStats.overallWeightAverage },
        { value: perRecipientStats.subQuestionWeightAverage.toString() },
      ]);
    });
  }
}
