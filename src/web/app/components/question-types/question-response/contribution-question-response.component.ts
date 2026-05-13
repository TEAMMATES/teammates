import { Component, Input, OnInit } from '@angular/core';
import { ContributionStatistics, FeedbackContributionResponseDetails } from '../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_RESPONSE_DETAILS } from '../../../../types/default-question-structs';
import {
  CONTRIBUTION_POINT_EQUAL_SHARE,
  CONTRIBUTION_POINT_NOT_INITIALIZED,
  CONTRIBUTION_POINT_NOT_SUBMITTED,
  CONTRIBUTION_POINT_NOT_SURE,
} from '../../../../types/feedback-response-details';
import { areEmailsEqual } from '../../teammates-common/email-utils';

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
  @Input() giverEmail = '';
  @Input() recipientEmail = '';

  answer = 100;

  CONTRIBUTION_POINT_EQUAL_SHARE: number = CONTRIBUTION_POINT_EQUAL_SHARE;
  CONTRIBUTION_POINT_NOT_SUBMITTED: number = CONTRIBUTION_POINT_NOT_SUBMITTED;
  CONTRIBUTION_POINT_NOT_SURE: number = CONTRIBUTION_POINT_NOT_SURE;
  CONTRIBUTION_POINT_NOT_INITIALIZED: number = CONTRIBUTION_POINT_NOT_INITIALIZED;

  ngOnInit(): void {
    this.answer = this.responseDetails.answer;
    if (this.statistics) {
      const statisticsObject: ContributionStatistics = JSON.parse(this.statistics);
      if (areEmailsEqual(this.giverEmail, this.recipientEmail)) {
        this.answer = statisticsObject.results[this.giverEmail].claimed;
      } else {
        this.answer = statisticsObject.results[this.giverEmail].claimedOthers[this.recipientEmail];
      }
    }
  }
}
