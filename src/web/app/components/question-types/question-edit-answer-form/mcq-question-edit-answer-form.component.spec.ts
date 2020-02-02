import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { McqQuestionEditAnswerFormComponent } from './mcq-question-edit-answer-form.component';

describe('McqQuestionEditAnswerFormComponent', () => {
  let component: McqQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<McqQuestionEditAnswerFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [McqQuestionEditAnswerFormComponent],
      imports: [
        FormsModule,
        RichTextEditorModule,
        TeammatesCommonModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
