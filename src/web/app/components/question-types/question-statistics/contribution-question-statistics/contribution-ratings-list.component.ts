import { Component, Input, OnInit } from '@angular/core';
import { CONTRIBUTION_POINT_NOT_SUBMITTED } from '../../../../../types/feedback-response-details';

/**
 * Display array of ratings in a line
 */
@Component({
  selector: 'tm-contribution-ratings-list',
  templateUrl: './contribution-ratings-list.component.html',
  styleUrls: ['./contribution-ratings-list.component.scss'],
})
export class ContributionRatingsListComponent implements OnInit {

  CONTRIBUTION_POINT_NOT_SUBMITTED: number = CONTRIBUTION_POINT_NOT_SUBMITTED;

  @Input()
  ratingsList: number[] = [];

  constructor() { }

  ngOnInit(): void {
  }

}
