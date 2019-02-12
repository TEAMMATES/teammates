import { Component } from '@angular/core';

import {
  FeedbackQuestionType,
  FeedbackTextQuestionDetails,
  FeedbackTextResponseDetails,
} from '../../../../../../types/api-output';
import { BasicRecipientSubmissionFormComponent } from '../basic-recipient-submission-form';

/**
 * The text question submission form for a recipient.
 */
@Component({
  selector: 'tm-text-recipient-submission-form',
  templateUrl: './text-recipient-submission-form.component.html',
  styleUrls: ['./text-recipient-submission-form.component.scss'],
})
export class TextRecipientSubmissionFormComponent
    extends BasicRecipientSubmissionFormComponent
        <FeedbackTextQuestionDetails, FeedbackTextResponseDetails> {

  constructor() {
    super({
      recommendedLength: 0,
      questionType: FeedbackQuestionType.TEXT,
      questionText: '',
    }, {
      answer: '',
      questionType: FeedbackQuestionType.TEXT,
    });
  }

  get wordCount(): number {
    return this.responseDetails.answer.split(/\s/g)
        .filter((item: string) => item.match(/\w/)).length;
  }

  get isWordCountWithinRecommendedBound(): boolean {
    if (this.questionDetails.recommendedLength === 0) {
      // not recommended length set
      return true;
    }

    const upperLimit: number = this.questionDetails.recommendedLength + this.questionDetails.recommendedLength * 0.1;
    const lowerLimit: number = this.questionDetails.recommendedLength - this.questionDetails.recommendedLength * 0.1;

    return this.wordCount > lowerLimit && this.wordCount < upperLimit;
  }
}
