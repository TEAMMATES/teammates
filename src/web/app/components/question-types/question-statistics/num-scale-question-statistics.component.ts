import { Component, OnChanges, OnInit } from '@angular/core';
import { DEFAULT_NUMSCALE_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';
import {
  NumScaleQuestionStatisticsCalculation,
} from './question-statistics-calculation/num-scale-question-statistics-calculation';

/**
 * Statistics for numerical scale questions.
 */
@Component({
  selector: 'tm-num-scale-question-statistics',
  templateUrl: './num-scale-question-statistics.component.html',
  styleUrls: ['./num-scale-question-statistics.component.scss'],
})
export class NumScaleQuestionStatisticsComponent extends NumScaleQuestionStatisticsCalculation
    implements OnInit, OnChanges {

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  constructor() {
    super(DEFAULT_NUMSCALE_QUESTION_DETAILS());
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
      { header: 'Average', sortBy: SortBy.NUMERICAL_SCALE_AVERAGE, headerToolTip: 'Average of the visible responses' },
      { header: 'Max', sortBy: SortBy.NUMERICAL_SCALE_MAX, headerToolTip: 'Maximum of the visible responses' },
      { header: 'Min', sortBy: SortBy.NUMERICAL_SCALE_MIN, headerToolTip: 'Minimum of the visible responses' },
      {
        header: 'Average excluding self response',
        sortBy: SortBy.NUMERICAL_SCALE_AVERAGE_EXCLUDE_SELF,
        headerToolTip: 'Average of the visible responses excluding recipient\'s own response to himself/herself',
      },
    ];

    this.rowsData = [];
    for (const team of Object.keys(this.teamToRecipientToScores)) {
      for (const recipient of Object.keys(this.teamToRecipientToScores[team])) {
        const stats: any = this.teamToRecipientToScores[team][recipient];
        const recipientEmail: string = this.recipientEmails[recipient];
        this.rowsData.push([
          { value: team },
          {
            value: recipient
            + (recipientEmail ? ` (${recipientEmail})` : ''),
          },
          { value: stats.average },
          { value: stats.max },
          { value: stats.min },
          { value: stats.averageExcludingSelf },
        ]);
      }
    }
  }

}
