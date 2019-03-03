import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ConstsumQuestionResponseComponent } from './constsum-question-response.component';
import { ContributionQuestionResponseComponent } from './contribution-question-response.component';
import { McqQuestionResponseComponent } from './mcq-question-response.component';
import { MsqQuestionResponseComponent } from './msq-question-response.component';
import { NumscaleQuestionResponseComponent } from './numscale-question-response.component';
import { RankOptionsQuestionResponseComponent } from './rank-options-question-response.component';
import { RankRecipientsQuestionResponseComponent } from './rank-recipients-question-response.component';
import { RubricQuestionResponseComponent } from './rubric-question-response.component';
import { TextQuestionResponseComponent } from './text-question-response.component';

/**
 * Module for all different types of question responses.
 */
@NgModule({
  declarations: [
    ContributionQuestionResponseComponent,
    TextQuestionResponseComponent,
    NumscaleQuestionResponseComponent,
    McqQuestionResponseComponent,
    MsqQuestionResponseComponent,
    RubricQuestionResponseComponent,
    ConstsumQuestionResponseComponent,
    RankOptionsQuestionResponseComponent,
    RankRecipientsQuestionResponseComponent,
  ],
  exports: [
    ContributionQuestionResponseComponent,
    TextQuestionResponseComponent,
    NumscaleQuestionResponseComponent,
    McqQuestionResponseComponent,
    MsqQuestionResponseComponent,
    RubricQuestionResponseComponent,
    ConstsumQuestionResponseComponent,
    RankOptionsQuestionResponseComponent,
    RankRecipientsQuestionResponseComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class QuestionResponseModule { }
