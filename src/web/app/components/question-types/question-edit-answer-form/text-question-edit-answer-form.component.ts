import { Component } from '@angular/core';

import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';
import {
  FeedbackTextQuestionDetails,
  FeedbackTextResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_TEXT_QUESTION_DETAILS,
  DEFAULT_TEXT_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';

/**
 * The text question submission form for a recipient.
 */
@Component({
  selector: 'tm-text-question-edit-answer-form',
  templateUrl: './text-question-edit-answer-form.component.html',
  styleUrls: ['./text-question-edit-answer-form.component.scss'],
})
export class TextQuestionEditAnswerFormComponent
    extends QuestionEditAnswerFormComponent
        <FeedbackTextQuestionDetails, FeedbackTextResponseDetails> {

  constructor() {
    super(DEFAULT_TEXT_QUESTION_DETAILS(), DEFAULT_TEXT_RESPONSE_DETAILS());
  }

  decodeHtml(html: string): string {
    const txt = document.createElement('textarea');
    txt.innerHTML = html;
    return txt.value;
  }

  get wordCount(): number {
    return this.responseDetails.answer.split(/\s/g)
        .filter((item: string) => item.match(/\w/)).length;
  }

  get isWordCountWithinRecommendedBound(): boolean {
    if (!this.questionDetails.recommendedLength) {
      return true;
    }

    const upperLimit: number = this.questionDetails.recommendedLength + this.questionDetails.recommendedLength * 0.1;
    const lowerLimit: number = this.questionDetails.recommendedLength - this.questionDetails.recommendedLength * 0.1;

    return this.wordCount > lowerLimit && this.wordCount < upperLimit;
  }

  get decodedAnswer(): string {
    return this.decodeHtml(this.responseDetails.answer);
  }
}
