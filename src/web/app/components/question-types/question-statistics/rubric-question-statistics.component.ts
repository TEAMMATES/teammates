import { Component, OnChanges, OnInit } from '@angular/core';
import {
  FeedbackParticipantType,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_RUBRIC_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../sortable-table/sortable-table.component';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for rubric questions.
 */
@Component({
  selector: 'tm-rubric-question-statistics',
  templateUrl: './rubric-question-statistics.component.html',
  styleUrls: ['./rubric-question-statistics.component.scss'],
})
export class RubricQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackRubricQuestionDetails, FeedbackRubricResponseDetails>
    implements OnInit, OnChanges {

  excludeSelf: boolean = false;

  subQuestions: string[] = [];
  choices: string[] = [];
  hasWeights: boolean = false;
  weights: number[][] = [];
  answers: number[][] = [];
  percentages: number[][] = [];
  answersExcludeSelf: number[][] = [];
  percentagesExcludeSelf: number[][] = [];

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  constructor() {
    super(DEFAULT_RUBRIC_QUESTION_DETAILS());
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
    this.answers = [];
    this.percentages = [];
    this.answersExcludeSelf = [];
    this.percentagesExcludeSelf = [];

    this.subQuestions = this.question.rubricSubQuestions;
    this.choices = this.question.rubricChoices;
    this.hasWeights = this.question.hasAssignedWeights;
    this.weights = this.question.rubricWeightsForEachCell;

    for (const _ of this.question.rubricSubQuestions) {
      const subQuestionAnswers: number[] = [];
      for (const __ of this.question.rubricChoices) {
        subQuestionAnswers.push(0);
      }
      this.answers.push(JSON.parse(JSON.stringify(subQuestionAnswers)));
      this.answersExcludeSelf.push(JSON.parse(JSON.stringify(subQuestionAnswers)));
    }

    const isRecipientTeam: boolean = this.recipientType === FeedbackParticipantType.TEAMS
        || this.recipientType === FeedbackParticipantType.TEAMS_EXCLUDING_SELF;

    for (const response of this.responses) {
      for (let i: number = 0; i < response.responseDetails.answer.length; i += 1) {
        const subAnswer: number = response.responseDetails.answer[i];
        if (subAnswer === -1) {
          continue;
        }
        this.answers[i][subAnswer] += 1;

        if (isRecipientTeam && response.recipient !== response.giver
            || !isRecipientTeam && response.recipientEmail !== response.giverEmail) {
          this.answersExcludeSelf[i][subAnswer] += 1;
        }
      }
    }

    this.percentages = this.calculatePercentages(this.answers);
    this.percentagesExcludeSelf = this.calculatePercentages(this.answersExcludeSelf);
  }

  private calculatePercentages(answers: number[][]): number[][] {
    // Deep-copy the answers
    const percentages: number[][] = JSON.parse(JSON.stringify(answers));
    // console.log(percentages);

    // Apply weights if applicable
    if (this.hasWeights) {
      for (let i: number = 0; i < answers.length; i += 1) {
        for (let j: number = 0; j < answers[i].length; j += 1) {
          percentages[i][j] = percentages[i][j] * this.weights[i][j];
        }
      }
    }

    // Calculate sums for each row
    const sums: number[] = percentages.map((weightedAnswers: number[]) =>
        weightedAnswers.reduce((a: number, b: number) => a + b, 0));

    // Calculate the percentages based on the entry of each cell and the sum of each row
    for (let i: number = 0; i < answers.length; i += 1) {
      for (let j: number = 0; j < answers[i].length; j += 1) {
        percentages[i][j] = sums[i] === 0 ? 0 : Math.round(percentages[i][j] / sums[i] * 100);
      }
    }

    return percentages;
  }

  getTableData(): void {
    this.columnsData = [
        { header: '' },
      ...this.choices.map((choice: string) => ({ header: choice, sortBy: SortBy.RUBRIC_CHOICE })),
    ];

    this.rowsData = this.subQuestions.map((subQuestion: string, questionIndex: number) => {
      return [
        { value: subQuestion },
        ...this.choices.map((_: string, choiceIndex: number) => {
          if (this.excludeSelf) {
            return { value: `${ this.percentagesExcludeSelf[questionIndex][choiceIndex] }% \
            (${ this.answersExcludeSelf[questionIndex][choiceIndex] }) \
            ${ this.hasWeights ? `[${ this.weights[questionIndex][choiceIndex] }]` : '' }` };
          }
          return { value: `${ this.percentages[questionIndex][choiceIndex] }% \
              (${ this.answers[questionIndex][choiceIndex] }) \
              ${ this.hasWeights ? `[${ this.weights[questionIndex][choiceIndex] }]` : '' }` };
        }),
      ];
    });
  }

}
