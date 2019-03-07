import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { QuestionTextWithInfoModule } from '../../question-text-with-info/question-text-with-info.module';
import { PerQuestionViewResponsesModule } from '../per-question-view-responses/per-question-view-responses.module';
import { GqrRqgViewResponsesComponent } from './gqr-rqg-view-responses.component';

/**
 * Module for component to display list of responses in GQR/RQG view.
 */
@NgModule({
  declarations: [GqrRqgViewResponsesComponent],
  exports: [GqrRqgViewResponsesComponent],
  imports: [
    CommonModule,
    QuestionTextWithInfoModule,
    PerQuestionViewResponsesModule,
  ],
})
export class GqrRqgViewResponsesModule { }
