import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextQuestionEditAnswerFormComponent } from './text-question-edit-answer-form.component';
import { FeedbackQuestionType } from '../../../../types/api-request';

describe('TextQuestionEditAnswerFormComponent', () => {
  let component: TextQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<TextQuestionEditAnswerFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(TextQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    component.questionDetails = {
      shouldAllowRichText: false,
      questionType: FeedbackQuestionType.TEXT,
      questionText: 'Sample question',
      recommendedLength: 50,
    };
    component.responseDetails = { answer: '', questionType: FeedbackQuestionType.TEXT };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should decode HTML entities for @ sign in plain-text mode', () => {
    component.questionDetails.shouldAllowRichText = false;
    component.responseDetails.answer = 'Test &#64; symbol';
    fixture.detectChanges();

    expect(component.decodedAnswer).toBe('Test @ symbol');
  });

  it('should decode HTML entities for apostrophe in plain-text mode', () => {
    component.questionDetails.shouldAllowRichText = false;
    component.responseDetails.answer = 'It&#39;s a test';
    fixture.detectChanges();

    expect(component.decodedAnswer).toBe("It's a test");
  });
});
