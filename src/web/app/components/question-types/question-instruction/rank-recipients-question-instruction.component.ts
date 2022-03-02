import { Component, Input } from '@angular/core';
import { FeedbackRankRecipientsQuestionDetails } from '../../../../types/api-output';
import {
  DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS,
} from '../../../../types/default-question-structs';
import { NO_VALUE } from '../../../../types/feedback-response-details';

/**
 * Instructions for Rank recipients question.
 */
@Component({
  selector: 'tm-rank-recipients-question-instruction',
  templateUrl: './rank-recipients-question-instruction.component.html',
  styleUrls: ['./rank-recipients-question-instruction.component.scss'],
})
export class RankRecipientsQuestionInstructionComponent {

  @Input()
  questionDetails: FeedbackRankRecipientsQuestionDetails = DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS();

  readonly NO_VALUE: number = NO_VALUE;

}
