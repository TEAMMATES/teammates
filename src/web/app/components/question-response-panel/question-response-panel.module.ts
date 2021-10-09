import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SingleStatisticsModule } from '../../components/question-responses/single-statistics/single-statistics.module';
import {
  StudentViewResponsesModule,
} from '../../components/question-responses/student-view-responses/student-view-responses.module';
import { QuestionTextWithInfoModule } from '../../components/question-text-with-info/question-text-with-info.module';

import { QuestionResponsePanelComponent } from './question-response-panel.component';

/**
 * Question Response Panel module.
 */
@NgModule({
  imports: [
    CommonModule,
    QuestionTextWithInfoModule,
    SingleStatisticsModule,
    StudentViewResponsesModule,
  ],
  declarations: [
    QuestionResponsePanelComponent,
  ],
  exports: [
    QuestionResponsePanelComponent,
  ],
})
export class QuestionResponsePanelModule { }
