import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ContributionQuestionInstructionComponent } from './contribution-question-instruction.component';
import { NumScaleQuestionInstructionComponent } from './num-scale-question-instruction.component';
import { RankOptionsQuestionInstructionComponent } from './rank-options-question-instruction.component';
import { RankRecipientsQuestionInstructionComponent } from './rank-recipients-question-instruction.component';
import { TextQuestionInstructionComponent } from './text-question-instruction.component';

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
  ],
  exports: [
    ContributionQuestionInstructionComponent,
    NumScaleQuestionInstructionComponent,
    TextQuestionInstructionComponent,
    RankOptionsQuestionInstructionComponent,
    RankRecipientsQuestionInstructionComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class QuestionInstructionModule { }
