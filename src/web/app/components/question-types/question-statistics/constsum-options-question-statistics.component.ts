import { Component, Input, OnChanges } from '@angular/core';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import {
  ConstsumOptionRow,
  FeedbackConstsumOptionsStatistics,
  FeedbackQuestionResultsStatisticsView,
  FeedbackQuestionType,
} from '../../../../types/api-output';

/**
 * Statistics for constsum options questions.
 */
@Component({
  selector: 'tm-constsum-options-question-statistics',
  templateUrl: './constsum-options-question-statistics.component.html',
  imports: [SortableTableComponent],
})
export class ConstsumOptionsQuestionStatisticsComponent implements OnChanges {
  @Input()
  statistics: FeedbackConstsumOptionsStatistics = {
    questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
    statisticsView: FeedbackQuestionResultsStatisticsView.COURSE_WIDE,
    options: [],
  };

  SortBy!: typeof SortBy;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  constructor() {
    this.SortBy = SortBy;
  }

  ngOnChanges(): void {
    this.buildTableData();
  }

  private buildTableData(): void {
    this.columnsData = [
      { header: 'Option', sortBy: SortBy.CONSTSUM_OPTIONS_OPTION },
      { header: 'Points Received' },
      { header: 'Total Points', sortBy: SortBy.CONSTSUM_OPTIONS_POINTS },
      { header: 'Average Points', sortBy: SortBy.CONSTSUM_OPTIONS_POINTS },
    ];

    this.rowsData = this.statistics.options.map((row: ConstsumOptionRow) => [
      { value: row.option },
      { value: row.pointsReceived.join(', ') },
      { value: row.total },
      { value: row.average },
    ]);
  }
}
