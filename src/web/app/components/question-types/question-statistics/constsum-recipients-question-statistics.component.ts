import { Component, Input, OnChanges } from '@angular/core';
import { DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../sortable-table/sortable-table.component';
import {
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackParticipantType,
} from '../../../../types/api-output';
import { calculateConstsumRecipientsQuestionStatistics } from '../../../utils/question-statistics.util';
import { ConstsumRecipientsQuestionStatistics, Response } from '../../../../types/question-statistics.model';

/**
 * Statistics for constsum recipients questions.
 */
@Component({
  selector: 'tm-constsum-recipients-question-statistics',
  templateUrl: './constsum-recipients-question-statistics.component.html',
  imports: [SortableTableComponent],
})
export class ConstsumRecipientsQuestionStatisticsComponent implements OnChanges {
  @Input()
  question: FeedbackConstantSumQuestionDetails = DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS();
  @Input()
  responses: Response<FeedbackConstantSumResponseDetails>[] = [];
  @Input()
  recipientType: FeedbackParticipantType = FeedbackParticipantType.NONE;
  @Input()
  isStudent = false;

  // enum
  SortBy: typeof SortBy = SortBy;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  ngOnChanges(): void {
    const stats = calculateConstsumRecipientsQuestionStatistics(this.responses, this.recipientType);
    this.getTableData(stats);
  }

  private getTableData(stats: ConstsumRecipientsQuestionStatistics): void {
    this.columnsData = [
      { header: 'Team', sortBy: SortBy.TEAM_NAME },
      { header: 'Recipient', sortBy: SortBy.RECIPIENT_NAME },
      { header: 'Points Received' },
      { header: 'Total Points', sortBy: SortBy.CONSTSUM_RECIPIENTS_POINTS },
      { header: 'Average Points', sortBy: SortBy.CONSTSUM_RECIPIENTS_POINTS },
      { header: 'Average Excluding Self', sortBy: SortBy.CONSTSUM_RECIPIENTS_POINTS },
    ];

    this.rowsData = Object.keys(stats.pointsPerOption).map((recipient: string) => [
      { value: stats.emailToTeamName[recipient] },
      {
        value: stats.emailToName[recipient] + (stats.emailToTeamName[recipient] ? ` (${recipient})` : ''),
      },
      { value: stats.pointsPerOption[recipient].join(', ') },
      { value: stats.totalPointsPerOption[recipient] },
      { value: stats.averagePointsPerOption[recipient] },
      { value: stats.averagePointsExcludingSelf[recipient] },
    ]);
  }
}
