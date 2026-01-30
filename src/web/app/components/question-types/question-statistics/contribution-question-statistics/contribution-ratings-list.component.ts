import { Component, Input } from '@angular/core';
import { CONTRIBUTION_POINT_NOT_SUBMITTED } from '../../../../../types/feedback-response-details';
import { NgFor, NgIf } from '@angular/common';
import { ContributionComponent } from './contribution.component';

/**
 * Display array of ratings in a line
 */
@Component({
  selector: 'tm-contribution-ratings-list',
  templateUrl: './contribution-ratings-list.component.html',
  styleUrls: ['./contribution-ratings-list.component.scss'],
  imports: [
    NgFor,
    NgIf,
    ContributionComponent,
  ],
})
export class ContributionRatingsListComponent {

  CONTRIBUTION_POINT_NOT_SUBMITTED: number = CONTRIBUTION_POINT_NOT_SUBMITTED;

  @Input()
  ratingsList: number[] = [];

}
