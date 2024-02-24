import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ConstsumOptionsQuestionAdditionalInfoComponent } from './constsum-options-question-additional-info.component';
import {
  ConstsumRecipientsQuestionAdditionalInfoComponent,
} from './constsum-recipients-question-additional-info.component';
import { ContributionQuestionAdditionalInfoComponent } from './contribution-question-additional-info.component';
import { McqQuestionAdditionalInfoComponent } from './mcq-question-additional-info.component';
import { MsqQuestionAdditionalInfoComponent } from './msq-question-additional-info.component';
import { NumScaleQuestionAdditionalInfoComponent } from './num-scale-question-additional-info.component';
import { RankOptionsQuestionAdditionalInfoComponent } from './rank-options-question-additional-info.component';
import { RankRecipientsQuestionAdditionalInfoComponent } from './rank-recipients-question-additional-info.component';
import { RubricQuestionAdditionalInfoComponent } from './rubric-question-additional-info.component';
import { TextQuestionAdditionalInfoComponent } from './text-question-additional-info.component';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';

/**
 * Module for all additional info components for all different question types.
 */
@NgModule({
  declarations: [
    ContributionQuestionAdditionalInfoComponent,
    TextQuestionAdditionalInfoComponent,
    NumScaleQuestionAdditionalInfoComponent,
    MsqQuestionAdditionalInfoComponent,
    McqQuestionAdditionalInfoComponent,
    RubricQuestionAdditionalInfoComponent,
    RankOptionsQuestionAdditionalInfoComponent,
    RankRecipientsQuestionAdditionalInfoComponent,
    ConstsumOptionsQuestionAdditionalInfoComponent,
    ConstsumRecipientsQuestionAdditionalInfoComponent,
  ],
  exports: [
    ContributionQuestionAdditionalInfoComponent,
    TextQuestionAdditionalInfoComponent,
    NumScaleQuestionAdditionalInfoComponent,
    MsqQuestionAdditionalInfoComponent,
    McqQuestionAdditionalInfoComponent,
    RubricQuestionAdditionalInfoComponent,
    RankOptionsQuestionAdditionalInfoComponent,
    RankRecipientsQuestionAdditionalInfoComponent,
    ConstsumOptionsQuestionAdditionalInfoComponent,
    ConstsumRecipientsQuestionAdditionalInfoComponent,
  ],
  imports: [
    CommonModule,
    TeammatesCommonModule,
  ],
})
export class QuestionAdditionalInfoModule { }
