import { Component, EventEmitter, Output } from '@angular/core';

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
  extends QuestionEditAnswerFormComponent<
    FeedbackTextQuestionDetails,
    FeedbackTextResponseDetails
  > {


  emailError = '';
  emailValid = false;


  @Output() validityChange = new EventEmitter<boolean>();

  private readonly EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  constructor() {
    super(DEFAULT_TEXT_QUESTION_DETAILS(), DEFAULT_TEXT_RESPONSE_DETAILS());
  }


  private isEmailQuestion(): boolean {
    const qText = (this.questionDetails?.questionText ?? '').toLowerCase();
    return qText.includes('email') || qText.includes('mail');
  }


  onAnswerChange(value: string): void {
    this.responseDetails.answer = value;

    if (this.isEmailQuestion()) {
      const v = (value ?? '').trim();
      if (!v || !this.EMAIL_REGEX.test(v)) {
        this.emailError = 'Invalid email format';
        this.emailValid = false;
        this.validityChange.emit(false);
      } else {
        this.emailError = '';
        this.emailValid = true;
        this.validityChange.emit(true);
      }
    } else {

      this.emailError = '';
      this.emailValid = true;
      this.validityChange.emit(true);
    }
  }


  isEmailValidForSubmit(): boolean {
    if (!this.isEmailQuestion()) { return true; }
    const v = (this.responseDetails?.answer ?? '').trim();
    return !!v && this.EMAIL_REGEX.test(v);
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

    const upperLimit: number = this.questionDetails.recommendedLength * 1.1;
    const lowerLimit: number = this.questionDetails.recommendedLength * 0.9;

    return this.wordCount > lowerLimit && this.wordCount < upperLimit;
  }

  get decodedAnswer(): string {
    return this.decodeHtml(this.responseDetails.answer);
  }
}