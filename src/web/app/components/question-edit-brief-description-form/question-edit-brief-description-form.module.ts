import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';

import { QuestionEditBriefDescriptionFormComponent } from './question-edit-brief-description-form.component';

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
