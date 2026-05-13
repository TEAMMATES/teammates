import { Component, Input, OnChanges } from '@angular/core';
import { DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import { FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails } from '../../../../types/api-output';
import { calculateConstsumOptionsQuestionStatistics } from '../../../utils/question-statistics.util';
import { ConstsumOptionsQuestionStatistics, Response } from '../../../../types/question-statistics.model';

/**
 * Statistics for constsum options questions.
 */
@Component({
  selector: 'tm-constsum-options-question-statistics',
  templateUrl: './constsum-options-question-statistics.component.html',
  imports: [SortableTableComponent],
})
export class ConstsumOptionsQuestionStatisticsComponent implements OnChanges {
  // enum
  @Input()
  question: FeedbackConstantSumQuestionDetails = DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS();
  @Input()
  responses: Response<FeedbackConstantSumResponseDetails>[] = [];
  @Input()
  isStudent = false;

  SortBy: typeof SortBy = SortBy;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  ngOnChanges(): void {
    const stats = calculateConstsumOptionsQuestionStatistics(this.question, this.responses);
    this.getTableData(stats);
  }

  private getTableData(stats: ConstsumOptionsQuestionStatistics): void {
    this.columnsData = [
      { header: 'Option', sortBy: SortBy.CONSTSUM_OPTIONS_OPTION },
      { header: 'Points Received' },
      { header: 'Total Points', sortBy: SortBy.CONSTSUM_OPTIONS_POINTS },
      { header: 'Average Points', sortBy: SortBy.CONSTSUM_OPTIONS_POINTS },
    ];

    this.rowsData = Object.keys(stats.pointsPerOption).map((option: string) => [
      { value: option },
      { value: stats.pointsPerOption[option].join(', ') },
      { value: stats.totalPointsPerOption[option] },
      { value: stats.averagePointsPerOption[option] },
    ]);
  }
}
