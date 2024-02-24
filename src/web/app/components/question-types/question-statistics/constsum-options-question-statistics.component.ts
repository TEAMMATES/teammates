import { Component, OnChanges, OnInit } from '@angular/core';
import {
  ConstsumOptionsQuestionStatisticsCalculation,
} from './question-statistics-calculation/constsum-options-question-statistics-calculation';
import { DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';

/**
 * Statistics for constsum options questions.
 */
@Component({
  selector: 'tm-constsum-options-question-statistics',
  templateUrl: './constsum-options-question-statistics.component.html',
  styleUrls: ['./constsum-options-question-statistics.component.scss'],
})
export class ConstsumOptionsQuestionStatisticsComponent
    extends ConstsumOptionsQuestionStatisticsCalculation
    implements OnInit, OnChanges {

  // enum
  SortBy: typeof SortBy = SortBy;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  constructor() {
    super(DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS());
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
      { header: 'Option', sortBy: SortBy.CONSTSUM_OPTIONS_OPTION },
      { header: 'Points Received' },
      { header: 'Total Points', sortBy: SortBy.CONSTSUM_OPTIONS_POINTS },
      { header: 'Average Points', sortBy: SortBy.CONSTSUM_OPTIONS_POINTS },
    ];

    this.rowsData = Object.keys(this.pointsPerOption).map((option: string) => [
      { value: option },
      { value: this.pointsPerOption[option].join(', ') },
      { value: this.totalPointsPerOption[option] },
      { value: this.averagePointsPerOption[option] },
    ]);
  }
}
