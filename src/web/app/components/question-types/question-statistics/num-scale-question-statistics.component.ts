import { Component, OnChanges, OnInit } from '@angular/core';
import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_NUMSCALE_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';
import { QuestionStatistics } from './question-statistics';

interface NumericalScaleStatsRowModel {
  teamName: string;
  recipientName: string;
  average: number;
  max: number;
  min: number;
  averageExceptSelf: number;
}

/**
 * Statistics for numerical scale questions.
 */
@Component({
  selector: 'tm-num-scale-question-statistics',
  templateUrl: './num-scale-question-statistics.component.html',
  styleUrls: ['./num-scale-question-statistics.component.scss'],
})
export class NumScaleQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackNumericalScaleQuestionDetails, FeedbackNumericalScaleResponseDetails>
    implements OnInit, OnChanges {

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  teamToRecipientToScoresSortBy: SortBy = SortBy.NONE;
  teamToRecipientToScoresSortOrder: SortOrder = SortOrder.ASC;

  // data
  teamToRecipientToScores: Record<string, Record<string, any>> = {};

  numericalScaleStatsRowModel: NumericalScaleStatsRowModel[] = [];

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

  private calculateStatistics(): void {
    this.teamToRecipientToScores = {};
    this.numericalScaleStatsRowModel = [];

    for (const response of this.responses) {
      const { giver }: { giver: string } = response;
      const { recipient }: { recipient: string } = response;
      const { recipientTeam }: { recipientTeam: string } = response;
      this.teamToRecipientToScores[recipientTeam] = this.teamToRecipientToScores[recipientTeam] || {};
      this.teamToRecipientToScores[recipientTeam][recipient] =
          this.teamToRecipientToScores[recipientTeam][recipient] || { responses: [] };
      this.teamToRecipientToScores[recipientTeam][recipient].responses.push({
        answer: response.responseDetails.answer,
        isSelf: giver === recipient,
      });
    }

    for (const team of Object.keys(this.teamToRecipientToScores)) {
      for (const recipient of Object.keys(this.teamToRecipientToScores[team])) {
        const stats: any = this.teamToRecipientToScores[team][recipient];
        const answersAsArray: number[] = stats.responses.map((resp: any) => resp.answer);
        stats.max = Math.max(...answersAsArray);
        stats.min = Math.min(...answersAsArray);
        stats.average = answersAsArray.reduce((a: number, b: number) => a + b, 0) / answersAsArray.length;

        const answersExcludingSelfAsArray: number[] = stats.responses.filter((resp: any) => !resp.isSelf)
            .map((resp: any) => resp.answer);
        if (answersExcludingSelfAsArray.length) {
          stats.averageExcludingSelf = answersExcludingSelfAsArray.reduce((a: number, b: number) => a + b, 0)
              / answersExcludingSelfAsArray.length;
        } else {
          stats.averageExcludingSelf = 0;
        }
        // copy the data to a sortable structure
        this.numericalScaleStatsRowModel.push({
          teamName: team, recipientName: recipient,
          average: stats.average,
          max: stats.max,
          min: stats.min,
          averageExceptSelf: stats.averageExcludingSelf,
        });
      }
    }
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

    this.rowsData = Object.values(this.numericalScaleStatsRowModel).map((statsRow: NumericalScaleStatsRowModel) => {
      return [
        { value: statsRow.teamName },
        { value: statsRow.recipientName },
        { value: statsRow.average },
        { value: statsRow.max },
        { value: statsRow.min },
        { value: statsRow.averageExceptSelf },
      ];
    });
  }

}
