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

@Component({
  selector: 'tm-text-question-edit-answer-form',
  templateUrl: './text-question-edit-answer-form.component.html',
  styleUrls: ['./text-question-edit-answer-form.component.scss'],
})
export class TextQuestionEditAnswerFormComponent
  extends QuestionEditAnswerFormComponent<FeedbackTextQuestionDetails, FeedbackTextResponseDetails> {

  /** Emit validity to parent QuestionSubmissionFormComponent */
  @Output() validityChange = new EventEmitter<boolean>();

  /** UI state for email validation hints */
  emailError: string = '';
  emailValid: boolean = false;


  private readonly emailRegex: RegExp = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  constructor() {
    super(DEFAULT_TEXT_QUESTION_DETAILS(), DEFAULT_TEXT_RESPONSE_DETAILS());
  }

  /** decode helper for plain text area */
  decodeHtml(html: string): string {
    const txt = document.createElement('textarea');
    txt.innerHTML = html;
    return txt.value;
  }

  get decodedAnswer(): string {
    return this.decodeHtml(this.responseDetails.answer);
  }

  get wordCount(): number {
    return this.responseDetails.answer
      .split(/\s/g)
      .filter((t: string) => /\w/.test(t)).length;
  }

  get isWordCountWithinRecommendedBound(): boolean {
    if (!this.questionDetails.recommendedLength) { return true; }
    const n = this.questionDetails.recommendedLength;
    return this.wordCount > n * 0.9 && this.wordCount < n * 1.1;
  }


  private isEmailQuestion(): boolean {
    return (this.questionDetails?.questionText ?? '')
      .toLowerCase()
      .includes('email');
  }


  onAnswerChange(value: string): void {
    if (!this.isEmailQuestion()) {

      this.emailError = '';
      this.emailValid = true;
      this.validityChange.emit(true);
      return;
    }

    const trimmed = (value ?? '').trim();

    if (!trimmed) {
      this.emailValid = false;
      this.emailError = 'Email is required';
      this.validityChange.emit(false);
      return;
    }

    if (!this.emailRegex.test(trimmed)) {
      this.emailValid = false;
      this.emailError = 'Invalid email format';
      this.validityChange.emit(false);
      return;
    }

    this.emailValid = true;
    this.emailError = '';
    this.validityChange.emit(true);
  }
}