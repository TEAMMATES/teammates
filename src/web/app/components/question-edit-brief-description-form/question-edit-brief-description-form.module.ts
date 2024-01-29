import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { QuestionEditBriefDescriptionFormComponent } from './question-edit-brief-description-form.component';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';

/**
 * Question brief and description edit form module.
 */
@NgModule({
  imports: [
    CommonModule,
    RichTextEditorModule,
    FormsModule,
  ],
  declarations: [
    QuestionEditBriefDescriptionFormComponent,
  ],
  exports: [
    QuestionEditBriefDescriptionFormComponent,
  ],
})
export class QuestionEditBriefDescriptionFormModule { }
