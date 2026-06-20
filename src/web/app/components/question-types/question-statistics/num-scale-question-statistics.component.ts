import { Component, Input, OnChanges } from '@angular/core';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import {
  FeedbackNumScaleStatistics,
  FeedbackQuestionResultsStatisticsView,
  FeedbackQuestionType,
  NumScaleRecipientRow,
} from '../../../../types/api-output';

/**
 * Statistics for numerical scale questions.
 */
@Component({
  selector: 'tm-num-scale-question-statistics',
  templateUrl: './num-scale-question-statistics.component.html',
  imports: [SortableTableComponent],
})
export class NumScaleQuestionStatisticsComponent implements OnChanges {
  @Input()
  statistics: FeedbackNumScaleStatistics = {
    questionType: FeedbackQuestionType.NUMSCALE,
    statisticsView: FeedbackQuestionResultsStatisticsView.COURSE_WIDE,
    rows: [],
  };

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  ngOnChanges(): void {
    this.buildTableData();
  }

  private buildTableData(): void {
    const showExcludeSelf = this.statistics.rows.some((row) => row.averageExcludingSelf != null);

    this.columnsData = [
      { header: 'Team', sortBy: SortBy.TEAM_NAME },
      { header: 'Recipient', sortBy: SortBy.RECIPIENT_NAME },
      { header: 'Average', sortBy: SortBy.NUMERICAL_SCALE_AVERAGE, headerToolTip: 'Average of the visible responses' },
      { header: 'Max', sortBy: SortBy.NUMERICAL_SCALE_MAX, headerToolTip: 'Maximum of the visible responses' },
      { header: 'Min', sortBy: SortBy.NUMERICAL_SCALE_MIN, headerToolTip: 'Minimum of the visible responses' },
    ];
    if (showExcludeSelf) {
      this.columnsData.push({
        header: 'Average excluding self response',
        sortBy: SortBy.NUMERICAL_SCALE_AVERAGE_EXCLUDE_SELF,
        headerToolTip: "Average of the visible responses excluding recipient's own response to himself/herself",
      });
    }

    this.rowsData = this.statistics.rows.map((row: NumScaleRecipientRow) => {
      const recipientDisplay = row.recipientName + (row.recipientEmail ? ` (${row.recipientEmail})` : '');
      const cells: SortableTableCellData[] = [
        { value: row.recipientTeam },
        { value: recipientDisplay },
        { value: row.average },
        { value: row.max },
        { value: row.min },
      ];
      if (showExcludeSelf) {
        cells.push({ value: row.averageExcludingSelf });
      }
      return cells;
    });
  }
}
