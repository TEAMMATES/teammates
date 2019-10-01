import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SingleResponseModule } from '../single-response/single-response.module';
import { PerQuestionViewResponsesComponent } from './per-question-view-responses.component';
import { RouterModule } from "@angular/router";

/**
 * Module for component to display list of responses for one question.
 */
@NgModule({
  declarations: [
    PerQuestionViewResponsesComponent,
  ],
  exports: [
    PerQuestionViewResponsesComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    SingleResponseModule,
  ],
})
export class PerQuestionViewResponsesModule { }
