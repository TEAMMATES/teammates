import { Component, OnChanges, OnInit } from '@angular/core';
import { DEFAULT_RANK_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';
import {
  RankOptionsQuestionStatisticsCalculation,
} from './question-statistics-calculation/rank-options-question-statistics-calculation';

/**
 * Statistics for rank options questions.
 */
@Component({
  selector: 'tm-rank-options-question-statistics',
  templateUrl: './rank-options-question-statistics.component.html',
  styleUrls: ['./rank-options-question-statistics.component.scss'],
})
export class RankOptionsQuestionStatisticsComponent extends RankOptionsQuestionStatisticsCalculation
    implements OnInit, OnChanges {

  // enum
  SortBy: typeof SortBy = SortBy;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  constructor() {
    super(DEFAULT_RANK_OPTIONS_QUESTION_DETAILS());
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
      { header: 'Option', sortBy: SortBy.RANK_OPTIONS_OPTION },
      { header: 'Ranks Received' },
      { header: 'Overall Rank', sortBy: SortBy.RANK_OPTIONS_OVERALL_RANK },
    ];

    this.rowsData = Object.keys(this.ranksReceivedPerOption).map((key: string) => {
      return [
        { value: key },
        { value: this.ranksReceivedPerOption[key].join(', ') },
        { value: this.rankPerOption[key] ? this.rankPerOption[key] : '' },
      ];
    });
  }

}
