import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { QuestionStatisticsModule } from '../../question-types/question-statistics/question-statistics.module';
import { SingleStatisticsComponent } from './single-statistics.component';

/**
 * Module for the single statistics component.
 */
@NgModule({
  declarations: [SingleStatisticsComponent],
  exports: [SingleStatisticsComponent],
  imports: [
    CommonModule,
    QuestionStatisticsModule,
  ],
})
export class SingleStatisticsModule { }
