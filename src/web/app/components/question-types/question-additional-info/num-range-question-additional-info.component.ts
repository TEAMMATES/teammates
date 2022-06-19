import { Component } from '@angular/core';
import { FeedbackNumericalRangeQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_NUMRANGE_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionAdditionalInfo } from './question-additional-info';

@Component({
  selector: 'tm-num-range-question-additional-info',
  templateUrl: './num-range-question-additional-info.component.html',
  styleUrls: ['./num-range-question-additional-info.component.scss']
})
export class NumRangeQuestionAdditionalInfoComponent
    extends QuestionAdditionalInfo<FeedbackNumericalRangeQuestionDetails> {

  constructor() {
    super(DEFAULT_NUMRANGE_QUESTION_DETAILS())
   }

}
