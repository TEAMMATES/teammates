import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SingleResponseComponent } from './single-response.component';
import { QuestionResponseModule } from '../../question-types/question-response/question-response.module';

/**
 * Module for single response component.
 */
@NgModule({
  exports: [
    SingleResponseComponent,
  ],
  imports: [
    CommonModule,
    QuestionResponseModule,
    SingleResponseComponent,
  ],
})
export class SingleResponseModule { }
