import { Component, Input, OnChanges } from '@angular/core';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import {
  FeedbackQuestionType,
  FeedbackRankRecipientsStatistics,
  FeedbackQuestionResultsStatisticsView,
  RankRecipientsRow,
} from '../../../../types/api-output';

/**
 * Statistics for rank recipients questions.
 */
@Component({
  selector: 'tm-rank-recipients-question-statistics',
  templateUrl: './rank-recipients-question-statistics.component.html',
  imports: [SortableTableComponent],
})
export class RankRecipientsQuestionStatisticsComponent implements OnChanges {
  @Input()
  statistics: FeedbackRankRecipientsStatistics = {
    questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    statisticsView: FeedbackQuestionResultsStatisticsView.COURSE_WIDE,
    rows: [],
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
      { header: 'Team', sortBy: SortBy.RANK_RECIPIENTS_TEAM },
      { header: 'Recipient', sortBy: SortBy.RANK_RECIPIENTS_RECIPIENT },
      { header: 'Ranks Received' },
      { header: 'Self Rank', sortBy: SortBy.RANK_RECIPIENTS_SELF_RANK },
      { header: 'Overall Rank', sortBy: SortBy.RANK_RECIPIENTS_OVERALL_RANK },
      { header: 'Overall Rank Excluding Self', sortBy: SortBy.RANK_RECIPIENTS_OVERALL_RANK_EXCLUDING_SELF },
      { header: 'Team Rank', sortBy: SortBy.RANK_RECIPIENTS_TEAM_RANK },
      { header: 'Team Rank Excluding Self', sortBy: SortBy.RANK_RECIPIENTS_TEAM_RANK_EXCLUDING_SELF },
    ];

    this.rowsData = this.statistics.rows.map((row: RankRecipientsRow) => {
      const displayName = row.recipientName + (row.recipientEmail ? ` (${row.recipientEmail})` : '');
      return [
        { value: row.recipientTeam },
        { value: displayName },
        { value: row.ranksReceived.join(', ') },
        { value: row.selfRank ?? '-' },
        { value: row.overallRank ?? '-' },
        { value: row.rankExcludingSelf ?? '-' },
        { value: row.rankInTeam ?? '-' },
        { value: row.rankInTeamExcludingSelf ?? '-' },
      ];
    });
  }
}
