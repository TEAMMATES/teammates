import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { ContributionPointDescriptionPipe } from './contribution-point-description.pipe';
import { ContributionQuestionEditAnswerFormComponent } from './contribution-question-edit-answer-form.component';
import { McqQuestionEditAnswerFormComponent } from './mcq-question-edit-answer-form.component';
import { MsqQuestionEditAnswerFormComponent } from './msq-question-edit-answer-form.component';
import { NumScaleQuestionEditAnswerFormComponent } from './num-scale-question-edit-answer-form.component';
import { RankOptionsQuestionEditAnswerFormComponent } from './rank-options-question-edit-answer-form.component';
import { TextQuestionEditAnswerFormComponent } from './text-question-edit-answer-form.component';

/**
 * Module for all different types of question edit answer forms.
 */
@NgModule({
  declarations: [
    ContributionQuestionEditAnswerFormComponent,
    TextQuestionEditAnswerFormComponent,
    McqQuestionEditAnswerFormComponent,
    NumScaleQuestionEditAnswerFormComponent,
    ContributionPointDescriptionPipe,
    MsqQuestionEditAnswerFormComponent,
    RankOptionsQuestionEditAnswerFormComponent,
  ],
  exports: [
    ContributionQuestionEditAnswerFormComponent,
    TextQuestionEditAnswerFormComponent,
    McqQuestionEditAnswerFormComponent,
    NumScaleQuestionEditAnswerFormComponent,
    ContributionPointDescriptionPipe,
    MsqQuestionEditAnswerFormComponent,
    RankOptionsQuestionEditAnswerFormComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RichTextEditorModule,
  ],
})
export class QuestionEditAnswerFormModule { }
