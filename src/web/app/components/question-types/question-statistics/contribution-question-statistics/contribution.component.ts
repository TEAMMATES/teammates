import { Component, Input, OnInit } from '@angular/core';
import {
  CONTRIBUTION_POINT_NOT_INITIALIZED,
  CONTRIBUTION_POINT_NOT_SUBMITTED,
} from '../../../../../types/feedback-response-details';

/**
 * Displays a contribution value relative to baseline of 100.
 */
@Component({
  selector: 'tm-contribution',
  templateUrl: './contribution.component.html',
  styleUrls: ['./contribution.component.scss'],
})
export class ContributionComponent implements OnInit {

  CONTRIBUTION_POINT_NOT_INITIALIZED: number = CONTRIBUTION_POINT_NOT_INITIALIZED;
  CONTRIBUTION_POINT_NOT_SUBMITTED: number = CONTRIBUTION_POINT_NOT_SUBMITTED;

  @Input() value: number = 100;
  @Input() diffOnly: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

}
