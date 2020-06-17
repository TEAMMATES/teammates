import { Component, OnChanges, OnInit } from '@angular/core';
import { FeedbackMsqQuestionDetails, FeedbackMsqResponseDetails } from '../../../../types/api-output';
import { DEFAULT_MSQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for MSQ questions.
 */
@Component({
  selector: 'tm-msq-question-statistics',
  templateUrl: './msq-question-statistics.component.html',
  styleUrls: ['./msq-question-statistics.component.scss'],
})
export class MsqQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackMsqQuestionDetails, FeedbackMsqResponseDetails>
    implements OnInit, OnChanges {

  answerFrequency: Record<string, number> = {};
  percentagePerOption: Record<string, number> = {};
  weightPerOption: Record<string, number> = {};
  weightedPercentagePerOption: Record<string, number> = {};
  perRecipientResponses: Record<string, any> = {};
  hasAnswers: boolean = false;

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

  private calculateStatistics(): void {
    this.answerFrequency = {};
    this.percentagePerOption = {};
    this.weightPerOption = {};
    this.weightedPercentagePerOption = {};
    this.perRecipientResponses = {};

    let numOfAnswers: number = 0;
    for (const answer of this.question.msqChoices) {
      this.answerFrequency[answer] = 0;
    }
    if (this.question.otherEnabled) {
      this.answerFrequency.Other = 0;
    }
    for (const response of this.responses) {
      const isOther: boolean = response.responseDetails.isOther;
      if (isOther) {
        this.answerFrequency.Other = (this.answerFrequency.Other || 0) + 1;
        numOfAnswers += 1;
      } else {
        for (const key of response.responseDetails.answers) {
          this.answerFrequency[key] = (this.answerFrequency[key] || 0) + 1;
          numOfAnswers += 1;
        }
      }
    }
    this.hasAnswers = !!numOfAnswers;
    if (!this.hasAnswers) {
      return;
    }

    if (this.question.hasAssignedWeights) {
      for (let i: number = 0; i < this.question.msqChoices.length; i += 1) {
        const option: string = this.question.msqChoices[i];
        const weight: number = this.question.msqWeights[i];
        this.weightPerOption[option] = weight;
      }
      if (this.question.otherEnabled) {
        this.weightPerOption.Other = this.question.msqOtherWeight;
      }

      let totalWeightedResponseCount: number = 0;
      for (const answer of Object.keys(this.answerFrequency)) {
        const weight: number = this.weightPerOption[answer];
        const weightedAnswer: number = weight * this.answerFrequency[answer];
        totalWeightedResponseCount += weightedAnswer;
      }

      for (const answer of Object.keys(this.weightPerOption)) {
        const weight: number = this.weightPerOption[answer];
        const frequency: number = this.answerFrequency[answer];
        const weightedPercentage: number = totalWeightedResponseCount ? 0
            : 100 * ((frequency - weight) / totalWeightedResponseCount);
        this.weightedPercentagePerOption[answer] = +weightedPercentage.toFixed(2);
      }
    }

    for (const answer of Object.keys(this.answerFrequency)) {
      const percentage: number = numOfAnswers ? 100 * this.answerFrequency[answer] / numOfAnswers : 0;
      this.percentagePerOption[answer] = +percentage.toFixed(2);
    }

    if (this.question.hasAssignedWeights) {
      const perRecipientResponse: Record<string, Record<string, number>> = {};
      const recipientToTeam: Record<string, string> = {};
      for (const response of this.responses) {
        perRecipientResponse[response.recipient] = perRecipientResponse[response.recipient] || {};
        for (const answer of response.responseDetails.answers) {
          perRecipientResponse[response.recipient][answer] = 0;
        }
        if (this.question.otherEnabled) {
          perRecipientResponse[response.recipient].Other = 0;
        }
        recipientToTeam[response.recipient] = response.recipientTeam;
      }
      for (const response of this.responses) {
        const isOther: boolean = response.responseDetails.isOther;
        if (isOther) {
          perRecipientResponse[response.recipient].Other = perRecipientResponse[response.recipient].Other + 1;
        } else {
          for (const answer of response.responseDetails.answers) {
            perRecipientResponse[response.recipient][answer] = perRecipientResponse[response.recipient][answer] + 1;
          }
        }
      }

      for (const recipient of Object.keys(perRecipientResponse)) {
        const responses: Record<string, number> = perRecipientResponse[recipient];
        let total: number = 0;
        let average: number = 0;
        let numOfResponsesForRecipient: number = 0;
        for (const answer of Object.keys(responses)) {
          const responseCount: number = responses[answer];
          const weight: number = this.weightPerOption[answer];
          total += responseCount * weight;
          numOfResponsesForRecipient += responseCount;
        }
        average = numOfResponsesForRecipient ? total / numOfResponsesForRecipient : 0;

        this.perRecipientResponses[recipient] = {
          recipient,
          total,
          average,
          recipientTeam: recipientToTeam[recipient],
          responses: perRecipientResponse[recipient],
        };
      }
    }
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
        { value: this.weightPerOption[key] || '-' },
        { value: this.answerFrequency[key] },
        { value: this.percentagePerOption[key] },
        { value: this.weightedPercentagePerOption[key] || '-' },
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
      return [
        { value: this.perRecipientResponses[key].recipientTeam },
        { value: this.perRecipientResponses[key].recipient },
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
