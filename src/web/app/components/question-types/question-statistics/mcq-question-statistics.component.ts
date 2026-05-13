import { Component, Input, OnChanges } from '@angular/core';
import { DEFAULT_MCQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import { FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails } from '../../../../types/api-output';
import { calculateMcqQuestionStatistics } from '../../../utils/question-statistics.util';
import { McqQuestionStatistics, Response } from '../../../../types/question-statistics.model';

/**
 * Statistics for MCQ questions.
 */
@Component({
  selector: 'tm-mcq-question-statistics',
  templateUrl: './mcq-question-statistics.component.html',
  imports: [SortableTableComponent],
})
export class McqQuestionStatisticsComponent implements OnChanges {
  @Input()
  question: FeedbackMcqQuestionDetails = DEFAULT_MCQ_QUESTION_DETAILS();
  @Input()
  responses: Response<FeedbackMcqResponseDetails>[] = [];
  @Input()
  isStudent = false;

  // enum
  SortBy: typeof SortBy = SortBy;

  summaryColumnsData: ColumnData[] = [];
  summaryRowsData: SortableTableCellData[][] = [];
  perRecipientColumnsData: ColumnData[] = [];
  perRecipientRowsData: SortableTableCellData[][] = [];

  ngOnChanges(): void {
    const stats = calculateMcqQuestionStatistics(this.question, this.responses);
    this.getTableData(stats);
  }

  private getTableData(stats: McqQuestionStatistics): void {
    this.summaryColumnsData = [
      { header: 'Choice', sortBy: SortBy.MCQ_CHOICE },
      { header: 'Weight', sortBy: SortBy.MCQ_WEIGHT },
      { header: 'Response Count', sortBy: SortBy.MCQ_RESPONSE_COUNT },
      { header: 'Percentage (%)', sortBy: SortBy.MCQ_PERCENTAGE },
      { header: 'Weighted Percentage (%)', sortBy: SortBy.MCQ_WEIGHTED_PERCENTAGE },
    ];

    this.summaryRowsData = Object.keys(stats.answerFrequency).map((key: string) => {
      return [
        { value: key },
        { value: stats.weightPerOption[key] === 0 ? 0 : stats.weightPerOption[key] || '-' },
        { value: stats.answerFrequency[key] },
        { value: stats.percentagePerOption[key] },
        { value: stats.weightedPercentagePerOption[key] === 0 ? 0 : stats.weightedPercentagePerOption[key] || '-' },
      ];
    });

    this.perRecipientColumnsData = [
      { header: 'Team', sortBy: SortBy.MCQ_TEAM },
      { header: 'Recipient Name', sortBy: SortBy.MCQ_RECIPIENT_NAME },
      ...Object.keys(stats.weightPerOption).map((key: string) => {
        return {
          header: `${key}[${stats.weightPerOption[key].toFixed(2)}]`,
          sortBy: SortBy.MCQ_OPTION_SELECTED_TIMES,
        };
      }),
      { header: 'Total', sortBy: SortBy.MCQ_WEIGHT_TOTAL },
      { header: 'Average', sortBy: SortBy.MCQ_WEIGHT_AVERAGE },
    ];

    this.perRecipientRowsData = Object.keys(stats.perRecipientResponses).map((key: string) => {
      const recipientEmail: string = stats.perRecipientResponses[key].recipientEmail;
      return [
        { value: stats.perRecipientResponses[key].recipientTeam },
        {
          value: stats.perRecipientResponses[key].recipient + (recipientEmail ? ` (${recipientEmail})` : ''),
        },
        ...Object.keys(stats.weightPerOption).map((option: string) => {
          return {
            value: stats.perRecipientResponses[key].responses[option],
          };
        }),
        { value: stats.perRecipientResponses[key].total.toFixed(2) },
        { value: stats.perRecipientResponses[key].average.toFixed(2) },
      ];
    });
  }
}
