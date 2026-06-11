import { Component, Input, OnInit } from '@angular/core';
import { ContributionStatistics, FeedbackContributionResponseDetails } from '../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_RESPONSE_DETAILS } from '../../../../types/default-question-structs';
import {
  CONTRIBUTION_POINT_EQUAL_SHARE,
  CONTRIBUTION_POINT_NOT_INITIALIZED,
  CONTRIBUTION_POINT_NOT_SUBMITTED,
  CONTRIBUTION_POINT_NOT_SURE,
} from '../../../../types/feedback-response-details';

/**
 * Contribution question response.
 */
@Component({
  selector: 'tm-contribution-question-response',
  templateUrl: './contribution-question-response.component.html',
  styleUrls: ['./contribution-question-response.component.scss'],
  imports: [],
})
export class ContributionQuestionResponseComponent implements OnInit {
  @Input() responseDetails: FeedbackContributionResponseDetails = DEFAULT_CONTRIBUTION_RESPONSE_DETAILS();
  @Input() statistics = '';
  @Input() giverUserId = '';
  @Input() recipientUserId = '';

  answer = 100;

  CONTRIBUTION_POINT_EQUAL_SHARE!: number;
  CONTRIBUTION_POINT_NOT_SUBMITTED!: number;
  CONTRIBUTION_POINT_NOT_SURE!: number;
  CONTRIBUTION_POINT_NOT_INITIALIZED!: number;

  constructor() {
    this.CONTRIBUTION_POINT_EQUAL_SHARE = CONTRIBUTION_POINT_EQUAL_SHARE;
    this.CONTRIBUTION_POINT_NOT_SUBMITTED = CONTRIBUTION_POINT_NOT_SUBMITTED;
    this.CONTRIBUTION_POINT_NOT_SURE = CONTRIBUTION_POINT_NOT_SURE;
    this.CONTRIBUTION_POINT_NOT_INITIALIZED = CONTRIBUTION_POINT_NOT_INITIALIZED;
  }

  ngOnInit(): void {
    this.answer = this.responseDetails.answer;
    if (this.statistics && this.giverUserId && this.recipientUserId) {
      const statisticsObject: ContributionStatistics = JSON.parse(this.statistics);
      const giverStats = statisticsObject.results[this.giverUserId];
      if (giverStats) {
        if (this.giverUserId === this.recipientUserId) {
          this.answer = giverStats.claimed;
        } else if (giverStats.claimedOthers[this.recipientUserId] !== undefined) {
          this.answer = giverStats.claimedOthers[this.recipientUserId];
        }
      }
    }
  }
}
