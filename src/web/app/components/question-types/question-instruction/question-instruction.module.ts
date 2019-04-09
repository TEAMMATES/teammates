import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ContributionQuestionInstructionComponent } from './contribution-question-instruction.component';
import { NumScaleQuestionInstructionComponent } from './num-scale-question-instruction.component';
import { RankOptionsQuestionInstructionComponent } from './rank-options-question-instruction.component';
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
  ],
  exports: [
    ContributionQuestionInstructionComponent,
    NumScaleQuestionInstructionComponent,
    TextQuestionInstructionComponent,
    RankOptionsQuestionInstructionComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class QuestionInstructionModule { }
