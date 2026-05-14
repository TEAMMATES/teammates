import { Component, Input, OnChanges } from '@angular/core';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import { FeedbackRankRecipientsResponseDetails, QuestionRecipientType } from '../../../../types/api-output';
import { RankRecipientsQuestionStatistics, Response } from '../../../../types/question-statistics.model';
import { calculateRankRecipientsQuestionStatistics } from '../../../utils/question-statistics.util';

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
  responses: Response<FeedbackRankRecipientsResponseDetails>[] = [];
  @Input()
  isStudent = false;
  @Input()
  recipientType: QuestionRecipientType = QuestionRecipientType.NONE;

  // enum
  SortBy: typeof SortBy = SortBy;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  ngOnChanges(): void {
    const stats = calculateRankRecipientsQuestionStatistics(this.responses, this.recipientType);
    this.getTableData(stats);
  }

  private getTableData(stats: RankRecipientsQuestionStatistics): void {
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

    this.rowsData = Object.keys(stats.ranksReceivedPerOption).map((key: string) => {
      return [
        { value: stats.emailToTeamName[key] },
        {
          value: stats.emailToName[key] + (key === stats.emailToName[key] ? '' : ` (${key})`),
        },
        { value: stats.ranksReceivedPerOption[key].join(', ') },
        { value: stats.selfRankPerOption[key] || '-' },
        { value: stats.rankPerOption[key] || '-' },
        { value: stats.rankPerOptionExcludeSelf[key] || '-' },
        { value: stats.rankPerOptionInTeam[key] || '-' },
        { value: stats.rankPerOptionInTeamExcludeSelf[key] || '-' },
      ];
    });
  }
}
