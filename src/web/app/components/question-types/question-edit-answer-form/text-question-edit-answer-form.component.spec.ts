import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TextQuestionEditAnswerFormComponent } from './text-question-edit-answer-form.component';

describe('TextQuestionEditAnswerFormComponent', () => {
  let component: TextQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<TextQuestionEditAnswerFormComponent>;

  beforeEach(async(() => {
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
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
