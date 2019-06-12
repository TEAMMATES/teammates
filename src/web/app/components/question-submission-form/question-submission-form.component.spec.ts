import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { CommentBoxModule } from '../comment-box/comment-box.module';
import { QuestionConstraintModule } from '../question-types/question-constraint/question-constraint.module';
import {
  QuestionEditAnswerFormModule,
} from '../question-types/question-edit-answer-form/question-edit-answer-form.module';
import { QuestionInstructionModule } from '../question-types/question-instruction/question-instruction.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';
import { QuestionSubmissionFormComponent } from './question-submission-form.component';
import { RecipientTypeNamePipe } from './recipient-type-name.pipe';

describe('QuestionSubmissionFormComponent', () => {
  let component: QuestionSubmissionFormComponent;
  let fixture: ComponentFixture<QuestionSubmissionFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        QuestionSubmissionFormComponent,
        RecipientTypeNamePipe,
      ],
      imports: [
        CommentBoxModule,
        HttpClientTestingModule,
        TeammatesCommonModule,
        VisibilityMessagesModule,
        QuestionInstructionModule,
        QuestionConstraintModule,
        QuestionEditAnswerFormModule,
        RichTextEditorModule,
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionSubmissionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
