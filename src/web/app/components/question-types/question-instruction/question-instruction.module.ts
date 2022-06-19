import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ConstsumOptionsQuestionInstructionComponent } from './constsum-options-question-instruction.component';
import { ConstsumRecipientsQuestionInstructionComponent } from './constsum-recipients-question-instruction.component';
import { ContributionQuestionInstructionComponent } from './contribution-question-instruction.component';
import { NumScaleQuestionInstructionComponent } from './num-scale-question-instruction.component';
import { RankOptionsQuestionInstructionComponent } from './rank-options-question-instruction.component';
import { RankRecipientsQuestionInstructionComponent } from './rank-recipients-question-instruction.component';
import { TextQuestionInstructionComponent } from './text-question-instruction.component';
import { NumRangeQuestionInstructionComponent } from './num-range-question-instruction.component';

/**
 * Module for all different types of question instructions.
 */
@NgModule({
  declarations: [
    ContributionQuestionInstructionComponent,
    NumScaleQuestionInstructionComponent,
    TextQuestionInstructionComponent,
    RankOptionsQuestionInstructionComponent,
    RankRecipientsQuestionInstructionComponent,
    ConstsumOptionsQuestionInstructionComponent,
    ConstsumRecipientsQuestionInstructionComponent,
    NumRangeQuestionInstructionComponent,
  ],
  exports: [
    ContributionQuestionInstructionComponent,
    NumScaleQuestionInstructionComponent,
    TextQuestionInstructionComponent,
    RankOptionsQuestionInstructionComponent,
    RankRecipientsQuestionInstructionComponent,
    ConstsumOptionsQuestionInstructionComponent,
    ConstsumRecipientsQuestionInstructionComponent,
    NumRangeQuestionInstructionComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class QuestionInstructionModule { }
