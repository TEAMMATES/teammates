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
      { header: 'Team', sortBy: SortBy.MCQ_CHOICE },
      { header: 'Recipient', sortBy: SortBy.MCQ_WEIGHT },
      { header: 'Average', sortBy: SortBy.MCQ_RESPONSE_COUNT, headerToolTip: 'Average of the visible responses' },
      { header: 'Max', sortBy: SortBy.MCQ_PERCENTAGE, headerToolTip: 'Maximum of the visible responses' },
      { header: 'Min', sortBy: SortBy.MCQ_WEIGHTED_PERCENTAGE, headerToolTip: 'Minimum of the visible responses' },
      { header: 'Average excluding self response', sortBy: SortBy.MCQ_WEIGHTED_PERCENTAGE,
        headerToolTip: 'Average of the visible responses excluding recipient\'s own response to himself/herself'},
    ];

    this.rowsData = [];
    for (const team of Object.keys(this.teamToRecipientToScores)) {
      for (const recipient of Object.keys(this.teamToRecipientToScores[team])) {
        const stats: any = this.teamToRecipientToScores[team][recipient];
        this.rowsData.push([
          { value: team },
          { value: recipient },
          { value: stats.average },
          { value: stats.max },
          { value: stats.min },
          { value: stats.averageExcludingSelf },
        ]);
      }
    }
  }

}
