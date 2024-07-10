import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { TextQuestionEditAnswerFormComponent } from './text-question-edit-answer-form.component';
import { FeedbackQuestionType } from '../../../../types/api-request';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';

describe('TextQuestionEditAnswerFormComponent', () => {
  let component: TextQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<TextQuestionEditAnswerFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [TextQuestionEditAnswerFormComponent],
      imports: [
        FormsModule,
        RichTextEditorModule,
      ],
    })
    .compileComponents();
  }));

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
