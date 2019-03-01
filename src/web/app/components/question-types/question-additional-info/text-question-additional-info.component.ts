import { Component } from '@angular/core';
import {
  FeedbackQuestionType,
  FeedbackTextQuestionDetails,
} from '../../../../types/api-output';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for text questions.
 */
@Component({
  selector: 'tm-text-question-additional-info',
  templateUrl: './text-question-additional-info.component.html',
  styleUrls: ['./text-question-additional-info.component.scss'],
})
export class TextQuestionAdditionalInfoComponent extends QuestionAdditionalInfo<FeedbackTextQuestionDetails> {

  constructor() {
    super({
      recommendedLength: 0,
      questionType: FeedbackQuestionType.TEXT,
      questionText: '',
    });
  }

}
