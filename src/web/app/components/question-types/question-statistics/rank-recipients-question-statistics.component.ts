import { Component, OnChanges, OnInit } from '@angular/core';
import { DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';
import {
  RankRecipientsQuestionStatisticsCalculation,
} from './question-statistics-calculation/rank-recipients-question-statistics-calculation';

/**
 * Statistics for rank recipients questions.
 */
@Component({
  selector: 'tm-rank-recipients-question-statistics',
  templateUrl: './rank-recipients-question-statistics.component.html',
  styleUrls: ['./rank-recipients-question-statistics.component.scss'],
})
export class RankRecipientsQuestionStatisticsComponent extends RankRecipientsQuestionStatisticsCalculation
    implements OnInit, OnChanges {

  // enum
  SortBy: typeof SortBy = SortBy;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  constructor() {
    super(DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS());
  }

  ngOnInit(): void {
    this.calculateStatistics();
    this.getTableData();
  }

  ngOnChanges(): void {
    this.calculateStatistics();
    this.getTableData();
  }

  private getTableData(): void {
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

    this.rowsData = Object.keys(this.ranksReceivedPerOption).map((key: string) => {
      return [
        { value: this.emailToTeamName[key] },
        {
          value: this.emailToName[key]
          + (key === this.emailToName[key] ? '' : key),
        },
        { value: this.ranksReceivedPerOption[key].join(', ') },
        { value: this.selfRankPerOption[key] || '-' },
        { value: this.rankPerOption[key] || '-' },
        { value: this.rankPerOptionExcludeSelf[key] || '-' },
        { value: this.rankPerOptionInTeam[key] || '-' },
        { value: this.rankPerOptionInTeamExcludeSelf[key] || '-' },
      ];
    });
  }

}
