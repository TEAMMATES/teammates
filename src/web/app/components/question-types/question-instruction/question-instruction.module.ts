import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ContributionQuestionInstructionComponent } from './contribution-question-instruction.component';
import { NumScaleQuestionInstructionComponent } from './num-scale-question-instruction.component';
import { TextQuestionInstructionComponent } from './text-question-instruction.component';

/**
 * Module for all different types of question instructions.
 */
@NgModule({
  declarations: [
    ContributionQuestionInstructionComponent,
    NumScaleQuestionInstructionComponent,
    TextQuestionInstructionComponent,
  ],
  exports: [
    ContributionQuestionInstructionComponent,
    NumScaleQuestionInstructionComponent,
    TextQuestionInstructionComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class QuestionInstructionModule { }
