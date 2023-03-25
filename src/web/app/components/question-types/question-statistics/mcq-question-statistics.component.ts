import { Component, OnChanges, OnInit } from '@angular/core';
import { DEFAULT_MCQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';
import {
  McqQuestionStatisticsCalculation,
} from './question-statistics-calculation/mcq-question-statistics-calculation';

/**
 * Statistics for MCQ questions.
 */
@Component({
  selector: 'tm-mcq-question-statistics',
  templateUrl: './mcq-question-statistics.component.html',
  styleUrls: ['./mcq-question-statistics.component.scss'],
})
export class McqQuestionStatisticsComponent extends McqQuestionStatisticsCalculation implements OnInit, OnChanges {

  // enum
  SortBy: typeof SortBy = SortBy;

  summaryColumnsData: ColumnData[] = [];
  summaryRowsData: SortableTableCellData[][] = [];
  perRecipientColumnsData: ColumnData[] = [];
  perRecipientRowsData: SortableTableCellData[][] = [];

  constructor() {
    super(DEFAULT_MCQ_QUESTION_DETAILS());
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
      { header: 'Choice', sortBy: SortBy.MCQ_CHOICE },
      { header: 'Weight', sortBy: SortBy.MCQ_WEIGHT },
      { header: 'Response Count', sortBy: SortBy.MCQ_RESPONSE_COUNT },
      { header: 'Percentage (%)', sortBy: SortBy.MCQ_PERCENTAGE },
      { header: 'Weighted Percentage (%)', sortBy: SortBy.MCQ_WEIGHTED_PERCENTAGE },
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
      { header: 'Team', sortBy: SortBy.MCQ_TEAM },
      { header: 'Recipient Name', sortBy: SortBy.MCQ_RECIPIENT_NAME },
      ...Object.keys(this.weightPerOption).map((key: string) => {
        return {
          header: `${key}[${(this.weightPerOption[key]).toFixed(2)}]`,
          sortBy: SortBy.MCQ_OPTION_SELECTED_TIMES,
        };
      }),
      { header: 'Total', sortBy: SortBy.MCQ_WEIGHT_TOTAL },
      { header: 'Average', sortBy: SortBy.MCQ_WEIGHT_AVERAGE },
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
        { value: (this.perRecipientResponses[key].total).toFixed(2) },
        { value: (this.perRecipientResponses[key].average).toFixed(2) },
      ];
    });
  }
}
