import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { ConstsumOptionsFieldComponent } from './constsum-options-field/constsum-options-field.component';
import {
  ConstsumOptionsQuestionEditDetailsFormComponent,
} from './constsum-options-question-edit-details-form.component';
import {
  ConstsumRecipientsQuestionEditDetailsFormComponent,
} from './constsum-recipients-question-edit-details-form.component';
import { ContributionQuestionEditDetailsFormComponent } from './contribution-question-edit-details-form.component';
import { McqFieldComponent } from './mcq-field/mcq-field.component';
import { McqQuestionEditDetailsFormComponent } from './mcq-question-edit-details-form.component';
import { MsqFieldComponent } from './msq-field/msq-field.component';
import { MsqQuestionEditDetailsFormComponent } from './msq-question-edit-details-form.component';
import { NumScaleQuestionEditDetailsFormComponent } from './num-scale-question-edit-details-form.component';
import { OptionRichTextEditorModule } from './option-rich-text-editor/option-rich-text-editor.module';
import { RankOptionsFieldComponent } from './rank-options-field/rank-options-field.component';
import { RankOptionsQuestionEditDetailsFormComponent } from './rank-options-question-edit-details-form.component';
import { RankRecipientsQuestionEditDetailsFormComponent } from './rank-recipients-question-edit-details-form.component';
import { RubricQuestionEditDetailsFormComponent } from './rubric-question-edit-details-form.component';
import { TextQuestionEditDetailsFormComponent } from './text-question-edit-details-form.component';
import { WeightFieldComponent } from './weight-field/weight-field.component';

/**
 * Module for all different types of question edit details forms.
 */
@NgModule({
  declarations: [
    ContributionQuestionEditDetailsFormComponent,
    McqFieldComponent,
    McqQuestionEditDetailsFormComponent,
    MsqFieldComponent,
    MsqQuestionEditDetailsFormComponent,
    NumScaleQuestionEditDetailsFormComponent,
    RankOptionsFieldComponent,
    RankOptionsQuestionEditDetailsFormComponent,
    RankRecipientsQuestionEditDetailsFormComponent,
    TextQuestionEditDetailsFormComponent,
    WeightFieldComponent,
    RubricQuestionEditDetailsFormComponent,
    ConstsumOptionsQuestionEditDetailsFormComponent,
    ConstsumOptionsFieldComponent,
    ConstsumRecipientsQuestionEditDetailsFormComponent,
  ],
  exports: [
    ContributionQuestionEditDetailsFormComponent,
    McqFieldComponent,
    McqQuestionEditDetailsFormComponent,
    MsqFieldComponent,
    MsqQuestionEditDetailsFormComponent,
    NumScaleQuestionEditDetailsFormComponent,
    RankOptionsFieldComponent,
    RankOptionsQuestionEditDetailsFormComponent,
    RankRecipientsQuestionEditDetailsFormComponent,
    TextQuestionEditDetailsFormComponent,
    WeightFieldComponent,
    RubricQuestionEditDetailsFormComponent,
    ConstsumOptionsQuestionEditDetailsFormComponent,
    ConstsumRecipientsQuestionEditDetailsFormComponent,
  ],
  imports: [
    CommonModule,
    DragDropModule,
    FormsModule,
    NgbTooltipModule,
    OptionRichTextEditorModule,
  ],
})
export class QuestionEditDetailsFormModule { }
