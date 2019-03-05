import { Component } from '@angular/core';
import { FeedbackRankRecipientsQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for rank recipients questions.
 */
@Component({
  selector: 'tm-rank-recipients-question-additional-info',
  templateUrl: './rank-recipients-question-additional-info.component.html',
  styleUrls: ['./rank-recipients-question-additional-info.component.scss'],
})
export class RankRecipientsQuestionAdditionalInfoComponent
    extends QuestionAdditionalInfo<FeedbackRankRecipientsQuestionDetails> {

  constructor() {
    super(DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS());
  }

}
