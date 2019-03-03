import { Component } from '@angular/core';
import {
  FeedbackQuestionType,
  FeedbackRankRecipientsQuestionDetails,
  FeedbackRankRecipientsResponseDetails,
} from '../../../../types/api-output';
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
    super({
      answer: 0,
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    }, {
      minOptionsToBeRanked: Number.MIN_VALUE,
      maxOptionsToBeRanked: Number.MIN_VALUE,
      areDuplicatesAllowed: false,
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      questionText: '',
    });
  }

}
