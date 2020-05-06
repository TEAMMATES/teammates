import { Component, Input } from '@angular/core';
import { FeedbackRankOptionsQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_RANK_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { NO_VALUE } from '../../../../types/feedback-response-details';

/**
 * Instructions for Rank options question.
 */
@Component({
  selector: 'tm-rank-options-question-instruction',
  templateUrl: './rank-options-question-instruction.component.html',
  styleUrls: ['./rank-options-question-instruction.component.scss'],
})
export class RankOptionsQuestionInstructionComponent {

  @Input()
  questionDetails: FeedbackRankOptionsQuestionDetails = DEFAULT_RANK_OPTIONS_QUESTION_DETAILS();

  readonly NO_VALUE: number = NO_VALUE;

  constructor() { }

}
