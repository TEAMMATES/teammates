import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SingleStatisticsComponent } from './single-statistics.component';
import { QuestionStatisticsModule } from '../../question-types/question-statistics/question-statistics.module';

/**
 * Module for the single statistics component.
 */
@NgModule({
  exports: [SingleStatisticsComponent],
  imports: [
    CommonModule,
    QuestionStatisticsModule,
    SingleStatisticsComponent,
  ],
})
export class SingleStatisticsModule { }
