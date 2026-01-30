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
  imports: [
    CommonModule,
    NgbTooltipModule,
    ContributionQuestionStatisticsModule,
    DynamicModule,
    SortableTableComponent,
  ],
  exports: [
    SortableTableComponent,
  ],
})
export class SortableTableModule { }
