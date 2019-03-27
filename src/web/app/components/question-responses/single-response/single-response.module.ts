import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { QuestionResponseModule } from '../../question-types/question-response/question-response.module';
import { SingleResponseComponent } from './single-response.component';

/**
 * Module for single response component.
 */
@NgModule({
  declarations: [
    SingleResponseComponent,
  ],
  exports: [
    SingleResponseComponent,
  ],
  imports: [
    CommonModule,
    QuestionResponseModule,
  ],
})
export class SingleResponseModule { }
