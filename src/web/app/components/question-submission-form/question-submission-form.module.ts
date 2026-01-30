import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { QuestionSubmissionFormComponent } from './question-submission-form.component';
import { RecipientTypeNamePipe } from './recipient-type-name.pipe';

import { CommentBoxModule } from '../comment-box/comment-box.module';



import {
  QuestionEditAnswerFormModule,
} from '../question-types/question-edit-answer-form/question-edit-answer-form.module';

import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';



/**
 * Module for all question submissions UI in session submissions page.
 */
@NgModule({
  imports: [
    CommonModule,
    NgbTooltipModule,
    FormsModule,
    RichTextEditorModule,
    QuestionEditAnswerFormModule,
    CommentBoxModule,
    QuestionSubmissionFormComponent,
    RecipientTypeNamePipe,
],
  exports: [
    QuestionSubmissionFormComponent,
  ],
})
export class QuestionSubmissionFormModule { }
