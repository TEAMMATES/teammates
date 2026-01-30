import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { ContributionRatingsListComponent } from './contribution-ratings-list.component';
import { ContributionComponent } from './contribution.component';

/**
 * Module for contribution question statistics
 */
@NgModule({
  imports: [
    CommonModule,
    NgbTooltipModule,
    ContributionComponent, ContributionRatingsListComponent,
  ],
  exports: [
    ContributionComponent, ContributionRatingsListComponent,
  ],
})
export class ContributionQuestionStatisticsModule { }
