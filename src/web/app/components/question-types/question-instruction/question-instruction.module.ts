import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ContributionQuestionInstructionComponent } from './contribution-question-instruction.component';
import { TextQuestionInstructionComponent } from './text-question-instruction.component';

/**
 * Module for all different types of question instructions.
 */
@NgModule({
  declarations: [
    ContributionQuestionInstructionComponent,
    TextQuestionInstructionComponent,
  ],
  exports: [
    ContributionQuestionInstructionComponent,
    TextQuestionInstructionComponent,
  ],
  imports: [
    CommonModule,
  ],
})
export class QuestionInstructionModule { }
