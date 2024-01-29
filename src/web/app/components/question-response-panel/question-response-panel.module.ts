import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { InViewportModule } from 'ng-in-viewport';
import { QuestionResponsePanelComponent } from './question-response-panel.component';
import { LoadingRetryModule } from '../loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../loading-spinner/loading-spinner.module';
import { SingleStatisticsModule } from '../question-responses/single-statistics/single-statistics.module';
import {
  StudentViewResponsesModule,
} from '../question-responses/student-view-responses/student-view-responses.module';
import { QuestionTextWithInfoModule } from '../question-text-with-info/question-text-with-info.module';

/**
 * Question Response Panel module.
 */
@NgModule({
  imports: [
    CommonModule,
    QuestionTextWithInfoModule,
    SingleStatisticsModule,
    StudentViewResponsesModule,
    InViewportModule,
    LoadingSpinnerModule,
    LoadingRetryModule,
  ],
  declarations: [
    QuestionResponsePanelComponent,
  ],
  exports: [
    QuestionResponsePanelComponent,
  ],
})
export class QuestionResponsePanelModule { }
