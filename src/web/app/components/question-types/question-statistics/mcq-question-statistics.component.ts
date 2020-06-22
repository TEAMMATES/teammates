import { Component, OnChanges, OnInit } from '@angular/core';
import { FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails } from '../../../../types/api-output';
import { DEFAULT_MCQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for MCQ questions.
 */
@Component({
  selector: 'tm-mcq-question-statistics',
  templateUrl: './mcq-question-statistics.component.html',
  styleUrls: ['./mcq-question-statistics.component.scss'],
})
export class McqQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails>
    implements OnInit, OnChanges {

  answerFrequency: Record<string, number> = {};
  percentagePerOption: Record<string, number> = {};
  weightPerOption: Record<string, number> = {};
  weightedPercentagePerOption: Record<string, number> = {};
  perRecipientResponses: Record<string, any> = {};

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

  private calculateStatistics(): void {
    this.answerFrequency = {};
    this.percentagePerOption = {};
    this.weightPerOption = {};
    this.weightedPercentagePerOption = {};
    this.perRecipientResponses = {};

    for (const answer of this.question.mcqChoices) {
      this.answerFrequency[answer] = 0;
    }
    if (this.question.otherEnabled) {
      this.answerFrequency.Other = 0;
    }
    for (const response of this.responses) {
      const isOther: boolean = response.responseDetails.isOther;
      const key: string = isOther ? 'Other' : response.responseDetails.answer;
      this.answerFrequency[key] = (this.answerFrequency[key] || 0) + 1;
    }

    if (this.question.hasAssignedWeights) {
      for (let i: number = 0; i < this.question.mcqChoices.length; i += 1) {
        const option: string = this.question.mcqChoices[i];
        const weight: number = this.question.mcqWeights[i];
        this.weightPerOption[option] = weight;
      }
      if (this.question.otherEnabled) {
        this.weightPerOption.Other = this.question.mcqOtherWeight;
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
      const percentage: number = 100 * this.answerFrequency[answer] / this.responses.length;
      this.percentagePerOption[answer] = +percentage.toFixed(2);
    }

    if (this.question.hasAssignedWeights) {
      const perRecipientResponse: Record<string, Record<string, number>> = {};
      const recipientToTeam: Record<string, string> = {};
      for (const response of this.responses) {
        perRecipientResponse[response.recipient] = perRecipientResponse[response.recipient] || {};
        perRecipientResponse[response.recipient][response.responseDetails.answer] = 0;
        if (this.question.otherEnabled) {
          perRecipientResponse[response.recipient].Other = 0;
        }
        recipientToTeam[response.recipient] = response.recipientTeam;
      }
      for (const response of this.responses) {
        const isOther: boolean = response.responseDetails.isOther;
        const answer: string = isOther ? 'Other' : response.responseDetails.answer;
        perRecipientResponse[response.recipient][answer] = perRecipientResponse[response.recipient][answer] + 1;
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
      { header: 'Choice', sortBy: SortBy.MCQ_CHOICE },
      { header: 'Weight', sortBy: SortBy.MCQ_WEIGHT },
      { header: 'Response Count', sortBy: SortBy.MCQ_RESPONSE_COUNT },
      { header: 'Percentage (%)', sortBy: SortBy.MCQ_PERCENTAGE },
      { header: 'Weighted Percentage (%)', sortBy: SortBy.MCQ_WEIGHTED_PERCENTAGE },
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
      { header: 'Team', sortBy: SortBy.MCQ_TEAM },
      { header: 'Recipient Name', sortBy: SortBy.MCQ_RECIPIENT_NAME },
      ...Object.keys(this.weightPerOption).map((key: string) => {
        return {
          header: `${key} [${this.weightPerOption[key]}]`,
          sortBy: SortBy.MCQ_OPTION_SELECTED_TIMES,
        };
      }),
      { header: 'Total', sortBy: SortBy.MCQ_WEIGHT_TOTAL },
      { header: 'Average', sortBy: SortBy.MCQ_WEIGHT_AVERAGE },
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
