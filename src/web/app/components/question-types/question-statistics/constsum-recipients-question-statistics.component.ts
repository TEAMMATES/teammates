import { Component, OnChanges, OnInit } from '@angular/core';
import { DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';
import {
  ConstsumRecipientsQuestionStatisticsCalculation,
} from './question-statistics-calculation/constsum-recipients-question-statistics-calculation';

/**
 * Statistics for constsum recipients questions.
 */
@Component({
  selector: 'tm-constsum-recipients-question-statistics',
  templateUrl: './constsum-recipients-question-statistics.component.html',
  styleUrls: ['./constsum-recipients-question-statistics.component.scss'],
})
export class ConstsumRecipientsQuestionStatisticsComponent extends ConstsumRecipientsQuestionStatisticsCalculation
    implements OnInit, OnChanges {

  // enum
  SortBy: typeof SortBy = SortBy;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  constructor() {
    super(DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS());
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
      { header: 'Team', sortBy: SortBy.TEAM_NAME },
      { header: 'Recipient', sortBy: SortBy.RECIPIENT_NAME },
      { header: 'Points Received' },
      { header: 'Total Points', sortBy: SortBy.CONSTSUM_RECIPIENTS_POINTS },
      { header: 'Average Points', sortBy: SortBy.CONSTSUM_RECIPIENTS_POINTS },
      { header: 'Average Excluding Self', sortBy: SortBy.CONSTSUM_RECIPIENTS_POINTS },
    ];

    this.rowsData = Object.keys(this.pointsPerOption).map((recipient: string) => [
      { value: this.emailToTeamName[recipient] },
      {
        value: this.emailToName[recipient]
        + (this.emailToTeamName[recipient] ? ` (${recipient})` : ''),
      },
      { value: this.pointsPerOption[recipient].join(', ') },
      { value: this.totalPointsPerOption[recipient] },
      { value: this.averagePointsPerOption[recipient] },
      { value: this.averagePointsExcludingSelf[recipient] },
    ]);
  }
}
