import { Component, OnChanges, OnInit } from '@angular/core';
import { DEFAULT_MSQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';
import {
  MsqQuestionStatisticsCalculation,
} from './question-statistics-calculation/msq-question-statistics-calculation';

/**
 * Statistics for MSQ questions.
 */
@Component({
  selector: 'tm-msq-question-statistics',
  templateUrl: './msq-question-statistics.component.html',
  styleUrls: ['./msq-question-statistics.component.scss'],
})
export class MsqQuestionStatisticsComponent extends MsqQuestionStatisticsCalculation implements OnInit, OnChanges {

  // enum
  SortBy: typeof SortBy = SortBy;

  summaryColumnsData: ColumnData[] = [];
  summaryRowsData: SortableTableCellData[][] = [];
  perRecipientColumnsData: ColumnData[] = [];
  perRecipientRowsData: SortableTableCellData[][] = [];

  constructor() {
    super(DEFAULT_MSQ_QUESTION_DETAILS());
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
    this.summaryColumnsData = [
      { header: 'Choice', sortBy: SortBy.MSQ_CHOICE },
      { header: 'Weight', sortBy: SortBy.MSQ_WEIGHT },
      { header: 'Response Count', sortBy: SortBy.MSQ_RESPONSE_COUNT },
      { header: 'Percentage (%)', sortBy: SortBy.MSQ_PERCENTAGE },
      { header: 'Weighted Percentage (%)', sortBy: SortBy.MSQ_WEIGHTED_PERCENTAGE },
    ];

    this.summaryRowsData = Object.keys(this.answerFrequency).map((key: string) => {
      return [
        { value: key },
        { value: this.weightPerOption[key] === 0 ? 0 : (this.weightPerOption[key] || '-') },
        { value: this.answerFrequency[key] },
        { value: this.percentagePerOption[key] },
        { value: this.weightedPercentagePerOption[key] === 0 ? 0 : (this.weightedPercentagePerOption[key] || '-') },
      ];
    });

    this.perRecipientColumnsData = [
      { header: 'Team', sortBy: SortBy.MSQ_TEAM },
      { header: 'Recipient Name', sortBy: SortBy.MSQ_RECIPIENT_NAME },
      ...Object.keys(this.weightPerOption).map((key: string) => {
        return {
          header: `${key} [${this.weightPerOption[key]}]`,
          sortBy: SortBy.MSQ_OPTION_SELECTED_TIMES,
        };
      }),
      { header: 'Total', sortBy: SortBy.MSQ_WEIGHT_TOTAL },
      { header: 'Average', sortBy: SortBy.MSQ_WEIGHT_AVERAGE },
    ];

    this.perRecipientRowsData = Object.keys(this.perRecipientResponses).map((key: string) => {
      const recipientEmail: string = this.perRecipientResponses[key].recipientEmail;
      return [
        { value: this.perRecipientResponses[key].recipientTeam },
        {
          value: this.perRecipientResponses[key].recipient
          + (recipientEmail ? ` (${recipientEmail})` : ''),
        },
        ...Object.keys(this.weightPerOption).map((option: string) => {
          return {
            value: this.perRecipientResponses[key].responses[option],
          };
        }),
        { value: this.perRecipientResponses[key].total },
        { value: this.perRecipientResponses[key].average },
      ];
    });
  }

}
