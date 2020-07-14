import { Component } from '@angular/core';
import { FeedbackConstantSumQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for constsum options questions.
 */
@Component({
  selector: 'tm-constsum-options-question-additional-info',
  templateUrl: './constsum-options-question-additional-info.component.html',
  styleUrls: ['./constsum-options-question-additional-info.component.scss'],
})
export class ConstsumOptionsQuestionAdditionalInfoComponent
    extends QuestionAdditionalInfo<FeedbackConstantSumQuestionDetails> {

  constructor() {
    super(DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS());
  }

}
