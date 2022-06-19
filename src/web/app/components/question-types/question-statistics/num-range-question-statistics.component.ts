import { Component, OnChanges, OnInit } from '@angular/core';
import { DEFAULT_NUMRANGE_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';
import { NumRangeQuestionStatisticsCalculation } from './question-statistics-calculation/num-range-question-statistics-calculation';


@Component({
  selector: 'tm-num-range-question-statistics',
  templateUrl: './num-range-question-statistics.component.html',
  styleUrls: ['./num-range-question-statistics.component.scss']
})
export class NumRangeQuestionStatisticsComponent extends NumRangeQuestionStatisticsCalculation
    implements OnInit, OnChanges {

      columnsData: ColumnData[] = [];
      rowsData: SortableTableCellData[][] = [];
    
      constructor() {
        super(DEFAULT_NUMRANGE_QUESTION_DETAILS());
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
          { header: 'Average Start', sortBy: SortBy.NUMERICAL_SCALE_AVERAGE, headerToolTip: 'Average Start of the visible responses' },
          { header: 'Max Start', sortBy: SortBy.NUMERICAL_SCALE_MAX, headerToolTip: 'Maximum Start of the visible responses' },
          { header: 'Min Start', sortBy: SortBy.NUMERICAL_SCALE_MIN, headerToolTip: 'Minimum Start of the visible responses' },
          { header: 'Average End', sortBy: SortBy.NUMERICAL_SCALE_AVERAGE, headerToolTip: 'Average End of the visible responses' },
          { header: 'Max End', sortBy: SortBy.NUMERICAL_SCALE_MAX, headerToolTip: 'Maximum End of the visible responses' },
          { header: 'Min End', sortBy: SortBy.NUMERICAL_SCALE_MIN, headerToolTip: 'Minimum End of the visible responses' },
          {
            header: 'Average Start excluding self response',
            sortBy: SortBy.NUMERICAL_SCALE_AVERAGE_EXCLUDE_SELF,
            headerToolTip: 'Average Start of the visible responses excluding recipient\'s own response to himself/herself',
          },
          {
            header: 'Average End excluding self response',
            sortBy: SortBy.NUMERICAL_SCALE_AVERAGE_EXCLUDE_SELF,
            headerToolTip: 'Average End of the visible responses excluding recipient\'s own response to himself/herself',
          },
        ];
    
        this.rowsData = [];
        for (const team of Object.keys(this.teamToRecipientToScores)) {
          for (const recipient of Object.keys(this.teamToRecipientToScores[team])) {
            const stats: any = this.teamToRecipientToScores[team][recipient];
            this.rowsData.push([
              { value: team },
              { value: recipient },
              { value: stats.averageStart },
              { value: stats.maxStart },
              { value: stats.minStart },
              { value: stats.averageEnd },
              { value: stats.maxEnd },
              { value: stats.minEnd },
              { value: stats.averageStartExcludingSelf },
              { value: stats.averageEndExcludingSelf },
            ]);
          }
        }
      }
}
