import { Component } from '@angular/core';
import { FeedbackConstantSumQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for constsum recipients questions.
 */
@Component({
  selector: 'tm-constsum-recipients-question-additional-info',
  templateUrl: './constsum-recipients-question-additional-info.component.html',
  styleUrls: ['./constsum-recipients-question-additional-info.component.scss'],
})
export class ConstsumRecipientsQuestionAdditionalInfoComponent
    extends QuestionAdditionalInfo<FeedbackConstantSumQuestionDetails> {

  constructor() {
    super(DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS());
  }

}
