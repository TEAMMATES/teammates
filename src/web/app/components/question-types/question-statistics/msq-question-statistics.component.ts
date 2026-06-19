import { Component, Input, OnChanges } from '@angular/core';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { DEFAULT_MSQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import { FeedbackMsqQuestionDetails, FeedbackMsqResponseDetails } from '../../../../types/api-output';
import { calculateMsqQuestionStatistics } from '../../../utils/question-statistics.util';
import { MsqQuestionStatistics, Response } from '../../../../types/question-statistics.model';

/**
 * Statistics for MSQ questions.
 */
@Component({
  selector: 'tm-msq-question-statistics',
  templateUrl: './msq-question-statistics.component.html',
  imports: [SortableTableComponent, NgbTooltip],
})
export class MsqQuestionStatisticsComponent implements OnChanges {
  @Input()
  question: FeedbackMsqQuestionDetails = DEFAULT_MSQ_QUESTION_DETAILS();
  @Input()
  responses: Response<FeedbackMsqResponseDetails>[] = [];
  @Input()
  isStudent = false;

  // enum
  SortBy!: typeof SortBy;

  summaryColumnsData: ColumnData[] = [];
  summaryRowsData: SortableTableCellData[][] = [];
  perRecipientColumnsData: ColumnData[] = [];
  perRecipientRowsData: SortableTableCellData[][] = [];
  hasAnswers = false;

  constructor() {
    this.SortBy = SortBy;
  }

  ngOnChanges(): void {
    const stats: MsqQuestionStatistics = calculateMsqQuestionStatistics(this.question, this.responses);
    this.hasAnswers = stats.hasAnswers;
    if (!this.hasAnswers) {
      return;
    }

    this.summaryColumnsData = [
      { header: 'Choice', sortBy: SortBy.MSQ_CHOICE },
      { header: 'Weight', sortBy: SortBy.MSQ_WEIGHT },
      { header: 'Response Count', sortBy: SortBy.MSQ_RESPONSE_COUNT },
      { header: 'Percentage (%)', sortBy: SortBy.MSQ_PERCENTAGE },
      { header: 'Weighted Percentage (%)', sortBy: SortBy.MSQ_WEIGHTED_PERCENTAGE },
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
      { header: 'Team', sortBy: SortBy.MSQ_TEAM },
      { header: 'Recipient Name', sortBy: SortBy.MSQ_RECIPIENT_NAME },
      ...Object.keys(stats.weightPerOption).map((key: string) => {
        return {
          header: `${key} [${stats.weightPerOption[key] !== null && stats.weightPerOption[key] !== undefined ? stats.weightPerOption[key].toFixed(2) : '-'}]`,
          sortBy: SortBy.MSQ_OPTION_SELECTED_TIMES,
        };
      }),
      { header: 'Total', sortBy: SortBy.MSQ_WEIGHT_TOTAL },
      { header: 'Average', sortBy: SortBy.MSQ_WEIGHT_AVERAGE },
    ];

    this.perRecipientRowsData = Object.keys(stats.perRecipientResponses).map((key: string) => {
      const recipientEmail: string | undefined = stats.perRecipientResponses[key].recipientEmail;
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
        { value: stats.perRecipientResponses[key].total !== null ? stats.perRecipientResponses[key].total.toFixed(2) : '-' },
        { value: stats.perRecipientResponses[key].average !== null ? stats.perRecipientResponses[key].average.toFixed(2) : '-' },
      ];
    });
  }
}
