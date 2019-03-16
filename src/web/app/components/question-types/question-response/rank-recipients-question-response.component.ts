import { Component } from '@angular/core';
import {
  FeedbackRankRecipientsQuestionDetails,
  FeedbackRankRecipientsResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS,
  DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { QuestionResponse } from './question-response';

/**
 * Rank recipients question response.
 */
@Component({
  selector: 'tm-rank-recipients-question-response',
  templateUrl: './rank-recipients-question-response.component.html',
  styleUrls: ['./rank-recipients-question-response.component.scss'],
})
export class RankRecipientsQuestionResponseComponent
    extends QuestionResponse<FeedbackRankRecipientsResponseDetails, FeedbackRankRecipientsQuestionDetails> {

  constructor() {
    super(DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS(), DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS());
  }

}
