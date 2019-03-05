import { Component } from '@angular/core';
import { FeedbackConstantSumQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_CONSTSUM_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for constant sum questions.
 */
@Component({
  selector: 'tm-constsum-question-additional-info',
  templateUrl: './constsum-question-additional-info.component.html',
  styleUrls: ['./constsum-question-additional-info.component.scss'],
})
export class ConstsumQuestionAdditionalInfoComponent
    extends QuestionAdditionalInfo<FeedbackConstantSumQuestionDetails> {

  constructor() {
    super(DEFAULT_CONSTSUM_QUESTION_DETAILS());
  }

}
