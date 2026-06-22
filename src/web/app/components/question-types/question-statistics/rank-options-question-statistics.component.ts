import { Component, Input, OnChanges } from '@angular/core';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import {
  FeedbackQuestionType,
  FeedbackRankOptionsStatistics,
  FeedbackQuestionResultsStatisticsView,
  RankOptionsOptionRow,
} from '../../../../types/api-output';

/**
 * Statistics for rank options questions.
 */
@Component({
  selector: 'tm-rank-options-question-statistics',
  templateUrl: './rank-options-question-statistics.component.html',
  imports: [SortableTableComponent],
})
export class RankOptionsQuestionStatisticsComponent implements OnChanges {
  @Input()
  statistics: FeedbackRankOptionsStatistics = {
    questionType: FeedbackQuestionType.RANK_OPTIONS,
    statisticsView: FeedbackQuestionResultsStatisticsView.COURSE_WIDE,
    options: [],
  };

  // enum
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
      { header: 'Option', sortBy: SortBy.RANK_OPTIONS_OPTION },
      { header: 'Ranks Received' },
      { header: 'Overall Rank', sortBy: SortBy.RANK_OPTIONS_OVERALL_RANK },
    ];

    this.rowsData = this.statistics.options.map((row: RankOptionsOptionRow) => [
      { value: row.option },
      { value: row.ranksReceived.join(', ') },
      { value: row.overallRank ?? '-' },
    ]);
  }
}
