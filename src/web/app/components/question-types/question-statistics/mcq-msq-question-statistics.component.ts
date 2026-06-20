import { Component, Input, OnChanges } from '@angular/core';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import { FeedbackMcqMsqCourseWideStatistics } from '../../../../types/api-output';
import { QuestionStatisticsTypeChecker } from '../../../../types/question-statistics-impl/question-statistics-caster';

/**
 * Statistics for MCQ/MSQ questions rendered from backend-calculated data.
 */
@Component({
  selector: 'tm-mcq-msq-question-statistics',
  templateUrl: './mcq-msq-question-statistics.component.html',
  imports: [SortableTableComponent],
})
export class McqMsqQuestionStatisticsComponent implements OnChanges {
  @Input()
  statistics: FeedbackMcqMsqCourseWideStatistics | undefined;
  @Input()
  isStudent = false;

  // enum
  SortBy!: typeof SortBy;

  summaryColumnsData: ColumnData[] = [];
  summaryRowsData: SortableTableCellData[][] = [];
  perRecipientColumnsData: ColumnData[] = [];
  perRecipientRowsData: SortableTableCellData[][] = [];

  constructor() {
    this.SortBy = SortBy;
  }

  ngOnChanges(): void {
    this.buildTableData();
  }

  private buildTableData(): void {
    const stats = this.statistics;
    if (!stats?.hasAnswers) {
      return;
    }

    this.summaryColumnsData = [
      { header: 'Choice', sortBy: SortBy.MCQ_MSQ_CHOICE },
      { header: 'Weight', sortBy: SortBy.MCQ_MSQ_WEIGHT },
      { header: 'Response Count', sortBy: SortBy.MCQ_MSQ_RESPONSE_COUNT },
      { header: 'Percentage (%)', sortBy: SortBy.MCQ_MSQ_PERCENTAGE },
      { header: 'Weighted Percentage (%)', sortBy: SortBy.MCQ_MSQ_WEIGHTED_PERCENTAGE },
    ];

    this.summaryRowsData = stats.rows.map((row) => [
      { value: row.option },
      { value: row.weight === 0 ? 0 : (row.weight ?? '-') },
      { value: row.count },
      { value: row.percentage },
      { value: row.weightedPercentage === 0 ? 0 : (row.weightedPercentage ?? '-') },
    ]);

    if (stats.hasWeights && stats.perRecipientRows.length > 0) {
      const optionLabels = stats.rows.map((r) => r.option);

      this.perRecipientColumnsData = [
        { header: 'Team', sortBy: SortBy.MCQ_MSQ_TEAM },
        { header: 'Recipient Name', sortBy: SortBy.MCQ_MSQ_RECIPIENT_NAME },
        ...optionLabels.map((label) => {
          const weight = stats.rows.find((r) => r.option === label)?.weight;
          return {
            header: `${label} [${weight?.toFixed(2) ?? '-'}]`,
            sortBy: SortBy.MCQ_MSQ_OPTION_SELECTED_TIMES,
          };
        }),
        { header: 'Total', sortBy: SortBy.MCQ_MSQ_WEIGHT_TOTAL },
        { header: 'Average', sortBy: SortBy.MCQ_MSQ_WEIGHT_AVERAGE },
      ];

      this.perRecipientRowsData = stats.perRecipientRows.map((row) => [
        { value: row.recipientTeam },
        { value: row.recipientName + (row.recipientEmail ? ` (${row.recipientEmail})` : '') },
        ...optionLabels.map((label) => ({ value: row.responseCountPerOption[label] ?? 0 })),
        { value: row.total.toFixed(2) },
        { value: row.average.toFixed(2) },
      ]);
    } else {
      this.perRecipientColumnsData = [];
      this.perRecipientRowsData = [];
    }
  }

  get hasPerRecipientData(): boolean {
    return (
      QuestionStatisticsTypeChecker.isMcqMsqCourseWide(this.statistics) &&
      this.statistics.hasWeights &&
      this.statistics.perRecipientRows.length > 0
    );
  }
}
