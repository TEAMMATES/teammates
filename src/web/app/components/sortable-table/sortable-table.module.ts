import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { DynamicModule } from 'ng-dynamic-component';
import { SortableTableComponent } from './sortable-table.component';
import {
  ContributionQuestionStatisticsModule,
} from '../question-types/question-statistics/contribution-question-statistics/contribution-question-statistics.module';

/**
 * Module for displaying data in a sortable table
 */
@NgModule({
  declarations: [SortableTableComponent],
  imports: [
    CommonModule,
    NgbTooltipModule,
    ContributionQuestionStatisticsModule,
    DynamicModule,
  ],
  exports: [
    SortableTableComponent,
  ],
})
export class SortableTableModule { }
