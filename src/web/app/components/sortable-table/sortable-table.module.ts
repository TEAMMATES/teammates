import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { DynamicComponentModule } from 'ng-dynamic-component';
import { ContributionQuestionStatisticsModule,
} from '../question-types/question-statistics/contribution-question-statistics/contribution-question-statistics.module';
import { SortableTableComponent } from './sortable-table.component';

/**
 * Module for displaying data in a sortable table
 */
@NgModule({
  declarations: [SortableTableComponent],
  imports: [
    CommonModule,
    NgbTooltipModule,
    ContributionQuestionStatisticsModule,
    DynamicComponentModule,
  ],
  exports: [
    SortableTableComponent,
  ],
})
export class SortableTableModule { }
