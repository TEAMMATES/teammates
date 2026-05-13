import { Component, Input, OnChanges } from '@angular/core';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import { NumScaleQuestionStatistics, Response } from '../../../../types/question-statistics.model';
import { FeedbackNumericalScaleResponseDetails } from '../../../../types/api-output';
import { calculateNumScaleQuestionStatistics } from '../../../utils/question-statistics.util';

/**
 * Statistics for numerical scale questions.
 */
@Component({
  selector: 'tm-num-scale-question-statistics',
  templateUrl: './num-scale-question-statistics.component.html',
  imports: [SortableTableComponent],
})
export class NumScaleQuestionStatisticsComponent implements OnChanges {
  @Input()
  responses: Response<FeedbackNumericalScaleResponseDetails>[] = [];
  @Input()
  isStudent = false;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  ngOnChanges(): void {
    const stats = calculateNumScaleQuestionStatistics(this.responses);
    this.getTableData(stats);
  }

  private getTableData(stats: NumScaleQuestionStatistics): void {
    this.columnsData = [
      { header: 'Team', sortBy: SortBy.TEAM_NAME },
      { header: 'Recipient', sortBy: SortBy.RECIPIENT_NAME },
      { header: 'Average', sortBy: SortBy.NUMERICAL_SCALE_AVERAGE, headerToolTip: 'Average of the visible responses' },
      { header: 'Max', sortBy: SortBy.NUMERICAL_SCALE_MAX, headerToolTip: 'Maximum of the visible responses' },
      { header: 'Min', sortBy: SortBy.NUMERICAL_SCALE_MIN, headerToolTip: 'Minimum of the visible responses' },
      {
        header: 'Average excluding self response',
        sortBy: SortBy.NUMERICAL_SCALE_AVERAGE_EXCLUDE_SELF,
        headerToolTip: "Average of the visible responses excluding recipient's own response to himself/herself",
      },
    ];

    this.rowsData = [];
    for (const team of Object.keys(stats.teamToRecipientToScores)) {
      for (const recipient of Object.keys(stats.teamToRecipientToScores[team])) {
        const rowStats: any = stats.teamToRecipientToScores[team][recipient];
        const recipientEmail: string = stats.recipientEmails[recipient];
        this.rowsData.push([
          { value: team },
          {
            value: recipient + (recipientEmail ? ` (${recipientEmail})` : ''),
          },
          { value: rowStats.average },
          { value: rowStats.max },
          { value: rowStats.min },
          { value: rowStats.averageExcludingSelf },
        ]);
      }
    }
  }
}
