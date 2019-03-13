import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ConstsumQuestionAdditionalInfoComponent } from './constsum-question-additional-info.component';
import { ContributionQuestionAdditionalInfoComponent } from './contribution-question-additional-info.component';
import { McqQuestionAdditionalInfoComponent } from './mcq-question-additional-info.component';
import { MsqQuestionAdditionalInfoComponent } from './msq-question-additional-info.component';
import { NumScaleQuestionAdditionalInfoComponent } from './num-scale-question-additional-info.component';
import { RankOptionsQuestionAdditionalInfoComponent } from './rank-options-question-additional-info.component';
import { RankRecipientsQuestionAdditionalInfoComponent } from './rank-recipients-question-additional-info.component';
import { RubricQuestionAdditionalInfoComponent } from './rubric-question-additional-info.component';
import { TextQuestionAdditionalInfoComponent } from './text-question-additional-info.component';

/**
 * Module for all additional info components for all different question types.
 */
@NgModule({
  declarations: [
    ContributionQuestionAdditionalInfoComponent,
    TextQuestionAdditionalInfoComponent,
    NumScaleQuestionAdditionalInfoComponent,
    ConstsumQuestionAdditionalInfoComponent,
    MsqQuestionAdditionalInfoComponent,
    McqQuestionAdditionalInfoComponent,
    RubricQuestionAdditionalInfoComponent,
    RankOptionsQuestionAdditionalInfoComponent,
    RankRecipientsQuestionAdditionalInfoComponent,
  ],
  exports: [
    ContributionQuestionAdditionalInfoComponent,
    TextQuestionAdditionalInfoComponent,
    NumScaleQuestionAdditionalInfoComponent,
    ConstsumQuestionAdditionalInfoComponent,
    MsqQuestionAdditionalInfoComponent,
    McqQuestionAdditionalInfoComponent,
    RubricQuestionAdditionalInfoComponent,
    RankOptionsQuestionAdditionalInfoComponent,
    RankRecipientsQuestionAdditionalInfoComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class QuestionAdditionalInfoModule { }
