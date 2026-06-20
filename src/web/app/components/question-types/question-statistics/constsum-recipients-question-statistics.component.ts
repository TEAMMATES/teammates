import { Component, Input, OnChanges } from '@angular/core';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import {
  ConstsumRecipientRow,
  FeedbackConstsumRecipientsStatistics,
  FeedbackQuestionResultsStatisticsView,
  FeedbackQuestionType,
} from '../../../../types/api-output';

/**
 * Statistics for constsum recipients questions.
 */
@Component({
  selector: 'tm-constsum-recipients-question-statistics',
  templateUrl: './constsum-recipients-question-statistics.component.html',
  imports: [SortableTableComponent],
})
export class ConstsumRecipientsQuestionStatisticsComponent implements OnChanges {
  @Input()
  statistics: FeedbackConstsumRecipientsStatistics = {
    questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
    statisticsView: FeedbackQuestionResultsStatisticsView.COURSE_WIDE,
    rows: [],
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
      { header: 'Team', sortBy: SortBy.TEAM_NAME },
      { header: 'Recipient', sortBy: SortBy.RECIPIENT_NAME },
      { header: 'Points Received' },
      { header: 'Total Points', sortBy: SortBy.CONSTSUM_RECIPIENTS_POINTS },
      { header: 'Average Points', sortBy: SortBy.CONSTSUM_RECIPIENTS_POINTS },
      { header: 'Average Excluding Self', sortBy: SortBy.CONSTSUM_RECIPIENTS_POINTS },
    ];

    this.rowsData = this.statistics.rows.map((row: ConstsumRecipientRow) => {
      const displayName = row.isCurrentRecipient
        ? 'You'
        : row.recipientName + (row.recipientEmail ? ` (${row.recipientEmail})` : '');
      return [
        { value: row.recipientTeam },
        { value: displayName },
        { value: row.pointsReceived.join(', ') },
        { value: row.total },
        { value: row.average },
        { value: row.averageExcludingSelf ?? '' },
      ];
    });
  }
}
