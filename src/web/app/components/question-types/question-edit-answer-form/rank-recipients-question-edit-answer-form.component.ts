import { Component, Input, OnInit } from '@angular/core';
import {
  FeedbackRankRecipientsQuestionDetails,
  FeedbackRankRecipientsResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS, DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED } from '../../../../types/feedback-response-details';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';

/**
 * The Rank recipients question submission form for a recipient.
 */
@Component({
  selector: 'tm-rank-recipients-question-edit-answer-form',
  templateUrl: './rank-recipients-question-edit-answer-form.component.html',
  styleUrls: ['./rank-recipients-question-edit-answer-form.component.scss'],
})
export class RankRecipientsQuestionEditAnswerFormComponent
    extends
        QuestionEditAnswerFormComponent<FeedbackRankRecipientsQuestionDetails, FeedbackRankRecipientsResponseDetails>
    implements OnInit {

  readonly RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED: number = RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED;

  @Input()
  numOfRecipients: number = 0;

  constructor() {
    super(DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS(), DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS());
  }

  ngOnInit(): void {
  }

  /**
   * Populates the possible Ranks that can be assigned.
   */
  get ranksToBeAssigned(): number[] {
    const ranks: number[] = [];
    for (let i: number = 1; i <= this.numOfRecipients; i += 1) {
      ranks.push(i);
    }
    return ranks;
  }

}
