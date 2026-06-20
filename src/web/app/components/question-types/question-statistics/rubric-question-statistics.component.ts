import { Component, Input, OnChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { StringHelper } from '../../../../services/string-helper';
import {
  FeedbackQuestionType,
  FeedbackQuestionResultsStatisticsView,
  FeedbackRubricStatistics,
  RubricPerRecipientStats,
  RubricSubQuestionRow,
} from '../../../../types/api-output';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';

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
  statistics: FeedbackRubricStatistics = {
    questionType: FeedbackQuestionType.RUBRIC,
    statisticsView: FeedbackQuestionResultsStatisticsView.COURSE_WIDE,
    subQuestions: [],
    choices: [],
    hasWeights: false,
    rows: [],
    rowsExcludeSelf: [],
    perRecipientStats: [],
  };

  excludeSelf = false;

  summaryColumnsData: ColumnData[] = [];
  summaryRowsData: SortableTableCellData[][] = [];
  perRecipientPerCriterionColumnsData: ColumnData[] = [];
  perRecipientPerCriterionRowsData: SortableTableCellData[][] = [];
  perRecipientOverallColumnsData: ColumnData[] = [];
  perRecipientOverallRowsData: SortableTableCellData[][] = [];

  ngOnChanges(): void {
    this.getTableData(this.statistics);
  }

  getTableData(stats: FeedbackRubricStatistics): void {
    const activeRows: RubricSubQuestionRow[] = this.excludeSelf ? stats.rowsExcludeSelf : stats.rows;
    const showWeights = stats.hasWeights;

    this.summaryColumnsData = [
      { header: 'Question', sortBy: SortBy.RUBRIC_SUBQUESTION },
      ...stats.choices.map((choice: string) => ({ header: choice, sortBy: SortBy.RUBRIC_CHOICE })),
    ];
    if (showWeights) {
      this.summaryColumnsData.push({ header: 'Average', sortBy: SortBy.RUBRIC_WEIGHT_AVERAGE });
    }

    this.summaryRowsData = activeRows.map((row: RubricSubQuestionRow, questionIndex: number) => {
      const currRow: SortableTableCellData[] = [
        { value: `${StringHelper.integerToLowerCaseAlphabeticalIndex(questionIndex + 1)}) ${row.subQuestion}` },
        ...row.cells.map((cell) => {
          const weightStr = showWeights && cell.weight != null ? ` [${cell.weight}]` : '';
          return { value: `${cell.percentage}% (${cell.count})${weightStr}` };
        }),
      ];
      if (showWeights) {
        currRow.push({ value: this.getDisplayWeight(row.weightAverage) });
      }
      return currRow;
    });

    if (!showWeights || stats.perRecipientStats.length === 0) {
      return;
    }

    this.perRecipientPerCriterionColumnsData = [
      { header: 'Team', sortBy: SortBy.TEAM_NAME },
      { header: 'Recipient Name', sortBy: SortBy.RECIPIENT_NAME },
      { header: 'Sub Question', sortBy: SortBy.QUESTION_TEXT },
      ...stats.choices.map((choice: string) => ({ header: choice, sortBy: SortBy.RUBRIC_CHOICE })),
      { header: 'Total', sortBy: SortBy.RUBRIC_TOTAL_CHOSEN_WEIGHT },
      { header: 'Average', sortBy: SortBy.RUBRIC_WEIGHT_AVERAGE },
    ];

    this.perRecipientPerCriterionRowsData = [];
    stats.perRecipientStats.forEach((perRecipientStats: RubricPerRecipientStats) => {
      perRecipientStats.perCriterionRows.forEach((criterionRow, questionIndex) => {
        this.perRecipientPerCriterionRowsData.push([
          { value: perRecipientStats.recipientTeam },
          {
            value:
              perRecipientStats.recipientName +
              (perRecipientStats.recipientEmail ? ` (${perRecipientStats.recipientEmail})` : ''),
          },
          {
            value: `${StringHelper.integerToLowerCaseAlphabeticalIndex(questionIndex + 1)}) ${criterionRow.subQuestion}`,
          },
          ...criterionRow.cells.map((cell) => {
            const weightStr = cell.weight != null ? ` [${cell.weight}]` : '';
            return { value: `${cell.percentage}% (${cell.count})${weightStr}` };
          }),
          { value: this.getDisplayWeight(criterionRow.total) },
          { value: this.getDisplayWeight(criterionRow.average) },
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
    stats.perRecipientStats.forEach((perRecipientStats: RubricPerRecipientStats) => {
      const perCriterionAverage: string = perRecipientStats.subQuestionAverages
        .map((val: number | null) => this.getDisplayWeight(val))
        .toString();
      this.perRecipientOverallRowsData.push([
        { value: perRecipientStats.recipientTeam },
        { value: perRecipientStats.recipientName },
        { value: perRecipientStats.recipientEmail },
        ...perRecipientStats.overallCells.map((cell) => {
          const weightStr = cell.weight != null ? ` [${cell.weight}]` : '';
          return { value: `${cell.percentage}% (${cell.count})${weightStr}` };
        }),
        { value: this.getDisplayWeight(perRecipientStats.overallTotal) },
        { value: this.getDisplayWeight(perRecipientStats.overallAverage) },
        { value: perCriterionAverage },
      ]);
    });
  }

  private getDisplayWeight(weight: number | null | undefined): string {
    return weight == null ? '-' : String(weight);
  }
}
