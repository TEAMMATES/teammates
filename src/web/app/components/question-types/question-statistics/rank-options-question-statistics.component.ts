import { Component, Input, OnChanges } from '@angular/core';
import { DEFAULT_RANK_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import { FeedbackRankOptionsQuestionDetails, FeedbackRankOptionsResponseDetails } from '../../../../types/api-output';
import { RankOptionsQuestionStatistics, Response } from '../../../../types/question-statistics.model';
import { calculateRankOptionsQuestionStatistics } from '../../../utils/question-statistics.util';

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
  question: FeedbackRankOptionsQuestionDetails = DEFAULT_RANK_OPTIONS_QUESTION_DETAILS();
  @Input()
  responses: Response<FeedbackRankOptionsResponseDetails>[] = [];
  @Input()
  isStudent = false;

  // enum
  SortBy: typeof SortBy = SortBy;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  ngOnChanges(): void {
    const stats = calculateRankOptionsQuestionStatistics(this.question, this.responses);
    this.getTableData(stats);
  }

  private getTableData(stats: RankOptionsQuestionStatistics): void {
    this.columnsData = [
      { header: 'Option', sortBy: SortBy.RANK_OPTIONS_OPTION },
      { header: 'Ranks Received' },
      { header: 'Overall Rank', sortBy: SortBy.RANK_OPTIONS_OVERALL_RANK },
    ];

    this.rowsData = Object.keys(stats.ranksReceivedPerOption).map((key: string) => {
      return [
        { value: key },
        { value: stats.ranksReceivedPerOption[key].join(', ') },
        { value: stats.rankPerOption[key] ? stats.rankPerOption[key] : '' },
      ];
    });
  }
}
